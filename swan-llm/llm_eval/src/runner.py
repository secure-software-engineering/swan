import argparse
import json
import logging
import multiprocessing
import os
import re
import shutil
import sys
import time
import traceback
from pathlib import Path
from sys import stdout
from typing import List, Optional

import prompts
import utils
import datetime

import vllm_helpers
import transformers_helpers
import openai_helpers

from vllm import LLM, SamplingParams
from vllm.lora.request import LoRARequest
import gc
import torch
import results_analyzer
from tqdm import tqdm

AUTOFIX_WITH_OPENAI = False
REQUEST_TIMEOUT = 60
USE_MULTIPROCESSING_FOR_TERMINATION = True
MAX_TOKENS = 64
TEMPARATURE = 0.001
MAX_NEW_TOKENS = 50

# Create a logger
logger = logging.getLogger("runner")
logger.setLevel(logging.DEBUG)

if utils.is_running_in_docker():
    file_handler = logging.FileHandler("/tmp/ollama_log.log", mode="w")
else:
    file_handler = logging.FileHandler("ollama_log_new.log", mode="w")

file_handler.setLevel(logging.DEBUG)

console_handler = logging.StreamHandler(stdout)
console_handler.setLevel(logging.DEBUG)
formatter = logging.Formatter("%(asctime)s - %(name)s - %(levelname)s - %(message)s")
file_handler.setFormatter(formatter)
console_handler.setFormatter(formatter)
logger.addHandler(file_handler)
logger.addHandler(console_handler)


def invoke_llm(llm, prompt, queue):
    try:
        output = llm.invoke(prompt)
        queue.put(output)
    except Exception as e:
        queue.put(e)


def load_dataset(json_dataset_path):
    # Load the dataset from json
    with open(json_dataset_path, "r") as f:
        dataset = json.load(f)
    return dataset


def get_prompt_mapping(prompt_template, dataset, use_system_prompt=False):
    id_mapping = {
        idx: {
            "data": data,
            # "result_filepath": str(file_path).replace(".py", f"_result.json"),
            # "result_dump_filepath": str(file_path).replace(".py", f"_result_dump.txt"),
            "prompt": utils.get_prompt(
                prompt_template, data, use_system_prompt=use_system_prompt
            ),
        }
        for idx, data in enumerate(dataset["methods"])
    }

    return id_mapping


def model_evaluation_vllm(
    model_name,
    prompt_template,
    dataset,
    engine,
    results_dst,
    use_system_prompt=False,
    lora_request=None,
    sampling_params=None,
):
    # TODO: Test this function
    results_dump_file = results_dst / f"{model_name}-{prompt_template}_results_dump.csv"

    id_mapping = get_prompt_mapping(prompt_template, dataset, use_system_prompt)

    prompts = [x["prompt"] for x in id_mapping.values()]

    processed_prompts = engine.tokenizer.tokenizer.apply_chat_template(
        prompts, tokenize=False, add_generation_template=True
    )

    request_outputs = vllm_helpers.process_requests(
        engine, processed_prompts, sampling_params, lora_request
    )

    for id, r_output in enumerate(request_outputs):
        req_data = id_mapping[id]["data"]
        expected_srm = req_data["srm"]
        utils.response_to_csv(
            req_data["name"], r_output, expected_srm, results_dump_file
        )


def model_evaluation_transformers(
    model_name,
    prompt_template,
    dataset,
    pipe,
    results_dst,
    use_system_prompt=False,
    batch_size=32,
    evaluation_type="srm",
):
    results_dump_file = results_dst / f"{model_name}-{prompt_template}_results_dump.csv"
    prompt_dump_file = results_dst / f"{model_name}-{prompt_template}_prompts_dump.csv"

    os.makedirs(os.path.dirname(results_dump_file), exist_ok=True)

    id_mapping = get_prompt_mapping(prompt_template, dataset, use_system_prompt)

    prompts = [x["prompt"] for x in id_mapping.values()]

    # dump prompts as csv
    with open(prompt_dump_file, "w") as f:
        for prompt in prompts:
            f.write(f"{prompt}\n")

    # processed_prompts = pipe.tokenizer.apply_chat_template(
    #     prompts, tokenize=False, add_generation_template=True
    # )

    # Split prompts into batches
    progress_batch = batch_size
    for i in tqdm(range(0, len(prompts), progress_batch)):
        prompt_batch = prompts[i : i + progress_batch]

        request_outputs = transformers_helpers.process_requests(
            pipe,
            prompt_batch,
            max_new_tokens=MAX_NEW_TOKENS,
            batch_size=batch_size,
        )

        for id, r_output in enumerate(request_outputs):
            file_info = id_mapping[id + i]

            output_raw = r_output[0]["generated_text"][-1]["content"]
            req_data = id_mapping[id + i]["data"]
            if evaluation_type == "cwe":
                expected = req_data["cwe"]
            elif evaluation_type == "srm":
                expected = req_data["srm"]
            else:
                logger.error(f"Invalid evaluation type: {evaluation_type}")
                sys.exit(-1)

            # If expected is not provided, set it to none
            if expected == []:
                expected = ["none"]

            utils.response_to_csv(
                req_data["name"],
                output_raw,
                expected,
                results_dump_file,
                evaluation_type=evaluation_type,
            )


def model_evaluation_openai(
    model_name,
    prompt_template,
    openai_key,
    dataset,
    results_dst,
    use_system_prompt=False,
    evaluation_type="srm",
):

    results_dump_file = results_dst / f"{model_name}-{prompt_template}_results_dump.csv"
    prompt_dump_file = results_dst / f"{model_name}-{prompt_template}_prompts_dump.csv"

    id_mapping = get_prompt_mapping(prompt_template, dataset, use_system_prompt)

    prompts = [x["prompt"] for x in id_mapping.values()]

    # dump prompts as csv
    with open(prompt_dump_file, "w") as f:
        for prompt in prompts:
            f.write(f"{prompt}\n")

    request_outputs = openai_helpers.process_requests(
        model_name,
        prompts,
        openai_key,
        max_new_tokens=MAX_NEW_TOKENS,
        max_workers=16,
    )

    for id, r_output in enumerate(request_outputs):
        req_data = id_mapping[id]["data"]
        if evaluation_type == "cwe":
            expected = req_data["cwe"]
        elif evaluation_type == "srm":
            expected = req_data["srm"]
        else:
            logger.error(f"Invalid evaluation type: {evaluation_type}")
            sys.exit(-1)

        # If expected is not provided, set it to none
        if expected == []:
            expected = ["none"]

        utils.response_to_csv(
            req_data["name"],
            r_output,
            expected,
            results_dump_file,
            evaluation_type=evaluation_type,
        )


def main_runner(args, runner_config, models_to_run, openai_models_models_to_run):
    runner_start_time = time.time()
    current_time = datetime.datetime.now().strftime("%Y-%m-%d_%H-%M-%S")
    bechmark_path = Path(args.bechmark_path)

    dataset = load_dataset(bechmark_path)

    for model in models_to_run:
        gc.collect()
        torch.cuda.empty_cache()
        if model["use_vllms_for_evaluation"]:
            pipe = vllm_helpers.initialize_engine(
                model["model_path"],
                model["quantization"],
                model["lora_repo"],
                model["max_model_len"],
            )
            lora_request = None
            if model["lora_repo"] is not None:
                lora_request = LoRARequest(
                    f"{model['name']}-lora", 1, model["lora_repo"]
                )

            sampling_params = SamplingParams(
                temperature=TEMPARATURE, top_p=0.95, max_tokens=MAX_TOKENS
            )
        else:
            if model["lora_repo"] is None:
                model_path = model["model_path"]
            else:
                model_path = model["lora_repo"]

            try:
                pipe = transformers_helpers.load_model_and_configurations(
                    args.hf_token, model_path, TEMPARATURE
                )
            except Exception as e:
                logger.error(f"Error loading model {model['name']}: {e}")
                continue

        for prompt_id in args.prompt_id:
            model_start_time = time.time()

            logger.info(f"Running model {model['name']} with prompt {prompt_id}")
            error_count = 0
            timeout_count = 0
            json_count = 0
            files_analyzed = 0

            # Create result folder for model specific results
            bechmark_path = Path(args.bechmark_path)
            if args.results_dir is None:
                results_dst = (
                    bechmark_path.parent
                    / ".scrapy/results"
                    / current_time
                    / f"{model}-{prompt_id}"
                )
            else:
                results_dst = (
                    Path(args.results_dir)
                    / current_time
                    / f'{model["name"]}-{prompt_id}'
                )

            os.makedirs(results_dst, exist_ok=True)

            if model["use_vllms_for_evaluation"]:
                model_evaluation_vllm(
                    model["name"],
                    prompt_id,
                    dataset,
                    pipe,
                    results_dst,
                    use_system_prompt=model["use_system_prompt"],
                    lora_request=lora_request,
                    sampling_params=sampling_params,
                )

            else:
                model_evaluation_transformers(
                    model["name"],
                    prompt_id,
                    dataset,
                    pipe,
                    results_dst,
                    use_system_prompt=model["use_system_prompt"],
                    batch_size=model["batch_size"],
                    evaluation_type=args.evaluation_type,
                )

            logger.info(
                f"Model {model['name']} finished in {time.time()-model_start_time:.2f} seconds"
            )

        del pipe
        gc.collect()
        torch.cuda.empty_cache()

    # running gpt models
    for model in openai_models_models_to_run:
        for prompt_id in args.prompt_id:

            error_count = 0
            timeout_count = 0
            json_count = 0
            files_analyzed = 0

            # Create result folder for model specific results
            bechmark_path = Path(args.bechmark_path)
            if args.results_dir is None:
                results_dst = (
                    bechmark_path.parent
                    / ".scrapy/results"
                    / current_time
                    / f"{model}-{prompt_id}"
                )
            else:
                results_dst = (
                    Path(args.results_dir)
                    / current_time
                    / f'{model["name"]}-{prompt_id}'
                )

            os.makedirs(results_dst, exist_ok=True)

            model_start_time = time.time()
            model_evaluation_openai(
                model["name"],
                prompt_id,
                args.openai_key,
                dataset,
                results_dst,
                use_system_prompt=model["use_system_prompt"],
                evaluation_type=args.evaluation_type,
            )

            logger.info(
                f"Model {model['name']} finished in {time.time()-model_start_time:.2f} seconds"
            )
            logger.info("Running translator")

    logger.info(
        f"Runner finished in {time.time()-runner_start_time:.2f} seconds, with errors:"
        f" {error_count} | JSON errors: {json_count}"
    )

    results_analyzer.generate_multi_label_classification_report(
        results_dst.parent, evaluation_type=args.evaluation_type
    )


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--bechmark_path",
        help="Specify the benchmark path",
        default="/tmp/micro-benchmark",
    )

    parser.add_argument(
        "--results_dir",
        help="Specify the benchmark path",
        default=None,
    )

    parser.add_argument("--hf_token", help="Specify the hf token", required=True)

    parser.add_argument(
        "--openai_key", help="Specify the OpenAI Auth Key", required=False
    )

    parser.add_argument(
        "--prompt_id",
        nargs="+",
        type=str,
        help="Space-separated list of Prompt IDs",
    )

    parser.add_argument(
        "--models_config",
        type=str,
        default="models_config.json",
    )

    parser.add_argument(
        "--models",
        nargs="+",
        type=str,
        help="Space-separated list of models",
    )

    parser.add_argument(
        "--custom_models",
        nargs="+",
        type=str,
        help="Space-separated list of custom models",
    )

    parser.add_argument(
        "--openai_models",
        nargs="+",
        type=str,
        help="Space-separated list of openai models",
    )

    parser.add_argument(
        "--enable_streaming",
        help="If LLM response should be streamed",
        type=bool,
        default=False,
    )

    parser.add_argument(
        "--evaluation_type",
        type=str,
        default="srm",
        help="Specify the evaluation type, srm or cwe",
    )

    args = parser.parse_args()

    # Set HF token
    os.environ["HF_TOKEN"] = args.hf_token

    models_config = utils.load_models_config(parser.parse_args().models_config)
    runner_config = utils.load_runner_config(parser.parse_args().models_config)

    models_to_run = []
    openai_models_models_to_run = []
    # check if args.models are in models_config

    if args.models:
        for model in args.models:
            if model not in models_config["models"]:
                logger.error(f"Model {model} not found in models_config")
                sys.exit(-1)
            else:
                models_to_run.append(models_config["models"][model])

    # check if args.custom_models are in models_config
    if args.custom_models:
        for model in args.custom_models:
            if model not in models_config["custom_models"]:
                logger.error(f"Model {model} not found in models_config")
                sys.exit(-1)
            else:
                models_to_run.append(models_config["custom_models"][model])

    if args.openai_models:
        for model in args.openai_models:
            if model not in models_config["openai_models"]:
                logger.error(f"Model {model} not found in models_config")
                sys.exit(-1)
            else:
                openai_models_models_to_run.append(
                    models_config["openai_models"][model]
                )

    main_runner(args, runner_config, models_to_run, openai_models_models_to_run)

# python /home/ssegpu/JavaLibMethodInfo/llm_eval/src/runner.py \
# --bechmark_path /home/ssegpu/JavaLibMethodInfo/owasp-benchmark-srm-list.json \
# --prompt_id prompt_1 \
# --models qwen2-it-7b qwen2-it-72b gemma2-it-9b gemma2-it-27b gemma2-it-2b codellama-it-7b codellama-it-13b codellama-it-34b llama3.1-it-8b llama3.1-it-70b tinyllama-1.1b phi3-small-it-7.3b phi3-medium-it-14b phi3-mini-it-3.8b phi3.5-mini-it-3.8b phi3.5-moe-it-41.9b mixtral-v0.1-it-8x22b mixtral-v0.1-it-8x7b mistral-v0.3-it-7b mistral-nemo-it-2407-12.2b mistral-large-it-2407-123b codestral-v0.1-22b \
# --hf_token hf \
# --openai_key sk \
# --enable_streaming True \
# --models_config /home/ssegpu/JavaLibMethodInfo/llm_eval/src/models_config.yaml \
# --results_dir /home/ssegpu/JavaLibMethodInfo/.scrapy/dataset_results

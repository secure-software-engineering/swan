import json
import os
import re
import shutil
import sys
import yaml

import requests
import logging
import prompts
import copy
import csv
from owasp_preprocessor import OwaspProcessor

logger = logging.getLogger("runner")
logger.setLevel(logging.DEBUG)

owasp_processor = OwaspProcessor()

VALID_LABELS_SRM = ["source", "propagator", "sanitizer", "sink", "none"]
VALID_LABELS_CWE = [
    "CWE-20",
    "CWE-22",
    "CWE-77",
    "CWE-78",
    "CWE-79",
    "CWE-89",
    "CWE-90",
    "CWE-94",
    "CWE-119",
    "CWE-125",
    "CWE-190",
    "CWE-269",
    "CWE-276",
    "CWE-287",
    "CWE-306",
    "CWE-327",
    "CWE-328",
    "CWE-330",
    "CWE-352",
    "CWE-362",
    "CWE-416",
    "CWE-434",
    "CWE-476",
    "CWE-501",
    "CWE-502",
    "CWE-614",
    "CWE-643",
    "CWE-787",
    "CWE-798",
    "CWE-862",
    "CWE-863",
    "CWE-918",
    "none",
]
SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
REPO_DIR = os.path.join(SCRIPT_DIR, "../..")

OWASP_BENCHMARK_PATH = os.path.join(REPO_DIR, "BenchmarkJava")


class JsonException(Exception):
    pass


class TimeoutException(Exception):
    pass


def is_ollama_online(server_url):
    try:
        res = requests.get(server_url)
        # Check if the request was successful
        if res.status_code == 200:
            # Check the content of the response
            if res.text == "Ollama is running":
                return True
        return False
    except requests.exceptions.RequestException as e:
        # Handle any exceptions that occur during the request
        print(f"An error occurred: {e}")
        return False


def copy_folder(src, dst):
    """
    Copies a folder from the source (src) to the destination (dst).

    :param src: Source folder path
    :param dst: Destination folder path
    """
    # Check if the source directory exists
    if not os.path.exists(src):
        print(f"Source folder {src} does not exist.")
        return

    # Check if the destination directory exists, if so, remove it
    if os.path.exists(dst):
        shutil.rmtree(dst)
        print(f"Existing folder at {dst} has been removed.")

    # Copy the folder
    shutil.copytree(src, dst, dirs_exist_ok=True)
    print(f"Folder copied from {src} to {dst}")


def is_running_in_docker():
    """Check if Python is running inside a Docker container."""
    return (
        os.path.exists("/.dockerenv")
        or os.environ.get(  # Check if the /.dockerenv file exists
            "DOCKER_CONTAINER", False
        )
        or os.environ.get(  # Check if DOCKER_CONTAINER environment variable is set
            "DOCKER_IMAGE_NAME", False
        )  # Check if DOCKER_IMAGE_NAME environment variable is set
    )


def generate_json_file(filename, type_info):
    # Generate JSON file with type information
    try:
        if isinstance(type_info, list):
            pass
        else:
            type_info = json.loads(type_info)
        is_valid_json = True
    except Exception as e:
        is_valid_json = False
        print(f"Not a valid JSON: {e}")

    json_data = json.dumps(type_info, indent=4)
    with open(filename, "w") as file:
        file.write(json_data)

    return is_valid_json


def generate_json_from_answers(gt_json_file, answers):
    try:
        with open(gt_json_file, "r") as file:
            gt_data = json.load(file)

        pattern = re.compile(r"^\s*(\d+)\.\s+(.+)\s*$", re.MULTILINE)
        parsed_answers = pattern.findall(answers)

        parsed_answers = {int(x) - 1: y for x, y in parsed_answers}
        # if len(gt_data) != len(parsed_answers):
        #     return []

        answers_json_data = []
        for fact in range(len(gt_data)):
            _result = gt_data[fact]
            _result.pop("type")
            if fact in parsed_answers:
                _result["type"] = [x.strip() for x in parsed_answers[fact].split(",")]
                answers_json_data.append(_result)

        return answers_json_data
    except Exception as e:
        print("Error generating json from questions")
        print(e)
        return []


def generate_answers_for_fine_tuning(json_file):
    # Read and parse the JSON file
    with open(json_file, "r") as file:
        data = json.load(file)

    counter = 1
    answers = []
    for fact in data:
        answers.append(f"{counter}. {', '.join(fact['type'])}")
        counter += 1

    return "\n".join(answers)


def generate_questions_from_json(json_file):
    # Read and parse the JSON file
    with open(json_file, "r") as file:
        data = json.load(file)

    questions = []

    for entry in data:
        file = entry["file"]
        line_number = entry["line_number"]
        col_offset = entry["col_offset"]

        # Generate different questions based on the content of each entry
        # Function Return type
        if "function" in entry and "parameter" not in entry and "variable" not in entry:
            question = (
                "What is the return type of the function"
                f" '{entry['function']}' at line {line_number}, column"
                f" {col_offset}?"
            )
        # Function Parameter type
        elif "parameter" in entry:
            question = (
                f"What is the type of the parameter '{entry['parameter']}' at line"
                f" {line_number}, column {col_offset}, within the function"
                f" '{entry['function']}'?"
            )
        # Variable in a function type
        elif "variable" in entry and "function" not in entry:
            question = (
                f"What is the type of the variable '{entry['variable']}' at line"
                f" {line_number}, column {col_offset}?"
            )
        elif "variable" in entry and "function" in entry:
            question = (
                f"What is the type of the variable '{entry['variable']}' at line"
                f" {line_number}, column {col_offset}, within the function"
                f" '{entry['function']}'?"
            )
        else:
            print("ERROR! Type could not be converted to types")
        questions.append(question)

    if len(data) != len(questions):
        print("ERROR! Type questions length does not match json length")
        sys.exit(-1)

    questions = [f"{x}. {y}" for x, y in zip(range(1, len(questions) + 1), questions)]
    return questions


def load_models_config(config_path):
    models_config = {"models": {}, "custom_models": {}, "openai_models": {}}
    with open(config_path, "r") as file:
        config_data = yaml.safe_load(file)
        for model_data in config_data["models"]:
            models_config["models"][model_data["name"]] = model_data
        for model_data in config_data["custom_models"]:
            models_config["custom_models"][model_data["name"]] = model_data
        for model_data in config_data["openai_models"]:
            models_config["openai_models"][model_data["name"]] = model_data

    return models_config


def load_runner_config(config_path):
    with open(config_path, "r") as file:
        config_data = yaml.safe_load(file)

    return config_data["runner_config"]


def get_usage_contexts(data, number_of_lines=10, number_of_contexts=2):
    usage_contexts = []

    for usage_context in data["usages"]:
        file_path_usage = os.path.join(OWASP_BENCHMARK_PATH, usage_context["path"])
        if os.path.exists(file_path_usage):
            with open(file_path_usage, "r") as file:
                full_file_content = file.read()
                # Get the file content around the line number based on usage_context["lineNumber"],
                start_line = max(usage_context["lineNumber"] - (number_of_lines + 1), 0)
                end_line = usage_context["lineNumber"] + number_of_lines
                selected_lines = "\n".join(
                    full_file_content.split("\n")[start_line:end_line]
                )
                filtered_lines = owasp_processor.apply_processing_steps(selected_lines)

                usage_contexts.append(filtered_lines)

    return usage_contexts[:number_of_contexts]


def get_prompt(
    prompt_id,
    data,
    answers_placeholders=True,
    use_system_prompt=True,
):
    if prompt_id.startswith("prompt_cwe"):
        prompt_type = "cwe"
    elif prompt_id.startswith("prompt_explained"):
        prompt_type = "explained"
    elif prompt_id.startswith("prompt_simple"):
        prompt_type = "simple"

    use_context = False
    usage_contexts = None
    if prompt_id.endswith("_contexts"):
        use_context = True

        usage_contexts = get_usage_contexts(data)
        usage_contexts_str = ""
        for i, context in enumerate(usage_contexts):
            usage_contexts_str += f"Context {i+1}:\n{context}\n\n"

    if prompt_id in [
        "prompt_simple_sig",
        "prompt_explained_sig",
        "prompt_cwe_sig",
        "prompt_simple_sig_contexts",
        "prompt_explained_sig_contexts",
        "prompt_cwe_sig_contexts",
    ]:
        prompt = prompts.create_prompt(
            prompt_type,
            signature=data["signature"],
            include_sys_prompt=use_system_prompt,
            contexts=usage_contexts_str if use_context else None,
        )
    elif prompt_id in [
        "prompt_simple_sig_code",
        "prompt_explained_sig_code",
        "prompt_cwe_sig_code",
        "prompt_simple_sig_code_contexts",
        "prompt_explained_sig_code_contexts",
        "prompt_cwe_sig_code_contexts",
    ]:
        prompt = prompts.create_prompt(
            prompt_type,
            code=data["body"],
            signature=data["signature"],
            include_sys_prompt=use_system_prompt,
            contexts=usage_contexts_str if use_context else None,
        )
    elif prompt_id in [
        "prompt_simple_sig_code_doc",
        "prompt_explained_sig_code_doc",
        "prompt_cwe_sig_code_doc",
        "prompt_simple_sig_code_doc_contexts",
        "prompt_explained_sig_code_doc_contexts",
        "prompt_cwe_sig_code_doc_contexts",
    ]:
        prompt = prompts.create_prompt(
            prompt_type,
            code=data["body"],
            signature=data["signature"],
            method_doc=data["javadoc"]["method"],
            class_doc=data["javadoc"]["class"],
            include_sys_prompt=use_system_prompt,
            contexts=usage_contexts_str if use_context else None,
        )

    else:
        logger.error(f"Prompt ID {prompt_id} not found.")
        sys.exit(-1)

    return prompt


# Function to parse valid tokens from output generated by llm
def parse_response(input_string, valid_labels):

    def create_variations(item):
        base = item.replace("_", " ")
        return {
            base: item,
            base.replace(" ", "_"): item,
            base.replace(" ", "-"): item,
            base.replace("_", " "): item,
            base.replace("-", " "): item,
            base.replace("_", "-"): item,
            base.replace("-", "_"): item,
            base.replace("\\_", "_"): item,
        }

    variation_map = {}
    for item in valid_labels:
        variation_map.update(create_variations(item))

    clean_string = re.sub(r"[^\x00-\x7F]+", " ", input_string)

    clean_string = re.sub(r"[^\w\s,\\]+", " ", clean_string)
    clean_string = re.sub(r"\\_", "_", clean_string)
    clean_string = clean_string.strip()

    matches = []
    for variation, canonical in variation_map.items():
        if re.search(r"\b" + re.escape(variation) + r"\b", clean_string, re.IGNORECASE):
            matches.append(canonical)

    valid_tokens = list(set(matches))

    return valid_tokens


# Function to write the response from llm and parsed_response to a csv file with ID of the record for further analysis
def response_to_csv(
    id, output, expected, results_dump_file, prompt_time=0, evaluation_type="srm"
):

    columns = [
        "ID",
        "response_from_model",
        "parsed_response",
        "ground_truth",
        "prompt_time",
    ]

    if evaluation_type == "srm":
        valid_labels = VALID_LABELS_SRM
    elif evaluation_type == "cwe":
        valid_labels = VALID_LABELS_CWE

    parsed_labels = parse_response(output, valid_labels=valid_labels)

    data = [[id, output, parsed_labels, expected, prompt_time]]

    with open(results_dump_file, "a", newline="") as file:
        writer = csv.writer(file)

        if file.tell() == 0:
            writer.writerow(columns)

        writer.writerows(data)


# Example usage:
# loader = ConfigLoader("models_config.yaml")
# loader.load_config()
# models = loader.get_models()
# for model in models:
#     print(model.name, model.model_path)

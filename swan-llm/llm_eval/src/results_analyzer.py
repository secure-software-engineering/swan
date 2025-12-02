import os
from sklearn.metrics import (
    classification_report,
    accuracy_score,
    hamming_loss,
    jaccard_score,
)
import pandas as pd
from pathlib import Path
from sklearn.preprocessing import MultiLabelBinarizer

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


def generate_multi_label_classification_report(results_dir, evaluation_type="srm"):
    if evaluation_type == "srm":
        valid_labels = VALID_LABELS_SRM
    elif evaluation_type == "cwe":
        valid_labels = VALID_LABELS_CWE
    output_results_dst = results_dir / "classification_reports"

    os.makedirs(output_results_dst, exist_ok=True)

    # find all csv files in the results directory
    llm_results_files = sorted(Path(results_dir).rglob("*_results_dump.csv"))
    target_labels = valid_labels

    consolidated_report = {}

    for results_dump_file in llm_results_files:
        llm_name = os.path.basename(results_dump_file).split("_results_dump.csv")[0]
        data_df = pd.read_csv(results_dump_file)
        predicted_labels = data_df["parsed_response"].apply(lambda x: eval(str(x)))
        if evaluation_type == "srm":
            ground_truth_labels = data_df["ground_truth"].apply(lambda x: eval(str(x)))
        elif evaluation_type == "cwe":
            ground_truth_labels = data_df["ground_truth"].apply(
                lambda x: [i.replace("CWE", "CWE-") for i in eval(str(x))]
            )

        mlb = MultiLabelBinarizer(classes=target_labels)
        mlb.fit([[]])
        ground_truth_binary = mlb.transform(ground_truth_labels)
        pred_response_binary = mlb.transform(predicted_labels)

        report = classification_report(
            y_true=ground_truth_binary,
            y_pred=pred_response_binary,
            target_names=target_labels,
            output_dict=True,
        )

        df = pd.DataFrame(report).transpose()

        df.to_csv(
            output_results_dst / f"{llm_name}_multi_label_classification_report.csv"
        )

        subset_accuracy = accuracy_score(ground_truth_binary, pred_response_binary)
        hamming_loss_val = hamming_loss(ground_truth_binary, pred_response_binary)
        jaccard_similarity = jaccard_score(
            ground_truth_binary, pred_response_binary, average="samples"
        )

        consolidated_report[llm_name] = {
            "subset_accuracy": subset_accuracy,
            "hamming_loss_val": hamming_loss_val,
            "jaccard_similarity": jaccard_similarity,
            "precision_micro_avg": report["micro avg"]["precision"],
            "recall_micro_avg": report["micro avg"]["recall"],
            "f1_micro_avg": report["micro avg"]["f1-score"],
            "support_micro_avg": report["micro avg"]["support"],
            "precision_macro_avg": report["macro avg"]["precision"],
            "recall_macro_avg": report["macro avg"]["recall"],
            "f1_macro_avg": report["macro avg"]["f1-score"],
            "support_macro_avg": report["macro avg"]["support"],
            "precision_weighted_avg": report["weighted avg"]["precision"],
            "recall_weighted_avg": report["weighted avg"]["recall"],
            "f1_weighted_avg": report["weighted avg"]["f1-score"],
            "support_weighted_avg": report["weighted avg"]["support"],
            "precision_samples_avg": report["samples avg"]["precision"],
            "recall_samples_avg": report["samples avg"]["recall"],
            "f1_samples_avg": report["samples avg"]["f1-score"],
            "support_samples_avg": report["samples avg"]["support"],
        }

    consolidated_df = pd.DataFrame(consolidated_report).transpose()
    consolidated_df.to_csv(
        output_results_dst / "consolidated_classification_report.csv", mode="a"
    )


if __name__ == "__main__":
    RESULTS_DIR = Path(
        "/home/ssegpu/JavaLibMethodInfo/.scrapy/results_cwe_1/2024-10-15_12-17-23"
    )
    generate_multi_label_classification_report(RESULTS_DIR, "cwe")

import os
import re
import shutil
import pandas as pd
import hashlib


class OwaspProcessor:
    """
    Process OWASP code examples.

    Parameters:
    - owasp_dir (str): The directory path where the OWASP dataset is located.
    - useCache (bool, optional): Flag indicating whether to use caching for processed examples. Defaults to True.
    - saveFiles (bool, optional): Flag indicating whether to save obfuscated files. Defaults to False.

    Attributes:
    - examples_dir (str): The directory path where the original code examples are located.
    - obfuscated_dir (str): The directory path where the obfuscated code examples will be saved.
    - groundTruth (str): The path to the file containing the ground truth.
    - cache_manager (CacheManager): An instance of the CacheManager class for managing caching.
    - useCache (bool): Flag indicating whether to use caching for processed examples.
    - saveFiles (bool): Flag indicating whether to save obfuscated files.
    - processing_options (dict): A dictionary containing the processing options.
    """

    def setup_obfuscated_dir(self):
        """
        Creates the obfuscated directory if it doesn't exist.
        """
        if not os.path.exists(self.obfuscated_dir):
            os.makedirs(self.obfuscated_dir)

    def rename(self, name, prefix):
        """
        Rename the given name by appending a prefix and a hash of the name.

        Parameters:
        - name (str): The original name to be renamed.
        - prefix (str): The prefix to be appended to the renamed name.

        Returns:
        - str: The renamed name.
        """
        return f"{prefix}_{hashlib.md5(name.encode()).hexdigest()[:8]}"

    def remove_multiline_comments(self, java_code):
        """
        Removes multiline comments from the given Java code.

        Parameters:
        - java_code (str): The Java code.

        Returns:
        - str: The Java code with multiline comments removed.
        """
        pattern = re.compile(r"/\*.*?\*/", re.DOTALL)
        cleaned_code = re.sub(pattern, "", java_code)
        return cleaned_code

    def remove_import_statements(self, java_code):
        """
        Removes import statements from the given Java code.

        Parameters:
        - java_code (str): The Java code.

        Returns:
        - str: The Java code with import statements removed.
        """
        lines = java_code.split("\n")
        cleaned_lines = [
            line for line in lines if not line.strip().startswith("import")
        ]
        return "\n".join(cleaned_lines)

    def remove_package_declarations(self, java_code):
        """
        Removes package declarations from the given Java code.

        Parameters:
        - java_code (str): The Java code.

        Returns:
        - str: The Java code with package declarations removed.
        """
        lines = java_code.split("\n")
        cleaned_lines = [
            line for line in lines if not line.strip().startswith("package")
        ]
        return "\n".join(cleaned_lines)

    def replace_benchmark_names(self, java_code):
        """
        Replaces benchmark related names in the given Java code with dummy names.

        Parameters:
        - java_code (str): The Java code.

        Returns:
        - str: The Java code with benchmark names replaced.
        """
        pattern = re.compile(r"\b(owasp|benchmark)\b", re.IGNORECASE)
        return pattern.sub(
            lambda match: self.replace_with_dummy(match, "suite"), java_code
        )

    def replace_cwe_names(self, java_code):
        """
        Replaces CWE names in the given Java code with dummy names.

        Parameters:
        - java_code (str): The Java code.

        Returns:
        - str: The Java code with CWE names replaced.
        """
        cwe_names = [
            "pathtraver",
            "hash",
            "trustbound",
            "crypto",
            "cmdi",
            "xss",
            "securecookie",
            "ldapi",
            "weakrand",
            "xpathi",
            "sqli",
        ]
        pattern = re.compile(r"\b(" + "|".join(cwe_names) + r")\b", re.IGNORECASE)
        return pattern.sub(
            lambda match: self.replace_with_dummy(match, "cat"), java_code
        )

    def replace_with_dummy(self, match, prefix):
        """
        Replaces the matched keyword with a dummy name.

        Parameters:
        - match (re.Match): The matched keyword.
        - prefix (str): The prefix to be used for renaming.

        Returns:
        - str: The renamed name (prefix_{hash of keyword}).
        """
        keyword = match.group().lower()
        return self.rename(keyword, prefix)

    def apply_processing_steps(self, java_code):
        """
        Applies the processing steps to the given Java code.

        Parameters:
        - java_code (str): The Java code.

        Returns:
        - tuple: A tuple containing the processed Java code and the new class name.
        """
        processing_steps = {
            "remove_multiline_comments": self.remove_multiline_comments,
            "remove_import_statements": self.remove_import_statements,
            "remove_package_declarations": self.remove_package_declarations,
            "replace_benchmark_names": self.replace_benchmark_names,
            "replace_cwe_names": self.replace_cwe_names,
        }

        new_class_name = None
        for step, method in processing_steps.items():
            java_code = method(java_code)

        return java_code

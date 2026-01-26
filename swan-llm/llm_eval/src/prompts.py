# Define system and user prompts as reusable templates
system_prompt_expl = """
You are an expert Java secure-code reviewer and static analysis assistant. Your task is to classify Java code artifacts into the following security-relevant method (SRM) categories required for taint analysis: ['source', 'sanitizer', 'sink', 'none']. Note that taint analysis is a technique used to identify security vulnerabilities in software applications by tracking the flow of sensitive data from sources to sinks.

Possible Inputs: For each query, you may receive one or more of the following (sometimes only one type, sometimes a mix):
- Method signature (e.g., name, parameters, return type)
- Documentation / Javadoc / comments describing the method
- Invocation / call site(s) showing how the method is used
- Method body (full or partial source code)

Task: For the given input:
- Infer the method's purpose and behaviour from whatever is provided (signature, docs, invocations, body)
- Map provided method to one or more SRMs.

Scope and typical SRM categories to consider:
- Source: introduces untrusted or confidential data into an application which is called tainted data.
- Sanitizer: sanitizes tainted data by removing sensitive data.
- Sink: performs sensitive operations, which leads to a security vulnerability or data leak.
- None: if the method does not belong to any of the above categories.
"""

system_prompt_simple = """
You are an expert Java secure-code reviewer and static analysis assistant. Your task is to classify Java code artifacts into the following security-relevant method (SRM) categories required for taint analysis: ['source', 'sanitizer', 'sink', 'none'].

Possible Inputs: For each query, you may receive one or more of the following (sometimes only one type, sometimes a mix):
- Method signature (e.g., name, parameters, return type)
- Documentation / Javadoc / comments describing the method
- Invocation / call site(s) showing how the method is used
- Method body (full or partial source code)

Task: For the given input:
- Infer the method's purpose and behaviour from whatever is provided (signature, docs, invocations, body)
- Map provided method to one or more SRMs.
"""

cwe_list = """
- CWE-79: Cross-site Scripting
- CWE-89: SQL Injection
"""

system_prompt_cwe = """
You are an expert Java secure-code reviewer and static analysis assistant. Your task is to classify Java code artifacts into relevant Common Weakness Enumeration (CWE) categories.

Possible Inputs: For each query, you may receive one or more of the following (sometimes only one type, sometimes a mix):
- Method signature (e.g., name, parameters, return type)
- Documentation / Javadoc / comments describing the method
- Invocation / call site(s) showing how the method is used
- Method body (full or partial source code)

Task: For the given input:
- Infer the method's purpose and behaviour from whatever is provided (signature, docs, invocations, body)
-Identify potential security weaknesses relevant to CWE, based on:
    - Data sources (user input, network, files, environment, DB, etc.)
    - Data sinks (SQL queries, file system, network, HTML/JS, OS commands, logs, etc.)
    - API usage, error handling, authentication/authorization, cryptography, etc.
- Map each identified weakness to one or more CWE IDs and CWE names.

Scope and typical CWE's to consider:
{cwe_list}
"""

SYSTEM_PROMPT_MAP = {
    "simple": system_prompt_simple,
    "explained": system_prompt_expl,
    "cwe": system_prompt_cwe.format(cwe_list=cwe_list),
}


# Define a function for user prompt construction
def create_user_prompt(
    code=None, signature=None, method_doc=None, class_doc=None, contexts=None
):
    user_prompt = "Identify to which category the provided code snippet and fully qualified method signature belongs. Assign the code snippet to at least 1 but up to 2 categories. If the code snippet does not belong to any of the categories, assign it to the 'none' category. Strictly provide your response as a list. Avoid providing any additional information, commentary, or personal opinions.\n"

    if code:
        user_prompt += f"\nJava Code:\n```{code}```\n"
    if signature:
        user_prompt += f"\nMethod Signature:\n```{signature}```\n"
    if method_doc:
        user_prompt += f"\nMethod Documentation:\n```{method_doc}```\n"
    if class_doc:
        user_prompt += f"\nClass Documentation:\n```{class_doc}```\n"
    if contexts:
        user_prompt += f"\nMethod Usage Contexts:\n```{contexts}```\n"

    return user_prompt


# Define a function to create a full prompt set
def create_prompt(
    system_prompt_id,
    code=None,
    signature=None,
    method_doc=None,
    class_doc=None,
    contexts=None,
    include_sys_prompt=True,
):
    if system_prompt_id in SYSTEM_PROMPT_MAP:
        system_prompt = SYSTEM_PROMPT_MAP[system_prompt_id]
    else:
        raise ValueError(f"Invalid system prompt type: {system_prompt_id}")

    user_content = create_user_prompt(
        code=code,
        signature=signature,
        method_doc=method_doc,
        class_doc=class_doc,
        contexts=contexts,
    )

    if include_sys_prompt:
        prompt = [
            {"role": "system", "content": system_prompt},
            {"role": "user", "content": user_content},
        ]
    else:
        prompt = [{"role": "user", "content": system_prompt + user_content}]

    return prompt


# Prompts

# 1
# "prompt_simple_sig",
# "prompt_simple_sig_contexts",

# "prompt_explained_sig",
# "prompt_explained_sig_contexts",

# "prompt_cwe_sig",
# "prompt_cwe_sig_contexts",

# 2
# "prompt_simple_sig_code",
# "prompt_simple_sig_code_contexts",

# "prompt_explained_sig_code",
# "prompt_explained_sig_code_contexts"

# "prompt_cwe_sig_code",
# "prompt_cwe_sig_code_contexts",

# 3
# "prompt_simple_sig_code_doc",
# "prompt_simple_sig_code_doc_contexts",

# "prompt_explained_sig_code_doc",
# "prompt_explained_sig_code_doc_contexts",

# "prompt_cwe_sig_code_doc",
# "prompt_cwe_sig_code_doc_contexts",

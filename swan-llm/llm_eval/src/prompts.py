# Define system and user prompts as reusable templates
system_prompt_expl = """
You are a security expert who can classify Java methods into the following security-relevant method (SRM) categories required for taint analysis: ['source', 'propagator', 'sanitizer', 'sink', 'none']. Note that taint analysis is a technique used to identify security vulnerabilities in software applications by tracking the flow of sensitive data from sources to sinks.

There are four SRM categories:   

- Source: introduces untrusted or confidential data into an application which is called tainted data.
- Propagator: propagates tainted data from one variable to another.
- Sanitizer: sanitizes tainted data by removing sensitive data.
- Sink: performs sensitive operations, which leads to a security vulnerability or data leak.
- None: if the method does not belong to any of the above categories.
"""

system_prompt_simple = """
You are a security expert who can classify Java methods into the following security-relevant method (SRM) categories required for taint analysis: ['source', 'propagator', 'sanitizer', 'sink', 'none'].
"""

cwe_list = """
- CWE-20: Improper Input Validation
- CWE-22: Path Traversal
- CWE-77: Command Injection
- CWE-78: OS Command Injection
- CWE-79: Cross-site Scripting
- CWE-89: SQL Injection
- CWE-90: LDAP Injection
- CWE-94: Code Injection
- CWE-119: Improper Restriction of Operations within the Bounds of a Memory Buffer
- CWE-125: Out-of-bounds Read
- CWE-190: Integer Overflow or Wraparound
- CWE-269: Improper Privilege Management
- CWE-276: Incorrect Default Permissions
- CWE-287: Improper Authentication
- CWE-306: Missing Authentication for Critical Function
- CWE-327: Weak Cryptography
- CWE-328: Weak Hashing
- CWE-330: Weak Randomness
- CWE-352: Cross-Site Request Forgery
- CWE-362: Race Condition
- CWE-416: Use After Free
- CWE-434: Unrestricted Upload of File with Dangerous Type
- CWE-476: NULL Pointer Dereference
- CWE-501: Trust Boundary Violation
- CWE-502: Deserialization of Untrusted Data
- CWE-614: Secure Cookie Flag
- CWE-643: XPATH Injection
- CWE-787: Out-of-bounds Write
- CWE-798: Use of Hard-coded Credentials
- CWE-862: Missing Authorization
- CWE-863: Incorrect Authorization
- CWE-918: Server-Side Request Forgery
"""

system_prompt_cwe = """
You are a security expert who can classify Java methods into the following CWE categories.

CWE categories:
{cwe_list}

Note that a method can belong to multiple categories. If the method does not belong to any of the categories, assign it to the 'none' category.
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

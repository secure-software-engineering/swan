# SWAN (Security methods for WeAkNess detection)

What is SWAN?
-------------
SWAN is a machine-learning approach used to detect of security-relevant methods (SRM) in Java programs.
SWAN should be used in combination with other static analyses tools and it helps the users to create a set of relevant methods required as an input for static analyses, e.g. taint- and type-state analysis.
The tool currently detects four types of security relevant methods, namely: source, sink, sanitizer, and authentication methods.
The detected methods are further categorized according to relevant vulnerabilities from the [Common Weakness Enumeration (CWE)](https://cwe.mitre.org/). The following CWEs are currently supported: [CWE78 OS Command Injection](https://cwe.mitre.org/data/definitions/78.html), [CWE79 Cross-site Scripting](https://cwe.mitre.org/data/definitions/79.html), [CWE89 SQL Injection](https://cwe.mitre.org/data/definitions/89.html), [CWE306 Missing Authentication](https://cwe.mitre.org/data/definitions/306.html), [CWE601 Open Redirect](https://cwe.mitre.org/data/definitions/601.html), [CWE862 Missing Authorisation](https://cwe.mitre.org/data/definitions/862.html), and
[CWE863 Incorrect Authorisation](https://cwe.mitre.org/data/definitions/863.html).

The project contains the following modules:
* **swan-pipeline**: core machine learning implementation for SWAN with components for data collection and preparation, feature engineering and model selection phases
* **swan-assist**: IntelliJ plugin provides GUI support for SWAN and enables active machine learning.
* **swan-javadoc-exporter**: Doclet exports doc comments to XML files so that they can be analyzed by the Natural Language Processing (NLP) module
* **doc-coverage-doclet**: Doclet calculates the software documentation coverage of Java programs based on the presence of doc comments for classes, methods, and other objects.

How do I get started with SWAN?
-------------
The easiest way to get started with SWAN is to use the pre-built binary from the newest release. To run SWAN, we provide a path to the Java project to be analyzed (JAR files or compiled classes) as well an output directory where SWAN will export its results.

After downloading the necessary files from the most recent release, SWAN can be executed on the command line with the following command:

<code>java -jar swan-<swan-version>.jar -test <java-project-path> -output <output-directory></code>

This command runs the application and exports the detected security-relevant methods to a JSON file in the provided output directory. The available command line options can be found in the Wiki or by using the <code>-help</code> command line option.

How do I build SWAN?
-------------
If you downloaded SWAN as a compressed release (e.g. .zip or .tar.gz), you can use <code>mvn package</code> to package the project. The commands provided above can then be used to run the generated JAR file. Alternatively, you can import the project directly into your IDE from the repository and package the project via the terminal or the Maven plugin in your IDE.


Contributors
-------------
The following persons have contributed to SWAN: Goran Piskachev (goran.piskachev@iem.fraunhofer.de), Lisa Nguyen (lisa.nguyen@uni-paderborn.de), Oshando Johnson (oshando@iem.fraunhofer.de), Eric Bodden (eric.bodden@uni-paderborn.de)


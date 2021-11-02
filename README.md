# swan
Security methods for WeAkNess detection

What is SWAN? 
-------------
SWAN is a machine-learning approach used to detect of security relevant methods in Java libraries. 
SWAN should be used in combination with other static analyses tools and it helps the users to create a set of relevant methods required as an input for static analyses, e.g. taint- and type-state analysis. 
The tool currently detects four types of security relevant methods: source, sink, sanitizer, and authentication methods. 
The detected methods are further categorized according to relevant vulnerabilities from the [Common Weakness Enumeration (CWE)](https://cwe.mitre.org/). Currently, SWAN supports the following CWEs: [CWE78 OS Command Injection](https://cwe.mitre.org/data/definitions/78.html), [CWE79 Cross-site Scripting](https://cwe.mitre.org/data/definitions/79.html), [CWE89 SQL Injection](https://cwe.mitre.org/data/definitions/89.html), [CWE306 Missing Authentication](https://cwe.mitre.org/data/definitions/306.html), [CWE601 Open Redirect](https://cwe.mitre.org/data/definitions/601.html), [CWE862 Missing Authorisation](https://cwe.mitre.org/data/definitions/862.html), and
[CWE863 Incorrect Authorisation](https://cwe.mitre.org/data/definitions/863.html).

The project contains the following modules: 
* **swan-pipeline**: core machine-learning approach implementation for SWAN. Application can be run using CLI. 
* **swan-assist**: GUI support for SWAN implemented as an IntelliJ plugin. 
* **swan-javadoc-exporter**: Doclet for exporting doc comments to XML files
* **swan-javadoc-coverage**: Doclet to calculate the documentation coverage of Java programs

How do I get started with SWAN?
-------------
The easiest way to get started with SWAN is to use the pre-built binary from the newest Release. After downloading the necessary files, we can run the JAR file with this command: 

<code>java -jar swan-pipeline/target/swan-<version>-jar-with-dependencies.jar -output <output-directory></code>

This command runs the application and stores the application's output in the specified output directory. Below are some of the most common command line options. The complete list of command line options can be viewed by providing the <code>-h</code> or <code>-help</code> command line option.


| Parameter        | Description    |
| -------------------------- |:---------------------------------------|
| <code>-train</code> or <code>-train-data</code>       | Path to training JAR/class files. Default: Path to [/input/train-data](./swan-pipeline/src/main/resources/input/train-data)| 
| <code>-d</code> or <code>-dataset</code>       | Path to JSON file that contains training examples. Default: Path to [swan-dataset.json](./swan-pipeline/src/main/resources/input/swan-dataset.json) |
| <code>-s</code> or <code>-srm</code>       | List of security-relevant types that should be classified. Options: <code>all</code>, <code>source</code>, <code>sink</code>, <code>sanitizer</code>, <code>authentication</code>. Default: <code>all</code> | 
| <code>-c</code> or <code>-cwe</code>       | List of CWE types that should be classified. Options: <code>cwe078</code>, <code>cwe079</code>, s<code>cwe089</code>, <code>cwe306</code>, <code>cwe601</code>, <code>cwe862</code> and <code>cwe863</code>. Default: <code>all</code> | 


How do I build SWAN?
-------------
If you downloaded SWAN as a compressed release (e.g. .zip or .tar.gz), you can use <code>mvn package</code> to package the project. Alternatively, you can import the project directly into your IDE from the repository and package the project via the terminal or the Maven plugin in your IDE. 


Contributors 
-------------
The following persons have contributed to SWAN: Goran Piskachev (goran.piskachev@iem.fraunhofer.de), Lisa Nguyen (lisa.nguyen@uni-paderborn.de), Oshando Johnson (oshando@iem.fraunhofer.de), Eric Bodden (eric.bodden@uni-paderborn.de)


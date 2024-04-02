# SWAN (Security methods for WeAkNess detection)

SWAN is a machine-learning approach that detects security-relevant methods (SRM) in Java programs.
SWAN should be used in combination with other static analyses tools and it helps the users to create a set of relevant methods required as an input for static analyses, e.g. taint- and type-state analysis.
The tool currently detects four types of security relevant methods, namely: source, sink, sanitizer, and authentication methods.
SWAN also labels methods as relevant for 7 [Common Weakness Enumeration (CWE)](https://cwe.mitre.org/), namely: [CWE78 OS Command Injection](https://cwe.mitre.org/data/definitions/78.html), [CWE79 Cross-site Scripting](https://cwe.mitre.org/data/definitions/79.html), [CWE89 SQL Injection](https://cwe.mitre.org/data/definitions/89.html), [CWE306 Missing Authentication](https://cwe.mitre.org/data/definitions/306.html), [CWE601 Open Redirect](https://cwe.mitre.org/data/definitions/601.html), [CWE862 Missing Authorisation](https://cwe.mitre.org/data/definitions/862.html), and
[CWE863 Incorrect Authorisation](https://cwe.mitre.org/data/definitions/863.html).

The project is divided into two main components: the command line tool [<code>**swan-cmd**</code>](https://github.com/secure-software-engineering/swan/tree/master/swan-cmd) and the IntelliJ plugin [<code>**dev-assist**</code>](https://github.com/secure-software-engineering/swan/tree/master/dev-assist) that provides a GUI for SWAN. 

1. <code>**swan-cmd**</code> is the command line implementation for SWAN with components for data collection, feature engineering, model selection and SRM prediction. The command line tool uses the following Maven modules:
    - <code>**training-data-jars**</code> contains dependencies from which the training examples are extracted.
    - Java Doclets to process and export software documentation
      - <code>**coverage-doclet**</code> calculates the software documentation coverage of Java programs based on the presence of doc comments for classes, methods, and other objects.
      - <code>**xml-doclet**</code> exports doc comments to XML files so that they can be analyzed by the Natural Language Processing (NLP) module
2. <code>**dev-assist**</code> provides GUI support for SWAN and enables active machine learning.


How do I get started with SWAN?
-------------
To run SWAN, you will need to provide a path to the Java project to be analyzed (JAR files or compiled classes) as well an output directory where SWAN will export its results. The easiest way to get started with SWAN is to use the pre-built binary from the newest release. After downloading the necessary files from the most recent release, SWAN can be executed on the command line with the following command:

<code>**java -jar swan-cmd-3.x.x.jar -test** */path/to/project/files* **-output** */output/directory* </code>

This command runs the application and exports the detected security-relevant methods to a JSON file in the provided output directory. This command uses the following default settings: training dataset <code>-in [dataset](/swan-cmd/src/main/resources/dataset)</code>, code features <code>-f code</code>, and the MEKA toolkit <code>-t meka</code>. The remaining default options are found in [CLIRunner](/swan-cmd/src/main/java/de/fraunhofer/iem/swan/cli/CliRunner.java). The available command line options can be found in the Wiki or by using the <code>-help</code> command line option.

How do I build SWAN?
-------------
If you cloned the project or downloaded SWAN as a compressed release (e.g. .zip or .tar.gz), you can use <code>mvn package</code> to package the project. The commands provided above can then be used to run the generated JAR file. Alternatively, you can import the project directly into your IDE from the repository and package the project via the terminal or the Maven plugin in your IDE.


Contributors
-------------
The following persons have contributed to SWAN: Goran Piskachev (gpiskach@amazon.de), Lisa Nguyen (lisa.nguyen@uni-paderborn.de), Oshando Johnson (oshando.johnson@iem.fraunhofer.de), Eric Bodden (eric.bodden@uni-paderborn.de).


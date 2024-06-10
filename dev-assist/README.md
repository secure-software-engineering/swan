## Dev-Assist IntelliJ Plugin

Dev-Assist is an IntelliJ IDEA plugin that uses [SWAN](https://github.com/secure-software-engineering/swan) to detect security-relevant methods (SRMs) that are required when configuring static analysis tools. After automatically detecting SRMs, the plugin can be used to adjust the list of security-relevant methods and also generate the tainit-flow specifications required to run the taint analysis tool [SecuCheck](https://github.com/secure-software-engineering/secucheck/).

The plugin works with IntelliJ IDEA 2022.2 and higher. 

### Plugin Features
The plugin has the following main features which are accessible in the plugin's tool window and from the editor:

- Detect security-relevant methods in Java programs with SWAN's machine learning approach
- Update security-relevant methods list using method dialog
  - Import existing SRM list
  - Add new SRMs from the editor
  - Update existing method (SRM labels, data-in/data-out and meta properties)
  - Delete SRMs
  - Filter SRM list
  - Expand/collapse method list
  - Export updated SRM list
- Generate [*fluent*TQL](https://github.com/secure-software-engineering/secucheck/tree/master) taint-flow specifications necessary to configure SecuCheck in order to detect vulnerabilities
- Run SecuCheck and displays results using Qodana 

### Installation

To install the plugin in IntelliJ IDEA:
- Download the [latest](https://github.com/secure-software-engineering/swan/releases) plugin archive file (ZIP or JAR) 
- Open the IDE settings and select <kbd>Plugins</kbd>
- On the <kbd>Plugins</kbd> page, click <kbd>Gear</kbd> icon and then click <kbd>Install plugin from disk...</kbd>.
- Select the Dev-Assist plugin archive file and select <kbd>OK</kbd>
- Click <kbd>OK</kbd> to apply the changes.
- Restart the IDE to complete the installation



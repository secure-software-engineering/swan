# mois-assist
IntelliJ IDEA Plug-in for the tool MOIS (Methods of interest for security)

Description: 
-------------
MOIS (/mwa/ security) is a machine-learning approach for detection of methods of interest for security in Java libraries. 
MOIS should be used in combination with other static analyses tools. It helps the users to create a set of relevant methods required as an input for static analyses, e.g. taint- and type-state analysis. 
MOIS detects four types of methods: source, sink, sanitizer, and authentication method. 
The found methods are further cathegorized according to relevant vulnerabilities (Common Weakness Enummeration  - CWE). Curretntly MOIS supports the following CWEs: CWE89, CWE79, CWE306, CWE862, and CWE863. 

MOIS-Assist provides a GUI support for MOIS. The user is able to interact with the learning process by giving feedback on the methods of interest. 
The tool helps users that write static analyses to create list of MOIS for their specific Java libraries. 
Moreover, users can manually inspect the proper usage of the methods detected by MOIS. 


Contributors:
* Goran Piskachev (goran.piskachev@iem.fraunhofer.de)
* Oshando Johnson (oshando@campus.uni-paderborn.de)
* Lisa Nguyen (lisa.nguyen@uni-paderborn.de)


Contact: 
-------------
Goran Piskachev (Fraunhofer IEM, Zukunftsmeile 1, 33102 Paderborn, Office: 02-13)

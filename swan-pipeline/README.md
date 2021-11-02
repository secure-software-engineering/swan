# swan
Security methods for WeAkNess detection (SWAN).

Description
------------
SWAN is a machine-learning approach for detection of methods of interest for security in Java libraries. 
SWAN should be used in combination with other static analyses tools. It helps the users to create a set of relevant methods required as an input for static analyses, e.g. taint- and type-state analysis. 
SWAN detects four types of methods: source, sink, sanitizer, and authentication method. 
The found methods are further categorized according to relevant vulnerabilities (Common Weakness Enumeration  - CWE). Currently, SWAN supports the following CWEs: CWE078, CWE079, CWE089, CWE306, CWE601, CWE862, and CWE863.

Main contributors:
* Goran Piskachev (goran.piskachev@iem.fraunhofer.de)
* Lisa Nguyen (lisa.nguyen@uni-paderborn.de)


The initial set of the features based on code information is contribution of Dr. Siegfried Rasthofer and Dr. Steven Arzt. 


Contact: 
-------------
Goran Piskachev (Fraunhofer IEM, Zukunftsmeile 1, 33102 Paderborn, Office: 02-05)

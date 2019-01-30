# swan
Security methods for WeAkNess detection

Description: 
-------------
SWAN is a machine-learning approach for detection of methods of interest for security in Java libraries. 
SWAN should be used in combination with other static analyses tools. It helps the users to create a set of relevant methods required as an input for static analyses, e.g. taint- and type-state analysis. 
SWAN detects four types of methods: source, sink, sanitizer, and authentication method. 
The found methods are further cathegorized according to relevant vulnerabilities (Common Weakness Enummeration  - CWE). Curretntly SWAN supports the following CWEs: CWE78, CWE79, CWE89, CWE306, CWE601, CWE862, and CWE863. 

SWAN_Assist provides a GUI support for SWAN. The user is able to interact with the learning process by giving feedback on the methods of interest. 
The tool helps users that write static analyses to create list of SWAN for their specific Java libraries. 
Moreover, users can manually inspect the proper usage of the methods detected by SWAN. 

Contributors:
* Goran Piskachev (goran.piskachev@iem.fraunhofer.de)
* Lisa Nguyen (lisa.nguyen@uni-paderborn.de)
* Oshando Johnson (oshando@iem.fraunhofer.de)
* Eric Bodden (eric.bodden@uni-paderborn.de)


Contact: 
-------------
Goran Piskachev (Fraunhofer IEM, Zukunftsmeile 1, 33102 Paderborn)

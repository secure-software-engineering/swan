# SWAN_Assist
IntelliJ IDEA Plug-in for the Security methods for WeAkNess detection (SWAN) tool.

Description: 
-------------
SWAN is a machine-learning approach for detection of methods of interest for security in Java libraries. 
SWAN should be used in combination with other static analyses tools. It helps the users to create a set of relevant methods required as an input for static analyses, e.g. taint- and type-state analysis. 
SWAN detects four types of methods: source, sink, sanitizer, and authentication method. 
The found methods are further cathegorized according to relevant vulnerabilities (Common Weakness Enummeration  - CWE). Curretntly SWAN supports the following CWEs: CWE89, CWE79, CWE306, CWE862, and CWE863. 

SWAN_Assist provides a GUI support for SWAN. The user is able to interact with the learning process by giving feedback on the methods of interest. 
The tool helps users that write static analyses to create list of SWAN for their specific Java libraries. 
Moreover, users can manually inspect the proper usage of the methods detected by SWAN. 

Contributors:
* Goran Piskachev (goran.piskachev@iem.fraunhofer.de)
* Oshando Johnson (oshando@campus.uni-paderborn.de)
* Lisa Nguyen (lisa.nguyen@uni-paderborn.de)

## Setting Up the Plugin

Import the project using either of the following methods:
##### Cloning Project from the Repository
1) Select the *File>Project from Version Control>Git* option, enter the repository’s URL and then select *Clone* to import the project.
2) Go to *File>Project Structure* to edit the project settings. 
    3) For the project's SDK, select the *IntelliJ IDEA IU-** option.
    4) Select *Modules* from the left panel and use the *Add* button to add a new project module. In the window that appears, select *IntelliJ Platform Plugin* from the left panel and select *OK*. Select a name for the module and ensure that the *Content Root* and *Module File Location* point to the project's root folder and select *Finish*. If a default module was generated while importing the project, you can remove it.
    5) Select Libraries from the left panel, select the *Add* button and select Java. Select the ``/libs`` folder in the window that appears and select *Open*.

##### Downloading and Importing Project
1) Download the project from Github and then use the *File>Project from Existing Resources* from the menu to import the project. Select the downloaded project's root folder and select *Open*.
2) Select the option to *Create Project from existing sources* and then proceed.
3) At the step to select the project's source files, deselect the ``test-project/src`` entry, if it was automatically selected. The project libraries will be automatically detected and a module will also be created.
4) Validate that the project was imported correctly and the module was correctly created. If there are issues, follow the steps in step 2 in the above section.

## Running the Plugin
To run the plugin, select the *Run Configuration* drop down menu and select *Edit Configurations*. Ensure that the module that was created previously is selected and press Ok. You should now be able to run the project.

A separate instance of IntelliJ will be launched. Use the open option to select the project found in ``/example-project`` directory. You may need to setup a project SDK for the project if one isn’t automatically configured. You should then be able to run the test project.

Logs for the plugin will appear in the initial instance of IntelliJ.

## Building the Plugin
To build the plugin, select the  "Prepare Plugin Module '...' For Deployment" option from the Build menu. This will generate the a zip file that contains the plugin's jars and resources in the project's root directory.

## Installing the Plugin
To install the plugin, go Preferences and select "Plugins" from the sidebar. Select the "Install Plugin from disk" button, locate the plugin file and select it. You will need to restart IntelliJ for the plugin to work. 

Contact: 
-------------
Goran Piskachev (Fraunhofer IEM, Zukunftsmeile 1, 33102 Paderborn, Office: 02-13)

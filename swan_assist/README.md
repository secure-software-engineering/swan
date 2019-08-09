# swan_assist
IntelliJ IDEA Plug-in for the Security methods for WeAkNess detection (SWAN) tool.

Description: 
-------------
SWAN is a machine-learning approach for detection of methods of interest for security in Java libraries. 
SWAN should be used in combination with other static analyses tools. It helps the users to create a set of relevant methods required as an input for static analyses, e.g. taint- and type-state analysis. 
SWAN detects four types of methods: source, sink, sanitizer, and authentication method. 
The found methods are further cathegorized according to relevant vulnerabilities (Common Weakness Enummeration  - CWE). Curretntly SWAN supports the following CWEs: CWE78, CWE79, CWE89, CWE306, CWE601, CWE862, and CWE863. 

SWAN_Assist provides a GUI support for SWAN. The user is able to interact with the learning process by giving feedback on the methods of interest. 
The tool helps users that write static analyses to create list of SWAN for their specific Java libraries. 
Moreover, users can manually inspect the proper usage of the methods detected by SWAN. 


## Setting Up the Plugin

The project can be downloaded using any of the methods below.  the project using either of the following methods:
##### Method 1: Cloning Project 
1) Select the **File>Project from Version Control>Git** option, enter the repository’s URL and then select **Clone** to import the project.
2) Go to **File>Project Structure** to edit the project settings. 
    3) For **Project SDK**, select the most recent Java SDK version.
    4) Select **Modules** from the left panel/
        1) Click the **Add** button and then **Import Module**. 
        2) In the window that appears, open the ``/swan_assist`` directory of the project.

##### Method 2: Downloading Project
1) Download the project from Github and then use the **File>Project from Existing Resources** from the menu to import the project. 
2) Select the ``/swan_assist`` directory in the downloaded project's root folder and select **Open**.

##### Importing Project Module
After following the steps of either Method 1 or 2, the **Import Module** dialog will appear. Follow the steps below to setup the project module.
1) Select **Import module from external module** and then the **Gradle** option.
2) Select the **Use auto-import** option. 
3) If the correct Gradle JVM isn’t selected, you can change it. 

The module should then be built. 


## Running the Plugin
To run the plugin:

1) Select the **Run Configuration** drop down menu and select **Edit Configurations** or from the **Run** menu select **Edit Configurations**. 
2) Click the **Add** button and select **Gradle**. 
3) Select the **swan_assist** Gradle module that was just created and enter ``:runIde`` as the value for **Tasks** - this tasks will run the plugin in a new instance of IntelliJ. 
4) When the new instance of IntelliJ launches, use the open option to select the project found in ``/test-project`` directory. You may need to set a project SDK, if one isn’t automatically configured. 

Logs for the plugin will appear in the initial instance of IntelliJ.




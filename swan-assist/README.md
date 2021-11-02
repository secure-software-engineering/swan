# swan_assist
IntelliJ IDEA Plug-in for the Security methods for WeAkNess detection (SWAN) tool.

Description: 
-------------
SWAN_Assist provides a GUI support for SWAN. The user is able to interact with the learning process by giving feedback on the methods of interest. 
The tool helps users that write static analyses to create list of SWAN for their specific Java libraries. 
Moreover, users can manually inspect the proper usage of the methods detected by SWAN. 


## Downloading the Project

The project can be downloaded using either of the following methods:

##### Method 1: Cloning the Project 
1) Select the **File>Project from Version Control>Git** option, enter the repository’s URL and then select **Clone** to import the project. The project will contain the following directories: ``swan_core`` (SWAN core application), ``swan_assist`` (IntelliJ Plugin) and ``swan_datasets`` (datasets for the research paper).
2) To configure the project settings and modules, go to **File>Project Structure**. 
3) For **Project SDK**, select the corresponding Java SDK version.
4) Select **Modules** from the left panel and remove the existing module that was automatically created.
5) Click the **Add** button and then **Import Module** to create the SWAN Core module. Follow the steps in the [Setting up the Project Modules](https://github.com/secure-software-engineering/swan/tree/master/swan_assist#setting-up-the-project-modules) section to finish configuring the core module as well as the plugin module.

		
##### Method 2: Downloading Project ZIP
1) Download and extract the project resources from GitHub.
2) In Intellij, use the **File>Project from Existing Resources** to import the project modules. This can also be done from the IntelliJ start screen.  
3) Follow the steps in the [Setting up the Project Modules](https://github.com/secure-software-engineering/swan/tree/master/swan_assist#setting-up-the-project-modules) section to finish configuring the core module as well as the plugin module.

## Setting up the Project Modules

##### SWAN Core
1) In the window that appears, open the ``/swan_core`` directory of the project. 
2) Select the **Import module from external Model** radio button and also select **Maven**. 
3) The default settings in the dialogs that appear can be used. 
4) Close the **Project Settings** dialog so that IntelliJ will index the new project module.

##### SWAN Assist
1) Return to the **Project Structure** dialog and Select **Modules** from the left panel.
2) Click the **Add** button and then **Import Module**. 
3) In the window that appears, open the ``/swan_assist`` directory. Select the **Import module from external Model** radio button and also select **Gradle**. 
4) The default settings in the dialogs that appear can be used. The plugin module should now be indexed. 

The core and plugin modules should now be imported. 

## Running the Plugin

The plugin uses ``swan_core`` dependency from [Maven Central](https://mvnrepository.com/artifact/de.upb.cs.swt/swan_core). If the version in the plugin's ``build.gradle`` file is not available on Maven Central, perform the following steps:
1) Run the Maven ``install`` command of the ``swan_core`` project from the console or using the Maven Plugin.
2) Add ``mavenLocal()`` in the ``repositories`` section of the ``build.gradle`` file. The locally installed library can now be use by the plugin.

##### To run the plugin:

1) Select the **Run Configuration** drop down menu and select **Edit Configurations** or from the **Run** menu, select **Edit Configurations**. 
2) Click the **Add** button and select **Gradle**. 
3) Select the **swan_assist** Gradle module that was just created and enter ``:runIde`` as the value for **Tasks** - this task will run the plugin in a new instance of IntelliJ. The plugin can also be executed using the Gradle Plugin in IntelliJ: Open the Gradle Tool Window, expand the ``intellij`` task and double click on ``runIde``. The other tasks can be used as necessary.
4) When the new instance of IntelliJ launches, use the open option to select the project found in ``/test-project`` directory. You may need to set a project SDK, if one isn’t automatically configured for the project. 

Logs for the plugin will appear in the initial instance of IntelliJ.




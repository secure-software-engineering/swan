/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.devassist.util;

import java.util.ResourceBundle;

/**
 * Constants for various classes.
 */
public class Constants {

    public final static ResourceBundle resource = ResourceBundle.getBundle("dialog_messages");

    //TODO update link to point to online help resources that will be created
    public final static String HELP_LINK = "https://github.com/secure-software-engineering/swan";

    public final static String PLUGIN_ID = "de.fraunhofer.devassist.";
    //SWAN arguments
    public final static String TRAIN_DIRECTORY = PLUGIN_ID + "trainingPath";
    public final static String SOURCE_DIRECTORY = PLUGIN_ID + "projectJarFiles"; //Test Library (or the project)
    public final static String CONFIGURATION_FILE = PLUGIN_ID + "configurationFile";
    public final static String LAST_SRM_LIST = PLUGIN_ID + "lastSrmList";
    public final static String LAST_SARIF_FILE = PLUGIN_ID + "lastSarifFile";
    public final static String SWAN_SETTINGS = PLUGIN_ID + "projectConfigured";
    public final static String OUTPUT_DIRECTORY = PLUGIN_ID + "outputPath";
    public final static String OUTPUT_FILE = PLUGIN_ID + "outputFile";
    public final static String OUTPUT_LOG = PLUGIN_ID + "outputLogs";
    public final static String ANALYSIS_RESULT = PLUGIN_ID + "analysisResult";
    public final static String TRAIN_FILE_SUGGESTED = PLUGIN_ID + "file_suggested_methods";

    public final static String TOOLKIT = PLUGIN_ID + "toolkit";
    public final static String DEFAULT_TRAINING_PATH = PLUGIN_ID + "defaultTrainingPath";
    public final static String PROJECT_CONFIGURATION_FILE = PLUGIN_ID + "defaultConfigurationFile";

    public final static String FILTER_CWE = "CWE";
    public final static String FILTER_TYPE = "Type";

    public final static Pair<String, String> FILE_FILTER = new Pair<>("file", resource.getString("Filter.File"));
    public final static Pair<String, String> TRAIN_FILTER = new Pair<>("training", resource.getString("Filter.Training"));
    public final static Pair<String, String> DELETED_FILTER = new Pair<>("deleted", resource.getString("Filter.Deleted"));
    public final static Pair<String, String> CLEAR_FILTER = new Pair<>("clear", resource.getString("Filter.Clear"));

    //Update operations
    public final static String METHOD_DELETED = "deleted";
    public final static String METHOD_ADDED = "added";
    public final static String TRAINING_METHOD = "manual";

    //Notification titles/strings
    public final static String PLUGIN_GROUP_DISPLAY_ID = "Plugin-1";
}

/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.util;

import javafx.util.Pair;

import java.util.ResourceBundle;

/**
 * Constants for various classes.
 */
public class Constants {

    public final static ResourceBundle resource = ResourceBundle.getBundle("dialog_messages");

    //TODO update link to point to online help resources that will be created
    public final static String HELP_LINK = "https://github.com/secure-software-engineering/swan";

    //SWAN arguments
    public final static String SWAN_TRAIN_DIR = "train_dir";
    public final static String SWAN_SOURCE_DIR = "source_dir"; //Test Library (or the project)
    public final static String SWAN_CONFIG_FILE = "configuration_file";
    public final static String SWAN_OUTPUT_DIR = "swan_output_dir";
    public final static String SWAN_JAR_DIR = "swan_core.jar";
    public final static String SWAN_OUTPUT_FILE = "output_file";
    public final static String SWAN_OUTPUT_LOG = "output_logs";
    public final static String SWAN_OUTPUT_MESSAGE = "output_message";
    public final static String TRAIN_FILE_SUGGESTED = "file_suggested_methods";


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
    public final static String PLUGIN_GROUP_DISPLAY_ID = "SWAN_Assist";



}

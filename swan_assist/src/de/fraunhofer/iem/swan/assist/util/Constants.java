package de.fraunhofer.iem.swan.assist.util;

import javafx.util.Pair;

import java.util.ResourceBundle;

/**
 * Constants for various classes.
 *
 * @author Oshando Johnson
 */
public class Constants {

    public final static ResourceBundle resource = ResourceBundle.getBundle("dialog_messages");

    //TODO update link to point to online help resources that will be created
    public final static String HELP_LINK = "https://github.com/Sable/soot";


    //SWAN arguments
    public final static String SWAN_TRAIN_DIR = "train_dir";
    public final static String SWAN_SOURCE_DIR = "source_dir"; //Test Library (or the project)
    public final static String SWAN_CONFIG_FILE = "configuration_file";
    public final static String SWAN_OUTPUT_DIR = "output_dir";
    public final static String SWAN_JAR_DIR = "swan_jar";
    public final static String SWAN_OUTPUT_FILE = "output_file";
    public final static String SWAN_OUTPUT_LOG = "output_logs";
    public final static String SWAN_OUTPUT_MESSAGE = "output_message";

    public final static String FILTER_CWE = "CWE";
    public final static String FILTER_TYPE = "Type";

    public final static Pair<String, String> FILE_FILTER = new Pair<>("file", resource.getString("Filter.File"));
    public final static Pair<String, String> TRAIN_FILTER = new Pair<>("training", resource.getString("Filter.Training"));
    public final static Pair<String, String> DELETED_FILTER = new Pair<>("deleted", resource.getString("Filter.Deleted"));
    public final static Pair<String, String> CLEAR_FILTER = new Pair<>("clear", resource.getString("Filter.Clear"));

    //Update operations
    public final static String METHOD_DELETED = "deleted";
    public final static String METHOD_ADDED = "added";
    public final static String METHOD_CHANGED = "changed";
    public final static String MSG_METHOD_RESTORED = "The method was restored.";

    public final static String NOTIFICATION_START_SWAN ="SWAN refresh started";
    public final static String NOTIFICATION_END_SWAN_SUCCESS ="SWAN execution completed";
    public final static String NOTIFICATION_END_SWAN_FAIL ="There was an error";
    public final static String NOTIFICATION_NONE ="No new notifications";
    public final static String NOTIFICATION_SWAN ="View results";

    public final static String TREE_EMPTY = "Select a configuration file using the \"Import\" button";
    public final static String TREE_FILTERS_EMPTY = "No methods match the filters you've selected";
    public final static String SWAN_TRAIN_DIR_NAME = "training_libs";
    public final static String SWAN_JAR_NAME = "swan.jar";

    public final static String LAUNCHER_PATH_NOT_SELECTED = "A path was not selected for one of the fields.";

    //Dialog titles
    public final static String TITLE_ADD_METHOD = "Add Method";
    public final static String TITLE_UPDATE_METHOD = "Update Method";
    public final static String TITLE_DELETE_METHOD = "Delete Method";
    public final static String TITLE_METHOD_PROPERTIES = "Method Properties";
    public final static String TITLE_RESTORE_METHOD = "Restore Method";

    //Notification titles/strings
    public final static String PLUGIN_GROUP_DISPLAY_ID = "SWAN_Assist";


}

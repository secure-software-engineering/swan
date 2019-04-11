package de.fraunhofer.iem.swan.assist.util;

/**
 * Constants for various classes.
 * @author Oshando Johnson
 */
public class Constants {


    //JSON Attributes labels
    public final static String METHOD_NAME_LABEL = "Method Name";
    public final static String RETURN_TYPE_LABEL = "Return Type";
    public final static String PARAMETER_LABEL = "Parameter(s)";
    public final static String SECURITY_LEVEL_LABEL = "Security Level";
    public final static String DISCOVERY_LABEL = "Discovery";
    public final static String FRAMEWORK_LABEL = "Framework";
    public final static String LINK_LABEL = "Link";
    public final static String CWE_LABEL = "CWE";
    public final static String TYPE_LABEL = "Type";
    public final static String COMMENT_LABEL = "Comment";

    //Messages
    public final static String INVALID_FILE_TYPE = "The selected file is invalid. Please select a .json file.";
    public final static String FILE_NOT_SELECTED = "A file was not selected. Select a configuration file.";

    public final static String ELEMENT_NOT_SELECTED = "Invalid element selected. Select a valid method from the class.";
    public final static String NOT_JAVA_FILE = "Files can only be added from a Java file.";
    public final static String METHOD_NOT_FOUND = "The selected method was not found.";
    public final static String URI_NOT_FOUND = "There was an error loading the URI";
    public final static String CONFIRM_METHOD_DELETION = "Are you sure you want to delete this method?";
    public final static String FILE_LOAD_ERROR = "An error occured loading the file.";
    public final static String NO_CATEGORY_SELECTED = "No type or CWE was selected for the method.";
    public final static String METHOD_NOT_FOUND_IN_EDITOR = "Method not found in project or cannot be located";
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

    //Details ToolWindow
    public final static String TABLE_HEADER_PROPERTY = "Method Property";
    public final static String TABLE_HEADER_VALUE = "Value";

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

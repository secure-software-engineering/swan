package de.fraunhofer.iem.mois.assist.util;

public class Constants {

    //JSON Attributes
    public final static String METHOD = "methods";
    public final static String NAME = "name";
    public final static String RETURN_TYPE = "return";
    public final static String PARAMETERS = "parameters";
    public final static String DATA_IN = "dataIn";
    public final static String DATA_OUT = "dataOut";
    public final static String DATA_RETURN_TYPE = "return";
    public final static String DATA_RETURN_PARAMETER = "parameters";
    public final static String SECURITY_LEVEL = "securityLevel";
    public final static String DISCOVERY = "discovery";
    public final static String FRAMEWORK = "framework";
    public final static String LINK = "link";
    public final static String CWE = "cwe";
    public final static String TYPE = "type";
    public final static String COMMENT = "comment";

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

    //Method Categories
    public final static String SOURCE = "Source";
    public final static String SINK = "Sink";
    public final static String SANITIZER = "Sanitizer";
    public final static String AUTHENTICATION = "Authentication";
    public final static String AUTHENTICATION_HIGH = "Authentications_to_high";
    public final static String AUTHENTICATION_NEUTRAL = "Authentications_neutral";
    public final static String AUTHENTICATION_LOW = "Authentications_to_low";
    public final static String NONE = "None";
    public final static String TEST = "Test";

    //Messages
    public final static String INVALID_FILE_TYPE = "The selected file is invalid. Please select a .json file.";
    public final static String FILE_NOT_SELECTED = "A file was not selected. Select a configuration file.";

    public final static String METHOD_NOT_SELECTED = "No method was selected. Select a valid method from the class.";
    public final static String METHOD_NOT_FOUND = "The selected method was not found.";
    public final static String URI_NOT_FOUND = "There was an error loading the URI";
    public final static String CONFIRM_METHOD_DELETION = "Are you sure you want to delete this method?";

    public final static String HELP_LINK = "https://github.com/Sable/soot";


    //Susi arguments
    public final static String SUSI_TRAIN_DIR = "train_dir";
    public final static String SUSI_SOURCE_DIR = "source_dir";
    public final static String SUSI_CONFIG_FILE = "configuration_file";
    public final static String SUSI_OUTPUT_DIR = "output_dir";
    public final static String SUSI_JAR_DIR = "susi_jar";
    public final static String SUSI_OUTPUT_FILE = "output_file";
    public final static String SUSI_OUTPUT_LOG = "output_logs";
    public final static String SUSI_OUTPUT_MESSAGE = "output_message";

    public final static String SUSI_LOG_SUFFIX = "_susiassist_log";
    public final static String OUTPUT_JSON_SUFFIX = "_output.json";
    public final static String OUTPUT_TEXT_SUFFIX = "_output.txt";

    public final static String FILTER_CURRENT_FILE_KEY = "file";
    public final static String FILTER_CURRENT_FILE_VALUE = "Current File";
    public final static String FILTER_CLEAR_KEY = "CLear Filters";
    public final static String FILTER_CLEAR_VALUE = "CLear Filters";
    public final static String FILTER_CWE = "CWE";
    public final static String FILTER_TYPE = "Type";

    //Details ToolWindow
    public final static String TABLE_HEADER_PROPERTY = "Method Property";
    public final static String TABLE_HEADER_VALUE = "Value";

    //Update operations
    public final static String METHOD_DELETED = "deleted";
    public final static String METHOD_ADDED = "added";
    public final static String METHOD_CHANGED = "changed";


    public final static String NOTIFICATION_START_SUSI ="Running Susi...";
    public final static String NOTIFICATION_END_SUSI_SUCCESS ="Susi completed successfully at ";
    public final static String NOTIFICATION_END_SUSI_FAIL ="There was an error";
    public final static String NOTIFICATION_NONE ="No new notifications";
    public final static String NOTIFICATION_SUSI ="View results";

    /*
    CONFIGURATION
     */

    public final static String SUSI_TRAIN_DIR_PATH = "/Users/oshando/IdeaProjects/iem-attract/02_code/susi/SuSi/SusiLib";
    public final static String SUSI_JAR_PATH = "/Users/oshando/IdeaProjects/iem-attract/02_code/Susiassist/libs/susi.jar";



}

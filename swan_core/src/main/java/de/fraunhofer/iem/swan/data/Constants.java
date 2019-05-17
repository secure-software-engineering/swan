package de.fraunhofer.iem.swan.data;

public final class Constants {
	
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

	//Method Categories
	public final static String SOURCE = "source";
	public final static String SINK = "sink";
	public final static String SANITIZER = "sanitizer";
	public final static String AUTHENTICATION = "authentication";
	public final static String AUTHENTICATION_SAFE = "auth-safe-state";
	public final static String AUTHENTICATION_NOCHANGE = "auth-no-change";
	public final static String AUTHENTICATION_UNSAFE = "auth-unsafe-state";
	public final static String NONE = "none";
	public final static String TEST = "test";

	//Authentication states
	public final static String AUTH_SAFE = "high";
	public final static String AUTH_NOCHANGE = "none";
	public final static String AUTH_UNSAFE = "low";

}

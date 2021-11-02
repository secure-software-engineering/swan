package de.fraunhofer.iem.swan.features.doc.manual.preprocessed;

/**
 * @author Oshando Johnson on 30.07.20
 */
public class Constants {

    //Regex patterns
    public static final String LINK_TAG_REGEX = "\\{@link(plain)?\\s(\\S+)(\\s\\S+)?}";
    public static final String CODE_TAG_REGEX =  "\\{@code\\s(\\S+)}";
    public static final String SEE_TAG_REGEX =  "\\{@see\\s(\\S+)}";
    public static final String DEPRECATED_TAG_REGEX =  "\\{@deprecated\\s(\\S+)}";
    public static final String NUMBER_PATTERN = "[0-9]+";
    public static final String UPPERCASE_WORD_PATTERN = "([A-Z]+)\\b";

    public enum TAG{
        CODE,
        DEPRECATED,
        LINK,
        SEE
    }
}

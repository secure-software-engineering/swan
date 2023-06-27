package de.fraunhofer.iem.swan.features.code.bow;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SecurityVocabulary {

    public static final Set<String> INNVOKED_METHOD_NAME_TOKENS= Stream.of("set",
            "delete",
            "has",
            "put",
            "get",
            "close",
            "open",
            "create",
            "is",
            "set",
            "on").collect(Collectors.toSet());
    public static final Set<String> METHOD_NAME_TOKENS= Stream.of(
            //For Authentication
            "unbind",
            "disconnect",
            "verif",
            "logout",
            "authen",
            "check",
            "privilege",
            "login",
            //For Sanitizers
            "saniti",
            "unescape",
            "strip",
            "replac",
            "regex",
            "verif",
            //For Sinks
            "writ",
            "verif",
            "handl",
            "log",
            "dump",
            "run",
            "updat",
            "pars",
            "execut",
            "setheader",
            "stream",
            "replac",
            "send",
            "redirect",
            //For Sources
            "object",
            "unescap",
            "pars",
            "stream",
            "request",
            "decod",
            "retriev",
            "creat",
            "name").collect(Collectors.toSet());

    public static final Set<String> CLASS_CONTAINS_TOKENS = Stream.of(

            //For Authentication
            "authen",
            "verif",
            "check",
            "oauth",
            "security",
            "bind",
            "connect",
            //For Sanitizers
            "encoder",
            "escap",
            "page",
            "encod",
            "saniti",
            "valid",
            //For Sinks
            "redirect",
            ".net",
            "sql",
            "web",
            "security",
            "jdbc",
            "html",
            ".io.",
            "response",
            "input",
            "manager",
            //For Sources
            "output",
            ".net",
            "sql",
            "web",
            "security",
            "jdbc",
            "html",
            ".io.",
            "manager",
            "security"
    ).collect(Collectors.toSet());

    public static final Set<String> INNVOKED_CLASS_NAME_TOKENS = Stream.of(
            //For Authentication
            ".io.",
            "db",
            "sql",
            //For Sanitizers
            "Saniti",
            "encod",
            "regex",
            "escap",
            //For Sinks
            ".net.",
            "Log.",
            //For Sources
            "web"
    ).collect(Collectors.toSet());

    public static final Set<String> PARAMETER_TYPES_TOKENS = Stream.of(
            //All
            "java.lang.string",
            "char[]",
            "java.lang.charsequence",
            "java.lang.stringbuilder",
            "byte[]"
    ).collect(Collectors.toSet());

    public static final Set<String> AUTHENTICATION_METHOD_START = Stream.of("set", "delete", "has", "put", "get", "close", "open", "create", "is").collect(Collectors.toSet());
    public static final Set<String> AUTHENTICATION_METHOD_INVOKED = Stream.of("authen", "bind", "verif", "connect", "security", "logout", "authori", "check", "credential", "login").collect(Collectors.toSet());
    public static final Set<String> AUTHENTICATION_CLASSES_INVOKED = Stream.of(".io.", "db", "sql").collect(Collectors.toSet());
    public static final Set<String> AUTHENTICATION_PARAMETER_TYPES = Stream.of("java.lang.string", "char[]", "java.lang.charsequence").collect(Collectors.toSet());

    public static final Set<String> SANITIZER_METHOD_START = Stream.of("set", "has", "put", "get").collect(Collectors.toSet());
    public static final Set<String> SANITIZER_METHOD_INVOKED = Stream.of("escap", "encod", "saniti", "replac", "match", "regex", "strip").collect(Collectors.toSet());
    public static final Set<String> SANITIZER_CLASSES_INVOKED = Stream.of("Saniti", "encod", "regex", "escap").collect(Collectors.toSet());
    public static final Set<String> SANITIZER_PARAMETER_TYPES = Stream.of("java.lang.string", "char[]", "java.lang.charsequence", "java.lang.stringbuilder", "byte[]").collect(Collectors.toSet());

    public static final Set<String> SINK_METHOD_START = Stream.of("set", "on").collect(Collectors.toSet());
    public static final Set<String> SINK_METHOD_INVOKED = Stream.of("updat", "dump", "handl", "set", "writ", "log", "put", "pars", "send", "print", "run", "replac", "execut").collect(Collectors.toSet());
    public static final Set<String> SINK_CLASSES_INVOKED = Stream.of(".io.", "db", "sql", ".net.", "Log.").collect(Collectors.toSet());
    public static final Set<String> SINK_PARAMETER_TYPES = Stream.of("java.lang.string", "char[]", "java.lang.charsequence", "java.lang.stringbuilder", "byte[]").collect(Collectors.toSet());
    public static final Set<String> PARAMETER_TO_SINK = Stream.of("writ", "set", "updat", "send", "handl", "put", "log", "run", "execut", "dump", "print", "pars", "stream" ).collect(Collectors.toSet());

    public static final Set<String> SOURCE_TO_RETURN = Stream.of("get", "red", "decode", "unescape", "load", "request", "create").collect(Collectors.toSet());
    public static final Set<String> SOURCE_METHOD_START = Stream.of("get").collect(Collectors.toSet());
    public static final Set<String> SOURCE_METHOD_INVOKED = Stream.of("read", "load", "decod", "get", "output", "creat", "unescap", "request").collect(Collectors.toSet());
    public static final Set<String> SOURCE_CLASSES_INVOKED = Stream.of(".io.", "sql", "db", "web", ".net.").collect(Collectors.toSet());
    public static final Set<String> SOURCE_PARAMETER_TYPES = Stream.of("java.lang.string", "char[]", "java.lang.charsequence", "byte[]").collect(Collectors.toSet());

}

package de.fraunhofer.iem.swan.features.code;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SecurityVocabulary {

    public static final Set<String> AUTHENTICATION_METHOD_START = Stream.of("set", "delete", "has", "put", "get", "close", "open", "create", "is")
            .collect(Collectors.toSet());

    public static final Set<String> AUTHENTICATION_METHOD_CONTAINS = Stream.of("unbind", "disconnect", "verif", "logout", "authen", "check", "privilege",
                    "login").collect(Collectors.toSet());

    public static final Set<String> AUTHENTICATION_CLASS_CONTAINS = Stream.of("authen", "verif", "check", "oauth", "security", "bind", "connect")
            .collect(Collectors.toSet());

    public static final Set<String> AUTHENTICATION_METHOD_INVOKED = Stream.of("authen", "bind", "verif", "connect", "security", "logout", "authori",
            "check", "credential", "login").collect(Collectors.toSet());

    public static final Set<String> AUTHENTICATION_CLASSES_INVOKED = Stream.of(".io.", "db", "sql")
            .collect(Collectors.toSet());

    public static final Set<String> AUTHENTICATION_PARAMETER_TYPES = Stream.of("java.lang.string", "char[]", "java.lang.charsequence")
            .collect(Collectors.toSet());

    public static final Set<String> SANITIZER_METHOD_START = Stream.of("set", "has", "put", "get")
            .collect(Collectors.toSet());

    public static final Set<String> SANITIZER_METHOD_CONTAINS = Stream.of("saniti", "unescape", "strip", "replac", "regex", "verif")
            .collect(Collectors.toSet());

    public static final Set<String> SANITIZER_CLASS_CONTAINS = Stream.of("encoder", "escap", "page", "encod", "saniti", "valid")
            .collect(Collectors.toSet());

    public static final Set<String> SANITIZER_METHOD_INVOKED = Stream.of("escap", "encod", "saniti", "replac", "match", "regex", "strip")
            .collect(Collectors.toSet());

    public static final Set<String> SANITIZER_CLASSES_INVOKED = Stream.of("Saniti", "encod", "regex", "escap")
            .collect(Collectors.toSet());

    public static final Set<String> SANITIZER_PARAMETER_TYPES = Stream.of("java.lang.string", "char[]", "java.lang.charsequence", "java.lang.stringbuilder", "byte[]")
            .collect(Collectors.toSet());

    public static final Set<String> SINK_METHOD_START = Stream.of("set", "on")
            .collect(Collectors.toSet());

    public static final Set<String> SINK_METHOD_CONTAINS = Stream.of("writ", "handl", "log", "dump", "run", "updat", "print", "pars", "execut", "setheader",
            "stream", "replac", "redirect", "send").collect(Collectors.toSet());

    public static final Set<String> SINK_CLASS_CONTAINS = Stream.of("redirect", ".net", "sql", "web", "security", "jdbc", "html", ".io.", "response",
            "input", "manager").collect(Collectors.toSet());

    public static final Set<String> SINK_METHOD_INVOKED = Stream.of("updat", "dump", "handl", "set", "writ", "log", "put", "pars", "send", "print",
            "run", "replac", "execut").collect(Collectors.toSet());

    public static final Set<String> SINK_CLASSES_INVOKED = Stream.of(".io.", "db", "sql", ".net.", "Log.", "sql")
            .collect(Collectors.toSet());

    public static final Set<String> SINK_PARAMETER_TYPES = Stream.of("java.lang.string", "char[]", "java.lang.charsequence", "java.lang.stringbuilder", "byte[]", ".io.", "db", "sql", "web")
            .collect(Collectors.toSet());

    public static final Set<String> SOURCE_METHOD_START = Stream.of("get")
            .collect(Collectors.toSet());

    public static final Set<String> SOURCE_METHOD_CONTAINS = Stream.of("object", "unescap", "pars", "stream", "request", "decod", "retriev", "creat", "name")
            .collect(Collectors.toSet());

    public static final Set<String> SOURCE_CLASS_CONTAINS = Stream.of("output", ".net", "sql", "web", "security", "jdbc", "html", ".io.", "manager")
            .collect(Collectors.toSet());

    public static final Set<String> SOURCE_METHOD_INVOKED = Stream.of("read", "load", "decod", "get", "output", "creat", "unescap", "request")
            .collect(Collectors.toSet());

    public static final Set<String> SOURCE_CLASSES_INVOKED = Stream.of("read", "load", "decod", "get", "output", "creat", "unescap", "request")
            .collect(Collectors.toSet());
    public static final Set<String> SOURCE_PARAMETER_TYPES = Stream.of("java.lang.string", "char[]", "java.lang.charsequence", "byte[]", ".io.", "db", "sql", "web")
            .collect(Collectors.toSet());

}

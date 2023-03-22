package de.fraunhofer.iem.swan.features.code;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SecurityVocabulary {

    public static final List<Pair<String, Integer>> METHOD_CONTAINS = new ArrayList<>(){{
        //For Authentication
        add(new Pair<>("unbind", 1)); add(new Pair<>("disconnect", 1));
        add(new Pair<>("verif", 1)); add(new Pair<>("logout", 1));
        add(new Pair<>("authen", 1)); add(new Pair<>("check", 1));
        add(new Pair<>("privilege", 1)); add(new Pair<>("login", 1));
        //For Sanitizers
        add(new Pair<>("saniti", 2)); add(new Pair<>("unescape", 2));
        add(new Pair<>("strip", 2)); add(new Pair<>("replac", 2));
        add(new Pair<>("regex", 2)); add(new Pair<>("verif", 2));
        //For Sinks
        add(new Pair<>("writ", 3)); add(new Pair<>("verif", 3));
        add(new Pair<>("handl", 3)); add(new Pair<>("log", 3));
        add(new Pair<>("dump", 3)); add(new Pair<>("run", 3));
        add(new Pair<>("updat", 3)); add(new Pair<>("pars", 3));
        add(new Pair<>("execut", 3)); add(new Pair<>("setheader", 3));
        add(new Pair<>("stream", 3)); add(new Pair<>("replac", 3));
        add(new Pair<>("send", 3)); add(new Pair<>("redirect", 3));
        //For Sources
        add(new Pair<>("object", 4)); add(new Pair<>("unescap", 4));
        add(new Pair<>("pars", 4)); add(new Pair<>("stream", 4));
        add(new Pair<>("request", 4)); add(new Pair<>("decod", 4));
        add(new Pair<>("retriev", 4));add(new Pair<>("creat", 4));
        add(new Pair<>("name", 4));
    }};

    public static final List<Pair<String, Integer>> CLASS_CONTAINS = new ArrayList<>(){{
        //For Authentication
        add(new Pair<>("authen", 1)); add(new Pair<>("verif", 1));
        add(new Pair<>("check", 1)); add(new Pair<>("oauth", 1));
        add(new Pair<>("security", 1)); add(new Pair<>("bind", 1));
        add(new Pair<>("connect", 1));
        //For Sanitizers
        add(new Pair<>("encoder", 2)); add(new Pair<>("escap", 2));
        add(new Pair<>("page", 2)); add(new Pair<>("encod", 2));
        add(new Pair<>("saniti", 2)); add(new Pair<>("valid", 2));
        //For Sinks
        add(new Pair<>("redirect", 3)); add(new Pair<>(".net", 3));
        add(new Pair<>("sql", 3)); add(new Pair<>("web", 3));
        add(new Pair<>("security", 3)); add(new Pair<>("jdbc", 3));
        add(new Pair<>("html", 3)); add(new Pair<>(".io.", 3));
        add(new Pair<>("response", 3)); add(new Pair<>("input", 3));
        add(new Pair<>("manager", 3));
        //For Sources
        add(new Pair<>("output", 4)); add(new Pair<>(".net", 4));
        add(new Pair<>("sql", 4)); add(new Pair<>("web", 4));
        add(new Pair<>("security", 4)); add(new Pair<>("jdbc", 4));
        add(new Pair<>("html", 4)); add(new Pair<>(".io.", 4));
        add(new Pair<>("manager", 4)); add(new Pair<>("security", 4));
    }};
    public static final Set<String> AUTHENTICATION_METHOD_START = Stream.of("set", "delete", "has", "put", "get", "close", "open", "create", "is")
            .collect(Collectors.toSet());
    public static final Set<String> AUTHENTICATION_METHOD_INVOKED = Stream.of("authen", "bind", "verif", "connect", "security", "logout", "authori",
            "check", "credential", "login").collect(Collectors.toSet());
    public static final Set<String> AUTHENTICATION_CLASSES_INVOKED = Stream.of(".io.", "db", "sql")
            .collect(Collectors.toSet());
    public static final Set<String> AUTHENTICATION_PARAMETER_TYPES = Stream.of("java.lang.string", "char[]", "java.lang.charsequence")
            .collect(Collectors.toSet());

    public static final Set<String> SANITIZER_METHOD_START = Stream.of("set", "has", "put", "get")
            .collect(Collectors.toSet());
    public static final Set<String> SANITIZER_METHOD_INVOKED = Stream.of("escap", "encod", "saniti", "replac", "match", "regex", "strip")
            .collect(Collectors.toSet());
    public static final Set<String> SANITIZER_CLASSES_INVOKED = Stream.of("Saniti", "encod", "regex", "escap")
            .collect(Collectors.toSet());
    public static final Set<String> SANITIZER_PARAMETER_TYPES = Stream.of("java.lang.string", "char[]", "java.lang.charsequence", "java.lang.stringbuilder", "byte[]")
            .collect(Collectors.toSet());

    public static final Set<String> SINK_METHOD_START = Stream.of("set", "on")
            .collect(Collectors.toSet());
    public static final Set<String> SINK_METHOD_INVOKED = Stream.of("updat", "dump", "handl", "set", "writ", "log", "put", "pars", "send", "print",
            "run", "replac", "execut").collect(Collectors.toSet());
    public static final Set<String> SINK_CLASSES_INVOKED = Stream.of(".io.", "db", "sql", ".net.", "Log.", "sql")
            .collect(Collectors.toSet());
    public static final Set<String> SINK_PARAMETER_TYPES = Stream.of("java.lang.string", "char[]", "java.lang.charsequence", "java.lang.stringbuilder", "byte[]", ".io.", "db", "sql", "web")
            .collect(Collectors.toSet());

    public static final Set<String> SOURCE_METHOD_START = Stream.of("get")
            .collect(Collectors.toSet());
    public static final Set<String> SOURCE_METHOD_INVOKED = Stream.of("read", "load", "decod", "get", "output", "creat", "unescap", "request")
            .collect(Collectors.toSet());
    public static final Set<String> SOURCE_CLASSES_INVOKED = Stream.of("read", "load", "decod", "get", "output", "creat", "unescap", "request")
            .collect(Collectors.toSet());
    public static final Set<String> SOURCE_PARAMETER_TYPES = Stream.of("java.lang.string", "char[]", "java.lang.charsequence", "byte[]", ".io.", "db", "sql", "web")
            .collect(Collectors.toSet());

}

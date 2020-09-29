package de.fraunhofer.iem.swan.doc.util;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Oshando Johnson on 04.08.20
 */
public class SecurityVocabulary {

    public static final Set<String> GENERAL_NOUNS = Stream.of("auth", "authentication", "authorization", "connection",
            "credentials", "data", "database", "db", "document", "encoder", "file", "header", "html", "io", "jdbc",
            "json", "logger", "message", "network", "node", "oauth", "object", "output", "privilege", "propert", "query",
            "request", "row", "security", "sql", "string", "system", "table", "url", "user", "web", "xml")
            .collect(Collectors.toSet());
    ///extract, package, find,

    /**
     * Source verbs and sinks.
     */
    public static final Set<String> SOURCE_VERBS = Stream.of("copy", "create", "decode", "fetch", "get", "import", "input",
            "load", "read", "request", "retrieve", "return", "search", "unescape")
            .collect(Collectors.toSet());
    public static final Set<String> SOURCE_NOUNS = Stream.of("array", "bytes", "data", "database", "datum", "document", "content", "file", "html", "io", "issue", "json",
            "line", "network", "node", "object", "output", "retrieve", "select", "text", "url", "value", "web", "xml")
            .collect(Collectors.toSet());
    public static final Set<String> SOURCE_PREPOSITIONS = Stream.of("at", "from", "within")
            .collect(Collectors.toSet());

    /**
     * Sink verbs and sinks
     */
    public static final Set<String> SINK_VERBS = Stream.of("commit", "cookie", "copy", "delete", "dump", "drop", "establish",
            "execute", "export", "handle", "hibernate", "insert", "line", "log", "manage", "move", "parse", "persist", "print",
            "put", "redirect", "render", "replace", "request", "response", "run", "save", "send", "set", "substitute",
            "take", "update", "write", "output")
            .collect(Collectors.toSet());
    public static final Set<String> SINK_NOUNS = Stream.of("array", "bytes", "connection", "data", "database", "datum",
            "file", "header", "html", "io", "jdbc", "logger", "message", "network",
            "security", "string", "url", "web")
            .collect(Collectors.toSet());
    public static final Set<String> SINK_PREPOSITIONS = Stream.of("in", "inside", "into", "on", "onto", "to")
            .collect(Collectors.toSet());

    /**
     * Sanitizer verbs and sinks
     */
    public static final Set<String> SANITIZER_VERBS = Stream.of("apply", "convert", "encode", "decode", "escape", "hash", "login", "logout", "match",
            "page", "replace", "sanitize", "strip", "translate", "turn", "validate")
            .collect(Collectors.toSet());
    public static final Set<String> SANITIZER_NOUNS = Stream.of("array", "bytes", "encoder", "data", "datum", "decoder", "digest", "hash", "mask", "message", "pattern", "regex", "regular expression", "salt", "string")
            .collect(Collectors.toSet());
    public static final Set<String> SANITIZER_PREPOSITIONS = Stream.of("for", "to")
            .collect(Collectors.toSet());

    /**
     * Authentication verbs and sinks
     */
    public static final Set<String> AUTH_SAFE_VERBS = Stream.of("access", "authenticate", "authorize", "bind",
            "connect", "create", "establish", "login", "open", "put", "verify")
            .collect(Collectors.toSet());

    public static final Set<String> AUTH_NO_CHANGE_VERBS = Stream.of("access", "check", "get", "has", "validate", "verify")
            .collect(Collectors.toSet());

    public static final Set<String> AUTH_UNSAFE_VERBS = Stream.of("close", "delete", "disconnect", "logout")
            .collect(Collectors.toSet());

    public static final Set<String> AUTHENTICATION_NOUNS = Stream.of("access", "account", "auth", "authentication", "authorization",
            "connection", "credential", "ldap", "oauth", "privilege", "right", "security", "server", "user")
            .collect(Collectors.toSet());
    public static final Set<String> AUTHENTICATION_PREPOSITIONS = Stream.of("for", "from", "to")
            .collect(Collectors.toSet());

    /**
     * CWE089 verbs and sinks
     */
    public static final Set<String> CWE089_VERBS = Stream.of("create", "execute", "insert", "make", "query",
            "run", "save", "write", "update")
            .collect(Collectors.toSet());
    public static final Set<String> CWE089_NOUNS = Stream.of("data", "datum", "database", "db", "encoder", "jdbc", "query",
            "request", "row", "table", "sql")
            .collect(Collectors.toSet());

    public static final Set<String> INCOMPLETE_CODE_KEYWORDS = Stream.of("backdoor", "broken", "bypass", "divert", "fixme",
            "hack", "kludge", "password", "steal", "stolen", "todo", "trick")
            .collect(Collectors.toSet());
}

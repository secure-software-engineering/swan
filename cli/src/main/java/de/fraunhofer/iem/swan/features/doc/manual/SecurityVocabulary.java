package de.fraunhofer.iem.swan.features.doc.manual;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Oshando Johnson on 04.08.20
 */
public class SecurityVocabulary {

    public static final Set<String> GENERAL_NOUNS = Stream.of("adapter", "task", "list", "child",
            "parent", "array", "cache", "buffer", "chunk", "client", "algorithm", "bug", "dataset", "firewall",
            "identifier", "interface", "fragment", "internet", "module", "packet", "garbage", "partition", "mobile",
            "matrix", "keyboard", "mouse", "integer", "hardware", "peer", "sensor", "sibling", "smartphone",
            "vector", "repository", "framework", "platform", "layer", "software", "memory", "disk")
            .collect(Collectors.toSet());
    ///extract, package, find,

    /**
     * Source verbs and sinks.
     */
    public static final Set<String> SOURCE_VERBS = Stream.of("browse", "copy", "create", "decode", "download", "fetch",
            "get", "import", "input", "line", "load", "lookup", "read", "request", "retrieve", "return", "search", "text", "unescape")
            .collect(Collectors.toSet());
    public static final Set<String> SOURCE_NOUNS = Stream.of("array", "bytes", "data", "database", "datum", "document",
            "content", "file", "html", "io", "issue", "json", "line", "network", "node", "object", "output", "retrieve",
            "select", "text", "url", "value", "web", "xml")
            .collect(Collectors.toSet());
    public static final Set<String> SOURCE_PREPOSITIONS = Stream.of("at", "from", "within")
            .collect(Collectors.toSet());
    public static final Set<String> SOURCE_ADJECTIVE = Stream.of("incoming", "external", "internal", "local")
            .collect(Collectors.toSet());

    /**
     * Sink verbs and sinks
     */
    public static final Set<String> SINK_VERBS = Stream.of("backup", "commit", "cookie", "copy", "delete", "dump",
            "drop", "email", "e-mail", "establish", "execute", "export", "handle", "hibernate", "insert", "leak",
            "line", "list", "log", "manage", "move", "parse", "persist", "print", "put", "redirect", "render",
            "replace", "request", "response", "run", "save", "scan", "send", "set", "substitute", "take", "update",
            "write", "output", "upload")
            .collect(Collectors.toSet());
    public static final Set<String> SINK_NOUNS = Stream.of("array", "bytes", "connection", "data", "database", "datum",
            "discard", "file", "header", "html", "io", "jdbc", "logger", "message", "network", "post", "print", "output",
            "security", "string", "url", "web")
            .collect(Collectors.toSet());
    public static final Set<String> SINK_PREPOSITIONS = Stream.of("in", "inside", "into", "on", "onto", "to")
            .collect(Collectors.toSet());
    public static final Set<String> SINK_ADJECTIVE = Stream.of("outgoing", "external", "internal", "malicious")
            .collect(Collectors.toSet());

    /**
     * Sanitizer verbs and sinks
     */
    public static final Set<String> SANITIZER_VERBS = Stream.of("apply", "convert", "encode", "encrypt", "decode",
            "decrypt", "escape", "hash", "login", "logout", "match", "page", "replace", "sanitize", "strip",
            "translate", "turn", "validate")
            .collect(Collectors.toSet());
    public static final Set<String> SANITIZER_NOUNS = Stream.of("array", "byte", "encoder", "data", "datum", "decoder",
            "digest", "hash", "mask", "message", "pattern", "regex", "regular expression", "salt", "string")
            .collect(Collectors.toSet());
    public static final Set<String> SANITIZER_PREPOSITIONS = Stream.of("for", "to")
            .collect(Collectors.toSet());

    /**
     * Authentication verbs and sinks
     */
    public static final Set<String> AUTH_SAFE_VERBS = Stream.of("access", "authenticate", "authorize", "bind",
            "connect", "create", "establish", "login", "open", "put", "token", "trust", "verify")
            .collect(Collectors.toSet());

    public static final Set<String> AUTH_NO_CHANGE_VERBS = Stream.of("access", "check", "get", "has", "validate", "verify")
            .collect(Collectors.toSet());

    public static final Set<String> AUTH_UNSAFE_VERBS = Stream.of("close", "delete", "end", "disconnect", "logout")
            .collect(Collectors.toSet());

    public static final Set<String> AUTHENTICATION_NOUNS = Stream.of("access", "account", "auth", "authentication",
            "authorization", "connection", "credential", "ldap", "oauth", "privilege", "right", "security", "server", "user")
            .collect(Collectors.toSet());
    public static final Set<String> AUTHENTICATION_PREPOSITIONS = Stream.of("for", "from", "to")
            .collect(Collectors.toSet());

    public static final Set<String> AUTHENTICATION_ADVERBS = Stream.of("register", "maliciously")
            .collect(Collectors.toSet());

    public static final Set<String> AUTHENTICATION_ADJECTIVE = Stream.of("online", "offline", "trust", "verify", "register", "malicious")
            .collect(Collectors.toSet());

    /**
     * CWE78 verbs and sinks
     */
    public static final Set<String> CWE78_VERBS = Stream.of("encode", "execute", "make", "process", "run", "delete",
            "sanitize", "compile", "sanitize")
            .collect(Collectors.toSet());
    public static final Set<String> CWE78_NOUNS = Stream.of("system", "command", "credential", "runtime", "encoder", "os",
            "operating", "host", "shell")
            .collect(Collectors.toSet());

    /**
     * CWE79 verbs and sinks
     */
    public static final Set<String> CWE79_VERBS = Stream.of("render", "input", "hibernate",
            "set", "sanitize")
            .collect(Collectors.toSet());
    public static final Set<String> CWE79_NOUNS = Stream.of("web", "website", "request", "html", "page", "css", "dom",
            "header", "document", "node")
            .collect(Collectors.toSet());

    /**
     * CWE89 verbs and sinks
     */
    public static final Set<String> CWE89_VERBS = Stream.of("create", "delete", "execute", "insert", "make", "persist","query",
            "remove", "run", "sanitize", "save", "schema", "script", "transact", "write", "update")
            .collect(Collectors.toSet());
    public static final Set<String> CWE89_NOUNS = Stream.of("data", "datum", "database", "db", "encoder", "jdbc", "query",
            "request", "row", "table", "tuple", "transaction", "value", "sql")
            .collect(Collectors.toSet());

    /**
     * CWE306 verbs and sinks
     */
    public static final Set<String> CWE306_VERBS = Stream.of("login", "logout", "authorise", "authenticate", "grant", "access",
            "connect", "disconnect")
            .collect(Collectors.toSet());
    public static final Set<String> CWE306_NOUNS = Stream.of("0auth", "auth", "authentication", "authorisation", "access", "privilege",
            "connection", "disconnection", "user", "account", "profile")
            .collect(Collectors.toSet());

    /**
     * CWE601 verbs and sinks
     */
    public static final Set<String> CWE601_VERBS = Stream.of("respond", "send", "forward", "route", "request", "serve",
            "respond", "redirect")
            .collect(Collectors.toSet());
    public static final Set<String> CWE601_NOUNS = Stream.of("request", "http", "servlet", "response", "redirect", "parameter",
             "web", "url")
            .collect(Collectors.toSet());

    /**
     * CWE862 verbs and sinks
     */
    public static final Set<String> CWE862_VERBS = Stream.of("bind", "connect", "login", "authorize", "authenticate")
            .collect(Collectors.toSet());
    public static final Set<String> CWE862_NOUNS = Stream.of("credential", "user", "encoder", "authorization", "role", "access")
            .collect(Collectors.toSet());
    /**
     * CWE863 verbs and sinks
     */
    public static final Set<String> CWE863_VERBS = Stream.of("bind", "connect", "login", "authorize", "authenticate")
            .collect(Collectors.toSet());
    public static final Set<String> CWE863_NOUNS = Stream.of("credential", "user", "encoder", "authorization", "role", "access")
            .collect(Collectors.toSet());

    public static final Set<String> INCOMPLETE_CODE_KEYWORDS = Stream.of("backdoor", "broken", "bypass", "divert", "fixme",
            "hack", "kludge", "password", "steal", "stolen", "todo", "trick")
            .collect(Collectors.toSet());

}

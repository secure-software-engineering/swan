package de.fraunhofer.iem.swan.data;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Categories for the learner.
 *
 * @author Lisa Nguyen Quang Do
 */

public enum Category {

    SOURCE(Constants.SOURCE, false, false),
    SINK(Constants.SINK, false, false),
    SANITIZER(Constants.SANITIZER, false, false),
    AUTHENTICATION_TO_HIGH(Constants.AUTHENTICATION_SAFE, false, true),
    AUTHENTICATION_TO_LOW(Constants.AUTHENTICATION_UNSAFE, false, true),
    AUTHENTICATION_NEUTRAL(Constants.AUTHENTICATION_NOCHANGE, false, true),
    AUTHENTICATION("authentication", false, true),
    RELEVANT(Constants.RELEVANT, false, false),
    PROPAGATOR(Constants.PROPAGATOR, false, false),
    NONE(Constants.NONE, false, false),
    CWE22("CWE22", true, false),
    CWE35("CWE35", true, false),
    CWE77("CWE77", true, false),
    CWE78("CWE78", true, false),
    CWE79("CWE79", true, false),
    CWE89("CWE89", true, false),
    CWE90("CWE90", true, false),
    CWE91("CWE91", true, false),
    CWE117("CWE117", true, false),
    CWE233("CWE233", true, false),
    CWE306("CWE306", true, false),
    CWE327("CWE327", true, false),
    CWE328("CWE328", true, false),
    CWE443("CWE443", true, false),
    CWE501("CWE501", true, false),
    CWE601("CWE601", true, false),
    CWE643("CWE643", true, false),
    CWE862("CWE862", true, false),
    CWE863("CWE863", true, false),
    CWE917("CWE917", true, false),
    CWE918("CWE918", true, false),
    CWETEST("CWEtest", true, false),
    CWE_NONE("cwe-none", true, false);

    private final String id;
    private final boolean cwe;
    private final boolean authentication;

    private Category(String id, boolean cwe, boolean authentication) {
        this.id = id;
        this.cwe = cwe;
        this.authentication = authentication;
    }

    public boolean isCwe() {
        return cwe;
    }

    @JsonValue
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }

    public boolean isAuthentication() {
        return authentication;
    }

    public boolean isNone() {
        return id.contains("none");
    }

    public static Category getCategoryForCWE(String cweName) {
        for (Category c : Category.values())
            if (c.id.equalsIgnoreCase(cweName))
                return c;
        return null;
    }

    public static Category fromText(String text) {

        for (Category cat : Category.values()) {
            if (cat.name().equalsIgnoreCase(text)) {
                return cat;
            }
        }

        throw new IllegalArgumentException(String.format(
                "There is no category with name '%s'",
                text
        ));
    }
}

package de.fraunhofer.iem.mois.assist.data;

import de.fraunhofer.iem.mois.assist.util.Constants;
import icons.PluginIcons;

import javax.swing.*;

/**
 * Categories for the learner.
 *
 * @author Lisa Nguyen Quang Do
 */

public enum Category {
    SOURCE(Constants.SOURCE, false), SINK(Constants.SINK, false), AUTHENTICATION(
            Constants.AUTHENTICATION, false), AUTHENTICATION_TO_LOW(
            Constants.AUTHENTICATION_LOW, false), AUTHENTICATION_NEUTRAL(
            Constants.AUTHENTICATION_NEUTRAL, false), SANITIZER(Constants.SANITIZER,
            false), NONE(Constants.NONE, false), TEST(Constants.TEST, false),

    CWE089("CWE089", true), CWE306("CWE306", true), CWE078("CWE078",
            true), CWE862("CWE862", true), CWE863("CWE863",
            true), CWETEST("CWEtest", true), CWE_NONE("none", true);

    private final String id;
    private final boolean cwe;

    Category(String id, boolean cwe) {
        this.id = id;
        this.cwe = cwe;
    }

    public boolean isCwe() {
        return cwe;
    }

    public Icon getIcon() {

        switch (id) {
            case Constants.SINK:
                return PluginIcons.SINK;
            case Constants.SOURCE:
                return PluginIcons.SOURCE;
            case Constants.SANITIZER:
                return PluginIcons.SANITIZER;
            case Constants.AUTHENTICATION:
                return PluginIcons.AUTHENTICATION;
            case Constants.CWE:
                return PluginIcons.CWE;
            default:
                return PluginIcons.OTHER;
        }
    }

    @Override
    public String toString() {
        return id;
    }

    public static Category getCategoryForCWE(String cweName) {
        for (Category c : Category.values())
            if (c.id.toLowerCase().equals(cweName.toLowerCase()))
                return c;
        return null;
    }
}

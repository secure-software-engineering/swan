package de.fraunhofer.iem.swan.data;

/**
 * Categories for the learner.
 *
 * @author Lisa Nguyen Quang Do
 */

public enum Category {

    SOURCE(Constants.SOURCE, false),
    SINK(Constants.SINK, false),
    SANITIZER(Constants.SANITIZER, false),
    AUTHENTICATION_TO_HIGH(Constants.AUTHENTICATION_SAFE, false),
    AUTHENTICATION_TO_LOW(Constants.AUTHENTICATION_UNSAFE, false),
    AUTHENTICATION_NEUTRAL(Constants.AUTHENTICATION_NOCHANGE, false),
    RELEVANT(Constants.RELEVANT, false),
    NONE(Constants.NONE, false),
    CWE078("CWE078", true),
    CWE079("CWE079", true),
    CWE089("CWE089", true),
    CWE306("CWE306", true),
    CWE601("CWE601", true),
    CWE862("CWE862", true),
    CWE863("CWE863", true),
    CWETEST("CWEtest", true),
    CWE_NONE("none", true);

    private final String id;
    private final boolean cwe;

    private Category(String id, boolean cwe) {
        this.id = id;
        this.cwe = cwe;
    }

    public boolean isCwe() {
        return cwe;
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

    public static Category fromText(String text) {

        for(Category cat: Category.values()){
            if(cat.name().equalsIgnoreCase(text)){
                return cat;
            }
        }

        throw new IllegalArgumentException(String.format(
                "There is no category with name '%s'",
                text
        ));
    }
}

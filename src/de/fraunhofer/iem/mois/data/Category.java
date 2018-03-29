package de.fraunhofer.iem.mois.data;

/**
 * Categories for the learner.
 *
 * @author Lisa Nguyen Quang Do
 *
 */

public enum Category {
  SOURCE("sources", false), SINK("sinks", false), AUTHENTICATION_TO_HIGH(
      "authentications_to_high", false), AUTHENTICATION_TO_LOW(
          "authentications_to_low", false), AUTHENTICATION_NEUTRAL(
              "authentications_neutral", false), SANITIZER("sanitizers",
                  false), NONE("none", false),

  CWE089("CWE089", true), CWE306("CWE306", true), CWE078("CWE078",
      true), CWE862("CWE862", true), CWE863("CWE863",
          true), CWETEST("CWEtest", true), CWE_NONE("none", true);

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
}

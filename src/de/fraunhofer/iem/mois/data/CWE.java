package de.fraunhofer.iem.mois.data;

/**
 * POJO for the CWEs
 *
 * @author Goran Piskachev
 *
 */

public class CWE {

  private String id;
  private String link;
  private String shortName;
  private String name;

  public CWE() {
    id = "";
    link = "";
    shortName = "";
    name = "";
  }

  public CWE(String i, String n, String sn, String l) {
    id = i;
    link = l;
    shortName = sn;
    name = n;
  }

  public String getId() {
    return id;
  }

  public String getLink() {
    return link;
  }

  public String getName() {
    return name;
  }

  public String getShortName() {
    return shortName;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }
}

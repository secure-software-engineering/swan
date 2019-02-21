package de.fraunhofer.iem.swan.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import soot.SootMethod;
import soot.jimple.infoflow.data.SootMethodAndClass;

/**
 * Class representing a single method
 *
 * @author Lisa Nguyen Quang Do, Goran Piskachev
 *
 */
public class Method extends SootMethodAndClass {

  public enum SecLevel {
    HIGH, LOW, NEUTRAL;
  }

  private String framework = "";
  private String link = "";
  private String comment = "";
  private String discovery = "";
  private SecLevel secLevel = SecLevel.NEUTRAL;
  private RelevantPart dataIn = new RelevantPart();
  private RelevantPart dataOut = new RelevantPart();
  // private Set<CWE> cwes = new HashSet<CWE>();
  private Set<Category> categoriesTrained = new HashSet<Category>();
  private Set<Category> categoriesClassified = new HashSet<Category>();

  private Category categoryClassified = null;

  public Method(String methodName, String returnType, String className) {
    super(methodName, className, returnType, new ArrayList<String>());
  }

  public Method(String methodName, List<String> parameters, String returnType,
      String className) {
    super(methodName, className, returnType, parameters);
  }

  public Method(SootMethod sm) {
    super(sm);
  }

  public Method(SootMethodAndClass methodAndClass) {
    super(methodAndClass);
  }

  public Method deriveWithNewClass(String className) {
    Method m = new Method(this.getMethodName(), this.getParameters(),
        this.getReturnType(), className);
    m.setFramework(this.framework);
    m.setLink(this.link);
    m.setComment(this.comment);
    m.setDiscovery(this.discovery);
    m.setSecLevel(m.secLevel);
    m.setDataIn(this.dataIn);
    m.setDataOut(this.dataOut);
    // m.setCwes(this.cwes);
    m.setCategoriesTrained(this.categoriesTrained);
    m.setCategoryClassified(this.categoryClassified);
    return m;
  }

  @Override
  public String toString() {
    if (this.categoryClassified == null) return "";
    return getSignature() + " ->_"
        + this.categoryClassified.toString().toUpperCase() + "_";
  }

  /**
   * Gets whether this method has been annotated as a source, sink, neither,
   * sanitizer or authentication nor.
   * 
   * @return True if there is an annotation for this method, otherwise false.
   */
  public boolean isAnnotated() {
    return !this.categoriesTrained.isEmpty();
  }

  public String getFramework() {
    return framework;
  }

  public void setFramework(String framework) {
    this.framework = framework;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getDiscovery() {
    return discovery;
  }

  public void setDiscovery(String discovery) {
    this.discovery = discovery;
  }

  public SecLevel getSecLevel() {
    return secLevel;
  }

  public void setSecLevel(SecLevel secLevel) {
    this.secLevel = secLevel;
  }

  public RelevantPart getDataIn() {
    return dataIn;
  }

  public void setDataIn(RelevantPart dataIn) {
    this.dataIn = dataIn;
  }

  public RelevantPart getDataOut() {
    return dataOut;
  }

  public void setDataOut(RelevantPart dataOut) {
    this.dataOut = dataOut;
  }

  public Category getCategoryClassified() {
    return categoryClassified;
  }

  public void setCategoryClassified(Category category) {
    this.categoryClassified = category;
  }

  public Set<Category> getCategoriesTrained() {
    return categoriesTrained;
  }

  public void setCategoriesTrained(Set<Category> categoriesTrained) {
    this.categoriesTrained = categoriesTrained;
  }

  public void addCategoriesTrained(Set<Category> categories) {
    this.categoriesTrained.addAll(categories);
  }

  public void addCategoryTrained(Category category) {
    this.categoriesTrained.add(category);
  }

  public Set<Category> getCategoriesClassified() {
    return this.categoriesClassified;
  }

  public void addCategoryClassified(Category category) {
    this.categoriesClassified.add(category);
  }

}
package de.fraunhofer.iem.swan.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import soot.SootMethod;
import soot.Type;

/**
 * Class representing a single method
 *
 * @author Lisa Nguyen Quang Do, Goran Piskachev
 *
 */
public class Method {

	// Inherited by SootMethodAndClass (from Soot Infoflow)
	private final String methodName;
	private final String className;
	private final String returnType;
	private final List<String> parameters;

	private String subSignature = null;
	private String signature = null;
	private int hashCode = 0;

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
		this.methodName = methodName;
		this.className = className;
		this.returnType = returnType;
		this.parameters = new ArrayList<String>();

	}

	public Method(String methodName, List<String> parameters, String returnType, String className) {
		this.methodName = methodName;
		this.className = className;
		this.returnType = returnType;
		this.parameters = parameters;
	}

	public Method(SootMethod sm) {
		this.methodName = sm.getName();
		this.className = sm.getDeclaringClass().getName();
		this.returnType = sm.getReturnType().toString();
		this.parameters = new ArrayList<String>();
		for (Type p : sm.getParameterTypes())
			this.parameters.add(p.toString());
	}

	public Method(Method methodAndClass) {
		this.methodName = methodAndClass.methodName;
		this.className = methodAndClass.className;
		this.returnType = methodAndClass.returnType;
		this.parameters = new ArrayList<String>(methodAndClass.parameters);
	}

	public Method deriveWithNewClass(String className) {
		Method m = new Method(this.getMethodName(), this.getParameters(), this.getReturnType(), className);
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
		if (this.categoryClassified == null)
			return "";
		return getSignature() + " ->_" + this.categoryClassified.toString().toUpperCase() + "_";
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

	// Inherited from SootMethodAndClass (from Soot Infoflow)
	public String getMethodName() {
		return this.methodName;
	}

	public String getClassName() {
		return this.className;
	}

	public String getReturnType() {
		return this.returnType;
	}

	public List<String> getParameters() {
		return this.parameters;
	}

	public String getSubSignature() {
		if (subSignature != null)
			return subSignature;

		StringBuilder sb = new StringBuilder(
				10 + this.returnType.length() + this.methodName.length() + (this.parameters.size() * 30));
		if (!this.returnType.isEmpty()) {
			sb.append(this.returnType);
			sb.append(" ");
		}
		sb.append(this.methodName);
		sb.append("(");

		for (int i = 0; i < this.parameters.size(); i++) {
			if (i > 0)
				sb.append(",");
			sb.append(this.parameters.get(i).trim());
		}
		sb.append(")");
		this.subSignature = sb.toString();

		return this.subSignature;
	}

	public String getSignature() {
		if (signature != null)
			return signature;

		StringBuilder sb = new StringBuilder(10 + this.className.length() + this.returnType.length()
				+ this.methodName.length() + (this.parameters.size() * 30));
		sb.append("<");
		sb.append(this.className);
		sb.append(": ");
		if (!this.returnType.isEmpty()) {
			sb.append(this.returnType);
			sb.append(" ");
		}
		sb.append(this.methodName);
		sb.append("(");

		for (int i = 0; i < this.parameters.size(); i++) {
			if (i > 0)
				sb.append(",");
			sb.append(this.parameters.get(i).trim());
		}
		sb.append(")>");
		this.signature = sb.toString();

		return this.signature;
	}

	@Override
	public boolean equals(Object another) {
		if (super.equals(another))
			return true;
		if (!(another instanceof Method))
			return false;
		Method otherMethod = (Method) another;

		if (!this.methodName.equals(otherMethod.methodName))
			return false;
		if (!this.parameters.equals(otherMethod.parameters))
			return false;
		if (!this.className.equals(otherMethod.className))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		if (this.hashCode == 0)
			this.hashCode = this.methodName.hashCode() + this.className.hashCode() * 5;
		// The parameter list is available from the outside, so we can't cache it
		return this.hashCode + this.parameters.hashCode() * 7;
	}

}
package de.fraunhofer.iem.swan.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import soot.SootMethod;
import soot.Type;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class representing a single method
 *
 * @author Lisa Nguyen Quang Do, Goran Piskachev
 */
public class Method {

    // Inherited by SootMethodAndClass (from Soot Infoflow)
    private String name;
    private String className;
    @JsonProperty("return")
    private String returnType;
    private List<String> parameters;
    private String subSignature;
    private String signature;
    private int hashCode = 0;
    private String framework;
    private String link;
    private String comment;
    private String discovery;
    private RelevantPart dataIn;
    private RelevantPart dataOut;
    @JsonProperty("type")
    private Set<Category> srm;
    private Set<Category> cwe;

    private Javadoc javadoc = new Javadoc();
    @JsonProperty("jar")
    private String sourceJar;

    public Method() {
        cwe = new HashSet<>();
    }

    public Method(String name, String returnType, String className) {
        this.name = name;
        this.className = className;
        this.returnType = returnType;
        this.parameters = new ArrayList<>();
    }

    public Method(String name, List<String> parameters, String returnType, String className) {
        this.name = name;
        this.className = className;
        this.returnType = returnType;
        this.parameters = parameters;
    }

    public Method(SootMethod sm) {
        this.name = sm.getName();
        this.className = sm.getDeclaringClass().getName();
        this.returnType = sm.getReturnType().toString();
        this.parameters = new ArrayList<String>();
        for (Type p : sm.getParameterTypes())
            this.parameters.add(p.toString());
    }

    public Method(Method methodAndClass) {
        this.name = methodAndClass.name;
        this.className = methodAndClass.className;
        this.returnType = methodAndClass.returnType;
        this.parameters = new ArrayList<String>(methodAndClass.parameters);
    }

    public Method deriveWithNewClass(String className) {
        Method m = new Method(className + "." + this.getName(), this.getParameters(), this.getReturnType());
        m.setFramework(this.framework);
        m.setLink(this.link);
        m.setComment(this.comment);
        m.setDiscovery(this.discovery);
        m.setDataIn(this.dataIn);
        m.setDataOut(this.dataOut);
        // m.setCwes(this.cwes);
        m.setSrm(this.srm);
        return m;
    }

    /**
     * Gets whether this method has been annotated as a source, sink, neither,
     * sanitizer or authentication nor.
     *
     * @return True if there is an annotation for this method, otherwise false.
     */
    @JsonIgnore
    public boolean isAnnotated() {
        return !this.srm.isEmpty();
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

    public Set<Category> getSrm() {
        return srm;
    }

    @JsonIgnore
    public Set<Category> getAuthSrm() {
        if (srm != null || cwe != null)
            return srm.stream().filter(Category::isAuthentication).collect(Collectors.toSet());
        else return new HashSet<>();
    }

    public void setSrm(Set<Category> srm) {
        this.srm = srm;
    }

    public void setCwe(Set<Category> categories) {
        this.cwe = categories;
    }

    public Set<Category> getCwe() {
        return this.cwe;
    }

    @JsonIgnore
    public Set<Category> getAllCategories() {
        if (srm != null || cwe != null)
            return Stream.of(srm, cwe).flatMap(Collection::stream).collect(Collectors.toSet());
        else
            return new HashSet<>();
    }

    public void addCategoryClassified(Category category) {
        this.cwe.add(category);
    }

    // Inherited from SootMethodAndClass (from Soot Infoflow)
    public String getName() {
        return this.name;
    }

    @JsonIgnore
    public String getClassName() {

        if (name.contains("."))
            return name.substring(0, name.lastIndexOf("."));
        else
            return name;
    }

    public String getReturnType() {
        return this.returnType;
    }

    public List<String> getParameters() {
        return this.parameters;
    }

    @JsonIgnore
    public String getSubSignature() {
        if (subSignature != null)
            return subSignature;

        StringBuilder sb = new StringBuilder();
        if (!this.returnType.isEmpty()) {
            sb.append(this.returnType);
            sb.append(" ");
        }
        sb.append(trimProperty(this.name));
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

    @JsonIgnore
    public String getTrimmedSignature() {

        String signature = getSignature();
        return signature.substring(1, signature.length() - 1);
    }

    @JsonIgnore
    public String getSignature() {
        if (signature != null)
            return signature;

        StringBuilder sb = new StringBuilder();
        sb.append("<");
        sb.append(getClassName());
        sb.append(": ");
        if (!this.returnType.isEmpty()) {
            sb.append(this.returnType);
            sb.append(" ");
        }
        sb.append(trimProperty(getName()));
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

    /**
     * Returns method's signature
     *
     * @return Method's signature
     */
    @JsonIgnore
    public String getSimpleSignature() {

        return trimProperty(getReturnType()) + " " + trimProperty(getName()) + " (" + StringUtils.join(getParameters(true), ", ") + ")";
    }

    /**
     * Returns the list of parameters
     *
     * @param isFullyQualifiedName Condition to determine if fully qualified name of class should be returned
     * @return List of parameters
     */
    @JsonIgnore
    public List<String> getParameters(boolean isFullyQualifiedName) {

        List<String> param = new ArrayList<>();

        if (!isFullyQualifiedName) {
            for (String parameter : getParameters()) {
                param.add(trimProperty(parameter));
            }
            return param;
        }

        return getParameters();
    }

    /**
     * Trim argument and returns last string
     *
     * @param property Classname or data to be trimmed
     * @return Trimmed data
     */
    private String trimProperty(String property) {
        return property.substring(property.lastIndexOf(".") + 1);
    }


    /**
     * Returns method signature in Java format.
     *
     * @return Java-style signature
     */
    @JsonIgnore
    public String getJavaSignature() {

        String methodName = getName();
        if (getName().equals("<init>"))
            methodName = getClassName().substring(getClassName().lastIndexOf(".") + 1);

        return this.returnType + " " + this.className + "." + methodName + "(" + StringUtils.join(this.parameters, ", ") + ")";
    }

    @JsonIgnore
    public String getArffSafeSignature() {

        return getSignature().replace(",", "+");
    }

    public void setJavadoc(Javadoc javadoc) {
        this.javadoc = javadoc;
    }

    public Javadoc getJavadoc() {
        return javadoc;
    }

    public String getSourceJar() {
        return sourceJar;
    }

    public void setSourceJar(String source) {
        this.sourceJar = source;
    }

    @Override
    public boolean equals(Object another) {
        if (super.equals(another))
            return true;
        if (!(another instanceof Method))
            return false;
        Method otherMethod = (Method) another;

        if (!this.name.equals(otherMethod.name))
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
            this.hashCode = this.name.hashCode() * 5;
        // The parameter list is available from the outside, so we can't cache it
        return this.hashCode + this.parameters.hashCode() * 7;
    }
}
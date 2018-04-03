package de.fraunhofer.iem.mois.assist.data;

import org.apache.commons.lang.StringUtils;

import java.util.*;

//Defines various properties for the methods.

public class Method extends Object {

    private String className;
    private String returnType;
    private String discovery;
    private String framework;
    private String link;
    private String comment;
    private String secLevel;
    private Set<Category> categories = new HashSet<Category>();
    private List<String> parameters = new ArrayList<String>();
    private String icon;
    private boolean isNewMethod;
    private String updateOperation = "";

    public Method() {

    }

    public Method(String className, String returnType, String discovery, String framework, String link, String comment, String secLevel) {
        this.className = className;
        this.returnType = returnType;
        this.discovery = discovery;
        this.framework = framework;
        this.link = link;
        this.comment = comment;
        this.secLevel = secLevel;
    }

    @Override
    public boolean equals(Object obj) {

        Method method;
     //   if (super.equals(obj))
        //    return true;
         if (obj instanceof Method) {

            method = (Method) obj;

            return this.getClassName(true).equals(method.getClassName(true));
        } else return false;
    }

    private String trimProperty(String property) {
        return property.substring(property.lastIndexOf(".") + 1);
    }

    //Returns classname for method
    public String getClassName(boolean fullyQualifiedName) {

        if (!fullyQualifiedName)
            return trimProperty(className);
        else
            return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void addCategory(Category category) {
        categories.add(category);
    }

    public void removeCategories() {
        categories.clear();
    }

    //Returns array list of CWEs that are assigned to the method
    public ArrayList<String> getCWEList() {

        Iterator<Category> val = categories.iterator();

        ArrayList<String> cweList = new ArrayList<String>();

        while (val.hasNext()) {

            Category cur = val.next();

            if (cur.isCwe()) {
                cweList.add(cur.toString());
            }
        }

        return cweList;
    }

    //Returns array list of types that are assigned to the method
    public ArrayList<String> getTypesList() {

        Iterator<Category> val = categories.iterator();

        ArrayList<String> typesList = new ArrayList<String>();

        while (val.hasNext()) {

            Category cur = val.next();

            if (!cur.isCwe()) {
                typesList.add(cur.toString());
            }
        }
        return typesList;
    }


    public void addParameter(List<String> params) {
        parameters = params;
    }

    public List<String> getParameters(){
        return parameters;
    }

    //Returns the list of parameters as a comma separated String based on setting for Fully Qualified Name
    public String getParameter(boolean isFullyQualifiedName) {

        List<String> param = new ArrayList<>(parameters);

        if (!isFullyQualifiedName)
            for (int counter = 0; counter < param.size(); counter++)
                param.set(counter, trimProperty(param.get(counter)));

        return StringUtils.join(param, ", ");
    }

    //Returns the return type of the function
    public String getReturnType(boolean isFullyQualifiedName) {

        if (!isFullyQualifiedName)
            return trimProperty(returnType);
        else
            return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getDiscovery() {
        return discovery;
    }

    public void setDiscovery(String discovery) {
        this.discovery = discovery;
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

    public String getSecLevel() {
        return secLevel;
    }

    public void setSecLevel(String secLevel) {
        this.secLevel = secLevel;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String iconName) {
        this.icon = iconName;
    }

    public boolean isNewMethod() {
        return isNewMethod;
    }

    public void setNewMethod(boolean newMethod) {
        isNewMethod = newMethod;
    }

    public String getUpdateOperation() {
        return updateOperation;
    }

    public void setUpdateOperation(String updateOperation) {
        this.updateOperation = updateOperation;
    }

    public enum SecLevel {
        HIGH, LOW, NEUTRAL
    }
}

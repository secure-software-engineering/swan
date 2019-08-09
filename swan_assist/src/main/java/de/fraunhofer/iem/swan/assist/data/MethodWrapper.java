/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.data;

import de.fraunhofer.iem.swan.assist.util.Formatter;
import de.fraunhofer.iem.swan.data.Category;
import org.apache.commons.lang3.StringUtils;
import de.fraunhofer.iem.swan.data.Method;
import java.util.*;

/**
 * Wrapper class to format data in method object to display in plugin.
 */
public class MethodWrapper {

    public enum MethodStatus {
        NONE,
        NEW,
        EXISTING,
        RESTORED,
        DELETED,
        SUGGESTED,
        TRAINING
    }

    private Method method;
    private Set<Category> categories = new HashSet<Category>();
    private List<String> parameters = new ArrayList<String>();
    private String icon;
    private boolean isTrainingMethod;
    private String updateOperation = "";
    private String markerMessage;
    private MethodStatus status;

    /**
     * Initializes MethodWrapper object.
     */
    public MethodWrapper() {

    }

    /**
     * Initializes MethodWrapper Object
     * @param method Method
     */
    public MethodWrapper(Method method) {
        this.method = method;
        status = MethodStatus.NONE;
    }

    /**
     * Initialises MethodWrapper object
     * @param methodName Name of method
     * @param parameters List of method's parameters
     * @param returnType Method's return type
     * @param className Method's fully qualified class name
     */
    public MethodWrapper(String methodName, List<String> parameters, String returnType,
                         String className) {

        this.method = new Method(methodName, parameters, returnType, className);
        status = MethodStatus.NONE;

    }

    /**
     * Returns Method object
     * @return Method object
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Trim argument and returns last string
     * @param property Classname or data to be trimmed
     * @return Trimmed data
     */
    private String trimProperty(String property) {
        return property.substring(property.lastIndexOf(".") + 1);
    }

    /**
     * Returns classname for method
     * @param fullyQualifiedName Condition to determine if fully qualified name of class should be returned
     * @return Method name
     */
    public String getMethodName(boolean fullyQualifiedName) {

        if (!fullyQualifiedName)
            return trimProperty(method.getClassName() + "." + method.getMethodName());
        else
            return method.getClassName() + "." + method.getMethodName();
    }

    /**
     * Returns classname for method
     * @param fullyQualifiedName Condition to determine if fully qualified name of class should be returned
     * @return Method's classname
     */
    public String getClassName(boolean fullyQualifiedName) {

        if (!fullyQualifiedName)
            return trimProperty(method.getClassName());
        else
            return method.getClassName();
    }

    /**
     * Returns filename for method
     * @return Filename with extension
     */
    public String getFileName() {
        return getClassName(false) + ".java";
    }


    /**
     * Returns method's signature
     * @param isfullyQualifiedName Condition to determine if fully qualified name of class should be returned
     * @return Method's signature
     */
    public String getSignature(boolean isfullyQualifiedName) {

        return getReturnType(isfullyQualifiedName) + " " + getMethodName(isfullyQualifiedName) + " (" + getParameter(isfullyQualifiedName) + ")";

    }

    /**
     * Returns the list of parameters as a comma separated String
     * @param isFullyQualifiedName Condition to determine if fully qualified name of class should be returned
     * @return List of parameters as comma separated spring
     */
    public String getParameter(boolean isFullyQualifiedName) {

        List<String> param = new ArrayList<>();

        if (!isFullyQualifiedName) {
            for (String parameter : method.getParameters()) {
                param.add(trimProperty(parameter));
            }
            return StringUtils.join(param, ", ");
        }

        return StringUtils.join(method.getParameters(), ", ");
    }

    /**
     * Returns the return type of the function
     * @param isFullyQualifiedName Condition to determine if fully qualified name of class should be returned
     * @return Method's return type
     */
    public String getReturnType(boolean isFullyQualifiedName) {

        if (!isFullyQualifiedName)
            return trimProperty(method.getReturnType());
        else
            return method.getReturnType();
    }

    /**
     * Returns array list of CWEs that are assigned to the method
     * @return Array list of CWE's
     */
    public ArrayList<String> getCWEList() {

        ArrayList<String> cweList = new ArrayList<String>();

        for (Category category : method.getCategoriesTrained()) {
            if (category.isCwe()) {
                cweList.add(category.toString());
            }
        }

        return cweList;
    }

    /**
     * Returns array list of types that are assigned to the method
     * @param capitalize Condition to capitialize first character of types
     * @return Array list of types
     */
    public ArrayList<String> getTypesList(boolean capitalize) {

        ArrayList<String> typesList = new ArrayList<String>();

        for (Category category : method.getCategoriesTrained()) {
            if (!category.isCwe() && !capitalize) {
                typesList.add(category.toString());
            } else if (!category.isCwe())
                typesList.add(Formatter.toTitleCase(category.toString()));
        }

        return typesList;
    }

    /**
     * Returns list of parameters
     * @return List of method parameters
     */
    public List<String> getParameters() {
        return parameters;
    }

    /**
     * Returns set of method categories
     * @return Set of Method categories
     */
    public Set<Category> getCategories() {
        return method.getCategoriesTrained();
    }

    /**
     * Set method categories
     * @param categories Method categories
     */
    public void setCategories(Set<Category> categories) {

       method.setCategoriesTrained(categories);
    }

    /**
     * Returns update operation for method: new, delete, etc.
     * @return Update operation for method.
     */
    public String getUpdateOperation() {
        return updateOperation;
    }

    /**
     * Returns whether or not the method is a training method
     * @return Whether or not the method is a training method
     */
    public boolean isTrainingMethod() {
        return isTrainingMethod;
    }

    /**
     * Set whether or not the method is a training method
     * @param trainingMethod Whether or not the method is a training method
     */
    public void setTrainingMethod(boolean trainingMethod) {
        isTrainingMethod = trainingMethod;
    }

    /**
     * Set method's update operation
     * @param updateOperation Method's update operation
     */
    public void setUpdateOperation(String updateOperation) {
        this.updateOperation = updateOperation;
    }

    //TODO Implement control structure to create customised messages

    /**
     * Generates and returns the message for the editor marker
     * @return Message for editor marker
     */
    public String getMarkerMessage() {
        return "This is a potential " + StringUtils.join(getTypesList(true), ", ") + " method of sensitive information relevant for " + StringUtils.join(getCWEList(), ", ");
    }

    /**
     * Set message that should be used for marker.
     * @param markerMessage Method's message
     */
    public void setMarkerMessage(String markerMessage) {
        this.markerMessage = markerMessage;
    }

    /**
     * Status of method: suggested, etc.
     * @return Method's status/origin
     */
    public MethodStatus getStatus() {
        return status;
    }

    /**
     * Set method status
     * @param status Method's status/origin
     */
    public void setStatus(MethodStatus status) {
        this.status = status;
    }

    /**
     * Returns method as string: method signature
     * @return Method signature
     */
    public String toString(){
        return getSignature(false);
    }
}

package de.fraunhofer.iem.swan.assist.data;

import de.fraunhofer.iem.swan.assist.util.Formatter;
import de.fraunhofer.iem.swan.data.Category;
import org.apache.commons.lang3.StringUtils;
import de.fraunhofer.iem.swan.data.Method;
import java.util.*;

/**
 * Wrapper class to format data in method object to display in plugin.
 *
 * @author Oshando Johnson on 25.10.18
 */
public class MethodWrapper {

    private Method method;
    private Set<Category> categories = new HashSet<Category>();
    private List<String> parameters = new ArrayList<String>();
    private String icon;
    private boolean isNewMethod;
    private boolean isTrainingMethod;
    private String updateOperation = "";
    private String markerMessage;

    public MethodWrapper() {

    }

    public MethodWrapper(Method method) {
        this.method = method;
    }

    public MethodWrapper(String methodName, List<String> parameters, String returnType,
                         String className) {

        this.method = new Method(methodName, parameters, returnType, className);

    }

    public Method getMethod() {
        return method;
    }

    private String trimProperty(String property) {
        return property.substring(property.lastIndexOf(".") + 1);
    }

    //Returns classname for method
    public String getMethodName(boolean fullyQualifiedName) {

        if (!fullyQualifiedName)
            return trimProperty(method.getClassName() + "." + method.getMethodName());
        else
            return method.getClassName() + "." + method.getMethodName();
    }

    //Returns classname for method
    public String getClassName(boolean fullyQualifiedName) {

        if (!fullyQualifiedName)
            return trimProperty(method.getClassName());
        else
            return method.getClassName();
    }

    //Returns filename for method
    public String getFileName() {
        return getClassName(false) + ".java";
    }

    //Returns classname for method
    public String getSignature(boolean isfullyQualifiedName) {

        return getReturnType(isfullyQualifiedName) + " " + getMethodName(isfullyQualifiedName) + " (" + getParameter(isfullyQualifiedName) + ")";

    }

    //Returns the list of parameters as a comma separated String based on setting for Fully Qualified Name
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

    //Returns the return type of the function
    public String getReturnType(boolean isFullyQualifiedName) {

        if (!isFullyQualifiedName)
            return trimProperty(method.getReturnType());
        else
            return method.getReturnType();
    }

    //Returns array list of CWEs that are assigned to the method
    public ArrayList<String> getCWEList() {

        ArrayList<String> cweList = new ArrayList<String>();

        for (Category category : method.getCategoriesTrained()) {
            if (category.isCwe()) {
                cweList.add(category.toString());
            }
        }

        return cweList;
    }

    //Returns array list of types that are assigned to the method
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

    public List<String> getParameters() {
        return parameters;
    }

    public Set<Category> getCategories() {
        return method.getCategoriesTrained();
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

    public boolean isTrainingMethod() {
        return isTrainingMethod;
    }

    public void setTrainingMethod(boolean trainingMethod) {
        isTrainingMethod = trainingMethod;
    }
    public void setUpdateOperation(String updateOperation) {
        this.updateOperation = updateOperation;
    }

    //TODO Implement control structure to create customised messages
    public String getMarkerMessage() {
        return "This is a potential " + StringUtils.join(getTypesList(true), ", ") + " method of sensitive information relevant for " + StringUtils.join(getCWEList(), ", ");
    }

    public void setMarkerMessage(String markerMessage) {
        this.markerMessage = markerMessage;
    }

}

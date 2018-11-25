package de.fraunhofer.iem.mois.assist.data;

import de.fraunhofer.iem.mois.data.Category;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Wrapper class to format data in method object to display in plugin.
 *
 * @author Oshando Johnson on 25.10.18
 */
public class MethodWrapper {

    private de.fraunhofer.iem.mois.data.Method method;
    private Set<Category> categories = new HashSet<Category>();
    private List<String> parameters = new ArrayList<String>();
    private String icon;
    private boolean isNewMethod;
    private String updateOperation = "";


    public MethodWrapper() {

    }

    public MethodWrapper(de.fraunhofer.iem.mois.data.Method method) {
        this.method = method;
    }

    public MethodWrapper(String methodName, List<String> parameters, String returnType,
                         String className) {

        this.method = new de.fraunhofer.iem.mois.data.Method(methodName, parameters, returnType, className);

    }

    public de.fraunhofer.iem.mois.data.Method getMethod() {
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


    //Returns classname for method
    public String getSignature(boolean isfullyQualifiedName) {

        return getReturnType(isfullyQualifiedName) + " " + getMethodName(isfullyQualifiedName) + " (" + getParameter(isfullyQualifiedName) + ")";

    }

    //Returns the list of parameters as a comma separated String based on setting for Fully Qualified Name
    public String getParameter(boolean isFullyQualifiedName) {

        List<String> param = method.getParameters();

        if (!isFullyQualifiedName)
            for (int counter = 0; counter < param.size(); counter++)
                param.set(counter, trimProperty(param.get(counter)));

        return StringUtils.join(param, ", ");
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
    public ArrayList<String> getTypesList() {

        ArrayList<String> typesList = new ArrayList<String>();

        for (Category category : method.getCategoriesTrained()) {
            if (!category.isCwe()) {
                typesList.add(category.toString());
            }
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

    public void setUpdateOperation(String updateOperation) {
        this.updateOperation = updateOperation;
    }


}

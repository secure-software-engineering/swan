/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.util;

import org.apache.commons.lang3.StringUtils;
import de.fraunhofer.iem.swan.data.Method;

import java.util.List;

/**
 * Functions to format data.
 */

public class Formatter {

    public static String toTitleCase(String text){

        return text.substring(0,1).toUpperCase()+text.substring(1);
    }

    public static String getFileNameFromPath(String path){

        return path.substring(0, path.indexOf("."));
    }


    public static String trimProperty(String property) {
        return property.substring(property.lastIndexOf(".") + 1);
    }

    //Returns classname for method
    public static String getClassName(String className, boolean fullyQualifiedName) {

        if (!fullyQualifiedName)
            return trimProperty(className);
        else
            return className;
    }

    //Returns classname for method
    public static String getSignature(Method method, boolean isfullyQualifiedName) {

        return getReturnType(method.getReturnType(),isfullyQualifiedName) + " " + getClassName(method.getClassName(), isfullyQualifiedName) + " (" + getParameter(method.getParameters(), isfullyQualifiedName) + ")";

    }


    //Returns the list of parameters as a comma separated String based on setting for Fully Qualified Name
    public static String getParameter(List<String> param , boolean isFullyQualifiedName) {

       // List<String> param = new ArrayList<>(parameters);

        if (!isFullyQualifiedName)
            for (int counter = 0; counter < param.size(); counter++)
                param.set(counter, trimProperty(param.get(counter)));

        return StringUtils.join(param, ", ");
    }

    //Returns the return type of the function
    public static String getReturnType(String returnType, boolean isFullyQualifiedName) {

        if (!isFullyQualifiedName)
            return trimProperty(returnType);
        else
            return returnType;
    }

}

package de.fraunhofer.iem.swan.util;

import de.fraunhofer.iem.swan.data.Method;
import java.util.Arrays;

public class SootUtils {

    public static Method convertSootSignature(String sootSignature){

        //Ignore < and > at the beginning and end of the signature
        String[] parts = sootSignature.substring(1, sootSignature.length()-1).split(":");

        String className = parts[0];
        String[] method = parts[1].trim().split(" ");
        String returnType = method[0];
        String methodName = method[1].substring(0, method[1].indexOf("("));
        String[] paramList = method[1].substring(method[1].indexOf("(") + 1, method[1].indexOf(")")).split(",");

        //TODO correctly handle <init> and <clinit>
        if (methodName.contains("<init>"))
            methodName = getSimpleName(className);

        return new Method(methodName, Arrays.asList(paramList), returnType, className);
    }

    /**
     * Returns last part of a fully qualified name string.
     *
     * @param value Namespace of object
     * @return returns simple name
     */
    public static String getSimpleName(String value) {

        if (!value.contains("."))
            return value;
        else
            return value.substring(value.lastIndexOf(".") + 1);
    }
}
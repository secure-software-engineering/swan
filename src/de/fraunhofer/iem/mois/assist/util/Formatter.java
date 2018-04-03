package de.fraunhofer.iem.mois.assist.util;

public class Formatter {

    public static String capitalizeFirstCharacter(String text){

        return text.substring(0,1).toUpperCase()+text.substring(1);
    }

    public static String getFileNameFromPath(String path){

        return path.substring(0, path.indexOf("."));
    }
}

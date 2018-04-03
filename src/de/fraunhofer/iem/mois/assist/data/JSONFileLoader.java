package de.fraunhofer.iem.mois.assist.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class JSONFileLoader {

    static private ArrayList<Method> methods;
    static private String congFile = "";

    //Get configuration file location
    public static void setConfigurationFile(String path) {

        congFile = path;
    }

    //Get configuration file location
    public static String getConfigurationFile(boolean path) {

        if (path)
            return congFile;
        else
            return congFile.substring(congFile.lastIndexOf("/") + 1, congFile.length());

    }

    //Returns whether or not a configuration file was selected
    public static boolean isFileSelected() {

        return !getConfigurationFile(true).isEmpty();
    }

    //Import configuration details from JSON file
    public static void loadInitialFile() {

        JSONFileParser fileParser = new JSONFileParser(congFile);
        methods = fileParser.parseJSONFile();
    }

    //Compares new JSON file with original file and
    public static void loadUpdatedFile(String newFilePath) {

        JSONFileComparator fileComparator = new JSONFileComparator(congFile, newFilePath);
        methods = fileComparator.compareJSONFile();
        setConfigurationFile(newFilePath);
    }

    //Return list of methods as an array
    public static ArrayList<Method> getMethods() {

        return methods;
    }


    //Return list of methods as an array using categories
    public static ArrayList<Method> getMethods(ArrayList<String> filters, String currentFile, boolean currentFileMode) {

        //TODO use data structure that can search based on keys (method names/signatures)
        if (filters.size() == 0 && currentFileMode) {
            ArrayList<Method> filteredList = new ArrayList<>();
            for (Method method : methods) {

                if (method.getClassName(true).contains(currentFile)) {
                    filteredList.add(method);
                }
            }
            return filteredList;
        } else if (filters.size() > 0) {

            ArrayList<Method> filteredList = new ArrayList<>();

            for (Method method : methods) {

                if (currentFileMode && !method.getClassName(true).contains(currentFile))
                    continue;

                for (Category category : method.getCategories()) {

                    if (filters.contains(category.toString())) {
                        filteredList.add(method);
                        break;
                    }
                }
            }
            return filteredList;
        } else
            return methods;
    }

    //Return list of categories as a set
    public static Set<Category> getCategories() {

        Set<Category> category = new HashSet<>();

        for (Method method : methods) {

            Iterator<Category> val = method.getCategories().iterator();

            while (val.hasNext()) {

                Category cur = val.next();

                if (!category.contains(cur))
                    category.add(cur);
            }
        }
        return category;
    }

    //Add new method to the list
    public static void addMethod(Method method) {

        Method originalMethod = null;

        for (Method search : methods) {

            if (search.getClassName(true).equals(method.getClassName(true))) {
                originalMethod = search;
            }
        }

        if (originalMethod == null) {
            methods.add(method);
        } else {
            methods.remove(originalMethod);
            methods.add(method);
        }
    }

    //Remove method from list
    public static void removeMethod(Method method) {

        System.out.println("Total; "+methods.size());
        Method originalMethod = null;

        for (Method search : methods) {

            if (search.getClassName(true).equals(method.getClassName(true))) {
                originalMethod = search;
            }
        }

        if (originalMethod != null) {
            methods.remove(originalMethod);
            System.out.println("Total; "+methods.size());
        }
    }
}

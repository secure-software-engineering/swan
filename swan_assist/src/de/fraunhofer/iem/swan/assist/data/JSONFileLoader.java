package de.fraunhofer.iem.swan.assist.data;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import de.fraunhofer.iem.swan.assist.util.Constants;
import de.fraunhofer.iem.swan.assist.util.Formatter;
import de.fraunhofer.iem.swan.data.Category;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class JSONFileLoader {

    static private HashMap<String, MethodWrapper> methods;
    static private String congFile = "";
    static public final int NEW_METHOD = 0;
    static public final int EXISTING_METHOD = 1;
    static public final int RESTORED_METHOD = 2;
    static private boolean reloadingSwan = false;


    //Get configuration file location
    public static void setConfigurationFile(String path) {

        congFile = path;
    }

    //Get configuration file location
    public static String getConfigurationFile(boolean path) {

        if (path)
            return congFile;
        else
            return congFile.substring(congFile.lastIndexOf("/") + 1);
    }

    //Returns whether or not a configuration file was selected
    public static boolean isFileSelected() {

        return !getConfigurationFile(true).isEmpty();
    }

    //Import configuration details from JSON file
    public static void loadInitialFile() {

        JSONFileParser fileParser = new JSONFileParser(congFile);
        methods = fileParser.parseJSONFileMap();
    }

    //Compares new JSON file with original file and
    public static void loadUpdatedFile(String newFilePath) {

        JSONFileComparator fileComparator = new JSONFileComparator(congFile, newFilePath);
        methods = fileComparator.compareJSONFile();
        setConfigurationFile(newFilePath);
    }

    //Return list of methods as an array
    public static ArrayList<MethodWrapper> getMethods() {

        return new ArrayList<>(methods.values());
    }

    //Return list of methods as an array
    public static HashMap<String, MethodWrapper> getAllMethods() {

        return methods;
    }

    //Return list of methods as an array using categories
    public static ArrayList<MethodWrapper> getMethods(ArrayList<Pair<String, String>> filters, String currentFile, Project project) {

        if (filters.size() > 0) {

            //case where file selected but no categories .
            if (filters.size() == 1 && filters.contains(Constants.FILE_FILTER)) {

                ArrayList<MethodWrapper> filteredList = new ArrayList<>();

                for (String methodSignature : methods.keySet()) {
                    if (methodSignature.contains(currentFile)) {
                        filteredList.add(methods.get(methodSignature));
                    }
                }
                return filteredList;
            } else {
                //case where file is selected and categories
                return filterList(filters, currentFile);
            }
        } else {
            ArrayList<MethodWrapper> filteredList = new ArrayList<>();

            for (MethodWrapper method : methods.values()) {


                if (method.getUpdateOperation().equals(Constants.METHOD_DELETED) || method.isTrainingMethod())
                    continue;

                filteredList.add(method);
            }
            return filteredList;
        }
    }

    private static ArrayList<MethodWrapper> filterList(ArrayList<Pair<String, String>> filters, String currentFile) {
        ArrayList<MethodWrapper> filteredList = new ArrayList<>();

        for (String methodSignature : methods.keySet()) {

            if ((filters.contains(Constants.FILE_FILTER) && !methodSignature.contains(currentFile))
                    || (!filters.contains(Constants.DELETED_FILTER) && methods.get(methodSignature).getUpdateOperation().equals(Constants.METHOD_DELETED))
                    || (methods.get(methodSignature).isTrainingMethod() && !filters.contains(Constants.TRAIN_FILTER)))
                continue;

            for (Category category : methods.get(methodSignature).getCategories()) {

                if ((filters.contains(Constants.DELETED_FILTER) && methods.get(methodSignature).getUpdateOperation().equals(Constants.METHOD_DELETED))
                        || filters.contains(new Pair<>(Constants.FILTER_TYPE, Formatter.toTitleCase(category.toString())))
                        || filters.contains(new Pair<>(Constants.FILTER_CWE, Formatter.toTitleCase(category.toString())))
                        || (methods.get(methodSignature).isTrainingMethod() && filters.contains(Constants.TRAIN_FILTER))) {

                    filteredList.add(methods.get(methodSignature));
                    break;
                }
            }
        }
        return filteredList;
    }


    //Return list of categories as a set
    public static Set<Category> getCategories() {

        Set<Category> categorySet = new HashSet<>();

        for (MethodWrapper method : methods.values()) {

            for (Category category : method.getCategories()) {
                if (!categorySet.contains(category))
                    categorySet.add(category);
            }
        }
        return categorySet;
    }

    //Add new method to the list
    public static int addMethod(MethodWrapper method) {

        if (methods.containsKey(method.getSignature(true))) {
            methods.replace(method.getSignature(true), method);
            return EXISTING_METHOD;
        } else {
            methods.put(method.getSignature(true), method);
            return NEW_METHOD;
        }
    }

    //Check if method exists in list
    public static boolean methodExists(String methodSignature) {

        return methods.containsKey(methodSignature);
    }

    //Returns method for the specified signature
    public static MethodWrapper getMethod(String methodSignature) {

        return methods.get(methodSignature);
    }

    //Remove method from list
    public static void removeMethod(MethodWrapper method) {

        methods.remove(method.getSignature(true));
    }

    //Check if method is in project
    private static boolean inProject(String classname, Project project) {

        JavaPsiFacade facade = JavaPsiFacade.getInstance(project);
        PsiClass psiClass = facade.findClass(classname, GlobalSearchScope.projectScope(project));

        //psiClass.getAllMethods()[0].
        return psiClass != null;
    }

    public static boolean isReloading() {
        return reloadingSwan;
    }

    public static void setReloading(boolean reloadingSwan) {
        JSONFileLoader.reloadingSwan = reloadingSwan;
    }
}

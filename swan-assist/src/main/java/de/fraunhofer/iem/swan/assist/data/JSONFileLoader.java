/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.data;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import de.fraunhofer.iem.swan.assist.util.Constants;
import de.fraunhofer.iem.swan.assist.util.Formatter;
import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.assist.util.Pair;

import java.util.*;

/**
 * Loads JSON configuration file and manipulates data.
 */
public class JSONFileLoader {

    static private HashMap<String, MethodWrapper> methods= new HashMap<>();
    static private String congFile = "";
    static public final int NEW_METHOD = 0;
    static public final int EXISTING_METHOD = 1;
    static public final int RESTORED_METHOD = 2;
    static private boolean reloadingSwan = false;

    /**
     * Set configuration file location
     *
     * @param path Configuration file path.
     */
    public static void setConfigurationFile(String path, Project project) {

        congFile = path;
        PropertiesComponent.getInstance(project).setValue(Constants.CONFIGURATION_FILE, path);
    }

    /**
     * Returns configuration file path or file name
     *
     * @param path Condition determines if the path or just the filename should be returned.
     * @return Returns either the filename or file path.
     */
    public static String getConfigurationFile(boolean path) {

        if (path)
            return congFile;
        else
            return congFile.substring(congFile.lastIndexOf("/") + 1);
    }

    /**
     * Returns whether or not a configuration file was selected
     */
    public static boolean isFileSelected() {

        return !getConfigurationFile(true).isEmpty();
    }

    /**
     * Import configuration details from JSON file
     */
    public static void loadInitialFile() {

        JSONFileParser fileParser = new JSONFileParser(congFile);
        methods = fileParser.parseJSONFileMap();
    }

    /**
     * Compares new JSON file with original file and updates list with merged results. Loads new file
     * is a JSON file was not selected.
     *
     * @param newFilePath File path of new configuration file
     */
    public static void loadUpdatedFile(String newFilePath, Project project) {

        if (isFileSelected()) {
            JSONFileComparator fileComparator = new JSONFileComparator(congFile, newFilePath);
            methods = fileComparator.compareJSONFile();
            setConfigurationFile(newFilePath, project);
        } else {
            setConfigurationFile(newFilePath, project);
            loadInitialFile();
        }
    }

    /**
     * Return list of methods as an array list
     */
    public static ArrayList<MethodWrapper> getMethods() {

        return new ArrayList<>(methods.values());
    }

    /**
     * Return list of methods as an HashMap
     */
    public static HashMap<String, MethodWrapper> getAllMethods() {

        return methods;
    }

    /**
     * Returns all methods based on the filters and or current file that's selected.
     *
     * @param filters     Filters that should be applied to the methods returned.
     * @param currentFile Current active file opened in the IDE's editor.
     * @param project     Active project in IDE.
     * @return Returns an array list of the methods that meet the specified criteria.
     */
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

    public static TreeMap<String, ArrayList<MethodWrapper>> getMethodsForTree(ArrayList<Pair<String, String>> filters, String currentFile, Project project) {

        ArrayList<MethodWrapper> methods = getMethods(filters, currentFile, project);

        TreeMap<String, ArrayList<MethodWrapper>> methodMap = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String class1, String class2) {
                return Formatter.trimProperty(class1).toLowerCase().compareTo(Formatter.trimProperty(class2).toLowerCase());
            }
        });

        for (MethodWrapper method : methods) {

            if (methodMap.containsKey(method.getClassName(true))) {
                methodMap.get(method.getClassName(true)).add(method);
            } else {
                ArrayList<MethodWrapper> list = new ArrayList<>();
                list.add(method);
                methodMap.put(method.getClassName(true), list);
            }
        }

        return methodMap;
    }


    /**
     * Filters method methods using the filters provided.
     *
     * @param filters     Filters that should be applied to the methods returned.
     * @param currentFile Current active file opened in the IDE's editor.
     * @return Methods matching the filtering criteria.
     */
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

    /**
     * Return list of categories.
     *
     * @return Set of categories used by methods.
     */
    public static Set<Category> getCategories() {

        Set<Category> categorySet = new HashSet<>();

        for (MethodWrapper method : methods.values()) {

            for (Category category : method.getCategories()) {
                categorySet.add(category);
            }
        }
        return categorySet;
    }

    /**
     * Returns list of possible categories.
     *
     * @return Set of all possible categories.
     */
    public static Set<Category> getAllCategories() {


        Set<Category> categorySet = new HashSet<>();

        for (Category category : Category.values()) {

            if (!category.toString().equals(de.fraunhofer.iem.swan.data.Constants.NONE)
                    && !category.equals(Category.CWETEST)) {
                categorySet.add(category);
            }
        }
        return categorySet;
    }

    /**
     * Add new method to the list
     *
     * @param method Method to be added to the list
     * @return Returns whether the method is new or existing.
     */
    public static int addMethod(MethodWrapper method) {

        if (methods.containsKey(method.getSignature(true))) {
            methods.replace(method.getSignature(true), method);
            return EXISTING_METHOD;
        } else {
            methods.put(method.getSignature(true), method);
            return NEW_METHOD;
        }
    }

    /**
     * Checks if method exists in list
     *
     * @param methodSignature Method signature of method being searched for.
     * @return Returns whether or not the method exists.
     */
    public static boolean methodExists(String methodSignature) {

        return methods.containsKey(methodSignature);
    }

    /**
     * Returns an instance of the method
     *
     * @param methodSignature Method Signature of requested method.
     * @return Instance of the method
     */
    public static MethodWrapper getMethod(String methodSignature) {

        return methods.get(methodSignature);
    }

    /**
     * Remove method from list
     *
     * @param method Method to be removed
     */
    public static void removeMethod(MethodWrapper method) {

        methods.remove(method.getSignature(true));
    }

    /**
     * Check if method is used in project
     *
     * @param classname Method's classname
     * @param project   Instance of project
     * @return Returns whether or not the method is used in the project
     */
    private static boolean inProject(String classname, Project project) {

        JavaPsiFacade facade = JavaPsiFacade.getInstance(project);
        PsiClass psiClass = facade.findClass(classname, GlobalSearchScope.projectScope(project));

        //psiClass.getAllMethods()[0].
        return psiClass != null;
    }

    /**
     * Gives the status of SWAN
     *
     * @return Returns whether or not SWAN is reloading
     */
    public static boolean isReloading() {
        return reloadingSwan;
    }

    /**
     * Set the status of SWAN
     *
     * @param reloadingSwan Status of SWAN - reloading or not
     */
    public static void setReloading(boolean reloadingSwan) {
        JSONFileLoader.reloadingSwan = reloadingSwan;
    }
}

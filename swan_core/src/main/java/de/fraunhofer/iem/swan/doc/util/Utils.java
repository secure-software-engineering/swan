package de.fraunhofer.iem.swan.doc.util;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;

import java.util.HashMap;
import java.util.Set;

/**
 * @author Oshando Johnson on 04.09.20
 */
public class Utils {

    /**
     * Counts the number of methods that have doc comments for each class.
     *
     * @param methods set of methods
     * @return report showing distribution of categories
     */
    public static String countCategories(Set<Method> methods, boolean hasJavadoc) {

        HashMap<String, Integer> results = new HashMap<>();

        //Add counters for classes and methods
        results.put("all-methods", 0);
        results.put("all-classes", 0);

        //Add counters for all categories
        for (Category category : Category.values())
            results.put(category.toString(), 0);

        for (Method met : methods) {

            if (!met.getJavadoc().getMethodComment().equals("") && hasJavadoc) {
                results.put("all-methods", results.get("all-methods") + 1);

                if (!met.getJavadoc().getClassComment().equals(""))
                    results.put("all-classes", results.get("all-classes") + 1);
            } else if (!hasJavadoc) {
                results.put("all-methods", results.get("all-methods") + 1);
            }

            for (Category cat : met.getCategoriesTrained()) {
                if (!met.getJavadoc().getMethodComment().equals("") && hasJavadoc)
                    results.put(cat.toString(), results.get(cat.toString()) + 1);
                else if (!hasJavadoc)
                    results.put(cat.toString(), results.get(cat.toString()) + 1);
            }
        }

        return results.toString();
    }

}

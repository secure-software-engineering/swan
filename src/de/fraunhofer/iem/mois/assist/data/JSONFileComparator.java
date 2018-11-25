package de.fraunhofer.iem.mois.assist.data;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Compares initial and new configuration files and outlines differences.
 * @author Oshando Johnson
 */


public class JSONFileComparator {

    private String originalFilePath;
    private String newFilePath;

    JSONFileComparator(String originalPath, String newPath) {
        originalFilePath = originalPath;
        newFilePath = newPath;
    }

    public HashMap<String, MethodWrapper> compareJSONFile() {

        JSONFileParser fileParser = new JSONFileParser(originalFilePath);
        HashMap<String, MethodWrapper> originalList = fileParser.parseJSONFileMap();

        fileParser.setCongFilePath(newFilePath);
        HashMap<String, MethodWrapper> updatedList = fileParser.parseJSONFileMap();

        //Determine methods that were deleted
        Set<String> deletedMethods = originalList.keySet().stream()
                .filter(method -> !updatedList.keySet().contains(method))
                .collect(Collectors.toSet());

        //Determine methods that were deleted
        Set<String> addedMethods = updatedList.keySet().stream()
                .filter(method -> !originalList.keySet().contains(method))
                .collect(Collectors.toSet());

        for (String methodSignature : deletedMethods) {

            MethodWrapper method = originalList.get(methodSignature);
         //  method.setUpdateOperation(Constants.METHOD_DELETED);
            updatedList.put(methodSignature, method);
        }

        for (String methodSignature : addedMethods) {
            MethodWrapper method = updatedList.get(methodSignature);
          //  method.setUpdateOperation(Constants.METHOD_ADDED);
            updatedList.replace(methodSignature, method);
        }

        return updatedList;
    }
}

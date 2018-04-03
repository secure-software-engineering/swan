package de.fraunhofer.iem.mois.assist.data;

import de.fraunhofer.iem.mois.assist.util.Constants;

import java.util.ArrayList;

public class JSONFileComparator {

    private String originalFilePath;
    private String newFilePath;
    private ArrayList<Method> mergedList;


    JSONFileComparator(String originalPath, String newPath) {
        originalFilePath = originalPath;
        newFilePath = newPath;
    }

    public ArrayList<Method> compareJSONFile() {

        mergedList = new ArrayList<Method>();

        JSONFileParser originalParser = new JSONFileParser(originalFilePath);
        ArrayList<Method> originalList = originalParser.parseJSONFile();

        JSONFileParser updatedParser = new JSONFileParser(newFilePath);
        ArrayList<Method> updatedList = updatedParser.parseJSONFile();


        for (Method updated : updatedList) {
            boolean newMethod = true;
            System.out.println(updated.getClassName(false));
            for (Method original : originalList) {
                //   System.out.println(">>> "+original.getClassName(false));

                //TODO    if(updated.equals(original))
                if (updated.getClassName(true).equals(original.getClassName(true))) {
                    System.out.println(">>Method exists");
                    newMethod = false;
                    break;

                }
            }
                if (newMethod) {
                    System.out.println("New: " + updated.getClassName(false));
                    updated.setUpdateOperation(Constants.METHOD_ADDED);
                }

                mergedList.add(updated);
            }


        for (Method original : originalList) {
            boolean deletedMethod = true;
        //    System.out.println(updated.getClassName(false));
            for (Method updated : updatedList) {
                //   System.out.println(">>> "+original.getClassName(false));

                //TODO    if(updated.equals(original))
                if (updated.getClassName(true).equals(original.getClassName(true))) {
                    System.out.println(">>Method Deleted");
                    deletedMethod = false;
                    break;

                }
            }
                if (deletedMethod) {
                    System.out.println("deleted: " + original.getClassName(false));
                    original.setUpdateOperation(Constants.METHOD_DELETED);
                    mergedList.add(original);
                }



        }

        return mergedList;
    }

    public void compareList(ArrayList<Method> original, ArrayList<Method> updated) {

    }
}

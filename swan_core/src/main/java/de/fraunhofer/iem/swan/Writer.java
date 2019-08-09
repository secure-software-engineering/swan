package de.fraunhofer.iem.swan;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Constants;
import de.fraunhofer.iem.swan.data.Method;

public class Writer {

    private final Set<Method> testMethods;

    public Writer() {

        testMethods = new HashSet<>();
    }

    public Writer(Set<Method> testMethods) {
        this.testMethods = testMethods;
    }

    public void writeResultsToFiles(String targetFileName, Set<Method> methods, Set<Category> categories)
            throws IOException {
        Map<Category, Integer> counters = new HashMap<Category, Integer>();
        BufferedWriter wr = null;

        File file = new File(targetFileName.substring(0, targetFileName.lastIndexOf(File.separator)));
        if (!file.exists())
            file.mkdir();

        try {
            wr = new BufferedWriter(new FileWriter(targetFileName));

            for (Category type : categories) {
                String fileName = appendFileName(targetFileName, "_" + type.toString());
                wr = new BufferedWriter(new FileWriter(fileName));
                for (Method am : methods) {
                    Category category = am.getCategoryClassified();
                    if (!testMethods.contains(am) || category == null)
                        continue;
                    if (category == type) {
                        if (counters.containsKey(category)) {
                            int counter = counters.get(category);
                            counters.put(category, ++counter);
                        } else
                            counters.put(am.getCategoryClassified(), 1);
                        wr.write(am.toString() + "\n");
                    }
                }
                wr.flush();
                wr.close();

                // if (counters.get(type) != null)
                // System.out.println(counters.get(type) + " " + type.toString() + " written to
                // file: " + fileName);
            }
        } finally {
            if (wr != null)
                wr.close();
        }
    }

    private String appendFileName(String targetFileName, String string) {
        int pos = targetFileName.lastIndexOf(".");
        return targetFileName.substring(0, pos) + string + targetFileName.substring(pos);
    }

    public void printResultsTXT(Set<Method> methods, String outputFile) throws IOException {

        Map<Category, Integer> counters = new HashMap<Category, Integer>();
        BufferedWriter wr = null;
        File file = new File(outputFile.substring(0, outputFile.lastIndexOf(File.separator)));
        if (!file.exists())
            file.mkdir();

        wr = new BufferedWriter(new FileWriter(outputFile));

        for (Method method : methods) {
            if (method.getCategoriesClassified().isEmpty())
                continue;
            StringBuilder sb = new StringBuilder();
            sb.append(method.getSignature());
            sb.append(" -> ");

            for (Category category : method.getCategoriesClassified()) {
                sb.append("_");
                sb.append(category.toString().toUpperCase());
                sb.append("_");

                if (counters.containsKey(category)) {
                    int counter = counters.get(category);
                    counters.put(category, ++counter);
                } else
                    counters.put(category, 1);
            }
            wr.write(sb.toString() + "\n");
        }

        wr.flush();
        wr.close();

        // System.out.println(methods.size() + " total methods written to file: " +
        // outputFile);
        for (Category type : counters.keySet())
            if (counters.get(type) != null)
                System.out.println(counters.get(type) + " " + type.toString() + " written to file: " + outputFile);
    }

    /* Outputs results as a JSON file using trained categories */
    @SuppressWarnings("unchecked")
    public void printResultsJSON(Set<Method> methods, String outputFile) throws IOException {

        printResultsJSON(methods, outputFile, true);
    }

    /* Outputs results to a JSON file using Classified categories*/
    @SuppressWarnings("unchecked")
    public void outputJSONFile(Set<Method> methods, String outputFile) throws IOException {

        printResultsJSON(methods, outputFile, false);
    }

    private void printResultsJSON(Set<Method> methods, String outputFile, boolean isClassified) throws IOException {

        Map<Category, Integer> counters = new HashMap<Category, Integer>();

        File file = new File(outputFile.substring(0, outputFile.lastIndexOf(File.separator)));
        if (!file.exists())
            file.mkdir();

        BufferedWriter wr = null;
        wr = new BufferedWriter(new FileWriter(outputFile));

        int count = 0;
        JSONArray arr = new JSONArray();

        HashMap<String, JSONObject> map = new HashMap<String, JSONObject>();

        for (Method method : methods) {

            JSONObject obj = new JSONObject();

            if ((isClassified ? method.getCategoriesClassified() : method.getCategoriesTrained()).isEmpty())
                continue;

            obj.put(Constants.NAME, method.getClassName() + "." + method.getMethodName());
            obj.put(Constants.RETURN_TYPE, method.getReturnType());
            obj.put(Constants.PARAMETERS, method.getParameters());

            switch (method.getSecLevel()) {
                case HIGH:
                    obj.put(Constants.SECURITY_LEVEL, Constants.AUTH_SAFE);
                    break;
                case LOW:
                    obj.put(Constants.SECURITY_LEVEL, Constants.AUTH_UNSAFE);
                    break;
                default:
                    obj.put(Constants.SECURITY_LEVEL, Constants.AUTH_NOCHANGE);
                    break;
            }

            obj.put(Constants.DISCOVERY, method.getDiscovery());
            obj.put(Constants.FRAMEWORK, method.getFramework());
            obj.put(Constants.LINK, method.getLink());

            // DataIn object
            JSONArray dataInParams = new JSONArray();
            dataInParams.addAll(method.getDataIn().getParameterIndeces());

            JSONObject dataIn = new JSONObject();
            dataIn.put(Constants.RETURN_TYPE, method.getDataIn().getReturnValue());
            dataIn.put(Constants.PARAMETERS, dataInParams);

            // DataOut object
            JSONArray dataOutParams = new JSONArray();
            dataOutParams.addAll(method.getDataOut().getParameterIndeces());

            JSONObject dataOut = new JSONObject();
            dataOut.put(Constants.RETURN_TYPE, method.getDataOut().getReturnValue());
            dataOut.put(Constants.PARAMETERS, dataOutParams);

            obj.put(Constants.DATA_IN, dataIn);
            obj.put(Constants.DATA_OUT, dataOut);

            JSONArray types = new JSONArray();
            JSONArray cwes = new JSONArray();

            for (Category category : (isClassified ? method.getCategoriesClassified() : method.getCategoriesTrained())) {

                if (category.isCwe())
                    cwes.add(category.toString());
                else {

                    switch (category.toString()) {

                        case Constants.AUTHENTICATION_SAFE:
                        case Constants.AUTHENTICATION_UNSAFE:
                        case Constants.AUTHENTICATION_NOCHANGE:
                            types.add(Constants.AUTHENTICATION);
                            break;
                        default:
                            types.add(category.toString());
                            break;
                    }
                }

                if (counters.containsKey(category)) {
                    int counter = counters.get(category);
                    counters.put(category, ++counter);
                } else
                    counters.put(category, 1);
            }
            obj.put(Constants.TYPE, types);
            obj.put(Constants.CWE, cwes);
            obj.put(Constants.COMMENT, method.getComment());

            map.put("method" + count, obj);
            arr.add(map.get("method" + count));
        }

        JSONObject parent = new JSONObject();
        parent.put(Constants.METHOD, arr);

        wr.write(parent.toJSONString());
        wr.flush();
        wr.close();
    }

    public void writeResultsQWELLegacy(Set<Method> methods, String outputFile) throws IOException {
        String path = outputFile.substring(0, outputFile.lastIndexOf(File.separator) + 1);
        BufferedWriter w = null;
        try {
            for (Category cat : Category.values()) {
                if (!cat.isCwe())
                    continue;

                String fullPath = path + cat.toString() + ".qwel";
                File statText = new File(fullPath);
                File file = new File(fullPath.substring(0, outputFile.lastIndexOf(File.separator)));
                if (!file.exists())
                    file.mkdir();

                FileOutputStream is = new FileOutputStream(statText);
                OutputStreamWriter osw = new OutputStreamWriter(is);
                w = new BufferedWriter(osw);

                for (Category catType : Category.values()) {
                    if (catType.isCwe())
                        continue;
                    StringBuilder sb = new StringBuilder();
                    sb.append("methods ");
                    sb.append(cat.toString());
                    sb.append("_");
                    sb.append(catType.toString());
                    sb.append(" [ \n");

                    for (Method method : methods) {
                        if (method.getCategoriesClassified().isEmpty())
                            continue;
                        for (Category category : method.getCategoriesClassified()) {
                            if (category.toString().equals(catType.toString())) {
                                String methodSig = method.getSignature().substring(1,
                                        method.getSignature().length() - 1);
                                String sig = methodSig.substring(methodSig.indexOf(' ') + 1, methodSig.lastIndexOf(' '))
                                        + " " + methodSig.substring(0, methodSig.indexOf(' ') - 1) + "."
                                        + methodSig.substring(methodSig.lastIndexOf(' ') + 1);
                                sb.append(sig);
                                sb.append("; \n");
                            }
                        }
                    }
                    sb.append("]");
                    w.write(sb.toString() + "\n");
                }

                w.flush();
                w.close();
            }
        } finally {
            if (w != null)
                w.close();
        }
    }

    public void writeResultsQWEL(Set<Method> methods, String outputFile) throws IOException {
        File path = new File(outputFile).getParentFile();
        for (Category cat : Category.values()) {
            if (cat.isCwe()) {
                File file = new File(path, cat.toString() + ".qwel");
                FileUtils.forceMkdirParent(file);
                PrintWriter pw = new PrintWriter(file);
                try {
                    for (Category catType : Category.values()) {
                        if (!catType.isCwe()) {
                            String methodSetName = catType.toString().toLowerCase().replaceAll("[^A-Za-z0-9]", "_");
                            pw.println("MethodSet " + methodSetName + " = new MethodSet();");
                            for (Method method : methods) {
                                if (method.getCategoriesClassified().isEmpty())
                                    continue;
                                for (Category category : method.getCategoriesClassified()) {
                                    if (category.toString().equals(catType.toString())) {
                                        String signature = method.getSignature();
                                        signature = signature.substring(1, signature.length() - 1);
                                        pw.println(
                                                "new Method(\"" + signature + "\", " + methodSetName + ");");
                                    }
                                }
                            }
                        }
                    }
                } finally {
                    pw.close();
                }
            }
        }
    }

    public void writeResultsSoot(Set<Method> methods, String outputFile) throws IOException {
        String path = outputFile.substring(0, outputFile.lastIndexOf(File.separator) + 1);
        BufferedWriter w = null;
        try {

            for (Category cat : Category.values()) {
                if (!cat.isCwe())
                    continue;

                String fullPath = path + cat.toString() + "_Soot.txt";
                File statText = new File(fullPath);
                File file = new File(fullPath.substring(0, outputFile.lastIndexOf(File.separator)));
                if (!file.exists())
                    file.mkdir();

                FileOutputStream is = new FileOutputStream(statText);
                OutputStreamWriter osw = new OutputStreamWriter(is);
                w = new BufferedWriter(osw);

                for (Category catType : Category.values()) {
                    if (catType.isCwe())
                        continue;
                    StringBuilder sb = new StringBuilder();
                    sb.append("***__");
                    sb.append(catType.toString());
                    sb.append("__*** \n\n");

                    for (Method method : methods) {
                        if (method.getCategoriesClassified().isEmpty())
                            continue;
                        for (Category category : method.getCategoriesClassified()) {
                            if (category.toString().equals(catType.toString())) {
                                sb.append(method.getSignature());
                                sb.append(" \n");
                            }
                        }
                    }
                    sb.append("\n");
                    w.write(sb.toString() + "\n");
                }

                w.flush();
                w.close();
            }
        } finally {
            if (w != null)
                w.close();
        }
    }

}

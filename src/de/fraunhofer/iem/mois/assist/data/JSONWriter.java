package de.fraunhofer.iem.mois.assist.data;

import de.fraunhofer.iem.mois.Writer;
import de.fraunhofer.iem.mois.data.Method;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class JSONWriter {

    /* Outputs results as a JSON file. */
    public void writeToJsonFile(ArrayList<MethodWrapper> methods, String outputPath) throws IOException {

        Set<Method> methodSet = new HashSet<Method>();

        for (MethodWrapper method : methods) {
            methodSet.add(method.getMethod());
        }

        Writer writer = new Writer();
        writer.printResultsJSON(methodSet, outputPath);

    }
}

/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.data;

import de.fraunhofer.iem.swan.Writer;
import de.fraunhofer.iem.swan.data.Method;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Output list of methods as a JSON file.
 */
public class JSONWriter {

    /**
     * Outputs results as a JSON file.
     * @param methods Arraylist of methods that should be exported
     * @param outputPath Path of configuration file.
     * @throws IOException Problems with writing or creating the file.
     */
    public void writeToJsonFile(ArrayList<MethodWrapper> methods, String outputPath) throws IOException {

        Set<Method> methodSet = new HashSet<Method>();

        for (MethodWrapper method : methods) {
            methodSet.add(method.getMethod());
        }

        Writer writer = new Writer();
        writer.outputJSONFile(methodSet, outputPath);
    }
}

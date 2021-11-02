package de.fraunhofer.iem.swan.training;

import de.fraunhofer.iem.swan.io.dataset.Parser;
import de.fraunhofer.iem.swan.io.dataset.Writer;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.io.doc.Javadoc;
import de.fraunhofer.iem.swan.io.doc.JavadocToXmlConverter;
import de.fraunhofer.iem.swan.io.doc.XmlDocletParser;
import de.fraunhofer.iem.swan.io.doc.ssldoclet.MethodBlockType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Adds the Javadoc method and class comments to the original training set and exports a new training file.
 *
 * @author Oshando Johnson on 21.07.20
 */
public class TrainingSetGenerator {

    public final static String ROOT = "/swan/core/";
    public final static String ORIGINAL_TRAINING_SET = ROOT + "src/main/resources/training-set-nodoc.json";
    public final static String ORIGINAL_MANUAL_TRAINING_SET = ROOT + "src/main/resources/training-set-nodoc-manual.json";
    public static String JAVADOC_OUTPUT = ROOT + "training-docs";

    public static void main(String[] args) throws IOException {

         /*
            PHASE 0: Load JAR files/directory and run doclet to export XML files
         */
        JavadocToXmlConverter javadocToXmlConverter = new JavadocToXmlConverter("Main.INPUT", "/swandoc/training-docs");
        javadocToXmlConverter.convert();

        System.out.println("DONE");
        /*
            PHASE 1: Parses the extracted XML files
         */
        XmlDocletParser javadocParser = new XmlDocletParser(JAVADOC_OUTPUT);
        ArrayList<Javadoc> javadocs = javadocParser.parse();

        /*
            PHASE 3: Use SWAN parser to load the original training file with methods
            that do not yet have class and method Javadoc comments.
         */
        Parser parser = new Parser();
        parser.parse(ORIGINAL_TRAINING_SET);

        Parser manualParser = new Parser();
        manualParser.parse(ORIGINAL_MANUAL_TRAINING_SET);

        Set<Method> methods = new HashSet<>(parser.methods());
        methods.addAll(manualParser.getMethods());

        HashMap<String, Method> trainingMethods = new HashMap<>();

        for (Method met : methods)
            trainingMethods.put(met.getJavaSignature(), met);

        /*
            PHASE 4: Add class and method doc comments to method objects.
         */
        for (Javadoc doc : javadocs) {

            for (MethodBlockType methodBlock : doc.getMethodBlocks().values()) {

                if (trainingMethods.containsKey(methodBlock.getSignature())) {

                    de.fraunhofer.iem.swan.data.Javadoc javadoc = new de.fraunhofer.iem.swan.data.Javadoc();

                    if (doc.getPackageBlock().getClassBlock().getClassCommentBlock() != null) {
                        String classComment = doc.getPackageBlock().getClassBlock().getClassCommentBlock().getClassComment().getValue();

                        javadoc.setClassComment(classComment);
                    }

                    if (methodBlock.getMethodCommentBlock() != null) {
                        String methodComment = methodBlock.getMethodCommentBlock().getMethodComment().getValue();

                        javadoc.setMethodComment(methodComment);
                    }

                    trainingMethods.get(methodBlock.getSignature()).setJavadoc(javadoc);
                }
            }
        }

        /*
            PHASE 4: Export updated training file that includes method and class comments
         */
        Writer writer = new Writer();
        writer.outputJSONFile(new HashSet<>(trainingMethods.values()), "Main.TRAINING_SET");
    }
}

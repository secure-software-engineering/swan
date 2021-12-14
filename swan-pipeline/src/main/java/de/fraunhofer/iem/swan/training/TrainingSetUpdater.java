package de.fraunhofer.iem.swan.training;

import de.fraunhofer.iem.swan.features.code.soot.SourceFileLoader;
import de.fraunhofer.iem.swan.io.dataset.SrmList;
import de.fraunhofer.iem.swan.util.Util;
import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * When given a list of methods and their classification,
 * Soot is used to extract the method information and export to a json file.
 * The new methods are merged with SWAN's existing training set.
 *
 * @author Oshando Johnson on 10.07.20
 */
public class TrainingSetUpdater {

    private static Set<String> uniqueMethod;
    private static HashSet<String> method;
    private static HashSet<String> classes;
    private static final String PROJECT_ROOT = "/swan/swan_core/src/main";
    private static final String ORIGINAL_TRAINING_FILE = PROJECT_ROOT + "/resources/input/TrainDataMethods/data-set.json";
    private static final String NEW_TRAINING_FILE = "/swandoc/src/main/resources/training-set-nodoc.json";
    private static final String MANUAL_TRAINING_FILE = "/swandoc/src/main/resources/training-set-nodoc-manual.json";
    private static final String TRAINING_JARS = PROJECT_ROOT + "/resources/input/TrainDataLibs";

    public static void main(String[] args) throws IOException {

        //Extract methods from find-sec-bugs plugin
        //getMethodsFromFiles();

        method = new HashSet<>();
        classes = new HashSet<>();
        uniqueMethod = new HashSet<>();

        //Load original training file methods
        SrmList srmListUtils = new SrmList();
        //parser.parse(ORIGINAL_TRAINING_FILE);

        for (Method m : srmListUtils.getMethods())
            uniqueMethod.add(m.getClassName() + "." + m.getName());

        //Add training methods to master list
        Set<Method> trainingMethods = new HashSet<>(srmListUtils.getMethods());

        //Load methods from text file: extracted from thecodemaster.com and find-sec-bugs plugin
        loadClassesAndMethods("/swan/swan_core/src/main/resources/swandoc-new-training-data.txt");

        trainingMethods.addAll(extractMethodData(classes, method));
    }

    public static void extractManualList() throws IOException {

        //Extract methods from fins-sec-bugs plugin
        //getMethodsFromFiles();

        method = new HashSet<>();
        classes = new HashSet<>();
        uniqueMethod = new HashSet<>();

        //Load original training file methods
        SrmList srmListUtils = new SrmList();
        //parser.parse(NEW_TRAINING_FILE);

        Set<Method> trainingMethods = new HashSet<>();

        for (Method m : srmListUtils.getMethods()){
            if(m.getJavaSignature().contains("org.owasp.esapi.reference.DefaultEncoder") &&
            m.getDiscovery().contentEquals("find-sec-bugs"))
                trainingMethods.add(m);
        }
    }

    /**
     * Loads the list of classes and creates a set to store them.
     *
     * @param file
     */
    public static void loadClassesAndMethods(String file) {
        File dir = new File(file);

        //Add list of methods
        try {
            method.addAll(FileUtils.readLines(dir, Charset.defaultCharset().toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Add all classes
        for (String m : method) {
            classes.add(m.substring(0, m.lastIndexOf(".")));
        }
    }


    /**
     * Extra method information using class names and fully qualified method name.
     * Outputs JSON file with the method information and their classifications.
     *
     * @throws IOException yea
     */
    public static Set<Method> extractMethodData(HashSet<String> classes, HashSet<String> methods) throws IOException {

        Set<String> testClasses = Util.getAllClassesFromDirectory(TRAINING_JARS);
        Map<String, String> classesJar = Util.getAllClassesFromDir(TRAINING_JARS);

        System.out.println("Classes: Total/" + classes.size());

        String classpath = Util.buildCP(TRAINING_JARS);

        HashMap<String, String> methodSet = new HashMap<>();

        for (String t : methods) {
            //System.out.println(t.substring(0, t.indexOf("|")) + ">>>" + t.substring(t.indexOf("|") + 1));
            methodSet.put(t.substring(0, t.indexOf("|")), t.substring(t.indexOf("|") + 1));
        }

        SourceFileLoader sourceFileLoader = new SourceFileLoader(classpath);
        sourceFileLoader.loadMethodsFromTestLib();

        Util.createSubclassAnnotations(sourceFileLoader.getMethods(), classpath);

        HashSet<Method> trainingMethods = new HashSet<>();


        for (Method m : sourceFileLoader.getMethods()) {

            String sig = m.getClassName() + "." + m.getName();

            //Do not add these methods to the list because their doc comments aren't helpful
            //They contain a see comment
            if (sig.contains("org.owasp.encoder.Encode.for") && m.getJavaSignature().endsWith("(java.io.Writer, java.lang.String)")) {
                System.out.println("SKIP: "+m.getJavaSignature());
                continue;
            }

            if (methodSet.containsKey(sig)) {

                String[] methodInfo = methodSet.get(sig).split("\\|");

                String[] cat = methodInfo[0].split(",");

                Set<Category> categories = new HashSet<>();

                for (String c : cat) {

                    switch (c) {
                        case "sink":
                            categories.add(Category.SINK);
                            break;
                        case "source":
                            categories.add(Category.SOURCE);
                            break;
                        case "sanitizer":
                            categories.add(Category.SANITIZER);
                            break;
                        case "cwe078":
                            categories.add(Category.CWE078);
                            break;
                        case "cwe079":
                            categories.add(Category.CWE079);
                            break;
                        case "cwe089":
                            categories.add(Category.CWE089);
                            break;
                    }
                }
                m.setSrm(categories);
                m.setSourceJar(classesJar.get(m.getClassName()));
                m.setDiscovery(methodInfo[1]);

                if (!uniqueMethod.contains(sig)) {
                    uniqueMethod.add(sig);
                    trainingMethods.add(m);
                    System.out.println("FOUND: " + m.getJavaSignature());
                }
            }
        }

        System.out.println("New Training Methods: " + trainingMethods.size());
        return trainingMethods;
    }

    public static void getMethodsFromFiles() {

        File dir = new File("/swan/swan_core/src/main/resources/find-sec-bugs/safe-encoders");

        HashSet<String> lines = new HashSet<>();

        for (File file : FileUtils.listFiles(dir, new String[]{"txt"}, true)) {

            //Add list of classes
            try {
                lines.addAll(FileUtils.readLines(file, Charset.defaultCharset().toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        int count = 0;
        for (String st : lines) {
            if (st.contains("org/owasp/esapi/Encoder.")) {
                //System.out.println(st);
                System.out.println(st.replace("/", ".").substring(0, st.indexOf("(")) + "|sanitizer|find-sec-bugs");
                count++;
            }
        }

        System.out.println("Count: " + count);
    }
}

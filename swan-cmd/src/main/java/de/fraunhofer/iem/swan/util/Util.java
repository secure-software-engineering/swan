package de.fraunhofer.iem.swan.util;

import de.fraunhofer.iem.swan.SwanPipeline;
import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import org.apache.commons.io.FileUtils;
import org.nd4j.common.io.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class Util {
    private static final Logger logger = LoggerFactory.getLogger(Util.class);

    /**
     * Checks whether there are semantic errors in the given set of methods and
     * tries to filter out duplicates by merging.
     *
     * @param methods The set of method definitions to check
     * @param init    The set of methods that should not be in the final set
     * @return The purged set of methods without duplicates
     */
    public static Set<Method> sanityCheck(Set<Method> methods, Set<Method> init) {

        Map<String, Method> signatureToMethod = new HashMap<>();
        for (Method m1 : methods) {
            String sig = m1.getSignature();
            Method m2 = signatureToMethod.get(sig);
            if (m2 == null)
                signatureToMethod.put(sig, m1);
            else if (!m1.equals(m2)) {
                //m2.addCategoriesTrained(m1.getSrm());
                signatureToMethod.put(sig, m2);
            }
        }

        Set<Method> ret = new HashSet<>();
        for (Method m : signatureToMethod.values()) {
            if (!init.contains(m))
                ret.add(m);
        }

        logger.info("Sanity check complete: merged " + (methods.size() - ret.size()) + " entries.");

        return ret;
    }

    public static void printStatistics(String message, Set<Method> methods) {
        Map<Category, Integer> counters = new HashMap<>();
        for (Method am : methods) {
            for (Category category : am.getSrm()) {
                if (counters.containsKey(category)) {
                    int counter = counters.get(category);
                    counters.put(category, ++counter);
                } else
                    counters.put(category, 1);
            }
        }

        logger.info(message + ": total methods={}, categories={}", methods.size(), counters);
    }

    public static Set<String> getAllClassesFromDirectory(String dir) throws IOException {

        Set<String> classes = new HashSet<>();

        for (File file : FileUtils.listFiles(new File(dir), new String[]{"class", "jar"}, true)) {
            if (file.getName().endsWith(".jar"))
                classes.addAll(getAllClassesFromJar(file.getAbsolutePath()));
            else if (file.getName().endsWith(".class")) {

                String packageName = file.getCanonicalPath().replace(dir, "").replace(".class", "");

                if (packageName.startsWith(File.separator))
                    packageName = packageName.replaceFirst(File.separator, "");

                classes.add(ClassUtils.convertResourcePathToClassName(packageName));
            }
        }
        return classes;
    }

    public static Map<String, String> getAllClassesFromDir(String dir) throws IOException {

        Map<String, String> classes = new HashMap<>();
        File folder = new File(dir);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File listOfFile : listOfFiles) {
                if (listOfFile.getName().endsWith(".jar"))


                    for (String str : getAllClassesFromJar(listOfFile.getAbsolutePath())) {
                        classes.put(str, listOfFile.getName());
                    }

                //	classes.put(listOfFiles[i].getName() , getAllClassesFromJar(listOfFiles[i].getAbsolutePath()));
            }
        }
        return classes;
    }

    private static Set<String> getAllClassesFromJar(String jarFile) throws IOException {
        Set<String> classes = new HashSet<>();
        ZipInputStream zip = new ZipInputStream(Files.newInputStream(Paths.get(jarFile)));
        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {

            if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                classes.add(getClassname(entry.getName()));
            }
        }
        zip.close();
        return classes;
    }

    public static String getClassname(String path) {

        String className = path.replace('/', '.');
        className = className.substring(0, className.length() - ".class".length());
        if (className.contains("$"))
            className = className.substring(0, className.indexOf("$") - 1);

        return className;
    }


    public static String buildCP(String[] dirs) {

        ArrayList<String> paths = new ArrayList<>();

        for (String dir : dirs) {

            File folder = new File(dir);

            if (dir.isEmpty() || !folder.exists() || !Files.isDirectory(folder.toPath()))
                continue;

            //If folder contains .class files, add path to classpath
            if (!FileUtils.listFiles(folder, new String[]{"class"}, true).isEmpty())
                paths.add(dir);
            else {
                for (File file : FileUtils.listFiles(folder, new String[]{"jar", "apk"}, true)) {
                    paths.add(file.getAbsolutePath());
                }
            }
        }
        return String.join(File.pathSeparator, paths);
    }

    /**
     * Creates artificial annotations for non-overridden methods in subclasses. If
     * class A implements some method foo() which is marked as e.g. a source and
     * class B extends A, but does not overwrite foo(), B.foo() must also be a
     * source.
     *
     * @param methods The list of method for which to create subclass annotations
     * @param cp      The classpath to use.
     */
    public static void createSubclassAnnotations(final Set<Method> methods, final String cp) {

        int copyCount = -1;
        int totalCopyCount = 0;
      /*  while (copyCount != 0) {
            copyCount = 0;
            for (Method am : methods) {
                // Check whether one of the parent classes is already annotated
                AbstractSootFeature asf = new AbstractSootFeature(cp) {

                    @Override
                    public Type appliesInternal(Method method) {
                        // This already searches up the class hierarchy until we
                        // find a match for the requested method.
                        SootMethod parentMethod = method.getSootMethod();
                        if (parentMethod == null)
                            return Type.NOT_SUPPORTED;

                        // If we have found the method in a base class and not in
                        // the current one, we can copy our current method's
                        // annotation to this base class. (copy-down)
                        boolean copied = false;
                        if (!parentMethod.getDeclaringClass().getName().equals(method.getClassName())) {
                            // Get the data object for the parent method
                            Method parentMethodData = findMethod(parentMethod);
                            if (parentMethodData == null)
                                return Type.NOT_SUPPORTED;

                            // If we have annotations for both methods, they must match
                            if (parentMethodData.isAnnotated() && method.isAnnotated())
                                if (!parentMethodData.getSrm().equals(method.getSrm()))
                                    throw new RuntimeException(
                                            "Categories mismatch for " + parentMethodData + " and " + method);

                            // If we only have annotations for the parent method, but not for
                            // the current one, we copy it down
                            if (parentMethodData.isAnnotated() && !method.isAnnotated()) {
                                method.setSrm(parentMethodData.getSrm());
                                copied = true;
                            }

                            // If we only have annotations for the current method, but not for
                            // the parent one, we can copy it up
                            if (!parentMethodData.isAnnotated() && method.isAnnotated()) {
                                parentMethodData.setSrm(method.getSrm());
                                copied = true;
                            }
                        }
                        return copied ? Type.TRUE : Type.FALSE;
                    }

                    private Method findMethod(SootMethod sm) {
                        Method smData = new Method(sm);
                        for (Method am : methods)
                            if (am.equals(smData))
                                return am;
                        return null;
                    }
                };
                if (asf.applies(am) == Type.TRUE) {
                    copyCount++;
                    totalCopyCount++;
                }
            }
        }*/
        logger.info("Created automatic annotations starting from " + totalCopyCount + " methods.");
    }

    public static Set<String> getFiles(String fileInDirectory) throws IOException {
        String directory = fileInDirectory.substring(0, fileInDirectory.lastIndexOf(File.separator));
        String fileName = fileInDirectory.substring(fileInDirectory.lastIndexOf(File.separator) + 1
        );
        Set<String> files = new HashSet<>();
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();
        assert listOfFiles != null;
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile() && listOfFile.getName().endsWith(".txt")
                    && !listOfFile.getName().endsWith("_" + Category.NONE + ".txt")
                    && !listOfFile.getName().equals(fileName)) {
                files.add(listOfFile.getCanonicalPath());
            }
        }
        return files;
    }

    /**
     * Loads the ARFF file to an instances object.
     *
     * @param filePath path to ARFF file
     * @return data as Instances object
     */
    public static Instances loadArffFile(String filePath) {
        Instances dataset = null;

        try {
            dataset = new Instances(new FileReader(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataset;
    }

    public static String getClassName(Instances instances) {

        String categoryName = "";

        Attribute classAttribute = instances.attribute("class");

        for (Enumeration<Object> category = classAttribute.enumerateValues(); category.hasMoreElements(); ) {

            String currentCategory = category.nextElement().toString();
            if (!currentCategory.equals("none")) {

                if (currentCategory.contains("auth")) {
                    categoryName = "authentications";
                    break;
                } else {
                    categoryName = currentCategory;
                }
            }
        }
        return categoryName;
    }

    /**
     * Export the instances to an ARFF file.
     *
     * @param instances WEKA instances to be exported
     */
    public static String exportInstancesToArff(Instances instances, String fileName) {
        ArffSaver saver = new ArffSaver();

        if (SwanPipeline.options.isExportArffData() && !SwanPipeline.options.getOutputDir().isEmpty()) {
            // Save arff data.
            saver.setInstances(instances);

            try {

                String relationName = instances.relationName();

                if (instances.relationName().contains(":"))
                    relationName = relationName.substring(0, instances.relationName().indexOf(":"));

                String arffFile = SwanPipeline.options.getOutputDir() + File.separator + "arff-data" + File.separator + fileName + ".arff";
                saver.setFile(new File(arffFile));
                saver.writeBatch();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return saver.retrieveFile().getAbsolutePath();
        }
        return null;
    }

    /**
     * Counts the number of methods that have doc comments for each class.
     *
     * @param methods set of methods
     * @return report showing distribution of categories
     */
    public static String countCategories(Set<Method> methods) {

        HashMap<String, Integer> results = new HashMap<>();

        //Add counters for classes and methods
        results.put("all-methods", 0);
        results.put("all-classes", 0);

        //Add counters for all categories
        for (Category category : Category.values())
            results.put(category.toString(), 0);

        for (Method met : methods) {

            results.put("all-methods", results.get("all-methods") + 1);

            for (Category cat : met.getAllCategories()) {
                results.put(cat.toString(), results.get(cat.toString()) + 1);
            }
        }
        return results.toString();
    }
}
package de.fraunhofer.iem.swan.io.doc;

import edu.stanford.nlp.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class SourceJarEvaluator {

    /**
     * Returns list of packages in a JAR file or directory.
     *
     * @param jarFile JAR file or directory
     * @return array list of package names
     */
    public static ArrayList<String> getPackages(File jarFile) {

        Set<String> packages = new HashSet<>();

        if (jarFile.getName().endsWith(".jar")) {

            try {
                JarInputStream jarInputStream = new JarInputStream(new FileInputStream(jarFile.getAbsolutePath()));

                JarEntry jarEntry;
                while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {

                    if (jarEntry.getName().contains("META-INF"))
                        continue;

                    if (jarEntry.isDirectory() || jarEntry.getName().endsWith(".java"))
                        packages.add(jarEntry.getName().replace("/", ".").replaceAll("(\\.|\\s)+$", ""));
                }

                if (packages.size() > 0) {
                    return new ArrayList<>(packages);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (jarFile.isDirectory()) {

            for (File sub : Objects.requireNonNull(jarFile.listFiles())) {
                //FileUtils.listFilesAndDirs(file, new NotFileFilter(TrueFileFilter.INSTANCE), DirectoryFileFilter.DIRECTORY)) {

                String subDir = sub.getPath().replace(jarFile.getPath(), "");

                if (subDir.length() > 1) {
                    packages.add(subDir
                            .replace("/", ".")
                            .substring(1));
                }
            }
            return new ArrayList<>(packages);
        }
        return new ArrayList<>();
    }

    public static String getRootPackages(File jarFile) {

        return StringUtils.join(getRootPackages(getPackages(jarFile)), ":");

    }

    /**
     * Returns root packages from given list of packages.
     *
     * @param packages
     * @return
     */
    public static ArrayList<String> getRootPackages(ArrayList<String> packages) {

        ArrayList<String> rootPackages = new ArrayList<>();

        Collections.sort(packages);
        String currentRoot = packages.get(0);
        rootPackages.add(currentRoot);

        for (String pack : packages) {

          /*  if (!pack.contains(currentRoot)) {
                currentRoot = pack;
            }*/

            if (pack.length() >= currentRoot.length() && !currentRoot.equals(pack.substring(0, currentRoot.length()))) {
                rootPackages.add(pack);
                currentRoot = pack;
            }
        }
        return rootPackages;
    }
}
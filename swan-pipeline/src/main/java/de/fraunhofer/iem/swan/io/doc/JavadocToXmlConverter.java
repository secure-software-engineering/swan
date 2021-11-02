package de.fraunhofer.iem.swan.io.doc;

import edu.stanford.nlp.util.StringUtils;
import org.apache.commons.io.FilenameUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * @author Oshando Johnson on 06.07.20
 */
public class JavadocToXmlConverter {

    private String inputPath;
    private String outputPath;

    public JavadocToXmlConverter(String inputPath, String outputPath) {
        this.inputPath = inputPath;
        this.outputPath = outputPath;
    }

    public void convert() throws IOException {

        HashMap<String, String> dependencies = new HashMap<>();
        dependencies.put("google-auth-library-oauth2-http-0.4.0-sources.jar", ":google-auth-library-credentials-0.4.0-sources.jar:google-http-client-1.19.0-sources.jar");
        dependencies.put("oauth2-essentials-0.1-sources.jar", ":rt");
        dependencies.put("jldap-2009-10-07-sources.jar", ":openspml-0.5-sources.jar:commons-httpclient-3.1:commons-logging-1.1-sources.jar:soap-2.3.1:mail-1.4-sources.jar:commons-codec-1.10-sources.jar");
        dependencies.put("spring-web-4.3.9.RELEASE-sources.jar", ":javax.servlet-api-3.0.1-sources.jar");
        dependencies.put("spring-websocket-4.3.9.RELEASE-sources.jar", ":spring-beans-4.3.9.RELEASE-sources.jar:spring-core-4.3.9.RELEASE-sources.jar:commons-logging-1.1-sources.jar");
        dependencies.put("spring-test-4.3.9.RELEASE-sources.jar", ":javax.servlet-api-3.0.1-sources.jar:javax.servlet.jsp-api-2.2.1-sources.jar:portlet-api-2.0-sources.jar");
        dependencies.put("spring-orm-4.3.9.RELEASE-sources.jar", ":hibernate-core-5.2.10.Final-sources.jar");
        dependencies.put("spring-context-4.3.9.RELEASE-sources.jar", ":spring-beans-4.3.9.RELEASE-sources.jar:spring-core-4.3.9.RELEASE-sources.jar:spring-aop-4.3.9.RELEASE-sources.jar:spring-expression-4.3.9.RELEASE-sources.jar:commons-logging-1.1-sources.jar");
        dependencies.put("spring-security-core-4.0.1.RELEASE-sources.jar", ":/Users/oshando/.jeka/cache/repo/org.springframework/spring-aop/sources/spring-aop-4.1.6.RELEASE-sources.jar:/Users/oshando/.jeka/cache/repo/org.springframework/spring-core/sources/spring-core-4.1.6.RELEASE-sources.jar:/Users/oshando/.jeka/cache/repo/org.springframework/spring-context/sources/spring-context-4.1.6.RELEASE-sources.jar:/Users/oshando/.jeka/cache/repo/aopalliance/aopalliance/sources/aopalliance-1.0-sources.jar:/Users/oshando/.jeka/cache/repo/org.springframework/spring-beans/sources/spring-beans-4.1.6.RELEASE-sources.jar:/Users/oshando/.jeka/cache/repo/org.springframework/spring-expression/sources/spring-expression-4.1.6.RELEASE-sources.jar:/Users/oshando/.jeka/cache/repo/commons-logging/commons-logging/sources/commons-logging-1.2-sources.jar");
        dependencies.put("httpclient-4.1-sources.jar", ":httpcore-4.1-sources.jar");
        dependencies.put("xmlrpc-server-3.1-sources.jar", ":xmlrpc-common-3.1-sources.jar");
        dependencies.put("esapi-2.0_rc10-sources.jar", ":commons-fileupload-1.2-sources.jar:commons-configuration-1.5-sources.jar");
        dependencies.put("wicket-core-7.8.0-sources.jar", ":/Users/oshando/.jeka/cache/repo/commons-fileupload/commons-fileupload/sources/commons-fileupload-1.3.2-sources.jar:/Users/oshando/.jeka/cache/repo/org.apache.wicket/wicket-util/sources/wicket-util-7.8.0-sources.jar:/Users/oshando/.jeka/cache/repo/org.slf4j/slf4j-api/sources/slf4j-api-1.7.21-sources.jar:/Users/oshando/.jeka/cache/repo/org.apache.wicket/wicket-request/sources/wicket-request-7.8.0-sources.jar:/Users/oshando/.jeka/cache/repo/org.apache.commons/commons-collections4/sources/commons-collections4-4.1-sources.jar:/Users/oshando/.jeka/cache/repo/commons-io/commons-io/sources/commons-io-2.5-sources.jar");
        dependencies.put("hibernate-core-5.2.10.Final-sources.jar", ":/Users/oshando/.jeka/cache/repo/org.javassist/javassist/sources/javassist-3.20.0-GA-sources.jar:/Users/oshando/.jeka/cache/repo/com.fasterxml/classmate/sources/classmate-1.3.0-sources.jar:/Users/oshando/.jeka/cache/repo/org.jboss.spec.javax.transaction/jboss-transaction-api_1.2_spec/sources/jboss-transaction-api_1.2_spec-1.0.1.Final-sources.jar:/Users/oshando/.jeka/cache/repo/org.jboss.logging/jboss-logging/sources/jboss-logging-3.3.0.Final-sources.jar:/Users/oshando/.jeka/cache/repo/org.jboss/jandex/sources/jandex-2.0.3.Final-sources.jar:/Users/oshando/.jeka/cache/repo/org.hibernate.javax.persistence/hibernate-jpa-2.1-api/sources/hibernate-jpa-2.1-api-1.0.0.Final-sources.jar:/Users/oshando/.jeka/cache/repo/org.hibernate.common/hibernate-commons-annotations/sources/hibernate-commons-annotations-5.0.1.Final-sources.jar:/Users/oshando/.jeka/cache/repo/dom4j/dom4j/sources/dom4j-1.6.1-sources.jar");

        File sourceFiles = new File(inputPath);

        if (sourceFiles.isDirectory()) {

            for (File file : Objects.requireNonNull(sourceFiles.listFiles())) {

                if (file.getName().endsWith("manual-1.0"))
                    if (file.getName().endsWith(".jar")) {

                        JarInputStream jarFiles = new JarInputStream(new FileInputStream(file.getAbsolutePath()));

                        Set<String> packages = new HashSet<>();
                        JarEntry jarEntry = null;

                        while ((jarEntry = jarFiles.getNextJarEntry()) != null) {

                            if (jarEntry.isDirectory()) {
                                packages.add(jarEntry.getName().replace(File.separator, ".").substring(0, jarEntry.getName().length() - 1));

                            } else if (jarEntry.getName().endsWith(".java")) {

                                String cpath = jarEntry.getName().replace(File.separator, ".").substring(0, jarEntry.getName().length() - 5);
                                packages.add(cpath.substring(0, cpath.lastIndexOf(".")));
                            }
                        }

                        String rootPackages = StringUtils.join(getRootPackages(new ArrayList<>(packages)), ":");

                        DocletExecutor xmlDoclet = new DocletExecutor();
                        xmlDoclet.setDoclet(DocletExecutor.SSL_DOCLET);

                        String sourcePath = file.getAbsolutePath();

                        if (dependencies.containsKey(file.getName())) {
                            sourcePath += dependencies.get(file.getName());//.replace(":", ":" + inputPath + File.separator);
                        }

                        System.out.println("runnign for"+ sourcePath);
                        xmlDoclet.runDoclet(sourcePath, rootPackages, getOutputPath(outputPath, file.getName()));
                    } else if (file.getName().endsWith(".java") || file.isDirectory()) {

                        Set<String> packages = new HashSet<>();

                        for (File sub : Objects.requireNonNull(file.listFiles())) {
                            // for (File sub : FileUtils.listFilesAndDirs(file, new NotFileFilter(TrueFileFilter.INSTANCE), DirectoryFileFilter.DIRECTORY)) {

                            String subDir = sub.getPath().replace(file.getPath(), "");

                            if (subDir.length() > 1) {
                                packages.add(subDir
                                        .replace("/", ".")
                                        .substring(1));
                            }
                        }

                        String rootPackages = StringUtils.join(packages, ":");

                        DocletExecutor xmlDoclet = new DocletExecutor();
                        xmlDoclet.setDoclet(DocletExecutor.SSL_DOCLET);
                        xmlDoclet.runDoclet(file.getAbsolutePath(), rootPackages, getOutputPath(outputPath, file.getName()));
                    }
            }
        }
    }

    public ArrayList<String> getRootPackages(ArrayList<String> packages) {

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

    public String getSourcePath(String path, File[] files) {

        if (files.length > 1) {
            return path + File.pathSeparator + StringUtils.join(files, ":");
        } else
            return path;
    }

    public String getOutputPath(String root, String path) {

        String outputPath = root + File.separator + path;

        if (path.endsWith(".jar"))
            outputPath = root + File.separator + FilenameUtils.removeExtension(path);

        File dir = new File(outputPath);

        if (dir.mkdirs())
            return dir.getAbsolutePath();
        else
            return outputPath;
    }
}
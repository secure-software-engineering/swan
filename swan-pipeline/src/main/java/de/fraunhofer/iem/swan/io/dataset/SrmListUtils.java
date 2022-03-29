package de.fraunhofer.iem.swan.io.dataset;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.io.doc.Javadoc;
import de.fraunhofer.iem.swan.io.doc.ssldoclet.MethodBlockType;
import edu.stanford.nlp.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Loads methods from file or jars.
 *
 * @author Lisa Nguyen Quang Do
 */

public class SrmListUtils {

    private static final Logger logger = LoggerFactory.getLogger(SrmListUtils.class);

    /**
     * Imports SRMs from JSON file.
     *
     * @param file JSON File that stores security-relevant methods
     * @return object containing all security-relevant methods
     */
    public static SrmList importFile(String file) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        SrmList srmList = objectMapper.readValue(new File(file), SrmList.class);
        logger.info("Collected {} methods from the training set.", srmList.getMethods().size());

        return srmList;
    }

    /**
     * Exports SRM list to JSON file.
     *
     * @param srmList list of SRMa
     * @param file    path of JSON file
     */
    public static void exportFile(SrmList srmList, String file) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File(file), srmList);
        logger.info("{} SRMs exported to {}", srmList.getMethods().size(), file);
    }

    public static void removeUndocumentedMethods(SrmList list) {
        Set<Method> temp = new HashSet<>(list.getMethods());

        for (Method method : temp) {
            List<String> words = StringUtils.split(method.getJavadoc().getMethodComment(), " ");

            if (method.getJavadoc().getMethodComment().length() == 0 || words.size() <= 1)
                list.getMethods().remove(method);
        }
    }

    /**
     * Adds doc comments to method set.
     * @param methods methods to be updated
     * @param javadocs list of Javadoc objects
     * @return updated method set
     */
    public static Set<Method> addDocComments(Set<Method> methods, ArrayList<Javadoc> javadocs) {

        for (Javadoc doc : javadocs) {

            for (MethodBlockType methodBlock : doc.getMethodBlocks().values()) {

                Method method = methods.stream().filter(m -> methodBlock.getSignature()
                        .equals(m.getJavaSignature())).findAny().orElse(null);

                if (method != null) {

                    de.fraunhofer.iem.swan.data.Javadoc javadoc = new de.fraunhofer.iem.swan.data.Javadoc();

                    if (doc.getPackageBlock().getClassBlock().getClassCommentBlock() != null) {
                        String classComment = doc.getPackageBlock().getClassBlock().getClassCommentBlock().getClassComment().getValue();

                        javadoc.setClassComment(classComment);
                    }

                    if (methodBlock.getMethodCommentBlock() != null) {
                        String methodComment = methodBlock.getMethodCommentBlock().getMethodComment().getValue();

                        javadoc.setMethodComment(methodComment);
                    }
                    method.setJavadoc(javadoc);
                }
            }
        }
        return methods;
    }
}
package de.fraunhofer.iem.swan.io.dataset;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iem.srm.dataset.SrmDataset;
import de.fraunhofer.iem.srm.dataset.Documentation;
import de.fraunhofer.iem.srm.dataset.Method;
import de.fraunhofer.iem.swan.io.doc.Javadoc;
import de.fraunhofer.iem.swan.io.doc.ssldoclet.MethodBlockType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

/**
 * Loads methods from file or jars.
 *
 * @author Lisa Nguyen Quang Do
 */

public class SrmDatasetUtils {

    private static final Logger logger = LoggerFactory.getLogger(SrmDatasetUtils.class);

    /**
     * Imports SRMs from JSON file.
     *
     * @param file JSON File that stores security-relevant methods
     * @return object containing all security-relevant methods
     */
    public static SrmDataset importFile(String file) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(new File(file), SrmDataset.class);
    }

    /**
     * Exports SRM list to JSON file.
     *
     * @param SrmDataset list of SRMa
     * @param file    path of JSON file
     */
    public static void exportFile(SrmDataset SrmDataset, String file) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File(file), SrmDataset);
        logger.info("Exporting {} SRMs to {}", SrmDataset.getMethods().size(), file);
    }

    /**
     * Adds doc comments to method set.
     *
     * @param methods  methods to be updated
     * @param javadocs list of Javadoc objects
     * @return updated method set
     */
    public static Set<Method> addDocComments(Set<Method> methods, ArrayList<Javadoc> javadocs) {
        //TODO Check if returned set is used
        for (Javadoc doc : javadocs) {

            for (MethodBlockType methodBlock : doc.getMethodBlocks().values()) {

                Method method = methods.stream().filter(m -> methodBlock.getSignature()
                        .equals(m.getJavaSignature())).findAny().orElse(null);

                if (method != null) {

                    Documentation documentation = new Documentation();

                    if (doc.getPackageBlock().getClassBlock().getClassCommentBlock() != null) {
                        String classComment = doc.getPackageBlock().getClassBlock().getClassCommentBlock().getClassComment().getValue();

                        documentation.setClassComment(classComment);
                    }

                    if (methodBlock.getMethodCommentBlock() != null) {
                        String methodComment = methodBlock.getMethodCommentBlock().getMethodComment().getValue();

                        documentation.setMethodComment(methodComment);
                    }
                    method.setDocumentation(documentation);
                }
            }
        }
        return methods;
    }
}
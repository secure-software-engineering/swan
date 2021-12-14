package de.fraunhofer.iem.swan.io.dataset;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.type.AbstractSootFeature;
import de.fraunhofer.iem.swan.features.code.type.IFeature.Type;
import de.fraunhofer.iem.swan.util.Util;
import edu.stanford.nlp.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.SootMethod;

import java.io.File;
import java.io.IOException;
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
     * @param file JSON File that stores security-relevant methods
     * @return object containing all security-relevant methods
     */
    public static SrmList importFile(String file, String sourceFiles) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        SrmList srmList = objectMapper.readValue(new File(file), SrmList.class);
        logger.info("Collected {} methods from the training set.", srmList.getMethods().size());

        return cleanupList(srmList, sourceFiles);
    }

    public static SrmList cleanupList(SrmList srmList, String sourceFiles) throws IOException {

        srmList.setClasspath(Util.buildCP(sourceFiles));

        SrmList list = prefilterInterfaces(srmList, srmList.getClasspath());
        Util.createSubclassAnnotations(srmList.getMethods(), srmList.getClasspath());
        Util.sanityCheck(srmList.getMethods(), new HashSet<>());
        //TODO When should this filter be applied?
        removeUndocumentedMethods(list);

        return list;
    }

    /**
     * Removes all interfaces from the given set of methods and returns the purged
     * set.
     */
    private static SrmList prefilterInterfaces(SrmList srmList, String classpath) {
        Set<Method> purgedMethods = new HashSet<Method>(srmList.getMethods().size());
        for (Method am : srmList.getMethods()) {
            AbstractSootFeature asf = new AbstractSootFeature(classpath) {
                @Override
                public Type appliesInternal(Method method) {

                    SootMethod sm = getSootMethod(method);
                    if (sm == null)
                        return Type.NOT_SUPPORTED;

                    if (sm.isAbstract())
                        return Type.FALSE;
                    else
                        return Type.TRUE;
                }
            };

            Type t = asf.applies(am);
            if (t == Type.TRUE) {
                purgedMethods.add(am);
            } else
                logger.info("Method purged from list {}", am.getSignature());
        }
        logger.info("{} methods purged down to {}", srmList.getMethods().size(), purgedMethods.size());
        srmList.setMethods(purgedMethods);
        return srmList;
    }

    /**
     * Exports SRM list to JSON file.
     * @param srmList list of SRMa
     * @param file path of JSON file
     */
    public static void exportFile(SrmList srmList, String file) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File(file), srmList);
        logger.info("Collected {} methods from the training set.", srmList.getMethods().size());
    }

    public static void removeUndocumentedMethods(SrmList list) {
        Set<Method> temp = new HashSet<>(list.getMethods());

        for (Method method : temp) {
            List<String> words = StringUtils.split(method.getJavadoc().getMethodComment(), " ");

            if (method.getJavadoc().getMethodComment().length() == 0 || words.size() <= 1)
                list.getMethods().remove(method);
        }
    }
}
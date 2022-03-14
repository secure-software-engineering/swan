package de.fraunhofer.iem.swan.io.dataset;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iem.swan.data.Method;
import edu.stanford.nlp.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public static SrmList importFile(String file) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        SrmList srmList = objectMapper.readValue(new File(file), SrmList.class);
        logger.info("Collected {} methods from the training set.", srmList.getMethods().size());

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
}
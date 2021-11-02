package de.fraunhofer.iem.swan.io.doc;

import de.fraunhofer.iem.swan.io.doc.ssldoclet.MethodBlockType;
import de.fraunhofer.iem.swan.io.doc.ssldoclet.SsldocletType;
import org.apache.commons.io.FileUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Oshando Johnson on 09.06.20
 */
public class XmlDocletParser {

    private String sourcePath;
    private JAXBContext jaxbContext;
    private Unmarshaller unmarshaller = null;
    private ArrayList<Javadoc> javadocs;


    public XmlDocletParser(String sourcePath) {
        this.sourcePath = sourcePath;
        javadocs = new ArrayList<>();

        try {
            jaxbContext = JAXBContext.newInstance(SsldocletType.class);
            unmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public ArrayList<Javadoc> parse() {

        File filePath = new File(sourcePath);

        if (filePath.isDirectory()) {

            for (File file : FileUtils.listFiles(filePath, new String[]{"xml"}, true))
                parseFile(file.getAbsolutePath());

        } else {
            parseFile(filePath.getAbsolutePath());
        }
        return javadocs;
    }


    public void parseFile(String xmlFile) {

        try {
            InputStream inputStream = new FileInputStream(xmlFile);
            SsldocletType xmlDoc = (SsldocletType) unmarshaller.unmarshal(inputStream);

            if (xmlDoc.getPackageBlock().getClassBlock() != null && xmlDoc.getPackageBlock().getClassBlock().getMethods() != null) {

                HashMap<String, MethodBlockType> methods = new HashMap<>();


                    for (MethodBlockType methodBlock : xmlDoc.getPackageBlock().getClassBlock().getMethods().getMethodBlock()) {

                        String methodSignature = getSignature(xmlDoc.getPackageBlock().getQualifiedName(), methodBlock);

                        methodBlock.setSignature(methodSignature);
                        methods.put(methodSignature, methodBlock);
                    }
                    javadocs.add(new Javadoc(xmlDoc.getPackageBlock(), methods, xmlFile));
            }

        } catch (FileNotFoundException | JAXBException e) {
            e.printStackTrace();
        }
    }


    public String getSignature(String packageName, MethodBlockType method) {

        String returnType = "void";

        if (method.getReturnBlock() != null)
            returnType = method.getReturnBlock().getReturn();

        return returnType + " " +
                packageName + "." + method.getMethod().getValue()
                + method.getMethod().getSignature();
    }
}

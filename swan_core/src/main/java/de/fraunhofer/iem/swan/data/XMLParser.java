package de.fraunhofer.iem.swan.data;

import javafx.util.Pair;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * @author Oshando Johnson on 2019-09-27
 */
public class XMLParser {

    //Elements
    private final String E_TAINT_FLOW = "TaintFlow";
    private final String E_PARTIAL_FLOW = "partialTaintFlows";
    private final String E_METHOD_SET = "MethodSet";
    private final String E_METHOD = "Method";
    private final String E_INPUT_DECLARATION = "inputDeclaration";
    private final String E_OUTPUT_DECLARATION = "outputDeclaration";
    private final String E_INPUT_VALUE = "inputs";
    private final String E_OUTPUT_VALUE = "outputs";

    //XML attributes
    private final String A_VERSION = "version";
    private final String A_XMI = "xmi";
    private final String A_XSI = "xsi";
    private final String A_QUERY = "query";
    private final String A_REPORT_MESSAGE = "reportMessage";
    private final String A_FROM = "from";
    private final String A_TO = "to";
    private final String A_NAME = "name";
    private final String A_METHODS = "methods";
    private final String A_SOURCE = "sources";
    private final String A_SINKS = "sinks";
    private final String A_SIGNATURE = "signature";
    private final String A_METHOD_SET = "methodSet";
    private final String A_TYPE = "type";
    private final String A_RETURN_VALUE = "query:ReturnValue";
    private final String A_PARAMETER = "query:Parameter";

    public void generateXML(String outputFile, LinkedHashMap<String, Pair<String, String>> methods, String sourcesList, String sinksList) throws TransformerException, ParserConfigurationException {

        Namespace xmi = Namespace.getNamespace(A_XMI, "http://www.omg.org/XMI");
        Namespace xsi = Namespace.getNamespace(A_XSI, XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
        Namespace query = Namespace.getNamespace(A_QUERY, "http://iem.fraunhofer.de/secucheck/query");

        //Root element
        Element root = new Element("XMI");
        root.setNamespace(xmi);
        root.setAttribute(A_VERSION,"2.0", xmi);
        root.addNamespaceDeclaration(xmi);
        root.addNamespaceDeclaration(xsi);
        root.addNamespaceDeclaration(query);

        Document document = new Document(root);

        //Taint flow element
        Element taintFlow = new Element(E_TAINT_FLOW);
        taintFlow.setNamespace(query);
        taintFlow.setAttribute(A_REPORT_MESSAGE, "Invalid Information Flow");

        //Partial taint flow element
        Element partialTaintFlow = new Element(E_PARTIAL_FLOW);
        partialTaintFlow.setAttribute(A_FROM, "/1");
        partialTaintFlow.setAttribute(A_TO, "/2");

        taintFlow.addContent(partialTaintFlow);
        document.getRootElement().addContent(taintFlow);

        //Method sets
        Element sourceMethodSet = new Element(E_METHOD_SET);
        sourceMethodSet.setNamespace(query);
        sourceMethodSet.setAttribute(A_NAME, A_SOURCE);
        sourceMethodSet.setAttribute(A_METHODS, sourcesList.trim());
        document.getRootElement().addContent(sourceMethodSet);

        Element sinkMethodSet = new Element(E_METHOD_SET);
        sinkMethodSet.setNamespace(query);
        sinkMethodSet.setAttribute(A_NAME, A_SINKS);
        sinkMethodSet.setAttribute(A_METHODS, sinksList.trim());
        document.getRootElement().addContent(sinkMethodSet);

        for (String methodKey : methods.keySet()) {

            Element method = new Element(E_METHOD);
            method.setNamespace(query);
            method.setAttribute(A_NAME, methodKey);
            method.setAttribute(A_SIGNATURE, methods.get(methodKey).getValue());

            String inputOutputType;
            String declarationValue;
            String returnValue;
            String methodSet;

            if (methods.get(methodKey).getKey().equals(Constants.SOURCE)) {

                inputOutputType = E_OUTPUT_DECLARATION;
                declarationValue = E_OUTPUT_VALUE;
                returnValue = A_RETURN_VALUE;
                methodSet = "/1";
            } else {

                inputOutputType = E_INPUT_DECLARATION;
                declarationValue = E_INPUT_VALUE;
                returnValue = A_PARAMETER;
                methodSet = "/2";
            }

            method.setAttribute(A_METHOD_SET, methodSet);

            //Output or input declaration
            Element declaration = new Element(inputOutputType);

            Element declarationType = new Element(declarationValue);
            declarationType.setAttribute(A_TYPE, returnValue, xsi);
            declaration.addContent(declarationType);

            method.addContent(declaration);
            document.getRootElement().addContent(method);
        }

        XMLOutputter xmlOutput = new XMLOutputter();

        xmlOutput.setFormat(Format.getPrettyFormat());
        try {
            xmlOutput.output(document, new FileWriter(outputFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/*
    SSLDoclet --  http://www.semanticsoftware.info/javadoclet

    This file is part of the SSLDoclet architecture.

    Copyright (C) 2009 Semantic Software Lab, http://www.semanticsoftware.info
        René Witte
        Ninus Khamis

    The SSLDoclet architecture is free software: you can
    redistribute and/or modify it under the terms of the
    GNU General Public License as published by the Free Software Foundation,
    either version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package info.semanticsoftware.doclet.xml;

import org.jsoup.Jsoup;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

/**
 * This class is in charge of serialzing all of the object sent by
 * the SSLDoclet
 *
 * @author Ninus
 * @version 1.2
 */
public class XMLSerializer {
    Document doc = null;
    Element root = null;
    Transformer trans = null;

    /**
     * The method in charge of creating XML using the DocumentBuilderFactory.
     */
    public void createDocument() {
        try {
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            doc = docBuilder.newDocument();
        } catch (ParserConfigurationException pcEx) {
            System.out.println(pcEx);
        }
    }

    /**
     * The method that creates the root nodes of the XML file
     */
    public Element createRoot(String value) {
        root = doc.createElement(value);
        doc.appendChild(root);
        return root;
    }

    /**
     * The method that creates the child nodes of the XML file
     */
    public Element createElement(String value) {
        Element e;
        try {
            e = doc.createElement(value);
            root.appendChild(e);
        } catch (DOMException exp) {

            exp.printStackTrace();
            e = doc.createElement("ErrorNode");
        }

        return e;
    }

    /**
     * The method that creates the text for the child nodes
     */
    public void createText(Element e, String value) {
        Text text = doc.createTextNode(cleanText(value));
        e.appendChild(text);
    }

    public String cleanText(String text) {

        return Jsoup.parse(text).text()
                .replaceAll("\\u0000", "null")
                .trim();
    }

    /**
     * The method that creates the attributes for the child nodes
     */
    public void createAttribute(Element n, String attribute, String value) {
        n.setAttribute(attribute, value);
    }


    /**
     * The method that formats the XML nodes
     */
    public void formatDocument() {
        try {
            TransformerFactory transfac = TransformerFactory.newInstance();
            trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
        } catch (TransformerConfigurationException tcEx) {
            System.out.println(tcEx);
        }

    }

    /**
     * The method that saves the XML files
     */
    public void save(String dir, String fileName, String packageName) {
        try {
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(doc);
            trans.transform(source, result);

            try {
                FileWriter out = new FileWriter(dir + "/" + fileName + " - " + packageName + ".xml");
                out.write(sw.toString());
                out.close();
            } catch (IOException ioEx) {
                System.out.println("Could not create '" + dir + fileName + "'");
                System.out.println(ioEx);
            }
        } catch (TransformerException tEx) {
            System.out.println(tEx);
        }
    }
}

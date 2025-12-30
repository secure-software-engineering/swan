/*
    SSLDoclet --  http://www.semanticsoftware.info/javadoclet

    This file is part of the SSLDoclet architecture.

    Copyright (C) 2009 Semantic Software Lab, http://www.semanticsoftware.info
        Rene Witte
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
package de.fraunhofer.iem.doclet.xml;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.util.DocTrees;
import com.sun.source.util.TreePath;
import de.fraunhofer.iem.doclet.xml.serializer.XMLSerializer;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import org.w3c.dom.Element;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.ElementScanner9;
import javax.tools.JavaFileObject;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This class is in charge of iterating through all of the Javadoc objects and
 * serializing them into an xml file using the XMLSerialiser
 *
 * @author Ninus
 * @version 1.2
 */
public class XMLDoclet implements Doclet {

    private static final boolean OK = true;
    private String sourcePath;
    private String outputDir;
    private static final String programversion = "2.0";
    private static XMLSerializer xmlSerializer;
    static DocTrees docTrees;
    static DocletEnvironment environment;

    final private Logger log = Logger.getLogger(XMLDoclet.class.getName());

    abstract class Option implements Doclet.Option {
        private final String name;
        private final boolean hasArg;
        private final String description;
        private final String parameters;

        Option(String name, boolean hasArg,
               String description, String parameters) {
            this.name = name;
            this.hasArg = hasArg;
            this.description = description;
            this.parameters = parameters;
        }

        @Override
        public int getArgumentCount() {
            return hasArg ? 1 : 0;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public Kind getKind() {
            return Kind.STANDARD;
        }

        @Override
        public List<String> getNames() {
            return List.of(name);
        }

        @Override
        public String getParameters() {
            return hasArg ? parameters : "";
        }
    }

    private final Set<Option> options = Set.of(

            // An option that takes a single string-valued argument.
            new Option("--sourcepath", true, "The directory containing the source code that needs to be processed", "<string>") {
                @Override
                public boolean process(String option,
                                       List<String> arguments) {
                    sourcePath = arguments.get(0);
                    return OK;
                }
            },

            // An option that takes a single integer-valued argument.
            new Option("--destdir", true, "The directory where the doclet will place the generated XML documents.", "<int>") {
                @Override
                public boolean process(String option,
                                       List<String> arguments) {
                    outputDir = arguments.get(0);
                    return OK;
                }
            }
    );

    @Override
    public void init(Locale locale, Reporter reporter) {

    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public Set<? extends Option> getSupportedOptions() {

        return options;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return null;
    }

    @Override
    public boolean run(DocletEnvironment environment) {

        docTrees = environment.getDocTrees();
        iterateClasses(environment);

        return OK;
    }

    /**
     * Needed by the Javadoc to iterate through the classes
     */
    private void iterateClasses(DocletEnvironment environment) {

        xmlSerializer = new XMLSerializer();
        this.environment = environment;

        Set<TypeElement> elements = ElementFilter.typesIn(environment.getIncludedElements());
        log.info("Exporting doc comments from " + elements.size() + " classes to: " + outputDir);

        for (TypeElement typeElement : elements) {
            generateRoot(new ShowFileObjects().getFilePath(typeElement));

            Element p = generatePackage((PackageElement) typeElement.getEnclosingElement());
            generateClass(typeElement, p);

            xmlSerializer.formatDocument();
            xmlSerializer.save(outputDir, typeElement.getQualifiedName().toString());
        }
    }

    /**
     * A scanner that displays the name of the source file
     * (if available) for any types that it encounters.
     */
    class ShowFileObjects extends ElementScanner9<Void, Void> {

        String filePath;

        ShowFileObjects() {
            filePath = "";
        }

        String getFilePath(TypeElement typeElement) {
            visitType(typeElement, null);
            return filePath;
        }

        @Override
        public Void visitType(TypeElement te, Void p) {
            TreePath dct = docTrees.getPath(te);
            if (dct != null) {
                JavaFileObject fo =
                        dct.getCompilationUnit().getSourceFile();
                filePath = fo.getName();
            }
            return super.visitType(te, p);
        }
    }

    /**
     * Generates the root element
     */
    private static void generateRoot(String sourcePath) {
        xmlSerializer.createDocument();
        Element root = xmlSerializer.createRoot("ssldoclet");
        xmlSerializer.createAttribute(root, "version", programversion);
        xmlSerializer.createAttribute(root, "creation", new Date().toString());
        xmlSerializer.createAttribute(root, "source", sourcePath);
    }

    public static boolean isDocumented(DocCommentTree docCommentTree) {

        return docCommentTree != null && !docCommentTree.getFullBody().isEmpty();
    }

    /**
     * Generates the Package node
     */
    private static Element generatePackage(PackageElement p) {
        Element packageBlockNode = xmlSerializer.createElement("Package_Block");
        Element packageNode = xmlSerializer.createElement("Package");
        Element commentBlockNode;
        Element commentNode;

        xmlSerializer.createText(packageNode, p.getQualifiedName().toString());

        DocCommentTree docCommentTree = docTrees.getDocCommentTree(p);

        if (isDocumented(docCommentTree)) {
            commentBlockNode = xmlSerializer.createElement("Package_Comment_Block");
            commentNode = xmlSerializer.createElement("Package_Comment");

            xmlSerializer.createText(commentNode, docCommentTree.getFullBody().toString());
            commentBlockNode.appendChild(commentNode);
        }
        packageBlockNode.appendChild(packageNode);

        return packageBlockNode;
    }

    /**
     * Generates the Class node
     */
    private static Element generateClass(TypeElement classDoc, Element p) {

        Element classBlockNode;
        Element classNode;

        if (classDoc.getKind().isInterface()) {
            classBlockNode = xmlSerializer.createElement("Interface_Block");
            classNode = xmlSerializer.createElement("Interface");
        } else if (classDoc.getKind().isClass() && classDoc.getModifiers().contains(Modifier.ABSTRACT) &&
                !classDoc.getKind().isInterface()) {
            classBlockNode = xmlSerializer.createElement("Abstract_Class_Block");
            classNode = xmlSerializer.createElement("Abstract_Class");
        } else {
            classBlockNode = xmlSerializer.createElement("Class_Block");
            classNode = xmlSerializer.createElement("Class");
        }

        classBlockNode.appendChild(classNode);
        p.appendChild(classBlockNode);
        xmlSerializer.createText(classNode, classDoc.getSimpleName().toString());

        for (TypeMirror extendClass : classDoc.getInterfaces()) {
            generateImplementClass(extendClass, classBlockNode);
        }

        if (!(classDoc.getSuperclass().toString().equals("java.lang.Object"))) {
            generateExtendClass(classDoc.getSuperclass(), classBlockNode);
        }

        xmlSerializer.createAttribute(classNode, "final", String.valueOf(classDoc.getModifiers().contains(Modifier.FINAL)));

        generateComments(classDoc, classBlockNode, false);
        generateFields(ElementFilter.fieldsIn(classDoc.getEnclosedElements()), classBlockNode);
        generateMethods(ElementFilter.methodsIn(classDoc.getEnclosedElements()), ElementFilter.constructorsIn(classDoc.getEnclosedElements()), classBlockNode);

        return classBlockNode;
    }

    /**
     * Generates the Class implemented by the processed class
     */
    private static void generateImplementClass(TypeMirror implementDoc, Element classBlockNode) {

        Element implementBlockNode = xmlSerializer.createElement("Interface_Block");
        Element implementNode = xmlSerializer.createElement("Interface");

        TypeElement element = (TypeElement) environment.getTypeUtils().asElement(implementDoc);

        generateExtendImplement(classBlockNode, implementBlockNode, implementNode, element);
    }

    /**
     * Generates the Class extended by the processed class
     */
    private static void generateExtendClass(TypeMirror extendDoc, Element classBlockNode) {

        Element extendBlockNode = xmlSerializer.createElement("Extends_Block");
        Element extendNode = xmlSerializer.createElement("Extends");

        TypeElement element = (TypeElement) environment.getTypeUtils().asElement(extendDoc);

        generateExtendImplement(classBlockNode, extendBlockNode, extendNode, element);
    }

    private static void generateExtendImplement(Element classBlockNode, Element extendBlockNode, Element extendNode, TypeElement element) {

        switch (element.getKind()) {
            case CLASS:
                if (element.getModifiers().contains(Modifier.ABSTRACT)) {
                    xmlSerializer.createAttribute(extendNode, "type", "Abstract_Class");
                } else {
                    xmlSerializer.createAttribute(extendNode, "type", "Class");
                }
                break;
            case INTERFACE:
                xmlSerializer.createAttribute(extendNode, "type", "Interface");
                break;
        }

        xmlSerializer.createAttribute(extendNode, "qualifiedType", element.getQualifiedName().toString());

        if (!(element.getSuperclass().toString().equals("java.lang.Object"))) {
            xmlSerializer.createAttribute(extendNode, "superclass", element.getSuperclass().toString());
            xmlSerializer.createAttribute(extendNode, "superclassFullType", element.getSuperclass().toString());
        }

        xmlSerializer.createAttribute(extendNode, "final", String.valueOf(element.getModifiers().contains(Modifier.FINAL)));

        xmlSerializer.createText(extendNode, String.valueOf(element.getSimpleName()));

        generateComments(element, extendBlockNode, true);

        extendBlockNode.appendChild(extendNode);
        classBlockNode.appendChild(extendBlockNode);
    }

    /**
     * Generates the Field node
     */
    private static void generateFields(List<VariableElement> fields, Element node) {

        Element fieldsNode = xmlSerializer.createElement("Fields");

        for (VariableElement field : fields) {

            Element fieldBlockNode = xmlSerializer.createElement("Field_Block");
            Element fieldNode = xmlSerializer.createElement("Field");

            xmlSerializer.createText(fieldNode, field.getSimpleName().toString());
            fieldBlockNode.appendChild(fieldNode);

            xmlSerializer.createAttribute(fieldNode, "type", field.asType().toString());
            xmlSerializer.createAttribute(fieldNode, "fullType", field.asType().toString());

            if (field.getConstantValue() != null && field.getConstantValue().toString().length() > 0) {
                xmlSerializer.createAttribute(fieldNode, "const", "true");
                xmlSerializer.createAttribute(fieldNode, "constantValueExpression", field.getConstantValue().toString());
            }

            setVisibility(field, fieldNode);

            xmlSerializer.createAttribute(fieldNode, "static", String.valueOf(field.getKind() == ElementKind.STATIC_INIT));
            xmlSerializer.createAttribute(fieldNode, "final", String.valueOf(field.getModifiers().contains(Modifier.FINAL)));
            xmlSerializer.createAttribute(fieldNode, "transient", String.valueOf(field.getModifiers().contains(Modifier.TRANSIENT)));
            xmlSerializer.createAttribute(fieldNode, "volatile", String.valueOf(field.getModifiers().contains(Modifier.VOLATILE)));
            generateComments(field, fieldBlockNode, false);

            fieldsNode.appendChild(fieldBlockNode);
        }
        node.appendChild(fieldsNode);
    }

    /**
     * Generates the Method node
     */
    private static void generateMethods(List<ExecutableElement> methods, List<ExecutableElement> constructors, Element node) {

        methods.addAll(constructors);
        Element methodsNode = xmlSerializer.createElement("Methods");

        for (ExecutableElement method : methods) {
            Element methodBlockNode = xmlSerializer.createElement("Method_Block");
            Element methodNode = xmlSerializer.createElement("Method");

            xmlSerializer.createText(methodNode, method.getSimpleName().toString());
            methodBlockNode.appendChild(methodNode);

            populateMethodNode(method, methodBlockNode, methodNode);

            methodsNode.appendChild(methodBlockNode);
        }
        node.appendChild(methodsNode);
    }

    /**
     * Generates additional information for Methods and Constructors node
     */
    private static void populateMethodNode(ExecutableElement method, Element blockNode, Element node) {

        setVisibility(method, node);

        xmlSerializer.createAttribute(node, "modifier", method.getModifiers().stream().map(Modifier::toString).collect(Collectors.joining(",")));

        String parameters = method.getParameters().stream().map(javax.lang.model.element.Element::asType).
                map(Objects::toString).collect(Collectors.joining(", "));

        xmlSerializer.createAttribute(node, "signature", "(" + parameters + ")");
        xmlSerializer.createAttribute(node, "static", String.valueOf(method.getKind() == ElementKind.STATIC_INIT));
        xmlSerializer.createAttribute(node, "interface", String.valueOf(method.getKind() == ElementKind.INTERFACE));

        xmlSerializer.createAttribute(node, "final", String.valueOf(method.getModifiers().contains(Modifier.FINAL)));
        xmlSerializer.createAttribute(node, "abstract", String.valueOf(method.getModifiers().contains(Modifier.ABSTRACT)));
        xmlSerializer.createAttribute(node, "synchronized", String.valueOf(method.getModifiers().contains(Modifier.SYNCHRONIZED)));
        //xmlSerializer.createAttribute(node, "synthetic", String.valueOf(method.getModifiers().contains(Modifier.));
        xmlSerializer.createAttribute(node, "constructor", String.valueOf(method.getKind() == ElementKind.CONSTRUCTOR));

        generateComments(method, blockNode, false);

        Element returnBlockNode = xmlSerializer.createElement("Return_Block");
        Element returnNode = xmlSerializer.createElement("Return");
        Element returnCommentNode = null;

        xmlSerializer.createText(returnNode, method.getReturnType().toString());
        returnBlockNode.appendChild(returnNode);
        blockNode.appendChild(returnBlockNode);
    }

    /**
     * Sets the visbility attribute for methods and fields
     */
    private static void setVisibility(javax.lang.model.element.Element member, Element node) {

        Set<Modifier> modifiers = member.getModifiers();

        if (modifiers.contains(Modifier.PRIVATE)) xmlSerializer.createAttribute(node, "visibility", "private");
        else if (modifiers.contains(Modifier.PROTECTED)) xmlSerializer.createAttribute(node, "visibility", "protected");
        else if (modifiers.contains(Modifier.PUBLIC)) xmlSerializer.createAttribute(node, "visibility", "public");
    }

    /**
     * Generates the comments for all elements
     */
    private static void generateComments(javax.lang.model.element.Element doc, Element node, boolean superClass) {

        Element commentBlockNode = null;
        Element commentNode = null;

        String overrideCommentText = "";

        DocCommentTree docCommentTree = docTrees.getDocCommentTree(doc);
        if (!isDocumented(docCommentTree))
            return;

        //TODO Process {@inheritDoc}

        switch (doc.getKind()) {
            case CLASS:
                if (superClass) {
                    commentNode = xmlSerializer.createElement("Extends_Comment");
                } else {
                    commentBlockNode = xmlSerializer.createElement("Class_Comment_Block");
                    commentNode = xmlSerializer.createElement("Class_Comment");
                }

                break;
            case FIELD:
                commentBlockNode = xmlSerializer.createElement("Field_Comment_Block");
                commentNode = xmlSerializer.createElement("Field_Comment");
                break;
            case EXCEPTION_PARAMETER:
                commentBlockNode = xmlSerializer.createElement("Exception_Comment_Block");
                commentNode = xmlSerializer.createElement("Exception_Comment");
                break;
            case CONSTRUCTOR:
            case METHOD:
                commentBlockNode = xmlSerializer.createElement("Method_Comment_Block");
                commentNode = xmlSerializer.createElement("Method_Comment");
                break;
            case ENUM:
                commentBlockNode = xmlSerializer.createElement("Enum_Comment_Block");
                commentNode = xmlSerializer.createElement("Enum_Comment");
                break;
            case INTERFACE:
                commentBlockNode = xmlSerializer.createElement("Interface_Comment_Block");
                commentNode = xmlSerializer.createElement("Interface_Comment");
                break;
            case TYPE_PARAMETER:
            default:
                return;
        }

        xmlSerializer.createText(commentNode, docCommentTree.getFullBody().toString());

        if (commentBlockNode != null) {
            commentBlockNode.appendChild(commentNode);
            node.appendChild(commentBlockNode);
        } else {
            node.appendChild(commentNode);
        }
    }

    private static boolean excludedFromComment(String tag) {
        if (tag.compareTo("@author") == 0)
            return true;
        if (tag.compareTo("@version") == 0)
            return true;

        return false;
    }

    private static boolean excludedTag(String tag) {
        if (tag.compareTo("@param") == 0)
            return true;
        if (tag.compareTo("@return") == 0)
            return true;
        if (tag.compareTo("@throws") == 0)
            return true;
        if (tag.compareTo("@exception") == 0)
            return true;
        return false;
    }
}

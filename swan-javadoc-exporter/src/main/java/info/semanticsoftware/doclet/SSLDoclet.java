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
package info.semanticsoftware.doclet;

import com.sun.javadoc.*;
import info.semanticsoftware.doclet.xml.XMLSerializer;
import org.w3c.dom.Element;

import java.util.Date;

/**
 * This class is in charge of iterating through all of the Javadoc objects and
 * serializing them into an xml file using the XMLSerialiser
 *
 * @author Ninus
 * @version 1.2
 */
public class SSLDoclet extends Doclet {
    private static String programversion = "2.0";
    private static String destdir = "./";
    private static String sourcePath;
    private static XMLSerializer xmlSerializer;


    /**
     * Needed by the Javadoc API
     */
    public static boolean start(RootDoc doc) {
        getParameters(doc);
        iterateClasses(doc);
        return true;
    }

    /**
     * Needed by the Javadoc to validate the options
     */
    public static int optionLength(String option) {
        if (option.compareToIgnoreCase("-d") == 0)
            return 2;
        return 0;
    }

    /**
     * Needed by the Javadoc for valid options
     */
    public static boolean validOptions(String options[][], DocErrorReporter reporter) {
        return true;
    }

    /**
     * Needed by the Javadoc to iterate through valid options
     */
    private static void getParameters(RootDoc doc) {
        String[][] options = doc.options();
        for (String[] option : options) {
            if (option[0].compareToIgnoreCase("-d") == 0) {
                destdir = option[1];
                String fs = System.getProperty("file.separator");

                if (!destdir.endsWith(fs)) {
                    destdir += fs;
                    continue;
                }
            } else if (option[0].equals("-sourcepath")) {

                if (option[1].contains(":"))
                    sourcePath = option[1].substring(0, option[1].indexOf(":"));
                else
                    sourcePath = option[1];
            }
        }

        System.out.println("XML Generator Version: " + programversion);
        System.out.println("Using output directory '" + destdir);
    }

    /**
     * Needed by the Javadoc to iterate through the classes
     */
    private static void iterateClasses(RootDoc doc) {
        ClassDoc[] classes = doc.classes();

        xmlSerializer = new XMLSerializer();

        for (ClassDoc c : classes) {
            generateRoot();
            Element p = generatePackage(c.containingPackage());
            generateClass(c, p);
            xmlSerializer.formatDocument();
            xmlSerializer.save(destdir, c.name(), c.containingPackage().name());
        }
    }

    /**
     * Generates the root element
     */
    private static void generateRoot() {
        xmlSerializer.createDocument();
        Element root = xmlSerializer.createRoot("ssldoclet");
        xmlSerializer.createAttribute(root, "version", programversion);
        xmlSerializer.createAttribute(root, "creation", new Date().toString());
        xmlSerializer.createAttribute(root, "source", sourcePath);
    }

    /**
     * Generates the Package node
     */
    private static Element generatePackage(PackageDoc p) {
        Element packageBlockNode = xmlSerializer.createElement("Package_Block");
        Element packageNode = xmlSerializer.createElement("Package");
        Element commentBlockNode = null;
        Element commentNode = null;

        xmlSerializer.createText(packageNode, p.name());
        if (p.commentText() != null && p.commentText().length() > 0) {
            commentBlockNode = xmlSerializer.createElement("Package_Comment_Block");
            commentNode = xmlSerializer.createElement("Package_Comment");

            xmlSerializer.createText(commentNode, p.commentText());
            commentBlockNode.appendChild(commentNode);
        }
        //x.createAttribute(commentNode, "line", String.valueOf(p.position().line()));
        packageBlockNode.appendChild(packageNode);

        return packageBlockNode;
    }

    /**
     * Generates the Class node
     */
    private static Element generateClass(ClassDoc classDoc, Element p) {
        Element classBlockNode;
        Element classNode;

        if (classDoc.isInterface()) {
            classBlockNode = xmlSerializer.createElement("Interface_Block");
            classNode = xmlSerializer.createElement("Interface");
        } else if (classDoc.isAbstract() && !classDoc.isInterface()) {
            classBlockNode = xmlSerializer.createElement("Abstract_Class_Block");
            classNode = xmlSerializer.createElement("Abstract_Class");
        } else {
            classBlockNode = xmlSerializer.createElement("Class_Block");
            classNode = xmlSerializer.createElement("Class");
        }

        classBlockNode.appendChild(classNode);
        p.appendChild(classBlockNode);

        xmlSerializer.createText(classNode, classDoc.name());


        ClassDoc[] extendClasses = classDoc.interfaces();
        if (extendClasses.length > 0) {
            for (ClassDoc extendClass : extendClasses) {
                generateImplementClass(extendClass, classBlockNode);
            }
        }

        if (classDoc.superclass() != null) {

            generateExtendClass(classDoc.superclass(), classBlockNode);
        }

        xmlSerializer.createAttribute(classNode, "line", String.valueOf(classDoc.position().line()));
        xmlSerializer.createAttribute(classNode, "final", String.valueOf(classDoc.isFinal()));
        xmlSerializer.createAttribute(classNode, "serializable", String.valueOf(classDoc.isSerializable()));

        generateComments(classDoc, classBlockNode);

        generateFields(classDoc.fields(), classBlockNode);
        // generateConstructors(classDoc.constructors(), classBlockNode);
        generateMethods(classDoc.methods(), classDoc.constructors(), classBlockNode);

        ClassDoc[] innerClasses = classDoc.innerClasses();
        for (int classIndex = 0; classIndex < innerClasses.length; classIndex++)
            classBlockNode.appendChild(generateClass(innerClasses[classIndex], classBlockNode));

        return classBlockNode;
    }

    /**
     * Generates the Class implemented by the processed class
     */
    private static void generateImplementClass(ClassDoc implementDoc, Element classBlockNode) {
        Element implementBlockNode;
        Element implementNode;
        Element implementCommentNode;

        implementBlockNode = xmlSerializer.createElement("Interface_Block");
        implementNode = xmlSerializer.createElement("Interface");

        xmlSerializer.createAttribute(implementNode, "type", "Interface");
        xmlSerializer.createAttribute(implementNode, "qualifiedType", implementDoc.qualifiedTypeName());

        if (implementDoc.superclass() != null) {
            xmlSerializer.createAttribute(implementNode, "superclass", implementDoc.superclass().name());
            xmlSerializer.createAttribute(implementNode, "superclassFullType", implementDoc.superclass().qualifiedName());
        }

        xmlSerializer.createAttribute(implementNode, "final", String.valueOf(implementDoc.isFinal()));
        xmlSerializer.createAttribute(implementNode, "serializable", String.valueOf(implementDoc.isSerializable()));

        xmlSerializer.createText(implementNode, implementDoc.name());

        if (implementDoc.commentText() != null && implementDoc.commentText().length() > 0) {
            implementCommentNode = xmlSerializer.createElement("Implements_Comment");
            xmlSerializer.createText(implementCommentNode, implementDoc.commentText());
            implementBlockNode.appendChild(implementCommentNode);
        }

        implementBlockNode.appendChild(implementNode);
        classBlockNode.appendChild(implementBlockNode);
    }

    /**
     * Generates the Class extended by the processed class
     */
    private static void generateExtendClass(ClassDoc extendDoc, Element classBlockNode) {
        Element extendBlockNode;
        Element extendNode;
        Element extendCommentNode;

        extendBlockNode = xmlSerializer.createElement("Extends_Block");
        extendNode = xmlSerializer.createElement("Extends");

        if (extendDoc.isAbstract()) {
            xmlSerializer.createAttribute(extendNode, "type", "Abstract_Class");
        } else {
            xmlSerializer.createAttribute(extendNode, "type", "Class");
        }

        xmlSerializer.createAttribute(extendNode, "qualifiedType", extendDoc.qualifiedTypeName());

        if (extendDoc.superclass() != null) {
            xmlSerializer.createAttribute(extendNode, "superclass", extendDoc.superclass().name());
            xmlSerializer.createAttribute(extendNode, "superclassFullType", extendDoc.superclass().qualifiedName());
        }


        xmlSerializer.createAttribute(extendNode, "final", String.valueOf(extendDoc.isFinal()));
        xmlSerializer.createAttribute(extendNode, "serializable", String.valueOf(extendDoc.isSerializable()));

        xmlSerializer.createText(extendNode, extendDoc.name());

        if (extendDoc.commentText() != null && extendDoc.commentText().length() > 0) {
            extendCommentNode = xmlSerializer.createElement("Extends_Comment");
            xmlSerializer.createText(extendCommentNode, extendDoc.commentText());
            extendBlockNode.appendChild(extendCommentNode);
        }

        extendBlockNode.appendChild(extendNode);
        classBlockNode.appendChild(extendBlockNode);
    }

    /**
     * Generates the Field node
     */
    private static void generateFields(FieldDoc[] fields, Element node) {
        if (fields.length < 1) return;

        Element fieldsNode = xmlSerializer.createElement("Fields");

        for (int index = 0; index < fields.length; index++) {
            Element fieldBlockNode = xmlSerializer.createElement("Field_Block");
            Element fieldNode = xmlSerializer.createElement("Field");

            xmlSerializer.createText(fieldNode, fields[index].name());
            fieldBlockNode.appendChild(fieldNode);
            xmlSerializer.createAttribute(fieldNode, "line", String.valueOf(fields[index].position().line()));
            xmlSerializer.createAttribute(fieldNode, "type", fields[index].type().typeName());
            xmlSerializer.createAttribute(fieldNode, "fullType", fields[index].type().toString());

            if (fields[index].constantValue() != null && fields[index].constantValue().toString().length() > 0)
                xmlSerializer.createAttribute(fieldNode, "const", "true");
            if (fields[index].constantValueExpression() != null && fields[index].constantValueExpression().length() > 0)
                xmlSerializer.createAttribute(fieldNode, "constantValueExpression", fields[index].constantValueExpression());

            setVisibility(fields[index], fieldNode);


            xmlSerializer.createAttribute(fieldNode, "static", String.valueOf(fields[index].isStatic()));
            xmlSerializer.createAttribute(fieldNode, "final", String.valueOf(fields[index].isFinal()));
            xmlSerializer.createAttribute(fieldNode, "transient", String.valueOf(fields[index].isTransient()));
            xmlSerializer.createAttribute(fieldNode, "volatile", String.valueOf(fields[index].isVolatile()));

            generateComments(fields[index], fieldBlockNode);

            fieldsNode.appendChild(fieldBlockNode);
        }
        node.appendChild(fieldsNode);
    }

    /**
     * Generates the Constructor node
     */
    private static void generateConstructors(ConstructorDoc[] constructors, Element node) {
        if (constructors.length < 1) return;

        Element constructorsNode = xmlSerializer.createElement("Constructors");

        for (int index = 0; index < constructors.length; index++) {
            Element constBlockNode = xmlSerializer.createElement("Constructor_Block");
            Element constNode = xmlSerializer.createElement("Constructor");

            xmlSerializer.createText(constNode, constructors[index].name());
            constBlockNode.appendChild(constNode);

            populateMethodNode(constructors[index], constBlockNode, constNode);

            constructorsNode.appendChild(constBlockNode);
        }

        node.appendChild(constructorsNode);
    }

    /**
     * Generates the Method node
     */
    private static void generateMethods(MethodDoc[] methods, ConstructorDoc[] constructors, Element node) {
        if ((methods.length + constructors.length) < 1) return;

        Element methodsNode = xmlSerializer.createElement("Methods");

        for (int index = 0; index < methods.length; index++) {
            Element methodBlockNode = xmlSerializer.createElement("Method_Block");
            Element methodNode = xmlSerializer.createElement("Method");

            xmlSerializer.createText(methodNode, methods[index].name());
            methodBlockNode.appendChild(methodNode);

            populateMethodNode(methods[index], methodBlockNode, methodNode);


            methodsNode.appendChild(methodBlockNode);
        }

        for (int index = 0; index < constructors.length; index++) {
            Element methodBlockNode = xmlSerializer.createElement("Method_Block");
            Element methodNode = xmlSerializer.createElement("Method");

            xmlSerializer.createText(methodNode, constructors[index].name());
            methodBlockNode.appendChild(methodNode);

            populateMethodNode(constructors[index], methodBlockNode, methodNode);


            methodsNode.appendChild(methodBlockNode);
        }

        node.appendChild(methodsNode);
    }


    /**
     * Generates additional information for Methods and Constructors node
     */
    private static void populateMethodNode(ExecutableMemberDoc method, Element blockNode, Element node) {
        setVisibility(method, node);

        xmlSerializer.createAttribute(node, "line", String.valueOf(method.position().line()));
        xmlSerializer.createAttribute(node, "modifier", method.modifiers());
        xmlSerializer.createAttribute(node, "signature", method.signature());
        xmlSerializer.createAttribute(node, "static", String.valueOf(method.isStatic()));
        xmlSerializer.createAttribute(node, "interface", String.valueOf(method.isInterface()));
        xmlSerializer.createAttribute(node, "final", String.valueOf(method.isFinal()));
        if (method instanceof MethodDoc)
            xmlSerializer.createAttribute(node, "abstract", String.valueOf(((MethodDoc) method).isAbstract()));
        xmlSerializer.createAttribute(node, "synchronized", String.valueOf(method.isSynchronized()));
        xmlSerializer.createAttribute(node, "synthetic", String.valueOf(method.isSynthetic()));
        xmlSerializer.createAttribute(node, "constructor", String.valueOf(method.isConstructor()));

        generateComments(method, blockNode);

        if (method instanceof MethodDoc) { //&&
            //((MethodDoc) method).returnType().simpleTypeName().compareToIgnoreCase("void") != 0

            Element returnBlockNode = xmlSerializer.createElement("Return_Block");
            Element returnNode = xmlSerializer.createElement("Return");
            Element returnCommentNode = null;

            xmlSerializer.createText(returnNode, ((MethodDoc) method).returnType().toString());

            returnBlockNode.appendChild(returnNode);

            Tag[] ts = method.tags();
            for (Tag t : ts) {
                if (t.name().compareToIgnoreCase("@return") == 0) {
                    if (t.text() != null && t.text().length() > 0) {
                        returnCommentNode = xmlSerializer.createElement("Return_Comment");
                        xmlSerializer.createText(returnCommentNode, t.text());
                        returnBlockNode.appendChild(returnCommentNode);
                    }
                }
            }
            blockNode.appendChild(returnBlockNode);
        }

        Parameter[] parameters = method.parameters();
        if (parameters.length > 0) {
            ParamTag[] paramTags = method.paramTags();

            for (int param = 0; param < parameters.length; param++) {
                Element paramBlockNode = xmlSerializer.createElement("Parameter_Block");
                Element paramNode = xmlSerializer.createElement("Parameter");
                Element paramCommentNode = null;

                xmlSerializer.createText(paramNode, parameters[param].name());
                paramBlockNode.appendChild(paramNode);

                for (int paramTag = 0; paramTag < paramTags.length; paramTag++) {
                    if (paramTags[paramTag].parameterName().compareToIgnoreCase(parameters[param].name()) == 0) {
                        if (paramTags[paramTag].parameterComment() != null && paramTags[paramTag].parameterComment().length() > 0) {
                            paramCommentNode = xmlSerializer.createElement("Parameter_Comment");
                            xmlSerializer.createText(paramCommentNode, paramTags[paramTag].parameterComment());
                            paramBlockNode.appendChild(paramCommentNode);
                        }
                    }
                }

                xmlSerializer.createAttribute(paramNode, "type", parameters[param].type().typeName());
                xmlSerializer.createAttribute(paramNode, "fulltype", parameters[param].type().toString());

                blockNode.appendChild(paramBlockNode);
            }
        }

        ClassDoc[] exceptions = method.thrownExceptions();
        if (exceptions.length > 0) {
            ThrowsTag[] throwsTags = method.throwsTags();

            for (int except = 0; except < exceptions.length; except++) {
                Element exceptBlockNode = xmlSerializer.createElement("Exception_Block");
                Element exceptNode = xmlSerializer.createElement("Exception");
                Element exceptCommentNode = null;

                xmlSerializer.createText(exceptNode, exceptions[except].name());
                exceptBlockNode.appendChild(exceptNode);

                for (int throwTag = 0; throwTag < throwsTags.length; throwTag++) {
                    if (throwsTags[throwTag].exceptionName().compareToIgnoreCase(exceptions[except].name()) == 0) {
                        if (throwsTags[throwTag].exceptionComment() != null && throwsTags[throwTag].exceptionComment().length() > 0) {
                            exceptCommentNode = xmlSerializer.createElement("Exception_Comment");
                            xmlSerializer.createText(exceptCommentNode, throwsTags[throwTag].exceptionComment());
                            exceptBlockNode.appendChild(exceptCommentNode);
                        }
                    }
                }

                xmlSerializer.createAttribute(exceptNode, "type", exceptions[except].typeName());
                xmlSerializer.createAttribute(exceptNode, "fulltype", exceptions[except].qualifiedTypeName());

                blockNode.appendChild(exceptBlockNode);
            }
        }
    }

    /**
     * Sets the visbility attribute for methods and fields
     */
    private static void setVisibility(ProgramElementDoc member, Element node) {
        if (member.isPrivate()) xmlSerializer.createAttribute(node, "visibility", "private");
        else if (member.isProtected()) xmlSerializer.createAttribute(node, "visibility", "protected");
        else if (member.isPublic()) xmlSerializer.createAttribute(node, "visibility", "public");
        else if (member.isPackagePrivate()) xmlSerializer.createAttribute(node, "visibility", "package-private");
    }

    /**
     * Generates the comments for all elements
     */
    private static void generateComments(Doc doc, Element node) {

        Element commentBlockNode = null;
        Element commentNode = null;

        String overrideCommentText = "";

        if (doc.commentText() == null || doc.commentText().length() <= 0)
            return;


        //if (doc.name().contentEquals("encodeForXML"))
            // System.out.println(((MethodDoc) doc).qualifiedName()+"****"+doc.commentText()+"****"+doc.getClass().getSimpleName());


           /* if (doc.commentText() == null || doc.commentText().length() <= 0 || doc.commentText().contains("{@inheritDoc}")) {


                if (doc instanceof MethodDoc) {


                    MethodDoc methodDoc = (MethodDoc) doc;
                    if (doc.name().contentEquals("encodeForXML"))
                        //   System.out.println("222222"+((MethodDoc) doc).qualifiedName()+"****"+doc.commentText()+"****"+methodDoc.overriddenMethod().name());

                        if (methodDoc.overriddenMethod() != null) {
                            if (doc.commentText().contains("{@inheritDoc}")) {

                                if (doc.name().contentEquals("encodeForXML"))
                                    //  System.out.println("****"+((MethodDoc) doc).qualifiedName()+"****"+methodDoc.overriddenMethod().qualifiedName());
                                    overrideCommentText = doc.commentText().replace("{@inheritDoc}", methodDoc.overriddenMethod().commentText());
                            } else
                                overrideCommentText = methodDoc.overriddenMethod().commentText();
                        } else
                            return;
                } else
                    return;
            }*/


            if (doc.isClass()) {
                commentBlockNode = xmlSerializer.createElement("Class_Comment_Block");
                commentNode = xmlSerializer.createElement("Class_Comment");
            } else if (doc.isInterface()) {
                commentBlockNode = xmlSerializer.createElement("Interface_Comment_Block");
                commentNode = xmlSerializer.createElement("Interface_Comment");
            } else if (doc.isEnum()) {
                commentBlockNode = xmlSerializer.createElement("Enum_Comment_Block");
                commentNode = xmlSerializer.createElement("Enum_Comment");
            } else if (doc.isMethod() ) {

                //   if()
                //  System.out.println("NAME: "+doc.name());
                commentBlockNode = xmlSerializer.createElement("Method_Comment_Block");
                commentNode = xmlSerializer.createElement("Method_Comment");
            } else if (doc.isConstructor()) {
            commentBlockNode = xmlSerializer.createElement("Constructor_Comment_Block");
            commentNode = xmlSerializer.createElement("Constructor_Comment");
        } else if (doc.isField()) {
                commentBlockNode = xmlSerializer.createElement("Field_Comment_Block");
                commentNode = xmlSerializer.createElement("Field_Comment");
            } else if (doc.isException()) {
                commentBlockNode = xmlSerializer.createElement("Exception_Comment_Block");
                commentNode = xmlSerializer.createElement("Exception_Comment");
            } else if (doc.isClass()) {
                commentBlockNode = xmlSerializer.createElement("Class_Comment_Block");
                commentNode = xmlSerializer.createElement("Class_Comment");
            } else
                return;

        boolean addNode = false;

       /* assert commentNode != null;


        if (overrideCommentText.length() > 0) {

            xmlSerializer.createText(commentNode, overrideCommentText);
        } else*/

            xmlSerializer.createText(commentNode, doc.commentText());
        xmlSerializer.createAttribute(commentNode, "line", String.valueOf(doc.position().line()));
        commentBlockNode.appendChild(commentNode);
        addNode = true;


        /*Tag[] tags = doc.tags();
        for (Tag value : tags) {
            Element paramNode;

            if (excludedTag(value.name())) continue;

            System.out.println("XML Element: "+value.name().substring(1).substring(0, 1).toUpperCase() + value.name().substring(2).toLowerCase());

            if (!excludedFromComment(value.name()) )
                paramNode = xmlSerializer.createElement(value.name().substring(1).substring(0, 1).toUpperCase() + value.name().substring(2).toLowerCase() + "_Comment");

            else
                paramNode = xmlSerializer.createElement(value.name().substring(1).substring(0, 1).toUpperCase() + value.name().substring(2).toLowerCase());

            xmlSerializer.createText(paramNode, value.text());
            commentBlockNode.appendChild(paramNode);
            addNode = true;
        }*/

        if (addNode) {
            node.appendChild(commentBlockNode);
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

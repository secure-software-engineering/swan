
package de.fraunhofer.iem.swan.io.doc.ssldoclet;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the de.fraunhofer.iem.swan.io.parser.ssldoclet package.
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Ssldoclet_QNAME = new QName("", "ssldoclet");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.fraunhofer.iem.swan.io.parser.ssldoclet
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SsldocletType }
     * 
     */
    public SsldocletType createSsldocletType() {
        return new SsldocletType();
    }

    /**
     * Create an instance of {@link ClassBlockType }
     * 
     */
    public ClassBlockType createClassBlockType() {
        return new ClassBlockType();
    }

    /**
     * Create an instance of {@link MethodType }
     * 
     */
    public MethodType createMethodType() {
        return new MethodType();
    }

    /**
     * Create an instance of {@link ConstructorsType }
     * 
     */
    public ConstructorsType createConstructorsType() {
        return new ConstructorsType();
    }

    /**
     * Create an instance of {@link MethodCommentBlockType }
     * 
     */
    public MethodCommentBlockType createMethodCommentBlockType() {
        return new MethodCommentBlockType();
    }

    /**
     * Create an instance of {@link InterfaceBlockType }
     * 
     */
    public InterfaceBlockType createInterfaceBlockType() {
        return new InterfaceBlockType();
    }

    /**
     * Create an instance of {@link ClassCommentType }
     * 
     */
    public ClassCommentType createClassCommentType() {
        return new ClassCommentType();
    }

    /**
     * Create an instance of {@link ClassCommentBlockType }
     * 
     */
    public ClassCommentBlockType createClassCommentBlockType() {
        return new ClassCommentBlockType();
    }

    /**
     * Create an instance of {@link ReturnBlockType }
     * 
     */
    public ReturnBlockType createReturnBlockType() {
        return new ReturnBlockType();
    }

    /**
     * Create an instance of {@link MethodCommentType }
     * 
     */
    public MethodCommentType createMethodCommentType() {
        return new MethodCommentType();
    }

    /**
     * Create an instance of {@link ExtendsType }
     * 
     */
    public ExtendsType createExtendsType() {
        return new ExtendsType();
    }

    /**
     * Create an instance of {@link ClassType }
     * 
     */
    public ClassType createClassType() {
        return new ClassType();
    }

    /**
     * Create an instance of {@link InterfaceType }
     * 
     */
    public InterfaceType createInterfaceType() {
        return new InterfaceType();
    }

    /**
     * Create an instance of {@link MethodBlockType }
     * 
     */
    public MethodBlockType createMethodBlockType() {
        return new MethodBlockType();
    }

    /**
     * Create an instance of {@link ConstructorCommentType }
     * 
     */
    public ConstructorCommentType createConstructorCommentType() {
        return new ConstructorCommentType();
    }

    /**
     * Create an instance of {@link ExtendsBlockType }
     * 
     */
    public ExtendsBlockType createExtendsBlockType() {
        return new ExtendsBlockType();
    }

    /**
     * Create an instance of {@link MethodsType }
     * 
     */
    public MethodsType createMethodsType() {
        return new MethodsType();
    }

    /**
     * Create an instance of {@link ConstructorType }
     * 
     */
    public ConstructorType createConstructorType() {
        return new ConstructorType();
    }

    /**
     * Create an instance of {@link PackageBlockType }
     * 
     */
    public PackageBlockType createPackageBlockType() {
        return new PackageBlockType();
    }

    /**
     * Create an instance of {@link ConstructorCommentBlockType }
     * 
     */
    public ConstructorCommentBlockType createConstructorCommentBlockType() {
        return new ConstructorCommentBlockType();
    }

    /**
     * Create an instance of {@link ConstructorBlockType }
     * 
     */
    public ConstructorBlockType createConstructorBlockType() {
        return new ConstructorBlockType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SsldocletType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ssldoclet")
    public JAXBElement<SsldocletType> createSsldoclet(SsldocletType value) {
        return new JAXBElement<SsldocletType>(_Ssldoclet_QNAME, SsldocletType.class, null, value);
    }

}

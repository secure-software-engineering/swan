
package de.fraunhofer.iem.swan.io.doc.ssldoclet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für Class_BlockType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="Class_BlockType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Class" type="{}ClassType"/>
 *         &lt;element name="Interface_Block" type="{}Interface_BlockType"/>
 *         &lt;element name="Extends_Block" type="{}Extends_BlockType"/>
 *         &lt;element name="Class_Comment_Block" type="{}Class_Comment_BlockType"/>
 *         &lt;element name="Constructors" type="{}ConstructorsType"/>
 *         &lt;element name="Methods" type="{}MethodsType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Class_BlockType", propOrder = {
    "clazz",
    "interfaceBlock",
    "extendsBlock",
    "classCommentBlock",
    "constructors",
    "methods"
})
public class ClassBlockType {

    @XmlElement(name = "Class", required = true)
    protected ClassType clazz;
    @XmlElement(name = "Interface_Block", required = true)
    protected InterfaceBlockType interfaceBlock;
    @XmlElement(name = "Extends_Block", required = true)
    protected ExtendsBlockType extendsBlock;
    @XmlElement(name = "Class_Comment_Block", required = true)
    protected ClassCommentBlockType classCommentBlock;
    @XmlElement(name = "Constructors", required = true)
    protected ConstructorsType constructors;
    @XmlElement(name = "Methods", required = true)
    protected MethodsType methods;

    /**
     * Ruft den Wert der clazz-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ClassType }
     *     
     */
    public ClassType getClazz() {
        return clazz;
    }

    /**
     * Legt den Wert der clazz-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ClassType }
     *     
     */
    public void setClazz(ClassType value) {
        this.clazz = value;
    }

    /**
     * Ruft den Wert der interfaceBlock-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link InterfaceBlockType }
     *     
     */
    public InterfaceBlockType getInterfaceBlock() {
        return interfaceBlock;
    }

    /**
     * Legt den Wert der interfaceBlock-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link InterfaceBlockType }
     *     
     */
    public void setInterfaceBlock(InterfaceBlockType value) {
        this.interfaceBlock = value;
    }

    /**
     * Ruft den Wert der extendsBlock-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ExtendsBlockType }
     *     
     */
    public ExtendsBlockType getExtendsBlock() {
        return extendsBlock;
    }

    /**
     * Legt den Wert der extendsBlock-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtendsBlockType }
     *     
     */
    public void setExtendsBlock(ExtendsBlockType value) {
        this.extendsBlock = value;
    }

    /**
     * Ruft den Wert der classCommentBlock-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ClassCommentBlockType }
     *     
     */
    public ClassCommentBlockType getClassCommentBlock() {
        return classCommentBlock;
    }

    /**
     * Legt den Wert der classCommentBlock-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ClassCommentBlockType }
     *     
     */
    public void setClassCommentBlock(ClassCommentBlockType value) {
        this.classCommentBlock = value;
    }

    /**
     * Ruft den Wert der constructors-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ConstructorsType }
     *     
     */
    public ConstructorsType getConstructors() {
        return constructors;
    }

    /**
     * Legt den Wert der constructors-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ConstructorsType }
     *     
     */
    public void setConstructors(ConstructorsType value) {
        this.constructors = value;
    }

    /**
     * Ruft den Wert der methods-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MethodsType }
     *     
     */
    public MethodsType getMethods() {
        return methods;
    }

    /**
     * Legt den Wert der methods-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MethodsType }
     *     
     */
    public void setMethods(MethodsType value) {
        this.methods = value;
    }

}

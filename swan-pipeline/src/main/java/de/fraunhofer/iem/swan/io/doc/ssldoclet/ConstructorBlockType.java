
package de.fraunhofer.iem.swan.io.doc.ssldoclet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für Constructor_BlockType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="Constructor_BlockType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Constructor" type="{}ConstructorType"/>
 *         &lt;element name="Constructor_Comment_Block" type="{}Constructor_Comment_BlockType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Constructor_BlockType", propOrder = {
    "constructor",
    "constructorCommentBlock"
})
public class ConstructorBlockType {

    @XmlElement(name = "Constructor", required = true)
    protected ConstructorType constructor;
    @XmlElement(name = "Constructor_Comment_Block", required = true)
    protected ConstructorCommentBlockType constructorCommentBlock;

    /**
     * Ruft den Wert der constructor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ConstructorType }
     *     
     */
    public ConstructorType getConstructor() {
        return constructor;
    }

    /**
     * Legt den Wert der constructor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ConstructorType }
     *     
     */
    public void setConstructor(ConstructorType value) {
        this.constructor = value;
    }

    /**
     * Ruft den Wert der constructorCommentBlock-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ConstructorCommentBlockType }
     *     
     */
    public ConstructorCommentBlockType getConstructorCommentBlock() {
        return constructorCommentBlock;
    }

    /**
     * Legt den Wert der constructorCommentBlock-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ConstructorCommentBlockType }
     *     
     */
    public void setConstructorCommentBlock(ConstructorCommentBlockType value) {
        this.constructorCommentBlock = value;
    }

}

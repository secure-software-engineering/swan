
package de.fraunhofer.iem.swan.io.doc.ssldoclet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für Constructor_Comment_BlockType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="Constructor_Comment_BlockType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Constructor_Comment" type="{}Constructor_CommentType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Constructor_Comment_BlockType", propOrder = {
    "constructorComment"
})
public class ConstructorCommentBlockType {

    @XmlElement(name = "Constructor_Comment", required = true)
    protected ConstructorCommentType constructorComment;

    /**
     * Ruft den Wert der constructorComment-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ConstructorCommentType }
     *     
     */
    public ConstructorCommentType getConstructorComment() {
        return constructorComment;
    }

    /**
     * Legt den Wert der constructorComment-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ConstructorCommentType }
     *     
     */
    public void setConstructorComment(ConstructorCommentType value) {
        this.constructorComment = value;
    }

}

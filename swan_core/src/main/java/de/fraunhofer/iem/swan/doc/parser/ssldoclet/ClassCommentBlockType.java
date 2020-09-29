
package de.fraunhofer.iem.swan.doc.parser.ssldoclet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für Class_Comment_BlockType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="Class_Comment_BlockType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Class_Comment" type="{}Class_CommentType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Class_Comment_BlockType", propOrder = {
    "classComment"
})
public class ClassCommentBlockType {

    @XmlElement(name = "Class_Comment", required = true)
    protected ClassCommentType classComment;

    /**
     * Ruft den Wert der classComment-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ClassCommentType }
     *     
     */
    public ClassCommentType getClassComment() {
        return classComment;
    }

    /**
     * Legt den Wert der classComment-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ClassCommentType }
     *     
     */
    public void setClassComment(ClassCommentType value) {
        this.classComment = value;
    }

}

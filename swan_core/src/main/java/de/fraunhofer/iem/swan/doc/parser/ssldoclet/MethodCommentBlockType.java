
package de.fraunhofer.iem.swan.doc.parser.ssldoclet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für Method_Comment_BlockType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="Method_Comment_BlockType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Method_Comment" type="{}Method_CommentType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Method_Comment_BlockType", propOrder = {
    "methodComment"
})
public class MethodCommentBlockType {

    @XmlElement(name = "Method_Comment", required = true)
    protected MethodCommentType methodComment;

    /**
     * Ruft den Wert der methodComment-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MethodCommentType }
     *     
     */
    public MethodCommentType getMethodComment() {
        return methodComment;
    }

    /**
     * Legt den Wert der methodComment-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MethodCommentType }
     *     
     */
    public void setMethodComment(MethodCommentType value) {
        this.methodComment = value;
    }

}

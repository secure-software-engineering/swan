
package de.fraunhofer.iem.swan.doc.parser.ssldoclet;

import javax.xml.bind.annotation.*;


/**
 * <p>Java-Klasse für Method_BlockType complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="Method_BlockType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Method" type="{}MethodType"/>
 *         &lt;element name="Method_Comment_Block" type="{}Method_Comment_BlockType"/>
 *         &lt;element name="Return_Block" type="{}Return_BlockType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Method_BlockType", propOrder = {
        "method",
        "methodCommentBlock",
        "returnBlock"
})
public class MethodBlockType {

    @XmlElement(name = "Method", required = true)
    protected MethodType method;
    @XmlElement(name = "Method_Comment_Block", required = true)
    protected MethodCommentBlockType methodCommentBlock;
    @XmlElement(name = "Return_Block", required = true)
    protected ReturnBlockType returnBlock;

    @XmlTransient
    private String signature;

    /**
     * Ruft den Wert der method-Eigenschaft ab.
     *
     * @return possible object is
     * {@link MethodType }
     */
    public MethodType getMethod() {
        return method;
    }

    /**
     * Legt den Wert der method-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link MethodType }
     */
    public void setMethod(MethodType value) {
        this.method = value;
    }

    /**
     * Ruft den Wert der methodCommentBlock-Eigenschaft ab.
     *
     * @return possible object is
     * {@link MethodCommentBlockType }
     */
    public MethodCommentBlockType getMethodCommentBlock() {
        return methodCommentBlock;
    }

    /**
     * Legt den Wert der methodCommentBlock-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link MethodCommentBlockType }
     */
    public void setMethodCommentBlock(MethodCommentBlockType value) {
        this.methodCommentBlock = value;
    }

    /**
     * Ruft den Wert der returnBlock-Eigenschaft ab.
     *
     * @return possible object is
     * {@link ReturnBlockType }
     */
    public ReturnBlockType getReturnBlock() {
        return returnBlock;
    }

    /**
     * Legt den Wert der returnBlock-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link ReturnBlockType }
     */
    public void setReturnBlock(ReturnBlockType value) {
        this.returnBlock = value;
    }


    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}

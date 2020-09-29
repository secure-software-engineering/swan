
package de.fraunhofer.iem.swan.doc.parser.ssldoclet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für Extends_BlockType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="Extends_BlockType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Extends_Comment" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Extends" type="{}ExtendsType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Extends_BlockType", propOrder = {
    "extendsComment",
    "_extends"
})
public class ExtendsBlockType {

    @XmlElement(name = "Extends_Comment", required = true)
    protected String extendsComment;
    @XmlElement(name = "Extends", required = true)
    protected ExtendsType _extends;

    /**
     * Ruft den Wert der extendsComment-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtendsComment() {
        return extendsComment;
    }

    /**
     * Legt den Wert der extendsComment-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtendsComment(String value) {
        this.extendsComment = value;
    }

    /**
     * Ruft den Wert der extends-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ExtendsType }
     *     
     */
    public ExtendsType getExtends() {
        return _extends;
    }

    /**
     * Legt den Wert der extends-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtendsType }
     *     
     */
    public void setExtends(ExtendsType value) {
        this._extends = value;
    }

}

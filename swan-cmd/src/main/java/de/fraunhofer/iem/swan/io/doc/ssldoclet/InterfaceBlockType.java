
package de.fraunhofer.iem.swan.io.doc.ssldoclet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für Interface_BlockType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="Interface_BlockType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Implements_Comment" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Interface" type="{}InterfaceType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Interface_BlockType", propOrder = {
    "implementsComment",
    "_interface"
})
public class InterfaceBlockType {

    @XmlElement(name = "Implements_Comment", required = true)
    protected String implementsComment;
    @XmlElement(name = "Interface", required = true)
    protected InterfaceType _interface;

    /**
     * Ruft den Wert der implementsComment-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImplementsComment() {
        return implementsComment;
    }

    /**
     * Legt den Wert der implementsComment-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImplementsComment(String value) {
        this.implementsComment = value;
    }

    /**
     * Ruft den Wert der interface-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link InterfaceType }
     *     
     */
    public InterfaceType getInterface() {
        return _interface;
    }

    /**
     * Legt den Wert der interface-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link InterfaceType }
     *     
     */
    public void setInterface(InterfaceType value) {
        this._interface = value;
    }

}


package de.fraunhofer.iem.swan.io.doc.ssldoclet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java-Klasse für ExtendsType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ExtendsType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="final" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="qualifiedType" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="serializable" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="superclass" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="superclassFullType" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtendsType", propOrder = {
    "value"
})
public class ExtendsType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "final")
    protected String _final;
    @XmlAttribute(name = "qualifiedType")
    protected String qualifiedType;
    @XmlAttribute(name = "serializable")
    protected String serializable;
    @XmlAttribute(name = "superclass")
    protected String superclass;
    @XmlAttribute(name = "superclassFullType")
    protected String superclassFullType;
    @XmlAttribute(name = "type")
    protected String type;

    /**
     * Ruft den Wert der value-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Legt den Wert der value-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Ruft den Wert der final-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFinal() {
        return _final;
    }

    /**
     * Legt den Wert der final-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFinal(String value) {
        this._final = value;
    }

    /**
     * Ruft den Wert der qualifiedType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQualifiedType() {
        return qualifiedType;
    }

    /**
     * Legt den Wert der qualifiedType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQualifiedType(String value) {
        this.qualifiedType = value;
    }

    /**
     * Ruft den Wert der serializable-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSerializable() {
        return serializable;
    }

    /**
     * Legt den Wert der serializable-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSerializable(String value) {
        this.serializable = value;
    }

    /**
     * Ruft den Wert der superclass-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSuperclass() {
        return superclass;
    }

    /**
     * Legt den Wert der superclass-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSuperclass(String value) {
        this.superclass = value;
    }

    /**
     * Ruft den Wert der superclassFullType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSuperclassFullType() {
        return superclassFullType;
    }

    /**
     * Legt den Wert der superclassFullType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSuperclassFullType(String value) {
        this.superclassFullType = value;
    }

    /**
     * Ruft den Wert der type-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Legt den Wert der type-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

}

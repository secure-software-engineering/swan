
package de.fraunhofer.iem.swan.doc.parser.ssldoclet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java-Klasse für ConstructorType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ConstructorType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="final" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="interface" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="line" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="modifier" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="signature" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="static" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="synchronized" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="synthetic" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="visibility" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConstructorType", propOrder = {
    "value"
})
public class ConstructorType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "final")
    protected String _final;
    @XmlAttribute(name = "interface")
    protected String _interface;
    @XmlAttribute(name = "line")
    protected String line;
    @XmlAttribute(name = "modifier")
    protected String modifier;
    @XmlAttribute(name = "signature")
    protected String signature;
    @XmlAttribute(name = "static")
    protected String _static;
    @XmlAttribute(name = "synchronized")
    protected String _synchronized;
    @XmlAttribute(name = "synthetic")
    protected String synthetic;
    @XmlAttribute(name = "visibility")
    protected String visibility;

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
     * Ruft den Wert der interface-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInterface() {
        return _interface;
    }

    /**
     * Legt den Wert der interface-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInterface(String value) {
        this._interface = value;
    }

    /**
     * Ruft den Wert der line-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLine() {
        return line;
    }

    /**
     * Legt den Wert der line-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLine(String value) {
        this.line = value;
    }

    /**
     * Ruft den Wert der modifier-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModifier() {
        return modifier;
    }

    /**
     * Legt den Wert der modifier-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModifier(String value) {
        this.modifier = value;
    }

    /**
     * Ruft den Wert der signature-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSignature() {
        return signature;
    }

    /**
     * Legt den Wert der signature-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSignature(String value) {
        this.signature = value;
    }

    /**
     * Ruft den Wert der static-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatic() {
        return _static;
    }

    /**
     * Legt den Wert der static-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatic(String value) {
        this._static = value;
    }

    /**
     * Ruft den Wert der synchronized-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSynchronized() {
        return _synchronized;
    }

    /**
     * Legt den Wert der synchronized-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSynchronized(String value) {
        this._synchronized = value;
    }

    /**
     * Ruft den Wert der synthetic-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSynthetic() {
        return synthetic;
    }

    /**
     * Legt den Wert der synthetic-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSynthetic(String value) {
        this.synthetic = value;
    }

    /**
     * Ruft den Wert der visibility-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVisibility() {
        return visibility;
    }

    /**
     * Legt den Wert der visibility-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVisibility(String value) {
        this.visibility = value;
    }

}

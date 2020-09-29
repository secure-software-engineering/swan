
package de.fraunhofer.iem.swan.doc.parser.ssldoclet;

import javax.xml.bind.annotation.*;


/**
 * <p>Java-Klasse für ssldocletType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ssldocletType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Package_Block" type="{}Package_BlockType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="creation" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="source" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "ssldoclet")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ssldocletType", propOrder = {
    "packageBlock"
})
public class SsldocletType {

    @XmlElement(name = "Package_Block", required = true)
    protected PackageBlockType packageBlock;
    @XmlAttribute(name = "creation")
    protected String creation;
    @XmlAttribute(name = "source")
    protected String source;
    @XmlAttribute(name = "version")
    protected String version;

    /**
     * Ruft den Wert der packageBlock-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PackageBlockType }
     *     
     */
    public PackageBlockType getPackageBlock() {
        return packageBlock;
    }

    /**
     * Legt den Wert der packageBlock-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PackageBlockType }
     *     
     */
    public void setPackageBlock(PackageBlockType value) {
        this.packageBlock = value;
    }

    /**
     * Ruft den Wert der creation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreation() {
        return creation;
    }

    /**
     * Legt den Wert der creation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreation(String value) {
        this.creation = value;
    }

    /**
     * Ruft den Wert der source-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSource() {
        return source;
    }

    /**
     * Legt den Wert der source-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSource(String value) {
        this.source = value;
    }

    /**
     * Ruft den Wert der version-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Legt den Wert der version-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }
}
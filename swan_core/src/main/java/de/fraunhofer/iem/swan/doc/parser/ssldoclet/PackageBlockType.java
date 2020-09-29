
package de.fraunhofer.iem.swan.doc.parser.ssldoclet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für Package_BlockType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="Package_BlockType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Package" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Class_Block" type="{}Class_BlockType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Package_BlockType", propOrder = {
    "_package",
    "classBlock"
})
public class PackageBlockType {

    @XmlElement(name = "Package", required = true)
    protected String _package;
    @XmlElement(name = "Class_Block", required = true)
    protected ClassBlockType classBlock;

    /**
     * Ruft den Wert der package-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPackage() {
        return _package;
    }

    public String getQualifiedName(){
        return _package +"."+classBlock.getClazz().getValue();
    }


    /**
     * Legt den Wert der package-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPackage(String value) {
        this._package = value;
    }

    /**
     * Ruft den Wert der classBlock-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ClassBlockType }
     *     
     */
    public ClassBlockType getClassBlock() {
        return classBlock;
    }

    /**
     * Legt den Wert der classBlock-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ClassBlockType }
     *     
     */
    public void setClassBlock(ClassBlockType value) {
        this.classBlock = value;
    }

    public String getFullyQualifiedClass(){
        return _package + getClassBlock().getClazz();
    }

}

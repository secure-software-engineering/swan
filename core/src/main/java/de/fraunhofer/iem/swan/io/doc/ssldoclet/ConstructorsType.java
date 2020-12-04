
package de.fraunhofer.iem.swan.io.doc.ssldoclet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ConstructorsType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ConstructorsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Constructor_Block" type="{}Constructor_BlockType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConstructorsType", propOrder = {
    "constructorBlock"
})
public class ConstructorsType {

    @XmlElement(name = "Constructor_Block", required = true)
    protected ConstructorBlockType constructorBlock;

    /**
     * Ruft den Wert der constructorBlock-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ConstructorBlockType }
     *     
     */
    public ConstructorBlockType getConstructorBlock() {
        return constructorBlock;
    }

    /**
     * Legt den Wert der constructorBlock-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ConstructorBlockType }
     *     
     */
    public void setConstructorBlock(ConstructorBlockType value) {
        this.constructorBlock = value;
    }

}

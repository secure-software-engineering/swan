
package de.fraunhofer.iem.swan.doc.parser.ssldoclet;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für MethodsType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="MethodsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Method_Block" type="{}Method_BlockType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MethodsType", propOrder = {
    "methodBlock"
})
public class MethodsType {

    @XmlElement(name = "Method_Block")
    protected List<MethodBlockType> methodBlock;

    /**
     * Gets the value of the methodBlock property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the methodBlock property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMethodBlock().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MethodBlockType }
     * 
     * 
     */
    public List<MethodBlockType> getMethodBlock() {
        if (methodBlock == null) {
            methodBlock = new ArrayList<MethodBlockType>();
        }
        return this.methodBlock;
    }

}

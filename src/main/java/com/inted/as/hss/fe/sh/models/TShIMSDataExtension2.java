//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.04.05 at 02:47:19 PM SAST 
//


package com.inted.as.hss.fe.sh.models;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tShIMSDataExtension2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tShIMSDataExtension2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DSAI" type="{}tDSAI" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Extension" type="{}tShIMSDataExtension3" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tShIMSDataExtension2", propOrder = {
    "dsai",
    "extension"
})
public class TShIMSDataExtension2 {

    @XmlElement(name = "DSAI")
    protected List<TDSAI> dsai;
    @XmlElement(name = "Extension")
    protected TShIMSDataExtension3 extension;

    /**
     * Gets the value of the dsai property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dsai property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDSAI().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TDSAI }
     * 
     * 
     */
    public List<TDSAI> getDSAI() {
        if (dsai == null) {
            dsai = new ArrayList<TDSAI>();
        }
        return this.dsai;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link TShIMSDataExtension3 }
     *     
     */
    public TShIMSDataExtension3 getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link TShIMSDataExtension3 }
     *     
     */
    public void setExtension(TShIMSDataExtension3 value) {
        this.extension = value;
    }

}
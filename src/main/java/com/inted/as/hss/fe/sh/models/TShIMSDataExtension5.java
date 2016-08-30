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
 * <p>Java class for tShIMSDataExtension5 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tShIMSDataExtension5">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ReferenceLocationInformation" type="{}tReferenceLocationInformation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Extension" type="{}tShIMSDataExtension6" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tShIMSDataExtension5", propOrder = {
    "referenceLocationInformation",
    "extension"
})
public class TShIMSDataExtension5 {

    @XmlElement(name = "ReferenceLocationInformation")
    protected List<TReferenceLocationInformation> referenceLocationInformation;
    @XmlElement(name = "Extension")
    protected TShIMSDataExtension6 extension;

    /**
     * Gets the value of the referenceLocationInformation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the referenceLocationInformation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReferenceLocationInformation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TReferenceLocationInformation }
     * 
     * 
     */
    public List<TReferenceLocationInformation> getReferenceLocationInformation() {
        if (referenceLocationInformation == null) {
            referenceLocationInformation = new ArrayList<TReferenceLocationInformation>();
        }
        return this.referenceLocationInformation;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link TShIMSDataExtension6 }
     *     
     */
    public TShIMSDataExtension6 getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link TShIMSDataExtension6 }
     *     
     */
    public void setExtension(TShIMSDataExtension6 value) {
        this.extension = value;
    }

}
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
 * <p>Java class for tShIMSDataExtension4 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tShIMSDataExtension4">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="STN-SR" type="{}tMSISDN" minOccurs="0"/>
 *         &lt;element name="UE-SRVCC-Capability" type="{}tUE-SRVCC-Capability" minOccurs="0"/>
 *         &lt;element name="ExtendedPriority" type="{}tExtendedPriority" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="CSRN" type="{}tMSISDN" minOccurs="0"/>
 *         &lt;element name="Extension" type="{}tShIMSDataExtension5" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tShIMSDataExtension4", propOrder = {
    "stnsr",
    "uesrvccCapability",
    "extendedPriority",
    "csrn",
    "extension"
})
public class TShIMSDataExtension4 {

    @XmlElement(name = "STN-SR")
    protected String stnsr;
    @XmlElement(name = "UE-SRVCC-Capability")
    protected Short uesrvccCapability;
    @XmlElement(name = "ExtendedPriority")
    protected List<TExtendedPriority> extendedPriority;
    @XmlElement(name = "CSRN")
    protected String csrn;
    @XmlElement(name = "Extension")
    protected TShIMSDataExtension5 extension;

    /**
     * Gets the value of the stnsr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSTNSR() {
        return stnsr;
    }

    /**
     * Sets the value of the stnsr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSTNSR(String value) {
        this.stnsr = value;
    }

    /**
     * Gets the value of the uesrvccCapability property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getUESRVCCCapability() {
        return uesrvccCapability;
    }

    /**
     * Sets the value of the uesrvccCapability property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setUESRVCCCapability(Short value) {
        this.uesrvccCapability = value;
    }

    /**
     * Gets the value of the extendedPriority property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the extendedPriority property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExtendedPriority().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TExtendedPriority }
     * 
     * 
     */
    public List<TExtendedPriority> getExtendedPriority() {
        if (extendedPriority == null) {
            extendedPriority = new ArrayList<TExtendedPriority>();
        }
        return this.extendedPriority;
    }

    /**
     * Gets the value of the csrn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCSRN() {
        return csrn;
    }

    /**
     * Sets the value of the csrn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCSRN(String value) {
        this.csrn = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link TShIMSDataExtension5 }
     *     
     */
    public TShIMSDataExtension5 getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link TShIMSDataExtension5 }
     *     
     */
    public void setExtension(TShIMSDataExtension5 value) {
        this.extension = value;
    }

}

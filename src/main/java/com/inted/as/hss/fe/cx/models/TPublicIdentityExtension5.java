//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.04.05 at 02:49:52 PM SAST 
//


package com.inted.as.hss.fe.cx.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tPublicIdentityExtension5 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tPublicIdentityExtension5">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MaxNumOfAllowedSimultRegistrations" type="{}tMaxNumOfAllowedSimultRegistrations" minOccurs="0"/>
 *         &lt;element name="Extension" type="{}tExtension" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPublicIdentityExtension5", propOrder = {
    "maxNumOfAllowedSimultRegistrations",
    "extension"
})
public class TPublicIdentityExtension5 {

    @XmlElement(name = "MaxNumOfAllowedSimultRegistrations")
    protected Integer maxNumOfAllowedSimultRegistrations;
    @XmlElement(name = "Extension")
    protected TExtension extension;

    /**
     * Gets the value of the maxNumOfAllowedSimultRegistrations property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxNumOfAllowedSimultRegistrations() {
        return maxNumOfAllowedSimultRegistrations;
    }

    /**
     * Sets the value of the maxNumOfAllowedSimultRegistrations property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxNumOfAllowedSimultRegistrations(Integer value) {
        this.maxNumOfAllowedSimultRegistrations = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link TExtension }
     *     
     */
    public TExtension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link TExtension }
     *     
     */
    public void setExtension(TExtension value) {
        this.extension = value;
    }

}

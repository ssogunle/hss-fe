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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tPublicIdentityExtension complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tPublicIdentityExtension">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IdentityType" type="{}tIdentityType" minOccurs="0"/>
 *         &lt;element name="WildcardedPSI" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="Extension" type="{}tPublicIdentityExtension2" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPublicIdentityExtension", propOrder = {
    "identityType",
    "wildcardedPSI",
    "extension"
})
public class TPublicIdentityExtension {

    @XmlElement(name = "IdentityType")
    protected Short identityType;
    @XmlElement(name = "WildcardedPSI")
    @XmlSchemaType(name = "anyURI")
    protected String wildcardedPSI;
    @XmlElement(name = "Extension")
    protected TPublicIdentityExtension2 extension;

    /**
     * Gets the value of the identityType property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getIdentityType() {
        return identityType;
    }

    /**
     * Sets the value of the identityType property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setIdentityType(Short value) {
        this.identityType = value;
    }

    /**
     * Gets the value of the wildcardedPSI property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWildcardedPSI() {
        return wildcardedPSI;
    }

    /**
     * Sets the value of the wildcardedPSI property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWildcardedPSI(String value) {
        this.wildcardedPSI = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link TPublicIdentityExtension2 }
     *     
     */
    public TPublicIdentityExtension2 getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link TPublicIdentityExtension2 }
     *     
     */
    public void setExtension(TPublicIdentityExtension2 value) {
        this.extension = value;
    }

}
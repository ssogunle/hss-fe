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
 * <p>Java class for tPublicIdentityExtension2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tPublicIdentityExtension2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DisplayName" type="{}tDisplayName" minOccurs="0"/>
 *         &lt;element name="AliasIdentityGroupID" type="{}tAliasIdentityGroupID" minOccurs="0"/>
 *         &lt;element name="Extension" type="{}tPublicIdentityExtension3" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPublicIdentityExtension2", propOrder = {
    "displayName",
    "aliasIdentityGroupID",
    "extension"
})
public class TPublicIdentityExtension2 {

    @XmlElement(name = "DisplayName")
    protected String displayName;
    @XmlElement(name = "AliasIdentityGroupID")
    protected String aliasIdentityGroupID;
    @XmlElement(name = "Extension")
    protected TPublicIdentityExtension3 extension;

    /**
     * Gets the value of the displayName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the value of the displayName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisplayName(String value) {
        this.displayName = value;
    }

    /**
     * Gets the value of the aliasIdentityGroupID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAliasIdentityGroupID() {
        return aliasIdentityGroupID;
    }

    /**
     * Sets the value of the aliasIdentityGroupID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAliasIdentityGroupID(String value) {
        this.aliasIdentityGroupID = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link TPublicIdentityExtension3 }
     *     
     */
    public TPublicIdentityExtension3 getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link TPublicIdentityExtension3 }
     *     
     */
    public void setExtension(TPublicIdentityExtension3 value) {
        this.extension = value;
    }

}

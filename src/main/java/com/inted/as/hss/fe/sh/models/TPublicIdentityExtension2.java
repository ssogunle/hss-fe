//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.04.05 at 02:47:19 PM SAST 
//


package com.inted.as.hss.fe.sh.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 *         &lt;element name="WildcardedIMPU" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
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
    "wildcardedIMPU",
    "extension"
})
public class TPublicIdentityExtension2 {

    @XmlElement(name = "WildcardedIMPU")
    @XmlSchemaType(name = "anyURI")
    protected String wildcardedIMPU;
    @XmlElement(name = "Extension")
    protected TPublicIdentityExtension3 extension;

    /**
     * Gets the value of the wildcardedIMPU property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWildcardedIMPU() {
        return wildcardedIMPU;
    }

    /**
     * Sets the value of the wildcardedIMPU property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWildcardedIMPU(String value) {
        this.wildcardedIMPU = value;
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

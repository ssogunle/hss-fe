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
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.w3c.dom.Element;


/**
 * <p>Java class for tSh-Data-Extension5 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tSh-Data-Extension5">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IMSI" type="{}tIMSI" minOccurs="0"/>
 *         &lt;element name="TWANLocationInformation" type="{}tTWANLocationInformation" minOccurs="0"/>
 *         &lt;element name="IMSPrivateUserIdentity" type="{}tIMSPrivateUserIdentity" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Extension" type="{}tExtension" minOccurs="0"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tSh-Data-Extension5", propOrder = {
    "imsi",
    "twanLocationInformation",
    "imsPrivateUserIdentity",
    "extension",
    "any"
})
public class TShDataExtension5 {

    @XmlElement(name = "IMSI")
    protected String imsi;
    @XmlElement(name = "TWANLocationInformation")
    protected TTWANLocationInformation twanLocationInformation;
    @XmlElement(name = "IMSPrivateUserIdentity")
    protected List<String> imsPrivateUserIdentity;
    @XmlElement(name = "Extension")
    protected TExtension extension;
    @XmlAnyElement(lax = true)
    protected List<Object> any;

    /**
     * Gets the value of the imsi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIMSI() {
        return imsi;
    }

    /**
     * Sets the value of the imsi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIMSI(String value) {
        this.imsi = value;
    }

    /**
     * Gets the value of the twanLocationInformation property.
     * 
     * @return
     *     possible object is
     *     {@link TTWANLocationInformation }
     *     
     */
    public TTWANLocationInformation getTWANLocationInformation() {
        return twanLocationInformation;
    }

    /**
     * Sets the value of the twanLocationInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link TTWANLocationInformation }
     *     
     */
    public void setTWANLocationInformation(TTWANLocationInformation value) {
        this.twanLocationInformation = value;
    }

    /**
     * Gets the value of the imsPrivateUserIdentity property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the imsPrivateUserIdentity property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIMSPrivateUserIdentity().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getIMSPrivateUserIdentity() {
        if (imsPrivateUserIdentity == null) {
            imsPrivateUserIdentity = new ArrayList<String>();
        }
        return this.imsPrivateUserIdentity;
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

    /**
     * Gets the value of the any property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * {@link Element }
     * 
     * 
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

}

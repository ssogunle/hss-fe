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
 * <p>Java class for tSh-Data-Extension4 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tSh-Data-Extension4">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EPSUserState" type="{}tPSUserState" minOccurs="0"/>
 *         &lt;element name="EPSLocationInformation" type="{}tEPSLocationInformation" minOccurs="0"/>
 *         &lt;element name="Extension" type="{}tSh-Data-Extension5" minOccurs="0"/>
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
@XmlType(name = "tSh-Data-Extension4", propOrder = {
    "epsUserState",
    "epsLocationInformation",
    "extension",
    "any"
})
public class TShDataExtension4 {

    @XmlElement(name = "EPSUserState")
    protected Short epsUserState;
    @XmlElement(name = "EPSLocationInformation")
    protected TEPSLocationInformation epsLocationInformation;
    @XmlElement(name = "Extension")
    protected TShDataExtension5 extension;
    @XmlAnyElement(lax = true)
    protected List<Object> any;

    /**
     * Gets the value of the epsUserState property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getEPSUserState() {
        return epsUserState;
    }

    /**
     * Sets the value of the epsUserState property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setEPSUserState(Short value) {
        this.epsUserState = value;
    }

    /**
     * Gets the value of the epsLocationInformation property.
     * 
     * @return
     *     possible object is
     *     {@link TEPSLocationInformation }
     *     
     */
    public TEPSLocationInformation getEPSLocationInformation() {
        return epsLocationInformation;
    }

    /**
     * Sets the value of the epsLocationInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link TEPSLocationInformation }
     *     
     */
    public void setEPSLocationInformation(TEPSLocationInformation value) {
        this.epsLocationInformation = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link TShDataExtension5 }
     *     
     */
    public TShDataExtension5 getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link TShDataExtension5 }
     *     
     */
    public void setExtension(TShDataExtension5 value) {
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
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.07.06 at 03:31:07 PM EST 
//


package au.com.sensis.mobile.crf.config.jaxb.generated;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 Imports a group(s) from other files. At least one of "name", "fromName"
 *                 or "fromConfigPath" must be set. 
 *             
 * 
 * <p>Java class for Import complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Import">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fromConfigPath" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fromName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Import")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-06T03:31:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class Import {

    @XmlAttribute(name = "name")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-06T03:31:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String name;
    @XmlAttribute(name = "fromConfigPath")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-06T03:31:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String fromConfigPath;
    @XmlAttribute(name = "fromName")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-06T03:31:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String fromName;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-06T03:31:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-06T03:31:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the fromConfigPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-06T03:31:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getFromConfigPath() {
        return fromConfigPath;
    }

    /**
     * Sets the value of the fromConfigPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-06T03:31:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setFromConfigPath(String value) {
        this.fromConfigPath = value;
    }

    /**
     * Gets the value of the fromName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-06T03:31:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getFromName() {
        return fromName;
    }

    /**
     * Sets the value of the fromName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-06T03:31:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setFromName(String value) {
        this.fromName = value;
    }

}

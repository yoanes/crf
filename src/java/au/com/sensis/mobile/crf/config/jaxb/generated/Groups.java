//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.02.07 at 11:34:03 AM EST 
//


package au.com.sensis.mobile.crf.config.jaxb.generated;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 Encapsulates a sequence of groups.
 *             
 * 
 * <p>Java class for Groups complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Groups">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="group" type="{http://mobile.sensis.com.au/web/crf/config}Group"/>
 *           &lt;element name="import" type="{http://mobile.sensis.com.au/web/crf/config}Import"/>
 *         &lt;/choice>
 *         &lt;element name="default-group" type="{http://mobile.sensis.com.au/web/crf/config}DefaultGroup"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Groups", propOrder = {
    "groupOrImport",
    "defaultGroup"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-02-07T11:34:03+11:00", comments = "JAXB RI v2.1.3-b01-fcs")
public class Groups {

    @XmlElements({
        @XmlElement(name = "group", type = Group.class),
        @XmlElement(name = "import", type = Import.class)
    })
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-02-07T11:34:03+11:00", comments = "JAXB RI v2.1.3-b01-fcs")
    protected List<Object> groupOrImport;
    @XmlElement(name = "default-group", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-02-07T11:34:03+11:00", comments = "JAXB RI v2.1.3-b01-fcs")
    protected DefaultGroup defaultGroup;

    /**
     * Gets the value of the groupOrImport property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the groupOrImport property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGroupOrImport().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Group }
     * {@link Import }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-02-07T11:34:03+11:00", comments = "JAXB RI v2.1.3-b01-fcs")
    public List<Object> getGroupOrImport() {
        if (groupOrImport == null) {
            groupOrImport = new ArrayList<Object>();
        }
        return this.groupOrImport;
    }

    /**
     * Gets the value of the defaultGroup property.
     * 
     * @return
     *     possible object is
     *     {@link DefaultGroup }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-02-07T11:34:03+11:00", comments = "JAXB RI v2.1.3-b01-fcs")
    public DefaultGroup getDefaultGroup() {
        return defaultGroup;
    }

    /**
     * Sets the value of the defaultGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link DefaultGroup }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-02-07T11:34:03+11:00", comments = "JAXB RI v2.1.3-b01-fcs")
    public void setDefaultGroup(DefaultGroup value) {
        this.defaultGroup = value;
    }

}

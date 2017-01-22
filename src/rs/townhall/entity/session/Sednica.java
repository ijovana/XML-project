//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5.1 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.06.14 at 11:36:19 PM CEST 
//


package rs.townhall.entity.session;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="glasanje" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="predmet_glasanja" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="broj_glasova_za" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                   &lt;element name="broj_glasova_protiv" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                   &lt;element name="broj_glasova_uzdrzani" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="stanje" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "glasanje",
    "stanje",
    "status"
})
@XmlRootElement(name = "sednica")
public class Sednica {

    @XmlElement(required = true)
    protected List<Sednica.Glasanje> glasanje;
    @XmlElement(required = true)
    protected String stanje;
    @XmlElement(required = true)
    protected String status;
    @XmlAttribute(name = "ID")
    protected String id;

    /**
     * Gets the value of the glasanje property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the glasanje property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGlasanje().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Sednica.Glasanje }
     * 
     * 
     */
    public List<Sednica.Glasanje> getGlasanje() {
        if (glasanje == null) {
            glasanje = new ArrayList<Sednica.Glasanje>();
        }
        return this.glasanje;
    }

    /**
     * Gets the value of the stanje property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStanje() {
        return stanje;
    }

    /**
     * Sets the value of the stanje property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStanje(String value) {
        this.stanje = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}



	/**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="predmet_glasanja" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="broj_glasova_za" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *         &lt;element name="broj_glasova_protiv" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *         &lt;element name="broj_glasova_uzdrzani" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "predmetGlasanja",
        "brojGlasovaZa",
        "brojGlasovaProtiv",
        "brojGlasovaUzdrzani"
    })
    public static class Glasanje {

        @XmlElement(name = "predmet_glasanja", required = true)
        protected String predmetGlasanja;
        @XmlElement(name = "broj_glasova_za")
        protected int brojGlasovaZa;
        @XmlElement(name = "broj_glasova_protiv")
        protected int brojGlasovaProtiv;
        @XmlElement(name = "broj_glasova_uzdrzani")
        protected int brojGlasovaUzdrzani;

        /**
         * Gets the value of the predmetGlasanja property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPredmetGlasanja() {
            return predmetGlasanja;
        }

        /**
         * Sets the value of the predmetGlasanja property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPredmetGlasanja(String value) {
            this.predmetGlasanja = value;
        }

        /**
         * Gets the value of the brojGlasovaZa property.
         * 
         */
        public int getBrojGlasovaZa() {
            return brojGlasovaZa;
        }

        /**
         * Sets the value of the brojGlasovaZa property.
         * 
         */
        public void setBrojGlasovaZa(int value) {
            this.brojGlasovaZa = value;
        }

        /**
         * Gets the value of the brojGlasovaProtiv property.
         * 
         */
        public int getBrojGlasovaProtiv() {
            return brojGlasovaProtiv;
        }

        /**
         * Sets the value of the brojGlasovaProtiv property.
         * 
         */
        public void setBrojGlasovaProtiv(int value) {
            this.brojGlasovaProtiv = value;
        }

        /**
         * Gets the value of the brojGlasovaUzdrzani property.
         * 
         */
        public int getBrojGlasovaUzdrzani() {
            return brojGlasovaUzdrzani;
        }

        /**
         * Sets the value of the brojGlasovaUzdrzani property.
         * 
         */
        public void setBrojGlasovaUzdrzani(int value) {
            this.brojGlasovaUzdrzani = value;
        }

    }

}

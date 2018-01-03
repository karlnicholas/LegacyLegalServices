
package statutes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for findStatutesResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="findStatutesResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="statuteKey" type="{http://statutes}statuteKey" minOccurs="0"/&gt;
 *         &lt;element name="statutesBaseClass" type="{http://statutes}statutesBaseClass" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "findStatutesResponse", propOrder = {
    "statuteKey",
    "statutesBaseClass"
})
public class FindStatutesResponse {

    protected StatuteKey statuteKey;
    protected StatutesBaseClass statutesBaseClass;

    /**
     * Gets the value of the statuteKey property.
     * 
     * @return
     *     possible object is
     *     {@link StatuteKey }
     *     
     */
    public StatuteKey getStatuteKey() {
        return statuteKey;
    }

    /**
     * Sets the value of the statuteKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatuteKey }
     *     
     */
    public void setStatuteKey(StatuteKey value) {
        this.statuteKey = value;
    }

    /**
     * Gets the value of the statutesBaseClass property.
     * 
     * @return
     *     possible object is
     *     {@link StatutesBaseClass }
     *     
     */
    public StatutesBaseClass getStatutesBaseClass() {
        return statutesBaseClass;
    }

    /**
     * Sets the value of the statutesBaseClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatutesBaseClass }
     *     
     */
    public void setStatutesBaseClass(StatutesBaseClass value) {
        this.statutesBaseClass = value;
    }

}

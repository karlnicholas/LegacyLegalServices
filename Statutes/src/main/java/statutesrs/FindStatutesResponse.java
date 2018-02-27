
package statutesrs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import statutes.StatuteKey;
import statutes.StatutesBaseClass;


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

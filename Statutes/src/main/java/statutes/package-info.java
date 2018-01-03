/**
 * Structure for handling Statutes.
 * 
 * @since 1.0
 * @author Karl Nicholas
 * @version 1.0
 *
 */
@XmlSchema(
    namespace = "http://statutes",
    elementFormDefault = XmlNsForm.QUALIFIED,
    xmlns = {
        @XmlNs(prefix="statutes", namespaceURI="http://statutes")
    }
) 
package statutes;

import javax.xml.bind.annotation.*;

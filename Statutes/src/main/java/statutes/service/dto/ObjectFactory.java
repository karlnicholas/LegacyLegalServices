//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.11.27 at 02:15:47 PM PST 
//


package statutes.service.dto;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the statutesws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: statutesws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link StatuteKey }
     * @return {@link StatuteKey} statuteKey
     */
    public StatuteKey createStatuteKey() {
        return new StatuteKey();
    }

    /**
     * Create an instance of {@link StatuteKeyArray }
     * @return {@link StatuteKeyArray} 
     */
    public StatuteKeyArray createStatuteKeyArray() {
        return new StatuteKeyArray();
    }

    /**
     * Create an instance of {@link KeyHierarchyPairs }
     * @return {@link KeyHierarchyPairs} 
     */
    public KeyHierarchyPairs createResponseArray() {
        return new KeyHierarchyPairs();
    }

    /**
     * Create an instance of {@link KeyHierarchyPair }
     * @return {@link KeyHierarchyPair} 
     */
    public KeyHierarchyPair createResponsePair() {
        return new KeyHierarchyPair();
    }

    /**
     * Create an instance of {@link StatutesTitlesArray }
     * @return {@link StatutesTitlesArray} 
     */
    public StatutesTitlesArray createStatutesTitlesArray() {
        return new StatutesTitlesArray();
    }

}

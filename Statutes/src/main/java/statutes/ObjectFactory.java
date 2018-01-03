
package statutes;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the statutes package. 
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

    private final static QName _StatutesBaseClass_QNAME = new QName("http://statutes", "statutesBaseClass");
    private final static QName _StatutesLeaf_QNAME = new QName("http://statutes", "statutesLeaf");
    private final static QName _StatutesNode_QNAME = new QName("http://statutes", "statutesNode");
    private final static QName _StatutesRoot_QNAME = new QName("http://statutes", "statutesRoot");
    private final static QName _StatutesTitles_QNAME = new QName("http://statutes", "statutesTitles");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: statutes
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link StatutesLeaf }
     * @return StatutesLeaf 
     */
    public StatutesLeaf createStatutesLeaf() {
        return new StatutesLeaf();
    }

    /**
     * Create an instance of {@link StatutesNode }
     * @return StatutesNode
     */
    public StatutesNode createStatutesNode() {
        return new StatutesNode();
    }

    /**
     * Create an instance of {@link StatutesRoot }
     * @return StatutesRoot
     */
    public StatutesRoot createStatutesRoot() {
        return new StatutesRoot();
    }

    /**
     * Create an instance of {@link StatutesTitles }
     * @return StatutesTitles
     */
    public StatutesTitles createStatutesTitles() {
        return new StatutesTitles();
    }

    /**
     * Create an instance of {@link StatuteRange }
     * @return StatuteRange
     */
    public StatuteRange createStatuteRange() {
        return new StatuteRange();
    }

    /**
     * Create an instance of {@link SectionNumber }
     * @return SectionNumber
     */
    public SectionNumber createSectionNumber() {
        return new SectionNumber();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * @param value Object 
     * @return {@link JAXBElement }{@code <}{@link Object }{@code >}}
     */
    @XmlElementDecl(namespace = "http://statutes", name = "statutesBaseClass")
    public JAXBElement<Object> createStatutesBaseClass(Object value) {
        return new JAXBElement<Object>(_StatutesBaseClass_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StatutesLeaf }{@code >}}
     * @param value StatutesLeaf 
     * @return {@link JAXBElement }{@code <}{@link StatutesLeaf }{@code >}}
     */
    @XmlElementDecl(namespace = "http://statutes", name = "statutesLeaf")
    public JAXBElement<StatutesLeaf> createStatutesLeaf(StatutesLeaf value) {
        return new JAXBElement<StatutesLeaf>(_StatutesLeaf_QNAME, StatutesLeaf.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StatutesNode }{@code >}}
     * @param value StatutesNode 
     * @return {@link JAXBElement }{@code <}{@link StatutesNode }{@code >}}
     */
    @XmlElementDecl(namespace = "http://statutes", name = "statutesNode")
    public JAXBElement<StatutesNode> createStatutesNode(StatutesNode value) {
        return new JAXBElement<StatutesNode>(_StatutesNode_QNAME, StatutesNode.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StatutesRoot }{@code >}}
     * @param value StatutesRoot 
     * @return {@link JAXBElement }{@code <}{@link StatutesRoot }{@code >}}
     */
    @XmlElementDecl(namespace = "http://statutes", name = "statutesRoot")
    public JAXBElement<StatutesRoot> createStatutesRoot(StatutesRoot value) {
        return new JAXBElement<StatutesRoot>(_StatutesRoot_QNAME, StatutesRoot.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StatutesTitles }{@code >}}
     * @param value StatutesTitles
     * @return {@link JAXBElement }{@code <}{@link StatutesTitles }{@code >}}
     */
    @XmlElementDecl(namespace = "http://statutes", name = "statutesTitles")
    public JAXBElement<StatutesTitles> createStatutesTitles(StatutesTitles value) {
        return new JAXBElement<StatutesTitles>(_StatutesTitles_QNAME, StatutesTitles.class, null, value);
    }

}

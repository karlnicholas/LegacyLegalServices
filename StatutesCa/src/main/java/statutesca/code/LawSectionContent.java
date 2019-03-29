package statutesca.code;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.w3c.dom.Element;

@XmlRootElement(name="Content",  namespace="http://lc.ca.gov/legalservices/schemas/caml.1#")
public class LawSectionContent {
	private Element content;

	@XmlAnyElement
	public Element getContent() {
		return content;
	}

	public void setContent(Element content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "LawSectionContent [content=" + content + "]";
	}

}

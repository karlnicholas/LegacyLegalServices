package statutesca.util;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import statutes.StatutesRoot;
import statutes.api.IStatutesApi;
import statutesca.statutesapi.CAStatutesApiImpl;

public class WriteStatutesXML {

	public static void main(String[] args) {
		IStatutesApi codes = new CAStatutesApiImpl();
		codes.loadStatutes();

		System.out.println(codes.getStatutesTitles().length);

		StatutesRoot code = codes.getStatutes().get(0);

		try {

			JAXBContext jaxbContext = JAXBContext.newInstance("statutes");
			// class responsible for the process of
			// serializing Java object into XML data
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// marshalled XML data is formatted with linefeeds and indentation
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			// specify the xsi:schemaLocation attribute value
			// to place in the marshalled XML output
			// jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
			// "http://www.mysamplecode.com/ws/v10 OrderService_v10.xsd");
			// try {
			// override for custom namespace prefix
			// jaxbMarshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper",
			// new MyNamespaceMapper());
			// } catch(PropertyException e) {
			// System.out.println(e);
			// }

			// send to console
			jaxbMarshaller.marshal(code, System.out);
			// send to file system
//			OutputStream os = new FileOutputStream("data/MyOrder.xml");
//			jaxbMarshaller.marshal(code, os);

		} catch (JAXBException e) {
			e.printStackTrace();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
		}

	}

}

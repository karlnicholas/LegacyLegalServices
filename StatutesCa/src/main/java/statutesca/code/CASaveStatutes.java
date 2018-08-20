package statutesca.code;

import java.io.BufferedWriter;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

import statutes.IteratorHandler;
import statutes.SectionNumber;
import statutes.StatuteRange;
import statutes.StatutesLeaf;
import statutes.StatutesRoot;
import statutes.api.IStatutesApi;
import statutesca.statutesapi.CAStatutesApiImpl;

public class CASaveStatutes {
	private static final Logger logger = Logger.getLogger(CASaveStatutes.class.getName());
	private static final String DEBUGFILE = null;	//"fam";
//	private static long globalsectioncount = 0;

    private int position;

	public List<Path> createSerializedStatutes(Path codesDir, Path xmlcodes) throws Exception {
		List<Path> outputFiles = new ArrayList<Path>();

		// purposely don't call loadStatutes because we are getting them from the raw files
		IStatutesApi iStatutesApi = new CAStatutesApiImpl();
		

		List<Path> files = new ArrayList<Path>();
	    try (DirectoryStream<Path> stream = Files.newDirectoryStream(codesDir)) {
	        for (Path entry : stream) {
	            if (Files.isDirectory(entry)) 
	            	continue;
				if (entry.getFileName().toString().contains("constitution")) 
					continue;
				if ( DEBUGFILE != null ) { 
					if (!entry.getFileName().toString().contains(DEBUGFILE)) 
						continue;
				}
            	files.add(entry);
	        }
	    }
		
		for ( Path file: files ) {
			logger.info( "Processing " + file);
			outputFiles.add(processFile(iStatutesApi, file, xmlcodes));
		}
		return outputFiles;
	}
		
	private Path processFile(IStatutesApi iStatutesApi, Path file, Path xmlcodes) throws Exception {
		StatutesParser parser = new StatutesParser();
		StatutesRoot c = parser.parse(iStatutesApi, StandardCharsets.ISO_8859_1, file);

/*
// debug code
		StatutesLeaf section = c.findReference( new SectionNumber("469") ).returnSection();
		File codeDetail = new File("c:/users/karln/code/pen/02001-03000/2635-2643");
		SectionParser.parseSectionFile(codeDetail, section);
// end debug code
*/

		IteratorHandler myHandler = new MyIteratorHandler(file);
		c.iterateLeafs(myHandler);

		Path outputFile = Paths.get(xmlcodes.toString(),"\\",c.getTitle(false)+".ser");
		OutputStream os = Files.newOutputStream(outputFile);
		ObjectOutputStream oos = new ObjectOutputStream(os);

		oos.writeObject(c);
		
		oos.close();

		return outputFile;
		// System.out.println(c.getTitle());
	}

	public class MyIteratorHandler extends IteratorHandler {
		private String abvr;
		private String inpath;
		private Charset encoding;
		
		public MyIteratorHandler(Path file) {
			position = 1;
			abvr = file.getFileName().toString().substring(0, file.getFileName().toString().indexOf('_'));
			inpath = file.getParent().toString();
			encoding = StandardCharsets.ISO_8859_1;
		}
		
		public boolean handleSection(StatutesLeaf section) throws Exception {
//			SectionRange range = section.getSectionRange();
//			if (range != null) {
			StatuteRange range = section.getStatuteRange();
			if (range != null) {
				String strRange = range.getsNumber().getSectionNumber();
				String firstInt = new String();
				for (int i = 0, il = strRange.length(); i < il; ++i) {
					char ch = strRange.charAt(i);
					if (Character.isDigit(ch)) {
						firstInt = firstInt.concat("" + ch);
					} else {
						break;
					}
				}
				int num = Integer.parseInt(firstInt);
				num = ((num - 1) / 1000) * 1000;
				String subdir = String.format("%05d-%05d", num + 1, num + 1000);
				if ( range.geteNumber() != null && range.geteNumber().getSectionNumber() != null ) strRange = strRange + "-" + range.geteNumber();
				String strPath = new String(inpath + "/" + abvr + "/" + subdir + "/" + strRange);
				Path codeDetail = Paths.get(strPath);
				ArrayList<String> sections = SectionParser.parseSectionFile(encoding, codeDetail, section);
				// update CodeRange positions
				range.getsNumber().setPosition(position);
				appendParagraph(sections, section);
				// update CodeRange positions
				range.geteNumber().setPosition(position-1);			
			}
			return true;
		}
	}

	private void appendParagraph(
			ArrayList<String> sections,
			StatutesLeaf section 
		) {
//			System.out.println(sections.size());

			for ( String sect: sections ) {
//				int slen = sect.length();
//				System.out.println(sect.substring(0, slen>20?20:slen ) + " ...");
//				Element eParagraph = xmlDoc.createElement(CodeReference.SECTIONTEXT);
//				Really, the only difference is that we don't actually put any text into the XML 
//				eParagraph.appendChild(xmlDoc.createCDATASection(sect)); 
				
				SectionNumber PNumber = SectionParser.getSectionNumber(position, sect);
//				eParagraph.setAttribute(CodeReference.SECTIONNUMBER, PNumber.toString());
//				eParagraph.setAttribute(CodeReference.POSITION, Integer.toString(position));
				section.getSectionNumbers().add(PNumber);
				position++;
				// System.out.println( sect.substring(0, 20) + " ...");
//				eSection.appendChild(eParagraph);

//				globalsectioncount++;
			}

//			return eSection;

		}
	
	/*
	 * Reqired to run this to create xml files in the resources folder that describe the code hierarchy 
	 */
	public static void main(String... args) throws Exception {
		
		final class Run {
			public void run() throws Exception {

				Path codesDir = Paths.get("c:/users/karln/code");

				Path xmlcodes = Paths.get("c:/users/karln/opcastorage/CaliforniaStatutes");
				
				List<Path> files = new CASaveStatutes().createSerializedStatutes(codesDir, xmlcodes );
				
				Path filePath = Paths.get(xmlcodes.toString(), "files" );
				BufferedWriter bw = Files.newBufferedWriter(filePath, StandardCharsets.US_ASCII);
				for(Path file: files) {
					bw.write(file.getFileName().toString());
					bw.newLine();
				}
				bw.close();
			}
		}

		Run run = new Run();
		run.run();
	}

}

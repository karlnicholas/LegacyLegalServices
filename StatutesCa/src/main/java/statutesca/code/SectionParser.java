package statutesca.code;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import statutes.SectionNumber;
import statutes.StatutesBaseClass;

public class SectionParser {

	
	// replace returns with returns and don't
	// trim the lines so that we can preserve the formatting found on the FTP site
	// quad CRLF combinations should mean end of paragraph, 
	// so, once we start a paragraph, then we need to continue until we get to a double CRLF
	// and then it might be the end of a paragraph.
//	public static ArrayList<String> parseParagraphFile(Path codeDetail, Section section ) throws Exception {
	public static ArrayList<String> parseSectionFile(Charset encoding, Path codeDetail, StatutesBaseClass reference ) throws IOException {
//		final String newLine = "\n";
		char[] cb = new char[(int) Files.size(codeDetail)];
		
		BufferedReader reader = Files.newBufferedReader(codeDetail, encoding);

		try {
			reader.read(cb);
// .
//			String[] strSections = new String(cb).split("\r\n\r\n\r\n");
			String[] strSections = new String(cb).replace("\r\n", "\n").split("\n\n\n");
			ArrayList<String> sections = new ArrayList<String>();
			for ( int i=1; i<strSections.length; ++i ) {
				String section = strSections[i].trim();
				if ( getSectionNumber(0, section) != null ) {
					sections.add( section );
				} else {
					int end = sections.size()-1;
					sections.set(end, sections.get(end).concat(section) );
				}
			}
			return sections;
		} finally {
			reader.close();
		}
	}

	public static SectionNumber getSectionNumber(int position, String sect) {
		// System.out.println(sect);
		// test the first character .. s
		if ( sect.isEmpty() ) return null;
		int sloc = 0;
//		System.out.println( Character .getNumericValue(§) );
		// first character could be a [  - with NNN and ].
		if ( sect.charAt(0) == '[' ) {
			sloc = 1;
		} else { 
			if ( sect.charAt(0) != '§' ) {
				if (!Character.isDigit(sect.charAt(0)))
					return null;
			} else {
				sloc = 1;
			}
		}
		int spaceLoc = sect.indexOf(' ');
		int crLoc = sect.indexOf('\r');
		if ( crLoc != -1 && crLoc < spaceLoc ) spaceLoc = crLoc;
		if (spaceLoc == -1)
			return null;
		if (spaceLoc > 20)
			return null;
		if ( sect.charAt(spaceLoc-1) == ']' ) spaceLoc--;
		String sNum = sect.substring(sloc, spaceLoc-1);
		try {
			return new SectionNumber(position, sNum);
		} catch (Exception ex) {
			return null;
		}
	}
}

package statutesca.code;

import java.io.BufferedWriter;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;

import javax.xml.bind.JAXBException;

import statutes.SectionNumber;
import statutes.StatuteRange;
import statutes.StatutesLeaf;
import statutes.StatutesRoot;

public class CASaveStatutesFromDb extends CAProcessDb {
	private final Path lawCodesPath;
	private int position;
	

	/*
	 * Reqired to run this to create xml files in the resources folder that describe
	 * the code hierarchy
	 */
	public static void main(String... args) throws Exception {
		new CASaveStatutesFromDb().run();
	}

	public CASaveStatutesFromDb() throws SQLException, JAXBException {
		super();
		lawCodesPath = Paths.get("c:/users/karln/opcastorage/CaliforniaStatutes");
		position = 1;
	}

	protected void run() throws Exception {
		List<Path> filePaths = new ArrayList<>();
		List<LawCode> lawCodes = retrieveLawCodes();
		for (LawCode lawCode : lawCodes) {
			if (lawCode.getCode().equals("CONS")) {
				continue;
			}
			try {
				StatutesRoot statutesRoot = parseLawCode(lawCode.getCode(), this::processStatutesLeaf);
				filePaths.add(processFile(statutesRoot));
			} catch (Exception ex) {
				System.out.println(lawCode.getCode());
				throw ex;
			}
		}
		Path filePath = Paths.get(lawCodesPath.toString(), "files");
		BufferedWriter bw = Files.newBufferedWriter(filePath, StandardCharsets.US_ASCII);
		for (Path file : filePaths) {
			bw.write(file.getFileName().toString());
			bw.newLine();
		}
		bw.close();
	}

	private void processStatutesLeaf(LawForCodeSections lawForCodeSections, StatutesLeaf statutesLeaf) {
		ArrayList<SectionNumber> sectionNumbers = statutesLeaf.getSectionNumbers();
		String firstSectionNum = null;
		String lastSectionNum = null;
		for (LawSection lawSection : lawForCodeSections.getSections()) {
			if ( firstSectionNum == null )
				firstSectionNum = lawSection.getSection_num();
			lastSectionNum = lawSection.getSection_num();
			if ( lawSection.getSection_num() != null ) {
				sectionNumbers.add(new SectionNumber(position++, 
					lawSection.getSection_num().substring(0, lawSection.getSection_num().length()-1)));
			}
		}
		if ( sectionNumbers.size() > 0 ) {
			statutesLeaf.setStatuteRange(new StatuteRange(
				sectionNumbers.get(0), 
				sectionNumbers.get(sectionNumbers.size()-1)
			));
		}
		// TODO: PUT WHERE CAN HANDLE STATUTE NODES AS WELL
		statutesLeaf.setTitle(statutesLeaf.getTitle().replace('[' + firstSectionNum + " - " + lastSectionNum + ']' , "").trim());
	}

	Path processFile(StatutesRoot statutesRoot) throws Exception {

		Path outputFile = Paths.get(lawCodesPath.toString(), "\\", statutesRoot.getTitle(false) + ".ser");
		OutputStream os = Files.newOutputStream(outputFile);
		ObjectOutputStream oos = new ObjectOutputStream(os);

		oos.writeObject(statutesRoot);

		oos.close();

		return outputFile;
		// System.out.println(c.getTitle());
	}

}

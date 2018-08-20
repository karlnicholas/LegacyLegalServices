package gsearch.load;


import java.util.ArrayList;

import statutes.StatutesBaseClass;

/*
 * I need to save the title, the categorypath, the full path for baseClass, the text, the part and partnumber
 * and of course the section and sectionParagraph if it exists
 */
public class LuceneCodeModel {
	private StatutesBaseClass baseClass;
	private ArrayList<LuceneSectionModel> sections;
	
	public LuceneCodeModel( StatutesBaseClass baseClass ) {
		this.baseClass = baseClass;
		sections = new ArrayList<LuceneSectionModel>();
	}
	
	public StatutesBaseClass getReference() {
		return baseClass;
	}

	public ArrayList<LuceneSectionModel> getSections() {
		return sections;
	}

}

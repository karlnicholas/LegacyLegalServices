package opca.scraper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFootnote;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import opca.model.SlipOpinion;
import opca.parser.ScrapedOpinionDocument;

public class CAParseScrapedDocument {

	public ScrapedOpinionDocument parseScrapedDocument(SlipOpinion slipOpinion, InputStream inputStream) throws IOException {
		ScrapedOpinionDocument scrapedDocument = new ScrapedOpinionDocument(slipOpinion);
		if ( slipOpinion.getFileExtension().equals(".DOC")) {
			HWPFDocument document = new HWPFDocument(inputStream);
			WordExtractor extractor = new WordExtractor(document);
	        scrapedDocument.getParagraphs().addAll(Arrays.asList(extractor.getParagraphText()) ); 
	        scrapedDocument.getFootnotes().addAll(Arrays.asList(extractor.getFootnoteText()) );
	        extractor.close();
		} else if ( slipOpinion.getFileExtension().equals(".DOCX")) {
			try ( XWPFDocument document = new XWPFDocument(inputStream) ) {
				Iterator<XWPFParagraph> pIter = document.getParagraphsIterator();
				while ( pIter.hasNext() ) {
					XWPFParagraph paragraph = pIter.next();
			        scrapedDocument.getParagraphs().add(paragraph.getParagraphText() ); 
				}
				for ( XWPFFootnote footnote: document.getFootnotes() ) {
					for ( XWPFParagraph p: footnote.getParagraphs()  ) {
				        scrapedDocument.getFootnotes().add(p.getText());
					}
				}
				document.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			throw new IllegalArgumentException("Unknown File Type: " + slipOpinion);
		}
        return scrapedDocument;
	}
}

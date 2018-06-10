package opca.scraper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
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
		// read doc into memory
		int count = 0;
		int total = 50000;
		ByteBuffer bb = ByteBuffer.allocate(50000);
		byte[] bytes = new byte[8196];
		int l;
		while ( (l = inputStream.read(bytes)) != -1 ) {
			for ( int i = 0; i < l; ++i ) {
				if ( ++count > total ) {
					ByteBuffer tb = ByteBuffer.allocate(total + 50000);
					bb = tb.put(bb.array());
					total += 50000;
				}
				bb.put(bytes[i]);
			}
		}
		inputStream.close();
		ByteArrayInputStream bais = new ByteArrayInputStream(bb.array());
		bais.mark(bb.position());		
		if ( slipOpinion.getFileExtension().equals(".DOC")) {
			try {
				parseHWPF(scrapedDocument, slipOpinion, bais);
			} catch ( IllegalArgumentException ex ) {
				bais.reset();
				parseXWPF(scrapedDocument, slipOpinion, bais);
			}
		} else if ( slipOpinion.getFileExtension().equals(".DOCX")) {
			parseXWPF(scrapedDocument, slipOpinion, bais);
		} else {
			bais.close();
			throw new IllegalArgumentException("Unknown File Type: " + slipOpinion);
		}
		bais.close();
        return scrapedDocument;
	}
	private void parseHWPF(ScrapedOpinionDocument scrapedDocument, SlipOpinion slipOpinion, InputStream inputStream) throws IOException {
		HWPFDocument document = new HWPFDocument(inputStream);
		WordExtractor extractor = new WordExtractor(document);
        scrapedDocument.getParagraphs().addAll(Arrays.asList(extractor.getParagraphText()) ); 
        scrapedDocument.getFootnotes().addAll(Arrays.asList(extractor.getFootnoteText()) );
        extractor.close();
		
	}
	private void parseXWPF(ScrapedOpinionDocument scrapedDocument, SlipOpinion slipOpinion, InputStream inputStream) {	
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
	}
	
}

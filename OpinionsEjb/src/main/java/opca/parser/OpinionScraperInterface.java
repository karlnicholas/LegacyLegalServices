package opca.parser;

import java.util.List;

import opca.model.SlipOpinion;

public interface OpinionScraperInterface {
	List<SlipOpinion> getCaseList();
	List<ScrapedOpinionDocument> scrapeOpinionFiles(List<SlipOpinion> slipOpinions);
}

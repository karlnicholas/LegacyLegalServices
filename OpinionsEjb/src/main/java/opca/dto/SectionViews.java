package opca.dto;

import java.util.ArrayList;
import java.util.List;

import opca.view.IterateSectionsHandler;
import opca.view.StatuteView;

public class SectionViews {
	private final List<SectionView> sections;
	public SectionViews(opca.view.OpinionView opinionView) {
		sections = new ArrayList<SectionView>(); 
    	for ( StatuteView statuteView: opinionView.getStatutes() ) {
    		statuteView.iterateSections(new IterateSectionsHandler() {
				@Override
				public boolean handleOpinionSection(opca.view.SectionView section) {
					sections.add(new SectionView(section));
					return true;
				}
    		});
    	}		
	}
	public List<SectionView> getSections() {
		return sections;
	}

}

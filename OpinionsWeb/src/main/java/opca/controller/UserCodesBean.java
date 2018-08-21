package opca.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import opca.ejb.util.StatutesServiceFactory;
import statutes.StatutesTitles;
import statutes.service.StatutesService;

/**
 * Simply provide the statutes to be combined with the user preferences.
 *  
 * @author Karl Nicholas
 * @version 2017-01-27
 *
 */
@ManagedBean
@ApplicationScoped
public class UserCodesBean {
    private List<List<StatutesTitles>> titleMatrix;
    private String[] titleArray;
	
	@PostConstruct
	public void postConstruct() {
		StatutesService statutesService = StatutesServiceFactory.getInstance().getStatutesServiceClient();
		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<StatutesTitles> statutesTitles = (List)statutesService.getStatutesTitles().getItem();

		Collections.sort(statutesTitles, new Comparator<StatutesTitles>() {
			@Override
			public int compare(StatutesTitles o1, StatutesTitles o2) {
				return o1.getShortTitle().compareTo(o2.getShortTitle());
			}
		});
		
		titleMatrix = new ArrayList<List<StatutesTitles>>();
		titleArray = new String[statutesTitles.size()];
		List<StatutesTitles> widths = new ArrayList<StatutesTitles>();
		for (int i=0; i<statutesTitles.size(); i++) {
			titleArray[i] = statutesTitles.get(i).getShortTitle(); 
			widths.add(statutesTitles.get(i));
			if ( (i+1) % 3 == 0 ) { 
				titleMatrix.add( widths );
				widths = new ArrayList<StatutesTitles>();
			}
		}
		if ( widths.size() > 0 ) titleMatrix.add( widths );		
	}

	public List<List<StatutesTitles>> getTitleMatrix() {
		return titleMatrix;
	}
	public String[] getTitleArray() {
		return titleArray;
	}

}

package opca.controller;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.event.PhaseEvent;
import javax.inject.Inject;

import opca.scraper.CACaseScraper;
import opca.service.CAOnlineUpdates;
import opca.service.SystemService;
import service.StatutesService;
import statutes.StatutesTitles;

@ManagedBean
public class IndexController {
	@EJB private SystemService systemService;
    private String userCountMessage;
	@Inject Logger logger;
	@EJB private CAOnlineUpdates caOnlineUpdates;
    
    public void testNothing() {
        logger.info("Test Nothing");
    }

    public void testUpdate() {
        logger.info("Starting Scraper Update");
        caOnlineUpdates.updateOpinionViews(caOnlineUpdates.updateDatabase(new CACaseScraper(false)));
        logger.info("Done Update");
    }
    
    public void testWelcome() {
    	systemService.doWelcomeService();
    }

    public void testOpinionReport() {
    	systemService.sendOpinionReports();
    }

    private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String displayText(){
		return "";
	}

	public void beforePhaseListener(PhaseEvent e){
		System.out.println("Before:" + e.getPhaseId().getName());
	}
	public void afterPhaseListener(PhaseEvent e){
		System.out.println("After:" + e.getPhaseId().getName());
	}

    /**
     * UserCountMessage field
     * @return userCountMessage
     */
    public String getUserCountMessage() {
        return userCountMessage;
    }

    /**
     * UserCountMessage Field
     * @param userCountMessage to set.
     */
    public void setUserCountMessage(String userCountMessage) {
        this.userCountMessage = userCountMessage;
    }
}

package opca.controller;

import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.event.PhaseEvent;
import javax.inject.Inject;

import opca.scraper.CACaseScraper;
import opca.scraper.TestCACaseScraper;
import opca.service.CAOnlineUpdates;
import opca.service.SystemService;

@ManagedBean
public class IndexController {
	@Inject private SystemService systemService;
    private String userCountMessage;
	@Inject Logger logger;
	@Inject private CAOnlineUpdates caOnlineUpdates;
    
    public void testUpdate() {
        logger.info("Starting Scraper Update");
        caOnlineUpdates.updateDatabase(new CACaseScraper(false));
//        caOnlineUpdates.updateDatabase(new TestCACaseScraper(false));
		logger.info("Starting OPINIONVIEW POSTCONSTRUCT");
        caOnlineUpdates.updatePostConstruct();
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

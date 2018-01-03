package opca.controller;

import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.event.PhaseEvent;
import javax.inject.Inject;

import opca.scraper.CACaseScraper;
import opca.scraper.TestCACaseScraper;
import opca.service.CAOnlineUpdates;
import opca.service.UserService;

@ManagedBean
public class IndexController {
	@Inject private UserService userService;
    private String userCountMessage;
	@Inject Logger logger;
	@Inject private CAOnlineUpdates caOnlineUpdates;
    
    public void testUpdate() {
        logger.info("STARTING SCRAPER UPDATE");
//        caOnlineUpdates.updateDatabase(new CACaseScraper(false));
        caOnlineUpdates.updateDatabase(new TestCACaseScraper(false));

        logger.info("DONE SCRAPER UPDATE");
		logger.info("STARTING OPINIONVIEW POSTCONSTRUCT");
        caOnlineUpdates.updatePostConstruct();
        logger.info("DONE OPINIONVIEW POSTCONSTRUCT");
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
     * Set UserCountMessage field with the number of registered users
     */
    public void updateUserCount() {
        userCountMessage = String.format("There are %d users", userService.userCount() );
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

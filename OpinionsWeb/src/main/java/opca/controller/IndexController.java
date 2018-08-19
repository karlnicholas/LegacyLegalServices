package opca.controller;

import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.event.PhaseEvent;
import javax.inject.Inject;

import opca.service.ScheduledService;

@ManagedBean
public class IndexController {
    private String userCountMessage;
	@Inject Logger logger;
	@EJB private ScheduledService scheduledService;
    
    public void testNothing() {
        logger.info("Test Nothing");
    }

    public void testUpdate() {
    	scheduledService.updateSlipOpinions();
    }
    
    public void testWelcome() {
    	scheduledService.welcomingService();;
    }

    public void testOpinionReport() {
    	scheduledService.opinionReport();
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

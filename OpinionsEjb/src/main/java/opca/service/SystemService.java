package opca.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import opca.mailer.SendGridMailer;
import opca.model.User;

@Stateless
public class SystemService {
	@EJB private UserService userService;
	@EJB private SystemService systemService;
	@EJB private OpinionViewSingleton opinionViewSingleton;
	@Inject private SendGridMailer sendGridMailer;
    @Inject private Logger logger;
    /**
     * Merge user with Database
     * @param user to merge.
     * @return Merged User
     */
    @Asynchronous
    public void startVerify(User user) {
		// prevent all exceptions from leaving @Asynchronous block
    	try {
	    	sendGridMailer.sendEmail(user, "/xsl/verify.xsl");
	    	user.setStartVerify(true);
	    	userService.merge(user);
	    	logger.info("Verification started: " + user.getEmail());
    	} catch ( Exception ex ) {
	    	logger.severe("Verification failed: " + ex.getMessage());
    	}
    }
    	
    /**
     * Merge user with Database
     * @param user to merge.
     * @return Merged User
     */
    @Asynchronous
	public void sendAboutEmail(String email, String comment, Locale locale) {
		// prevent all exceptions from leaving @Asynchronous block
    	try {
	    	sendGridMailer.sendComment(email, comment, locale);
	    	logger.info("Comment sent" );
		} catch ( Exception ex ) {
	    	logger.severe("Comment send failed: " + ex.getMessage());
		}
    }

	public void sendWelcomeEmail(User user) {
    	user.setWelcomed(true);
    	userService.merge(user);
		sendGridMailer.sendEmail(user, "/xsl/welcome.xsl");
    }

	public void doWelcomeService() {
        logger.info("Welcome service started");
        // So, do the real work.
        // / accountRepository.findUnverified
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
        dayOfYear = dayOfYear - 4;
        if ( dayOfYear < 1 ) {
            year = year - 1;
            dayOfYear = 365 + dayOfYear;
        }
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_YEAR, dayOfYear);
        Date threeDaysAgo = cal.getTime();

        List<User> users = userService.findAllUnWelcomed();
        for ( User user: users ) {
        	if ( !user.isAdmin() ) {
	            if ( user.getCreateDate().compareTo(threeDaysAgo) < 0 ) {
	            		userService.delete(user.getId());
	            } else {
		            // Prepare the evaluation context
		            sendWelcomeEmail(user);
		            logger.info("Welcome email sent: " + user.getEmail());
		            //            System.out.println("Resend = " + account.getEmail());
	            }
        	}
        }
            
//        String htmlContent = mailTemplateEngine.process("verify.html", ctx);
        logger.info("Welcome service completed");
	}

	public void sendOpinionReports() {
        logger.info("Opinion Reports started");
        Calendar calNow = Calendar.getInstance();
        Calendar calLastWeek = Calendar.getInstance();
        int year = calLastWeek.get(Calendar.YEAR);
        int dayOfYear = calLastWeek.get(Calendar.DAY_OF_YEAR);
        dayOfYear = dayOfYear - 7;
        if ( dayOfYear < 1 ) {
            year = year - 1;
            dayOfYear = 365 + dayOfYear;
        }
        calLastWeek.set(Calendar.YEAR, year);
        calLastWeek.set(Calendar.DAY_OF_YEAR, dayOfYear);

        List<User> users = userService.findAll();
        for ( User user: users ) {
        	if ( !user.isOptout() ) {
	            // Prepare the evaluation context
        		ViewParameters viewInfo= new ViewParameters(calLastWeek.getTime(), calNow.getTime());
        		sendGridMailer.sendOpinionReport(user, opinionViewSingleton.getOpinionCasesForAccount(viewInfo));
	            logger.info("Case Report sent: " + user.getEmail());
	            //            System.out.println("Resend = " + account.getEmail());
        	}
        }
        logger.info("Case Reports completed");
	}


}

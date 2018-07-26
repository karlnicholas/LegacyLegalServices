package opca.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;

import opca.mailer.SendGridMailer;
import opca.model.User;

@Stateless
public class SystemService {
	@Inject private UserService userService;
	@Inject private OpinionViewSingleton opinionViewSingleton;
	@Inject private SendGridMailer sendGridMailer;
    @Resource private SessionContext ctx;
    @Inject private Logger logger;
    /**
     * Merge user with Database
     * @param user to merge.
     * @return Merged User
     */
    public void startVerify(User user) {
    	user.setStartVerify(true);
    	userService.merge(user);
    	ctx.getBusinessObject(SystemService.class).sendVerifyEmail(user);
    }
    	
    @Asynchronous
    public void sendVerifyEmail(User user) {
    	sendGridMailer.sendEmail(user, "/xsl/verify.xsl");
	}
    	
    /**
     * Merge user with Database
     * @param user to merge.
     * @return Merged User
     */
	public void sendAboutEmail(String email, String comment, Locale locale) {
    	ctx.getBusinessObject(SystemService.class).sendAboutEmailAsync(email, comment, locale);
    }

    @Asynchronous
    public void sendAboutEmailAsync(String email, String comment, Locale locale) {
    	sendGridMailer.sendComment(email, comment, locale);
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

	public void sendCaseReports() {
        logger.info("Case Reports started");
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
        		ViewParameters viewInfo= new ViewParameters(calLastWeek.getTime(), calNow.getTime(), true, 2);
        		sendGridMailer.sendOpinionReport(user, opinionViewSingleton.getOpinionCasesForAccount(viewInfo));
	            logger.info("Case Report sent: " + user.getEmail());
	            //            System.out.println("Resend = " + account.getEmail());
        	}
        }
        logger.info("Case Reports completed");
	}


}

package opca.service;

import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;

import opca.scraper.CACaseScraper;

@Singleton
public class ScheduledService {
    @Inject private Logger logger;
    @Inject private CAOnlineUpdates caOnlineUpdates;
    @EJB private SystemService systemService;
    @EJB private OpinionViewSingleton opinionViewSingleton;

    @Schedule(second="0", minute="30", hour="03", persistent=false)        // 03:30 am (12:30 am AZ ) every day
    // timeout issue.
    // @TransactionTimeout(value=1, unit = TimeUnit.HOURS)
    // this is handled in wildfly standalone.xml configuration file
    // though it is currently pretty fast, so maybe not needed.
    public void updateSlipOpinions() {
        logger.info("STARTING updateOpinionViews");
//      caOnlineUpdates.updateDatabase(new TestCACaseScraper(false));
        opinionViewSingleton.updateOpinionViews(
    		caOnlineUpdates.updateDatabase(new CACaseScraper(false))
		);
        logger.info("DONE updateOpinionViews");
    }

/*
    @Schedule(second="0", minute="30", hour="0", persistent=false)        // 12:30 am every day
    public void verifyHousekeeping() {
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

        List<User> users = userSession.findAllUnverified();
        for ( User user: users ) {
            if ( user.getCreateDate().compareTo(threeDaysAgo) < 0 ) {
                userSession.delete(user.getId());
                continue;
            }
    
            // Prepare the evaluation context
            verifyMailer.sendEmail(user);
//            System.out.println("Resend = " + account.getEmail());
        }
            
//        String htmlContent = mailTemplateEngine.process("verify.html", ctx);
        logger.info("VerifyEmail's sent"  );
    }
    @Schedule(second="0", minute="27", hour="14", persistent=false)        // 04:00 am every day
    public void welcomingService() {
    	systemService.doWelcomeService();
    }
    public void opinionReport() {
    	systemService.doWelcomeService();
    }
*/
    @Schedule(second="0", minute="0", hour="0", persistent=false)        // 12:00 am every day
    public void welcomingService() {
    	systemService.sendOpinionReports();
    }
}

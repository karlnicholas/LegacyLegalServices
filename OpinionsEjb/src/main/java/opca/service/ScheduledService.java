package opca.service;

import java.util.Date;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;

import opca.scraper.TestCACaseScraper;

@Singleton
public class ScheduledService {
    @Inject private Logger logger;
    @Inject private CAOnlineUpdates caOnlineUpdates;
//    @Inject private VerifyMailer verifyMailer;
    @EJB private UserService userSession;
    @EJB private OpinionViewSingleton opinionViewSingleton;

/*
    @Schedule(second="0", minute="15", hour="3", persistent=false)        // 03:30 am (12:30 am CA ) every day
    public void kickStatutesWS() {
        logger.info("KICK STATUESWS");
        new RestServicesService().connectStatutesRsService();
        logger.info("DONE KICK STATUESWS");
    }
*/    
    @Schedule(second="0", minute="50", hour="17", persistent=false)        // 03:30 am (12:30 am AZ ) every day
    // Wildfly specific transaction timeout setting
    // this isn't working at the moment, there are confiugrations necessary in wildfly?
    // also, this is hibernate/jboss specific. There was a Java EE way of acheiving this?
    // finally, shouldn't this whole process of updating the database
    // be moved over to a "microservice"?
    // meaning, running completely separately in a separate JVM
    // and separate wildfly instance?
    // seems overkill to run in a separate wildfly instance ..
    // but then again, memory problems is what caused the separation of the
    // statuesWS web service (that and learning).
    // also, there were constraints about running things in openshift free account.
    // but now I have a paid account, and I think I have 10 instances available .. 
    // using 1 ... so 9 left .. 
    // how many different services are we dealing with here?
    // update service, statutes service, viewing service, accounts service, board service, email service
    // so, do I want to break this up into 6 difference services.
    // hmmmm ... let's start with the update service anyway - because that seriously makes sense.
    // and I can fix this timeout issue.
    // @TransactionTimeout(value=1, unit = TimeUnit.HOURS)
    public void updateSlipOpinions() {
        Date currentTime = new Date();
        logger.info("STARTING SCRAPER UPDATE");
//        caOnlineUpdates.updateDatabase(new CACaseScraper(false));
        caOnlineUpdates.updateDatabase(new TestCACaseScraper(false));
        logger.info("DONE SCRAPER UPDATE");

        logger.info("STARTING OPINIONVIEW POSTCONSTRUCT");
        Date lastBuildDate = opinionViewSingleton.getLastBuildDate();
        if ( lastBuildDate == null || lastBuildDate.compareTo(currentTime) < 0 ) {
            logger.info("calling postConstruct()");
            opinionViewSingleton.postConstruct();
        }
        logger.info("DONE OPINIONVIEW POSTCONSTRUCT");
    }
/*    
    @Schedule(second="0", minute="0", hour="16")        // 01:30 am every day
    public void rebuildSlipOpinionViews() {
        logger.info("STARTING OPINIONVIEW POSTCONSTRUCT");
        opinionViewSingleton.postConstruct();
        logger.info("DONE OPINIONVIEW POSTCONSTRUCT");
    }
*/
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
*/
}

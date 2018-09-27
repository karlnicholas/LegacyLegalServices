package opca.service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.transaction.UserTransaction;

import opca.ejb.util.StatutesServiceFactory;
import opca.model.OpinionKey;
import opca.scraper.CACaseScraper;
import statutes.service.StatutesService;

@TransactionManagement(TransactionManagementType.BEAN)
@Singleton
public class ScheduledService {
    @Inject private Logger logger;
    @EJB private CAOnlineUpdates caOnlineUpdates;
    @EJB private SystemService systemService;
    @EJB private OpinionViewSingleton opinionViewSingleton;
    @Resource private EJBContext context;

    @Schedule(second="0", minute="00", hour="01", persistent=false)        // 03:30 am (12:30 am AZ ) every day
    // timeout issue.
    // @TransactionTimeout(value=1, unit = TimeUnit.HOURS)
    // this is handled in wildfly standalone.xml configuration file
    // though it is currently pretty fast, so maybe not needed.
    public void updateSlipOpinions() {
        logger.info("STARTING updateSlipOpinions");
//      caOnlineUpdates.updateDatabase(new TestCACaseScraper(false));
//		caOnlineUpdates.updateDatabase(new CACaseScraper(false), statutesService);
        List<OpinionKey> opinionKeys = null;
        UserTransaction userTransaction = context.getUserTransaction();
        StatutesService statutesService = StatutesServiceFactory.getStatutesServiceClient();
        try {
			userTransaction.begin();
	        opinionKeys = caOnlineUpdates.updateDatabase(new CACaseScraper(false), statutesService);
			userTransaction.commit();
		} catch (Exception e) {
			try {
				userTransaction.rollback();
			} catch (Exception e1) {
				throw new RuntimeException(e1);
			}
	        logger.severe(e.getMessage());
	        return;
		}
        if ( opinionKeys != null ) {
        	opinionViewSingleton.updateOpinionViews(opinionKeys, statutesService);
        }
        logger.info("DONE updateSlipOpinions");
    }

    @Schedule(second="0", minute="10", hour="01", persistent=false)        // 12:00 am every day
    public void opinionReport() {
    	systemService.sendOpinionReports();
    }

    @Schedule(second="0", minute="15", hour="01", persistent=false)        // 12:00 am every day
    public void systemReport() {
        Map<String, Long> memoryMap = new TreeMap<>();
        OperatingSystemMXBean osMxBean = ManagementFactory.getOperatingSystemMXBean();
        memoryMap.put("0 cpuLoad", (long)osMxBean.getSystemLoadAverage());

        ThreadMXBean threadmxBean = ManagementFactory.getThreadMXBean();
        int threadCount = threadmxBean.getThreadCount();
        memoryMap.put("1 cpuRunningThreads", (long)threadCount);

        MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage memHeapUsage = memBean.getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = memBean.getNonHeapMemoryUsage();
        memoryMap.put("2 heapInit", memHeapUsage.getInit() / (1024*1024));
        memoryMap.put("3 heapMax", memHeapUsage.getMax() / (1024*1024));
        memoryMap.put("4 heapCommit", memHeapUsage.getCommitted() / (1024*1024));
        memoryMap.put("5 heapUsed", memHeapUsage.getUsed() / (1024*1024));
        memoryMap.put("5 nonHeapInit", nonHeapUsage.getInit() / (1024*1024));
        memoryMap.put("7 nonHeapMax", nonHeapUsage.getMax() / (1024*1024));
        memoryMap.put("8 nonHeapCommit", nonHeapUsage.getCommitted() / (1024*1024));
        memoryMap.put("9 nonHeapUsed", nonHeapUsage.getUsed() / (1024*1024));
    	systemService.sendSystemReport(memoryMap);
    }
    
    @Schedule(second="0", minute="20", hour="01", persistent=false)        // 04:00 am every day
    public void welcomingService() {
        logger.info("STARTING welcomingService");
        UserTransaction userTransaction = context.getUserTransaction();
        try {
			userTransaction.begin();
			systemService.doWelcomeService();
			userTransaction.commit();
		} catch (Exception e) {
			try {
				userTransaction.rollback();
			} catch (Exception e1) {
				throw new RuntimeException(e1);
			}
	        logger.severe(e.getMessage());
	        return;
		}
        logger.info("DONE welcomingService");
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
*/
}

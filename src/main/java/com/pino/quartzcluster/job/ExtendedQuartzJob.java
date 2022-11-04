package com.pino.quartzcluster.job;

import com.pino.quartzcluster.manager.JobLogManager;
import com.pino.quartzcluster.manager.QuartzScheduleManager;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public abstract class ExtendedQuartzJob extends QuartzJobBean {

    protected final String jobName;
    protected final String instanceId;
    protected final Logger logger;

    protected JobLogManager jobLogManager;

    protected ExtendedQuartzJob(QuartzScheduleManager quartzScheduleManager, JobLogManager jobLogManager) {
        Class<?> jobClass = super.getClass();
        this.jobName = jobClass.getSimpleName();
        this.instanceId = quartzScheduleManager.getSchedulerInstanceId();
        this.logger = LoggerFactory.getLogger(jobClass);
        this.jobLogManager = jobLogManager;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) {
        String timestamp = Long.toString(System.currentTimeMillis());
        String jobCode = timestamp.substring(timestamp.length() - 6); // 用於配對工作開始結束

        writeLog("啟動 - " + jobCode);

        try {
            executeWork();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            writeLog(ex.getMessage());
        }

        writeLog("結束 - " + jobCode);
    }

    protected void writeLog(String content) {
        jobLogManager.writeLog(jobName, instanceId, content);
    }

    protected abstract void executeWork();
}

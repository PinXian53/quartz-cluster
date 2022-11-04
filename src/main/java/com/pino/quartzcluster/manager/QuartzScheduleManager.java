package com.pino.quartzcluster.manager;

import com.pino.quartzcluster.exception.InternalServerErrorException;
import com.pino.quartzcluster.job.EveryMinuteJob1;
import com.pino.quartzcluster.job.EveryMinuteJob2;
import com.pino.quartzcluster.job.Pm4Job;
import com.pino.quartzcluster.model.ScheduleJobDTO;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.*;

@RequiredArgsConstructor
@Component
public class QuartzScheduleManager {
    private static final Logger logger = LoggerFactory.getLogger(QuartzScheduleManager.class);
    private static final Map<Class<? extends Job>, ScheduleJobDTO> SYSTEM_JOBS = new HashMap<>();
    private static final String SYSTEM = "System";

    static {
        SYSTEM_JOBS.put(EveryMinuteJob1.class, ScheduleJobDTO.builder()
            .setJobClassName(EveryMinuteJob1.class.getName())
            .setJobName(EveryMinuteJob1.class.getSimpleName())
            .setJobGroup(SYSTEM)
            .setCronExpression("0 * * ? * * *")
            .build()
        );
        SYSTEM_JOBS.put(EveryMinuteJob2.class, ScheduleJobDTO.builder()
            .setJobClassName(EveryMinuteJob2.class.getName())
            .setJobName(EveryMinuteJob2.class.getSimpleName())
            .setJobGroup(SYSTEM)
            .setCronExpression("0 * * ? * * *")
            .build()
        );
        SYSTEM_JOBS.put(Pm4Job.class, ScheduleJobDTO.builder()
            .setJobClassName(Pm4Job.class.getName())
            .setJobName(Pm4Job.class.getSimpleName())
            .setJobGroup(SYSTEM)
            .setCronExpression("0 0 17 ? * * *")
            .build()
        );
    }

    private final Scheduler scheduler;

    @PostConstruct
    public void init() {
        // 刪除多餘的排程
        List<ScheduleJobDTO> scheduleJobDTOList = new ArrayList<>(SYSTEM_JOBS.values());
        try {
            for (String groupName : scheduler.getJobGroupNames()) {
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    String jobName = jobKey.getName();
                    boolean exist = scheduleJobDTOList.stream().anyMatch(dto -> jobName.equals(dto.getJobName()));
                    if (!exist) {
                        this.scheduler.deleteJob(jobKey);
                        logger.info("delete {} job", jobName);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        // 重新載入牌程
        SYSTEM_JOBS.forEach((jobClass, scheduleJobVO) -> {
            JobDetail job = JobBuilder.newJob(jobClass)
                .withIdentity(scheduleJobVO.getJobName(), scheduleJobVO.getJobGroup())
                .build();
            scheduleJob(
                job,
                scheduleJobVO.getCronExpression()
            );
        });
    }

    public String getSchedulerInstanceId() {
        try {
            return scheduler.getSchedulerInstanceId();
        } catch (SchedulerException e) {
            throw new InternalServerErrorException(e.getMessage(), e);
        }
    }

    private void scheduleJob(JobDetail job, String cronExpression) {
        try {
            CronExpression.validateExpression(cronExpression);
            JobKey jobKey = job.getKey();
            TriggerKey triggerKey = TriggerKey.triggerKey(jobKey.getName());
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
            CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .withSchedule(cronScheduleBuilder.inTimeZone(TimeZone.getTimeZone("Asia/Taipei")))
                .build();
            if (scheduler.checkExists(triggerKey)) {
                scheduler.rescheduleJob(triggerKey, cronTrigger);
            } else {
                scheduler.scheduleJob(job, cronTrigger);
            }
            logger.info("{} scheduled", job.getKey());
        } catch (SchedulerException | ParseException e) {
            throw new InternalServerErrorException(e.getMessage(), e);
        }
    }
}

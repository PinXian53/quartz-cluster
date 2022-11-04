package com.pino.quartzcluster.job;

import com.pino.quartzcluster.manager.JobLogManager;
import com.pino.quartzcluster.manager.QuartzScheduleManager;
import com.pino.quartzcluster.util.ThreadUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@DisallowConcurrentExecution
@Component
public class EveryMinuteJob2 extends ExtendedQuartzJob {

    @Autowired EveryMinuteJob2(
        QuartzScheduleManager quartzScheduleManager,
        JobLogManager jobLogManager) {
        super(quartzScheduleManager, jobLogManager);
    }

    @SneakyThrows
    protected void executeWork() {
        ThreadUtils.sleep(90000);
    }
}

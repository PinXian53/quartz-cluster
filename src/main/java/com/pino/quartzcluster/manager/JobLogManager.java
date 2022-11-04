package com.pino.quartzcluster.manager;

import com.pino.quartzcluster.dao.JobLogRepository;
import com.pino.quartzcluster.model.JobLogEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobLogManager {

    private final JobLogRepository jobLogRepository;

    public void writeLog(String jobName, String instanceId, String content) {
        try {
            jobLogRepository.save(
                JobLogEntity.builder()
                    .jobName(jobName)
                    .instanceId(instanceId)
                    .content(content)
                    .triggerDateTime(OffsetDateTime.now())
                    .build()
            );
        } catch (Exception ex) {
            log.info("排程 Log 寫入 DB 失敗！", ex);
        }
    }
}

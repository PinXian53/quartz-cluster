package com.pino.quartzcluster.dao;

import com.pino.quartzcluster.model.JobLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobLogRepository extends JpaRepository<JobLogEntity, Integer> {
}

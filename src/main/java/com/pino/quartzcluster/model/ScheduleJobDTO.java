package com.pino.quartzcluster.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "set")
public class ScheduleJobDTO {
    private String jobName;
    private String jobGroup;
    private String cronExpression;
    private String jobClassName;
    private Boolean disabled;
}

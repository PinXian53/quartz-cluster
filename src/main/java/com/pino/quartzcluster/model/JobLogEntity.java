package com.pino.quartzcluster.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "job_log")
public class JobLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oid")
    private Integer oid;

    @Column(name = "job_name")
    private String jobName;

    @Column(name = "instance_id")
    private String instanceId;

    @Column(name = "content")
    private String content;

    @Column(name = "trigger_date_time")
    private OffsetDateTime triggerDateTime;
}

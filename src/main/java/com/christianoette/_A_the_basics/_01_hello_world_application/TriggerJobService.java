package com.christianoette._A_the_basics._01_hello_world_application;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TriggerJobService {

    private final JobLauncher jobLauncher;
    private final Job job;

    //    public TriggerJobService(JobLauncher jobLauncher, @Qualifier("myJob") Job job) {

    public TriggerJobService(JobLauncher jobLauncher, @MyJob Job job) {
        this.jobLauncher = jobLauncher;
        this.job = job;
    }

    public void runJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException,
            JobParametersInvalidException, JobRestartException, InterruptedException {

        JobParameters jobParameters = new JobParametersBuilder()
                .addParameter("outputText", new JobParameter("my first spring batch app"))
                .toJobParameters();
        jobLauncher.run(job, jobParameters);

        JobParameters jobParameters2 = new JobParametersBuilder()
                .addParameter("outputText", new JobParameter("secondRun"))
                .toJobParameters();
        Thread.sleep(1000);
        jobLauncher.run(job, jobParameters2);
    }
}

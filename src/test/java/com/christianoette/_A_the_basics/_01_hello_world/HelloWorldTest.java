package com.christianoette._A_the_basics._01_hello_world;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@SpringBootTest(classes = HelloWorldTest.TestConfig.class)
class HelloWorldTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void test() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addParameter("outputText", new JobParameter("Hello spring batch"))
                .toJobParameters();

        jobLauncherTestUtils.launchJob();
    }

    @Configuration
    @EnableBatchProcessing
    public static class TestConfig {

        @Autowired
        private JobBuilderFactory jobBuilderFactory;

        @Autowired
        private StepBuilderFactory stepBuilderFactory;

        @Bean
        public Job HelloWorldJob() {
            Step step = stepBuilderFactory.get("step")
                    .tasklet((stepContribution, chunkContext) -> {
                        Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
                        Object outputText = jobParameters.get("outputText");
                        System.out.println(outputText);
                        return RepeatStatus.FINISHED;
                    }).build();

            return jobBuilderFactory.get("helloWorldJob")
                    .start(step)
                    .build();
        }

        @Bean
        public JobLauncherTestUtils utils() {
            return new JobLauncherTestUtils();
        }
    }
}

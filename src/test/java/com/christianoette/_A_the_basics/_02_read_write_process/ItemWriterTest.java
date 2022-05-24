package com.christianoette._A_the_basics._02_read_write_process;

import com.christianoette.testutils.CourseUtilBatchTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {ItemWriterTest.TestConfig.class, CourseUtilBatchTestConfig.class})
class ItemWriterTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    void runJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }

    @SuppressWarnings("WeakerAccess")
    @Configuration
    static class TestConfig {

        @Autowired
        private JobBuilderFactory jobBuilderFactory;

        @Autowired
        private StepBuilderFactory stepBuilderFactory;

        @Bean
        public Job job() {
            return jobBuilderFactory.get("myJob")
                    .start(readerStep())
                    .build();
        }

        @Bean
        public Step readerStep() {
            return stepBuilderFactory.get("readJsonStep")
                    .<InputAndOutputObject,InputAndOutputObject>chunk(1)
                    .reader(reader())
                    //no need this processor in newer spring, this processor just passes read objects to the writer
                    .processor(new PassThroughItemProcessor<>())
                    .writer(writer()).build();
        }

        @Bean
        public JsonFileItemWriter<InputAndOutputObject> writer() {
            Resource outputResourse = new FileSystemResource("output/output.json");

            return new JsonFileItemWriterBuilder<InputAndOutputObject>()
                    .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                    .resource(outputResourse)
                    .name("jsonItemWriter")
                    .build();
        }

        @Bean
        public JsonItemReader<InputAndOutputObject> reader() {
            File file;
            try {
                file = ResourceUtils.getFile("classpath:files/_A/input.json");
            } catch (FileNotFoundException ex) {
                throw new IllegalArgumentException(ex);
            }

            return new JsonItemReaderBuilder<InputAndOutputObject>()
                    .jsonObjectReader(new JacksonJsonObjectReader<>(InputAndOutputObject.class))
                    .resource(new FileSystemResource(file))
                    .name("jsonItemReader")
                    .build();
        }

        public static class InputAndOutputObject {
            public String value;

            @Override
            public String toString() {
                return "InputAndOutputObject{" +
                        "value='" + value + '\'' +
                        '}';
            }
        }
    }

}

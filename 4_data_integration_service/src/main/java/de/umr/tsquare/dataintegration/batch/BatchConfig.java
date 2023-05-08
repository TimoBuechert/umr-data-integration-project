package de.umr.tsquare.dataintegration.batch;

import de.umr.tsquare.dataintegration.persistence.dbstation.DbStationEntity;
import de.umr.tsquare.dataintegration.persistence.dbstation.DbStationWriter;
import de.umr.tsquare.dataintegration.persistence.rmvstation.RmvStationEntity;
import de.umr.tsquare.dataintegration.persistence.rmvstation.RmvStationWriter;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@AllArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;

    private final ReaderConfig readerConfig;

    private final PlatformTransactionManager transactionManager;

    private final DbStationWriter dbStationWriter;

    private final RmvStationWriter rmvStationWriter;

    @Bean
    public Job importData(final JobRepository jobRepository, final JobCompletionNotificationListener listener) {
        return new JobBuilder("importDataJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(splitImportFlow())
                .end()
                .build();
    }

    @Bean
    public Flow splitImportFlow() {
        return new FlowBuilder<SimpleFlow>("splitFlow")
                .split(taskExecutor())
                .add(importDbFlow(), importRmvFlow())
                .build();
    }

    @Bean
    public Flow importDbFlow() {
        return new FlowBuilder<SimpleFlow>("importDbFlow")
                .start(importDbStep())
                .build();
    }

    @Bean
    public Flow importRmvFlow() {
        return new FlowBuilder<SimpleFlow>("importRmvFlow")
                .start(importRmvStep())
                .build();
    }


    @Bean
    public Step importDbStep() {
        return new StepBuilder("importDbStep", jobRepository)
                .<DbStationEntity, DbStationEntity>chunk(100, transactionManager)
                .reader(readerConfig.dbReader())
                .writer(dbStationWriter)
                .build();
    }

    @Bean
    public Step importRmvStep() {
        return new StepBuilder("importRmvStep", jobRepository)
                .<RmvStationEntity, RmvStationEntity>chunk(100, transactionManager)
                .reader(readerConfig.rmvReader())
                .writer(rmvStationWriter)
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("spring_batch");
    }

}

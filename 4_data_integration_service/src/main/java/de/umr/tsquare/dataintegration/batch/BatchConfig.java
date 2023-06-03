package de.umr.tsquare.dataintegration.batch;

import de.umr.tsquare.dataintegration.persistence.integration.dbstation.IntegratedDbStationEntity;
import de.umr.tsquare.dataintegration.persistence.integration.dbstation.IntegratedDbStationRepository;
import de.umr.tsquare.dataintegration.persistence.integration.dbstation.IntegratedDbStationWriter;
import de.umr.tsquare.dataintegration.persistence.integration.rmvstation.IntegratedRmvStationEntity;
import de.umr.tsquare.dataintegration.persistence.integration.rmvstation.IntegratedRmvStationRepository;
import de.umr.tsquare.dataintegration.persistence.integration.rmvstation.IntegratedRmvStationWriter;
import de.umr.tsquare.dataintegration.persistence.integration.transferoption.IntegratedTransferOptionEntity;
import de.umr.tsquare.dataintegration.persistence.integration.transferoption.IntegratedTransferOptionWriter;
import de.umr.tsquare.dataintegration.persistence.preparation.dbstation.DbStationEntity;
import de.umr.tsquare.dataintegration.persistence.preparation.dbstation.DbStationRepository;
import de.umr.tsquare.dataintegration.persistence.preparation.dbstation.DbStationWriter;
import de.umr.tsquare.dataintegration.persistence.preparation.rmvstation.RmvStationEntity;
import de.umr.tsquare.dataintegration.persistence.preparation.rmvstation.RmvStationRepository;
import de.umr.tsquare.dataintegration.persistence.preparation.rmvstation.RmvStationWriter;
import de.umr.tsquare.dataintegration.process.IntegratedDbStationProcessor;
import de.umr.tsquare.dataintegration.process.IntegratedRmvStationProcessor;
import de.umr.tsquare.dataintegration.process.IntegratedTransferOptionProcessor;
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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@AllArgsConstructor
public class BatchConfig {

    private final ApplicationConfig config;

    private final JobRepository jobRepository;

    private final ReaderConfig readerConfig;

    private final PlatformTransactionManager transactionManager;

    private final DbStationWriter dbStationWriter;

    private final RmvStationWriter rmvStationWriter;

    private final DbStationRepository dbStationRepository;

    private final RmvStationRepository rmvStationRepository;

    private final IntegratedRmvStationRepository integratedRmvStationRepository;

    private final IntegratedDbStationRepository integratedDbStationRepository;

    private final IntegratedDbStationWriter integratedDbStationWriter;

    private final IntegratedRmvStationWriter integratedRmvStationWriter;

    private final IntegratedTransferOptionWriter integratedTransferOptionWriter;

    @Bean
    public Job importData(final JobRepository jobRepository, final JobCompletionNotificationListener listener) {
        return new JobBuilder("importDataJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(splitImportFlow())
                .next(splitStageFlow())
                .next(createTransferOptionFlow())
                .end()
                .build();
    }
    @Bean
    public Flow createTransferOptionFlow() {
        return new FlowBuilder<SimpleFlow>("createTransferOptionFlow")
                .start(new StepBuilder("createTransferOptionStep", jobRepository)
                        .<IntegratedDbStationEntity, List<IntegratedTransferOptionEntity>>chunk(config.getChunkSize(), transactionManager)
                        .reader(readerConfig.integratedDbReader(integratedDbStationRepository))
                        .processor(integratedTransferOptionProcessor())
                        .writer(integratedTransferOptionWriter)
                        .build())
                .build();
    }

    @Bean
    public Flow splitStageFlow() {
        return new FlowBuilder<SimpleFlow>("splitStageFlow")
                .split(taskExecutor())
                .add(new FlowBuilder<SimpleFlow>("integrateDbFlow")
                                .start(integrateDbStep())
                                .build(),
                        new FlowBuilder<SimpleFlow>("integrateRmvFlow")
                                .start(integrateRmvStep())
                                .build())
                .build();
    }

    @Bean
    public Flow splitImportFlow() {
        return new FlowBuilder<SimpleFlow>("splitFlow")
                .split(taskExecutor())
                .add(new FlowBuilder<SimpleFlow>("importDbFlow")
                                .start(importDbStep())
                                .build(),
                        new FlowBuilder<SimpleFlow>("importRmvFlow")
                                .start(importRmvStep())
                                .build())
                .build();
    }

    @Bean
    public Step importDbStep() {
        return new StepBuilder("importDbStep", jobRepository)
                .<DbStationEntity, DbStationEntity>chunk(config.getChunkSize(), transactionManager)
                .reader(readerConfig.dbReader())
                .writer(dbStationWriter)
                .build();
    }

    @Bean
    public Step importRmvStep() {
        return new StepBuilder("importRmvStep", jobRepository)
                .<RmvStationEntity, RmvStationEntity>chunk(config.getChunkSize(), transactionManager)
                .reader(readerConfig.rmvReader())
                .writer(rmvStationWriter)
                .build();
    }

    @Bean
    public Step integrateDbStep() {
        return new StepBuilder("integrateDbStep", jobRepository)
                .<DbStationEntity, IntegratedDbStationEntity>chunk(config.getChunkSize(), transactionManager)
                .reader(readerConfig.stagedDbReader(dbStationRepository))
                .processor(integratedDbStationProcessor())
                .writer(integratedDbStationWriter)
                .build();
    }

    @Bean
    public Step integrateRmvStep() {
        return new StepBuilder("integrateRmvStep", jobRepository)
                .<RmvStationEntity, IntegratedRmvStationEntity>chunk(config.getChunkSize(), transactionManager)
                .reader(readerConfig.stagedRmvReader(rmvStationRepository))
                .processor(integratedRmvStationProcessor())
                .writer(integratedRmvStationWriter)
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("spring_batch");
    }

    @Bean
    public ItemProcessor<DbStationEntity, IntegratedDbStationEntity> integratedDbStationProcessor() {
        return new IntegratedDbStationProcessor();
    }

    @Bean
    public ItemProcessor<RmvStationEntity, IntegratedRmvStationEntity> integratedRmvStationProcessor() {
        return new IntegratedRmvStationProcessor();
    }

    @Bean
    public ItemProcessor<IntegratedDbStationEntity, List<IntegratedTransferOptionEntity>> integratedTransferOptionProcessor() {
        return new IntegratedTransferOptionProcessor(integratedRmvStationRepository, config.getThresholdMeters(), config.getEqualityThresholdMeters(), config.getEqualityLevenshteinDistanceThreshold());
    }


}

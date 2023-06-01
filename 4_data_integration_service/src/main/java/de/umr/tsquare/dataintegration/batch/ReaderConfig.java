package de.umr.tsquare.dataintegration.batch;

import de.umr.tsquare.dataintegration.persistence.integration.rmvstation.IntegratedRmvStationEntity;
import de.umr.tsquare.dataintegration.persistence.integration.rmvstation.IntegratedRmvStationRepository;
import de.umr.tsquare.dataintegration.persistence.preparation.dbstation.DbStationEntity;
import de.umr.tsquare.dataintegration.persistence.preparation.dbstation.DbStationRepository;
import de.umr.tsquare.dataintegration.persistence.preparation.rmvstation.RmvStationEntity;
import de.umr.tsquare.dataintegration.persistence.preparation.rmvstation.RmvStationRepository;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;

import java.util.Map;

@Configuration
public class ReaderConfig {

    @Value("${import.db.file.name}")
    private String dbFileName;

    @Value("${import.rmv.file.name}")
    private String rmvFileName;

    @Bean
    public FlatFileItemReader<DbStationEntity> dbReader() {
        return new FlatFileItemReaderBuilder<DbStationEntity>()
                .name("dbStationReader")
                .resource(new ClassPathResource(dbFileName))
                .linesToSkip(1)
                .delimited()
                .delimiter(";")
                .names("evaNr", "ds100", "ifopt", "name", "verkehr", "laenge", "breite", "betreiberName", "betreiberNr", "status")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(DbStationEntity.class);
                }})
                .build();
    }

    @Bean
    public FlatFileItemReader<RmvStationEntity> rmvReader() {
        return new FlatFileItemReaderBuilder<RmvStationEntity>()
                .name("rmvStationReader")
                .resource(new ClassPathResource(rmvFileName))
                .linesToSkip(1)
                .delimited()
                .delimiter(";")
                .names("hafasId", "rmvId", "dhid", "hstName", "nameFahrplan", "xIplWert", "yIplWert", "xWgs84", "yWgs84", "lno", "istBahnhof", "gueltigAb", "gueltigBis", "verbund1IstgleichRmv", "land", "rp", "landkreis", "gemeindename", "ortsteilname", "agsLand", "agsRp", "agsLk", "agsG", "agsOt")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(RmvStationEntity.class);
                }})
                .build();
    }

    @Bean
    public RepositoryItemReader<IntegratedRmvStationEntity> integratedRmvReader(final IntegratedRmvStationRepository repository) {
        return new RepositoryItemReaderBuilder<IntegratedRmvStationEntity>()
                .methodName("findAll")
                .pageSize(100)
                .sorts(Map.of("stationId", Sort.Direction.ASC))
                .repository(repository)
                .name("integratedRmvReader")
                .build();
    }

    @Bean
    public RepositoryItemReader<RmvStationEntity> stagedRmvReader(final RmvStationRepository repository) {
        return new RepositoryItemReaderBuilder<RmvStationEntity>()
                .methodName("findAll")
                .pageSize(100)
                .sorts(Map.of("hafasId", Sort.Direction.ASC))
                .repository(repository)
                .name("rmvStationReader")
                .build();
    }

    @Bean
    public RepositoryItemReader<DbStationEntity> stagedDbReader(final DbStationRepository repository) {
        return new RepositoryItemReaderBuilder<DbStationEntity>()
                .methodName("findAll")
                .pageSize(100)
                .sorts(Map.of("evaNr", Sort.Direction.ASC))
                .repository(repository)
                .name("dbStationReader")
                .build();
    }
}

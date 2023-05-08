package de.umr.tsquare.dataintegration.batch;

import de.umr.tsquare.dataintegration.persistence.dbstation.DbStationEntity;
import de.umr.tsquare.dataintegration.persistence.rmvstation.RmvStationEntity;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

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
                .names("evaNr", "name", "ds100", "ifopt", "verkehr", "laenge", "breite", "betreiberName", "betreiberNr", "status")
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
}

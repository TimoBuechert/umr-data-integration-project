package de.umr.tsquare.dataintegration.task;

import de.umr.tsquare.dataintegration.persistence.integration.dbstation.IntegratedDbStationEntity;
import de.umr.tsquare.dataintegration.persistence.integration.dbstation.IntegratedDbStationRepository;
import de.umr.tsquare.dataintegration.persistence.integration.rmvstation.IntegratedRmvStationEntity;
import de.umr.tsquare.dataintegration.persistence.integration.rmvstation.IntegratedRmvStationRepository;
import de.umr.tsquare.dataintegration.persistence.integration.transferoption.IntegratedTransferOptionEntity;
import de.umr.tsquare.dataintegration.persistence.integration.transferoption.IntegratedTransferOptionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author timo.buechert
 */

@Service
@AllArgsConstructor
@Slf4j
public class UnifyCityNamesTasklet implements Tasklet {

    final IntegratedTransferOptionRepository integratedTransferOptionRepository;

    final IntegratedRmvStationRepository integratedRmvStationRepository;

    final IntegratedDbStationRepository integratedDbStationRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {

        final List<IntegratedDbStationEntity> dbStationEntities
                = integratedDbStationRepository.findAll();

        final List<IntegratedRmvStationEntity> rmvStationEntities = new ArrayList<>();

        for (final IntegratedDbStationEntity dbStationEntity : dbStationEntities) {
            final List<IntegratedTransferOptionEntity> integratedTransferOptionEntities
                    = integratedTransferOptionRepository.findByDbStation(dbStationEntity);

            final List<IntegratedRmvStationEntity> connectedRmvStations
                    = integratedTransferOptionEntities
                    .stream()
                    .map(IntegratedTransferOptionEntity::getRmvStation).toList();

            final String dbCityName = dbStationEntity.getCityName();
            final String rmvCityName = integratedTransferOptionEntities.get(0).getRmvStation().getCityName();

            if (dbCityName.equals(rmvCityName)) {
                continue;
            }

            final String longestCityName =
                    Stream.of(dbCityName, rmvCityName)
                            .max(Comparator.comparingInt(String::length))
                            .get();

            dbStationEntity.setCityName(longestCityName);
            connectedRmvStations.forEach(integratedRmvStationEntity -> integratedRmvStationEntity.setCityName(longestCityName));
            rmvStationEntities.addAll(connectedRmvStations);

            log.info("Transformed {} and {} to {}", dbCityName, rmvCityName, longestCityName);
        }


        integratedDbStationRepository.saveAll(dbStationEntities);
        integratedRmvStationRepository.saveAll(rmvStationEntities);

        return RepeatStatus.FINISHED;
    }


}

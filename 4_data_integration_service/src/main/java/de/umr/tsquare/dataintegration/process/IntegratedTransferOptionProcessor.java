package de.umr.tsquare.dataintegration.process;

import de.umr.tsquare.dataintegration.persistence.integration.dbstation.IntegratedDbStationEntity;
import de.umr.tsquare.dataintegration.persistence.integration.dbstation.IntegratedDbStationRepository;
import de.umr.tsquare.dataintegration.persistence.integration.rmvstation.IntegratedRmvStationEntity;
import de.umr.tsquare.dataintegration.persistence.integration.rmvstation.IntegratedRmvStationRepository;
import de.umr.tsquare.dataintegration.persistence.integration.transferoption.IntegratedTransferOptionEntity;
import lombok.AllArgsConstructor;
import org.apache.lucene.util.SloppyMath;
import org.springframework.batch.item.ItemProcessor;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class IntegratedTransferOptionProcessor implements ItemProcessor<IntegratedDbStationEntity, List<IntegratedTransferOptionEntity>> {

    private IntegratedRmvStationRepository integratedRmvStationRepository;

    private int distanceThresholdInMeters;

    @Override
    public List<IntegratedTransferOptionEntity> process(final IntegratedDbStationEntity dbStation) {
        final List<IntegratedRmvStationEntity> possibleRmvStations =
                integratedRmvStationRepository.findByCityName(dbStation.getCityName());

        return possibleRmvStations.stream()
                .filter(rmvStation -> getDistanceInMeters(dbStation, rmvStation) < distanceThresholdInMeters)
                .map(rmvStation -> createTransferOptionEntity(dbStation, rmvStation))
                .collect(Collectors.toList());
    }

    private static double getDistanceInMeters(IntegratedDbStationEntity dbStation, IntegratedRmvStationEntity rmvStation) {
        return SloppyMath.haversinMeters(dbStation.getLatitude(), dbStation.getLongitude(),
                rmvStation.getLatitude(), rmvStation.getLongitude());
    }

    private IntegratedTransferOptionEntity createTransferOptionEntity(final IntegratedDbStationEntity dbStation,
                                                                      final IntegratedRmvStationEntity rmvStation) {
        final IntegratedTransferOptionEntity integratedTransferOptionEntity = new IntegratedTransferOptionEntity();
        integratedTransferOptionEntity.setDbStation(dbStation);
        integratedTransferOptionEntity.setRmvStation(rmvStation);
        integratedTransferOptionEntity.setDistance(getDistanceInMeters(dbStation, rmvStation));
        return integratedTransferOptionEntity;
    }

}

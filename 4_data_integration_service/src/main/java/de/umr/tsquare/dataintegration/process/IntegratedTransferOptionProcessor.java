package de.umr.tsquare.dataintegration.process;

import de.umr.tsquare.dataintegration.persistence.integration.dbstation.IntegratedDbStationEntity;
import de.umr.tsquare.dataintegration.persistence.integration.dbstation.IntegratedDbStationRepository;
import de.umr.tsquare.dataintegration.persistence.integration.rmvstation.IntegratedRmvStationEntity;
import de.umr.tsquare.dataintegration.persistence.integration.transferoption.IntegratedTransferOptionEntity;
import lombok.AllArgsConstructor;
import org.apache.lucene.util.SloppyMath;
import org.springframework.batch.item.ItemProcessor;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class IntegratedTransferOptionProcessor implements ItemProcessor<IntegratedRmvStationEntity, List<IntegratedTransferOptionEntity>> {

    private IntegratedDbStationRepository integratedDbStationRepository;

    private int distanceThresholdInMeters;

    @Override
    public List<IntegratedTransferOptionEntity> process(final IntegratedRmvStationEntity rmvStation) {
        final List<IntegratedDbStationEntity> possibleDbStations =
                integratedDbStationRepository.findByCityName(rmvStation.getCityName());

        return possibleDbStations.stream()
                .filter(dbStation -> getDistanceInMeters(dbStation, rmvStation) < distanceThresholdInMeters)
                .map(dbStation -> createTransferOptionEntity(dbStation, rmvStation))
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

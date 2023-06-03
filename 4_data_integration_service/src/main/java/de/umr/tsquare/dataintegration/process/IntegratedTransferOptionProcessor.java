package de.umr.tsquare.dataintegration.process;

import de.umr.tsquare.dataintegration.persistence.integration.dbstation.IntegratedDbStationEntity;
import de.umr.tsquare.dataintegration.persistence.integration.rmvstation.IntegratedRmvStationEntity;
import de.umr.tsquare.dataintegration.persistence.integration.rmvstation.IntegratedRmvStationRepository;
import de.umr.tsquare.dataintegration.persistence.integration.transferoption.IntegratedTransferOptionEntity;
import lombok.AllArgsConstructor;
import org.apache.lucene.util.SloppyMath;
import org.springframework.batch.item.ItemProcessor;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static de.umr.tsquare.dataintegration.util.StringUtil.calculateLevenshteinStationDistance;
import static de.umr.tsquare.dataintegration.util.StringUtil.removeCharsBetweenBrackets;

@AllArgsConstructor
public class IntegratedTransferOptionProcessor implements ItemProcessor<IntegratedDbStationEntity, List<IntegratedTransferOptionEntity>> {

    private IntegratedRmvStationRepository integratedRmvStationRepository;

    private int distanceThresholdInMeters;

    private int equalityDistanceThresholdInMeters;

    private int levenshteinDistanceThreshold;

    @Override
    public List<IntegratedTransferOptionEntity> process(final IntegratedDbStationEntity dbStation) {
        final List<IntegratedRmvStationEntity> possibleRmvStations =
                integratedRmvStationRepository.findByCityName(dbStation.getCityName());

        List<IntegratedTransferOptionEntity> transferOptions = possibleRmvStations.stream()
                .filter(rmvStation -> getDistanceInMeters(dbStation, rmvStation) < distanceThresholdInMeters)
                .map(rmvStation -> createTransferOptionEntity(dbStation, rmvStation))
                .collect(Collectors.toList());

        markSameStationTransfers(transferOptions);
        markBestTransferOption(transferOptions);
        return transferOptions;
    }

    private void markBestTransferOption(List<IntegratedTransferOptionEntity> transferOptions) {
        transferOptions.stream().filter(it -> !it.isIdenticalStations())
                .min(Comparator.comparing(IntegratedTransferOptionEntity::getDistance))
                .ifPresent(it -> it.setBestTransferOption(true));
    }

    private void markSameStationTransfers(List<IntegratedTransferOptionEntity> transferOptions) {
        transferOptions.stream()
                .filter(this::isEqualStationTransferCandidate)
                .min(Comparator.comparing(it ->
                    calculateLevenshteinStationDistance(
                            it.getDbStation().getStationName(),
                            it.getRmvStation().getStationNameLong()
                    )
                ))
                .ifPresent(it -> it.setIdenticalStations(true));
    }

    private boolean isEqualStationTransferCandidate(IntegratedTransferOptionEntity transferOption) {
        return transferOption.getDistance() < equalityDistanceThresholdInMeters && (
                calculateLevenshteinStationDistance(
                        removeCharsBetweenBrackets(transferOption.getDbStation().getStationName()),
                        removeCharsBetweenBrackets(transferOption.getRmvStation().getStationNameLong())
                ) < levenshteinDistanceThreshold);
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

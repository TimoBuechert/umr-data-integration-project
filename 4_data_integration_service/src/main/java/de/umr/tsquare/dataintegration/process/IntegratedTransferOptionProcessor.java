package de.umr.tsquare.dataintegration.process;

import de.umr.tsquare.dataintegration.persistence.integration.dbstation.IntegratedDbStationEntity;
import de.umr.tsquare.dataintegration.persistence.integration.rmvstation.IntegratedRmvStationEntity;
import de.umr.tsquare.dataintegration.persistence.integration.rmvstation.IntegratedRmvStationRepository;
import de.umr.tsquare.dataintegration.persistence.integration.transferoption.IntegratedTransferOptionEntity;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.util.SloppyMath;
import org.springframework.batch.item.ItemProcessor;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static de.umr.tsquare.dataintegration.util.StringUtil.*;

@RequiredArgsConstructor
public class IntegratedTransferOptionProcessor implements ItemProcessor<IntegratedDbStationEntity, List<IntegratedTransferOptionEntity>> {
    private final IntegratedRmvStationRepository integratedRmvStationRepository;

    private final int distanceThresholdInMeters;

    private final int equalityDistanceThresholdInMeters;

    private final int equalityLevenshteinDistanceThreshold;

    private final int equalityLevenshteinDistanceThresholdForCityName;

    private final int prefixLength;

    private List<IntegratedRmvStationEntity> integratedRmvStations;

    @Override
    public List<IntegratedTransferOptionEntity> process(final IntegratedDbStationEntity dbStation) {
        System.out.println("done");
        final List<IntegratedRmvStationEntity> possibleRmvStations =
                findRmvStationByCityName(dbStation.getCityName());

        List<IntegratedTransferOptionEntity> transferOptions = possibleRmvStations.stream()
                .filter(rmvStation -> getDistanceInMeters(dbStation, rmvStation) < distanceThresholdInMeters)
                .map(rmvStation -> createTransferOptionEntity(dbStation, rmvStation))
                .collect(Collectors.toList());

        markSameStationTransfers(transferOptions);
        markBestTransferOption(transferOptions);
        return transferOptions;
    }

    private List<IntegratedRmvStationEntity> findRmvStationByCityName(String cityName) {
        if (integratedRmvStations == null) {
            integratedRmvStations = integratedRmvStationRepository.findAll();
        }
        List<IntegratedRmvStationEntity> resultList = integratedRmvStations.stream()
                .filter(rmvStation -> rmvStation.getCityName().equals(cityName))
                .collect(Collectors.toList());
        if (resultList.isEmpty()) {
            Comparator<IntegratedRmvStationEntity> comparator =
                    Comparator.comparing(rmvStation ->
                            calculateLevenshteinDistance(
                                    cityName,
                                    rmvStation.getCityName(),
                                    equalityLevenshteinDistanceThresholdForCityName + 1));
            int length = cityName.length();
            int prefixLength = Integer.min(length, this.prefixLength);
            String mostCommonCity = integratedRmvStations.stream()
                    .filter(rmvStation -> rmvStation.getCityName().startsWith(cityName.substring(0, prefixLength)))
                    .filter(rmvStation ->
                            rmvStation.getCityName().length()
                                    <= length + equalityLevenshteinDistanceThresholdForCityName)
                    .filter(rmvStation ->
                            rmvStation.getCityName().length()
                                    >= length - equalityLevenshteinDistanceThresholdForCityName)
                    .distinct()
                    .min(comparator)
                    .map(IntegratedRmvStationEntity::getCityName)
                    .orElse(null);
            if (mostCommonCity != null &&
                    calculateLevenshteinDistance(mostCommonCity, cityName)
                            < equalityLevenshteinDistanceThresholdForCityName) {
                resultList = integratedRmvStations.stream()
                        .filter(rmvStation -> mostCommonCity.equals(rmvStation.getCityName()))
                        .collect(Collectors.toList());
            }
        }
        return resultList;
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
                ) < equalityLevenshteinDistanceThreshold);
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

package de.umr.tsquare.dataintegration.persistence.integration.rmvstation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntegratedRmvStationRepository extends JpaRepository<IntegratedRmvStationEntity, String> {

    List<IntegratedRmvStationEntity> findByCityName(String cityName);

    List<IntegratedRmvStationEntity> deleteByStationIdNotIn(List<String> stationIdList);


}

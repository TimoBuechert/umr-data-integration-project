package de.umr.tsquare.dataintegration.persistence.integration.dbstation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntegratedDbStationRepository extends JpaRepository<IntegratedDbStationEntity, String> {

    List<IntegratedDbStationEntity> deleteByStationIdNotIn(List<String> stationIdList);

}

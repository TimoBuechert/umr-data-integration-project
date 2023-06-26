package de.umr.tsquare.dataintegration.persistence.integration.transferoption;

import de.umr.tsquare.dataintegration.persistence.integration.dbstation.IntegratedDbStationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntegratedTransferOptionRepository extends JpaRepository<IntegratedTransferOptionEntity, String> {

    List<IntegratedTransferOptionEntity> findByDbStation(IntegratedDbStationEntity integratedDbStationEntity);

}

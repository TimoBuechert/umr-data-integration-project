package de.umr.tsquare.dataintegration.persistence.integration.dbstation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntegratedDbStationRepository extends JpaRepository<IntegratedDbStationEntity, String> {
}

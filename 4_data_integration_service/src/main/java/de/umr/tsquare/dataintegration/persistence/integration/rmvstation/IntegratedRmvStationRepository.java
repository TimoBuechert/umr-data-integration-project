package de.umr.tsquare.dataintegration.persistence.integration.rmvstation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntegratedRmvStationRepository extends JpaRepository<IntegratedRmvStationEntity, String> {
}

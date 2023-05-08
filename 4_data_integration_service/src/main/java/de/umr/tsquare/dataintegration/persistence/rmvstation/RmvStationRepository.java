package de.umr.tsquare.dataintegration.persistence.rmvstation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RmvStationRepository extends JpaRepository<RmvStationEntity, Long> {
}

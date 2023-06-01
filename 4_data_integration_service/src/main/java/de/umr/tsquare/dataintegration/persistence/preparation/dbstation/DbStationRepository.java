package de.umr.tsquare.dataintegration.persistence.preparation.dbstation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DbStationRepository extends JpaRepository<DbStationEntity, String> {
}

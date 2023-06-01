package de.umr.tsquare.dataintegration.persistence.integration.transferoption;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
@Repository
public interface IntegratedTransferOptionRepository extends JpaRepository<IntegratedTransferOptionEntity, String> {
}

package de.umr.tsquare.dataintegration.persistence.integration.dbstation;

import de.umr.tsquare.dataintegration.persistence.preparation.dbstation.DbStationEntity;
import de.umr.tsquare.dataintegration.persistence.preparation.dbstation.DbStationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class IntegratedDbStationWriter implements ItemWriter<IntegratedDbStationEntity> {

    private IntegratedDbStationRepository integratedDbStationRepository;

    @Override
    public void write(Chunk<? extends IntegratedDbStationEntity> chunk) {
        integratedDbStationRepository.saveAll(chunk.getItems());
        log.info("Saved {} entities to database", chunk.size());
    }
}

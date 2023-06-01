package de.umr.tsquare.dataintegration.persistence.integration.rmvstation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class IntegratedRmvStationWriter implements ItemWriter<IntegratedRmvStationEntity> {

    private IntegratedRmvStationRepository integratedRmvStationRepository;

    @Override
    public void write(Chunk<? extends IntegratedRmvStationEntity> chunk) {
        integratedRmvStationRepository.saveAll(chunk.getItems());
        log.info("Saved {} entities to database", chunk.size());
    }
}

package de.umr.tsquare.dataintegration.persistence.preparation.rmvstation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class RmvStationWriter implements ItemWriter<RmvStationEntity> {

    private RmvStationRepository rmvStationRepository;

    @Override
    public void write(Chunk<? extends RmvStationEntity> chunk) {
        rmvStationRepository.saveAll(chunk.getItems());
        log.info("Saved {} entities to database", chunk.size());
    }
}

package de.umr.tsquare.dataintegration.persistence.dbstation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class DbStationWriter implements ItemWriter<DbStationEntity> {

    private DbStationRepository dbStationRepository;

    @Override
    public void write(Chunk<? extends DbStationEntity> chunk) {
        dbStationRepository.saveAll(chunk.getItems());
        log.info("Saved {} entities to database", chunk.size());
    }
}

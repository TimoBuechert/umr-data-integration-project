package de.umr.tsquare.dataintegration.persistence.integration.transferoption;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class IntegratedTransferOptionWriter implements ItemWriter<List<IntegratedTransferOptionEntity>> {

    private IntegratedTransferOptionRepository integratedTransferOptionRepository;

    @Override
    public void write(Chunk<? extends List<IntegratedTransferOptionEntity>> chunk) {
        final List<IntegratedTransferOptionEntity> integratedTransferOptionEntities =
                chunk.getItems().stream().flatMap(List::stream).toList();
        integratedTransferOptionRepository.saveAll(integratedTransferOptionEntities);
        log.info("Saved {} entities to database", integratedTransferOptionEntities.size());
    }
}

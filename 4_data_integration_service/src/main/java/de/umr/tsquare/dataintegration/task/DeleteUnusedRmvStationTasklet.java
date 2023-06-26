package de.umr.tsquare.dataintegration.task;

import de.umr.tsquare.dataintegration.persistence.integration.rmvstation.IntegratedRmvStationEntity;
import de.umr.tsquare.dataintegration.persistence.integration.rmvstation.IntegratedRmvStationRepository;
import de.umr.tsquare.dataintegration.persistence.integration.transferoption.IntegratedTransferOptionEntity;
import de.umr.tsquare.dataintegration.persistence.integration.transferoption.IntegratedTransferOptionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author timo.buechert
 */
@Service
@Slf4j
@AllArgsConstructor
public class DeleteUnusedRmvStationTasklet implements Tasklet {

    IntegratedRmvStationRepository integratedRmvStationRepository;

    IntegratedTransferOptionRepository integratedTransferOptionRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        final List<String> idList = integratedTransferOptionRepository.findAll()
                .stream()
                .map(IntegratedTransferOptionEntity::getRmvStation)
                .map(IntegratedRmvStationEntity::getStationId).toList();

        final List<IntegratedRmvStationEntity> deletedEntities
                = integratedRmvStationRepository.deleteByStationIdNotIn(idList);

        log.info("Deleted {} integrated rmv station entities", deletedEntities.size());

        return RepeatStatus.FINISHED;
    }
}

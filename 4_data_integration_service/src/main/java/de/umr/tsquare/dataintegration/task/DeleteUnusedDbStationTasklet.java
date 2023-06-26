package de.umr.tsquare.dataintegration.task;

import de.umr.tsquare.dataintegration.persistence.integration.dbstation.IntegratedDbStationEntity;
import de.umr.tsquare.dataintegration.persistence.integration.dbstation.IntegratedDbStationRepository;
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
public class DeleteUnusedDbStationTasklet implements Tasklet {

    final IntegratedDbStationRepository integratedDbStationRepository;

    final IntegratedTransferOptionRepository integratedTransferOptionRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        final List<String> idList = integratedTransferOptionRepository.findAll()
                .stream()
                .map(IntegratedTransferOptionEntity::getDbStation)
                .map(IntegratedDbStationEntity::getStationId).toList();

        final List<IntegratedDbStationEntity> deletedEntities
                = integratedDbStationRepository.deleteByStationIdNotIn(idList);

        log.info("Deleted {} integrated db station entities", deletedEntities.size());

        return RepeatStatus.FINISHED;
    }
}

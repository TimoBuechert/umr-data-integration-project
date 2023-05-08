package de.umr.tsquare.dataintegration.persistence.dbstation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DbStationWriterTest {

    @Mock
    private DbStationRepository dbStationRepository;

    @InjectMocks
    private DbStationWriter dbStationWriter;

    @Test
    void testWrite() {
        //given
        final List<DbStationEntity> dbStationEntities = List.of(new DbStationEntity());
        when(dbStationRepository.saveAll(any())).thenReturn(dbStationEntities);

        //when
        dbStationWriter.write(new Chunk<>(dbStationEntities));

        //then
        verify(dbStationRepository, times(1)).saveAll(any());
    }
}
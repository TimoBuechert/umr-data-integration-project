package de.umr.tsquare.dataintegration.process;

import de.umr.tsquare.dataintegration.persistence.integration.rmvstation.IntegratedRmvStationEntity;
import de.umr.tsquare.dataintegration.persistence.preparation.rmvstation.RmvStationEntity;
import org.springframework.batch.item.ItemProcessor;

import java.text.ParseException;

import static de.umr.tsquare.dataintegration.util.NumberUtil.parseDouble;

public class IntegratedRmvStationProcessor implements ItemProcessor<RmvStationEntity, IntegratedRmvStationEntity> {
    @Override
    public IntegratedRmvStationEntity process(final RmvStationEntity rmvStationEntity) throws ParseException {
        final IntegratedRmvStationEntity integratedRmvStationEntity = new IntegratedRmvStationEntity();

        integratedRmvStationEntity.setStationId(rmvStationEntity.getDhid());
        integratedRmvStationEntity.setStationName(rmvStationEntity.getHstName());
        integratedRmvStationEntity.setStationNameLong(rmvStationEntity.getNameFahrplan());
        integratedRmvStationEntity.setCityName(rmvStationEntity.getGemeindename());
        integratedRmvStationEntity.setLongitude(parseDouble(rmvStationEntity.getXWgs84()));
        integratedRmvStationEntity.setLatitude(parseDouble(rmvStationEntity.getYWgs84()));

        return integratedRmvStationEntity;
    }

}

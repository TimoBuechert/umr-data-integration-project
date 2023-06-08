package de.umr.tsquare.dataintegration.process;

import de.umr.tsquare.dataintegration.persistence.integration.dbstation.IntegratedDbStationEntity;
import de.umr.tsquare.dataintegration.persistence.preparation.dbstation.DbStationEntity;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

import java.text.ParseException;

import static de.umr.tsquare.dataintegration.util.NumberUtil.parseDouble;

@Service
public class IntegratedDbStationProcessor implements ItemProcessor<DbStationEntity, IntegratedDbStationEntity> {
    @Override
    public IntegratedDbStationEntity process(final DbStationEntity dbStationEntity) throws ParseException {
        final IntegratedDbStationEntity integratedDbStationEntity = new IntegratedDbStationEntity();

        integratedDbStationEntity.setStationId(dbStationEntity.getEvaNr());
        integratedDbStationEntity.setStationName(dbStationEntity.getName());
        integratedDbStationEntity.setCityName(getCityName(dbStationEntity.getName()));
        integratedDbStationEntity.setLongitude(parseDouble(dbStationEntity.getLaenge()));
        integratedDbStationEntity.setLatitude(parseDouble(dbStationEntity.getBreite()));

        return integratedDbStationEntity;
    }

    private String getCityName(final String name) {
        //get the city name from the station name via regex
        //the station name is either in the format "cityName(stationName)" or "cityName stationName"
        //the regex matches the first group of characters before the first "(" or " "
        return name.split("(\\(|\\s)")[0];
    }
}

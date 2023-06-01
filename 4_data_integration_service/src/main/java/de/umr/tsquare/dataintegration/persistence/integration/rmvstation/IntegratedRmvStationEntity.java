package de.umr.tsquare.dataintegration.persistence.integration.rmvstation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "integrated_rmv_station")
public class IntegratedRmvStationEntity {

    @Id
    @Column(name = "station_id")
    private String stationId;

    @Column(name = "station_name")
    private String stationName;

    @Column(name = "station_name_long")
    private String stationNameLong;

    @Column(name = "city_name")
    private String cityName;

    private double latitude;

    private double longitude;

}

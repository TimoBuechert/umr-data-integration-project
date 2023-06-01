package de.umr.tsquare.dataintegration.persistence.integration.dbstation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "integrated_db_station")
public class IntegratedDbStationEntity {

    @Id
    @Column(name = "station_id")
    private String stationId;

    @Column(name = "station_name")
    private String stationName;

    @Column(name = "city_name")
    private String cityName;

    private double latitude;

    private double longitude;

}

package de.umr.tsquare.dataintegration.persistence.dbstation;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "db_station")
public class DbStationEntity {

    @Id
    private String evaNr;

    private String ds100;

    private String ifopt;

    private String name;

    private String verkehr;

    private String laenge;

    private String breite;

    private String betreiberName;

    private String betreiberNr;

    private String status;

}

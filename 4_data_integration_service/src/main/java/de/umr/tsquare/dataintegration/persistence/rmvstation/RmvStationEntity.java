package de.umr.tsquare.dataintegration.persistence.rmvstation;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "rmv_station")
public class RmvStationEntity {

    @Id
    private String hafasId;

    private String rmvId;

    private String dhid;

    private String hstName;

    private String nameFahrplan;

    private String xIplWert;

    private String yIplWert;

    private String xWgs84;

    private String yWgs84;

    private String lno;

    private String istBahnhof;

    private String gueltigAb;

    private String gueltigBis;

    private String verbund1IstgleichRmv;

    private String land;

    private String rp;

    private String landkreis;

    private String gemeindename;

    private String ortsteilname;

    private String agsLand;

    private String agsRp;

    private String agsLk;

    private String agsG;

    private String agsOt;

}

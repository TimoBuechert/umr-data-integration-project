package de.umr.tsquare.dataintegration.persistence.integration.transferoption;

import de.umr.tsquare.dataintegration.persistence.integration.dbstation.IntegratedDbStationEntity;
import de.umr.tsquare.dataintegration.persistence.integration.rmvstation.IntegratedRmvStationEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "integrated_transfer_option")
@IdClass(IntegratedTransferOptionEntityId.class)
public class IntegratedTransferOptionEntity {

    @ManyToOne
    @Id
    private IntegratedDbStationEntity dbStation;

    @ManyToOne
    @Id
    private IntegratedRmvStationEntity rmvStation;

    private double distance;

    @Column(name = "identical_stations")
    private boolean identicalStations;

    @Column(name = "best_transfer_option")
    private boolean bestTransferOption;

}

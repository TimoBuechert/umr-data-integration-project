package de.umr.tsquare.dataintegration.persistence.integration.transferoption;

import de.umr.tsquare.dataintegration.persistence.integration.dbstation.IntegratedDbStationEntity;
import de.umr.tsquare.dataintegration.persistence.integration.rmvstation.IntegratedRmvStationEntity;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class IntegratedTransferOptionEntityId implements Serializable {

    private IntegratedDbStationEntity dbStation;

    private IntegratedRmvStationEntity rmvStation;

}

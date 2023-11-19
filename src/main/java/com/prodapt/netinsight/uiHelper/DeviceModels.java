package com.prodapt.netinsight.uiHelper;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class DeviceModels {

    @Id
    private String deviceTypes;

    public DeviceModels(String deviceModel) {
        this.deviceTypes = deviceModel;
    }

    public DeviceModels() {

    }
}

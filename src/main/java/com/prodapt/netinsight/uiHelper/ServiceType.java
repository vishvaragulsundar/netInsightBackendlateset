package com.prodapt.netinsight.uiHelper;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
@Getter
@Setter
@Entity
public class ServiceType {
    @Id
    private String Name;

    public ServiceType() {
    }

    public ServiceType(String name) {
        Name = name;
    }
}

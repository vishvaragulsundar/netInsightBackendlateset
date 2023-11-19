package com.prodapt.netinsight.uiHelper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
@AllArgsConstructor
public class Vendors {

    @Id
    private String vendorName;

    public Vendors() {

    }
}

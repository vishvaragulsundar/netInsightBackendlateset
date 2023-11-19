package com.prodapt.netinsight.uiHelper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class ObjectCount {

    @Id
    private String objectType;

    private Long objectCount;

    public ObjectCount() {
    }
}

package com.prodapt.netinsight.objectModelManager;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class ObjectModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private ObjectModelName objectModelName;

    private String labelName;

    private String required;

    private String dataType;

    private Date createdDate;

    private Date updatedDate;

    private String createdBy;

    private String updatedBy;

    private String isAdditionalAttribute;
}

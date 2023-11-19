package com.prodapt.netinsight.uiHelper;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class CardModels {

    @Id
    private String cardTypes;

    public CardModels(String cardModel) {
        this.cardTypes = cardModel;
    }

    public CardModels() {

    }
}

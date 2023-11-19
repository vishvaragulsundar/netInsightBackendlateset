package com.prodapt.netinsight.deviceInstanceManager;

import com.prodapt.netinsight.objectModelManager.AdditionalAttribute;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;

@Getter
@Setter
@Node("Card")
public class Card {
    @Id
    @GeneratedValue
    private Long id;

    @Property("name")
    private String name;

    @Property("shelfPosition")
    private Integer shelfPosition;

    @Property("slotPosition")
    private Integer slotPosition;

    @Property("vendor")
    private String vendor;

    @Property("cardModel")
    private String cardModel;

    @Property("cardPartNumber")
    private String cardPartNumber;

    @Property("operationalState")
    private String operationalState;

    @Property("administrativeState")
    private String administrativeState;

    @Property("usageState")
    private String usageState;

    @Property("href")
    private String href;

    @Relationship(type = "CARD_TO_CARD_SLOT", direction = Relationship.Direction.OUTGOING)
    private ArrayList<CardSlot> cardSlots;

    @Relationship(type = "CARD_TO_ADDITIONAL_ATTRIBUTE", direction = Relationship.Direction.OUTGOING)
    private ArrayList<AdditionalAttribute> additionalAttributes;

    @Override
    public String toString() {
        return "Card{" +
                "id:" + id +
                ", name:'" + name + '\'' +
                ", shelfPosition:" + shelfPosition +
                ", slotPosition:" + slotPosition +
                ", vendor:'" + vendor + '\'' +
                ", cardModel:'" + cardModel + '\'' +
                ", cardPartNumber:'" + cardPartNumber + '\'' +
                ", operationalState:'" + operationalState + '\'' +
                ", administrativeState:'" + administrativeState + '\'' +
                ", usageState:'" + usageState + '\'' +
                ", href:'" + href + '\'' +
                '}';
    }
}

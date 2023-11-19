package com.prodapt.netinsight.deviceInstanceManager;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;

@Getter
@Setter
@Node("Slot")
public class Slot {
    @Id
    @GeneratedValue
    private Long id;

    @Property("name")
    private String name;

    @Property("slotPosition")
    private int slotPosition;

    @Property("operationalState")
    private String operationalState;

    @Property("administrativeState")
    private String administrativeState;

    @Property("usageState")
    private String usageState;

    @Property("href")
    private String href;

    @Relationship(type = "SLOT_TO_CARD", direction = Relationship.Direction.OUTGOING)
    private ArrayList<Card> cards;
}

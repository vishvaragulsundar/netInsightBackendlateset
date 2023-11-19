package com.prodapt.netinsight.deviceInstanceManager;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

@Getter
@Setter
@Node("CardSlot")
public class CardSlot {
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

    @Relationship(type = "CARD_SLOT_TO_PLUGGABLE", direction = Relationship.Direction.OUTGOING)
    private Pluggable pluggables;

    @Relationship(type = "CARD_SLOT_TO_PORT", direction = Relationship.Direction.OUTGOING)
    private Port ports;
}

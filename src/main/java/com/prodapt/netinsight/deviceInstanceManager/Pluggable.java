package com.prodapt.netinsight.deviceInstanceManager;

import com.prodapt.netinsight.objectModelManager.AdditionalAttribute;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;

@Getter
@Setter
@Node("Pluggable")
public class Pluggable {
    @Id
    @GeneratedValue
    private Long id;

    @Property("name")
    private String name;

    @Property("positionOnCard")
    private Integer positionOnCard;

    @Property("positionOnDevice")
    private Integer positionOnDevice;

    @Property("vendor")
    private String vendor;

    @Property("pluggableModel")
    private String pluggableModel;

    @Property("pluggablePartNumber")
    private String pluggablePartNumber;

    @Property("operationalState")
    private String operationalState;

    @Property("administrativeState")
    private String administrativeState;

    @Property("usageState")
    private String usageState;

    @Property("href")
    private String href;

    @Relationship(type = "PHYSICAL_TO_LOGICAL_PORT", direction = Relationship.Direction.OUTGOING)
    private ArrayList<LogicalPort> logicalPorts;


//    @Relationship(type = "CARD_SLOT_TO_PLUGGABLE", direction = Relationship.Direction.INCOMING)
//    private CardSlot CARD_SLOT_TO_PLUGGABLE;
//
//    @Relationship(type = "DEVICE_TO_PLUGGABLE", direction = Relationship.Direction.INCOMING)
//    private Device DEVICE_TO_PLUGGABLE;

    @Relationship(type = "PLUGGABLE_TO_ADDITIONAL_ATTRIBUTE", direction = Relationship.Direction.OUTGOING)
    private ArrayList<AdditionalAttribute> additionalAttributes;

    @Override
    public String toString() {
        return "Pluggable{" +
                "id:" + id +
                ", name:'" + name + '\'' +
                ", positionOnCard:" + positionOnCard +
                ", vendor:'" + vendor + '\'' +
                ", pluggableModel:'" + pluggableModel + '\'' +
                ", pluggablePartNumber:'" + pluggablePartNumber + '\'' +
                ", operationalState:'" + operationalState + '\'' +
                ", administrativeState:'" + administrativeState + '\'' +
                ", usageState:'" + usageState + '\'' +
                '}';
    }
}

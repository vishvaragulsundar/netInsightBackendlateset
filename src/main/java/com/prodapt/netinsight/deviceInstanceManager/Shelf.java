package com.prodapt.netinsight.deviceInstanceManager;

import com.prodapt.netinsight.objectModelManager.AdditionalAttribute;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;

@Getter
@Setter
@Node("Shelf")
public class Shelf {
    @Id
    @GeneratedValue
    private Long id;

    @Property("name")
    private String name;

    @Property("shelfPosition")
    private int shelfPosition;

    @Property("operationalState")
    private String operationalState;

    @Property("administrativeState")
    private String administrativeState;

    @Property("href")
    private String href;

    @Property("usageState")
    private String usageState;

//    @Relationship(type = "DEVICE_TO_SHELF", direction = Relationship.Direction.INCOMING)
//    private Device device;

    @Relationship(type = "SHELF_TO_SLOT", direction = Relationship.Direction.OUTGOING)
    private ArrayList<Slot> slots;

    @Relationship(type = "SHELF_TO_ADDITIONAL_ATTRIBUTE", direction = Relationship.Direction.OUTGOING)
    private ArrayList<AdditionalAttribute> additionalAttributes;

    @Override
    public String toString() {
        return "Shelf{" +
                "id:" + id +
                ", name:'" + name + '\'' +
                ", shelfPosition:" + shelfPosition +
                ", operationalState:'" + operationalState + '\'' +
                ", administrativeState:'" + administrativeState + '\'' +
                ", usageState:'" + usageState + '\'' +
                ", slot:" + slots +
                ", href:'" + href + '\'' +
                '}';
    }
}

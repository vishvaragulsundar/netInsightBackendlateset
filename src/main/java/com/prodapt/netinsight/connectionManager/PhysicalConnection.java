package com.prodapt.netinsight.connectionManager;

import com.prodapt.netinsight.deviceInstanceManager.Pluggable;
import com.prodapt.netinsight.deviceInstanceManager.Port;
import com.prodapt.netinsight.objectModelManager.AdditionalAttribute;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Node("PhysicalConnection")
public class PhysicalConnection {
    @Id
    @GeneratedValue
    private Long id;

    @Property("name")
    private String name;

    @Property("deviceA")
    private String deviceA;

    @Property("deviceZ")
    private String deviceZ;

    /*
    Naming convention used,
    shelf: deviceName + "_shelf_" + shelfNumber
    shelfSlot: deviceName + "/" + card.getShelfPosition().toString() + "/" + card.getSlotPosition()
    cardSlot: card + "/" + pluggable.getPositionOnCard()
     */

    /*
    Provide the port details in this format (shelfNo/SlotNoOnShelf/PortNoOnCard)
    Example: 5th port on a card present in shelf-1, slot-2 should be represented as 1/2/5
    For a single shelf or pizza box kind of devices, 5th port on the device should be represented as 5
     */
    @Property("deviceAPort")
    private String deviceAPort;

    @Property("deviceZPort")
    private String deviceZPort;

    @Property("connectionType")
    private String connectionType;

    @Property("bandwidth")
    private Integer bandwidth;

    @Relationship(type = "PHYSICALLY_CONNECTED", direction = Relationship.Direction.INCOMING)
    private List<Pluggable> pluggables;

    @Relationship(type = "PHYSICALLY_CONNECTED", direction = Relationship.Direction.INCOMING)
    private List<Port> ports;

    @Relationship(type = "PHYSICAL_CONNECTION_TO_ADDITIONAL_ATTRIBUTE", direction = Relationship.Direction.OUTGOING)
    ArrayList<AdditionalAttribute> additionalAttributes;


    @Override
    public String toString() {
        return "PhysicalConnection{" +
                "id:" + id +
                ", name:'" + name + '\'' +
                ", aEndDevice:'" + deviceA + '\'' +
                ", zEndDevice:'" + deviceZ + '\'' +
                ", aEndDevicePort:'" + deviceAPort + '\'' +
                ", zEndDevicePort:'" + deviceZPort + '\'' +
                ", connectionType:'" + connectionType + '\'' +
                ", bandwidth:" + bandwidth +
                '}';
    }
}

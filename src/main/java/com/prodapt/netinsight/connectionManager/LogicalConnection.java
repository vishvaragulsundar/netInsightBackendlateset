package com.prodapt.netinsight.connectionManager;

import com.prodapt.netinsight.objectModelManager.AdditionalAttribute;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;

@Getter
@Setter
@Node("LogicalConnection")
public class LogicalConnection {
    @Id
    @GeneratedValue
    private Long id;

    @Property("name")
    private String name;

    @Property("deviceA")
    private String deviceA;

    @Property("deviceZ")
    private String deviceZ;

    @Property("devicesConnected")
    private ArrayList<String> devicesConnected;

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

    @Property("connectionType")
    private String connectionType;

    @Property("bandwidth")
    private Integer bandwidth;

    @Property("deviceALogicalPort")
    private String deviceALogicalPort;

    @Property("deviceZLogicalPort")
    private String deviceZLogicalPort;

    @Property("physicalConnections")
    private ArrayList<String> physicalConnections;

    @Relationship(type = "LOGICAL_CONNECTION_TO_ADDITIONAL_ATTRIBUTE", direction = Relationship.Direction.OUTGOING)
    ArrayList<AdditionalAttribute> additionalAttributes;

    @Override
    public String toString() {
        return "LogicalConnection{" +
                "id:" + id +
                ", name:'" + name + '\'' +
                ", deviceA:'" + deviceA + '\'' +
                ", deviceZ:'" + deviceZ + '\'' +
                ", devicesConnected:" + devicesConnected +
                ", connectionType:'" + connectionType + '\'' +
                ", bandwidth:" + bandwidth +
                ", deviceALogicalPort:'" + deviceALogicalPort + '\'' +
                ", deviceZLogicalPort:'" + deviceZLogicalPort + '\'' +
                ", physicalConnections:" + physicalConnections +
                '}';
    }
}
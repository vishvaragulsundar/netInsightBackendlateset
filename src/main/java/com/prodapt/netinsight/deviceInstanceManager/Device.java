package com.prodapt.netinsight.deviceInstanceManager;

import com.prodapt.netinsight.objectModelManager.AdditionalAttribute;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;

@Getter
@Setter
@Node("Device")
public class Device {
    @Id
    @GeneratedValue
    private Long id;

    @Property("name")
    private String name;

    @Property("deviceModel")
    private String deviceModel;

    @Property("location")
    private String location;

    @Property("organisation")
    private String organisation;

    @Property("customer")
    private String customer;

    @Property("managementIp")
    private String managementIp;

    @Property("rackPosition")
    private String rackPosition;

    @Property("operationalState")
    private String operationalState;

    @Property("administrativeState")
    private String administrativeState;

    @Property("usageState")
    private String usageState;

    @Property("serialNumber")
    private String serialNumber;

    @Property("href")
    private String href;

    @Property("credentials")
    private String credentials;

    @Property("accessKey")
    private String accessKey;

    @Property("pollInterval")
    private String pollInterval;


    @Relationship(type = "DEVICE_TO_SHELF", direction = Relationship.Direction.OUTGOING)
    private ArrayList<Shelf> shelves;

    @Relationship(type = "DEVICE_TO_PORT", direction = Relationship.Direction.OUTGOING)
    private ArrayList<Port> devicePorts;

    @Relationship(type = "DEVICE_TO_PLUGGABLE", direction = Relationship.Direction.OUTGOING)
    private ArrayList<Pluggable> devicePluggables;

    @Relationship(type = "DEVICE_TO_ADDITIONAL_ATTRIBUTE", direction = Relationship.Direction.OUTGOING)
    private ArrayList<AdditionalAttribute> additionalAttributes;

    @Override
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", deviceModel='" + deviceModel + '\'' +
                ", location='" + location + '\'' +
                ", organisation='" + organisation + '\'' +
                ", customer='" + customer + '\'' +
                ", managementIp='" + managementIp + '\'' +
                ", rackPosition='" + rackPosition + '\'' +
                ", operationalState='" + operationalState + '\'' +
                ", administrativeState='" + administrativeState + '\'' +
                ", usageState='" + usageState + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", href='" + href + '\'' +
                ", credentials='" + credentials + '\'' +
                ", accessKey='" + accessKey + '\'' +
                ", pollInterval='" + pollInterval + '\'' +
                ", shelves=" + shelves +
                ", devicePorts=" + devicePorts +
                ", devicePluggables=" + devicePluggables +
                ", additionalAttributes=" + additionalAttributes +
                '}';
    }
}
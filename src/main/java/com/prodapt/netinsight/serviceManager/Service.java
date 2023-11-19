package com.prodapt.netinsight.serviceManager;

import com.prodapt.netinsight.customerManager.Customer;
import com.prodapt.netinsight.deviceInstanceManager.Device;
import com.prodapt.netinsight.objectModelManager.AdditionalAttribute;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;

@Data
@Node("Service")
public class Service {
    @Id
    @GeneratedValue
    private Long id;

    @Property("name")
    private String name;

    @Property("type")
    private String type;

    @Property("operationalState")
    private String operationalState;

    @Property("administrativeState")
    private String administrativeState;

    @Property("notes")
    private String notes;

    @Property("logicalConnections")
    private ArrayList<String> logicalConnections;

    @Property("physicalConnections")
    private ArrayList<String> physicalConnections;

    @Relationship(type = "SERVICE_RELATED_TO", direction = Relationship.Direction.OUTGOING)
    private Customer customer;

    @Relationship(type = "SERVICE_RELATED_TO", direction = Relationship.Direction.OUTGOING)
    private ArrayList<Device> devices;

    @Relationship(type = "SERVICE_TO_ADDITIONAL_ATTRIBUTE", direction = Relationship.Direction.OUTGOING)
    private ArrayList<AdditionalAttribute> additionalAttributes;

    //mandatory fields on UI - name & type
}

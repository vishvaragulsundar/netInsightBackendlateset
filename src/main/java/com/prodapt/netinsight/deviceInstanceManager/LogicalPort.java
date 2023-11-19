package com.prodapt.netinsight.deviceInstanceManager;

import com.prodapt.netinsight.objectModelManager.AdditionalAttribute;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;

@Getter
@Setter
@Node("LogicalPort")
public class LogicalPort {
    @Id
    @GeneratedValue
    private Long id;

    @Property("name")
    private String name;

    @Property("positionOnCard")
    private Integer positionOnCard;

    @Property("positionOnDevice")
    private Integer positionOnDevice;

    @Property("portType")
    private String portType;

    @Property("operationalState")
    private String operationalState;

    @Property("administrativeState")
    private String administrativeState;

    @Property("usageState")
    private String usageState;

    @Property("href")
    private String href;

    @Property(name = "portSpeed")
    private String portSpeed;

    @Property(name = "capacity")
    private Integer capacity;

    @Property(name = "positionOnPort")
    private Integer positionOnPort;

    @Relationship(type = "LOGICAL_PORT_TO_ADDITIONAL_ATTRIBUTE", direction = Relationship.Direction.OUTGOING)
    private ArrayList<AdditionalAttribute> additionalAttributes;


    @Override
    public String toString() {
        return "LogicalPort{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", positionOnCard=" + positionOnCard +
                ", positionOnDevice=" + positionOnDevice +
                ", portType='" + portType + '\'' +
                ", operationalState='" + operationalState + '\'' +
                ", administrativeState='" + administrativeState + '\'' +
                ", usageState='" + usageState + '\'' +
                ", href='" + href + '\'' +
                ", portSpeed='" + portSpeed + '\'' +
                ", capacity=" + capacity +
                ", positionOnPort=" + positionOnPort +
                '}';
    }
}






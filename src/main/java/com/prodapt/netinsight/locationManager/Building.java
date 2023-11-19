package com.prodapt.netinsight.locationManager;

import com.prodapt.netinsight.objectModelManager.AdditionalAttribute;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;

@Getter
@Setter
@Node("Building")
public class Building {
    @Id
    @GeneratedValue
    private Long id;

    @Property("name")
    private String name;

    @Property("clliCode")
    private String clliCode;

    @Property("phoneNumber")
    private String phoneNumber;

    @Property("contactPerson")
    private String contactPerson;

    @Property("address")
    private String address;

    @Property("latitude")
    private String latitude;

    @Property("longitude")
    private String longitude;

    @Property("drivingInstructions")
    private String drivingInstructions;

    @Property("href")
    private String href;

    @Property("notes")
    private String notes;

    @Relationship(type = "BUILDING_TO_ADDITIONAL_ATTRIBUTE", direction = Relationship.Direction.OUTGOING)
    private ArrayList<AdditionalAttribute> additionalAttributes;

    @Override
    public String toString() {
        return "Building{" +
                "id:" + id +
                ", name:'" + name + '\'' +
                ", clliCode:'" + clliCode + '\'' +
                ", phoneNumber:'" + phoneNumber + '\'' +
                ", contactPerson:'" + contactPerson + '\'' +
                ", address:'" + address + '\'' +
                ", latitude:'" + latitude + '\'' +
                ", longitude:'" + longitude + '\'' +
                ", drivingInstructions:'" + drivingInstructions + '\'' +
                '}';
    }
}

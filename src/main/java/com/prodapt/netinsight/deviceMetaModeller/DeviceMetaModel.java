package com.prodapt.netinsight.deviceMetaModeller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.ArrayList;

@Getter
@Setter
@Node("DeviceMetaModel")
public class DeviceMetaModel {
    @Id
    @GeneratedValue
    private Long id;

    @Property("deviceModel")
    private String deviceModel;

    @Property("partNumber")
    private String partNumber;

    @Property("vendor")
    private String vendor;

    @Property("height")
    private float height;

    @Property("depth")
    private float depth;

    @Property("width")
    private float width;

    @Property("shelvesContained")
    private int shelvesContained;


    @Property("numOfRackPositionOccupied")
    private int numOfRackPositionOccupied;

    @Property("allowedCardList")
    private ArrayList<String> allowedCardList;

    @Override
    public String toString() {
        return "DeviceMetaModel{" +
                "id=" + id +
                ", deviceModel='" + deviceModel + '\'' +
                ", partNumber='" + partNumber + '\'' +
                ", vendor='" + vendor + '\'' +
                ", height=" + height +
                ", depth=" + depth +
                ", width=" + width +
                ", shelvesContained=" + shelvesContained +
                ", numOfRackPositionOccupied=" + numOfRackPositionOccupied +
                ", allowedCardList=" + allowedCardList +
                '}';
    }
}
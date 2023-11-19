package com.prodapt.netinsight.locationManager;

import com.prodapt.netinsight.objectModelManager.AdditionalAttribute;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;

@Getter
@Setter
@Node("Rack")
public class Rack {
    @Id
    @GeneratedValue
    private Long id;

    @Property("name")
    private String name;

    @Property("location")
    private String location;

    @Property("height")
    private float height;

    @Property("width")
    private float width;

    @Property("depth")
    private float depth;

    @Property("numOfPositionsContained")
    private Integer numOfPositionsContained;


    @Property("reservedPositions")
    private ArrayList<Integer> reservedPositions;

    @Property("freePositions")
    private ArrayList<Integer> freePositions;

    @Property("href")
    private String href;

    @Property("notes")
    private String notes;

    @Relationship(type = "RACK_TO_ADDITIONAL_ATTRIBUTE", direction = Relationship.Direction.OUTGOING)
    private ArrayList<AdditionalAttribute> additionalAttributes;


    @Override
    public String toString() {
        return "Rack{" +
                "id:" + id +
                ", name:'" + name + '\'' +
                ", location:'" + location + '\'' +
                ", height:" + height +
                ", width:" + width +
                ", depth:" + depth +
                ", numOfPositionsContained:" + numOfPositionsContained +
                ", reservedPositions:" + reservedPositions +
                ", freePositions:" + freePositions +
                '}';
    }
}

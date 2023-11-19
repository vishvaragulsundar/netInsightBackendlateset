package com.prodapt.netinsight.locationManager;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Getter
@Setter
@Node("City")
public class City {
    @Id
    @GeneratedValue
    private Long id;

    @Property("name")
    private String name;

    @Property("notes")
    private String notes;

    @Override
    public String toString() {
        return "City{" +
                "id:" + id +
                ", name:'" + name + '\'' +
                '}';
    }
}

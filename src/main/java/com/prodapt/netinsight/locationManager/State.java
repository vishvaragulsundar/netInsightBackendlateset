package com.prodapt.netinsight.locationManager;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

@Getter
@Setter
@Node("State")
public class State {
    @Id
    @GeneratedValue
    private Long id;

    @Property("name")
    private String name;

    @Property("notes")
    private String notes;

    @Relationship(type = "COUNTRY_TO_STATE", direction = Relationship.Direction.INCOMING)
    private Country country;

    @Override
    public String toString() {
        return "State{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}

package com.prodapt.netinsight.customerManager;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Getter
@Setter
@Node("CustomerGroup")
public class CustomerGroup {
    @Id
    @GeneratedValue
    private Long id;
    @Property("name")
    private String name;

    @Property("categoryName")
    private String categoryName;

    @Property("pollingInterval")
    private int pollingInterval;

    @Property("credentialName")
    private String credentialName;

    @Property("value")
    private String value;

    @Property("description")
    private String description;


    @Override
    public String toString() {
        return "CustomerGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", pollingInterval=" + pollingInterval +
                ", credentialName='" + credentialName + '\'' +
                ", value='" + value + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
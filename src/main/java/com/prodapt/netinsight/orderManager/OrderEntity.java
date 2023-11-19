package com.prodapt.netinsight.orderManager;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Getter
@Setter
@Node("Order")
public class OrderEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Property("status")
    private String status;

    @Property("category")
    private String category;

    @Property("description")
    private String description;


    public String toString() {
        return "Order{" +
                "id:" + id +
                ", status:'" + status + '\'' +
                ", category:'" + category + '\'' +
                ", description:'" + description + '\'' +
                '}';
    }
}

package com.prodapt.netinsight.objectModelManager;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

@Getter
@Setter
@Node("AdditionalAttribute")
public class AdditionalAttribute {

    @Id
    @GeneratedValue
    private Long id;

    @Property("Key")
    private String key;

    @Property("Value")
    private String value;
}

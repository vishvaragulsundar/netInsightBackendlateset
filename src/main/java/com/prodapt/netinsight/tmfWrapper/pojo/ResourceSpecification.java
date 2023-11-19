package com.prodapt.netinsight.tmfWrapper.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResourceSpecification {
    private Long id;
    private String name;
    private String href;

    @JsonProperty("@referredType")
    private String referredType = "ResourceSpecificationRef";

    public ResourceSpecification(Long id, String deviceModel, String href) {
        this.id = id;
        this.name = deviceModel;
        this.href = href;
    }

    public ResourceSpecification(){

    }
}

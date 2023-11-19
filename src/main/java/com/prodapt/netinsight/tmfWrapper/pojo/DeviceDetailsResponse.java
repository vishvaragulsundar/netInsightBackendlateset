package com.prodapt.netinsight.tmfWrapper.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.prodapt.netinsight.deviceInstanceManager.Pluggable;
import com.prodapt.netinsight.deviceInstanceManager.Port;
import com.prodapt.netinsight.deviceInstanceManager.Shelf;
import com.prodapt.netinsight.locationManager.Building;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

@Data
@Component
public class DeviceDetailsResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("@type")
    private String type;

    @JsonProperty("isBundle")
    private Boolean isBundle = false;

    @JsonProperty("place")
    private ArrayList<Building> place;

    @JsonProperty("resourceCharacteristics")
    private ArrayList<HashMap<String, String>> resourceCharacteristics;

    @JsonProperty("operationalState")
    private String operationalState;

    @JsonProperty("administrativeState")
    private String administrativeState;

    @JsonProperty("usageState")
    private String usageState;

    @JsonProperty("shelves")
    private ArrayList<Shelf> shelves;

    @JsonProperty("devicePorts")
    private ArrayList<Port> devicePorts;

    @JsonProperty("devicePluggables")
    private ArrayList<Pluggable> devicePluggables;

    @JsonProperty("resourceSpecification")
    private ResourceSpecification resourceSpecification;

    @JsonProperty("relatedParty")
    private RelatedParty relatedParty;
}

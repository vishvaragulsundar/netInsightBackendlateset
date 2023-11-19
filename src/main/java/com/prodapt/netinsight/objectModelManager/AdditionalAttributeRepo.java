package com.prodapt.netinsight.objectModelManager;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface AdditionalAttributeRepo extends Neo4jRepository<AdditionalAttribute, Long> {

    @Query("Match (b:Building{name : $buildingName})-[:BUILDING_TO_ADDITIONAL_ATTRIBUTE]-(a:AdditionalAttribute) return a")
    ArrayList<AdditionalAttribute> getAdditionalAttributeForBuilding(@Param("buildingName") String buildingName);

    @Query("Match (r:Rack{name : $rackName})-[:RACK_TO_ADDITIONAL_ATTRIBUTE]-(a:AdditionalAttribute) return a")
    ArrayList<AdditionalAttribute> getAdditionalAttributeForRack(@Param("rackName") String rackName);

    @Query("Match (d:Device{name : $deviceName})-[:DEVICE_TO_ADDITIONAL_ATTRIBUTE]-(a:AdditionalAttribute) return a")
    ArrayList<AdditionalAttribute> getAdditionalAttributeForDevice(@Param("deviceName") String deviceName);

    @Query("Match (s:Shelf{name : $shelfName})-[:SHELF_TO_ADDITIONAL_ATTRIBUTE]-(a:AdditionalAttribute) return a")
    ArrayList<AdditionalAttribute> getAdditionalAttributeForShelf(@Param("shelfName") String shelfName);

    @Query("Match (c:Card{name : $cardName})-[:CARD_TO_ADDITIONAL_ATTRIBUTE]-(a:AdditionalAttribute) return a")
    ArrayList<AdditionalAttribute> getAdditionalAttributeForCard(@Param("cardName") String cardName);

    @Query("Match (p:Port{name : $portName})-[:PORT_TO_ADDITIONAL_ATTRIBUTE]-(a:AdditionalAttribute) return a")
    ArrayList<AdditionalAttribute> getAdditionalAttributeForPort(@Param("portName") String portName);

    @Query("Match (p:Pluggable{name : $pluggableName})-[:PLUGGABLE_TO_ADDITIONAL_ATTRIBUTE]-(a:AdditionalAttribute) return a")
    ArrayList<AdditionalAttribute> getAdditionalAttributeForPluggable(@Param("pluggableName") String pluggableName);

    @Query("Match (lp:LogicalPort{name : $logicalPortName})-[:LOGICAL_PORT_TO_ADDITIONAL_ATTRIBUTE]-(a:AdditionalAttribute) return a")
    ArrayList<AdditionalAttribute> getAdditionalAttributeForLogicalPort(@Param("logicalPortName") String logicalPortName);

    @Query("Match (pc:PhysicalConnection{name : $physicalConnectionName})-[:PHYSICAL_CONNECTION_TO_ADDITIONAL_ATTRIBUTE]-(a:AdditionalAttribute) return a")
    ArrayList<AdditionalAttribute> getAdditionalAttributeForPhysicalConnection(@Param("physicalConnectionName") String physicalConnectionName);

    @Query("Match (lp:LogicalConnection{name : $logicalConnectionName})-[:LOGICAL_CONNECTION_TO_ADDITIONAL_ATTRIBUTE]-(a:AdditionalAttribute) return a")
    ArrayList<AdditionalAttribute> getAdditionalAttributeForLogicalConnection(@Param("logicalConnectionName") String logicalConnectionName);

    @Query("Match (s:Service{name : $serviceName})-[:SERVICE_TO_ADDITIONAL_ATTRIBUTE]-(a:AdditionalAttribute) return a")
    ArrayList<AdditionalAttribute> getAdditionalAttributeForService(@Param("serviceName") String serviceName);
}

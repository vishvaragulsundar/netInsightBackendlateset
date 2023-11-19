package com.prodapt.netinsight.deviceInstanceManager;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface PluggableRepository extends Neo4jRepository<Pluggable, Long> {

    @Query("MATCH (c:Pluggable {name: $name}) return c")
    Pluggable getPluggable(String name);

    Pluggable getByName(String name);

    @Query("MATCH (c:Card {name: $cardName})-[:CARD_TO_CARD_SLOT]-(CardSlot)-[:CARD_SLOT_TO_PLUGGABLE]-(p:Pluggable {name: $name}) RETURN p")
    Pluggable getPluggable(@Param("cardName") String cardName, @Param("name") String name);

    @Query("MATCH (d:Device{name:$deviceName})-[:DEVICE_TO_PLUGGABLE]-(c:Pluggable {name: $pluggableName}) return c")
    Pluggable getPluggableOnDevice(@Param("pluggableName") String pluggableName,
                                   @Param("deviceName") String deviceName);

    Pluggable findPluggableById(@Param("id") Long id);

    @Query("MATCH (c:Pluggable {name: $name}) \n" +
            "OPTIONAL MATCH (c)-[:PLUGGABLE_TO_ADDITIONAL_ATTRIBUTE]->(a:AdditionalAttribute)\n" +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]->(c) " +
            "WITH [c, a] AS nodes\n" +
            "UNWIND nodes AS node\n" +
            "DETACH DELETE node")
    void deletePluggable(@Param("name") String name, @Param("orderId") Long orderId);

    @Query("match(d:Device{name:$device})-[:DEVICE_TO_PLUGGABLE]->(p:Pluggable) " +
            "where p.positionOnDevice=$portNo return p")
    Pluggable getPluggableOnDeviceByNumber(@Param("device") String device, @Param("portNo") Integer portNo);

    @Query("match(c:CardSlot{name:$cardSlot})-[:CARD_SLOT_TO_PLUGGABLE]->(p:Pluggable) " +
            "where p.positionOnCard=$portNo return p")
    Pluggable getPluggableOnCardByNumber(@Param("cardSlot") String cardSlot, @Param("portNo") Integer portNo);

    @Query("Match (d:Device{name:$deviceName})-[:DEVICE_TO_PLUGGABLE]->(p:Pluggable) return p")
    ArrayList<Pluggable> getPluggablesOnDevice(@Param("deviceName") String deviceName);

    @Query("Match (d:Device{name: $deviceName})-[:DEVICE_TO_SHELF]-(Shelf)-[:SHELF_TO_SLOT]-(Slot)-[:SLOT_TO_CARD]-(Card)-[:CARD_TO_CARD_SLOT]-(CardSlot)-[:CARD_SLOT_TO_PLUGGABLE]-(p:Pluggable) return p")
    ArrayList<Pluggable> getAllPluggablesOnDevice(@Param("deviceName") String deviceName);

    @Query("MERGE (c:Pluggable{name:$pluggableName}) ON CREATE SET c.positionOnCard = $positionOnCard," +
            "c.pluggableModel=$pluggableModel,c.vendor=$vendor,c.pluggablePartNumber=$pluggablePartNumber," +
            "c.operationalState=$operationalState, c.administrativeState=$administrativeState," +
            "c.usageState=$usageState \n" +
            "with c \n" +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]->(c) " +
            "with c \n" +
            "MATCH (s:CardSlot{name:$cardSlotName}) " +
            "MERGE (s)-[:CARD_SLOT_TO_PLUGGABLE]->(c) " +
            "return c")
    Pluggable createPluggable(@Param("pluggableName") String pluggableName,
                              @Param("positionOnCard") Integer positionOnCard,
                              @Param("vendor") String vendor, @Param("pluggableModel") String pluggableModel,
                              @Param("pluggablePartNumber") String pluggablePartNumber,
                              @Param("operationalState") String operationalState,
                              @Param("administrativeState") String administrativeState,
                              @Param("usageState") String usageState,
                              @Param("cardSlotName") String cardSlotName,
                              @Param("orderId") Long orderId);

    @Query("MERGE (c:Pluggable{name:$pluggableName}) ON MATCH SET c.positionOnCard = $positionOnCard," +
            "c.pluggableModel=$pluggableModel,c.vendor=$vendor,c.pluggablePartNumber=$pluggablePartNumber," +
            "c.operationalState=$operationalState, c.administrativeState=$administrativeState," +
            "c.usageState=$usageState \n" +
            "with c \n" +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]->(c) " +
            "with c \n" +
            "MATCH (s:CardSlot{name:$cardSlotName}) " +
            "MERGE (s)-[:CARD_SLOT_TO_PLUGGABLE]->(c) " +
            " return c")
    Pluggable updatePluggable(@Param("pluggableName") String pluggableName,
                              @Param("positionOnCard") Integer positionOnCard,
                              @Param("vendor") String vendor, @Param("pluggableModel") String pluggableModel,
                              @Param("pluggablePartNumber") String pluggablePartNumber,
                              @Param("operationalState") String operationalState,
                              @Param("administrativeState") String administrativeState,
                              @Param("usageState") String usageState,
                              @Param("cardSlotName") String cardSlotName,
                              @Param("orderId") Long orderId);

    @Query("MERGE (c:Pluggable{name:$pluggableName}) ON CREATE SET c.positionOnDevice = $positionOnDevice," +
            "c.pluggableModel=$pluggableModel,c.vendor=$vendor,c.pluggablePartNumber=$pluggablePartNumber," +
            "c.operationalState=$operationalState, c.administrativeState=$administrativeState," +
            "c.usageState=$usageState \n" +
            "with c \n" +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]->(c) " +
            "with c \n" +
            "MATCH (d:Device{name:$deviceName}) " +
            "MERGE (d)-[:DEVICE_TO_PLUGGABLE]->(c) " +
            "return c")
    Pluggable createPluggableOnDevice(@Param("pluggableName") String pluggableName,
                                      @Param("positionOnDevice") Integer positionOnDevice,
                                      @Param("vendor") String vendor, @Param("pluggableModel") String pluggableModel,
                                      @Param("pluggablePartNumber") String pluggablePartNumber,
                                      @Param("operationalState") String operationalState,
                                      @Param("administrativeState") String administrativeState,
                                      @Param("usageState") String usageState,
                                      @Param("deviceName") String deviceName,
                                      @Param("orderId") Long orderId);

    @Query("MERGE (c:Pluggable{name:$pluggableName}) ON MATCH SET c.positionOnDevice = $positionOnDevice," +
            "c.pluggableModel=$pluggableModel,c.vendor=$vendor,c.pluggablePartNumber=$pluggablePartNumber," +
            "c.operationalState=$operationalState, c.administrativeState=$administrativeState," +
            "c.usageState=$usageState \n" +
            "with c \n" +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]->(c) " +
            "with c \n" +
            "MATCH (d:Device{name:$deviceName}) " +
            "MERGE (d)-[:DEVICE_TO_PLUGGABLE]->(c) " +
            "return c")
    Pluggable updatePluggableOnDevice(@Param("pluggableName") String pluggableName,
                                      @Param("positionOnDevice") Integer positionOnDevice,
                                      @Param("vendor") String vendor, @Param("pluggableModel") String pluggableModel,
                                      @Param("pluggablePartNumber") String pluggablePartNumber,
                                      @Param("operationalState") String operationalState,
                                      @Param("administrativeState") String administrativeState,
                                      @Param("usageState") String usageState,
                                      @Param("deviceName") String deviceName,
                                      @Param("orderId") Long orderId);

    @Query("match (c:CardSlot{name:$cardSlot})-[:CARD_SLOT_TO_PLUGGABLE]->(p:Pluggable) return p")
    Pluggable getPluggableOnCardSlot(@Param("cardSlot") String cardSlot);

    @Query("match(p:Pluggable)-[:PHYSICAL_TO_LOGICAL_PORT]->(l:LogicalPort{name:$logicalPortName}) return p")
    Pluggable getPluggableFromLogicalPortName(@Param("logicalPortName") String logicalPortName);


    /**
     * This method is used to get all pluggables related to given card name
     *
     * @param name contains the card name
     * @return Pluggable entities
     */
    @Query("match (d:Device{name: $deviceName})-[:DEVICE_TO_SHELF]-(Shelf)-[:SHELF_TO_SLOT]-(Slot)-[:SLOT_TO_CARD]-(c:Card{name: $name})-[:CARD_TO_CARD_SLOT]-(CardSlot)-[:CARD_SLOT_TO_PLUGGABLE]-(p:Pluggable) return p")
    ArrayList<Pluggable> getAllPluggablesOnCard(@Param("deviceName") String deviceName, @Param("name") String name);

    @Query("Match(p:Pluggable{name:$pluggableName}) return id(p)")
    Long getIdByName(@Param("pluggableName") String pluggableName);

    @Query("match(s:Slot{name:$slotName})-[:SLOT_TO_CARD]-(c:Card)-[:CARD_TO_CARD_SLOT]-(:CardSlot)-[:CARD_SLOT_TO_PLUGGABLE]-(p:Pluggable) return p")
    ArrayList<Pluggable> getAllPluggableOnCardBySlot(@Param("slotName") String slotName);

}

package com.prodapt.netinsight.deviceInstanceManager;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface PortRepository extends Neo4jRepository<Port, Long> {

    @Query("MATCH (c:Port {name: $name}) return c")
    Port getPort(String name);

    Port getByName(String name);

    @Query("MATCH (d:Device {name: $deviceName})-[:DEVICE_TO_SHELF]-(:Shelf)-[:SHELF_TO_SLOT]-(:Slot)-[:SLOT_TO_CARD]-(:Card)-[:CARD_TO_CARD_SLOT]-(:CardSlot)-[:CARD_SLOT_TO_PORT]-(c:Port {name: $portName})\n" +
            "RETURN c \n" +
            "UNION\n" +
            "MATCH (d:Device {name: $deviceName})-[:DEVICE_TO_PORT]-(c:Port {name: $portName})\n" +
            "RETURN c\n")
    Port getPort(@Param("deviceName") String deviceName, @Param("portName") String portName);

    @Query("MATCH (d:Card{name: $cardName})-[:CARD_TO_CARD_SLOT]-(:CardSlot)-[:CARD_SLOT_TO_PORT]-(c:Port {name: $portName}) return c")
    Port getPortOnCard(@Param("cardName") String cardName, @Param("portName") String portName);

    Port findPortById(@Param("id") Long id);

    @Query("MATCH (c:Port {name: $name}) \n" +
            "OPTIONAL MATCH (c)-[:PORT_TO_ADDITIONAL_ATTRIBUTE]->(a:AdditionalAttribute)\n" +
            "WITH [c, a] AS nodes\n" +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]->(c) " +
            "WITH nodes\n" +
            "UNWIND nodes AS node\n" +
            "DETACH DELETE node")
    void deletePort(@Param("name") String name, @Param("orderId") Long orderId);

    @Query("match(d:Device{name:$device})-[:DEVICE_TO_PORT]->(p:Port) " +
            "where p.positionOnDevice=$portNo return p")
    Port getPortOnDeviceByNumber(@Param("device") String device, @Param("portNo") Integer portNo);

    @Query("match(c:CardSlot{name:$cardSlot})-[:CARD_SLOT_TO_PORT]->(p:Port) " +
            "where p.positionOnCard=$portNo return p")
    Port getPortOnCardByNumber(@Param("cardSlot") String cardSlot, @Param("portNo") Integer portNo);

    @Query("Match (d:Device{name:$deviceName})-[:DEVICE_TO_PORT]->(p:Port) return p")
    ArrayList<Port> getPortsOnDevice(@Param("deviceName") String deviceName);

    @Query("Match (d:Device{name:$deviceName})-[:DEVICE_TO_SHELF]-(Shelf)-[:SHELF_TO_SLOT]-(Slot)-[:SLOT_TO_CARD]-(Card)-[:CARD_TO_CARD_SLOT]-(CardSlot)-[:CARD_SLOT_TO_PORT]-(p:Port) return p")
    ArrayList<Port> getAllPortsOnDevice(@Param("deviceName") String deviceName);

    @Query("MERGE (c:Port{name:$PortName}) ON CREATE SET c.positionOnDevice = $positionOnDevice," +
            "c.portType=$portType," +
            "c.operationalState=$operationalState, c.administrativeState=$administrativeState," +
            "c.usageState=$usageState \n" +
            "with c \n" +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]->(c) " +
            "WITH c \n" +
            "MATCH (d:Device{name:$deviceName}) " +
            "MERGE (d)-[:DEVICE_TO_PORT]->(c) " +
            "return c")
    Port createPortOnDevice(@Param("PortName") String PortName,
                            @Param("positionOnDevice") Integer positionOnDevice,
                            @Param("portType") String portType,
                            @Param("operationalState") String operationalState,
                            @Param("administrativeState") String administrativeState,
                            @Param("usageState") String usageState,
                            @Param("deviceName") String deviceName,
                            @Param("orderId") Long orderId);

    @Query("MERGE (c:Port{name:$PortName}) ON MATCH SET c.positionOnDevice = $positionOnDevice," +
            "c.portType=$portType," +
            "c.operationalState=$operationalState, c.administrativeState=$administrativeState," +
            "c.usageState=$usageState \n" +
            "with c \n" +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]->(c) " +
            "WITH c \n" +
            "MATCH (d:Device{name:$deviceName}) " +
            "MERGE (d)-[:DEVICE_TO_PORT]->(c) " +
            "return c")
    Port updatePortOnDevice(@Param("PortName") String PortName,
                            @Param("positionOnDevice") Integer positionOnDevice,
                            @Param("portType") String portType,
                            @Param("operationalState") String operationalState,
                            @Param("administrativeState") String administrativeState,
                            @Param("usageState") String usageState,
                            @Param("deviceName") String deviceName,
                            @Param("orderId") Long orderId);

    @Query("MERGE (c:Port{name:$PortName}) ON CREATE SET c.positionOnCard = $positionOnCard," +
            "c.portType=$portType," +
            "c.operationalState=$operationalState, c.administrativeState=$administrativeState," +
            "c.usageState=$usageState \n" +
            "with c \n" +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]->(c) " +
            "WITH c \n" +
            "MATCH (s:CardSlot{name:$cardSlotName}) " +
            "MERGE (s)-[:CARD_SLOT_TO_PORT]->(c) " +
            "return c")
    Port createPort(@Param("PortName") String PortName,
                    @Param("positionOnCard") Integer positionOnCard,
                    @Param("portType") String portType,
                    @Param("operationalState") String operationalState,
                    @Param("administrativeState") String administrativeState,
                    @Param("usageState") String usageState,
                    @Param("cardSlotName") String cardSlotName,
                    @Param("orderId") Long orderId);


    @Query("MERGE (c:Port{name:$PortName}) ON MATCH SET c.positionOnCard = $positionOnCard," +
            "c.portType=$portType," +
            "c.operationalState=$operationalState, c.administrativeState=$administrativeState," +
            "c.usageState=$usageState \n" +
            "with c \n" +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]->(c) " +
            "WITH c \n" +
            "MATCH (s:CardSlot{name:$cardSlotName}) " +
            "MERGE (s)-[:CARD_SLOT_TO_PORT]->(c) " +
            "return c")
    Port updatePort(@Param("PortName") String PortName,
                    @Param("positionOnCard") Integer positionOnCard,
                    @Param("portType") String portType,
                    @Param("operationalState") String operationalState,
                    @Param("administrativeState") String administrativeState,
                    @Param("usageState") String usageState,
                    @Param("cardSlotName") String cardSlotName,
                    @Param("orderId") Long orderId);

    @Query("match(c:CardSlot{name:$cardSlot})-[:CARD_SLOT_TO_PORT]->(p:Port) return p")
    Port getPortOnCardSlot(@Param("cardSlot") String cardSlot);

    @Query("match(s:Slot{name:$slotName})-[:SLOT_TO_CARD]-(c:Card)-[:CARD_TO_CARD_SLOT]-(:CardSlot)-[:CARD_SLOT_TO_PORT]-(p:Port) return p")
    ArrayList<Port> getAllPortsOnCardBySlot(@Param("slotName") String slotName);

    @Query("match (d:Device {name: $deviceName})-[:DEVICE_TO_SHELF]-(:Shelf)-[:SHELF_TO_SLOT]-(:Slot)-[:SLOT_TO_CARD]-(c:Card{name: $cardName})-[:CARD_TO_CARD_SLOT]-(:CardSlot)-[:CARD_SLOT_TO_PORT]-(p:Port) return p")
    ArrayList<Port> getAllPortsOnCard(@Param("deviceName") String deviceName, @Param("cardName") String cardName);

    @Query("match(p:Port)-[:PHYSICAL_TO_LOGICAL_PORT]->(l:LogicalPort{name:$logicalPortName}) return p")
    Port getPortFromLogicalPortName(@Param("logicalPortName") String logicalPortName);

    @Query("Match(p:Port{name:$portName}) return id(p)")
    Long getIdByName(@Param("portName") String portName);
}

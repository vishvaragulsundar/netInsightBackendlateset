package com.prodapt.netinsight.deviceInstanceManager;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface LogicalPortRepository extends Neo4jRepository<LogicalPort, Long> {

    @Query("MATCH (c:LogicalPort {name: $name}) return c")
    LogicalPort getPort(String name);

    LogicalPort getByName(String name);

    LogicalPort findPortById(@Param("id") Long id);

    @Query("MATCH (c:LogicalPort {name: $name}) " +
            "OPTIONAL MATCH (c)-[:PORT_TO_ADDITIONAL_ATTRIBUTE]->(a:AdditionalAttribute)\n" +
            "WITH [c, a] AS nodes\n" +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]-(d) " +
            "WITH nodes\n" +
            "UNWIND nodes AS node\n" +
            "DETACH DELETE node")
    void deletePort(@Param("name") String name, @Param("orderId") Long orderId);

    @Query("match(d:Device{name:$device})-[:DEVICE_TO_PORT]->(p:Port{positionOnDevice:$portNo})-[:PHYSICAL_TO_LOGICAL_PORT]->(lp:LogicalPort) " +
            " return lp")
    ArrayList<LogicalPort> getLogicalPortOnDeviceByNumber(@Param("device") String device,
                                                          @Param("portNo") Integer portNo);

    @Query("match(c:CardSlot{name:$cardSlot})-[:CARD_SLOT_TO_PORT]->(p:Port{positionOnCard:$portNo})-[:PHYSICAL_TO_LOGICAL_PORT]->(lp:LogicalPort) " +
            " return lp")
    ArrayList<LogicalPort> getLogicalPortOnCardByNumber(@Param("cardSlot") String cardSlot, @Param("portNo") Integer portNo);

    @Query("Match (d:Device{name:$deviceName})-[:DEVICE_TO_PORT]->(p:Port)-[:PHYSICAL_TO_LOGICAL_PORT]->(lp:LogicalPort) " +
            "return lp")
    ArrayList<LogicalPort> getLogicalPortsOnDevice(@Param("deviceName") String deviceName);

    @Query("MERGE (c:LogicalPort{name:$PortName}) ON CREATE SET c.positionOnDevice = $positionOnDevice," +
            "c.portType=$portType, c.positionOnPort = $positionOnPort," +
            "c.operationalState=$operationalState, c.administrativeState=$administrativeState," +
            "c.usageState=$usageState, " +
            "c.portSpeed=$portSpeed, c.capacity=$capacity \n" +
            "with c \n" +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]->(c) " +
            "with c \n" +
            "match(d:Device{name:$deviceName})-[:DEVICE_TO_PORT]->(p:Port{positionOnDevice:$positionOnDevice}) " +
            "MERGE (p)-[:PHYSICAL_TO_LOGICAL_PORT]->(c) " +
            "return c")
    LogicalPort createLogicalPortOnDevice(@Param("PortName") String PortName,
                                          @Param("positionOnDevice") Integer positionOnDevice,
                                          @Param("portType") String portType,
                                          @Param("operationalState") String operationalState,
                                          @Param("administrativeState") String administrativeState,
                                          @Param("usageState") String usageState,
                                          @Param("portSpeed") String portSpeed, @Param("capacity") Integer capacity,
                                          @Param("deviceName") String deviceName,
                                          @Param("positionOnPort") Integer positionOnPort,
                                          @Param("orderId") Long orderId);

    @Query("Match (c:LogicalPort{name:$PortName})<-[r:PHYSICAL_TO_LOGICAL_PORT]-(p:Port)" +
            " SET c.positionOnDevice = $positionOnDevice," +
            "c.portType=$portType, c.positionOnPort = $positionOnPort," +
            "c.operationalState=$operationalState, c.administrativeState=$administrativeState," +
            "c.usageState=$usageState, c.portSpeed=$portSpeed," +
            "c.capacity=$capacity \n" +
            "with c,r,p \n" +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]->(c) " +
            "with c,r,p \n" +
            "match(d:Device{name:$deviceName})-[:DEVICE_TO_PORT]->(p1:Port{positionOnDevice:$positionOnDevice}) " +
            "WHERE p.positionOnDevice <> $positionOnDevice DETACH DELETE r MERGE (p1)-[:PHYSICAL_TO_LOGICAL_PORT]->(c)" +
            "return c")
    LogicalPort updateLogicalPortOnDevice(@Param("PortName") String PortName,
                                          @Param("positionOnDevice") Integer positionOnDevice,
                                          @Param("portType") String portType,
                                          @Param("operationalState") String operationalState,
                                          @Param("administrativeState") String administrativeState,
                                          @Param("usageState") String usageState,
                                          @Param("portSpeed") String portSpeed, @Param("capacity") Integer capacity,
                                          @Param("deviceName") String deviceName,
                                          @Param("positionOnPort") Integer positionOnPort,
                                          @Param("orderId") Long orderId);

    @Query("MERGE (c:LogicalPort{name:$PortName}) ON CREATE SET c.positionOnCard = $positionOnCard, \n" +
            "            c.portType=$portType, \n" +
            "            c.operationalState=$operationalState,c.positionOnPort=$positionOnPort ,c.administrativeState=$administrativeState, \n" +
            "            c.usageState=$usageState \n" +
            "WITH c \n" +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]->(c) " +
            "with c \n" +
            "OPTIONAL MATCH " +
            "(s:CardSlot{name:$cardSlotName})-[:CARD_SLOT_TO_PORT]->" +
            "(port:Port{positionOnCard:$positionOnCard}) \n" +
            " with c, port \n" +
            "OPTIONAL MATCH " +
            "(s:CardSlot{name:$cardSlotName})-[:CARD_SLOT_TO_PLUGGABLE]->" +
            "(pluggable:Pluggable{positionOnCard:$positionOnCard}) \n" +
            " with c, port, pluggable \n" +
            "FOREACH (p in CASE WHEN port IS NOT NULL THEN [1] ELSE [] END | \n" +
            "    MERGE (port)-[:PHYSICAL_TO_LOGICAL_PORT]->(c))\n" +
            " with c, port, pluggable \n" +
            "FOREACH (p in CASE WHEN pluggable IS NOT NULL THEN [1] ELSE [] END | \n" +
            "    MERGE (pluggable)-[:PHYSICAL_TO_LOGICAL_PORT]->(c))\n" +
            "RETURN c")
    LogicalPort createLogicalPortOnCard(@Param("PortName") String PortName,
                                        @Param("positionOnCard") Integer positionOnCard,
                                        @Param("portType") String portType,
                                        @Param("operationalState") String operationalState,
                                        @Param("administrativeState") String administrativeState,
                                        @Param("usageState") String usageState,
                                        @Param("cardSlotName") String cardSlotName,
                                        @Param("positionOnPort") Integer positionOnPort,
                                        @Param("orderId") Long orderId);

    @Query("MATCH (c:LogicalPort{name:$PortName}) ON CREATE SET c.positionOnCard = $positionOnCard, \n" +
            "            c.portType=$portType, \n" +
            "            c.operationalState=$operationalState, c.administrativeState=$administrativeState, \n" +
            "            c.usageState=$usageState \n" +
            "WITH c \n" +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]->(c) " +
            "with c \n" +
            "OPTIONAL MATCH " +
            "(s:CardSlot{name:$cardSlotName})-[:CARD_SLOT_TO_PORT]->" +
            "(port:Port{positionOnCard:$positionOnCard}) \n" +
            " with c, port \n" +
            "OPTIONAL MATCH " +
            "(s:CardSlot{name:$cardSlotName})-[:CARD_SLOT_TO_PLUGGABLE]->" +
            "(pluggable:Pluggable{positionOnCard:$positionOnCard}) \n" +
            " with c, port, pluggable \n" +
            "FOREACH (p in CASE WHEN port IS NOT NULL THEN [1] ELSE [] END | \n" +
            "    MERGE (port)-[:PHYSICAL_TO_LOGICAL_PORT]->(c))\n" +
            " with c, port, pluggable \n" +
            "FOREACH (p in CASE WHEN pluggable IS NOT NULL THEN [1] ELSE [] END | \n" +
            "    MERGE (pluggable)-[:PHYSICAL_TO_LOGICAL_PORT]->(c))\n" +
            "RETURN c")
    LogicalPort updateLogicalPortOnCard(@Param("PortName") String PortName,
                                        @Param("positionOnCard") Integer positionOnCard,
                                        @Param("portType") String portType,
                                        @Param("operationalState") String operationalState,
                                        @Param("administrativeState") String administrativeState,
                                        @Param("usageState") String usageState,
                                        @Param("cardSlotName") String cardSlotName,
                                        @Param("orderId") Long orderId);


    @Query("MATCH (c:LogicalPort{name:$PortName}) SET c.positionOnCard = $positionOnCard," +
            "c.positionOnDevice = $positionOnDevice, c.portType=$portType," +
            "c.operationalState=$operationalState, c.administrativeState=$administrativeState," +
            "c.usageState=$usageState " +
            "WITH c " +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]->(c) " +
            "return c")
    LogicalPort updateLogicalPort(@Param("PortName") String PortName,
                                  @Param("positionOnCard") Integer positionOnCard,
                                  @Param("positionOnDevice") Integer positionOnDevice,
                                  @Param("portType") String portType,
                                  @Param("operationalState") String operationalState,
                                  @Param("administrativeState") String administrativeState,
                                  @Param("usageState") String usageState,
                                  @Param("orderId") Long orderId);

    @Query("match(c:CardSlot{name:$cardSlot})-[:CARD_SLOT_TO_PORT]->(p:Port)-[:PHYSICAL_TO_LOGICAL_PORT]->(c) return c")
    ArrayList<LogicalPort> getLogicalPortOnCardSlot(@Param("cardSlot") String cardSlot);

    @Query("match(c:Card)-[:CARD_TO_CARD_SLOT]-(cs:CardSlot)-[:CARD_SLOT_TO_PORT]->" +
            "(p:Port)-[:PHYSICAL_TO_LOGICAL_PORT]->(lp:LogicalPort) where c.name=$cardName AND (lp.name = $logicalPortName OR lp.positionOnPort = $positionOnPort) return lp")
    ArrayList<LogicalPort> getLogicPortsOnCard(@Param("cardName") String cardName,
                                               @Param("logicalPortName") String logicalPortName,
                                               @Param("positionOnPort") Integer positionOnPort);

    // LogicalPort on Pluggable
    @Query("match(d:Device{name:$device})-[:DEVICE_TO_PLUGGABLE]->(p:Pluggable{positionOnDevice:$portNo})-[:PHYSICAL_TO_LOGICAL_PORT]->(lp:LogicalPort)  " +
            " return lp")
    ArrayList<LogicalPort> getLogicalPluggableOnDeviceByNumber(@Param("device") String device,
                                                               @Param("portNo") Integer portNo);

    @Query("match(c:CardSlot{name:$cardSlot})-[:CARD_SLOT_TO_PLUGGABLE]->(p:Pluggable{positionOnCard:$portNo})-[:PHYSICAL_TO_LOGICAL_PORT]->(lp:LogicalPort) " +
            " return lp")
    ArrayList<LogicalPort> getLogicalPluggableOnCardByNumber(@Param("cardSlot") String cardSlot, @Param("portNo") Integer portNo);

    @Query("Match (d:Device{name:$deviceName})-[:DEVICE_TO_PLUGGABLE]->(p:Pluggable)-[:PHYSICAL_TO_LOGICAL_PORT]->(lp:LogicalPort) " +
            " return p")
    ArrayList<LogicalPort> getLogicalPluggablesOnDevice(@Param("deviceName") String deviceName);

    @Query("MERGE (c:Pluggable{name:$pluggableName}) ON CREATE SET c.positionOnDevice = $positionOnDevice," +
            "c.pluggableModel=$pluggableModel,c.vendor=$vendor,c.pluggablePartNumber=$pluggablePartNumber," +
            "c.operationalState=$operationalState, c.administrativeState=$administrativeState," +
            "c.usageState=$usageState \n" +
            "with c \n" +
            "MATCH (d:Device{name:$deviceName})-[:DEVICE_TO_PLUGGABLE]->(p:Pluggable{positionOnDevice:$positionOnDevice}) " +
            "MERGE (p)-[:PHYSICAL_TO_LOGICAL_PORT]->(c) " +
            "return c")
    LogicalPort createLogicalPluggableOnDevice(@Param("pluggableName") String pluggableName,
                                               @Param("positionOnDevice") Integer positionOnDevice,
                                               @Param("vendor") String vendor, @Param("pluggableModel") String pluggableModel,
                                               @Param("pluggablePartNumber") String pluggablePartNumber,
                                               @Param("operationalState") String operationalState,
                                               @Param("administrativeState") String administrativeState,
                                               @Param("usageState") String usageState,
                                               @Param("deviceName") String deviceName);

    @Query("MATCH (c:Pluggable{name:$pluggableName}) SET c.positionOnDevice = $positionOnDevice," +
            "c.pluggableModel=$pluggableModel,c.vendor=$vendor,c.pluggablePartNumber=$pluggablePartNumber," +
            "c.operationalState=$operationalState, c.administrativeState=$administrativeState," +
            "c.usageState=$usageState \n" +
            "with c \n" +
            "MATCH (d:Device{name:$deviceName})-[:DEVICE_TO_PLUGGABLE]->(p:Pluggable{positionOnDevice:$positionOnDevice}) " +
            "MERGE (p)-[:PHYSICAL_TO_LOGICAL_PORT]->(c) " +
            "return c")
    LogicalPort updateLogicalPluggableOnDevice(@Param("pluggableName") String pluggableName,
                                               @Param("positionOnDevice") Integer positionOnDevice,
                                               @Param("vendor") String vendor, @Param("pluggableModel") String pluggableModel,
                                               @Param("pluggablePartNumber") String pluggablePartNumber,
                                               @Param("operationalState") String operationalState,
                                               @Param("administrativeState") String administrativeState,
                                               @Param("usageState") String usageState,
                                               @Param("deviceName") String deviceName);

    @Query("match(c:CardSlot{name:$cardSlot})-[:CARD_SLOT_TO_PLUGGABLE]->(p:Pluggable)-[:PHYSICAL_TO_LOGICAL_PORT]->(c) return c")
    LogicalPort getLogicalPluggableOnCardSlot(@Param("cardSlot") String cardSlot);

    @Query("Optional match(n:Device{name:$deviceName})-[]-(s:Shelf{shelfPosition:$shelfPosition})-[]-(sl:Slot{slotPosition:$slotPosition})" +
            "-[]-(c:Card)-[]-(cs:CardSlot)-[]-(p:Port{positionOnCard:$portPosition})-[]-(l:LogicalPort{positionOnPort:$positionOnPort}) " +
            "return l")
    LogicalPort getLogicalOnCardPort(@Param("deviceName") String deviceName, @Param("shelfPosition") Integer shelfPosition,
                                     @Param("slotPosition") Integer slotPosition, @Param("portPosition") Integer portPosition,
                                     @Param("positionOnPort") Integer positionOnPort);

    @Query("Optional Match (d:Device{name:$deviceName})-[:DEVICE_TO_PORT]->(p:Port{positionOnDevice:$positionOnDevice})" +
            "-[:PHYSICAL_TO_LOGICAL_PORT]->(lp:LogicalPort{positionOnPort:$positionOnPort}) " +
            "Optional Match (d:Device{name:$deviceName})-[:DEVICE_TO_PLUGGABLE]->(p:Pluggable{positionOnDevice:$positionOnDevice})" +
            "-[:PHYSICAL_TO_LOGICAL_PORT]->(lp:LogicalPort{positionOnPort:$positionOnPort}) " +
            "return lp")
    LogicalPort getLogicalPortOnDevice(@Param("deviceName") String deviceName, @Param("positionOnDevice") Integer positionOnDevice,
                                       @Param("positionOnPort") Integer positionOnPort);

    @Query("MATCH (n:Device)-[:DEVICE_TO_SHELF]-(S:Shelf)-[:SHELF_TO_SLOT]-(Sl:Slot)-[:SLOT_TO_CARD]-(C:Card)-[:CARD_TO_CARD_SLOT]-(cs:CardSlot)-[:CARD_SLOT_TO_PORT|:CARD_SLOT_TO_PLUGGABLE]-(P)-[:PHYSICAL_TO_LOGICAL_PORT]-(L:LogicalPort) " +
            "WHERE P.name= $Name RETURN L")
    ArrayList<LogicalPort> findLogicalPortsOnCardRelation(@Param("Name") String name);

    @Query("MATCH (n:Device)-[:DEVICE_TO_PORT|:DEVICE_TO_PLUGGABLE]-(P)-[:PHYSICAL_TO_LOGICAL_PORT]-(Lp:LogicalPort)" +
            "WHERE P.name = $Name " +
            "RETURN Lp")
    ArrayList<LogicalPort> findLogicalPortDevice(@Param("Name") String name);
}

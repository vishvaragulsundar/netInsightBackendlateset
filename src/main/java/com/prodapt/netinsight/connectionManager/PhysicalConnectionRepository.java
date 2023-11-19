package com.prodapt.netinsight.connectionManager;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface PhysicalConnectionRepository extends Neo4jRepository<PhysicalConnection, Long> {

    @Query("Match (phy:PhysicalConnection{name:$connectionName}) return phy")
    PhysicalConnection getPhysicalConnection(@Param("connectionName") String connectionName);

    @Query("Match (phy:PhysicalConnection{name:$connectionName}) \n" +
            "OPTIONAL MATCH (phy)-[:PHYSICAL_CONNECTION_TO_ADDITIONAL_ATTRIBUTE]->(a:AdditionalAttribute)\n" +
            "WITH [phy, a] AS nodes\n" +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]->(phy) " +
            "WITH nodes\n" +
            "UNWIND nodes AS node\n" +
            "DETACH DELETE node")
    void deletePhysicalConnection(@Param("connectionName") String connectionName, @Param("orderId") Long orderId);

    @Query("Merge (phy:PhysicalConnection{name:$connectionName}) ON CREATE SET phy.deviceA=$deviceA," +
            "phy.deviceZ=$deviceZ, phy.deviceAPort=$deviceAPort, phy.deviceZPort=$deviceZPort, " +
            "phy.connectionType=$connectionType, phy.bandwidth=$bandwidth \n" +
            "With phy \n" +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]->(phy) " +
            "With phy \n" +
            "Match (a:Pluggable{name:$pluggableA})" +
            "Merge (a)-[:PHYSICALLY_CONNECTED]->(phy) " +
            "With phy \n" +
            "Match (z:Pluggable{name:$pluggableZ})" +
            "Merge (z)-[:PHYSICALLY_CONNECTED]->(phy) " +
            "With phy \n" +
            "Match (devA:Device{name:$deviceA})" +
            "Merge (devA)-[:DEVICE_TO_PHYSICAL]->(phy) " +
            "With phy \n" +
            "Match (devZ:Device{name:$deviceZ})" +
            "Merge (devZ)-[:DEVICE_TO_PHYSICAL]->(phy) " +
            "return phy")
    PhysicalConnection createPhysicalConnectionOnPluggable(@Param("connectionName") String connectionName,
                                                           @Param("deviceA") String deviceA,
                                                           @Param("deviceZ") String deviceZ,
                                                           @Param("deviceAPort") String deviceAPort,
                                                           @Param("deviceZPort") String deviceZPort,
                                                           @Param("connectionType") String connectionType,
                                                           @Param("bandwidth") Integer bandwidth,
                                                           @Param("pluggableA") String pluggableA,
                                                           @Param("pluggableZ") String pluggableZ,
                                                           @Param("orderId") Long orderId);

    @Query("Merge (phy:PhysicalConnection{name:$connectionName}) ON CREATE SET phy.deviceA=$deviceA," +
            "phy.deviceZ=$deviceZ, phy.deviceAPort=$deviceAPort, phy.deviceZPort=$deviceZPort, " +
            "phy.connectionType=$connectionType, phy.bandwidth=$bandwidth \n" +
            "With phy \n" +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]->(phy) " +
            "With phy \n" +
            "Match (devA:Device{name:$deviceA})" +
            "Merge (devA)-[:DEVICE_TO_PHYSICAL]->(phy) " +
            "With phy \n" +
            "Match (devZ:Device{name:$deviceZ})" +
            "Merge (devZ)-[:DEVICE_TO_PHYSICAL]->(phy) " +
            "With phy \n" +
            "Match (a:Port{name:$portA})" +
            "Merge (a)-[:PHYSICALLY_CONNECTED]->(phy) " +
            "With phy \n" +
            "Match (z:Port{name:$portZ})" +
            "Merge (z)-[:PHYSICALLY_CONNECTED]->(phy) " +
            "return phy")
    PhysicalConnection createPhysicalConnectionOnPort(@Param("connectionName") String connectionName,
                                                      @Param("deviceA") String deviceA,
                                                      @Param("deviceZ") String deviceZ,
                                                      @Param("deviceAPort") String deviceAPort,
                                                      @Param("deviceZPort") String deviceZPort,
                                                      @Param("connectionType") String connectionType,
                                                      @Param("bandwidth") Integer bandwidth,
                                                      @Param("portA") String portA,
                                                      @Param("portZ") String portZ,
                                                      @Param("orderId") Long orderId);

    @Query("Match (p:PhysicalConnection{name:$connectionName}) set p.connectionType=$connectionType, " +
            "p.bandwidth=$bandwidth " +
            "WITH p\n" +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]->(p) " +
            "return p")
    PhysicalConnection updatePhysicalConnection(@Param("connectionName") String connectionName,
                                                @Param("connectionType") String connectionType,
                                                @Param("bandwidth") Integer bandwidth,
                                                @Param("orderId") Long orderId);

    @Query("MATCH (d:Device {name: $deviceName}) " +
            "OPTIONAL MATCH (d)-[:DEVICE_TO_PLUGGABLE]-(:Pluggable)-[:PHYSICALLY_CONNECTED]-(phy1:PhysicalConnection) " +
            "OPTIONAL MATCH (d)-[:DEVICE_TO_PORT]-(:Port)-[:PHYSICALLY_CONNECTED]-(phy2:PhysicalConnection) " +
            "OPTIONAL MATCH (d)-[:DEVICE_TO_CARD]-(:Card)-[:CARD_TO_CARD_SLOT]-(cs:CardSlot)-" +
            "[:CARD_SLOT_TO_PORT|CARD_SLOT_TO_PLUGGABLE]-(pp:Port)-[:PHYSICALLY_CONNECTED]-(phy3:PhysicalConnection) " +
            "RETURN phy1, phy2, phy3")
    ArrayList<PhysicalConnection> validateDeviceForDeletion(@Param("deviceName") String deviceName);

    @Query("Match (d:Card{name:$cardName})-[:CARD_TO_CARD_SLOT]-(:Card)-[:CARD_SLOT_TO_PLUGGABLE]-(:Pluggable)-[:PHYSICALLY_CONNECTED]->(phy:PhysicalConnection) return phy")
    ArrayList<PhysicalConnection> validateCardForDeletion(@Param("cardName") String cardName);

    @Query("Match (d:Pluggable{name: $pluggableName})-[:PHYSICALLY_CONNECTED]->(phy:PhysicalConnection) return phy")
    ArrayList<PhysicalConnection> validatePluggableForDeletion(@Param("pluggableName") String pluggableName);

    @Query("Match (d:Port {name: $portName})-[:PHYSICALLY_CONNECTED]->(phy:PhysicalConnection) return phy")
    ArrayList<PhysicalConnection> validatePortForDeletion(@Param("portName") String portName);

    @Query("Optional match(d:Device)-[:DEVICE_TO_PHYSICAL]-(PhysicalConnection{name:$connectionName}) " +
            "return d.name")
    ArrayList<String> getDevicesForConnection(@Param("connectionName") String connectionName);

    @Query("match(n:PhysicalConnection) return n")
    ArrayList<PhysicalConnection> getAllPhysicalConnections();

    @Query("MATCH (deviceA:Device {name: $deviceA}) " +
            "MATCH (deviceZ:Device {name: $deviceZ})  " +
            "MATCH shortestPath = allShortestPaths((deviceA)-[:DEVICE_TO_PHYSICAL*]-(deviceZ)) " +
            "WITH nodes(shortestPath) as physicalConnections " +
            "UNWIND physicalConnections as node " +
            "WITH node " +
            "WHERE \"PhysicalConnection\" in labels(node) " +
            "RETURN COLLECT(node)")
    ArrayList<PhysicalConnection> getShortestPathPhysicalConnections(@Param("deviceA") String deviceA,
                                                                     @Param("deviceZ") String deviceZ);

    @Query("MATCH (d:Device)-[:DEVICE_TO_PHYSICAL]-(p:PhysicalConnection) WHERE d.name = $deviceName RETURN p")
    ArrayList<PhysicalConnection> findPhysicalConnectionByDevice(@Param("deviceName") String deviceName);

    @Query("match(N:PhysicalConnection) where N.name= $Connection return N")
    PhysicalConnection findPhysicalconnectionbyName(@Param("Connection") String Connection);

    PhysicalConnection getByName(String name);


}

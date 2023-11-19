package com.prodapt.netinsight.connectionManager;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface LogicalConnectionRepository extends Neo4jRepository<LogicalConnection, Long> {

    @Query("Match (phy:LogicalConnection{name:$connectionName}) return phy")
    LogicalConnection getLogicalConnection(@Param("connectionName") String connectionName);

    LogicalConnection getByName(String name);

    @Query("Match (phy:LogicalConnection{name:$connectionName}) \n" +
            "OPTIONAL MATCH (phy)-[:LOGICAL_CONNECTION_TO_ADDITIONAL_ATTRIBUTE]->(a:AdditionalAttribute)\n" +
            "WITH [phy, a] AS nodes\n" +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]->(phy) " +
            "WITH nodes\n" +
            "UNWIND nodes AS node\n" +
            "DETACH DELETE node")
    void deleteLogicalConnection(@Param("connectionName") String connectionName, @Param("orderId") Long orderId);

    @Query("Merge (phy:LogicalConnection{name:$connectionName}) ON CREATE SET phy.deviceA=$deviceA," +
            "phy.deviceZ=$deviceZ, phy.deviceAPort=$deviceAPort, phy.deviceZPort=$deviceZPort, " +
            "phy.connectionType=$connectionType, phy.bandwidth=$bandwidth, phy.devicesConnected=$devicesConnected \n" +
            "With phy \n" +
            "Match (a:Pluggable{name:$pluggableA})" +
            "Merge (a)-[:LOGICALLY_CONNECTED]->(phy) " +
            "With phy \n" +
            "Match (z:Pluggable{name:$pluggableZ})" +
            "Merge (z)-[:LOGICALLY_CONNECTED]->(phy) " +
            "return phy")
    LogicalConnection createLogicalConnectionOnPluggable(@Param("connectionName") String connectionName,
                                                         @Param("deviceA") String deviceA,
                                                         @Param("deviceZ") String deviceZ,
                                                         @Param("deviceAPort") String deviceAPort,
                                                         @Param("deviceZPort") String deviceZPort,
                                                         @Param("connectionType") String connectionType,
                                                         @Param("bandwidth") Integer bandwidth,
                                                         @Param("pluggableA") String pluggableA,
                                                         @Param("pluggableZ") String pluggableZ,
                                                         @Param("devicesConnected") ArrayList<String> devicesConnected);

    @Query("Merge (phy:PhysicalConnection{name:$connectionName}) ON CREATE SET phy.deviceA=$deviceA," +
            "phy.deviceZ=$deviceZ, phy.deviceAPort=$deviceAPort, phy.deviceZPort=$deviceZPort, " +
            "phy.connectionType=$connectionType, phy.bandwidth=$bandwidth, phy.devicesConnected=$devicesConnected \n" +
            "With phy \n" +
            "Match (a:Port{name:$portA})" +
            "Merge (a)-[:PHYSICALLY_CONNECTED]->(phy) " +
            "With phy \n" +
            "Match (z:Port{name:$portZ})" +
            "Merge (z)-[:PHYSICALLY_CONNECTED]->(phy) " +
            "return phy")
    LogicalConnection createLogicalConnectionOnPort(@Param("connectionName") String connectionName,
                                                    @Param("deviceA") String deviceA,
                                                    @Param("deviceZ") String deviceZ,
                                                    @Param("deviceAPort") String deviceAPort,
                                                    @Param("deviceZPort") String deviceZPort,
                                                    @Param("connectionType") String connectionType,
                                                    @Param("bandwidth") Integer bandwidth,
                                                    @Param("portA") String portA,
                                                    @Param("portZ") String portZ,
                                                    @Param("devicesConnected") ArrayList<String> devicesConnected,
                                                    @Param("orderId") Long orderId);

    @Query("Match (l:LogicalConnection{name:$connectionName}) set l.connectionType=$connectionType, l.bandwidth=$bandwidth " +
            "WITH l\n" +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]->(l) " +
            "return l")
    LogicalConnection updateLogicalConnection(@Param("connectionName") String connectionName,
                                              @Param("connectionType") String connectionType,
                                              @Param("bandwidth") Integer bandwidth,
                                              @Param("orderId") Long orderId);


    @Query("Merge (lc:LogicalConnection{name:$connectionName}) ON CREATE SET lc.deviceA=$deviceA,lc.deviceZ=$deviceZ, " +
            "lc.deviceALogicalPort=$deviceALogicalPort, lc.deviceZLogicalPort=$deviceZLogicalPort, lc.connectionType=$connectionType, " +
            "lc.bandwidth=$bandwidth, lc.devicesConnected=$devicesConnected, lc.physicalConnections=$physicalConnections " +
            "With lc \n" +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]->(lc) " +
            "With lc \n" +
            "Match (a:LogicalPort{name:$deviceALogicalPort})" +
            "Merge (a)-[:LOGICALLY_CONNECTED]->(lc) " +
            "With lc \n" +
            "Match (z:LogicalPort{name:$deviceZLogicalPort})" +
            "Merge (z)-[:LOGICALLY_CONNECTED]->(lc) " +
            "return lc")
    LogicalConnection createLogicalConnectionOnPort(@Param("connectionName") String connectionName,
                                                    @Param("deviceA") String deviceA,
                                                    @Param("deviceZ") String deviceZ,
                                                    @Param("deviceALogicalPort") String deviceALogicalPort,
                                                    @Param("deviceZLogicalPort") String deviceZLogicalPort,
                                                    @Param("connectionType") String connectionType,
                                                    @Param("bandwidth") Integer bandwidth,
                                                    @Param("devicesConnected") ArrayList<String> devicesConnected,
                                                    @Param("physicalConnections") ArrayList<String> physicalConnections,
                                                    @Param("orderId") Long orderId);

    @Query("match(n:LogicalConnection) return n")
    ArrayList<LogicalConnection> getAllLogicalConnections();

    @Query("MATCH (n:Device {name: $device}) " +
            "OPTIONAL MATCH (n)-[r1:DEVICE_TO_PORT|DEVICE_TO_PLUGGABLE]-(p1)-[:PHYSICAL_TO_LOGICAL_PORT]-(l1:LogicalPort)-[:LOGICALLY_CONNECTED]-(lc1:LogicalConnection) " +
            "WITH n, COALESCE(COLLECT(lc1), []) AS collectedLc1 " +
            "OPTIONAL MATCH (n)-[:DEVICE_TO_SHELF]-(s)-[:SHELF_TO_SLOT]-(sl)-[:SLOT_TO_CARD]-(c)-[:CARD_TO_CARD_SLOT]-(cs)-[:CARD_SLOT_TO_PORT|:CARD_SLOT_TO_PLUGGABLE]-(p2)-[:PHYSICAL_TO_LOGICAL_PORT]-(l2:LogicalPort)-[:LOGICALLY_CONNECTED]-(lc2:LogicalConnection) " +
            "WITH n, collectedLc1, COALESCE(COLLECT(lc2), []) AS collectedLc2 " +
            "WITH collectedLc1 + collectedLc2 AS allConnections " +
            "UNWIND allConnections AS logicalConnection " +
            "RETURN logicalConnection")
    ArrayList<LogicalConnection> findLogicalConnectionByDevice(@Param("device") String device);

    @Query("Match (d:LogicalPort {name: $portName})-[:LOGICALLY_CONNECTED]->(log:LogicalConnection) return log")
    ArrayList<LogicalConnection> validateLogicalPortForDeletion(@Param("portName") String portName);

}

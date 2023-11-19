package com.prodapt.netinsight.serviceManager;

import com.prodapt.netinsight.deviceInstanceManager.Device;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface ServiceRepository extends Neo4jRepository<Service, Long> {

    Service findByName(String name);

    @Query("MERGE (s:Service {name: $serviceName}) " +
            "ON CREATE SET s.type = $serviceType, s.operationalState = $operationalState, s.administrativeState = $administrativeState," +
            " s. notes = $notes, s.physicalConnections = $physicalConnections, s.logicalConnections = $logicalConnections " +
            "WITH s " +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]->(s) " +
            "WITH s " +
            "OPTIONAL MATCH (l:LogicalConnection) WHERE l.name IN $logicalConnections " +
            "WITH s, COLLECT(l) AS logicals " +
            "OPTIONAL MATCH (p:PhysicalConnection) WHERE p.name IN $physicalConnections " +
            "WITH s, logicals, COLLECT(p) AS physicals " +
            "FOREACH (logical IN logicals | MERGE (logical)-[:LOGICAL_TO_SERVICE]->(s)) " +
            "FOREACH (physical IN physicals | MERGE (physical)-[:PHYSICAL_TO_SERVICE]->(s)) " +
            "RETURN s")
    Service createService(@Param("serviceName") String serviceName, @Param("serviceType") String serviceType,
                          @Param("operationalState") String operationalState, @Param("administrativeState") String administrativeState,
                          @Param("notes") String notes, @Param("physicalConnections") ArrayList<String> physicalConnections,
                          @Param("logicalConnections") ArrayList<String> logicalConnections,
                          @Param("orderId") Long orderId);

    @Query("MATCH (s:Service {name: $name})\n" +
            "MATCH (o:Order) WHERE id(o) = $orderId\n" +
            "MERGE (o)-[:ORDER_FOR]->(s)\n" +
            "DETACH DELETE s")
    void deleteService(@Param("name") String name, @Param("orderId") Long orderId);

    @Query("MATCH (n:Device {name: $deviceName})\n" +
            "OPTIONAL MATCH (n)-[r1:DEVICE_TO_PORT|DEVICE_TO_PLUGGABLE]-(p1)-[:PHYSICAL_TO_LOGICAL_PORT]-(l1:LogicalPort)-[:LOGICALLY_CONNECTED]-(lc1:LogicalConnection)-[:LOGICAL_TO_SERVICE]-(s:Service)\n" +
            "WITH n, COLLECT(s) AS collectedService1\n" +
            "OPTIONAL MATCH (n)-[r2:DEVICE_TO_PORT|DEVICE_TO_PLUGGABLE]-(p2)-[:PHYSICALLY_CONNECTED]-(pc1:PhysicalConnection)-[:PHYSICAL_TO_SERVICE]-(s1:Service)\n" +
            "WITH n, collectedService1, COLLECT(s1) AS collectedService2\n" +
            "OPTIONAL MATCH (n)-[:DEVICE_TO_SHELF]-(s)-[:SHELF_TO_SLOT]-(sl)-[:SLOT_TO_CARD]-(c)-[:CARD_TO_CARD_SLOT]-(cs)-[:CARD_SLOT_TO_PORT|:CARD_SLOT_TO_PLUGGABLE]-(p3)-[:PHYSICAL_TO_LOGICAL_PORT]-(l3:LogicalPort)-[:LOGICALLY_CONNECTED]-(lc2:LogicalConnection)-[:LOGICAL_TO_SERVICE]-(s2:Service)\n" +
            "WITH n, collectedService1, collectedService2, COLLECT(s2) AS collectedService3\n" +
            "OPTIONAL MATCH (n)-[:DEVICE_TO_SHELF]-(s)-[:SHELF_TO_SLOT]-(sl)-[:SLOT_TO_CARD]-(c)-[:CARD_TO_CARD_SLOT]-(cs)-[:CARD_SLOT_TO_PORT|:CARD_SLOT_TO_PLUGGABLE]-(p4)-[:PHYSICALLY_CONNECTED]-(pc2:PhysicalConnection)-[:PHYSICAL_TO_SERVICE]-(s3:Service)\n" +
            "WITH n, collectedService1, collectedService2, collectedService3, COLLECT(s3) AS collectedService4\n" +
            "WITH collectedService1 + collectedService2 + collectedService3 + collectedService4 AS allServices\n" +
            "UNWIND allServices AS service\n" +
            "RETURN service\n")
    ArrayList<Service> getAllServices(@Param("deviceName") String deviceName);

    @Query("Optional Match (l:LogicalConnection) where not $logicalConnections = [] and l.name in $logicalConnections " +
            "with l " +
            "Optional match (s:Service {name: $name}) " +
            "WITH s " +
            "Optional match (l)-[r:LOGICAL_TO_SERVICE]-(s) where l is not null DELETE r " +
            "WITH s " +
            "OPTIONAL MATCH (log:LogicalConnection) WHERE log.name IN $newLogicalConnections " +
            "WITH s, COLLECT(log) AS logicals " +
            "FOREACH (logical IN logicals | MERGE (logical)-[:LOGICAL_TO_SERVICE]->(s))")
    void updateLogicalConnections(@Param("name") String serviceName, @Param("logicalConnections") ArrayList<String> logicalConnections,
                                  @Param("newLogicalConnections") ArrayList<String> newLogicalConnections);

    @Query("Optional Match (p:PhysicalConnection) where not $physicalConnections = [] and p.name in $physicalConnections " +
            "with p " +
            "Optional match (s:Service {name: $name}) " +
            "WITH s " +
            "Optional match (p)-[r:PHYSICAL_TO_SERVICE]-(s) where p is not null DELETE r " +
            "WITH s " +
            "OPTIONAL MATCH (phy:PhysicalConnection) WHERE phy.name IN $newPhysicalConnections " +
            "WITH s, COLLECT(phy) AS physicals " +
            "FOREACH (physical IN physicals | MERGE (physical)-[:PHYSICAL_TO_SERVICE]->(s))")
    void updatePhysicalConnections(@Param("name") String serviceName, @Param("physicalConnections") ArrayList<String> physicalConnections,
                                   @Param("newPhysicalConnections") ArrayList<String> newPhysicalConnections);

    @Query("MATCH (s:Service {name: $serviceName})\n" +
            "SET s.type = $serviceType,\n" +
            "    s.operationalState = $operationalState,\n" +
            "    s.administrativeState = $administrativeState,\n" +
            "    s.notes = $notes,\n" +
            "    s.physicalConnections = $physicalConnections,\n" +
            "    s.logicalConnections = $logicalConnections\n" +
            "WITH s\n" +
            "MATCH (o:Order)\n" +
            "WHERE id(o) = $orderId\n" +
            "MERGE (o)-[:ORDER_FOR]->(s)\n" +
            "RETURN s")
    Service updateService(@Param("serviceName") String serviceName, @Param("serviceType") String serviceType,
                          @Param("operationalState") String operationalState, @Param("administrativeState") String administrativeState,
                          @Param("notes") String notes, @Param("physicalConnections") ArrayList<String> physicalConnections,
                          @Param("logicalConnections") ArrayList<String> logicalConnections,
                          @Param("orderId") Long orderId);

    @Query("Match (s:Service{name:$serviceName}) return s")
    Service getServiceByName(@Param("serviceName") String serviceName);

    @Query("MATCH (s:Service {name: $serviceName})\n" +
            "SET s.type = $serviceType,\n" +
            "    s.operationalState = $operationalState,\n" +
            "    s.administrativeState = $administrativeState,\n" +
            "    s.notes = $notes,\n" +
            "    s.customer = $customerName,\n" +
            "    s.devices = $deviceName\n" +
            "WITH s\n" +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]->(s) " +
            "WITH s " +
            "OPTIONAL MATCH (c:Customer{name : $customerName})\n" +
            "WITH s, COLLECT(c) AS customers\n" +
            "OPTIONAL MATCH (d:Device) WHERE d.name IN $deviceName\n" +
            "WITH s, customers, COLLECT(d) AS devices\n" +
            "FOREACH (customer IN customers | MERGE (customer)<-[:SERVICE_RELATED_TO]-(s))\n" +
            "FOREACH (device IN devices | MERGE (device)<-[:SERVICE_RELATED_TO]-(s))\n" +
            "RETURN s")
    Service associateServiceToOtherAttributes(@Param("serviceName") String serviceName, @Param("serviceType") String serviceType,
                                              @Param("operationalState") String operationalState, @Param("administrativeState") String administrativeState,
                                              @Param("notes") String notes, @Param("customerName") String customerName, @Param("deviceName") ArrayList<String> deviceName, @Param("orderId") Long orderId);

    @Query("MATCH (n:Service) WHERE n.name CONTAINS $name RETURN n.name")
    ArrayList<String> findServiceByNameContaining(@Param("name") String name);

    @Query("OPTIONAL MATCH (c:Customer)\n" +
            "WHERE c.name = $customerName\n" +
            "WITH c\n" +
            "OPTIONAL MATCH (s:Service {name: $name})\n" +
            "WITH s\n" +
            "OPTIONAL MATCH (c)<-[r:SERVICE_RELATED_TO]-(s)\n" +
            "WHERE c IS NOT NULL\n" +
            "DELETE r\n")
    void deleteExistingCustomerRelation(@Param("customerName") String customerName, @Param("name") String serviceName);

    @Query("Optional Match (d:Device) where not $devices = [] and d.name in $devices " +
            "with d " +
            "Optional match (s:Service {name: $name}) " +
            "WITH s " +
            "Optional match (d)<-[r:SERVICE_RELATED_TO]-(s) where d is not null DELETE r")
    void deleteExistingDeviceRelation(@Param("devices") ArrayList<Device> devices, @Param("name") String serviceName);

}

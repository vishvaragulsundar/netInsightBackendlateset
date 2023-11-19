package com.prodapt.netinsight.deviceInstanceManager;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface DeviceRepository extends Neo4jRepository<Device, Long> {

    @Query("match(d:Device)-[:RACK_TO_DEVICE]-(r:Rack{name:$rackName}) return d")
    List<Device> getDevicesInRack(@Param("rackName") String rackName);

    @Query("match(d:Device{name:$deviceName})-[:RACK_TO_DEVICE]-(r:Rack{name:$rackName}) return d")
    Device getDeviceInRack(@Param("rackName") String rackName, @Param("deviceName") String deviceName);

    Device findDevicesById(@Param("id") Long id);

    @Query("match(d:Device)-[:BUILDING_TO_DEVICE]-(b:Building{name:$buildingName}) return d")
    List<Device> getDevicesInBuilding(@Param("buildingName") String buildingName);

    @Query("match(d:Device{name:$name}) return d")
    Device findByName(@Param("name") String name);

    @Query("match(d:Device{id:$id}) return d")
    Device getById(@Param("id") Long id);

    Device getByName(String name);

    @Query("WITH $numberOfShelves as numShelves \n" +
            "MERGE (d:Device {name: $name}) ON CREATE SET d.name = $name, d.managementIp = $managementIp, " +
            "d.deviceModel= $deviceModel, " +
            "d.location= $location," +
            "d.organisation= $organisation, d.customer= $customer, d.rackPosition= $rackPosition, " +
            "d.operationalState= $operationalState, d.administrativeState= $administrativeState, " +
            "d.usageState= $usageState, d.serialNumber= $serialNumber \n" +
            "WITH d, numShelves " +
            "UNWIND range(1, numShelves) as shelfNumber " +
            "MERGE (s:Shelf {name: $name + \"_shelf_\" + shelfNumber})\n" +
            "ON CREATE SET s.shelfPosition = shelfNumber, s.operationalState = \"unassigned\", " +
            "s.administrativeState = \"unassigned\", s.usageState = \"unassigned\"\n" +
            "MERGE (d)-[:DEVICE_TO_SHELF]->(s)")
    void createDevice(@Param("name") String name, @Param("deviceModel") String deviceModel,
                      @Param("location") String location, @Param("organisation") String organisation,
                      @Param("customer") String customer, @Param("managementIp") String managementIp,
                      @Param("rackPosition") String rackPosition,
                      @Param("operationalState") String operationalState,
                      @Param("administrativeState") String administrativeState,
                      @Param("usageState") String usageState,
                      @Param("serialNumber") String serialNumber,
                      @Param("numberOfShelves") int numberOfShelves);

    @Query("WITH $numberOfShelves as numShelves \n" +
            "MATCH (d:Device {name: $name}) SET d.name = $name, d.managementIp = $managementIp, " +
            "d.deviceModel= $deviceModel, d.location= $location, " +
            "d.organisation= $organisation, d.customer= $customer, d.rackPosition= $rackPosition, " +
            "d.operationalState= $operationalState, d.administrativeState= $administrativeState, " +
            "d.usageState= $usageState, d.serialNumber= $serialNumber \n" +
            "WITH d, numShelves " +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]-(d) " +
            "WITH d, numShelves " +
            "UNWIND range(1, numShelves) as shelfNumber " +
            "MERGE (s:Shelf {name: $name + \"_shelf_\" + shelfNumber})\n" +
            "ON CREATE SET s.shelfPosition = shelfNumber, s.operationalState = \"unassigned\", " +
            "s.administrativeState = \"unassigned\", s.usageState = \"unassigned\"\n" +
            "MERGE (d)-[:DEVICE_TO_SHELF]->(s)")
    void updateDevice(@Param("name") String name, @Param("deviceModel") String deviceModel,
                      @Param("location") String location, @Param("organisation") String organisation,
                      @Param("customer") String customer, @Param("managementIp") String managementIp,
                      @Param("rackPosition") String rackPosition,
                      @Param("operationalState") String operationalState,
                      @Param("administrativeState") String administrativeState,
                      @Param("usageState") String usageState,
                      @Param("serialNumber") String serialNumber,
                      @Param("numberOfShelves") int numberOfShelves,
                      @Param("orderId") Long orderId);

    @Query("WITH $numberOfShelves as numShelves \n" +
            "MERGE (d:Device {name: $name}) ON CREATE SET d.name = $name, d.managementIp = $managementIp, " +
            "d.deviceModel= $deviceModel, " +
            "d.location= $location," +
            "d.organisation= $organisation, d.customer= $customer, d.rackPosition= $rackPosition, " +
            "d.operationalState= $operationalState, d.administrativeState= $administrativeState, " +
            "d.usageState= $usageState, d.serialNumber= $serialNumber \n" +
            "WITH d, numShelves " +
            "MATCH (r:Rack{name:$rackName})" +
            "MERGE (r)-[:RACK_TO_DEVICE]->(d) " +
            "WITH d, numShelves " +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]-(d) " +
            "WITH d, numShelves " +
            "UNWIND range(1, numShelves) as shelfNumber " +
            "MERGE (s:Shelf {name: $name + \"_shelf_\" + shelfNumber})\n" +
            "ON CREATE SET s.shelfPosition = shelfNumber, s.operationalState = \"unassigned\", " +
            "s.administrativeState = \"unassigned\", s.usageState = \"unassigned\"\n" +
            "MERGE (d)-[:DEVICE_TO_SHELF]->(s)")
    void createDeviceOnRack(@Param("rackName") String rackName, @Param("name") String name,
                            @Param("deviceModel") String deviceModel,
                            @Param("location") String location, @Param("organisation") String organisation,
                            @Param("customer") String customer, @Param("managementIp") String managementIp,
                            @Param("rackPosition") String rackPosition,
                            @Param("operationalState") String operationalState,
                            @Param("administrativeState") String administrativeState,
                            @Param("usageState") String usageState,
                            @Param("serialNumber") String serialNumber,
                            @Param("numberOfShelves") int numberOfShelves,
                            @Param("orderId") Long orderId);


    @Query("WITH $numberOfShelves as numShelves \n" +
            "MATCH (d:Device {name: $name}) SET d.name = $name, d.managementIp = $managementIp, " +
            "d.deviceModel= $deviceModel, d.location= $location, " +
            "d.organisation= $organisation, d.customer= $customer, d.rackPosition= $rackPosition, " +
            "d.operationalState= $operationalState, d.administrativeState= $administrativeState, " +
            "d.usageState= $usageState, d.serialNumber= $serialNumber \n" +
            "WITH d, numShelves " +
            "MATCH (r:Rack{name:$rackName})" +
            "MERGE (r)-[:RACK_TO_DEVICE]->(d) " +
            "WITH d, numShelves " +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]-(d) " +
            "WITH d, numShelves " +
            "UNWIND range(1, numShelves) as shelfNumber " +
            "MERGE (s:Shelf {name: $name + \"_shelf_\" + shelfNumber})\n" +
            "ON CREATE SET s.shelfPosition = shelfNumber, s.operationalState = \"unassigned\", " +
            "s.administrativeState = \"unassigned\", s.usageState = \"unassigned\"\n" +
            "MERGE (d)-[:DEVICE_TO_SHELF]->(s)")
    void updateDeviceOnRack(@Param("rackName") String rackName, @Param("name") String name,
                            @Param("deviceModel") String deviceModel,
                            @Param("location") String location, @Param("organisation") String organisation,
                            @Param("customer") String customer, @Param("managementIp") String managementIp,
                            @Param("rackPosition") String rackPosition,
                            @Param("operationalState") String operationalState,
                            @Param("administrativeState") String administrativeState,
                            @Param("usageState") String usageState,
                            @Param("serialNumber") String serialNumber,
                            @Param("numberOfShelves") int numberOfShelves,
                            @Param("orderId") Long orderId);


    @Query("WITH $numberOfShelves as numShelves \n" +
            "MERGE (d:Device {name: $name}) ON CREATE SET d.name = $name, d.managementIp = $managementIp, " +
            "d.deviceModel= $deviceModel, " +
            "d.location= $location," +
            "d.organisation= $organisation, d.customer= $customer, d.rackPosition= $rackPosition, " +
            "d.operationalState= $operationalState, d.administrativeState= $administrativeState, " +
            "d.usageState= $usageState, d.serialNumber= $serialNumber \n" +
            "WITH d, numShelves " +
            "MATCH (r:Building{name:$buildingName})" +
            "MERGE (r)-[:BUILDING_TO_DEVICE]->(d) " +
            "WITH d, numShelves " +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]->(d) " +
            "WITH d, numShelves " +
            "UNWIND range(1, numShelves) as shelfNumber " +
            "MERGE (s:Shelf {name: $name + \"_shelf_\" + shelfNumber})\n" +
            "ON CREATE SET s.shelfPosition = shelfNumber, s.operationalState = \"unassigned\", " +
            "s.administrativeState = \"unassigned\", s.usageState = \"unassigned\"\n" +
            "MERGE (d)-[:DEVICE_TO_SHELF]->(s) ")
    void createDeviceOnBuilding(@Param("buildingName") String buildingName, @Param("name") String name,
                                @Param("deviceModel") String deviceModel,
                                @Param("location") String location, @Param("organisation") String organisation,
                                @Param("customer") String customer, @Param("managementIp") String managementIp,
                                @Param("rackPosition") String rackPosition,
                                @Param("operationalState") String operationalState,
                                @Param("administrativeState") String administrativeState,
                                @Param("usageState") String usageState,
                                @Param("serialNumber") String serialNumber,
                                @Param("numberOfShelves") int numberOfShelves,
                                @Param("orderId") Long orderId);

    @Query("WITH $numberOfShelves as numShelves \n" +
            "MATCH (d:Device {name: $name}) SET d.name = $name, d.managementIp = $managementIp, " +
            "d.deviceModel= $deviceModel, d.location= $location, " +
            "d.organisation= $organisation, d.customer= $customer, d.rackPosition= $rackPosition, " +
            "d.operationalState= $operationalState, d.administrativeState= $administrativeState, " +
            "d.usageState= $usageState, d.serialNumber= $serialNumber \n" +
            "WITH d, numShelves " +
            "MATCH (r:Building{name:$buildingName})" +
            "MERGE (r)-[:BUILDING_TO_DEVICE]->(d) " +
            "WITH d, numShelves " +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]-(d) " +
            "WITH d, numShelves " +
            "UNWIND range(1, numShelves) as shelfNumber " +
            "MERGE (s:Shelf {name: $name + \"_shelf_\" + shelfNumber})\n" +
            "ON CREATE SET s.shelfPosition = shelfNumber, s.operationalState = \"unassigned\", " +
            "s.administrativeState = \"unassigned\", s.usageState = \"unassigned\"\n" +
            "MERGE (d)-[:DEVICE_TO_SHELF]->(s)")
    void updateDeviceOnBuilding(@Param("buildingName") String buildingName, @Param("name") String name,
                                @Param("deviceModel") String deviceModel,
                                @Param("location") String location, @Param("organisation") String organisation,
                                @Param("customer") String customer, @Param("managementIp") String managementIp,
                                @Param("rackPosition") String rackPosition,
                                @Param("operationalState") String operationalState,
                                @Param("administrativeState") String administrativeState,
                                @Param("usageState") String usageState,
                                @Param("serialNumber") String serialNumber,
                                @Param("numberOfShelves") int numberOfShelves,
                                @Param("orderId") Long orderId);

    @Query("Match (d:Device{name:$deviceName}) " +
            "Match (r:Rack{name:$rackName}) " +
            "Merge (d)-[:RACK_TO_DEVICE]-(r) return d")
    Device attachDeviceOnRack(@Param("deviceName") String deviceName, @Param("rackName") String rackName);

    @Query("Match (d:Device{name:$deviceName}) detach delete d")
    void deleteDevice(@Param("deviceName") String deviceName);

    @Query("Match (d:Device{name:$deviceName}) " +
            "Match (r:Building{name:$buildingName}) " +
            "Merge (d)-[:BUILDING_TO_DEVICE]-(r) return d")
    Device attachDeviceOnBuilding(@Param("deviceName") String deviceName, @Param("buildingName") String buildingName);

    @Query("MATCH (c:Country {name: $name})-[:COUNTRY_TO_STATE]-(:State)-[:STATE_TO_CITY]-(:City)-[:CITY_TO_BUILDING]-(:Building)-[:BUILDING_TO_RACK]-(:Rack)-[:RACK_TO_DEVICE]-(d:Device)\n" +
            "RETURN d")
    ArrayList<Device> validateCountryForDeletion(@Param("name") String name);

    @Query("MATCH (c:State {name:$name})-[:STATE_TO_CITY]-(city)-[:CITY_TO_BUILDING]-(building)-[:BUILDING_TO_RACK]-(rack)-[:RACK_TO_DEVICE]->(d:Device) RETURN d")
    ArrayList<Device> validateStateForDeletion(@Param("name") String name);

    @Query("MATCH (c:City {name: $name})-[:CITY_TO_BUILDING]-(building)-[:BUILDING_TO_RACK]-(rack)-[:RACK_TO_DEVICE]->(d:Device)\n" +
            "RETURN d")
    ArrayList<Device> validateCityForDeletion(@Param("name") String name);

    @Query("MATCH (c:Building {name: $name})-[:BUILDING_TO_RACK]-(rack)-[:RACK_TO_DEVICE]->(d:Device) RETURN d")
    ArrayList<Device> validateBuildingForDeletion(@Param("name") String name);

    @Query("MATCH (c:Rack {name: $name})-[:RACK_TO_DEVICE]->(d:Device) RETURN d")
    ArrayList<Device> validateRackForDeletion(@Param("name") String name);

    @Query("MATCH (d:Device {name: $name})\n" +
            "MATCH (o:Order) where id(o) = $orderId\n" +
            "MERGE (o)-[:ORDER_FOR]->(d)\n" +
            "with d\n" +
            "OPTIONAL MATCH (d)-[:DEVICE_TO_SHELF]->(s:Shelf)\n" +
            "OPTIONAL MATCH (s)-[:SHELF_TO_SLOT]->(sl:Slot)\n" +
            "OPTIONAL MATCH (sl)-[:SLOT_TO_CARD]->(c:Card)\n" +
            "OPTIONAL MATCH (c)-[:CARD_TO_CARD_SLOT]->(cs:CardSlot)\n" +
            "OPTIONAL MATCH (cs)-[:CARD_SLOT_TO_PLUGGABLE]->(p:Pluggable)\n" +
            "OPTIONAL MATCH (cs)-[:CARD_SLOT_TO_PORT]->(port:Port)\n" +
            "OPTIONAL MATCH (d)-[:DEVICE_TO_PORT]->(po:Port)\n" +
            "OPTIONAL MATCH (d)-[:DEVICE_TO_PLUGGABLE]->(pl:Pluggable)\n" +
            "OPTIONAL MATCH (d)-[:DEVICE_TO_ADDITIONAL_ATTRIBUTE]->(a:AdditionalAttribute)\n" +
            "WITH [d, s, sl, c, cs, p, po, pl, port, a] AS nodes\n" +
            "UNWIND nodes AS node\n" +
            "DETACH DELETE node")
    void deleteDeviceAndAllRelatedNodes(@Param("name") String name,
                                        @Param("orderId") Long orderId);

    @Query("MATCH (d:Device)-[:DEVICE_TO_SHELF]-()-[SHELF_TO_SLOT]-()-[:SLOT_TO_CARD]-(c:Card) where c.name = $cardName return d")
    Device findByCardName(@Param("cardName") String cardName);

    @Query("MATCH (d:Device)-[]-(p:Pluggable) where p.name = $pluggableName return d")
    Device findByPluggableName(@Param("pluggableName") String pluggableName);

    @Query("MATCH (d:Device)-[]-(p:Port) where p.name = $portName return d")
    Device findByPortName(@Param("portName") String portName);

    @Query("match(d:Device{name:$deviceName}) return d.deviceModel")
    public String getDeviceModel(@Param("deviceName") String deviceName);

    @Query("Match(d:Device{name:$deviceName}) return id(d)")
    Long getIdByName(@Param("deviceName") String deviceName);

    @Query("Match (d:Device)-[:SERVICE_RELATED_TO]-(s:Service{name:$serviceName}) return d")
    ArrayList<Device> getDeviceForService(@Param("serviceName") String serviceName);

    @Query("MATCH (n:Device) WHERE n.name CONTAINS $name RETURN n.name")
    ArrayList<String> findDeviceByNameContaining(@Param("name") String name);
}

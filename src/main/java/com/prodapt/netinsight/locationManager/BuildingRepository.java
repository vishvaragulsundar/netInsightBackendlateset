package com.prodapt.netinsight.locationManager;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface BuildingRepository extends Neo4jRepository<Building, Long> {

    Building findByName(String name);

    Building findBuildingById(@Param("id") Long id);

    @Query("match(c:City {name: $cityName})-[:CITY_TO_BUILDING]->(b:Building)" +
            "return b")
    ArrayList<Building> findByCity(@Param("cityName") String cityName);

    @Query("MERGE (d:Building {name: $buildingName}) ON CREATE SET d.name = $buildingName, d.clliCode = $clliCode," +
            "d.phoneNumber = $phoneNumber, d.contactPerson = $contactPerson, d.address = $address," +
            "d.latitude = $latitude, d.longitude = $longitude, d.drivingInstructions = $drivingInstructions," +
            "d.notes=$notes " +
            "WITH d " +
            "match(c:City {name: $cityName}) " +
            "MERGE (c)-[:CITY_TO_BUILDING]->(d)" +
            "return d")
    Building createBuilding(@Param("cityName") String cityName, @Param("buildingName") String buildingName,
                            @Param("clliCode") String clliCode,
                            @Param("phoneNumber") String phoneNumber,
                            @Param("contactPerson") String contactPerson, @Param("address") String address,
                            @Param("latitude") String latitude,
                            @Param("longitude") String longitude,
                            @Param("drivingInstructions") String drivingInstructions,
                            @Param("notes") String notes);

    @Query("MERGE (d:Building {name: $buildingName}) ON MATCH SET d.name = $buildingName, d.clliCode = $clliCode," +
            "d.phoneNumber = $phoneNumber, d.contactPerson = $contactPerson, d.address = $address," +
            "d.latitude = $latitude, d.longitude = $longitude, d.drivingInstructions = $drivingInstructions," +
            "d.notes=$notes " +
            "WITH d " +
            "match(c:City {name: $cityName}) " +
            "MERGE (c)-[:CITY_TO_BUILDING]->(d)" +
            "return d")
    Building updateBuilding(@Param("cityName") String cityName, @Param("buildingName") String buildingName,
                            @Param("clliCode") String clliCode,
                            @Param("phoneNumber") String phoneNumber,
                            @Param("contactPerson") String contactPerson, @Param("address") String address,
                            @Param("latitude") String latitude,
                            @Param("longitude") String longitude,
                            @Param("drivingInstructions") String drivingInstructions,
                            @Param("notes") String notes);

    @Query("MERGE (d:Building {name: $buildingName}) ON MATCH SET d.name = $buildingName, d.clliCode = $clliCode," +
            "d.phoneNumber = $phoneNumber, d.contactPerson = $contactPerson, d.address = $address," +
            "d.latitude = $latitude, d.longitude = $longitude, d.drivingInstructions = $drivingInstructions," +
            "d.notes=$notes return d")
    Building updateBuilding(@Param("buildingName") String buildingName,
                            @Param("clliCode") String clliCode,
                            @Param("phoneNumber") String phoneNumber,
                            @Param("contactPerson") String contactPerson, @Param("address") String address,
                            @Param("latitude") String latitude,
                            @Param("longitude") String longitude,
                            @Param("drivingInstructions") String drivingInstructions,
                            @Param("notes") String notes);

    @Query("MATCH (b:Building{name:$name})\n" +
            "OPTIONAL MATCH  (b)-[:BUILDING_TO_RACK]->(r:Rack)\n" +
            "OPTIONAL MATCH (b)-[:BUILDING_TO_ADDITIONAL_ATTRIBUTE]->(a:AdditionalAttribute)\n" +
            "WITH [b, r, a] AS nodes\n" +
            "UNWIND nodes AS node\n" +
            "DETACH DELETE node")
    void deleteBuilding(@Param("name") String name);

    @Query("match (d:Device{name:$device})-[:RACK_TO_DEVICE]-(r:Rack)-[:BUILDING_TO_RACK]-(b:Building) return b;")
    Building getBuildingForDeviceOnRack(@Param("device") String device);

    @Query("match (d:Device{name:$device})-[:BUILDING_TO_DEVICE]-(b:Building) return b;")
    Building getBuildingForDevice(@Param("device") String device);
}

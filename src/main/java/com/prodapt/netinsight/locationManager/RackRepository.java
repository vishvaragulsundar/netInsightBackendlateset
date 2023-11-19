package com.prodapt.netinsight.locationManager;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface RackRepository extends Neo4jRepository<Rack, Long> {

    @Query("match(d:Rack{name:$name}) return d")
    Rack findByName(String name);

    Rack getByName(String name);

    @Query("match(b:Building {name: $buildingName})-[:BUILDING_TO_RACK]->(r:Rack)" +
            "return r")
    ArrayList<Rack> findByBuilding(@Param("buildingName") String buildingName);

    @Query("match (r:Rack)-[:RACK_TO_DEVICE]-(d:Device{name:$deviceName}) return r")
    Rack getRackWithDeviceName(@Param("deviceName") String deviceName);

    Rack findRackById(@Param("id") Long id);

    @Query("MERGE (d:Rack {name: $rackName}) ON CREATE SET d.name = $rackName, d.location = $location, " +
            "d.height = $height, " +
            "d.notes=$notes, " +
            "d.width = $width, d.depth = $depth, d.numOfPositionsContained = $numOfPositionsContained, " +
            "d.reservedPositions = $reservedPositions, " +
            "d.freePositions = $freePositions " +
            "WITH d " +
            "MATCH (b:Building{name: $buildingName})" +
            "MERGE (b)-[:BUILDING_TO_RACK]->(d)" +
            "RETURN d")
    Rack createRack(@Param("buildingName") String buildingName, @Param("rackName") String rackName,
                    @Param("location") String location, @Param("height") float height,
                    @Param("width") float width, @Param("depth") float depth,
                    @Param("numOfPositionsContained") int numOfPositionsContained,
                    @Param("reservedPositions") ArrayList<Integer> reservedPositions,
                    @Param("freePositions") ArrayList<Integer> freePositions,
                    @Param("notes") String notes);

    @Query("Match (d:Rack {name: $rackName}) with d SET d.name = $rackName, d.location = $location, " +
            "d.height = $height, " +
            "d.notes=$notes, " +
            "d.width = $width, d.depth = $depth, d.numOfPositionsContained = $numOfPositionsContained," +
            "d.reservedPositions = $reservedPositions," +
            "d.freePositions = $freePositions " +
            "WITH d " +
            "MATCH (b:Building{name: $buildingName})" +
            "MERGE (b)-[:BUILDING_TO_RACK]->(d)" +
            "RETURN d")
    Rack updateRack(@Param("buildingName") String buildingName, @Param("rackName") String rackName,
                    @Param("location") String location, @Param("height") float height,
                    @Param("width") float width, @Param("depth") float depth,
                    @Param("numOfPositionsContained") int numOfPositionsContained,
                    @Param("reservedPositions") ArrayList<Integer> reservedPositions,
                    @Param("freePositions") ArrayList<Integer> freePositions,
                    @Param("notes") String notes);

    @Query("Match (d:Rack {name: $rackName}) SET d.location = $location, " +
            "d.height = $height, d.notes=$notes, " +
            "d.width = $width, d.depth = $depth, d.numOfPositionsContained = $numOfPositionsContained," +
            "d.reservedPositions = $reservedPositions," +
            "d.freePositions = $freePositions RETURN d")
    Rack updateRack(@Param("rackName") String rackName, @Param("location") String location,
                    @Param("height") float height, @Param("width") float width, @Param("depth") float depth,
                    @Param("numOfPositionsContained") int numOfPositionsContained,
                    @Param("reservedPositions") ArrayList<Integer> reservedPositions,
                    @Param("freePositions") ArrayList<Integer> freePositions,
                    @Param("notes") String notes);

    @Query("Match (d:Rack {name: $name}) \n" +
            "OPTIONAL MATCH (d)-[:RACK_TO_ADDITIONAL_ATTRIBUTE]->(a:AdditionalAttribute)\n" +
            "WITH [d, a] AS nodes\n" +
            "UNWIND nodes AS node\n" +
            "DETACH DELETE node")
    void deleteRack(@Param("name") String name);
}

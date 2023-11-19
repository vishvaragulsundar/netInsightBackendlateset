package com.prodapt.netinsight.locationManager;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface CityRepository extends Neo4jRepository<City, Long> {

    City findByName(String name);

    @Query("match(s:State {name: $stateName})-[:STATE_TO_CITY]->(c:City)" +
            "return c")
    ArrayList<City> findByState(@Param("stateName") String stateName);

    @Query("match(s:State {name: $stateName})-[:STATE_TO_CITY]->(c:City{name:$cityName}) " +
            "return c")
    City findUniqueCity(@Param("stateName") String stateName, @Param("cityName") String cityName);

    @Query("match(s:State {name: $stateName})" +
            "WITH s MERGE (c:City {name: $cityName}) ON CREATE SET c.notes=$notes ON MATCH SET c.notes=$notes " +
            "MERGE (s)-[:STATE_TO_CITY]->(c) RETURN c")
    City createCity(@Param("stateName") String stateName, @Param("cityName") String cityName,
                    @Param("notes") String notes);

    @Query("MATCH (ci:City{name:$name})\n" +
            "OPTIONAL MATCH (ci)-[:CITY_TO_BUILDING]->(b:Building)\n" +
            "OPTIONAL MATCH (b)-[:BUILDING_TO_RACK]->(r:Rack)\n" +
            "WITH [ci, b, r] AS nodes\n" +
            "UNWIND nodes AS node\n" +
            "DETACH DELETE node")
    void deleteCity(@Param("name") String name);

    @Query("match (c:City {name: $cityName}) SET c.notes=$notes return c")
    City updateCity(@Param("cityName") String cityName, @Param("notes") String notes);
}

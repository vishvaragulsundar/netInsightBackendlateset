package com.prodapt.netinsight.locationManager;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface StateRepository extends Neo4jRepository<State, Long> {
    @Query("match (c:State {name: $stateName}) return c")
    State findByName(@Param("stateName") String stateName);

    @Query("match(d:Country {name: $countryName})-[:COUNTRY_TO_STATE]->(s:State)" +
            "return s")
    ArrayList<State> findByCountry(@Param("countryName") String countryName);

    @Query("match(c:Country {name: $countryName})" +
            "WITH c MERGE (s:State {name: $stateName}) ON CREATE SET s.notes=$notes " +
            "ON MATCH SET s.notes=$notes " +
            "MERGE (c)-[:COUNTRY_TO_STATE]->(s) RETURN s")
    State createState(@Param("countryName") String countryName, @Param("stateName") String stateName,
                      @Param("notes") String notes);

    @Query("MATCH (s:State{name:$name})\n" +
            "OPTIONAL MATCH (s)-[:STATE_TO_CITY]->(ci:City)\n" +
            "OPTIONAL MATCH (ci)-[:CITY_TO_BUILDING]->(b:Building)\n" +
            "OPTIONAL MATCH (b)-[:BUILDING_TO_RACK]->(r:Rack)\n" +
            "WITH [s, ci, b, r] AS nodes\n" +
            "UNWIND nodes AS node\n" +
            "DETACH DELETE node")
    void deleteState(@Param("name") String name);

    @Query("match(s:State {name: $stateName}) SET s.notes=$notes return s")
    State updateState(@Param("stateName") String stateName, @Param("notes") String notes);

    @Query("match(c:City {name: $cityName})-[:STATE_TO_CITY]-(s:State) return s")
    State findStateByCity(@Param("cityName") String cityName);
}

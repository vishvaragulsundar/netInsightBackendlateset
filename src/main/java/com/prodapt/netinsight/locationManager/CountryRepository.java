package com.prodapt.netinsight.locationManager;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface CountryRepository extends Neo4jRepository<Country, Long> {

    @Query("match(d:Country{name:$name}) return d")
    Country findByName(String name);

    @Query("match(d:Country) return d")
    ArrayList<Country> getAllCountry();

    @Query("MERGE (d:Country {name: $name}) ON CREATE SET d.notes=$notes ON MATCH SET d.notes=$notes RETURN d")
    Country createCountry(@Param("name") String name, @Param("notes") String notes);

    @Query("MATCH (c:Country {name: $name})\n" +
            "OPTIONAL MATCH (c)-[:COUNTRY_TO_STATE]->(s:State)\n" +
            "OPTIONAL MATCH (s)-[:STATE_TO_CITY]->(ci:City)\n" +
            "OPTIONAL MATCH (ci)-[:CITY_TO_BUILDING]->(b:Building)\n" +
            "OPTIONAL MATCH (b)-[:BUILDING_TO_RACK]->(r:Rack)\n" +
            "WITH [c, s, ci, b, r] AS nodes\n" +
            "UNWIND nodes AS node\n" +
            "DETACH DELETE node")
    void deleteCountry(@Param("name") String name);

    @Query("MERGE (d:Country {name:$name}) " +
            "ON CREATE SET d.name = $name, d.notes=$notes " +
            "ON MATCH SET d.notes=$notes RETURN d")
    Country updateCountry(@Param("name") String name, @Param("notes") String notes);

    @Query("MATCH (s:State {name:$stateName})-[:COUNTRY_TO_STATE]-(c:Country) return c")
    Country findCountryByState(@Param("stateName") String stateName);
}


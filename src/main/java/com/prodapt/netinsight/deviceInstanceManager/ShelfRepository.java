package com.prodapt.netinsight.deviceInstanceManager;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;

@Repository
public interface ShelfRepository extends Neo4jRepository<Shelf, Long> {

    @Query("MATCH (d:Device {name: $name})\n" +
            "       OPTIONAL MATCH (d)-[DEVICE_TO_SHELF]->(s:Shelf)\n" +
            "       return s")
    ArrayList<Shelf> getShelves(String name);

    @Query("MATCH (d:Device {name: $deviceName})\n" +
            "       OPTIONAL MATCH (d)-[DEVICE_TO_SHELF]->(s:Shelf{name:$shelfName})\n" +
            "       return s")
    Shelf getShelf(String deviceName, String shelfName);

    @Query("MATCH (s:Shelf {name: $name})\n" +
            "return s")
    Shelf getShelf(String name);

    Shelf getByName(String name);

    Shelf findShelfById(@Param("id") Long id);

    @Query("MATCH (s:Shelf {name: $name}) " +
            "MATCH (s)-[:SHELF_TO_SLOT]->(sl:Slot)-[:SLOT_TO_CARD]-(c:Card) " +
            "  return c")
    Collection<Card> getCardOnShelf(String name);

    @Query("Match(s:Shelf{name:$shelfName}) return id(s)")
    Long getIdByName(@Param("shelfName") String shelfName);

    @Query("MATCH (s:Shelf{name:$shelfName})\n" +
            "SET s.operationalState = $operationalState,\n" +
            "    s.administrativeState = $administrativeState,\n" +
            "    s.usageState = $usageState\n" +
            "WITH s\n" +
            "MATCH (o:Order)\n" +
            "WHERE id(o) = $orderId\n" +
            "MERGE (o)-[:ORDER_FOR]->(s)\n" +
            "RETURN s\n")
    Shelf updateShelf(@Param("shelfName") String shelfName, @Param("operationalState") String operationalState,
                      @Param("administrativeState") String administrativeState, @Param("usageState") String usageState,
                      @Param("orderId") Long orderId);
}

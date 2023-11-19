package com.prodapt.netinsight.deviceInstanceManager;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface SlotRepository extends Neo4jRepository<Slot, Long> {

    @Query("MATCH (d:Slot {name: $name})\n" +
            "       return d")
    Slot getSlot(String name);

    Slot getByName(String name);

    @Query("MATCH (d:Shelf {name: $shelfName})-[:SHELF_TO_SLOT]->(s:Slot {name: $slotName}) return s")
    Slot getSlotOnShelf(@Param("shelfName") String shelfName, @Param("slotName") String slotName);

    Slot findSlotById(@Param("id") Long id);

    @Query("Match (s:Shelf{name:$shelfName})-[:SHELF_TO_SLOT]->(slot:Slot) return slot")
    ArrayList<Slot> getSlotsOnShelf(@Param("shelfName") String shelfName);

    @Query("MATCH (d:Slot {name: $name})\n" +
            "       detach delete d")
    void deleteSlot(String name);

    @Query("MERGE (s:Slot{name:$name}) ON CREATE SET s.slotPosition = $slotPosition, " +
            "s.operationalState=$operationalState, s.administrativeState=$administrativeState," +
            "s.usageState=$usageState \n" +
            "with s " +
            "MATCH (shelf:Shelf{name:$shelfName})" +
            "MERGE (shelf)-[:SHELF_TO_SLOT]->(s)" +
            "return s")
    Slot createSlot(@Param("name") String name, @Param("slotPosition") Integer slotPosition,
                    @Param("operationalState") String operationalState,
                    @Param("administrativeState") String administrativeState,
                    @Param("usageState") String usageState,
                    @Param("shelfName") String shelfName);

    @Query("Match(s:Slot{name:$slotName}) return id(s)")
    Long getIdByName(@Param("slotName") String slotName);

    @Query("MATCH (s:Slot{name:$slotName})\n" +
            "SET s.operationalState = $operationalState,\n" +
            "s.administrativeState = $administrativeState,\n" +
            "s.usageState = $usageState\n" +
            "WITH s\n" +
            "MATCH (o:Order)\n" +
            "WHERE id(o) = $orderId\n" +
            "MERGE (o)-[:ORDER_FOR]->(s)\n" +
            "RETURN s")
    Slot updateSlot(@Param("slotName") String shelfName, @Param("operationalState") String operationalState,
                    @Param("administrativeState") String administrativeState, @Param("usageState") String usageState,
                    @Param("orderId") Long orderId);
}

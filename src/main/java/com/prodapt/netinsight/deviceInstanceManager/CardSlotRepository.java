package com.prodapt.netinsight.deviceInstanceManager;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface CardSlotRepository extends Neo4jRepository<CardSlot, Long> {

    @Query("MATCH (d:CardSlot {name: $name}) " +
            "return d")
    CardSlot getCardSlot(String name);

    CardSlot getByName(String name);

    @Query("MATCH (c:Card{name: $cardName})-[:CARD_TO_CARD_SLOT]->(cs:CardSlot{name: $cardSlot}) return cs")
    CardSlot getCardSlotOnACard(@Param("cardName") String cardName, @Param("cardSlot") String cardSlot);

    @Query("Match (cs:CardSlot)<-[:CARD_TO_CARD_SLOT]-(c:Card{name:$cardName}) return cs")
    ArrayList<CardSlot> getCardSlotsOnACard(@Param("cardName") String cardName);

    CardSlot findCardSlotById(@Param("id") Long id);

    @Query("MATCH (d:CardSlot {name: $name}) " +
            "detach delete d")
    void deleteCardSlot(String name);

    @Query("MERGE (cs:CardSlot{name:$name}) ON CREATE SET cs.slotPosition = $slotPosition, " +
            "cs.operationalState=$operationalState, cs.administrativeState=$administrativeState," +
            "cs.usageState=$usageState \n" +
            "with cs " +
            "MATCH (c:Card{name:$cardName})" +
            "MERGE (c)-[:CARD_TO_CARD_SLOT]->(cs)" +
            "return cs")
    CardSlot createCardSlot(@Param("name") String name, @Param("slotPosition") Integer slotPosition,
                            @Param("operationalState") String operationalState,
                            @Param("administrativeState") String administrativeState,
                            @Param("usageState") String usageState,
                            @Param("cardName") String cardName);

    @Query("Match(cs:CardSlot{name:$cardSlotName}) return id(cs)")
    Long getIdByName(@Param("cardSlotName") String cardSlotName);

    @Query("MATCH (cs:CardSlot{name:$cardSlotName})\n" +
            "SET cs.operationalState = $operationalState,\n" +
            "cs.administrativeState = $administrativeState,\n" +
            "cs.usageState = $usageState\n" +
            "WITH cs\n" +
            "MATCH (o:Order)\n" +
            "WHERE id(o) = $orderId\n" +
            "MERGE (o)-[:ORDER_FOR]->(cs)\n" +
            "RETURN cs")
    CardSlot updateCardSlot(@Param("cardSlotName") String shelfName, @Param("operationalState") String operationalState,
                            @Param("administrativeState") String administrativeState, @Param("usageState") String usageState,
                            @Param("orderId") Long orderId);
}

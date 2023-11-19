package com.prodapt.netinsight.deviceInstanceManager;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface CardRepository extends Neo4jRepository<Card, Long> {

    @Query("MATCH (c:Card {name: $cardName}) return c")
    Card getCard(@Param("cardName") String cardName);

    Card getByName(String name);

    @Query("MATCH (d:Device{name: $device})-[:DEVICE_TO_SHELF]-(shelf)-[:SHELF_TO_SLOT]-(slot)-[:SLOT_TO_CARD]-(c:Card {name: $cardName}) return c")
    Card getCard(@Param("device") String device, @Param("cardName") String cardName);

    Card findCardById(@Param("id") Long id);

    @Query("MATCH (c:Card {name: $cardName}) " +
            "Match (c)-[r]->(p:Pluggable)" +
            "detach delete c, p, r")
    Card deleteCard(@Param("cardName") String cardName);

    @Query("MATCH (c:Slot {name: $SlotName}) " +
            "MATCH (c)-[:SLOT_TO_CARD]->(card:Card) " +
            "return card")
    Card getCardOnASlot(@Param("SlotName") String SlotName);

    @Query("MERGE (c:Card{name:$cardName}) ON CREATE SET c.shelfPosition = $shelfPosition," +
            "c.slotPosition=$slotPosition,c.vendor=$vendor,c.cardModel=$cardModel,c.cardPartNumber= $cardPartNumber," +
            "c.operationalState=$operationalState, c.administrativeState=$administrativeState," +
            "c.usageState=$usageState \n" +
            "with c \n" +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]->(c) " +
            "with c \n" +
            "MATCH (s:Slot{name:$slotName}) " +
            "MERGE (s)-[:SLOT_TO_CARD]->(c) " +
            "return c")
    Card createCard(@Param("cardName") String cardName, @Param("shelfPosition") int shelfPosition,
                    @Param("slotPosition") int slotPosition, @Param("vendor") String vendor,
                    @Param("cardModel") String cardModel, @Param("cardPartNumber") String cardPartNumber,
                    @Param("operationalState") String operationalState,
                    @Param("administrativeState") String administrativeState,
                    @Param("usageState") String usageState,
                    @Param("slotName") String slotName,
                    @Param("orderId") Long orderId);

    @Query("MERGE (c:Card{name:$cardName}) ON MATCH SET c.shelfPosition = $shelfPosition," +
            "c.slotPosition=$slotPosition,c.vendor=$vendor,c.cardModel=$cardModel,c.cardPartNumber= $cardPartNumber," +
            "c.operationalState=$operationalState, c.administrativeState=$administrativeState," +
            "c.usageState=$usageState \n" +
            "with c \n" +
            "MATCH (o:Order) WHERE id(o) = $orderId " +
            "MERGE (o)-[:ORDER_FOR]->(c) " +
            "with c \n" +
            "MATCH (s:Slot{name:$slotName}) " +
            "MERGE (s)-[:SLOT_TO_CARD]->(c) " +
            "return c")
    Card updateCard(@Param("cardName") String cardName, @Param("shelfPosition") int shelfPosition,
                    @Param("slotPosition") int slotPosition, @Param("vendor") String vendor,
                    @Param("cardModel") String cardModel, @Param("cardPartNumber") String cardPartNumber,
                    @Param("operationalState") String operationalState,
                    @Param("administrativeState") String administrativeState,
                    @Param("usageState") String usageState,
                    @Param("slotName") String slotName,
                    @Param("orderId") Long orderId);

    @Query("MATCH (c:Card{name:$cardName})" +
            "MATCH (o:Order) where id(o) = $orderId\n" +
            "MERGE (o)-[:ORDER_FOR]->(d)\n" +
            "with c\n" +
            "OPTIONAL MATCH (c)-[:CARD_TO_CARD_SLOT]->(cs:CardSlot)\n" +
            "OPTIONAL MATCH (cs)-[:CARD_SLOT_TO_PLUGGABLE]->(p:Pluggable)\n" +
            "OPTIONAL MATCH (cs)-[:CARD_SLOT_TO_PORT]->(port:Port)\n" +
            "OPTIONAL MATCH (c)-[:CARD_TO_ADDITIONAL_ATTRIBUTE]->(a:AdditionalAttribute)\n" +
            "WITH [c, cs, p, port, a] AS nodes\n" +
            "UNWIND nodes AS node\n" +
            "DETACH DELETE node")
    void deleteCardAndAllRelatedNodes(@Param("cardName") String cardName,
                                      @Param("orderId") Long orderId);

    @Query("MATCH (c:Card)-[:CARD_TO_CARD_SLOT]-(cs:CardSlot)-[:CARD_SLOT_TO_PLUGGABLE]-(p:Pluggable) where p.name = $pluggableName return c")
    Card findByPluggableName(@Param("pluggableName") String pluggableName);

    @Query("MATCH (c:Card)-[:CARD_TO_CARD_SLOT]-(cs:CardSlot)-[:CARD_SLOT_TO_PORT]-(p:Port) where p.name = $portName return c")
    Card getCardFromPortName(@Param("portName") String portName);

    @Query("MATCH (d:Device{name:$deviceName})-[:DEVICE_TO_SHELF]-(s:Shelf)-[:SHELF_TO_SLOT]-(sl:Slot)-[:SLOT_TO_CARD]-(c:Card) return c")
    ArrayList<Card> getAllCardsOnDevice(@Param("deviceName") String deviceName);

    @Query("Match(c:Card{name:$cardName}) return id(c)")
    Long getIdByName(@Param("cardName") String cardName);
}

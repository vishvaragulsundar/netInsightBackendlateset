Relationship hierarchy,

(Device)-[:DEVICE_TO_SHELF]->(Shelf)-[:SHELF_TO_SLOT]->(Slot)-[:SLOT_TO_CARD]->(Card)-[:CARD_TO_CARD_SLOT]->(CardSlot)-[:CARD_SLOT_TO_PLUGGABLE]->(Pluggable)-[:PHYSICALLY_CONNECTED]->(PhysicalConnection)<-[:PHYSICALLY_CONNECTED]-(Pluggable)

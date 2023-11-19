package com.prodapt.netinsight.orderManager;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;

public interface OrderRepository extends Neo4jRepository<OrderEntity, Long> {

    @Query("CREATE (o:Order) SET o.status = $status, o.category = $category, o.description = $description " +
            "WITH o " +
            "MATCH(c:Customer) WHERE id(c) = $customerId " +
            "MERGE (c)-[:CUSTOMER_TO_ORDER]->(o)" +
            "RETURN o")
    OrderEntity createOrderWithCustomer(@Param("customerId") Long customerId,
                                        @Param("status") String status,
                                        @Param("category") String category,
                                        @Param("description") String description);

    @Query("CREATE (o:Order) SET o.status = $status, o.category = $category, o.description = $description " +
            "RETURN o")
    OrderEntity createOrder(@Param("status") String status,
                            @Param("category") String category,
                            @Param("description") String description);

    @Query("MATCH (o:Order) WHERE id(o) = $orderId SET o.status = $status RETURN o")
    OrderEntity updateOrderById(@Param("orderId") Long orderId, @Param("status") String status);

    @Query("MATCH(o:Order) RETURN o")
    ArrayList<OrderEntity> getAllOrders();

    OrderEntity findOrderById(@Param("orderId") Long orderId);

    @Query("MATCH (c:Customer)-[:CUSTOMER_TO_ORDER]-(n:Order) WITH n ORDER BY n.id DESC LIMIT 1 RETURN n")
    OrderEntity findLatestOrderId();

    @Query("MATCH (o:Order)\n" +
            "WHERE toString(id(o)) CONTAINS $orderId\n" +
            "RETURN id(o)\n")
    ArrayList<Long> findOrderByIdContaining(@Param("orderId") String orderId);
}

package com.prodapt.netinsight.customerManager;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;

public interface CustomerRepository extends Neo4jRepository<Customer, Long> {

    @Query("MERGE (c:Customer {name: $name}) ON CREATE SET  c.description=$description, c.emailId=$emailId, " +
            "c.contactNo=$contactNo RETURN c")
    Customer createCustomer(@Param("name") String name, @Param("description") String description,
                            @Param("emailId") String emailId, @Param("contactNo") @NotEmpty String contactNo);

    @Query("MATCH (c:Customer {customerId: $customerId}) RETURN c")
    Customer findByCustomerId(@Param("customerId") String customerId);

    @Query("MATCH (c:Customer {name: $name}) RETURN c")
    Customer findByCustomerName(@Param("name") String name);

    @Query("MERGE (c:Customer {name:$name}) ON MATCH SET c.emailId = $emailId,c.contactNo = $contactNo, " +
            "c.description = $description RETURN c")
    Customer updateCustomer(@Param("name") String name,
                            @Param("description") String description, @Param("emailId") String emailId,
                            @Param("contactNo") String contactNo);

    @Query("MATCH(c:Customer) RETURN c")
    ArrayList<Customer> getAllCustomers();

    @Query("Match (c:Customer)-[:SERVICE_RELATED_TO]-(s:Service{name:$serviceName}) return c")
    Customer getCustomerForService(@Param("serviceName") String serviceName);


    Customer findCustomerById(@Param("customerId") Long customerId);

    @Query("MATCH (c:Customer) WITH c ORDER BY c.id DESC LIMIT 1 RETURN c")
    Customer findLatestCustomerId();
}

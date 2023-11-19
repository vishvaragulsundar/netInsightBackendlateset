package com.prodapt.netinsight.IntegrationTesting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prodapt.netinsight.customerManager.Customer;
import com.prodapt.netinsight.customerManager.CustomerRepository;
import com.prodapt.netinsight.orderManager.OrderEntity;
import com.prodapt.netinsight.orderManager.OrderManagerRestApis;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CreateOrderIntegrationTest {

    @Autowired
    OrderManagerRestApis orderManagerRestApis;

    @Autowired
    CustomerRepository customerRepository;

    @Test
    @Order(1)
    public void createOrder() throws JsonProcessingException {
        String createOrder = "{\n " +
                " \"status\": \"Active\",\n " +
                " \"category\": \"Create\",\n " +
                " \"description\": \"Mock Create\"\n " +
                "}";

        ObjectMapper objectMapper = new ObjectMapper();
        OrderEntity order = objectMapper.readValue(createOrder, OrderEntity.class);

        Customer existingCustomer = customerRepository.findLatestCustomerId();

        orderManagerRestApis.createOrder(existingCustomer.getId(), order);

    }
}

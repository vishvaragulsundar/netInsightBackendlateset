package com.prodapt.netinsight.IntegrationTesting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prodapt.netinsight.customerManager.Customer;
import com.prodapt.netinsight.customerManager.CustomerManagerRestApis;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CreateCustomerIntegrationTest {

    @Autowired
    CustomerManagerRestApis customerManagerRestApis;

    @Test
    @Order(1)
    public void createCustomer() throws JsonProcessingException {
        String createCustomer = "{\n " +
                " \"name\": \"MockCustomer\",\n " +
                " \"description\": \"Create Mock Customer\",\n " +
                " \"emailId\": \"MockCustomer@gmail.com\",\n " +
                " \"contactNo\": \"45678934\",\n " +
                " \"customerGroupName\": \"Mock Customer Group\"\n " +
                "}";

        ObjectMapper objectMapper = new ObjectMapper();
        Customer customer = objectMapper.readValue(createCustomer, Customer.class);

        customerManagerRestApis.createCustomer(customer);
    }
}

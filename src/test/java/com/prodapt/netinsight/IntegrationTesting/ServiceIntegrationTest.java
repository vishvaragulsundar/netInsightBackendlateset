package com.prodapt.netinsight.IntegrationTesting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prodapt.netinsight.orderManager.OrderEntity;
import com.prodapt.netinsight.orderManager.OrderRepository;
import com.prodapt.netinsight.serviceManager.Service;
import com.prodapt.netinsight.serviceManager.ServiceRestApis;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * This class creates, Updates and retrieves the Services.
 */

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceIntegrationTest {

    @Autowired
    ServiceRestApis serviceRestApis;

    @Autowired
    OrderRepository orderRepository;

    /**
     * Test Method to Create a Service.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(1)
    public void createService() throws JsonProcessingException {
        String serviceDetails = "{" +
                "\"name\":\"MockService1\",\n" +
                "\"type\":\"Mock\",\n" +
                "\"operationalState\":\"Active\",\n" +
                "\"administrativeState\":\"Active\",\n" +
                "\"notes\":\"Mock12345\",\n" +
                "\"logicalConnections\":[\"mockconnection1\"],\n" +
                "\"physicalConnections\":[\"mock_ont_1\", \"mock_ont_3\", \"mock_ont_5\"]\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Service service = objectMapper.readValue(serviceDetails, Service.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To Create a Service
        serviceRestApis.createService(existingOrder.getId(), service);
    }

    /**
     * Test Method to Update a Service.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(2)
    public void updateService() throws JsonProcessingException {
        String updateServiceDetails = "{" +
                "\"name\":\"MockService1\",\n" +
                "\"type\":\"Mock\",\n" +
                "\"operationalState\":\"Active\",\n" +
                "\"administrativeState\":\"Active\",\n" +
                "\"notes\":\"Mock01\",\n" +
                "\"logicalConnections\":[\"mockconnection1\"],\n" +
                "\"physicalConnections\":[\"mock_ont_1\", \"mock_ont_3\", \"mock_ont_5\"]\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Service service = objectMapper.readValue(updateServiceDetails, Service.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To Update a Service
        serviceRestApis.updateService(existingOrder.getId(), "MockService1", service);
    }

    /**
     * Test Method to retrieve a Service Details.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(3)
    public void getService() throws JsonProcessingException {
        String expectedJson = "{" +
                "\"name\":\"MockService1\",\n" +
                "\"type\":\"Mock\",\n" +
                "\"operationalState\":\"Active\",\n" +
                "\"administrativeState\":\"Active\",\n" +
                "\"notes\":\"Mock01\",\n" +
                "\"logicalConnections\":[\"mockconnection1\"],\n" +
                "\"physicalConnections\":[\"mock_ont_1\", \"mock_ont_3\", \"mock_ont_5\"]\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Service expectedService = objectMapper.readValue(expectedJson, Service.class);

        //To retrieve particular Service Details
        Service actualService = serviceRestApis.findServiceByName("MockService1");

        //To check whether the given data and actual data from the database are equal or not
        Assertions.assertEquals(expectedService.getName().toLowerCase(), actualService.getName());
        Assertions.assertEquals(expectedService.getAdministrativeState(), actualService.getAdministrativeState());
        Assertions.assertEquals(expectedService.getType().toLowerCase(), actualService.getType());
        Assertions.assertEquals(expectedService.getOperationalState(), actualService.getOperationalState());
        Assertions.assertEquals(expectedService.getPhysicalConnections(), actualService.getPhysicalConnections());
        Assertions.assertEquals(expectedService.getLogicalConnections(), actualService.getLogicalConnections());
    }
}

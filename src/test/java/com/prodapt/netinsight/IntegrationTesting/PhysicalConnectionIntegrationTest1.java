package com.prodapt.netinsight.IntegrationTesting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prodapt.netinsight.connectionManager.ConnectionRestApis;
import com.prodapt.netinsight.connectionManager.PhysicalConnection;
import com.prodapt.netinsight.orderManager.OrderEntity;
import com.prodapt.netinsight.orderManager.OrderRepository;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertTrue;

/**
 * This class creates the Physical Connection on pluggables and ports between two devices.
 */

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PhysicalConnectionIntegrationTest1 {

    @Autowired
    ConnectionRestApis connectionRestApis;

    @Autowired
    OrderRepository orderRepository;

    /**
     * Test method to Create Physical connection on ports between no shelf device 1
     * and no shelf device 2.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(1)
    public void createPhysicalConnectionOnPort() throws JsonProcessingException {
        String createPhysicalConnectionJson = "{\n " +
                "\"name\": \"Mock_ONT_1\",\n " +
                "\"deviceA\": \"noshelf_dev_1\",\n " +
                "\"deviceZ\": \"noshelf_dev_2\",\n " +
                "\"deviceAPort\": 1,\n " +
                "\"deviceZPort\": 1,\n " +
                "\"connectionType\": \"rj45\",\n " +
                "\"bandwidth\": 100\n " +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        PhysicalConnection physicalConnection = objectMapper.readValue(createPhysicalConnectionJson, PhysicalConnection.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To create physical connection according to given json
        connectionRestApis.createPhysicalConnection(existingOrder.getId(), physicalConnection);
    }

    /**
     * Test method to Create Physical connection on pluggables between no shelf device 1
     * and no shelf device 2.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(2)
    public void createPhysicalConnectionOnPluggable() throws JsonProcessingException {
        String createPhysicalConnectionJson = "{\n " +
                "\"name\": \"Mock_ONT_2\",\n " +
                "\"deviceA\": \"noshelf_dev_1\",\n " +
                "\"deviceZ\": \"noshelf_dev_2\",\n " +
                "\"deviceAPort\": 3,\n " +
                "\"deviceZPort\": 3,\n " +
                "\"connectionType\": \"Cable Fiber\",\n " +
                "\"bandwidth\": 100\n " +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        PhysicalConnection physicalConnection = objectMapper.readValue(createPhysicalConnectionJson, PhysicalConnection.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To create physical connection according to given json
        connectionRestApis.createPhysicalConnection(existingOrder.getId(), physicalConnection);
    }

    /**
     * Test method to Check whether it throws an exception while creating
     * Physical connection between a port and a pluggable.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(3)
    public void createPhysicalConnectionException() throws JsonProcessingException {
        String createPhysicalConnectionException = "{\n " +
                "\"name\": \"Mock_ONT\",\n " +
                "\"deviceA\": \"noshelf_dev_1\",\n " +
                "\"deviceZ\": \"noshelf_dev_2\",\n " +
                "\"deviceAPort\": 1,\n " +
                "\"deviceZPort\": 3,\n " +
                "\"connectionType\": \"Cable Fiber\",\n " +
                "\"bandwidth\": 100\n " +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        PhysicalConnection physicalConnection = objectMapper.readValue(createPhysicalConnectionException, PhysicalConnection.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        // Perform the test
        JSONObject result = connectionRestApis.createPhysicalConnection(existingOrder.getId(), physicalConnection);

        // Assert the expected response
        assertTrue(result.toString().contains("Failed due to incompatibility between port and pluggable for connection creation"));
    }

    /**
     * Test method to Update Physical connection on ports between no shelf device 1
     * and no shelf device 2 based on Connection Name.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(4)
    public void updatePhysicalConnectionOnPort() throws JsonProcessingException {
        String updatePhysicalConnectionJson = "{\n " +
                "\"name\": \"Mock_ONT_1\",\n " +
                "\"deviceA\": \"noshelf_dev_1\",\n " +
                "\"deviceZ\": \"noshelf_dev_2\",\n " +
                "\"deviceAPort\": 1,\n " +
                "\"deviceZPort\": 1,\n " +
                "\"connectionType\": \"rj45\",\n " +
                "\"bandwidth\": 200\n " +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        PhysicalConnection updatedPhysicalConnection = objectMapper.readValue(updatePhysicalConnectionJson, PhysicalConnection.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To update physical connection by using the connection name
        connectionRestApis.updatePhysicalConnectionByName(existingOrder.getId(), "Mock_ONT_1", updatedPhysicalConnection);
    }

    /**
     * Test method to Update Physical connection on pluggables between no shelf device 1
     * and no shelf device 2 based on Connection Name.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(5)
    public void updatePhysicalConnectionOnPluggable() throws JsonProcessingException {
        String updatePhysicalConnectionJson = "{\n " +
                "\"name\": \"Mock_ONT_2\",\n " +
                "\"deviceA\": \"noshelf_dev_1\",\n " +
                "\"deviceZ\": \"noshelf_dev_2\",\n " +
                "\"deviceAPort\": 3,\n " +
                "\"deviceZPort\": 3,\n " +
                "\"connectionType\": \"Cable Fiber\",\n " +
                "\"bandwidth\": 200\n " +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        PhysicalConnection updatedPhysicalConnection = objectMapper.readValue(updatePhysicalConnectionJson, PhysicalConnection.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To update physical connection by using the connection name
        connectionRestApis.updatePhysicalConnectionByName(existingOrder.getId(), "Mock_ONT_2", updatedPhysicalConnection);
    }

    /**
     * Test method to Get Physical connection on ports between no shelf device 1
     * and no shelf device 2.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(6)
    public void getPhysicalConnectionOnPort() throws JsonProcessingException {
        String expectedJson = "{\n " +
                "\"name\": \"Mock_ONT_1\",\n " +
                "\"deviceA\": \"noshelf_dev_1\",\n " +
                "\"deviceZ\": \"noshelf_dev_2\",\n " +
                "\"deviceAPort\": 1,\n " +
                "\"deviceZPort\": 1,\n " +
                "\"connectionType\": \"rj45\",\n " +
                "\"bandwidth\": 200\n " +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        PhysicalConnection expectedPhysicalConnection = objectMapper.readValue(expectedJson, PhysicalConnection.class);

        //To get the physical connection based on the name
        PhysicalConnection actualPhysicalConnection = connectionRestApis.findPhysicalConnection("Mock_ONT_1");

        //To check whether the given data and actual data from the database are equal or not
        Assertions.assertEquals(expectedPhysicalConnection.getName().toLowerCase(), actualPhysicalConnection.getName());
        Assertions.assertEquals(expectedPhysicalConnection.getDeviceA(), actualPhysicalConnection.getDeviceA());
        Assertions.assertEquals(expectedPhysicalConnection.getDeviceZ(), actualPhysicalConnection.getDeviceZ());
        Assertions.assertEquals(expectedPhysicalConnection.getDeviceAPort(), actualPhysicalConnection.getDeviceAPort());
        Assertions.assertEquals(expectedPhysicalConnection.getDeviceZPort(), actualPhysicalConnection.getDeviceZPort());
        Assertions.assertEquals(expectedPhysicalConnection.getConnectionType(), actualPhysicalConnection.getConnectionType());
        Assertions.assertEquals(expectedPhysicalConnection.getBandwidth(), actualPhysicalConnection.getBandwidth());
    }

    /**
     * Test method to Get Physical connection on pluggables between no shelf device 1
     * and no shelf device 2.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(7)
    public void getPhysicalConnectionOnPluggable() throws JsonProcessingException {
        String expectedJson = "{\n " +
                "\"name\": \"Mock_ONT_2\",\n " +
                "\"deviceA\": \"noshelf_dev_1\",\n " +
                "\"deviceZ\": \"noshelf_dev_2\",\n " +
                "\"deviceAPort\": 3,\n " +
                "\"deviceZPort\": 3,\n " +
                "\"connectionType\": \"Cable Fiber\",\n " +
                "\"bandwidth\": 200\n " +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        PhysicalConnection expectedPhysicalConnection = objectMapper.readValue(expectedJson, PhysicalConnection.class);

        //To get the physical connection based on the name
        PhysicalConnection actualPhysicalConnection = connectionRestApis.findPhysicalConnection("Mock_ONT_2");

        //To check whether the given data and actual data from the database are equal or not
        Assertions.assertEquals(expectedPhysicalConnection.getName().toLowerCase(), actualPhysicalConnection.getName());
        Assertions.assertEquals(expectedPhysicalConnection.getDeviceA(), actualPhysicalConnection.getDeviceA());
        Assertions.assertEquals(expectedPhysicalConnection.getDeviceZ(), actualPhysicalConnection.getDeviceZ());
        Assertions.assertEquals(expectedPhysicalConnection.getDeviceAPort(), actualPhysicalConnection.getDeviceAPort());
        Assertions.assertEquals(expectedPhysicalConnection.getDeviceZPort(), actualPhysicalConnection.getDeviceZPort());
        Assertions.assertEquals(expectedPhysicalConnection.getConnectionType(), actualPhysicalConnection.getConnectionType());
        Assertions.assertEquals(expectedPhysicalConnection.getBandwidth(), actualPhysicalConnection.getBandwidth());
    }
}

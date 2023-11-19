package com.prodapt.netinsight.IntegrationTesting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prodapt.netinsight.connectionManager.ConnectionRestApis;
import com.prodapt.netinsight.connectionManager.LogicalConnection;
import com.prodapt.netinsight.deviceInstanceManager.DeviceInstanceRestApis;
import com.prodapt.netinsight.deviceInstanceManager.LogicalPort;
import com.prodapt.netinsight.orderManager.OrderEntity;
import com.prodapt.netinsight.orderManager.OrderRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * This class creates Logical Ports and Logical Connection between the Devices.
 */

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RunLogicalConnectionIntegrationTest {

    @Autowired
    DeviceInstanceRestApis deviceInstanceRestApis;

    @Autowired
    ConnectionRestApis connectionRestApis;

    @Autowired
    OrderRepository orderRepository;

    /**
     * Test Method to Create a Logical Port with respect to the Device.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(1)
    public void createLogicalPortInDevice1() throws JsonProcessingException {
        String logicalPort = "{" +
                "\"name\":\"mocklogicalport1\",\n" +
                "\"positionOnDevice\":1,\n" +
                "\"portType\":\"rj45\",\n" +
                "\"operationalState\":\"active\",\n" +
                "\"administrativeState\":\"active\",\n" +
                "\"usageState\":\"active\",\n" +
                "\"positionOnPort\":1\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        LogicalPort logicalPortDetails = objectMapper.readValue(logicalPort, LogicalPort.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To Create a Logical Port with respect to the Device
        deviceInstanceRestApis.createLogicalPortOnPhysicalPortOnDevice(existingOrder.getId(), "noShelf_dev_1", logicalPortDetails);
    }

    /**
     * Test Method to Create a Logical Port with respect to the Device.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(2)
    public void createLogicalPortInDevice2() throws JsonProcessingException {
        String logicalPort = "{" +
                "\"name\":\"mocklogicalport2\"\n," +
                "\"positionOnDevice\":1,\n" +
                "\"portType\":\"rj45+\",\n" +
                "\"operationalState\":\"active\",\n" +
                "\"administrativeState\":\"active\",\n" +
                "\"usageState\":\"active\",\n" +
                "\"positionOnPort\":1\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        LogicalPort logicalPortDetails = objectMapper.readValue(logicalPort, LogicalPort.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To Create a Logical Port with respect to the Device
        deviceInstanceRestApis.createLogicalPortOnPhysicalPortOnDevice(existingOrder.getId(), "noShelf_dev_2", logicalPortDetails);
    }

    /**
     * Test Method to Create a Logical Port with respect to the Device.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(3)
    public void createLogicalPortInDevice3() throws JsonProcessingException {
        String logicalPort = "{" +
                "\"name\":\"mocklogicalport3\"\n," +
                "\"positionOnCard\":2,\n" +
                "\"portType\":\"rj45\",\n" +
                "\"operationalState\":\"active\",\n" +
                "\"administrativeState\":\"active\",\n" +
                "\"usageState\":\"active\",\n" +
                "\"positionOnPort\":2\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        LogicalPort logicalPortDetails = objectMapper.readValue(logicalPort, LogicalPort.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To Create a Logical Port with respect to the Device
        deviceInstanceRestApis.createLogicalPortOnPhysicalPortInCard(existingOrder.getId(), "mockCard_1", logicalPortDetails);
    }

    /**
     * Test Method to Create a Logical Port with respect to the Device.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(4)
    public void createLogicalPortInDevice4() throws JsonProcessingException {
        String logicalPort = "{" +
                "\"name\":\"mocklogicalport4\"\n," +
                "\"positionOnCard\":2,\n" +
                "\"portType\":\"rj45\",\n" +
                "\"operationalState\":\"active\",\n" +
                "\"administrativeState\":\"active\",\n" +
                "\"usageState\":\"active\",\n" +
                "\"positionOnPort\":2\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        LogicalPort logicalPortDetails = objectMapper.readValue(logicalPort, LogicalPort.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To Create a Logical Port with respect to the Device
        deviceInstanceRestApis.createLogicalPortOnPhysicalPortInCard(existingOrder.getId(), "mockCard_4", logicalPortDetails);
    }

    /**
     * Test Method to Update a Logical Port.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(5)
    public void updateLogicalPortInDevice1() throws JsonProcessingException {
        String logicalPort = "{" +
                "\"name\":\"mocklogicalport1\"\n," +
                "\"positionOnDevice\":1,\n" +
                "\"portType\":\"rj12\",\n" +
                "\"operationalState\":\"active\",\n" +
                "\"administrativeState\":\"active\",\n" +
                "\"usageState\":\"active\",\n" +
                "\"positionOnPort\":1\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        LogicalPort logicalPortDetails = objectMapper.readValue(logicalPort, LogicalPort.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To Update a Logical Port
        deviceInstanceRestApis.updateLogicalPortByName(existingOrder.getId(), "mocklogicalport1", logicalPortDetails);
    }

    /**
     * Test Method to Update a Logical Port.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(6)
    public void updateLogicalPortInDevice2() throws JsonProcessingException {
        String logicalPort = "{" +
                "\"name\":\"mocklogicalport2\"\n," +
                "\"positionOnDevice\":1,\n" +
                "\"portType\":\"rj12\",\n" +
                "\"operationalState\":\"active\",\n" +
                "\"administrativeState\":\"active\",\n" +
                "\"usageState\":\"active\",\n" +
                "\"positionOnPort\":1\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        LogicalPort logicalPortDetails = objectMapper.readValue(logicalPort, LogicalPort.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To Update a Logical Port
        deviceInstanceRestApis.updateLogicalPortByName(existingOrder.getId(), "mocklogicalport2", logicalPortDetails);
    }

    /**
     * Test Method to Update a Logical Port.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(7)
    public void updateLogicalPortInDevice3() throws JsonProcessingException {
        String logicalPort = "{" +
                "\"name\":\"mocklogicalport3\"\n," +
                "\"positionOnCard\":2,\n" +
                "\"portType\":\"rj12\",\n" +
                "\"operationalState\":\"active\",\n" +
                "\"administrativeState\":\"active\",\n" +
                "\"usageState\":\"active\",\n" +
                "\"positionOnPort\":2\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        LogicalPort logicalPortDetails = objectMapper.readValue(logicalPort, LogicalPort.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To Update a Logical Port
        deviceInstanceRestApis.updateLogicalPortByName(existingOrder.getId(), "mocklogicalport3", logicalPortDetails);
    }

    /**
     * Test Method to Update a Logical Port.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(8)
    public void updateLogicalPortInDevice4() throws JsonProcessingException {
        String logicalPort = "{" +
                "\"name\":\"mocklogicalport4\"\n," +
                "\"positionOnCard\":2,\n" +
                "\"portType\":\"rj12\",\n" +
                "\"operationalState\":\"active\",\n" +
                "\"administrativeState\":\"active\",\n" +
                "\"usageState\":\"active\",\n" +
                "\"positionOnPort\":2\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        LogicalPort logicalPortDetails = objectMapper.readValue(logicalPort, LogicalPort.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To Update a Logical Port
        deviceInstanceRestApis.updateLogicalPortByName(existingOrder.getId(), "mocklogicalport4", logicalPortDetails);
    }

    /**
     * Test Method to Retrieve the logical Port Details.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(9)
    public void getLogicalPortInDevice1() throws JsonProcessingException {
        String expectedJson = "{" +
                "\"name\":\"mocklogicalport1\"\n," +
                "\"positionOnDevice\":1,\n" +
                "\"portType\":\"rj12\",\n" +
                "\"operationalState\":\"active\",\n" +
                "\"administrativeState\":\"active\",\n" +
                "\"usageState\":\"active\",\n" +
                "\"positionOnPort\":1\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        LogicalPort expectedDetails = objectMapper.readValue(expectedJson, LogicalPort.class);

        //To Retrieve the logical Port Details
        LogicalPort actualDetails = deviceInstanceRestApis.getLogicalPortByName("mocklogicalport1");

        //To check whether the given data and actual data from the database are equal or not
        Assertions.assertEquals(expectedDetails.getName(), actualDetails.getName());
        Assertions.assertEquals(expectedDetails.getPositionOnDevice(), actualDetails.getPositionOnDevice());
        Assertions.assertEquals(expectedDetails.getPortType(), actualDetails.getPortType());
        Assertions.assertEquals(expectedDetails.getOperationalState(), actualDetails.getOperationalState());
        Assertions.assertEquals(expectedDetails.getAdministrativeState(), actualDetails.getAdministrativeState());
        Assertions.assertEquals(expectedDetails.getUsageState(), actualDetails.getUsageState());
    }

    /**
     * Test Method to Retrieve the logical Port Details.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(10)
    public void getLogicalPortInDevice2() throws JsonProcessingException {
        String expectedJson = "{" +
                "\"name\":\"mocklogicalport2\"\n," +
                "\"positionOnDevice\":1,\n" +
                "\"portType\":\"rj12\",\n" +
                "\"operationalState\":\"active\",\n" +
                "\"administrativeState\":\"active\",\n" +
                "\"usageState\":\"active\",\n" +
                "\"positionOnPort\":1\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        LogicalPort expectedDetails = objectMapper.readValue(expectedJson, LogicalPort.class);

        //To Retrieve the logical Port Details
        LogicalPort actualDetails = deviceInstanceRestApis.getLogicalPortByName("mocklogicalport2");

        //To check whether the given data and actual data from the database are equal or not
        Assertions.assertEquals(expectedDetails.getName(), actualDetails.getName());
        Assertions.assertEquals(expectedDetails.getPositionOnDevice(), actualDetails.getPositionOnDevice());
        Assertions.assertEquals(expectedDetails.getPortType(), actualDetails.getPortType());
        Assertions.assertEquals(expectedDetails.getOperationalState(), actualDetails.getOperationalState());
        Assertions.assertEquals(expectedDetails.getAdministrativeState(), actualDetails.getAdministrativeState());
        Assertions.assertEquals(expectedDetails.getUsageState(), actualDetails.getUsageState());
    }

    /**
     * Test Method to Retrieve the logical Port Details.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(11)
    public void getLogicalPortInDevice3() throws JsonProcessingException {
        String expectedJson = "{" +
                "\"name\":\"mocklogicalport3\"\n," +
                "\"positionOnCard\":2,\n" +
                "\"portType\":\"rj12\",\n" +
                "\"operationalState\":\"active\",\n" +
                "\"administrativeState\":\"active\",\n" +
                "\"usageState\":\"active\",\n" +
                "\"positionOnPort\":2\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        LogicalPort expectedDetails = objectMapper.readValue(expectedJson, LogicalPort.class);

        //To Retrieve the logical Port Details
        LogicalPort actualDetails = deviceInstanceRestApis.getLogicalPortByName("mocklogicalport3");

        //To check whether the given data and actual data from the database are equal or not
        Assertions.assertEquals(expectedDetails.getName(), actualDetails.getName());
        Assertions.assertEquals(expectedDetails.getPositionOnDevice(), actualDetails.getPositionOnDevice());
        Assertions.assertEquals(expectedDetails.getPortType(), actualDetails.getPortType());
        Assertions.assertEquals(expectedDetails.getOperationalState(), actualDetails.getOperationalState());
        Assertions.assertEquals(expectedDetails.getAdministrativeState(), actualDetails.getAdministrativeState());
        Assertions.assertEquals(expectedDetails.getUsageState(), actualDetails.getUsageState());
    }

    /**
     * Test Method to Retrieve the logical Port Details.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(12)
    public void getLogicalPortInDevice4() throws JsonProcessingException {
        String expectedJson = "{" +
                "\"name\":\"mocklogicalport4\"\n," +
                "\"positionOnCard\":2,\n" +
                "\"portType\":\"rj12\",\n" +
                "\"operationalState\":\"active\",\n" +
                "\"administrativeState\":\"active\",\n" +
                "\"usageState\":\"active\",\n" +
                "\"positionOnPort\":2\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        LogicalPort expectedDetails = objectMapper.readValue(expectedJson, LogicalPort.class);

        //To Retrieve the logical Port Details
        LogicalPort actualDetails = deviceInstanceRestApis.getLogicalPortByName("mocklogicalport4");

        //To check whether the given data and actual data from the database are equal or not
        Assertions.assertEquals(expectedDetails.getName(), actualDetails.getName());
        Assertions.assertEquals(expectedDetails.getPositionOnDevice(), actualDetails.getPositionOnDevice());
        Assertions.assertEquals(expectedDetails.getPortType(), actualDetails.getPortType());
        Assertions.assertEquals(expectedDetails.getOperationalState(), actualDetails.getOperationalState());
        Assertions.assertEquals(expectedDetails.getAdministrativeState(), actualDetails.getAdministrativeState());
        Assertions.assertEquals(expectedDetails.getUsageState(), actualDetails.getUsageState());
    }

    /**
     * Test Method to Create a Logical Connection between two devices.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(13)
    void createLogicalConnection() throws JsonProcessingException {
        String logicJson = "{" +
                "\"name\":\"MockConnection1\"\n," +
                "\"deviceA\":\"noshelf_dev_1\",\n" +
                "\"deviceZ\":\"multishelf_dev_4\",\n" +
                "\"devicesConnected\":[\"noShelf_dev_1\", \"noShelf_dev_2\", \"multiShelf_dev_3\", \"multiShelf_dev_4\"],\n" +
                "\"connectionType\":\"logical\",\n" +
                "\"bandwidth\":100,\n" +
                "\"deviceALogicalPort\":\"1/1\",\n" +
                "\"deviceZLogicalPort\":\"2/1/2/2\",\n" +
                "\"physicalConnections\":[\"mock_ont_1\", \"mock_ont_3\", \"mock_ont_5\"]\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        LogicalConnection logicalConnection = objectMapper.readValue(logicJson, LogicalConnection.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To Create a Logical Connection between two devices
        connectionRestApis.createLogicalConnection(existingOrder.getId(), logicalConnection);
    }

    /**
     * Test Method to Update the Logical Connection Details
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(14)
    void updateLogicalConnection() throws JsonProcessingException {
        String updateLogicJson = "{" +
                "\"name\":\"MockConnection1\"\n," +
                "\"deviceA\":\"noshelf_dev_1\",\n" +
                "\"deviceZ\":\"multishelf_dev_4\",\n" +
                "\"devicesConnected\":[\"noShelf_dev_1\", \"noShelf_dev_2\", \"multiShelf_dev_3\", \"multiShelf_dev_4\"],\n" +
                "\"connectionType\":\"logicalConnection\",\n" +
                "\"bandwidth\":200,\n" +
                "\"deviceALogicalPort\":\"1/1\",\n" +
                "\"deviceZLogicalPort\":\"2/1/2/2\",\n" +
                "\"physicalConnections\":[\"mock_ont_1\", \"mock_ont_3\", \"mock_ont_5\"]\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        LogicalConnection logicalConnection = objectMapper.readValue(updateLogicJson, LogicalConnection.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To Update the Logical Connection Details
        connectionRestApis.updateLogicalConnectionByName(existingOrder.getId(), "MockConnection1", logicalConnection);
    }

    /**
     * Test Method to retrieve the Logical Connection Details.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(15)
    void getLogicalConnection() throws JsonProcessingException {
        String expectedJson = "{" +
                "\"name\":\"MockConnection1\"\n," +
                "\"deviceA\":\"noshelf_dev_1\",\n" +
                "\"deviceZ\":\"multishelf_dev_4\",\n" +
                "\"devicesConnected\":[\"noShelf_dev_1\", \"noShelf_dev_2\", \"multiShelf_dev_3\", \"multiShelf_dev_4\"],\n" +
                "\"connectionType\":\"logicalConnection\",\n" +
                "\"bandwidth\":200,\n" +
                "\"deviceALogicalPort\":\"mocklogicalport1\",\n" +
                "\"deviceZLogicalPort\":\"mocklogicalport4\",\n" +
                "\"physicalConnections\":[\"mock_ont_1\", \"mock_ont_3\", \"mock_ont_5\"]\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        LogicalConnection expectedLogicalConnection = objectMapper.readValue(expectedJson, LogicalConnection.class);

        //To retrieve the Logical Connection Details
        LogicalConnection actualLogicalConnection = connectionRestApis.findLogicalConnection("MockConnection1");

        //To check whether the given data and actual data from the database are equal or not
        Assertions.assertEquals(expectedLogicalConnection.getName().toLowerCase(), actualLogicalConnection.getName());
        Assertions.assertEquals(expectedLogicalConnection.getDeviceA(), actualLogicalConnection.getDeviceA());
        Assertions.assertEquals(expectedLogicalConnection.getDeviceZ(), actualLogicalConnection.getDeviceZ());
        Assertions.assertEquals(expectedLogicalConnection.getConnectionType(), actualLogicalConnection.getConnectionType());
        Assertions.assertEquals(expectedLogicalConnection.getBandwidth(), actualLogicalConnection.getBandwidth());
        Assertions.assertEquals(expectedLogicalConnection.getDeviceALogicalPort(), actualLogicalConnection.getDeviceALogicalPort());
        Assertions.assertEquals(expectedLogicalConnection.getDeviceZLogicalPort(), actualLogicalConnection.getDeviceZLogicalPort());
        Assertions.assertEquals(expectedLogicalConnection.getPhysicalConnections(), actualLogicalConnection.getPhysicalConnections());
    }
}

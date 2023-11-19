package com.prodapt.netinsight.IntegrationTesting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prodapt.netinsight.connectionManager.ConnectionRestApis;
import com.prodapt.netinsight.connectionManager.LogicalConnection;
import com.prodapt.netinsight.connectionManager.PhysicalConnection;
import com.prodapt.netinsight.customerManager.Customer;
import com.prodapt.netinsight.customerManager.CustomerManagerRestApis;
import com.prodapt.netinsight.deviceInstanceManager.*;
import com.prodapt.netinsight.deviceMetaModeller.DeviceMetaModel;
import com.prodapt.netinsight.deviceMetaModeller.ModellingRestApis;
import com.prodapt.netinsight.locationManager.*;
import com.prodapt.netinsight.orderManager.OrderEntity;
import com.prodapt.netinsight.orderManager.OrderManagerRestApis;
import com.prodapt.netinsight.orderManager.OrderRepository;
import com.prodapt.netinsight.serviceManager.Service;
import com.prodapt.netinsight.serviceManager.ServiceRestApis;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * This class helps in deleting all the Services, Logical Connections, Logical Ports
 * Physical Connections, Pluggables, Ports, Cards, Devices, Racks, Building, City, State
 * and Country
 */

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestIntegrationDeleteAll {

    @Autowired
    ServiceRestApis serviceRestApis;

    @Autowired
    ConnectionRestApis connectionRestApis;

    @Autowired
    DeviceInstanceRestApis deviceInstanceRestApis;

    @Autowired
    ModellingRestApis modellingRestApis;

    @Autowired
    LocationManagerRestApis locationManagerRestApis;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderManagerRestApis orderManagerRestApis;

    @Autowired
    CustomerManagerRestApis customerManagerRestApis;

    /**
     * Test Method to Delete a Service.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(1)
    public void deleteService() throws JsonProcessingException {
        String deleteService = "{\n" +
                "\"name\": \"MockService1\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Service service = objectMapper.readValue(deleteService, Service.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To Delete a Service
        serviceRestApis.deleteService(existingOrder.getId(), service.getName());
    }

    /**
     * Test Method to Delete a Logical Connection.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(2)
    public void deleteLogicalConnection() throws JsonProcessingException {
        String deleteLogicalConnection = "{\n" +
                "\"name\": \"MockConnection1\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        LogicalConnection logicalConnection = objectMapper.readValue(deleteLogicalConnection, LogicalConnection.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To Delete a Logical Connection
        connectionRestApis.deleteLogicalConnection(existingOrder.getId(), logicalConnection.getName());
    }

    /**
     * Test Method to Delete a Logical Port.
     * Here 4 Logical Ports have been Deleted.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(3)
    public void deleteLogicalPort() throws JsonProcessingException {
        String deleteLogicalPort4 = "{\n" +
                "\"name\": \"mocklogicalport4\"\n" +
                "}";

        String deleteLogicalPort3 = "{\n" +
                "\"name\": \"mocklogicalport3\"\n" +
                "}";

        String deleteLogicalPort2 = "{\n" +
                "\"name\": \"mocklogicalport2\"\n" +
                "}";

        String deleteLogicalPort1 = "{\n" +
                "\"name\": \"mocklogicalport1\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        LogicalPort logicalPort1 = objectMapper.readValue(deleteLogicalPort4, LogicalPort.class);
        LogicalPort logicalPort2 = objectMapper.readValue(deleteLogicalPort3, LogicalPort.class);
        LogicalPort logicalPort3 = objectMapper.readValue(deleteLogicalPort2, LogicalPort.class);
        LogicalPort logicalPort4 = objectMapper.readValue(deleteLogicalPort1, LogicalPort.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To Delete a Logical Port
        deviceInstanceRestApis.deleteLogicalPort(existingOrder.getId(), logicalPort1.getName());
        deviceInstanceRestApis.deleteLogicalPort(existingOrder.getId(), logicalPort2.getName());
        deviceInstanceRestApis.deleteLogicalPort(existingOrder.getId(), logicalPort3.getName());
        deviceInstanceRestApis.deleteLogicalPort(existingOrder.getId(), logicalPort4.getName());
    }

    /**
     * Test Method to Delete a Physical Connection on Pluggable.
     * Here 3 Physical Connection on Pluggable have been Deleted.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(4)
    public void deletePhysicalConnectionOnPluggable() throws JsonProcessingException {
        String deletePhysicalConnection3OnPluggable = "{\n" +
                "\"name\": \"Mock_ONT_6\"\n" +
                "}";

        String deletePhysicalConnection2OnPluggable = "{\n" +
                "\"name\": \"Mock_ONT_4\"\n" +
                "}";

        String deletePhysicalConnection1OnPluggable = "{\n" +
                "\"name\": \"Mock_ONT_2\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        PhysicalConnection physicalConnection1 = objectMapper.readValue(deletePhysicalConnection3OnPluggable, PhysicalConnection.class);
        PhysicalConnection physicalConnection2 = objectMapper.readValue(deletePhysicalConnection2OnPluggable, PhysicalConnection.class);
        PhysicalConnection physicalConnection3 = objectMapper.readValue(deletePhysicalConnection1OnPluggable, PhysicalConnection.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To Delete a Physical Connection on Pluggable
        connectionRestApis.deletePhysicalConnection(existingOrder.getId(), physicalConnection1.getName());
        connectionRestApis.deletePhysicalConnection(existingOrder.getId(), physicalConnection2.getName());
        connectionRestApis.deletePhysicalConnection(existingOrder.getId(), physicalConnection3.getName());
    }

    /**
     * Test Method to Delete a Physical Connection on Port.
     * Here 3 Physical Connection on Port have been Deleted.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(5)
    public void deletePhysicalConnectionOnPort() throws JsonProcessingException {
        String deletePhysicalConnection3OnPort = "{\n" +
                "\"name\": \"Mock_ONT_5\"\n" +
                "}";

        String deletePhysicalConnection2OnPort = "{\n" +
                "\"name\": \"Mock_ONT_3\"\n" +
                "}";

        String deletePhysicalConnection1OnPort = "{\n" +
                "\"name\": \"Mock_ONT_1\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        PhysicalConnection physicalConnection1 = objectMapper.readValue(deletePhysicalConnection3OnPort, PhysicalConnection.class);
        PhysicalConnection physicalConnection2 = objectMapper.readValue(deletePhysicalConnection2OnPort, PhysicalConnection.class);
        PhysicalConnection physicalConnection3 = objectMapper.readValue(deletePhysicalConnection1OnPort, PhysicalConnection.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To Delete a Physical Connection on Port
        connectionRestApis.deletePhysicalConnection(existingOrder.getId(), physicalConnection1.getName());
        connectionRestApis.deletePhysicalConnection(existingOrder.getId(), physicalConnection2.getName());
        connectionRestApis.deletePhysicalConnection(existingOrder.getId(), physicalConnection3.getName());
    }

    /**
     * Test Method to Delete a Pluggable on Card.
     * Here 2 Pluggable on Card have been Deleted.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(6)
    void deletePluggableOnCard() throws JsonProcessingException {
        String deletePluggableOnCard1 = "{\n" +
                "\"name\": \"mockPluggable_7\"\n " +
                "}";

        String deletePluggableOnCard2 = "{\n" +
                "\"name\": \"MockPluggable_5\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Pluggable pluggable1 = objectMapper.readValue(deletePluggableOnCard1, Pluggable.class);
        Pluggable pluggable2 = objectMapper.readValue(deletePluggableOnCard2, Pluggable.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To Delete a Pluggable on Card
        deviceInstanceRestApis.deletePluggable(existingOrder.getId(), pluggable1.getName());
        deviceInstanceRestApis.deletePluggable(existingOrder.getId(), pluggable2.getName());
    }

    /**
     * Test Method to Delete a Port on Card.
     * Here 2 Port on Card have been Deleted.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(7)
    void deletePortOnCard() throws JsonProcessingException {
        String deletePortOnCard1 = "{\n" +
                "\"name\": \"MockPort_7\"\n" +
                "}";

        String deletePortOnCard2 = "{\n" +
                "\"name\": \"MockPort_5\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Port port1 = objectMapper.readValue(deletePortOnCard1, Port.class);
        Port port2 = objectMapper.readValue(deletePortOnCard2, Port.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To Delete a Port on Card
        deviceInstanceRestApis.deletePort(existingOrder.getId(), "multiShelf_dev_4", port1.getName());
        deviceInstanceRestApis.deletePort(existingOrder.getId(), "multiShelf_dev_3", port2.getName());
    }

    /**
     * Test Method to Delete card on Shelves.
     * Here 4 Cards on Shelves have been Deleted
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(8)
    void deleteCardOnShelves() throws JsonProcessingException {
        String deleteCard1 = "{\n" +
                " \"name\": \"mockcard_4\"\n " +
                "}";

        String deleteCard2 = "{\n" +
                " \"name\": \"mockcard_3\"\n " +
                "}";

        String deleteCard3 = "{\n" +
                " \"name\": \"mockcard_2\"\n " +
                "}";

        String deleteCard4 = "{\n" +
                " \"name\": \"mockcard_1\"\n " +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Card card1 = objectMapper.readValue(deleteCard1, Card.class);
        Card card2 = objectMapper.readValue(deleteCard2, Card.class);
        Card card3 = objectMapper.readValue(deleteCard3, Card.class);
        Card card4 = objectMapper.readValue(deleteCard4, Card.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To Delete card on Shelves
        deviceInstanceRestApis.deleteCard(existingOrder.getId(), "multiShelf_dev_4", card1.getName());
        deviceInstanceRestApis.deleteCard(existingOrder.getId(), "multiShelf_dev_4", card2.getName());
        deviceInstanceRestApis.deleteCard(existingOrder.getId(), "multiShelf_dev_3", card3.getName());
        deviceInstanceRestApis.deleteCard(existingOrder.getId(), "multiShelf_dev_3", card4.getName());
    }

    /**
     * To Delete the pluggables which are connected in order with the device.
     * Here two pluggables on Device have been deleted
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(9)
    void deletePluggableOnDevice() throws JsonProcessingException {

        String deletePluggableOnDevice1 = "{\n" +
                "\"name\": \"mockPluggable_3\"\n " +
                "}";

        String deletePluggableOnDevice2 = "{\n" +
                "\"name\": \"mockPluggable_1\"\n " +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Pluggable pluggable = objectMapper.readValue(deletePluggableOnDevice1, Pluggable.class);
        Pluggable pluggable1 = objectMapper.readValue(deletePluggableOnDevice2, Pluggable.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //Delete pluggable by using it's name
        deviceInstanceRestApis.deletePluggable(existingOrder.getId(), pluggable.getName());
        deviceInstanceRestApis.deletePluggable(existingOrder.getId(), pluggable1.getName());
    }

    /**
     * To delete the ports which are connected in order with the device.
     * Here two ports on Device have been deleted
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(10)
    void deletePortOnDevice() throws JsonProcessingException {

        String deletePortOnDevice1 = "{\n" +
                "\"name\": \"mockPort_3\"\n " +
                "}";

        String deletePortOnDevice2 = "{\n" +
                "\"name\": \"mockPort_1\"\n " +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Port port = objectMapper.readValue(deletePortOnDevice1, Port.class);
        Port port1 = objectMapper.readValue(deletePortOnDevice2, Port.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //Delete ports by using it's name
        deviceInstanceRestApis.deletePort(existingOrder.getId(), "noshelf_dev_2", port.getName());
        deviceInstanceRestApis.deletePort(existingOrder.getId(), "noshelf_dev_1", port1.getName());
    }

    /**
     * To delete the devices
     * Here four devices have been deleted
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(11)
    public void DeleteDevices() throws JsonProcessingException {

        String deleteDevice1 = "{\n" +
                "\"name\": \"noshelf_dev_1\"\n " +
                "}";

        String deleteDevice2 = "{\n" +
                "\"name\": \"noshelf_dev_2\"\n " +
                "}";

        String deleteDevice3 = "{\n" +
                "\"name\": \"multishelf_dev_3\"\n " +
                "}";

        String deleteDevice4 = "{\n" +
                "\"name\": \"multishelf_dev_4\"\n " +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Device Device1 = objectMapper.readValue(deleteDevice1, Device.class);
        Device Device2 = objectMapper.readValue(deleteDevice2, Device.class);
        Device Device3 = objectMapper.readValue(deleteDevice3, Device.class);
        Device Device4 = objectMapper.readValue(deleteDevice4, Device.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //Delete devices by using it's name
        deviceInstanceRestApis.deleteDevice(existingOrder.getId(), Device1.getName());
        deviceInstanceRestApis.deleteDevice(existingOrder.getId(), Device2.getName());
        deviceInstanceRestApis.deleteDevice(existingOrder.getId(), Device3.getName());
        deviceInstanceRestApis.deleteDevice(existingOrder.getId(), Device4.getName());
    }

    /**
     * To delete the device model
     * Here 4 device models have been deleted
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(12)
    public void deleteDeviceModel() throws JsonProcessingException {

        String deleteDeviceModelJson1 = "{\n" +
                "\"deviceModel\": \"mockdevicemodel_1\"\n" +
                "}";

        String deleteDeviceModelJson2 = "{\n" +
                "\"deviceModel\": \"mockdevicemodel_2\"\n" +
                "}";

        String deleteDeviceModelJson3 = "{\n" +
                "\"deviceModel\": \"mockdevicemodel_3\"\n" +
                "}";

        String deleteDeviceModelJson4 = "{\n" +
                "\"deviceModel\": \"mockdevicemodel_4\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        DeviceMetaModel deviceMetaModel1 = objectMapper.readValue(deleteDeviceModelJson1, DeviceMetaModel.class);
        DeviceMetaModel deviceMetaModel2 = objectMapper.readValue(deleteDeviceModelJson2, DeviceMetaModel.class);
        DeviceMetaModel deviceMetaModel3 = objectMapper.readValue(deleteDeviceModelJson3, DeviceMetaModel.class);
        DeviceMetaModel deviceMetaModel4 = objectMapper.readValue(deleteDeviceModelJson4, DeviceMetaModel.class);

        //Delete device model by using device model attribute
        modellingRestApis.deleteDeviceMetaModel(deviceMetaModel1.getDeviceModel());
        modellingRestApis.deleteDeviceMetaModel(deviceMetaModel2.getDeviceModel());
        modellingRestApis.deleteDeviceMetaModel(deviceMetaModel3.getDeviceModel());
        modellingRestApis.deleteDeviceMetaModel(deviceMetaModel4.getDeviceModel());
    }

    /**
     * To delete the rack
     * Here 2 racks have been deleted
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(13)
    public void deleteRack() throws JsonProcessingException {

        String deleteRack1 = "{\n" +
                "\"name\": \"MockRack_2\"\n" +
                "}";

        String deleteRack2 = "{\n" +
                "\"name\": \"MockRack_4\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Rack rack1 = objectMapper.readValue(deleteRack1, Rack.class);
        Rack rack2 = objectMapper.readValue(deleteRack2, Rack.class);

        //Delete racks by using it's name
        locationManagerRestApis.deleteRack(rack1.getName());
        locationManagerRestApis.deleteRack(rack2.getName());

    }

    /**
     * Test Method to Delete a Building.
     * Here 4 Buildings have been Deleted
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(14)
    public void deleteBuilding() throws JsonProcessingException {
        String deleteBuilding1 = "{\n" +
                "\"name\": \"mockbuilding_1\"\n" +
                "}";

        String deleteBuilding2 = "{\n" +
                "\"name\": \"mockbuilding_2\"\n" +
                "}";

        String deleteBuilding3 = "{\n" +
                "\"name\": \"mockbuilding_3\"\n" +
                "}";

        String deleteBuilding4 = "{\n" +
                "\"name\": \"mockbuilding_4\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Building building1 = objectMapper.readValue(deleteBuilding1, Building.class);
        Building building2 = objectMapper.readValue(deleteBuilding2, Building.class);
        Building building3 = objectMapper.readValue(deleteBuilding3, Building.class);
        Building building4 = objectMapper.readValue(deleteBuilding4, Building.class);

        //To Delete a Building
        locationManagerRestApis.deleteBuilding(building1.getName());
        locationManagerRestApis.deleteBuilding(building2.getName());
        locationManagerRestApis.deleteBuilding(building3.getName());
        locationManagerRestApis.deleteBuilding(building4.getName());
    }

    /**
     * Test Method to Delete a City.
     * Here 4 Cities have been Deleted
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(15)
    public void deleteCity() throws JsonProcessingException {
        String deleteCity1 = "{\n" +
                "\"name\": \"mockCity_1\"\n" +
                "}";

        String deleteCity2 = "{\n" +
                "\"name\": \"mockCity_2\"\n" +
                "}";

        String deleteCity3 = "{\n" +
                "\"name\": \"mockCity_3\"\n" +
                "}";

        String deleteCity4 = "{\n" +
                "\"name\": \"mockCity_4\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        City city1 = objectMapper.readValue(deleteCity1, City.class);
        City city2 = objectMapper.readValue(deleteCity2, City.class);
        City city3 = objectMapper.readValue(deleteCity3, City.class);
        City city4 = objectMapper.readValue(deleteCity4, City.class);

        //To Delete a City
        locationManagerRestApis.deleteCity(city1.getName());
        locationManagerRestApis.deleteCity(city2.getName());
        locationManagerRestApis.deleteCity(city3.getName());
        locationManagerRestApis.deleteCity(city4.getName());
    }

    /**
     * Test Method to Delete a State.
     * Here 4 States have been Deleted
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(16)
    public void deleteState() throws JsonProcessingException {
        String deleteState1 = "{\n" +
                "\"name\": \"mockstate_1\"\n" +
                "}";

        String deleteState2 = "{\n" +
                "\"name\": \"mockstate_2\"\n" +
                "}";

        String deleteState3 = "{\n" +
                "\"name\": \"mockstate_3\"\n" +
                "}";

        String deleteState4 = "{\n" +
                "\"name\": \"mockstate_4\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        State state1 = objectMapper.readValue(deleteState1, State.class);
        State state2 = objectMapper.readValue(deleteState2, State.class);
        State state3 = objectMapper.readValue(deleteState3, State.class);
        State state4 = objectMapper.readValue(deleteState4, State.class);

        //To Delete a State
        locationManagerRestApis.deleteState(state1.getName());
        locationManagerRestApis.deleteState(state2.getName());
        locationManagerRestApis.deleteState(state3.getName());
        locationManagerRestApis.deleteState(state4.getName());
    }

    /**
     * Test Method to Delete a Country.
     * Here 4 Countries have been Deleted
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(17)
    public void deleteCountry() throws JsonProcessingException {
        String deleteCountry1 = "{\n" +
                "\"name\": \"mockcountry_1\"\n" +
                "}";

        String deleteCountry2 = "{\n" +
                "\"name\": \"mockcountry_2\"\n" +
                "}";

        String deleteCountry3 = "{\n" +
                "\"name\": \"mockcountry_3\"\n" +
                "}";

        String deleteCountry4 = "{\n" +
                "\"name\": \"mockcountry_4\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Country country1 = objectMapper.readValue(deleteCountry1, Country.class);
        Country country2 = objectMapper.readValue(deleteCountry2, Country.class);
        Country country3 = objectMapper.readValue(deleteCountry3, Country.class);
        Country country4 = objectMapper.readValue(deleteCountry4, Country.class);

        //To Delete a Country
        locationManagerRestApis.deleteCountry(country1.getName());
        locationManagerRestApis.deleteCountry(country2.getName());
        locationManagerRestApis.deleteCountry(country3.getName());
        locationManagerRestApis.deleteCountry(country4.getName());
    }

    @Test
    @Order(18)
    public void deleteOrder() throws JsonProcessingException {
        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To Delete a Order
        orderManagerRestApis.deleteOrderById(existingOrder.getId());
    }

    @Test
    @Order(19)
    public void deleteCustomer() throws JsonProcessingException {
        String deleteCustomer = "{\n" +
                "\"name\": \"MockCustomer\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Customer customer = objectMapper.readValue(deleteCustomer, Customer.class);

        //To Delete a Customer
        customerManagerRestApis.deleteCustomer(customer.getName().toLowerCase());
    }
}
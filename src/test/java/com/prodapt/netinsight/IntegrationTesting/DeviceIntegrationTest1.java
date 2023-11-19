package com.prodapt.netinsight.IntegrationTesting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prodapt.netinsight.deviceInstanceManager.Device;
import com.prodapt.netinsight.deviceInstanceManager.DeviceInstanceRestApis;
import com.prodapt.netinsight.deviceInstanceManager.Pluggable;
import com.prodapt.netinsight.deviceInstanceManager.Port;
import com.prodapt.netinsight.deviceMetaModeller.DeviceMetaModel;
import com.prodapt.netinsight.deviceMetaModeller.ModellingRestApis;
import com.prodapt.netinsight.locationManager.*;
import com.prodapt.netinsight.orderManager.OrderEntity;
import com.prodapt.netinsight.orderManager.OrderRepository;
import com.prodapt.netinsight.tmfWrapper.DeviceInstanceApiWrappers;
import com.prodapt.netinsight.tmfWrapper.pojo.DeviceDetailsResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * This class creates a device with respect to the Building with no shelves.
 */

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DeviceIntegrationTest1 {

    @Autowired
    LocationManagerRestApis locationManagerRestApis;

    @Autowired
    ModellingRestApis modellingRestApis;

    @Autowired
    DeviceInstanceRestApis deviceInstanceRestApis;

    @Autowired
    DeviceInstanceApiWrappers deviceInstanceApiWrappers;

    @Autowired
    OrderRepository orderRepository;

    /**
     * Test Method to Create a Country
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(1)
    public void createCountry() throws JsonProcessingException {
        String createCountry = "{\n" +
                "\"name\": \"mockCountry_1\",\n" +
                "\"notes\": \"mockCountry_1 is my country\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Country country = objectMapper.readValue(createCountry, Country.class);

        //To Create A Country
        locationManagerRestApis.createCountry(country);
    }

    /**
     * Test Method to Retrieve Country Details
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(2)
    public void getCountry() throws JsonProcessingException {
        String expectedJson = "{\n" +
                "\"name\": \"mockCountry_1\",\n" +
                "\"notes\": \"mockCountry_1 is my country\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Country expectedCountry = objectMapper.readValue(expectedJson, Country.class);

        //To retrieve the details of the Country
        Country actualCountry = locationManagerRestApis.getCountry("mockCountry_1");

        //To check whether the given data and actual data from the database are equal or not
        Assertions.assertEquals(expectedCountry.getName().toLowerCase(), actualCountry.getName());
        Assertions.assertEquals(expectedCountry.getNotes(), actualCountry.getNotes());
    }

    /**
     * Test Method to Create a State with respect to the Country.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(3)
    void createState() throws JsonProcessingException {
        String createState = "{\n" +
                "\"name\": \"mockState_1\",\n" +
                "\"notes\": \"mockState_1 is a state\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        State state = objectMapper.readValue(createState, State.class);

        //To Create a State with respect to the Country
        locationManagerRestApis.createState("mockCountry_1", state);
    }

    /**
     * Test Method to retrieve the State Details
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(4)
    public void getState() throws JsonProcessingException {
        String expectedJson = "{\n" +
                "\"name\": \"mockState_1\",\n" +
                "\"notes\": \"mockState_1 is a state\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        State expectedState = objectMapper.readValue(expectedJson, State.class);

        //To retrieve details of the State
        State actualState = locationManagerRestApis.getState("mockState_1");

        //To check whether the given data and actual data from the database are equal or not
        Assertions.assertEquals(expectedState.getName().toLowerCase(), actualState.getName());
        Assertions.assertEquals(expectedState.getNotes(), actualState.getNotes());
    }

    /**
     * Test Method to Create a City with respect to the State.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(5)
    public void createCity() throws JsonProcessingException {
        String createCity = "{\n" +
                "\"name\":\"mockCity_1\",\n" +
                "\"notes\":\"mockCity_1 is a city\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        City city = objectMapper.readValue(createCity, City.class);

        //To Create a City with respect to the State
        locationManagerRestApis.createCity("mockState_1", city);
    }

    /**
     * Test Method to retrieve the City Details
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(6)
    public void getCity() throws JsonProcessingException {
        String expectedJson = "{\n" +
                "\"name\":\"mockCity_1\",\n" +
                "\"notes\":\"mockCity_1 is a city\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        City expectedCity = objectMapper.readValue(expectedJson, City.class);

        //To retrieve the details of the City
        City actualCity = locationManagerRestApis.getCity("mockCity_1");

        //To check whether the given data and actual data from the database are equal or not
        Assertions.assertEquals(expectedCity.getName().toLowerCase(), actualCity.getName());
        Assertions.assertEquals(expectedCity.getNotes(), actualCity.getNotes());
    }

    /**
     * Test Method to Create a Building with respect to the City.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(7)
    void createBuilding() throws JsonProcessingException {
        String createbuilding = "{\n" +
                " \"name\": \"mockBuilding_1\",\n" +
                " \"clliCode\": \"mockBuilding1234\",\n" +
                " \"phoneNumber\": \"9876543210\",\n" +
                " \"contactPerson\": \"mock1\",\n" +
                " \"address\": \"mockAddress1, Chennai, Tamil Nadu\",\n" +
                " \"latitude\": \"12.6789\",\n" +
                " \"longitude\": \"85.4567\",\n" +
                " \"drivingInstructions\": \"road, metro\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Building building = objectMapper.readValue(createbuilding, Building.class);

        //To Create a building with respect to the City
        locationManagerRestApis.createBuilding("mockCity_1", building);
    }

    /**
     * Test Method to retrieve the Building Details.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(8)
    public void getBuilding() throws JsonProcessingException {
        String expectedJson = "{\n" +
                " \"name\": \"mockBuilding_1\",\n" +
                " \"clliCode\": \"mockBuilding1234\",\n" +
                " \"phoneNumber\": \"9876543210\",\n" +
                " \"contactPerson\": \"mock1\",\n" +
                " \"address\": \"mockAddress1, Chennai, Tamil Nadu\",\n" +
                " \"latitude\": \"12.6789\",\n" +
                " \"longitude\": \"85.4567\",\n" +
                " \"drivingInstructions\": \"road, metro\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Building expectedBuilding = objectMapper.readValue(expectedJson, Building.class);

        //To retrieve the details of the Building
        Building actualBuilding = locationManagerRestApis.getBuilding("mockBuilding_1");

        //To check whether the given data and actual data from the database are equal or not
        Assertions.assertEquals(expectedBuilding.getName().toLowerCase(), actualBuilding.getName());
        Assertions.assertEquals(expectedBuilding.getClliCode(), actualBuilding.getClliCode());
        Assertions.assertEquals(expectedBuilding.getPhoneNumber(), actualBuilding.getPhoneNumber());
        Assertions.assertEquals(expectedBuilding.getContactPerson(), actualBuilding.getContactPerson());
        Assertions.assertEquals(expectedBuilding.getAddress(), actualBuilding.getAddress());
        Assertions.assertEquals(expectedBuilding.getLatitude(), actualBuilding.getLatitude());
        Assertions.assertEquals(expectedBuilding.getLongitude(), actualBuilding.getLongitude());
        Assertions.assertEquals(expectedBuilding.getDrivingInstructions(), actualBuilding.getDrivingInstructions());
    }

    /**
     * Test Method to Update the Building with respect to the City.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(9)
    public void updateBuilding() throws JsonProcessingException {
        String updateBuilding = "{\n" +
                " \"name\": \"mockBuilding_1\",\n" +
                " \"clliCode\": \"mockBuilding1\",\n" +
                " \"phoneNumber\": \"9876543210\",\n" +
                " \"contactPerson\": \"mock12345\",\n" +
                " \"address\": \"mockaddress12345, Chennai, Tamil Nadu\",\n" +
                " \"latitude\": \"12.6789\",\n" +
                " \"longitude\": \"85.4567\",\n" +
                " \"drivingInstructions\": \"road, metro\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Building updatedBuilding = objectMapper.readValue(updateBuilding, Building.class);

        //To Update the Building with respect to the City
        locationManagerRestApis.updateBuilding("mockCity_1", updatedBuilding);
    }

    /**
     * Test Method to retrieve details of the updated Building.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(10)
    public void getUpdatedBuilding() throws JsonProcessingException {
        String expectedJson = "{\n" +
                " \"name\": \"mockBuilding_1\",\n" +
                " \"clliCode\": \"mockBuilding1\",\n" +
                " \"phoneNumber\": \"9876543210\",\n" +
                " \"contactPerson\": \"mock12345\",\n" +
                " \"address\": \"mockaddress12345, Chennai, Tamil Nadu\",\n" +
                " \"latitude\": \"12.6789\",\n" +
                " \"longitude\": \"85.4567\",\n" +
                " \"drivingInstructions\": \"road, metro\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Building expectedBuilding = objectMapper.readValue(expectedJson, Building.class);

        //To retrieve details of the updated Building
        Building actualBuilding = locationManagerRestApis.getBuilding("mockBuilding_1");

        //To check whether the given data and actual data from the database are equal or not
        Assertions.assertEquals(expectedBuilding.getName().toLowerCase(), actualBuilding.getName());
        Assertions.assertEquals(expectedBuilding.getClliCode(), actualBuilding.getClliCode());
        Assertions.assertEquals(expectedBuilding.getPhoneNumber(), actualBuilding.getPhoneNumber());
        Assertions.assertEquals(expectedBuilding.getContactPerson(), actualBuilding.getContactPerson());
        Assertions.assertEquals(expectedBuilding.getAddress(), actualBuilding.getAddress());
        Assertions.assertEquals(expectedBuilding.getLatitude(), actualBuilding.getLatitude());
        Assertions.assertEquals(expectedBuilding.getLongitude(), actualBuilding.getLongitude());
        Assertions.assertEquals(expectedBuilding.getDrivingInstructions(), actualBuilding.getDrivingInstructions());
    }

    /**
     * Test Method to Create a DeviceMetamodel with shelves contained and cards that are compatible.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(11)
    public void createDeviceMetaModel() throws JsonProcessingException {
        String createDeviceMetaModel = "{\n" +
                " \"deviceModel\": \"mockDeviceModel_1\",\n" +
                " \"partNumber\": \"QWERTY123\",\n" +
                " \"vendor\": \"Mock\",\n" +
                " \"height\": 12,\n" +
                " \"depth\": 13,\n" +
                " \"width\": 12,\n" +
                " \"shelvesContained\": 0,\n" +
                " \"numOfRackPositionOccupied\": 0,\n" +
                " \"allowedCardList\": []\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        DeviceMetaModel deviceMetaModel = objectMapper.readValue(createDeviceMetaModel, DeviceMetaModel.class);

        //To Create a DeviceMetamodel with shelves contained and cards that are compatible
        modellingRestApis.createDeviceMetaModel(deviceMetaModel);
    }

    /**
     * Test Method to Create a No-Shelf Device with respect to the Building.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(12)
    void createNoshelfDeviceOnBuilding() throws JsonProcessingException {
        String createDeviceJson = "{\n " +
                " \"name\": \"noShelf_dev_1\",\n " +
                " \"deviceModel\": \"mockdevicemodel_1\",\n " +
                " \"location\": \"mock_location\",\n   " +
                " \"organisation\": \"Mock_org\",\n  " +
                " \"customer\": \"Mr.Mock\",\n  " +
                " \"managementIp\": \"\",\n " +
                " \"rackPosition\": 0,\n  " +
                " \"operationalState\": \"Active\",\n  " +
                " \"administrativeState\": \"Active\",\n " +
                " \"usageState\": \"unreserved\",\n   " +
                " \"serialNumber\": \"987654df\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Device createdevice = objectMapper.readValue(createDeviceJson, Device.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To Create a Device with respect to the building
        deviceInstanceRestApis.createDeviceOnBuilding(existingOrder.getId(), "mockBuilding_1", createdevice);
    }

    /**
     * Test Method to Create a Device Level Port with respect to the Device.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(13)
    public void createPortOnDevice() throws JsonProcessingException {
        String createPortOnDevice1 = "{\n" +
                "\"name\": \"MockPort_1\",\n" +
                "\"positionOnCard\": 0,\n" +
                "\"positionOnDevice\": 1,\n" +
                "\"portType\": \"rj45\",\n" +
                "\"operationalState\": \"Active\",\n" +
                "\"administrativeState\": \"Active\",\n" +
                "\"usageState\": \"reserved\"\n" +
                "}";

        String createPortOnDevice2 = "{\n" +
                "\"name\": \"MockPort2\",\n" +
                "\"positionOnCard\": 0,\n" +
                "\"positionOnDevice\": 2,\n" +
                "\"portType\": \"rj45\",\n" +
                "\"operationalState\": \"Active\",\n" +
                "\"administrativeState\": \"Active\",\n" +
                "\"usageState\": \"reserved\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Port port1 = objectMapper.readValue(createPortOnDevice1, Port.class);
        Port port2 = objectMapper.readValue(createPortOnDevice2, Port.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To Create a Device Level Port with respect to the Device
        deviceInstanceRestApis.createPortOnDevice(existingOrder.getId(), "noShelf_dev_1", port1);
        deviceInstanceRestApis.createPortOnDevice(existingOrder.getId(), "noShelf_dev_1", port2);
    }

    /**
     * Test Method to Update the Device level Port Details with respect to the device.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(14)
    public void updatePort() throws JsonProcessingException {
        String updatePort = "{\n" +
                "\"name\": \"MockPort2\",\n" +
                "\"positionOnCard\": 0,\n" +
                "\"positionOnDevice\": 2,\n" +
                "\"portType\": \"rj12\",\n" +
                "\"operationalState\": \"Active\",\n" +
                "\"administrativeState\": \"Active\",\n" +
                "\"usageState\": \"reserved\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Port updatedPort = objectMapper.readValue(updatePort, Port.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To Update the Device level Port details with respect to the device
        deviceInstanceRestApis.updatePortOnDevice(existingOrder.getId(), "noShelf_dev_1", updatedPort);
    }

    /**
     * Test Method to Update the Device Details with respect to the Building.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(15)
    void updateNoshelfDeviceAttributes() throws JsonProcessingException {
        String updateDevice = "{\n " +
                "\"name\": \"noShelf_dev_1\",\n " +
                "\"deviceModel\": \"mockdevicemodel_1\",\n " +
                "\"location\": \"mock_location_1\",\n   " +
                "\"organisation\": \"Mock_org_1\",\n  " +
                "\"customer\": \"Mrs.Mock\",\n  " +
                "\"managementIp\": \"\",\n " +
                "\"rackPosition\": 0,\n  " +
                "\"operationalState\": \"Active\",\n  " +
                "\"administrativeState\": \"Active\",\n " +
                "\"usageState\": \"unreserved\",\n   " +
                "\"serialNumber\": \"987655df\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Device updatedDevice = objectMapper.readValue(updateDevice, Device.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To Update the Device Details with respect to the Building
        deviceInstanceRestApis.updateDeviceOnBuilding(existingOrder.getId(), "mockBuilding_1", updatedDevice);
    }

    /**
     * Test Method to Create a Pluggable with respect to the device.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(16)
    void createPluggable() throws JsonProcessingException {
        String createPluggable1 = "{\n " +
                "\"name\": \"mockPluggable_1\",\n " +
                "\"positionOnCard\": 0,\n " +
                "\"positionOnDevice\": 3,\n " +
                "\"operationalState\": \"Active\",\n " +
                "\"administrativeState\": \"Active\",\n " +
                "\"usageState\": \"unreserved\",\n " +
                "\"vendor\": \"Adtran\",\n " +
                "\"pluggableModel\": \"SFP+\",\n " +
                "\"pluggablePartNumber\": \"avdcdc12as3\"\n" +
                "}";

        String createPluggable2 = "{\n " +
                "\"name\": \"mockPluggable_2\",\n " +
                "\"positionOnCard\": 0,\n " +
                "\"positionOnDevice\": 4,\n " +
                "\"operationalState\": \"Active\",\n " +
                "\"administrativeState\": \"Active\",\n " +
                "\"usageState\": \"unreserved\",\n " +
                "\"vendor\": \"Adtran\",\n " +
                "\"pluggableModel\": \"SFP+\",\n " +
                "\"pluggablePartNumber\": \"avdcdc12\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Pluggable pluggable1 = objectMapper.readValue(createPluggable1, Pluggable.class);
        Pluggable pluggable2 = objectMapper.readValue(createPluggable2, Pluggable.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To Create a Pluggable with respect to the device
        deviceInstanceRestApis.createPluggableOnDevice(existingOrder.getId(), "noShelf_dev_1", pluggable1);
        deviceInstanceRestApis.createPluggableOnDevice(existingOrder.getId(), "noShelf_dev_1", pluggable2);
    }

    /**
     * Test Method to Update the Pluggable with respect to the device.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(17)
    void UpdatePluggable() throws JsonProcessingException {
        String updatePluggable = "{\n " +
                "\"name\": \"mockPluggable_2\",\n " +
                "\"positionOnCard\": 0,\n " +
                "\"positionOnDevice\": 5,\n " +
                "\"operationalState\": \"Active\",\n " +
                "\"administrativeState\": \"Active\",\n " +
                "\"usageState\": \"unreserved\",\n " +
                "\"vendor\": \"Mock\",\n " +
                "\"pluggableModel\": \"SFP+\",\n " +
                "\"pluggablePartNumber\": \"avdcdc12\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Pluggable pluggable = objectMapper.readValue(updatePluggable, Pluggable.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To Update the Pluggable with respect to the device
        deviceInstanceRestApis.updatePluggableOnDevice(existingOrder.getId(), "noShelf_dev_1", pluggable);
    }

    /**
     * Test method to Delete the Pluggable on Device.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(18)
    public void deletePluggableOnDevice() throws JsonProcessingException {
        String deletePluggableOnDevice = "{\n" +
                "\"name\": \"mockPluggable_2\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Pluggable pluggable = objectMapper.readValue(deletePluggableOnDevice, Pluggable.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //To Delete a Pluggable by Name
        deviceInstanceRestApis.deletePluggable(existingOrder.getId(), pluggable.getName());
    }

    /**
     * Test method to Delete the Port on Device.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(19)
    public void deletePortOnDevice() throws JsonProcessingException {
        String deletePortOnDevice = "{\n" +
                "\"name\": \"MockPort2\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Port port = objectMapper.readValue(deletePortOnDevice, Port.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        ///To Delete a Port by Name
        deviceInstanceRestApis.deletePort(existingOrder.getId(), "noShelf_dev_1", port.getName());
    }

    /**
     * Test Method to retrieve overall details about the device.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(20)
    public void getDeviceDetailsTmf() throws JsonProcessingException {
        String expectedJson = "{\n" +
                "\"name\":\"noshelf_dev_1\",\n" +
                "\"@type\":\"device\",\n" +
                "\"isBundle\":false,\n" +
                "\"place\":[\n" +
                "{\n" +
                "\"name\":\"mockbuilding_1\",\n" +
                "\"clliCode\":\"mockBuilding1\",\n" +
                "\"phoneNumber\":\"9876543210\",\n" +
                "\"contactPerson\":\"mock12345\",\n" +
                "\"address\":\"mockaddress12345,Salem,TamilNadu\",\n" +
                "\"latitude\":\"12.6789\",\n" +
                "\"longitude\":\"85.4567\",\n" +
                "\"drivingInstructions\":\"road,metro\"\n" +
                "}\n" +
                "],\n" +
                "\"resourceCharacteristics\":[\n" +
                "{\n" +
                "\"name\":\"serialNumber\",\n" +
                "\"value\":\"987655df\"\n" +
                "},\n" +
                "{\n" +
                "\"name\":\"managementIp\",\n" +
                "\"value\":\"\"\n" +
                "},\n" +
                "{\n" +
                "\"name\":\"rackPosition\",\n" +
                "\"value\":\"0\"\n" +
                "},\n" +
                "{\n" +
                "\"name\":\"deviceModel\",\n" +
                "\"value\":\"mockdevicemodel_1\"\n" +
                "}\n" +
                "],\n" +
                "\"operationalState\":\"Active\",\n" +
                "\"administrativeState\":\"Active\",\n" +
                "\"usageState\":\"unreserved\",\n" +
                "\"shelves\":[],\n" +
                "\"devicePorts\":[\n" +
                "{\n" +
                "\"name\":\"mockport_1\",\n" +
                "\"positionOnCard\":null,\n" +
                "\"positionOnDevice\":1,\n" +
                "\"portType\":\"rj45\",\n" +
                "\"operationalState\":\"Active\",\n" +
                "\"administrativeState\":\"Active\",\n" +
                "\"usageState\":\"reserved\"\n" +
                "}\n" +
                "],\n" +
                "\"devicePluggables\":[\n" +
                "{\n" +
                "\"name\":\"mockpluggable_1\",\n" +
                "\"positionOnCard\":null,\n" +
                "\"positionOnDevice\":3,\n" +
                "\"vendor\":\"Adtran\",\n" +
                "\"pluggableModel\":\"SFP+\",\n" +
                "\"pluggablePartNumber\":\"avdcdc12as3\",\n" +
                "\"operationalState\":\"Active\",\n" +
                "\"administrativeState\":\"Active\",\n" +
                "\"usageState\":\"unreserved\"\n" +
                "}\n" +
                "],\n" +
                "\"resourceSpecification\":{\n" +
                "\"name\":\"mockdevicemodel_1\"\n" +
                "},\n" +
                "\"relatedParty\": null\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        DeviceDetailsResponse expectedDevice = objectMapper.readValue(expectedJson, DeviceDetailsResponse.class);

        //To retrieve the overall detail about the device.
        DeviceDetailsResponse actualDevice = deviceInstanceApiWrappers.getDeviceDetailsTmf("noshelf_dev_1");

        //To check whether the given data and actual data from the database are equal or not
        Assertions.assertEquals(expectedDevice.getName(), actualDevice.getName());
        Assertions.assertEquals(expectedDevice.getPlace().stream().map(Building::getName).findAny().get(), actualDevice.getPlace().stream().map(Building::getName).findAny().get());
        Assertions.assertEquals(expectedDevice.getDevicePorts().stream().map(Port::getName).findAny().get(), actualDevice.getDevicePorts().stream().map(Port::getName).findAny().get());
        Assertions.assertEquals(expectedDevice.getDevicePluggables().stream().map(Pluggable::getName).findAny().get(), actualDevice.getDevicePluggables().stream().map(Pluggable::getName).findAny().get());
        Assertions.assertEquals(expectedDevice.getResourceSpecification().getName(), actualDevice.getResourceSpecification().getName());
    }

//    /**
//     * Test Method to Update the Device Attributes which involves Shelf, Card, Port, Pluggable.
//     *
//     * @throws JsonProcessingException If there is an issue with JSON processing.
//     */
//    @Test
//    @Order(21)
//    void updateDeviceTMFWrapper() throws JsonProcessingException {
//        String updateDevice = "{\n" +
//                "\"name\":\"noshelf_dev_1\",\n" +
//                "\"@type\":\"device\",\n" +
//                "\"isBundle\":false,\n" +
//                "\"place\":[\n" +
//                "{\n" +
//                "\"name\":\"mockbuilding_1\",\n" +
//                "\"clliCode\":\"mockBuilding1\",\n" +
//                "\"phoneNumber\":\"9876543210\",\n" +
//                "\"contactPerson\":\"mock12345\",\n" +
//                "\"address\":\"mockaddress12345,Salem,TamilNadu\",\n" +
//                "\"latitude\":\"12.6789\",\n" +
//                "\"longitude\":\"85.4567\",\n" +
//                "\"drivingInstructions\":\"road,metro\"\n" +
//                "}\n" +
//                "],\n" +
//                "\"resourceCharacteristics\":[\n" +
//                "{\n" +
//                "\"name\":\"serialNumber\",\n" +
//                "\"value\":\"987655df\"\n" +
//                "},\n" +
//                "{\n" +
//                "\"name\":\"managementIp\",\n" +
//                "\"value\":\"\"\n" +
//                "},\n" +
//                "{\n" +
//                "\"name\":\"rackPosition\",\n" +
//                "\"value\":\"0\"\n" +
//                "},\n" +
//                "{\n" +
//                "\"name\":\"deviceModel\",\n" +
//                "\"value\":\"mockdevicemodel_1\"\n" +
//                "}\n" +
//                "],\n" +
//                "\"operationalState\":\"Active\",\n" +
//                "\"administrativeState\":\"Active\",\n" +
//                "\"usageState\":\"reserved\",\n" +
//                "\"shelves\":[],\n" +
//                "\"devicePorts\":[\n" +
//                "{\n" +
//                "\"name\":\"mockport_1\",\n" +
//                "\"positionOnCard\":null,\n" +
//                "\"positionOnDevice\":1,\n" +
//                "\"portType\":\"rj45\",\n" +
//                "\"operationalState\":\"Active\",\n" +
//                "\"administrativeState\":\"Active\",\n" +
//                "\"usageState\":\"unreserved\"\n" +
//                "}\n" +
//                "],\n" +
//                "\"devicePluggables\":[\n" +
//                "{\n" +
//                "\"name\":\"mockpluggable_1\",\n" +
//                "\"positionOnCard\":null,\n" +
//                "\"positionOnDevice\":3,\n" +
//                "\"vendor\":\"Adtran\",\n" +
//                "\"pluggableModel\":\"SFP+\",\n" +
//                "\"pluggablePartNumber\":\"QWERTY120201\",\n" +
//                "\"operationalState\":\"Active\",\n" +
//                "\"administrativeState\":\"Active\",\n" +
//                "\"usageState\":\"unreserved\"\n" +
//                "}\n" +
//                "],\n" +
//                "\"resourceSpecification\":{\n" +
//                "\"name\":\"mockdevicemodel_1\"\n" +
//                "},\n" +
//                "\"relatedParty\": null\n" +
//                "}";
//
//        // Deserialize the expected JSON into object
//        ObjectMapper objectMapper = new ObjectMapper();
//        DeviceDetailsResponse updateDeviceDetails = objectMapper.readValue(updateDevice, DeviceDetailsResponse.class);
//
//        //To Update the overall device attributes which also involves shelf, card, port, pluggable
//        deviceInstanceApiWrappers.createDeviceTmf(updateDeviceDetails);
//    }
}
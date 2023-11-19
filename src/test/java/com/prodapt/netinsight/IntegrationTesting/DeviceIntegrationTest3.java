package com.prodapt.netinsight.IntegrationTesting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prodapt.netinsight.deviceInstanceManager.*;
import com.prodapt.netinsight.deviceMetaModeller.DeviceMetaModel;
import com.prodapt.netinsight.deviceMetaModeller.DeviceMetaModelRepository;
import com.prodapt.netinsight.deviceMetaModeller.ModellingRestApis;
import com.prodapt.netinsight.exceptionsHandler.ServiceException;
import com.prodapt.netinsight.locationManager.*;
import com.prodapt.netinsight.orderManager.OrderEntity;
import com.prodapt.netinsight.orderManager.OrderRepository;
import com.prodapt.netinsight.tmfWrapper.DeviceInstanceApiWrappers;
import com.prodapt.netinsight.tmfWrapper.pojo.DeviceDetailsResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This class creates a device with respect to the Building with multiple shelves.
 */

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DeviceIntegrationTest3 {

    @Autowired
    LocationManagerRestApis locationManagerRestApis;

    @Autowired
    DeviceInstanceRestApis deviceInstanceRestApis;

    @Autowired
    ModellingRestApis modellingRestApis;

    @Autowired
    DeviceInstanceApiWrappers deviceInstanceApiWrappers;

    @Autowired
    DeviceMetaModelRepository deviceMetaModelRepository;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    PortRepository portRepository;

    @Autowired
    PluggableRepository pluggableRepository;

    @Autowired
    OrderRepository orderRepository;

    /**
     * Test method to create a country and verify its attributes.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(1)
    public void createCountry() throws JsonProcessingException {
        String createCountry = "{\n" +
                "\"name\": \"mockCountry_3\",\n" +
                "\"notes\": \"mockCountry_3 is my country\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Country country = objectMapper.readValue(createCountry, Country.class);

        // To create the country
        locationManagerRestApis.createCountry(country);
    }

    /**
     * Test method to retrieve a country by name and verify its attributes.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(2)
    public void getCountry() throws JsonProcessingException {
        String expectedJson = "{\n" +
                "\"name\": \"mockCountry_3\",\n" +
                "\"notes\": \"mockCountry_3 is my country\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Country expectedCountry = objectMapper.readValue(expectedJson, Country.class);

        // To retrieve the country by name
        Country actualCountry = locationManagerRestApis.getCountry("mockCountry_3");

        // Compare the retrieved country with the expected country
        Assertions.assertEquals(expectedCountry.getName().toLowerCase(), actualCountry.getName());
        Assertions.assertEquals(expectedCountry.getNotes(), actualCountry.getNotes());
    }

    /**
     * Test method to create a state and verify its attributes.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(3)
    void createState() throws JsonProcessingException {
        String createState = "{\n" +
                "\"name\": \"mockState_3\",\n" +
                "\"notes\": \"mockState_3 is a state\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        State state = objectMapper.readValue(createState, State.class);

        // To create the state
        locationManagerRestApis.createState("mockCountry_3", state);
    }

    /**
     * Test method to retrieve a state by name and verify its attributes.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(4)
    public void getState() throws JsonProcessingException {
        String expectedJson = "{\n" +
                "\"name\": \"mockState_3\",\n" +
                "\"notes\": \"mockState_3 is a state\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        State expectedState = objectMapper.readValue(expectedJson, State.class);

        // To retrieve the state by name
        State actualState = locationManagerRestApis.getState("mockState_3");

        // Compare the retrieved state with the expected state
        Assertions.assertEquals(expectedState.getName().toLowerCase(), actualState.getName());
        Assertions.assertEquals(expectedState.getNotes(), actualState.getNotes());
    }

    /**
     * Test method to create a city and verify its attributes.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(5)
    public void createCity() throws JsonProcessingException {
        String createCity = "{\n" +
                "\"name\":\"mockCity_3\",\n" +
                "\"notes\":\"mockCity_3 is a city\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        City city = objectMapper.readValue(createCity, City.class);

        // To create the city
        locationManagerRestApis.createCity("mockState_3", city);
    }

    /**
     * Test method to retrieve a city by name and verify its attributes.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(6)
    public void getCity() throws JsonProcessingException {
        String expectedJson = "{\n" +
                "\"name\":\"mockCity_3\",\n" +
                "\"notes\":\"mockCity_3 is a city\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        City expectedCity = objectMapper.readValue(expectedJson, City.class);

        // To retrieve the city by name
        City actualCity = locationManagerRestApis.getCity("mockCity_3");

        // Compare the retrieved city with the expected city
        Assertions.assertEquals(expectedCity.getName().toLowerCase(), actualCity.getName());
        Assertions.assertEquals(expectedCity.getNotes(), actualCity.getNotes());
    }

    /**
     * Test method to create a building and verify its attributes.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(7)
    void createBuilding() throws JsonProcessingException {
        String createBuilding = "{\n" +
                " \"name\": \"mockBuilding_3\",\n" +
                " \"clliCode\": \"mockBuilding1234\",\n" +
                " \"phoneNumber\": \"9876543210\",\n" +
                " \"contactPerson\": \"mock3\",\n" +
                " \"address\": \"mockAddress3, Chennai, Tamil Nadu\",\n" +
                " \"latitude\": \"12.6789\",\n" +
                " \"longitude\": \"85.4567\",\n" +
                " \"drivingInstructions\": \"road, metro\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Building building = objectMapper.readValue(createBuilding, Building.class);

        // To create the building
        locationManagerRestApis.createBuilding("mockCity_3", building);
    }

    /**
     * Test method to retrieve a building by name and verify its attributes.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(8)
    public void getBuilding() throws JsonProcessingException {
        String expectedJson = "{\n" +
                " \"name\": \"mockBuilding_3\",\n" +
                " \"clliCode\": \"mockBuilding1234\",\n" +
                " \"phoneNumber\": \"9876543210\",\n" +
                " \"contactPerson\": \"mock3\",\n" +
                " \"address\": \"mockAddress3, Chennai, Tamil Nadu\",\n" +
                " \"latitude\": \"12.6789\",\n" +
                " \"longitude\": \"85.4567\",\n" +
                " \"drivingInstructions\": \"road, metro\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Building expectedBuilding = objectMapper.readValue(expectedJson, Building.class);

        // To retrieve the building by name
        Building actualBuilding = locationManagerRestApis.getBuilding("mockBuilding_3");

        // Compare the retrieved building with the expected building
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
     * Test method to update a building and verify its attributes.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(9)
    public void updateBuilding() throws JsonProcessingException {
        String updateBuilding = "{\n" +
                " \"name\": \"mockBuilding_3\",\n" +
                " \"clliCode\": \"mockBuidling3\",\n" +
                " \"phoneNumber\": \"9876543210\",\n" +
                " \"contactPerson\": \"mock12345\",\n" +
                " \"address\": \"mockadress12345, Chennai, Tamil Nadu\",\n" +
                " \"latitude\": \"12.6789\",\n" +
                " \"longitude\": \"85.4567\",\n" +
                " \"drivingInstructions\": \"road, metro\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Building updatedBuilding = objectMapper.readValue(updateBuilding, Building.class);

        // To update the building
        locationManagerRestApis.updateBuilding("mockCity_3", updatedBuilding);
    }

    /**
     * Test method to retrieve the updatedBuilding by name and verify its attributes.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(10)
    public void getUpdatedBuilding() throws JsonProcessingException {
        String expectedJson = "{\n" +
                " \"name\": \"mockBuilding_3\",\n" +
                " \"clliCode\": \"mockBuidling3\",\n" +
                " \"phoneNumber\": \"9876543210\",\n" +
                " \"contactPerson\": \"mock12345\",\n" +
                " \"address\": \"mockadress12345, Chennai, Tamil Nadu\",\n" +
                " \"latitude\": \"12.6789\",\n" +
                " \"longitude\": \"85.4567\",\n" +
                " \"drivingInstructions\": \"road, metro\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Building expectedBuilding = objectMapper.readValue(expectedJson, Building.class);

        // To retrieve the updatedBuilding by name
        Building actualBuilding = locationManagerRestApis.getBuilding("mockBuilding_3");

        // Compare the retrieved updatedBuilding with the expected updatedBuilding
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
     * Test method to create a multiShelf deviceMetaModel and verify its attributes.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(11)
    public void createMsDevice() throws JsonProcessingException {
        String createMsDevice = "{\n" +
                "\"deviceModel\": \"mockdevicemodel_3\",\n " +
                "\"partNumber\": \"mock1234\",\n " +
                "\"vendor\": \"mockvendor_3\",\n " +
                "\"height\": 1,\n " +
                "\"depth\": 1,\n " +
                "\"width\": 1,\n " +
                "\"shelvesContained\": 3,\n " +
                "\"allowedCardList\": [\"mockCard_1\",\"mockCard_2\"]\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        DeviceMetaModel msDevice = objectMapper.readValue(createMsDevice, DeviceMetaModel.class);

        // To create the multiShelf deviceMetaModel
        modellingRestApis.createDeviceMetaModel(msDevice);
    }

    /**
     * Test method to create a multiShelf deviceInstance on building and verify its attributes.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(12)
    public void createMsDeviceInstance() throws JsonProcessingException {
        String createMsDeviceInstance = "{\n " +
                "\"name\": \"multiShelf_dev_3\",\n " +
                "\"deviceModel\": \"mockdevicemodel_3\",\n " +
                "\"location\": \"mockBuilding_3\",\n " +
                "\"organisation\": \"Prodapt\",\n " +
                "\"customer\": \"Mr.Mock\",\n " +
                "\"managementIp\": \"\",\n " +
                "\"rackPosition\": 0,\n " +
                "\"operationalState\": \"Active\",\n " +
                "\"administrativeState\": \"Active\",\n " +
                "\"usageState\": \"unreserved\",\n " +
                "\"serialNumber\": \"987654df\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Device device = objectMapper.readValue(createMsDeviceInstance, Device.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        // To create the multiShelf deviceInstance on building
        deviceInstanceRestApis.createDeviceOnBuilding(existingOrder.getId(), "mockBuilding_3", device);
    }

    /**
     * Test method to update a multiShelf deviceInstance on building and verify its attributes.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(13)
    public void updateMsDeviceInstance() throws JsonProcessingException {
        String updateMsDeviceInstance = "{\n " +
                "\"name\": \"multiShelf_dev_3\",\n " +
                "\"deviceModel\": \"mockdevicemodel_3\",\n " +
                "\"location\": \"123456\",\n " +
                "\"organisation\": \"Prodapt_1\",\n " +
                "\"customer\": \"Mrs.Mockie\",\n " +
                "\"managementIp\": \"\",\n " +
                "\"rackPosition\": 0,\n " +
                "\"operationalState\": \"Active\",\n " +
                "\"administrativeState\": \"Active\",\n " +
                "\"usageState\": \"unreserved\",\n " +
                "\"serialNumber\": \"987654df\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Device updatedDevice = objectMapper.readValue(updateMsDeviceInstance, Device.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        // To update the multiShelf deviceInstance on building
        deviceInstanceRestApis.updateDeviceOnBuilding(existingOrder.getId(), "mockBuilding_3", updatedDevice);
    }

    /**
     * Test method to create a card on the firstShelf and verify its attributes.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(14)
    public void createCardOnShelf1() throws JsonProcessingException {
        String createCard1 = "{\n " +
                " \"name\": \"mockCard_1\",\n " +
                "\"shelfPosition\": 1,\n " +
                "\"slotPosition\": 1,\n " +
                "\"vendor\": \"Mock\",\n " +
                "\"cardModel\": \"mockCard_1\",\n " +
                "\"cardPartNumber\": \"1346789\",\n " +
                "\"operationalState\": \"Active\",\n " +
                "\"administrativeState\": \"Active\",\n " +
                "\"usageState\": \"unreserved\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Card card = objectMapper.readValue(createCard1, Card.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        // To create a card
        deviceInstanceRestApis.createCard(existingOrder.getId(), "multiShelf_dev_3", card);
    }

    /**
     * Test method to create a card on the same slot position where a card already exists
     * And raises an exception.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(15)
    public void createCardOnSameSlot() throws JsonProcessingException {
        String createCardException = "{\n " +
                " \"name\": \"mockCard_5\",\n " +
                "\"shelfPosition\": 1,\n " +
                "\"slotPosition\": 1,\n " +
                "\"vendor\": \"Mock\",\n " +
                "\"cardModel\": \"mockCard_2\",\n " +
                "\"cardPartNumber\": \"1346789\",\n " +
                "\"operationalState\": \"Active\",\n " +
                "\"administrativeState\": \"Active\",\n " +
                "\"usageState\": \"unreserved\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Card card = objectMapper.readValue(createCardException, Card.class);

        //To get the details of the card on a slot by slotName
        Card cardOnSlot = cardRepository.getCardOnASlot("multishelf_dev_3/1/1");

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //Assert the exception
        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createCard(existingOrder.getId(), "multiShelf_dev_3", card);
        });

        // Assert the expected response
        assertTrue(e.getMessage().contains("A card already exist on the given shelf, slot position. " +
                "Existing card details: " + cardOnSlot));
    }

    /**
     * Test method to create a non-compatible card on a shelf
     * And raises an exception.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(16)
    public void createNonCompatibleCard() throws JsonProcessingException {
        String createCardException = "{\n " +
                " \"name\": \"mockCard_4\",\n " +
                "\"shelfPosition\": 2,\n " +
                "\"slotPosition\": 1,\n " +
                "\"vendor\": \"Mock\",\n " +
                "\"cardModel\": \"mockCard_4\",\n " +
                "\"cardPartNumber\": \"1346789\",\n " +
                "\"operationalState\": \"Active\",\n " +
                "\"administrativeState\": \"Active\",\n " +
                "\"usageState\": \"unreserved\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Card card = objectMapper.readValue(createCardException, Card.class);

        //To get the deviceModel details by using deviceModel attribute
        DeviceMetaModel deviceModel = deviceMetaModelRepository.getDeviceMetaModel("mockdevicemodel_3");

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //Assert the exception
        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createCard(existingOrder.getId(), "multiShelf_dev_3", card);
        });

        // Assert the expected response
        assertTrue(e.getMessage().contains("Given card model: " + card.getCardModel() +
                " is not compatible with the device model, AllowedCardList on the device: " +
                deviceModel.getAllowedCardList()));
    }

    /**
     * Test method to create a card on the secondShelf and verify its attributes.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(17)
    public void createCardOnShelf2() throws JsonProcessingException {
        String createCard1 = "{\n " +
                " \"name\": \"mockCard_2\",\n " +
                "\"shelfPosition\": 2,\n " +
                "\"slotPosition\": 1,\n " +
                "\"vendor\": \"Mock\",\n " +
                "\"cardModel\": \"mockCard_2\",\n " +
                "\"cardPartNumber\": \"1346789\",\n " +
                "\"operationalState\": \"Active\",\n " +
                "\"administrativeState\": \"Active\",\n " +
                "\"usageState\": \"unreserved\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Card card = objectMapper.readValue(createCard1, Card.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        // To create a card
        deviceInstanceRestApis.createCard(existingOrder.getId(), "multiShelf_dev_3", card);
    }

    /**
     * Test method to create a port on a card and verify its attributes.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(18)
    public void createPort() throws JsonProcessingException {
        String createPort = "{\n " +
                "\"name\": \"mockPort_5\",\n " +
                "\"positionOnCard\": 2,\n " +
                "\"portType\": \"rj45\",\n " +
                "\"operationalState\": \"Active\",\n " +
                "\"administrativeState\": \"Active\",\n " +
                "\"usageState\": \"unreserved\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Port port = objectMapper.readValue(createPort, Port.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        // To create the port on a specific card
        deviceInstanceRestApis.createPort(existingOrder.getId(), "mockCard_1", port);
    }

    /**
     * Test method to create a port with the same position on a card where a port already exists.
     * And raises an exception.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(19)
    public void createPortOnSamePosition() throws JsonProcessingException {
        String createPortException = "{\n " +
                "\"name\": \"mockPort_6\",\n " +
                "\"positionOnCard\": 2,\n " +
                "\"portType\": \"rj45\",\n " +
                "\"operationalState\": \"Active\",\n " +
                "\"administrativeState\": \"Active\",\n " +
                "\"usageState\": \"unreserved\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Port port = objectMapper.readValue(createPortException, Port.class);

        //To get the existing port details in a particular card by port name
        Port existingPortDetails = portRepository.getPort("mockport_5");

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //Assert the exception
        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createPort(existingOrder.getId(), "mockCard_1", port);
        });

        // Assert the expected response
        assertTrue(e.getMessage().contains("A port already exist on the card with the given port number, " +
                "existingPortDetails: " + existingPortDetails));
    }

    /**
     * Test method to create a pluggable component on a card and verify its attributes.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(20)
    public void createPluggable() throws JsonProcessingException {
        String createPluggable1 = "{\n " +
                "\"name\": \"mockPluggable_5\",\n " +
                "\"positionOnCard\": 9,\n " +
                "\"vendor\": \"Mock\",\n " +
                "\"pluggableModel\": \"mockPluggable_1\",\n " +
                "\"pluggablePartNumber\": \"SFP65498\",\n " +
                "\"operationalState\": \"Active\",\n " +
                "\"administrativeState\": \"Active\",\n " +
                "\"usageState\": \"unreserved\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Pluggable pluggable = objectMapper.readValue(createPluggable1, Pluggable.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        // Create the pluggable on a specified card
        deviceInstanceRestApis.createPluggable(existingOrder.getId(), "multiShelf_dev_3", "mockCard_1", pluggable);
    }

    /**
     * Test method to create a pluggable with the same position on a card where a pluggable already exists.
     * And raises an exception.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(21)
    public void createPluggableOnSamePosition() throws JsonProcessingException {
        String createPluggableException = "{\n " +
                "\"name\": \"mockPluggable_6\",\n " +
                "\"positionOnCard\": 9,\n " +
                "\"vendor\": \"Mock\",\n " +
                "\"pluggableModel\": \"mockPluggable_2\",\n " +
                "\"pluggablePartNumber\": \"SFP65498\",\n " +
                "\"operationalState\": \"Active\",\n " +
                "\"administrativeState\": \"Active\",\n " +
                "\"usageState\": \"unreserved\"\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Pluggable pluggable = objectMapper.readValue(createPluggableException, Pluggable.class);

        //To get the pluggable details in a particular card slot by using cardSlot name and positionOnTheCard attributes
        Pluggable pluggablesOnCardSlot = pluggableRepository.getPluggableOnCardByNumber("mockcard_1/9", 9);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //Assert the exception
        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createPluggable(existingOrder.getId(), "multiShelf_dev_3", "mockCard_1", pluggable);
        });

        // Assert the expected response
        assertTrue(e.getMessage().contains("A pluggable already exist on the card with the given position " +
                "number, existingPluggableDetails: " + pluggablesOnCardSlot));
    }

    /**
     * Test method to retrieve the overall device's details using a TMF wrapper.
     *
     * @throws JsonProcessingException if there is an issue with JSON processing.
     */
    @Test
    @Order(22)
    public void getDeviceTMFwrapper() throws JsonProcessingException {
        String expectedJson = "{\n" +
                "\"name\":\"multishelf_dev_3\",\n" +
                "\"@type\":\"device\",\n" +
                "\"isBundle\":false,\n" +
                "\"place\":\n" +
                "[{\"name\":\"mockbuilding_3\",\n" +
                "\"clliCode\":\"mockBuidling3\",\n" +
                "\"phoneNumber\":\"9876543210\",\n" +
                "\"contactPerson\":\"mock12345\",\n" +
                "\"address\":\"mockadress12345,Chennai,TamilNadu\",\n" +
                "\"latitude\":\"12.6789\",\n" +
                "\"longitude\":\"85.4567\",\n" +
                "\"drivingInstructions\":\"road,metro\",\n" +
                "\"href\":\"https://10.169.60.35:8080/getBuildingById?id=1\",\n" +
                "\"notes\":null,\n" +
                "\"additionalAttributes\":[]}],\n" +
                "\"resourceCharacteristics\":\n" +
                "[{\"name\":\"serialNumber\",\n" +
                "\"value\":\"987654df\"},\n" +
                "{\"name\":\"managementIp\",\n" +
                "\"value\":\"\"},\n" +
                "{\"name\":\"rackPosition\",\n" +
                "\"value\":\"0\"},\n" +
                "{\"name\":\"deviceModel\",\n" +
                "\"value\":\"mockdevicemodel_3\"}],\n" +
                "\"operationalState\":\"Active\",\n" +
                "\"administrativeState\":\"Active\",\n" +
                "\"usageState\":\"unreserved\",\n" +
                "\"shelves\":\n" +
                "[{\"name\":\"multishelf_dev_3_shelf_1\",\n" +
                "\"shelfPosition\":1,\n" +
                "\"operationalState\":\"unassigned\",\n" +
                "\"administrativeState\":\"unassigned\",\n" +
                "\"href\":\"https://10.169.60.35:8080/getShelfById?id=4\",\n" +
                "\"usageState\":\"unassigned\",\n" +
                "\"slots\":\n" +
                "[{\"name\":\"multishelf_dev_3/1/1\",\n" +
                "\"slotPosition\":1,\n" +
                "\"operationalState\":\"Active\",\n" +
                "\"administrativeState\":\"Active\",\n" +
                "\"usageState\":\"unreserved\",\n" +
                "\"href\":\"https://10.169.60.35:8080/getSlotById?id=7\",\n" +
                "\"cards\":\n" +
                "[{\"name\":\"mockcard_1\",\n" +
                "\"shelfPosition\":1,\n" +
                "\"slotPosition\":1,\n" +
                "\"vendor\":\"Mock\",\n" +
                "\"cardModel\":\"mockCard_1\",\n" +
                "\"cardPartNumber\":\"1346789\",\n" +
                "\"operationalState\":\"Active\",\n" +
                "\"administrativeState\":\"Active\",\n" +
                "\"usageState\":\"unreserved\",\n" +
                "\"href\":\"https://10.169.60.35:8080/getCardById?id=8\",\n" +
                "\"cardSlots\":\n" +
                "[{\"name\":\"mockcard_1/2\",\n" +
                "\"slotPosition\":2,\n" +
                "\"operationalState\":\"Active\",\n" +
                "\"administrativeState\":\"Active\",\n" +
                "\"usageState\":\"unreserved\"," +
                "\"href\":\"https://10.169.60.35:8080/getCardSlotById?id=11\",\n" +
                "\"pluggables\":null,\n" +
                "\"ports\":\n" +
                "{\"name\":\"mockport_5\",\n" +
                "\"positionOnCard\":2,\n" +
                "\"positionOnDevice\":null,\n" +
                "\"portType\":\"rj45\",\n" +
                "\"operationalState\":\"Active\",\n" +
                "\"administrativeState\":\"Active\",\n" +
                "\"usageState\":\"unreserved\",\n" +
                "\"href\":\"https://10.169.60.35:8080/getPortById?id=12\",\n" +
                "\"logicalPorts\":[],\n" +
                "\"additionalAttributes\":[],\n" +
                "\"portSpeed\":null,\n" +
                "\"capacity\":null}},\n" +
                "{\"name\":\"mockcard_1/9\",\n" +
                "\"slotPosition\":9,\n" +
                "\"operationalState\":\"Active\",\n" +
                "\"administrativeState\":\"Active\",\n" +
                "\"usageState\":\"unreserved\",\n" +
                "\"href\":\"https://10.169.60.35:8080/getCardSlotById?id=13\",\n" +
                "\"pluggables\":\n" +
                "{\"name\":\"mockpluggable_5\",\n" +
                "\"positionOnCard\":9,\n" +
                "\"positionOnDevice\":null,\n" +
                "\"vendor\":\"Mock\",\n" +
                "\"pluggableModel\":\"mockPluggable_5\",\n" +
                "\"pluggablePartNumber\":\"SFP65498\",\n" +
                "\"operationalState\":\"Active\",\n" +
                "\"administrativeState\":\"Active\",\n" +
                "\"usageState\":\"unreserved\",\n" +
                "\"href\":\"https://10.169.60.35:8080/getPluggableById?id=14\",\n" +
                "\"logicalPorts\":[],\n" +
                "\"additionalAttributes\":[]},\n" +
                "\"ports\":null}],\n" +
                "\"additionalAttributes\":[]}]}],\n" +
                "\"additionalAttributes\":[]},\n" +
                "{\"name\":\"multishelf_dev_3_shelf_2\",\n" +
                "\"shelfPosition\":2,\n" +
                "\"operationalState\":\"unassigned\",\n" +
                "\"administrativeState\":\"unassigned\",\n" +
                "\"href\":\"https://10.169.60.35:8080/getShelfById?id=5\",\n" +
                "\"usageState\":\"unassigned\",\n" +
                "\"slots\":\n" +
                "[{\"name\":\"multishelf_dev_3/2/1\",\n" +
                "\"slotPosition\":1,\n" +
                "\"operationalState\":\"Active\",\n" +
                "\"administrativeState\":\"Active\",\n" +
                "\"usageState\":\"unreserved\",\n" +
                "\"href\":\"https://10.169.60.35:8080/getSlotById?id=9\",\n" +
                "\"cards\":\n" +
                "[{\"name\":\"mockcard_2\",\n" +
                "\"shelfPosition\":2,\n" +
                "\"slotPosition\":1,\n" +
                "\"vendor\":\"Mock\",\n" +
                "\"cardModel\":\"mockcard_2\",\n" +
                "\"cardPartNumber\":\"1346789\",\n" +
                "\"operationalState\":\"Active\",\n" +
                "\"administrativeState\":\"Active\"," +
                "\"usageState\":\"unreserved\",\n" +
                "\"href\":\"https://10.169.60.35:8080/getCardById?id=10\",\n" +
                "\"cardSlots\":[],\n" +
                "\"additionalAttributes\":[]}]}],\n" +
                "\"additionalAttributes\":[]},\n" +
                "{\"name\":\"multishelf_dev_3_shelf_3\",\n" +
                "\"shelfPosition\":3,\n" +
                "\"operationalState\":\"unassigned\",\n" +
                "\"administrativeState\":\"unassigned\",\n" +
                "\"href\":\"https://10.169.60.35:8080/getShelfById?id=6\",\n" +
                "\"usageState\":\"unassigned\",\n" +
                "\"slots\":[],\n" +
                "\"additionalAttributes\":[]}],\n" +
                "\"devicePorts\":[],\n" +
                "\"devicePluggables\":[],\n" +
                "\"resourceSpecification\":\n" +
                "{\"name\":\"mockdevicemodel_3\",\n" +
                "\"href\":\"https://10.169.60.35:8080/getModelById?id=2\",\n" +
                "\"@referredType\":\"ResourceSpecificationRef\"},\n" +
                "\"relatedParty\":null\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        DeviceDetailsResponse expectedDevice = objectMapper.readValue(expectedJson, DeviceDetailsResponse.class);

        //To retrieve the overall detail about the device.
        DeviceDetailsResponse actualDevice = deviceInstanceApiWrappers.getDeviceDetailsTmf("multishelf_dev_3");

        // Compare the retrieved attributes with the expected attributes
        Assertions.assertEquals(expectedDevice.getName(), actualDevice.getName());
        Assertions.assertEquals(expectedDevice.getPlace().stream().map(Building::getName).collect(Collectors.toList()), actualDevice.getPlace().stream().map(Building::getName).collect(Collectors.toList()));
        Assertions.assertEquals(expectedDevice.getResourceSpecification().getName(), actualDevice.getResourceSpecification().getName());
        assertShelves(expectedDevice.getShelves(), actualDevice.getShelves());
    }

    /**
     * Method to compare lists of shelves and their contents.
     *
     * @param expectedShelves The expected list of shelves.
     * @param actualShelves   The actual list of shelves.
     */
    private void assertShelves(List<Shelf> expectedShelves, List<Shelf> actualShelves) {
        if (expectedShelves == null || actualShelves == null) {
            Assertions.fail("One of the shelves list is null!");
            return;
        }

        // Compare the retrieved shelfSize with the expected shelfSize
        Assertions.assertEquals(expectedShelves.size(), actualShelves.size(), "Mismatch in number of Shelves");

        for (int i = 0; i < expectedShelves.size(); i++) {
            for (int n = 0; n < actualShelves.size(); n++) {

                Shelf expectedShelf = expectedShelves.get(i);
                Shelf actualShelf = actualShelves.get(n);

                if (!(expectedShelf.getName().equals(actualShelf.getName()))) {
                    continue;
                } else {
                    // Compare the retrieved shelves with the expected shelves
                    Assertions.assertEquals(expectedShelf.getName(), actualShelf.getName(), "Mismatch in Shelf Name at index " + i);
                }

                // Comparing slots for each shelf
                List<Slot> expectedSlots = expectedShelf.getSlots();
                List<Slot> actualSlots = actualShelf.getSlots();

                if (expectedSlots == null || actualSlots == null) {
                    Assertions.fail("One of the slot lists is null for Shelf " + expectedShelf.getName());
                    continue; // Skip further checks for this shelf iteration
                }

                // Compare the retrieved slotSize with the expected slotSize
                Assertions.assertEquals(expectedSlots.size(), actualSlots.size(), "Mismatch in number of Slots for Shelf " + expectedShelf.getName());

                for (int j = 0; j < expectedSlots.size(); j++) {
                    for (int o = 0; o < actualSlots.size(); o++) {

                        Slot expectedSlot = expectedSlots.get(j);
                        Slot actualSlot = actualSlots.get(o);

                        if (!(expectedSlot.getName().equals(actualSlot.getName()))) {
                            continue;
                        } else {
                            // Compare the retrieved slot with the expected slot
                            Assertions.assertEquals(expectedSlot.getName(), actualSlot.getName(), "Mismatch in Slot Name at index " + j + " for Shelf " + expectedShelf.getName());
                        }

                        // Retrieve the cards for the current slot
                        List<Card> expectedCards = expectedSlot.getCards();
                        List<Card> actualCards = actualSlot.getCards();

                        if (expectedCards == null || actualCards == null) {
                            Assertions.fail("One of the card lists is null for Slot " + expectedSlot.getName());
                            continue; // Skip further checks for this slot iteration
                        }

                        Assertions.assertEquals(expectedCards.size(), actualCards.size(), "Mismatch in number of Cards for Slot " + expectedSlot.getName());

                        for (int k = 0; k < expectedCards.size(); k++) {
                            for (int p = 0; p < actualCards.size(); p++) {

                                Card expectedCard = expectedCards.get(k);
                                Card actualCard = actualCards.get(p);

                                if (!(expectedCard.getName().equals(actualCard.getName()))) {
                                    continue;
                                } else {
                                    // Compare the retrieved card with the expected card
                                    Assertions.assertEquals(expectedCard.getName(), actualCard.getName(), "Mismatch in Card Name at index " + k + " for Slot " + expectedSlot.getName());
                                }

                                // Comparing cardSlots for each card
                                List<CardSlot> expectedCardSlots = expectedCard.getCardSlots();
                                List<CardSlot> actualCardSlots = actualCard.getCardSlots();

                                if (expectedCardSlots == null || actualCardSlots == null) {
                                    Assertions.fail("One of the CardSlot lists is null for Card " + expectedCard.getName());
                                    continue; // Skip further checks for this card iteration
                                }

                                // Compare the retrieved cardSlotSize with the expected cardSlotSize
                                Assertions.assertEquals(expectedCardSlots.size(), actualCardSlots.size(), "Mismatch in number of CardSlot");

                                for (int l = 0; l < expectedCardSlots.size(); l++) {
                                    for (int q = 0; q < actualCardSlots.size(); q++) {

                                        CardSlot expectedCardSlot = expectedCardSlots.get(l);
                                        CardSlot actualCardSlot = actualCardSlots.get(q);

                                        if (!(expectedCardSlot.getName().equals(actualCardSlot.getName()))) {
                                            continue;
                                        } else {
                                            Assertions.assertEquals(expectedCardSlot.getName(), actualCardSlot.getName(), "Mismatch in CardSlot Name at index " + i + ", slot " + j + ", card " + k + ", cardSlot " + l);
                                        }

                                        // Comparing pluggable on the card
                                        Pluggable expectedPluggable = expectedCardSlot.getPluggables();
                                        Pluggable actualPluggable = actualCardSlot.getPluggables();

                                        if (expectedPluggable == null && actualPluggable == null) {
                                            continue;
                                        } else if (expectedPluggable == null) {
                                            Assertions.fail("Expected pluggable is null for CardSlot " + expectedCardSlot.getName());
                                            continue;
                                        } else if (actualPluggable == null) {
                                            Assertions.fail("Actual pluggable is null for CardSlot " + expectedCardSlot.getName());
                                            continue;
                                        }
                                        // Compare the retrieved pluggable with the expected pluggable
                                        Assertions.assertEquals(expectedPluggable.getName(), actualPluggable.getName());
                                        Assertions.assertEquals(expectedPluggable.getPluggablePartNumber(), actualPluggable.getPluggablePartNumber());
                                    }

                                    for (int m = 0; m < expectedCardSlots.size(); m++) {
                                        for (int t = 0; t < actualCardSlots.size(); t++) {

                                            CardSlot expectedCardSlot1 = expectedCardSlots.get(m);
                                            CardSlot actualCardSlot1 = actualCardSlots.get(t);

                                            if (!(expectedCardSlot1.getName().equals(actualCardSlot1.getName()))) {
                                                continue;
                                            } else {
                                                Assertions.assertEquals(expectedCardSlot1.getName(), actualCardSlot1.getName(), "Mismatch in CardSlot Name at index " + i + ", slot " + j + ", card " + k + ", cardSlot " + l);
                                            }

                                            Assertions.assertEquals(expectedCardSlot1.getName(), actualCardSlot1.getName(), "Mismatch in CardSlot Name at index " + i + ", slot " + j + ", card " + k + ", cardSlot " + m);

                                            // Comparing port on the card
                                            Port expectedPorts = expectedCardSlot1.getPorts();
                                            Port actualPorts = actualCardSlot1.getPorts();

                                            if (expectedPorts == null && actualPorts == null) {
                                                continue;
                                            } else if (expectedPorts == null) {
                                                Assertions.fail("Expected port is null for CardSlot " + expectedCardSlot1.getName());
                                                continue;
                                            } else if (actualPorts == null) {
                                                Assertions.fail("Actual port is null for CardSlot " + actualCardSlot1.getName());
                                                continue;
                                            }
                                            // Compare the retrieved port with the expected port
                                            Assertions.assertEquals(expectedPorts.getName(), actualPorts.getName());
                                            Assertions.assertEquals(expectedPorts.getPortType(), actualPorts.getPortType());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

//    /**
//     * Test method to update a device attributes using a predefined TMF wrapper.
//     *
//     * @throws JsonProcessingException If there's an issue processing JSON.
//     */
//    @Test
//    @Order(23)
//    public void updateDeviceTMFWrapper() throws JsonProcessingException {
//        String updateDeviceTMFWrapper = "{\n" +
//                "\"name\":\"multishelf_dev_3\",\n" +
//                "\"@type\":\"device\",\n" +
//                "\"isBundle\":false,\n" +
//                "\"place\":\n" +
//                "[{\"name\":\"mockbuilding_3\",\n" +
//                "\"clliCode\":\"mockBuidling3\",\n" +
//                "\"phoneNumber\":\"9876543210\",\n" +
//                "\"contactPerson\":\"mock12345\",\n" +
//                "\"address\":\"mockadress12345,Chennai,TamilNadu\",\n" +
//                "\"latitude\":\"12.6789\",\n" +
//                "\"longitude\":\"85.4567\",\n" +
//                "\"drivingInstructions\":\"road,metro\",\n" +
//                "\"href\":\"https://10.169.60.35:8080/getBuildingById?id=1\",\n" +
//                "\"notes\":null,\n" +
//                "\"additionalAttributes\":[]}],\n" +
//                "\"resourceCharacteristics\":\n" +
//                "[{\"name\":\"serialNumber\",\n" +
//                "\"value\":\"987654df\"},\n" +
//                "{\"name\":\"managementIp\",\n" +
//                "\"value\":\"\"},\n" +
//                "{\"name\":\"rackPosition\",\n" +
//                "\"value\":\"0\"},\n" +
//                "{\"name\":\"deviceModel\",\n" +
//                "\"value\":\"mockdevicemodel_3\"}],\n" +
//                "\"operationalState\":\"Active\",\n" +
//                "\"administrativeState\":\"Active\",\n" +
//                "\"usageState\":\"reserved\",\n" +
//                "\"shelves\":\n" +
//                "[{\"name\":\"multishelf_dev_3_shelf_1\",\n" +
//                "\"shelfPosition\":1,\n" +
//                "\"operationalState\":\"unassigned\",\n" +
//                "\"administrativeState\":\"unassigned\",\n" +
//                "\"href\":\"https://10.169.60.35:8080/getShelfById?id=4\",\n" +
//                "\"usageState\":\"assigned\",\n" +
//                "\"slots\":\n" +
//                "[{\"name\":\"multishelf_dev_3/1/1\",\n" +
//                "\"slotPosition\":1,\n" +
//                "\"operationalState\":\"Active\",\n" +
//                "\"administrativeState\":\"Active\",\n" +
//                "\"usageState\":\"reserved\",\n" +
//                "\"href\":\"https://10.169.60.35:8080/getSlotById?id=7\",\n" +
//                "\"cards\":\n" +
//                "[{\"name\":\"mockcard_1\",\n" +
//                "\"shelfPosition\":1,\n" +
//                "\"slotPosition\":1,\n" +
//                "\"vendor\":\"Mock\",\n" +
//                "\"cardModel\":\"mockCard_1\",\n" +
//                "\"cardPartNumber\":\"12345678\",\n" +
//                "\"operationalState\":\"Active\",\n" +
//                "\"administrativeState\":\"Active\",\n" +
//                "\"usageState\":\"unreserved\",\n" +
//                "\"href\":\"https://10.169.60.35:8080/getCardById?id=8\",\n" +
//                "\"cardSlots\":\n" +
//                "[{\"name\":\"mockcard_1/2\",\n" +
//                "\"slotPosition\":2,\n" +
//                "\"operationalState\":\"Active\",\n" +
//                "\"administrativeState\":\"Active\",\n" +
//                "\"usageState\":\"reserved\",\n" +
//                "\"href\":\"https://10.169.60.35:8080/getCardSlotById?id=11\",\n" +
//                "\"pluggables\":null,\n" +
//                "\"ports\":\n" +
//                "{\"name\":\"mockport_5\",\n" +
//                "\"positionOnCard\":2,\n" +
//                "\"positionOnDevice\":null,\n" +
//                "\"portType\":\"rj45\",\n" +
//                "\"operationalState\":\"Active\",\n" +
//                "\"administrativeState\":\"Active\",\n" +
//                "\"usageState\":\"reserved\",\n" +
//                "\"href\":\"https://10.169.60.35:8080/getPortById?id=12\",\n" +
//                "\"logicalPorts\":[],\n" +
//                "\"additionalAttributes\":[],\n" +
//                "\"portSpeed\":null,\n" +
//                "\"capacity\":null}},\n" +
//                "{\"name\":\"mockcard_1/9\",\n" +
//                "\"slotPosition\":9,\n" +
//                "\"operationalState\":\"Active\",\n" +
//                "\"administrativeState\":\"Active\",\n" +
//                "\"usageState\":\"reserved\",\n" +
//                "\"href\":\"https://10.169.60.35:8080/getCardSlotById?id=13\",\n" +
//                "\"pluggables\":\n" +
//                "{\"name\":\"mockpluggable_5\",\n" +
//                "\"positionOnCard\":9,\n" +
//                "\"positionOnDevice\":null,\n" +
//                "\"vendor\":\"Mock\",\n" +
//                "\"pluggableModel\":\"mockPluggable_5\",\n" +
//                "\"pluggablePartNumber\":\"SFP65498\",\n" +
//                "\"operationalState\":\"Active\",\n" +
//                "\"administrativeState\":\"Active\",\n" +
//                "\"usageState\":\"reserved\",\n" +
//                "\"href\":\"https://10.169.60.35:8080/getPluggableById?id=14\",\n" +
//                "\"logicalPorts\":[],\n" +
//                "\"additionalAttributes\":[]},\n" +
//                "\"ports\":null}],\n" +
//                "\"additionalAttributes\":[]}]}],\n" +
//                "\"additionalAttributes\":[]},\n" +
//                "{\"name\":\"multishelf_dev_3_shelf_2\",\n" +
//                "\"shelfPosition\":2,\n" +
//                "\"operationalState\":\"unassigned\",\n" +
//                "\"administrativeState\":\"unassigned\",\n" +
//                "\"href\":\"https://10.169.60.35:8080/getShelfById?id=5\",\n" +
//                "\"usageState\":\"assigned\",\n" +
//                "\"slots\":\n" +
//                "[{\"name\":\"multishelf_dev_3/2/1\",\n" +
//                "\"slotPosition\":1,\n" +
//                "\"operationalState\":\"Active\",\n" +
//                "\"administrativeState\":\"Active\",\n" +
//                "\"usageState\":\"reserved\",\n" +
//                "\"href\":\"https://10.169.60.35:8080/getSlotById?id=9\",\n" +
//                "\"cards\":\n" +
//                "[{\"name\":\"mockcard_2\",\n" +
//                "\"shelfPosition\":2,\n" +
//                "\"slotPosition\":1,\n" +
//                "\"vendor\":\"Mock\",\n" +
//                "\"cardModel\":\"mockcard_2\",\n" +
//                "\"cardPartNumber\":\"12345678\",\n" +
//                "\"operationalState\":\"Active\",\n" +
//                "\"administrativeState\":\"Active\",\n" +
//                "\"usageState\":\"reserved\",\n" +
//                "\"href\":\"https://10.169.60.35:8080/getCardById?id=10\",\n" +
//                "\"cardSlots\":[],\n" +
//                "\"additionalAttributes\":[]}]}],\n" +
//                "\"additionalAttributes\":[]},\n" +
//                "{\"name\":\"multishelf_dev_3_shelf_3\",\n" +
//                "\"shelfPosition\":3,\n" +
//                "\"operationalState\":\"unassigned\",\n" +
//                "\"administrativeState\":\"unassigned\",\n" +
//                "\"href\":\"https://10.169.60.35:8080/getShelfById?id=6\",\n" +
//                "\"usageState\":\"assigned\",\n" +
//                "\"slots\":[],\n" +
//                "\"additionalAttributes\":[]}],\n" +
//                "\"devicePorts\":[],\n" +
//                "\"devicePluggables\":[],\n" +
//                "\"resourceSpecification\":\n" +
//                "{\"name\":\"mockdevicemodel_3\",\n" +
//                "\"href\":\"https://10.169.60.35:8080/getModelById?id=2\",\n" +
//                "\"@referredType\":\"ResourceSpecificationRef\"},\n" +
//                "\"relatedParty\":null\n" +
//                "}";
//
//        // Deserialize the expected JSON into object
//        ObjectMapper objectMapper = new ObjectMapper();
//        DeviceDetailsResponse updateDeviceDetails = objectMapper.readValue(updateDeviceTMFWrapper, DeviceDetailsResponse.class);
//
//        // To update the device using the provided details
//        deviceInstanceApiWrappers.createDeviceTmf(updateDeviceDetails);
//    }
}
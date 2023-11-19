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

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This class creates a device with respect to the Rack with multiple shelves.
 */

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DeviceIntegrationTest4 {

    @Autowired
    LocationManagerRestApis locationManagerRestApis;

    @Autowired
    DeviceInstanceRestApis deviceInstanceRestApis;

    @Autowired
    ModellingRestApis modellingRestApis;

    @Autowired
    DeviceInstanceApiWrappers deviceInstanceApiWrappers;

    @Autowired
    PortRepository portRepository;

    @Autowired
    PluggableRepository pluggableRepository;

    @Autowired
    DeviceMetaModelRepository deviceMetaModelRepository;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    OrderRepository orderRepository;

    /**
     * Test method to create a new country.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(1)
    public void createCountry() throws JsonProcessingException {
        String createCountry = "{\n" +
                "\"name\": \"mockCountry_4\",\n" +
                "\"notes\": \"mockCountry_4 is my country\"\n" +
                "}";

        // Deserialize the country JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Country country = objectMapper.readValue(createCountry, Country.class);

        // Call the method to create the country
        locationManagerRestApis.createCountry(country);
    }

    /**
     * Test method to retrieve a country and verify.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(2)
    public void getCountry() throws JsonProcessingException {
        String expectedJson = "{\n" +
                "\"name\": \"mockCountry_4\",\n" +
                "\"notes\": \"mockCountry_4 is my country\"\n" +
                "}";

        // Deserialize the country JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Country expectedCountry = objectMapper.readValue(expectedJson, Country.class);

        // Retrieve the actual Country object from the API
        Country actualCountry = locationManagerRestApis.getCountry("mockCountry_4");

        // Assertions to compare expected and actual values
        Assertions.assertEquals(expectedCountry.getName().toLowerCase(), actualCountry.getName());
        Assertions.assertEquals(expectedCountry.getNotes(), actualCountry.getNotes());
    }

    /**
     * Test method to create a new state.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(3)
    void createState() throws JsonProcessingException {
        String createState = "{\n" +
                "\"name\": \"mockState_4\",\n" +
                "\"notes\": \"mockState_4 is a state\"\n" +
                "}";

        // Deserialize the country JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        State state = objectMapper.readValue(createState, State.class);

        // Call the method to create the state
        locationManagerRestApis.createState("mockCountry_4", state);
    }

    /**
     * Test method to retrieve a state and verify.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(4)
    public void getState() throws JsonProcessingException {
        String expectedJson = "{\n" +
                "\"name\": \"mockState_4\",\n" +
                "\"notes\": \"mockState_4 is a state\"\n" +
                "}";

        // Deserialize the country JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        State expectedState = objectMapper.readValue(expectedJson, State.class);

        // Retrieve the actual State object from the API
        State actualState = locationManagerRestApis.getState("mockState_4");

        // Assertions to compare expected and actual values
        Assertions.assertEquals(expectedState.getName().toLowerCase(), actualState.getName());
        Assertions.assertEquals(expectedState.getNotes(), actualState.getNotes());
    }

    /**
     * Test method to create a new city.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(5)
    public void createCity() throws JsonProcessingException {
        String createCity = "{\n" +
                "\"name\":\"mockCity_4\",\n" +
                "\"notes\":\"mockCity_4 is a city\"\n" +
                "}";

        // Deserialize the country JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        City city = objectMapper.readValue(createCity, City.class);

        // Call the method to create the city
        locationManagerRestApis.createCity("mockState_4", city);
    }

    /**
     * Test method to retrieve a city and verify.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(6)
    public void getCity() throws JsonProcessingException {
        String expectedJson = "{\n" +
                "\"name\":\"mockCity_4\",\n" +
                "\"notes\":\"mockCity_4 is a city\"\n" +
                "}";

        // Deserialize the country JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        City expectedCity = objectMapper.readValue(expectedJson, City.class);

        // Retrieve the actual City object from the API
        City actualCity = locationManagerRestApis.getCity("mockCity_4");

        // Assertions to compare expected and actual values
        Assertions.assertEquals(expectedCity.getName().toLowerCase(), actualCity.getName());
        Assertions.assertEquals(expectedCity.getNotes(), actualCity.getNotes());
    }

    /**
     * Test method to create a new building.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(7)
    void createBuilding() throws JsonProcessingException {
        String createBuilding = "{\n" +
                " \"name\": \"mockBuilding_4\",\n" +
                " \"clliCode\": \"mockBuilding1234\",\n" +
                " \"phoneNumber\": \"9876543210\",\n" +
                " \"contactPerson\": \"mock4\",\n" +
                " \"address\": \"mockAddress4, Chennai, Tamil Nadu\",\n" +
                " \"latitude\": \"12.6789\",\n" +
                " \"longitude\": \"85.4567\",\n" +
                " \"drivingInstructions\": \"road, metro\"\n" +
                "}";

        // Deserialize the country JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Building building = objectMapper.readValue(createBuilding, Building.class);

        // Call the method to create the building
        locationManagerRestApis.createBuilding("mockCity_4", building);
    }

    /**
     * Test method to retrieve a building and verify.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(8)
    public void getBuilding() throws JsonProcessingException {
        String expectedJson = "{\n" +
                " \"name\": \"mockBuilding_4\",\n" +
                " \"clliCode\": \"mockBuilding1234\",\n" +
                " \"phoneNumber\": \"9876543210\",\n" +
                " \"contactPerson\": \"mock4\",\n" +
                " \"address\": \"mockAddress4, Chennai, Tamil Nadu\",\n" +
                " \"latitude\": \"12.6789\",\n" +
                " \"longitude\": \"85.4567\",\n" +
                " \"drivingInstructions\": \"road, metro\"\n" +
                "}";

        // Deserialize the country JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Building expectedBuilding = objectMapper.readValue(expectedJson, Building.class);

        // Retrieve the actual Building object from the API
        Building actualBuilding = locationManagerRestApis.getBuilding("mockBuilding_4");

        // Assertions to compare expected and actual values
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
     * Test method to update a building.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(9)
    public void updateBuilding() throws JsonProcessingException {
        String updateBuilding = "{\n" +
                " \"name\": \"mockBuilding_4\",\n" +
                " \"clliCode\": \"mockBuidling4\",\n" +
                " \"phoneNumber\": \"9876543210\",\n" +
                " \"contactPerson\": \"mock12345\",\n" +
                " \"address\": \"mockadress12345, Chennai, Tamil Nadu\",\n" +
                " \"latitude\": \"12.6789\",\n" +
                " \"longitude\": \"85.4567\",\n" +
                " \"drivingInstructions\": \"road, metro\"\n" +
                "}";

        // Deserialize the country JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Building updatedBuilding = objectMapper.readValue(updateBuilding, Building.class);

        // Call the method to update the building
        locationManagerRestApis.updateBuilding("mockCity_4", updatedBuilding);
    }

    /**
     * Test method to retrieve an updated building and verify.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(10)
    public void getUpdatedBuilding() throws JsonProcessingException {
        String expectedJson = "{\n" +
                " \"name\": \"mockBuilding_4\",\n" +
                " \"clliCode\": \"mockBuidling4\",\n" +
                " \"phoneNumber\": \"9876543210\",\n" +
                " \"contactPerson\": \"mock12345\",\n" +
                " \"address\": \"mockadress12345, Chennai, Tamil Nadu\",\n" +
                " \"latitude\": \"12.6789\",\n" +
                " \"longitude\": \"85.4567\",\n" +
                " \"drivingInstructions\": \"road, metro\"\n" +
                "}";

        // Deserialize the country JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Building expectedBuilding = objectMapper.readValue(expectedJson, Building.class);

        // Retrieve the actual Building object from the API
        Building actualBuilding = locationManagerRestApis.getBuilding("mockBuilding_4");

        // Assertions to compare expected and actual values
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
     * Test method to create a new Rack.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(11)
    public void createRack() throws JsonProcessingException {
        String createRack = "{\n" +
                "\"name\": \"MockRack_4\",\n" +
                "\"location\": \"mockbuilding_4\",\n" +
                "\"height\": 19.08,\n" +
                "\"width\": 1.73,\n" +
                "\"depth\": 11.23,\n" +
                "\"numOfPositionsContained\": 5,\n" +
                "\"reservedPositions\": [],\n" +
                "\"freePositions\": [1,2,3,4,5]\n" +
                "}";

        // Deserialize the country JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Rack rack = objectMapper.readValue(createRack, Rack.class);

        // Call the method to create the rack
        locationManagerRestApis.createRack("mockbuilding_4", rack);
    }

    /**
     * Test method to retrieve a Rack and verify.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(12)
    public void getRack() throws JsonProcessingException {
        String expectedJson = "{\n" +
                "\"name\": \"MockRack_4\",\n" +
                "\"location\": \"mockbuilding_4\",\n" +
                "\"height\": 19.08,\n" +
                "\"width\": 1.73,\n" +
                "\"depth\": 11.23,\n" +
                "\"numOfPositionsContained\": 5,\n" +
                "\"reservedPositions\": [],\n" +
                "\"freePositions\": [1,2,3,4,5]\n" +
                "}";

        // Deserialize the country JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Rack expectedRack = objectMapper.readValue(expectedJson, Rack.class);

        // Retrieve the actual Rack object from the API
        Rack actualRack = locationManagerRestApis.getRack("MockRack_4");

        // Assertions to compare expected and actual values
        Assertions.assertEquals(expectedRack.getName().toLowerCase(), actualRack.getName().toLowerCase());
        Assertions.assertEquals(expectedRack.getLocation(), actualRack.getLocation());
        Assertions.assertEquals(expectedRack.getHeight(), actualRack.getHeight());
        Assertions.assertEquals(expectedRack.getWidth(), actualRack.getWidth());
        Assertions.assertEquals(expectedRack.getDepth(), actualRack.getDepth());
        Assertions.assertEquals(expectedRack.getNumOfPositionsContained(), actualRack.getNumOfPositionsContained());
        Assertions.assertEquals(expectedRack.getReservedPositions(), actualRack.getReservedPositions());
        Assertions.assertEquals(expectedRack.getFreePositions(), actualRack.getFreePositions());
    }

    /**
     * Test method to update a rack.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(13)
    public void updateRack() throws JsonProcessingException {
        String updateRack = "{\n" +
                "\"name\": \"MockRack_4\",\n" +
                "\"location\": \"mockbuilding_4\",\n" +
                "\"height\": 19.08,\n" +
                "\"width\": 1.73,\n" +
                "\"depth\": 11.23,\n" +
                "\"numOfPositionsContained\": 6,\n" +
                "\"reservedPositions\": [1,2],\n" +
                "\"freePositions\": [3,4,5,6]\n" +
                "}";

        // Deserialize the country JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Rack updatedRack = objectMapper.readValue(updateRack, Rack.class);

        // Call the method to update the rack
        locationManagerRestApis.updateRack("mockbuilding_4", updatedRack);
    }

    /**
     * Test method to retrieve an updated rack and verify.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(14)
    public void getUpdatedRack() throws JsonProcessingException {
        String expectedJson = "{\n" +
                "\"name\": \"MockRack_4\",\n" +
                "\"location\": \"mockbuilding_4\",\n" +
                "\"height\": 19.08,\n" +
                "\"width\": 1.73,\n" +
                "\"depth\": 11.23,\n" +
                "\"numOfPositionsContained\": 6,\n" +
                "\"reservedPositions\": [1,2],\n" +
                "\"freePositions\": [3,4,5,6]\n" +
                "}";

        // Deserialize the country JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Rack expectedUpdatedRack = objectMapper.readValue(expectedJson, Rack.class);

        // Retrieve the actual updated Rack object from the API
        Rack actualUpdatedRack = locationManagerRestApis.getRack("MockRack_4");

        // Assertions to compare expected and actual values
        Assertions.assertEquals(expectedUpdatedRack.getName().toLowerCase(), actualUpdatedRack.getName().toLowerCase());
        Assertions.assertEquals(expectedUpdatedRack.getLocation(), actualUpdatedRack.getLocation());
        Assertions.assertEquals(expectedUpdatedRack.getHeight(), actualUpdatedRack.getHeight());
        Assertions.assertEquals(expectedUpdatedRack.getWidth(), actualUpdatedRack.getWidth());
        Assertions.assertEquals(expectedUpdatedRack.getDepth(), actualUpdatedRack.getDepth());
        Assertions.assertEquals(expectedUpdatedRack.getNumOfPositionsContained(), actualUpdatedRack.getNumOfPositionsContained());
        Assertions.assertEquals(expectedUpdatedRack.getReservedPositions(), actualUpdatedRack.getReservedPositions());
        Assertions.assertEquals(expectedUpdatedRack.getFreePositions(), actualUpdatedRack.getFreePositions());
    }

    /**
     * Test method to create a new device metamodel.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(15)
    public void createMsDeviceMetaModel() throws JsonProcessingException {
        String createDeviceModel = "{\n" +
                " \"deviceModel\": \"mockDeviceModel_4\",\n" +
                " \"partNumber\": \"Mock_123\",\n" +
                " \"vendor\": \"Mocker\",\n" +
                " \"height\": 12,\n" +
                " \"depth\": 13,\n" +
                " \"width\": 12,\n" +
                " \"shelvesContained\": 3,\n" +
                " \"numOfRackPositionOccupied\": 1,\n" +
                " \"allowedCardList\": [\"mockCard_3\", \"mockCard_4\"]\n" +
                "}";

        // Deserialize the country JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        DeviceMetaModel deviceMetaModel = objectMapper.readValue(createDeviceModel, DeviceMetaModel.class);

        // Call the method to create the device metamodel
        modellingRestApis.createDeviceMetaModel(deviceMetaModel);
    }

    /**
     * Test method to create the device.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(16)
    public void createMsDeviceOnRack() throws JsonProcessingException {
        String createDevice = "{\n " +
                "   \"name\": \"multiShelf_dev_4\",\n " +
                "   \"deviceModel\": \"mockDeviceModel_4\",\n " +
                "   \"location\": \"Mock_location\",\n   " +
                " \"organisation\": \"Mock_org_4\",\n  " +
                "  \"customer\": \"Mr.Mock\",\n  " +
                "  \"managementIp\": \"\",\n " +
                "   \"rackPosition\": 5,\n  " +
                "  \"operationalState\": \"Active\",\n  " +
                "  \"administrativeState\": \"Active\",\n " +
                "   \"usageState\": \"unreserved\",\n   " +
                " \"serialNumber\": \"887654ff\"\n" +
                "}";

        // Deserialize the country JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Device device = objectMapper.readValue(createDevice, Device.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        // Call the method to create the device
        deviceInstanceRestApis.createDevice(existingOrder.getId(), "MockRack_4", device);
    }

    /**
     * Test method to update the device.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(17)
    public void updateMsDeviceAttributes() throws JsonProcessingException {
        String updateMsDeviceAttributes = "{\n " +
                "   \"name\": \"multiShelf_dev_4\",\n " +
                "   \"deviceModel\": \"mockDeviceModel_4\",\n " +
                "   \"location\": \"Mock_location_1\",\n   " +
                " \"organisation\": \"Mock_org_2\",\n  " +
                "  \"customer\": \"Mr.Mock_Person\",\n  " +
                "  \"managementIp\": \"\",\n " +
                "   \"rackPosition\": 6,\n  " +
                "  \"operationalState\": \"Active\",\n  " +
                "  \"administrativeState\": \"Active\",\n " +
                "   \"usageState\": \"unreserved\",\n   " +
                " \"serialNumber\": \"887654fg\"\n" +
                "}";

        // Deserialize the country JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Device device = objectMapper.readValue(updateMsDeviceAttributes, Device.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        // Call the method to update the device
        deviceInstanceRestApis.updateDevice(existingOrder.getId(), "MockRack_4", device);
    }

    /**
     * Test method to create the card with respect to device.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(18)
    public void createCardOnShelf1() throws JsonProcessingException {
        String createCard1 = "{\n " +
                " \"name\": \"mockCard_3\",\n " +
                "\"shelfPosition\": 1,\n " +
                "\"slotPosition\": 1,\n " +
                "\"vendor\": \"Sandisk\",\n " +
                "\"cardModel\": \"mockCard_3\",\n " +
                "\"cardPartNumber\": \"1346789\",\n " +
                "\"operationalState\": \"Active\",\n " +
                "\"administrativeState\": \"Active\",\n " +
                "\"usageState\": \"unreserved\"\n" +
                "}";

        // Deserialize the country JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Card card = objectMapper.readValue(createCard1, Card.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        // Call the method to create the card with respect to device
        deviceInstanceRestApis.createCard(existingOrder.getId(), "multiShelf_dev_4", card);
    }

    /**
     * Test method to create a card in same slot.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(19)
    public void createCardOnSameSlot() throws JsonProcessingException {
        String createCardException = "{\n " +
                " \"name\": \"mockCard_5\",\n " +
                "\"shelfPosition\": 1,\n " +
                "\"slotPosition\": 1,\n " +
                "\"vendor\": \"Sandisk\",\n " +
                "\"cardModel\": \"mockCard_4\",\n " +
                "\"cardPartNumber\": \"1346789\",\n " +
                "\"operationalState\": \"Active\",\n " +
                "\"administrativeState\": \"Active\",\n " +
                "\"usageState\": \"unreserved\"\n" +
                "}";

        // Deserialize the country JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Card card = objectMapper.readValue(createCardException, Card.class);

        //To get the details of card on a slot by using slotname
        Card cardOnSlot = cardRepository.getCardOnASlot("multishelf_dev_4/1/1");

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //Assert the exception
        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createCard(existingOrder.getId(), "multiShelf_dev_4", card);
        });

        // Assert the expected response
        assertTrue(e.getMessage().contains("A card already exist on the given shelf, slot position. " +
                "Existing card details: " + cardOnSlot));
    }

    /**
     * Test method to create a non-compatible card.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(20)
    public void createNonCompatibleCard() throws JsonProcessingException {
        String createCardException = "{\n " +
                " \"name\": \"mockCard_5\",\n " +
                "\"shelfPosition\": 2,\n " +
                "\"slotPosition\": 1,\n " +
                "\"vendor\": \"Sandisk\",\n " +
                "\"cardModel\": \"mockCard_5\",\n " +
                "\"cardPartNumber\": \"1346783\",\n " +
                "\"operationalState\": \"Active\",\n " +
                "\"administrativeState\": \"Active\",\n " +
                "\"usageState\": \"unreserved\"\n" +
                "}";

        // Deserialize the country JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Card card = objectMapper.readValue(createCardException, Card.class);

        //To get the device model details by using deviceModel attribute
        DeviceMetaModel deviceModel = deviceMetaModelRepository.getDeviceMetaModel("mockdevicemodel_4");

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        //Assert the exception
        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createCard(existingOrder.getId(), "multiShelf_dev_4", card);
        });

        // Assert the expected response
        assertTrue(e.getMessage().contains("Given card model: " + card.getCardModel() +
                " is not compatible with the device model, AllowedCardList on the device: " +
                deviceModel.getAllowedCardList()));
    }

    /**
     * Test method to create a new card on Shelf 2.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(21)
    public void createCardOnShelf2() throws JsonProcessingException {
        String createCard1 = "{\n " +
                "\"name\": \"mockCard_4\",\n " +
                "\"shelfPosition\": 2,\n " +
                "\"slotPosition\": 1,\n " +
                "\"vendor\": \"Sandisk\",\n " +
                "\"cardModel\": \"mockCard_4\",\n " +
                "\"cardPartNumber\": \"1346784\",\n " +
                "\"operationalState\": \"Active\",\n " +
                "\"administrativeState\": \"Active\",\n " +
                "\"usageState\": \"unreserved\"\n" +
                "}";

        // Deserialize the country JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Card card = objectMapper.readValue(createCard1, Card.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        // Create the card using the deviceInstanceRestApis
        deviceInstanceRestApis.createCard(existingOrder.getId(), "multiShelf_dev_4", card);
    }

    /**
     * Test method to create a new port.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(22)
    public void createPort() throws JsonProcessingException {
        String createPort = "{\n " +
                "\"name\": \"mockPort_7\",\n " +
                "\"positionOnCard\": 2,\n " +
                "\"portType\": \"rj45\",\n " +
                "\"operationalState\": \"Active\",\n " +
                "\"administrativeState\": \"Active\",\n " +
                "\"usageState\": \"unreserved\"\n" +
                "}";

        // Deserialize the country JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Port port = objectMapper.readValue(createPort, Port.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        // Create the port using the deviceInstanceRestApis
        deviceInstanceRestApis.createPort(existingOrder.getId(), "mockCard_4", port);
    }

    /**
     * Test method to create a new port on the same position as an existing one.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(23)
    public void createPortOnSamePosition() throws JsonProcessingException {
        String createPortException = "{\n " +
                "\"name\": \"mockPort_8\",\n " +
                "\"positionOnCard\": 2,\n " +
                "\"portType\": \"rj45\",\n " +
                "\"operationalState\": \"Active\",\n " +
                "\"administrativeState\": \"Active\",\n " +
                "\"usageState\": \"unreserved\"\n" +
                "}";

        // Deserialize the country JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Port port = objectMapper.readValue(createPortException, Port.class);

        //To get already existing port by using port name
        Port existingPortDetails = portRepository.getPort("mockport_7");

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        // Perform the test
        ServiceException portsOnCardSlotFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createPort(existingOrder.getId(), "mockCard_4", port);
        });

        // Assert the expected response
        assertTrue(portsOnCardSlotFoundException.getMessage().contains("A port already exist on the card with the given port number, " + "existingPortDetails: " + existingPortDetails));

    }

    /**
     * Test method to create a new pluggable.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(24)
    public void createPluggable() throws JsonProcessingException {
        String createPluggable1 = "{\n " +
                "\"name\": \"mockPluggable_7\",\n " +
                "\"positionOnCard\": 9,\n " +
                "\"vendor\": \"Nokia\",\n " +
                "\"pluggableModel\": \"mockPluggable_6\",\n " +
                "\"pluggablePartNumber\": \"SFP65499\",\n " +
                "\"operationalState\": \"Active\",\n " +
                "\"administrativeState\": \"Active\",\n " +
                "\"usageState\": \"unreserved\"\n" +
                "}";

        // Deserialize the country JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Pluggable pluggable = objectMapper.readValue(createPluggable1, Pluggable.class);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        // Create the pluggable using the deviceInstanceResetApis
        deviceInstanceRestApis.createPluggable(existingOrder.getId(), "multiShelf_dev_4", "mockCard_4", pluggable);
    }

    /**
     * Test method to create a new pluggable on the same position as an existing one.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(25)
    public void createPluggableOnSamePosition() throws JsonProcessingException {
        String createPluggableException = "{\n " +
                "\"name\": \"mockPluggable_8\",\n " +
                "\"positionOnCard\": 9,\n " +
                "\"vendor\": \"Nokia\",\n " +
                "\"pluggableModel\": \"mockPluggable_7\",\n " +
                "\"pluggablePartNumber\": \"SFP65499\",\n " +
                "\"operationalState\": \"Active\",\n " +
                "\"administrativeState\": \"Active\",\n " +
                "\"usageState\": \"unreserved\"\n" +
                "}";

        // Deserialize the country JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        Pluggable pluggable = objectMapper.readValue(createPluggableException, Pluggable.class);

        //Get existing pluggable on the card slot
        Pluggable pluggablesOnCardSlot = pluggableRepository.getPluggableOnCardByNumber("mockcard_4/9", 9);

        OrderEntity existingOrder = orderRepository.findLatestOrderId();

        // Perform the test
        ServiceException pluggablesOnCardSlotException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createPluggable(existingOrder.getId(), "multiShelf_dev_4", "mockCard_4", pluggable);
        });

        // Assert the expected response
        assertTrue(pluggablesOnCardSlotException.getMessage().contains("A pluggable already exist on the card with the given position " +
                "number, existingPluggableDetails: " + pluggablesOnCardSlot));
    }


    /**
     * Test method to retrieve and validate device details using TMF wrapper.
     *
     * @throws JsonProcessingException If there is an issue with JSON processing.
     */
    @Test
    @Order(26)
    public void getDeviceTMFwrapper() throws JsonProcessingException {
        String expectedJson = "{\n" +
                "\"name\":\"multishelf_dev_4\",\n" +
                "\"@type\":\"device\",\n" +
                "\"isBundle\":false,\n" +
                "\"place\":\n" +
                "[{\"name\":\"mockbuilding_4\",\n" +
                "\"clliCode\":\"mockBuidling4\",\n" +
                "\"phoneNumber\":\"9876543210\",\n" +
                "\"contactPerson\":\"mock12345\",\n" +
                "\"address\":\"mockadress12345,Chennai,TamilNadu\",\n" +
                "\"latitude\":\"12.6789\",\n" +
                "\"longitude\":\"85.4567\",\n" +
                "\"drivingInstructions\":\"road,metro\",\n" +
                "\"href\":\"https://192.168.1.100:8080/getBuildingById?id=53\",\n" +
                "\"notes\":null,\n" +
                "\"additionalAttributes\":[]}],\n" +
                "\"resourceCharacteristics\":\n" +
                "[{\"name\":\"serialNumber\",\n" +
                "\"value\":\"887654fg\"},\n" +
                "{\"name\":\"managementIp\",\n" +
                "\"value\":\"\"},\n" +
                "{\"name\":\"rackPosition\",\n" +
                "\"value\":\"6\"},\n" +
                "{\"name\":\"deviceModel\",\n" +
                "\"value\":\"mockdevicemodel_4\"}],\n" +
                "\"operationalState\":\"Active\",\n" +
                "\"administrativeState\":\"Active\",\n" +
                "\"usageState\":\"unreserved\",\n" +
                "\"shelves\":\n" +
                "[{\"name\":\"multishelf_dev_4_shelf_1\",\n" +
                "\"shelfPosition\":1,\n" +
                "\"operationalState\":\"unassigned\",\n" +
                "\"administrativeState\":\"unassigned\",\n" +
                "\"href\":\"https://192.168.1.100:8080/getShelfById?id=57\",\n" +
                "\"usageState\":\"unassigned\",\n" +
                "\"slots\":\n" +
                "[{\"name\":\"multishelf_dev_4/1/1\",\n" +
                "\"slotPosition\":1,\n" +
                "\"operationalState\":\"Active\",\n" +
                "\"administrativeState\":\"Active\",\n" +
                "\"usageState\":\"unreserved\",\n" +
                "\"href\":\"https://192.168.1.100:8080/getSlotById?id=60\",\n" +
                "\"cards\":\n" +
                "[{\"name\":\"mockcard_3\",\n" +
                "\"shelfPosition\":1,\n" +
                "\"slotPosition\":1,\n" +
                "\"vendor\":\"Sandisk\",\n" +
                "\"cardModel\":\"mockCard_3\",\n" +
                "\"cardPartNumber\":\"1346789\",\n" +
                "\"operationalState\":\"Active\",\n" +
                "\"administrativeState\":\"Active\",\n" +
                "\"usageState\":\"unreserved\",\n" +
                "\"href\":\"https://192.168.1.100:8080/getCardById?id=61\",\n" +
                "\"cardSlots\":[],\n" +
                "\"additionalAttributes\":[]}]}],\n" +
                "\"additionalAttributes\":[]},\n" +
                "{\"name\":\"multishelf_dev_4_shelf_2\",\n" +
                "\"shelfPosition\":2,\n" +
                "\"operationalState\":\"unassigned\",\n" +
                "\"administrativeState\":\"unassigned\",\n" +
                "\"href\":\"https://192.168.1.100:8080/getShelfById?id=58\",\n" +
                "\"usageState\":\"unassigned\",\n" +
                "\"slots\":\n" +
                "[{\"name\":\"multishelf_dev_4/2/1\",\n" +
                "\"slotPosition\":1,\n" +
                "\"operationalState\":\"Active\",\n" +
                "\"administrativeState\":\"Active\",\n" +
                "\"usageState\":\"unreserved\",\n" +
                "\"href\":\"https://192.168.1.100:8080/getSlotById?id=62\",\n" +
                "\"cards\":\n" +
                "[{\"name\":\"mockcard_4\",\n" +
                "\"shelfPosition\":2,\n" +
                "\"slotPosition\":1,\n" +
                "\"vendor\":\"Sandisk\",\n" +
                "\"cardModel\":\"mockCard_4\",\n" +
                "\"cardPartNumber\":\"13467\",\n" +
                "\"operationalState\":\"Active\",\n" +
                "\"administrativeState\":\"Active\",\n" +
                "\"usageState\":\"unreserved\",\n" +
                "\"href\":\"https://192.168.1.100:8080/getCardById?id=63\",\n" +
                "\"cardSlots\":\n" +
                "[{\"name\":\"mockcard_4/2\",\n" +
                "\"slotPosition\":2,\n" +
                "\"operationalState\":\"Active\",\n" +
                "\"administrativeState\":\"Active\",\n" +
                "\"usageState\":\"unreserved\",\n" +
                "\"href\":\"https://192.168.1.100:8080/getCardSlotById?id=64\",\n" +
                "\"pluggables\":null,\n" +
                "\"ports\":\n" +
                "{\"name\":\"mockport_7\",\n" +
                "\"positionOnCard\":2,\n" +
                "\"positionOnDevice\":null,\n" +
                "\"portType\":\"rj45\",\n" +
                "\"operationalState\":\"Active\",\n" +
                "\"administrativeState\":\"Active\",\n" +
                "\"usageState\":\"unreserved\",\n" +
                "\"href\":\"https://192.168.1.100:8080/getPortById?id=65\",\n" +
                "\"logicalPorts\":[],\n" +
                "\"additionalAttributes\":[],\n" +
                "\"capacity\":null,\n" +
                "\"portSpeed\":null}},\n" +
                "{\"name\":\"mockcard_4/9\",\n" +
                "\"slotPosition\":9,\n" +
                "\"operationalState\":\"Active\",\n" +
                "\"administrativeState\":\"Active\",\n" +
                "\"usageState\":\"unreserved\",\n" +
                "\"href\":\"https://192.168.1.100:8080/getCardSlotById?id=66\",\n" +
                "\"pluggables\":\n" +
                "{\"name\":\"mockpluggable_7\",\n" +
                "\"positionOnCard\":9,\n" +
                "\"positionOnDevice\":null,\n" +
                "\"vendor\":\"Nokia\",\n" +
                "\"pluggableModel\":\"mockPluggable_6\",\n" +
                "\"pluggablePartNumber\":\"SFP65499\",\n" +
                "\"operationalState\":\"Active\",\n" +
                "\"administrativeState\":\"Active\",\n" +
                "\"usageState\":\"unreserved\",\n" +
                "\"href\":\"https://192.168.1.100:8080/getPluggableById?id=67\",\n" +
                "\"logicalPorts\":[],\n" +
                "\"additionalAttributes\":[]},\n" +
                "\"ports\":null}],\n" +
                "\"additionalAttributes\":[]}]}],\n" +
                "\"additionalAttributes\":[]},\n" +
                "{\"name\":\"multishelf_dev_4_shelf_3\",\n" +
                "\"shelfPosition\":3,\n" +
                "\"operationalState\":\"unassigned\",\n" +
                "\"administrativeState\":\"unassigned\",\n" +
                "\"href\":\"https://192.168.1.100:8080/getShelfById?id=59\",\n" +
                "\"usageState\":\"unassigned\",\n" +
                "\"slots\":[],\n" +
                "\"additionalAttributes\":[]}],\n" +
                "\"devicePorts\":[],\n" +
                "\"devicePluggables\":[],\n" +
                "\"resourceSpecification\":\n" +
                "{\"name\":\"mockdevicemodel_4\",\n" +
                "\"href\":\"https://192.168.1.100:8080/getModelById?id=55\",\n" +
                "\"@referredType\":\"ResourceSpecificationRef\"},\n" +
                "\"relatedParty\":\n" +
                "{\"rack\":\n" +
                "{\"name\":\"mockrack_4\",\n" +
                "\"location\":\"mockbuilding_4\",\n" +
                "\"height\":19.08,\n" +
                "\"width\":1.73,\n" +
                "\"depth\":11.23,\n" +
                "\"numOfPositionsContained\":6,\n" +
                "\"reservedPositions\":[1,2,6],\n" +
                "\"freePositions\":[3,4,5],\n" +
                "\"notes\":null,\n" +
                "\"additionalAttributes\":[]}}\n" +
                "}";

        // Deserialize the expected JSON into object
        ObjectMapper objectMapper = new ObjectMapper();
        DeviceDetailsResponse expectedDevice = objectMapper.readValue(expectedJson, DeviceDetailsResponse.class);

        //To retrieve the overall detail about the device
        DeviceDetailsResponse actualDevice = deviceInstanceApiWrappers.getDeviceDetailsTmf("multishelf_dev_4");

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
//     * Test method to update a device using the TMF API wrapper.
//     * This test case updates device details with a specific JSON payload.
//     *
//     * @throws JsonProcessingException If there is an issue with JSON processing.
//     */
//    @Test
//    @Order(27)
//    public void updateDeviceTMFWrapper() throws JsonProcessingException {
//        String updateDeviceTMFWrapper = "{\n" +
//                "\"name\":\"multishelf_dev_4\",\n" +
//                "\"@type\":\"device\",\n" +
//                "\"isBundle\":false,\n" +
//                "\"place\":\n" +
//                "[{\"name\":\"mockbuilding_4\",\n" +
//                "\"clliCode\":\"mockBuidling4\",\n" +
//                "\"phoneNumber\":\"9876543210\",\n" +
//                "\"contactPerson\":\"mock12345\",\n" +
//                "\"address\":\"mockadress12345,Chennai,TamilNadu\",\n" +
//                "\"latitude\":\"12.6789\",\n" +
//                "\"longitude\":\"85.4567\",\n" +
//                "\"drivingInstructions\":\"road,metro\",\n" +
//                "\"href\":\"https://192.168.1.100:8080/getBuildingById?id=53\",\n" +
//                "\"notes\":null,\n" +
//                "\"additionalAttributes\":[]}],\n" +
//                "\"resourceCharacteristics\":\n" +
//                "[{\"name\":\"serialNumber\",\n" +
//                "\"value\":\"887654fg\"},\n" +
//                "{\"name\":\"managementIp\",\n" +
//                "\"value\":\"\"},\n" +
//                "{\"name\":\"rackPosition\",\n" +
//                "\"value\":\"6\"},\n" +
//                "{\"name\":\"deviceModel\",\n" +
//                "\"value\":\"mockdevicemodel_4\"}],\n" +
//                "\"operationalState\":\"Active\",\n" +
//                "\"administrativeState\":\"Active\",\n" +
//                "\"usageState\":\"reserved\",\n" +
//                "\"shelves\":\n" +
//                "[{\"name\":\"multishelf_dev_4_shelf_1\",\n" +
//                "\"shelfPosition\":1,\n" +
//                "\"operationalState\":\"unassigned\",\n" +
//                "\"administrativeState\":\"unassigned\",\n" +
//                "\"href\":\"https://192.168.1.100:8080/getShelfById?id=57\",\n" +
//                "\"usageState\":\"assigned\",\n" +
//                "\"slots\":\n" +
//                "[{\"name\":\"multishelf_dev_4/1/1\",\n" +
//                "\"slotPosition\":1,\n" +
//                "\"operationalState\":\"Active\",\n" +
//                "\"administrativeState\":\"Active\",\n" +
//                "\"usageState\":\"reserved\",\n" +  // Updated to "reserved"
//                "\"href\":\"https://192.168.1.100:8080/getSlotById?id=60\",\n" +
//                "\"cards\":\n" +
//                "[{\"name\":\"mockcard_3\",\n" +
//                "\"shelfPosition\":1,\n" +
//                "\"slotPosition\":1,\n" +
//                "\"vendor\":\"Sandisk\",\n" +
//                "\"cardModel\":\"mockCard_3\",\n" +
//                "\"cardPartNumber\":\"12345678\",\n" +
//                "\"operationalState\":\"Active\",\n" +
//                "\"administrativeState\":\"Active\",\n" +
//                "\"usageState\":\"reserved\",\n" +
//                "\"href\":\"https://192.168.1.100:8080/getCardById?id=61\",\n" +
//                "\"cardSlots\":[],\n" +
//                "\"additionalAttributes\":[]}]}],\n" +
//                "\"additionalAttributes\":[]},\n" +
//                "{\"name\":\"multishelf_dev_4_shelf_2\",\n" +
//                "\"shelfPosition\":2,\n" +
//                "\"operationalState\":\"unassigned\",\n" +
//                "\"administrativeState\":\"unassigned\",\n" +
//                "\"href\":\"https://192.168.1.100:8080/getShelfById?id=58\",\n" +
//                "\"usageState\":\"assigned\",\n" +
//                "\"slots\":\n" +
//                "[{\"name\":\"multishelf_dev_4/2/1\",\n" +
//                "\"slotPosition\":1,\n" +
//                "\"operationalState\":\"Active\",\n" +
//                "\"administrativeState\":\"Active\",\n" +
//                "\"usageState\":\"reserved\",\n" +
//                "\"href\":\"https://192.168.1.100:8080/getSlotById?id=62\",\n" +
//                "\"cards\":\n" +
//                "[{\"name\":\"mockcard_4\",\n" +
//                "\"shelfPosition\":2,\n" +
//                "\"slotPosition\":1,\n" +
//                "\"vendor\":\"Sandisk\",\n" +
//                "\"cardModel\":\"mockCard_4\",\n" +
//                "\"cardPartNumber\":\"12345678\",\n" +
//                "\"operationalState\":\"Active\",\n" +
//                "\"administrativeState\":\"Active\",\n" +
//                "\"usageState\":\"reserved\",\n" +
//                "\"href\":\"https://192.168.1.100:8080/getCardById?id=63\",\n" +
//                "\"cardSlots\":\n" +
//                "[{\"name\":\"mockcard_4/2\",\n" +
//                "\"slotPosition\":2,\n" +
//                "\"operationalState\":\"Active\",\n" +
//                "\"administrativeState\":\"Active\",\n" +
//                "\"usageState\":\"reserved\",\n" +
//                "\"href\":\"https://192.168.1.100:8080/getCardSlotById?id=64\",\n" +
//                "\"pluggables\":null,\n" +
//                "\"ports\":\n" +
//                "{\"name\":\"mockport_7\",\n" +
//                "\"positionOnCard\":2,\n" +
//                "\"positionOnDevice\":null,\n" +
//                "\"portType\":\"rj45\",\n" +
//                "\"operationalState\":\"Active\",\n" +
//                "\"administrativeState\":\"Active\",\n" +
//                "\"usageState\":\"reserved\",\n" +
//                "\"href\":\"https://192.168.1.100:8080/getPortById?id=65\",\n" +
//                "\"logicalPorts\":[],\n" +
//                "\"additionalAttributes\":[],\n" +
//                "\"capacity\":null,\n" +
//                "\"portSpeed\":null}},\n" +
//                "{\"name\":\"mockcard_4/9\",\n" +
//                "\"slotPosition\":9,\n" +
//                "\"operationalState\":\"Active\",\n" +
//                "\"administrativeState\":\"Active\",\n" +
//                "\"usageState\":\"reserved\",\n" +
//                "\"href\":\"https://192.168.1.100:8080/getCardSlotById?id=66\",\n" +
//                "\"pluggables\":\n" +
//                "{\"name\":\"mockpluggable_7\",\n" +
//                "\"positionOnCard\":9,\n" +
//                "\"positionOnDevice\":null,\n" +
//                "\"vendor\":\"Nokia\",\n" +
//                "\"pluggableModel\":\"mockPluggable_6\",\n" +
//                "\"pluggablePartNumber\":\"SFP65499\",\n" +
//                "\"operationalState\":\"Active\",\n" +
//                "\"administrativeState\":\"Active\",\n" +
//                "\"usageState\":\"reserved\",\n" +
//                "\"href\":\"https://192.168.1.100:8080/getPluggableById?id=67\",\n" +
//                "\"logicalPorts\":[],\n" +
//                "\"additionalAttributes\":[]},\n" +
//                "\"ports\":null}],\n" +
//                "\"additionalAttributes\":[]}]}],\n" +
//                "\"additionalAttributes\":[]},\n" +
//                "{\"name\":\"multishelf_dev_4_shelf_3\",\n" +
//                "\"shelfPosition\":3,\n" +
//                "\"operationalState\":\"unassigned\",\n" +
//                "\"administrativeState\":\"unassigned\",\n" +
//                "\"href\":\"https://192.168.1.100:8080/getShelfById?id=59\",\n" +
//                "\"usageState\":\"assigned\",\n" +
//                "\"slots\":[],\n" +
//                "\"additionalAttributes\":[]}],\n" +
//                "\"devicePorts\":[],\n" +
//                "\"devicePluggables\":[],\n" +
//                "\"resourceSpecification\":\n" +
//                "{\"name\":\"mockdevicemodel_4\",\n" +
//                "\"href\":\"https://192.168.1.100:8080/getModelById?id=55\",\n" +
//                "\"@referredType\":\"ResourceSpecificationRef\"},\n" +
//                "\"relatedParty\":\n" +
//                "{\"rack\":\n" +
//                "{\"name\":\"mockrack_4\",\n" +
//                "\"location\":\"mockbuilding_4\",\n" +
//                "\"height\":19.08,\n" +
//                "\"width\":1.73,\n" +
//                "\"depth\":11.23,\n" +
//                "\"numOfPositionsContained\":6,\n" +
//                "\"reservedPositions\":[1,2,6],\n" +
//                "\"freePositions\":[3,4,5],\n" +
//                "\"notes\":null,\n" +
//                "\"additionalAttributes\":[]}}\n" +
//                "}";
//
//        // Deserialize the country JSON into object
//        ObjectMapper objectMapper = new ObjectMapper();
//        DeviceDetailsResponse updateDeviceDetails = objectMapper.readValue(updateDeviceTMFWrapper, DeviceDetailsResponse.class);
//
//        // Call the API to update the device
//        deviceInstanceApiWrappers.createDeviceTmf(updateDeviceDetails);
//    }
}
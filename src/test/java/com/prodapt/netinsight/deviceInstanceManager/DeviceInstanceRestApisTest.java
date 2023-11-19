package com.prodapt.netinsight.deviceInstanceManager;

import com.prodapt.netinsight.connectionManager.PhysicalConnection;
import com.prodapt.netinsight.connectionManager.PhysicalConnectionRepository;
import com.prodapt.netinsight.deviceMetaModeller.DeviceMetaModel;
import com.prodapt.netinsight.deviceMetaModeller.DeviceMetaModelRepository;
import com.prodapt.netinsight.exceptionsHandler.ServiceException;
import com.prodapt.netinsight.locationManager.*;
import com.prodapt.netinsight.orderManager.OrderEntity;
import com.prodapt.netinsight.orderManager.OrderRepository;
import com.prodapt.netinsight.uiHelper.AsyncService;
import com.prodapt.netinsight.uiHelper.ObjectCount;
import com.prodapt.netinsight.uiHelper.ObjectCountRepo;
import com.prodapt.netinsight.uiHelper.VendorsRepo;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@ActiveProfiles("UNIT")
public class DeviceInstanceRestApisTest {
    @MockBean
    CardRepository cardRepository;

    @MockBean
    VendorsRepo vendorsRepo;

    @MockBean
    CardSlotRepository cardSlotRepository;
    @MockBean
    DeviceRepository deviceRepository;
    @MockBean
    LogicalPortRepository logicalPortRepository;
    @MockBean
    PluggableRepository pluggableRepository;
    @MockBean
    PortRepository portRepository;
    @MockBean
    ShelfRepository shelfRepository;
    @MockBean
    SlotRepository slotRepository;
    @MockBean
    DeviceMetaModelRepository deviceMetaModelRepository;
    @Autowired
    DeviceInstanceRestApis deviceInstanceRestApis;
    @MockBean
    BuildingRepository buildingRepository;
    @MockBean
    RackRepository rackRepository;
    @MockBean
    OrderRepository orderRepository;

    @Test
    void updateDeviceByNameFieldNotFoundTest() {
        Device device = new Device();
        Long orderId = 3456l;
        Exception e = assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateDeviceWithName(orderId, "deviceName", device);
        });
        assertEquals("Device name field is mandatory", e.getMessage());
    }

    @Test
    void updateDeviceByNameOrderNotFoundTest() {
        Device device = new Device();
        device.setName("deviceNameTest");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(null);

        Exception e = assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateDeviceWithName(orderId, "deviceName", device);
        });
        assertEquals("The order " + orderId + " is not found", e.getMessage());
    }

    @Test
    void updateDeviceByNameParamsMismatchTest() {
        Device device = new Device();
        device.setName("deviceNameTest");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        Exception e = assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateDeviceWithName(orderId, "deviceName", device);
        });
        assertEquals("Request parameter does not match with the request body", e.getMessage());
    }

    @Test
    void updateDeviceByNameModelDoNotExistTest() {
        Device device = new Device();
        device.setName("deviceName");
        device.setDeviceModel("deviceModel");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel().toLowerCase())).thenReturn(null);
        Exception e = assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateDeviceWithName(orderId, "deviceName", device);
        });
        assertEquals("Device Meta-model doesn't exist for the device type: deviceModel", e.getMessage());
    }

    @Test
    void updateDeviceByNameDeviceDoNotExistTest() {
        Device device = new Device();
        device.setName("deviceName");
        device.setDeviceModel("deviceModel");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        DeviceMetaModel deviceMetaModel = new DeviceMetaModel();

        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel())).thenReturn(deviceMetaModel);
        Mockito.when(deviceRepository.getByName(device.getName())).thenReturn(null);
        Exception e = assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateDeviceWithName(orderId, "deviceName", device);
        });
        assertEquals("Device with the given name devicename doesn't exist", e.getMessage());
    }

    @Test
    void updateDeviceByNameSuccessTest() {
        Device device = new Device();
        device.setName("deviceName");
        device.setDeviceModel("deviceModel");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        DeviceMetaModel deviceMetaModel = new DeviceMetaModel();

        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel())).thenReturn(deviceMetaModel);
        Mockito.when(deviceRepository.findByName(device.getName().toLowerCase())).thenReturn(device);
        doNothing().when(deviceRepository).updateDevice(device.getName().toLowerCase(), device.getDeviceModel(), device.getLocation(),
                device.getOrganisation(), device.getCustomer(), device.getManagementIp(), device.getRackPosition(),
                device.getOperationalState(), device.getAdministrativeState(), device.getUsageState(),
                device.getSerialNumber(), deviceMetaModel.getShelvesContained(), orderId);

        JSONObject response = deviceInstanceRestApis.updateDeviceWithName(orderId, "deviceName", device);
        assertEquals(response.get("status"), "Success");
    }

    //@@@@//

    @Test
    void updateCardByNameFieldNotFoundTest() {
        Card card = new Card();
        Long orderId = 3456l;
        Exception e = assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateCardByName(orderId, "deviceName", "cardName", card);
        });
        assertEquals("Card name field is mandatory", e.getMessage());
    }

    @Test
    void updateCardByNameOrderNotFoundTest() {
        Card card = new Card();
        card.setName("cardNameTest");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(null);

        Exception e = assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateCardByName(orderId, "deviceName", "cardName", card);
        });
        assertEquals("The order " + orderId + " is not found", e.getMessage());
    }

    @Test
    void updateCardByNameParamsMismatchTest() {
        Card card = new Card();
        card.setName("cardNameTest");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        Exception e = assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateCardByName(orderId, "deviceName", "cardName", card);
        });
        assertEquals("Request parameter does not match with the request body", e.getMessage());
    }

    @Test
    void updateCardByNameDoNotExistTest() {
        Card card = new Card();
        card.setName("cardName");

        String deviceName = "test";

        Mockito.when(cardRepository.getCard(deviceName, card.getName().toLowerCase())).thenReturn(null);
        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        Mockito.when(cardRepository.getCard(card.getName().toLowerCase())).thenReturn(null);
        Exception e = assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateCardByName(orderId, deviceName, "cardName", card);
        });
        assertEquals("Card with the given name cardname doesn't exist", e.getMessage());
    }

    @Test
    void updateCardByNameDeviceDoNotExistTest() {
        Card card = new Card();
        card.setName("cardName");

        String deviceName = "test";

        Mockito.when(cardRepository.getCard(deviceName, card.getName().toLowerCase())).thenReturn(card);
        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        Mockito.when(cardRepository.getCard(card.getName().toLowerCase())).thenReturn(card);
        Mockito.when(deviceRepository.findByCardName(card.getName().toLowerCase())).thenReturn(null);
        Exception e = assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateCardByName(orderId, deviceName, "cardName", card);
        });
        assertEquals("Unable to get device details for the given device name: " + deviceName, e.getMessage());
    }

    @Test
    void updateCardByNameDeviceModelDoNotExistTest() {
        Card card = new Card();
        card.setName("cardName");

        Device device = new Device();

        Mockito.when(cardRepository.getCard("test", card.getName().toLowerCase())).thenReturn(card);
        Mockito.when(deviceRepository.findByName("test")).thenReturn(device);
        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        Mockito.when(cardRepository.getCard(card.getName().toLowerCase())).thenReturn(card);
        Mockito.when(deviceRepository.findByCardName(card.getName().toLowerCase())).thenReturn(device);
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel())).thenReturn(null);
        Exception e = assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateCardByName(orderId, "test", "cardName", card);
        });
        assertEquals("Device Meta-model doesn't exist for the device type: null", e.getMessage());
    }

    @Test
    void updateCardByNameCardNotAllowedTest() {
        Card card = new Card();
        card.setName("cardName");
        card.setCardModel("cardModel");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        Device device = new Device();
        device.setDeviceModel("deviceModel");

        ArrayList cardList = new ArrayList();
        cardList.add("cardModel1");

        DeviceMetaModel deviceMetaModel = new DeviceMetaModel();
        deviceMetaModel.setAllowedCardList(cardList);

        Mockito.when(cardRepository.getCard("test", card.getName().toLowerCase())).thenReturn(card);
        Mockito.when(deviceRepository.findByName("test")).thenReturn(device);
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel())).thenReturn(deviceMetaModel);

        Exception e = assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateCardByName(orderId, "test", "cardName", card);
        });
        assertTrue(e.getMessage().contains("Given card model: cardModel is not compatible with the device model," +
                " AllowedCardList on the device:"));
    }

    @Test
    void updateCardByNameShelfDoNotExistTest() {
        Card card = new Card();
        card.setName("cardName");
        card.setCardModel("cardModel");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        Device device = new Device();
        device.setDeviceModel("deviceModel");

        DeviceMetaModel deviceMetaModel = new DeviceMetaModel();
        deviceMetaModel.setAllowedCardList(new ArrayList<>(Collections.singleton("cardModel")));

        Mockito.when(cardRepository.getCard("test", card.getName().toLowerCase())).thenReturn(card);
        Mockito.when(deviceRepository.findByName("test")).thenReturn(device);
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel())).thenReturn(deviceMetaModel);
        Mockito.when(shelfRepository.getShelf(any())).thenReturn(null);

        Exception e = assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateCardByName(orderId, "test", "cardName", card);
        });
        assertEquals("Given shelf doesn't exist", e.getMessage());
    }

    @Test
    void updateCardByNameCardDoNotExistTest() {
        Card card = new Card();
        card.setName("cardName");
        card.setCardModel("cardModel");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        Device device = new Device();
        device.setDeviceModel("deviceModel");

        DeviceMetaModel deviceMetaModel = new DeviceMetaModel();
        deviceMetaModel.setAllowedCardList(new ArrayList<>(Collections.singleton("cardModel")));

        Mockito.when(cardRepository.getCard("test", card.getName().toLowerCase())).thenReturn(card).thenReturn(null);
        Mockito.when(deviceRepository.findByName("test")).thenReturn(device);
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel())).thenReturn(deviceMetaModel);
        Mockito.when(shelfRepository.getShelf(any())).thenReturn(new Shelf());

        Exception e = assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateCardByName(orderId, "test", "cardName", card);
        });
        assertEquals("Given card doesn't exist", e.getMessage());
    }

    @Test
    void updateCardByNameCardAlreadyExistTest() {
        Card card = new Card();
        card.setName("cardName");
        card.setCardModel("cardModel");
        card.setShelfPosition(1);
        card.setSlotPosition(1);

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        Card card2 = new Card();
        card2.setName("cardNameTest");
        card2.setSlotPosition(2);

        Device device = new Device();
        device.setDeviceModel("deviceModel");

        DeviceMetaModel deviceMetaModel = new DeviceMetaModel();
        deviceMetaModel.setAllowedCardList(new ArrayList<>(Collections.singleton("cardModel")));

        Mockito.when(cardRepository.getCard("test", card.getName().toLowerCase())).thenReturn(card).thenReturn(card2);
        Mockito.when(deviceRepository.findByName("test")).thenReturn(device);
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel())).thenReturn(deviceMetaModel);
        Mockito.when(shelfRepository.getShelf(any())).thenReturn(new Shelf());
        Mockito.when(cardRepository.getCardOnASlot(any())).thenReturn(card2);

        Exception e = assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateCardByName(orderId, "test", "cardName", card);
        });
        assertTrue(e.getMessage().contains("A card already exist on the given shelf, slot position. Existing card details: "));
    }

    @Test
    void updateCardByNameSuccessTest() {
        Card card = new Card();
        card.setName("cardName");
        card.setCardModel("cardModel");
        card.setShelfPosition(1);
        card.setSlotPosition(1);

        Card card2 = new Card();
        card2.setShelfPosition(2);
        card2.setSlotPosition(2);

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        Device device = new Device();
        device.setDeviceModel("deviceModel");

        DeviceMetaModel deviceMetaModel = new DeviceMetaModel();
        deviceMetaModel.setAllowedCardList(new ArrayList<>(Collections.singleton("cardModel")));

        Mockito.when(cardRepository.getCard("test", card.getName().toLowerCase())).thenReturn(card).thenReturn(card2);
        Mockito.when(deviceRepository.findByName("test")).thenReturn(device);
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel())).thenReturn(deviceMetaModel);
        Mockito.when(shelfRepository.getShelf(any())).thenReturn(new Shelf());
        Mockito.when(cardRepository.getCardOnASlot(any())).thenReturn(null);
        doNothing().when(slotRepository).deleteSlot(any());
        Mockito.when(slotRepository.createSlot(any(), any(), any(), any(), any(), any())).thenReturn(new Slot());
        Mockito.when(cardRepository.updateCard(any(), anyInt(), anyInt(), any(), any(), any(), any(), any(), any(), any(), anyLong())).thenReturn(card);

        JSONObject response = deviceInstanceRestApis.updateCardByName(orderId, "test", "cardName", card);
        assertEquals(response.get("status"), "Success");
        assertEquals(response.get("card"), card);
    }

    @Test
    void getAllPluggablesOnCardTestSuccess() {
        // Arrange
        String device = "testd";
        String cardName = "testDevice";
        cardName = cardName.toLowerCase();
        ArrayList<Pluggable> PluggablesOnCard = new ArrayList<>();
        PluggablesOnCard.add(new Pluggable());
        Mockito.when(pluggableRepository.getAllPluggablesOnCard(device, cardName)).thenReturn(PluggablesOnCard);
        // Act
        ArrayList<Pluggable> actualPluggablesOnCard = deviceInstanceRestApis.getAllPluggablesOnCard(device, cardName);
        // Assert
        assertEquals(PluggablesOnCard, actualPluggablesOnCard);
    }

    @Test
    void getAllPluggablesOnCardTestFailure() {
        // Arrange
        String device = "testd";
        String cardName = "testDevice";
        String Name = cardName.toLowerCase();
        // Mock the port repository to throw an exception
        Mockito.when(pluggableRepository.getAllPluggablesOnCard(device, Name))
                .thenThrow(new RuntimeException("Failed to fetch PluggablesOnCard: " + Name));
        // Act and Assert
        Exception e = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.getAllPluggablesOnCard(device, Name);
        });
        assertTrue(e.getMessage().contains("Failed to fetch PluggablesOnCard: " + Name));
    }

    @Test
    void getAllPluggablesOnDeviceTestSuccess() {
        // Arrange
        String devicename = "testDevice";
        devicename = devicename.toLowerCase();
        ArrayList<Pluggable> PluggablesOnDevice = new ArrayList<>();
        PluggablesOnDevice.add(new Pluggable());
        Mockito.when(pluggableRepository.getPluggablesOnDevice(devicename)).thenReturn(PluggablesOnDevice);
        // Act
        ArrayList<Pluggable> actualPluggablesOnCard = deviceInstanceRestApis.getAllPluggalblesOnDevice(devicename);
        // Assert
        assertEquals(PluggablesOnDevice, actualPluggablesOnCard);
    }

    @Test
    void getAllPluggablesOnDeviceTestFailure() {
        // Arrange
        String deviceName = "testDevice";
        String name = deviceName.toLowerCase();
        Mockito.when(pluggableRepository.getPluggablesOnDevice(name)).thenThrow(new RuntimeException("Failed to fetch Pluggables for device: " + name));

        // Act and Assert
        Exception e = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.getAllPluggalblesOnDevice(name);
        });
        assertTrue(e.getMessage().contains("Failed to fetch Pluggables for device: " + name));
    }

    @Test
    void getAllPortsOnDeviceTestSuccess() {
        // Arrange
        String deviceName = "testDevice";
        deviceName = deviceName.toLowerCase();
        ArrayList<Port> ports = new ArrayList<>();
        ports.add(new Port());
        Mockito.when(portRepository.getPortsOnDevice(deviceName)).thenReturn(ports);
        // Act
        ArrayList<Port> actualports = deviceInstanceRestApis.getAllPortsOnDevice(deviceName);
        // Assert
        assertEquals(ports, actualports);
    }

    @Test
    void getAllPortsOnDeviceTestFailure() {
        // Arrange
        String deviceName = "testDevice";
        String Name = deviceName.toLowerCase();
        // Mock the port repository to throw an exception
        Mockito.when(portRepository.getPortsOnDevice(Name))
                .thenThrow(new RuntimeException("Failed to fetch ports for device: " + Name));
        // Act and Assert
        Exception e = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.getAllPortsOnDevice(Name);
        });
        assertTrue(e.getMessage().contains("Failed to fetch ports for device: " + Name));
    }

    @Test
    void getAllPortsOnCardTestSuccess() {
        // Arrange
        String deviceName = "testd";
        String cardName = "testDevice";
        String Name = cardName.toLowerCase();
        ArrayList<Port> PortsOnCard = new ArrayList<>();
        PortsOnCard.add(new Port());
        Mockito.when(portRepository.getAllPortsOnCard(deviceName, Name)).thenReturn(PortsOnCard);
        // Act
        ArrayList<Port> actualPortsOnCard = deviceInstanceRestApis.getAllPortsOnCard(deviceName, Name);
        // Assert
        assertEquals(PortsOnCard, actualPortsOnCard);
    }

    @Test
    void getDeviceSuccess() {
        String devicename = "testdevice";
        Device device = new Device();
        device.setName(devicename);

        Mockito.when(deviceRepository.getByName(devicename)).thenReturn(device);
        Device actualdevice = deviceInstanceRestApis.getDevice(devicename);
        Assertions.assertEquals(device, actualdevice);
    }

    @Test
    void getDeviceFailure() {
        String devicename = "testdevice";
        Mockito.when(deviceRepository.getByName(devicename)).thenReturn(null);

        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.getDevice(devicename);
        });
        Assertions.assertEquals("Device not found: " + devicename, e.getMessage());
    }

    @Test
    void getDevicesInRackSuccess() {
        String rackname = "testrack";
        ArrayList<Device> device = new ArrayList<>();
        Mockito.when(deviceRepository.getDevicesInRack(rackname)).thenReturn(device);
        List<Device> actualdevice = deviceInstanceRestApis.getDevicesInRack(rackname);
        Assertions.assertEquals(device, actualdevice);
    }

    @Test
    void getDevicesInRackFailure() {
        String rackname = "testrack";
        Mockito.when(deviceRepository.getDevicesInRack(rackname)).thenThrow(new RuntimeException("Device not found in the given rack" + rackname));
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.getDevicesInRack(rackname);
        });
        Assertions.assertEquals("Device not found in the given rack" + rackname, e.getMessage());
    }

    @Test
    void getDevicesInBuildingSuccess() {
        String buildingname = "testbuilding";
        ArrayList<Device> device = new ArrayList<>();
        Mockito.when(deviceRepository.getDevicesInBuilding(buildingname)).thenReturn(device);
        List<Device> actualdevice = deviceInstanceRestApis.getDevicesInbuilding(buildingname);
        Assertions.assertEquals(device, actualdevice);
    }

    @Test
    void getDevicesInBuildingFailure() {
        String buildingname = "testbuilding";
        Mockito.when(deviceRepository.getDevicesInBuilding(buildingname)).thenThrow(new RuntimeException("Device not found in the given building" + buildingname));
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.getDevicesInbuilding(buildingname);
        });
        Assertions.assertEquals("Device not found in the given building" + buildingname, e.getMessage());
    }

    @Test
    void getCardSuccess() {
        String cardname = "testcard";
        Card card = new Card();
        card.setName(cardname);

        Mockito.when(cardRepository.getByName(cardname)).thenReturn(card);
        Card actualcard = deviceInstanceRestApis.getCard(cardname);
        Assertions.assertEquals(card, actualcard);
    }

    @Test
    void getCardFailure() {
        String cardname = "testcard";
        Mockito.when(cardRepository.getByName(cardname)).thenThrow(new RuntimeException("Card not found"));

        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.getCard(cardname);
        });
        Assertions.assertEquals("Card not found", e.getMessage());
    }

    @Test
    void getShelfSuccess() {
        String shelfname = "testshelf";
        Shelf shelf = new Shelf();
        shelf.setName(shelfname);

        Mockito.when(shelfRepository.getByName(shelfname)).thenReturn(shelf);
        Shelf actualshelf = deviceInstanceRestApis.getShelf(shelfname);
        Assertions.assertEquals(shelf, actualshelf);
    }

    @Test
    void getShelfFailure() {
        String shelfname = "testshelf";
        Mockito.when(shelfRepository.getByName(shelfname)).thenThrow(new RuntimeException("Shelf not found"));

        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.getShelf(shelfname);
        });
        Assertions.assertEquals("Shelf not found", e.getMessage());
    }

    @Test
    void getShelvesOnDeviceSuccess() {
        String devicename = "testdevice";
        ArrayList<Shelf> shelves = new ArrayList<>();
        Mockito.when(shelfRepository.getShelves(devicename)).thenReturn(shelves);
        ArrayList<Shelf> actualshelves = deviceInstanceRestApis.getShelvesOnDevice(devicename);
        Assertions.assertEquals(shelves, actualshelves);
    }

    @Test
    void getShelvesOnDeviceFailure() {
        String devicename = "testdevice";
        Mockito.when(shelfRepository.getShelves(devicename)).thenThrow(new RuntimeException("Shelves not found on device" + devicename));

        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.getShelvesOnDevice(devicename);
        });
        Assertions.assertEquals("Shelves not found on device" + devicename, e.getMessage());
    }

    @Test
    void getShelfByIDSuccess() {
        Long id = Long.valueOf(87);
        Shelf shelf = new Shelf();
        shelf.setHref("http://localhost/id");

        Mockito.when(shelfRepository.findShelfById(id)).thenReturn(shelf);
        Shelf actualshelf = deviceInstanceRestApis.findShelfById(id);

        Assertions.assertEquals(shelf, actualshelf);
    }

    @Test
    void getShelfByIdFailure() {
        Long id = Long.valueOf(87);
        Mockito.when(shelfRepository.findShelfById(id)).thenThrow(new RuntimeException("Cannot find the shelf by the given Id"));
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.findShelfById(id);
        });
        Assertions.assertEquals("Cannot find the shelf by the given Id", e.getMessage());
    }

    @Test
    void getSlotByIdSuccess() {
        Long id = Long.valueOf(87);
        Slot slot = new Slot();
        slot.setHref("http://loscalhost/slot");
        Mockito.when(slotRepository.findSlotById(id)).thenReturn(slot);
        Slot actualslot = deviceInstanceRestApis.findSlotById(id);
        Assertions.assertEquals(slot, actualslot);
    }

    @Test
    void getSlotByIdFailure() {
        Long id = Long.valueOf(87);
        Mockito.when(slotRepository.findSlotById(id)).thenThrow(new RuntimeException("Cannot find the slot by the given Id"));
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.findSlotById(id);
        });
        Assertions.assertEquals("Cannot find the slot by the given Id", e.getMessage());

    }

    @Test
    void getSlotByNameSuccess() {
        String slotname = "testslot";
        Slot slot = new Slot();
        slot.setHref("http://loscalhost/slot");
        Mockito.when(slotRepository.getByName(slotname)).thenReturn(slot);
        Slot actualslot = deviceInstanceRestApis.getSlotByName(slotname);
        Assertions.assertEquals(slot, actualslot);
    }

    @Test
    void getSlotByNameFailure() {
        String slotname = "testslot";
        Mockito.when(slotRepository.getByName(slotname)).thenThrow(new RuntimeException("Cannot find the slot by the given name"));
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.getSlotByName(slotname);
        });
        Assertions.assertEquals("Cannot find the slot by the given name", e.getMessage());
    }

    @Test
    void getSlotOnShelf() {
        String shelfname = "testshelf";
        ArrayList<Slot> slots = new ArrayList<>();
        Mockito.when(slotRepository.getSlotsOnShelf(shelfname)).thenReturn(slots);
        ArrayList<Slot> actualslot = deviceInstanceRestApis.getSlotOnShelf(shelfname);
        Assertions.assertEquals(slots, actualslot);
    }

    @Test
    void getSlotOnShelfFailure() {
        String shelfname = "testshelf";
        Mockito.when(slotRepository.getSlotsOnShelf(shelfname)).thenThrow(new RuntimeException("Cannot find the slot by the given shelfname"));
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.getSlotOnShelf(shelfname);
        });
        Assertions.assertEquals("Cannot find the slot by the given shelfname", e.getMessage());
    }

    @Test
    void getPort() {
        String portname = "testport";
        Port port = new Port();
        port.setName(portname);
        Mockito.when(portRepository.getByName(portname)).thenReturn(port);
        Port actualport = deviceInstanceRestApis.getPort(portname);
        Assertions.assertEquals(port, actualport);
    }

    @Test
    void getPortFailure() {
        String portname = "testport";
        Mockito.when(portRepository.getByName(portname)).thenThrow(new RuntimeException("Cannot find the port"));
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.getPort(portname);
        });
        Assertions.assertEquals("Cannot find the port", e.getMessage());
    }

    @Test
    void getPluggable() {
        String pluggable = "testpluggable";
        Pluggable pluggable1 = new Pluggable();
        Mockito.when(pluggableRepository.getByName(pluggable)).thenReturn(pluggable1);
        Pluggable actualpluggable = deviceInstanceRestApis.getPluggable(pluggable);
        Assertions.assertEquals(pluggable1, actualpluggable);
    }

    @Test
    void getPluggableFailure() {
        String pluggable = "testpluggable";
        Mockito.when(pluggableRepository.getByName(pluggable)).thenThrow(new RuntimeException("Cannot find the pluggable"));
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.getPluggable(pluggable);
        });
        Assertions.assertEquals("Cannot find the pluggable", e.getMessage());
    }

    @Test
    void createDeviceOnBuildingSuccess() {
        String building = "testbuilding";

        Device device = new Device();
        device.setName("testdevice");
        device.setDeviceModel("nokia");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        DeviceMetaModel deviceMetaModel = new DeviceMetaModel();
        deviceMetaModel.setDeviceModel("nokia");

        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel())).thenReturn(deviceMetaModel);

        Building buildingDetails = new Building();
        buildingDetails.setName(building);

        Mockito.when(buildingRepository.findByName(building)).thenReturn(buildingDetails);
        Mockito.when(deviceRepository.findByName(device.getName())).thenReturn(null);
        doNothing().when(deviceRepository).createDeviceOnBuilding(building, device.getName(), device.getDeviceModel(),
                device.getLocation(), device.getOrganisation(), device.getCustomer(), device.getManagementIp(),
                device.getRackPosition(), device.getOperationalState(), device.getAdministrativeState(),
                device.getUsageState(), device.getSerialNumber(), deviceMetaModel.getShelvesContained(), orderId);
        JSONObject response = deviceInstanceRestApis.createDeviceOnBuilding(orderId, building, device);
        Assertions.assertEquals("Success", response.get("status"));

    }

    @Test
    void createDeviceOnBuildingFailure() {
        String building = "testbuilding";

        Device device = new Device();
        device.setName("testdevice");
        device.setDeviceModel("nokia");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);

        DeviceMetaModel deviceMetaModel = new DeviceMetaModel();
        deviceMetaModel.setDeviceModel("nokia");

        //case-1
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(null);
        Exception orderIdException = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.createDeviceOnBuilding(orderId, building, device);
        });
        Assertions.assertEquals("The order " + orderId + " is not found", orderIdException.getMessage());

        //case-2
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel())).thenReturn(null);
        Exception devicemetamodelexception = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.createDeviceOnBuilding(orderId, building, device);
        });
        Assertions.assertEquals("Device Meta-model doesn't exist for the device type: " + device.getDeviceModel(), devicemetamodelexception.getMessage());

        //case-3
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel())).thenReturn(deviceMetaModel);
        Mockito.when(buildingRepository.findByName(building)).thenReturn(null);
        Exception buildingdoesnotexistexception = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.createDeviceOnBuilding(orderId, building, device);
        });
        Assertions.assertEquals("Given building doesn't exist", buildingdoesnotexistexception.getMessage());

        //case-4
        Device existingdevice = new Device();
        existingdevice.setName(device.getName());

        Building buildingDetails = new Building();
        buildingDetails.setName(building);

        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel())).thenReturn(deviceMetaModel);
        Mockito.when(buildingRepository.findByName(building)).thenReturn(buildingDetails);
        Mockito.when(deviceRepository.findByName(device.getName())).thenReturn(existingdevice);
        Exception devicewithsamenameexception = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.createDeviceOnBuilding(orderId, building, device);
        });
        Assertions.assertEquals("Device with same name already exist, existingDeviceDetails: " + existingdevice, devicewithsamenameexception.getMessage());
    }

    @Test
    void updateDeviceOnBuildingSuccess() {
        String building = "testbuilding";

        Device device = new Device();
        device.setName("testdevice");
        device.setDeviceModel("nokia");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        DeviceMetaModel deviceMetaModel = new DeviceMetaModel();
        deviceMetaModel.setDeviceModel("nokia");

        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel())).thenReturn(deviceMetaModel);

        Building buildingDetails = new Building();
        buildingDetails.setName(building);

        Device existingdevice = new Device();
        existingdevice.setName(device.getName());

        Mockito.when(buildingRepository.findByName(building)).thenReturn(buildingDetails);
        Mockito.when(deviceRepository.findByName(device.getName())).thenReturn(existingdevice);
        doNothing().when(deviceRepository).updateDeviceOnBuilding(building, device.getName(), device.getDeviceModel(),
                device.getLocation(), device.getOrganisation(), device.getCustomer(), device.getManagementIp(),
                device.getRackPosition(), device.getOperationalState(), device.getAdministrativeState(),
                device.getUsageState(), device.getSerialNumber(), deviceMetaModel.getShelvesContained(), orderId);
        JSONObject response = deviceInstanceRestApis.updateDeviceOnBuilding(orderId, building, device);
        Assertions.assertEquals("Success", response.get("status"));
    }

    @Test
    void updateDeviceOnBuildingFailure() {
        String building = "testbuilding";

        Device device = new Device();
        device.setName("testdevice");
        device.setDeviceModel("nokia");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);

        DeviceMetaModel deviceMetaModel = new DeviceMetaModel();
        deviceMetaModel.setDeviceModel("nokia");

        //case-1
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(null);
        Exception orderIdException = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.createDeviceOnBuilding(orderId, building, device);
        });
        Assertions.assertEquals("The order " + orderId + " is not found", orderIdException.getMessage());

        //case-2
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel())).thenReturn(null);
        Exception devicemetamodelexception = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateDeviceOnBuilding(orderId, building, device);
        });
        Assertions.assertEquals("Device Meta-model doesn't exist for the device type: " + device.getDeviceModel(), devicemetamodelexception.getMessage());

        //case-3
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel())).thenReturn(deviceMetaModel);
        Mockito.when(buildingRepository.findByName(building)).thenReturn(null);
        Exception buildingdoesnotexistexception = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateDeviceOnBuilding(orderId, building, device);
        });
        Assertions.assertEquals("Given building doesn't exist", buildingdoesnotexistexception.getMessage());

        //case-4
        Building buildingDetails = new Building();
        buildingDetails.setName(building);

        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel())).thenReturn(deviceMetaModel);
        Mockito.when(buildingRepository.findByName(building)).thenReturn(buildingDetails);
        Mockito.when(deviceRepository.findByName(device.getName())).thenReturn(null);
        Exception devicewithsamenameexception = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateDeviceOnBuilding(orderId, building, device);
        });
        Assertions.assertEquals("Device with the given name doesn't exist", devicewithsamenameexception.getMessage());
    }

    @Test
    void createDeviceOnRack() {
        String rack = "testrack";

        Device device = new Device();
        device.setName("testdevice");
        device.setDeviceModel("nokia");
        device.setRackPosition("2");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        DeviceMetaModel deviceMetaModel = new DeviceMetaModel();
        deviceMetaModel.setDeviceModel("nokia");
        deviceMetaModel.setNumOfRackPositionOccupied(2);

        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel())).thenReturn(deviceMetaModel);
        Rack rack1 = new Rack();

        ArrayList<Integer> freePositions = new ArrayList<>();
        freePositions.add(2);
        freePositions.add(3);
        freePositions.add(2);
        ArrayList<Integer> reservedPositions = new ArrayList<>();
        reservedPositions.add(4);
        reservedPositions.add(5);

        rack1.setName(rack);
        rack1.setFreePositions(freePositions);
        rack1.setReservedPositions(reservedPositions);

        Mockito.when(rackRepository.findByName(rack)).thenReturn(rack1);

        int numberOfPositionsOccupied = deviceMetaModel.getNumOfRackPositionOccupied();
        freePositions = rack1.getFreePositions(); //[2,3]
        reservedPositions = rack1.getReservedPositions();//[4,5]

        int startPosition = Integer.parseInt(device.getRackPosition());//2
        int endPosition = (startPosition + numberOfPositionsOccupied) - 1;//(2+2)-1 = 3

        Rack rack2 = new Rack();
        rack2.setFreePositions(freePositions);
        rack2.setReservedPositions(reservedPositions);
        ArrayList<Integer> updatedFreePositions = rack2.getFreePositions();//[2,3]
        ArrayList<Integer> updatedReservedPositions = rack2.getReservedPositions();//[4,5]

        updatedReservedPositions.add(startPosition);//[4,5,2]
        updatedFreePositions.remove(Integer.valueOf(startPosition));//[3]

        Mockito.when(deviceRepository.findByName(device.getName())).thenReturn(null);


        rack2.setReservedPositions(updatedReservedPositions);
        rack2.setFreePositions(updatedFreePositions);

        doNothing().when(deviceRepository).createDeviceOnRack(rack, device.getName(), device.getDeviceModel(),
                device.getLocation(), device.getOrganisation(), device.getCustomer(), device.getManagementIp(),
                device.getRackPosition(), device.getOperationalState(), device.getAdministrativeState(),
                device.getUsageState(), device.getSerialNumber(), deviceMetaModel.getShelvesContained(), orderId);
        JSONObject response = deviceInstanceRestApis.createDevice(orderId, rack, device);
        Assertions.assertEquals("Success", response.get("status"));
    }

    @Test
    void createDeviceOnRackFailure() {
        String rack = "testrack";

        Device device = new Device();
        device.setName("testdevice");
        device.setDeviceModel("nokia");
        device.setRackPosition("2");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);

        DeviceMetaModel deviceMetaModel = new DeviceMetaModel();
        deviceMetaModel.setDeviceModel("nokia");
        deviceMetaModel.setNumOfRackPositionOccupied(2);

        //case-1
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(null);
        Exception orderIdException = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.createDevice(orderId, rack, device);
        });
        Assertions.assertEquals("The order " + orderId + " is not found", orderIdException.getMessage());

        //case-2
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel())).thenReturn(null);
        Exception devicemetamodelexception = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.createDevice(orderId, rack, device);
        });
        Assertions.assertEquals("Device Meta-model doesn't exist for the device type: " + device.getDeviceModel(), devicemetamodelexception.getMessage());

        //case-3
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel())).thenReturn(deviceMetaModel);
        Mockito.when(rackRepository.findByName(rack)).thenReturn(null);
        Exception rackdoesnotexistexception = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.createDevice(orderId, rack, device);
        });
        Assertions.assertEquals("Given Rack doesn't exist", rackdoesnotexistexception.getMessage());

        //case-4
        Rack rack1 = new Rack();
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel())).thenReturn(deviceMetaModel);

        ArrayList<Integer> freePositions = new ArrayList<>();
        freePositions.add(2);
        freePositions.add(3);
//        freePositions.add(2);
        ArrayList<Integer> reservedPositions = new ArrayList<>();
        reservedPositions.add(4);
        reservedPositions.add(5);
        rack1.setName(rack);
        rack1.setFreePositions(freePositions);
        rack1.setReservedPositions(reservedPositions);

        Mockito.when(rackRepository.findByName(rack)).thenReturn(rack1);

        int numberOfPositionsOccupied = deviceMetaModel.getNumOfRackPositionOccupied();
        freePositions = rack1.getFreePositions(); //[2,3]
        reservedPositions = rack1.getReservedPositions();//[4,5]

        int startPosition = Integer.parseInt(device.getRackPosition());//2
        int endPosition = (startPosition + numberOfPositionsOccupied) - 1;//(2+2)-1 = 3

        Rack rack2 = new Rack();
        rack2.setFreePositions(freePositions);
        rack2.setReservedPositions(reservedPositions);
        ArrayList<Integer> updatedFreePositions = rack2.getFreePositions();//[2,3]
        ArrayList<Integer> updatedReservedPositions = rack2.getReservedPositions();//[4,5]

        updatedReservedPositions.add(startPosition);//[4,5,2]
        updatedFreePositions.remove(Integer.valueOf(startPosition));//[3]

        Mockito.when(deviceRepository.findByName(device.getName())).thenReturn(null);


        rack2.setReservedPositions(updatedReservedPositions);
        rack2.setFreePositions(updatedFreePositions);

        //case-5
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel())).thenReturn(deviceMetaModel);
        Rack rack4 = new Rack();
        ArrayList<Integer> freePositions2 = new ArrayList<>();
        freePositions2.add(2);
        freePositions2.add(3);
        freePositions2.add(2);
        ArrayList<Integer> reservedPositions2 = new ArrayList<>();
        reservedPositions2.add(4);
        reservedPositions2.add(5);
        rack4.setName(rack);
        rack4.setFreePositions(freePositions2);
        rack4.setReservedPositions(reservedPositions2);

        Mockito.when(rackRepository.findByName(rack)).thenReturn(rack4);

        int numberOfPositionsOccupied2 = deviceMetaModel.getNumOfRackPositionOccupied();
        freePositions2 = rack4.getFreePositions(); //[2,3]
        reservedPositions2 = rack4.getReservedPositions();//[4,5]

        int startPosition2 = Integer.parseInt(device.getRackPosition());//2
        int endPosition2 = (startPosition2 + numberOfPositionsOccupied2) - 1;//(2+2)-1 = 3

        Rack rack3 = new Rack();
        rack3.setFreePositions(freePositions);
        rack3.setReservedPositions(reservedPositions);
        ArrayList<Integer> updatedFreePositions2 = rack3.getFreePositions();//[2,3]
        ArrayList<Integer> updatedReservedPositions2 = rack3.getReservedPositions();//[4,5]

        updatedReservedPositions2.add(startPosition);//[4,5,2]
        updatedFreePositions2.remove(Integer.valueOf(startPosition));//[3]

        Mockito.when(deviceRepository.findByName(device.getName())).thenReturn(null);


        rack3.setReservedPositions(updatedReservedPositions);
        rack3.setFreePositions(updatedFreePositions);

        Device existingdevice = new Device();
        existingdevice.setName(device.getName());
        Mockito.when(deviceRepository.findByName(device.getName())).thenReturn(existingdevice);
        Exception devicewithsamenameexception = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.createDevice(orderId, rack, device);
        });
        Assertions.assertEquals("Device with same name already exist, existingDeviceDetails: " + existingdevice, devicewithsamenameexception.getMessage());
    }

    @Test
    void updateDeviceOnRackSuccess() {
        String rack = "testrack";

        Device device = new Device();
        device.setName("testdevice");
        device.setDeviceModel("nokia");
        device.setRackPosition("3");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        DeviceMetaModel deviceMetaModel = new DeviceMetaModel();
        deviceMetaModel.setDeviceModel("nokia");
        deviceMetaModel.setNumOfRackPositionOccupied(2);

        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel())).thenReturn(deviceMetaModel);

        Rack rack1 = new Rack();

        ArrayList<Integer> freePositions = new ArrayList<>();
        freePositions.add(3);
        freePositions.add(4);

        ArrayList<Integer> reservedPositions = new ArrayList<>();
        reservedPositions.add(5);
        reservedPositions.add(6);


        rack1.setName(rack);
        rack1.setReservedPositions(reservedPositions);
        rack1.setFreePositions(freePositions);

        Device existingdevice = new Device();
        existingdevice.setName(device.getName());
        existingdevice.setRackPosition("5");

        Mockito.when(rackRepository.findByName(rack)).thenReturn(rack1);
        Mockito.when(deviceRepository.getDeviceInRack(rack, device.getName())).thenReturn(existingdevice);

        int numOfRackPositionOccupied = deviceMetaModel.getNumOfRackPositionOccupied();
        freePositions = rack1.getFreePositions();
        reservedPositions = rack1.getReservedPositions();
        ArrayList<Integer> updatedFreePositions = rack1.getFreePositions();//[3,4]
        ArrayList<Integer> updatedReservedPositions = rack1.getReservedPositions();//[5,6]
        int prevStartPosition = Integer.parseInt(existingdevice.getRackPosition());//[5]
        int prevEndPosition = (prevStartPosition + numOfRackPositionOccupied) - 1;//[(5+2) -1 = 6]
        int presentStartPosition = Integer.parseInt(device.getRackPosition());//[3]
        int presentEndPosition = (presentStartPosition + numOfRackPositionOccupied) - 1;//[(3+2) -1 = 4]

        Collections.sort(updatedReservedPositions);
        Collections.sort(updatedFreePositions);

        rack1.setReservedPositions(updatedReservedPositions);
        rack1.setFreePositions(updatedFreePositions);

        doNothing().when(deviceRepository).updateDeviceOnRack(rack, device.getName(), device.getDeviceModel(),
                device.getLocation(), device.getOrganisation(), device.getCustomer(), device.getManagementIp(),
                device.getRackPosition(), device.getOperationalState(), device.getAdministrativeState(),
                device.getUsageState(), device.getSerialNumber(), deviceMetaModel.getShelvesContained(), orderId);
        JSONObject response = deviceInstanceRestApis.updateDevice(orderId, rack, device);
        Assertions.assertEquals("Success", response.get("status"));
    }

    @Test
    void updateDeviceOnRackFailure() {
        String rack = "testrack";

        Device device = new Device();
        device.setName("testdevice");
        device.setDeviceModel("nokia");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);

        DeviceMetaModel deviceMetaModel = new DeviceMetaModel();
        deviceMetaModel.setDeviceModel("nokia");

        //case-1
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(null);
        Exception orderIdException = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateDevice(orderId, rack, device);
        });
        Assertions.assertEquals("The order " + orderId + " is not found", orderIdException.getMessage());

        //case-2
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel())).thenReturn(null);
        Exception devicemetamodelexception = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateDevice(orderId, rack, device);
        });
        Assertions.assertEquals("Device Meta-model doesn't exist for the device type: " + device.getDeviceModel(), devicemetamodelexception.getMessage());

        //case-3
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel())).thenReturn(deviceMetaModel);
        Mockito.when(rackRepository.findByName(rack)).thenReturn(null);
        Exception buildingdoesnotexistexception = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateDevice(orderId, rack, device);
        });
        Assertions.assertEquals("Given Rack doesn't exist", buildingdoesnotexistexception.getMessage());

        //case-4
        Rack rack1 = new Rack();
        rack1.setName(rack);

        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel())).thenReturn(deviceMetaModel);
        Mockito.when(rackRepository.findByName(rack)).thenReturn(rack1);
        Mockito.when(deviceRepository.getDeviceInRack(rack, device.getName())).thenReturn(null);
        Exception devicewithsamenameexception = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateDevice(orderId, rack, device);
        });
        Assertions.assertEquals("Device with the given name doesn't exist on the rack", devicewithsamenameexception.getMessage());
    }

    @Test
    void getDeviceByIdSuccess() {
        Long id = Long.valueOf(65);
        Device device = new Device();
        device.setHref("http://localhost");

        Mockito.when(deviceRepository.findDevicesById(id)).thenReturn(device);
        Device actualdevice = deviceInstanceRestApis.getDeviceById(id);
        Assertions.assertEquals(device, actualdevice);
    }

    @Test
    void getDeviceByIdFailure() {
        Long id = Long.valueOf(65);
        Mockito.when(deviceRepository.findDevicesById(id)).thenThrow(new RuntimeException("cannot find a device by the given Id"));
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.getDeviceById(id);
        });
        assertTrue(e.getMessage().contains("cannot find a device by the given Id"));
    }

    @Test
    void getCardByIdSuccess() {
        Long id = Long.valueOf(65);
        Card card = new Card();
        card.setHref("http://loaclhost");

        Mockito.when(cardRepository.findCardById(id)).thenReturn(card);
        Card actualcard = deviceInstanceRestApis.getCardById(id);
        Assertions.assertEquals(card, actualcard);
    }

    @Test
    void getCardIdFailure() {
        Long id = Long.valueOf(65);
        Mockito.when(cardRepository.findCardById(id)).thenThrow(new RuntimeException("cannot find a card by the given Id"));
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.getCardById(id);
        });
        assertTrue(e.getMessage().contains("cannot find a card by the given Id"));
    }

    @Test
    void getCardSlotByIdSuccess() {
        Long id = Long.valueOf(65);
        CardSlot cardSlot = new CardSlot();
        cardSlot.setHref("http://localhost");

        ArrayList<CardSlot> cardSlots = new ArrayList<>();
        cardSlots.add(cardSlot);

        Mockito.when(cardSlotRepository.findCardSlotById(id)).thenReturn(cardSlot);
        ArrayList<CardSlot> actualcardslot = deviceInstanceRestApis.getCardSlotById(id);
        Assertions.assertEquals(cardSlots, actualcardslot);
    }

    @Test
    void getCardSlotByIdFailure() {
        Long id = Long.valueOf(65);
        Mockito.when(cardSlotRepository.findCardSlotById(id)).thenThrow(new RuntimeException("cannot find cardSlot by the given Id"));
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.getCardSlotById(id);
        });
        assertTrue(e.getMessage().contains("cannot find cardSlot by the given Id"));
    }

    @Test
    void getCardSlotByName() {
        String cardslotname = "testcardslot";

        CardSlot cardSlot = new CardSlot();
        cardSlot.setHref("http://localhost");

        ArrayList<CardSlot> cardSlots = new ArrayList<>();
        cardSlots.add(cardSlot);
        Mockito.when(cardSlotRepository.getByName(cardslotname)).thenReturn(cardSlot);

        ArrayList<CardSlot> actualcardslot = deviceInstanceRestApis.getCardSlotByName(cardslotname);
        Assertions.assertEquals(cardSlots, actualcardslot);
    }

    @Test
    void getCardSlotByNameFailure() {
        String cardslotname = "testcardslot";

        Mockito.when(cardSlotRepository.getByName(cardslotname)).thenThrow(new RuntimeException("cannot find cardSlot by the given name"));

        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.getCardSlotByName(cardslotname);
        });
        assertTrue(e.getMessage().contains("cannot find cardSlot by the given name"));
    }

    @Test
    void getCardSlotOnCard() {
        String cardName = "testcard";
        ArrayList<CardSlot> cardSlots = new ArrayList<>();
        Mockito.when(cardSlotRepository.getCardSlotsOnACard(cardName)).thenReturn(cardSlots);
        ArrayList<CardSlot> actualcardslot = deviceInstanceRestApis.getCardSlotOnCard(cardName);
        Assertions.assertEquals(cardSlots, actualcardslot);
    }

    @Test
    void getCardSlotCardFailure() {
        String cardName = "testcard";
        Mockito.when(cardSlotRepository.getCardSlotsOnACard(cardName)).thenThrow(new RuntimeException("cannot find cardSlot on the given card"));
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.getCardSlotOnCard(cardName);
        });
        assertTrue(e.getMessage().contains("cannot find cardSlot on the given card"));
    }

    @Test
    void getPluggableByIdSuccess() {
        Long id = Long.valueOf(75);

        Pluggable pluggable = new Pluggable();
        pluggable.setHref("http://localhost");
        Mockito.when(pluggableRepository.findPluggableById(id)).thenReturn(pluggable);
        Pluggable actualpluggable = deviceInstanceRestApis.getPluggableById(id);
        Assertions.assertEquals(pluggable, actualpluggable);
    }

    @Test
    void getPluggableByIdFailure() {
        Long id = Long.valueOf(75);

        Mockito.when(pluggableRepository.findPluggableById(id)).thenThrow(new RuntimeException("cannot find pluggable by the given id"));
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.getPluggableById(id);
        });
        assertTrue(e.getMessage().contains("cannot find pluggable by the given id"));
    }

    @Test
    void getPortById() {
        Long id = Long.valueOf(75);
        Port port = new Port();
        port.setHref("http:/localhost");
        Mockito.when(portRepository.findPortById(id)).thenReturn(port);
        Port actualport = deviceInstanceRestApis.getPortById(id);
        Assertions.assertEquals(port, actualport);
    }

    @Test
    void getPortByIdFailure() {
        Long id = Long.valueOf(75);
        Mockito.when(portRepository.findPortById(id)).thenThrow(new RuntimeException("cannot find port by the given id"));
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.getPortById(id);
        });
        assertTrue(e.getMessage().contains("cannot find port by the given id"));
    }

    @Test
    void getPluggableOnCardSlotSuccess() {
        String cardslotname = "testcardslot";
        Pluggable pluggable = new Pluggable();
        Mockito.when(pluggableRepository.getPluggableOnCardSlot(cardslotname)).thenReturn(pluggable);
        Pluggable actual = deviceInstanceRestApis.getPluggableOnCardByCardSlot(cardslotname);
        Assertions.assertEquals(pluggable, actual);
    }

    @Test
    void getPluggableOnCardSlotFailure() {
        String cardslotname = "testcardslot";
        Mockito.when(pluggableRepository.getPluggableOnCardSlot(cardslotname)).thenThrow(new RuntimeException("cannot find pluggable by the given cardSlot"));
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.getPluggableOnCardByCardSlot(cardslotname);
        });
        assertTrue(e.getMessage().contains("cannot find pluggable by the given cardSlot"));
    }

    @Test
    void getPortOnCardSlotSuccess() {
        String cardslotname = "testcardslot";
        Port port = new Port();
        Mockito.when(portRepository.getPortOnCardSlot(cardslotname)).thenReturn(port);
        Port actual = deviceInstanceRestApis.getPortOnCardByCardSlot(cardslotname);
        Assertions.assertEquals(port, actual);
    }

    @Test
    void getPortOnCardSlotFailure() {
        String cardslotname = "testcardslot";
        Mockito.when(portRepository.getPortOnCardSlot(cardslotname)).thenThrow(new RuntimeException("cannot find port by the given cardSlot"));
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.getPortOnCardByCardSlot(cardslotname);
        });
        assertTrue(e.getMessage().contains("cannot find port by the given cardSlot"));
    }

    @Test
    void createCardSuccess() {
        String device = "testdevice";
        Card card = new Card();
        card.setName("testcard");
        card.setShelfPosition(1);
        card.setSlotPosition(2);
        card.setVendor("testvendor");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        //case-1 if the device exist
        Device existingdevice = new Device();
        existingdevice.setDeviceModel("nokia");
        Mockito.when(deviceRepository.findByName(device)).thenReturn(existingdevice);

        //case-2 if the device model exist
        DeviceMetaModel deviceModel = new DeviceMetaModel();
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(existingdevice.getDeviceModel())).thenReturn(deviceModel);
        //if the card model exist on the device model
        card.setCardModel("harsh");
        ArrayList<String> cards = new ArrayList<>();
        cards.add("harsh");
        deviceModel.setAllowedCardList(cards);

        //case-3 if the shelf exist
        Shelf shelf = new Shelf();
        Mockito.when(shelfRepository.getShelf(device + "_shelf_" + card.getShelfPosition())).thenReturn(shelf);

        //case-4 if a card with given name exist
        Mockito.when(cardRepository.getCard(device, card.getName())).thenReturn(null);

        //case-5 if a card with given shelf, slot position exist
        Slot slot = new Slot();
        Card cardDetails = new Card();
        Mockito.when(cardRepository.getCardOnASlot(device + "/" +
                card.getShelfPosition().toString() + "/" + card.getSlotPosition())).thenReturn(null);
        Mockito.when(slotRepository.createSlot(device + "/" + card.getShelfPosition().toString() + "/" +
                        card.getSlotPosition(), card.getSlotPosition(), card.getOperationalState(),
                card.getAdministrativeState(), card.getUsageState(), shelf.getName())).thenReturn(slot);
        Mockito.when(cardRepository.createCard(card.getName(), card.getShelfPosition(),
                card.getSlotPosition(), card.getVendor(), card.getCardModel(), card.getCardPartNumber(),
                card.getOperationalState(), card.getAdministrativeState(), card.getUsageState(),
                slot.getName(), orderId)).thenReturn(cardDetails);

        JSONObject response = deviceInstanceRestApis.createCard(orderId, device, card);
        assertEquals("Success", response.get("status"));
        assertEquals(cardDetails, response.get("card"));
    }

    @Test
    void createCardFailure() {
        String device = "testdevice";
        Card card = new Card();
        card.setName("testcard");
        card.setShelfPosition(1);
        card.setSlotPosition(2);

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);

        //case-0 if vendor is null
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.createCard(orderId, device, card);
        });
        assertEquals("Card vendor cannot be empty", exception.getMessage());

        card.setVendor("nokia");

        //case-1
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(null);
        Exception orderIdException = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.createCard(orderId, device, card);
        });
        Assertions.assertEquals("The order " + orderId + " is not found", orderIdException.getMessage());

        //case-1
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceRepository.findByName(device)).thenReturn(null);
        Exception devicedomotexist = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.createCard(orderId, device, card);
        });
        assertEquals("Device with the given name doesn't exist", devicedomotexist.getMessage());

        //case-2
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Device existingdevice = new Device();
        existingdevice.setDeviceModel("nokia");
        Mockito.when(deviceRepository.findByName(device)).thenReturn(existingdevice);
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(existingdevice.getDeviceModel())).thenReturn(null);
        Exception devicemodeldomotexist = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.createCard(orderId, device, card);
        });
        assertEquals("Device Meta-model doesn't exist for the device type: " + existingdevice.getDeviceModel(), devicemodeldomotexist.getMessage());

        //case-3
        card.setCardModel("harsh");
        ArrayList<String> cards = new ArrayList<>();
        DeviceMetaModel deviceModel = new DeviceMetaModel();
        deviceModel.setAllowedCardList(cards);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceRepository.findByName(device)).thenReturn(existingdevice);
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(existingdevice.getDeviceModel())).thenReturn(deviceModel);
        Exception cardmodeldonotexist = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.createCard(orderId, device, card);
        });
        assertEquals("Given card model: " + card.getCardModel() +
                " is not compatible with the device model, AllowedCardList on the device: " +
                deviceModel.getAllowedCardList(), cardmodeldonotexist.getMessage());

        //case-4Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        Mockito.when(deviceRepository.findByName(device)).thenReturn(existingdevice);
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(existingdevice.getDeviceModel())).thenReturn(deviceModel);
        cards.add("harsh");
        Mockito.when(shelfRepository.getShelf(device + "_shelf_" + card.getShelfPosition())).thenReturn(null);
        Exception shelftexist = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.createCard(orderId, device, card);
        });
        assertEquals("Given shelf doesn't exist", shelftexist.getMessage());

        //case-5
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceRepository.findByName(device)).thenReturn(existingdevice);
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(existingdevice.getDeviceModel())).thenReturn(deviceModel);
        Shelf shelf = new Shelf();
        Mockito.when(shelfRepository.getShelf(device + "_shelf_" + card.getShelfPosition())).thenReturn(shelf);
        Card existingcard = new Card();
        Mockito.when(cardRepository.getCard(device, card.getName())).thenReturn(existingcard);
        Exception cardnameexist = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.createCard(orderId, device, card);
        });
        assertEquals("Card with given name already exist, existingCardDetails: " +
                existingcard, cardnameexist.getMessage());

        //case-6
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceRepository.findByName(device)).thenReturn(existingdevice);
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(existingdevice.getDeviceModel())).thenReturn(deviceModel);
        Mockito.when(shelfRepository.getShelf(device + "_shelf_" + card.getShelfPosition())).thenReturn(shelf);
        Mockito.when(cardRepository.getCard(device, card.getName())).thenReturn(null);
        Card cardslot = new Card();
        Mockito.when(cardRepository.getCardOnASlot(device + "/" +
                card.getShelfPosition().toString() + "/" + card.getSlotPosition())).thenReturn(cardslot);
        Exception cardShelfexist = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.createCard(orderId, device, card);
        });
        assertEquals("A card already exist on the given shelf, slot position. " +
                "Existing card details: " + cardslot, cardShelfexist.getMessage());
    }

    @Test
    void updateCardSuccess() {
        String device = "testdevice";
        Card card = new Card();
        card.setName("testcard");
        card.setShelfPosition(1);
        card.setSlotPosition(2);

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        //case-1 if the device exist
        Device existingdevice = new Device();
        existingdevice.setDeviceModel("nokia");
        Mockito.when(deviceRepository.findByName(device)).thenReturn(existingdevice);

        //case-2 if the device model exist
        DeviceMetaModel deviceModel = new DeviceMetaModel();
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(existingdevice.getDeviceModel())).thenReturn(deviceModel);
        //if the card model exist on the device model
        card.setCardModel("harsh");
        ArrayList<String> cards = new ArrayList<>();
        cards.add("harsh");
        deviceModel.setAllowedCardList(cards);

        //case-3 if the shelf exist
        Shelf shelf = new Shelf();
        Mockito.when(shelfRepository.getShelf(device.toLowerCase() + "_shelf_" + card.getShelfPosition())).thenReturn(shelf);

        //case-4 if a card with given name exist
        Card cardDetails = new Card();
        Mockito.when(cardRepository.getCard(device, card.getName())).thenReturn(cardDetails);

        //case-5
        Mockito.when(cardRepository.getCardOnASlot(device + "/" +
                card.getShelfPosition().toString() + "/" + card.getSlotPosition())).thenReturn(null);

        //case-6
        cardDetails.setSlotPosition(2);
        Slot slot = new Slot();
        Mockito.when(slotRepository.createSlot(device + "/" + card.getShelfPosition().toString() + "/" +
                        card.getSlotPosition(),
                card.getSlotPosition(), card.getOperationalState(), card.getAdministrativeState(),
                card.getUsageState(), shelf.getName())).thenReturn(slot);
        Card updateCard = new Card();
        slot.setName("testslot");
        Mockito.when(cardRepository.updateCard(card.getName().toLowerCase(), card.getShelfPosition(),
                card.getSlotPosition(), card.getVendor(), card.getCardModel(), card.getCardPartNumber(),
                card.getOperationalState(), card.getAdministrativeState(), card.getUsageState(),
                slot.getName(), orderId)).thenReturn(updateCard);

        JSONObject response = deviceInstanceRestApis.updateCard(orderId, device, card);
        assertEquals("Success", response.get("status"));
        assertEquals(updateCard, response.get("card"));
    }

    @Test
    void updateCardFailure() {
        String device = "testdevice";
        Card card = new Card();
        card.setName("testcard");
        card.setShelfPosition(1);
        card.setSlotPosition(2);

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);

        //case-1
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(null);
        Exception orderIdException = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateCard(orderId, device, card);
        });
        Assertions.assertEquals("The order " + orderId + " is not found", orderIdException.getMessage());

        //case-2
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceRepository.findByName(device)).thenReturn(null);
        Exception devicedomotexist = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateCard(orderId, device, card);
        });
        assertEquals("Device with the given name doesn't exist", devicedomotexist.getMessage());

        //case-2
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Device existingdevice = new Device();
        existingdevice.setDeviceModel("nokia");
        Mockito.when(deviceRepository.findByName(device)).thenReturn(existingdevice);
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(existingdevice.getDeviceModel())).thenReturn(null);
        Exception devicemodeldomotexist = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateCard(orderId, device, card);
        });
        assertEquals("Device Meta-model doesn't exist for the device type: " + existingdevice.getDeviceModel(), devicemodeldomotexist.getMessage());

        //case-3
        card.setCardModel("harsh");
        ArrayList<String> cards = new ArrayList<>();
        DeviceMetaModel deviceModel = new DeviceMetaModel();
        deviceModel.setAllowedCardList(cards);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceRepository.findByName(device)).thenReturn(existingdevice);
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(existingdevice.getDeviceModel())).thenReturn(deviceModel);
        Exception cardmodeldonotexist = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateCard(orderId, device, card);
        });
        assertEquals("Given card model: " + card.getCardModel() +
                " is not compatible with the device model, AllowedCardList on the device: " +
                deviceModel.getAllowedCardList(), cardmodeldonotexist.getMessage());

        //case-4
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceRepository.findByName(device)).thenReturn(existingdevice);
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(existingdevice.getDeviceModel())).thenReturn(deviceModel);
        cards.add("harsh");
        Mockito.when(shelfRepository.getShelf(device + "_Shelf_" + card.getShelfPosition())).thenReturn(null);
        Exception shelftexist = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateCard(orderId, device, card);
        });
        assertEquals("Given shelf doesn't exist", shelftexist.getMessage());

        //case-5
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceRepository.findByName(device)).thenReturn(existingdevice);
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(existingdevice.getDeviceModel())).thenReturn(deviceModel);
        Shelf shelf = new Shelf();
        Mockito.when(shelfRepository.getShelf(device + "_shelf_" + card.getShelfPosition())).thenReturn(shelf);
        Mockito.when(cardRepository.getCard(device, card.getName())).thenReturn(null);
        Exception cardexist = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateCard(orderId, device, card);
        });
        assertEquals("Given card doesn't exist", cardexist.getMessage());

        //case-6
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceRepository.findByName(device)).thenReturn(existingdevice);
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(existingdevice.getDeviceModel())).thenReturn(deviceModel);
        Mockito.when(shelfRepository.getShelf(device + "_Shelf_" + card.getShelfPosition())).thenReturn(shelf);
        Card cardDetails = new Card();
        Mockito.when(cardRepository.getCard(device, card.getName())).thenReturn(cardDetails);
        Card cardOnSlot = new Card();
        Mockito.when(cardRepository.getCardOnASlot(device + "/" +
                card.getShelfPosition().toString() + "/" + card.getSlotPosition())).thenReturn(cardOnSlot);
        cardOnSlot.setName("testcardslot");
        Exception cardslotexist = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateCard(orderId, device, card);
        });
        assertEquals("A card already exist on the given shelf, slot position. " +
                "Existing card details: " + cardOnSlot, cardslotexist.getMessage());

        //case-7
        cardDetails.setSlotPosition(1);
        doNothing().when(slotRepository).deleteSlot(any());
    }

    @Test
    void getCardOnSlot() {
        String slotName = "testslot";
        Card card = new Card();
        ArrayList<Card> cards = new ArrayList<>();
        Mockito.when(cardRepository.getCardOnASlot(slotName)).thenReturn(card);
        cards.add(card);
        ArrayList<Card> actualCard = deviceInstanceRestApis.getCardOnSlot(slotName);
        assertEquals(cards, actualCard);
    }

    @Test
    void getCardOnSlotFailure() {
        String slotName = "testslot";
        Mockito.when(cardRepository.getCardOnASlot(slotName)).thenThrow(new RuntimeException("Cannot find cards on given slotName" + slotName));
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.getCardOnSlot(slotName);
        });
        assertEquals("Cannot find cards on given slotName" + slotName, e.getMessage());
    }


    @Test
    void getAllPortsOnCardTestFailure() {
        // Arrange
        String device = "testd";
        String cardName = "testDevice";
        String Name = cardName.toLowerCase();
        // Mock the port repository to throw an exception
        Mockito.when(portRepository.getAllPortsOnCard(device, Name))
                .thenThrow(new RuntimeException("Failed to fetch ports for Card: " + Name));
        // Act and Assert
        Exception e = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.getAllPortsOnCard(device, Name);
        });
        assertTrue(e.getMessage().contains("Failed to fetch ports for Card: " + Name));
    }

    @Test
    void getAllCardsOnDeviceTestSuccess() {
        // Arrange
        String deviceName = "testDevice";
        String Name = deviceName.toLowerCase();
        ArrayList<Card> cardsOnDevice = new ArrayList<>();
        cardsOnDevice.add(new Card());
        Mockito.when(cardRepository.getAllCardsOnDevice(Name)).thenReturn(cardsOnDevice);
        // Act
        ArrayList<Card> actualCardsOnDevice = deviceInstanceRestApis.getAllCardsOnDevice(Name);
        // Assert
        assertEquals(cardsOnDevice, actualCardsOnDevice);
    }

    @Test
    void getAllCardsOnDeviceTestFailure() {
        // Arrange
        String deviceName = "testDevice";
        String name = deviceName.toLowerCase();
        Mockito.when(cardRepository.getAllCardsOnDevice(name))
                .thenThrow(new RuntimeException("Failed to fetch CardsOnDevice: " + name));
        // Act and Assert
        ServiceException e = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.getAllCardsOnDevice(deviceName);
        });
        assertTrue(e.getMessage().contains("Failed to fetch CardsOnDevice: " + name));
    }


    @MockBean
    CityRepository cityRepository;
    @MockBean
    CountryRepository countryRepository;
    @MockBean
    StateRepository stateRepository;
    @MockBean
    PhysicalConnectionRepository physicalConnectionRepository;


    @Test
    void deleteCardTestSuccess() {
        String devicName = "testd";
        String Cardname = "test";
        Cardname = Cardname.toLowerCase();
        Card card = new Card();
        card.setName(Cardname);

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        ArrayList<PhysicalConnection> physicalConnection = new ArrayList<>();
        JSONObject response = new JSONObject();
        response.put("status", "Success");
        Mockito.when(cardRepository.getCard(devicName, Cardname)).thenReturn(card);
        Mockito.when(physicalConnectionRepository.validateCardForDeletion(Cardname)).thenReturn(physicalConnection);
        JSONObject receivedData = deviceInstanceRestApis.deleteCard(orderId, devicName, Cardname);
        assertEquals("Success", receivedData.get("status"));
    }

    @Test
    void deleteCardTestFailure() {
        // Test case 1: Order not found
        // Test case 1: Card not found
        String deviceName = "testd";
        String Cardname = "nonexistingcard";
        String name = Cardname.toLowerCase();

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(null);
        Exception OrderNotFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.deleteCard(orderId, deviceName, name);
        });
        assertTrue(OrderNotFoundException.getMessage().contains("The order " + orderId + " is not found"));

        // Test case 2: Card not found
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(cardRepository.getCard(name)).thenReturn(null);
        Mockito.when(cardRepository.getCard(deviceName, name)).thenReturn(null);
        Exception CardNotFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.deleteCard(orderId, deviceName, name);
        });
        assertTrue(CardNotFoundException.getMessage().contains("Card with the given name doesn't exist"));

        // Test case 3: Card being used in physical connections
        Card card = new Card();
        card.setName(name);
        ArrayList<PhysicalConnection> physicalConnection = new ArrayList<>();
        PhysicalConnection physicalConnection1 = new PhysicalConnection();
        physicalConnection1.setName("test");
        physicalConnection.add(physicalConnection1);
        Mockito.when(cardRepository.getCard(deviceName, name)).thenReturn(card);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(cardRepository.getCard(name)).thenReturn(card);
        Mockito.when(physicalConnectionRepository.validateCardForDeletion(name)).thenReturn(physicalConnection);
        Exception physicalconnectionsException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.deleteCard(orderId, deviceName, name);
        });
        assertTrue(physicalconnectionsException.getMessage().contains("Card with the given name cannot be deleted, " +
                "as it is being used in physical connections"));
    }

    @Test
    void deleteDeviceTestSuccess() {
        String Devicename = "test";
        Devicename = Devicename.toLowerCase();
        Device device = new Device();
        device.setName(Devicename);

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        ArrayList<PhysicalConnection> physicalConnection = new ArrayList<>();
        JSONObject response = new JSONObject();
        response.put("status", "Success");
        Mockito.when(deviceRepository.findByName(Devicename)).thenReturn(device);
        Mockito.when(physicalConnectionRepository.validateCardForDeletion(Devicename)).thenReturn(physicalConnection);
        JSONObject receivedData = deviceInstanceRestApis.deleteDevice(orderId, Devicename);
        assertEquals("Success", receivedData.get("status"));
    }

    @Test
    void deleteDeviceTestFailure() {
        // Test case 1: Order not found
        String Devicename = "nonexistingDevice";
        String name = Devicename.toLowerCase();

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(null);

        Exception OrderNotFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.deleteDevice(orderId, name);
        });
        assertTrue(OrderNotFoundException.getMessage().contains("The order " + orderId + " is not found"));

        // Test case 2: Device not found
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceRepository.findByName(name)).thenReturn(null);
        Exception DeviceNotFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.deleteDevice(orderId, name);
        });
        assertTrue(DeviceNotFoundException.getMessage().contains("Device with the given name doesn't exist"));

        // Test case 3: Device being used in physical connections
        Device device = new Device();
        device.setName(name);
        ArrayList<PhysicalConnection> physicalConnection = new ArrayList<>();
        PhysicalConnection physicalConnection1 = new PhysicalConnection();
        physicalConnection1.setName("test");
        physicalConnection.add(physicalConnection1);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceRepository.findByName(name)).thenReturn(device);
        Mockito.when(physicalConnectionRepository.validateDeviceForDeletion(name)).thenReturn(physicalConnection);
        Exception physicalconnectionsException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.deleteDevice(orderId, name);
        });
        assertTrue(physicalconnectionsException.getMessage().contains("Device with the given name cannot be deleted, " +
                "as it is being used in physical connections"));
    }

    @Test
    void deletePortTestSuccess() {
        String deviceName = "testd";
        String Portname = "test";
        Portname = Portname.toLowerCase();
        Port port = new Port();
        port.setName(Portname);

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        ArrayList<PhysicalConnection> physicalConnection = new ArrayList<>();
        JSONObject response = new JSONObject();
        response.put("status", "Success");
        Mockito.when(portRepository.getPort(deviceName, Portname)).thenReturn(port);
        Mockito.when(physicalConnectionRepository.validatePortForDeletion(Portname)).thenReturn(physicalConnection);
        JSONObject receivedData = deviceInstanceRestApis.deletePort(orderId, deviceName, Portname);
        assertEquals("Success", receivedData.get("status"));
    }

    @Test
    void deletePortTestFailure() {
        // Test case 1: Order not found
        // Test case 1: Port not found
        String deviceName = "testd";
        String portName = "nonexistingPort";
        String name = portName.toLowerCase();

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(null);

        Exception OrderNotFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.deletePort(orderId, deviceName, name);
        });
        assertTrue(OrderNotFoundException.getMessage().contains("The order " + orderId + " is not found"));

        // Test case 2: Port not found
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(portRepository.getPort(portName)).thenReturn(null);
        Mockito.when(portRepository.getPort(deviceName, portName)).thenReturn(null);
        ServiceException portNotFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.deletePort(orderId, deviceName, name);
        });
        assertTrue(portNotFoundException.getMessage().contains("Port with the given name doesn't exist"));

        // Test case 3: Port being used in physical connections
        Port port = new Port();
        String name1 = portName.toLowerCase();
        port.setName(name1);
        ArrayList<PhysicalConnection> physicalConnections = new ArrayList<>();
        physicalConnections.add(new PhysicalConnection());
        Mockito.when(portRepository.getPort(deviceName, name1)).thenReturn(port);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(portRepository.getPort(name1)).thenReturn(port);
        Mockito.when(physicalConnectionRepository.validatePortForDeletion(port.getName())).thenReturn(physicalConnections);
        ServiceException physicalConnectionsException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.deletePort(orderId, deviceName, name1);
        });
        assertTrue(physicalConnectionsException.getMessage().contains("Port can't be deleted as it is used in , " +
                "physicalConnections: "));
    }

    @Test
    void deletePluggableTestSuccess() {
        String Pluggablename = "test";
        Pluggablename = Pluggablename.toLowerCase();
        Pluggable pluggable = new Pluggable();
        pluggable.setName(Pluggablename);

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        ArrayList<PhysicalConnection> physicalConnection = new ArrayList<>();
        JSONObject response = new JSONObject();
        response.put("status", "Success");
        Mockito.when(pluggableRepository.getPluggable(Pluggablename)).thenReturn(pluggable);
        Mockito.when(physicalConnectionRepository.validatePluggableForDeletion(Pluggablename)).thenReturn(physicalConnection);
        JSONObject receivedData = deviceInstanceRestApis.deletePluggable(orderId, Pluggablename);
        assertEquals("Success", receivedData.get("status"));
    }

    @Test
    void deletePluggableTestFailure() {
        // Test case 1: Order not found
        String Pluggablename = "nonexistingPlugable";
        String name = Pluggablename.toLowerCase();

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(null);

        Exception OrderNotFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.deleteDevice(orderId, name);
        });
        assertTrue(OrderNotFoundException.getMessage().contains("The order " + orderId + " is not found"));

        // Test case 1: Plugable not found
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(pluggableRepository.getPluggable(Pluggablename)).thenReturn(null);
        ServiceException PluggableNotFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.deletePluggable(orderId, name);
        });
        assertTrue(PluggableNotFoundException.getMessage().contains("Pluggable with given name doesn't exist"));
        // Test case 2: Plugable being used in physical connections
        Pluggable pluggable = new Pluggable();
        String name1 = Pluggablename.toLowerCase();
        pluggable.setName(name1);
        ArrayList<PhysicalConnection> physicalConnections = new ArrayList<>();
        physicalConnections.add(new PhysicalConnection());
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(pluggableRepository.getPluggable(name1)).thenReturn(pluggable);
        Mockito.when(physicalConnectionRepository.validatePluggableForDeletion(name1)).thenReturn(physicalConnections);
        ServiceException physicalConnectionsException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.deletePluggable(orderId, name1);
        });
        assertTrue(physicalConnectionsException.getMessage().contains("Pluggable is connected to a physical connection, please delete the" +
                " connection before deleting the pluggable"));
    }


    @Test
    void updateLogicalPortOnPhysicalPortOnDeviceTestSuccess() {
        // Arrange
        String deviceName = "sampledevice";
        LogicalPort port = new LogicalPort();
        port.setName("sampleport");
        port.setPositionOnDevice(2);
        // Set other properties of the logicalPort object as needed
        port.setPortType("Ethernet");
        port.setOperationalState("Up");
        port.setAdministrativeState("Enabled");
        port.setUsageState("In Use");
        port.setPortSpeed("1Gbps");
        port.setCapacity(1000);

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        Device device = new Device();
        Port portOnDevice = new Port();
        // Set the necessary properties of portOnDevice
        portOnDevice.setName("sampleport");
        portOnDevice.setPositionOnDevice(2);
        portOnDevice.setPortType("Ethernet");
        portOnDevice.setOperationalState("Down");
        portOnDevice.setAdministrativeState("Disabled");
        portOnDevice.setUsageState("Available");
        portOnDevice.setPortSpeed("1Gbps");
        portOnDevice.setCapacity(1000);

        LogicalPort existingLogicalPort = new LogicalPort();
        // Set the necessary properties of existingLogicalPort
        existingLogicalPort.setName(portOnDevice.getName());
        existingLogicalPort.setPositionOnDevice(portOnDevice.getPositionOnDevice());
        existingLogicalPort.setPortType(portOnDevice.getPortType());
        existingLogicalPort.setOperationalState(portOnDevice.getOperationalState());
        existingLogicalPort.setAdministrativeState(portOnDevice.getAdministrativeState());
        existingLogicalPort.setUsageState(portOnDevice.getUsageState());
        existingLogicalPort.setPortSpeed(portOnDevice.getPortSpeed());
        existingLogicalPort.setCapacity(portOnDevice.getCapacity());

        LogicalPort updatedLogicalPort = new LogicalPort();
        // Set the necessary properties of updatedLogicalPort
        updatedLogicalPort.setName("sampleport");
        updatedLogicalPort.setPositionOnDevice(2);
        updatedLogicalPort.setPortType("Ethernet");
        updatedLogicalPort.setOperationalState("Up");
        updatedLogicalPort.setAdministrativeState("Enabled");
        updatedLogicalPort.setUsageState("In Use");
        updatedLogicalPort.setPortSpeed("1Gbps");
        updatedLogicalPort.setCapacity(1000);

        Mockito.when(deviceRepository.getByName(deviceName)).thenReturn(device);
        Mockito.when(portRepository.getPortOnDeviceByNumber(deviceName, port.getPositionOnDevice())).thenReturn(portOnDevice);
        Mockito.when(logicalPortRepository.getLogicalPortOnDevice(deviceName, port.getPositionOnDevice(), port.getPositionOnPort())).thenReturn(existingLogicalPort);
        Mockito.when(logicalPortRepository.updateLogicalPortOnDevice(port.getName().toLowerCase(),
                port.getPositionOnDevice(), port.getPortType(), port.getOperationalState(),
                port.getAdministrativeState(), port.getUsageState(),
                port.getPortSpeed(), port.getCapacity(), deviceName, port.getPositionOnPort(), orderId)).thenReturn(updatedLogicalPort);

        // Act
        JSONObject receivedData = deviceInstanceRestApis.updateLogicalPortOnPhysicalPortOnDevice(orderId, deviceName, port);

        // Assert
        assertEquals("LogicalPort successfully updated!", receivedData.get("status"));
        assertEquals(updatedLogicalPort, receivedData.get("logicalPort"));
    }

    @Test
    void updateLogicalPortOnPhysicalPortOnDevicetestOrderNotFoundException() {
        // Test case: OrderNotFoundException
        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);

        String Devicename = "SampleDevice";
        String name = Devicename.toLowerCase();

        LogicalPort port = new LogicalPort();
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(null);
        ServiceException DeviceNotFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.updateLogicalPortOnPhysicalPortOnDevice(orderId, name, port);
        });
        assertTrue(DeviceNotFoundException.getMessage().contains("The order " + orderId + " is not found"));
    }

    @Test
    void updateLogicalPortOnPhysicalPortOnDevicetestDeviceNotFoundException() {
        // Test case: DeviceNotFoundException
        String Devicename = "SampleDevice";
        String name = Devicename.toLowerCase();
        Device device = new Device();
        LogicalPort port = new LogicalPort();

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        Mockito.when(deviceRepository.getByName(name)).thenReturn(null);
        ServiceException DeviceNotFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.updateLogicalPortOnPhysicalPortOnDevice(orderId, name, port);
        });
        assertTrue(DeviceNotFoundException.getMessage().contains("Device with the given name doesn't exist"));
    }

    @Test
    void updateLogicalPortOnPhysicalPortOnDevicetestportOnDeviceNotFoundException() {
        String Devicename = "SampleDevice";
        String name = Devicename.toLowerCase();
        Device device = new Device();
        device.setName(name);
        LogicalPort port = new LogicalPort();

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        Mockito.when(deviceRepository.getByName(name)).thenReturn(device);
        Mockito.when(portRepository.getPortOnDeviceByNumber(name, port.getPositionOnDevice())).thenReturn(null);
        ServiceException DeviceNotFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.updateLogicalPortOnPhysicalPortOnDevice(orderId, name, port);
        });
        assertTrue(DeviceNotFoundException.getMessage().contains("No port found on the device " + name +
                " with given portPositon " +
                port.getPositionOnDevice() +
                " with portName: " + port.getName()));
    }

    @Test
    void createLogicalPortOnPhysicalPortOnDevicetestSuccess() {
        String Devicename = "SampleDevice";
        String name = Devicename.toLowerCase();
        Device device = new Device();
        device.setName(name);
        Port port = new Port();
        port.setName("sampleport");
        port.setPositionOnDevice(3);
        Port portOnDevice = new Port();
        portOnDevice.setPositionOnDevice(port.getPositionOnDevice());
        LogicalPort findExistingLogicalPort = new LogicalPort();
        LogicalPort logicalPort = new LogicalPort();
        logicalPort.setName(port.getName().toLowerCase());
        logicalPort.setPositionOnDevice(port.getPositionOnDevice());
        logicalPort.setPositionOnPort(1);
        logicalPort.setPortType(port.getPortType());
        logicalPort.setOperationalState(port.getOperationalState());
        logicalPort.setAdministrativeState(port.getAdministrativeState());
        logicalPort.setUsageState(port.getUsageState());
        logicalPort.setPortSpeed(port.getPortSpeed());
        logicalPort.setCapacity(port.getCapacity());

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        Mockito.when(deviceRepository.getByName(name)).thenReturn(device);
        Mockito.when(portRepository.getPortOnDeviceByNumber(name, port.getPositionOnDevice())).thenReturn(portOnDevice);
        Mockito.when(logicalPortRepository.getLogicalPortOnDevice(name, logicalPort.getPositionOnDevice(), logicalPort.getPositionOnPort())).thenReturn(null);
        Mockito.when(logicalPortRepository.createLogicalPortOnDevice(port.getName().toLowerCase(),
                port.getPositionOnDevice(), port.getPortType(), port.getOperationalState(),
                port.getAdministrativeState(), port.getUsageState(),
                port.getPortSpeed(), port.getCapacity(), name, logicalPort.getPositionOnPort(), orderId)).thenReturn(logicalPort);
        // Act
        JSONObject receivedData = deviceInstanceRestApis.createLogicalPortOnPhysicalPortOnDevice(orderId, name, logicalPort);

        // Assert
        assertEquals("LogicalPort successfully created!", receivedData.get("status"));
        assertEquals(logicalPort, receivedData.get("logicalPort"));

    }

    @Test
    void createLogicalPortOnPhysicalPortOnDevicetestOrdernotfound() {
        // Test data
        String deviceName = "SampleDevice";
        String name = deviceName.toLowerCase();
        LogicalPort logicalPort = new LogicalPort();

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(null);

        // Assert the ServiceException
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createLogicalPortOnPhysicalPortOnDevice(orderId, name, logicalPort);
        });

        // Assert the exception message
        assertTrue(exception.getMessage().contains("The order " + orderId + " is not found"));
    }

    @Test
    void createLogicalPortOnPhysicalPortOnDevicetestdevicenotfound() {
        // Test data
        String deviceName = "SampleDevice";
        String name = deviceName.toLowerCase();
        LogicalPort logicalPort = new LogicalPort();
        logicalPort.setName("sampleport");
        logicalPort.setPositionOnDevice(3);

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        // Mock repository responses
        Mockito.when(deviceRepository.getByName(name)).thenReturn(null); // Device does not exist
        // Assert the ServiceException
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createLogicalPortOnPhysicalPortOnDevice(orderId, name, logicalPort);
        });

        // Assert the exception message
        assertTrue(exception.getMessage().contains("Device with the given name doesn't exist"));
    }

    @Test
    void createLogicalPortOnPhysicalPortOnDeviceTestportOnDevicenotfound() {
        // Test data
        String deviceName = "SampleDevice";
        String name = deviceName.toLowerCase();
        Device device = new Device();
        device.setName(name);

        LogicalPort logicalPort = new LogicalPort();
        logicalPort.setName("sampleport");
        logicalPort.setPositionOnDevice(3);

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        // Mock repository responses
        Mockito.when(deviceRepository.getByName(name)).thenReturn(device);
        Mockito.when(portRepository.getPortOnDeviceByNumber(name, logicalPort.getPositionOnDevice())).thenReturn(null); // Port does not exist

        // Assert the ServiceException
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createLogicalPortOnPhysicalPortOnDevice(orderId, name, logicalPort);
        });
// Assert the exception message
        String expectedErrorMessage = "No port found on the device " + name + " with given portPositon " + logicalPort.getPositionOnDevice() + " with portName: " + logicalPort.getName();
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    void createLogicalPortOnPhysicalPortOnDevicetestFailure() {
        String deviceName = "SampleDevice";
        String name = deviceName.toLowerCase();
        Device device = new Device();
        device.setName(name);

        Port port = new Port();
        port.setName("sampleport");
        port.setPositionOnDevice(3);

        Port portOnDevice = new Port();
        portOnDevice.setPositionOnDevice(port.getPositionOnDevice());

        LogicalPort findExistingLogicalPort = new LogicalPort();
        LogicalPort logicalPort = new LogicalPort();
        logicalPort.setName(port.getName().toLowerCase());
        logicalPort.setPositionOnDevice(port.getPositionOnDevice());
        logicalPort.setPositionOnPort(1);
        logicalPort.setPortType(port.getPortType());
        logicalPort.setOperationalState(port.getOperationalState());
        logicalPort.setAdministrativeState(port.getAdministrativeState());
        logicalPort.setUsageState(port.getUsageState());
        logicalPort.setPortSpeed(port.getPortSpeed());
        logicalPort.setCapacity(port.getCapacity());

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        Mockito.when(deviceRepository.getByName(name)).thenReturn(device);
        Mockito.when(portRepository.getPortOnDeviceByNumber(name, port.getPositionOnDevice())).thenReturn(portOnDevice);
        Mockito.when(logicalPortRepository.getLogicalPortOnDevice(name, logicalPort.getPositionOnDevice(), logicalPort.getPositionOnPort())).thenReturn(logicalPort); // Existing logical port
        // Assert the ServiceException
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createLogicalPortOnPhysicalPortOnDevice(orderId, name, logicalPort);
        });

// Assert the exception message
        String expectedPartialErrorMessage = "Logical Port with given name is already existed onthe given port " + port.getName().toLowerCase();
        assertTrue(exception.getMessage().contains(expectedPartialErrorMessage));

    }

    @Test
    void updateLogicalPortOnPhysicalPortInCardSuccess() {
        String device = "test";
        String cardName = "Samplecard";
        cardName = cardName.toLowerCase();
        String portName = "test";
        portName = portName.toLowerCase();
        int positionCard = 1;
        String cardSlotName = cardName + "/" + positionCard;

        Card card = new Card();
        card.setName(cardName);
        Port port = new Port();
        port.setName(portName);
        port.setPositionOnCard(positionCard);
        List<LogicalPort> existingPortDetails = new ArrayList<>();
        LogicalPort logicalPort = new LogicalPort();
        logicalPort.setName(portName);
        logicalPort.setPositionOnCard(positionCard);
        existingPortDetails.add(logicalPort);

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        Mockito.when(cardRepository.getCard(cardName)).thenReturn(card);
        Mockito.when(cardRepository.getCard(device, cardName)).thenReturn(card);
        Mockito.when(logicalPortRepository.getLogicPortsOnCard(cardName, portName, logicalPort.getPositionOnPort())).thenReturn((ArrayList<LogicalPort>) existingPortDetails);
        Mockito.when(logicalPortRepository.updateLogicalPortOnCard(
                        port.getName(),
                        port.getPositionOnCard(),
                        port.getPortType(),
                        port.getOperationalState(),
                        port.getAdministrativeState(),
                        port.getUsageState(),
                        cardSlotName, orderId))
                .thenReturn(logicalPort);
        JSONObject response = deviceInstanceRestApis.updateLogicalPortOnPhysicalPortInCard(orderId, device, cardName, logicalPort);
        assertEquals("Success", response.get("status"));
        assertEquals(logicalPort, response.get("logicalPort"));
    }

    @Test
    void updateLogicalPortOnPhysicalPortInCardTestFailure() {
        // Test case 1: Order not found
        // Test case 1: Card with the given name doesn't exist
        String device = "test";
        String cardName = "nonexistingCard";
        String name = cardName.toLowerCase();

        LogicalPort logicalPort = new LogicalPort();
        logicalPort.setName("Sample");
        logicalPort.setPortType("Active");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(null);

        Exception OrderNotFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.updateLogicalPortOnPhysicalPortInCard(orderId, device, name, logicalPort);
        });
        assertTrue(OrderNotFoundException.getMessage().contains("The order " + orderId + " is not found"));

        // Test case 2: Card with the given name doesn't exist
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(cardRepository.getCard(cardName.toLowerCase())).thenReturn(null);
        Mockito.when(cardRepository.getCard(device, cardName.toLowerCase())).thenReturn(null);
        ServiceException cardNotFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.updateLogicalPortOnPhysicalPortInCard(orderId, device, name, logicalPort);
        });
        assertTrue(cardNotFoundException.getMessage().contains("Card with the given name doesn't exist"));

        // Test case 3: LogicalPort with the given name doesn't exist
        Card card = new Card();
        card.setName(name);
        Port port = new Port();
        String portName = "NonexistingPort";
        port.setName(portName.toLowerCase());
        Mockito.when(cardRepository.getCard(device, cardName.toLowerCase())).thenReturn(card);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(cardRepository.getCard(cardName.toLowerCase())).thenReturn(card);
        Mockito.when(logicalPortRepository.getLogicPortsOnCard(name, port.getName(), 1))
                .thenReturn(null); // Mock to return null instead of an empty list
        ServiceException logicalPortFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.updateLogicalPortOnPhysicalPortInCard(orderId, device, name, logicalPort);
        });
        assertTrue(logicalPortFoundException.getMessage().contains("LogicalPort with given name doesn't exist on the card,"));
    }

    @Test
    void createLogicalPortOnPhysicalPortInCardTestSuccess() {
        // Mock data
        String cardName = "Samplecard";
        cardName = cardName.toLowerCase();
        String portName = "test";
        int positionCard = 1;
        String cardSlotName = cardName + "/" + positionCard;

        Card existingCardDetails = new Card();
        existingCardDetails.setName(cardName.toLowerCase());

        ArrayList<LogicalPort> existingPortDetails = new ArrayList<>();

        Port portsOnCardSlot = new Port();

        Pluggable pluggableOnCardSlot = new Pluggable();

        LogicalPort createdLogicalPort = new LogicalPort();
        createdLogicalPort.setName(portName);
        createdLogicalPort.setPositionOnCard(positionCard);

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        // Mock behavior
        Mockito.when(cardRepository.getCard(cardName.toLowerCase())).thenReturn(existingCardDetails);
        Mockito.when(logicalPortRepository.getLogicPortsOnCard(cardName, portName, createdLogicalPort.getPositionOnPort())).thenReturn(existingPortDetails);
        Mockito.when(portRepository.getPortOnCardByNumber(cardSlotName, positionCard)).thenReturn(portsOnCardSlot);
        Mockito.when(pluggableRepository.getPluggableOnCardSlot(cardSlotName)).thenReturn(pluggableOnCardSlot);
        Mockito.when(logicalPortRepository.createLogicalPortOnCard(
                portName.toLowerCase(),
                positionCard,
                createdLogicalPort.getPortType(),
                createdLogicalPort.getOperationalState(),
                createdLogicalPort.getAdministrativeState(),
                createdLogicalPort.getUsageState(),
                cardSlotName, createdLogicalPort.getPositionOnPort(), orderId
        )).thenReturn(createdLogicalPort);

        // Invoke the method under test
        JSONObject response = deviceInstanceRestApis.createLogicalPortOnPhysicalPortInCard(orderId, cardName, createdLogicalPort);

        // Assertions
        assertEquals("Success", response.get("status"));
        assertEquals(createdLogicalPort, response.get("logicalPort"));
    }

    @Test
    void createLogicalPortOnPhysicalPortInCardOrderNotFoundException() {
        String Cardname = "SampleCard";
        LogicalPort port = new LogicalPort();
        port.setName("sample");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(null);

        // Assert the ServiceException
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createLogicalPortOnPhysicalPortInCard(orderId, Cardname.toLowerCase(), port);
        });
        // Assert the exception message
        String expectedErrorMessage = "The order " + orderId + " is not found";
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    void createLogicalPortOnPhysicalPortInCardexistingcardException() {
        String Cardname = "SampleCard";
        LogicalPort port = new LogicalPort();
        port.setName("sample");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);

        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(cardRepository.getCard(Cardname.toLowerCase())).thenReturn(null);
        // Assert the ServiceException
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createLogicalPortOnPhysicalPortInCard(orderId, Cardname.toLowerCase(), port);
        });
// Assert the exception message
        String expectedErrorMessage = "Card with the given name doesn't exist";
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    void createLogicalPortOnPhysicalPortInCardExistingPortDetailsException() {
        // Mock data
        String cardName = "SampleCard";
        String portName = "Sample";
        int positionCard = 1;
        String cardSlotName = cardName.toLowerCase() + "/" + positionCard;

        Card existingCardDetails = new Card();
        existingCardDetails.setName(cardName.toLowerCase());

        LogicalPort port = new LogicalPort();
        port.setName(portName);
        port.setPositionOnDevice(2);
        // Set other properties of the logicalPort object as needed
        port.setPortType("Ethernet");
        port.setOperationalState("Up");
        port.setAdministrativeState("Enabled");
        port.setUsageState("In Use");
        port.setPortSpeed("1Gbps");
        port.setCapacity(1000);
        port.setPositionOnCard(2);

        ArrayList<LogicalPort> existingPortDetails = new ArrayList<>();
        existingPortDetails.add(port);

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        // Mock behavior
        Mockito.when(cardRepository.getCard(cardName.toLowerCase())).thenReturn(existingCardDetails);
        Mockito.when(logicalPortRepository.getLogicPortsOnCard(cardName.toLowerCase(), portName.toLowerCase(), port.getPositionOnPort())).thenReturn(existingPortDetails);

        // Invoke the method and handle the exception
        try {
            JSONObject response = deviceInstanceRestApis.createLogicalPortOnPhysicalPortInCard(orderId, cardName.toLowerCase(), port);
            fail("Expected ServiceException was not thrown.");
        } catch (ServiceException e) {
            // Assert the exception message
            String expectedErrorMessage = "LogicalPort with given name already exist on the card," +
                    " existingPortDetails: " + existingPortDetails.toString();
            assertEquals(expectedErrorMessage, e.getMessage());
        }
    }

    @Test
    void createLogicalPortOnPhysicalPortInCardpluggableOnCardSlotException() {
        // Mock data
        String cardName = "SampleCard";
        String portName = "Sample";
        int positionCard = 1;
        String cardSlotName = cardName.toLowerCase() + "/" + positionCard;

        Card existingCardDetails = new Card();
        existingCardDetails.setName(cardName.toLowerCase());

        LogicalPort port = new LogicalPort();
        port.setName(portName);
        port.setPositionOnDevice(2);
        // Set other properties of the logicalPort object as needed
        port.setPortType("Ethernet");
        port.setOperationalState("Up");
        port.setAdministrativeState("Enabled");
        port.setUsageState("In Use");
        port.setPortSpeed("1Gbps");
        port.setCapacity(1000);
        port.setPositionOnCard(2);

        ArrayList<LogicalPort> existingPortDetails = new ArrayList<>();
        //existingPortDetails.add(port);

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        // Mock behavior
        Mockito.when(cardRepository.getCard(cardName.toLowerCase())).thenReturn(existingCardDetails);
        Mockito.when(logicalPortRepository.getLogicPortsOnCard(cardName, portName, 1)).thenReturn(existingPortDetails);
        Mockito.when(portRepository.getPortOnCardByNumber(cardSlotName, positionCard)).thenReturn(null);
        Mockito.when(pluggableRepository.getPluggableOnCardSlot(cardSlotName)).thenReturn(null);

        // Invoke the method and handle the exception
        try {
            JSONObject response = deviceInstanceRestApis.createLogicalPortOnPhysicalPortInCard(orderId, cardName.toLowerCase(), port);
            fail("Expected ServiceException was not thrown.");
        } catch (ServiceException e) {
            // Assert the exception message
            String expectedErrorMessage = "A pluggable / port doesn't exist on the card with the given" +
                    " port number";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
    }

    @Test
    public void testCreatePortOnDeviceSuccess() {
        // device
        String deviceName = "device-1";
        Device device = new Device();
        device.setName(deviceName.toLowerCase());
        Port port = new Port();
        port.setName("port-1");
        port.setPositionOnDevice(1);  // Provide a valid port number
        port.setPositionOnCard(0);
        port.setPortType("PortType");
        port.setOperationalState("OperationalState");
        port.setAdministrativeState("AdministrativeState");
        port.setUsageState("UsageState");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        // Mock the dependencies
        Mockito.when(deviceRepository.findByName(device.getName())).thenReturn(device);
        Mockito.when(portRepository.getPort(device.getName(), port.getName())).thenReturn(null);
        Mockito.when(pluggableRepository.getPluggableOnDeviceByNumber(device.getName(), port.getPositionOnDevice())).thenReturn(null);
        Mockito.when(portRepository.getPortOnDeviceByNumber(device.getName(), port.getPositionOnDevice())).thenReturn(null);
        Mockito.when(portRepository.createPortOnDevice(
                port.getName(),
                port.getPositionOnDevice(),
                port.getPortType(),
                port.getOperationalState(),
                port.getAdministrativeState(),
                port.getUsageState(),
                device.getName(), orderId
        )).thenReturn(port);
        // Act
        JSONObject response = deviceInstanceRestApis.createPortOnDevice(orderId, device.getName(), port);
        // Assert
        JSONObject expectedResponse = new JSONObject();
        expectedResponse.put("status", "Success");
        expectedResponse.put("port", port);
        assertEquals("Success", expectedResponse.get("status"));
        assertEquals(port, expectedResponse.get("port"));
    }

    @Test
    void createPortOnDeviceOrderNotFound() {
        String deviceName = "SampleDevice";
        Port port = new Port();
        port.setName("sampleport");
        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(null);

        // Invoke the method and handle the exception
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createPortOnDevice(orderId, deviceName, port);
        });

        // Assert the exception message
        String expectedErrorMessage = "The order " + orderId + " is not found";
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    void createPortOnDeviceexistingDevice() {
        String deviceName = "SampleDevice";
        Port port = new Port();
        port.setName("sampleport");
        port.setPortType("Active");
        port.setPositionOnDevice(1);
        port.setUsageState("20gb");
        port.setCapacity(500);
        port.setOperationalState("Up");
        port.setAdministrativeState("Admin");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        // Mock behavior
        Mockito.when(deviceRepository.findByName(deviceName.toLowerCase())).thenReturn(null);

        // Invoke the method and handle the exception
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createPortOnDevice(orderId, deviceName, port);
        });

        // Assert the exception message
        String expectedErrorMessage = "Device with the given name doesn't exist";
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    void portnumberisvalid() {
        String deviceName = "SampleDevice";
        Device device = new Device();
        device.setName(deviceName.toLowerCase());
        Port port = new Port();
        port.setName("sampleport");
        port.setPortType("Active");
        port.setPositionOnDevice(0);
        port.setPositionOnCard(2);
        port.setUsageState("20gb");
        port.setCapacity(500);
        port.setOperationalState("Up");
        port.setAdministrativeState("Admin");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        // Mock behavior
        Mockito.when(deviceRepository.findByName(deviceName.toLowerCase())).thenReturn(device);
        Mockito.when(portRepository.getPortOnDeviceByNumber(anyString(), Mockito.argThat(arg ->
                port.getPositionOnDevice() == 0 || port.getPositionOnCard() != 0))).thenAnswer(invocation -> {
            int positionOnDevice = invocation.getArgument(1);
            if (positionOnDevice == 0 || port.getPositionOnCard() != 0) {
                return null;
            } else {
                return port;
            }
        });
        // Invoke the method and handle the exception
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createPortOnDevice(orderId, deviceName.toLowerCase(), port);
        });

        // Assert the exception message
        String expectedErrorMessage = "Invalid input received, please provide valid port number on " +
                "PositionOnDevice";
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    void existingPortDetails() {
        String deviceName = "SampleDevice";
        Device device = new Device();
        device.setName(deviceName.toLowerCase());

        Port port = new Port();
        port.setName("sampleport");
        port.setPortType("Active");
        port.setPositionOnDevice(1);
        port.setPositionOnCard(0);
        port.setUsageState("20gb");
        port.setCapacity(500);
        port.setOperationalState("Up");
        port.setAdministrativeState("Admin");

        Port existingPortDetails = new Port();
        existingPortDetails.setName(port.getName());
        existingPortDetails.setPositionOnCard(port.getPositionOnCard());
        existingPortDetails.setPortType(port.getPortType());
        existingPortDetails.setPositionOnDevice(port.getPositionOnDevice());
        existingPortDetails.setUsageState(port.getUsageState());
        existingPortDetails.setCapacity(port.getCapacity());
        existingPortDetails.setOperationalState(port.getOperationalState());
        existingPortDetails.setAdministrativeState(port.getAdministrativeState());

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        // Mock behavior
        Mockito.when(deviceRepository.findByName(deviceName.toLowerCase())).thenReturn(device);
        Mockito.when(portRepository.getPort(deviceName.toLowerCase(), port.getName())).thenReturn(existingPortDetails);

        // Invoke the method and handle the exception
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createPortOnDevice(orderId, deviceName.toLowerCase(), port);
        });

        // Assert the exception message
        String expectedErrorMessage = "Port with given name already exist on the device, existingPortDetails: " +
                existingPortDetails.toString();
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    void createPortTestSuccess() {
        // Arrange
        String cardName = "testcard";
        cardName = cardName.toLowerCase();
        Card card = new Card();
        card.setName(cardName);
        String portName = "testPort";
        portName = portName.toLowerCase();
        Port port = new Port();
        port.setName(portName);
        port.setPositionOnCard(1);
        port.setPortType("Ethernet");
        port.setOperationalState("Up");
        port.setAdministrativeState("Enabled");
        port.setUsageState("Active");
        Card existingCardDetails = new Card();
        existingCardDetails.setName(cardName);
        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        //check given card is present or not
        Mockito.when(cardRepository.getCard(cardName)).thenReturn(existingCardDetails);
        Port existingPortDetails = null;
        //check given port is present or not
        Mockito.when(portRepository.getPortOnCard(cardName, portName)).thenReturn(existingPortDetails);
        Pluggable pluggableOnCardSlot = null;
        Mockito.when(pluggableRepository.getPluggableOnCardByNumber(cardName + "/" + port.getPositionOnCard().toString(), port.getPositionOnCard())).thenReturn(pluggableOnCardSlot);
        Port portsOnCardSlot = null;
        //check given portsOnCardSlot is present or not
        Mockito.when(portRepository.getPortOnCardByNumber(cardName + "/" + port.getPositionOnCard().toString(), port.getPositionOnCard())).thenReturn(portsOnCardSlot);
        CardSlot createdCardSlot = new CardSlot();
        Mockito.when(cardSlotRepository.createCardSlot(cardName + "/" + port.getPositionOnCard().toString(), port.getPositionOnCard(),
                port.getOperationalState(), port.getAdministrativeState(), port.getUsageState(), cardName)).thenReturn(createdCardSlot);
        Port createdPort = new Port();
        Mockito.when(portRepository.createPort(portName, port.getPositionOnCard(), port.getPortType(),
                port.getOperationalState(), port.getAdministrativeState(), port.getUsageState(),
                createdCardSlot.getName(), orderId)).thenReturn(createdPort);
        // Act
        JSONObject receivedData = deviceInstanceRestApis.createPort(orderId, cardName, port);
        // Assert
        assertEquals("Success", receivedData.get("status"));
        assertEquals(createdPort, receivedData.get("port"));
    }

    @Test
    void createPortTestFailure() {
        // Test case 1: Card with the given name doesn't exist
        String cardName = "nonexistingCard";

        Port port = new Port();
        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(null);
        Mockito.when(cardRepository.getCard(cardName.toLowerCase())).thenReturn(null);
        ServiceException OrderNotFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createPort(orderId, cardName, port);
        });
        assertTrue(OrderNotFoundException.getMessage().contains("The order " + orderId + " is not found"));
        // Test case 1: Card with the given name doesn't exist
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(cardRepository.getCard(cardName.toLowerCase())).thenReturn(null);
        ServiceException cardNotFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createPort(orderId, cardName, port);
        });
        assertTrue(cardNotFoundException.getMessage().contains("Card with the given name doesn't exist"));
        // Test case 2:  validating if the port already exist
        String cardname = cardName.toLowerCase();
        Card card = new Card();
        card.setName(cardname);
        String existingPortName = "ExistingPort";
        port.setName(existingPortName.toLowerCase());
        Port existingPort = new Port();
        existingPort.setName(existingPortName.toLowerCase());
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(cardRepository.getCard(cardname)).thenReturn(card);
        Mockito.when(portRepository.getPortOnCard(cardname, port.getName())).thenReturn(existingPort);
        ServiceException PortFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createPort(orderId, cardname, port);
        });
        assertTrue(PortFoundException.getMessage().contains("Port with given name already exist, existingPortDetails: " + existingPort.toString()));
        // Test case 3:  validating if the Plugable already exist
        int positionOnCard = 1;
        port.setPositionOnCard(positionOnCard);
        Pluggable existingPluggable = new Pluggable();
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(cardRepository.getCard(cardname)).thenReturn(card);
        Mockito.when(portRepository.getPortOnCard(cardname, port.getName())).thenReturn(null);
        Mockito.when(pluggableRepository.getPluggableOnCardByNumber(
                cardname + "/" + port.getPositionOnCard().toString(), port.getPositionOnCard())).thenReturn(existingPluggable);
        ServiceException PlugableFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createPort(orderId, cardname, port);
        });
        assertTrue(PlugableFoundException.getMessage().contains("A pluggable already exist on the card with the given port number, " + "existingPluggableDetails: " + existingPluggable));
        // Test case 4:  validating if the position is already consumed by a port or pluggable
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(cardRepository.getCard(cardname)).thenReturn(card);
        Mockito.when(portRepository.getPortOnCard(cardname, port.getName())).thenReturn(null);
        Mockito.when(pluggableRepository.getPluggableOnCardByNumber(
                cardname + "/" + port.getPositionOnCard().toString(), port.getPositionOnCard())).thenReturn(null);
        Mockito.when(portRepository.getPortOnCardByNumber(
                cardname + "/" + port.getPositionOnCard().toString(), port.getPositionOnCard())).thenReturn(existingPort);
        ServiceException portsOnCardSlotFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createPort(orderId, cardname, port);
        });
        assertTrue(portsOnCardSlotFoundException.getMessage().contains("A port already exist on the card with the given port number, " + "existingPortDetails: " + existingPort));

    }

    @Test
    void updatePortOnDevicetest() throws Exception {
        // Set up test data
        String deviceName = "Sample";
        Device device = new Device();
        device.setName(deviceName.toLowerCase());
        Port port = new Port();
        port.setName("port-1");
        port.setPositionOnDevice(1);  // Provide a valid port number
        port.setPositionOnCard(0);
        port.setPortType("PortType");
        port.setOperationalState("OperationalState");
        port.setAdministrativeState("AdministrativeState");
        port.setUsageState("UsageState");
        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        // Mock the repository methods
        Mockito.when(deviceRepository.findByName(deviceName.toLowerCase())).thenReturn(device); // Mocking the device retrieval
        Mockito.when(portRepository.getPort(deviceName.toLowerCase(), port.getName())).thenReturn(port);
        Mockito.when(pluggableRepository.getPluggableOnDeviceByNumber(deviceName.toLowerCase(), port.getPositionOnDevice())).thenReturn(null);
        Mockito.when(portRepository.getPortOnDeviceByNumber(deviceName.toLowerCase(), port.getPositionOnDevice())).thenReturn(null);
        Mockito.when(portRepository.updatePortOnDevice(port.getName(), port.getPositionOnDevice(), port.getPortType(),
                port.getOperationalState(), port.getAdministrativeState(), port.getUsageState(), deviceName.toLowerCase(), orderId)).thenReturn(port);
        // Call the method under test
        JSONObject response = deviceInstanceRestApis.updatePortOnDevice(orderId, deviceName.toLowerCase(), port);
        // Assert the response
        JSONObject expectedResponse = new JSONObject();
        expectedResponse.put("status", "Success");
        expectedResponse.put("port", port);
        assertEquals("Success", response.get("status"));
        assertEquals(port, response.get("port"));
    }

    @Test
    void updatePortOnDeviceTestFailure() {
        //test1: Order Not Found
        String Devicename = "Sample";
        String name = Devicename.toLowerCase();
        Device device = new Device();
        Port port = new Port();
        port.setName("port-1");
        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(null);
        Exception orderIdException = Assertions.assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.updatePortOnDevice(orderId, name, port);
        });
        assertTrue(orderIdException.getMessage().contains("The order " + orderId + " is not found"));
        //test2:Device with the given name doesn't exist
        port.setPositionOnDevice(1);  // Provide a valid port number
        port.setPositionOnCard(0);
        port.setPortType("PortType");
        port.setOperationalState("OperationalState");
        port.setAdministrativeState("AdministrativeState");
        port.setUsageState("UsageState");
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceRepository.findByName(name)).thenReturn(null);
        ServiceException DeviceNotFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.updatePortOnDevice(orderId, name, port);
        });
        assertTrue(DeviceNotFoundException.getMessage().contains("Device with the given name doesn't exist"));
        //Test2: Check if port name already exist on the device
        device.setName(name);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceRepository.findByName(name)).thenReturn(device);
        Mockito.when(portRepository.getPort(name, port.getName())).thenReturn(null);
        ServiceException PortNotFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.updatePortOnDevice(orderId, name, port);
        });
        assertTrue(PortNotFoundException.getMessage().contains("Port with given name doesn't exist"));
        //Test3:"A pluggable already exist on the card with the given port number, " +
        //                        "existingPluggableDetails: "
        Pluggable pluggableOnDevice = new Pluggable();
        pluggableOnDevice.setName(name);
        pluggableOnDevice.setPositionOnDevice(port.getPositionOnDevice());
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceRepository.findByName(name)).thenReturn(device);
        Mockito.when(portRepository.getPort(name, port.getName())).thenReturn(port);
        Mockito.when(pluggableRepository.getPluggableOnDeviceByNumber(name,
                port.getPositionOnDevice())).thenReturn(pluggableOnDevice);
        ServiceException pluggableOnDeviceFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.updatePortOnDevice(orderId, name, port);
        });
        assertTrue(pluggableOnDeviceFoundException.getMessage().contains("A pluggable already exist on the card with the given port number, " +
                "existingPluggableDetails: " + pluggableOnDevice));
        //Test3:4 port already exist on the device with the given port number
        Port portDetailsOnSameNo = new Port();
        portDetailsOnSameNo.setName("port1");
        portDetailsOnSameNo.setPositionOnDevice(port.getPositionOnDevice());
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceRepository.findByName(name)).thenReturn(device);
        Mockito.when(portRepository.getPort(name, port.getName())).thenReturn(port);
        Mockito.when(pluggableRepository.getPluggableOnDeviceByNumber(name,
                port.getPositionOnDevice())).thenReturn(null);
        Mockito.when(portRepository.getPortOnDeviceByNumber(name, port.getPositionOnDevice())).thenReturn(portDetailsOnSameNo);
        ServiceException portDetailsOnSameNoException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.updatePortOnDevice(orderId, name, port);
        });
        assertTrue(portDetailsOnSameNoException.getMessage().contains("A port already exist on the device with the given port number, " +
                "existingPortDetails: " + portDetailsOnSameNo));
    }

    @Test
    void createPluggableOnDeviceTestSuccess() {
        String deviceName = "Sampledevice";
        String name = deviceName.toLowerCase();

        // Create a mock device
        Device device = new Device();
        device.setName(name);

        // Create a mock existing pluggable
        Pluggable pluggable = new Pluggable();
        pluggable.setId(1L);
        pluggable.setName("sample");
        pluggable.setVendor("Juniper");
        pluggable.setPluggableModel("Ethernet");
        pluggable.setPluggablePartNumber("234");
        pluggable.setOperationalState("Tamilnadu");
        pluggable.setAdministrativeState("Up");
        pluggable.setUsageState("Active");
        pluggable.setPositionOnDevice(1);
        pluggable.setPositionOnCard(0);

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        // Mock repository methods
        Mockito.when(deviceRepository.findByName(name)).thenReturn(device);
        Mockito.when(pluggableRepository.getPluggableOnDevice(pluggable.getName(), name)).thenReturn(null);
        Mockito.when(portRepository.getPortOnDeviceByNumber(name, pluggable.getPositionOnDevice())).thenReturn(null);
        Mockito.when(pluggableRepository.getPluggableOnDeviceByNumber(name,
                pluggable.getPositionOnDevice())).thenReturn(null);
        Mockito.when(pluggableRepository.createPluggableOnDevice(pluggable.getName(),
                pluggable.getPositionOnDevice(), pluggable.getVendor(), pluggable.getPluggableModel(),
                pluggable.getPluggablePartNumber(), pluggable.getOperationalState(),
                pluggable.getAdministrativeState(), pluggable.getUsageState(),
                name, orderId)).thenReturn(pluggable);
        // Call the method under test
        JSONObject response = deviceInstanceRestApis.createPluggableOnDevice(orderId, name, pluggable);

        // Assert the response
        JSONObject expectedResponse = new JSONObject();
        expectedResponse.put("status", "Success");
        expectedResponse.put("pluggable", pluggable);

        assertEquals("Success", response.get("status"));
        assertEquals(pluggable, response.get("pluggable"));
    }

    @Test
    void createPluggableOnDeviceTestFailure() {
        //test1: Device with the given name doesn't exist
        String cardName = "test";
        String deviceName = "Sample";
        String name = deviceName.toLowerCase();
        Device device = new Device();
        Pluggable pluggable = new Pluggable();
        Port port = new Port();
        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        pluggable.setId(1L);
        pluggable.setName("sample");
        pluggable.setPluggableModel("Ethernet");
        pluggable.setPluggablePartNumber("234");
        pluggable.setOperationalState("Tamilnadu");
        pluggable.setAdministrativeState("Up");
        pluggable.setUsageState("Active");
        pluggable.setPositionOnDevice(1);
        pluggable.setPositionOnCard(0);

        //if pluggable vendor is empty
        Exception exception = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createPluggableOnDevice(orderId, name, pluggable);
        });
        assertTrue(exception.getMessage().contains("Pluggable vendor cannot be empty"));
        //test1: Order Not Found
        pluggable.setVendor("Juniper");
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(null);
        Exception orderIdException = Assertions.assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createPluggableOnDevice(orderId, name, pluggable);
        });
        assertTrue(orderIdException.getMessage().contains("The order " + orderId + " is not found"));

        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceRepository.findByName(name)).thenReturn(null);
        ServiceException DeviceNotFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createPluggableOnDevice(orderId, name, pluggable);
        });
        assertTrue(DeviceNotFoundException.getMessage().contains("Device with the given name doesn't exist"));
        //Test 2: validating position
        device.setName(name);
        pluggable.setPositionOnDevice(0);
        pluggable.setPositionOnCard(1);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceRepository.findByName(name)).thenReturn(device);
        ServiceException validpositionException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createPluggableOnDevice(orderId, name, pluggable);
        });
        assertTrue(validpositionException.getMessage().contains("Invalid input received, please provide valid port number on " +
                "PositionOnDevice"));
        //Test 3: validating pluggable details for uniqueness
        pluggable.setPositionOnDevice(1);
        pluggable.setPositionOnCard(0);
        Pluggable existingPluggableDetails = new Pluggable();
        existingPluggableDetails.setName(pluggable.getName());
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceRepository.findByName(name)).thenReturn(device);
        Mockito.when(pluggableRepository.getPluggableOnDevice(pluggable.getName(), name)).thenReturn(pluggable);
        ServiceException existingPluggableDetailsException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createPluggableOnDevice(orderId, name, pluggable);
        });
        assertTrue(existingPluggableDetailsException.getMessage().contains("Pluggable with given name already exist on the device, " +
                "existingPluggableDetails: " + pluggable.toString()));
        //Test 4: validating whether a port or pluggable already exist on the given position
        Port portsOnDevice = new Port();
        portsOnDevice.setName("sampleport");
        portsOnDevice.setPositionOnDevice(pluggable.getPositionOnDevice());
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceRepository.findByName(name)).thenReturn(device);
        Mockito.when(pluggableRepository.getPluggableOnDevice(pluggable.getName(), name)).thenReturn(null);
        Mockito.when(portRepository.getPortOnDeviceByNumber(name,
                pluggable.getPositionOnDevice())).thenReturn(portsOnDevice);
        ServiceException portsOnDeviceException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createPluggableOnDevice(orderId, name, pluggable);
        });
        assertTrue(portsOnDeviceException.getMessage().contains("Port with given name already exist on the device,  " +
                "existingPortDetails:  " + portsOnDevice));
        //Test5:A pluggable already exist on the device with the given position
        Pluggable pluggablesOnDevice = new Pluggable();
        pluggablesOnDevice.setName("sampleplugable");
        pluggablesOnDevice.setPositionOnDevice(pluggable.getPositionOnDevice());
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceRepository.findByName(name)).thenReturn(device);
        Mockito.when(pluggableRepository.getPluggableOnDevice(pluggable.getName(), name)).thenReturn(null);
        Mockito.when(portRepository.getPortOnDeviceByNumber(name,
                pluggable.getPositionOnDevice())).thenReturn(null);
        Mockito.when(pluggableRepository.getPluggableOnDeviceByNumber(name,
                pluggable.getPositionOnDevice())).thenReturn(pluggablesOnDevice);
        ServiceException pluggablesOnDeviceException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createPluggableOnDevice(orderId, name, pluggable);
        });
        assertTrue(pluggablesOnDeviceException.getMessage().contains("A pluggable already exist on the device with the given position " +
                "number, existingPluggableDetails: " + pluggablesOnDevice));
    }


    @Test
    void testupdatePluggableOnDeviceFailure() {
        //test1: Order Not Found
        String Devicename = "Sample";
        String name = Devicename.toLowerCase();
        Device device = new Device();
        Pluggable pluggable = new Pluggable();
        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        pluggable.setId(1L);
        pluggable.setName("sample");
        pluggable.setVendor("Juniper");
        pluggable.setPluggableModel("Ethernet");
        pluggable.setPluggablePartNumber("234");
        pluggable.setOperationalState("Tamilnadu");
        pluggable.setAdministrativeState("Up");
        pluggable.setUsageState("Active");
        pluggable.setPositionOnDevice(1);
        pluggable.setPositionOnCard(0);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(null);
        Exception orderIdException = Assertions.assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.updatePluggableOnDevice(orderId, name, pluggable);
        });
        assertTrue(orderIdException.getMessage().contains("The order " + orderId + " is not found"));
        //Test1: Device with the given name doesn't exist
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceRepository.findByName(Devicename.toLowerCase())).thenReturn(null);
        ServiceException DeviceNotFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.updatePluggableOnDevice(orderId, name, pluggable);
        });
        assertTrue(DeviceNotFoundException.getMessage().contains("Device with the given name doesn't exist"));
        //test2: validating position
        pluggable.setPositionOnDevice(null);
        device.setName(device.getName());
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceRepository.findByName(name)).thenReturn(device);

        ServiceException InvalidinputException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.updatePluggableOnDevice(orderId, name, pluggable);
        });
        assertTrue(InvalidinputException.getMessage().contains("Invalid input received, please provide valid port number on " +
                "PositionOnDevice"));
        //test3: Pluggable with given name doesn't exist on the device,
        pluggable.setPositionOnDevice(1);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceRepository.findByName(name)).thenReturn(device);
        Mockito.when(pluggableRepository.getPluggableOnDevice(pluggable.getName(), name)).thenReturn(null);
        ServiceException existingPluggableDetailsException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.updatePluggableOnDevice(orderId, name, pluggable);
        });
        assertTrue(existingPluggableDetailsException.getMessage().contains("Pluggable with given name doesn't exist on the device, "));
        //test4: "A port already exist on the device with the given port number, "
        Port portsOnDevice = new Port();
        pluggable.setPositionOnDevice(1);
        portsOnDevice.setPositionOnDevice(pluggable.getPositionOnDevice());
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceRepository.findByName(name)).thenReturn(device);
        Mockito.when(pluggableRepository.getPluggableOnDevice(pluggable.getName(), name)).thenReturn(pluggable);
        Mockito.when(portRepository.getPortOnDeviceByNumber(name,
                pluggable.getPositionOnDevice())).thenReturn(portsOnDevice);
        ServiceException existingportsOnDeviceException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.updatePluggableOnDevice(orderId, name, pluggable);
        });
        assertTrue(existingportsOnDeviceException.getMessage().contains("A port already exist on the device with the given port number, " +
                "existingPortDetails: " + portsOnDevice));
        //test5:A pluggable already exist on the device with the given position
        Pluggable pluggablesOnDevice = new Pluggable();
        pluggablesOnDevice.setName("new");
        pluggablesOnDevice.setPositionOnDevice(pluggable.getPositionOnDevice());
        Pluggable existingPluggableDetails = new Pluggable();
        existingPluggableDetails.setName(pluggable.getName());
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(deviceRepository.findByName(name)).thenReturn(device);
        Mockito.when(pluggableRepository.getPluggableOnDevice(pluggable.getName(), name)).thenReturn(pluggable);
        Mockito.when(portRepository.getPortOnDeviceByNumber(any(), any())).thenReturn(null);
        Mockito.when(pluggableRepository.getPluggableOnDeviceByNumber(name,
                pluggable.getPositionOnDevice())).thenReturn(pluggablesOnDevice);
        ServiceException pluggablesOnDeviceException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.updatePluggableOnDevice(orderId, name, pluggable);
        });
        assertTrue(pluggablesOnDeviceException.getMessage().contains("A pluggable already exist on the device with the given position " +
                "number. Existing pluggable details: " + pluggablesOnDevice));
    }

    @Test
    void updatePluggableOnDeviceTestSuccess() {
        String Devicename = "SampleDevice";
        String name = Devicename.toLowerCase();
        Device device = new Device();
        device.setName(name);
        Pluggable pluggable = new Pluggable();
        pluggable.setId(1L);
        pluggable.setName("sample");
        pluggable.setVendor("Juniper");
        pluggable.setPluggableModel("Ethernet");
        pluggable.setPluggablePartNumber("234");
        pluggable.setOperationalState("Tamilnadu");
        pluggable.setAdministrativeState("Up");
        pluggable.setUsageState("Active");
        pluggable.setPositionOnDevice(1);
        pluggable.setPositionOnCard(0);
        Pluggable updateplugable = new Pluggable();
        updateplugable.setId(1L);
        updateplugable.setName("sample");
        updateplugable.setVendor("Juniper");
        updateplugable.setPluggableModel("Ethernet");
        updateplugable.setPluggablePartNumber("234");
        updateplugable.setOperationalState("Tamilnadu");
        updateplugable.setAdministrativeState("Up");
        updateplugable.setUsageState("Active");
        updateplugable.setPositionOnDevice(1);
        updateplugable.setPositionOnCard(0);

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        Mockito.when(deviceRepository.findByName(name)).thenReturn(device);
        Mockito.when(pluggableRepository.getPluggableOnDevice(pluggable.getName(), name)).thenReturn(pluggable);
        Mockito.when(portRepository.getPortOnDeviceByNumber(name, pluggable.getPositionOnDevice())).thenReturn(null);
        Mockito.when(pluggableRepository.getPluggableOnDeviceByNumber(name,
                pluggable.getPositionOnDevice())).thenReturn(null);
        Mockito.when(pluggableRepository.updatePluggableOnDevice(pluggable.getName(),
                pluggable.getPositionOnDevice(), pluggable.getVendor(), pluggable.getPluggableModel(),
                pluggable.getPluggablePartNumber(), pluggable.getOperationalState(),
                pluggable.getAdministrativeState(), pluggable.getUsageState(),
                name, orderId)).thenReturn(updateplugable);
        JSONObject response = deviceInstanceRestApis.updatePluggableOnDevice(orderId, name, pluggable);

        // Assert the response
        JSONObject expectedResponse = new JSONObject();
        expectedResponse.put("status", "Success");
        expectedResponse.put("pluggable", updateplugable);

        assertEquals("Success", response.get("status"));
        assertEquals(updateplugable, response.get("pluggable"));
    }

    @Test
    void createPluggableTest() {
        // Test parameters
        String cardName = "samplecard";
        String deviceName = "test";
        String name = cardName.toLowerCase();
        String pluggableName = "sampleplugable";
        int positionOnCard = 1;
        // Create the pluggable object
        Pluggable pluggable = new Pluggable();
        pluggable.setName(pluggableName.toLowerCase());
        pluggable.setPositionOnCard(positionOnCard);
        pluggable.setOperationalState("Tamilnadu");
        pluggable.setAdministrativeState("Up");
        pluggable.setUsageState("200gb");
        pluggable.setPositionOnCard(positionOnCard);
        pluggable.setPluggableModel("Sample");
        pluggable.setVendor("Cisco");
        pluggable.setPluggablePartNumber("1.0.09");

        // Create the card object
        Card card = new Card();
        card.setName(name);


        // Create the card slot object
        CardSlot slot = new CardSlot();
        slot.setName(name);//Cardname
        slot.setOperationalState(pluggable.getOperationalState());
        slot.setAdministrativeState(pluggable.getAdministrativeState());
        slot.setUsageState(pluggable.getUsageState());

        Pluggable pluggableDetails = new Pluggable();
        pluggableDetails.setName(pluggableName.toLowerCase());
        pluggableDetails.setPositionOnCard(positionOnCard);
        pluggableDetails.setOperationalState("Tamilnadu");
        pluggableDetails.setAdministrativeState("Up");
        pluggableDetails.setUsageState("200gb");
        pluggableDetails.setPositionOnCard(positionOnCard);
        pluggableDetails.setPluggableModel("Sample");
        pluggableDetails.setVendor("Cisco");
        pluggableDetails.setPluggablePartNumber("1.0.09");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        // Mock the dependencies
        Mockito.when(cardRepository.getCard(deviceName, name)).thenReturn(card);
        Mockito.when(pluggableRepository.getPluggable(name, pluggableName)).thenReturn(null);
        Mockito.when(portRepository.getPortOnCardByNumber(name + "/" + positionOnCard, positionOnCard))
                .thenReturn(null);
        Mockito.when(pluggableRepository.getPluggableOnCardByNumber(name + "/" + positionOnCard, positionOnCard))
                .thenReturn(null);
        Mockito.when(cardSlotRepository.createCardSlot(name + "/" + pluggable.
                        getPositionOnCard().toString(), pluggable.getPositionOnCard(),
                pluggable.getOperationalState(), pluggable.getAdministrativeState(),
                pluggable.getUsageState(), name)).thenReturn(slot);

        Mockito.when(pluggableRepository.createPluggable(pluggable.getName(),
                pluggable.getPositionOnCard(), pluggable.getVendor(), pluggable.getPluggableModel(),
                pluggable.getPluggablePartNumber(), pluggable.getOperationalState(),
                pluggable.getAdministrativeState(), pluggable.getUsageState(),
                slot.getName(), orderId)).thenReturn(pluggableDetails);
        // Call the method under test
        JSONObject response = deviceInstanceRestApis.createPluggable(orderId, deviceName, cardName, pluggable);
        assertEquals("Success", response.get("status"));
        assertEquals(pluggableDetails, response.get("pluggable"));
    }

    @Test
    void createPluggableTestFailure() {
        //Test1:Check if the card exist
        String deviceName = "test";
        String Cardname = "SampleCard";
        String name = Cardname.toLowerCase();
        Card card = new Card();
        Pluggable pluggable = new Pluggable();
        pluggable.setName("sample");
        Long orderId = 3456l;

        //if pluggable vendor is empty
        Exception exception = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createPluggable(orderId, deviceName, name, pluggable);
        });
        assertTrue(exception.getMessage().contains("Pluggable vendor cannot be empty"));

        //Order not found
        pluggable.setVendor("Cisco");
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(null);

        Exception OrderNotFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createPluggable(orderId, deviceName, name, pluggable);
        });
        assertTrue(OrderNotFoundException.getMessage().contains("The order " + orderId + " is not found"));

        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(cardRepository.getCard(name)).thenReturn(null);
        ServiceException existingCardDetailsException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createPluggable(orderId, deviceName, name, pluggable);
        });
        assertTrue(existingCardDetailsException.getMessage().contains("Card with the given name doesn't exist"));

        //Test2:Check if any pluggable already exist with same name
        card.setName(name);//Setting the Card name
        Pluggable existingPluggableDetails = new Pluggable();
        existingPluggableDetails.setName(pluggable.getName());
        Mockito.when(cardRepository.getCard(deviceName, name)).thenReturn(card);
        Mockito.when(pluggableRepository.getPluggable(name, pluggable.getName())).thenReturn(existingPluggableDetails);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(cardRepository.getCard(name)).thenReturn(card);
        Mockito.when(pluggableRepository.getPluggable(pluggable.getName())).thenReturn(existingPluggableDetails);
        ServiceException existingPluggableDetailsException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createPluggable(orderId, deviceName, name, pluggable);
        });
        assertTrue(existingPluggableDetailsException.getMessage().contains("Pluggable with given name already exist, " +
                "existingPluggableDetails: " + existingPluggableDetails.toString()));
        //Test3:Check if the given position on card is already consumed by a port/plyggable
        pluggable.setPositionOnCard(2);//Setting Pluggable position
        Port portsOnCard = new Port();
        //portsOnCard.setName("sample");
        portsOnCard.setPositionOnCard(pluggable.getPositionOnCard());
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(cardRepository.getCard(name)).thenReturn(card);
        Mockito.when(pluggableRepository.getPluggable(pluggable.getName())).thenReturn(null);
        Mockito.when(cardRepository.getCard(deviceName, name)).thenReturn(card);
        Mockito.when(pluggableRepository.getPluggable(name, pluggable.getName())).thenReturn(null);
        Mockito.when(portRepository.getPortOnCardByNumber(name + "/" + pluggable.
                getPositionOnCard().toString(), pluggable.getPositionOnCard())).thenReturn(portsOnCard);
        ServiceException portsOnCardException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createPluggable(orderId, deviceName, name, pluggable);
        });
        assertTrue(portsOnCardException.getMessage().contains("A port already exist on the card with the given port number, " +
                "existingPortDetails: " + portsOnCard));
//Test 4: pluggable already exist on the card with the given position
        Pluggable pluggablesOnCardSlot = new Pluggable();
        pluggablesOnCardSlot.setPositionOnCard(pluggable.getPositionOnCard());
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(cardRepository.getCard(name)).thenReturn(card);
        Mockito.when(pluggableRepository.getPluggable(pluggable.getName())).thenReturn(null);
        Mockito.when(cardRepository.getCard(deviceName, name)).thenReturn(card);
        Mockito.when(pluggableRepository.getPluggable(name, pluggable.getName())).thenReturn(null);
        Mockito.when(portRepository.getPortOnCardByNumber(name + "/" + pluggable.
                getPositionOnCard().toString(), pluggable.getPositionOnCard())).thenReturn(null);
        Mockito.when(pluggableRepository.getPluggableOnCardByNumber(
                        name + "/" + pluggable.getPositionOnCard().toString(), pluggable.getPositionOnCard()))
                .thenReturn(pluggablesOnCardSlot);
        ServiceException pluggablesOnCardSlotException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.createPluggable(orderId, deviceName, name, pluggable);
        });
        assertTrue(pluggablesOnCardSlotException.getMessage().contains("A pluggable already exist on the card with the given position " +
                "number, existingPluggableDetails: " + pluggablesOnCardSlot));
    }

    @Test
    void updatePluggableTestSuccess() {
        String card = "testcard";
        String deviceName = "test";
        Pluggable pluggable = new Pluggable();
        pluggable.setName("testpluggable");
        pluggable.setPositionOnCard(1);

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        //case-1
        Card existingCardDetails = new Card();
        Mockito.when(cardRepository.getCard(card)).thenReturn(existingCardDetails);
        //case-2
        Pluggable existingPluggableDetails = new Pluggable();
        existingPluggableDetails.setPositionOnCard(2);
        Mockito.when(pluggableRepository.getPluggable(card, pluggable.getName())).thenReturn(existingPluggableDetails);
        //case-3
        Port portsOnCardSlot = new Port();
        Mockito.when(portRepository.getPortOnCardByNumber(card + "/" +
                pluggable.getPositionOnCard().toString(), pluggable.getPositionOnCard())).thenReturn(null);
        //case-4
        Mockito.when(pluggableRepository.getPluggableOnCardByNumber(card + "/" + pluggable.getPositionOnCard().toString(), pluggable.getPositionOnCard())).thenReturn(null);
        //case-5
        String slotName = card + "/" + existingPluggableDetails.getPositionOnCard().toString();
        doNothing().when(cardSlotRepository).deleteCardSlot(card + "/" + existingPluggableDetails.
                getPositionOnCard().toString());
        CardSlot slot = new CardSlot();
        Mockito.when(cardSlotRepository.createCardSlot(card + "/" +
                        pluggable.getPositionOnCard().toString(),
                pluggable.getPositionOnCard(), pluggable.getOperationalState(), pluggable.getAdministrativeState(),
                pluggable.getUsageState(), card)).thenReturn(slot);
        slotName = slot.getName();
        //case-6
        Pluggable pluggableDetails = new Pluggable();
        Mockito.when(pluggableRepository.updatePluggable(pluggable.getName(), pluggable.getPositionOnCard(), pluggable.getVendor(), pluggable.getPluggableModel(),
                pluggable.getPluggablePartNumber(), pluggable.getOperationalState(),
                pluggable.getAdministrativeState(), pluggable.getUsageState(),
                slotName, orderId)).thenReturn(pluggableDetails);
        JSONObject response = deviceInstanceRestApis.updatePluggable(orderId, card, pluggable);
        assertEquals("Success", response.get("status"));
        assertEquals(pluggableDetails, response.get("pluggable"));
    }

    @Test
    void updatePluggableTestfailure() {
        // Test case 1: Order not found
        String Cardname = "samplecard";
        Pluggable pluggable = new Pluggable();
        pluggable.setName("sample");
        pluggable.setPositionOnCard(1);
        pluggable.setPositionOnDevice(2);
        pluggable.setVendor("Cisco");
        pluggable.setPluggableModel("cisco1");
        pluggable.setPluggablePartNumber("1.09.09");
        pluggable.setOperationalState("Tamillnadu");
        pluggable.setAdministrativeState("Up");
        pluggable.setUsageState("200gb");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(null);

        Exception OrderNotFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.updatePluggable(orderId, Cardname.toLowerCase(), pluggable);
        });
        assertTrue(OrderNotFoundException.getMessage().contains("The order " + orderId + " is not found"));
        //Test1:Card with the given name doesn't exist
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(cardRepository.getCard(Cardname.toLowerCase())).thenReturn(null);
        ServiceException existingCardDetailsException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.updatePluggable(orderId, Cardname.toLowerCase(), pluggable);
        });
        assertTrue(existingCardDetailsException.getMessage().contains("Card with the given name doesn't exist"));
        Card card = new Card();
        card.setName(Cardname.toLowerCase());
        //Test2:Pluggable with given name doesn't exist
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(cardRepository.getCard(Cardname.toLowerCase())).thenReturn(card);
        Mockito.when(pluggableRepository.getPluggable(Cardname.toLowerCase(), pluggable.getName())).thenReturn(null);
        ServiceException existingPluggableDetailsException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.updatePluggable(orderId, Cardname.toLowerCase(), pluggable);
        });
        assertTrue(existingPluggableDetailsException.getMessage().contains("Pluggable with given name doesn't exist"));
        //Test3:"A port already exist on the card with the given port number, "
        Port portsOnCardSlot = new Port();
        pluggable.setPositionOnCard(1);
        portsOnCardSlot.setPositionOnCard(1);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(cardRepository.getCard(Cardname.toLowerCase())).thenReturn(card);
        Mockito.when(pluggableRepository.getPluggable(Cardname.toLowerCase(), pluggable.getName())).thenReturn(pluggable);
        Mockito.when(portRepository.getPortOnCardByNumber(Cardname.toLowerCase() + "/" +
                pluggable.getPositionOnCard().toString(), pluggable.getPositionOnCard())).thenReturn(portsOnCardSlot);
        ServiceException portsOnCardSlotException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.updatePluggable(orderId, Cardname.toLowerCase(), pluggable);
        });
        assertTrue(portsOnCardSlotException.getMessage().contains("A port already exist on the card with the given port number, " +
                "existingPortDetails: " + portsOnCardSlot));
        //Test4:A port already exist on the card with the given port number
        Pluggable pluggablesOnCardSlot = new Pluggable();
        pluggablesOnCardSlot.setName("different");
        pluggablesOnCardSlot.setPositionOnCard(1);
        Pluggable existingPluggableDetails = new Pluggable();
        existingPluggableDetails.setName(pluggable.getName());
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(cardRepository.getCard(Cardname.toLowerCase())).thenReturn(card);
        Mockito.when(pluggableRepository.getPluggable(Cardname.toLowerCase(), pluggable.getName())).thenReturn(pluggable);
        Mockito.when(portRepository.getPortOnCardByNumber(Cardname.toLowerCase() + "/" +
                pluggable.getPositionOnCard().toString(), pluggable.getPositionOnCard())).thenReturn(null);
        Mockito.when(pluggableRepository.getPluggableOnCardByNumber(
                Cardname.toLowerCase() + "/" + pluggable.getPositionOnCard().toString(), pluggable.getPositionOnCard())).thenReturn(pluggablesOnCardSlot);
        ServiceException pluggablesOnCardSlotException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.updatePluggable(orderId, Cardname.toLowerCase(), pluggable);
        });
        assertTrue(pluggablesOnCardSlotException.getMessage().contains("A pluggable already exist on the card with the " +
                "given position number, existingPluggableDetails: " +
                pluggablesOnCardSlot));
    }

    @Test
    void updateCardSlotTestSuccess() {
        String cardName = "testcard";
        String cardSlotName = "test";
        CardSlot cardSlot = new CardSlot();
        cardSlot.setName(cardSlotName);

        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);

        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(cardSlotRepository.getCardSlotOnACard(cardName, cardSlotName)).thenReturn(cardSlot);
        Mockito.when(cardSlotRepository.updateCardSlot(cardSlot.getName(), cardSlot.getOperationalState(), cardSlot.getAdministrativeState(),
                cardSlot.getUsageState(), orderId)).thenReturn(cardSlot);

        JSONObject response = deviceInstanceRestApis.updateCardSlot(orderId, cardSlotName, cardName, cardSlot);
        assertEquals("Success", response.get("status"));
        assertEquals(cardSlot, response.get("slotDetails"));
    }

    @Test
    void updateCardSlotOrderNotFoundTest() {
        String cardSlotName = "test";
        String cardName = "testcard";
        CardSlot cardSlot = new CardSlot();
        cardSlot.setName(cardSlotName);

        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);

        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(null);

        ServiceException orderNotFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.updateCardSlot(orderId, cardSlotName, cardName, cardSlot);
        });
        assertTrue(orderNotFoundException.getMessage().contains("The order " + orderId + " is not found"));
    }

    @Test
    void updateCardSlotTestFailure() {
        String cardName = "testcard";
        String cardSlotName = "test";
        CardSlot cardSlot = new CardSlot();
        cardSlot.setName(cardSlotName);
        Mockito.when(cardSlotRepository.getCardSlotOnACard(cardName, cardSlotName)).thenReturn(null);

        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);

        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(cardSlotRepository.getCardSlot(cardSlotName)).thenReturn(null);
        ServiceException existingCardSlotDetailsException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.updateCardSlot(orderId, cardSlotName, cardName, cardSlot);
            ;
        });
        assertTrue(existingCardSlotDetailsException.getMessage().contains("CardSlot with the given name doesn't exist"));
    }

    @Test
    void updateSlotTestSuccess() {
        String shelfName = "tests";
        String slotName = "test";
        Slot slot = new Slot();
        slot.setName(slotName);

        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);

        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        Mockito.when(slotRepository.getSlotOnShelf(shelfName, slotName)).thenReturn(slot);
        Mockito.when(slotRepository.updateSlot(slot.getName(), slot.getOperationalState(), slot.getAdministrativeState(),
                slot.getUsageState(), orderId)).thenReturn(slot);
        JSONObject response = deviceInstanceRestApis.updateSlot(orderId, slotName, shelfName, slot);
        assertEquals("Success", response.get("status"));
        assertEquals(slot, response.get("slotDetails"));
    }

    @Test
    void updateSlotOrderNotFoundTest() {
        String shelfName = "tests";
        String slotName = "test";
        Slot slot = new Slot();
        slot.setName(slotName);

        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);

        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(null);

        ServiceException orderNotFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.updateSlot(orderId, slotName, shelfName, slot);
        });
        assertTrue(orderNotFoundException.getMessage().contains("The order " + orderId + " is not found"));
    }

    @Test
    void updateSlotTestFailure() {
        String shelfName = "tests";
        String slotName = "test";
        Slot slot = new Slot();
        slot.setName(slotName);
        Mockito.when(slotRepository.getSlotOnShelf(shelfName, slotName)).thenReturn(null);

        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);

        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(slotRepository.getSlot(slotName)).thenReturn(null);
        ServiceException existingCardSlotDetailsException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.updateSlot(orderId, slotName, shelfName, slot);
        });
        assertTrue(existingCardSlotDetailsException.getMessage().contains("Slot with the given name doesn't exist"));
    }

    @Test
    void updateShelfTestSuccess() {
        String deviceName = "testd";
        String shelfName = "test";
        Shelf shelf = new Shelf();
        shelf.setName(shelfName);

        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);

        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(shelfRepository.getShelf(deviceName, shelfName)).thenReturn(shelf);
        Mockito.when(shelfRepository.updateShelf(shelf.getName(), shelf.getOperationalState(), shelf.getAdministrativeState(),
                shelf.getUsageState(), orderId)).thenReturn(shelf);
        JSONObject response = deviceInstanceRestApis.updateShelf(orderId, shelfName, shelf, deviceName);
        assertEquals("Success", response.get("status"));
        assertEquals(shelf, response.get("shelfDetails"));
    }

    @Test
    void updateShelfOrderNotFoundTest() {
        String deviceName = "testd";
        String shelfName = "test";
        Shelf shelf = new Shelf();
        shelf.setName(shelfName);

        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);

        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(null);
        ServiceException orderNotFoundException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.updateShelf(orderId, shelfName, shelf, deviceName);
        });
        assertTrue(orderNotFoundException.getMessage().contains("The order " + orderId + " is not found"));
    }

    @Test
    void updateShelfTestFailure() {
        String deviceName = "testd";
        String shelfName = "test";
        Shelf shelf = new Shelf();
        shelf.setName(shelfName);

        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);

        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(shelfRepository.getShelf(deviceName, shelfName)).thenReturn(null);
        ServiceException existingCardSlotDetailsException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.updateShelf(orderId, shelfName, shelf, deviceName);
        });
        assertTrue(existingCardSlotDetailsException.getMessage().contains("Shelf with the given name doesn't exist"));
    }

    @MockBean
    AsyncService asyncService;
    @MockBean
    ObjectCountRepo objectCountRepo;

    @Test
    void testSyncDashboardDetails() {
        // Mock the behavior of the asyncService.syncDashboard() method
        doNothing().when(asyncService).syncDashboard();
        // Call the method under test
        ResponseEntity<Object> response = deviceInstanceRestApis.syncDashboardDetails();
        // Verify the response
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void getDashboardDetailsTest() {
        List<ObjectCount> mockDashboardDetails = new ArrayList<>();
        Mockito.when(objectCountRepo.findByObjectTypeIn(any())).thenReturn(mockDashboardDetails);
        List<ObjectCount> result = deviceInstanceRestApis.getDashboardDetails();
        assertEquals(mockDashboardDetails, result);
    }

    @Test
    void getDashboardDetailsFailureTest() {
        doThrow(new NullPointerException("Null pointer exception")).when(objectCountRepo).findByObjectTypeIn(any());
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.getDashboardDetails();
        });
        Assertions.assertTrue(exception.getMessage().contains("Null pointer exception"));
    }

    @Test
    void updateLogicalPortOnPhysicalPortInCardTest() {
        String device = "test";
        String Cardname = "samplecard";
        Card card = new Card();
        card.setName(Cardname);
        LogicalPort port = new LogicalPort();
        port.setName("sampleport");
        ArrayList<LogicalPort> existingPortDetails = new ArrayList<>();
        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(cardRepository.getCard(Cardname)).thenReturn(card);
        Mockito.when(cardRepository.getCard(device, Cardname)).thenReturn(card);
        Mockito.when(logicalPortRepository.
                getLogicPortsOnCard(Cardname, port.getName(), 1)).thenReturn(null);
        ServiceException existingCardSlotDetailsException = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.updateLogicalPortOnPhysicalPortInCard(orderId, device, Cardname, port);
        });
        assertTrue(existingCardSlotDetailsException.getMessage().contains("LogicalPort with given name doesn't exist on the card,"));
    }

    @Test
    void updatePortSuccess() {
        String device = "test";
        String card = "testcard";
        Port port = new Port();
        port.setName("testport");
        port.setPositionOnCard(2);

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        //case-1
        Card existingCardDetails = new Card();
        Mockito.when(cardRepository.getCard(device, card)).thenReturn(existingCardDetails);

        //case-2
        Port existingPort = new Port();
        existingPort.setPositionOnCard(3);
        Mockito.when(portRepository.getPortOnCard(card, port.getName())).thenReturn(existingPort);

        //case-3
        Mockito.when(pluggableRepository.getPluggableOnCardByNumber(card + "/" + port.getPositionOnCard().toString(), port.getPositionOnCard())).thenReturn(null);

        //case-4
        Mockito.when(portRepository.getPortOnCardByNumber(card + "/" + existingPort.getPositionOnCard().toString(), port.getPositionOnCard())).thenReturn(null);

        //case-5
        String cardSlotName = card + "/" + existingPort.getPositionOnCard().toString();
        doNothing().when(cardSlotRepository).deleteCardSlot(card + "/" + existingPort.getPositionOnCard().toString());
        CardSlot slot = new CardSlot();
        Mockito.when(cardSlotRepository.createCardSlot(card + "/" + port.getPositionOnCard().toString(),
                port.getPositionOnCard(), port.getOperationalState(), port.getAdministrativeState(),
                port.getUsageState(), card)).thenReturn(slot);
        cardSlotName = slot.getName();

        //case-6
        Port portDetails = new Port();
        Mockito.when(portRepository.updatePort(port.getName(),
                port.getPositionOnCard(), port.getPortType(), port.getOperationalState(),
                port.getAdministrativeState(), port.getUsageState(),
                cardSlotName, orderId)).thenReturn(portDetails);

        JSONObject response = deviceInstanceRestApis.updatePort(orderId, device, card, port);
        assertEquals("Success", response.get("status"));
        assertEquals(portDetails, response.get("port"));
    }

    @Test
    void updatePortFailure() {
        String device = "test";
        String card = "testcard";
        Port port = new Port();
        port.setName("testport");
        port.setPositionOnCard(2);

        //case-1
        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Exception orderIdException = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updatePort(orderId, device, card, port);
        });
        assertEquals("Card with the given name doesn't exist", orderIdException.getMessage());

        //case-1
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(cardRepository.getCard(card)).thenReturn(null);
        Mockito.when(cardRepository.getCard(device, card)).thenReturn(null);
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updatePort(orderId, device, card, port);
        });
        assertEquals("Card with the given name doesn't exist", e.getMessage());
        //case-2
        Card existingCardDetails = new Card();
        Mockito.when(cardRepository.getCard(device, card)).thenReturn(existingCardDetails);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(cardRepository.getCard(card)).thenReturn(existingCardDetails);
        Mockito.when(portRepository.getPortOnCard(card, port.getName())).thenReturn(null);

        Exception portdoesnotexist = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updatePort(orderId, device, card, port);
        });
        assertEquals("Port with given name doesn't exist", portdoesnotexist.getMessage());
        //case-3
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(cardRepository.getCard(card)).thenReturn(existingCardDetails);
        Mockito.when(cardRepository.getCard(device, card)).thenReturn(existingCardDetails);
        Port existingPort = new Port();
        existingPort.setPositionOnCard(3);
        Mockito.when(portRepository.getPortOnCard(card, port.getName())).thenReturn(existingPort);
        Pluggable pluggableOnCardSlot = new Pluggable();
        Mockito.when(pluggableRepository.getPluggableOnCardByNumber(card + "/" + port.getPositionOnCard().toString(), port.getPositionOnCard())).thenReturn(pluggableOnCardSlot);

        Exception pluggableAlreadyexist = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updatePort(orderId, device, card, port);
        });
        assertEquals("A pluggable already exist on the card with the given " +
                "port number, existingPluggableDetails: " + pluggableOnCardSlot, pluggableAlreadyexist.getMessage());

        //case-4
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(cardRepository.getCard(card)).thenReturn(existingCardDetails);
        Mockito.when(cardRepository.getCard(device, card)).thenReturn(existingCardDetails);
        Mockito.when(portRepository.getPortOnCard(card, port.getName())).thenReturn(existingPort);
        Mockito.when(pluggableRepository.getPluggableOnCardByNumber(card + "/" + port.getPositionOnCard().toString(), port.getPositionOnCard())).thenReturn(null);
        Port portsOnCardSlot = new Port();
        portsOnCardSlot.setName("testportoncardslot");
        Mockito.when(portRepository.getPortOnCardByNumber(card + "/" + existingPort.getPositionOnCard().toString(), port.getPositionOnCard())).thenReturn(portsOnCardSlot);

        Exception portAlreadyexist = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updatePort(orderId, device, card, port);
        });
        assertEquals("A port already exist on the card with the given port number, " +
                "existingPortDetails: " + portsOnCardSlot, portAlreadyexist.getMessage());

    }

    @Test
    void getCardModelsForDeviceNameNotProvidedTest() {
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.getCardModelsForDevice(null);
        });
        Assertions.assertEquals("Device name is not provided", e.getMessage());
    }

    @Test
    void getCardModelsForDeviceDoesntExistTest() {
        Mockito.when(deviceRepository.getByName("devicename")).thenReturn(null);
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.getCardModelsForDevice("deviceName");
        });
        Assertions.assertEquals("Device doesn't exist", e.getMessage());
    }

    @Test
    void getCardModelsForDeviceModelDoesntExistTest() {
        Mockito.when(deviceRepository.getByName("devicename")).thenReturn(new Device());
        Mockito.when(deviceRepository.getDeviceModel("devicename")).thenReturn(null);
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.getCardModelsForDevice("deviceName");
        });
        Assertions.assertEquals("Device model doesn't exist for this device", e.getMessage());
    }

    @Test
    void getCardModelsForDeviceSuccessTest() {
        DeviceMetaModel deviceModel = new DeviceMetaModel();
        Mockito.when(deviceRepository.getByName("devicename")).thenReturn(new Device());
        Mockito.when(deviceRepository.getDeviceModel("devicename")).thenReturn("devicemodel");
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel("devicemodel")).thenReturn(deviceModel);
        deviceInstanceRestApis.getCardModelsForDevice("deviceName");
    }

    @Test
    void getAvailableShelvesOnDeviceNameNullTest() {
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.getAvailableShelvesOnDevice(null);
        });
        Assertions.assertEquals("Device name is not provided", e.getMessage());
    }

    @Test
    void getAvailableShelvesOnDeviceDoesntExistTest() {
        Mockito.when(deviceRepository.getByName("devicename")).thenReturn(null);
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.getAvailableShelvesOnDevice("deviceName");
        });
        Assertions.assertEquals("Device doesn't exist", e.getMessage());
    }

    @Test
    void getAvailableShelvesOnDeviceSuccessTest() {
        Shelf shelf1 = new Shelf();
        shelf1.setName("shelf1");
        Shelf shelf2 = new Shelf();
        shelf2.setName("shelf2");

        ArrayList<Shelf> shelves = new ArrayList<>();
        shelves.add(shelf1);
        shelves.add(shelf2);

        Mockito.when(deviceRepository.getByName("devicename")).thenReturn(new Device());
        Mockito.when(shelfRepository.getShelves("devicename")).thenReturn(shelves);
        Mockito.when(shelfRepository.getCardOnShelf("shelf1")).thenReturn(Collections.emptyList());
        Mockito.when(shelfRepository.getCardOnShelf("shelf2")).thenReturn(Collections.singleton(new Card()));

        deviceInstanceRestApis.getAvailableShelvesOnDevice("deviceName");
    }

    @Test
    void updateLogicalPortByName() {
        String logicalPortName = "testlogicalport";
        LogicalPort logicalPort = new LogicalPort();
        logicalPort.setName(logicalPortName);
        logicalPort.setPositionOnDevice(2);
        logicalPort.setPositionOnCard(3);

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        //case-1
        LogicalPort existingLogicalPort = new LogicalPort();
        Mockito.when(logicalPortRepository.getPort(logicalPortName)).thenReturn(existingLogicalPort);

        LogicalPort updatedLogicalPort = new LogicalPort();
        //case-2
        Port port = new Port();
        Mockito.when(portRepository.getPortFromLogicalPortName(logicalPortName)).thenReturn(null);
        //case-2.1
        Pluggable pluggable = new Pluggable();
        pluggable.setName("testpluggable");
        pluggable.setPositionOnCard(logicalPort.getPositionOnCard());
        pluggable.setPositionOnDevice(logicalPort.getPositionOnDevice());
        Mockito.when(pluggableRepository.getPluggableFromLogicalPortName(logicalPortName)).thenReturn(pluggable);
        //case-2.2
        Card cardDetails = new Card();
        Mockito.when(cardRepository.findByPluggableName(pluggable.getName())).thenReturn(cardDetails);

        //case-3
        Mockito.when(logicalPortRepository.updateLogicalPort(logicalPortName,
                logicalPort.getPositionOnCard(), logicalPort.getPositionOnDevice(), logicalPort.getPortType(),
                logicalPort.getOperationalState(), logicalPort.getAdministrativeState(), logicalPort.getUsageState(), orderId)).thenReturn(updatedLogicalPort);

        JSONObject response = deviceInstanceRestApis.updateLogicalPortByName(orderId, logicalPortName, logicalPort);
        assertEquals("Success", response.get("status"));
        assertEquals(updatedLogicalPort, response.get("logicalPort"));
        //Case 2nd
        logicalPort.setPositionOnCard(0);

        Device deviceDetails = new Device();
        Mockito.when(deviceRepository.findByPluggableName(pluggable.getName())).thenReturn(deviceDetails);

        //case-2nd 1
        Mockito.when(logicalPortRepository.updateLogicalPort(logicalPortName,
                logicalPort.getPositionOnCard(), logicalPort.getPositionOnDevice(), logicalPort.getPortType(),
                logicalPort.getOperationalState(), logicalPort.getAdministrativeState(), logicalPort.getUsageState(), orderId)).thenReturn(updatedLogicalPort);

        JSONObject response2 = deviceInstanceRestApis.updateLogicalPortByName(orderId, logicalPortName, logicalPort);
        assertEquals("Success", response2.get("status"));
        assertEquals(updatedLogicalPort, response2.get("logicalPort"));

        //case 3rd
        port.setName("testport");
        port.setPositionOnCard(logicalPort.getPositionOnCard());
        Mockito.when(portRepository.getPortFromLogicalPortName(logicalPortName)).thenReturn(port);
        logicalPort.setPositionOnCard(0);
        Mockito.when(cardRepository.getCardFromPortName(port.getName())).thenReturn(cardDetails);
        Mockito.when(logicalPortRepository.updateLogicalPort(logicalPortName,
                logicalPort.getPositionOnCard(), logicalPort.getPositionOnDevice(), logicalPort.getPortType(),
                logicalPort.getOperationalState(), logicalPort.getAdministrativeState(), logicalPort.getUsageState(), orderId)).thenReturn(updatedLogicalPort);

        JSONObject response3 = deviceInstanceRestApis.updateLogicalPortByName(orderId, logicalPortName, logicalPort);
        assertEquals("Success", response3.get("status"));
        assertEquals(updatedLogicalPort, response3.get("logicalPort"));
        //case3rd.1
        logicalPort.setPositionOnCard(null);
        Mockito.when(deviceRepository.findByPortName(port.getName())).thenReturn(deviceDetails);
        port.setPositionOnDevice(logicalPort.getPositionOnDevice());
        Mockito.when(logicalPortRepository.updateLogicalPort(logicalPortName,
                logicalPort.getPositionOnCard(), logicalPort.getPositionOnDevice(), logicalPort.getPortType(),
                logicalPort.getOperationalState(), logicalPort.getAdministrativeState(), logicalPort.getUsageState(), orderId)).thenReturn(updatedLogicalPort);

        JSONObject response4 = deviceInstanceRestApis.updateLogicalPortByName(orderId, logicalPortName, logicalPort);
        assertEquals("Success", response4.get("status"));
        assertEquals(updatedLogicalPort, response4.get("logicalPort"));

        //case-4th
        logicalPort.setPositionOnDevice(null);
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updateLogicalPortByName(orderId, logicalPortName, logicalPort);
        });
        assertEquals("Logical port position on card or device is not provided", e.getMessage());


    }

    @Test
    void UpdatePluggableByNameSuccess() {
        String pluggableName = "testpluggable";
        Pluggable pluggable = new Pluggable();
        pluggable.setName(pluggableName);
        pluggable.setPositionOnDevice(2);
        pluggable.setPositionOnCard(4);

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        //case-1
        Pluggable existingPluggable = new Pluggable();
        existingPluggable.setName("testpluggable");
        existingPluggable.setPositionOnCard(2);
        Mockito.when(pluggableRepository.getPluggable(pluggableName)).thenReturn(existingPluggable);

        //case-2
        Device deviceDetails = new Device();
        deviceDetails.setName("testdevice");
        Mockito.when(deviceRepository.findByPluggableName(pluggableName)).thenReturn(deviceDetails);

        //case-3
        Pluggable pluggabelDetails = new Pluggable();
        Mockito.when(pluggableRepository.updatePluggableOnDevice(pluggable.getName(),
                pluggable.getPositionOnDevice(), pluggable.getVendor(), pluggable.getPluggableModel(),
                pluggable.getPluggablePartNumber(), pluggable.getOperationalState(),
                pluggable.getAdministrativeState(), pluggable.getUsageState(),
                deviceDetails.getName(), orderId)).thenReturn(pluggabelDetails);


        JSONObject response = deviceInstanceRestApis.updatePluggableByName(orderId, pluggableName, pluggable);
        assertEquals("Success", response.get("status"));
        assertEquals(pluggabelDetails, response.get("pluggable"));
    }

    @Test
    void updatePluggableByNameFailureTest() {
        //case-1
        String name = null;
        Pluggable pluggable = new Pluggable();
        String finalPluggableName = name;
        Long orderId = 3456l;
        Exception pluggableNameException = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updatePluggableByName(orderId, finalPluggableName, pluggable);
        });
        assertEquals("Pluggable name field is mandatory", pluggableNameException.getMessage());

        //case-2
        String pluggableName = "testpluggable";
        pluggable.setName("testfinalpluggable");
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(null);
        Exception orderIdException = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updatePluggableByName(orderId, pluggableName, pluggable);
        });
        assertEquals("The order " + orderId + " is not found", orderIdException.getMessage());

        //case-2
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Exception pluggableException = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updatePluggableByName(orderId, pluggableName, pluggable);
        });
        assertEquals("Request parameter does not match with the request body", pluggableException.getMessage());

        //case-3
        pluggable.setName(pluggableName);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(pluggableRepository.getPluggable(pluggableName)).thenReturn(null);

        Exception existingPluggableException = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updatePluggableByName(orderId, pluggableName, pluggable);
        });
        assertEquals("Pluggable with the given name " + pluggableName + " doesn't exist", existingPluggableException.getMessage());

        //case-4
        pluggable.setPositionOnDevice(2);
        Pluggable existingPluggableDetails = new Pluggable();
        existingPluggableDetails.setName("testepluggable");
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Mockito.when(pluggableRepository.getPluggable(pluggableName)).thenReturn(existingPluggableDetails);
        Mockito.when(deviceRepository.findByPluggableName(pluggableName)).thenReturn(null);
        Exception deviceException = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updatePluggableByName(orderId, pluggableName, pluggable);
        });
        assertEquals("Pluggable with the given name " + pluggableName + " doesn't exist on a device to update", deviceException.getMessage());

        //case-5
        pluggable.setPositionOnDevice(0);
        pluggable.setPositionOnCard(4);
        Mockito.when(cardRepository.findByPluggableName(pluggableName)).thenReturn(null);
        Exception cardException = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updatePluggableByName(orderId, pluggableName, pluggable);
        });
        assertEquals("Pluggable with the given name " + pluggableName + " doesn't exist on a card to update", cardException.getMessage());

        //case-6
        Card cardDetails = new Card();
        cardDetails.setName("testcard");
        Mockito.when(cardRepository.findByPluggableName(pluggableName)).thenReturn(cardDetails);
        Port portsOnCardSlot = new Port();
        Mockito.when(portRepository.getPortOnCardByNumber(cardDetails.getName() + "/" +
                pluggable.getPositionOnCard().toString(), pluggable.getPositionOnCard())).thenReturn(portsOnCardSlot);
        Exception portException = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updatePluggableByName(orderId, pluggableName, pluggable);
        });
        assertEquals("A port already exist on the card with the given port number, " + "existingPortDetails: " + portsOnCardSlot, portException.getMessage());

        //case-7
        Mockito.when(portRepository.getPortOnCardByNumber(cardDetails.getName() + "/" +
                pluggable.getPositionOnCard().toString(), pluggable.getPositionOnCard())).thenReturn(null);
        Pluggable pluggableOnCardSlot = new Pluggable();
        pluggableOnCardSlot.setName("testcardslotpluggabel");
        Mockito.when(pluggableRepository.getPluggableOnCardByNumber(cardDetails.getName() + "/" + pluggable.getPositionOnCard().toString(), pluggable.getPositionOnCard())).thenReturn(pluggableOnCardSlot);
        Exception pluggabelOnCardSlotException = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updatePluggableByName(orderId, pluggableName, pluggable);
        });
        assertEquals("A pluggable already exist on the card with the " +
                "given position number, existingPluggableDetails: " +
                pluggableOnCardSlot, pluggabelOnCardSlotException.getMessage());

        //case-8
        Mockito.when(pluggableRepository.getPluggableOnCardByNumber(cardDetails.getName() + "/" + pluggable.getPositionOnCard().toString(), pluggable.getPositionOnCard())).thenReturn(null);
        pluggable.setPositionOnDevice(0);
        pluggable.setPositionOnCard(0);
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updatePluggableByName(orderId, pluggableName, pluggable);
        });
        assertEquals("Pluggable with the given name " + pluggableName + " doesn't exist on a device or card", exception.getMessage());
    }

    @Test
    void updatePluggableByNameSuccess2() {
        String pluggableName = "testpluggable";
        Pluggable pluggable = new Pluggable();
        pluggable.setName(pluggableName);
        pluggable.setPositionOnCard(4);

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        //case-1
        Pluggable existingPluggable = new Pluggable();
        existingPluggable.setName("testpluggable");
        existingPluggable.setPositionOnCard(2);
        Mockito.when(pluggableRepository.getPluggable(pluggableName)).thenReturn(existingPluggable);

        Pluggable pluggabelDetails = new Pluggable();
        // case-4
        Card cardDetails = new Card();
        cardDetails.setName("testcard");
        Mockito.when(cardRepository.findByPluggableName(pluggableName)).thenReturn(cardDetails);

        //case-5
        Mockito.when(portRepository.getPortOnCardByNumber(cardDetails.getName() + "/" +
                pluggable.getPositionOnCard().toString(), pluggable.getPositionOnCard())).thenReturn(null);

        //case-6
        Pluggable pluggableOnCardSlot = new Pluggable();
        pluggableOnCardSlot.setName("testpluggabel");
        Mockito.when(pluggableRepository.getPluggableOnCardByNumber(cardDetails.getName() + "/" + pluggable.getPositionOnCard().toString(), pluggable.getPositionOnCard())).thenReturn(null);
        //case-7
        String slotName = cardDetails.getName() + "/" + existingPluggable.getPositionOnCard().toString();
        doNothing().when(cardSlotRepository).deleteCardSlot(cardDetails.getName() + "/" + existingPluggable.
                getPositionOnCard().toString());
        CardSlot slot = new CardSlot();
        Mockito.when(cardSlotRepository.createCardSlot(cardDetails.getName() + "/" +
                        pluggable.getPositionOnCard().toString(),
                pluggable.getPositionOnCard(), pluggable.getOperationalState(), pluggable.getAdministrativeState(),
                pluggable.getUsageState(), cardDetails.getName())).thenReturn(slot);
        slotName = slot.getName();
        Mockito.when(pluggableRepository.updatePluggable(pluggableName,
                pluggable.getPositionOnCard(), pluggable.getVendor(), pluggable.getPluggableModel(),
                pluggable.getPluggablePartNumber(), pluggable.getOperationalState(),
                pluggable.getAdministrativeState(), pluggable.getUsageState(),
                slotName, orderId)).thenReturn(pluggabelDetails);
        JSONObject response = deviceInstanceRestApis.updatePluggableByName(orderId, pluggableName, pluggable);
        assertEquals("Success", response.get("status"));
        assertEquals(pluggabelDetails, response.get("pluggable"));
    }

    @Test
    void updatePortByNameSuccess() {
        String portName = "testport";
        Port port = new Port();
        port.setName(portName);
        port.setPositionOnDevice(2);
        port.setPositionOnCard(4);

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        //case-1
        Port existingPort = new Port();
        existingPort.setName("testport");
        existingPort.setPositionOnCard(2);
        Mockito.when(portRepository.getPort(portName)).thenReturn(existingPort);

        Port portDetails = new Port();
        //case-2
        Card cardDetails = new Card();
        cardDetails.setName("testcard");
        Mockito.when(cardRepository.getCardFromPortName(portName)).thenReturn(cardDetails);

        //case-5
        String cardName = cardDetails.getName();
        Mockito.when(portRepository.getPortOnCardByNumber(cardName + "/" + existingPort.getPositionOnCard().toString(), port.getPositionOnCard())).thenReturn(null);

        //case-6
        Pluggable pluggableOnCardSlot = new Pluggable();
        pluggableOnCardSlot.setName("testpluggabel");
        Mockito.when(pluggableRepository.getPluggableOnCardByNumber(cardName + "/" + port.getPositionOnCard().toString(), port.getPositionOnCard())).thenReturn(null);
        //case-7
        String cardSlotName = cardName + "/" + existingPort.getPositionOnCard().toString();
        doNothing().when(cardSlotRepository).deleteCardSlot(cardName + "/" + existingPort.getPositionOnCard().toString());
        CardSlot slot = new CardSlot();
        Mockito.when(cardSlotRepository.createCardSlot(cardName + "/" + port.getPositionOnCard().toString(),
                port.getPositionOnCard(), port.getOperationalState(), port.getAdministrativeState(),
                port.getUsageState(), cardName)).thenReturn(slot);
        cardSlotName = slot.getName();
        Mockito.when(portRepository.updatePort(portName,
                port.getPositionOnCard(), port.getPortType(), port.getOperationalState(),
                port.getAdministrativeState(), port.getUsageState(),
                cardSlotName, orderId)).thenReturn(portDetails);
        JSONObject response = deviceInstanceRestApis.updatePortByName(orderId, portName, port);
        assertEquals("Success", response.get("status"));
        assertEquals(portDetails, response.get("port"));
    }

    @Test
    void UpdatePortByNameSuccess2() {
        String portName = "testport";
        Port port = new Port();
        port.setName(portName);
        port.setPositionOnDevice(2);

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        //case-1
        Port existingPort = new Port();
        existingPort.setName("testport");
        Mockito.when(portRepository.getPort(portName)).thenReturn(existingPort);

        Port portDetails = new Port();
        //case-2
        Device deviceDetails = new Device();
        deviceDetails.setName("testdevice");
        Mockito.when(deviceRepository.findByPortName(portName)).thenReturn(deviceDetails);

        //case-3
        Mockito.when(portRepository.updatePortOnDevice(portName,
                port.getPositionOnDevice(), port.getPortType(), port.getOperationalState(),
                port.getAdministrativeState(), port.getUsageState(),
                deviceDetails.getName(), orderId)).thenReturn(portDetails);


        JSONObject response = deviceInstanceRestApis.updatePortByName(orderId, portName, port);
        assertEquals("Success", response.get("status"));
        assertEquals(portDetails, response.get("port"));
    }

    @Test
    void updatePortByNameFailureTest() {
        //case-1
        String name = null;
        Port port = new Port();
        String finalPortName = name;
        Long orderId = 3456l;
        Exception portNameException = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updatePortByName(orderId, finalPortName, port);
        });
        assertEquals("Port name field is mandatory", portNameException.getMessage());

        //case-2
        String portName = "testport";
        port.setName("testfinalport");
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(null);
        Exception OrderIdException = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updatePortByName(orderId, portName, port);
        });
        assertEquals("The order " + orderId + " is not found", OrderIdException.getMessage());

        //case-2
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);
        Exception portException = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updatePortByName(orderId, portName, port);
        });
        assertEquals("Request parameter does not match with the request body", portException.getMessage());

        //case-3
        port.setName(portName);
        Mockito.when(portRepository.getPort(portName)).thenReturn(null);

        Exception existingPluggableException = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updatePortByName(orderId, portName, port);
        });
        assertEquals("Port with the given name " + portName + " doesn't exist", existingPluggableException.getMessage());

        //case-4
        port.setPositionOnCard(2);
        Port existingPortDetails = new Port();
        existingPortDetails.setName("testport");
        existingPortDetails.setPositionOnCard(1);
        Mockito.when(portRepository.getPort(portName)).thenReturn(existingPortDetails);
        Mockito.when(cardRepository.getCardFromPortName(portName)).thenReturn(null);
        Exception deviceException = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updatePortByName(orderId, portName, port);
        });
        assertEquals("Port with the given name " + portName + " doesn't exist on a card to update", deviceException.getMessage());

        //case-5
        Card cardDetails = new Card();
        cardDetails.setName("testcard");
        String cardName = cardDetails.getName();
        Pluggable pluggableOnCardSlot = new Pluggable();
        pluggableOnCardSlot.setName("testcardslotpluggabel");
        Mockito.when(cardRepository.getCardFromPortName(portName)).thenReturn(cardDetails);
        Mockito.when(pluggableRepository.getPluggableOnCardByNumber(cardName + "/" + port.getPositionOnCard().toString(), port.getPositionOnCard())).thenReturn(pluggableOnCardSlot);
        Exception pluggabelOnCardSlotException = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updatePortByName(orderId, portName, port);
        });
        assertEquals("A pluggable already exist on the card with the given " + "port number, existingPluggableDetails: " + pluggableOnCardSlot, pluggabelOnCardSlotException.getMessage());

        //case-6
        Port portsOnCardSlot = new Port();
        portsOnCardSlot.setName("testportsoncardslot");
        Mockito.when(pluggableRepository.getPluggableOnCardByNumber(cardName + "/" + port.getPositionOnCard().toString(), port.getPositionOnCard())).thenReturn(null);
        Mockito.when(portRepository.getPortOnCardByNumber(
                cardName + "/" + existingPortDetails.getPositionOnCard().toString(), port.getPositionOnCard())).thenReturn(portsOnCardSlot);
        Exception portException2 = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updatePortByName(orderId, portName, port);
        });
        assertEquals("A port already exist on the card with the given port number, " + "existingPortDetails: " + portsOnCardSlot, portException2.getMessage());

        //case-7
        port.setPositionOnCard(0);
        port.setPositionOnDevice(3);
        Mockito.when(deviceRepository.findByPortName(portName)).thenReturn(null);
        Exception deviceException2 = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updatePortByName(orderId, portName, port);
        });
        assertEquals("Port with the given name " + portName + " doesn't exist on a device to update", deviceException2.getMessage());

        //case-8
        port.setPositionOnDevice(0);
        port.setPositionOnCard(0);
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updatePortByName(orderId, portName, port);
        });
    }

    @Test
    public void updatePluggableNameNullTest() {
        // Set up the mock behavior (assuming pluggableName is null)
        String pluggableName = null;
        Pluggable pluggable = new Pluggable();
        pluggable.setName("SamplePluggable");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);

        // Call the method to update the pluggable and assert the exception
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updatePluggableByName(orderId, pluggableName, pluggable);
        });

        // Verify the exception message
        Assertions.assertTrue(exception.getMessage().contains("Pluggable name field is mandatory"));
    }

    @Test
    public void updatePluggableNameOrderNotFoundTest() {
        String pluggableName = "SamplePluggable";
        Pluggable pluggable = new Pluggable();
        pluggable.setName("DifferentPluggable");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(null);

        // Call the method to update the pluggable and assert the exception
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updatePluggableByName(orderId, pluggableName, pluggable);
        });

        // Verify the exception message
        Assertions.assertTrue(exception.getMessage().contains("The order " + orderId + " is not found"));
    }

    @Test
    public void updatePluggableNameMismatchTest() {
        // Set up the mock behavior (assuming pluggableName and pluggable have different names)
        String pluggableName = "SamplePluggable";
        Pluggable pluggable = new Pluggable();
        pluggable.setName("DifferentPluggable");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        // Call the method to update the pluggable and assert the exception
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updatePluggableByName(orderId, pluggableName, pluggable);
        });

        // Verify the exception message
        Assertions.assertTrue(exception.getMessage().contains("Request parameter does not match with the request body"));
    }

    @Test
    public void PluggableDoesNotExistExceptionRaised() {
        // Arrange
        String pluggableName = "pluggableName";
        Pluggable pluggable = new Pluggable();
        pluggable.setName("pluggableName");

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        Mockito.when(pluggableRepository.getPluggable(Mockito.anyString())).thenReturn(null);

        // Act
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updatePluggableByName(orderId, pluggableName, pluggable);
        });

        // Verify that the appExceptionHandler was called with the correct error message
        Assertions.assertTrue(e.getMessage().contains("Pluggable with the given name " + pluggableName.toLowerCase() + " doesn't exist"));
    }

    @Test
    public void PluggableDoesNotExistOnDeviceExceptionRaised() {
        // Arrange
        String pluggableName = "pluggableName";
        Pluggable pluggable = new Pluggable();
        pluggable.setName("pluggableName");
        pluggable.setPositionOnDevice(1); // Assuming positionOnDevice is set to a non-null and non-zero value

        OrderEntity order = new OrderEntity();
        Long orderId = 3456l;
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(order.getId())).thenReturn(order);

        Mockito.when(pluggableRepository.getPluggable(Mockito.anyString())).thenReturn(new Pluggable());
        Mockito.when(deviceRepository.findByPluggableName(Mockito.anyString())).thenReturn(null);

        // Act
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            deviceInstanceRestApis.updatePluggableByName(orderId, pluggableName, pluggable);
        });

        // Verify that the appExceptionHandler was called with the correct error message
        Assertions.assertTrue(e.getMessage().contains("Pluggable with the given name " + pluggableName.toLowerCase() +
                " doesn't exist on a device to update"));
    }

    @Test
    void findLogicalPortsOnCardRelationTestSuccess() {
        // Arrange
        String deviceName = "testDevice";
        String Name = deviceName.toLowerCase();
        ArrayList<LogicalPort> LogicalPortOnCard = new ArrayList<>();
        LogicalPortOnCard.add(new LogicalPort());
        Mockito.when(logicalPortRepository.findLogicalPortsOnCardRelation(Name)).thenReturn(LogicalPortOnCard);
        // Act
        ArrayList<LogicalPort> response = deviceInstanceRestApis.findLogicalPortsOnCardRelation(Name);
        // Assert
        assertEquals(LogicalPortOnCard, response);
    }

    @Test
    void findLogicalPortsOnCardRelationTestFailure() {
        // Arrange
        String deviceName = "testDevice";
        String name = deviceName.toLowerCase();
        Mockito.when(logicalPortRepository.findLogicalPortsOnCardRelation(name))
                .thenThrow(new RuntimeException("Failed to fetch LogicalPortOnDevice: " + name));
        // Act and Assert
        ServiceException e = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.findLogicalPortsOnCardRelation(name);
        });
        assertTrue(e.getMessage().contains("Failed to fetch LogicalPortOnDevice: " + name));
    }

    @Test
    void getLogicalPortOnDeviceTestSuccess() {
        // Arrange
        String deviceName = "testDevice";
        String Name = deviceName.toLowerCase();
        ArrayList<LogicalPort> LogicalPortOnDevice = new ArrayList<>();
        LogicalPortOnDevice.add(new LogicalPort());
        Mockito.when(logicalPortRepository.findLogicalPortDevice(Name)).thenReturn(LogicalPortOnDevice);
        // Act
        ArrayList<LogicalPort> Response = deviceInstanceRestApis.getLogicalPortOnDevice(Name);
        // Assert
        assertEquals(LogicalPortOnDevice, Response);
    }

    @Test
    void getLogicalPortOnDeviceTestFailure() {
        // Arrange
        String deviceName = "testDevice";
        String name = deviceName.toLowerCase();
        Mockito.when(logicalPortRepository.findLogicalPortDevice(name))
                .thenThrow(new RuntimeException("Failed to fetch LogicalPortOnDevice: " + name));
        // Act and Assert
        ServiceException e = assertThrows(ServiceException.class, () -> {
            deviceInstanceRestApis.getLogicalPortOnDevice(name);
        });
        assertTrue(e.getMessage().contains("Failed to fetch LogicalPortOnDevice: " + name));
    }


}

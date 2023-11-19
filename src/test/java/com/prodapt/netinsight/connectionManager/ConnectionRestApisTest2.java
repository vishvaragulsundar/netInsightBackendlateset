package com.prodapt.netinsight.connectionManager;

import com.prodapt.netinsight.deviceInstanceManager.*;
import com.prodapt.netinsight.exceptionsHandler.ServiceException;
import com.prodapt.netinsight.orderManager.OrderEntity;
import com.prodapt.netinsight.orderManager.OrderRepository;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("UNIT")
public class ConnectionRestApisTest2 {

    @MockBean
    PhysicalConnectionRepository physicalConnectionRepository;

    @MockBean
    LogicalConnectionRepository logicalConnectionRepository;

    @MockBean
    CardRepository cardRepository;

    @MockBean
    PluggableRepository pluggableRepository;

    @MockBean
    PortRepository portRepository;

    @Autowired
    ConnectionRestApis connectionRestApis;

    @MockBean
    LogicalPortRepository logicalPortRepository;

    @MockBean
    OrderRepository orderRepository;

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalSystemOut = System.out;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalSystemOut);
    }

    @Test
    void createPhysicalConnectionOrderNotFoundTest() {
        PhysicalConnection physicalConnection = new PhysicalConnection();
        physicalConnection.setName("test");
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(null);
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            connectionRestApis.createPhysicalConnection(orderId, physicalConnection);
        });
        assertEquals("The order " + orderId + " is not found", exception.getMessage());
    }

    /**
     * Test to check if correct exception is thrown when physical
     * connection with given name already exists
     **/
    @Test
    void createPhysicalConnectionTestFailConnectionExists() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        PhysicalConnection physicalConnection = new PhysicalConnection();
        physicalConnection.setName("test");
        physicalConnection.setDeviceA("DeviceA");
        physicalConnection.setDeviceZ("DeviceZ");
        physicalConnection.setDeviceAPort("1/1/1");
        physicalConnection.setDeviceZPort("2/1/1");
        physicalConnection.setConnectionType("Test");
        physicalConnection.setBandwidth(1);
        physicalConnection.setConnectionType("Test");
        physicalConnection.setId(1L);

        // Mock the behavior of physicalConnectionRepository
        when(physicalConnectionRepository.getPhysicalConnection(physicalConnection.getName()))
                .thenReturn(physicalConnection);

        //Assert the exception
        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            connectionRestApis.createPhysicalConnection(orderId, physicalConnection);
        });

        assertTrue(e.getMessage().contains("A Physical connection with given name already exist"));
    }

    /**
     * Test to check whether correct
     * exception is thrown out
     * when source end (aEnd) card is null
     **/
    @Test
    void aEndCardisNullTestFail() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        PhysicalConnection physicalConnection = new PhysicalConnection();
        physicalConnection.setName("test");
        physicalConnection.setDeviceA("DeviceA");
        physicalConnection.setDeviceZ("DeviceZ");
        physicalConnection.setDeviceAPort("1/1/1");
        physicalConnection.setDeviceZPort("2/1/1");
        physicalConnection.setConnectionType("Test");
        physicalConnection.setBandwidth(1);
        physicalConnection.setConnectionType("Test");
        physicalConnection.setId(1L);
        cardRepository.getCardOnASlot("Test");
        cardRepository.getCard("Test");
        String[] aEndPortDetails = physicalConnection.getDeviceAPort().split("/");
        int aEndShelfNo = Integer.parseInt(aEndPortDetails[0]);
        int aEndSlotNo = Integer.parseInt(aEndPortDetails[1]);
        int aEndPortNoOnCard = Integer.parseInt(aEndPortDetails[2]);


        // Mock the behavior of physicalConnectionRepository
        when(physicalConnectionRepository.getPhysicalConnection(physicalConnection.getDeviceAPort())).thenReturn(physicalConnection);

        when(cardRepository.getCardOnASlot(any())).thenReturn(null);

        Exception e = assertThrows(ServiceException.class, () -> {
            connectionRestApis.createPhysicalConnection(orderId, physicalConnection);
        });

        assertTrue(e.getMessage().contains("No card exist on the given A-End shelf/slot position"));
    }

    /**
     * Test to check whether correct
     * exception is thrown out
     * when source end (aEnd)port is null
     **/
    @Test
    void aEndDevicePortIsNullTestFail() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        PhysicalConnection physicalConnection = new PhysicalConnection();
        physicalConnection.setName("test");
        physicalConnection.setDeviceA("DeviceA");
        physicalConnection.setDeviceZ("DeviceZ");
        physicalConnection.setDeviceAPort("1/1/1");
        physicalConnection.setDeviceZPort("2/1/1");
        physicalConnection.setConnectionType("Test");
        physicalConnection.setBandwidth(1);
        physicalConnection.setId(1L);

        String[] aEndPortDetails = physicalConnection.getDeviceAPort().split("/");
        int aEndShelfNo = Integer.parseInt(aEndPortDetails[0]);
        int aEndSlotNo = Integer.parseInt(aEndPortDetails[1]);
        int aEndPortNoOnCard = Integer.parseInt(aEndPortDetails[2]);
        Card aEndCard = new Card();

        // Mock the behavior of cardRepository.getCardOnASlot(aEndShelfSlot) to return aEndCard
        Mockito.when(cardRepository.getCardOnASlot(anyString())).thenReturn(aEndCard);

        // Mock the behavior of pluggableRepository.getPluggableOnCardByNumber() and portRepository.getPortOnCardByNumber() to return null
        Mockito.when(pluggableRepository.getPluggableOnCardByNumber(anyString(), anyInt())).thenReturn(null);
        Mockito.when(portRepository.getPortOnCardByNumber(anyString(), anyInt())).thenReturn(null);

        // Invoke the method under test
        Exception e = assertThrows(ServiceException.class, () -> {
            connectionRestApis.createPhysicalConnection(orderId, physicalConnection);
        });

        // Assert the exception message
        assertTrue(e.getMessage().contains("No Port exist on the given A-End shelf/slot/port position"));
    }

    /**
     * Test to check whether name of the port
     * is returned correctly if aEndPort is not null
     **/
    @Test
    void aEndPortNameTestSuccess() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        PhysicalConnection physicalConnection = new PhysicalConnection();
        physicalConnection.setName("test");
        physicalConnection.setDeviceA("DeviceA");
        physicalConnection.setDeviceZ("DeviceZ");
        physicalConnection.setDeviceAPort("1/1/1");
        physicalConnection.setDeviceZPort("2/1/1");
        physicalConnection.setConnectionType("Test");
        physicalConnection.setBandwidth(1);
        physicalConnection.setId(1L);

        // Create a non-null aEndCard object
        Card aEndCard = new Card();
        // Create a non-null aEndDevicePort object
        Port aEndDevicePort = new Port();
        aEndDevicePort.setName("aEndPort");  // Set a valid name for the port

        // Mock the behavior of cardRepository.getCardOnASlot(aEndShelfSlot) to return aEndCard
        Mockito.when(cardRepository.getCardOnASlot(anyString())).thenReturn(aEndCard);

        // Mock the behavior of portRepository.getPortOnCardByNumber(aEndCardSlot, aEndPortNoOnCard) to return aEndDevicePort
        Mockito.when(portRepository.getPortOnCardByNumber(anyString(), anyInt())).thenReturn(aEndDevicePort);

        // Invoke the method under test
        connectionRestApis.createPhysicalConnection(orderId, physicalConnection);

        // Assert the value of aEndPortName or perform additional assertions on the outcome
        assertNotNull(aEndDevicePort.getName());

    }

    /**
     * Test to check whether name of the pluggable
     * is returned correctly if aEndPluggable is not null
     **/
    @Test
    void aEndPluggableTestSuccess() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        PhysicalConnection physicalConnection = new PhysicalConnection();
        physicalConnection.setName("test");
        physicalConnection.setDeviceA("DeviceA");
        physicalConnection.setDeviceZ("DeviceZ");
        physicalConnection.setDeviceAPort("1/1/1");
        physicalConnection.setDeviceZPort("2/1/1");
        physicalConnection.setConnectionType("Test");
        physicalConnection.setBandwidth(1);
        physicalConnection.setId(1L);
        Port aEndDevicePort = new Port();
        // Create a non-null aEndCard object
        Card aEndCard = new Card();

        // Mock the behavior of cardRepository.getCardOnASlot(aEndShelfSlot) to return aEndCard
        Mockito.when(cardRepository.getCardOnASlot(anyString())).thenReturn(aEndCard);

        // Create a non-null aEndPluggable object
        Pluggable aEndPluggable = new Pluggable();
        aEndPluggable.setName("aEndPluggable");  // Set a valid name for the pluggable

        // Mock the behavior of pluggableRepository.getPluggableOnCardByNumber() to return aEndPluggable
        Mockito.when(pluggableRepository.getPluggableOnCardByNumber(anyString(), anyInt())).thenReturn(aEndPluggable);

        // Invoke the method under test
        connectionRestApis.createPhysicalConnection(orderId, physicalConnection);

        // Assert the value of aEndPluggableName or perform additional assertions on the outcome
        assertNotNull(aEndPluggable.getName());
        assertEquals("aEndPluggable", aEndPluggable.getName());
    }

    /**
     * Test to check whether the correct exception
     * is thrown out if aEndPluggable is null
     **/
    @Test
    void AEndDevicePluggableIsNullTestFail() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        // Create a PhysicalConnection object with a deviceAPort without a slash
        PhysicalConnection physicalConnection = new PhysicalConnection();
        physicalConnection.setDeviceAPort("1234"); // Set a deviceAPort without a slash
        physicalConnection.setName("test");
        physicalConnection.setDeviceA("DeviceA");
        physicalConnection.setDeviceZ("DeviceZ");
        physicalConnection.setDeviceZPort("5678");
        physicalConnection.setConnectionType("Test");
        physicalConnection.setBandwidth(1);
        physicalConnection.setId(1L);

        Mockito.when(pluggableRepository.getPluggableOnDeviceByNumber(anyString(), anyInt())).thenReturn(null);
        Mockito.when(portRepository.getPortOnDeviceByNumber(anyString(), anyInt())).thenReturn(null);

        Exception e = assertThrows(ServiceException.class, () -> {
            connectionRestApis.createPhysicalConnection(orderId, physicalConnection);
        });

        assertTrue(e.getMessage().contains("Pluggable on given position on A-End-device doesn't exist"));
    }

    /**
     * Test to check whether name of the pluggable
     * is returned correctly if aEndPluggable is not null
     * and DeviceAPort does not contain a slash
     **/
    @Test
    void NoSlashAEndDevicePluggableNotNullTestPass() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        // Create a PhysicalConnection object with a deviceAPort without a slash
        PhysicalConnection physicalConnection = new PhysicalConnection();
        physicalConnection.setDeviceAPort("1234"); // Set a deviceAPort without a slash
        physicalConnection.setName("test");
        physicalConnection.setDeviceA("DeviceA");
        physicalConnection.setDeviceZ("DeviceZ");
        physicalConnection.setDeviceZPort("5678");
        physicalConnection.setConnectionType("Test");
        physicalConnection.setBandwidth(1);
        physicalConnection.setId(1L);

        // Create a non-null aEndDevicePluggable object
        Pluggable aEndDevicePluggable = new Pluggable();
        aEndDevicePluggable.setName("PluggableName");

        Mockito.when(pluggableRepository.getPluggableOnDeviceByNumber(anyString(), anyInt()))
                .thenReturn(aEndDevicePluggable);

        // Invoke the method under test
        connectionRestApis.createPhysicalConnection(orderId, physicalConnection);

        // Assert the value of aEndPluggableName or perform additional assertions based on the desired outcome
        assertEquals("PluggableName", aEndDevicePluggable.getName());
    }

    /**
     * Test to check whether name of the port
     * is returned correctly if aEndDevicePort is not null
     * and DeviceAPort does not contain a slash
     **/
    @Test
    void AEndDevicePortNotNullTest() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        // Create a PhysicalConnection object with a deviceAPort without a slash
        PhysicalConnection physicalConnection = new PhysicalConnection();
        physicalConnection.setDeviceAPort("1234"); // Set a deviceAPort without a slash
        physicalConnection.setName("test");
        physicalConnection.setDeviceA("DeviceA");
        physicalConnection.setDeviceZ("DeviceZ");
        physicalConnection.setDeviceZPort("5678");
        physicalConnection.setConnectionType("Test");
        physicalConnection.setBandwidth(1);
        physicalConnection.setId(1L);

        // Create a non-null aEndDevicePort object
        Port aEndDevicePort = new Port();
        aEndDevicePort.setName("PortName");

        Mockito.when(portRepository.getPortOnDeviceByNumber(anyString(), anyInt()))
                .thenReturn(aEndDevicePort);

        // Invoke the method under test
        connectionRestApis.createPhysicalConnection(orderId, physicalConnection);

        // Assert the value of aEndPortName or perform additional assertions based on the desired outcome
        assertEquals("PortName", aEndDevicePort.getName());
    }

    /**
     * Test to check whether the correct exception
     * is thrown out if ZEndCard is null
     * and DeviceZPort contains a slash
     **/
    @Test
    public void ZEndCardNullTestFail() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        // Create a PhysicalConnection object
        PhysicalConnection physicalConnection = new PhysicalConnection();
        // Set the required attributes
        physicalConnection.setName("test");
        physicalConnection.setDeviceA("DeviceA");
        physicalConnection.setDeviceZ("DeviceZ");
        physicalConnection.setDeviceAPort("1");
        physicalConnection.setDeviceZPort("2/1/1");
        physicalConnection.setConnectionType("Test");
        physicalConnection.setBandwidth(1);

        // Mock the behavior of physicalConnectionRepository
        when(physicalConnectionRepository.getPhysicalConnection(anyString())).thenReturn(null);

        // Mock the behavior of portRepository
        Port port = new Port();
        when(portRepository.getPortOnDeviceByNumber(anyString(), anyInt())).thenReturn(port);
        when(pluggableRepository.getPluggableOnDeviceByNumber(anyString(), anyInt())).thenReturn(null);

        // Perform the unit test
        Exception exception = assertThrows(ServiceException.class, () -> {
            connectionRestApis.createPhysicalConnection(orderId, physicalConnection);
        });

        // Assert the expected response
        assertTrue(exception.getMessage().contains("No card exist on the given Z-End shelf/slot position"));
    }

    /**
     * Test to check whether the correct exception
     * is thrown out if ZEndDevicePort is null
     * and DeviceZPort contains a slash
     **/
    @Test
    public void ZEndDevicePortIsNullTestFail() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        // Create a PhysicalConnection object
        PhysicalConnection physicalConnection = new PhysicalConnection();
        // Set the required attributes
        physicalConnection.setName("test");
        physicalConnection.setDeviceA("DeviceA");
        physicalConnection.setDeviceZ("DeviceZ");
        physicalConnection.setDeviceAPort("1");
        physicalConnection.setDeviceZPort("2/1/1");
        physicalConnection.setConnectionType("Test");
        physicalConnection.setBandwidth(1);

        // Mock the behavior of physicalConnectionRepository
        when(physicalConnectionRepository.getPhysicalConnection(anyString())).thenReturn(null);


        // Mock the behavior of cardRepository
        Card card = new Card();
        when(cardRepository.getCardOnASlot(anyString())).thenReturn(card);
        when(cardRepository.getCard(anyString())).thenReturn(null);


        // Mock the behavior of portRepository
        Port port = new Port();
        when(portRepository.getPortOnDeviceByNumber(anyString(), anyInt())).thenReturn(port);
        when(pluggableRepository.getPluggableOnDeviceByNumber(anyString(), anyInt())).thenReturn(null);

        // Perform the unit test
        Exception exception = assertThrows(ServiceException.class, () -> {
            connectionRestApis.createPhysicalConnection(orderId, physicalConnection);
        });

        // Assert the expected response
        assertTrue(exception.getMessage().contains("No Port exist on the given Z-End shelf/slot/port position"));
    }


    /**
     * Test to check whether the correct port name is returned if
     * ZEndDevicePort is not null
     * and DeviceZPort contains a slash
     **/
    @Test
    public void zEndDevicePortIsNotNullTest() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        // Create a PhysicalConnection object
        PhysicalConnection physicalConnection = new PhysicalConnection();
        // Set the required attributes
        physicalConnection.setName("test");
        physicalConnection.setDeviceA("DeviceA");
        physicalConnection.setDeviceZ("DeviceZ");
        physicalConnection.setDeviceAPort("1");
        physicalConnection.setDeviceZPort("2/1/1");
        physicalConnection.setConnectionType("Test");
        physicalConnection.setBandwidth(1);

        // Mock the behavior of physicalConnectionRepository
        when(physicalConnectionRepository.getPhysicalConnection(anyString())).thenReturn(null);

        // Mock the behavior of cardRepository
        Card zEndCard = new Card();
        when(cardRepository.getCardOnASlot(anyString())).thenReturn(zEndCard);

        // Mock the behavior of portRepository
        Port zEndDevicePort = new Port();
        zEndDevicePort.setName("ZEndPortName");
        when(portRepository.getPortOnCardByNumber(anyString(), anyInt())).thenReturn(zEndDevicePort);

        // Mock the behavior of pluggableRepository for A-end device
        Pluggable aEndDevicePluggable = new Pluggable(); // Create a mock Pluggable object
        when(pluggableRepository.getPluggableOnDeviceByNumber(
                physicalConnection.getDeviceA(), Integer.parseInt(physicalConnection.getDeviceAPort())))
                .thenReturn(aEndDevicePluggable);

        // Perform the unit test
        connectionRestApis.createPhysicalConnection(orderId, physicalConnection);

        // Assert the expected results
        assertNotNull(zEndDevicePort.getName());

    }

    /**
     * Test to check whether the correct pluggable name is returned if
     * ZEndPluggable is not null
     * and DeviceZPort contains a slash
     **/
    @Test
    public void zEndPluggableIsNotNullTest() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        // Create a PhysicalConnection object
        PhysicalConnection physicalConnection = new PhysicalConnection();
        // Set the required attributes
        physicalConnection.setName("test");
        physicalConnection.setDeviceA("DeviceA");
        physicalConnection.setDeviceZ("DeviceZ");
        physicalConnection.setDeviceAPort("1");
        physicalConnection.setDeviceZPort("2/1/1");
        physicalConnection.setConnectionType("Test");
        physicalConnection.setBandwidth(1);

        // Mock the behavior of physicalConnectionRepository
        when(physicalConnectionRepository.getPhysicalConnection(anyString())).thenReturn(null);

        // Mock the behavior of cardRepository
        Card zEndCard = new Card();
        when(cardRepository.getCardOnASlot(anyString())).thenReturn(zEndCard);

        // Mock the behavior of pluggableRepository
        Pluggable aEndDevicePluggable = new Pluggable(); // Create a mock Pluggable object
        when(pluggableRepository.getPluggableOnDeviceByNumber(
                physicalConnection.getDeviceA(), Integer.parseInt(physicalConnection.getDeviceAPort())))
                .thenReturn(aEndDevicePluggable);

        // Mock the behavior of pluggableRepository for Z-end pluggable
        Pluggable zEndPluggable = new Pluggable(); // Create a mock Pluggable object
        zEndPluggable.setName("ZEndPluggableName");
        when(pluggableRepository.getPluggableOnCardByNumber(anyString(), anyInt()))
                .thenReturn(zEndPluggable);

        // Perform the unit test
        connectionRestApis.createPhysicalConnection(orderId, physicalConnection);

        // Assert the expected results
        assertNotNull(zEndPluggable.getName());
    }

    /**
     * Test to check whether the correct port name is returned if
     * ZEndPDevicePort is not null
     * and DeviceZPort does not contain a slash
     **/
    @Test
    public void zEndDeviceNoSlashPortIsNotNullTest() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        // Create a PhysicalConnection object
        PhysicalConnection physicalConnection = new PhysicalConnection();
        // Set the required attributes
        physicalConnection.setName("test");
        physicalConnection.setDeviceA("DeviceA");
        physicalConnection.setDeviceZ("DeviceZ");
        physicalConnection.setDeviceAPort("1");
        physicalConnection.setDeviceZPort("211");
        physicalConnection.setConnectionType("Test");
        physicalConnection.setBandwidth(1);

        // Mock the behavior of physicalConnectionRepository
        when(physicalConnectionRepository.getPhysicalConnection(anyString())).thenReturn(null);

        // Mock the behavior of cardRepository
        Card zEndCard = new Card();
        when(cardRepository.getCardOnASlot(anyString())).thenReturn(zEndCard);

        // Mock the behavior of portRepository for Z-end device port
        Port zEndDevicePort = new Port(); // Create a mock Port object
        zEndDevicePort.setName("ZEndDevicePortName");
        when(portRepository.getPortOnDeviceByNumber(anyString(), anyInt())).thenReturn(zEndDevicePort);

        // Perform the unit test
        connectionRestApis.createPhysicalConnection(orderId, physicalConnection);

        // Assert the expected results
        assertNotNull("zEndPortName: ", zEndDevicePort.getName());

    }

    /**
     * Test to check whether the correct exception
     * is thrown out if ZEndPDevicePluggable is null
     * and DeviceZPort does not contain a slash
     **/
    @Test
    public void zEndDeviceNoSlashPluggableIsNullTest() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        // Create a PhysicalConnection object
        PhysicalConnection physicalConnection = new PhysicalConnection();
        // Set the required attributes
        physicalConnection.setName("test");
        physicalConnection.setDeviceA("DeviceA");
        physicalConnection.setDeviceZ("DeviceZ");
        physicalConnection.setDeviceAPort("111");
        physicalConnection.setDeviceZPort("2");
        physicalConnection.setConnectionType("Test");
        physicalConnection.setBandwidth(1);

        Pluggable aEndDevicePluggable = new Pluggable();
        aEndDevicePluggable.setName("PluggableName");

        Mockito.when(pluggableRepository.getPluggableOnDeviceByNumber(anyString(), anyInt()))
                .thenReturn(null);

        Card aEndCard = new Card();
        aEndCard.setName("aEndCard");

        // Create a non-null aEndDevicePort object
        Port aEndDevicePort = new Port();
        aEndDevicePort.setName("PortName");

        Port zEndDevicePort = null;
        Pluggable zEndDevicePluggable = null;


        Mockito.when(portRepository.getPortOnDeviceByNumber(anyString(), anyInt()))
                .thenReturn(aEndDevicePort).thenReturn(null);


        // Perform the unit test
        Exception exception = assertThrows(ServiceException.class, () -> {
            connectionRestApis.createPhysicalConnection(orderId, physicalConnection);
        });

        // Assert the expected response
        assertTrue(exception.getMessage().contains("Pluggable on given position on Z-End-device doesn't exist"));
    }


    /**
     * Test to check whether the connection is
     * successfully created if aEndPortName and
     * zEndPortName are not null
     **/
    @Test
    public void createPhysicalConnection_PortNamesNotNull_Success() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        // Prepare test data
        PhysicalConnection physicalConnection = new PhysicalConnection();
        physicalConnection.setName("test");
        physicalConnection.setDeviceA("DeviceA");
        physicalConnection.setDeviceZ("DeviceZ");
        physicalConnection.setDeviceAPort("111");
        physicalConnection.setDeviceZPort("111");
        physicalConnection.setConnectionType("Test");
        physicalConnection.setBandwidth(1);

        String aEndPortName = "PortA";
        String zEndPortName = "PortZ";

        Port aEndDevicePort = new Port();
        aEndDevicePort.setName(aEndPortName);
        Port zEndDevicePort = new Port();
        zEndDevicePort.setName(zEndPortName);
        Pluggable aEndDevicePluggable = new Pluggable();
        aEndDevicePluggable.setName("aEndDevicePluggable");// Set the pluggable object to null
        Pluggable zEndDevicePluggable = new Pluggable();
        zEndDevicePluggable.setName(("zEndDevicePluggable"));// Set the pluggable object to null

        PhysicalConnection createdConnection = new PhysicalConnection();
        createdConnection.setId(1L);
        createdConnection.setName("Test");
        createdConnection.setDeviceA("DeviceA");
        createdConnection.setDeviceZ("DeviceZ");
        createdConnection.setDeviceAPort("111");
        createdConnection.setDeviceZPort("111");
        createdConnection.setConnectionType("Test");
        createdConnection.setBandwidth(1);

        Mockito.when(pluggableRepository.getPluggableOnDeviceByNumber(eq(physicalConnection.getDeviceA()), anyInt()))
                .thenReturn(aEndDevicePluggable).thenReturn(zEndDevicePluggable);
        Mockito.when(portRepository.getPortOnDeviceByNumber(anyString(), anyInt())).thenReturn(aEndDevicePort).thenReturn(zEndDevicePort);

        Mockito.when(physicalConnectionRepository.createPhysicalConnectionOnPort(
                        physicalConnection.getName(), physicalConnection.getDeviceA(), physicalConnection.getDeviceZ(),
                        physicalConnection.getDeviceAPort(), physicalConnection.getDeviceZPort(),
                        physicalConnection.getConnectionType(), physicalConnection.getBandwidth(),
                        aEndPortName, zEndPortName, orderId))
                .thenReturn(createdConnection);

        // Call the method under test
        JSONObject response = connectionRestApis.createPhysicalConnection(orderId, physicalConnection);

        // Verify the response
        assertEquals("Success", response.get("status"));
        assertEquals(createdConnection, response.get("physicalConnection"));
    }

    /**
     * Test to check whether the connection is
     * successfully created if aEndPortName
     * and zEndPortName are null but aEndPluggableName
     * and zEndPluggable Name are not null
     **/
    @Test
    public void createPhysicalConnection_PluggableNamesNotNull_Success() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        // Prepare test data
        PhysicalConnection physicalConnection = new PhysicalConnection();
        physicalConnection.setName("test");
        physicalConnection.setDeviceA("DeviceA");
        physicalConnection.setDeviceZ("DeviceZ");
        physicalConnection.setDeviceAPort("111");
        physicalConnection.setDeviceZPort("111");
        physicalConnection.setConnectionType("Test");
        physicalConnection.setBandwidth(1);

        String aEndPortName = "PortA";
        String zEndPortName = "PortZ";

        Pluggable aEndDevicePluggable = new Pluggable();
        aEndDevicePluggable.setName("aEndDevicePluggable");
        Pluggable zEndDevicePluggable = new Pluggable();
        zEndDevicePluggable.setName("zEndDevicePluggable");

        PhysicalConnection createdConnection = new PhysicalConnection();
        createdConnection.setId(1L);
        createdConnection.setName("Test");
        createdConnection.setDeviceA("DeviceA");
        createdConnection.setDeviceZ("DeviceZ");
        createdConnection.setDeviceAPort("111");
        createdConnection.setDeviceZPort("111");
        createdConnection.setConnectionType("Test");
        createdConnection.setBandwidth(1);

        Mockito.when(pluggableRepository.getPluggableOnDeviceByNumber(eq(physicalConnection.getDeviceA()), anyInt()))
                .thenReturn(aEndDevicePluggable);

        Mockito.when(pluggableRepository.getPluggableOnDeviceByNumber(eq(physicalConnection.getDeviceZ()), anyInt()))
                .thenReturn(zEndDevicePluggable);
        Mockito.when(portRepository.getPortOnDeviceByNumber(physicalConnection.getDeviceZ(),
                Integer.parseInt(physicalConnection.getDeviceZPort()))).thenReturn(null);

        Mockito.when(physicalConnectionRepository.createPhysicalConnectionOnPluggable(
                        any(), any(), any(), any(), any(), any(), any(), any(), any(), anyLong()))
                .thenReturn(createdConnection);

        // Call the method under test
        JSONObject response = connectionRestApis.createPhysicalConnection(orderId, physicalConnection);

        // Verify the response
        assertEquals("Success", response.get("status"));
        assertEquals(createdConnection, response.get("physicalConnection"));
    }

    /**
     * Test to check whether the correct message is printed
     * if the connection is not created due to
     * incompatibility between port and pluggable
     **/
    @Test
    public void IncompatibilityBetweenPortAndPluggable() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        // Create a PhysicalConnection object
        PhysicalConnection physicalConnection = new PhysicalConnection();
        // Set the required attributes
        physicalConnection.setName("test");
        physicalConnection.setDeviceA("DeviceA");
        physicalConnection.setDeviceZ("DeviceZ");
        physicalConnection.setDeviceAPort("111"); // return port
        physicalConnection.setDeviceZPort("211"); // return pluggable
        physicalConnection.setConnectionType("Test");
        physicalConnection.setBandwidth(1);

        Pluggable zEndDevicePluggable = new Pluggable();
        zEndDevicePluggable.setName("zEndDevicePluggable");

        // Mock the behavior of physicalConnectionRepository
        when(physicalConnectionRepository.getPhysicalConnection(anyString())).thenReturn(null);

        Mockito.when(pluggableRepository.getPluggableOnDeviceByNumber(eq(physicalConnection.getDeviceZ()), anyInt()))
                .thenReturn(zEndDevicePluggable);


        // Mock the behavior of cardRepository
        Card aEndCard = new Card();

        // Mock the behavior of cardRepository.getCardOnASlot(aEndShelfSlot) to return aEndCard
        Mockito.when(cardRepository.getCardOnASlot(anyString())).thenReturn(aEndCard);

        // Create a non-null aEndDevicePort object
        Port aEndDevicePort = new Port();
        aEndDevicePort.setName("PortName");

        Mockito.when(portRepository.getPortOnDeviceByNumber(anyString(), anyInt()))
                .thenReturn(aEndDevicePort);
        Mockito.when(portRepository.getPortOnDeviceByNumber(physicalConnection.getDeviceZ(),
                Integer.parseInt(physicalConnection.getDeviceZPort()))).thenReturn(null);

        // Perform the unit test
        JSONObject result = connectionRestApis.createPhysicalConnection(orderId, physicalConnection);

        // Assert the expected response
        assertTrue(result.toString().contains("Failed due to incompatibility between port and pluggable for connection creation"));
    }

    @Test
    void updatePhysicalConnectionByNameOrderNotFoundTest() {
        PhysicalConnection physicalConnection = new PhysicalConnection();
        physicalConnection.setName("test");
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(null);
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            connectionRestApis.updatePhysicalConnectionByName(orderId, "connectionName", physicalConnection);
        });
        assertEquals("The order " + orderId + " is not found", exception.getMessage());
    }

    @Test
    void updatePhysicalConnectionByNameFieldNotFoundTest() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        PhysicalConnection physicalConnection = new PhysicalConnection();
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.updatePhysicalConnectionByName(orderId, "connectionName", physicalConnection);
        });
        Assertions.assertEquals("Physical connection name is mandatory", e.getMessage());
    }

    @Test
    void updatePhysicalConnectionByNameTypeNotFoundTest() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        PhysicalConnection physicalConnection = new PhysicalConnection();
        physicalConnection.setName("connectionName");
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.updatePhysicalConnectionByName(orderId, "connectionName", physicalConnection);
        });
        Assertions.assertEquals("Physical connection type is mandatory", e.getMessage());
    }

    @Test
    void updatePhysicalConnectionByNameParamsMismatchTest() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        PhysicalConnection physicalConnection = new PhysicalConnection();
        physicalConnection.setName("connectionNameTest");
        physicalConnection.setConnectionType("Cable Fiber");
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.updatePhysicalConnectionByName(orderId, "connectionName", physicalConnection);
        });
        Assertions.assertEquals("Request parameter does not match with the request body", e.getMessage());
    }

    @Test
    void updatePhysicalConnectionByNameDoNotExistTest() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        PhysicalConnection physicalConnection = new PhysicalConnection();
        physicalConnection.setName("connectionName");
        physicalConnection.setConnectionType("Cable Fiber");

        Mockito.when(physicalConnectionRepository.getPhysicalConnection(physicalConnection.getName().toLowerCase())).thenReturn(null);
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.updatePhysicalConnectionByName(orderId, "connectionName", physicalConnection);
        });
        Assertions.assertEquals("Physical connection with given name connectionname doesn't exist", e.getMessage());
    }

    @Test
    void updatePhysicalConnectionByNameSuccessTest() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        PhysicalConnection physicalConnection = new PhysicalConnection();
        physicalConnection.setName("connectionName");
        physicalConnection.setConnectionType("Cable Fiber");

        Mockito.when(physicalConnectionRepository.getPhysicalConnection(physicalConnection.getName().toLowerCase())).thenReturn(physicalConnection);
        Mockito.when(physicalConnectionRepository.updatePhysicalConnection(physicalConnection.getName().toLowerCase()
                , physicalConnection.getConnectionType(), physicalConnection.getBandwidth(), orderId)).thenReturn(physicalConnection);
        JSONObject physicalConnectionResponse = connectionRestApis.updatePhysicalConnectionByName(orderId, "connectionName", physicalConnection);

        Assertions.assertEquals("success", physicalConnectionResponse.get("status"));
        Assertions.assertEquals(physicalConnection, physicalConnectionResponse.get("updatedPhysicalConnection"));
    }

    @Test
    void updateLogicalConnectionByNameOrderNotFoundTest() {
        LogicalConnection logicalConnection = new LogicalConnection();
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(null);
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            connectionRestApis.updateLogicalConnectionByName(orderId, "connectionName", logicalConnection);
        });
        assertEquals("The order " + orderId + " is not found", exception.getMessage());
    }

    @Test
    void updateLogicalConnectionByNameFieldNotFoundTest() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        LogicalConnection logicalConnection = new LogicalConnection();
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.updateLogicalConnectionByName(orderId, "connectionName", logicalConnection);
        });
        Assertions.assertEquals("Logical connection name is mandatory", e.getMessage());
    }

    @Test
    void updateLogicalConnectionByNameTypeNotFoundTest() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        LogicalConnection logicalConnection = new LogicalConnection();
        logicalConnection.setName("connectionName");
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.updateLogicalConnectionByName(orderId, "connectionName", logicalConnection);
        });
        Assertions.assertEquals("Logical connection type is mandatory", e.getMessage());
    }

    @Test
    void updateLogicalConnectionByNameBWNotFoundTest() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        LogicalConnection logicalConnection = new LogicalConnection();
        logicalConnection.setName("connectionName");
        logicalConnection.setConnectionType("Ethernet");
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.updateLogicalConnectionByName(orderId, "connectionName", logicalConnection);
        });
        Assertions.assertEquals("Logical connection bandwidth is mandatory", e.getMessage());
    }

    @Test
    void updateLogicalConnectionByNameParamsMismatchTest() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        LogicalConnection logicalConnection = new LogicalConnection();
        logicalConnection.setName("connectionNameTest");
        logicalConnection.setConnectionType("Ethernet");
        logicalConnection.setBandwidth(1000);
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.updateLogicalConnectionByName(orderId, "connectionName", logicalConnection);
        });
        Assertions.assertEquals("Request parameter does not match with the request body", e.getMessage());
    }

    @Test
    void updateLogicalConnectionByNameDoNotExistTest() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        LogicalConnection logicalConnection = new LogicalConnection();
        logicalConnection.setName("connectionName");
        logicalConnection.setConnectionType("Ethernet");
        logicalConnection.setBandwidth(1000);

        Mockito.when(logicalConnectionRepository.getLogicalConnection(logicalConnection.getName().toLowerCase())).thenReturn(null);
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.updateLogicalConnectionByName(orderId, "connectionName", logicalConnection);
        });
        Assertions.assertEquals("No logical connection exists with the name connectionname", e.getMessage());
    }

    @Test
    void updateLogicalConnectionByNameSuccessTest() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        LogicalConnection logicalConnection = new LogicalConnection();
        logicalConnection.setName("connectionName");
        logicalConnection.setConnectionType("Ethernet");
        logicalConnection.setBandwidth(1000);

        Mockito.when(logicalConnectionRepository.getLogicalConnection(logicalConnection.getName().toLowerCase())).thenReturn(logicalConnection);
        Mockito.when(logicalConnectionRepository.updateLogicalConnection(logicalConnection.getName().toLowerCase()
                , logicalConnection.getConnectionType(), logicalConnection.getBandwidth(), orderId)).thenReturn(logicalConnection);
        JSONObject logicalConnectionResponse = connectionRestApis.updateLogicalConnectionByName(orderId, "connectionName", logicalConnection);

        Assertions.assertEquals("success", logicalConnectionResponse.get("status"));
        Assertions.assertEquals(logicalConnection, logicalConnectionResponse.get("updatedLogicalConnection"));
    }

    @Test
    void updatePhysicalConnectionSuccessTest() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        PhysicalConnection physicalConnection = new PhysicalConnection();
        physicalConnection.setName("sampleconnection");
        PhysicalConnection updatedPhysicalCon = new PhysicalConnection();
        updatedPhysicalCon.setName(physicalConnection.getName());
        Mockito.when(physicalConnectionRepository.
                getPhysicalConnection(physicalConnection.getName())).thenReturn(physicalConnection);
        Mockito.when(physicalConnectionRepository.updatePhysicalConnection(physicalConnection.getName(),
                physicalConnection.getConnectionType(), physicalConnection.getBandwidth(), orderId)).thenReturn(updatedPhysicalCon);
        JSONObject Response = connectionRestApis.updatePhysicalConnection(orderId, physicalConnection);
        Assertions.assertEquals("success", Response.get("status"));
        Assertions.assertEquals(updatedPhysicalCon, Response.get("updatedPhysicalConnection"));
    }

    @Test
    void updatePhysicalConnectionOrderNotFoundTest() {
        PhysicalConnection physicalConnection = new PhysicalConnection();
        physicalConnection.setName("test");
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(null);
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            connectionRestApis.updatePhysicalConnection(orderId, physicalConnection);
        });
        assertEquals("The order " + orderId + " is not found", exception.getMessage());
    }

    @Test
    void updatePhysicalConnectionTestFailed() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        PhysicalConnection physicalConnection = new PhysicalConnection();
        physicalConnection.setName("samplephysical");
        Mockito.when(physicalConnectionRepository.
                getPhysicalConnection(physicalConnection.getName())).thenReturn(null);
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.updatePhysicalConnection(orderId, physicalConnection);
        });
        Assertions.assertEquals("A Physical connection with given name doesn't exist", e.getMessage());
    }

    @Test
    void createLogicalConnectionOrderNotFoundTest() {
        LogicalConnection logicalConnection = new LogicalConnection();
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(null);
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            connectionRestApis.createLogicalConnection(orderId, logicalConnection);
        });
        assertEquals("The order " + orderId + " is not found", exception.getMessage());
    }

    @Test
    void createLogicalConnectionNameErrorTest() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        LogicalConnection logicalConnection = new LogicalConnection();
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.createLogicalConnection(orderId, logicalConnection);
        });
        Assertions.assertEquals("Logical connection name is mandatory", e.getMessage());
    }

    @Test
    void createLogicalConnectionAlreadyExistsTest() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        LogicalConnection logicalConnection = new LogicalConnection();
        logicalConnection.setName("connectionName");
        Mockito.when(logicalConnectionRepository.getLogicalConnection(logicalConnection.getName().toLowerCase())).thenReturn(logicalConnection);
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.createLogicalConnection(orderId, logicalConnection);
        });
        Assertions.assertEquals("A Logical connection with given name already exist", e.getMessage());
    }

    @Test
    void createLogicalConnectionDeviceANullTest() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        LogicalConnection logicalConnection = new LogicalConnection();
        logicalConnection.setName("connectionName");
        Mockito.when(logicalConnectionRepository.getLogicalConnection(logicalConnection.getName().toLowerCase())).thenReturn(null);

        Exception e = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.createLogicalConnection(orderId, logicalConnection);
        });
        Assertions.assertEquals("Device A and Device Z are mandatory", e.getMessage());
    }

    @Test
    void createLogicalConnectionDeviceZNullTest() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        LogicalConnection logicalConnection = new LogicalConnection();
        logicalConnection.setName("connectionName");
        logicalConnection.setDeviceA("r1");
        Mockito.when(logicalConnectionRepository.getLogicalConnection(logicalConnection.getName().toLowerCase())).thenReturn(null);

        Exception e = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.createLogicalConnection(orderId, logicalConnection);
        });
        Assertions.assertEquals("Device A and Device Z are mandatory", e.getMessage());
    }

    @Test
    void createLogicalConnectionPhysicalDoesntExistTest() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        LogicalConnection logicalConnection = new LogicalConnection();
        logicalConnection.setName("connectionName");
        logicalConnection.setDeviceA("r1");
        logicalConnection.setDeviceZ("r2");

        ArrayList<String> physicalConnections = new ArrayList<>();
        physicalConnections.add("physicalConnection1");
        physicalConnections.add("physicalConnection2");
        logicalConnection.setPhysicalConnections(physicalConnections);

        Mockito.when(logicalConnectionRepository.getLogicalConnection(logicalConnection.getName().toLowerCase())).thenReturn(null);
        Mockito.when(physicalConnectionRepository.getPhysicalConnection("physicalConnection1"))
                .thenReturn(new PhysicalConnection());
        Mockito.when(physicalConnectionRepository.getPhysicalConnection("physicalConnection2"))
                .thenReturn(null);

        Exception e = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.createLogicalConnection(orderId, logicalConnection);
        });
        Assertions.assertEquals("Physical connection with name physicalConnection2 doesn't exist", e.getMessage());
    }

    @Test
    void createLogicalConnectionNDeviceValidationTest() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        LogicalConnection logicalConnection = new LogicalConnection();
        logicalConnection.setName("connectionName");
        logicalConnection.setDeviceA("r1");
        logicalConnection.setDeviceZ("r2");

        ArrayList<String> physicalConnections = new ArrayList<>();
        physicalConnections.add("physicalConnection1");
        physicalConnections.add("physicalConnection2");
        logicalConnection.setPhysicalConnections(physicalConnections);

        Mockito.when(logicalConnectionRepository.getLogicalConnection(logicalConnection.getName().toLowerCase())).thenReturn(null);
        Mockito.when(physicalConnectionRepository.getPhysicalConnection(any())).thenReturn(new PhysicalConnection());
        Mockito.when(physicalConnectionRepository.getDevicesForConnection(any())).thenReturn(physicalConnections);

        Exception e = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.createLogicalConnection(orderId, logicalConnection);
        });
        Assertions.assertEquals("The given list of physical connections do not connect Device A and Device Z", e.getMessage());
    }

    @Test
    void createLogicalConnectionLogicalAErrorTest() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        LogicalConnection logicalConnection = new LogicalConnection();
        logicalConnection.setName("connectionName");
        logicalConnection.setDeviceA("r1");
        logicalConnection.setDeviceZ("r2");
        logicalConnection.setDeviceALogicalPort("1");

        ArrayList<String> physicalConnections = new ArrayList<>();
        physicalConnections.add("physicalConnection1");
        physicalConnections.add("physicalConnection2");
        logicalConnection.setPhysicalConnections(physicalConnections);

        ArrayList<String> deviceNames = new ArrayList<>(Arrays.asList("r1", "r2"));

        Mockito.when(logicalConnectionRepository.getLogicalConnection(logicalConnection.getName().toLowerCase())).thenReturn(null);
        Mockito.when(physicalConnectionRepository.getPhysicalConnection(any())).thenReturn(new PhysicalConnection());
        Mockito.when(physicalConnectionRepository.getDevicesForConnection(any())).thenReturn(deviceNames);

        Exception e = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.createLogicalConnection(orderId, logicalConnection);
        });
        Assertions.assertEquals("Invalid logical port format on A end", e.getMessage());
    }

    @Test
    void createLogicalConnectionLogicalANullTest() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        LogicalConnection logicalConnection = new LogicalConnection();
        logicalConnection.setName("connectionName");
        logicalConnection.setDeviceA("r1");
        logicalConnection.setDeviceZ("r2");
        logicalConnection.setDeviceALogicalPort("1/1");

        ArrayList<String> physicalConnections = new ArrayList<>();
        physicalConnections.add("physicalConnection1");
        physicalConnections.add("physicalConnection2");
        logicalConnection.setPhysicalConnections(physicalConnections);

        ArrayList<String> deviceNames = new ArrayList<>(Arrays.asList("r1", "r2"));

        Mockito.when(logicalConnectionRepository.getLogicalConnection(logicalConnection.getName().toLowerCase())).thenReturn(null);
        Mockito.when(physicalConnectionRepository.getPhysicalConnection(any())).thenReturn(new PhysicalConnection());
        Mockito.when(physicalConnectionRepository.getDevicesForConnection(any())).thenReturn(deviceNames);
        Mockito.when(logicalPortRepository.getLogicalPortOnDevice(logicalConnection.getDeviceA(), 1, 1))
                .thenReturn(null);

        Exception e = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.createLogicalConnection(orderId, logicalConnection);
        });
        Assertions.assertEquals("Logical port on A end does not exist at the given position on the given " +
                "physical port/pluggable", e.getMessage());
    }

    @Test
    void createLogicalConnectionLogicalZErrorTest() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        LogicalConnection logicalConnection = new LogicalConnection();
        logicalConnection.setName("connectionName");
        logicalConnection.setDeviceA("r1");
        logicalConnection.setDeviceZ("r2");
        logicalConnection.setDeviceALogicalPort("1/1");
        logicalConnection.setDeviceZLogicalPort("2");

        ArrayList<String> physicalConnections = new ArrayList<>();
        physicalConnections.add("physicalConnection1");
        physicalConnections.add("physicalConnection2");
        logicalConnection.setPhysicalConnections(physicalConnections);

        ArrayList<String> deviceNames = new ArrayList<>(Arrays.asList("r1", "r2"));

        Mockito.when(logicalConnectionRepository.getLogicalConnection(logicalConnection.getName().toLowerCase())).thenReturn(null);
        Mockito.when(physicalConnectionRepository.getPhysicalConnection(any())).thenReturn(new PhysicalConnection());
        Mockito.when(physicalConnectionRepository.getDevicesForConnection(any())).thenReturn(deviceNames);
        Mockito.when(logicalPortRepository.getLogicalPortOnDevice(logicalConnection.getDeviceA(), 1, 1))
                .thenReturn(new LogicalPort());

        Exception e = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.createLogicalConnection(orderId, logicalConnection);
        });
        Assertions.assertEquals("Invalid logical port format on Z end", e.getMessage());
    }

    @Test
    void createLogicalConnectionLogicalZNullTest() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        LogicalConnection logicalConnection = new LogicalConnection();
        logicalConnection.setName("connectionName");
        logicalConnection.setDeviceA("r1");
        logicalConnection.setDeviceZ("r2");
        logicalConnection.setDeviceALogicalPort("1/1/2/3");
        logicalConnection.setDeviceZLogicalPort("2/5");

        ArrayList<String> physicalConnections = new ArrayList<>();
        physicalConnections.add("physicalConnection1");
        physicalConnections.add("physicalConnection2");
        logicalConnection.setPhysicalConnections(physicalConnections);

        ArrayList<String> deviceNames = new ArrayList<>(Arrays.asList("r1", "r2"));

        Mockito.when(logicalConnectionRepository.getLogicalConnection(logicalConnection.getName().toLowerCase())).thenReturn(null);
        Mockito.when(physicalConnectionRepository.getPhysicalConnection(any())).thenReturn(new PhysicalConnection());
        Mockito.when(physicalConnectionRepository.getDevicesForConnection(any())).thenReturn(deviceNames);
        Mockito.when(logicalPortRepository.getLogicalOnCardPort(logicalConnection.getDeviceA(), 1, 1, 2,
                3)).thenReturn(new LogicalPort());
        Mockito.when(logicalPortRepository.getLogicalPortOnDevice(logicalConnection.getDeviceZ(), 2, 5))
                .thenReturn(null);

        Exception e = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.createLogicalConnection(orderId, logicalConnection);
        });
        Assertions.assertEquals("Logical port on Z end does not exist at the given position on the given " +
                "physical port/pluggable", e.getMessage());
    }

    @Test
    void createLogicalConnectionSuccessTest() {
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        LogicalConnection logicalConnection = new LogicalConnection();
        logicalConnection.setName("connectionName");
        logicalConnection.setConnectionType("LSP");
        logicalConnection.setDeviceA("r1");
        logicalConnection.setDeviceZ("r2");
        logicalConnection.setDeviceALogicalPort("1/1/2/3");
        logicalConnection.setDeviceZLogicalPort("2/5/1/2");

        ArrayList<String> physicalConnections = new ArrayList<>();
        physicalConnections.add("physicalConnection1");
        physicalConnections.add("physicalConnection2");
        logicalConnection.setPhysicalConnections(physicalConnections);

        ArrayList<String> deviceNames = new ArrayList<>(Arrays.asList("r1", "r2"));

        LogicalPort logicalPort = new LogicalPort();
        logicalPort.setName("logicalPort");

        Mockito.when(logicalConnectionRepository.getLogicalConnection(logicalConnection.getName().toLowerCase())).thenReturn(null);
        Mockito.when(physicalConnectionRepository.getPhysicalConnection(any())).thenReturn(new PhysicalConnection());
        Mockito.when(physicalConnectionRepository.getDevicesForConnection(any())).thenReturn(deviceNames);
        Mockito.when(logicalPortRepository.getLogicalOnCardPort(logicalConnection.getDeviceA(), 1, 1, 2,
                3)).thenReturn(logicalPort);
        Mockito.when(logicalPortRepository.getLogicalOnCardPort(logicalConnection.getDeviceZ(), 2, 5, 1,
                2)).thenReturn(logicalPort);
        Mockito.when(logicalConnectionRepository.createLogicalConnectionOnPort("connectionname", logicalConnection.getDeviceA(),
                        logicalConnection.getDeviceZ(), logicalPort.getName(), logicalPort.getName(), logicalConnection.getConnectionType(),
                        logicalConnection.getBandwidth(), deviceNames, logicalConnection.getPhysicalConnections(), orderId))
                .thenReturn(logicalConnection);
        connectionRestApis.createLogicalConnection(orderId, logicalConnection);
    }

    @Test
    void getAllPhysicalConnectionsSuccess() {
        ArrayList<PhysicalConnection> demophysicalConnections = new ArrayList<>();
        PhysicalConnection physicalConnection = new PhysicalConnection();
        demophysicalConnections.add(physicalConnection);
        Mockito.when(physicalConnectionRepository.getAllPhysicalConnections()).thenReturn(demophysicalConnections);
        ArrayList<PhysicalConnection> result = connectionRestApis.getAllPhysicalConnections();
        Assertions.assertEquals(demophysicalConnections, result);
    }

    @Test
    void testgetAllPhysicalConnection_Failure() {
        Mockito.when(physicalConnectionRepository.getAllPhysicalConnections()).thenThrow(new RuntimeException("Failed to fetch PhysicalConnection"));
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.getAllPhysicalConnections();
        });
        String expectedMessage = "Failed to fetch PhysicalConnection";
        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void getAllLogicalConnectionsSuccess() {
        ArrayList<LogicalConnection> demologicalConnections = new ArrayList<>();
        LogicalConnection logicalConnection = new LogicalConnection();
        demologicalConnections.add(logicalConnection);
        Mockito.when(logicalConnectionRepository.getAllLogicalConnections()).thenReturn(demologicalConnections);
        ArrayList<LogicalConnection> result = connectionRestApis.getAllLogicalConnections();
        Assertions.assertEquals(demologicalConnections, result);
    }

    @Test
    void testgetAllLogicalConnections_Failure() {
        Mockito.when(logicalConnectionRepository.getAllLogicalConnections()).thenThrow(new RuntimeException("Failed to fetch LogicalConnection"));
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.getAllLogicalConnections();
        });
        String expectedMessage = "Failed to fetch LogicalConnection";
        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void getShortestPathPhysicalConnectionsFailureTest() {
        Exception e1 = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.getShortestPathPhysicalConnections("  ", "deviceZ");
        });
        Assertions.assertEquals("Device A and Device Z are mandatory", e1.getMessage());

        Exception e2 = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.getShortestPathPhysicalConnections("deviceA", "   ");
        });
        Assertions.assertEquals("Device A and Device Z are mandatory", e2.getMessage());

        Exception e3 = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.getShortestPathPhysicalConnections(null, "   ");
        });
        Assertions.assertEquals("Device A and Device Z are mandatory", e3.getMessage());

        Exception e4 = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.getShortestPathPhysicalConnections("deviceA", null);
        });
        Assertions.assertEquals("Device A and Device Z are mandatory", e4.getMessage());
    }

    @Test
    void getShortestPathPhysicalConnectionsSuccessTest() {
        ArrayList<PhysicalConnection> physicalConnections = new ArrayList<>();
        PhysicalConnection physicalConnection = new PhysicalConnection();
        physicalConnections.add(physicalConnection);
        Mockito.when(physicalConnectionRepository.getShortestPathPhysicalConnections("deviceA", "deviceZ"))
                .thenReturn(physicalConnections);
        ArrayList<PhysicalConnection> physicalConnectionArrayList =
                connectionRestApis.getShortestPathPhysicalConnections("deviceA", "deviceZ");
        assertEquals(1, physicalConnectionArrayList.size());
    }

    @Test
    public void findLogicalConnectionByDevice() {
        ArrayList<LogicalConnection> logicalConnections = new ArrayList<>();
        String device = "test1";
        LogicalConnection connection = new LogicalConnection();
        connection.setDeviceA(device);
        connection.setDeviceZ("test1");
        logicalConnections.add(connection);
        Mockito.when(logicalConnectionRepository.findLogicalConnectionByDevice(device)).thenReturn(logicalConnections);
        ArrayList<LogicalConnection> response = connectionRestApis.findLogicalConnectionByDevice(device);
        Assertions.assertEquals(logicalConnections, response);
    }

    @Test
    void findLogicalConnectionByDeviceFailure() {
        String deviceName = "testdevice";
        Mockito.when(logicalConnectionRepository.findLogicalConnectionByDevice(deviceName)).thenReturn(null);

        Exception e = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.findLogicalConnectionByDevice(deviceName);
        });
        Assertions.assertEquals("Given device name is not associated with any logical Connection", e.getMessage());
    }


    @Test
    public void findPhysicalConnectionByDevice_Success() {
        ArrayList<PhysicalConnection> physicalConnections = new ArrayList<>();
        String device = "testDevice";
        PhysicalConnection connection = new PhysicalConnection();
        connection.setDeviceA(device);
        connection.setDeviceZ("testDevice2");
        physicalConnections.add(connection);
        Mockito.when(physicalConnectionRepository.findPhysicalConnectionByDevice(device.toLowerCase())).thenReturn(physicalConnections);
        ArrayList<PhysicalConnection> response = connectionRestApis.findPhysicalConnectionByDevice(device);
        Assertions.assertEquals(physicalConnections, response);
    }

    @Test
    void findPhysicalConnectionByDeviceFailure() {
        String deviceName = "testdevice";
        Mockito.when(physicalConnectionRepository.findPhysicalConnectionByDevice(deviceName)).thenReturn(null);

        Exception e = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.findPhysicalConnectionByDevice(deviceName);
        });
        Assertions.assertEquals("Given device name is not associated with any Physical Connection", e.getMessage());
    }

    @Test
    void getPhysicalConnectionSuccess() {
        String connectionName = "test";
        PhysicalConnection physicalConnection = new PhysicalConnection();
        physicalConnection.setName(connectionName);

        Mockito.when(physicalConnectionRepository.getByName(connectionName)).thenReturn(physicalConnection);

        // Act
        PhysicalConnection actualConnection = connectionRestApis.findPhysicalConnection(connectionName);

        // Assert
        Assertions.assertEquals(physicalConnection, actualConnection);
    }

    @Test
    void getPhysicalConnectionFailure() {
        String connectionName = "test";
        Mockito.when(physicalConnectionRepository.getByName(connectionName)).thenReturn(null);

        Exception e = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.findPhysicalConnection(connectionName);
        });
        Assertions.assertEquals("Given Physical Connection is not Found", e.getMessage());
    }

    @Test
    void getLogicalConnectionSuccess() {
        String connectionName = "test";
        LogicalConnection logicalConnection = new LogicalConnection();
        logicalConnection.setName(connectionName);

        Mockito.when(logicalConnectionRepository.getByName(connectionName)).thenReturn(logicalConnection);

        // Act
        LogicalConnection actualConnection = connectionRestApis.findLogicalConnection(connectionName);

        // Assert
        Assertions.assertEquals(logicalConnection, actualConnection);
    }

    @Test
    void getLogicalConnectionFailure() {
        String connectionName = "test";
        Mockito.when(logicalConnectionRepository.getByName(connectionName)).thenReturn(null);

        Exception e = Assertions.assertThrows(Exception.class, () -> {
            connectionRestApis.findLogicalConnection(connectionName);
        });
        Assertions.assertEquals("Given logical Connection is not Found", e.getMessage());
    }
}






















package com.prodapt.netinsight.serviceManager;

import com.prodapt.netinsight.connectionManager.LogicalConnection;
import com.prodapt.netinsight.connectionManager.LogicalConnectionRepository;
import com.prodapt.netinsight.connectionManager.PhysicalConnection;
import com.prodapt.netinsight.connectionManager.PhysicalConnectionRepository;
import com.prodapt.netinsight.customerManager.Customer;
import com.prodapt.netinsight.customerManager.CustomerRepository;
import com.prodapt.netinsight.deviceInstanceManager.Device;
import com.prodapt.netinsight.deviceInstanceManager.DeviceRepository;
import com.prodapt.netinsight.exceptionsHandler.ServiceException;
import com.prodapt.netinsight.orderManager.OrderEntity;
import com.prodapt.netinsight.orderManager.OrderRepository;
import com.prodapt.netinsight.uiHelper.ServiceType;
import com.prodapt.netinsight.uiHelper.ServiceTypeRepo;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ActiveProfiles("UNIT")
class ServiceRestApisTest {

    @Autowired
    ServiceRestApis serviceRestApis;

    @MockBean
    ServiceRepository serviceRepository;
    @MockBean
    ServiceTypeRepo serviceTypeRepo;

    @MockBean
    CustomerRepository customerRepository;

    @MockBean
    DeviceRepository deviceRepository;
    @MockBean
    PhysicalConnectionRepository physicalConnectionRepository;

    @MockBean
    LogicalConnectionRepository logicalConnectionRepository;

    @MockBean
    OrderRepository orderRepository;

    @Test
    void createServiceNameNotFoundTest() {
        Service service = new Service();
        service.setName("  ");
        Long orderId = 3456l;
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            serviceRestApis.createService(orderId, service);
        });
        assertEquals("Service name cannot be empty", exception.getMessage());
    }

    @Test
    void createServiceOrderNotFoundTest() {
        Service service = new Service();
        service.setName("New Service");
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(null);
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            serviceRestApis.createService(orderId, service);
        });
        assertEquals("The Order" + orderId + "is not found", exception.getMessage());
    }

    @Test
    void createServiceWithExistingNameTest() {
        Service service = new Service();
        service.setName("New Service");
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);
        Mockito.when(serviceRepository.findByName("new service")).thenReturn(service);
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            serviceRestApis.createService(orderId, service);
        });
        assertEquals("A service already exists with the name new service", exception.getMessage());
    }

    @Test
    void createServicePhysicalConnectionNotFound() {
        Service service = new Service();
        service.setName("New Service");
        ArrayList<String> physicalConnections = new ArrayList<>();
        physicalConnections.add("connection1");
        physicalConnections.add("connection2");
        service.setPhysicalConnections(physicalConnections);
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);
        Mockito.when(serviceRepository.findByName("new service")).thenReturn(null);

        for (String validatePhysicalConnection : physicalConnections) {
            Mockito.when(physicalConnectionRepository.findPhysicalconnectionbyName(validatePhysicalConnection)).thenReturn(null);
        }

        String connectionsAsString = physicalConnections.get(0);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            serviceRestApis.createService(orderId, service);
        });
        assertEquals("Given Physical Connection is not found : " + connectionsAsString, exception.getMessage());
    }

    @Test
    void createServiceLogicalConnectionNotFound() {
        Service service = new Service();
        service.setName("New Service");
        ArrayList<String> physicalConnections = new ArrayList<>();
        physicalConnections.add("connection1");
        physicalConnections.add("connection2");
        service.setPhysicalConnections(physicalConnections);
        ArrayList<String> logicalConnections = new ArrayList<>();
        logicalConnections.add("connection1");
        logicalConnections.add("connection2");
        service.setLogicalConnections(logicalConnections);
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);
        Mockito.when(serviceRepository.findByName("new service")).thenReturn(null);

        PhysicalConnection existingPhysicalConnections = new PhysicalConnection();
        LogicalConnection existingLogicalConnections = new LogicalConnection();

        for (String validatePhysicalConnection : physicalConnections) {
            Mockito.when(physicalConnectionRepository.findPhysicalconnectionbyName(validatePhysicalConnection)).thenReturn(existingPhysicalConnections);
        }

        for (String validateLogicalConnection : logicalConnections) {
            Mockito.when(logicalConnectionRepository.getLogicalConnection(validateLogicalConnection)).thenReturn(null);
        }

        String connectionsAsString = logicalConnections.get(0);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            serviceRestApis.createService(orderId, service);
        });
        assertEquals("Given Logical Connection is not found : " + connectionsAsString, exception.getMessage());
    }

    @Test
    void createServiceSuccessTest() {
        Service service = new Service();
        service.setName("New Service");
        service.setType("sample");

        ArrayList<String> physicalConnections = new ArrayList<>();
        physicalConnections.add("connection1");
        physicalConnections.add("connection2");
        service.setPhysicalConnections(physicalConnections);

        ArrayList<String> logicalConnections = new ArrayList<>();
        logicalConnections.add("connection1");
        logicalConnections.add("connection2");

        service.setLogicalConnections(logicalConnections);
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);
        Mockito.when(serviceRepository.findByName("new service")).thenReturn(null);

        PhysicalConnection existingPhysicalConnections = new PhysicalConnection();
        LogicalConnection existingLogicalConnections = new LogicalConnection();

        for (String validatePhysicalConnection : physicalConnections) {
            Mockito.when(physicalConnectionRepository.findPhysicalconnectionbyName(validatePhysicalConnection)).thenReturn(existingPhysicalConnections);
        }

        for (String validateLogicalConnection : logicalConnections) {
            Mockito.when(logicalConnectionRepository.getLogicalConnection(validateLogicalConnection)).thenReturn(existingLogicalConnections);
        }

        Mockito.when(serviceRepository.findByName("new service")).thenReturn(null);
        Mockito.when(serviceRepository.createService("new service", null, null,
                null, null, null, null, orderId)).thenReturn(service);
        ;

        Mockito.when(serviceTypeRepo.save(any())).thenReturn(new ServiceType("sample"));

        assertEquals("success", serviceRestApis.createService(orderId, service).get("status"));
    }

    @Test
    void deleteServiceSuccessTest() {
        Service service = new Service();
        service.setName("New Service");
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        Mockito.when(serviceRepository.findByName("new service")).thenReturn(service);
        assertEquals("success", serviceRestApis.deleteService(orderId, "new service").get("status"));
    }

    @Test
    void deleteServiceNameNotFoundTest() {
        Long orderId = 3456l;
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            serviceRestApis.deleteService(orderId, "  ");
        });
        assertEquals("Service name cannot be empty", exception.getMessage());
    }

    @Test
    void deleteServiceOrderNotFoundTest() {
        Service service = new Service();
        service.setName("New Service");
        Long orderId = 3456l;
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(null);
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            serviceRestApis.deleteService(orderId, "new service");
        });
        assertEquals("The Order" + orderId + "is not found", exception.getMessage());
    }

    @Test
    void deleteServiceNotFoundTest() {
        Service service = new Service();
        service.setName("New Service");
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);
        Mockito.when(serviceRepository.findByName("new service")).thenReturn(null);
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            serviceRestApis.deleteService(orderId, "new service");
        });
        assertEquals("No service exists with the name new service", exception.getMessage());
    }

    @Test
    void getAllServicesSuccess() {
        ArrayList<Service> demoService = new ArrayList<>();
        Service service = new Service();
        demoService.add(service);
        Device device = new Device();
        device.setName("device01");
        Mockito.when(serviceRepository.getAllServices(device.getName().toLowerCase())).thenReturn(demoService);
        List<Service> result = serviceRestApis.getAllServices(device.getName());
        Assertions.assertEquals(demoService, result);
    }

    @Test
    void getAllServices_Failure() {
        Device device = new Device();
        device.setName("device01");
        Mockito.when(serviceRepository.getAllServices(device.getName().toLowerCase())).thenReturn(null);
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            serviceRestApis.getAllServices(device.getName());
        });
        Assertions.assertEquals("Given device name is not associated with any service : " + device.getName().toLowerCase(), exception.getMessage());
    }

    @Test
    void getAllServicesWithAdditionalAttributeSuccess() {
        ArrayList<Service> demoService = new ArrayList<>();
        Service service = new Service();
        demoService.add(service);
        Device device = new Device();
        device.setName("device01");
        Mockito.when(serviceRepository.getAllServices(device.getName().toLowerCase())).thenReturn(demoService);
        List<Service> result = serviceRestApis.getAllServicesWithAdditionalAttribute(device.getName());
        Assertions.assertEquals(demoService, result);
    }

    @Test
    void getAllServicesWithAdditionalAttribute_Failure() {
        Device device = new Device();
        device.setName("device01");
        Mockito.when(serviceRepository.getAllServices(device.getName().toLowerCase())).thenReturn(null);
        ;
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            serviceRestApis.getAllServicesWithAdditionalAttribute(device.getName());
        });
        Assertions.assertEquals("Given device name is not associated with any service : " + device.getName().toLowerCase(), exception.getMessage());
    }

    @Test
    void updateServiceNameNullTest() {
        Long orderId = 3456l;
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            serviceRestApis.updateService(orderId, "new service", new Service());
        });
        assertEquals("Service name cannot be empty", exception.getMessage());
    }

    @Test
    void updateServiceOrderNotFoundTest() {
        Service service = new Service();
        service.setName("New Service");
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(null);
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            serviceRestApis.deleteService(orderId, "new service");
        });
        assertEquals("The Order" + orderId + "is not found", exception.getMessage());
    }

    @Test
    void updateServiceDoesntExistTest() {
        Service service = new Service();
        service.setName("New Service");
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);
        Mockito.when(serviceRepository.findByName("new service")).thenReturn(null);
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            serviceRestApis.updateService(orderId, "new service", service);
        });
        assertEquals("Service new service doesn't exist to update", exception.getMessage());
    }

    @Test
    void updateServicePhysicalConnectionNotFound() {
        Service service = new Service();
        service.setName("New Service");
        ArrayList<String> physicalConnections = new ArrayList<>();
        physicalConnections.add("connection1");
        physicalConnections.add("connection2");
        service.setPhysicalConnections(physicalConnections);

        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        Service existingService = new Service();
        Mockito.when(serviceRepository.findByName("new service")).thenReturn(existingService);

        for (String validatePhysicalConnection : physicalConnections) {
            Mockito.when(physicalConnectionRepository.findPhysicalconnectionbyName(validatePhysicalConnection.toLowerCase())).thenReturn(null);
        }

        String connectionsAsString = physicalConnections.get(0);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            serviceRestApis.updateService(orderId, "New Service", service);
        });
        assertEquals("Given Physical Connection is not found : " + connectionsAsString, exception.getMessage());
    }

    @Test
    void updateServiceLogicalConnectionNotFound() {
        Service service = new Service();
        service.setName("New Service");
        ArrayList<String> physicalConnections = new ArrayList<>();
        physicalConnections.add("connection1");
        physicalConnections.add("connection2");
        service.setPhysicalConnections(physicalConnections);
        ArrayList<String> logicalConnections = new ArrayList<>();
        logicalConnections.add("connection1");
        logicalConnections.add("connection2");
        service.setLogicalConnections(logicalConnections);

        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        Service existingService = new Service();
        Mockito.when(serviceRepository.findByName("new service")).thenReturn(existingService);

        PhysicalConnection existingPhysicalConnections = new PhysicalConnection();

        for (String validatePhysicalConnection : physicalConnections) {
            Mockito.when(physicalConnectionRepository.findPhysicalconnectionbyName(validatePhysicalConnection)).thenReturn(existingPhysicalConnections);
        }

        for (String validateLogicalConnection : logicalConnections) {
            Mockito.when(logicalConnectionRepository.getLogicalConnection(validateLogicalConnection)).thenReturn(null);
        }

        // Convert the ArrayList to a comma-separated string for the error message
        String connectionsAsString = logicalConnections.get(0);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            serviceRestApis.updateService(orderId, "New Service", service);
        });
        assertEquals("Given Logical Connection is not found : " + connectionsAsString, exception.getMessage());
    }

    @Test
    void updateServiceUpdateLogicalTest() {
        Service service = new Service();
        service.setName("New Service");
        service.setType("sample");
        service.setLogicalConnections(new ArrayList<>(Collections.singleton("logical1")));

        Service existingService = new Service();

        ArrayList<String> physicalConnections = new ArrayList<>();
        physicalConnections.add("connection1");
        service.setPhysicalConnections(physicalConnections);

        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        Mockito.when(serviceRepository.findByName("new service")).thenReturn(existingService);

        PhysicalConnection existingPhysicalConnections = new PhysicalConnection();
        LogicalConnection existingLogicalConnections = new LogicalConnection();

        for (String validatePhysicalConnection : physicalConnections) {
            Mockito.when(physicalConnectionRepository.findPhysicalconnectionbyName(validatePhysicalConnection)).thenReturn(existingPhysicalConnections);
        }

        for (String validateLogicalConnection : service.getLogicalConnections()) {
            Mockito.when(logicalConnectionRepository.getLogicalConnection(validateLogicalConnection)).thenReturn(existingLogicalConnections);
        }

        Mockito.when(serviceRepository.updateService("new service", "sample", null, null,
                null, service.getPhysicalConnections(), service.getLogicalConnections(), orderId)).thenReturn(service);
        JSONObject response = serviceRestApis.updateService(orderId, "new service", service);
        Mockito.when(serviceTypeRepo.save(any())).thenReturn(new ServiceType("sample"));
        assertEquals("success", response.get("status"));
    }

    @Test
    void updateServiceUpdatePhysicalTest() {
        Service service = new Service();
        service.setName("New Service");
        service.setPhysicalConnections(new ArrayList<>(Collections.singleton("physical1")));
        service.setType("sample");

        Service existingService = new Service();

        ArrayList<String> logicalConnections = new ArrayList<>();
        logicalConnections.add("connection1");
        service.setLogicalConnections(logicalConnections);

        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        Mockito.when(serviceRepository.findByName("new service")).thenReturn(existingService);

        PhysicalConnection existingPhysicalConnections = new PhysicalConnection();
        LogicalConnection existingLogicalConnections = new LogicalConnection();

        for (String validatePhysicalConnection : service.getPhysicalConnections()) {
            Mockito.when(physicalConnectionRepository.findPhysicalconnectionbyName(validatePhysicalConnection)).thenReturn(existingPhysicalConnections);
        }

        for (String validateLogicalConnection : logicalConnections) {
            Mockito.when(logicalConnectionRepository.getLogicalConnection(validateLogicalConnection)).thenReturn(existingLogicalConnections);
        }

        Mockito.when(serviceRepository.updateService("new service", "sample", null, null,
                null, service.getPhysicalConnections(), service.getLogicalConnections(), orderId)).thenReturn(service);
        Mockito.when(serviceTypeRepo.save(any())).thenReturn(new ServiceType("sample"));
        JSONObject response = serviceRestApis.updateService(orderId, "new service", service);
        assertEquals("success", response.get("status"));
    }

    @Test
    void getServiceSuccess() {
        String serviceName = "testService";
        Service service = new Service();
        service.setName(serviceName);

        Mockito.when(serviceRepository.getServiceByName(serviceName.toLowerCase())).thenReturn(service);

        Service actualService = serviceRestApis.findServiceByName(serviceName);
        Assertions.assertEquals(service, actualService);
    }

    @Test
    void getServiceFailure() {
        String serviceName = "testService";
        Service service = new Service();
        service.setName(serviceName);

        Mockito.when(serviceRepository.findByName(service.getName().toLowerCase())).thenReturn(null);

        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            serviceRestApis.findServiceByName(serviceName);
        });
        Assertions.assertEquals("Given Service is not Found : " + service.getName(), e.getMessage());
    }

    @Test
    void testAssociateServiceToOtherAttributes() {
        Service service = new Service();
        String serviceName = "New Service";
        String customerName = "customer1";
        service.setName(serviceName);
        service.setType("sample");

        ArrayList<Device> Devices = new ArrayList<>();
        Device device = new Device();
        device.setName("device1");
        Devices.add(device);

        Service existingService = new Service();
        existingService.setDevices(Devices);

        Customer customer = new Customer();
        customer.setName("customer1");

        service.setCustomer(customer);
        service.setDevices(Devices);

        Customer existingCustomer = new Customer();
        Device existingDevice = new Device();

        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        Mockito.when(serviceRepository.findByName(serviceName.toLowerCase())).thenReturn(existingService);

        Mockito.when(customerRepository.findByCustomerName(customerName)).thenReturn(existingCustomer);

        ArrayList<String> deviceName = new ArrayList<>();
        for (Device validateConnections : service.getDevices()) {
            String deviceConnection = validateConnections.getName();
            Mockito.when(deviceRepository.findByName(deviceConnection)).thenReturn(existingDevice);
            deviceName.add(device.getName());
        }

        Mockito.when(serviceRepository.associateServiceToOtherAttributes(
                serviceName.toLowerCase(),
                service.getType(),
                service.getOperationalState(),
                service.getAdministrativeState(),
                service.getNotes(),
                customerName,
                deviceName,
                orderId
        )).thenReturn(service);

        JSONObject response = serviceRestApis.associateServiceToOtherAttributes(orderId, serviceName, service);

        assertEquals("success", response.get("status"));
    }

    @Test
    void AssociateServiceToOtherAttributesNameNotFoundTest() {
        Service service = new Service();
        service.setName("  ");
        Long orderId = 3456l;
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            serviceRestApis.associateServiceToOtherAttributes(orderId, "new service", service);
        });
        assertEquals("Service name cannot be empty", exception.getMessage());
    }

    @Test
    void AssociateServiceToOtherAttributesOrderNotFoundTest() {
        Service service = new Service();
        service.setName("New Service");
        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(null);
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            serviceRestApis.associateServiceToOtherAttributes(orderId, "new service", service);
        });
        assertEquals("The Order" + orderId + "is not found", exception.getMessage());
    }

    @Test
    void AssociateServiceToOtherAttributesNameNotSameTest() {
        Service service = new Service();
        service.setName("NewService");

        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            serviceRestApis.associateServiceToOtherAttributes(orderId, "new service", service);
        });
        assertEquals("Request Parameter does not match with request Body", exception.getMessage());
    }

    @Test
    void AssociateServiceToOtherAttributesServiceNotFoundTest() {
        Service service = new Service();
        service.setName("New Service");

        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        Service existingService = new Service();
        Mockito.when(serviceRepository.findByName(service.getName().toLowerCase())).thenReturn(null);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            serviceRestApis.associateServiceToOtherAttributes(orderId, "new service", service);
        });
        assertEquals("Service " + service.getName().toLowerCase() + " doesn't exist to update", exception.getMessage());
    }

    @Test
    void AssociateServiceToOtherAttributesCustomerNotFoundTest() {
        Service service = new Service();
        service.setName("New Service");

        String customerName = "customer1";

        Service existingService = new Service();

        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        Customer customer = new Customer();
        customer.setName(customerName);
        service.setCustomer(customer);

        Mockito.when(serviceRepository.findByName(service.getName().toLowerCase())).thenReturn(existingService);

        Mockito.when(customerRepository.findByCustomerName(customerName)).thenReturn(null);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            serviceRestApis.associateServiceToOtherAttributes(orderId, "New Service", service);
        });

        assertEquals("The given Customer is not found : " + customerName, exception.getMessage());
    }


    @Test
    void AssociateServiceToOtherAttributesDeviceNotFoundTest() {
        Service service = new Service();
        service.setName("New Service");
        String customerName = "customer1";

        Device device = new Device();
        device.setName("device1");
        Device device1 = new Device();
        device.setName("device2");

        Customer customer = new Customer();
        customer.setName(customerName);
        service.setCustomer(customer);

        ArrayList<Device> Devices = new ArrayList<>();
        Devices.add(device);
        Devices.add(device1);
        service.setDevices(Devices);

        Service existingService = new Service();

        Long orderId = 3456l;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        Mockito.when(orderRepository.findOrderById(orderId)).thenReturn(order);

        Mockito.when(serviceRepository.findByName(service.getName().toLowerCase())).thenReturn(existingService);

        Mockito.when(customerRepository.findByCustomerName(service.getCustomer().getName())).thenReturn(customer);

        ArrayList<String> deviceName = new ArrayList<>();
        for (Device validateConnections : service.getDevices()) {
            String deviceConnection = validateConnections.getName();
            Mockito.when(deviceRepository.findByName(deviceConnection)).thenReturn(null);
            deviceName.add(device.getName());
        }

        String connectionsAsString = String.valueOf(deviceName.get(0));

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            serviceRestApis.associateServiceToOtherAttributes(orderId, "new service", service);
        });

        assertEquals("The given Device is not found : " + connectionsAsString, exception.getMessage());
    }
}
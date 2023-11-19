package com.prodapt.netinsight.serviceManager;

import com.prodapt.netinsight.connectionManager.LogicalConnection;
import com.prodapt.netinsight.connectionManager.LogicalConnectionRepository;
import com.prodapt.netinsight.connectionManager.PhysicalConnection;
import com.prodapt.netinsight.connectionManager.PhysicalConnectionRepository;
import com.prodapt.netinsight.customerManager.Customer;
import com.prodapt.netinsight.customerManager.CustomerRepository;
import com.prodapt.netinsight.deviceInstanceManager.Device;
import com.prodapt.netinsight.deviceInstanceManager.DeviceRepository;
import com.prodapt.netinsight.exceptionsHandler.AppExceptionHandler;
import com.prodapt.netinsight.objectModelManager.AdditionalAttribute;
import com.prodapt.netinsight.objectModelManager.AdditionalAttributeRepo;
import com.prodapt.netinsight.objectModelManager.ObjectModellerApis;
import com.prodapt.netinsight.orderManager.OrderEntity;
import com.prodapt.netinsight.orderManager.OrderRepository;
import com.prodapt.netinsight.uiHelper.ServiceType;
import com.prodapt.netinsight.uiHelper.ServiceTypeRepo;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class holds all the REST API endpoints for handling service level operations
 */
@RestController
public class ServiceRestApis {
    Logger logger = LoggerFactory.getLogger(ServiceRestApis.class);

    @Autowired
    AppExceptionHandler appExceptionHandler;

    @Autowired
    ServiceRepository serviceRepo;
    @Autowired
    ServiceTypeRepo serviceTypeRepo;

    @Autowired
    PhysicalConnectionRepository physicalConnectionRepository;

    @Autowired
    LogicalConnectionRepository logicalConnectionRepository;

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ObjectModellerApis objectModellerApis;

    @Autowired
    AdditionalAttributeRepo additionalAttributeRepo;

    @Autowired
    OrderRepository orderRepository;

    /**
     * This function is responsible for handling creation of service
     *
     * @param service has service details
     * @return status of operation
     */
    @PostMapping("/createService")
    public JSONObject createService(@RequestParam("orderId") Long orderId, @RequestBody Service service) {
        JSONObject response = new JSONObject();
        try {
            //Validate service name
            if (service.getName() == null || service.getName().trim().isEmpty()) {
                appExceptionHandler.raiseException("Service name cannot be empty");
            }

            //Check if order exists
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The Order" + orderId + "is not found");
            }

            String serviceName = service.getName().trim().toLowerCase();
            logger.info("Inside createService for the service: {}", serviceName);

            //Validate existing service
            Service existingService = serviceRepo.findByName(serviceName);
            if (existingService != null) {
                appExceptionHandler.raiseException("A service already exists with the name " + serviceName);
            }

            //Validate existing Physical Connection
            ArrayList<String> physicalConnection = new ArrayList<>();
            ArrayList<String> physicalConnections = service.getPhysicalConnections();
            for (String existingPhysicalConnection : physicalConnections) {
                String physicalCon = existingPhysicalConnection.toLowerCase();
                PhysicalConnection validatePhysicalConnection = physicalConnectionRepository.findPhysicalconnectionbyName(physicalCon);
                if (validatePhysicalConnection == null) {
                    appExceptionHandler.raiseException("Given Physical Connection is not found : " + physicalCon);
                }
                physicalConnection.add(physicalCon);
            }

            //Validate existing Logical Connection
            ArrayList<String> logicalConnection = new ArrayList<>();
            ArrayList<String> logicalConnections = service.getLogicalConnections();
            for (String existingLogicalConnection : logicalConnections) {
                String logicalCon = existingLogicalConnection.toLowerCase();
                LogicalConnection validateLogicalConnection = logicalConnectionRepository.getLogicalConnection(logicalCon);
                if (validateLogicalConnection == null) {
                    appExceptionHandler.raiseException("Given Logical Connection is not found : " + logicalCon);
                }
                logicalConnection.add(logicalCon);
            }

            Service serviceEntity = serviceRepo.createService(serviceName, service.getType().toLowerCase(), service.getOperationalState(),
                    service.getAdministrativeState(), service.getNotes(), physicalConnection, logicalConnection, orderId);

            serviceTypeRepo.save(new ServiceType(service.getType().toLowerCase()));

            if (service.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", service.getAdditionalAttributes());
                objectModellerApis.addAdditionalAttributes(service.getAdditionalAttributes(), serviceEntity);
            }

            response.put("status", "success");
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    @DeleteMapping("/deleteService")
    public JSONObject deleteService(@RequestParam("orderId") Long orderId, @RequestParam(name = "name") String serviceName) {
        JSONObject response = new JSONObject();
        try {
            //Validate service name
            if (serviceName == null || serviceName.trim().isEmpty()) {
                appExceptionHandler.raiseException("Service name cannot be empty");
            }

            //Check if order exists
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The Order" + orderId + "is not found");
            }

            serviceName = serviceName.trim().toLowerCase();
            logger.info("Inside deleteService for the service: {}", serviceName);

            //Validate existing service
            Service existingService = serviceRepo.findByName(serviceName);
            logger.debug("Existing Service Details : {}" + existingService);
            if (existingService == null) {
                appExceptionHandler.raiseException("No service exists with the name " + serviceName);
            }
            serviceRepo.deleteService(serviceName, orderId);

            response.put("status", "success");
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    @GetMapping("/getAllServices")
    public List<Service> getAllServices(@RequestParam(name = "deviceName") String deviceName) {
        List<Service> services = null;
        try {
            services = serviceRepo.getAllServices(deviceName.toLowerCase());
            if (services == null || services.size() == 0) {
                appExceptionHandler.raiseException("Given device name is not associated with any service : " + deviceName);
            }

        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return services;
    }

    @GetMapping("/getAllServicesWithAdditionalAttribute")
    public List<Service> getAllServicesWithAdditionalAttribute(@RequestParam(name = "deviceName") String deviceName) {
        List<Service> services = null;
        try {
            services = serviceRepo.getAllServices(deviceName.toLowerCase());
            if (services == null || services.size() == 0) {
                appExceptionHandler.raiseException("Given device name is not associated with any service : " + deviceName);
            }

            for (Service service : services) {
                Customer customer = customerRepository.getCustomerForService(service.getName());
                service.setCustomer(customer);

                ArrayList<Device> devices = deviceRepository.getDeviceForService(service.getName());
                service.setDevices(devices);

                ArrayList<AdditionalAttribute> additionalAttributes = additionalAttributeRepo.getAdditionalAttributeForService(service.getName());
                service.setAdditionalAttributes(additionalAttributes);
            }

        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return services;
    }

    /**
     * This function is responsible for handling updation of service
     *
     * @param service has service details
     * @return status of operation
     */
    @PutMapping("/updateService")
    public JSONObject updateService(@RequestParam("orderId") Long orderId, @RequestParam(name = "name") String name, @RequestBody Service service) {
        JSONObject response = new JSONObject();
        try {
            //Validate service name
            if (service.getName() == null || service.getName().trim().isEmpty()) {
                appExceptionHandler.raiseException("Service name cannot be empty");
            }

            //Check if order exists
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The Order" + orderId + "is not found");
            }

            if (!(name.toLowerCase().equalsIgnoreCase(service.getName()))) {
                appExceptionHandler.raiseException("Request Parameter does not match with request Body");
            }

            String serviceName = service.getName().trim().toLowerCase();
            logger.info("Inside update service for the service: {}", serviceName);

            //Validate existing service
            Service existingService = serviceRepo.findByName(name.toLowerCase());
            if (existingService == null) {
                appExceptionHandler.raiseException("Service " + serviceName + " doesn't exist to update");
            }

            //Validate existing Physical Connection
            ArrayList<String> physicalConnection = new ArrayList<>();
            ArrayList<String> physicalConnections = service.getPhysicalConnections();
            for (String existingPhysicalConnection : physicalConnections) {
                String physicalCon = existingPhysicalConnection.toLowerCase();
                PhysicalConnection validatePhysicalConnection = physicalConnectionRepository.findPhysicalconnectionbyName(physicalCon);
                if (validatePhysicalConnection == null) {
                    appExceptionHandler.raiseException("Given Physical Connection is not found : " + physicalCon);
                }
                physicalConnection.add(physicalCon);
            }

            //Validate existing Logical Connection
            ArrayList<String> logicalConnection = new ArrayList<>();
            ArrayList<String> logicalConnections = service.getLogicalConnections();
            for (String existingLogicalConnection : logicalConnections) {
                String logicalCon = existingLogicalConnection.toLowerCase();
                LogicalConnection validateLogicalConnection = logicalConnectionRepository.getLogicalConnection(logicalCon);
                if (validateLogicalConnection == null) {
                    appExceptionHandler.raiseException("Given Logical Connection is not found : " + logicalCon);
                }
                logicalConnection.add(logicalCon);
            }

            if (existingService.getLogicalConnections() != service.getLogicalConnections()) {
                serviceRepo.updateLogicalConnections(serviceName, existingService.getLogicalConnections(), logicalConnection);
            }
            if (existingService.getPhysicalConnections() != service.getPhysicalConnections()) {
                serviceRepo.updatePhysicalConnections(serviceName, existingService.getPhysicalConnections(), physicalConnection);
            }
            Service serviceEntity = serviceRepo.updateService(serviceName, service.getType().toLowerCase(), service.getOperationalState(),
                    service.getAdministrativeState(), service.getNotes(), physicalConnection, logicalConnection, orderId);
            serviceTypeRepo.save(new ServiceType(service.getType().toLowerCase()));

            if (service.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", service.getAdditionalAttributes());
                Service serviceDetails = serviceRepo.findByName(serviceName);
                objectModellerApis.addAdditionalAttributes(service.getAdditionalAttributes(), serviceDetails);
            }

            response.put("status", "success");
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    @GetMapping("/findServiceByName")
    public Service findServiceByName(@RequestParam(name = "Service") String Service) {
        Service serviceDetail = null;
        logger.info("inside findServiceByName : {}" + Service);
        try {
            serviceDetail = serviceRepo.getServiceByName(Service.toLowerCase());
            logger.debug("Existing Service Details : {}" + serviceDetail);
            if (serviceDetail == null) {
                appExceptionHandler.raiseException("Given Service is not Found : " + Service);
            }

            Customer customer = customerRepository.getCustomerForService(serviceDetail.getName());
            serviceDetail.setCustomer(customer);

            ArrayList<Device> devices = deviceRepository.getDeviceForService(serviceDetail.getName());
            serviceDetail.setDevices(devices);

            ArrayList<AdditionalAttribute> additionalAttributes = additionalAttributeRepo.getAdditionalAttributeForService(serviceDetail.getName());
            serviceDetail.setAdditionalAttributes(additionalAttributes);

        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return serviceDetail;
    }

    @PutMapping("/associateServiceToOtherAttributes")
    public JSONObject associateServiceToOtherAttributes(@RequestParam("orderId") Long orderId, @RequestParam(name = "name") String name, @RequestBody Service service) {
        logger.info("Inside associateServiceToOtherAttributes: {}, {}", name, service.toString());
        JSONObject response = new JSONObject();
        try {
            //Validate service name
            if (service.getName() == null || service.getName().trim().isEmpty()) {
                appExceptionHandler.raiseException("Service name cannot be empty");
            }

            //Check if order exists
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The Order" + orderId + "is not found");
            }

            if (!(name.toLowerCase().equalsIgnoreCase(service.getName()))) {
                appExceptionHandler.raiseException("Request Parameter does not match with request Body");
            }

            String serviceName = service.getName().trim().toLowerCase();

            //Validate existing service
            Service existingService = serviceRepo.findByName(name.toLowerCase());
            if (existingService == null) {
                appExceptionHandler.raiseException("Service " + serviceName + " doesn't exist to update");
            }
            logger.debug("Existing Service Details : {}" + existingService);

            //To validate existing Customer
            String customerName = null;
            if (service.getCustomer() != null) {
                customerName = service.getCustomer().getName().toLowerCase();
                if (customerName != null && !customerName.trim().isEmpty()) {
                    Customer customer = customerRepository.findByCustomerName(customerName);
                    if (customer == null) {
                        appExceptionHandler.raiseException("The given Customer is not found : " + customerName);
                    }
                    logger.debug("Existing Customer Details: {} {}" + customerName, customer);
                }
            }

            //To validate existing Devices
            ArrayList<String> deviceName = new ArrayList<>();
            if (service.getDevices() != null) {
                ArrayList<Device> Devices = service.getDevices();
                for (Device existingDevice : Devices) {
                    String devicename = existingDevice.getName().toLowerCase();
                    Device device = deviceRepository.findByName(devicename);
                    if (device == null) {
                        appExceptionHandler.raiseException("The given Device is not found : " + devicename);
                    }
                    deviceName.add(devicename);
                    logger.debug("Existing Device Details : {} {}" + deviceName, device);
                }
            }

            if (service.getCustomer() != null && existingService.getCustomer() != null) {
                if (existingService.getCustomer().getName().equals(customerName)) {
                    customerName = existingService.getCustomer().getName();
                } else {
                    serviceRepo.deleteExistingCustomerRelation(existingService.getCustomer().getName(), serviceName);
                }
            } else if (service.getCustomer() == null && existingService.getCustomer() != null) {
                serviceRepo.deleteExistingCustomerRelation(existingService.getCustomer().getName(), serviceName);
            }

            if (service.getDevices() != null && !existingService.getDevices().isEmpty()) {
                if (existingService.getDevices().stream().map(Device::getName).collect(Collectors.toList()).equals(deviceName)) {
                    deviceName = (ArrayList<String>) existingService.getDevices().stream().map(Device::getName).collect(Collectors.toList());
                } else {
                    serviceRepo.deleteExistingDeviceRelation(existingService.getDevices(), serviceName);
                }
            } else if (service.getDevices() == null && !existingService.getDevices().isEmpty()) {
                serviceRepo.deleteExistingDeviceRelation(existingService.getDevices(), serviceName);
            }

            Service serviceEntity = serviceRepo.associateServiceToOtherAttributes(serviceName, service.getType().toLowerCase(), service.getOperationalState(),
                    service.getAdministrativeState(), service.getNotes(), customerName, deviceName, orderId);

            if (service.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", service.getAdditionalAttributes());
                Service serviceDetails = serviceRepo.findByName(serviceName);
                objectModellerApis.addAdditionalAttributes(service.getAdditionalAttributes(), serviceDetails);
            }

            response.put("status", "success");
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }

        return response;
    }

    @GetMapping("/findServicesByName")
    public ArrayList<String> findServicesByName(@RequestParam("name") String serviceName) {
        serviceName = serviceName.toLowerCase();
        ArrayList<String> findServicesByName = new ArrayList<>();
        try {
            findServicesByName = serviceRepo.findServiceByNameContaining(serviceName);
            logger.info("inside findServicesByName :{}", serviceName);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return findServicesByName;
    }
}

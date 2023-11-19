package com.prodapt.netinsight.connectionManager;

import com.prodapt.netinsight.deviceInstanceManager.*;
import com.prodapt.netinsight.exceptionsHandler.AppExceptionHandler;
import com.prodapt.netinsight.objectModelManager.ObjectModellerApis;
import com.prodapt.netinsight.orderManager.OrderEntity;
import com.prodapt.netinsight.orderManager.OrderRepository;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is responsible for handling connection related API calls
 */
@RestController
public class ConnectionRestApis {

    Logger logger = LoggerFactory.getLogger(ConnectionRestApis.class);

    @Autowired
    PhysicalConnectionRepository physicalConnectionRepository;

    @Autowired
    PortRepository portRepository;

    @Autowired
    SlotRepository slotRepository;

    @Autowired
    LogicalConnectionRepository logicalConnectionRepository;

    @Autowired
    PluggableRepository pluggableRepository;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    AppExceptionHandler appExceptionHandler;

    @Autowired
    LogicalPortRepository logicalPortRepository;

    @Autowired
    ObjectModellerApis objectModellerApis;

    @Autowired
    OrderRepository orderRepository;

    /**
     * This function is responsible for handling update of physical connection
     *
     * @param physicalConnection connection details
     * @return status of operation
     */
    @PutMapping("/updatePhysicalConnection")
    public JSONObject updatePhysicalConnection(@RequestParam(name = "orderId") Long orderId, @RequestBody PhysicalConnection physicalConnection) {
        physicalConnection.setName(physicalConnection.getName().toLowerCase());
        logger.info("Inside updatePhysicalConnection: {}", physicalConnection);
        JSONObject response = new JSONObject();
        try {

            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }

            PhysicalConnection existingPhysicalConnection = physicalConnectionRepository.
                    getPhysicalConnection(physicalConnection.getName());
            if (existingPhysicalConnection == null) {
                appExceptionHandler.raiseException("A Physical connection with given name doesn't exist");
            }
            PhysicalConnection updatedPhysicalCon = physicalConnectionRepository.updatePhysicalConnection(physicalConnection.getName(),
                    physicalConnection.getConnectionType(), physicalConnection.getBandwidth(), orderId);

            if (physicalConnection.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", physicalConnection.getAdditionalAttributes());
                PhysicalConnection physicalConnectionDetails = physicalConnectionRepository.getByName(physicalConnection.getName());
                objectModellerApis.addAdditionalAttributes(physicalConnection.getAdditionalAttributes(), physicalConnectionDetails);
            }

            response.put("status", "success");
            response.put("updatedPhysicalConnection", updatedPhysicalCon);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for handling creation of physical connection
     *
     * @param physicalConnection connection details
     * @return status of operation
     */
    @PostMapping("/createPhysicalConnection")
    public JSONObject createPhysicalConnection(@RequestParam(name = "orderId") Long orderId, @RequestBody PhysicalConnection physicalConnection) {
        physicalConnection.setName(physicalConnection.getName().toLowerCase());
        logger.info("Inside createPhysicalConnection: {}", physicalConnection);

        OrderEntity existingOrder = orderRepository.findOrderById(orderId);
        if (existingOrder == null) {
            appExceptionHandler.raiseException("The order " + orderId + " is not found");
        }

        String aEndPortName = null;
        String zEndPortName = null;
        String aEndPluggableName = null;
        String zEndPluggableName = null;
        if (physicalConnectionRepository.getPhysicalConnection(physicalConnection.getName()) != null) {
            appExceptionHandler.raiseException("A Physical connection with given name already exist");
        }

        // Get A-END port/pluggable details
        if (physicalConnection.getDeviceAPort().contains("/")) {
            String[] aEndPortDetails = physicalConnection.getDeviceAPort().split("/");
            int aEndShelfNo = Integer.parseInt(aEndPortDetails[0]);
            int aEndSlotNo = Integer.parseInt(aEndPortDetails[1]);
            int aEndPortNoOnCard = Integer.parseInt(aEndPortDetails[2]);

            String aEndShelfSlot = physicalConnection.getDeviceA() + "/" + aEndShelfNo + "/" + aEndSlotNo;
            Card aEndCard = cardRepository.getCardOnASlot(aEndShelfSlot);
            if (aEndCard == null) {
                appExceptionHandler.raiseException("No card exist on the given A-End shelf/slot position");
            }
            String aEndCardSlot = aEndCard.getName() + "/" + aEndPortNoOnCard;
            Pluggable aEndPluggable = pluggableRepository.getPluggableOnCardByNumber(aEndCardSlot, aEndPortNoOnCard);
            if (aEndPluggable == null) {
                Port aEndDevicePort = portRepository.getPortOnCardByNumber(aEndCardSlot, aEndPortNoOnCard);
                if (aEndDevicePort == null) {
                    appExceptionHandler.raiseException("No Port exist on the given A-End shelf/slot/port position");
                } else {
                    aEndPortName = aEndDevicePort.getName();
                }
            } else {
                aEndPluggableName = aEndPluggable.getName();
            }
        } else {
            Port aEndDevicePort = portRepository.getPortOnDeviceByNumber(physicalConnection.getDeviceA(),
                    Integer.parseInt(physicalConnection.getDeviceAPort()));
            if (aEndDevicePort == null) {
                Pluggable aEndDevicePluggable = pluggableRepository.getPluggableOnDeviceByNumber(
                        physicalConnection.getDeviceA(), Integer.parseInt(physicalConnection.getDeviceAPort()));
                if (aEndDevicePluggable == null) {
                    appExceptionHandler.raiseException("Pluggable on given position on A-End-device doesn't exist");
                } else {
                    aEndPluggableName = aEndDevicePluggable.getName();
                }
            } else {
                aEndPortName = aEndDevicePort.getName();
            }
        }

        // Get Z-END Port/Pluggable details
        if (physicalConnection.getDeviceZPort().contains("/")) {
            String[] zEndPortDetails = physicalConnection.getDeviceZPort().split("/");
            int zEndShelfNo = Integer.parseInt(zEndPortDetails[0]);
            int zEndSlotNo = Integer.parseInt(zEndPortDetails[1]);
            int zEndPortNoOnCard = Integer.parseInt(zEndPortDetails[2]);

            String zEndShelfSlot = physicalConnection.getDeviceZ() + "/" + zEndShelfNo + "/" + zEndSlotNo;
            Card zEndCard = cardRepository.getCardOnASlot(zEndShelfSlot);
            if (zEndCard == null) {
                appExceptionHandler.raiseException("No card exist on the given Z-End shelf/slot position");
            }
            String zEndCardSlot = zEndCard.getName() + "/" + zEndPortNoOnCard;
            Pluggable zEndPluggable = pluggableRepository.getPluggableOnCardByNumber(zEndCardSlot, zEndPortNoOnCard);

            if (zEndPluggable == null) {
                Port zEndDevicePort = portRepository.getPortOnCardByNumber(zEndCardSlot, zEndPortNoOnCard);
                if (zEndDevicePort == null) {
                    appExceptionHandler.raiseException("No Port exist on the given Z-End shelf/slot/port position");
                } else {
                    zEndPortName = zEndDevicePort.getName();
                }
            } else {
                zEndPluggableName = zEndPluggable.getName();
            }
        } else {
            Port zEndDevicePort = portRepository.getPortOnDeviceByNumber(physicalConnection.getDeviceZ(),
                    Integer.parseInt(physicalConnection.getDeviceZPort()));
            if (zEndDevicePort == null) {
                Pluggable zEndDevicePluggable = pluggableRepository.getPluggableOnDeviceByNumber(
                        physicalConnection.getDeviceZ(), Integer.parseInt(physicalConnection.getDeviceZPort()));
                if (zEndDevicePluggable == null) {
                    appExceptionHandler.raiseException("Pluggable on given position on Z-End-device doesn't exist");
                }
                zEndPluggableName = zEndDevicePluggable.getName();
            } else {
                zEndPortName = zEndDevicePort.getName();
            }
        }

        logger.info("Creating Physical connection between devices {} and {} on ports {} and {} respectively",
                physicalConnection.getDeviceA(), physicalConnection.getDeviceZ(), aEndPortName, zEndPortName);

        JSONObject details = new JSONObject();
        details.put("A-PortName", aEndPortName);
        details.put("zEndPortName", zEndPortName);
        details.put("aEndPluggableName", aEndPluggableName);
        details.put("zEndPluggableName", zEndPluggableName);

        if (aEndPortName != null && zEndPortName != null) {
            PhysicalConnection createdConnection = physicalConnectionRepository.createPhysicalConnectionOnPort(
                    physicalConnection.getName(), physicalConnection.getDeviceA(), physicalConnection.getDeviceZ(),
                    physicalConnection.getDeviceAPort(), physicalConnection.getDeviceZPort(),
                    physicalConnection.getConnectionType(), physicalConnection.getBandwidth(), aEndPortName, zEndPortName, orderId);

            if (physicalConnection.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", physicalConnection.getAdditionalAttributes());
                PhysicalConnection physicalConnectionDetails = physicalConnectionRepository.getByName(physicalConnection.getName());
                objectModellerApis.addAdditionalAttributes(physicalConnection.getAdditionalAttributes(), physicalConnectionDetails);
            }

            JSONObject response = new JSONObject();
            response.put("status", "Success");
            response.put("physicalConnection", createdConnection);
            return response;
        } else if (aEndPluggableName != null && zEndPluggableName != null) {
            PhysicalConnection createdConnection = physicalConnectionRepository.createPhysicalConnectionOnPluggable(
                    physicalConnection.getName(), physicalConnection.getDeviceA(), physicalConnection.getDeviceZ(),
                    physicalConnection.getDeviceAPort(), physicalConnection.getDeviceZPort(),
                    physicalConnection.getConnectionType(), physicalConnection.getBandwidth(),
                    aEndPluggableName, zEndPluggableName, orderId);

            if (physicalConnection.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", physicalConnection.getAdditionalAttributes());
                PhysicalConnection physicalConnectionDetails = physicalConnectionRepository.getByName(physicalConnection.getName());
                objectModellerApis.addAdditionalAttributes(physicalConnection.getAdditionalAttributes(), physicalConnectionDetails);
            }

            JSONObject response = new JSONObject();
            response.put("status", "Success");
            response.put("physicalConnection", createdConnection);
            return response;
        }
        JSONObject response = new JSONObject();
        response.put("status", "Failed due to incompatibility between port and pluggable for connection creation");
        response.put("port-PluggableDetails", details);
        return response;
    }

    /**
     * This function is responsible for handling update of physical connection using connection name
     *
     * @param connectionName     name of the connection
     * @param physicalConnection connection details
     * @return status of operation
     */
    @PutMapping("/updatePhysicalConnectionByName")
    public JSONObject updatePhysicalConnectionByName(@RequestParam(name = "orderId") Long orderId, @RequestParam(name = "name") String connectionName,
                                                     @RequestBody PhysicalConnection physicalConnection) {
        logger.info("Inside updatePhysicalConnectionByName: {}", physicalConnection);
        JSONObject response = new JSONObject();
        try {
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }

            if (physicalConnection.getName() == null) {
                appExceptionHandler.raiseException("Physical connection name is mandatory");
            }
            if (physicalConnection.getConnectionType() == null) {
                appExceptionHandler.raiseException("Physical connection type is mandatory");
            }
            if (!connectionName.equalsIgnoreCase(physicalConnection.getName())) {
                appExceptionHandler.raiseException("Request parameter does not match with the request body");
            }
            connectionName = connectionName.toLowerCase();

            PhysicalConnection existingPhysicalConnection = physicalConnectionRepository.getPhysicalConnection(connectionName);
            if (existingPhysicalConnection == null) {
                appExceptionHandler.raiseException("Physical connection with given name " + connectionName + " doesn't exist");
            }
            PhysicalConnection updatedPhysicalCon = physicalConnectionRepository.updatePhysicalConnection(connectionName,
                    physicalConnection.getConnectionType(), physicalConnection.getBandwidth(), orderId);

            if (physicalConnection.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", physicalConnection.getAdditionalAttributes());
                PhysicalConnection physicalConnectionDetails = physicalConnectionRepository.getByName(connectionName);
                objectModellerApis.addAdditionalAttributes(physicalConnection.getAdditionalAttributes(), physicalConnectionDetails);
            }

            response.put("status", "success");
            response.put("updatedPhysicalConnection", updatedPhysicalCon);
            logger.info("Physical connection updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for handling update of logical connection using connection name
     *
     * @param connectionName    name of the connection
     * @param logicalConnection logical connection details
     * @return status of operation
     */
    @PutMapping("/updateLogicalConnectionByName")
    public JSONObject updateLogicalConnectionByName(@RequestParam(name = "orderId") Long orderId, @RequestParam(name = "name") String connectionName,
                                                    @RequestBody LogicalConnection logicalConnection) {
        logger.info("Inside updateLogicalConnection: {}", logicalConnection);
        JSONObject response = new JSONObject();
        try {
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }

            if (logicalConnection.getName() == null) {
                appExceptionHandler.raiseException("Logical connection name is mandatory");
            }
            if (logicalConnection.getConnectionType() == null) {
                appExceptionHandler.raiseException("Logical connection type is mandatory");
            }
            if (logicalConnection.getBandwidth() == null) {
                appExceptionHandler.raiseException("Logical connection bandwidth is mandatory");
            }
            if (!connectionName.equalsIgnoreCase(logicalConnection.getName())) {
                appExceptionHandler.raiseException("Request parameter does not match with the request body");
            }
            connectionName = connectionName.toLowerCase();

            LogicalConnection existingLogicalConnection = logicalConnectionRepository.getLogicalConnection(connectionName);
            if (existingLogicalConnection == null) {
                appExceptionHandler.raiseException("No logical connection exists with the name " + connectionName);
            }

            LogicalConnection updatedLogicalCon = logicalConnectionRepository.updateLogicalConnection(connectionName,
                    logicalConnection.getConnectionType(), logicalConnection.getBandwidth(), orderId);

            if (logicalConnection.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", logicalConnection.getAdditionalAttributes());
                objectModellerApis.addAdditionalAttributes(logicalConnection.getAdditionalAttributes(), updatedLogicalCon);
            }

            response.put("status", "success");
            response.put("updatedLogicalConnection", updatedLogicalCon);
            logger.info("Logical connection updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    @PostMapping("/createLogicalConnection")
    public JSONObject createLogicalConnection(@RequestParam(name = "orderId") Long orderId, @RequestBody LogicalConnection logicalConnection) {
        logger.info("Inside createLogicalConnectionOnLogicalPort: {}", logicalConnection);
        JSONObject response = new JSONObject();
        try {
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }

            LogicalPort logicalPortA = null;
            LogicalPort logicalPortZ = null;
            if (logicalConnection.getName() == null) {
                appExceptionHandler.raiseException("Logical connection name is mandatory");
            }
            String connectionName = logicalConnection.getName().toLowerCase();
            if (logicalConnectionRepository.getLogicalConnection(connectionName) != null) {
                appExceptionHandler.raiseException("A Logical connection with given name already exist");
            }

            if (logicalConnection.getDeviceA() == null || logicalConnection.getDeviceZ() == null) {
                appExceptionHandler.raiseException("Device A and Device Z are mandatory");
            }
            Set<String> deviceNames = new HashSet<>();

            for (String physicalConnection : logicalConnection.getPhysicalConnections()) {
                if (physicalConnectionRepository.getPhysicalConnection(physicalConnection) == null) {
                    appExceptionHandler.raiseException("Physical connection with name " + physicalConnection + " doesn't exist");
                }
                deviceNames.addAll(physicalConnectionRepository.getDevicesForConnection(physicalConnection));
            }

            if (!deviceNames.contains(logicalConnection.getDeviceA()) || !deviceNames.contains(logicalConnection.getDeviceZ())) {
                appExceptionHandler.raiseException("The given list of physical connections do not connect Device A and Device Z");
            }

            //TODO: Validate connection type and bandwidth

            //get a end details
            String[] aEndDetails = logicalConnection.getDeviceALogicalPort().split("/");
            if (aEndDetails.length == 2) {
                logger.debug("creating logical connection on logical port {} of port/pluggable {}", aEndDetails[1], aEndDetails[0]);
                logicalPortA = logicalPortRepository.getLogicalPortOnDevice(logicalConnection.getDeviceA(),
                        Integer.parseInt(aEndDetails[0]), Integer.parseInt(aEndDetails[1]));
            } else if (aEndDetails.length == 4) {
                logger.debug("creating logical connection on logical port {} of port/pluggable {} of card slot {} of shelf slot {} ",
                        aEndDetails[3], aEndDetails[2], aEndDetails[1], aEndDetails[0]);
                logicalPortA = logicalPortRepository.getLogicalOnCardPort(logicalConnection.getDeviceA(),
                        Integer.parseInt(aEndDetails[0]), Integer.parseInt(aEndDetails[1]), Integer.parseInt(aEndDetails[2]),
                        Integer.parseInt(aEndDetails[3]));
            } else {
                appExceptionHandler.raiseException("Invalid logical port format on A end");
            }
            if (logicalPortA == null) {
                appExceptionHandler.raiseException("Logical port on A end does not exist at the given position on the given " +
                        "physical port/pluggable");
            }

            //get z end details
            String[] zEndDetails = logicalConnection.getDeviceZLogicalPort().split("/");
            if (zEndDetails.length == 2) {
                logger.debug("creating logical connection on logical port {} of port {}", zEndDetails[1], zEndDetails[0]);
                logicalPortZ = logicalPortRepository.getLogicalPortOnDevice(logicalConnection.getDeviceZ(),
                        Integer.parseInt(zEndDetails[0]), Integer.parseInt(zEndDetails[1]));
            } else if (zEndDetails.length == 4) {
                logger.debug("creating logical connection on logical port {} of port/pluggable {} of card slot {} of shelf slot {} ",
                        zEndDetails[3], zEndDetails[2], zEndDetails[1], zEndDetails[0]);
                logicalPortZ = logicalPortRepository.getLogicalOnCardPort(logicalConnection.getDeviceZ(),
                        Integer.parseInt(zEndDetails[0]), Integer.parseInt(zEndDetails[1]), Integer.parseInt(zEndDetails[2]),
                        Integer.parseInt(zEndDetails[3]));
            } else {
                appExceptionHandler.raiseException("Invalid logical port format on Z end");
            }
            if (logicalPortZ == null) {
                appExceptionHandler.raiseException("Logical port on Z end does not exist at the given position on the given " +
                        "physical port/pluggable");
            }

            LogicalConnection createdLogicalConnection = logicalConnectionRepository.createLogicalConnectionOnPort(connectionName, logicalConnection.getDeviceA(),
                    logicalConnection.getDeviceZ(), logicalPortA.getName(), logicalPortZ.getName(), logicalConnection.getConnectionType(),
                    logicalConnection.getBandwidth(), new ArrayList<>(deviceNames), logicalConnection.getPhysicalConnections(), orderId);

            if (logicalConnection.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", logicalConnection.getAdditionalAttributes());
                objectModellerApis.addAdditionalAttributes(logicalConnection.getAdditionalAttributes(), createdLogicalConnection);
            }

            response.put("status", "Success");
            response.put("logicalConnection", createdLogicalConnection);

        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    @GetMapping("/getAllPhysicalConnections")
    public ArrayList<PhysicalConnection> getAllPhysicalConnections() {
        ArrayList<PhysicalConnection> physicalConnections = null;
        try {
            physicalConnections = physicalConnectionRepository.getAllPhysicalConnections();
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return physicalConnections;
    }

    @GetMapping("/getAllLogicalConnections")
    public ArrayList<LogicalConnection> getAllLogicalConnections() {
        ArrayList<LogicalConnection> logicalConnections = null;
        try {
            logicalConnections = logicalConnectionRepository.getAllLogicalConnections();
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return logicalConnections;
    }

    @GetMapping("/getShortestPathPhysicalConnections")
    public ArrayList<PhysicalConnection> getShortestPathPhysicalConnections(@RequestParam(name = "deviceA") String deviceA,
                                                                            @RequestParam(name = "deviceZ") String deviceZ) {
        ArrayList<PhysicalConnection> physicalConnections = null;
        try {
            if (deviceA == null || deviceA.trim().isEmpty() || deviceZ == null || deviceZ.trim().isEmpty()) {
                appExceptionHandler.raiseException("Device A and Device Z are mandatory");
            }
            physicalConnections = physicalConnectionRepository.getShortestPathPhysicalConnections(deviceA, deviceZ);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }

        return physicalConnections;
    }

    @GetMapping("/findLogicalConnectionByDevice")
    public ArrayList<LogicalConnection> findLogicalConnectionByDevice(@RequestParam(name = "device") String device) {
        ArrayList<LogicalConnection> logicalConnections = null;
        try {
            logicalConnections = logicalConnectionRepository.findLogicalConnectionByDevice(device.toLowerCase());
            if (logicalConnections == null || logicalConnections.size() == 0) {
                appExceptionHandler.raiseException("Given device name is not associated with any logical Connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }

        return logicalConnections;
    }

    @GetMapping("/findPhysicalConnectionByDevice")
    public ArrayList<PhysicalConnection> findPhysicalConnectionByDevice(@RequestParam(name = "device") String device) {
        ArrayList<PhysicalConnection> PhysicalConnections = null;
        try {
            PhysicalConnections = physicalConnectionRepository.findPhysicalConnectionByDevice(device.toLowerCase());
            if (PhysicalConnections == null || PhysicalConnections.size() == 0) {
                appExceptionHandler.raiseException("Given device name is not associated with any Physical Connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return PhysicalConnections;
    }

    @GetMapping("/findphysicalconnection")
    public PhysicalConnection findPhysicalConnection(@RequestParam(name = "Connection") String Connection) {
        PhysicalConnection PhysicalConnections = null;
        try {
            PhysicalConnections = physicalConnectionRepository.getByName(Connection.toLowerCase());
            if (PhysicalConnections == null) {
                appExceptionHandler.raiseException("Given Physical Connection is not Found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return PhysicalConnections;
    }

    @GetMapping("/findLogicalConnection")
    public LogicalConnection findLogicalConnection(@RequestParam(name = "Connection") String Connection) {
        LogicalConnection logicalConnections = null;
        try {
            logicalConnections = logicalConnectionRepository.getByName(Connection.toLowerCase());
            if (logicalConnections == null) {
                appExceptionHandler.raiseException("Given logical Connection is not Found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return logicalConnections;
    }

    @DeleteMapping("/deleteLogicalConnection")
    public JSONObject deleteLogicalConnection(@RequestParam(name = "orderId") Long orderId, @RequestParam(name = "connectionName") String connectionName) {
        JSONObject response = new JSONObject();
        connectionName = connectionName.toLowerCase();
        logger.info("Inside deleteLogicalConnection, Name value received: {}", connectionName);
        try {
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
            LogicalConnection logicalConnection = logicalConnectionRepository.getLogicalConnection(connectionName);
            if (logicalConnection == null) {
                appExceptionHandler.raiseException("Given logicalConnection is not available");
            }
            logicalConnectionRepository.deleteLogicalConnection(connectionName, orderId);
            response.put("status", "Success");
        } catch (Exception e) {

            e.printStackTrace();

            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    @DeleteMapping("/deletePhysicalConnection")
    public JSONObject deletePhysicalConnection(@RequestParam(name = "orderId") Long orderId, @RequestParam(name = "connectionName") String connectionName) {
        JSONObject response = new JSONObject();
        logger.info("Inside deletePhysicalConnection, Name value received: {}", connectionName);
        connectionName = connectionName.toLowerCase();
        try {
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }

            PhysicalConnection physicalConnection = physicalConnectionRepository.getPhysicalConnection(connectionName);
            if (physicalConnection == null) {
                appExceptionHandler.raiseException("Given physicalConnection is not available");
            }
            physicalConnectionRepository.deletePhysicalConnection(connectionName, orderId);

            response.put("status", "Success");

        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

}
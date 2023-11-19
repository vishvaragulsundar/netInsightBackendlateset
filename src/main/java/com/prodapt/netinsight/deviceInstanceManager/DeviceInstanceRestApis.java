package com.prodapt.netinsight.deviceInstanceManager;

import com.prodapt.netinsight.connectionManager.LogicalConnection;
import com.prodapt.netinsight.connectionManager.LogicalConnectionRepository;
import com.prodapt.netinsight.connectionManager.PhysicalConnection;
import com.prodapt.netinsight.connectionManager.PhysicalConnectionRepository;
import com.prodapt.netinsight.deviceMetaModeller.DeviceMetaModel;
import com.prodapt.netinsight.deviceMetaModeller.DeviceMetaModelRepository;
import com.prodapt.netinsight.exceptionsHandler.AppExceptionHandler;
import com.prodapt.netinsight.locationManager.*;
import com.prodapt.netinsight.objectModelManager.AdditionalAttribute;
import com.prodapt.netinsight.objectModelManager.AdditionalAttributeRepo;
import com.prodapt.netinsight.objectModelManager.ObjectModellerApis;
import com.prodapt.netinsight.orderManager.OrderEntity;
import com.prodapt.netinsight.orderManager.OrderRepository;
import com.prodapt.netinsight.uiHelper.*;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * This class holds all the REST API endpoints for handling device level operations
 */
@RestController
public class DeviceInstanceRestApis {

    Logger logger = LoggerFactory.getLogger(DeviceInstanceRestApis.class);

    @Autowired
    ObjectModellerApis objectModellerApis;

    @Autowired
    AdditionalAttributeRepo additionalAttributeRepo;

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    PortRepository portRepository;

    @Autowired
    VendorsRepo vendorsRepo;

    @Autowired
    SlotRepository slotRepository;

    @Autowired
    PluggableRepository pluggableRepository;

    @Autowired
    CardSlotRepository cardSlotRepository;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    DeviceMetaModelRepository deviceMetaModelRepository;

    @Autowired
    AppExceptionHandler appExceptionHandler;

    @Autowired
    ShelfRepository shelfRepository;

    @Autowired
    RackRepository rackRepository;

    @Autowired
    BuildingRepository buildingRepository;

    @Autowired
    LogicalPortRepository logicalPortRepository;

    @Autowired
    OrderRepository orderRepository;

    @Value(value = "${server.port}")
    private String port;

    public String ip = InetAddress.getLocalHost().getHostAddress();

    public DeviceInstanceRestApis() throws UnknownHostException {
    }

    /**
     * This function is responsible for retrieving device information with the device name
     *
     * @param name device name
     * @return device details
     */
    @GetMapping("/getDevice")
    public Device getDevice(@RequestParam(name = "name") String name) {
        name = name.toLowerCase();
        Device device = deviceRepository.getByName(name);
        try {
            logger.info("inside getDevice: {}", name);
            if (device == null) {
                appExceptionHandler.raiseException("Device not found: " + name);
            }
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return device;
    }

    /**
     * This function is responsible for retrieving rack information with the rack name
     *
     * @param name rack name
     * @return rack details
     */
    @GetMapping("/getDevicesInRack")
    public List<Device> getDevicesInRack(@RequestParam(name = "rackName") String name) {
        name = name.toLowerCase();
        List<Device> deviceinrack = null;
        try {
            logger.info("inside getDevicesInRack: {}", name);
            deviceinrack = deviceRepository.getDevicesInRack(name);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return deviceinrack;
    }

    /**
     * This function is responsible for retrieving building information with the building name
     *
     * @param name building name
     * @return building details
     */
    @GetMapping("/getDevicesInBuilding")
    public List<Device> getDevicesInbuilding(@RequestParam(name = "building") String name) {
        name = name.toLowerCase();
        List<Device> deviceInBuilding = null;
        try {
            logger.info("inside getDevicesInbuilding: {}", name);
            deviceInBuilding = deviceRepository.getDevicesInBuilding(name);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return deviceInBuilding;
    }

    /**
     * This function is responsible for retrieving card information with the card name
     *
     * @param name card name
     * @return card details
     */
    @GetMapping("/getCard")
    public Card getCard(@RequestParam(name = "cardName") String name) {
        name = name.toLowerCase();
        Card card = null;
        try {
            logger.info("Inside getCard: {}", name);
            card = cardRepository.getByName(name);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return card;
    }

    /**
     * This function is responsible for retrieving shelf information with the shelf name
     *
     * @param name shelf name
     * @return shelf details
     */
    @GetMapping("/getShelf")
    public Shelf getShelf(@RequestParam(name = "shelfName") String name) {
        name = name.toLowerCase();
        Shelf shelf = null;
        try {
            logger.info("Inside getShelf: {}", name);
            shelf = shelfRepository.getByName(name);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return shelf;
    }

    /**
     * This function is responsible for retrieving shelves information with the device name
     *
     * @param name device name
     * @return device details
     */
    @GetMapping("/getShelvesOnDevice")
    public ArrayList<Shelf> getShelvesOnDevice(@RequestParam(name = "deviceName") String name) {
        name = name.toLowerCase();
        ArrayList<Shelf> shelvesOnDevice = null;
        try {
            logger.info("Inside getShelvesOnDevice: {}", name);
            shelvesOnDevice = shelfRepository.getShelves(name);

            for (Shelf shelf : shelvesOnDevice) {
                ArrayList<AdditionalAttribute> additionalAttribute = additionalAttributeRepo.getAdditionalAttributeForShelf(shelf.getName());
                shelf.setAdditionalAttributes(additionalAttribute);
            }
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return shelvesOnDevice;
    }

    @GetMapping("/getShelfById")
    public Shelf findShelfById(@RequestParam("id") Long id) {
        logger.info("Inside findShelfById: {}", id);
        Shelf shelf = null;
        try {
            shelf = shelfRepository.findShelfById(id);
            shelf.setHref("https://" + ip + ":" + port + "/getShelfById?id=" + shelf.getId());
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return shelf;
    }

    @GetMapping("/getSlotById")
    public Slot findSlotById(@RequestParam("id") Long id) {
        logger.info("Inside findSlotById: {}", id);
        Slot slot = null;
        try {
            slot = slotRepository.findSlotById(id);
            slot.setHref("https://" + ip + ":" + port + "/findSlotById?id=" + slot.getId());
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return slot;
    }

    @GetMapping("/getSlotByName")
    public Slot getSlotByName(@RequestParam("name") String name) {
        name = name.toLowerCase();
        Slot slot = null;
        try {
            logger.info("Inside getSlotByName: {}", name);
            slot = slotRepository.getByName(name);
            slot.setHref("https://" + ip + ":" + port + "/findSlotById?id=" + slot.getId());
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return slot;
    }

    @GetMapping("/getSlotsOnShelf")
    public ArrayList<Slot> getSlotOnShelf(@RequestParam("name") String name) {
        name = name.toLowerCase();
        ArrayList<Slot> slots = null;
        try {
            logger.info("Inside getSlotOnShelf: {}", name);
            slots = slotRepository.getSlotsOnShelf(name);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return slots;
    }

    /**
     * This function is responsible for retrieving port information with the port name
     *
     * @param name name
     * @return port details
     */
    @GetMapping("/getPort")
    public Port getPort(@RequestParam(name = "portName") String name) {
        name = name.toLowerCase();
        Port port = null;
        try {
            logger.info("Inside getPort: {}", name);
            port = portRepository.getByName(name);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return port;
    }

    /**
     * This function is responsible for retrieving pluggable information with the pluggable name
     *
     * @param name pluggable
     * @return pluggable details
     */
    @GetMapping("/getPluggable")
    public Pluggable getPluggable(@RequestParam(name = "pluggableName") String name) {
        name = name.toLowerCase();
        Pluggable pluggable = null;
        try {
            logger.info("Inside getPluggable: {}", name);
            pluggable = pluggableRepository.getByName(name);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return pluggable;
    }

    /**
     * This function is responsible for updating the Shelf details
     *
     * @param orderId   orderId id
     * @param shelfName shelfName Name
     * @param shelf     shelf Details
     * @return status
     */
    @PutMapping("/UpdateShelf")
    public JSONObject updateShelf(@RequestParam(name = "orderId") Long orderId, @RequestParam(name = "shelf") String shelfName, @RequestBody Shelf shelf, @RequestParam(name = "device") String deviceName) {
        JSONObject response = new JSONObject();
        try {
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }

            Shelf existingShelfDetails = shelfRepository.getShelf(deviceName, shelfName);
            if (existingShelfDetails == null) {
                appExceptionHandler.raiseException("Shelf with the given name doesn't exist");
            }

            Shelf updatedShelf = shelfRepository.updateShelf(shelfName, shelf.getOperationalState(), shelf.getAdministrativeState(),
                    shelf.getUsageState(), orderId);
            if (shelf.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", shelf.getAdditionalAttributes());
                objectModellerApis.addAdditionalAttributes(shelf.getAdditionalAttributes(), updatedShelf);
            }
            response.put("status", "Success");
            response.put("shelfDetails", updatedShelf);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for updating the slot details
     *
     * @param orderId  orderId id
     * @param slotName slotName Name
     * @param slot     slot Details
     * @return status
     */
    @PutMapping("/UpdateSlot")
    public JSONObject updateSlot(@RequestParam(name = "orderId") Long orderId, @RequestParam(name = "slot") String slotName, @RequestParam(name = "shelf") String shelfName, @RequestBody Slot slot) {
        JSONObject response = new JSONObject();
        try {
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }

            Slot existingSlotDetails = slotRepository.getSlotOnShelf(shelfName, slotName);
            if (existingSlotDetails == null) {
                appExceptionHandler.raiseException("Slot with the given name doesn't exist");
            }

            Slot updatedSlot = slotRepository.updateSlot(slotName, slot.getOperationalState(), slot.getAdministrativeState(),
                    slot.getUsageState(), orderId);

            response.put("status", "Success");
            response.put("slotDetails", updatedSlot);

        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());

        }
        return response;
    }

    /**
     * This function is responsible for updating the cardSlot details
     *
     * @param orderId      orderId id
     * @param cardSlotName cardSlotName Name
     * @param cardSlot     cardSlot Details
     * @return status
     */
    @PutMapping("/UpdateCardSlot")
    public JSONObject updateCardSlot(@RequestParam(name = "orderId") Long orderId, @RequestParam(name = "cardslot") String cardSlotName, @RequestParam(name = "card") String cardName, @RequestBody CardSlot cardSlot) {
        JSONObject response = new JSONObject();
        try {
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }

            CardSlot existingCardSlotDetails = cardSlotRepository.getCardSlotOnACard(cardName, cardSlotName);
            if (existingCardSlotDetails == null) {
                appExceptionHandler.raiseException("CardSlot with the given name doesn't exist");
            }

            CardSlot updatedCardSlot = cardSlotRepository.updateCardSlot(cardSlotName, cardSlot.getOperationalState(), cardSlot.getAdministrativeState(),
                    cardSlot.getUsageState(), orderId);

            response.put("status", "Success");
            response.put("slotDetails", updatedCardSlot);

        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());

        }
        return response;
    }

    /**
     * This function is responsible for creating device on a building
     *
     * @param orderId  orderId id
     * @param building building name
     * @param device   device details
     * @return status
     */
    @PostMapping("/createDeviceOnBuilding")
    public JSONObject createDeviceOnBuilding(@RequestParam(name = "orderId") Long orderId, @RequestParam(name = "building") String building,
                                             @RequestBody Device device) {
        device.setName(device.getName().toLowerCase());
        building = building.toLowerCase();
        logger.info("Inside createDeviceOnBuilding: {}, {}", building, device.toString());
        JSONObject response = new JSONObject();
        try {
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
            DeviceMetaModel deviceModel = deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel());
            if (deviceModel == null) {
                appExceptionHandler.raiseException("Device Meta-model doesn't exist for the device type: " +
                        device.getDeviceModel());
            }
            Building buildingDetails = buildingRepository.findByName(building);
            if (buildingDetails == null) {
                appExceptionHandler.raiseException("Given building doesn't exist");
            }

            Device existingDeviceDetails = deviceRepository.findByName(device.getName());
            if (existingDeviceDetails != null) {
                appExceptionHandler.raiseException("Device with same name already exist, existingDeviceDetails: " +
                        existingDeviceDetails.toString());
            }
            deviceRepository.createDeviceOnBuilding(building, device.getName(), device.getDeviceModel(),
                    device.getLocation(), device.getOrganisation(), device.getCustomer(), device.getManagementIp(),
                    device.getRackPosition(), device.getOperationalState(), device.getAdministrativeState(),
                    device.getUsageState(), device.getSerialNumber(), deviceModel.getShelvesContained(), orderId);
            if (device.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", device.getAdditionalAttributes());
                Device deviceDetails = deviceRepository.findByName(device.getName());
                objectModellerApis.addAdditionalAttributes(device.getAdditionalAttributes(), deviceDetails);
            }
            response.put("status", "Success");
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for updating device on a building
     *
     * @param orderId  orderId id
     * @param building building name
     * @param device   device details
     * @return status
     */
    @PutMapping("/updateDeviceOnBuilding")
    public JSONObject updateDeviceOnBuilding(@RequestParam(name = "orderId") Long orderId, @RequestParam(name = "building") String building,
                                             @RequestBody Device device) {
        device.setName(device.getName().toLowerCase());
        building = building.toLowerCase();
        logger.info("Inside updateDeviceOnBuilding: {}, {}", building, device.toString());
        JSONObject response = new JSONObject();
        try {
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
            DeviceMetaModel deviceModel = deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel().toLowerCase());
            if (deviceModel == null) {
                appExceptionHandler.raiseException("Device Meta-model doesn't exist for the device type: " +
                        device.getDeviceModel().toLowerCase());
            }
            Building buildingDetails = buildingRepository.findByName(building);
            if (buildingDetails == null) {
                appExceptionHandler.raiseException("Given building doesn't exist");
            }
            Device existingDeviceDetails = deviceRepository.findByName(device.getName().toLowerCase());
            if (existingDeviceDetails == null) {
                appExceptionHandler.raiseException("Device with the given name doesn't exist");
            }
            deviceRepository.updateDeviceOnBuilding(building, device.getName().toLowerCase(), device.getDeviceModel(),
                    device.getLocation(), device.getOrganisation(), device.getCustomer(), device.getManagementIp(),
                    device.getRackPosition(), device.getOperationalState(), device.getAdministrativeState(),
                    device.getUsageState(), device.getSerialNumber(), deviceModel.getShelvesContained(), orderId);
            if (device.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", device.getAdditionalAttributes());
                Device deviceDetails = deviceRepository.findByName(device.getName());
                objectModellerApis.addAdditionalAttributes(device.getAdditionalAttributes(), deviceDetails);
            }
            response.put("status", "Success");
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for creating device on a rack
     *
     * @param orderId orderId id
     * @param rack    rack name
     * @param device  device details
     * @return status
     */
    @PostMapping("/createDeviceOnRack")
    public JSONObject createDevice(@RequestParam(name = "orderId") Long orderId, @RequestParam(name = "rack") String rack, @RequestBody Device device) {
        device.setName(device.getName().toLowerCase());
        rack = rack.toLowerCase();
        logger.info("Inside createDeviceOnRack: {}, {}", rack, device.toString());
        JSONObject response = new JSONObject();
        try {
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
            DeviceMetaModel deviceModel = deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel().toLowerCase());
            if (deviceModel == null) {
                appExceptionHandler.raiseException("Device Meta-model doesn't exist for the device type: " +
                        device.getDeviceModel());
            }
            Rack rackDetails = rackRepository.findByName(rack);
            if (rackDetails == null) {
                appExceptionHandler.raiseException("Given Rack doesn't exist");
            }

            int numOfRackPositionOccupied = deviceModel.getNumOfRackPositionOccupied();
            ArrayList<Integer> freePositions = rackDetails.getFreePositions();
            ArrayList<Integer> reservedPosition = rackDetails.getReservedPositions();

            int startPosition = Integer.parseInt(device.getRackPosition());
            int endPosition = (startPosition + numOfRackPositionOccupied) - 1;
            logger.debug("endPosition :{}", endPosition);

            for (int i = startPosition; i <= endPosition; i++) {
                if (!freePositions.contains(i)) {
                    appExceptionHandler.raiseException("Given Position " + i + " is not available  in rack This are the available position  :" + freePositions);
                }
            }
            ArrayList<Integer> updatedFreePositions = rackDetails.getFreePositions();
            ArrayList<Integer> updatedReservedPosition = rackDetails.getReservedPositions();
            logger.debug("BeforereservedPosition and beforefreePosition:{},{}", updatedReservedPosition, updatedFreePositions);


            for (int i = startPosition; i <= endPosition; i++) {
                updatedReservedPosition.add(i);
                updatedFreePositions.remove(Integer.valueOf(i));
                logger.debug("inside for reservedPosition and freePosition :{},{}", updatedReservedPosition, updatedFreePositions);

            }
            logger.debug("AfterreservedPosition and AfterfreePosition:{},{}", updatedReservedPosition, updatedFreePositions);


            Device existingDeviceDetails = deviceRepository.findByName(device.getName());
            if (existingDeviceDetails != null) {
                appExceptionHandler.raiseException("Device with same name already exist, existingDeviceDetails: " +
                        existingDeviceDetails.toString());
            }

            rackDetails.setReservedPositions(updatedReservedPosition);
            rackDetails.setFreePositions(updatedFreePositions);
            rackRepository.save(rackDetails);
            deviceRepository.createDeviceOnRack(rack, device.getName(), device.getDeviceModel(), device.getLocation(),
                    device.getOrganisation(), device.getCustomer(), device.getManagementIp(), device.getRackPosition(),
                    device.getOperationalState(), device.getAdministrativeState(), device.getUsageState(),
                    device.getSerialNumber(), deviceModel.getShelvesContained(), orderId);
            if (device.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", device.getAdditionalAttributes());
                Device deviceDetails = deviceRepository.findByName(device.getName());
                objectModellerApis.addAdditionalAttributes(device.getAdditionalAttributes(), deviceDetails);
            }
            response.put("status", "Success");
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for updating device on a rack
     *
     * @param orderId orderId id
     * @param rack    rack name
     * @param device  device details
     * @return status
     */
    @PutMapping("/updateDeviceOnRack")
    public JSONObject updateDevice(@RequestParam(name = "orderId") Long orderId, @RequestParam(name = "rack") String rack, @RequestBody Device device) {
        device.setName(device.getName().toLowerCase());
        rack = rack.toLowerCase();
        logger.info("Inside updateDeviceOnRack: {}, {}", rack, device.toString());
        JSONObject response = new JSONObject();
        try {
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
            DeviceMetaModel deviceModel = deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel().toLowerCase());
            if (deviceModel == null) {
                appExceptionHandler.raiseException("Device Meta-model doesn't exist for the device type: " +
                        device.getDeviceModel().toLowerCase());
            }
            Rack rackDetails = rackRepository.findByName(rack);
            if (rackDetails == null) {
                appExceptionHandler.raiseException("Given Rack doesn't exist");
            }
            Device existingDeviceDetails = deviceRepository.getDeviceInRack(rack, device.getName().toLowerCase());
            if (existingDeviceDetails == null) {
                appExceptionHandler.raiseException("Device with the given name doesn't exist on the rack");
            }
            int numOfRackPositionOccupied = deviceModel.getNumOfRackPositionOccupied();
            ArrayList<Integer> freePositions = rackDetails.getFreePositions();
            ArrayList<Integer> reservedPosition = rackDetails.getReservedPositions();
            ArrayList<Integer> updatedFreePositions = rackDetails.getFreePositions();
            ArrayList<Integer> updatedReservedPosition = rackDetails.getReservedPositions();
            int prevStartPosition = Integer.parseInt(existingDeviceDetails.getRackPosition());
            int prevEndPosition = (prevStartPosition + numOfRackPositionOccupied) - 1;
            int presentStartPosition = Integer.parseInt(device.getRackPosition());
            int presentEndPosition = (presentStartPosition + numOfRackPositionOccupied) - 1;
            logger.debug("endPosition :{}", prevEndPosition);
            for (int i = prevStartPosition; i <= prevEndPosition; i++) {
                if (updatedReservedPosition.contains(i)) {
                    updatedFreePositions.add(i);
                    updatedReservedPosition.remove(Integer.valueOf(i));
                }
                logger.debug("inside for reservedPosition and freePosition :{},{}", updatedReservedPosition, updatedFreePositions);
            }
            for (int i = presentStartPosition; i <= presentEndPosition; i++) {
                if (!updatedFreePositions.contains(i)) {
                    appExceptionHandler.raiseException("Given Position " + i + " is not available  in rack This are the available position  :" + freePositions);

                }
            }
            logger.debug("BeforereservedPosition and beforefreePosition:{},{}", updatedReservedPosition, updatedFreePositions);

            for (int i = presentStartPosition; i <= presentEndPosition; i++) {
                updatedReservedPosition.add(i);
                updatedFreePositions.remove(Integer.valueOf(i));
                logger.debug("inside for reservedPosition and freePosition :{},{}", updatedReservedPosition, updatedFreePositions);
            }
            Collections.sort(updatedReservedPosition);
            Collections.sort(updatedFreePositions);
            logger.debug("AfterreservedPosition and AfterfreePosition:{},{}", updatedReservedPosition, updatedFreePositions);
            rackDetails.setReservedPositions(updatedReservedPosition);
            rackDetails.setFreePositions(updatedFreePositions);
            rackRepository.save(rackDetails);
            deviceRepository.updateDeviceOnRack(rack, device.getName().toLowerCase(), device.getDeviceModel().toLowerCase(), device.getLocation(),
                    device.getOrganisation(), device.getCustomer(), device.getManagementIp(), device.getRackPosition(),
                    device.getOperationalState(), device.getAdministrativeState(), device.getUsageState(),
                    device.getSerialNumber(), deviceModel.getShelvesContained(), orderId);
            if (device.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", device.getAdditionalAttributes());
                Device deviceDetails = deviceRepository.findByName(device.getName());
                objectModellerApis.addAdditionalAttributes(device.getAdditionalAttributes(), deviceDetails);
            }
            response.put("status", "Success");
            response.put("device", device);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible to creating card on a device
     *
     * @param orderId orderId id
     * @param device  deviceName
     * @param card    card Details
     * @return status
     */
    @PostMapping("/createCard")
    public JSONObject createCard(@RequestParam(name = "orderId") Long orderId, @RequestParam(name = "device") String device, @RequestBody Card card) {
        card.setName(card.getName().toLowerCase());
        device = device.toLowerCase();
        logger.info("Inside createCard: {}, {}", device, card.toString());
        JSONObject response = new JSONObject();
        try {
            //Validate card vendor
            if (card.getVendor() == null || card.getVendor().trim().isEmpty()) {
                appExceptionHandler.raiseException("Card vendor cannot be empty");
            }
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
            // Check if the device exist
            Device existingDeviceDetails = deviceRepository.findByName(device);
            if (existingDeviceDetails == null) {
                appExceptionHandler.raiseException("Device with the given name doesn't exist");
            }
            // Check if the device model exist
            DeviceMetaModel deviceModel = deviceMetaModelRepository.getDeviceMetaModel(
                    existingDeviceDetails.getDeviceModel().toLowerCase());
            if (deviceModel == null) {
                appExceptionHandler.raiseException("Device Meta-model doesn't exist for the device type: " +
                        existingDeviceDetails.getDeviceModel());
            }
            // Check if the card model exist on the device model
            if (!deviceModel.getAllowedCardList().contains(card.getCardModel())) {
                appExceptionHandler.raiseException("Given card model: " + card.getCardModel() +
                        " is not compatible with the device model, AllowedCardList on the device: " +
                        deviceModel.getAllowedCardList());
            }
            // Check if the shelf exist
            Shelf shelf = shelfRepository.getShelf(device + "_shelf_" + card.getShelfPosition());
            if (shelf == null) {
                appExceptionHandler.raiseException("Given shelf doesn't exist");
            }
            // Check if a card with given name exist on a given device
            Card existingCardDetails = cardRepository.getCard(device, card.getName());
            if (existingCardDetails != null) {
                appExceptionHandler.raiseException("Card with given name already exist, existingCardDetails: " +
                        existingCardDetails.toString());
            }

            // Check if a card with given shelf, slot position exist
            Card cardOnSlot = cardRepository.getCardOnASlot(device + "/" +
                    card.getShelfPosition().toString() + "/" + card.getSlotPosition());
            if (cardOnSlot != null) {
                appExceptionHandler.raiseException("A card already exist on the given shelf, slot position. " +
                        "Existing card details: " + cardOnSlot);
            }

            Slot slot = slotRepository.createSlot(device + "/" + card.getShelfPosition().toString() + "/" +
                            card.getSlotPosition(), card.getSlotPosition(), card.getOperationalState(),
                    card.getAdministrativeState(), card.getUsageState(), shelf.getName());

            Card cardDetails = cardRepository.createCard(card.getName(), card.getShelfPosition(),
                    card.getSlotPosition(), card.getVendor(), card.getCardModel(), card.getCardPartNumber(),
                    card.getOperationalState(), card.getAdministrativeState(), card.getUsageState(),
                    slot.getName(), orderId);
            vendorsRepo.save(new Vendors(card.getVendor()));
            if (card.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", card.getAdditionalAttributes());
                objectModellerApis.addAdditionalAttributes(card.getAdditionalAttributes(), cardDetails);
            }
            response.put("status", "Success");
            response.put("card", cardDetails);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible to updating card on a device
     *
     * @param orderId orderId id
     * @param device  deviceName
     * @param card    card Details
     * @return status
     */
    @PutMapping("/updateCard")
    public JSONObject updateCard(@RequestParam(name = "orderId") Long orderId, @RequestParam(name = "device") String device, @RequestBody Card card) {
        card.setName(card.getName().toLowerCase());
        device = device.toLowerCase();
        logger.info("Inside updateCard: {}, {}", device, card.toString());
        JSONObject response = new JSONObject();
        try {
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
            // Check if the device exist
            Device existingDeviceDetails = deviceRepository.findByName(device);
            if (existingDeviceDetails == null) {
                appExceptionHandler.raiseException("Device with the given name doesn't exist");
            }
            // Check if the device model exist
            DeviceMetaModel deviceModel = deviceMetaModelRepository.getDeviceMetaModel(
                    existingDeviceDetails.getDeviceModel().toLowerCase());
            if (deviceModel == null) {
                appExceptionHandler.raiseException("Device Meta-model doesn't exist for the device type: " +
                        existingDeviceDetails.getDeviceModel().toLowerCase());
            }
            // Check if the card model exist on the device model
            if (!deviceModel.getAllowedCardList().contains(card.getCardModel())) {
                appExceptionHandler.raiseException("Given card model: " + card.getCardModel() +
                        " is not compatible with the device model, AllowedCardList on the device: " +
                        deviceModel.getAllowedCardList());
            }
            Shelf shelf = shelfRepository.getShelf(device.toLowerCase() + "_shelf_" + card.getShelfPosition());
            if (shelf == null) {
                appExceptionHandler.raiseException("Given shelf doesn't exist");
            }
            // Check if a card with given name exist on device
            Card cardDetails = cardRepository.getCard(device, card.getName());
            if (cardDetails == null) {
                appExceptionHandler.raiseException("Given card doesn't exist");
            }

            // check whether a card already exist on the given shelf/slot position
            Card cardOnSlot = cardRepository.getCardOnASlot(device + "/" +
                    card.getShelfPosition().toString() + "/" + card.getSlotPosition());
            if (cardOnSlot != null) {
                if (!cardOnSlot.getName().toLowerCase().equals(card.getName().toLowerCase())) {
                    appExceptionHandler.raiseException("A card already exist on the given shelf, slot position. " +
                            "Existing card details: " + cardOnSlot);
                }
            }

            // delete the slot if the slot position is changed
            if (cardDetails.getSlotPosition() != card.getSlotPosition()) {
                slotRepository.deleteSlot(device + "/" + cardDetails.getShelfPosition().toString() + "/" +
                        cardDetails.getSlotPosition());
            }
            Slot slot = slotRepository.createSlot(device + "/" + card.getShelfPosition().toString() + "/" +
                            card.getSlotPosition(),
                    card.getSlotPosition(), card.getOperationalState(), card.getAdministrativeState(),
                    card.getUsageState(), shelf.getName());

            Card updatedCardDetails = cardRepository.updateCard(card.getName().toLowerCase(), card.getShelfPosition(),
                    card.getSlotPosition(), card.getVendor(), card.getCardModel(), card.getCardPartNumber(),
                    card.getOperationalState(), card.getAdministrativeState(), card.getUsageState(),
                    slot.getName(), orderId);
            if (card.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", card.getAdditionalAttributes());
                objectModellerApis.addAdditionalAttributes(card.getAdditionalAttributes(), cardDetails);
            }
            response.put("status", "Success");
            response.put("card", updatedCardDetails);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible to creating pluggable on a card
     *
     * @param orderId   orderId id
     * @param card      cardName
     * @param pluggable pluggable Details
     * @return status
     */
    @PostMapping("/createPluggable")
    public JSONObject createPluggable(@RequestParam(name = "orderId") Long orderId, @RequestParam(name = "device") String device, @RequestParam(name = "card") String card, @RequestBody Pluggable pluggable) {
        pluggable.setName(pluggable.getName().toLowerCase());
        card = card.toLowerCase();
        device = device.toLowerCase();
        logger.info("Inside createPluggable: {}, {}", card, pluggable.toString());
        JSONObject response = new JSONObject();
        try {
            //Validate pluggable vendor
            if (pluggable.getVendor() == null || pluggable.getVendor().trim().isEmpty()) {
                appExceptionHandler.raiseException("Pluggable vendor cannot be empty");
            }
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
            // Check if the card exist
            Card existingCardDetails = cardRepository.getCard(device, card);
            if (existingCardDetails == null) {
                appExceptionHandler.raiseException("Card with the given name doesn't exist");
            }
            // Check if any pluggable already exist with same name
            Pluggable existingPluggableDetails = pluggableRepository.getPluggable(card, pluggable.getName());
            if (existingPluggableDetails != null) {
                appExceptionHandler.raiseException("Pluggable with given name already exist, " +
                        "existingPluggableDetails: " + existingPluggableDetails.toString());
            }

            // Check if the given position on card is already consumed by a port/plyggable
            Port portsOnCard = portRepository.getPortOnCardByNumber(card + "/" + pluggable.
                    getPositionOnCard().toString(), pluggable.getPositionOnCard());
            if (portsOnCard != null) {
                appExceptionHandler.raiseException("A port already exist on the card with the given port number, " +
                        "existingPortDetails: " + portsOnCard);
            }
            Pluggable pluggablesOnCardSlot = pluggableRepository.getPluggableOnCardByNumber(card + "/" +
                    pluggable.getPositionOnCard().toString(), pluggable.getPositionOnCard());
            if (pluggablesOnCardSlot != null) {
                appExceptionHandler.raiseException("A pluggable already exist on the card with the given position " +
                        "number, existingPluggableDetails: " + pluggablesOnCardSlot);
            }

            CardSlot slot = cardSlotRepository.createCardSlot(card + "/" + pluggable.
                            getPositionOnCard().toString(), pluggable.getPositionOnCard(),
                    pluggable.getOperationalState(), pluggable.getAdministrativeState(),
                    pluggable.getUsageState(), card);

            Pluggable pluggableDetails = pluggableRepository.createPluggable(pluggable.getName(),
                    pluggable.getPositionOnCard(), pluggable.getVendor(), pluggable.getPluggableModel(),
                    pluggable.getPluggablePartNumber(), pluggable.getOperationalState(),
                    pluggable.getAdministrativeState(), pluggable.getUsageState(),
                    slot.getName(), orderId);
            vendorsRepo.save(new Vendors(pluggable.getVendor()));
            if (pluggable.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", pluggable.getAdditionalAttributes());
                objectModellerApis.addAdditionalAttributes(pluggable.getAdditionalAttributes(), pluggableDetails);
            }
            response.put("status", "Success");
            response.put("pluggable", pluggableDetails);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible to creating pluggable on a device
     *
     * @param orderId   orderId id
     * @param device    deviceName
     * @param pluggable pluggable Details
     * @return status
     */
    @PostMapping("/createPluggableOnDevice")
    public JSONObject createPluggableOnDevice(@RequestParam(name = "orderId") Long orderId, @RequestParam(name = "device") String device, @RequestBody Pluggable pluggable) {
        pluggable.setName(pluggable.getName().toLowerCase());
        device = device.toLowerCase();
        logger.info("Inside createPluggableOnDevice: {}, {}", device, pluggable.toString());
        JSONObject response = new JSONObject();
        try {
            //Validate pluggable vendor
            if (pluggable.getVendor() == null || pluggable.getVendor().trim().isEmpty()) {
                appExceptionHandler.raiseException("Pluggable vendor cannot be empty");
            }
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
            // validating device details
            Device existingDeviceDetails = deviceRepository.findByName(device);
            if (existingDeviceDetails == null) {
                appExceptionHandler.raiseException("Device with the given name doesn't exist");
            }

            // validating position
            if (pluggable.getPositionOnDevice() == 0 || pluggable.getPositionOnCard() != 0) {
                appExceptionHandler.raiseException("Invalid input received, please provide valid port number on " +
                        "PositionOnDevice");
            }

            // validating pluggable details for uniqueness
            Pluggable existingPluggableDetails = pluggableRepository.getPluggableOnDevice(pluggable.getName(), device);
            if (existingPluggableDetails != null) {
                appExceptionHandler.raiseException("Pluggable with given name already exist on the device, " +
                        "existingPluggableDetails: " + existingPluggableDetails.toString());
            }

            // validating whether a port or pluggable already exist on the given position
            Port portsOnDevice = portRepository.getPortOnDeviceByNumber(device,
                    pluggable.getPositionOnDevice());
            if (portsOnDevice != null) {
                appExceptionHandler.raiseException(" Port with given name already exist on the device,  " +
                        "existingPortDetails:  " + portsOnDevice);
            }
            Pluggable pluggablesOnDevice = pluggableRepository.getPluggableOnDeviceByNumber(device,
                    pluggable.getPositionOnDevice());
            if (pluggablesOnDevice != null) {
                appExceptionHandler.raiseException("A pluggable already exist on the device with the given position " +
                        "number, existingPluggableDetails: " + pluggablesOnDevice);
            }

            Pluggable pluggableDetails = pluggableRepository.createPluggableOnDevice(pluggable.getName(),
                    pluggable.getPositionOnDevice(), pluggable.getVendor(), pluggable.getPluggableModel(),
                    pluggable.getPluggablePartNumber(), pluggable.getOperationalState(),
                    pluggable.getAdministrativeState(), pluggable.getUsageState(),
                    device, orderId);
            vendorsRepo.save(new Vendors(pluggable.getVendor()));
            if (pluggable.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", pluggable.getAdditionalAttributes());
                objectModellerApis.addAdditionalAttributes(pluggable.getAdditionalAttributes(), pluggableDetails);
            }
            response.put("status", "Success");
            response.put("pluggable", pluggableDetails);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible to updating pluggable on a device
     *
     * @param orderId   orderId id
     * @param device    deviceName
     * @param pluggable pluggable Details
     * @return status
     */
    @PutMapping("/updatePluggableOnDevice")
    public JSONObject updatePluggableOnDevice(@RequestParam(name = "orderId") Long orderId, @RequestParam(name = "device") String device,
                                              @RequestBody Pluggable pluggable) {
        pluggable.setName(pluggable.getName().toLowerCase());
        device = device.toLowerCase();
        logger.info("Inside updatePluggableOnDevice: {}, {}", device, pluggable.toString());
        JSONObject response = new JSONObject();
        try {
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
            // validating device details
            Device existingDeviceDetails = deviceRepository.findByName(device);
            if (existingDeviceDetails == null) {
                appExceptionHandler.raiseException("Device with the given name doesn't exist");
            }

            // validating position
            if (pluggable.getPositionOnDevice() == null) {
                appExceptionHandler.raiseException("Invalid input received, please provide valid port number " +
                        "on PositionOnDevice");
            }

            // Retrieving pluggable details
            Pluggable existingPluggableDetails = pluggableRepository.getPluggableOnDevice(pluggable.getName(), device);
            if (existingPluggableDetails == null) {
                appExceptionHandler.raiseException("Pluggable with given name doesn't exist on the device, ");
            }

            // validating whether a port or pluggable already exist on the given position
            Port portsOnDevice = portRepository.getPortOnDeviceByNumber(device,
                    pluggable.getPositionOnDevice());
            if (portsOnDevice != null) {
                appExceptionHandler.raiseException("A port already exist on the device with the given port number, " +
                        "existingPortDetails: " + portsOnDevice);
            }
            Pluggable pluggablesOnDevice = pluggableRepository.getPluggableOnDeviceByNumber(device,
                    pluggable.getPositionOnDevice());
            if (pluggablesOnDevice != null) {
                if (!pluggablesOnDevice.getName().equalsIgnoreCase(existingPluggableDetails.getName())) {
                    appExceptionHandler.raiseException("A pluggable already exist on the device with the given position " +
                            "number. Existing pluggable details: " + pluggablesOnDevice);
                }
            }

            Pluggable pluggableDetails = pluggableRepository.updatePluggableOnDevice(pluggable.getName(),
                    pluggable.getPositionOnDevice(), pluggable.getVendor(), pluggable.getPluggableModel(),
                    pluggable.getPluggablePartNumber(), pluggable.getOperationalState(),
                    pluggable.getAdministrativeState(), pluggable.getUsageState(),
                    device, orderId);
            if (pluggable.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", pluggable.getAdditionalAttributes());
                objectModellerApis.addAdditionalAttributes(pluggable.getAdditionalAttributes(), pluggableDetails);
            }
            response.put("status", "Success");
            response.put("pluggable", pluggableDetails);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible to updating pluggable on a card
     *
     * @param orderId   orderId id
     * @param card      cardName
     * @param pluggable pluggable Details
     * @return status
     */
    @PutMapping("/updatePluggable")
    public JSONObject updatePluggable(@RequestParam(name = "orderId") Long orderId, @RequestParam(name = "card") String card, @RequestBody Pluggable pluggable) {
        pluggable.setName(pluggable.getName().toLowerCase());
        card = card.toLowerCase();
        logger.info("Inside updatePluggable: {}, {}", card, pluggable.toString());
        JSONObject response = new JSONObject();
        try {
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
            // validating card details
            Card existingCardDetails = cardRepository.getCard(card);
            if (existingCardDetails == null) {
                appExceptionHandler.raiseException("Card with the given name doesn't exist");
            }

            // validating if the pluggable exist on the card
            Pluggable existingPluggableDetails = pluggableRepository.getPluggable(card, pluggable.getName());
            if (existingPluggableDetails == null) {
                appExceptionHandler.raiseException("Pluggable with given name doesn't exist");
            }

            // validating if the position is already consumed by a port or pluggable
            Port portsOnCardSlot = portRepository.getPortOnCardByNumber(card + "/" +
                    pluggable.getPositionOnCard().toString(), pluggable.getPositionOnCard());
            if (portsOnCardSlot != null) {
                appExceptionHandler.raiseException("A port already exist on the card with the given port number, " +
                        "existingPortDetails: " + portsOnCardSlot);
            }
            Pluggable pluggablesOnCardSlot = pluggableRepository.getPluggableOnCardByNumber(
                    card + "/" + pluggable.getPositionOnCard().toString(), pluggable.getPositionOnCard());
            if (pluggablesOnCardSlot != null) {
                if (!pluggablesOnCardSlot.getName().equalsIgnoreCase(existingPluggableDetails.getName())) {
                    appExceptionHandler.raiseException("A pluggable already exist on the card with the " +
                            "given position number, existingPluggableDetails: " +
                            pluggablesOnCardSlot);
                }
            }

            // deleting the card slot if the position is changed and create new one
            String slotName = card + "/" + existingPluggableDetails.getPositionOnCard().toString();
            if (existingPluggableDetails.getPositionOnCard() != pluggable.getPositionOnCard()) {
                logger.debug("Position changed, deleting the card slot and creating new one");
                cardSlotRepository.deleteCardSlot(card + "/" + existingPluggableDetails.
                        getPositionOnCard().toString());
                CardSlot slot = cardSlotRepository.createCardSlot(card + "/" +
                                pluggable.getPositionOnCard().toString(),
                        pluggable.getPositionOnCard(), pluggable.getOperationalState(), pluggable.getAdministrativeState(),
                        pluggable.getUsageState(), card);
                slotName = slot.getName();
            }

            Pluggable pluggableDetails = pluggableRepository.updatePluggable(pluggable.getName(),
                    pluggable.getPositionOnCard(), pluggable.getVendor(), pluggable.getPluggableModel(),
                    pluggable.getPluggablePartNumber(), pluggable.getOperationalState(),
                    pluggable.getAdministrativeState(), pluggable.getUsageState(),
                    slotName, orderId);
            if (pluggable.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", pluggable.getAdditionalAttributes());
                objectModellerApis.addAdditionalAttributes(pluggable.getAdditionalAttributes(), pluggableDetails);
            }
            response.put("status", "Success");
            response.put("pluggable", pluggableDetails);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for deleting pluggable
     *
     * @param orderId   orderId id
     * @param pluggable pluggable Name
     * @return status
     */
    @DeleteMapping("/deletePluggable")
    public JSONObject deletePluggable(@RequestParam(name = "orderId") Long orderId, @RequestParam(name = "pluggable") String pluggable) {
        pluggable = pluggable.toLowerCase();
        logger.info("Inside deletePluggable: {}", pluggable);
        JSONObject response = new JSONObject();
        try {
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
            // validating pluggable details
            Pluggable existingPluggableDetails = pluggableRepository.getPluggable(pluggable);
            if (existingPluggableDetails == null) {
                appExceptionHandler.raiseException("Pluggable with given name doesn't exist");
            }

            ArrayList<PhysicalConnection> physicalConnections = physicalConnectionRepository.
                    validatePluggableForDeletion(existingPluggableDetails.getName());
            if (physicalConnections.size() > 0) {
                appExceptionHandler.raiseException("Pluggable is connected to a physical connection, please delete the" +
                        " connection before deleting the pluggable");
            }
            pluggableRepository.deletePluggable(pluggable, orderId);
            response.put("status", "Success");
            response.put("pluggable", pluggable);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for deleting pluggable
     *
     * @param orderId orderId id
     * @param card    card Name
     * @param port    port Details
     * @return status
     */
    @PostMapping("/createPort")
    public JSONObject createPort(@RequestParam(name = "orderId") Long orderId, @RequestParam(name = "card") String card, @RequestBody Port port) {
        card = card.toLowerCase();
        logger.info("Inside createPort: {}, {}", card, port.toString());
        JSONObject response = new JSONObject();
        try {
            // Check if the order exist or not
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
            // validating card details
            Card existingCardDetails = cardRepository.getCard(card);
            if (existingCardDetails == null) {
                appExceptionHandler.raiseException("Card with the given name doesn't exist");
            }
            // validating if the port already exist
            Port existingPortDetails = portRepository.getPortOnCard(card, port.getName().toLowerCase());
            if (existingPortDetails != null) {
                appExceptionHandler.raiseException("Port with given name already exist, existingPortDetails: " +
                        existingPortDetails.toString());
            }
            // validating if the position is already consumed by a port or pluggable
            Pluggable pluggableOnCardSlot = pluggableRepository.getPluggableOnCardByNumber(
                    card + "/" + port.getPositionOnCard().toString(), port.getPositionOnCard());
            if (pluggableOnCardSlot != null) {
                appExceptionHandler.raiseException("A pluggable already exist on the card with the given port number, " +
                        "existingPluggableDetails: " + pluggableOnCardSlot);
            }
            Port portsOnCardSlot = portRepository.getPortOnCardByNumber(
                    card + "/" + port.getPositionOnCard().toString(), port.getPositionOnCard());
            if (portsOnCardSlot != null) {
                appExceptionHandler.raiseException("A port already exist on the card with the given port number, " +
                        "existingPortDetails: " + portsOnCardSlot);
            }

            CardSlot slot = cardSlotRepository.createCardSlot(card + "/" + port.getPositionOnCard().toString(),
                    port.getPositionOnCard(), port.getOperationalState(), port.getAdministrativeState(),
                    port.getUsageState(), card);

            Port portDetails = portRepository.createPort(port.getName().toLowerCase(), port.getPositionOnCard(), port.getPortType(),
                    port.getOperationalState(), port.getAdministrativeState(), port.getUsageState(),
                    slot.getName(), orderId);
            if (port.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", port.getAdditionalAttributes());
                objectModellerApis.addAdditionalAttributes(port.getAdditionalAttributes(), portDetails);
            }
            response.put("status", "Success");
            response.put("port", portDetails);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for updating port on a card
     *
     * @param orderId orderId id
     * @param card    cardName
     * @param port    port details
     * @return status
     */
    @PutMapping("/updatePort")
    public JSONObject updatePort(@RequestParam(name = "orderId") Long orderId, @RequestParam(name = "device") String device, @RequestParam(name = "card") String card, @RequestBody Port port) {
        card = card.toLowerCase();
        port.setName(port.getName().toLowerCase());
        logger.info("Inside updatePort: {}, {}", card, port.toString());
        JSONObject response = new JSONObject();
        try {
            // Check if the order exist or not
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
            // validating card details
            Card existingCardDetails = cardRepository.getCard(device, card);
            if (existingCardDetails == null) {
                appExceptionHandler.raiseException("Card with the given name doesn't exist");
            }
            // validating if the port already exist
            Port existingPortDetails = portRepository.getPortOnCard(card, port.getName());
            if (existingPortDetails == null) {
                appExceptionHandler.raiseException("Port with given name doesn't exist");
            }

            // validating if the position is already consumed by a port or pluggable
            Pluggable pluggableOnCardSlot = pluggableRepository.getPluggableOnCardByNumber(
                    card + "/" + port.getPositionOnCard().toString(), port.getPositionOnCard());
            logger.debug("pluggableOnCardSlot: {}", pluggableOnCardSlot);
            if (pluggableOnCardSlot != null) {
                appExceptionHandler.raiseException("A pluggable already exist on the card with the given " +
                        "port number, existingPluggableDetails: " + pluggableOnCardSlot);
            }
            Port portsOnCardSlot = portRepository.getPortOnCardByNumber(
                    card + "/" + existingPortDetails.getPositionOnCard().toString(), port.getPositionOnCard());
            if (portsOnCardSlot != null) {
                if (!portsOnCardSlot.getName().equals(port.getName())) {
                    logger.debug("portsOnCardSlot: {}", portsOnCardSlot);
                    appExceptionHandler.raiseException("A port already exist on the card with the given port number, " +
                            "existingPortDetails: " + portsOnCardSlot);
                }
            }

            // validating if the position is changed
            String cardSlotName = card + "/" + existingPortDetails.getPositionOnCard().toString();
            if (existingPortDetails.getPositionOnCard() != port.getPositionOnCard()) {
                cardSlotRepository.deleteCardSlot(card + "/" + existingPortDetails.getPositionOnCard().toString());
                CardSlot slot = cardSlotRepository.createCardSlot(card + "/" + port.getPositionOnCard().toString(),
                        port.getPositionOnCard(), port.getOperationalState(), port.getAdministrativeState(),
                        port.getUsageState(), card);
                cardSlotName = slot.getName();
            }

            Port portDetails = portRepository.updatePort(port.getName(),
                    port.getPositionOnCard(), port.getPortType(), port.getOperationalState(),
                    port.getAdministrativeState(), port.getUsageState(),
                    cardSlotName, orderId);
            if (port.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", port.getAdditionalAttributes());
                objectModellerApis.addAdditionalAttributes(port.getAdditionalAttributes(), portDetails);
            }
            response.put("status", "Success");
            response.put("port", portDetails);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for creating port on a device
     *
     * @param orderId orderId id
     * @param device  deviceName
     * @param port    port details
     * @return status
     */
    @PostMapping("/createPortOnDevice")
    public JSONObject createPortOnDevice(@RequestParam("orderId") Long orderId, @RequestParam(name = "device") String device, @RequestBody Port port) {
        device = device.toLowerCase();
        port.setName(port.getName().toLowerCase());
        logger.info("Inside createPortOnDevice: {}, {}", device, port.toString());
        JSONObject response = new JSONObject();
        try {
            // Check if the order exist or not
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
            // Check if device exist
            Device existingDeviceDetails = deviceRepository.findByName(device);
            if (existingDeviceDetails == null) {
                appExceptionHandler.raiseException("Device with the given name doesn't exist");
            }

            // Check if port number is valid
            if (port.getPositionOnDevice() == 0 || port.getPositionOnCard() != 0) {
                appExceptionHandler.raiseException("Invalid input received, please provide valid port number on " +
                        "PositionOnDevice");
            }

            // Check if port name already exist on the device
            Port existingPortDetails = portRepository.getPort(device, port.getName());
            if (existingPortDetails != null) {
                appExceptionHandler.raiseException("Port with given name already exist on the device, existingPortDetails: " +
                        existingPortDetails.toString());
            }

            // Check if any pluggable already exist on the device with the given port number
            Pluggable pluggableOnDevice = pluggableRepository.getPluggableOnDeviceByNumber(device,
                    port.getPositionOnDevice());
            if (pluggableOnDevice != null) {
                appExceptionHandler.raiseException("A pluggable already exist on the device with the " +
                        "given port number, existingPluggableDetails: " + pluggableOnDevice);
            }
            Port portDetailsOnSameNo = portRepository.getPortOnDeviceByNumber(device, port.getPositionOnDevice());
            if (portDetailsOnSameNo != null) {
                appExceptionHandler.raiseException("A port already exist on the device with the given port number, " +
                        "existingPortDetails: " + portDetailsOnSameNo);
            }

            Port portDetails = portRepository.createPortOnDevice(port.getName(),
                    port.getPositionOnDevice(), port.getPortType(), port.getOperationalState(),
                    port.getAdministrativeState(), port.getUsageState(),
                    device, orderId);
            if (port.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", port.getAdditionalAttributes());
                objectModellerApis.addAdditionalAttributes(port.getAdditionalAttributes(), portDetails);
            }
            response.put("status", "Success");
            response.put("port", portDetails);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for updating port on a device
     *
     * @param orderId orderId id
     * @param device  deviceName
     * @param port    port details
     * @return status
     */
    @PutMapping("/updatePortOnDevice")
    public JSONObject updatePortOnDevice(@RequestParam("orderId") Long orderId, @RequestParam(name = "device") String device, @RequestBody Port port) {
        device = device.toLowerCase();
        port.setName(port.getName().toLowerCase());
        logger.info("Inside updatePortOnDevice: {}, {}", device, port.toString());
        JSONObject response = new JSONObject();
        try {
            // Check if the order exist or not
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
            // Check if device exist
            Device existingDeviceDetails = deviceRepository.findByName(device);
            if (existingDeviceDetails == null) {
                appExceptionHandler.raiseException("Device with the given name doesn't exist");
            }
            // Check if port number is valid
            if (port.getPositionOnDevice() == 0 || port.getPositionOnCard() != 0) {
                appExceptionHandler.raiseException("Invalid input received, please provide valid port number " +
                        "on PositionOnDevice and PositionOnCard");
            }
            // Check if port name already exist on the device
            Port existingPortDetails = portRepository.getPort(device, port.getName());
            if (existingPortDetails == null) {
                appExceptionHandler.raiseException("Port with given name doesn't exist");
            }

            // Check if any pluggable already exist on the device with the given port number
            Pluggable pluggableOnDevice = pluggableRepository.getPluggableOnDeviceByNumber(device,
                    port.getPositionOnDevice());
            if (pluggableOnDevice != null) {
                appExceptionHandler.raiseException("A pluggable already exist on the card with the given port number, " +
                        "existingPluggableDetails: " + pluggableOnDevice);
            }
            // Check if any port already exist on the device with the given port number
            Port portDetailsOnSameNo = portRepository.getPortOnDeviceByNumber(device, port.getPositionOnDevice());
            if (portDetailsOnSameNo != null) {
                if (!portDetailsOnSameNo.getName().equalsIgnoreCase(port.getName())) {
                    appExceptionHandler.raiseException("A port already exist on the device with the given port number, " +
                            "existingPortDetails: " + portDetailsOnSameNo);
                }
            }

            Port portDetails = portRepository.updatePortOnDevice(port.getName(),
                    port.getPositionOnDevice(), port.getPortType(), port.getOperationalState(),
                    port.getAdministrativeState(), port.getUsageState(),
                    device, orderId);
            if (port.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", port.getAdditionalAttributes());
                objectModellerApis.addAdditionalAttributes(port.getAdditionalAttributes(), portDetails);
            }
            response.put("status", "Success");
            response.put("port", portDetails);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for deleting port
     *
     * @param orderId orderId id
     * @param port    port name
     * @return status
     */
    @DeleteMapping("/deletePort")
    public JSONObject deletePort(@RequestParam("orderId") Long orderId, @RequestParam(name = "device") String device, @RequestParam(name = "port") String port) {
        device = device.toLowerCase();
        port = port.toLowerCase();
        logger.info("Inside deletePort: {}", port);
        JSONObject response = new JSONObject();
        try {
            // Check if the order exist or not
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
            Port portDetails = portRepository.getPort(device, port);
            if (portDetails == null) {
                appExceptionHandler.raiseException("Port with the given name doesn't exist");
            }
            ArrayList<PhysicalConnection> physicalConnections = physicalConnectionRepository.
                    validatePortForDeletion(portDetails.getName());
            if (physicalConnections.size() > 0) {
                appExceptionHandler.raiseException("Port can't be deleted as it is used in , " +
                        "physicalConnections: ");
            }
            portRepository.deletePort(port, orderId);
            response.put("status", "Success");
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    @GetMapping("/getDeviceById")
    public Device getDeviceById(@RequestParam(name = "id") Long id) {
        logger.info("Inside getDeviceById: {}", id);
        Device device = null;
        try {
            device = deviceRepository.findDevicesById(id);
            device.setHref("https://" + ip + ":" + port + "/getDeviceById?id=" + device.getId());
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return device;
    }

    @GetMapping("/getCardById")
    public Card getCardById(@RequestParam(name = "id") Long id) {
        logger.info("Inside getCardById: {}", id);
        Card card = new Card();
        try {
            card = cardRepository.findCardById(id);
            card.setHref("https://" + ip + ":" + port + "/getCardById?id=" + card.getId());
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return card;
    }

    @GetMapping("/getCardSlotById")
    public ArrayList<CardSlot> getCardSlotById(@RequestParam("id") Long id) {
        logger.info("Inside getCardSlotById: {}", id);
        ArrayList<CardSlot> cardSlots = new ArrayList<>();
        try {
            CardSlot cardSlot = cardSlotRepository.findCardSlotById(id);
            cardSlot.setHref("https://" + ip + ":" + port + "/getCardSlotById?id=" + cardSlot.getId());
            cardSlots.add(cardSlot);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return cardSlots;
    }

    @GetMapping("/getCardSlotByName")
    public ArrayList<CardSlot> getCardSlotByName(@RequestParam("name") String slotName) {
        slotName = slotName.toLowerCase();
        logger.info("Inside getCardSlotByName: {}", slotName);
        ArrayList<CardSlot> cardSlots = new ArrayList<>();
        try {
            CardSlot cardSlot = cardSlotRepository.getByName(slotName);
            cardSlot.setHref("https://" + ip + ":" + port + "/getCardSlotById?id=" + cardSlot.getId());
            cardSlots.add(cardSlot);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return cardSlots;
    }

    @GetMapping("/getCardSlotOnCard")
    public ArrayList<CardSlot> getCardSlotOnCard(@RequestParam("cardName") String cardName) {
        cardName = cardName.toLowerCase();
        ArrayList<CardSlot> cardSlots = null;
        try {
            logger.info("Inside getCardSlotOnCard: {}", cardName);
            cardSlots = cardSlotRepository.getCardSlotsOnACard(cardName);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return cardSlots;
    }

    @GetMapping("/getCardOnSlot")
    public ArrayList<Card> getCardOnSlot(@RequestParam(name = "slotName") String slotName) {
        slotName = slotName.toLowerCase();
        ArrayList<Card> cards = new ArrayList<>();
        try {
            logger.info("Inside getCardOnSlot: {}", slotName);
            cards.add(cardRepository.getCardOnASlot(slotName));
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return cards;
    }

    @GetMapping("/getPluggableById")
    public Pluggable getPluggableById(@RequestParam("id") Long id) {
        logger.info("Inside getPluggableById: {}", id);
        Pluggable pluggable = null;
        try {
            pluggable = pluggableRepository.findPluggableById(id);
            pluggable.setHref("https://" + ip + ":" + port + "/getPluggableById?id=" + pluggable.getId());
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return pluggable;
    }

    @GetMapping("/getPortById")
    public Port getPortById(@RequestParam("id") Long id) {
        logger.info("Inside getPortById: {}", id);
        Port port = null;
        try {
            port = portRepository.findPortById(id);
            port.setHref("https://" + ip + ":" + port + "/getPortById?id=" + port.getId());
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return port;
    }

    @GetMapping("/getPluggableOnCardSlot")
    public Pluggable getPluggableOnCardByCardSlot(@RequestParam("cardSlotName") String cardSlotName) {
        cardSlotName = cardSlotName.toLowerCase();
        logger.info("Inside getPluggableOnCardByCardSlot: {}", cardSlotName);
        Pluggable pluggableOnCardByCardSlot = null;
        try {
            pluggableOnCardByCardSlot = pluggableRepository.getPluggableOnCardSlot(cardSlotName);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return pluggableOnCardByCardSlot;
    }

    @GetMapping("/getPortOnCardSlot")
    public Port getPortOnCardByCardSlot(@RequestParam("cardSlotName") String cardSlotName) {
        cardSlotName = cardSlotName.toLowerCase();
        logger.info("Inside getPortOnCardByCardSlot: {}", cardSlotName);
        Port portOnCardByCardSlot = null;
        try {
            portOnCardByCardSlot = portRepository.getPortOnCardSlot(cardSlotName);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return portOnCardByCardSlot;
    }

    /**
     * This function is responsible for creating Logical port on a physical port in a card
     *
     * @param orderId orderId id
     * @param name    cardName
     * @param port    logical port details
     * @return status
     */
    @PostMapping("/createLogicalPortOnCard")
    public JSONObject createLogicalPortOnPhysicalPortInCard(@RequestParam(name = "orderId") Long orderId, @RequestParam(name = "card") String name,
                                                            @RequestBody LogicalPort port) {
        name = name.toLowerCase();
        port.setName(port.getName().toLowerCase());
        logger.info("Inside createLogicalPortOnPhysicalPortInCard: {}, {}", name, port);
        JSONObject response = new JSONObject();
        try {
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
            Card existingCardDetails = cardRepository.getCard(name);
            if (existingCardDetails == null) {
                appExceptionHandler.raiseException("Card with the given name doesn't exist");
            }
            ArrayList<LogicalPort> existingPortDetails = logicalPortRepository.
                    getLogicPortsOnCard(name, port.getName().toLowerCase(), port.getPositionOnPort());
            // TODO: do the same for ports and pluggables
            if (existingPortDetails.size() != 0) {
                appExceptionHandler.raiseException("LogicalPort with given name already exist on the card," +
                        " existingPortDetails: " + existingPortDetails.toString());
            }

            String cardSlotName = name + "/" + port.getPositionOnCard().toString();
            Port portsOnCardSlot = portRepository.getPortOnCardByNumber(cardSlotName, port.getPositionOnCard());
            if (portsOnCardSlot == null) {
                Pluggable pluggableOnCardSlot = pluggableRepository.getPluggableOnCardSlot(cardSlotName);
                if (pluggableOnCardSlot == null) {
                    appExceptionHandler.raiseException("A pluggable / port doesn't exist on the card with the given" +
                            " port number");
                }
            }
            LogicalPort logicalPort = logicalPortRepository.createLogicalPortOnCard(port.getName().toLowerCase(),
                    port.getPositionOnCard(), port.getPortType(), port.getOperationalState(),
                    port.getAdministrativeState(), port.getUsageState(),
                    cardSlotName, port.getPositionOnPort(), orderId);
            if (port.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", port.getAdditionalAttributes());
                objectModellerApis.addAdditionalAttributes(port.getAdditionalAttributes(), logicalPort);
            }
            response.put("status", "Success");
            response.put("logicalPort", logicalPort);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for Updating Logical port on a physical port in a card
     *
     * @param orderId orderId id
     * @param name    cardName Name
     * @param port    port Details
     * @return status
     */
    @PutMapping("/updateLogicalPortOnCard")
    public JSONObject updateLogicalPortOnPhysicalPortInCard(@RequestParam(name = "orderId") Long orderId, @RequestParam(name = "device") String device, @RequestParam(name = "card") String name,
                                                            @RequestBody LogicalPort port) {
        String deviceName = device.toLowerCase();
        String card = name.toLowerCase();
        port.setName(port.getName().toLowerCase());
        logger.info("Inside updateLogicalPortOnPhysicalPortInCard: {}, {}", card, port);
        JSONObject response = new JSONObject();
        try {
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
            // Similar checks as in the create method
            Card existingCardDetails = cardRepository.getCard(deviceName, card);
            if (existingCardDetails == null) {
                appExceptionHandler.raiseException("Card with the given name doesn't exist");
            }
            ArrayList<LogicalPort> existingPortDetails = logicalPortRepository.
                    getLogicPortsOnCard(card, port.getName(), port.getPositionOnPort());
            if (existingPortDetails == null || existingPortDetails.size() == 0) {
                appExceptionHandler.raiseException("LogicalPort with given name doesn't exist on the card,");
            }
            String cardSlotName = card + "/" + port.getPositionOnCard().toString();
            LogicalPort updatedLogicalPort = logicalPortRepository.updateLogicalPortOnCard(port.getName(),
                    port.getPositionOnCard(), port.getPortType(), port.getOperationalState(),
                    port.getAdministrativeState(), port.getUsageState(),
                    cardSlotName, orderId);
            if (port.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", port.getAdditionalAttributes());
                objectModellerApis.addAdditionalAttributes(port.getAdditionalAttributes(), updatedLogicalPort);
            }
            response.put("status", "Success");
            response.put("logicalPort", updatedLogicalPort);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for creating Logical port on a physical port on a Device
     *
     * @param orderId    orderId id
     * @param deviceName deviceName
     * @param port       logical port details
     * @return status
     */
    @PostMapping("/createLogicalPortOnDevice")
    public JSONObject createLogicalPortOnPhysicalPortOnDevice(@RequestParam(name = "orderId") Long orderId, @RequestParam(name = "device") String deviceName,
                                                              @RequestBody LogicalPort port) {
        logger.info("Inside createLogicalPortOnPhysicalPortOnDevice: {}, {}", deviceName, port);
        JSONObject response = new JSONObject();
        try {
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
            deviceName = deviceName.toLowerCase();
            Device existingDeviceDetails = deviceRepository.getByName(deviceName);
            if (existingDeviceDetails == null) {
                appExceptionHandler.raiseException("Device with the given name doesn't exist");
            }
            logger.info("Found device details {}", existingDeviceDetails);

            Port portOnDevice = portRepository.getPortOnDeviceByNumber(deviceName, port.getPositionOnDevice());
            if (portOnDevice == null) {
                appExceptionHandler.raiseException("No port found on the device " + deviceName +
                        " with given portPositon " +
                        port.getPositionOnDevice() +
                        " with portName: " + port.getName());
            }
            logger.info("Port found on device, info {}", portOnDevice);

            LogicalPort findExistingLogicalPort = logicalPortRepository.getLogicalPortOnDevice(deviceName, port.getPositionOnDevice(), port.getPositionOnPort());

            logger.info("ExistingLogicalPort {}", findExistingLogicalPort);
            if (findExistingLogicalPort != null) {
                appExceptionHandler.raiseException("Logical Port with given name is already existed on" +
                        "the given port " + port.getName()
                        + findExistingLogicalPort.toString());
            }

            LogicalPort logicalPort = logicalPortRepository.createLogicalPortOnDevice(port.getName().toLowerCase(),
                    port.getPositionOnDevice(), port.getPortType(), port.getOperationalState(),
                    port.getAdministrativeState(), port.getUsageState(),
                    port.getPortSpeed(), port.getCapacity(), deviceName, port.getPositionOnPort(), orderId);

            if (port.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", port.getAdditionalAttributes());
                objectModellerApis.addAdditionalAttributes(port.getAdditionalAttributes(), logicalPort);
            }
            response.put("status", "LogicalPort successfully created!");
            response.put("logicalPort", logicalPort);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for updating Logical port on a physical port on a Device
     *
     * @param orderId    orderId id
     * @param deviceName deviceName
     * @param port       logical port details
     * @return status
     */
    @PostMapping("/updateLogicalPortOnDevice")
    public JSONObject updateLogicalPortOnPhysicalPortOnDevice(@RequestParam(name = "orderId") Long orderId, @RequestParam(name = "device") String deviceName,
                                                              @RequestBody LogicalPort port) {
        logger.info("Inside updateLogicalPortOnPhysicalPortOnDevice: {}, {}", deviceName, port);
        JSONObject response = new JSONObject();
        try {
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
            deviceName = deviceName.toLowerCase();
            Device existingDeviceDetails = deviceRepository.getByName(deviceName);
            if (existingDeviceDetails == null) {
                appExceptionHandler.raiseException("Device with the given name doesn't exist");
            }
            logger.info("Found device details {}", existingDeviceDetails);

            Port portOnDevice = portRepository.getPortOnDeviceByNumber(deviceName, port.getPositionOnDevice());
            if (portOnDevice == null) {
                appExceptionHandler.raiseException("No port found on the device " + deviceName +
                        " with given portPositon " +
                        port.getPositionOnDevice() +
                        " with portName: " + port.getName());
            }
            logger.info("Port found on device, info {}", portOnDevice);

            LogicalPort findExistingLogicalPort = logicalPortRepository.getLogicalPortOnDevice(deviceName, port.getPositionOnDevice(), port.getPositionOnPort());

            if (findExistingLogicalPort == null) {
                appExceptionHandler.raiseException("Logical Port with given name is not existed "
                        + findExistingLogicalPort.toString());
            }

            LogicalPort logicalPort = logicalPortRepository.updateLogicalPortOnDevice(port.getName().toLowerCase(),
                    port.getPositionOnDevice(), port.getPortType(), port.getOperationalState(),
                    port.getAdministrativeState(), port.getUsageState(),
                    port.getPortSpeed(), port.getCapacity(), deviceName, port.getPositionOnPort(), orderId);
            if (port.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", port.getAdditionalAttributes());
                objectModellerApis.addAdditionalAttributes(port.getAdditionalAttributes(), logicalPort);
            }
            response.put("status", "LogicalPort successfully updated!");
            response.put("logicalPort", logicalPort);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }


    @Autowired
    CityRepository cityRepository;

    @Autowired
    CountryRepository countryRepository;

    @Autowired
    StateRepository stateRepository;
    @Autowired
    ObjectCountRepo objectCountRepo;

    @Autowired
    AsyncService asyncService;

    @PutMapping("/syncDashboard")
    public ResponseEntity<Object> syncDashboardDetails() {
        logger.info("Sync DashboardDetails");
        asyncService.syncDashboard();
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/dashboard")
    public List<ObjectCount> getDashboardDetails() {
        logger.info("Inside getDashboardDetails");
        List<ObjectCount> dashboardDetails = new ArrayList<>();
        try {
            List<String> objectTypes = new ArrayList<>(Arrays.asList("country", "state", "city", "building", "rack", "deviceMetaModel"));
            dashboardDetails.addAll(objectCountRepo.findByObjectTypeIn(objectTypes));
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return dashboardDetails;
    }

    @GetMapping("/deviceEntityCount")
    public List<ObjectCount> getDeviceEntityCount() {
        logger.info("Inside getDeviceEntityCount");
        List<ObjectCount> dashboardDetails = new ArrayList<>();
        try {
            List<String> objectTypes = new ArrayList<>(Arrays.asList("device", "card", "port", "pluggable",
                    "logicalPort"));
            dashboardDetails.addAll(objectCountRepo.findByObjectTypeIn(objectTypes));
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return dashboardDetails;
    }


    /*
     * Device Delete APIs
     */
    @Autowired
    PhysicalConnectionRepository physicalConnectionRepository;

    @Autowired
    LogicalConnectionRepository logicalConnectionRepository;

    /**
     * This function is responsible for deleting a device
     *
     * @param orderId    orderId id
     * @param deviceName device name
     * @return status
     */
    @DeleteMapping("/deleteDevice")
    public JSONObject deleteDevice(@RequestParam(name = "orderId") Long orderId,
                                   @RequestParam("name") String deviceName) {
        deviceName = deviceName.toLowerCase();
        logger.info("Inside deleteDevice: {}", deviceName);
        JSONObject response = new JSONObject();
        try {
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
            Device device = deviceRepository.findByName(deviceName);
            if (device == null) {
                appExceptionHandler.raiseException("Device with the given name doesn't exist");
            }
            ArrayList<PhysicalConnection> physicalConnections = physicalConnectionRepository.
                    validateDeviceForDeletion(deviceName);
            if (physicalConnections.size() != 0) {
                if (physicalConnections.get(0).getName() != null) {
                    appExceptionHandler.raiseException("Device with the given name cannot be deleted, " +
                            "as it is being used in physical connections");
                }
            }
            deviceRepository.deleteDeviceAndAllRelatedNodes(deviceName, orderId);
            response.put("status", "Success");
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for deleting a device
     *
     * @param orderId  orderId id
     * @param cardname cardname name
     * @return status
     */
    @DeleteMapping("/deleteCard")
    public JSONObject deleteCard(@RequestParam(name = "orderId") Long orderId, @RequestParam("deviceName") String deviceName, @RequestParam("name") String cardname) {
        String device = deviceName.toLowerCase();
        String name = cardname.toLowerCase();
        logger.info("Inside deleteCard: {}", name);
        JSONObject response = new JSONObject();
        try {
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
            Card card = cardRepository.getCard(device, name);
            if (card == null) {
                appExceptionHandler.raiseException("Card with the given name doesn't exist");
            }
            ArrayList<PhysicalConnection> physicalConnections = physicalConnectionRepository.
                    validateCardForDeletion(name);
            if (physicalConnections.size() != 0) {
                if (physicalConnections.get(0).getName() != null) {
                    appExceptionHandler.raiseException("Card with the given name cannot be deleted, " +
                            "as it is being used in physical connections");
                }
            }
            cardRepository.deleteCardAndAllRelatedNodes(name, orderId);
            response.put("status", "Success");
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for updating device using device name
     *
     * @param orderId    orderId id
     * @param deviceName device name
     * @param device     device details
     * @return status
     */
    @PutMapping("/updateDeviceByName")
    public JSONObject updateDeviceWithName(@RequestParam(name = "orderId") Long orderId,
                                           @RequestParam(name = "name") String deviceName, @RequestBody Device device) {
        logger.info("Inside updateDeviceWithName: {}", device.toString());
        JSONObject response = new JSONObject();
        try {
            //validate name field
            if (device.getName() == null) {
                appExceptionHandler.raiseException("Device name field is mandatory");
            }
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
            if (!device.getName().equalsIgnoreCase(deviceName)) {
                appExceptionHandler.raiseException("Request parameter does not match with the request body");
            }

            //validate device model
            DeviceMetaModel deviceModel = deviceMetaModelRepository.getDeviceMetaModel(device.getDeviceModel());
            if (deviceModel == null) {
                appExceptionHandler.raiseException("Device Meta-model doesn't exist for the device type: " +
                        device.getDeviceModel());
            }

            deviceName = deviceName.toLowerCase();
            //get existing details
            Device existingDeviceDetails = deviceRepository.findByName(deviceName);
            if (existingDeviceDetails == null) {
                appExceptionHandler.raiseException("Device with the given name " + deviceName + " doesn't exist");
            }

            deviceRepository.updateDevice(deviceName, device.getDeviceModel(), device.getLocation(),
                    device.getOrganisation(), device.getCustomer(), device.getManagementIp(), device.getRackPosition(),
                    device.getOperationalState(), device.getAdministrativeState(), device.getUsageState(),
                    device.getSerialNumber(), deviceModel.getShelvesContained(), orderId);
            if (device.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", device.getAdditionalAttributes());
                Device deviceDetails = deviceRepository.findByName(device.getName());
                objectModellerApis.addAdditionalAttributes(device.getAdditionalAttributes(), deviceDetails);
            }
            response.put("status", "Success");
            logger.info("Updated device successfully");
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for updating card using card name
     *
     * @param orderId  orderId id
     * @param cardName card name
     * @param card     card Details
     * @return status
     */
    @PutMapping("/updateCardByName")
    public JSONObject updateCardByName(@RequestParam(name = "orderId") Long orderId, @RequestParam(name = "deviceName") String name, @RequestParam(name = "name") String cardName, @RequestBody Card card) {
        logger.info("Inside updateCardByName: {}", card.toString());
        JSONObject response = new JSONObject();
        try {
            //validate card name
            if (card.getName() == null) {
                appExceptionHandler.raiseException("Card name field is mandatory");
            }
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
            if (!card.getName().equalsIgnoreCase(cardName)) {
                appExceptionHandler.raiseException("Request parameter does not match with the request body");
            }
            cardName = cardName.toLowerCase();
            name = name.toLowerCase();
            //get existing details
            Card existingCardDetails = cardRepository.getCard(name, cardName);
            if (existingCardDetails == null) {
                appExceptionHandler.raiseException("Card with the given name " + cardName + " doesn't exist");
            }

            Device deviceDetails = deviceRepository.findByName(name);
            if (deviceDetails == null) {
                appExceptionHandler.raiseException("Unable to get device details for the given device name: " + name);
            }

            // Check if the device model exist
            DeviceMetaModel deviceModel = deviceMetaModelRepository.getDeviceMetaModel(deviceDetails.getDeviceModel());
            if (deviceModel == null) {
                appExceptionHandler.raiseException("Device Meta-model doesn't exist for the device type: " +
                        deviceDetails.getDeviceModel());
            }

            // Check if the card model is allowed on the device model
            if (!deviceModel.getAllowedCardList().contains(card.getCardModel())) {
                appExceptionHandler.raiseException("Given card model: " + card.getCardModel() +
                        " is not compatible with the device model, AllowedCardList on the device: " +
                        deviceModel.getAllowedCardList());
            }

            //validate shelf position
            Shelf shelf = shelfRepository.getShelf(name + "_shelf_" + card.getShelfPosition());
            if (shelf == null) {
                appExceptionHandler.raiseException("Given shelf doesn't exist");
            }

            //get existing card details
            Card cardDetails = cardRepository.getCard(name, cardName);
            if (cardDetails == null) {
                appExceptionHandler.raiseException("Given card doesn't exist");
            }

            // check whether a card already exist on the given shelf/slot position
            Card cardOnSlot = cardRepository.getCardOnASlot(name + "/" +
                    card.getShelfPosition().toString() + "/" + card.getSlotPosition());
            if (cardOnSlot != null) {
                if (!cardOnSlot.getName().equals(cardName)) {
                    appExceptionHandler.raiseException("A card already exist on the given shelf, slot position. " +
                            "Existing card details: " + cardOnSlot);
                }
            }

            // delete the slot if the slot position is changed
            if (cardDetails.getSlotPosition() != card.getSlotPosition()) {
                slotRepository.deleteSlot(name + "/" + cardDetails.getShelfPosition().toString() + "/" +
                        cardDetails.getSlotPosition());
            }
            slotRepository.createSlot(name + "/" + card.getShelfPosition().toString() + "/" +
                            card.getSlotPosition(),
                    card.getSlotPosition(), card.getOperationalState(), card.getAdministrativeState(),
                    card.getUsageState(), shelf.getName());

            Card updatedCardDetails = cardRepository.updateCard(cardName, card.getShelfPosition(),
                    card.getSlotPosition(), card.getVendor(), card.getCardModel(), card.getCardPartNumber(),
                    card.getOperationalState(), card.getAdministrativeState(), card.getUsageState(),
                    deviceDetails.getName() + "/" + card.getShelfPosition().toString() + "/" + card.getSlotPosition(), orderId);
            if (card.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", card.getAdditionalAttributes());
                objectModellerApis.addAdditionalAttributes(card.getAdditionalAttributes(), updatedCardDetails);
            }
            response.put("status", "Success");
            response.put("card", updatedCardDetails);
            logger.info("Updated card successfully");

        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }


    /**
     * This function is responsible for updating pluggable with a given pluggable name
     *
     * @param orderId       orderId id
     * @param pluggableName pluggable name
     * @param pluggable     pluggable Details
     * @return status
     */
    @PutMapping("/updatePluggableByName")
    public JSONObject updatePluggableByName(@RequestParam(name = "orderId") Long orderId,
                                            @RequestParam(name = "name") String pluggableName, @RequestBody Pluggable pluggable) {
        logger.info("Inside updatePluggableByName: {}", pluggable.toString());
        JSONObject response = new JSONObject();
        try {
            //validate pluggable name
            if (pluggableName == null) {
                appExceptionHandler.raiseException("Pluggable name field is mandatory");
            }
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
            if (!pluggable.getName().equalsIgnoreCase(pluggableName)) {
                appExceptionHandler.raiseException("Request parameter does not match with the request body");
            }

            pluggableName = pluggableName.toLowerCase();
            //get existing details
            Pluggable existingPluggableDetails = pluggableRepository.getPluggable(pluggableName);
            if (existingPluggableDetails == null) {
                appExceptionHandler.raiseException("Pluggable with the given name " + pluggableName + " doesn't exist");
            }

            Pluggable pluggableDetails = null;
            //check if the pluggable exists on a device
            if (pluggable.getPositionOnDevice() != null && pluggable.getPositionOnDevice() != 0) {
                Device deviceDetails = deviceRepository.findByPluggableName(pluggableName);
                if (deviceDetails == null) {
                    appExceptionHandler.raiseException("Pluggable with the given name " + pluggableName +
                            " doesn't exist on a device to update");
                }

                logger.info("The given pluggable exists on the device {} at position {}", deviceDetails.getName(),
                        pluggable.getPositionOnDevice());
                pluggableDetails = pluggableRepository.updatePluggableOnDevice(pluggable.getName(),
                        pluggable.getPositionOnDevice(), pluggable.getVendor(), pluggable.getPluggableModel(),
                        pluggable.getPluggablePartNumber(), pluggable.getOperationalState(),
                        pluggable.getAdministrativeState(), pluggable.getUsageState(),
                        deviceDetails.getName(), orderId);
            } else if (pluggable.getPositionOnCard() != null && pluggable.getPositionOnCard() != 0) {
                Card cardDetails = cardRepository.findByPluggableName(pluggableName);
                if (cardDetails == null) {
                    appExceptionHandler.raiseException("Pluggable with the given name " + pluggableName +
                            " doesn't exist on a card to update");
                }

                logger.info("The given pluggable exists on the card {} at position {}", cardDetails.getName(),
                        pluggable.getPositionOnCard());

                // validating if the position is already consumed by a port or pluggable
                Port portsOnCardSlot = portRepository.getPortOnCardByNumber(cardDetails.getName() + "/" +
                        pluggable.getPositionOnCard().toString(), pluggable.getPositionOnCard());
                if (portsOnCardSlot != null) {
                    appExceptionHandler.raiseException("A port already exist on the card with the given port number, " +
                            "existingPortDetails: " + portsOnCardSlot);
                }
                Pluggable pluggablesOnCardSlot = pluggableRepository.getPluggableOnCardByNumber(
                        cardDetails.getName() + "/" + pluggable.getPositionOnCard().toString(), pluggable.getPositionOnCard());
                if (pluggablesOnCardSlot != null) {
                    if (!pluggablesOnCardSlot.getName().equalsIgnoreCase(existingPluggableDetails.getName())) {
                        appExceptionHandler.raiseException("A pluggable already exist on the card with the " +
                                "given position number, existingPluggableDetails: " +
                                pluggablesOnCardSlot);
                    }
                }

                // deleting the card slot if the position is changed and create new one
                String slotName = cardDetails.getName() + "/" + existingPluggableDetails.getPositionOnCard().toString();
                if (existingPluggableDetails.getPositionOnCard() != pluggable.getPositionOnCard()) {
                    logger.debug("Position changed, deleting the card slot and creating new one");
                    cardSlotRepository.deleteCardSlot(cardDetails.getName() + "/" + existingPluggableDetails.
                            getPositionOnCard().toString());
                    CardSlot slot = cardSlotRepository.createCardSlot(cardDetails.getName() + "/" +
                                    pluggable.getPositionOnCard().toString(),
                            pluggable.getPositionOnCard(), pluggable.getOperationalState(), pluggable.getAdministrativeState(),
                            pluggable.getUsageState(), cardDetails.getName());
                    slotName = slot.getName();
                }
                pluggableDetails = pluggableRepository.updatePluggable(pluggableName,
                        pluggable.getPositionOnCard(), pluggable.getVendor(), pluggable.getPluggableModel(),
                        pluggable.getPluggablePartNumber(), pluggable.getOperationalState(),
                        pluggable.getAdministrativeState(), pluggable.getUsageState(),
                        slotName, orderId);
            } else {
                appExceptionHandler.raiseException("Pluggable with the given name " + pluggableName + " doesn't exist on a device or card");
            }
            if (pluggable.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", pluggable.getAdditionalAttributes());
                objectModellerApis.addAdditionalAttributes(pluggable.getAdditionalAttributes(), pluggableDetails);
            }
            logger.info("Updated pluggable successfully");
            response.put("status", "Success");
            response.put("pluggable", pluggableDetails);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for updating port using port name
     *
     * @param orderId  orderId id
     * @param portName port name
     * @param port     port details
     * @return status
     */
    @PutMapping("/updatePortByName")
    public JSONObject updatePortByName(@RequestParam(name = "orderId") Long orderId,
                                       @RequestParam(name = "name") String portName, @RequestBody Port port) {
        logger.info("Inside updatePortByName: {}", port.toString());
        JSONObject response = new JSONObject();
        try {
            //validate port name
            if (portName == null) {
                appExceptionHandler.raiseException("Port name field is mandatory");
            }
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
            if (!port.getName().equalsIgnoreCase(portName)) {
                appExceptionHandler.raiseException("Request parameter does not match with the request body");
            }
            portName = portName.toLowerCase();

            //validate existing port details
            Port existingPortDetails = portRepository.getPort(portName);
            if (existingPortDetails == null) {
                appExceptionHandler.raiseException("Port with the given name " + portName + " doesn't exist");
            }

            Port portDetails = null;
            if (port.getPositionOnCard() != null && port.getPositionOnCard() != 0) {
                Card cardDetails = cardRepository.getCardFromPortName(portName);
                if (cardDetails == null) {
                    appExceptionHandler.raiseException("Port with the given name " + portName + " doesn't exist on a card to update");
                }
                String cardName = cardDetails.getName();

                // validating if the position is already consumed by a port or pluggable
                Pluggable pluggableOnCardSlot = pluggableRepository.getPluggableOnCardByNumber(
                        cardName + "/" + port.getPositionOnCard().toString(), port.getPositionOnCard());
                logger.debug("pluggableOnCardSlot: {}", pluggableOnCardSlot);
                if (pluggableOnCardSlot != null) {
                    appExceptionHandler.raiseException("A pluggable already exist on the card with the given " +
                            "port number, existingPluggableDetails: " + pluggableOnCardSlot);
                }
                Port portsOnCardSlot = portRepository.getPortOnCardByNumber(
                        cardName + "/" + existingPortDetails.getPositionOnCard().toString(), port.getPositionOnCard());
                if (portsOnCardSlot != null) {
                    if (!portsOnCardSlot.getName().equals(port.getName())) {
                        logger.debug("portsOnCardSlot: {}", portsOnCardSlot);
                        appExceptionHandler.raiseException("A port already exist on the card with the given port number, " +
                                "existingPortDetails: " + portsOnCardSlot);
                    }
                }

                // validating if the position is changed
                String cardSlotName = cardName + "/" + existingPortDetails.getPositionOnCard().toString();
                if (existingPortDetails.getPositionOnCard() != port.getPositionOnCard()) {
                    cardSlotRepository.deleteCardSlot(cardName + "/" + existingPortDetails.getPositionOnCard().toString());
                    CardSlot slot = cardSlotRepository.createCardSlot(cardName + "/" + port.getPositionOnCard().toString(),
                            port.getPositionOnCard(), port.getOperationalState(), port.getAdministrativeState(),
                            port.getUsageState(), cardName);
                    cardSlotName = slot.getName();
                }

                portDetails = portRepository.updatePort(portName,
                        port.getPositionOnCard(), port.getPortType(), port.getOperationalState(),
                        port.getAdministrativeState(), port.getUsageState(),
                        cardSlotName, orderId);
            } else if (port.getPositionOnDevice() != null && port.getPositionOnDevice() != 0) {
                Device deviceDetails = deviceRepository.findByPortName(portName);
                if (deviceDetails == null) {
                    appExceptionHandler.raiseException("Port with the given name " + portName + " doesn't exist on a device to update");
                }

                portDetails = portRepository.updatePortOnDevice(portName,
                        port.getPositionOnDevice(), port.getPortType(), port.getOperationalState(),
                        port.getAdministrativeState(), port.getUsageState(),
                        deviceDetails.getName(), orderId);
            } else {
                appExceptionHandler.raiseException("Port position on card or device is not provided");
            }
            if (port.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", port.getAdditionalAttributes());
                objectModellerApis.addAdditionalAttributes(port.getAdditionalAttributes(), portDetails);
            }
            response.put("status", "Success");
            response.put("port", portDetails);
            logger.info("Updated port successfully");
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for updating logical port using the name
     *
     * @param orderId         orderId id
     * @param logicalPortName logical port name
     * @param logicalPort     logical port details
     * @return status
     */
    @PutMapping("/updateLogicalPortByName")
    public JSONObject updateLogicalPortByName(@RequestParam(name = "orderId") Long orderId,
                                              @RequestParam(name = "name") String logicalPortName,
                                              @RequestBody LogicalPort logicalPort) {
        logger.info("Inside updateLogicalPortByName: {}", logicalPort);
        JSONObject response = new JSONObject();
        try {
            //validate logical port name
            if (logicalPortName == null) {
                appExceptionHandler.raiseException("Logical port name field is mandatory");
            }
            OrderEntity existingOrder = orderRepository.findOrderById(orderId);
            if (existingOrder == null) {
                appExceptionHandler.raiseException("The order " + orderId + " is not found");
            }
            if (!logicalPort.getName().equalsIgnoreCase(logicalPortName)) {
                appExceptionHandler.raiseException("Request parameter does not match with the request body");
            }

            logicalPortName = logicalPortName.toLowerCase();
            LogicalPort existingLogicalPort = logicalPortRepository.getPort(logicalPortName);
            logger.debug("ExistingLogicalPort: " + existingLogicalPort);
            if (existingLogicalPort == null) {
                appExceptionHandler.raiseException("LogicalPort with the given name " + logicalPortName + " doesn't exist");
            }

            LogicalPort updatedLogicalPort;
            //get physical port associated with this logical port
            Port port = portRepository.getPortFromLogicalPortName(logicalPortName);
            if (port == null) {
                //check for pluggable
                Pluggable pluggable = pluggableRepository.getPluggableFromLogicalPortName(logicalPortName);
                if (pluggable == null) {
                    appExceptionHandler.raiseException("LogicalPort with the given name " + logicalPortName +
                            " doesn't exist on a port or pluggable to update");
                }

                logger.debug("Given logical port exists on a pluggable");
                if (logicalPort.getPositionOnCard() != null && logicalPort.getPositionOnCard() != 0) {
                    //check for pluggable on card
                    logger.debug("Checking for pluggable on card");
                    Card cardDetails = cardRepository.findByPluggableName(pluggable.getName());
                    if (cardDetails == null) {
                        appExceptionHandler.raiseException("Pluggable " + pluggable.getName() + " doesn't exist on a card");
                    }

                    //validate pluggable position matches with logical port position
                    logger.debug("Checking if pluggable position matches");
                    if (!pluggable.getPositionOnCard().equals(logicalPort.getPositionOnCard())) {
                        appExceptionHandler.raiseException("Pluggable position on card doesn't match with logical port position");
                    }
                } else if (logicalPort.getPositionOnDevice() != null && logicalPort.getPositionOnDevice() != 0) {
                    //check for pluggable on device
                    logger.debug("Checking for pluggable on device");
                    Device deviceDetails = deviceRepository.findByPluggableName(pluggable.getName());
                    if (deviceDetails == null) {
                        appExceptionHandler.raiseException("Pluggable " + pluggable.getName() + " doesn't exist on a device");
                    }

                    //validate pluggable position matches with logical port position
                    logger.debug("Checking if pluggable position matches");
                    if (!pluggable.getPositionOnDevice().equals(logicalPort.getPositionOnDevice())) {
                        appExceptionHandler.raiseException("Pluggable position on device doesn't match with logical port position");
                    }
                } else {
                    appExceptionHandler.raiseException("Logical port position on card or device is not provided");
                }

            } else {
                logger.debug("Given logical port exists on a physical port");
                if (logicalPort.getPositionOnCard() != null) {
                    //check for physical port on card
                    logger.debug("Checking for physical port on card");
                    Card cardDetails = cardRepository.getCardFromPortName(port.getName());
                    if (cardDetails == null) {
                        appExceptionHandler.raiseException("Physical port " + port.getName() + " doesn't exist on a card");
                    }

                    //validate physical port position matches with logical port position
                    logger.debug("Checking if physical port position matches");
                    if (!port.getPositionOnCard().equals(logicalPort.getPositionOnCard())) {
                        appExceptionHandler.raiseException("Physical port position on card doesn't match with logical port position");
                    }
                } else if (logicalPort.getPositionOnDevice() != null) {
                    //check for physical port on device
                    logger.debug("Checking for physical port on device");
                    Device deviceDetails = deviceRepository.findByPortName(port.getName());
                    if (deviceDetails == null) {
                        appExceptionHandler.raiseException("Physical port " + port.getName() + " doesn't exist on a device");
                    }

                    //validate physical port position matches with logical port position
                    logger.debug("Checking if physical port position matches");
                    if (!port.getPositionOnDevice().equals(logicalPort.getPositionOnDevice())) {
                        appExceptionHandler.raiseException("Physical port position on device doesn't match with logical port position");
                    }
                } else {
                    appExceptionHandler.raiseException("Logical port position on card or device is not provided");
                }
            }
            updatedLogicalPort = logicalPortRepository.updateLogicalPort(logicalPortName,
                    logicalPort.getPositionOnCard(), logicalPort.getPositionOnDevice(), logicalPort.getPortType(),
                    logicalPort.getOperationalState(), logicalPort.getAdministrativeState(), logicalPort.getUsageState(), orderId);
            if (logicalPort.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", logicalPort.getAdditionalAttributes());
                objectModellerApis.addAdditionalAttributes(logicalPort.getAdditionalAttributes(), updatedLogicalPort);
            }
            response.put("status", "Success");
            response.put("logicalPort", updatedLogicalPort);

        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for getting the all Ports on card
     *
     * @param cardName card name
     * @return port entites
     */
    @GetMapping("/getAllPortsOnCard")
    public ArrayList<Port> getAllPortsOnCard(@RequestParam("deviceName") String deviceName, @RequestParam("name") String cardName) {
        deviceName = deviceName.toLowerCase();
        cardName = cardName.toLowerCase();
        logger.info("Inside getPortsOnCard: {}", cardName);
        ArrayList<Port> portsOnCard = null;
        try {
            portsOnCard = portRepository.getAllPortsOnCard(deviceName, cardName);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return portsOnCard;
    }

    @GetMapping("/getAllPortsOnCardBySlot")
    public ArrayList<Port> getAllPortsOnCardBySlot(@RequestParam("slotName") String slotName) {
        slotName = slotName.toLowerCase();
        ArrayList<Port> portsOnCardBySlot = null;
        try {
            Slot existingSlot = slotRepository.getSlot(slotName);
            if (existingSlot == null) {
                appExceptionHandler.raiseException("Give slot name is not available: " + slotName);
            }
            portsOnCardBySlot = portRepository.getAllPortsOnCardBySlot(slotName);
            logger.info("inside portsOnCardBySlot :{}", portsOnCardBySlot);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return portsOnCardBySlot;
    }

    /**
     * This function is responsible for getting the all Pluggables on card
     *
     * @param cardName card name
     * @return pluggable entites
     */
    @GetMapping("/getAllPluggablesOnCard")
    public ArrayList<Pluggable> getAllPluggablesOnCard(@RequestParam("deviceName") String deviceName, @RequestParam("name") String cardName) {
        cardName = cardName.toLowerCase();
        deviceName = deviceName.toLowerCase();
        ArrayList<Pluggable> pluggables = null;
        try {
            logger.info("Inside getPluggablesOnCard: {}", cardName);
            pluggables = pluggableRepository.getAllPluggablesOnCard(deviceName, cardName);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return pluggables;
    }

    /**
     * This function is responsible for getting the pluggables related to given device name
     *
     * @param deviceName device name
     * @return card entites
     */
    @GetMapping("/getAllPortsOnDevice")
    public ArrayList<Port> getAllPortsOnDevice(@RequestParam("name") String deviceName) {
        deviceName = deviceName.toLowerCase();
        ArrayList<Port> ports = null;
        try {
            logger.info("Inside getPortsOnDevice: {}", deviceName);
            ports = portRepository.getPortsOnDevice(deviceName);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return ports;
    }

    /**
     * This function is responsible for getting the pluggables related to given device name
     *
     * @param deviceName device name
     * @return card entites
     */
    @GetMapping("/getAllPluggablesOnDevice")
    public ArrayList<Pluggable> getAllPluggalblesOnDevice(@RequestParam("name") String deviceName) {
        deviceName = deviceName.toLowerCase();
        ArrayList<Pluggable> pluggables = null;
        try {
            logger.info("Inside getPluggablesOnDevice: {}", deviceName);
            pluggables = pluggableRepository.getPluggablesOnDevice(deviceName);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return pluggables;
    }

    @GetMapping("/getAllCardsOnDevice")
    public ArrayList<Card> getAllCardsOnDevice(@RequestParam(name = "deviceName") String name) {
        String deviceName = name.toLowerCase();
        ArrayList<Card> cards = null;
        try {
            logger.info("Inside getAllCardsOnDevice: {}", deviceName);
            cards = cardRepository.getAllCardsOnDevice(deviceName);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return cards;
    }

    @GetMapping("/getCardModelsForDevice")
    public ArrayList<String> getCardModelsForDevice(@RequestParam(name = "deviceName") String name) {
        DeviceMetaModel model = new DeviceMetaModel();
        try {
            if (name == null) {
                appExceptionHandler.raiseException("Device name is not provided");
            }
            String deviceName = name.trim().toLowerCase();
            logger.info("Inside getCardModelsForDevice: {}", deviceName);
            if (deviceRepository.getByName(deviceName) == null) {
                appExceptionHandler.raiseException("Device doesn't exist");
            }
            String deviceMetaModel = deviceRepository.getDeviceModel(deviceName);
            if (deviceMetaModel == null) {
                appExceptionHandler.raiseException("Device model doesn't exist for this device");
            }
            model = deviceMetaModelRepository.getDeviceMetaModel(deviceMetaModel);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return model.getAllowedCardList();
    }

    @GetMapping("/getAvailableShelvesOnDevice")
    public ArrayList<Integer> getAvailableShelvesOnDevice(@RequestParam(name = "deviceName") String name) {
        ArrayList<Integer> shelfPositionsList = new ArrayList<>();
        try {
            if (name == null) {
                appExceptionHandler.raiseException("Device name is not provided");
            }
            String deviceName = name.trim().toLowerCase();
            logger.info("Inside getAvailableShelvesOnDevice: {}", deviceName);
            if (deviceRepository.getByName(deviceName) == null) {
                appExceptionHandler.raiseException("Device doesn't exist");
            }
            ArrayList<Shelf> shelves = getShelvesOnDevice(deviceName);
            Set<Integer> shelfPositions = new HashSet<>();
            for (Shelf shelf : shelves) {
                if (shelfRepository.getCardOnShelf(shelf.getName()).isEmpty()) {
                    shelfPositions.add(shelf.getShelfPosition());
                }
            }
            shelfPositionsList = new ArrayList<>(shelfPositions);
            Collections.sort(shelfPositionsList);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return shelfPositionsList;
    }

    @GetMapping("/findLogicalPortByName")
    public LogicalPort getLogicalPortByName(@RequestParam(name = "logicalPortName") String name) {
        LogicalPort logicalPortDetails = null;
        try {
            String logicalPort = name.toLowerCase();
            logicalPortDetails = logicalPortRepository.getByName(logicalPort);
            if (logicalPortDetails == null) {
                appExceptionHandler.raiseException("The logicalPort is not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return logicalPortDetails;
    }

    /**
     * This function is responsible for deleting a logical Port
     *
     * @param orderId     orderId id
     * @param logicalPort logicalPort Details
     * @return status
     */
    @DeleteMapping("/deleteLogicalPort")
    public JSONObject deleteLogicalPort(@RequestParam(name = "orderId") Long orderId, @RequestParam(name = "logicalPort") String logicalPort) {
        logicalPort = logicalPort.toLowerCase();
        logger.info("Inside deleteLogicalPort: {}", logicalPort);
        JSONObject response = new JSONObject();
        try {
            LogicalPort logicalPortDetails = logicalPortRepository.getPort(logicalPort);
            if (logicalPortDetails == null) {
                appExceptionHandler.raiseException("Logical Port with the given name doesn't exist");
            }
            ArrayList<LogicalConnection> logicalConnections = logicalConnectionRepository.
                    validateLogicalPortForDeletion(logicalPortDetails.getName());
            if (logicalConnections.size() > 0) {
                appExceptionHandler.raiseException("LogicalPort can't be deleted as it is used in , " +
                        "logicalConnections: ");
            }
            logicalPortRepository.deletePort(logicalPort, orderId);
            response.put("status", "Success");
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    @GetMapping("/findLogicalPortsOnCardRelation")
    public ArrayList<LogicalPort> findLogicalPortsOnCardRelation(@RequestParam(name = "Name") String name) {
        name = name.toLowerCase();
        ArrayList<LogicalPort> LogicalPortOnCard = null;
        try {
            logger.info("Inside findLogicalPortsOnCardRelation: {}", name);
            LogicalPortOnCard = logicalPortRepository.findLogicalPortsOnCardRelation(name);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return LogicalPortOnCard;
    }

    @GetMapping("/getLogicalPortOnDevice")
    public ArrayList<LogicalPort> getLogicalPortOnDevice(@RequestParam(name = "Name") String name) {
        name = name.toLowerCase();
        ArrayList<LogicalPort> LogicalPortOnDevice = null;
        try {
            logger.info("Inside findLogicalPortsOnCardRelation: {}", name);
            LogicalPortOnDevice = logicalPortRepository.findLogicalPortDevice(name);

        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return LogicalPortOnDevice;
    }

    @GetMapping("/getAllPluggableOnCardBySlot")
    public ArrayList<Pluggable> getAllPluggableOnCardBySlot(@RequestParam("slotName") String slotName) {
        slotName = slotName.toLowerCase();
        ArrayList<Pluggable> PluggableOnCardBySlot = null;
        try {
            Slot existingSlot = slotRepository.getSlot(slotName);
            if (existingSlot == null) {
                appExceptionHandler.raiseException("Give slot name is not available: " + slotName);
            }
            PluggableOnCardBySlot = pluggableRepository.getAllPluggableOnCardBySlot(slotName);
            logger.info("inside portsOnCardBySlot :{}", PluggableOnCardBySlot);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return PluggableOnCardBySlot;
    }

    @GetMapping("/findDevicesByName")
    public ArrayList<String> findDevicesByName(@RequestParam("name") String deviceName) {
        deviceName = deviceName.toLowerCase();
        ArrayList<String> findDevicesByName = new ArrayList<>();
        try {
            findDevicesByName = deviceRepository.findDeviceByNameContaining(deviceName);
            logger.info("inside findDevicesByName :{}", deviceName);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return findDevicesByName;
    }
}

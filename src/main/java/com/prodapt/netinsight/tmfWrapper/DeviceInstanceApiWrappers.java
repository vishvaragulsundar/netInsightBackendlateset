package com.prodapt.netinsight.tmfWrapper;

import com.prodapt.netinsight.deviceInstanceManager.*;
import com.prodapt.netinsight.exceptionsHandler.AppExceptionHandler;
import com.prodapt.netinsight.locationManager.*;
import com.prodapt.netinsight.deviceMetaModeller.DeviceMetaModel;
import com.prodapt.netinsight.deviceMetaModeller.DeviceMetaModelRepository;
import com.prodapt.netinsight.tmfWrapper.pojo.DeviceDetailsResponse;
import com.prodapt.netinsight.tmfWrapper.pojo.RelatedParty;
import com.prodapt.netinsight.tmfWrapper.pojo.ResourceSpecification;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * This class is responsible for handling all TMF APIs for device instance management
 */
@RestController
public class DeviceInstanceApiWrappers {

    Logger logger = LoggerFactory.getLogger(DeviceInstanceApiWrappers.class);

    @Autowired
    DeviceInstanceRestApis deviceInstanceRestApis;

    @Autowired
    DeviceMetaModelRepository deviceMetaModelRepository;

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    PortRepository portRepository;

    @Autowired
    PluggableRepository pluggableRepository;

    @Value(value = "${server.port}")
    private String serverPort;

    @Autowired
    BuildingRepository buildingRepository;

    @Autowired
    RackRepository rackRepository;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    ShelfRepository shelfRepository;

    @Autowired
    SlotRepository slotRepository;

    @Autowired
    CardSlotRepository cardSlotRepository;

    @Autowired
    AppExceptionHandler appExceptionHandler;

    public String ip = InetAddress.getLocalHost().getHostAddress();

    public DeviceInstanceApiWrappers() throws UnknownHostException {
    }

    @GetMapping("/tmf-api/resourceInventoryManagement/v4/resource/device")
    public DeviceDetailsResponse getDeviceDetailsTmf(@RequestParam(name = "name") String name) {
        try {
            name = name.toLowerCase();
            Device deviceDetails = deviceInstanceRestApis.getDevice(name);
            DeviceDetailsResponse deviceDetailsTmf = new DeviceDetailsResponse();
            deviceDetailsTmf.setId(deviceDetails.getId());
            deviceDetailsTmf.setName(deviceDetails.getName());
            deviceDetailsTmf.setType("device");

            ArrayList<HashMap<String, String>> resourceCharacteristics = new ArrayList<>();
            resourceCharacteristics.add(new HashMap<String, String>() {
                {
                    put("name", "serialNumber");
                    put("value", deviceDetails.getSerialNumber());
                }
            });
            resourceCharacteristics.add(new HashMap<String, String>() {
                {
                    put("name", "managementIp");
                    put("value", deviceDetails.getManagementIp());
                }
            });
            resourceCharacteristics.add(new HashMap<String, String>() {
                {
                    put("name", "rackPosition");
                    put("value", String.valueOf(deviceDetails.getRackPosition()));
                }
            });

            resourceCharacteristics.add(new HashMap<String, String>() {
                {
                    put("name", "deviceModel");
                    put("value", deviceDetails.getDeviceModel());
                }
            });
            deviceDetailsTmf.setResourceCharacteristics(resourceCharacteristics);


            deviceDetailsTmf.setOperationalState(deviceDetails.getOperationalState());
            deviceDetailsTmf.setAdministrativeState(deviceDetails.getAdministrativeState());
            deviceDetailsTmf.setUsageState(deviceDetails.getUsageState());
            ArrayList<Shelf> shelves = deviceDetails.getShelves();
            for (Shelf shelf : shelves) {
                shelf.setHref("https://" + ip + ":" + serverPort + "/getShelfById?id=" + shelf.getId());
                for (Slot slot : shelf.getSlots()) {
                    slot.setHref("https://" + ip + ":" + serverPort + "/getSlotById?id=" + slot.getId());
                    for (Card card : slot.getCards()) {
                        card.setHref("https://" + ip + ":" + serverPort + "/getCardById?id=" + card.getId());
                        for (CardSlot cardSlot : card.getCardSlots()) {
                            cardSlot.setHref("https://" + ip + ":" + serverPort + "/getCardSlotById?id=" + cardSlot.getId());
                            if (cardSlot.getPluggables() != null) {
                                cardSlot.getPluggables().setHref("https://" + ip + ":" + serverPort + "/getPluggableById?id="
                                        + cardSlot.getPluggables().getId());
                            }
                            if (cardSlot.getPorts() != null) {
                                cardSlot.getPorts().setHref("https://" + ip + ":" + serverPort + "/getPortById?id="
                                        + cardSlot.getPorts().getId());
                            }
                        }
                    }
                }
            }
            if (deviceDetails.getDevicePluggables() != null) {
                for (Pluggable pluggable : deviceDetails.getDevicePluggables()) {
                    pluggable.setHref("https://" + ip + ":" + serverPort + "/getPluggableById?id=" + pluggable.getId());
                }
            }
            if (deviceDetails.getDevicePorts() != null) {
                for (Port portDetails : deviceDetails.getDevicePorts()) {
                    portDetails.setHref("https://" + ip + ":" + serverPort + "/getPortById?id=" + portDetails.getId());
                }
            }
            deviceDetailsTmf.setDevicePluggables(deviceDetails.getDevicePluggables());
            deviceDetailsTmf.setDevicePorts(deviceDetails.getDevicePorts());

            deviceDetailsTmf.setShelves(shelves);

            DeviceMetaModel modelDetails = deviceMetaModelRepository.
                    getDeviceMetaModel(deviceDetails.getDeviceModel());
            deviceDetailsTmf.setResourceSpecification(new ResourceSpecification(modelDetails.getId(),
                    modelDetails.getDeviceModel(),
                    "https://" + ip + ":" + serverPort + "/getModelById?id=" + modelDetails.getId()));

            Building building = buildingRepository.getBuildingForDevice(name);
            Rack rack = null;
            logger.debug("building for device: {}", building);
            if (building == null) {
                building = buildingRepository.getBuildingForDeviceOnRack(name);
                logger.debug("Value of building for device on rack: {}", building);
                building.setHref("https://" + ip + ":" + serverPort + "/getBuildingById?id=" + building.getId());
            } else {
                building.setHref("https://" + ip + ":" + serverPort + "/getBuildingById?id=" + building.getId());
            }
            deviceDetailsTmf.setPlace(new ArrayList<>(Arrays.asList(building)));

            rack = rackRepository.getRackWithDeviceName(name);
            if (rack != null) {
                rack.setHref("https://" + ip + ":" + serverPort + "/getRackById?id=" + rack.getId());
                deviceDetailsTmf.setRelatedParty(new RelatedParty(rack));
            }

            return deviceDetailsTmf;
        } catch (Exception e) {
            logger.error("Exception in getDeviceDetailsTmf: {}", e);
            appExceptionHandler.raiseException(e.getMessage());
        }
        return null;
    }

    @PostMapping("/tmf-api/resourceInventoryManagement/v4/resource/createdevice")
    public JSONObject createDeviceTmf(@RequestBody DeviceDetailsResponse deviceDetailsTmf) {
        try {
            logger.info("Inside createDeviceTmf for device: {}", deviceDetailsTmf.getName());
            logger.debug("Device details: {}", deviceDetailsTmf);
            Device device = deviceRepository.findByName(deviceDetailsTmf.getName().toLowerCase());
            if (device == null) {
                device = new Device();
                device.setName(deviceDetailsTmf.getName().toLowerCase());
            }
            for (HashMap<String, String> resourceCharacteristic : deviceDetailsTmf.getResourceCharacteristics()) {
                if (resourceCharacteristic.get("name").equals("managementIp")) {
                    device.setManagementIp(resourceCharacteristic.get("value"));
                } else if (resourceCharacteristic.get("name").equals("serialNumber")) {
                    device.setSerialNumber(resourceCharacteristic.get("value"));
                } else if (resourceCharacteristic.get("name").equals("rackPosition")) {
                    device.setRackPosition(resourceCharacteristic.get("value"));
                } else if (resourceCharacteristic.get("name").equals("deviceModel")) {
                    device.setDeviceModel(resourceCharacteristic.get("value").toLowerCase());
                }
            }

            device.setOperationalState(deviceDetailsTmf.getOperationalState());
            device.setAdministrativeState(deviceDetailsTmf.getAdministrativeState());
            device.setUsageState(deviceDetailsTmf.getUsageState());

            device.setDevicePorts(deviceDetailsTmf.getDevicePorts());
            device.setDevicePluggables(deviceDetailsTmf.getDevicePluggables());

            ArrayList<Shelf> shelves = deviceDetailsTmf.getShelves();
            ArrayList<Shelf> deviceShelves = new ArrayList<>();
            if (shelves != null) {
                for (Shelf shelf : shelves) {
                    Shelf copiedShelf = new Shelf();
                    copiedShelf.setName(shelf.getName().toLowerCase());
                    copiedShelf.setShelfPosition(shelf.getShelfPosition());
                    copiedShelf.setOperationalState(shelf.getOperationalState());
                    copiedShelf.setAdministrativeState(shelf.getAdministrativeState());
                    copiedShelf.setUsageState(shelf.getUsageState());
                    copiedShelf.setHref("https://" + ip + ":" + serverPort + "/getShelfById?id=" + shelf.getId());

                    ArrayList<Slot> slots = shelf.getSlots();
                    ArrayList<Slot> copiedSlots = new ArrayList<>();
                    for (Slot slot : slots) {
                        Slot copiedSlot = new Slot();
                        copiedSlot.setName(slot.getName().toLowerCase());
                        copiedSlot.setSlotPosition(slot.getSlotPosition());
                        copiedSlot.setOperationalState(slot.getOperationalState());
                        copiedSlot.setAdministrativeState(slot.getAdministrativeState());
                        copiedSlot.setUsageState(slot.getUsageState());
                        copiedSlot.setHref("https://" + ip + ":" + serverPort + "/getSlotById?id=" + slot.getId());

                        ArrayList<Card> cards = slot.getCards();
                        ArrayList<Card> copiedCards = new ArrayList<>();
                        for (Card card : cards) {
                            Card copiedCard = new Card();
                            copiedCard.setName(card.getName().toLowerCase());
                            copiedCard.setShelfPosition(card.getShelfPosition());
                            copiedCard.setSlotPosition(card.getSlotPosition());
                            copiedCard.setVendor(card.getVendor());
                            copiedCard.setCardModel(card.getCardModel());
                            copiedCard.setCardPartNumber(card.getCardPartNumber());
                            copiedCard.setOperationalState(card.getOperationalState());
                            copiedCard.setAdministrativeState(card.getAdministrativeState());
                            copiedCard.setUsageState(card.getUsageState());
                            copiedCard.setHref("https://" + ip + ":" + serverPort +
                                    "/getCardById?id=" + card.getId());

                            ArrayList<CardSlot> cardSlots = card.getCardSlots();
                            ArrayList<CardSlot> copiedCardSlots = new ArrayList<>();
                            for (CardSlot cardSlot : cardSlots) {
                                CardSlot copiedCardSlot = new CardSlot();
                                copiedCardSlot.setName(cardSlot.getName().toLowerCase());
                                copiedCardSlot.setSlotPosition(cardSlot.getSlotPosition());
                                copiedCardSlot.setOperationalState(cardSlot.getOperationalState());
                                copiedCardSlot.setAdministrativeState(cardSlot.getAdministrativeState());
                                copiedCardSlot.setUsageState(cardSlot.getUsageState());
                                copiedCardSlot.setHref("https://" + ip + ":" + serverPort +
                                        "/getCardSlotById?id=" + cardSlot.getId());

                                Pluggable pluggable = cardSlot.getPluggables();
                                if (pluggable != null) {
                                    Pluggable copiedPluggable = new Pluggable();
                                    copiedPluggable.setName(pluggable.getName().toLowerCase());
                                    copiedPluggable.setPositionOnCard(pluggable.getPositionOnCard());
                                    copiedPluggable.setPositionOnDevice(pluggable.getPositionOnDevice());
                                    copiedPluggable.setVendor(pluggable.getVendor());
                                    copiedPluggable.setPluggableModel(pluggable.getPluggableModel());
                                    copiedPluggable.setPluggablePartNumber(pluggable.getPluggablePartNumber());
                                    copiedPluggable.setOperationalState(pluggable.getOperationalState());
                                    copiedPluggable.setAdministrativeState(pluggable.getAdministrativeState());
                                    copiedPluggable.setUsageState(pluggable.getUsageState());
                                    copiedPluggable.setHref("https://" + ip + ":" + serverPort +
                                            "/getPluggableById?id=" + pluggable.getId());

                                    copiedCardSlot.setPluggables(copiedPluggable);
                                }

                                Port port = cardSlot.getPorts();
                                if (port != null) {
                                    Port copiedPort = new Port();
                                    copiedPort.setName(port.getName().toLowerCase());
                                    copiedPort.setPositionOnCard(port.getPositionOnCard());
                                    copiedPort.setPositionOnDevice(port.getPositionOnDevice());
                                    copiedPort.setPortType(port.getPortType());
                                    copiedPort.setOperationalState(port.getOperationalState());
                                    copiedPort.setAdministrativeState(port.getAdministrativeState());
                                    copiedPort.setUsageState(port.getUsageState());
                                    copiedPort.setHref("https://" + ip + ":" + serverPort +
                                            "/getPortById?id=" + port.getId());

                                    copiedCardSlot.setPorts(copiedPort);
                                }

                                copiedCardSlots.add(copiedCardSlot);
                            }

                            copiedCard.setCardSlots(copiedCardSlots);
                            copiedCards.add(copiedCard);
                        }

                        copiedSlot.setCards(copiedCards);
                        copiedSlots.add(copiedSlot);
                    }

                    copiedShelf.setSlots(copiedSlots);
                    deviceShelves.add(copiedShelf);
                }

                device.setShelves(deviceShelves);
            }
            // Copy resource specification
            String deviceModel = device.getDeviceModel();
            DeviceMetaModel modelDetails = deviceMetaModelRepository.
                    getDeviceMetaModel(deviceModel);
            int modelledShelvesCount = modelDetails.getShelvesContained();
            int createdShelvesCount = deviceShelves.size();

            if (modelledShelvesCount != createdShelvesCount) {
                // create missing shelves
                logger.debug("Create missing shelves");
            }
            deviceRepository.save(device);

            // Copy related party
            if (deviceDetailsTmf.getPlace() == null && deviceDetailsTmf.getRelatedParty() == null) {
                deviceRepository.attachDeviceOnBuilding(device.getName(), "discovered");
            } else {
                String buildingName = deviceDetailsTmf.getPlace().get(0).getName().toLowerCase();
                if (deviceDetailsTmf.getRelatedParty() != null) {
                    String rackName = deviceDetailsTmf.getRelatedParty().getRack().getName().toLowerCase();
                    logger.debug("attaching rack: {} to device: {}", rackName, deviceDetailsTmf.getName());
                    Rack rack = rackRepository.findByName(rackName);
                    if (rack == null) {
                        appExceptionHandler.raiseException("Rack not found: " + rackName);
                    }
                    deviceRepository.attachDeviceOnRack(device.getName(), rackName);
                } else {
                    logger.debug("attaching building: {} to device: {}", buildingName, deviceDetailsTmf.getName());
                    Building building = buildingRepository.findByName(buildingName);
                    if (building == null) {
                        appExceptionHandler.raiseException("Building not found: " + buildingName);
                    }
                    deviceRepository.attachDeviceOnBuilding(device.getName(), buildingName);
                }
            }
            JSONObject response = new JSONObject();
            response.put("Status", "success");
            response.put("Device", device);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception occurred while creating device: {}", e.getMessage());
            appExceptionHandler.raiseException(e.getMessage());
        }
        return null;
    }

}

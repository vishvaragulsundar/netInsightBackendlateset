package com.prodapt.netinsight.objectModelManager;

import com.prodapt.netinsight.connectionManager.LogicalConnection;
import com.prodapt.netinsight.connectionManager.LogicalConnectionRepository;
import com.prodapt.netinsight.connectionManager.PhysicalConnection;
import com.prodapt.netinsight.connectionManager.PhysicalConnectionRepository;
import com.prodapt.netinsight.deviceInstanceManager.*;
import com.prodapt.netinsight.exceptionsHandler.AppExceptionHandler;
import com.prodapt.netinsight.locationManager.Building;
import com.prodapt.netinsight.locationManager.BuildingRepository;
import com.prodapt.netinsight.locationManager.Rack;
import com.prodapt.netinsight.locationManager.RackRepository;
import com.prodapt.netinsight.serviceManager.Service;
import com.prodapt.netinsight.serviceManager.ServiceRepository;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ObjectModellerApis {

    Logger logger = LoggerFactory.getLogger(ObjectModellerApis.class);

    @Autowired
    ObjectModelsRepo objectModelsRepo;

    @Autowired
    AppExceptionHandler appExceptionHandler;

    @PostMapping("/createObjectModel")
    public JSONObject createObjectModel(@RequestBody ObjectModelInfo objectModelInfo) {
        logger.info("Inside createObjectModel");
        logger.debug("Object Model Name: {}", objectModelInfo.getObjectModelName());
        try {
            // Get the username of the logged-in user
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username;
            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }
            logger.debug("Name of user modelling the object: " + username);
            ObjectModelName modelName = ObjectModelName.fromValue(objectModelInfo.getObjectModelName());

            for (PerLabelPojo label : objectModelInfo.getLabels()) {
                if (objectModelsRepo.findByObjectModelNameAndLabelName(modelName, label.getLabelName()) == null) {
                    ObjectModel objectModel = new ObjectModel();
                    objectModel.setObjectModelName(modelName);
                    objectModel.setLabelName(label.getLabelName());
                    objectModel.setRequired(label.getRequired());
                    objectModel.setDataType(label.getDataType());
                    objectModel.setIsAdditionalAttribute("Yes");
                    objectModel.setCreatedDate(new java.util.Date());
                    objectModel.setCreatedBy(username);
                    objectModel.setUpdatedBy(username);
                    objectModelsRepo.save(objectModel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        JSONObject response = new JSONObject();
        response.put("status", "success");
        return response;
    }

    @PutMapping("/updateObjectModel")
    public JSONObject updateObjectModel(@RequestBody ObjectModelInfo objectModelInfo) {
        logger.info("Inside updateObjectModel");
        logger.debug("Object Model Name: " + objectModelInfo.getObjectModelName());
        try {
            // Get the username of the logged-in user
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username;
            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }
            logger.debug("Name of user modelling the object: " + username);
            ObjectModelName modelName = ObjectModelName.fromValue(objectModelInfo.getObjectModelName());

            Boolean changesDone = false;
            for (PerLabelPojo label : objectModelInfo.getLabels()) {
                ObjectModel objectModel = objectModelsRepo.findByObjectModelNameAndLabelName(modelName,
                        label.getLabelName());
                if (objectModel != null) {
                    changesDone = true;
                    objectModel.setRequired(label.getRequired());
                    objectModel.setDataType(label.getDataType());
                    objectModel.setUpdatedDate(new java.util.Date());
                    objectModel.setUpdatedBy(username);
                    objectModelsRepo.save(objectModel);
                }
            }
            if (!changesDone) {
                appExceptionHandler.raiseException("No existing label matches the received labels for object: " +
                        modelName.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        JSONObject response = new JSONObject();
        response.put("status", "success");
        return response;
    }

    @DeleteMapping("/deleteObjectModel")
    public JSONObject deleteObjectModel(@RequestBody ObjectModelInfo objectModelInfo) {
        logger.info("Inside deleteObjectModel");
        logger.debug("Object Model Name: " + objectModelInfo.getObjectModelName());
        try {
            // Get the username of the logged-in user
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username;
            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }
            logger.debug("Name of user modelling the object: " + username);
            ObjectModelName modelName = ObjectModelName.fromValue(objectModelInfo.getObjectModelName());

            Boolean changesDone = false;
            for (PerLabelPojo label : objectModelInfo.getLabels()) {
                ObjectModel objectModel = objectModelsRepo.findByObjectModelNameAndLabelName(modelName, label.getLabelName());
                if (objectModel != null) {
                    changesDone = true;
                    objectModelsRepo.delete(objectModel);
                }
            }
            if (!changesDone) {
                appExceptionHandler.raiseException("No existing label matches the received labels for object: " +
                        modelName.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        JSONObject response = new JSONObject();
        response.put("status", "success");
        return response;
    }

    @GetMapping("/getObjectModel")
    public ObjectModelInfo getObjectModel(@RequestParam String objectModelName) {
        logger.info("Inside getObjectModel");
        logger.debug("Object Model Name: " + objectModelName);
        try {
            ObjectModelName modelName = ObjectModelName.fromValue(objectModelName);
            ObjectModelInfo objectModelInfo = new ObjectModelInfo();
            objectModelInfo.setObjectModelName(objectModelName);
            objectModelInfo.setLabels(new ArrayList<>());
            List<ObjectModel> objectModels = objectModelsRepo.findByObjectModelName(modelName);
            if (objectModels == null || objectModels.size() == 0) {
                appExceptionHandler.raiseException("No labels found for object: " + modelName.getValue());
            }
            for (ObjectModel objectModel : objectModels) {
                PerLabelPojo label = new PerLabelPojo();
                label.setLabelName(objectModel.getLabelName());
                label.setRequired(objectModel.getRequired());
                label.setDataType(objectModel.getDataType());
                objectModelInfo.getLabels().add(label);
            }
            return objectModelInfo;
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return null;
    }

    @Autowired
    BuildingRepository buildingRepository;

    @Autowired
    RackRepository rackRepository;

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    PortRepository portRepository;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    PluggableRepository pluggableRepository;

    @Autowired
    ShelfRepository shelfRepository;

    @Autowired
    LogicalPortRepository logicalPortRepository;

    @Autowired
    LogicalConnectionRepository logicalConnectionRepository;

    @Autowired
    PhysicalConnectionRepository physicalConnectionRepository;

    @Autowired
    ServiceRepository serviceRepository;

    public void addAdditionalAttributes(ArrayList<AdditionalAttribute> additionalAttributes,
                                        Object inventoryObject) {
        if (additionalAttributes.size() != 0) {
            if (inventoryObject instanceof Building) {
                Building building = (Building) inventoryObject;
                building.setAdditionalAttributes(additionalAttributes);
                buildingRepository.save(building);
            } else if (inventoryObject instanceof Rack) {
                Rack rack = (Rack) inventoryObject;
                rack.setAdditionalAttributes(additionalAttributes);
                rackRepository.save(rack);
            } else if (inventoryObject instanceof Device) {
                Device device = (Device) inventoryObject;
                device.setAdditionalAttributes(additionalAttributes);
                deviceRepository.save(device);
            } else if (inventoryObject instanceof Port) {
                Port port = (Port) inventoryObject;
                port.setAdditionalAttributes(additionalAttributes);
                portRepository.save(port);
            } else if (inventoryObject instanceof Card) {
                Card card = (Card) inventoryObject;
                card.setAdditionalAttributes(additionalAttributes);
                cardRepository.save(card);
            } else if (inventoryObject instanceof Pluggable) {
                Pluggable pluggable = (Pluggable) inventoryObject;
                pluggable.setAdditionalAttributes(additionalAttributes);
                pluggableRepository.save(pluggable);
            } else if (inventoryObject instanceof Shelf) {
                Shelf shelf = (Shelf) inventoryObject;
                shelf.setAdditionalAttributes(additionalAttributes);
                shelfRepository.save(shelf);
            } else if (inventoryObject instanceof LogicalPort) {
                LogicalPort logicalPort = (LogicalPort) inventoryObject;
                logicalPort.setAdditionalAttributes(additionalAttributes);
                logicalPortRepository.save(logicalPort);
            } else if (inventoryObject instanceof LogicalConnection) {
                LogicalConnection logicalConnection = (LogicalConnection) inventoryObject;
                logicalConnection.setAdditionalAttributes(additionalAttributes);
                logicalConnectionRepository.save(logicalConnection);
            } else if (inventoryObject instanceof PhysicalConnection) {
                PhysicalConnection physicalConnection = (PhysicalConnection) inventoryObject;
                physicalConnection.setAdditionalAttributes(additionalAttributes);
                physicalConnectionRepository.save(physicalConnection);
            } else if (inventoryObject instanceof Service) {
                Service service = (Service) inventoryObject;
                service.setAdditionalAttributes(additionalAttributes);
                serviceRepository.save(service);
            }
        }
    }
}

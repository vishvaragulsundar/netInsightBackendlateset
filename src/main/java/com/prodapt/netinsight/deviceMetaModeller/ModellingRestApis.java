package com.prodapt.netinsight.deviceMetaModeller;

import com.prodapt.netinsight.exceptionsHandler.AppExceptionHandler;
import com.prodapt.netinsight.uiHelper.*;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * This class is responsible for handling all modelling related APIs
 */
@RestController
public class ModellingRestApis {

    Logger logger = LoggerFactory.getLogger(ModellingRestApis.class);
    @Autowired
    DeviceMetaModelRepository deviceMetaModelRepository;

    @Autowired
    AppExceptionHandler appExceptionHandler;

    @Autowired
    CardModelsRepo cardModelsRepo;

    @Autowired
    DeviceModelsRepo deviceModelsRepo;

    @Autowired
    VendorsRepo vendorsRepo;

    /**
     * This function is responsible for handling creation of device models
     *
     * @param deviceMetaModel device model data
     * @return status of operation
     */
    @PostMapping("/createDeviceMetaModel")
    public DeviceMetaModel createDeviceMetaModel(@RequestBody DeviceMetaModel deviceMetaModel) {
        deviceMetaModel.setDeviceModel(deviceMetaModel.getDeviceModel().toLowerCase());
        logger.info("Inside createDeviceMetaModel for modelling: {}", deviceMetaModel.getDeviceModel());
        DeviceMetaModel existingDeviceModel = null;
        try {
            //Validate device model vendor
            if (deviceMetaModel.getVendor() == null || deviceMetaModel.getVendor().trim().isEmpty()) {
                appExceptionHandler.raiseException("Device meta model vendor cannot be empty");
            }
            existingDeviceModel = deviceMetaModelRepository.
                    getDeviceMetaModel(deviceMetaModel.getDeviceModel());
            if (existingDeviceModel != null) {
                appExceptionHandler.raiseException("Given Device Model already Exist, existingDeviceModel: " +
                        existingDeviceModel.toString());
            }
            existingDeviceModel = deviceMetaModelRepository.createDeviceMetaModel(deviceMetaModel.getDeviceModel(),
                    deviceMetaModel.getPartNumber(), deviceMetaModel.getVendor(), deviceMetaModel.getHeight(),
                    deviceMetaModel.getDepth(), deviceMetaModel.getWidth(), deviceMetaModel.getShelvesContained(), deviceMetaModel.getNumOfRackPositionOccupied(),
                    deviceMetaModel.getAllowedCardList());
            deviceModelsRepo.save(new DeviceModels(deviceMetaModel.getDeviceModel()));
            for (String cardModel : deviceMetaModel.getAllowedCardList()) {
                cardModelsRepo.save(new CardModels(cardModel));
            }
            vendorsRepo.save(new Vendors(deviceMetaModel.getVendor()));
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return existingDeviceModel;
    }

    /**
     * This function is responsible for handling update of device models
     *
     * @param deviceMetaModel device model data
     * @return status of operation
     */
    @PutMapping("/updateDeviceMetaModel")
    public DeviceMetaModel updateDeviceMetaModel(@RequestBody DeviceMetaModel deviceMetaModel) {
        deviceMetaModel.setDeviceModel(deviceMetaModel.getDeviceModel().toLowerCase());
        DeviceMetaModel existingDeviceModel = null;
        try {
            logger.info("Inside updateDeviceMetaModel for modelling: {}", deviceMetaModel.getDeviceModel());
            existingDeviceModel = deviceMetaModelRepository.
                    getDeviceMetaModel(deviceMetaModel.getDeviceModel());
            if (existingDeviceModel == null) {
                appExceptionHandler.raiseException("Given Device Model doesn't Exist");
            }
            existingDeviceModel = deviceMetaModelRepository.updateDeviceMetaModel(deviceMetaModel.getDeviceModel(),
                    deviceMetaModel.getPartNumber(), deviceMetaModel.getVendor(), deviceMetaModel.getHeight(),
                    deviceMetaModel.getDepth(), deviceMetaModel.getWidth(), deviceMetaModel.getShelvesContained(),
                    deviceMetaModel.getAllowedCardList(), deviceMetaModel.getNumOfRackPositionOccupied());
            for (String cardModel : deviceMetaModel.getAllowedCardList()) {
                logger.debug("Adding card model: {} to UI helper db", cardModel);
                cardModelsRepo.save(new CardModels(cardModel));
            }
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return existingDeviceModel;
    }

    /**
     * This function is responsible for getting device model information
     *
     * @return device model data
     */
    @GetMapping("/getDeviceMetaModel")
    public DeviceMetaModel getDeviceMetaModel(@RequestParam(name = "deviceModel") String name) {
        String deviceModel = name.toLowerCase();
        DeviceMetaModel existingDeviceModel = null;
        try {
            logger.info("Inside getDeviceMetaModel for model: {}", deviceModel);
            existingDeviceModel = deviceMetaModelRepository.getDeviceMetaModel(deviceModel);
            if (existingDeviceModel == null) {
                appExceptionHandler.raiseException("Given Device Model doesn't Exist");
            }
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return existingDeviceModel;
    }

    @GetMapping("/getModelById")
    public DeviceMetaModel getModelById(@RequestParam(name = "id") Long id) {
        DeviceMetaModel Id = null;
        try {
            logger.info("Inside getModelById for id: {}", id);
            Id = deviceMetaModelRepository.findDeviceMetaModelById(id);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return Id;
    }

    /**
     * This function is responsible for handling deletion of device models
     *
     * @param name device model name
     * @return status of operation
     */
    @DeleteMapping("/deleteDeviceMetaModel")
    public JSONObject deleteDeviceMetaModel(@RequestParam(name = "deviceModel") String name) {
        String deviceModel = name.toLowerCase();
        JSONObject response = new JSONObject();
        try {
            logger.info("Inside deleteDeviceMetaModel for model: {}", deviceModel);
            DeviceMetaModel existingDeviceModel = deviceMetaModelRepository.getDeviceMetaModel(deviceModel);
            if (existingDeviceModel == null) {
                appExceptionHandler.raiseException("Given Device Model doesn't Exist");
            }
            deviceMetaModelRepository.deleteDeviceMetaModel(deviceModel);
            //
            response.put("status", "Success");
            response.put("DeletedDeviceModel", deviceModel);
//            return response;
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * Below function is to get all device models of a vendor
     *
     * @param vendor device manufacturer
     * @return list of device models
     */
    @GetMapping("/getAllModelsOfVendor")
    public ArrayList<DeviceMetaModel> getAllModelsOfVendor(@RequestParam(name = "vendor") String vendor) {
        ArrayList<DeviceMetaModel> allModels = new ArrayList<>();
        try {
            logger.info("Inside getAllModelsOfVendor for vendor: {}", vendor);
            allModels = (ArrayList<DeviceMetaModel>) deviceMetaModelRepository.getByVendorName(vendor);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return allModels;
    }

    /**
     * Below function is to get all device models
     *
     * @return list of device models
     */
    @GetMapping("/getAllModels")
    public ArrayList<DeviceMetaModel> getAllModels() {
        ArrayList<DeviceMetaModel> allModels = new ArrayList<>();
        try {
            logger.info("Inside getAllModels");
            allModels = (ArrayList<DeviceMetaModel>) deviceMetaModelRepository.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return allModels;
    }

    /**
     * This function is responsible for handling update of device models using metamodel name
     *
     * @param metaModelName   device model name
     * @param deviceMetaModel device model data
     * @return status of operation
     */
    @PutMapping("/updateDeviceMetaModelByName")
    public DeviceMetaModel updateDeviceMetaModelByName(@RequestParam(name = "name") String metaModelName,
                                                       @RequestBody DeviceMetaModel deviceMetaModel) {
        try {
            logger.info("Inside updateDeviceMetaModelByName for modelling: {}", deviceMetaModel.getDeviceModel());
            //validate device metamodel name
            if (deviceMetaModel.getDeviceModel() == null) {
                appExceptionHandler.raiseException("Device model name is mandatory");
            }
            if (!metaModelName.equalsIgnoreCase(deviceMetaModel.getDeviceModel())) {
                appExceptionHandler.raiseException("Request parameter does not match with the request body");
            }
            metaModelName = metaModelName.toLowerCase();

            DeviceMetaModel existingDeviceModel = deviceMetaModelRepository.getDeviceMetaModel(metaModelName);
            if (existingDeviceModel == null) {
                appExceptionHandler.raiseException("Given device model doesn't exist");
            }
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return deviceMetaModelRepository.updateDeviceMetaModel(metaModelName,
                deviceMetaModel.getPartNumber(), deviceMetaModel.getVendor(), deviceMetaModel.getHeight(),
                deviceMetaModel.getDepth(), deviceMetaModel.getWidth(), deviceMetaModel.getShelvesContained(),
                deviceMetaModel.getAllowedCardList(), deviceMetaModel.getNumOfRackPositionOccupied());
    }
}

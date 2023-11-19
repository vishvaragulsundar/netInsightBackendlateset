package com.prodapt.netinsight.uiHelper;

import com.prodapt.netinsight.deviceMetaModeller.DeviceMetaModelRepository;
import com.prodapt.netinsight.exceptionsHandler.AppExceptionHandler;
import com.prodapt.netinsight.serviceManager.Service;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UiHelperRestApis {

    Logger logger = LoggerFactory.getLogger(UiHelperRestApis.class);

    @Autowired
    CardModelsRepo cardModelsRepo;

    @Autowired
    DeviceModelsRepo deviceModelsRepo;
    @Autowired
    ServiceTypeRepo serviceTypeRepo;

    @Autowired
    DeviceMetaModelRepository deviceMetaModelRepository;
    @Autowired
    Mgmt_StateRepo mgmtStateRepo;
    @Autowired
    AppExceptionHandler appExceptionHandler;

    /*
     * This function is responsible for getting all the card models
     */
    @GetMapping("/getCardModels")
    public List<String> getCardModels() {
        List<CardModels> cardModels = cardModelsRepo.findAll();
        List<String> cardModelList = new ArrayList<>();
        for (CardModels cardModel : cardModels) {
            cardModelList.add(cardModel.getCardTypes());
        }
        return cardModelList;
    }

    @Autowired
    VendorsRepo vendorsRepo;

    /*
     * This function is responsible for getting all the vendors
     */
    @GetMapping("/getVendors")
    public List<String> getVendors() {
        List<Vendors> vendors = vendorsRepo.findAll();
        List<String> vendorList = new ArrayList<>();
        for (Vendors vendor : vendors) {
            vendorList.add(vendor.getVendorName());
        }
        return vendorList;
    }

    /*
     * This function is responsible for getting all the device models
     */
    @GetMapping("/getDeviceModels")
    public List<String> getDeviceModels() {
        List<DeviceModels> deviceModels = deviceModelsRepo.findAll();
        List<String> deviceModelList = new ArrayList<>();
        for (DeviceModels deviceModel : deviceModels) {
            deviceModelList.add(deviceModel.getDeviceTypes());
        }
        return deviceModelList;
    }

    @Autowired
    AsyncService asyncService;

    /*
     * This function is responsible for syncing the UI helper tables
     */
    @PutMapping("/syncUiHelper")
    public ResponseEntity<Object> syncUiHelper() {
        logger.info("Syncing UI helper tables");
        asyncService.syncUiHelper();
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/getAllService")
    public List<String> getAllService() {
        List<ServiceType> serviceTypes = serviceTypeRepo.findAll();
        List<String> serviceTypeList = new ArrayList<>();
        for (ServiceType Type : serviceTypes) {
            serviceTypeList.add(Type.getName());
        }
        return serviceTypeList;
    }

    @PostMapping("/createMgmtState")
    public JSONObject createMgmtState(@RequestBody Mgmt_State.MgmtStateId mgmtStateId) {
        JSONObject response = new JSONObject();
        try {
            String Name = mgmtStateId.getName().trim().toLowerCase();
            String Type = mgmtStateId.getType().trim().toLowerCase();
            logger.info("Inside createMgmtState : {}", Name, Type);
            Mgmt_State State = mgmtStateRepo.save(new Mgmt_State(Name, Type));
            response.put("State", State);
            response.put("status", "success");
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }
    @GetMapping("/getAllAdministrativeState")
    public List<Mgmt_State> getAllAdministrativeState() {
        List<Mgmt_State> mgmtStates = null;
        try {
            mgmtStates = mgmtStateRepo.findAllByAdministrativeState();
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return mgmtStates;
    }
    @GetMapping("/getAllUsageState")
    public List<Mgmt_State> getAllUsageState() {
        List<Mgmt_State> mgmtStates = null;
        try {
            mgmtStates = mgmtStateRepo.findAllByUsageState();
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return mgmtStates;
    }
    @GetMapping("/getAllOperationalState")
    public List<Mgmt_State> getAllOperationalState() {
        List<Mgmt_State> mgmtStates = null;
        try {
            mgmtStates = mgmtStateRepo.findAllByOperationalState();
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return mgmtStates;
    }
}

package com.prodapt.netinsight.uiHelper;

import com.prodapt.netinsight.deviceInstanceManager.*;
import com.prodapt.netinsight.exceptionsHandler.AppExceptionHandler;
import com.prodapt.netinsight.exceptionsHandler.ExceptionEntity;
import com.prodapt.netinsight.exceptionsHandler.ExceptionRepo;
import com.prodapt.netinsight.locationManager.*;
import com.prodapt.netinsight.deviceMetaModeller.DeviceMetaModel;
import com.prodapt.netinsight.deviceMetaModeller.DeviceMetaModelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AsyncService {

    Logger logger = LoggerFactory.getLogger(AsyncService.class);

    @Autowired
    private DeviceMetaModelRepository deviceMetaModelRepository;

    @Autowired
    private DeviceModelsRepo deviceModelsRepo;

    @Autowired
    private CardModelsRepo cardModelsRepo;

    @Autowired
    private VendorsRepo vendorsRepo;

    @Autowired
    ExceptionRepo exceptionRepo;

    @Async("taskExecutor")
    public void syncUiHelper() {
        logger.debug("Inside syncUiHelper for loading card and device models into UI helper tables");
        List<DeviceMetaModel> deviceMetaModels = deviceMetaModelRepository.findAll();
        for (DeviceMetaModel deviceMetaModel : deviceMetaModels) {
            //Validate device model vendor
            if(deviceMetaModel.getVendor() == null || deviceMetaModel.getVendor().trim().isEmpty()) {
                logger.error("Device meta model vendor for " + deviceMetaModel.getDeviceModel() + " is empty");
                ExceptionEntity exceptionEntity = new ExceptionEntity("syncUiHelper",
                        "Device meta model vendor for " + deviceMetaModel.getDeviceModel() + " is empty");
                exceptionRepo.save(exceptionEntity);
                continue;
            }
            deviceModelsRepo.save(new DeviceModels(deviceMetaModel.getDeviceModel()));
            vendorsRepo.save(new Vendors(deviceMetaModel.getVendor()));
            for (String cardModel : deviceMetaModel.getAllowedCardList()) {
                cardModelsRepo.save(new CardModels(cardModel));
            }
        }
        logger.debug("Completed syncUiHelper for loading card and device models into UI helper tables");
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
    DeviceRepository deviceRepository;

    @Autowired
    PortRepository portRepository;

    @Autowired
    LogicalPortRepository logicalPortRepository;

    @Autowired
    SlotRepository slotRepository;

    @Autowired
    PluggableRepository pluggableRepository;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    BuildingRepository buildingRepository;

    @Autowired
    RackRepository rackRepository;

    @Autowired
    AppExceptionHandler appExceptionHandler;

    @Async("taskExecutor")
    public void syncDashboard() {
        logger.debug("Inside syncDashboard for loading object counts into dashboard");
        try {
            Long cardCount = cardRepository.count();
            objectCountRepo.save(new ObjectCount("card", cardCount));
            Long deviceCount = deviceRepository.count();
            objectCountRepo.save(new ObjectCount("device", deviceCount));
            Long buildingCount = buildingRepository.count();
            objectCountRepo.save(new ObjectCount("building", buildingCount));
            Long citiesCount = cityRepository.count();
            objectCountRepo.save(new ObjectCount("city", citiesCount));
            Long countryCount = countryRepository.count();
            objectCountRepo.save(new ObjectCount("country", countryCount));
            Long stateCount = stateRepository.count();
            objectCountRepo.save(new ObjectCount("state", stateCount));
            Long pluggableCount = pluggableRepository.count();
            objectCountRepo.save(new ObjectCount("pluggable", pluggableCount));
            Long portCount = portRepository.count();
            objectCountRepo.save(new ObjectCount("port", portCount));
            Long logicalPortCount = logicalPortRepository.count();
            objectCountRepo.save(new ObjectCount("logicalPort", logicalPortCount));
            Long deviceMetaModelCount = deviceMetaModelRepository.count();
            objectCountRepo.save(new ObjectCount("deviceMetaModel", deviceMetaModelCount));
            Long rackCount = rackRepository.count();
            objectCountRepo.save(new ObjectCount("rack", rackCount));
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return;
    }
}

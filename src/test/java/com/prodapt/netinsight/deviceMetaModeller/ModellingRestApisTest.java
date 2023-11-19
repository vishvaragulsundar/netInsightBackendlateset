package com.prodapt.netinsight.deviceMetaModeller;

import com.prodapt.netinsight.exceptionsHandler.ExceptionDetails;
import com.prodapt.netinsight.exceptionsHandler.ServiceException;
import com.prodapt.netinsight.uiHelper.CardModelsRepo;
import com.prodapt.netinsight.uiHelper.DeviceModelsRepo;
import com.prodapt.netinsight.uiHelper.VendorsRepo;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@ActiveProfiles("UNIT")
class ModellingRestApisTest {

    @MockBean
    DeviceMetaModelRepository deviceMetaModelRepository;

    @Autowired
    ModellingRestApis modellingRestApis;

    @MockBean
    CardModelsRepo cardModelsRepo;

    @MockBean
    DeviceModelsRepo deviceModelsRepo;

    @MockBean
    VendorsRepo vendorsRepo;

    /**
     * Test to check if the device meta model is created successfully
     */
    @Test
    void createDeviceMetaModelTestSuccess() {

        DeviceMetaModel deviceMetaModel = new DeviceMetaModel();
        deviceMetaModel.setDeviceModel("test");
        deviceMetaModel.setPartNumber("Test");
        deviceMetaModel.setVendor("Test");
        deviceMetaModel.setHeight(1);
        deviceMetaModel.setDepth(1);
        deviceMetaModel.setWidth(1);
        deviceMetaModel.setShelvesContained(1);
        ArrayList<String> allowedCardList = new ArrayList<>();
        allowedCardList.add("Test");
        deviceMetaModel.setAllowedCardList(allowedCardList);

        Mockito.when(deviceMetaModelRepository.
                getDeviceMetaModel(deviceMetaModel.getDeviceModel())).thenReturn(null);
        Mockito.when(deviceMetaModelRepository.createDeviceMetaModel(deviceMetaModel.getDeviceModel(),
                deviceMetaModel.getPartNumber(), deviceMetaModel.getVendor(), deviceMetaModel.getHeight(),
                deviceMetaModel.getDepth(), deviceMetaModel.getWidth(), deviceMetaModel.getShelvesContained(),
                deviceMetaModel.getNumOfRackPositionOccupied(),
                deviceMetaModel.getAllowedCardList())).thenReturn(deviceMetaModel);
        assertEquals(deviceMetaModel, modellingRestApis.createDeviceMetaModel(deviceMetaModel));
    }

    /**
     * Test to check if correct exception is thrown when device meta model already exists
     */
    @Test
    void createDeviceMetaModelTestFailModelExist() {

        DeviceMetaModel deviceMetaModel = new DeviceMetaModel();
        deviceMetaModel.setDeviceModel("test");
        deviceMetaModel.setPartNumber("Test");
        deviceMetaModel.setVendor(" ");
        deviceMetaModel.setHeight(1);
        deviceMetaModel.setDepth(1);
        deviceMetaModel.setWidth(1);
        deviceMetaModel.setShelvesContained(1);
        ArrayList<String> allowedCardList = new ArrayList<>();
        allowedCardList.add("Test");
        deviceMetaModel.setAllowedCardList(allowedCardList);

        Exception exception = Assertions.assertThrows(ServiceException.class, () -> {
            modellingRestApis.createDeviceMetaModel(deviceMetaModel);
        });
        assertTrue(exception.getMessage().equals("Device meta model vendor cannot be empty"));

        deviceMetaModel.setVendor("Test");

        Mockito.when(deviceMetaModelRepository.
                getDeviceMetaModel(deviceMetaModel.getDeviceModel())).thenReturn(deviceMetaModel);
        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            modellingRestApis.createDeviceMetaModel(deviceMetaModel);
        });
        assertTrue(e.getMessage().contains("Given Device Model already Exist, existingDeviceModel:"));
    }

    /**
     * Test case for updating a device meta model.
     */
    @Test
    void updateDeviceMetaModelTestSuccess() {

        DeviceMetaModel deviceMetaModel = new DeviceMetaModel();
        deviceMetaModel.setDeviceModel("test");
        ;
        deviceMetaModel.setPartNumber("Test");
        deviceMetaModel.setVendor("Test");
        deviceMetaModel.setHeight(1);
        deviceMetaModel.setWidth(1);
        deviceMetaModel.setShelvesContained(1);
        ArrayList<String> allowedCardList = new ArrayList<>();
        allowedCardList.add("Test");
        deviceMetaModel.setAllowedCardList(allowedCardList);

        Mockito.when(deviceMetaModelRepository.
                getDeviceMetaModel(deviceMetaModel.getDeviceModel())).thenReturn(deviceMetaModel);
        Mockito.when(deviceMetaModelRepository.updateDeviceMetaModel(deviceMetaModel.getDeviceModel(),
                deviceMetaModel.getPartNumber(), deviceMetaModel.getVendor(), deviceMetaModel.getHeight(),
                deviceMetaModel.getDepth(), deviceMetaModel.getWidth(), deviceMetaModel.getShelvesContained(),
                deviceMetaModel.getAllowedCardList(), deviceMetaModel.getNumOfRackPositionOccupied())).thenReturn(deviceMetaModel);

        assertEquals(deviceMetaModel, modellingRestApis.updateDeviceMetaModel(deviceMetaModel));
    }

    /**
     * Test case for updating a device meta model when the model does not exist.
     */
    @Test
    void updateDeviceMetaModelTestFailModelExist() {
        DeviceMetaModel deviceMetaModel = new DeviceMetaModel();
        deviceMetaModel.setDeviceModel("test");
        ;
        deviceMetaModel.setPartNumber("Test");
        deviceMetaModel.setVendor("Test");
        deviceMetaModel.setHeight(1);
        deviceMetaModel.setWidth(1);
        deviceMetaModel.setShelvesContained(1);
        ArrayList<String> allowedCardList = new ArrayList<>();
        allowedCardList.add("Test");
        deviceMetaModel.setAllowedCardList(allowedCardList);

        Mockito.when(deviceMetaModelRepository.
                getDeviceMetaModel(deviceMetaModel.getDeviceModel())).thenReturn(null);
        Mockito.when(deviceMetaModelRepository.updateDeviceMetaModel(deviceMetaModel.getDeviceModel(),
                deviceMetaModel.getPartNumber(), deviceMetaModel.getVendor(), deviceMetaModel.getHeight(),
                deviceMetaModel.getDepth(), deviceMetaModel.getWidth(), deviceMetaModel.getShelvesContained(),
                deviceMetaModel.getAllowedCardList(), deviceMetaModel.getNumOfRackPositionOccupied())).thenReturn(deviceMetaModel);
        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            modellingRestApis.updateDeviceMetaModel(deviceMetaModel);
        });
        assertTrue(e.getMessage().contains("Given Device Model doesn't Exist"));

    }

    /**
     * Test case for retrieving a device meta model.
     */
    @Test
    void getDeviceMetaModelTestSuccess() {
        DeviceMetaModel deviceMetaModel = new DeviceMetaModel();
        deviceMetaModel.setDeviceModel("test");
        ;
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(deviceMetaModel.getDeviceModel())).thenReturn(deviceMetaModel);
        assertEquals(deviceMetaModel, modellingRestApis.getDeviceMetaModel(deviceMetaModel.getDeviceModel()));
    }

    /**
     * Test case for retrieving a device meta model when the model does not exist.
     */
    @Test
    void getDeviceMetaModelTestFailModelExist() {
        DeviceMetaModel deviceMetaModel = new DeviceMetaModel();
        deviceMetaModel.setDeviceModel("test");
        ;
        Mockito.when(deviceMetaModelRepository.
                getDeviceMetaModel(deviceMetaModel.getDeviceModel())).thenReturn(null);
        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            modellingRestApis.getDeviceMetaModel("test");
        });
        assertTrue(e.getMessage().contains("Given Device Model doesn't Exist"));
    }

    /**
     * Test case for retrieving a device meta model by ID successfully.
     */
    @Test
    void getModelByIdTestSuccess() {
        DeviceMetaModel deviceMetaModel = new DeviceMetaModel();
        deviceMetaModel.setId(1l);
        Mockito.when(deviceMetaModelRepository.findDeviceMetaModelById(deviceMetaModel.getId())).thenReturn(deviceMetaModel);
        assertEquals(deviceMetaModel, modellingRestApis.getModelById(deviceMetaModel.getId()));
    }

    /**
     * Test case for retrieving a device meta model by ID when the retrieval fails.
     */
    @Test
    void getModelByIdTestFailModel() {
        DeviceMetaModel deviceMetaModel = new DeviceMetaModel();
        doThrow(new ServiceException("test error", new ExceptionDetails("1", "error test")))
                .when(deviceMetaModelRepository).findDeviceMetaModelById(123213l);

        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            modellingRestApis.getModelById(123213l);
        });
        assertTrue(e.getMessage().contains("test error"));
    }

    /**
     * Test case for retrieving all device meta models of a specific vendor successfully.
     */
    @Test
    void getAllModelsOfVendorTestSuccess() {
        ArrayList<DeviceMetaModel> deviceMetaModels = new ArrayList<>();
        deviceMetaModels.add(new DeviceMetaModel());

        Mockito.when(deviceMetaModelRepository.getByVendorName("Vendor Test")).thenReturn(deviceMetaModels);

        ArrayList<DeviceMetaModel> actualModels = modellingRestApis.getAllModelsOfVendor("Vendor Test");

        assertEquals(deviceMetaModels, actualModels);
    }

    /**
     * Test case for retrieving all device meta models of a specific vendor when the retrieval fails.
     */
    @Test
    void getAllModelsOfVendorTestFailModel() {
        ArrayList<DeviceMetaModel> deviceMetaModels = new ArrayList<>();
        deviceMetaModels.add(new DeviceMetaModel());
        doThrow(new ServiceException("test error", new ExceptionDetails("1", "error test"))).when(deviceMetaModelRepository).getByVendorName("Vendor Test");

        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            modellingRestApis.getAllModelsOfVendor("Vendor Test");
        });
        assertTrue(e.getMessage().contains("test error"));
    }

    /**
     * Test case for retrieving all device meta models successfully.
     */
    @Test
    public void getAllModelsTestSuccess() {
        ArrayList<DeviceMetaModel> deviceMetaModels = new ArrayList<>();
        deviceMetaModels.add(new DeviceMetaModel());

        Mockito.when(deviceMetaModelRepository.findAll()).thenReturn(deviceMetaModels);

        ArrayList<DeviceMetaModel> actualModels = modellingRestApis.getAllModels();

        assertEquals(deviceMetaModels, actualModels);

    }

    /**
     * Test case for retrieving all device meta models when the retrieval fails.
     */
    @Test
    public void getAllModelsTestFailModel() {
        doThrow(new ServiceException("test error", new ExceptionDetails("1", "error test"))).when(deviceMetaModelRepository).findAll();

        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            modellingRestApis.getAllModels();
        });
        assertTrue(e.getMessage().contains("test error"));
    }

    /**
     * Test case for deleting a device meta model successfully.
     */
    @Test
    void deleteDeviceMetaModelTestSuccess() {
        DeviceMetaModel deviceMetaModel = new DeviceMetaModel();
        deviceMetaModel.setDeviceModel("test");
        JSONObject response = new JSONObject();
        response.put("status", "Success");
        response.put("DeletedDeviceModel", deviceMetaModel.getDeviceModel());
        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(deviceMetaModel.getDeviceModel())).thenReturn(new DeviceMetaModel());
        JSONObject receivedData = modellingRestApis.deleteDeviceMetaModel(deviceMetaModel.getDeviceModel());
        assertEquals("Success", receivedData.get("status"));
    }

    /**
     * Test case for deleting a device meta model when the model does not exist.
     */
    @Test
    void deleteDeviceMetaModelFailModelExist() {
        // Create a new DeviceMetaModel object
        DeviceMetaModel deviceMetaModel = new DeviceMetaModel();
        deviceMetaModel.setDeviceModel("test");

        // Set up the mock behavior for the repository
        Mockito.when(deviceMetaModelRepository.deleteDeviceMetaModel(deviceMetaModel.getDeviceModel()))
                .thenReturn(null);

        // Perform the deletion operation and assert the exception
        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            modellingRestApis.deleteDeviceMetaModel(deviceMetaModel.getDeviceModel());
        });
        assertTrue(e.getMessage().contains("Given Device Model doesn't Exist"));
    }

    @Test
    void updateMetaModelByNameFieldNotFoundTest() {
        DeviceMetaModel metaModel = new DeviceMetaModel();
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            modellingRestApis.updateDeviceMetaModelByName("metaModelName", metaModel);
        });
        Assertions.assertEquals("Device model name is mandatory", e.getMessage());
    }

    @Test
    void updateMetaModelByNameParamsMismatchTest() {
        DeviceMetaModel metaModel = new DeviceMetaModel();
        metaModel.setDeviceModel("metaModelNameTest");
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            modellingRestApis.updateDeviceMetaModelByName("metaModelName", metaModel);
        });
        Assertions.assertEquals("Request parameter does not match with the request body", e.getMessage());
    }

    @Test
    void updateMetaModelByNameDoNotExistTest() {
        DeviceMetaModel metaModel = new DeviceMetaModel();
        metaModel.setDeviceModel("metaModelName");

        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(metaModel.getDeviceModel().toLowerCase())).thenReturn(null);
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            modellingRestApis.updateDeviceMetaModelByName("metaModelName", metaModel);
        });
        Assertions.assertEquals("Given device model doesn't exist", e.getMessage());
    }

    @Test
    void updateMetaModelByNameSuccessTest() {
        DeviceMetaModel metaModel = new DeviceMetaModel();
        metaModel.setDeviceModel("metamodelname");
        metaModel.setPartNumber("partNumber");
        metaModel.setVendor("vendor");
        metaModel.setHeight(1);
        metaModel.setDepth(1);
        metaModel.setWidth(1);
        metaModel.setShelvesContained(1);
        metaModel.setAllowedCardList(new ArrayList<>());

        Mockito.when(deviceMetaModelRepository.getDeviceMetaModel(metaModel.getDeviceModel().toLowerCase())).thenReturn(metaModel);
        Mockito.when(deviceMetaModelRepository.updateDeviceMetaModel(metaModel.getDeviceModel().toLowerCase(),
                metaModel.getPartNumber(), metaModel.getVendor(), metaModel.getHeight(),
                metaModel.getDepth(), metaModel.getWidth(), metaModel.getShelvesContained(),
                metaModel.getAllowedCardList(), metaModel.getNumOfRackPositionOccupied())).thenReturn(metaModel);
        DeviceMetaModel deviceMetaModel = modellingRestApis.updateDeviceMetaModelByName("metamodelname", metaModel);

        Assertions.assertEquals("metamodelname", deviceMetaModel.getDeviceModel());
        Assertions.assertEquals("partNumber", deviceMetaModel.getPartNumber());
        Assertions.assertEquals("vendor", deviceMetaModel.getVendor());
        Assertions.assertEquals(1, deviceMetaModel.getHeight());
        Assertions.assertEquals(1, deviceMetaModel.getDepth());
        Assertions.assertEquals(1, deviceMetaModel.getWidth());
        Assertions.assertEquals(1, deviceMetaModel.getShelvesContained());
        Assertions.assertEquals(0, deviceMetaModel.getAllowedCardList().size());
    }
}
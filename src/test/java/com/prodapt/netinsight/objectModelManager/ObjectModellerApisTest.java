package com.prodapt.netinsight.objectModelManager;

import com.prodapt.netinsight.exceptionsHandler.ServiceException;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ActiveProfiles("UNIT")
class ObjectModellerApisTest {

    @Autowired
    ObjectModellerApis objectModellerApis;

    @MockBean
    ObjectModelsRepo objectModelsRepo;

    @Mock
    Authentication authentication;

    @BeforeEach
    void setUp() {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn("testUser");
    }

    @Test
    void createObjectModelWithInvalidObjectModel() {
        ObjectModelInfo objectModelInfo = new ObjectModelInfo();
        objectModelInfo.setObjectModelName("test");
        ArrayList<PerLabelPojo> labels = new ArrayList<>();
        PerLabelPojo perLabelPojo = new PerLabelPojo();
        perLabelPojo.setLabelName("test");
        perLabelPojo.setDataType("String");
        perLabelPojo.setRequired("true");
        labels.add(perLabelPojo);
        objectModelInfo.setLabels(labels);
        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            objectModellerApis.createObjectModel(objectModelInfo);
        });
        Assert.assertEquals("Invalid ObjectModelName value: test", e.getMessage());
    }

    @Test
    void createObjectModelWithValidObjectModel() {
        ObjectModelInfo objectModelInfo = new ObjectModelInfo();
        objectModelInfo.setObjectModelName("Card");
        ArrayList<PerLabelPojo> labels = new ArrayList<>();
        PerLabelPojo perLabelPojo = new PerLabelPojo();
        perLabelPojo.setLabelName("test");
        perLabelPojo.setDataType("String");
        perLabelPojo.setRequired("true");
        labels.add(perLabelPojo);
        objectModelInfo.setLabels(labels);
        JSONObject response = objectModellerApis.createObjectModel(objectModelInfo);
        Assert.assertEquals("success", response.get("status"));
    }

    @Test
    void updateObjectModelSuccess() {
        ObjectModelInfo objectModelInfo = new ObjectModelInfo();
        objectModelInfo.setObjectModelName("Card");
        ArrayList<PerLabelPojo> labels = new ArrayList<>();
        PerLabelPojo perLabelPojo = new PerLabelPojo();
        perLabelPojo.setLabelName("test");
        perLabelPojo.setDataType("String");
        perLabelPojo.setRequired("true");
        labels.add(perLabelPojo);
        objectModelInfo.setLabels(labels);
        Mockito.when(objectModelsRepo.findByObjectModelNameAndLabelName(ObjectModelName.fromValue(objectModelInfo.
                        getObjectModelName()),
                "test")).thenReturn(new ObjectModel());
        JSONObject response = objectModellerApis.updateObjectModel(objectModelInfo);
        Assert.assertEquals("success", response.get("status"));
    }

    @Test
    void updateObjectModelFailedWithNoExistingLabel() {
        ObjectModelInfo objectModelInfo = new ObjectModelInfo();
        objectModelInfo.setObjectModelName("Card");
        ArrayList<PerLabelPojo> labels = new ArrayList<>();
        PerLabelPojo perLabelPojo = new PerLabelPojo();
        perLabelPojo.setLabelName("test");
        perLabelPojo.setDataType("String");
        perLabelPojo.setRequired("true");
        labels.add(perLabelPojo);
        objectModelInfo.setLabels(labels);
        Mockito.when(objectModelsRepo.findByObjectModelNameAndLabelName(ObjectModelName.fromValue(objectModelInfo.
                getObjectModelName()), "test")).thenReturn(null);

        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            objectModellerApis.updateObjectModel(objectModelInfo);
        });
        Assert.assertEquals("No existing label matches the received labels for object: Card", e.getMessage());
    }

    @Test
    void deleteObjectModelFailed() {
        ObjectModelInfo objectModelInfo = new ObjectModelInfo();
        objectModelInfo.setObjectModelName("Card");
        ArrayList<PerLabelPojo> labels = new ArrayList<>();
        PerLabelPojo perLabelPojo = new PerLabelPojo();
        perLabelPojo.setLabelName("test");
        perLabelPojo.setDataType("String");
        perLabelPojo.setRequired("true");
        labels.add(perLabelPojo);
        objectModelInfo.setLabels(labels);
        Mockito.when(objectModelsRepo.findByObjectModelNameAndLabelName(ObjectModelName.fromValue(objectModelInfo.
                getObjectModelName()), "test")).thenReturn(null);

        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            objectModellerApis.deleteObjectModel(objectModelInfo);
        });
        Assert.assertEquals("No existing label matches the received labels for object: Card", e.getMessage());
    }

    @Test
    void deleteObjectModelSuccess() {
        ObjectModelInfo objectModelInfo = new ObjectModelInfo();
        objectModelInfo.setObjectModelName("Card");
        ArrayList<PerLabelPojo> labels = new ArrayList<>();
        PerLabelPojo perLabelPojo = new PerLabelPojo();
        perLabelPojo.setLabelName("test");
        perLabelPojo.setDataType("String");
        perLabelPojo.setRequired("true");
        labels.add(perLabelPojo);
        objectModelInfo.setLabels(labels);
        Mockito.when(objectModelsRepo.findByObjectModelNameAndLabelName(ObjectModelName.fromValue(objectModelInfo.
                getObjectModelName()), "test")).thenReturn(new ObjectModel());

        JSONObject response = objectModellerApis.deleteObjectModel(objectModelInfo);
        Assert.assertEquals("success", response.get("status"));
    }


    @Test
    void getObjectModelSuccess() {
        List<ObjectModel> labels = new ArrayList<>();
        labels.add(new ObjectModel());
        Mockito.when(objectModelsRepo.findByObjectModelName(ObjectModelName.fromValue("Card"))).
                thenReturn(labels);
        ObjectModelInfo response = objectModellerApis.getObjectModel("Card");
        Assert.assertEquals("Card", response.getObjectModelName());
    }

    @Test
    void getObjectModelFailed() {
        Mockito.when(objectModelsRepo.findByObjectModelName(ObjectModelName.fromValue("Card"))).
                thenReturn(null);
        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
        objectModellerApis.getObjectModel("Card");
        });
        Assert.assertEquals("No labels found for object: Card", e.getMessage());
    }
}
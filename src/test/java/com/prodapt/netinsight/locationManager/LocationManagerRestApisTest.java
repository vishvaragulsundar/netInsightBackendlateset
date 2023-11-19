package com.prodapt.netinsight.locationManager;

import com.prodapt.netinsight.deviceInstanceManager.Device;
import com.prodapt.netinsight.deviceInstanceManager.DeviceRepository;
import com.prodapt.netinsight.exceptionsHandler.ServiceException;
import com.prodapt.netinsight.objectModelManager.AdditionalAttributeRepo;
import com.prodapt.netinsight.objectModelManager.ObjectModellerApis;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("UNIT")
public class LocationManagerRestApisTest {
    @MockBean
    BuildingRepository buildingRepository;
    @MockBean
    CityRepository cityRepository;
    @MockBean
    CountryRepository countryRepository;
    @MockBean
    RackRepository rackRepository;
    @MockBean
    StateRepository stateRepository;
    @Autowired
    LocationManagerRestApis locationManagerRestApis;

    @MockBean
    DeviceRepository deviceRepository;

    @MockBean
    AdditionalAttributeRepo additionalAttributeRepo;

    @MockBean
    ObjectModellerApis objectModellerApis;


    @Test
    void getBuildingTestSuccess() {
        // Arrange
        String buildingname = "test";
        Building building = new Building();
        building.setName(buildingname);

        Mockito.when(buildingRepository.findByName(buildingname)).thenReturn(building);

        // Act
        Building actualBuilding = locationManagerRestApis.getBuilding(buildingname);

        // Assert
        assertEquals(building, actualBuilding);
    }

    @Test
    void getBuildingTestFailureBuildingNotFound() {
        // Arrange
        String buildingName = "nonexistentbuilding";
        Mockito.when(buildingRepository.findByName(buildingName)).thenReturn(null);

        // Act and Assert
        try {
            locationManagerRestApis.getBuilding(buildingName);
            fail("Expected ServiceException to be thrown");
        } catch (ServiceException e) {
            assertEquals("Building not found", e.getMessage());
        }
    }


    @Test
    void getCityTestSucces() {
        String cityname = "test";
        City city = new City();
        city.setName(cityname);
        Mockito.when(cityRepository.findByName(cityname)).thenReturn(city);

        City actualcity = locationManagerRestApis.getCity(cityname);
        assertEquals(city, actualcity);
    }


    @Test
    void getCityTestFailure() {
        // Arrange
        String cityname = "nonexistentcity";
        Mockito.when(cityRepository.findByName(cityname)).thenReturn(null);

        // Act and Assert
        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            locationManagerRestApis.getCity(cityname);
        });
        assertTrue(e.getMessage().contains("City not found"));
    }


    @Test
    void getStateTestSucces() {
        String statename = "chennai";
        State state = new State();
        state.setName(statename);
        Mockito.when(stateRepository.findByName(statename)).thenReturn(state);
        State Actutalstate = locationManagerRestApis.getState(statename);
        assertEquals(state, Actutalstate);

    }

    @Test
    void getStateTestFailure() {
        // Arrange
        String statename = "nonexistentstate";
        Mockito.when(stateRepository.findByName(statename)).thenReturn(null);

        // Act and Assert
        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            locationManagerRestApis.getState(statename);
        });
        assertTrue(e.getMessage().contains("State not found"));
    }

    @Test
    void getRackTestSucces() {
        String Rackname = "rack1";
        Rack rack = new Rack();
        rack.setName(Rackname);
        Mockito.when(rackRepository.getByName(Rackname)).thenReturn(rack);
        Rack ActutalRack = locationManagerRestApis.getRack(Rackname);
        assertEquals(rack, ActutalRack);
    }

    @Test
    void getRackTestFailure() {
        // Arrange
        String Rackname = "nonexistentrack";
        Mockito.when(stateRepository.findByName(Rackname)).thenReturn(null);

        // Act and Assert
        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            locationManagerRestApis.getRack(Rackname);
        });
        assertTrue(e.getMessage().contains("Rack not found"));
    }

    @Test
    void createCountryTestSuccess() {
        // Arrange
        Country country = new Country();
        country.setName("test");
        country.setNotes("test");
        Mockito.when(countryRepository.createCountry(country.getName(), country.getNotes())).thenReturn(country);

        // Act
        JSONObject receivedData = locationManagerRestApis.createCountry(country);

        // Assert
        assertEquals("Success", receivedData.get("status"));
        assertEquals(country, receivedData.get("country"));
    }


    @Test
    void createCountryTestFailure() {
        // Arrange
        Country country = new Country();
        country.setName("test");
        country.setNotes("test");

        Mockito.when(countryRepository.createCountry(Mockito.eq(country.getName().toLowerCase()), Mockito.eq(country.getNotes())))
                .thenThrow(new RuntimeException("Failed to create country"));

        // Act and Assert
        RuntimeException e = assertThrows(RuntimeException.class, () -> {
            locationManagerRestApis.createCountry(country);
        });

        assertEquals("Failed to create country", e.getMessage());
    }


    @Test
    void createStateTestSuccess() {
        // Arrange
        String countryName = "testcountry";
        Country country = new Country();
        country.setName(countryName);
        country.setNotes("Test Notes");

        State state = new State();
        state.setName("teststate");
        state.setNotes("Test Notes");

        Mockito.when(countryRepository.findByName(countryName)).thenReturn(country);
        Mockito.when(stateRepository.createState(country.getName(), state.getName(), state.getNotes())).thenReturn(state);

        // Act
        JSONObject receivedResponse = locationManagerRestApis.createState(countryName, state);

        // Assert

        assertEquals("Success", receivedResponse.get("status"));
        assertEquals(state, receivedResponse.get("state"));
    }


    @Test
    void createStateTestFailure() {
        // Test1:Given Country not available
        String countryName = "nonexistentcountry";
        Country country = new Country();
        country.setName(countryName);
        country.setId(1l);
        State state = new State();
        state.setName("teststate");
        state.setNotes("Test Notes");

        Mockito.when(countryRepository.findByName(countryName)).thenReturn(null);

        // Act and Assert
        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            locationManagerRestApis.createState(countryName, state);
        });
        assertTrue(e.getMessage().contains("Given Country not available"));
        // Test2:Given Country not available
        state.setName("nonexistentcountry");
        Mockito.when(countryRepository.findByName(countryName)).thenReturn(country);
        Exception e1 = Assertions.assertThrows(ServiceException.class, () -> {
            locationManagerRestApis.createState(countryName, state);
        });
        assertTrue(e1.getMessage().contains("Country name cannot be the same as the state name" + ":" + countryName));
        // Test3:State already exists
        state.setName("teststate");
        Country existingCountry = new Country();
        existingCountry.setName(country.getName());
        existingCountry.setId(2l);
        Mockito.when(countryRepository.findByName(countryName)).thenReturn(country);
        Mockito.when(countryRepository.findCountryByState(state.getName())).thenReturn(existingCountry);

        Exception e2 = Assertions.assertThrows(ServiceException.class, () -> {
            locationManagerRestApis.createState(countryName, state);
        });
        assertTrue(e2.getMessage().contains("State already exists in " + existingCountry.getName()));

    }

    @Test
    void createCityTestSuccess() {
        // Arrange
        String stateName = "teststate";
        State state = new State();
        state.setName(stateName);
        state.setNotes("Test Notes");

        City city = new City();
        city.setName("testcity");
        city.setNotes("Test Notes");

        Mockito.when(stateRepository.findByName(stateName)).thenReturn(state);
        Mockito.when(cityRepository.createCity(state.getName(), city.getName(), city.getNotes())).thenReturn(city);

        // Act
        JSONObject receivedResponse = locationManagerRestApis.createCity(stateName, city);

        // Assert

        assertEquals("Success", receivedResponse.get("status"));
        assertEquals(city, receivedResponse.get("city"));

    }

    @Test
    void createCityTestFailure() {
        // Test1:Given Country not available
        String statename = "nonexistentstate";
        State state = new State();
        state.setName(statename);
        state.setId(1l);
        City city = new City();
        city.setName("samplecity");
        city.setNotes("Test Notes");

        Mockito.when(stateRepository.findByName(statename)).thenReturn(null);

        // Act and Assert
        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            locationManagerRestApis.createCity(statename, city);
        });
        assertTrue(e.getMessage().contains("Given state is not available"));
        //Test2:State name cannot be the same as the city name
        city.setName("nonexistentstate");
        Mockito.when(stateRepository.findByName(statename)).thenReturn(state);
        // Act and Assert
        Exception e1 = Assertions.assertThrows(ServiceException.class, () -> {
            locationManagerRestApis.createCity(statename, city);
        });
        assertTrue(e1.getMessage().contains("State name cannot be the same as the city name" + ":" + statename));
        //Test3:City already exists
        State existingState = new State();
        existingState.setName(state.getName());
        existingState.setId(3l);
        city.setName("samplecity");
        Mockito.when(stateRepository.findByName(statename)).thenReturn(state);
        Mockito.when(stateRepository.findStateByCity(city.getName())).thenReturn(existingState);
        // Act and Assert
        Exception e2 = Assertions.assertThrows(ServiceException.class, () -> {
            locationManagerRestApis.createCity(statename, city);
        });
        assertTrue(e2.getMessage().contains("City already exists in " + existingState.getName()));
    }

    @Test
    void createBuildingTestSuccess() {
        // Arrange
        String city = "testcity";
        String buildingName = "testbuilding";
        Building building = new Building();
        building.setName(buildingName);
        building.setClliCode("TestCode");
        building.setPhoneNumber("TestNumber");
        building.setContactPerson("TestContact");
        building.setAddress("TestAddress");
        building.setLatitude("123.456");
        building.setLongitude("789.012");
        building.setDrivingInstructions("TestInstructions");
        building.setNotes("TestNotes");

        City cityDetails = new City();
        cityDetails.setName(city);

        Mockito.when(cityRepository.findByName(city)).thenReturn(cityDetails);
        Mockito.when(buildingRepository.findByName(buildingName)).thenReturn(null);
        Mockito.when(buildingRepository.createBuilding(city, building.getName(), building.getClliCode(),
                building.getPhoneNumber(), building.getContactPerson(), building.getAddress(),
                building.getLatitude(), building.getLongitude(), building.getDrivingInstructions(),
                building.getNotes())).thenReturn(building);

        // Act
        JSONObject receivedData = locationManagerRestApis.createBuilding(city, building);

        // Assert
        assertEquals("Success", receivedData.get("status"));
        assertEquals(building, receivedData.get("building"));
    }

    @Test
    void createBuildingTestFailure() {
        // Test case 1: City not found
        {
            // Arrange
            String city = "nonexistentcity";
            Building building = new Building();
            building.setName("testbuilding");

            Mockito.when(cityRepository.findByName(city)).thenReturn(null);

            // Act and Assert
            Exception e = Assertions.assertThrows(ServiceException.class, () -> {
                locationManagerRestApis.createBuilding(city, building);
            });
            assertTrue(e.getMessage().contains("Given city is not available"));
        }

        // Test case 2: Building name already used
        {
            // Arrange
            String city = "existingcity";
            Building building = new Building();
            building.setName("existingbuilding");

            City cityDetails = new City();
            cityDetails.setName(city);

            Building existingBuilding = new Building();
            existingBuilding.setName(building.getName());

            Mockito.when(cityRepository.findByName(city)).thenReturn(cityDetails);
            Mockito.when(buildingRepository.findByName(building.getName())).thenReturn(existingBuilding);

            // Act and Assert
            Exception e = Assertions.assertThrows(ServiceException.class, () -> {
                locationManagerRestApis.createBuilding(city, building);
            });
            assertTrue(e.getMessage().contains("Given building name is already used"));
        }
    }

    @Test
    void testCreateRack_Success() {
        // Arrange
        String building = "testbuilding";
        Rack rack = new Rack();
        rack.setName("testrack");
        rack.setLocation("TestLocation");
        rack.setHeight(10);
        rack.setWidth(20);
        rack.setDepth(3.f);
        rack.setNumOfPositionsContained(14);
        rack.setReservedPositions(new ArrayList<>(Arrays.asList())); // Assigning ArrayList with a single element: 5
        rack.setFreePositions(new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14))); // Assigning ArrayList with a single element: 35
        rack.setNotes("TestNotes");

        Building buildingDetails = new Building();
        buildingDetails.setName(building);

        when(buildingRepository.findByName(building)).thenReturn(buildingDetails);
        when(rackRepository.findByName(rack.getName())).thenReturn(null);
        when(rackRepository.createRack(building, rack.getName(), rack.getLocation(),
                rack.getHeight(), rack.getWidth(), rack.getDepth(), rack.getNumOfPositionsContained(),
                rack.getReservedPositions(), rack.getFreePositions(),
                rack.getNotes())).thenReturn(rack);

        // Act
        JSONObject receivedData = locationManagerRestApis.createRack(building, rack);

        // Assert
        assertEquals("Success", receivedData.get("status"));
        assertEquals(rack, receivedData.get("rack"));
    }

    @Test
    void testCreateRack_Failure() {
        // Test case 1: Building not found
        {
            // Arrange
            String building = "nonexistentbuilding";
            Rack rack = new Rack();
            rack.setName("testrack");

            Mockito.when(buildingRepository.findByName(building)).thenReturn(null);

            // Act and Assert
            Exception e = Assertions.assertThrows(ServiceException.class, () -> {
                locationManagerRestApis.createRack(building, rack);
            });
            assertTrue(e.getMessage().contains("Given building not available"));
        }

        // Test case 2: Rack name already used
        {
            // Arrange
            String building = "existingbuilding";
            Rack rack = new Rack();
            rack.setName("existingrack");

            Building buildingDetails = new Building();
            buildingDetails.setName(building);

            Rack existingRack = new Rack();
            existingRack.setName(rack.getName());

            Mockito.when(buildingRepository.findByName(building)).thenReturn(buildingDetails);
            Mockito.when(rackRepository.findByName(rack.getName())).thenReturn(existingRack);

            // Act and Assert
            Exception e = Assertions.assertThrows(ServiceException.class, () -> {
                locationManagerRestApis.createRack(building, rack);
            });
            assertTrue(e.getMessage().contains("A rack already exists with the given name"));
        }
    }

    @Test
    void updateBuildingTestSuccess() {
        // Arrange
        String city = "testcity";
        Building building = new Building();
        building.setName("testbuilding");
        building.setClliCode("TestCLLICode");
        building.setPhoneNumber("TestPhoneNumber");
        building.setContactPerson("TestContactPerson");
        building.setAddress("TestAddress");
        building.setLatitude("123.456");
        building.setLongitude("789.012");
        building.setDrivingInstructions("TestDrivingInstructions");
        building.setNotes("TestNotes");

        City cityDetails = new City();
        cityDetails.setName(city);

        Building existingBuilding = new Building();
        existingBuilding.setName(building.getName());

        Mockito.when(cityRepository.findByName(city)).thenReturn(cityDetails);
        Mockito.when(buildingRepository.findByName(building.getName())).thenReturn(existingBuilding);
        Mockito.when(buildingRepository.updateBuilding(city, building.getName(), building.getClliCode(),
                building.getPhoneNumber(), building.getContactPerson(), building.getAddress(),
                building.getLatitude(), building.getLongitude(), building.getDrivingInstructions(),
                building.getNotes())).thenReturn(building);

        // Act
        JSONObject receivedData = locationManagerRestApis.updateBuilding(city, building);

        // Assert
        assertEquals("Success", receivedData.get("status"));
        assertEquals(building, receivedData.get("building"));
    }

    @Test
    void updateBuildingTestFailure() {
        // Test case 1: City does not exist
        {
            // Arrange
            String city = "nonexistentcity";
            Building building = new Building();
            building.setName("testbuilding");

            Mockito.when(cityRepository.findByName(city)).thenReturn(null);

            // Act and Assert
            Exception e = Assertions.assertThrows(ServiceException.class, () -> {
                locationManagerRestApis.updateBuilding(city, building);
            });
            assertTrue(e.getMessage().contains("Given city not available"));
        }

        // Test case 2: Building does not exist
        {
            // Arrange
            String city = "existingcity";
            Building building = new Building();
            building.setName("nonexistentbuilding");

            City cityDetails = new City();
            cityDetails.setName(city);

            Mockito.when(cityRepository.findByName(city)).thenReturn(cityDetails);
            Mockito.when(buildingRepository.findByName(building.getName())).thenReturn(null);

            // Act and Assert
            Exception e = Assertions.assertThrows(ServiceException.class, () -> {
                locationManagerRestApis.updateBuilding(city, building);
            });
            assertTrue(e.getMessage().contains("Given building name doesn't exist"));
        }
    }

    @Test
    void updateRackTestSuccess() {
        // Arrange
        String building = "existingbuilding";
        Rack rack = new Rack();
        rack.setName("existingrack");
        rack.setLocation("new location");
        rack.setHeight(10);
        rack.setWidth(5);
        rack.setDepth(8);
        rack.setNumOfPositionsContained(20);
        rack.setReservedPositions(new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5))); // Assigning ArrayList with a single element: 5
        rack.setFreePositions(new ArrayList<>(Arrays.asList(6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20))); // Assigning ArrayList with a single element: 35

        rack.setNotes("Notes");

        Building buildingDetails = new Building();
        buildingDetails.setName(building);

        Rack updatedRack = new Rack();
        updatedRack.setName(rack.getName());
        updatedRack.setLocation(rack.getLocation());
        updatedRack.setHeight(rack.getHeight());
        updatedRack.setWidth(rack.getWidth());
        updatedRack.setDepth(rack.getDepth());
        updatedRack.setNumOfPositionsContained(rack.getNumOfPositionsContained());

        updatedRack.setReservedPositions(rack.getReservedPositions());
        updatedRack.setFreePositions(rack.getFreePositions());
        updatedRack.setNotes("Updated Notes");

        Mockito.when(buildingRepository.findByName(building)).thenReturn(buildingDetails);
        Mockito.when(rackRepository.findByName(rack.getName())).thenReturn(rack);
        Mockito.when(rackRepository.updateRack(building, rack.getName(), rack.getLocation(),
                rack.getHeight(), rack.getWidth(), rack.getDepth(), rack.getNumOfPositionsContained(),
                rack.getReservedPositions(), rack.getFreePositions(),
                rack.getNotes())).thenReturn(updatedRack);

        // Act
        JSONObject response = locationManagerRestApis.updateRack(building, rack);

        // Assert
        assertEquals("Success", response.get("status"));
        assertEquals(updatedRack, response.get("rack"));
    }

    @Test
    void updateRackTestFailure() {
        // Test case: Failure - Building does not exist
        // Arrange
        String nonExistentBuilding = "nonexistentbuilding";
        Rack existingRack = new Rack();
        existingRack.setName("existingRack");

        Mockito.when(buildingRepository.findByName(nonExistentBuilding)).thenReturn(null);

        // Act and Assert
        Exception buildingException = Assertions.assertThrows(ServiceException.class, () -> {
            locationManagerRestApis.updateRack(nonExistentBuilding, existingRack);
        });
        assertTrue(buildingException.getMessage().contains("Given building not available"));

        // Test case: Failure - Rack does not exist
        // Arrange
        String existingBuilding = "existingbuilding";
        Rack nonExistentRack = new Rack();
        nonExistentRack.setName("nonexistentrack");

        Building buildingDetails = new Building();
        buildingDetails.setName(existingBuilding);

        Mockito.when(buildingRepository.findByName(existingBuilding)).thenReturn(buildingDetails);
        Mockito.when(rackRepository.findByName(nonExistentRack.getName())).thenReturn(null);

        // Act and Assert
        Exception rackException = Assertions.assertThrows(ServiceException.class, () -> {
            locationManagerRestApis.updateRack(existingBuilding, nonExistentRack);
        });
        assertTrue(rackException.getMessage().contains("A rack doesn't exist on the given name"));
    }

    @Test
    void deleteBuildingTestSuccess() {
        String Buildingname = "test";
        Building building = new Building();
        building.setName(Buildingname);
        ArrayList<Device> devices = new ArrayList<>();
        JSONObject response = new JSONObject();
        response.put("status", "Success");
        Mockito.when(buildingRepository.findByName(Buildingname)).thenReturn(building);
        Mockito.when(deviceRepository.validateBuildingForDeletion(Buildingname)).thenReturn(devices);
        JSONObject receivedData = locationManagerRestApis.deleteBuilding(Buildingname);
        assertEquals("Success", receivedData.get("status"));
    }

    @Test
    void deleteBuildingTestFailure() {
        // Test case 1: Building not found
        String nonExistingBuildingName = "nonexistingbuilding";
        Mockito.when(buildingRepository.findByName(nonExistingBuildingName)).thenReturn(null);
        Exception buildingNotFoundException = Assertions.assertThrows(ServiceException.class, () -> {
            locationManagerRestApis.deleteBuilding(nonExistingBuildingName);
        });
        assertTrue(buildingNotFoundException.getMessage().contains("Given building not available"));

        // Test case 2: Associated devices exist
        String buildingName = "building1";
        Building building = new Building();
        building.setName(buildingName);
        ArrayList<Device> devices = new ArrayList<>();
        devices.add(new Device());
        Mockito.when(buildingRepository.findByName(buildingName)).thenReturn(building);
        Mockito.when(deviceRepository.validateBuildingForDeletion(buildingName)).thenReturn(devices);
        Exception associatedDevicesException = Assertions.assertThrows(ServiceException.class, () -> {
            locationManagerRestApis.deleteBuilding(buildingName);
        });
        assertTrue(associatedDevicesException.getMessage().contains("Building cannot be deleted as it is associated with devices"));
    }


    @Test
    void deleteCountryTestSuccess() {
        String Countryname = "test";
        Country country = new Country();
        country.setName(Countryname);
        ArrayList<Device> devices = new ArrayList<>();
        JSONObject response = new JSONObject();
        response.put("status", "Success");
        Mockito.when(countryRepository.findByName(Countryname)).thenReturn(country);
        Mockito.when(deviceRepository.validateCountryForDeletion(Countryname)).thenReturn(devices);
        JSONObject receivedData = locationManagerRestApis.deleteCountry(Countryname);
        assertEquals("Success", receivedData.get("status"));
    }

    @Test
    void deleteCountryTestFailure() {
        // Test case 1: Country not found
        String nonExistingCountryName = "nonexistingcountry";
        Mockito.when(countryRepository.findByName(nonExistingCountryName)).thenReturn(null);
        Exception countryNotFoundException = Assertions.assertThrows(ServiceException.class, () -> {
            locationManagerRestApis.deleteCountry(nonExistingCountryName);
        });
        assertTrue(countryNotFoundException.getMessage().contains("Given country not available"));

        // Test case 2: Associated devices exist
        String countryName = "india";
        Country country = new Country();
        country.setName(countryName);
        ArrayList<Device> devices = new ArrayList<>();
        devices.add(new Device());
        Mockito.when(countryRepository.findByName(countryName)).thenReturn(country);
        Mockito.when(deviceRepository.validateCountryForDeletion(countryName)).thenReturn(devices);
        Exception associatedDevicesException = Assertions.assertThrows(ServiceException.class, () -> {
            locationManagerRestApis.deleteCountry(countryName);
        });
        assertTrue(associatedDevicesException.getMessage().contains("Country cannot be deleted as it is associated with devices"));
    }


    @Test
    void deleteStateTestSuccess() {
        String Statename = "chennai";
        State state = new State();
        state.setName(Statename);
        ArrayList<Device> devices = new ArrayList<>();

        JSONObject response = new JSONObject();
        response.put("status", "Success");
        Mockito.when(stateRepository.findByName(Statename)).thenReturn(state);
        Mockito.when(deviceRepository.validateStateForDeletion(Statename)).thenReturn(devices);

        JSONObject receivedData = locationManagerRestApis.deleteState(Statename);
        assertEquals("Success", receivedData.get("status"));
    }

    @Test
    void deleteStateTestFailure() {
        // Test case 1: State not found
        String nonExistingStateName = "nonexistingstate";
        Mockito.when(stateRepository.findByName(nonExistingStateName)).thenReturn(null);
        Exception stateNotFoundException = Assertions.assertThrows(ServiceException.class, () -> {
            locationManagerRestApis.deleteState(nonExistingStateName);
        });
        assertTrue(stateNotFoundException.getMessage().contains("Given state not available"));

        // Test case 2: Associated devices exist
        String stateName = "chennai";
        State state = new State();
        state.setName(stateName);
        ArrayList<Device> devices = new ArrayList<>();
        devices.add(new Device());
        Mockito.when(stateRepository.findByName(stateName)).thenReturn(state);
        Mockito.when(deviceRepository.validateStateForDeletion(stateName)).thenReturn(devices);
        Exception associatedDevicesException = Assertions.assertThrows(ServiceException.class, () -> {
            locationManagerRestApis.deleteState(stateName);
        });
        assertTrue(associatedDevicesException.getMessage().contains("State cannot be deleted as it is associated with devices"));
    }

    @Test
    void deleteCityTestSuccess() {
        String Cityname = "test";
        City city = new City();
        city.setName(Cityname);
        ArrayList<Device> devices = new ArrayList<>();
        JSONObject response = new JSONObject();
        response.put("status", "Success");
        Mockito.when(cityRepository.findByName(Cityname)).thenReturn(city);
        Mockito.when(deviceRepository.validateCityForDeletion(Cityname)).thenReturn(devices);

        JSONObject receivedData = locationManagerRestApis.deleteCity(Cityname);
        assertEquals("Success", receivedData.get("status"));
    }

    @Test
    void deleteCityTestFailure() {
        // Test case 1: City not found
        String nonExistingCityName = "nonexistingcity";
        Mockito.when(cityRepository.findByName(nonExistingCityName)).thenReturn(null);
        Exception cityNotFoundException = Assertions.assertThrows(ServiceException.class, () -> {
            locationManagerRestApis.deleteCity(nonExistingCityName);
        });
        assertTrue(cityNotFoundException.getMessage().contains("Given city not available"));

        // Test case 2: Associated devices exist
        String cityName = "city1";
        City city = new City();
        city.setName(cityName);
        ArrayList<Device> devices = new ArrayList<>();
        devices.add(new Device());
        Mockito.when(cityRepository.findByName(cityName)).thenReturn(city);
        Mockito.when(deviceRepository.validateCityForDeletion(cityName)).thenReturn(devices);
        Exception associatedDevicesException = Assertions.assertThrows(ServiceException.class, () -> {
            locationManagerRestApis.deleteCity(cityName);
        });
        assertTrue(associatedDevicesException.getMessage().contains("City cannot be deleted as it is associated with devices"));
    }


    @Test
    void deleteRackTestSuccess() {
        String Rackname = "test";
        Rack rack = new Rack();
        rack.setName(Rackname);
        ArrayList<Device> devices = new ArrayList<>();
        JSONObject response = new JSONObject();
        response.put("status", "Success");
        Mockito.when(rackRepository.findByName(Rackname)).thenReturn(rack);
        Mockito.when(deviceRepository.validateRackForDeletion(Rackname)).thenReturn(devices);
        JSONObject receivedData = locationManagerRestApis.deleteRack(Rackname);
        assertEquals("Success", receivedData.get("status"));
    }

    @Test
    void deleteRackTestFailure() {
        // Test case 1: Rack not found
        String nonExistingRackName = "nonexistingrack";
        Mockito.when(rackRepository.findByName(nonExistingRackName)).thenReturn(null);
        Exception rackNotFoundException = Assertions.assertThrows(ServiceException.class, () -> {
            locationManagerRestApis.deleteRack(nonExistingRackName);
        });
        assertTrue(rackNotFoundException.getMessage().contains("A rack doesn't exist on the given name"));

        // Test case 2: Associated devices exist
        String rackName = "rack1";
        Rack rack = new Rack();
        rack.setName(rackName);
        ArrayList<Device> devices = new ArrayList<>();
        devices.add(new Device());
        Mockito.when(rackRepository.findByName(rackName)).thenReturn(rack);
        Mockito.when(deviceRepository.validateRackForDeletion(rackName)).thenReturn(devices);
        Exception associatedDevicesException = Assertions.assertThrows(ServiceException.class, () -> {
            locationManagerRestApis.deleteRack(rackName);
        });
        assertTrue(associatedDevicesException.getMessage().contains("Rack cannot be deleted as it is associated with devices"));
    }

    @Test
    void getCountryTestSucces() {
        String Countryname = "india";
        Country country = new Country();
        country.setName(Countryname);
        Mockito.when(countryRepository.findByName(Countryname)).thenReturn(country);

        Country Actutalcountry = locationManagerRestApis.getCountry(Countryname);
        assertEquals(country, Actutalcountry);
    }

    @Test
    void getCountryTestFailure() {
        // Arrange
        String countryName = "india";
        Mockito.when(countryRepository.findByName(countryName)).thenReturn(null);

        // Act and Assert
        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            locationManagerRestApis.getCountry(countryName);
        });
        assertTrue(e.getMessage().contains("country not found"));
    }

    @Test
    void getRackByIdTestSucces() {
        Long Rackid = 1l;
        Rack rack = new Rack();
        rack.setId(Rackid);
        when(rackRepository.findRackById(Rackid)).thenReturn(rack);
        Rack ActutalRack = locationManagerRestApis.getRackById(Rackid);
        assertEquals(rack, ActutalRack);
    }

    @Test
    void getRackByIdTestFailure() {
        // Arrange
        Long Rackid = 1l;
        Mockito.when(rackRepository.findRackById(Rackid)).thenReturn(null);

        // Act and Assert
        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            locationManagerRestApis.getRackById(Rackid);
        });
        Assertions.assertTrue(e.getMessage().contains("Rack id is not found"));
    }

    @Test
    void getBuildingByIdTestSucces() {
        Long Buildingid = 1l;
        Building building = new Building();
        building.setId(Buildingid);
        when(buildingRepository.findBuildingById(Buildingid)).thenReturn(building);

        Building ActutalBuilding = locationManagerRestApis.getBuildingById(Buildingid);
        assertEquals(building, ActutalBuilding);
    }

    @Test
    void getBuildingByIdTestFailure() {
        // Arrange
        Long Buildingid = 1l;
        Mockito.when(buildingRepository.findBuildingById(Buildingid)).thenReturn(null);

        // Act and Assert
        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            locationManagerRestApis.getBuildingById(Buildingid);
        });
        assertTrue(e.getMessage().contains("Building id is not found"));
    }

    @Test
    void testBuildingInCity_Success() {
        // Mock the building repository response
        ArrayList<Building> mockBuildings = new ArrayList<>();
        // Add some dummy buildings to the list
        Building building = new Building();
        mockBuildings.add(building);
        // Define the input parameters
        String city = "testcity";
        // Mock the repository call
        when(buildingRepository.findByCity(city)).thenReturn(mockBuildings);
        // Call the controller method
        ArrayList<Building> result = locationManagerRestApis.getAllBuildingInCity(city);
        // Assert the result
        Assertions.assertEquals(mockBuildings, result);
    }

    @Test
    void testGetAllBuildingInCity_Failure() {
        // Define the input parameters
        String city = "testcity";

        // Mock the repository call to throw an exception
        when(buildingRepository.findByCity(city)).thenThrow(new RuntimeException("Failed to fetch BuildingInCity"));

        // Call the controller method
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            locationManagerRestApis.getAllBuildingInCity(city);
        });

        // Assert the exception message
        String expectedMessage = "Failed to fetch BuildingInCity";
        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testGetRacksInBuilding_Success() {
        // Mock the rack repository response
        ArrayList<Rack> mockRacks = new ArrayList<>();
        // Add some dummy racks to the list
        Rack rack1 = new Rack();
        mockRacks.add(rack1);

        // Define the input parameters
        String building = "testbuilding";

        // Mock the repository call
        when(rackRepository.findByBuilding(building)).thenReturn(mockRacks);

        // Call the controller method
        ArrayList<Rack> result = locationManagerRestApis.getRacksInBuilding(building);

        // Assert the result
        Assertions.assertEquals(mockRacks, result);
    }

    @Test
    void testGetRacksInBuilding_Failure() {
        // Define the input parameters
        String building = "testbuilding";

        // Mock the repository call to throw an exception
        when(rackRepository.findByBuilding(building)).thenThrow(new RuntimeException("Failed to fetch racks"));

        // Call the controller method
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            locationManagerRestApis.getRacksInBuilding(building);
        });
        // Assert the exception message
        String expectedMessage = "Failed to fetch racks";
        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void getAllCountrySuccess() {
        // Mock the building repository response
        ArrayList<Country> mockCountry = new ArrayList<>();
        // Add some dummy buildings to the list
        Country country = new Country();
        mockCountry.add(country);
        // Define the input parameters

        // Mock the repository call
        when(countryRepository.getAllCountry()).thenReturn(mockCountry);
        // Call the controller method
        ArrayList<Country> result = locationManagerRestApis.getAllCountry();
        // Assert the result
        Assertions.assertEquals(mockCountry, result);
    }

    @Test
    void testgetAllCountry_Failure() {
        // Mock the repository call to throw an exception
        when(countryRepository.getAllCountry()).thenThrow(new RuntimeException("Failed to fetch Country"));

        // Call the controller method
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            locationManagerRestApis.getAllCountry();
        });

        // Assert the exception message
        String expectedMessage = "Failed to fetch Country";
        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void getAllStatesSuccess() {
        // Mock the building repository response
        ArrayList<State> mockStates = new ArrayList<>();
        // Add some dummy buildings to the list
        State state = new State();
        mockStates.add(state);
        // Mock the repository call
        when(stateRepository.findAll()).thenReturn(mockStates);
        // Call the controller method
        List<State> result = locationManagerRestApis.getAllStates();
        // Assert the result
        Assertions.assertEquals(mockStates, result);
    }

    @Test
    void testGetAllStates_Failure() {
        // Mock the repository call to throw an exception
        when(stateRepository.findAll()).thenThrow(new RuntimeException("Failed to fetch states"));

        // Call the controller method
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            locationManagerRestApis.getAllStates();
        });

        // Assert the exception message
        String expectedMessage = "Failed to fetch states";
        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }


    @Test
    void testGetAllStateInCountry_Success() {
        // Mock the state repository response
        ArrayList<State> mockStates = new ArrayList<>();
        // Add some dummy states to the list
        State state1 = new State();
        mockStates.add(state1);

        // Define the input parameters
        String country = "testcountry";

        // Mock the repository call
        when(stateRepository.findByCountry(country)).thenReturn(mockStates);

        // Call the controller method
        ArrayList<State> result = locationManagerRestApis.getAllStateInCountry(country);

        // Assert the result
        Assertions.assertEquals(mockStates, result);
    }

    @Test
    void testGetAllStateInCountry_Failure() {
        // Define the input parameters
        String country = "testcountry";
        // Mock the repository call to throw an exception
        when(stateRepository.findByCountry(country)).thenThrow(new RuntimeException("Failed to fetch StateInCountry"));

        // Call the controller method
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            locationManagerRestApis.getAllStateInCountry(country);
        });

        // Assert the exception message
        String expectedMessage = "Failed to fetch StateInCountry";
        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testGetAllCityInState_Success() {
        // Mock the city repository response
        ArrayList<City> mockCities = new ArrayList<>();
        // Add some dummy cities to the list
        City city1 = new City();
        mockCities.add(city1);
        // Define the input parameters
        String state = "teststate";
        // Mock the repository call
        when(cityRepository.findByState(state)).thenReturn(mockCities);
        when(locationManagerRestApis.getAllCityInState(state)).thenReturn(mockCities);
        // Call the controller method
        ArrayList<City> result = locationManagerRestApis.getAllCityInState(state);
        // Assert the result
        Assertions.assertEquals(mockCities, result);
    }

    @Test
    void testGetAllCityInState_Failure() {
        String state = "fakestate";
        // Mock the repository call to throw an exception
        when(cityRepository.findByState(state)).thenThrow(new RuntimeException("Failed to fetch cities"));

        // Call the controller method and assert the exception
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            locationManagerRestApis.getAllCityInState(state);
        });
        // Assert the exception message
        String expectedMessage = "Failed to fetch cities";
        Assertions.assertEquals(expectedMessage, e.getMessage());
    }

    @Test
    void updateCountryByNameFieldNotFoundTest() {
        Country country = new Country();
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            locationManagerRestApis.updateCountryByName("countryName", country);
        });
        Assertions.assertEquals("Country name field is mandatory", e.getMessage());
    }

    @Test
    void updateCountryByNameParamsMismatchTest() {
        Country country = new Country();
        country.setName("countryNameTest");
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            locationManagerRestApis.updateCountryByName("countryName", country);
        });
        Assertions.assertEquals("Request parameter does not match with the request body", e.getMessage());
    }

    @Test
    void updateCountryByNameDoNotExistTest() {
        Country country = new Country();
        country.setName("countryName");

        Mockito.when(countryRepository.findByName(country.getName().toLowerCase())).thenReturn(null);
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            locationManagerRestApis.updateCountryByName("countryName", country);
        });
        Assertions.assertEquals("Country countryname does not exist", e.getMessage());
    }

    @Test
    void updateCountryByNameSuccessTest() {
        Country country = new Country();
        country.setName("countryName");
        country.setNotes("Sample Notes");

        Mockito.when(countryRepository.findByName(country.getName().toLowerCase())).thenReturn(country);
        Mockito.when(countryRepository.updateCountry(country.getName().toLowerCase(), country.getNotes())).thenReturn(country);
        JSONObject countryResponse = locationManagerRestApis.updateCountryByName("countryName", country);

        Assertions.assertEquals("Success", countryResponse.get("status"));
        Assertions.assertEquals(country, countryResponse.get("country"));
    }

    @Test
    void updateStateByNameFieldNotFoundTest() {
        State state = new State();
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            locationManagerRestApis.updateStateByName("stateName", state);
        });
        Assertions.assertEquals("State name field is mandatory", e.getMessage());
    }

    @Test
    void updateStateByNameParamsMismatchTest() {
        State state = new State();
        state.setName("stateNameTest");
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            locationManagerRestApis.updateStateByName("stateName", state);
        });
        Assertions.assertEquals("Request parameter does not match with the request body", e.getMessage());
    }

    @Test
    void updateStateByNameDoNotExistTest() {
        State state = new State();
        state.setName("stateName");

        Mockito.when(stateRepository.findByName(state.getName().toLowerCase())).thenReturn(null);
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            locationManagerRestApis.updateStateByName("stateName", state);
        });
        Assertions.assertEquals("State statename does not exist", e.getMessage());
    }

    @Test
    void updateStateByNameSuccessTest() {
        State state = new State();
        state.setName("stateName");
        state.setNotes("Sample Notes");

        Mockito.when(stateRepository.findByName(state.getName().toLowerCase())).thenReturn(state);
        Mockito.when(stateRepository.updateState(state.getName().toLowerCase(), state.getNotes())).thenReturn(state);
        JSONObject stateResponse = locationManagerRestApis.updateStateByName("stateName", state);

        Assertions.assertEquals("Success", stateResponse.get("status"));
        Assertions.assertEquals(state, stateResponse.get("state"));
    }

    @Test
    void updateCityByNameFieldNotFoundTest() {
        City city = new City();
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            locationManagerRestApis.updateCityByName("cityName", city);
        });
        Assertions.assertEquals("City name field is mandatory", e.getMessage());
    }

    @Test
    void updateCityByNameParamsMismatchTest() {
        City city = new City();
        city.setName("cityNameTest");
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            locationManagerRestApis.updateCityByName("cityName", city);
        });
        Assertions.assertEquals("Request parameter does not match with the request body", e.getMessage());
    }

    @Test
    void updateCityByNameDoNotExistTest() {
        City city = new City();
        city.setName("cityName");

        Mockito.when(cityRepository.findByName(city.getName())).thenReturn(null);
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            locationManagerRestApis.updateCityByName("cityName", city);
        });
        Assertions.assertEquals("City cityname does not exist", e.getMessage());
    }

    @Test
    void updateCityByNameSuccessTest() {
        City city = new City();
        city.setName("cityName");
        city.setNotes("Sample Notes");

        Mockito.when(cityRepository.findByName(city.getName().toLowerCase())).thenReturn(city);
        Mockito.when(cityRepository.updateCity(city.getName().toLowerCase(), city.getNotes())).thenReturn(city);
        JSONObject cityResponse = locationManagerRestApis.updateCityByName("cityName", city);

        Assertions.assertEquals("Success", cityResponse.get("status"));
        Assertions.assertEquals(city, cityResponse.get("city"));
    }

    @Test
    void updateBuildingByNameFieldNotFoundTest() {
        Building building = new Building();
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            locationManagerRestApis.updateBuildingByName("buildingName", building);
        });
        Assertions.assertEquals("Building name field is mandatory", e.getMessage());
    }

    @Test
    void updateBuildingByNameParamsMismatchTest() {
        Building building = new Building();
        building.setName("buildingNameTest");
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            locationManagerRestApis.updateBuildingByName("buildingName", building);
        });
        Assertions.assertEquals("Request parameter does not match with the request body", e.getMessage());
    }

    @Test
    void updateBuildingByNameDoNotExistTest() {
        Building building = new Building();
        building.setName("buildingName");

        Mockito.when(buildingRepository.findByName(building.getName().toLowerCase())).thenReturn(null);
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            locationManagerRestApis.updateBuildingByName("buildingName", building);
        });
        Assertions.assertEquals("Building buildingname does not exist", e.getMessage());
    }

    @Test
    void updateBuildingByNameSuccessTest() {
        Building building = new Building();
        building.setName("buildingName");
        building.setClliCode("BEVLARXA");
        building.setPhoneNumber("12345");
        building.setAddress("Sample Address");
        building.setContactPerson("Sush");
        building.setLatitude("123.1");
        building.setLongitude("123.2");
        building.setDrivingInstructions("Sample Driving Instructions");
        building.setNotes("Sample Notes");

        Mockito.when(buildingRepository.findByName(building.getName().toLowerCase())).thenReturn(building);
        Mockito.when(buildingRepository.updateBuilding(building.getName().toLowerCase(),
                building.getClliCode(), building.getPhoneNumber(), building.getContactPerson(), building.getAddress(),
                building.getLatitude(), building.getLongitude(), building.getDrivingInstructions(),
                building.getNotes())).thenReturn(building);
        JSONObject buildingResponse = locationManagerRestApis.updateBuildingByName("buildingName", building);

        Assertions.assertEquals("Success", buildingResponse.get("status"));
        Assertions.assertEquals(building, buildingResponse.get("building"));
    }

    @Test
    void updateRackByNameFieldNotFoundTest() {
        Rack rack = new Rack();
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            locationManagerRestApis.updateRackByName("rackName", rack);
        });
        Assertions.assertEquals("Rack name field is mandatory", e.getMessage());
    }

    @Test
    void updateRackByNameParamsMismatchTest() {
        Rack rack = new Rack();
        rack.setName("rackNameTest");
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            locationManagerRestApis.updateRackByName("rackName", rack);
        });
        Assertions.assertEquals("Request parameter does not match with the request body", e.getMessage());
    }

    @Test
    void updateRackByNameDoNotExistTest() {
        Rack rack = new Rack();
        rack.setName("rackName");

        Mockito.when(rackRepository.findByName(rack.getName().toLowerCase())).thenReturn(null);
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            locationManagerRestApis.updateRackByName("rackName", rack);
        });
        Assertions.assertEquals("Rack rackname does not exist", e.getMessage());
    }

    @Test
    void updateRackByNameSuccessTest() {
        Rack rack = new Rack();
        rack.setName("rackName");
        rack.setLocation("BEVLARXA");
        rack.setHeight(12);
        rack.setWidth(11);
        rack.setDepth(10);
        rack.setNumOfPositionsContained(9);
        rack.setReservedPositions(new ArrayList<>());
        rack.setFreePositions(new ArrayList<>());
        rack.setNotes("Sample Notes");

        Mockito.when(rackRepository.findByName(rack.getName().toLowerCase())).thenReturn(rack);
        Mockito.when(rackRepository.updateRack(rack.getName().toLowerCase(), rack.getLocation(),
                rack.getHeight(), rack.getWidth(), rack.getDepth(), rack.getNumOfPositionsContained(),
                rack.getReservedPositions(), rack.getFreePositions(),
                rack.getNotes())).thenReturn(rack);
        JSONObject rackResponse = locationManagerRestApis.updateRackByName("rackName", rack);

        Assertions.assertEquals("Success", rackResponse.get("status"));
        Assertions.assertEquals(rack, rackResponse.get("rack"));
    }

    @Test
    void checkNameFieldTest() {
        Country country = new Country();
        country.setName("  ");
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            locationManagerRestApis.createCountry(country);
        });
        Assertions.assertEquals("Name cannot be empty or null", e.getMessage());
    }
}
package com.prodapt.netinsight.locationManager;

import com.prodapt.netinsight.deviceInstanceManager.Device;
import com.prodapt.netinsight.deviceInstanceManager.DeviceRepository;
import com.prodapt.netinsight.exceptionsHandler.AppExceptionHandler;
import com.prodapt.netinsight.exceptionsHandler.ExceptionDetails;
import com.prodapt.netinsight.exceptionsHandler.ServiceException;
import com.prodapt.netinsight.objectModelManager.ObjectModellerApis;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class holds all the REST API endpoints for handling location level operations
 */
@RestController
public class LocationManagerRestApis {

    Logger logger = LoggerFactory.getLogger(LocationManagerRestApis.class);
    @Autowired
    BuildingRepository buildingRepository;

    @Autowired
    AppExceptionHandler appExceptionHandler;

    @Autowired
    CityRepository cityRepository;

    @Autowired
    StateRepository stateRepository;

    @Autowired
    CountryRepository countryRepository;

    @Autowired
    RackRepository rackRepository;

    @Autowired
    ObjectModellerApis objectModellerApis;

    /**
     * This function is responsible for getting details of a building
     *
     * @param name building name
     * @return building details
     */
    @GetMapping("/getBuilding")
    public Building getBuilding(@RequestParam(name = "name") String name) {
        name = name.toLowerCase();
        logger.info("Inside getBuilding");
        Building building = null;
        try {
            building = buildingRepository.findByName(name);
            if (building == null) {
                appExceptionHandler.raiseException("Building not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return building;
    }

    /**
     * This function is responsible for getting list of all buildings in a city
     *
     * @param city city name
     * @return list of buildings in a city
     */
    @GetMapping("/getAllBuilding")
    public ArrayList<Building> getAllBuildingInCity(@RequestParam(name = "city") String city) {
        city = city.toLowerCase();
        logger.info("Inside getAllBuildingInCity");
        ArrayList<Building> buildings = null;
        try {
            buildings = buildingRepository.findByCity(city);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return buildings;
    }

    /**
     * This function is responsible for getting details of a city
     *
     * @param name city name
     * @return city details
     */
    @GetMapping("/getCity")
    public City getCity(@RequestParam(name = "name") String name) {
        name = name.toLowerCase();
        logger.info("Inside getCity");
        City city = null;
        try {
            city = cityRepository.findByName(name);
            if (city == null) {
                appExceptionHandler.raiseException("City not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return city;
    }

    /**
     * This function is responsible for getting details of all cities in a state
     *
     * @param state state name
     * @return list of cities in a state
     */
    @GetMapping("/getAllCity")
    public ArrayList<City> getAllCityInState(@RequestParam(name = "state") String state) {
        state = state.toLowerCase();
        logger.info("Inside getAllCityInState");
        ArrayList<City> cities = null;
        try {
            cities = cityRepository.findByState(state);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return cities;
    }

    /**
     * This function is responsible for getting details of a state
     *
     * @param name state name
     * @return state details
     */
    @GetMapping("/getState")
    public State getState(@RequestParam(name = "name") String name) {
        name = name.toLowerCase();
        logger.info("Inside getState");
        State state = null;
        try {
            state = stateRepository.findByName(name);
            if (state == null) {
                appExceptionHandler.raiseException("State not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return state;
    }

    /**
     * This function is responsible for getting details of all states in a country
     *
     * @param country country name
     * @return list of states in country
     */
    @GetMapping("/getAllState")
    public ArrayList<State> getAllStateInCountry(@RequestParam(name = "country") String country) {
        country = country.toLowerCase();
        logger.info("Inside getAllStateInCountry");
        ArrayList<State> states = null;
        try {
            states = stateRepository.findByCountry(country);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return states;
    }

    /**
     * This function is responsible for getting details of all states
     *
     * @return list of states
     */
    @GetMapping("/getAllStates")
    public List<State> getAllStates() {
        logger.info("Inside getAllStates");
        List<State> states = null;
        try {
            states = stateRepository.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return states;
    }

    /**
     * This function is responsible for getting details of a country
     *
     * @param name country name
     * @return country details
     */
    @GetMapping("/getCountry")
    public Country getCountry(@RequestParam(name = "name") String name) {
        name = name.toLowerCase();
        logger.info("Inside getCountry");
        Country country = null;

        try {
            country = countryRepository.findByName(name);
            if (country == null) {
                appExceptionHandler.raiseException("country not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return country;
    }

    /**
     * This function is responsible for getting details of all countries
     *
     * @return list of all countries
     */
    @GetMapping("/getAllCountry")
    public ArrayList<Country> getAllCountry() {
        logger.info("Inside getAllCountry");
        ArrayList<Country> countries = null;
        try {
            countries = countryRepository.getAllCountry();
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return countries;
    }

    /**
     * This function is responsible for getting details of a rack
     *
     * @param name rack name
     * @return rack details
     */
    @GetMapping("/getRack")
    public Rack getRack(@RequestParam(name = "name") String name) {
        name = name.toLowerCase();
        logger.info("Inside getRack");
        Rack rack = null;
        try {
            rack = rackRepository.getByName(name);
            if (rack == null) {
                appExceptionHandler.raiseException("Rack not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return rack;
    }

    /**
     * This function is responsible for getting details of all racks in a building
     *
     * @param building building name
     * @return list of racks in a building
     */
    @GetMapping("/getAllRack")
    public ArrayList<Rack> getRacksInBuilding(@RequestParam(name = "building") String building) {
        building = building.toLowerCase();
        logger.info("Inside getRacksInBuilding");
        ArrayList<Rack> racks = null;
        try {
            racks = rackRepository.findByBuilding(building);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return racks;
    }

    /**
     * This function is responsible for creating a country
     *
     * @param country country details
     * @return status of operation
     */
    @PostMapping("/createCountry")
    public JSONObject createCountry(@RequestBody Country country) {
        logger.info("Inside createCountry, country value received: {}", country.getName().toLowerCase());
        JSONObject response = new JSONObject();

        try {
            checkName(country.getName());
            Country countryDetails = countryRepository.createCountry(country.getName().toLowerCase(), country.getNotes());
            response.put("status", "Success");
            response.put("country", countryDetails);
        } catch (ServiceException e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());

        }
        return response;
    }

    /**
     * This function is responsible for creating a state within a country
     *
     * @param name  country name
     * @param state state details
     * @return status of operation
     */
    @PostMapping("/createState")
    public JSONObject createState(@RequestParam(name = "country") String name, @RequestBody State state) {
        String country = name.toLowerCase();
        state.setName(state.getName().toLowerCase());
        logger.info("Inside createState, country value received: {}", country);
        JSONObject response = new JSONObject();

        try {
            checkName(state.getName());
            logger.debug("Inside createState, state value received: {}", state);
            Country countryDetails = countryRepository.findByName(country);
            if (countryDetails == null) {
                appExceptionHandler.raiseException("Given Country not available");
            }
            // Country name and state name cannot be the same, handle the validation error
            if (country.equalsIgnoreCase(state.getName())) {
                appExceptionHandler.raiseException("Country name cannot be the same as the state name" + ":" + name);
            }
            // Check if the state already exists in any country
            Country existingCountry = countryRepository.findCountryByState(state.getName());
            if (existingCountry != null) {
                // State already exists, check if it has a relation with any country
                if (existingCountry.getId() != countryDetails.getId()) {
                    // State is related to a different country, throw an exception
                    appExceptionHandler.raiseException("State already exists in " + existingCountry.getName());
                }

            }
            // State doesn't exist, create a new state
            State stateDetails = stateRepository.createState(
                    countryDetails.getName(),
                    state.getName(),
                    state.getNotes()
            );
            response.put("status", "Success");
            response.put("state", stateDetails);

        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for creating a city within a state
     *
     * @param state state name
     * @param city  city details
     * @return status of operation
     */
    @PostMapping("/createCity")
    public JSONObject createCity(@RequestParam(name = "state") String state, @RequestBody City city) {
        state = state.toLowerCase();
        logger.info("Inside createCity, state value received: {}", state);
        JSONObject response = new JSONObject();

        try {
            checkName(city.getName());
            logger.debug("Inside createCity, city value received: {}", city.getName().toLowerCase());
            State stateDetails = stateRepository.findByName(state);
            logger.debug("stateDetails: {}", stateDetails);
            if (stateDetails == null) {
                appExceptionHandler.raiseException("Given state is not available");
            }
            if (state.equalsIgnoreCase(city.getName())) {
                appExceptionHandler.raiseException("State name cannot be the same as the city name" + ":" + state);
            }

            // Check if the city already exists in any state
            State existingState = stateRepository.findStateByCity(city.getName());
            if (existingState != null) {
                // City already exists, check if it has a relation with any state
                if (existingState.getId() != stateDetails.getId()) {
                    // City is related to a different state, throw an exception
                    appExceptionHandler.raiseException("City already exists in " + existingState.getName());
                }

            }
            //create new city
            City cityDetails = cityRepository.createCity(stateDetails.getName(), city.getName().toLowerCase(), city.getNotes());

            response.put("status", "Success");
            response.put("city", cityDetails);

        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for creating a building within a city
     *
     * @param city     city name
     * @param building building details
     * @return status of operation
     */
    @PostMapping("/createBuilding")
    public JSONObject createBuilding(@RequestParam(name = "city") String city, @RequestBody Building building) {
        city = city.toLowerCase();

        logger.info("Inside createBuilding, city value received: {}", city);
        JSONObject response = new JSONObject();
        try {
            logger.debug("Inside createBuilding: {}", building.toString());
            City cityDetails = cityRepository.findByName(city);
            if (cityDetails == null) {
                appExceptionHandler.raiseException("Given city is not available");
            }

            Building existingBuilding = buildingRepository.findByName(building.getName().toLowerCase());
            if (existingBuilding != null) {
                appExceptionHandler.raiseException("Given building name is already used");
            }
            Building buildingDetails = buildingRepository.createBuilding(city, building.getName().toLowerCase(), building.getClliCode(),
                    building.getPhoneNumber(), building.getContactPerson(), building.getAddress(),
                    building.getLatitude(), building.getLongitude(), building.getDrivingInstructions(),
                    building.getNotes());
            if (building.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", building.getAdditionalAttributes());
                objectModellerApis.addAdditionalAttributes(building.getAdditionalAttributes(), buildingDetails);
            }
            response.put("status", "Success");
            response.put("building", buildingDetails);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for updating a building within a city
     *
     * @param city     city name
     * @param building building details
     * @return status of operation
     */
    @PutMapping("/updateBuilding")
    public JSONObject updateBuilding(@RequestParam(name = "city") String city, @RequestBody Building building) {
        city = city.toLowerCase();
        logger.info("Inside updateBuilding, city value received: {}", city);
        JSONObject response = new JSONObject();
        try {
            logger.debug("Inside updateBuilding: {}", building.toString());

            City cityDetails = cityRepository.findByName(city);
            if (cityDetails == null) {
                appExceptionHandler.raiseException("Given city not available");
            }
            Building existingBuilding = buildingRepository.findByName(building.getName().toLowerCase());
            if (existingBuilding == null) {
                appExceptionHandler.raiseException("Given building name doesn't exist");
            }
            Building buildingDetails = buildingRepository.updateBuilding(city, building.getName().toLowerCase(), building.getClliCode(),
                    building.getPhoneNumber(), building.getContactPerson(), building.getAddress(),
                    building.getLatitude(), building.getLongitude(), building.getDrivingInstructions(),
                    building.getNotes());
            if (building.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", building.getAdditionalAttributes());
                objectModellerApis.addAdditionalAttributes(building.getAdditionalAttributes(), buildingDetails);
            }
            response.put("status", "Success");
            response.put("building", buildingDetails);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for creating a rack within a building
     *
     * @param building building name
     * @param rack     rack details
     * @return status of operation
     */
    @PostMapping("/createRack")
    public JSONObject createRack(@RequestParam(name = "building") String building, @RequestBody Rack rack) {
        building = building.toLowerCase();
        logger.info("Inside createRack, building value received: {}", building);
        JSONObject response = new JSONObject();
        try {
            logger.debug("Inside createRack: {}", rack.toString());
            Building buildingDetails = buildingRepository.findByName(building);
            if (buildingDetails == null) {
                appExceptionHandler.raiseException("Given building not available");
            }
            Rack existingRack = rackRepository.findByName(rack.getName().toLowerCase());
            if (existingRack != null) {
                appExceptionHandler.raiseException("A rack already exists with the given name");
            }

            int NumOfPositionContained = rack.getNumOfPositionsContained();
            rack.setFreePositions((ArrayList<Integer>) IntStream.rangeClosed(1, NumOfPositionContained).boxed().
                    collect(Collectors.toList()));
            rack.setReservedPositions(new ArrayList<>());
            Rack rackDetails = rackRepository.createRack(building, rack.getName().toLowerCase(), rack.getLocation(),
                    rack.getHeight(), rack.getWidth(), rack.getDepth(), rack.getNumOfPositionsContained()
                    , rack.getReservedPositions(), rack.getFreePositions(),
                    rack.getNotes());
            if (rack.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", rack.getAdditionalAttributes());
                objectModellerApis.addAdditionalAttributes(rack.getAdditionalAttributes(), rackDetails);
            }
            response.put("status", "Success");
            response.put("rack", rackDetails);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }


    /**
     * This function is responsible for updating a rack within a building
     *
     * @param building building name
     * @param rack     rack details
     * @return status of operation
     */
    @PutMapping("/updateRack")
    public JSONObject updateRack(@RequestParam(name = "building") String building, @RequestBody Rack rack) {
        building = building.toLowerCase();
        logger.info("Inside updateRack, building value received: {}", building);
        JSONObject response = new JSONObject();
        try {
            logger.debug("Inside updateRack: {}", rack.toString());
            Building buildingDetails = buildingRepository.findByName(building);
            if (buildingDetails == null) {
                appExceptionHandler.raiseException("Given building not available");
            }
            Rack existingRack = rackRepository.findByName(rack.getName().toLowerCase());
            if (existingRack == null) {
                appExceptionHandler.raiseException("A rack doesn't exist on the given name");
            }
            int NumOfPositionContained = rack.getNumOfPositionsContained();

            List<Integer> freePos = rack.getFreePositions();
            List<Integer> reservedPos = rack.getReservedPositions();
            if (NumOfPositionContained != (freePos.size() + reservedPos.size())) {
                appExceptionHandler.raiseException("sum of Free Postion and  Reserved Postion  should be  equal to Number Of Position Contained");
            }
            for (int i = 0; i < freePos.size(); i++) {
                if (freePos.get(i) > NumOfPositionContained) {
                    appExceptionHandler.raiseException("Given free position " + freePos.get(i) + " is Not Available in rack");
                }
            }
            for (int i = 0; i < reservedPos.size(); i++) {
                if (reservedPos.get(i) > NumOfPositionContained) {
                    appExceptionHandler.raiseException("Given reserved position " + reservedPos.get(i) + " is Not Available in rack");
                }
            }
            for (int i = 0; i < freePos.size(); i++) {
                for (int j = 0; j < reservedPos.size(); j++) {
                    if (freePos.get(i) == reservedPos.get(j)) {
                        appExceptionHandler.raiseException("free Position and reserved position cannot hold the same position in rack");
                    }
                }
            }
            Rack rackDetails = rackRepository.updateRack(building, rack.getName().toLowerCase(), rack.getLocation(),
                    rack.getHeight(), rack.getWidth(), rack.getDepth(), rack.getNumOfPositionsContained(),
                    rack.getReservedPositions(), rack.getFreePositions(),
                    rack.getNotes());
            if (rack.getAdditionalAttributes() != null) {
                logger.debug("Additional attributes found: {}", rack.getAdditionalAttributes());
                objectModellerApis.addAdditionalAttributes(rack.getAdditionalAttributes(), rackDetails);
            }
            response.put("status", "Success");
            response.put("rack", rackDetails);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for getting rack details with ID
     *
     * @param id rack id
     * @return rack details
     */
    @GetMapping("/getRackById")
    public Rack getRackById(@RequestParam("id") Long id) {
        logger.info("Inside getRackById, id value received: {}", id);
        Rack rack = null;
        try {
            rack = rackRepository.findRackById(id);
            if (rack == null) {
                appExceptionHandler.raiseException("Rack id is not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return rack;
    }

    /**
     * This function is responsible for getting building details with ID
     *
     * @param id building id
     * @return building details
     */
    @GetMapping("/getBuildingById")
    public Building getBuildingById(@RequestParam("id") Long id) {
        logger.info("Inside getBuildingById, id value received: {}", id);
        Building building = null;
        try {
            building = buildingRepository.findBuildingById(id);
            if (building == null) {
                appExceptionHandler.raiseException("Building id is not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return building;
    }

    /*
     * Location deletion APIs
     */
    @Autowired
    DeviceRepository deviceRepository;


    /**
     * This function is responsible for deleting a building
     *
     * @param building building name
     * @return status of operation
     */
    @DeleteMapping("/deleteBuilding")
    public JSONObject deleteBuilding(@RequestParam(name = "building") String building) {
        building = building.toLowerCase();
        logger.info("Inside deleteBuilding, building value received: {}", building);
        JSONObject response = new JSONObject();
        try {
            logger.debug("Inside deleteBuilding: {}", building);
            Building buildingDetails = buildingRepository.findByName(building);
            if (buildingDetails == null) {
                appExceptionHandler.raiseException("Given building not available");
            }
            ArrayList<Device> devices = deviceRepository.validateBuildingForDeletion(building);
            if (devices.size() > 0) {
                appExceptionHandler.raiseException("Building cannot be deleted as it is associated with devices");
            }
            buildingRepository.deleteBuilding(building);
            response.put("status", "Success");
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for deleting a country
     *
     * @param country country name
     * @return status of operation
     */
    @DeleteMapping("/deleteCountry")
    public JSONObject deleteCountry(@RequestParam(name = "country") String country) {
        country = country.toLowerCase();
        logger.info("Inside deleteCountry, country value received: {}", country);
        JSONObject response = new JSONObject();
        try {
            logger.debug("Inside deleteCountry: {}", country);
            Country countryDetails = countryRepository.findByName(country);
            if (countryDetails == null) {
                appExceptionHandler.raiseException("Given country not available");
            }
            ArrayList<Device> devices = deviceRepository.validateCountryForDeletion(country);
            if (devices.size() > 0) {
                appExceptionHandler.raiseException("Country cannot be deleted as it is associated with devices");
            }
            countryRepository.deleteCountry(country);
            response.put("status", "Success");
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for deleting a state
     *
     * @param state state name
     * @return status of operation
     */
    @DeleteMapping("/deleteState")
    public JSONObject deleteState(@RequestParam(name = "state") String state) {
        state = state.toLowerCase();
        logger.info("Inside deleteState, state value received: {}", state);
        JSONObject response = new JSONObject();
        try {
            logger.debug("Inside deleteState: {}", state);
            State stateDetails = stateRepository.findByName(state);
            if (stateDetails == null) {
                appExceptionHandler.raiseException("Given state not available");
            }
            ArrayList<Device> devices = deviceRepository.validateStateForDeletion(state);
            if (devices.size() > 0) {
                appExceptionHandler.raiseException("State cannot be deleted as it is associated with devices");
            }
            stateRepository.deleteState(state);
            response.put("status", "Success");
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for deleting city
     *
     * @param city city name
     * @return status of operation
     */
    @DeleteMapping("/deleteCity")
    public JSONObject deleteCity(@RequestParam(name = "city") String city) {
        city = city.toLowerCase();
        logger.info("Inside deleteCity, city value received: {}", city);
        JSONObject response = new JSONObject();
        try {
            logger.debug("Inside deleteCity: {}", city);
            City cityDetails = cityRepository.findByName(city);
            if (cityDetails == null) {
                appExceptionHandler.raiseException("Given city not available");
            }
            ArrayList<Device> devices = deviceRepository.validateCityForDeletion(city);
            if (devices.size() > 0) {
                appExceptionHandler.raiseException("City cannot be deleted as it is associated with devices");
            }
            cityRepository.deleteCity(city);
            response.put("status", "Success");
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for deleting rack
     *
     * @param rack rack name
     * @return status of operation
     */
    @DeleteMapping("/deleteRack")
    public JSONObject deleteRack(@RequestParam(name = "rack") String rack) {
        rack = rack.toLowerCase();
        logger.info("Inside deleteRack, rack value received: {}", rack);
        JSONObject response = new JSONObject();
        try {
            logger.debug("Inside deleteRack: {}", rack);
            Rack existingRack = rackRepository.findByName(rack);
            if (existingRack == null) {
                appExceptionHandler.raiseException("A rack doesn't exist on the given name");
            }
            ArrayList<Device> devices = deviceRepository.validateRackForDeletion(rack);
            if (devices.size() > 0) {
                appExceptionHandler.raiseException("Rack cannot be deleted as it is associated with devices");
            }
            rackRepository.deleteRack(rack);
            response.put("status", "Success");
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for updating a country using country name
     *
     * @param countryName country name
     * @param country     country details
     * @return status of operation
     */
    @PutMapping("/updateCountryByName")
    public JSONObject updateCountryByName(@RequestParam(name = "name") String countryName, @RequestBody Country country) {
        countryName = countryName.toLowerCase();
        logger.info("Inside updateCountryByName for {}", countryName);
        JSONObject response = new JSONObject();
        try {
            //validate country name
            if (country.getName() == null) {
                appExceptionHandler.raiseException("Country name field is mandatory");
            }
            if (!country.getName().equalsIgnoreCase(countryName)) {
                appExceptionHandler.raiseException("Request parameter does not match with the request body");
            }

            countryName = countryName.toLowerCase();
            Country existingCountryDetails = countryRepository.findByName(countryName);
            if (existingCountryDetails == null) {
                appExceptionHandler.raiseException("Country " + countryName + " does not exist");
            }

            Country countryDetails = countryRepository.updateCountry(country.getName().toLowerCase(), country.getNotes());

            response.put("status", "Success");
            response.put("country", countryDetails);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for updating a state using state name
     *
     * @param stateName state name
     * @param state     state details
     * @return status of operation
     */
    @PutMapping("/updateStateByName")
    public JSONObject updateStateByName(@RequestParam(name = "name") String stateName, @RequestBody State state) {
        stateName = stateName.toLowerCase();
        logger.info("Inside updateStateByName for {}", stateName);
        JSONObject response = new JSONObject();
        try {
            if (state.getName() == null) {
                appExceptionHandler.raiseException("State name field is mandatory");
            }
            if (!state.getName().equalsIgnoreCase(stateName)) {
                appExceptionHandler.raiseException("Request parameter does not match with the request body");
            }

            stateName = stateName.toLowerCase();
            State existingStateDetails = stateRepository.findByName(stateName);
            if (existingStateDetails == null) {
                appExceptionHandler.raiseException("State " + stateName + " does not exist");
            }

            State stateDetails = stateRepository.updateState(stateName, state.getNotes());

            response.put("status", "Success");
            response.put("state", stateDetails);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for updating a city using city name
     *
     * @param cityName city name
     * @param city     city details
     * @return status of operation
     */
    @PutMapping("/updateCityByName")
    public JSONObject updateCityByName(@RequestParam(name = "name") String cityName, @RequestBody City city) {
        cityName = cityName.toLowerCase();
        logger.info("Inside updateCityByName for {}", city);
        JSONObject response = new JSONObject();
        try {
            //validate city name
            if (city.getName() == null) {
                appExceptionHandler.raiseException("City name field is mandatory");
            }
            if (!city.getName().equalsIgnoreCase(cityName)) {
                appExceptionHandler.raiseException("Request parameter does not match with the request body");
            }

            cityName = cityName.toLowerCase();
            City existingCityDetails = cityRepository.findByName(cityName);
            if (existingCityDetails == null) {
                appExceptionHandler.raiseException("City " + cityName + " does not exist");
            }

            City cityDetails = cityRepository.updateCity(cityName, city.getNotes());

            response.put("status", "Success");
            response.put("city", cityDetails);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for updating a building using building name
     *
     * @param buildingName building name
     * @param building     building details
     * @return status of operation
     */
    @PutMapping("/updateBuildingByName")
    public JSONObject updateBuildingByName(@RequestParam(name = "name") String buildingName, @RequestBody Building building) {
        logger.info("Inside updateBuildingByName {}", building);
        JSONObject response = new JSONObject();
        try {
            //validate building name
            if (building.getName() == null) {
                appExceptionHandler.raiseException("Building name field is mandatory");
            }
            if (!building.getName().equalsIgnoreCase(buildingName)) {
                appExceptionHandler.raiseException("Request parameter does not match with the request body");
            }

            buildingName = buildingName.toLowerCase();
            Building existingBuilding = buildingRepository.findByName(buildingName);
            if (existingBuilding == null) {
                appExceptionHandler.raiseException("Building " + buildingName + " does not exist");
            }

            Building buildingDetails = buildingRepository.updateBuilding(buildingName,
                    building.getClliCode(), building.getPhoneNumber(), building.getContactPerson(), building.getAddress(),
                    building.getLatitude(), building.getLongitude(), building.getDrivingInstructions(),
                    building.getNotes());
            response.put("status", "Success");
            response.put("building", buildingDetails);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    /**
     * This function is responsible for updating a rack using rack name
     *
     * @param rackName rack name
     * @param rack     rack details
     * @return status of operation
     */
    @PutMapping("/updateRackByName")
    public JSONObject updateRackByName(@RequestParam(name = "name") String rackName, @RequestBody Rack rack) {
        rackName = rackName.toLowerCase();
        logger.info("Inside updateRackByName for rack {}", rackName);
        JSONObject response = new JSONObject();
        try {
            //validate rack name
            if (rack.getName() == null) {
                appExceptionHandler.raiseException("Rack name field is mandatory");
            }
            if (!rack.getName().equalsIgnoreCase(rackName)) {
                appExceptionHandler.raiseException("Request parameter does not match with the request body");
            }

            rackName = rackName.toLowerCase();
            Rack existingRack = rackRepository.findByName(rackName);
            if (existingRack == null) {
                appExceptionHandler.raiseException("Rack " + rackName + " does not exist");
            }

            Rack rackDetails = rackRepository.updateRack(rackName, rack.getLocation(),
                    rack.getHeight(), rack.getWidth(), rack.getDepth(), rack.getNumOfPositionsContained(),
                    rack.getReservedPositions(), rack.getFreePositions(),
                    rack.getNotes());
            response.put("status", "Success");
            response.put("rack", rackDetails);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;

    }

    public static void checkName(String Name) throws ServiceException {
        if (Name == null || Name.trim().isEmpty()) {
            throw new ServiceException("Name cannot be empty or null", new ExceptionDetails(
                    "Name cannot be empty or null", "Name cannot be empty or null"));
        }
    }
}

package com.prodapt.netinsight.exceptionsHandler;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * This class handles the retrieval of exceptions from the exception db
 */
@RestController
public class ExceptionEndpoints implements ExceptionDb {

    @Autowired
    ExceptionRepo exceptionRepo;

    /**
     *  This function is used to add new exceptions
     */
    @PostMapping("/addexception")
    @Override
    public JSONObject addException(@RequestBody ExceptionEntity exception) {
        if (exception.getCreationTime() == null) {
            exception.setCreationTime(Date.from(Instant.now()));
        }
        exceptionRepo.save(exception);
        JSONObject resp = new JSONObject();
        resp.put("Status", "Success");
        return resp;
    }

    /**
     * This function is used to remove exception from ExceptionsDb with a given id
     *
     * @param exceptionId has exception id
     * @return null
     */
    @Override
    public JSONObject removeException(int exceptionId) {
        return null;
    }

    /* Returns all exceptions from the DB
        Don't run this unless required, as this api call will pull all exceptions which would take long time.
        Sample Url https://<ip>:<8091>/exceptions/getall
     */
    @GetMapping("/exceptions/getall")
    @Override
    public List<ExceptionEntity> getAllExceptions() {
        return exceptionRepo.findAll();
    }

    /*
       Returns exception with a given id
       Sample Url https://<ip>:<8091>/get?id=12
       @Param id
    */
    @GetMapping("/get")
    public ExceptionEntity getById(@RequestParam(name = "id") Integer id) {
        return exceptionRepo.findById(id).get();
    }

    /*
       Returns exceptions on a given device name
       Sample Url https://<ip>:<8091>/getbyname?device=testinput
       @Param device deviceName
    */
    @GetMapping("/getbyname")
    public List<ExceptionEntity> getByName(@RequestParam(name = "device") String device) {
        System.out.println("name: " + device);
        if(device == null || device.length() == 0) {
            return Collections.emptyList();
        }
        return exceptionRepo.getExceptionsWithNeName(device);
    }

    /*
       Returns exceptions between 2 dates
    */
    @PostMapping("/exceptions/dateFiltered")
    @Override
    public List<ExceptionEntity> getExceptionsFiltered(@RequestBody ExceptionFilter exceptionFilter) {
        return exceptionRepo.findAllByCreationTimeBetween(exceptionFilter.getStartDate(), exceptionFilter.getEndDate());
    }

    /*
       Returns exceptions on a given date
    */
    @PostMapping("/exceptions/specificDate")
    public List<ExceptionEntity> getSpecificDateExceptionsFiltered(@RequestBody ExceptionFilter exceptionFilter) {
        return exceptionRepo.findAllByCreationTime(exceptionFilter.getStartDate());
    }

    /*
       deletes all exceptions from the DB.
       Don't run this unless you are sure of the consequences.
    */
    @DeleteMapping("/exceptions/deleteEveryException")
    public JSONObject deleteAll() {
        exceptionRepo.deleteAll();
        JSONObject resp = new JSONObject();
        resp.put("Status", "Deleted all exceptions");
        return resp;
    }

    /*
       Returns number of exceptions on the DB
    */
    @GetMapping("/count")
    public JSONObject getCount() {
        Long count = exceptionRepo.count();
        System.out.println("count = " + count);
        JSONObject resp = new JSONObject();
        resp.put("count", count);
        return resp;
    }
}

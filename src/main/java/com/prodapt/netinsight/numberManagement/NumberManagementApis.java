package com.prodapt.netinsight.numberManagement;

import com.prodapt.netinsight.exceptionsHandler.AppExceptionHandler;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class NumberManagementApis {

    Logger logger = LoggerFactory.getLogger(NumberManagementApis.class);

    @Autowired
    AppExceptionHandler appExceptionHandler;

    @Autowired
    MsisdnIccidRepo msisdnIccidRepo;

    @Autowired
    MsisdnPoolRepo msisdnPoolRepo;

    /**
     * This API returns the list of MSISDNs that are available in the database.
     * Covers the required functionalities of UQ01 UIV Query – Get Number
     * @param count Number of msisdn numbers to be returned
     * @return list of msisdn numbers
     */
    @GetMapping("/msisdn/free")
    public List<String> getFreeMsisdn(@RequestParam(name = "count") Integer count) {
        try {
            return msisdnIccidRepo.getFreeMsisdn(count).stream()
                    .limit(Math.min(count, 100))
                    .collect(Collectors.toList());
        }  catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException("Error in fetching free MSISDN numbers, Error Message: " +
                    e.getMessage());
        }
        return null;
    }

    /**
     * This API returns the list of ICCID that are available in the database.
     * Covers the required functionalities of UQ01 UIV Query – Get Number
     * @param count Number of ICCID numbers to be returned
     * @return list of iccid numbers
     */
    @GetMapping("/iccid/free")
    public List<String> getFreeIccid(@RequestParam(name = "count") Integer count) {
        try {
            return msisdnIccidRepo.getFreeIccid(count).stream()
                    .limit(Math.min(count, 100))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException("Error in fetching free ICCID numbers, Error Message: " +
                    e.getMessage());
        }
        return null;
    }

    /**
     * This API returns the status of MSISDNs
     * Covers the required functionalities of UQ02 UIV Query – Get Status
     * @return status of msisdn number
     */
    @GetMapping("/msisdn/status")
    public String getMsisdnStatus(@RequestParam(name = "msisdn") String msisdn) {
        String response = null;
        try {
            response = msisdnIccidRepo.getMsisdnStatus(msisdn);
            if (response == null) {
                appExceptionHandler.raiseException("MSISDN number not found in the database");
            }
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException("Error in fetching MSISDN number status, Error Message: " +
                    e.getMessage());
        }
        return response;
    }

    @PostMapping("/upload-msisdn-iccid")
    public String uploadMsisdnIccid(MultipartFile file) throws Exception {
        logger.info("Start uploadMsisdnIccid with file {}", file.getOriginalFilename());
        List<MsisdnIccid> msisdnIccidList = new ArrayList<>();
        InputStream input = file.getInputStream();
        CsvParserSettings setting = new CsvParserSettings();
        //Spilt the Delimiter from Csv
        setting.getFormat().setDelimiter(',');
        //Spilt the Topic for Csv
        setting.setHeaderExtractionEnabled(true);
        CsvParser parser = new CsvParser(setting);
        List<com.univocity.parsers.common.record.Record> parseAllRecords = parser.parseAllRecords(input);
        logger.debug("CSV parsed successfully");
        logger.info("Total records to be processed: {}", parseAllRecords.size());
        parseAllRecords.forEach(record -> msisdnIccidList.add(processRecord(record)));
        msisdnIccidRepo.saveAll(msisdnIccidList);
        logger.info("Successfully uploaded {} MsisdnIccid records", msisdnIccidList.size());
        return "Upload Success";
    }

    public MsisdnIccid processRecord(com.univocity.parsers.common.record.Record record) {
        logger.debug("Start processRecord with record {}", record.toString());
        MsisdnIccid msisdnIccid = new MsisdnIccid();
        msisdnIccid.setMsisdn(record.getString("msisdn") != null ? record.getString("msisdn") : null);
        msisdnIccid.setIccid(record.getString("iccid") != null ? record.getString("iccid") : null);
        msisdnIccid.setImsi(record.getString("imsi") != null ? record.getString("imsi") : null);
        msisdnIccid.setImei(record.getString("imei") != null ? record.getString("imei") : null);
        msisdnIccid.setCustomerId(record.getString("customer_id") != null ? Long.parseLong(record.getString("customer_id")) : null);
        msisdnIccid.setPlanId(record.getString("plan_id") != null ? Long.parseLong(record.getString("plan_id")) : null);
        msisdnIccid.setIccidStatus(record.getString("iccid_status") != null ? record.getString("iccid_status") : null);
        msisdnIccid.setMsisdnStatus(record.getString("msisdn_status") != null ? record.getString("msisdn_status") : null);
        msisdnIccid.setActivationCode(record.getString("activation_code") != null ? record.getString("activation_code") : null);
        msisdnIccid.setCreatedInPool(record.getString("created_in_pool") != null ? record.getString("created_in_pool") : null);
        msisdnIccid.setPortedOut(record.getString("ported_out") != null ? Boolean.parseBoolean(record.getString("ported_out")) : null);
        msisdnIccid.setOlsp(record.getString("olsp") != null ? record.getString("olsp") : null);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date activationDate = record.getString("activation_date") != null ? dateFormat.parse(record.getString("activation_date")) : null;
            msisdnIccid.setActivationDate(activationDate);
            Date expirationDate = record.getString("expiration_date") != null ? dateFormat.parse(record.getString("expiration_date")) : null;
            msisdnIccid.setExpirationDate(expirationDate);
        } catch (ParseException e) {
            logger.error("Error parsing dates for record: " + record, e);
            throw new RuntimeException("Error parsing dates for record: " + record, e);
        }
        logger.debug("Record processed successfully");
        logger.info("Successfully processed record: {}", msisdnIccid.getMsisdn());
        return msisdnIccid;
    }

    /**
     * This API returns the free MSISDNs matching the given pattern
     * Covers the required functionalities of UQ04 UIV Query – Get MSISDN
     * @return msisdn numbers
     */
    @GetMapping("/msisdn/pattern")
    public List<String> getMsisdnWithPattern(@RequestBody PatternPojo patternPojo) {
        ArrayList<String> response = new ArrayList<>();
        try {
            String pattern = patternPojo.getGoldenPattern().replace("*", "%");
            response = msisdnIccidRepo.getGoldenPatternMsisdn(Math.min(patternPojo.getCount(), 100),
                    patternPojo.getCountryCode(), pattern);
            if (response == null || response.size() == 0) {
                appExceptionHandler.raiseException("No MSISDN number found with the given pattern");
            }
        }  catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException("Error in fetching MSISDN number, Error Message: " +
                    e.getMessage());
        }
        return response;
    }

    /**
     * This API returns the status of ICCID
     * Covers the required functionalities of UQ02 UIV Query – Get Status
     * @return status of iccid number
     */
    @GetMapping("/iccid/status")
    public String getIccidStatus(@RequestParam(name = "iccid") String iccid) {
        String response = null;
        try {
            response = msisdnIccidRepo.getIccidStatus(iccid);
            if (response == null) {
                appExceptionHandler.raiseException("ICCID number not found in the database");
            }
        }  catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException("Error in fetching ICCID number status, Error Message: " +
                    e.getMessage());
        }
        return response;
    }

    /**
     * This API assigns the status of given MSISDN to reserved.
     * Covers the required functionalities of UN01 UIV Number Management – Reserve Number (ICCID or MSISDN)
     * @param msisdn id to be reserved
     * @return status of operation
     */
    @PutMapping("/msisdn/reserve")
    public JSONObject reserveMsisdn(@RequestParam(name = "msisdn") String msisdn) {
        JSONObject response = new JSONObject();
        try {
            String status = msisdnIccidRepo.getMsisdnStatus(msisdn);
            if(status == null) {
                appExceptionHandler.raiseException("MSISDN number not found in the database");
            }
            else if (!status.equalsIgnoreCase("Free")) {
                appExceptionHandler.raiseException("MSISDN number is not free, current status: " + status);
            }
            // set expiry after 28 days from today
            Date expiryDate = new Date();
            expiryDate.setTime(expiryDate.getTime() + (28 * 24 * 60 * 60 * 1000));
            logger.debug("Expiry date: {}", expiryDate);
            msisdnIccidRepo.reserveMsisdn(msisdn, expiryDate);
        }  catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException("Error in reserving MSISDN number, Error Message: " +
                    e.getMessage());
        }
        response.put("Status", "Success");
        return response;
    }


    @PostMapping("/upload-MsisdnPool")
    public String uploadMsisdnPool(MultipartFile file) throws Exception {
        logger.info("Start uploadMsisdnPool with file {}", file.getOriginalFilename());
        List<MsisdnPool> msisdnPoolList = new ArrayList<>();
        InputStream input = file.getInputStream();
        CsvParserSettings setting = new CsvParserSettings();
        //Spilt the Delimiter from Csv
        setting.getFormat().setDelimiter(',');
        //Spilt the Topic for Csv
        setting.setHeaderExtractionEnabled(true);
        CsvParser parser = new CsvParser(setting);
        List<com.univocity.parsers.common.record.Record> parseAllRecords = parser.parseAllRecords(input);
        logger.debug("CSV parsed successfully");
        logger.info("Total records to be processed: {}", parseAllRecords.size());
        parseAllRecords.forEach(record -> msisdnPoolList.add(processPoolRecord(record)));
        msisdnPoolRepo.saveAll(msisdnPoolList);
        logger.info("Successfully uploaded {} MsisdnPool records", msisdnPoolList.size());
        return "Upload Success";
    }
    public MsisdnPool processPoolRecord(com.univocity.parsers.common.record.Record record) {
        logger.debug("Start processPoolRecord with record {}", record.toString());
        MsisdnPool msisdnPool = new MsisdnPool();
        msisdnPool.setMsisdn(record.getString("msisdn") != null ? record.getString("msisdn") : null);
        msisdnPool.setStatus(record.getString("status") != null ? record.getString("status") : null);
        msisdnPool.setPoolId(record.getString("poolId") != null ? record.getString("poolId") : null);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            msisdnPool.setAllocationDate(new Date());
            Date expirationDate = record.getString("expiration_date") != null ? dateFormat.parse(record.getString("expiration_date")) : null;
            msisdnPool.setExpirationDate(expirationDate);
        } catch (ParseException e) {
            logger.error("Error parsing dates for record: " + record, e);
            throw new RuntimeException("Error parsing dates for record: " + record, e);
        }
        logger.debug("Record processed successfully");
        logger.info("Successfully processed record: {}", msisdnPool.getMsisdn());
        return msisdnPool;
    }
    // todo: create api end point for loading CSV into  msisdn_iccid table (load 120+ msisdn_iccid records)
    // todo: create api end point for loading CSV into  msisdn_pool table (load all pool records)
}

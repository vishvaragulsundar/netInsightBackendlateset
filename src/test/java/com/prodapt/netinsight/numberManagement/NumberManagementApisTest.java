package com.prodapt.netinsight.numberManagement;
import com.prodapt.netinsight.exceptionsHandler.ServiceException;
import com.univocity.parsers.common.record.Record;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@SpringBootTest
@ActiveProfiles("UNIT")
public class NumberManagementApisTest {
    @MockBean
    MsisdnPoolRepo msisdnPoolRepo;
    @MockBean
    MsisdnIccidRepo msisdnIccidRepo;
    @Autowired
    NumberManagementApis numberManagementApis;

    @Test
    void uploadMsisdnIccid_Success() throws Exception {
        // create a mock file
        MultipartFile file = new MockMultipartFile("file", "file content".getBytes());
        // act
        String result = numberManagementApis.uploadMsisdnIccid(file);
        // assert
        assertEquals("Upload Success", result);
        verify(msisdnIccidRepo, times(1)).saveAll(any());
    }

    @Test
    void processRecordTest() {
        // Prepare
        Record record = mock(Record.class);
        Mockito.when(record.getString("msisdn")).thenReturn("test-msisdn");
        Mockito.when(record.getString("iccid")).thenReturn("test-iccid");
        Mockito.when(record.getString("imsi")).thenReturn("test-imsi");
        Mockito.when(record.getString("imei")).thenReturn("test-imei");
        Mockito.when(record.getString("customer_id")).thenReturn("12345");
        Mockito.when(record.getString("plan_id")).thenReturn("67890");
        Mockito.when(record.getString("iccid_status")).thenReturn("test-iccid_status");
        Mockito.when(record.getString("msisdn_status")).thenReturn("test-msisdn_status");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(new Date());
        Mockito.when(record.getString("activation_date")).thenReturn(dateString);
        Mockito.when(record.getString("expiration_date")).thenReturn(dateString);

        Mockito.when(record.getString("activation_code")).thenReturn("test-activation_code");
        Mockito.when(record.getString("created_in_pool")).thenReturn("test-created_in_pool");
        Mockito.when(record.getString("ported_out")).thenReturn("false");
        Mockito.when(record.getString("olsp")).thenReturn("test-olsp");

        // Act
        MsisdnIccid result = numberManagementApis.processRecord(record);

        // Assert
        assertEquals("test-msisdn", result.getMsisdn());
        assertEquals("test-iccid", result.getIccid());
        assertEquals(Long.valueOf("12345"), result.getCustomerId());
        assertEquals(Long.valueOf("67890"), result.getPlanId());
        assertEquals(Boolean.FALSE, result.getPortedOut());
        assertEquals("test-iccid_status", result.getIccidStatus());
        assertEquals("test-msisdn_status", result.getMsisdnStatus());

        // Date Assertions
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        assertEquals(sdf.format(new Date()), sdf.format(result.getActivationDate()));
        assertEquals(sdf.format(new Date()), sdf.format(result.getExpirationDate()));

        assertEquals("test-activation_code", result.getActivationCode());
        assertEquals("test-created_in_pool", result.getCreatedInPool());
        assertEquals(Boolean.FALSE, result.getPortedOut());
        assertEquals("test-olsp", result.getOlsp());

    }

    @Test
    void processRecordTest_ThrowsParseException() {
        Record record = mock(Record.class);
        Mockito.when(record.getString("activation_date")).thenReturn("incorrect_date_format");
        // Act and Assert
        assertThrows(RuntimeException.class, () -> numberManagementApis.processRecord(record),
                "Expected processRecord() to throw, but it didn't");
    }

    @Test
    void uploadMsisdnPool_Success() throws Exception {
        // create a mock file
        MultipartFile file = new MockMultipartFile("file", "file content".getBytes());
        // act
        String result = numberManagementApis.uploadMsisdnPool(file);
        // assert
        assertEquals("Upload Success", result);
        verify(msisdnPoolRepo, times(1)).saveAll(any());
    }

    @Test
    void processPoolRecordTest() {
        Record record = mock(Record.class);
        Mockito.when(record.getString("msisdn")).thenReturn("test-msisdn");
        Mockito.when(record.getString("status")).thenReturn("test-iccid");
        Mockito.when(record.getString("poolId")).thenReturn("test-pool");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(new Date());
        Mockito.when(record.getString("expiration_date")).thenReturn(dateString);
        MsisdnPool result = numberManagementApis.processPoolRecord(record);
        assertEquals("test-msisdn", result.getMsisdn());
        assertEquals("test-iccid", result.getStatus());
        assertEquals("test-pool", result.getPoolId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        assertEquals(sdf.format(new Date()), sdf.format(result.getExpirationDate()));
    }

    @Test
    void processPoolRecordTest_ThrowsParseException() {
        Record record = mock(Record.class);
        Mockito.when(record.getString("expiration_date")).thenReturn("incorrect_date_format");
        // Act and Assert
        assertThrows(RuntimeException.class, () -> numberManagementApis.processPoolRecord(record),
                "Expected processRecord() to throw, but it didn't");
    }

    @Test
    void getMsisdnStatusTestSuccess() {
        String msisdn = "917320000061";
        MsisdnIccid msisdnIccid = new MsisdnIccid();
        msisdnIccid.setMsisdn(msisdn);
        Mockito.when(msisdnIccidRepo.getMsisdnStatus(msisdn)).thenReturn(msisdn);
        String response = numberManagementApis.getMsisdnStatus(msisdn);
        assertEquals(msisdn, response);
    }

    @Test
    void getMsisdnStatusTestFailure() {
        String msisdn = "917320000061";
        MsisdnIccid msisdnIccid = new MsisdnIccid();
        Mockito.when(msisdnIccidRepo.getMsisdnStatus(msisdn)).thenReturn(null);
        // Act and Assert
        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            numberManagementApis.getMsisdnStatus(msisdn);
        });
        assertTrue(e.getMessage().contains("MSISDN number not found in the database"));
    }

    @Test
    void getIccidStatusTestSuccess() {
        String Iccid = "89911012000032000001";
        MsisdnIccid msisdnIccid = new MsisdnIccid();
        msisdnIccid.setMsisdn(Iccid);
        Mockito.when(msisdnIccidRepo.getIccidStatus(Iccid)).thenReturn(Iccid);
        String response = numberManagementApis.getIccidStatus(Iccid);
        assertEquals(Iccid, response);
    }

    @Test
    void getIccidStatusTestFailure() {
        String Iccid = "89911012000032000001";
        MsisdnIccid msisdnIccid = new MsisdnIccid();
        Mockito.when(msisdnIccidRepo.getIccidStatus(Iccid)).thenReturn(null);
        // Act and Assert
        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            numberManagementApis.getIccidStatus(Iccid);
        });
        assertTrue(e.getMessage().contains("ICCID number not found in the database"));
    }

    @Test
    void reserveMsisdnTestFailure() {
        String msisdn = "917320000004";
        MsisdnIccid msisdnIccid = new MsisdnIccid();
        msisdnIccid.setMsisdnStatus("Resrved");
        msisdnIccid.setMsisdn(msisdn);
        Mockito.when(msisdnIccidRepo.getMsisdnStatus(msisdn)).thenReturn(msisdnIccid.getMsisdnStatus());
        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            numberManagementApis.reserveMsisdn(msisdn);
        });
        assertTrue(e.getMessage().contains("MSISDN number is not free, current status: " + msisdnIccid.getMsisdnStatus()));
    }

    @Test
    void reserveMsisdnTestFailureNull() {
        String msisdn = "917320000004";
        Mockito.when(msisdnIccidRepo.getMsisdnStatus(msisdn)).thenReturn(null);
        Exception e = Assertions.assertThrows(ServiceException.class, () -> {
            numberManagementApis.reserveMsisdn(msisdn);
        });
        assertTrue(e.getMessage().contains("MSISDN number not found in the database"));
    }

    @Test
    void reserveMsisdnTestSuccess() {
        String msisdn = "917320000004";
        MsisdnIccid msisdnIccid = new MsisdnIccid();
        msisdnIccid.setMsisdn(msisdn);
        msisdnIccid.setMsisdnStatus("Free");
        Date expiryDate = new Date();
        expiryDate.setTime(expiryDate.getTime() + (28 * 24 * 60 * 60 * 1000));
        MsisdnIccid msisdnIccid1 = new MsisdnIccid();
        msisdnIccid1.setMsisdn(msisdn);
        msisdnIccid1.setMsisdnStatus("Resrved");
        msisdnIccid1.setExpirationDate(expiryDate);
        Mockito.when(msisdnIccidRepo.getMsisdnStatus(msisdn)).thenReturn(msisdnIccid.getMsisdnStatus());
        doNothing().when(msisdnIccidRepo).reserveMsisdn(msisdn, expiryDate);
        JSONObject response = numberManagementApis.reserveMsisdn(msisdn);
        response.put("Status", "Success");
        assertEquals("Success", response.get("Status"));
    }

    @Test
    public void testGetFreeMsisdn() {
        // Given
        int count = 5;
        // Return list from repo
        ArrayList<String> mockData = new ArrayList<>(Arrays.asList("1234", "1235", "1236", "1237", "1238"));
        // Stub the getFreeMsisdn method
        when(msisdnIccidRepo.getFreeMsisdn(count)).thenReturn(mockData);
        // When
        List<String> result = numberManagementApis.getFreeMsisdn(count);
        // Then
        assertEquals(mockData, result);
    }

    @Test
    public void testGetFreeIccid() {
        // Given
        int count = 5;
        // Return list from repo
        ArrayList<String> mockData = new ArrayList<>(Arrays.asList("1234", "1235", "1236", "1237", "1238"));
        // Stub the getFreeMsisdn method
        when(msisdnIccidRepo.getFreeIccid(count)).thenReturn(mockData);
        // When
        List<String> result = numberManagementApis.getFreeIccid(count);
        // Then
        assertEquals(mockData, result);
    }
    @Test
    public void testGetFreeIccidException() {
        // arrange
        int count = 2;
        String exceptionMessage = "Test exception";
        when(msisdnIccidRepo.getFreeIccid(anyInt())).thenThrow(new RuntimeException(exceptionMessage));
        // act
        Exception exception = assertThrows(ServiceException.class, () -> numberManagementApis.getFreeIccid(count));
        // assert
        assertTrue(exception.getMessage().contains(exceptionMessage));
    }
    @Test
    public void testGetFreeMsisdnException() {
        // arrange
        int count = 2;
        String exceptionMessage = "Test exception";
        when(msisdnIccidRepo.getFreeMsisdn(anyInt())).thenThrow(new RuntimeException(exceptionMessage));
        // act
        Exception exception = assertThrows(ServiceException.class, () -> numberManagementApis.getFreeMsisdn(count));
        // assert
        assertTrue(exception.getMessage().contains(exceptionMessage));
    }
    @Test
    public void testGetMsisdnWithPattern() {
        // Given
        PatternPojo patternPojo = new PatternPojo();
        patternPojo.setGoldenPattern("123*");
        patternPojo.setCount(50);
        patternPojo.setCountryCode("IN");
        ArrayList<String> mockData = new ArrayList<>(Arrays.asList("1234", "1235", "1236"));
        when(msisdnIccidRepo.getGoldenPatternMsisdn(anyInt(), anyString(), anyString()))
                .thenReturn(mockData);
        // When
        List<String> result = numberManagementApis.getMsisdnWithPattern(patternPojo);
        // Then
        assertEquals(mockData, result);
    }
    @Test
    public void testGetMsisdnWithPattern_noMsisdnsFound() {
        // Given
        PatternPojo patternPojo = new PatternPojo();
        patternPojo.setGoldenPattern("123*");
        patternPojo.setCount(50);
        patternPojo.setCountryCode("IN");

        // Return an empty list from the repo
        when(msisdnIccidRepo.getGoldenPatternMsisdn(anyInt(), anyString(), anyString())).thenReturn(new ArrayList<>());

        // When & Then
        Exception exception = assertThrows(Exception.class, () -> numberManagementApis.getMsisdnWithPattern(patternPojo));
        assertTrue(exception.getMessage().contains("No MSISDN number found with the given pattern"));
    }
}
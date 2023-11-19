package com.prodapt.netinsight.scaleTest;

import com.google.gson.Gson;
import com.prodapt.netinsight.tmfWrapper.DeviceInstanceApiWrappers;
import com.prodapt.netinsight.tmfWrapper.pojo.DeviceDetailsResponse;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

@SpringBootTest
@ActiveProfiles("UNIT")
public class ScaleTest {

    Logger logger = LoggerFactory.getLogger(ScaleTest.class);

    @Autowired
    DeviceInstanceApiWrappers deviceInstanceApiWrappers;

//    @Test
//    public void test() throws IOException {
//        Gson gson = new Gson();
//
//        StringBuilder contentBuilder = new StringBuilder();
//        BufferedReader br = new BufferedReader(new FileReader("src/test/java/com/prodapt/netinsight/scaleTest/createDev.json"));
//        String line;
//        while ((line = br.readLine()) != null) {
//            contentBuilder.append(line).append("\n");
//        }
//        br.close();
//        String fileContent = contentBuilder.toString();
//
//        logger.info("fileContent: {}", fileContent);
//        for (int i=5006; i<5007; i++) {
//            String updatedContent = fileContent.replace("name-context", "nokia-Isam-dev_"+i);
//            DeviceDetailsResponse deviceDetails = gson.fromJson(updatedContent, DeviceDetailsResponse.class);
//            deviceInstanceApiWrappers.createDeviceTmf(deviceDetails);
//        }
//        logger.info("Done");
//    }
}

package com.prodapt.netinsight.numberManagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;

@Repository
public interface MsisdnIccidRepo extends JpaRepository<MsisdnIccid, Long> {

    @Query(value="select msisdn from msisdn_iccid where MSISDN_Status = 'Free' and olsp IS NULL " +
            "ORDER BY msisdn ASC LIMIT :count", nativeQuery = true)
    public ArrayList<String> getFreeMsisdn(@Param("count") Integer count);

    @Query(value="select iccid from msisdn_iccid where ICCID_Status = 'Free' and olsp IS NULL " +
            "ORDER BY msisdn ASC LIMIT :count", nativeQuery = true)
    public ArrayList<String> getFreeIccid(@Param("count") Integer count);

    @Query(value="select MSISDN_Status from msisdn_iccid where msisdn = :msisdn", nativeQuery = true)
    public String getMsisdnStatus(@Param("msisdn") String msisdn);

    @Query(value="select ICCID_Status from msisdn_iccid where iccid = :iccid", nativeQuery = true)
    public String getIccidStatus(@Param("iccid") String iccid);

    @Query(value="SELECT msisdn FROM msisdn_iccid WHERE MSISDN_Status = 'Free' and msisdn LIKE :countryCode% and" +
            " msisdn LIKE :goldenPattern ORDER BY msisdn ASC LIMIT :count", nativeQuery = true)
    public ArrayList<String> getGoldenPatternMsisdn(@Param("count") Integer count,
                                               @Param("countryCode") String countryCode,
                                               @Param("goldenPattern") String goldenPattern);

    @Transactional
    @Modifying
    @Query(value="UPDATE msisdn_iccid SET MSISDN_Status = 'Reserved', expiration_date = :expiryDate " +
            "where msisdn = :msisdn and MSISDN_Status = 'Free' ", nativeQuery = true)
    public void reserveMsisdn(@Param("msisdn") String msisdn,
                                @Param("expiryDate") Date expiryDate);
}

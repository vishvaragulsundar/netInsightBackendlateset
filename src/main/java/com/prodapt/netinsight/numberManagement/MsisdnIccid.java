package com.prodapt.netinsight.numberManagement;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
public class MsisdnIccid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "MSISDN", unique = true)
    private String msisdn;

    @Column(name = "ICCID",unique = true)
    private String iccid;

    @Column(name = "IMSI", unique = true)
    private String imsi;

    @Column(name = "IMEI",unique = true)
    private String imei;

    @Column(name = "Customer_ID")
    private Long customerId;

    @Column(name = "Plan_ID")
    private Long planId;

    @Column(name = "MSISDN_Status")
    private String msisdnStatus;

    @Column(name = "ICCID_Status")
    private String iccidStatus;

    @Column(name = "Activation_Date")
    private Date activationDate;

    @Column(name = "Expiration_Date")
    private Date expirationDate;

    @Column(name = "Activation_Code")
    private String activationCode;

    @Column(name = "CREATED_IN_POOL")
    private String createdInPool;

    @Column(name = "PORTED_OUT")
    private Boolean portedOut;

    @Column(name = "OLSP")
    private String olsp;
}
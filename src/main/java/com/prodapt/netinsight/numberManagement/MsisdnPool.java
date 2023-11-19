package com.prodapt.netinsight.numberManagement;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
public class MsisdnPool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "MSISDN", nullable = false, unique = true)
    private String msisdn;

    @Column(name = "Status", nullable = false)
    private String status;

    @Column(name = "Allocation_Date")
    private Date allocationDate;

    @Column(name = "Expiration_Date")
    private Date expirationDate;

    @Column(name = "Pool_ID", nullable = false)
    private String poolId;

    // constructors, getters and setters, equals, hashCode, and toString methods
}
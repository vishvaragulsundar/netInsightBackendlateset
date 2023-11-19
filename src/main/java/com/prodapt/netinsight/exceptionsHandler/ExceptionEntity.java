package com.prodapt.netinsight.exceptionsHandler;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * This class is the POJO for storing exceptions on DB
 */
@Entity
public class ExceptionEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;

    public ExceptionEntity(String inputMessage, String errorMessage) {
        this.inventorySystem = "netInsightCoreApi";
        this.creationTime = Date.from(Instant.now());
        this.inputMessage = inputMessage;
        this.errorMessage = errorMessage;
    }

    public String getInputMessage() {
        return inputMessage;
    }

    public void setInputMessage(String inputMessage) {
        this.inputMessage = inputMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="creation_time")
    Date creationTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(nullable = false, name="inputMessage")
    private String inputMessage;

    @Column(nullable = false, name="errorMessage")
    private String errorMessage;

    public String getInventorySystem() {
        return inventorySystem;
    }

    public void setInventorySystem(String inventorySystem) {
        this.inventorySystem = inventorySystem;
    }

    @Column(nullable = false, name="inventorySystem")
    private String inventorySystem;

    public ExceptionEntity() {
    }
}

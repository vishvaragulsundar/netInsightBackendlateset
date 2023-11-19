package com.prodapt.netinsight.exceptionsHandler;

import java.util.Date;

/**
 * POJO for retrieving exceptions filtered on date
 */
public class ExceptionFilter {
    Date startDate;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    Date endDate;
}

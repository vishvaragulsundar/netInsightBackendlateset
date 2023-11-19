package com.prodapt.netinsight.exceptionsHandler;

import org.springframework.stereotype.Component;

/**
 * This class extends the RuntimeException class and helps in creating custom exceptions on code.
 */
@Component
public class ServiceException extends RuntimeException {

    ExceptionDetails exceptionDetails;

    public ServiceException()
    {

    }
    public ServiceException(String message, ExceptionDetails exceptionDetails) {
        super(message);
        this.exceptionDetails = exceptionDetails;
    }
}

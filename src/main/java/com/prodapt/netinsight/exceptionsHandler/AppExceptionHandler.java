package com.prodapt.netinsight.exceptionsHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.Instant;

/**
 * This class holds the functions to raise exceptions
 */
@Component
public class AppExceptionHandler {
    Logger logger = LoggerFactory.getLogger(AppExceptionHandler.class);

    @Autowired
    ExceptionRepo exceptionRepo;

    /**
     * This function is used to raise exceptions without logging it on ExceptionsDb
     * @param errorMessage
     */
    public void raiseException(String errorMessage) {
        logger.error("Exception thrown with error message: {}", errorMessage);
        String exceptionId = String.valueOf(Instant.now());
        ExceptionDetails exceptionDetails = new ExceptionDetails(exceptionId, errorMessage);
        throw new ServiceException(errorMessage, exceptionDetails);
    }

    /**
     * This function is used to raise exceptions and log it on ExceptionsDb
     *
     * @param inputMessage
     * @param errorMessage
     */
    public void raiseMainException(String inputMessage, String errorMessage) {
        logger.debug("Exception thrown with error message:{}", errorMessage);
        String exceptionId = String.valueOf(Instant.now());
        ExceptionDetails exceptionDetails = new ExceptionDetails(exceptionId, inputMessage);
        ExceptionEntity exceptionEntity = new ExceptionEntity(inputMessage, errorMessage);
        exceptionRepo.save(exceptionEntity);
        throw new ServiceException(errorMessage, exceptionDetails);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }
}
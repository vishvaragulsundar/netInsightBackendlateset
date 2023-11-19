package com.prodapt.netinsight.exceptionsHandler;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This interface acts as the standard interface for interacting with exception DB
 */
@Component
public interface ExceptionDb {

    public abstract JSONObject addException(ExceptionEntity exception);

    public abstract JSONObject removeException(int exceptionId);

    public abstract List<ExceptionEntity> getAllExceptions();

    public abstract List<ExceptionEntity> getExceptionsFiltered(ExceptionFilter exceptionFilter);
}

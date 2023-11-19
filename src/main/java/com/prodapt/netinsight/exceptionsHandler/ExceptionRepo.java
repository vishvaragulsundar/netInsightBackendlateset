package com.prodapt.netinsight.exceptionsHandler;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * This interface extends JpaRepository and provides the basic CRUD operations on exception DB
 */
@Component
public interface ExceptionRepo extends JpaRepository<ExceptionEntity, Integer> {

    List<ExceptionEntity> findAllByCreationTime(Date creationTime);

    List<ExceptionEntity> findAllByCreationTimeBetween(Date timeStart, Date timeEnd);

    /*
      Returns list of exceptions on a given device name
     */
    @Query(value = "select * from exceptionEntity where inputMessage like %:neName%", nativeQuery = true)
    List<ExceptionEntity> getExceptionsWithNeName(@Param("neName") String neName);
}

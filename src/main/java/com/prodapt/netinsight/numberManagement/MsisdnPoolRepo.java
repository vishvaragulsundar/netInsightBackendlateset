package com.prodapt.netinsight.numberManagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MsisdnPoolRepo extends JpaRepository<MsisdnPool, Long> {

}

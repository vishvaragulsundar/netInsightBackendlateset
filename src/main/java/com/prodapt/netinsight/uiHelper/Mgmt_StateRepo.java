package com.prodapt.netinsight.uiHelper;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Mgmt_StateRepo extends JpaRepository<Mgmt_State, String> {
    @Query(value = "SELECT * FROM mgmt_state ms WHERE ms.\"type\" = 'administrativestate'", nativeQuery = true)
    List<Mgmt_State> findAllByAdministrativeState();

    @Query(value = "SELECT * FROM mgmt_state ms WHERE ms.\"type\" = 'usagestate'", nativeQuery = true)
    List<Mgmt_State> findAllByUsageState();


    @Query(value = "SELECT * FROM mgmt_state ms WHERE ms.\"type\" = 'operationalstate'", nativeQuery = true)
    List<Mgmt_State> findAllByOperationalState();


}

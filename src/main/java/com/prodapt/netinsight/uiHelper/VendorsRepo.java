package com.prodapt.netinsight.uiHelper;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorsRepo extends JpaRepository<Vendors, String> {

}

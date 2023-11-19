package com.prodapt.netinsight.uiHelper;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObjectCountRepo extends JpaRepository<ObjectCount, String> {

    List<ObjectCount> findByObjectTypeIn(List<String> objectTypes);
}

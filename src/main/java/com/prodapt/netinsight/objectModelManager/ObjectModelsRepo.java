package com.prodapt.netinsight.objectModelManager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObjectModelsRepo extends JpaRepository<ObjectModel, Integer> {

    public ObjectModel findByObjectModelNameAndLabelName(ObjectModelName objectModelName, String labelName);

    List<ObjectModel> findByObjectModelName(ObjectModelName objectModelName);

}

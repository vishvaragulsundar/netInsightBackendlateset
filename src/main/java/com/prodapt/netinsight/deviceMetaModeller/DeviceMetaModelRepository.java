package com.prodapt.netinsight.deviceMetaModeller;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface DeviceMetaModelRepository extends Neo4jRepository<DeviceMetaModel, Long> {

    @Query("MERGE (d:DeviceMetaModel {deviceModel: $deviceModel})\n" +
            "ON CREATE SET d.partNumber = $partNumber, d.vendor = $vendor, d.height = $height, d.depth = $depth, " +
            "d.width = $width, d.shelvesContained = $shelvesContained, d.title = $deviceModel,d.numOfRackPositionOccupied=$numOfRackPositionOccupied, d.allowedCardList = $allowedCardList\n" +
            "RETURN d\n")
    public DeviceMetaModel createDeviceMetaModel(@Param("deviceModel") String deviceModel,
                                                 @Param("partNumber") String partNumber,
                                                 @Param("vendor") String vendor,
                                                 @Param("height") float height,
                                                 @Param("depth") float depth,
                                                 @Param("width") float width,
                                                 @Param("shelvesContained") int shelvesContained,
                                                 @Param("numOfRackPositionOccupied") int numOfRackPositionOccupied,
                                                 @Param("allowedCardList") ArrayList<String> allowedCardList);

    @Query("MERGE (d:DeviceMetaModel {deviceModel: $deviceModel})\n" +
            "ON CREATE SET d.partNumber = $partNumber, d.vendor = $vendor, d.height = $height, d.depth = $depth, " +
            "d.width = $width, d.shelvesContained = $shelvesContained, d.title = $deviceModel, d.allowedCardList = $allowedCardList\n" +
            "ON MATCH SET d.partNumber = $partNumber, d.vendor = $vendor, d.height = $height, d.depth = $depth, " +
            "d.width = $width, d.shelvesContained = $shelvesContained, d.title = $deviceModel, d.allowedCardList = $allowedCardList, \n" +
            "d.numOfRackPositionOccupied = $numOfRackPositionOccupied RETURN d\n")
    public DeviceMetaModel updateDeviceMetaModel(@Param("deviceModel") String deviceModel,
                                                 @Param("partNumber") String partNumber,
                                                 @Param("vendor") String vendor,
                                                 @Param("height") float height,
                                                 @Param("depth") float depth,
                                                 @Param("width") float width,
                                                 @Param("shelvesContained") int shelvesContained,
                                                 @Param("allowedCardList") ArrayList<String> allowedCardList,
                                                 @Param("numOfRackPositionOccupied") int numOfRackPositionOccupied);

    @Query("match (d:DeviceMetaModel{deviceModel:$deviceModel})  return d")
    public DeviceMetaModel getDeviceMetaModel(@Param("deviceModel") String deviceModel);

    @Query("match (d:DeviceMetaModel{deviceModel:$deviceModel})  detach delete d")
    public DeviceMetaModel deleteDeviceMetaModel(@Param("deviceModel") String deviceModel);

    public DeviceMetaModel findDeviceMetaModelById(@Param("id") Long id);

    @Query("Match (models:DeviceMetaModel) where models.vendor contains $name return models")
    public List<DeviceMetaModel> getByVendorName(@Param("name") String name);
}

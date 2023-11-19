package com.prodapt.netinsight.objectModelManager;

import lombok.Data;

import java.util.ArrayList;

@Data
public class ObjectModelInfo {

    private String objectModelName;
    private ArrayList<PerLabelPojo> labels;
}

package com.prodapt.netinsight.objectModelManager;

public enum ObjectModelName {
    DEVICE("Device"),
    CARD("Card"),
    SHELF("Shelf"),
    PORT("Port"),
    PLUGGABLE("Pluggable"),
    PHYSICAL_CONNECTION("PhysicalConnection"),
    LOGICAL_CONNECTION("LogicalConnection"),
    SERVICE("Service"),
    CUSTOMER("Customer"),
    BUILDING("Building"),
    RACK("Rack");

    private String value;

    ObjectModelName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ObjectModelName fromValue(String value) {
        for (ObjectModelName objectModelName : ObjectModelName.values()) {
            if (objectModelName.getValue().equalsIgnoreCase(value)) {
                return objectModelName;
            }
        }
        System.out.println("Invalid ObjectModelName value: " + value);
        throw new IllegalArgumentException("Invalid ObjectModelName value: " + value);
    }
}
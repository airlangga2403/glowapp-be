package com.skincare.enums;

public enum ActiveStatusEnums {
    ACTIVE (1, "ACTIVE"),
    INACTIVE (0, "INACTIVE");

    private final int code;
    private final String name;

    ActiveStatusEnums(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}

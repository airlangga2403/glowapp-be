package com.skincare.enums;

public enum GenderEnums {
    MALE (1, "MALE"),
    FEMALE (0, "FEMALE");

    private final int code;
    private final String name;

    GenderEnums(int code, String name) {
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

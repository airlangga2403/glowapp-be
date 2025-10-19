package com.skincare.enums;

public enum SkinTypeEnums {
    COMBINATION (1, "COMBINATION"),
    DRY (2, "DRY"),
    NORMAL (3, "NORMAL"),
    OILY (4, "OILY"),
    SENSITIVE (5, "SENSITIVE");

    private final int code;
    private final String name;

    SkinTypeEnums(int code, String name) {
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

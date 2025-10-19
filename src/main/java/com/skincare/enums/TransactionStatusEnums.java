package com.skincare.enums;

public enum TransactionStatusEnums {
    COMPLETED (1, "COMPLETED"),
    ON_PROCESS (2, "ON PROCESS"),
    CANCELLED (3, "CANCELLED");

    private final int code;
    private final String name;

    TransactionStatusEnums(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String fromCode(Integer transactionstatusId) {
        for (TransactionStatusEnums status : TransactionStatusEnums.values()) {
            if (status.getCode() == transactionstatusId) {
                return status.getName();
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}

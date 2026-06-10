package com.dbizz.model;

public enum PaymentStatus {
    PENDING("pending"), COMPLETED("completed");

    private String statusString;

    private PaymentStatus(String statusString) {
        this.statusString = statusString;
    }

    @Override
    public String toString() {
        return statusString;
    }

    public static PaymentStatus toStatus(String statusString) {
        if (statusString.equalsIgnoreCase("pending"))
            return PaymentStatus.PENDING;
        else if (statusString.equalsIgnoreCase("completed"))
            return PaymentStatus.COMPLETED;
        else
            return null;
    }

}

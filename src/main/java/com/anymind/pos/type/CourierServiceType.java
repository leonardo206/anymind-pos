package com.anymind.pos.type;

public enum CourierServiceType {
    YAMATO,
    SAGAWA,
    OTHER;

    public static CourierServiceType fromString(String courier) {
        try {
            return CourierServiceType.valueOf(courier.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid courier: " + courier);
        }
    }
}

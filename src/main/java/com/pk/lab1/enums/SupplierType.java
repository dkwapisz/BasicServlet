package com.pk.lab1.enums;

public enum SupplierType {
    POLISH_POST("Polish Post"),
    DHL("DHL"),
    DPD("DPD"),
    UPS("UPS"),
    INPOST("InPost");

    private final String displayName;

    SupplierType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

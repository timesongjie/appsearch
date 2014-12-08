package com.bbkmobile.iqoo.platform.annotation;

public enum ProjectionType {
    PROPERTY;
    public Boolean isDefault() {
        return this.equals(PROPERTY);
    }
}

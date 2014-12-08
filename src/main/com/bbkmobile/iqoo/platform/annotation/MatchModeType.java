package com.bbkmobile.iqoo.platform.annotation;

import java.util.Map;

import org.hibernate.criterion.MatchMode;

import com.bbkmobile.iqoo.util.ReflectUtil;

public enum MatchModeType {
    EXACT, ID_EQ, ILIKE, IN, IS_EMPTY, IS_NOT_EMPTY, IS_NULL, IS_NOT_NULL, GE, GT, LE, LT, NE, START, END, ANYWHERE;
    public static Map<String, MatchMode> INSTANCES;

    @SuppressWarnings("unchecked")
    public MatchMode getMatchMode() {
        if (INSTANCES == null) {
            INSTANCES = (Map<String, MatchMode>) ReflectUtil
                    .getClassPropertyValue(MatchMode.class, "INSTANCES");
        }
        return INSTANCES.get(this.toString());
    }

    public Boolean isDefault() {
        return this.equals(EXACT);
    }
}

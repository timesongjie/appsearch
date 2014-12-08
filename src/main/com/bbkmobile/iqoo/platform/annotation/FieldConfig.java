package com.bbkmobile.iqoo.platform.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldConfig {
    ProjectionType criterionProjection() default ProjectionType.PROPERTY;

    MatchModeType criterionMatchMode() default MatchModeType.EXACT;

    OrderType criterionOrder() default OrderType.NULL;

    String entityFieldName() default "";
}

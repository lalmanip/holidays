package com.vivance.holidays.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Maps Java boolean fields to MySQL TINYINT(0/1) columns used in holidays_* DDL.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@JdbcTypeCode(SqlTypes.TINYINT)
public @interface TinyIntBoolean {
}

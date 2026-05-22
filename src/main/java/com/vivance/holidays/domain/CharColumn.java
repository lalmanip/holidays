package com.vivance.holidays.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Maps fixed-length CHAR columns in holidays_* DDL (e.g. currency CHAR(3)).
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@JdbcTypeCode(SqlTypes.CHAR)
public @interface CharColumn {
}

package com.yx.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Author by YX, Date on 2019/8/7.
 */
@Target(FIELD)
@Retention(SOURCE)
public @interface BindView {
    int value();
}

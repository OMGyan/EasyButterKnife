package com.yx.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Author by YX, Date on 2019/8/7.
 */
@Target(METHOD)
@Retention(CLASS)
public @interface OnClick {
    int[] value();
}

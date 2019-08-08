package com.yx.easybutterknifelib;

/**
 * Author by YX, Date on 2019/8/7.
 */
public interface IBind<T> {
    void bind(T target);
    void onClick(T target);
}

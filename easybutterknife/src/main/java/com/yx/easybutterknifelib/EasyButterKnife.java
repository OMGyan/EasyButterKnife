package com.yx.easybutterknifelib;

import android.content.Context;

/**
 * Author by YX, Date on 2019/8/7.
 */
public class EasyButterKnife {

    public static void bind(Context context){
        String name = context.getClass().getName() + "_ViewBinding";
        try {
            Class<?> aClass = Class.forName(name);
            IBind iBind = (IBind) aClass.newInstance();
            iBind.bind(context);
            iBind.onClick(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

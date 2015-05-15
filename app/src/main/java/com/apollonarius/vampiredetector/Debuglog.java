package com.apollonarius.vampiredetector;

import android.util.Log;

/**
 *
 * @author Michael Bruno
 *
 */
public class Debuglog {


    public static void d(Object obj, String message){
        if(BuildConfig.DEBUG){
            Log.d(obj.getClass().getSimpleName(), message);
        }
    }
}

package com.tanishqbhatia.truthordare.fragments;

import android.util.Log;

public class FragmentInstance {

    private static int instance = 0;

    public static int getInstance() {
        Log.e("instance", String.valueOf(instance+1));
        return ++instance;
    }
}

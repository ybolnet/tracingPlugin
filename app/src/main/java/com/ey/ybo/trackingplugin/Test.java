package com.ey.ybo.trackingplugin;


import com.ey.ybo.trackingplugin.annotations.Trace;

public class Test {

    @Trace
    public void bob(int b, float c) {
    }

    @Trace
    public void bosb(int b, float c) {
        int v=2;
    }

}
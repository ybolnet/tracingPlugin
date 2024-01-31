package com.ey.ybo.trackingplugin;

import com.ey.ybo.trackingplugin.annotations.ToTrace;

public class Test {

    @ToTrace
    public void bob(int b, float c) {
    }

    @ToTrace
    public void bosb(int b, float c) {
        int v=2;
    }

}
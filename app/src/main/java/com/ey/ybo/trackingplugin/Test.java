package com.ey.ybo.trackingplugin;

import com.ey.ybo.trackingplugin.annotations.TraceWithReturns;

public class Test {

    @TraceWithReturns
    public void bob(int b, float c) {
    }

    @TraceWithReturns
    public void bosb(int b, float c) {
        int v=2;
    }

}
package com.vaadin.tests.widgetset.client;

import com.google.gwt.user.client.ui.Label;
import com.vaadin.client.Profiler;

public class ProfilerCompilationCanary extends Label {
    public ProfilerCompilationCanary() {
        if (Profiler.isEnabled()) {
            setText("Test does not work when profiler is enabled {dummyCode;}");
        } else {
            setText(getCanaryCode());
        }
    }

    /*
     * Finds the native js function for the canaryWithProfiler method and gets a
     * string representation of it, which in most browsers produces the actual
     * method implementation that we want to verify has an empty body.
     */
    private static native String getCanaryCode()
    /*-{
        return @ProfilerCompilationCanary::canaryWithProfiler(*).toString();
    }-*/;

    /*
     * We don't care about running this method, we just want to make sure that
     * the generated implementation is empty.
     */
    public static void canaryWithProfiler() {
        Profiler.enter("canaryWithProfiler");
        Profiler.leave("canaryWithProfiler");
    }
}

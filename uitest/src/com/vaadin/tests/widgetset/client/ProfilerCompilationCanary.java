/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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

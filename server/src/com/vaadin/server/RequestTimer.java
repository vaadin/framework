/*
 * Copyright 2000-2013 Vaadin Ltd.
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

package com.vaadin.server;

import java.io.Serializable;

/**
 * Times the handling of requests and stores the information as an attribute in
 * the request. The timing info is later passed on to the client in the UIDL and
 * the client provides JavaScript API for accessing this data from e.g.
 * TestBench.
 * 
 * @author Jonatan Kronqvist / Vaadin Ltd
 */
public class RequestTimer implements Serializable {
    private long requestStartTime = 0;

    /**
     * Starts the timing of a request. This should be called before any
     * processing of the request.
     */
    public void start() {
        requestStartTime = System.nanoTime();
    }

    /**
     * Stops the timing of a request. This should be called when all processing
     * of a request has finished.
     * 
     * @param context
     */
    public void stop(VaadinSession context) {
        // Measure and store the total handling time. This data can be
        // used in TestBench 3 tests.
        long time = (System.nanoTime() - requestStartTime) / 1000000;

        // The timings must be stored in the context, since a new
        // RequestTimer is created for every request.
        context.setLastRequestDuration(time);
    }
}

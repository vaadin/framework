/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

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
    public void stop(AbstractWebApplicationContext context) {
        // Measure and store the total handling time. This data can be
        // used in TestBench 3 tests.
        long time = (System.nanoTime() - requestStartTime) / 1000000;
        // The timings must be stored in the context, since a new
        // RequestTimer is created for every request.
        context.setLastRequestTime(time);
    }
}

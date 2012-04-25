/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import com.vaadin.terminal.WrappedRequest;

/**
 * Times the handling of requests and stores the information as an attribute in
 * the request. The timing info is later passed on to the client in the UIDL and
 * the client provides JavaScript API for accessing this data from e.g.
 * TestBench.
 * 
 * @author Jonatan Kronqvist / Vaadin Ltd
 */
public class RequestTimer {
    public static final String SESSION_ATTR_ID = "REQUESTTIMER";

    private long requestStartTime = 0;
    private long totalSessionTime = 0;
    private long lastRequestTime = -1;

    /**
     * Starts the timing of a request. This should be called before any
     * processing of the request.
     * 
     * @param request
     *            the request.
     */
    public void start(WrappedRequest request) {
        requestStartTime = System.nanoTime();
        request.setAttribute("TOTAL", totalSessionTime);
        request.setAttribute("LASTREQUEST", lastRequestTime);
    }

    /**
     * Stops the timing of a request. This should be called when all processing
     * of a request has finished.
     */
    public void stop() {
        // Measure and store the total handling time. This data can be
        // used in TestBench 3 tests.
        long time = (System.nanoTime() - requestStartTime) / 1000000;
        lastRequestTime = time;
        totalSessionTime += time;
    }

    /**
     * Returns a valid request timer for the specified request. Timers are
     * session bound.
     * 
     * @param request
     *            the request for which to get a valid timer.
     * @return a valid timer.
     */
    public static RequestTimer get(WrappedRequest request) {
        RequestTimer timer = (RequestTimer) request
                .getSessionAttribute(SESSION_ATTR_ID);
        if (timer == null) {
            timer = new RequestTimer();
        }
        return timer;
    }

    /**
     * Associates the specified request timer with the specified request. Since
     * {@link #get(RequestWrapper)} will, at one point or another, return a new
     * instance, this method should be called to keep the request <-> timer
     * association updated.
     * 
     * @param request
     *            the request for which to set the timer.
     * @param requestTimer
     *            the timer.
     */
    public static void set(WrappedRequest request, RequestTimer requestTimer) {
        request.setSessionAttribute(RequestTimer.SESSION_ATTR_ID, requestTimer);
    }
}

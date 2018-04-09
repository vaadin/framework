package com.vaadin.tests.push;

import com.vaadin.server.VaadinRequest;

public abstract class ExtremelyLongPushTime extends PushLargeData {

    private static final int DURATION_MS = 48 * 60 * 60 * 1000; // 48 H
    private static int INTERVAL_MS = 60 * 1000; // 1 minute
    private static int PAYLOAD_SIZE = 100 * 1024; // 100 KB

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        super.setup(request);
        duration.setConvertedValue(DURATION_MS);
        interval.setConvertedValue(INTERVAL_MS);
        dataSize.setConvertedValue(PAYLOAD_SIZE);
    }

    @Override
    protected String getTestDescription() {
        return "Test which pushes data every minute for 48 hours";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}

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

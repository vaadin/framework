/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.server.component.upload;

import org.junit.Test;

import com.vaadin.server.StreamVariable.StreamingProgressEvent;
import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.FinishedListener;
import com.vaadin.ui.Upload.ProgressListener;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

public class UploadListenersTest extends AbstractListenerMethodsTestBase {

    @Test
    public void testProgressListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Upload.class, StreamingProgressEvent.class,
                ProgressListener.class);
    }

    @Test
    public void testSucceededListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Upload.class, SucceededEvent.class,
                SucceededListener.class);
    }

    @Test
    public void testStartedListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Upload.class, StartedEvent.class,
                StartedListener.class);
    }

    @Test
    public void testFailedListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Upload.class, FailedEvent.class,
                FailedListener.class);
    }

    @Test
    public void testFinishedListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Upload.class, FinishedEvent.class,
                FinishedListener.class);
    }
}

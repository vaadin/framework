package com.vaadin.tests.server.component.upload;

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
    public void testProgressListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Upload.class, StreamingProgressEvent.class,
                ProgressListener.class);
    }

    public void testSucceededListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Upload.class, SucceededEvent.class,
                SucceededListener.class);
    }

    public void testStartedListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Upload.class, StartedEvent.class,
                StartedListener.class);
    }

    public void testFailedListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Upload.class, FailedEvent.class,
                FailedListener.class);
    }

    public void testFinishedListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Upload.class, FinishedEvent.class,
                FinishedListener.class);
    }
}

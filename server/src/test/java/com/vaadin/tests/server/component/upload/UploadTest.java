package com.vaadin.tests.server.component.upload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.server.StreamVariable;
import com.vaadin.server.StreamVariable.StreamingErrorEvent;
import com.vaadin.shared.ui.upload.UploadState;
import com.vaadin.ui.Upload;

public class UploadTest {

    @Test
    public void getStreamVariable_streamingFailed_endUploadIsCalled() {
        TestUpload upload = new TestUpload();
        upload.startUpload();
        StreamVariable variable = upload.getStreamVariable();
        try {
            variable.streamingFailed(new TestStreamingErrorEvent());
        } catch (Exception e) {
        }
        assertFalse(upload.isUploading());
    }

    @Test
    public void setImmediateMode_defaultTrue() {
        Upload upload = new Upload();

        assertTrue("Upload should be in immediate mode by default",
                upload.isImmediateMode());
    }

    @Test
    public void getState_uploadHasCustomState() {
        TestUpload upload = new TestUpload();
        UploadState state = upload.getState();
        assertEquals("Unexpected state class", UploadState.class,
                state.getClass());
    }

    @Test
    public void getPrimaryStyleName_uploadHasCustomPrimaryStyleName() {
        Upload upload = new Upload();
        UploadState state = new UploadState();
        assertEquals("Unexpected primary style name", state.primaryStyleName,
                upload.getPrimaryStyleName());
    }

    @Test
    public void uploadStateHasCustomPrimaryStyleName() {
        UploadState state = new UploadState();
        assertEquals("Unexpected primary style name", "v-upload",
                state.primaryStyleName);
    }

    private static class TestStreamingErrorEvent
            implements StreamingErrorEvent {

        @Override
        public String getFileName() {
            return null;
        }

        @Override
        public String getMimeType() {
            return null;
        }

        @Override
        public long getContentLength() {
            return 0;
        }

        @Override
        public long getBytesReceived() {
            return 0;
        }

        @Override
        public Exception getException() {
            return new Exception();
        }

    }

    private static class TestUpload extends Upload {

        @Override
        public StreamVariable getStreamVariable() {
            return super.getStreamVariable();
        }

        @Override
        public UploadState getState() {
            return super.getState();
        }

        @Override
        protected void fireNoInputStream(String filename, String MIMEType,
                long length) {
            fireEvent();
        }

        @Override
        protected void fireNoOutputStream(String filename, String MIMEType,
                long length) {
            fireEvent();
        }

        @Override
        protected void fireUploadInterrupted(String filename, String MIMEType,
                long length, Exception e) {
            fireEvent();
        }

        private void fireEvent() {
            throw new NullPointerException();
        }
    }
}

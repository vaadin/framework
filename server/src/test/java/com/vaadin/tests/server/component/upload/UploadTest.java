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

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.server.StreamVariable;
import com.vaadin.server.StreamVariable.StreamingErrorEvent;
import com.vaadin.shared.ui.upload.UploadState;
import com.vaadin.ui.Upload;

/**
 *
 * @author Vaadin Ltd
 */
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
        Assert.assertFalse(upload.isUploading());
    }

    @Test
    public void setImmediateMode_defaultTrue() {
        Upload upload = new Upload();

        Assert.assertTrue("Upload should be in immediate mode by default",
                upload.isImmediateMode());
    }

    @Test
    public void getState_uploadHasCustomState() {
        TestUpload upload = new TestUpload();
        UploadState state = upload.getState();
        Assert.assertEquals("Unexpected state class", UploadState.class,
                state.getClass());
    }

    @Test
    public void getPrimaryStyleName_uploadHasCustomPrimaryStyleName() {
        Upload upload = new Upload();
        UploadState state = new UploadState();
        Assert.assertEquals("Unexpected primary style name",
                state.primaryStyleName, upload.getPrimaryStyleName());
    }

    @Test
    public void uploadStateHasCustomPrimaryStyleName() {
        UploadState state = new UploadState();
        Assert.assertEquals("Unexpected primary style name", "v-upload",
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

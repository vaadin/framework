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
package com.vaadin.tests.components.upload;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.Receiver;

public class TestUploadFilename extends TestBase {

    private Label result = new Label("Waiting for upload");
    private UploadReceiver receiver = new UploadReceiver();

    @Override
    protected void setup() {

        Upload upload = new Upload("Upload a file", receiver);

        addComponent(upload);
        addComponent(result);

        upload.addFinishedListener(new Upload.FinishedListener() {
            @Override
            public void uploadFinished(FinishedEvent event) {
                result.setValue("Got file (should not contain path): "
                        + receiver.getFilename());
            }
        });

    }

    public static class UploadReceiver implements Receiver {

        private String filename;

        @Override
        public OutputStream receiveUpload(String filename, String MIMEType) {
            this.filename = filename;
            return new ByteArrayOutputStream();
        }

        public String getFilename() {
            return filename;
        }

    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}

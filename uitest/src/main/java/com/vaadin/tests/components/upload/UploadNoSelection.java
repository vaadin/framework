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
package com.vaadin.tests.components.upload;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.Receiver;

public class UploadNoSelection extends AbstractTestUIWithLog implements
        Receiver {

    static final String LOG_ID_PREFIX = "Log_row_";
    static final String UPLOAD_ID = "u";

    static final String UPLOAD_FINISHED = "Upload Finished";
    static final String RECEIVING_UPLOAD = "Receiving upload";

    static final String FILE_LENGTH_PREFIX = "File length:";
    static final String FILE_NAME_PREFIX = "File name:";

    @Override
    protected Integer getTicketNumber() {
        return 9602;
    }

    @Override
    protected String getTestDescription() {
        return "Uploading an empty selection (no file) will trigger FinishedEvent with 0-length file size and empty filename.";
    }

    @Override
    protected void setup(VaadinRequest request) {
        Upload u = new Upload("Upload", this);
        u.setId(UPLOAD_ID);
        u.setSizeUndefined();

        addComponent(u);

        u.addFinishedListener(new Upload.FinishedListener() {
            @Override
            public void uploadFinished(FinishedEvent event) {
                log(UPLOAD_FINISHED);
                log(FILE_LENGTH_PREFIX + " " + event.getLength());
                log(FILE_NAME_PREFIX + " " + event.getFilename());
            }
        });
        u.addFailedListener(new Upload.FailedListener() {

            @Override
            public void uploadFailed(FailedEvent event) {
                log("Upload Failed");
                log(FILE_LENGTH_PREFIX + " " + event.getLength());
                log(FILE_NAME_PREFIX + " " + event.getFilename());
            }
        });
    }

    @Override
    public OutputStream receiveUpload(String filename, String MIMEType) {
        log(RECEIVING_UPLOAD);
        return new ByteArrayOutputStream();
    }

}

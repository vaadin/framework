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
package com.vaadin.v7.tests.components.upload;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.apache.commons.codec.digest.DigestUtils;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.v7.ui.Upload;
import com.vaadin.v7.ui.Upload.FailedEvent;
import com.vaadin.v7.ui.Upload.FailedListener;
import com.vaadin.v7.ui.Upload.Receiver;
import com.vaadin.v7.ui.Upload.SucceededEvent;
import com.vaadin.v7.ui.Upload.SucceededListener;

public class TestFileUpload extends TestBase implements Receiver {

    private Log log = new Log(5);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    @Override
    protected void setup() {
        Upload u = new Upload("Upload", new Upload.Receiver() {

            @Override
            public OutputStream receiveUpload(String filename,
                    String mimeType) {
                return baos;
            }
        });
        u.setId("UPL");
        u.addFailedListener(new FailedListener() {

            @Override
            public void uploadFailed(FailedEvent event) {
                String hash = DigestUtils.md5Hex(baos.toByteArray());

                log.log("<span style=\"color: red;\">Upload failed. Name: "
                        + event.getFilename() + ", Size: " + baos.size()
                        + ", md5: " + hash + "</span>");
                baos.reset();
            }
        });
        u.addSucceededListener(new SucceededListener() {

            @Override
            public void uploadSucceeded(SucceededEvent event) {
                String hash = DigestUtils.md5Hex(baos.toByteArray());
                log.log("Upload finished. Name: " + event.getFilename()
                        + ", Size: " + baos.size() + ", md5: " + hash);
                baos.reset();
            }
        });

        addComponent(log);
        addComponent(u);
    }

    @Override
    public OutputStream receiveUpload(String filename, String MIMEType) {
        getMainWindow().showNotification("Receiving upload");
        return new ByteArrayOutputStream();
    }

    @Override
    protected Integer getTicketNumber() {
        return 6465;
    }

    @Override
    protected String getDescription() {
        return "Creates and prints an MD5 hash of any uploaded file.";
    }

}

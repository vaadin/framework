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
import java.util.ArrayList;
import java.util.List;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;

public class TestUploadAndDisableOnSuccess extends ComponentTestCase<Upload>
        implements Receiver {

    @Override
    protected String getTestDescription() {
        return "If upload is detached and attached during upload, the client side componenent never receives information that the upload has finished. Second update will not be successful.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4605;
    }

    @Override
    protected Class<Upload> getTestClass() {
        return Upload.class;
    }

    int counter = 0;
    private Label l;

    @Override
    protected void initializeComponents() {

        final Label labe = new Label();

        addComponent(labe);

        final Upload u;
        u = new Upload(null, this);
        u.setImmediateMode(true);
        addTestComponent(u);

        l = new Label(getUploadcount());
        addComponent(l);

        u.addStartedListener(new Upload.StartedListener() {

            @Override
            public void uploadStarted(StartedEvent event) {
                /*
                 * Remove component before upload from the same vertical layout.
                 * Causes upload to be detached/attached -> upload loses it
                 * target iframes onload listener -> puts VUpload inappropriate
                 * state.
                 */
                getLayout().removeComponent(labe);
            }
        });

        u.addFinishedListener(new Upload.FinishedListener() {
            @Override
            public void uploadFinished(FinishedEvent event) {
                Notification.show("Done");
                l.setValue(getUploadcount());
            }
        });

    }

    private String getUploadcount() {
        return counter++ + " uploads";
    }

    @Override
    protected List<Component> createActions() {
        List<Component> actions = new ArrayList<>();
        actions.add(createButtonAction("Toggle Enabled",
                new Command<Upload, Boolean>() {

                    @Override
                    public void execute(Upload c, Boolean value, Object data) {
                        c.setEnabled(!c.isEnabled());
                    }
                }));

        return actions;
    }

    @Override
    public OutputStream receiveUpload(String filename, String MIMEType) {
        // sleep to ensure change before upload is complete
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new ByteArrayOutputStream();
    }

}

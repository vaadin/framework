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
import java.util.Collections;
import java.util.List;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;

public class TestImmediateUploadInFormLayout
        extends ComponentTestCase<FormLayout> implements Receiver {

    @Override
    protected String getTestDescription() {
        return "On Firefox 3.5 and Opera 10.10, clicking on an immediate upload in a wide FormLayout has no effect";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4359;
    }

    @Override
    protected Class<FormLayout> getTestClass() {
        return FormLayout.class;
    }

    @Override
    protected void initializeComponents() {

        FormLayout formLayout = new FormLayout();
        formLayout.setWidth("100%");
        Upload u = new Upload("Upload in FormLayout", this);
        u.setImmediateMode(true);
        formLayout.addComponent(u);
        addTestComponent(formLayout);

    }

    @Override
    protected List<Component> createActions() {
        return Collections.emptyList();
    }

    @Override
    public OutputStream receiveUpload(String filename, String MIMEType) {
        Notification.show("Receiving upload");
        return new ByteArrayOutputStream();
    }
}

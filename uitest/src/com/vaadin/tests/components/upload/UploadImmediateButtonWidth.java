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

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

public class UploadImmediateButtonWidth extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout lt = new VerticalLayout();
        lt.setWidth("500px");

        Upload upload1 = new Upload();
        upload1.setImmediate(true);
        upload1.setId("upload1");
        upload1.setWidth("300px");
        lt.addComponent(upload1);

        Upload upload2 = new Upload();
        upload2.setImmediate(true);
        upload2.setWidth("50%");
        upload2.setId("upload2");
        lt.addComponent(upload2);

        addComponent(lt);
    }

    @Override
    protected String getTestDescription() {
        return "Width of immediate upload button should obey setWidth()";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14485;
    }

}

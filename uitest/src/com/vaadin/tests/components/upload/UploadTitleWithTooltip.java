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

/**
 * Test UI for browser-dependent tootlip for Upload component.
 * 
 * @author Vaadin Ltd
 */
public class UploadTitleWithTooltip extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Upload upload = new Upload();
        upload.setDescription("tootlip");

        addComponent(upload);
    }

    @Override
    protected String getTestDescription() {
        return "Browser dependent title should not be visible for upload if Vaadin tooltip is used";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14482;
    }

}

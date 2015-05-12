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
package com.vaadin.tests.debug;

import org.atmosphere.util.Version;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;

/**
 * Test UI for PUSH version string in debug window.
 * 
 * @author Vaadin Ltd
 */
public class PushVersionInfo extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        if (request.getParameter("enablePush") != null) {
            getPushConfiguration().setPushMode(PushMode.AUTOMATIC);
            Label label = new Label(Version.getRawVersion());
            label.addStyleName("atmosphere-version");
            addComponent(label);
        }
    }

    @Override
    public String getDescription() {
        return "Debug window shows Push version in info Tab.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14904;
    }
}

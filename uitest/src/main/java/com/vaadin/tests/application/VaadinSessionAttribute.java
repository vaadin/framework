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

package com.vaadin.tests.application;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;

public class VaadinSessionAttribute extends AbstractTestUI {

    private static final String ATTR_NAME = "myAttribute";

    @Override
    protected void setup(VaadinRequest request) {
        getSession().setAttribute(ATTR_NAME, Integer.valueOf(42));
        getSession().setAttribute(Integer.class, Integer.valueOf(42 * 2));

        addComponent(new Button("Show attribute values",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        Notification notification = new Notification(
                                getSession().getAttribute(ATTR_NAME)
                                        + " & "
                                        + getSession().getAttribute(
                                                Integer.class));
                        notification.setDelayMsec(Notification.DELAY_FOREVER);
                        notification.show(getPage());
                    }
                }));
    }

    @Override
    protected String getTestDescription() {
        return "Test to verify that session attributes are saved between requests.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(9514);
    }

}

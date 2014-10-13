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
package com.vaadin.tests.applicationservlet;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class SessionExpiration extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        getSession().getSession().setMaxInactiveInterval(2);
        addButton("Click to avoid expiration", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                log("Clicked");
            }
        });
    }

    @Override
    protected String getTestDescription() {
        return "Test for what happens when the session expires (2 second expiration time).";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12139;
    }
}

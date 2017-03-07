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
package com.vaadin.tests.application;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Label;

@PreserveOnRefresh
public class RefreshStatePreserve extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        // Internal parameter sent by vaadinBootstrap.js,
        addComponent(new Label("window.name: " + request.getParameter("v-wn")));
        addComponent(new Label("UI id: " + getUIId()));

        log("Initial fragment: " + getPage().getUriFragment());
        getPage().addUriFragmentChangedListener(
                new UriFragmentChangedListener() {
                    @Override
                    public void uriFragmentChanged(
                            UriFragmentChangedEvent event) {
                        log("Fragment changed to " + event.getUriFragment());
                    }
                });
    }

    @Override
    protected String getTestDescription() {
        return "Refreshing the browser window should preserve the state";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(8068);
    }
}

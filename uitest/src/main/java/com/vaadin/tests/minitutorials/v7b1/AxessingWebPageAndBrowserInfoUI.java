/*
 * Copyright 2012 Vaadin Ltd.
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
package com.vaadin.tests.minitutorials.v7b1;

import com.vaadin.server.Page;
import com.vaadin.server.Page.BrowserWindowResizeEvent;
import com.vaadin.server.Page.BrowserWindowResizeListener;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

public class AxessingWebPageAndBrowserInfoUI extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        UI someUI = this;

        Page page = someUI.getPage();
        page.addBrowserWindowResizeListener(new BrowserWindowResizeListener() {
            @Override
            public void browserWindowResized(BrowserWindowResizeEvent event) {
                Notification.show("Window width=" + event.getWidth()
                        + ", height=" + event.getHeight());
            }
        });

        page.setUriFragment(page.getUriFragment() + "foo");
        page.addUriFragmentChangedListener(new UriFragmentChangedListener() {
            @Override
            public void uriFragmentChanged(UriFragmentChangedEvent event) {
                Notification.show("Fragment=" + event.getUriFragment());
            }
        });
    }

    @Override
    protected String getTestDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}

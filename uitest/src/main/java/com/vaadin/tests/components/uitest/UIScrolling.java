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
package com.vaadin.tests.components.uitest;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

public class UIScrolling extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        // Set layout to high enough to get scroll.
        getLayout().setHeight("2250px");
        addComponent(new Button("scroll to 1000px", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                UI.getCurrent().setScrollTop(1000);
            }
        }));
        addComponent(new Button(
                "This button is halfway down. Click to report scroll position.",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        Notification.show("Scrolled to "
                                + event.getButton().getUI().getScrollTop()
                                + " px");
                    }
                }));
    }

    @Override
    protected String getTestDescription() {
        return "Windows can be programmatically scrolled";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9952;
    }

}

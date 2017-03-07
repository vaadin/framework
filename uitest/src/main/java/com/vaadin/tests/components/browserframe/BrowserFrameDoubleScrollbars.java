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
package com.vaadin.tests.components.browserframe;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.TabSheet;

public class BrowserFrameDoubleScrollbars extends AbstractReindeerTestUI {

    @Override
    protected Integer getTicketNumber() {
        return 11780;
    }

    @Override
    protected void setup(VaadinRequest request) {

        getLayout().setHeight("100%");
        getLayout().setSizeFull();
        getLayout().getParent().setSizeFull();

        TabSheet tabs = new TabSheet();
        tabs.setSizeFull();
        getLayout().addComponent(tabs);

        BrowserFrame help = new BrowserFrame();
        help.setSizeFull();
        help.setSource(new ExternalResource("/statictestfiles/long-html.htm"));

        tabs.addComponent(help);

    }

    @Override
    protected String getTestDescription() {
        return "Embedded browser causes second scrollbar";
    }

}

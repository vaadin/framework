/*
 * Copyright 2000-2020 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components.window;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 *
 * @since 7.1.9
 * @author Vaadin Ltd
 */
public class ScrollingBodyElementWithModalOpened extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setHeight("10000px");

        Window window = new Window("Caption");

        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("300px");
        layout.setHeight("300px");
        window.setContent(layout);

        addWindow(window);

        window.setModal(true);

        addComponent(verticalLayout);
    }

    @Override
    protected String getTestDescription() {
        return "Screen must not scroll with modal opened.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12899;
    }
}

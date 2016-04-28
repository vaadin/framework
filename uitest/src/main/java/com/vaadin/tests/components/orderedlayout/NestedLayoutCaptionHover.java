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
package com.vaadin.tests.components.orderedlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.VerticalLayout;

/**
 * Test hovering over nested layout caption
 * 
 * @author Vaadin Ltd
 */
public class NestedLayoutCaptionHover extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout test = new VerticalLayout();
        test.setCaption("inner layout");
        addComponent(new VerticalLayout(new VerticalLayout(new VerticalLayout(
                test))));
    }

    @Override
    protected String getTestDescription() {
        return "Hovering over nested layout caption should not freeze the browser";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12469;
    }
}

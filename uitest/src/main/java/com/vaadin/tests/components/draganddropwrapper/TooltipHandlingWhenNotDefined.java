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
package com.vaadin.tests.components.draganddropwrapper;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.Label;

public class TooltipHandlingWhenNotDefined extends TestBase {

    @Override
    protected void setup() {

        CssLayout wrapperLayout = new CssLayout();
        wrapperLayout.setWidth("100%");

        Label label = new Label("Can I has the tooltip?", ContentMode.HTML);
        label.setId("tooltipLabel");
        label.setDescription("Good! Tooltip works!");
        label.setSizeUndefined();
        wrapperLayout.addComponent(label);

        DragAndDropWrapper wrapper = new DragAndDropWrapper(wrapperLayout);
        wrapper.setWidth("100%");
        wrapper.setDragStartMode(DragStartMode.WRAPPER);

        addComponent(wrapper);

    }

    @Override
    protected String getDescription() {
        return "Wrapper most not prevent child from showing tooltip";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7766;
    }

}

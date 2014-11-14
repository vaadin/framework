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

package com.vaadin.tests.components.layout;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class EmptySpaceOnPageAfterExpandedComponent extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        getLayout().setHeight("200px");

        VerticalLayout container = new VerticalLayout();
        container.setStyleName("mystyle");
        container.setId("container");
        container.setSpacing(true);
        container.setSizeFull();
        addComponent(container);

        Page.getCurrent().getStyles()
                .add(".mystyle {border: 1px solid black;}");

        GridLayout grid = new GridLayout();
        grid.setSpacing(true);

        TextField text1 = new TextField();
        text1.setCaption("Text1");
        text1.setRequired(true);

        grid.setColumns(1);
        grid.setRows(1);

        grid.addComponent(text1);

        grid.setSizeUndefined();

        Panel panel = new Panel();
        panel.setContent(grid);

        panel.setSizeUndefined();

        container.addComponent(panel);

        TextArea expand = new TextArea();
        expand.setId("expandedElement");
        expand.setSizeFull();
        container.addComponent(expand);

        container.setExpandRatio(expand, 1);
    }

    @Override
    protected String getTestDescription() {
        return "Height calculation should be correct in Chrome. There should not be any empty space after expanded component.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12672;
    }
}
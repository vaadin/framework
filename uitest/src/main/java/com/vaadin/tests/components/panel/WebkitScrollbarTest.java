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

package com.vaadin.tests.components.panel;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class WebkitScrollbarTest extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Panel panel = new Panel();

        VerticalLayout content = new VerticalLayout();
        panel.setContent(content);

        GridLayout gridLayout = new GridLayout();
        gridLayout.setHeight(null);
        gridLayout.setWidth(100, Unit.PERCENTAGE);
        content.addComponent(gridLayout);

        ListSelect listSelect = new ListSelect();

        listSelect.setWidth(100, Unit.PERCENTAGE);
        listSelect.setHeight(300, Unit.PIXELS);

        gridLayout.addComponent(listSelect);

        gridLayout.setMargin(true);

        setContent(panel);
    }

    @Override
    protected String getTestDescription() {
        return "When opening the window, it should NOT contain a horizontal";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12727;
    }

}

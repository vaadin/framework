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

package com.vaadin.tests.components.draganddropwrapper;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;

public class DragAndDropWrapperInPanel extends TestBase {

    @Override
    protected void setup() {

        addComponent(new Button("Click to resize", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                for (int i = 1; i < getLayout().getComponentCount(); ++i) {
                    Component c = getLayout().getComponent(i);
                    c.setWidth("400px");
                    c.setHeight("200px");
                }
            }
        }));

        Component content;

        content = new Button("Undefined-sized Button");
        content.setSizeUndefined();
        addDnDPanel(content);

        content = new Label("Full-sized Label");
        content.setSizeFull();
        addDnDPanel(content);

        content = new TextArea(null, "200x100px TextArea");
        content.setWidth("200px");
        content.setHeight("100px");
        addDnDPanel(content);
    }

    @Override
    protected String getDescription() {
        return "A full-sized DragAndDropWrapper causes scrollbars inside Panel";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6880;
    }

    private void addDnDPanel(Component content) {
        Panel panel = new Panel();
        panel.setSizeUndefined();
        panel.setWidth("300px");
        panel.setHeight("150px");
        DragAndDropWrapper dndWrapper = new DragAndDropWrapper(content);
        dndWrapper.setSizeFull();
        panel.setContent(dndWrapper);
        addComponent(panel);
    }
}

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
package com.vaadin.tests.components.tabsheet;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;

/**
 * Test UI to check that TabsheetBaseConnector reacts on disabling its parent.
 * 
 * @author Vaadin Ltd
 */
public class TabSheetInDisabledParent extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final HorizontalLayout layout = new HorizontalLayout();
        addComponent(new Button("toggle", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                layout.setEnabled(!layout.isEnabled());
            }
        }));
        addComponent(layout);

        TabSheet sheet = new TabSheet();
        Label label1 = new Label("Label1");
        label1.setCaption("Label 1");
        sheet.addComponent(label1);

        Label label2 = new Label("Label2");
        label2.setCaption("Label 2");
        sheet.addComponent(label2);

        Label label3 = new Label("Label3");
        label3.setCaption("Label 3");
        sheet.addComponent(label3);

        layout.addComponent(sheet);
    }

    @Override
    protected String getTestDescription() {
        return "VTabsheetBase widget should implement HasEnabled interface.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14114;
    }

}

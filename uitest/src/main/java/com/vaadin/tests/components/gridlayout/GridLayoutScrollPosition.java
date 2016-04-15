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
package com.vaadin.tests.components.gridlayout;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

public class GridLayoutScrollPosition extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        Panel panel = new Panel();
        setContent(panel);

        GridLayout gridLayout = new GridLayout();
        gridLayout.setWidth("500px");
        panel.setContent(gridLayout);
        gridLayout.setColumns(1);
        gridLayout.setRows(1);

        Label dummyLabel = new Label("Dummy");
        dummyLabel.setHeight("500px");
        gridLayout.addComponent(dummyLabel);

        final CheckBox visibilityToggleCheckBox = new CheckBox(
                "Hide / Show toggleable components");
        visibilityToggleCheckBox.setId("visibility-toggle");
        visibilityToggleCheckBox.setHeight("2000px");
        visibilityToggleCheckBox.setImmediate(true);
        visibilityToggleCheckBox.setValue(false); // Initially unchecked
        gridLayout.addComponent(visibilityToggleCheckBox);

        final Label toggleableLabel = new Label("Toggleable Label");
        toggleableLabel.setHeight("2000px");
        toggleableLabel.setVisible(false); // Initially hidden
        gridLayout.addComponent(toggleableLabel);

        visibilityToggleCheckBox
                .addValueChangeListener(new ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        toggleableLabel.setVisible(visibilityToggleCheckBox
                                .getValue());
                    }
                });

    }

    @Override
    protected String getTestDescription() {
        return "The UI scroll position should not be reset when visibility of GridLayout children is toggled";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13386;
    }
}
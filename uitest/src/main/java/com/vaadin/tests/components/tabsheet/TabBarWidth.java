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
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

/**
 * Tests the width of the tab bar, especially when using relative width for the
 * {@link TabSheet}.
 * 
 * Created for ticket <a href="http://dev.vaadin.com/ticket/12805">#12805</a>.
 */
public class TabBarWidth extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setWidth("500px");
        setContent(layout);

        // Add a Button to toggle between the widths and undefined.
        Button toggle = new Button("Toggle widths", new Button.ClickListener() {

            private boolean removeWidths = true;

            @Override
            public void buttonClick(ClickEvent event) {
                restoreOrRemoveWidths(layout, removeWidths);
                removeWidths = !removeWidths;
            }
        });
        toggle.setId("toggleWidths");
        layout.addComponent(toggle);

        // Add TabSheets with different widths specified.
        layout.addComponent(newTabSheet(null));
        layout.addComponent(newTabSheet("100%"));
        layout.addComponent(newTabSheet("75%"));
        layout.addComponent(newTabSheet("50%"));
        layout.addComponent(newTabSheet("150px"));
    }

    @Override
    protected String getTestDescription() {
        return "Tests the width of the tab bar.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12805;
    }

    private void restoreOrRemoveWidths(VerticalLayout layout,
            boolean removeWidths) {
        for (Component component : layout) {
            if (component instanceof TabSheet) {
                if (removeWidths) {
                    component.setWidth(null);
                    component.setCaption("Width: undefined");
                } else {
                    String originalWidth = (String) ((TabSheet) component)
                            .getData();
                    component.setWidth(originalWidth);
                    component.setCaption("Width: "
                            + (originalWidth == null ? "undefined"
                                    : originalWidth));
                }
            }
        }
    }

    private TabSheet newTabSheet(String width) {
        TabSheet tabSheet = new TabSheet();
        tabSheet.setCaption("Width: " + (width == null ? "undefined" : width));
        tabSheet.setWidth(width);
        tabSheet.setData(width);

        // Add dummy components to fill the TabSheet.
        for (int i = 1; i <= 10; i++) {
            tabSheet.addComponent(new Button(i + ". tab"));
        }
        return tabSheet;
    }
}

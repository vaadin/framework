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

package com.vaadin.tests.components.orderedlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.util.TestUtils;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class ExpandChangeReattach extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setHeight(null);

        Table table = new Table("Table", TestUtils.getISO3166Container());
        verticalLayout.addComponent(table);
        verticalLayout.addComponent(new Button("Toggle expand logic",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        if (verticalLayout.getHeight() == -1) {
                            verticalLayout.setHeight("900px");
                        } else {
                            verticalLayout.setHeight(null);
                        }
                    }
                }));

        addComponent(verticalLayout);
    }

    @Override
    protected String getTestDescription() {
        return "Table should not forget its scroll position when it is temporarily detached from the DOM because an ordered layout changes expand modes.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(10489);
    }

}

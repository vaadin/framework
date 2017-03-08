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
package com.vaadin.tests.components.splitpanel;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;

public class SplitPanelSwapComponents extends TestBase {

    @Override
    protected void setup() {
        final HorizontalSplitPanel hsplit = new HorizontalSplitPanel();
        hsplit.setWidth("300px");
        hsplit.setHeight("300px");
        hsplit.setSecondComponent(new Label("A label"));
        hsplit.setFirstComponent(new Label("Another label"));
        getLayout().addComponent(hsplit);

        Button swap = new Button("Swap components", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Component first = hsplit.getFirstComponent();
                hsplit.removeComponent(first);

                Component second = hsplit.getSecondComponent();
                hsplit.removeComponent(second);

                hsplit.setFirstComponent(second);
                hsplit.setSecondComponent(first);
            }
        });

        getLayout().addComponent(swap);

    }

    @Override
    protected String getDescription() {
        return "Swapping components should work";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6171;
    }

}

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
package com.vaadin.tests.components.orderedlayout;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;

public class ReplaceComponentNPE extends TestBase {

    @Override
    protected String getDescription() {
        return "Clicking 'ReplaceComponent' should replace the 'Button' button with a VericalLayout, and move the button inside the verticalLayout. Visually this can be seen by the added margins of the VerticalLayout.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3195;
    }

    final Button button = new Button("Button");
    final VerticalLayout outer = new VerticalLayout();

    @Override
    protected void setup() {
        outer.setMargin(true);

        Button changer = new Button("ReplaceComponent");
        changer.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                getLayout().replaceComponent(button, outer);
                outer.addComponent(button);
            }
        });

        getLayout().addComponent(button);
        getLayout().addComponent(changer);

    }

}

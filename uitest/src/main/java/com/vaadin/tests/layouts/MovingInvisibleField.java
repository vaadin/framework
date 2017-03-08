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
package com.vaadin.tests.layouts;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.TextField;

@SuppressWarnings("serial")
public class MovingInvisibleField extends TestBase {

    @Override
    protected void setup() {
        final VerticalLayout layout1 = new VerticalLayout();
        final VerticalLayout layout2 = new VerticalLayout();

        final TextField tfHidden = new TextField("Hidden text field caption",
                "A hidden text field");
        final TextField tfVisible = new TextField("Visible text field caption",
                "A visible text field");
        tfHidden.setVisible(false);
        Button b = new Button("Move hidden textfield to other layout");
        b.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (layout1.getComponentIndex(tfHidden) != -1) {
                    layout2.addComponent(tfVisible);
                    layout2.addComponent(tfHidden);
                } else {
                    layout1.addComponent(tfVisible);
                    layout1.addComponent(tfHidden);
                }

            }

        });

        layout1.addComponent(tfVisible);
        layout1.addComponent(tfHidden);

        addComponent(layout1);
        addComponent(b);
        addComponent(layout2);
    }

    @Override
    protected String getDescription() {
        return "Above and below the button is a VerticalLayout. Initially the first one contains two components: a visiable and an invisible TextField. Click the button to move the TextFields to the second layout, both should be moved but only the visible rendered.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5278;
    }
}

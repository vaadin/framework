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
package com.vaadin.tests.components.gridlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class MoveComponentsFromGridLayoutToInnerLayout
        extends AbstractReindeerTestUI {

    protected Button testButton;
    private GridLayout gl;
    protected AbstractOrderedLayout vl;

    @Override
    protected void setup(VaadinRequest request) {
        gl = new GridLayout();
        gl.setHideEmptyRowsAndColumns(true);
        gl.setWidth("200px");
        gl.setHeight("200px");

        testButton = new Button("Click to move to inner layout",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        vl.addComponent(testButton);
                    }
                });

        gl.addComponent(testButton);

        vl = new VerticalLayout();
        vl.setMargin(false);
        vl.setSpacing(false);
        vl.addComponent(new Label("I'm inside the inner layout"));
        gl.addComponent(vl);

        addComponent(gl);

        Button b = new Button("Repaint inner layout",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        vl.markAsDirty();
                    }
                });

        addComponent(b);
    }

    @Override
    protected String getTestDescription() {
        return "Click the first button to move it from an outer layout to an inner. Then click the second button to repaint the inner layout.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6060;
    }

}

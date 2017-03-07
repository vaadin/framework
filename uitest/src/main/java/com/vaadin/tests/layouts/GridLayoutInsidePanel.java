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
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class GridLayoutInsidePanel extends TestBase {

    @Override
    protected String getDescription() {
        return "The first Panel contains a VerticalLayout, which contains a GridLayout, which contains a Label. The second panel directly contains a GridLayout, which contains a Label. Both should be rendered in the same way.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2652;
    }

    @Override
    protected void setup() {
        {
            GridLayout gl = new GridLayout(1, 1);
            gl.setSizeUndefined();
            gl.addComponent(
                    new Label("A label which defines the size of the GL"));

            VerticalLayout pl = new VerticalLayout();
            pl.setMargin(true);
            pl.setSizeUndefined();
            Panel p = new Panel("Panel 1", pl);
            pl.setMargin(false);
            p.setSizeUndefined();

            pl.addComponent(gl);
            addComponent(p);
        }
        {
            GridLayout gl = new GridLayout(1, 1);
            gl.setSizeUndefined();
            gl.addComponent(
                    new Label("A label which defines the size of the GL"));

            Panel p = new Panel("Panel 2", gl);
            gl.setMargin(false);
            p.setSizeUndefined();
            gl.setSizeUndefined();

            addComponent(p);
        }
    }

}

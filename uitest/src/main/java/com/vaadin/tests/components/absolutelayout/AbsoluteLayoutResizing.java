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
package com.vaadin.tests.components.absolutelayout;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.v7.ui.TextArea;

public class AbsoluteLayoutResizing extends TestBase {

    @Override
    protected void setup() {
        getLayout().setSizeFull();

        AbsoluteLayout al = new AbsoluteLayout();

        TextArea ta = new TextArea();
        ta.setValue(
                "When resizing the layout this text area should also get resized");
        ta.setSizeFull();
        al.addComponent(ta,
                "left: 10px; right: 10px; top: 10px; bottom: 10px;");

        HorizontalSplitPanel horizPanel = new HorizontalSplitPanel();
        horizPanel.setSizeFull();
        horizPanel.setFirstComponent(al);

        VerticalSplitPanel vertPanel = new VerticalSplitPanel();
        vertPanel.setSizeFull();
        vertPanel.setFirstComponent(horizPanel);

        addComponent(vertPanel);

    }

    @Override
    protected String getDescription() {
        return "Absolute layout should correctly dynamically resize itself";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10427;
    }

}

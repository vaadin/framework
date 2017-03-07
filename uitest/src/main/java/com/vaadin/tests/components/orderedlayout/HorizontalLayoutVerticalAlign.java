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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.TextArea;

public class HorizontalLayoutVerticalAlign extends TestBase {

    @Override
    protected void setup() {
        HorizontalLayout p = new HorizontalLayout();

        p.addComponent(new TextArea());

        Label top = new Label("top");
        p.addComponent(top);
        p.setComponentAlignment(top, Alignment.TOP_CENTER);

        Label middle = new Label("middle");
        p.addComponent(middle);
        p.setComponentAlignment(middle, Alignment.MIDDLE_CENTER);

        Label bottom = new Label("bottom");
        p.addComponent(bottom);
        p.setComponentAlignment(bottom, Alignment.BOTTOM_CENTER);

        p.addComponent(new TextArea());

        addComponent(p);
    }

    @Override
    protected String getDescription() {
        return "Vertical alignments should be top-middle-bottom";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10852;
    }

}

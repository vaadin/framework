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
package com.vaadin.tests.layouts;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class HorizontalLayoutWithLabelAndButton extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.setWidth("100%");
        Label l = new Label();
        l.setCaption("POTUS Database");
        l.setSizeUndefined();

        Button b = new Button("Add new");
        hl.addComponents(l, b);
        b.setStyleName("primary");
        hl.setExpandRatio(b, 1);

        addComponent(hl);
    }

}

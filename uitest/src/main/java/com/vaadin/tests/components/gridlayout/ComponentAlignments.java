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
package com.vaadin.tests.components.gridlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.VerticalLayout;

/**
 * Test UI for TOP_CENTER and TOP_RIGHT alignments in VerticalLayout.
 * 
 * @author Vaadin Ltd
 */
public class ComponentAlignments extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        CheckBox topcenter = new CheckBox("Top Center");
        topcenter.setSizeUndefined();
        VerticalLayout verticalLayout1 = new VerticalLayout(topcenter);
        verticalLayout1.setHeight("40px");
        verticalLayout1.setWidth("140px");
        verticalLayout1.setComponentAlignment(topcenter, Alignment.TOP_CENTER);
        addComponent(verticalLayout1);

        CheckBox topright = new CheckBox("Top Right");
        topright.setSizeUndefined();
        VerticalLayout verticalLayout2 = new VerticalLayout(topright);
        verticalLayout2.setHeight("40px");
        verticalLayout2.setWidth("140px");
        verticalLayout2.setComponentAlignment(topright, Alignment.TOP_RIGHT);
        addComponent(verticalLayout2);

    }

    @Override
    protected Integer getTicketNumber() {
        return 14137;
    }

    @Override
    public String getDescription() {
        return "TOP_CENTER and TOP_RIGHT alignments should work in VerticalLayout";
    }

}

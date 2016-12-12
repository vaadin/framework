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
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;

public class GridLayoutCaptionAlignment extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(createLayout(Alignment.BOTTOM_CENTER));
        addComponent(createLayout(Alignment.BOTTOM_LEFT));
        addComponent(createLayout(Alignment.BOTTOM_RIGHT));
        addComponent(createLayout(Alignment.MIDDLE_CENTER));
        addComponent(createLayout(Alignment.MIDDLE_LEFT));
        addComponent(createLayout(Alignment.MIDDLE_RIGHT));
        addComponent(createLayout(Alignment.TOP_CENTER));
        addComponent(createLayout(Alignment.TOP_LEFT));
        addComponent(createLayout(Alignment.TOP_RIGHT));
    }

    private GridLayout createLayout(Alignment align) {
        TextField field = new TextField("Some caption");

        GridLayout layout = new GridLayout(3, 3);
        layout.setSizeFull();
        layout.addComponent(field);
        layout.setComponentAlignment(field, align);
        return layout;
    }

    @Override
    protected Integer getTicketNumber() {
        return 17619;
    }

    @Override
    protected String getTestDescription() {
        return "Test alignment of component captions inside GridLayout – "
                + "all captions should be aligned directly above the TextField components.";
    }

}

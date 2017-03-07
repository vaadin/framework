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

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.themes.Reindeer;

@SuppressWarnings("serial")
public class VerticalLayoutSlotExpansionAndAlignment extends UI {

    @Override
    protected void init(VaadinRequest request) {

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        setContent(layout);

        HorizontalLayout header = new HorizontalLayout(new Label("HEADER"));
        header.setHeight("100px");
        header.setWidth("100%");
        header.setStyleName(Reindeer.LAYOUT_WHITE);
        layout.addComponent(header);

        HorizontalLayout content = new HorizontalLayout(new Label("CONTENT"));
        content.setSizeFull();
        content.setStyleName(Reindeer.LAYOUT_BLUE);
        layout.addComponent(content);

        HorizontalLayout footer = new HorizontalLayout(new Label("FOOTER"));
        footer.setHeight("150px");
        footer.setWidth("100%");
        footer.setStyleName(Reindeer.LAYOUT_BLACK);
        layout.addComponent(footer);

        // This break things
        layout.setComponentAlignment(footer, Alignment.BOTTOM_LEFT);
        layout.setExpandRatio(content, 1);

    }

}

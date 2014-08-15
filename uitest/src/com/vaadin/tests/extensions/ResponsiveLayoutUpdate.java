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

package com.vaadin.tests.extensions;

import com.vaadin.annotations.Theme;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

@Theme("tests-responsive")
public class ResponsiveLayoutUpdate extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.addStyleName("layout-update");
        layout.setWidth("100%");
        setContent(layout);
        Responsive.makeResponsive(layout);

        Label label = new Label(
                "This label changes its size between the breakpoints, allowing more space for the adjacent component.");
        label.addStyleName("change-width");
        label.setSizeUndefined();
        layout.addComponent(label);

        Panel panel = new Panel("Panel");
        panel.setContent(new Label(
                "This Panel should be maximized in both breakpoints."));
        panel.setSizeFull();
        layout.addComponent(panel);
        layout.setExpandRatio(panel, 1);
    }

    @Override
    protected String getTestDescription() {
        return "A new layout phase should be requested after a new breakpoint is triggered, ensuring any style changes affecting component sizes are taken into account.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14354;
    }
}

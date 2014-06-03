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
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

@Theme("tests-responsive")
public class ResponsiveWidthAndHeight extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        CssLayout layout = new CssLayout();
        layout.addStyleName("width-and-height");
        layout.setSizeFull();
        setContent(layout);
        Responsive.makeResponsive(layout);

        layout.addComponent(new Label(
                "Resize the browser window in both dimensions to see the background color change."));
    }

    @Override
    protected String getTestDescription() {
        return "The CssLayout with both width-range and height-range defined";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13587;
    }
}

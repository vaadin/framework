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
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;

@Theme("tests-responsive")
public class ResponsiveUI extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        HorizontalSplitPanel split = new HorizontalSplitPanel();
        setContent(split);
        split.setSplitPosition(50, Unit.PERCENTAGE);
        split.setMinSplitPosition(100, Unit.PIXELS);
        split.setMaxSplitPosition(1200, Unit.PIXELS);
        setStyleName("responsive-test");

        CssLayout firstGrid = makeGrid("first");
        CssLayout secondGrid = makeGrid("second");
        CssLayout grids = new CssLayout();
        grids.setSizeFull();
        grids.addComponent(firstGrid);
        grids.addComponent(secondGrid);
        split.addComponent(grids);

        Label description = new Label(
                "<h3>This application demonstrates the Responsive extension in Vaadin.</h3>"
                        + "<p>Drag the splitter to see how the boxes on the left side adapt to "
                        + "different widths. They maintain a width of 100-200px, and always "
                        + "span the entire width of the container.</p><p>This label will "
                        + "adapt its font size and line height for different widths.</p>"
                        + "<p><a href=\"http://vaadin.com/download\">Download "
                        + "Vaadin</a></p>", ContentMode.HTML);
        description.addStyleName("description");
        split.addComponent(description);

        // Add the responsive capabilities to the components
        Responsive.makeResponsive(firstGrid);
        Responsive.makeResponsive(secondGrid);
        Responsive.makeResponsive(description);
    }

    private CssLayout makeGrid(String styleName) {
        CssLayout grid = new CssLayout();
        grid.setWidth("100%");
        grid.addStyleName("grid");
        grid.addStyleName(styleName);

        for (int i = 1; i < 10; i++) {
            Label l = new Label("" + i);
            l.setSizeUndefined();
            grid.addComponent(l);
        }
        return grid;
    }

    @Override
    protected String getTestDescription() {
        return "The CssLayouts (grids) and Label should be responsive";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12394;
    }
}

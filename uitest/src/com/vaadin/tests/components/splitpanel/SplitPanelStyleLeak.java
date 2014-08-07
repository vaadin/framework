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
package com.vaadin.tests.components.splitpanel;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.AbstractSplitPanel;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalSplitPanel;

public class SplitPanelStyleLeak extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        CssLayout wrap = new CssLayout();
        addComponent(wrap);

        wrap.addComponent(getSplit(true, null));
        wrap.addComponent(getSplit(false, null));

        wrap.addComponent(getSplit(true, "small"));
        wrap.addComponent(getSplit(false, "small"));

        wrap.addComponent(getSplit(true, "large"));
        wrap.addComponent(getSplit(false, "large"));
    }

    private AbstractSplitPanel getSplit(boolean horizontal, String style) {
        AbstractSplitPanel split = horizontal ? new HorizontalSplitPanel()
                : new VerticalSplitPanel();

        if (style != null) {
            split.addStyleName(style);
        }
        split.setWidth("300px");
        split.setHeight("300px");

        AbstractSplitPanel content = horizontal ? new VerticalSplitPanel()
                : new HorizontalSplitPanel();
        content.addComponent(new Label("First"));
        content.addComponent(new Label("Second"));
        split.addComponent(content);

        content = horizontal ? new VerticalSplitPanel()
                : new HorizontalSplitPanel();
        content.addComponent(new Label("First"));
        split.addComponent(content);

        // Inception level nesting, but we need to test that the first level
        // styles don't leak to a nested split panel with the same orientation
        AbstractSplitPanel content2 = horizontal ? new HorizontalSplitPanel()
                : new VerticalSplitPanel();
        content2.addComponent(new Label("First"));
        content2.addComponent(new Label("Second"));
        content.addComponent(content2);

        return split;
    }

    @Override
    protected String getTestDescription() {
        return "Vertical/horizontal SplitPanel styles should not leak to any contained horizontal/vertical SplitPanel.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14152;
    }

}

/*
 * Copyright 2012 Vaadin Ltd.
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

import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalSplitPanel;

public class RetainSplitterPositionWhenOutOfBounds extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        // Replacing default content to get the intended expansions
        setContent(new MainLayout());
    }

    public class MainLayout extends GridLayout {

        public MainLayout() {
            super(1, 3);
            setSizeFull();

            VerticalSplitPanel splitPanel = new VerticalSplitPanel();
            splitPanel.setFirstComponent(new Label("Top"));
            splitPanel.setSecondComponent(new Label("Middle"));
            splitPanel.setSplitPosition(50, Sizeable.Unit.PERCENTAGE);

            HorizontalLayout bottom = new HorizontalLayout();
            bottom.setWidth("100%");
            bottom.addComponent(new Label("Bottom"));

            addComponent(new Label(getTestDescription()));
            addComponent(splitPanel);
            addComponent(bottom);
        }

    }

    @Override
    protected String getTestDescription() {
        return "The original splitter position value should be respected even if it's recalculated because it's of out bounds.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(10596);
    }

}

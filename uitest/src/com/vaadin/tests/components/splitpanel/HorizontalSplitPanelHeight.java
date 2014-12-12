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
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalSplitPanel;

/**
 * Test UI for horizontal split panel height.
 * 
 * @author Vaadin Ltd
 */
public class HorizontalSplitPanelHeight extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        getLayout().setMargin(new MarginInfo(true, false, false, false));

        HorizontalSplitPanel panel = new HorizontalSplitPanel();
        panel.setCaption("Horizontal 1 - no first component, label as second component");
        panel.setId("Horizontal 1");
        Label label = new Label("Label");
        label.addStyleName("target");
        panel.setSecondComponent(label);

        addComponent(panel);

        panel = new HorizontalSplitPanel();
        panel.setCaption("Horizontal 2 - button as first component, label as second component");
        panel.setId("Horizontal 2");
        label = new Label("Label");
        label.addStyleName("target");
        panel.setSecondComponent(label);
        panel.setFirstComponent(new Button("button"));

        addComponent(panel);

        panel = new HorizontalSplitPanel();
        panel.setCaption("Horizontal 3 - fixed height, no first component, label as second component");
        panel.setId("Horizontal 3");
        label = new Label("Label");
        label.addStyleName("target");
        panel.setSecondComponent(label);
        panel.setHeight(30, Unit.PIXELS);

        addComponent(panel);

        VerticalSplitPanel vPanel = new VerticalSplitPanel();
        vPanel.setCaption("Vertical 1 - no first component, label as second component");
        vPanel.setId("Vertical 1");
        vPanel.setHeight(100, Unit.PIXELS);
        Label vLabel = new Label("Label");
        vLabel.addStyleName("target");
        vPanel.setSecondComponent(vLabel);

        addComponent(vPanel);

    }

    @Override
    protected Integer getTicketNumber() {
        return 15149;
    }

    @Override
    public String getTestDescription() {
        return "Height of split panel should be greater than height of second component.";
    }
}

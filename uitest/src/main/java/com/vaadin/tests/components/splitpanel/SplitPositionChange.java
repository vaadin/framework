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
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.AbstractSplitPanel;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

/**
 * Test for {@link SplitPositionChangeListeners}.
 * 
 * @author Vaadin Ltd
 */
public class SplitPositionChange extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        addSplitPanel(true, "Left", "Right");
        addSplitPanel(false, "Top", "Bottom");
    }

    private void addSplitPanel(final boolean horizontal, String firstCaption,
            String secondCaption) {
        AbstractSplitPanel splitPanel;
        if (horizontal) {
            splitPanel = new HorizontalSplitPanel();
        } else {
            splitPanel = new VerticalSplitPanel();
        }
        splitPanel.setWidth("200px");
        splitPanel.setHeight("200px");
        splitPanel.addComponent(buildPanel(firstCaption));
        splitPanel.addComponent(buildPanel(secondCaption));
        splitPanel
                .addSplitPositionChangeListener(new AbstractSplitPanel.SplitPositionChangeListener() {

                    @Override
                    public void onSplitPositionChanged(
                            AbstractSplitPanel.SplitPositionChangeEvent event) {
                        log(String.format(
                                "Split position changed: %s, position: %s %s",
                                (horizontal ? "horizontal" : "vertical"),
                                event.getSplitPosition(),
                                event.getSplitPositionUnit()));
                    }
                });
        addComponent(splitPanel);
    }

    private Panel buildPanel(String caption) {
        VerticalLayout pl = new VerticalLayout();
        pl.setMargin(true);
        pl.addComponent(new Label("content"));
        Panel panel = new Panel(caption, pl);
        panel.setSizeFull();
        return panel;
    }

    @Override
    protected String getTestDescription() {
        return "SplitPanel should have an event for the splitter being moved";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3855;
    }

}

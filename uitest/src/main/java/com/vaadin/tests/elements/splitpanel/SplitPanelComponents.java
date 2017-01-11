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
package com.vaadin.tests.elements.splitpanel;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalSplitPanel;

/**
 * This UI is used for testing that TestBench gets the second component of a
 * split panel using getSecondComponent() even when there is no first component.
 */
public class SplitPanelComponents extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        // A horizontal split panel without a first component
        Panel p1 = new Panel();
        HorizontalSplitPanel hsp = new HorizontalSplitPanel();
        hsp.setSecondComponent(new Label("Label 1.2"));
        p1.setContent(hsp);
        hsp.setSizeFull();
        p1.setHeight("30px");

        // A vertical split panel without a first component
        Panel p2 = new Panel();
        VerticalSplitPanel vsp = new VerticalSplitPanel();
        vsp.setSecondComponent(new Label("Label 2.2"));
        p2.setContent(vsp);
        vsp.setSizeFull();
        p2.setHeight("100px");

        // A split panel containing both components
        Panel p3 = new Panel();
        HorizontalSplitPanel hsp2 = new HorizontalSplitPanel();
        hsp2.setFirstComponent(new Label("Label 3.1"));
        hsp2.setSecondComponent(new Label("Label 3.2"));
        p3.setContent(hsp2);
        hsp2.setSizeFull();
        p3.setHeight("30px");

        // A vertical split panel without a second component
        Panel p4 = new Panel();
        VerticalSplitPanel vsp2 = new VerticalSplitPanel();
        vsp2.setFirstComponent(new Button("Button"));
        p4.setContent(vsp2);
        vsp2.setSizeFull();
        p4.setHeight("100px");

        addComponent(p1);
        addComponent(p2);
        addComponent(p3);
        addComponent(p4);
    }

    @Override
    protected String getTestDescription() {
        return "The second component of a split panel should be accessible"
                + " in TestBench using getSecondComponent() regardless of whether"
                + " the panel contains a first component.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14073;
    }
}
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
package com.vaadin.tests.tooltip;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TooltipConfiguration;

/**
 * When moving between adjacent elements, the tooltip replace should obey
 * quickOpenDelay
 * 
 * @author Vaadin Ltd
 */
public class AdjacentElementsWithTooltips extends AbstractTestUI {

    private int buttonCount = 0;

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        TooltipConfiguration ttc = super.getTooltipConfiguration();
        ttc.setMaxWidth(350);
        ttc.setOpenDelay(200);
        ttc.setCloseTimeout(200);
        ttc.setQuickOpenDelay(1000);
        ttc.setQuickOpenTimeout(1000);
        HorizontalLayout layout = new HorizontalLayout();
        layout.addComponent(makeButton("first"));
        layout.addComponent(makeButton("second"));
        addComponent(layout);

    }

    private Component makeButton(String tooltip) {
        Button button = new Button("Button " + buttonCount++);
        button.setDescription(tooltip);
        return button;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Moving between adjacent elements with tooltips should open quickOpenDelay";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 13998;
    }

}

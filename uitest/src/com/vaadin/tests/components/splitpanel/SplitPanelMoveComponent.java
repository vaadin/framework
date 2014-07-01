/*
 * Copyright 2000-2013 Vaadin Ltd.
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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalSplitPanel;

public class SplitPanelMoveComponent extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final HorizontalSplitPanel split = new HorizontalSplitPanel();
        split.setHeight("200px");
        final Button targetComponent = new Button(
                "Button in splitpanel. Click to move to the other side");
        split.setFirstComponent(targetComponent);

        targetComponent.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (split.getFirstComponent() != null) {
                    split.setFirstComponent(null);
                    split.setSecondComponent(targetComponent);
                } else {
                    split.setSecondComponent(null);
                    split.setFirstComponent(targetComponent);

                }
            }

        });

        addComponent(split);
    }

    @Override
    protected String getTestDescription() {
        return "Fail to swap components in HorizontalSplitPanel";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11920;
    }

}

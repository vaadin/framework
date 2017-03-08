/*
 * Copyright 2000-2016 Vaadin Ltd.
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
import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.TextField;

public class SplitPanelWidthOnResize extends AbstractTestCase {

    @Override
    public void init() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        LegacyWindow w = new LegacyWindow("", layout);
        setMainWindow(w);
        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        Button button = new NativeButton("A huge button");
        button.setSizeFull();
        TextField textField = new TextField("A small textfield");

        splitPanel.setFirstComponent(button);
        splitPanel.setSecondComponent(textField);
        splitPanel.setSizeFull();
        splitPanel.setSplitPosition(100, Sizeable.UNITS_PERCENTAGE);

        layout.addComponent(splitPanel);
    }

    @Override
    protected String getDescription() {
        return "Make the browser window smaller and then larger again. The huge button should always stay visible and the TextField should never be shown.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3322;
    }

}

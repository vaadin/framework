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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.NativeButton;

public class SplitPanelExtraScrollbars extends AbstractTestCase
        implements ClickListener {

    private HorizontalSplitPanel sp;
    private HorizontalLayout hl;
    private Button b;

    @Override
    public void init() {
        sp = new HorizontalSplitPanel();
        sp.setSizeFull();
        sp.setSplitPosition(0, Sizeable.UNITS_PIXELS);

        hl = new HorizontalLayout();
        hl.setMargin(true);
        hl.setWidth("100%");
        hl.setHeight(null);

        b = createButton("200px");
        sp.setSecondComponent(hl);
        hl.addComponent(b);

        LegacyWindow w = new LegacyWindow("Test", sp);
        setMainWindow(w);
    }

    private Button createButton(String height) {
        Button b = new NativeButton("A BIG button");
        b.setHeight(height);
        b.addClickListener(this);
        return b;
    }

    @Override
    protected String getDescription() {
        return "Click the button to change its height. Making the button higher than the browser should not cause vertical but not horizontal scrollbars to appear.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3458;
    }

    @Override
    public void buttonClick(ClickEvent event) {
        if (b.getHeight() == 200) {
            b.setHeight("1200px");
        } else {
            b.setHeight("200px");
        }

        // Sending all changes in one repaint triggers the bug
        hl.markAsDirty();
        sp.markAsDirty();
    }

}

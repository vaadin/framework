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
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbstractSplitPanel.SplitterClickEvent;
import com.vaadin.ui.AbstractSplitPanel.SplitterClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.v7.ui.TextArea;

public class SplitPanelReversePosition extends TestBase {

    private boolean hsplitReversed = true;
    private boolean vsplitReversed = true;

    @Override
    protected void setup() {
        getLayout().setSizeFull();
        getLayout().setSpacing(true);

        final HorizontalSplitPanel hsplit = new HorizontalSplitPanel();
        hsplit.setSizeFull();
        hsplit.setSplitPosition(100, Sizeable.UNITS_PIXELS, hsplitReversed);
        hsplit.addSplitterClickListener(
                new HorizontalSplitPanel.SplitterClickListener() {
                    @Override
                    public void splitterClick(SplitterClickEvent event) {
                        getMainWindow().showNotification(
                                "Horizontal Splitter Clicked");
                    }
                });

        TextArea area = new TextArea("");
        area.setSizeFull();
        hsplit.addComponent(area);

        final VerticalSplitPanel vsplit = new VerticalSplitPanel();
        vsplit.setSizeFull();
        vsplit.setSplitPosition(10, Sizeable.UNITS_PERCENTAGE, vsplitReversed);
        vsplit.addSplitterClickListener(new SplitterClickListener() {
            @Override
            public void splitterClick(SplitterClickEvent event) {
                getMainWindow().showNotification("Vertical Splitter Clicked");
            }
        });
        hsplit.addComponent(vsplit);

        addComponent(hsplit);

        area = new TextArea("");
        area.setSizeFull();
        vsplit.addComponent(area);

        area = new TextArea("");
        area.setSizeFull();
        vsplit.addComponent(area);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);

        buttons.addComponent(new Button("Swap horizontal positioning",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        hsplitReversed = !hsplitReversed;
                        hsplit.setSplitPosition(100, Sizeable.UNITS_PIXELS,
                                hsplitReversed);

                    }
                }));

        buttons.addComponent(new Button("Swap vertical positioning",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        vsplitReversed = !vsplitReversed;
                        vsplit.setSplitPosition(10, Sizeable.UNITS_PERCENTAGE,
                                vsplitReversed);
                    }
                }));

        addComponent(buttons);

    }

    @Override
    protected String getDescription() {
        return "The horizontal split panel should be splitted "
                + "100px from the right and the vertical split panel should "
                + "be splitted 10% from the bottom";

    }

    @Override
    protected Integer getTicketNumber() {
        return 1588;
    }

}

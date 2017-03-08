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

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

public class SplitPanelSplitterWidth extends TestBase {

    @Override
    protected Integer getTicketNumber() {
        return 2510;
    }

    @Override
    protected String getDescription() {
        return "SplitPanel splitter is effectively a 1px wide target after unlocking previously locked splitter.";
    }

    @Override
    protected void setup() {
        final HorizontalSplitPanel split = new HorizontalSplitPanel();
        split.setWidth("200px");
        split.setHeight("200px");
        split.setLocked(true);
        Panel p = buildPanel("Left");
        p.setSizeFull();
        split.addComponent(p);
        p = buildPanel("Right");
        p.setSizeFull();
        split.addComponent(p);

        final VerticalSplitPanel split2 = new VerticalSplitPanel();
        split2.setWidth("200px");
        split2.setHeight("200px");
        split2.setLocked(true);
        p = buildPanel("Top");
        p.setSizeFull();
        split2.addComponent(p);
        p = buildPanel("Bottom");
        p.setSizeFull();
        split2.addComponent(p);

        getLayout()
                .addComponent(new Button("Unlock", new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        split.setLocked(false);
                        split2.setLocked(false);
                        getMainWindow().showNotification(
                                "Try moving split. Then reload page.",
                                Notification.TYPE_WARNING_MESSAGE);
                        getLayout().removeComponent(event.getButton());
                    }

                }));
        getLayout().addComponent(split);
        getLayout().addComponent(split2);

    }

    private Panel buildPanel(String caption) {
        VerticalLayout pl = new VerticalLayout();
        pl.setMargin(true);
        return new Panel(caption, pl);
    }
}

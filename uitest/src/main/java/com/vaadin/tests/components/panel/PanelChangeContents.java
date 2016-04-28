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
package com.vaadin.tests.components.panel;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class PanelChangeContents extends AbstractTestUI implements
        ClickListener {

    VerticalLayout stats = new VerticalLayout();
    VerticalLayout companies = new VerticalLayout();
    VerticalLayout settings = new VerticalLayout();

    Button statsButton = new Button("Stats", this);
    Button companiesButton = new Button("Companies", this);
    Button settingsButton = new Button("Settings", this);

    private Panel panel;

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout content = new VerticalLayout();
        setSizeFull();
        HorizontalLayout buttons = new HorizontalLayout();
        stats.addComponent(new Label("stats"));
        companies.addComponent(new Label("companies"));
        settings.addComponent(new Label("settings"));
        buttons.addComponent(statsButton);
        buttons.addComponent(companiesButton);
        buttons.addComponent(settingsButton);
        panel = new Panel();
        panel.setSizeFull();
        panel.setContent(stats);
        content.addComponent(buttons);
        content.addComponent(panel);
        content.setMargin(true);
        content.setSpacing(true);
        content.setExpandRatio(panel, 1);
        setContent(content);
    }

    @Override
    public void buttonClick(ClickEvent event) {
        if (event.getButton() == statsButton) {
            panel.setContent(stats);
        } else if (event.getButton() == companiesButton) {
            panel.setContent(companies);
        } else if (event.getButton() == settingsButton) {
            panel.setContent(settings);
        }

    }

    @Override
    protected String getTestDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        return 8735;
    }

}

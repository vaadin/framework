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
package com.vaadin.tests.components.calendar;

import java.util.Date;
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.BackwardHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.ForwardHandler;

public class HiddenFwdBackButtons extends UI {

    @SuppressWarnings("deprecation")
    @Override
    protected void init(VaadinRequest request) {
        GridLayout content = new GridLayout(1, 2);
        content.setSizeFull();
        setContent(content);

        final Calendar calendar = new Calendar();
        calendar.setLocale(new Locale("fi", "FI"));

        calendar.setSizeFull();
        calendar.setStartDate(new Date(100, 1, 1));
        calendar.setEndDate(new Date(100, 1, 7));
        content.addComponent(calendar);
        Button button = new Button("Hide forward and back buttons");
        button.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // This should hide the forward and back navigation buttons
                calendar.setHandler((BackwardHandler) null);
                calendar.setHandler((ForwardHandler) null);
            }
        });
        content.addComponent(button);

        content.setRowExpandRatio(0, 1);

    }
}

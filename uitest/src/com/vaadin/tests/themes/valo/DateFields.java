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
package com.vaadin.tests.themes.valo;

import java.util.Date;
import java.util.Locale;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class DateFields extends VerticalLayout implements View {
    public DateFields() {
        setMargin(true);

        Label h1 = new Label("Date Fields");
        h1.addStyleName("h1");
        addComponent(h1);

        HorizontalLayout row = new HorizontalLayout();
        row.addStyleName("wrapping");
        row.setSpacing(true);
        addComponent(row);

        DateField date = new DateField("Default resolution");
        date.setValue(new Date());
        row.addComponent(date);

        CssLayout group = new CssLayout();
        group.setCaption("Grouped with a Button");
        group.addStyleName("v-component-group");
        row.addComponent(group);

        final DateField date2 = new DateField();
        group.addComponent(date2);

        Button today = new Button("Today", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                date2.setValue(new Date());
            }
        });
        group.addComponent(today);

        date = new DateField("Default resolution, explicit size");
        date.setValue(new Date());
        row.addComponent(date);
        date.setWidth("260px");
        date.setHeight("60px");

        date = new DateField("Second resolution");
        date.setValue(new Date());
        date.setResolution(Resolution.SECOND);
        row.addComponent(date);

        date = new DateField("Minute resolution");
        date.setValue(new Date());
        date.setResolution(Resolution.MINUTE);
        row.addComponent(date);

        date = new DateField("Hour resolution");
        date.setValue(new Date());
        date.setResolution(Resolution.HOUR);
        row.addComponent(date);

        date = new DateField("Disabled");
        date.setValue(new Date());
        date.setResolution(Resolution.HOUR);
        date.setEnabled(false);
        row.addComponent(date);

        date = new DateField("Day resolution");
        date.setValue(new Date());
        date.setResolution(Resolution.DAY);
        row.addComponent(date);

        date = new DateField("Month resolution");
        date.setValue(new Date());
        date.setResolution(Resolution.MONTH);
        row.addComponent(date);

        date = new DateField("Year resolution");
        date.setValue(new Date());
        date.setResolution(Resolution.YEAR);
        row.addComponent(date);

        date = new DateField("Custom color");
        date.setValue(new Date());
        date.setResolution(Resolution.DAY);
        date.addStyleName("color1");
        row.addComponent(date);

        date = new DateField("Custom color");
        date.setValue(new Date());
        date.setResolution(Resolution.DAY);
        date.addStyleName("color2");
        row.addComponent(date);

        date = new DateField("Custom color");
        date.setValue(new Date());
        date.setResolution(Resolution.DAY);
        date.addStyleName("color3");
        row.addComponent(date);

        date = new DateField("Small");
        date.setValue(new Date());
        date.setResolution(Resolution.DAY);
        date.addStyleName("small");
        row.addComponent(date);

        date = new DateField("Large");
        date.setValue(new Date());
        date.setResolution(Resolution.DAY);
        date.addStyleName("large");
        row.addComponent(date);

        date = new DateField("Borderless");
        date.setValue(new Date());
        date.setResolution(Resolution.DAY);
        date.addStyleName("borderless");
        row.addComponent(date);

        date = new DateField("Week numbers");
        date.setValue(new Date());
        date.setResolution(Resolution.DAY);
        date.setLocale(new Locale("fi", "fi"));
        date.setShowISOWeekNumbers(true);
        row.addComponent(date);

        date = new DateField("US locale");
        date.setValue(new Date());
        date.setResolution(Resolution.SECOND);
        date.setLocale(new Locale("en", "US"));
        row.addComponent(date);

        date = new DateField("Custom format");
        date.setValue(new Date());
        date.setDateFormat("E dd/MM/yyyy");
        row.addComponent(date);

        date = new InlineDateField("Date picker");
        row.addComponent(date);

        date = new InlineDateField("Date picker with week numbers");
        date.setLocale(new Locale("fi", "fi"));
        date.setShowISOWeekNumbers(true);
        row.addComponent(date);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }

}

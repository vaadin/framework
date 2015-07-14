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

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class DateFields extends VerticalLayout implements View {
    public DateFields() {
        setMargin(true);

        Label h1 = new Label("Date Fields");
        h1.addStyleName(ValoTheme.LABEL_H1);
        addComponent(h1);

        HorizontalLayout row = new HorizontalLayout();
        row.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
        row.setSpacing(true);
        addComponent(row);

        DateField date = new DateField("Default resolution");
        setDate(date);
        row.addComponent(date);

        date = new DateField("Error");
        setDate(date);
        date.setComponentError(new UserError("Fix it, now!"));
        row.addComponent(date);

        date = new DateField("Error, borderless");
        setDate(date);
        date.setComponentError(new UserError("Fix it, now!"));
        date.addStyleName(ValoTheme.DATEFIELD_BORDERLESS);
        row.addComponent(date);

        CssLayout group = new CssLayout();
        group.setCaption("Grouped with a Button");
        group.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
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
        setDate(date);
        row.addComponent(date);
        date.setWidth("260px");
        date.setHeight("60px");

        date = new DateField("Second resolution");
        setDate(date);
        date.setResolution(Resolution.SECOND);
        row.addComponent(date);

        date = new DateField("Minute resolution");
        setDate(date);
        date.setResolution(Resolution.MINUTE);
        row.addComponent(date);

        date = new DateField("Hour resolution");
        setDate(date);
        date.setResolution(Resolution.HOUR);
        row.addComponent(date);

        date = new DateField("Disabled");
        setDate(date);
        date.setResolution(Resolution.HOUR);
        date.setEnabled(false);
        row.addComponent(date);

        date = new DateField("Day resolution");
        setDate(date);
        date.setResolution(Resolution.DAY);
        row.addComponent(date);

        date = new DateField("Month resolution");
        setDate(date);
        date.setResolution(Resolution.MONTH);
        row.addComponent(date);

        date = new DateField("Year resolution");
        setDate(date);
        date.setResolution(Resolution.YEAR);
        row.addComponent(date);

        date = new DateField("Custom color");
        setDate(date);
        date.setResolution(Resolution.DAY);
        date.addStyleName("color1");
        row.addComponent(date);

        date = new DateField("Custom color");
        setDate(date);
        date.setResolution(Resolution.DAY);
        date.addStyleName("color2");
        row.addComponent(date);

        date = new DateField("Custom color");
        setDate(date);
        date.setResolution(Resolution.DAY);
        date.addStyleName("color3");
        row.addComponent(date);

        date = new DateField("Small");
        setDate(date);
        date.setResolution(Resolution.DAY);
        date.addStyleName(ValoTheme.DATEFIELD_SMALL);
        row.addComponent(date);

        date = new DateField("Large");
        setDate(date);
        date.setResolution(Resolution.DAY);
        date.addStyleName(ValoTheme.DATEFIELD_LARGE);
        row.addComponent(date);

        date = new DateField("Borderless");
        setDate(date);
        date.setResolution(Resolution.DAY);
        date.addStyleName(ValoTheme.DATEFIELD_BORDERLESS);
        row.addComponent(date);

        date = new DateField("Week numbers");
        setDate(date);
        date.setResolution(Resolution.DAY);
        date.setLocale(new Locale("fi", "fi"));
        date.setShowISOWeekNumbers(true);
        row.addComponent(date);

        date = new DateField("US locale");
        setDate(date);
        date.setResolution(Resolution.SECOND);
        date.setLocale(new Locale("en", "US"));
        row.addComponent(date);

        date = new DateField("Custom format");
        setDate(date);
        date.setDateFormat("E dd/MM/yyyy");
        row.addComponent(date);

        date = new DateField("Tiny");
        setDate(date);
        date.setResolution(Resolution.DAY);
        date.addStyleName(ValoTheme.DATEFIELD_TINY);
        row.addComponent(date);

        date = new DateField("Huge");
        setDate(date);
        date.setResolution(Resolution.DAY);
        date.addStyleName(ValoTheme.DATEFIELD_HUGE);
        row.addComponent(date);

        date = new InlineDateField("Date picker");
        setDate(date);
        setDateRange(date);
        row.addComponent(date);

        date = new InlineDateField("Date picker with week numbers");
        setDate(date);
        date.setLocale(new Locale("fi", "fi"));
        date.setShowISOWeekNumbers(true);
        row.addComponent(date);

        PropertysetItem item = new PropertysetItem();
        item.addItemProperty("date", new ObjectProperty<Date>(getDefaultDate()));

        FormLayout form = new FormLayout();
        form.setMargin(false);

        FieldGroup binder = new FieldGroup(item);
        form.addComponent(binder.buildAndBind(
                "Picker in read-only field group", "date"));
        binder.setReadOnly(true);

        row.addComponent(form);
    }

    private void setDateRange(DateField date) {
        date.setRangeStart(getDefaultDate());

        Date endDate = getDefaultDate();
        endDate.setMonth(endDate.getMonth() + 1);
        date.setRangeEnd(endDate);
    }

    private void setDate(DateField date) {
        date.setValue(getDefaultDate());
    }

    private Date getDefaultDate() {
        if (ValoThemeUI.isTestMode()) {
            return new Date(2014 - 1900, 5, 7);
        } else {
            return new Date();
        }
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }
}

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

import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.tests.components.TestDateField;
import com.vaadin.ui.AbstractDateField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.data.util.PropertysetItem;

public class DateFields extends VerticalLayout implements View {
    public DateFields() {
        setSpacing(false);

        Label h1 = new Label("Date Fields");
        h1.addStyleName(ValoTheme.LABEL_H1);
        addComponent(h1);

        HorizontalLayout row = new HorizontalLayout();
        row.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
        addComponent(row);

        AbstractDateField<LocalDate, DateResolution> date = new TestDateField("Default resolution");
        setDate(date);
        row.addComponent(date);

        date = new TestDateField("Error");
        setDate(date);
        date.setComponentError(new UserError("Fix it, now!"));
        row.addComponent(date);

        date = new TestDateField("Error, borderless");
        setDate(date);
        date.setComponentError(new UserError("Fix it, now!"));
        date.addStyleName(ValoTheme.DATEFIELD_BORDERLESS);
        row.addComponent(date);

        CssLayout group = new CssLayout();
        group.setCaption("Grouped with a Button");
        group.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        row.addComponent(group);

        final TestDateField date2 = new TestDateField();
        group.addComponent(date2);

        Button today = new Button("Today", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                date2.setValue(LocalDate.now());
            }
        });
        group.addComponent(today);

        date = new TestDateField("Default resolution, explicit size");
        setDate(date);
        row.addComponent(date);
        date.setWidth("260px");
        date.setHeight("60px");

        date = new TestDateField("Day resolution");
        setDate(date);
        date.setResolution(DateResolution.DAY);
        row.addComponent(date);

        date = new TestDateField("Month resolution");
        setDate(date);
        date.setResolution(DateResolution.MONTH);
        row.addComponent(date);

        date = new TestDateField("Year resolution");
        setDate(date);
        date.setResolution(DateResolution.YEAR);
        row.addComponent(date);

        date = new TestDateField("Custom color");
        setDate(date);
        date.setResolution(DateResolution.DAY);
        date.addStyleName("color1");
        row.addComponent(date);

        date = new TestDateField("Custom color");
        setDate(date);
        date.setResolution(DateResolution.DAY);
        date.addStyleName("color2");
        row.addComponent(date);

        date = new TestDateField("Custom color");
        setDate(date);
        date.setResolution(DateResolution.DAY);
        date.addStyleName("color3");
        row.addComponent(date);

        date = new TestDateField("Small");
        setDate(date);
        date.setResolution(DateResolution.DAY);
        date.addStyleName(ValoTheme.DATEFIELD_SMALL);
        row.addComponent(date);

        date = new TestDateField("Large");
        setDate(date);
        date.setResolution(DateResolution.DAY);
        date.addStyleName(ValoTheme.DATEFIELD_LARGE);
        row.addComponent(date);

        date = new TestDateField("Borderless");
        setDate(date);
        date.setResolution(DateResolution.DAY);
        date.addStyleName(ValoTheme.DATEFIELD_BORDERLESS);
        row.addComponent(date);

        date = new TestDateField("Week numbers");
        setDate(date);
        date.setResolution(DateResolution.DAY);
        date.setLocale(new Locale("fi", "fi"));
        date.setShowISOWeekNumbers(true);
        row.addComponent(date);

        date = new TestDateField("Custom format");
        setDate(date);
        date.setDateFormat("E dd/MM/yyyy");
        row.addComponent(date);

        date = new TestDateField("Tiny");
        setDate(date);
        date.setResolution(DateResolution.DAY);
        date.addStyleName(ValoTheme.DATEFIELD_TINY);
        row.addComponent(date);

        date = new TestDateField("Huge");
        setDate(date);
        date.setResolution(DateResolution.DAY);
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
        item.addItemProperty("date", new ObjectProperty<>(getDefaultOldDate()));

        FormLayout form = new FormLayout();
        form.setMargin(false);

        FieldGroup binder = new FieldGroup(item);
        form.addComponent(
                binder.buildAndBind("Picker in read-only field group", "date"));
        binder.setReadOnly(true);

        row.addComponent(form);
    }

    private void setDateRange(AbstractDateField<LocalDate, DateResolution> date) {
        date.setRangeStart(getDefaultDate());

        LocalDate endDate = getDefaultDate();
        date.setRangeEnd(endDate.plusMonths(1));
    }

    private void setDate(AbstractDateField<LocalDate, DateResolution> date) {
        date.setValue(getDefaultDate());
    }

    private LocalDate getDefaultDate() {
        if (ValoThemeUI.isTestMode()) {
            return LocalDate.of(2014, 6, 7);
        } else {
            return LocalDate.now();
        }
    }

    private Date getDefaultOldDate() {
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

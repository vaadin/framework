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
package com.vaadin.tokka.ui.components.fields;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalQueries;

import org.jsoup.nodes.Element;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.shared.tokka.ui.components.fields.DateFieldServerRpc;
import com.vaadin.shared.tokka.ui.components.fields.DateFieldState;
import com.vaadin.tokka.event.EventListener;
import com.vaadin.tokka.event.Registration;
import com.vaadin.ui.declarative.DesignContext;

/**
 * A simple textual date input component.
 * 
 * @author Vaadin Ltd.
 */
public class DateField extends AbstractField<LocalDate> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("dd-MM-uuuu");

    public class DateChange extends ValueChange<LocalDate> {
        protected DateChange(boolean userOriginated) {
            super(DateField.this, userOriginated);
        }
    }

    public DateField() {
        registerRpc(new DateFieldServerRpc() {

            @Override
            public void blur() {
                fireEvent(new BlurEvent(DateField.this));
            }

            @Override
            public void focus() {
                fireEvent(new FocusEvent(DateField.this));
            }

            @Override
            public void setDate(String value) {
                LocalDate localDate = FORMATTER.parse(value,
                        TemporalQueries.localDate());
                setValue(localDate, true);
            }
        });
    }

    @Override
    public LocalDate getValue() {
        DateFieldState state = getState(false);
        return FORMATTER.parse(state.date, LocalDate::from);
    }

    @Override
    public Registration addValueChangeListener(
            EventListener<ValueChange<LocalDate>> listener) {
        return addListener(DateChange.class, listener);
    }

    @Override
    public void readDesign(Element design, DesignContext designContext) {
        super.readDesign(design, designContext);
    }

    @Override
    protected DateFieldState getState() {
        return (DateFieldState) super.getState();
    }

    @Override
    protected DateFieldState getState(boolean markAsDirty) {
        return (DateFieldState) super.getState(markAsDirty);
    }

    @Override
    protected void doSetValue(LocalDate value) {
        getState().date = value.format(FORMATTER);
    }

    @Override
    protected DateChange createValueChange(boolean userOriginated) {
        return new DateChange(userOriginated);
    }
}

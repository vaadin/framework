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
package com.vaadin.tests.components.abstractfield;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.vaadin.server.VaadinSession;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Component;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.ComboBox;

public abstract class AbstractComponentDataBindingTest extends TestBase
        implements ValueChangeListener {
    private static final Object CAPTION = "CAPTION";
    private Log log = new Log(5);
    private ComboBox localeSelect;

    @Override
    protected void setup() {
        addComponent(log);
        localeSelect = createLocaleSelect();
        addComponent(localeSelect);

        // Causes fields to be created
        localeSelect.setValue(Locale.US);
    }

    private ComboBox createLocaleSelect() {
        ComboBox cb = new ComboBox("Locale");
        cb.addContainerProperty(CAPTION, String.class, "");
        cb.setItemCaptionPropertyId(CAPTION);
        cb.setNullSelectionAllowed(false);
        for (Locale l : Locale.getAvailableLocales()) {
            Item i = cb.addItem(l);
            i.getItemProperty(CAPTION)
                    .setValue(l.getDisplayName(Locale.ENGLISH));
        }
        ((Container.Sortable) cb.getContainerDataSource())
                .sort(new Object[] { CAPTION }, new boolean[] { true });
        cb.setImmediate(true);
        cb.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                updateLocale((Locale) localeSelect.getValue());
            }
        });
        return cb;
    }

    protected void updateLocale(Locale locale) {
        VaadinSession.getCurrent().setLocale(locale);
        for (Component c : fields) {
            removeComponent(c);
        }
        fields.clear();
        createFields();
    }

    protected abstract void createFields();

    private Set<Component> fields = new HashSet<>();

    @Override
    protected void addComponent(Component c) {
        super.addComponent(c);
        if (c instanceof AbstractField) {
            configureField((AbstractField<?>) c);
            if (c != localeSelect) {
                fields.add(c);
            }
        }
    }

    protected void configureField(AbstractField<?> field) {
        field.setImmediate(true);
        field.addListener(this);
    }

    @Override
    protected String getDescription() {
        return "";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    @Override
    public void valueChange(ValueChangeEvent event) {
        AbstractField<?> field = (AbstractField<?>) event.getProperty();
        // if (field == localeSelect) {
        // return;
        // }

        Object newValue = field.getValue();
        if (newValue != null) {
            newValue = newValue + " (" + newValue.getClass().getName() + ")";
        }

        String message = "Value of " + field.getCaption() + " changed to "
                + newValue + ".";
        if (field.getPropertyDataSource() != null) {
            Object dataSourceValue = field.getPropertyDataSource().getValue();
            message += "Data model value is " + dataSourceValue;
            message += " (" + field.getPropertyDataSource().getType().getName()
                    + ")";
        }
        log.log(message);

    }

}

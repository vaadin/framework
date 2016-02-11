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
package com.vaadin.ui.proto;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.vaadin.data.util.BeanUtil;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.communication.data.typed.DataSource;
import com.vaadin.shared.util.SharedUtil;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Generic TypedForm component. This class provides some automatic field
 * detection to make form creation easier. This class is meant to be customised
 * by extending it.
 * 
 * @since
 * @param <T>
 *            form data type
 */
public class TypedForm<T> extends FormLayout {

    private Map<String, AbstractField<?>> fields = new LinkedHashMap<String, AbstractField<?>>();
    // TODO: Figure out the serialization of this.
    private Map<AbstractField<?>, Method> getters = new HashMap<AbstractField<?>, Method>();
    private Map<AbstractField<?>, Method> setters = new HashMap<AbstractField<?>, Method>();
    protected DataSource<T> dataSource;
    protected T data = null;

    public TypedForm() {
        setEnabled(false);
    }

    public TypedForm(Class<T> cls) {
        this();
        generateFields(cls);
    }

    public TypedForm(Class<T> cls, DataSource<T> dataSource) {
        this(cls);
        setDataSource(dataSource);
    }

    public void setDataSource(DataSource<T> dataSource) {
        this.dataSource = dataSource;
    }

    public void generateFields(Class<T> cls) {
        try {
            List<PropertyDescriptor> props = BeanUtil
                    .getBeanPropertyDescriptor(cls);

            for (PropertyDescriptor p : props) {
                if (p.getName().equals("class") || p.getReadMethod() == null) {
                    continue;
                }

                // TODO: improve the type/editor combo detection
                AbstractField<?> f;
                if (p.getPropertyType().isAssignableFrom(Date.class)) {
                    f = new DateField(SharedUtil.camelCaseToHumanFriendly(p
                            .getName()));
                } else if (p.getPropertyType().isAssignableFrom(String.class)) {
                    TextField textField = new TextField(
                            SharedUtil.camelCaseToHumanFriendly(p.getName()));
                    textField.setNullRepresentation("");
                    f = textField;

                } else {
                    continue;
                }

                if (p.getWriteMethod() == null) {
                    f.setReadOnly(true);
                }

                fields.put(p.getName(), f);
                getters.put(f, p.getReadMethod());
                setters.put(f, p.getWriteMethod());
            }

        } catch (IntrospectionException e) {
            throw new IllegalArgumentException(
                    "Unable to detect fields from given type "
                            + cls.getSimpleName(), e);
        }

        for (Entry<String, AbstractField<?>> e : fields.entrySet()) {
            addComponent(e.getValue());
        }
    }

    public void removeField(String name) {
        if (fields.containsKey(name)) {
            removeComponent(fields.remove(name));
        }
    }

    public void edit(T data) {
        this.data = data;

        setEnabled(data != null);
        if (data == null) {
            for (AbstractField<?> f : fields.values()) {
                f.clear();
            }
            return;
        }

        for (AbstractField<?> f : getters.keySet()) {
            setValueWithGetter(f, getters.get(f));
        }
    }

    public void clear() {
        edit(null);
    }

    protected void save() {
        if (data == null) {
            // NO-OP
            return;
        }

        for (AbstractField<?> f : setters.keySet()) {
            storeValueWithSetter(f, setters.get(f));
        }

        if (dataSource != null) {
            dataSource.save(data);
        }
    }

    protected void cancel() {
        clear();
    }

    @SuppressWarnings("unchecked")
    private <V> void setValueWithGetter(AbstractField<V> field, Method getter) {
        try {
            field.setValue((V) getter.invoke(data));
        } catch (Exception e) {
            // TODO: Exception handling
            e.printStackTrace();
        }
    }

    private <V> void storeValueWithSetter(AbstractField<V> field, Method setter) {
        try {
            setter.invoke(data, field.getValue());
        } catch (Exception e) {
            // TODO: Exception handling
            e.printStackTrace();
        }
    }

    public Component getButtonLayout() {
        Button saveButton = new Button("Save", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                save();
            }
        });
        saveButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        saveButton.setClickShortcut(KeyCode.ENTER);
        Button cancelButton = new Button("Cancel", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                cancel();
            }
        });
        HorizontalLayout layout = new HorizontalLayout(saveButton, cancelButton);
        layout.setSpacing(true);
        return layout;
    }

    public void setFields(String... fieldNames) {
        List<String> names = Arrays.asList(fieldNames);
        removeAllComponents();

        List<AbstractField<?>> order = new ArrayList<AbstractField<?>>();
        for (String s : names) {
            if (fields.containsKey(s)) {
                AbstractField<?> c = fields.get(s);
                order.add(c);
            }
        }

        for (AbstractField<?> f : order) {
            addComponent(f);
        }
    }
}

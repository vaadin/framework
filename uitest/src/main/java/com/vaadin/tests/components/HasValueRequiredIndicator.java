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
package com.vaadin.tests.components;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.gentyref.GenericTypeReflector;
import com.vaadin.data.HasValue;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbsoluteLayout.ComponentPosition;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Vaadin Ltd
 *
 */
public abstract class HasValueRequiredIndicator<C extends HasValue & Component>
        extends AbstractTestUI {

    private static final List<Class<? extends Layout>> LAYOUTS = getVaadinLayouts();

    @Override
    protected void setup(VaadinRequest request) {
        getContent().setSizeFull();
        getVaadinLayouts().stream().map(this::createLayout).forEach(layout -> {
            addComponent(layout, createComponent());
            addComponent(layout);
        });
    }

    protected void addComponent(Layout layout, C component) {
        layout.addComponent(component);
        if (layout instanceof AbsoluteLayout) {
            AbsoluteLayout absLayout = (AbsoluteLayout) layout;
            ComponentPosition position = absLayout.new ComponentPosition();
            position.setTop(30f, Unit.PIXELS);
            absLayout.setPosition(component, position);
        }
    }

    protected Layout createLayout(Class<? extends Layout> clazz) {
        try {
            Layout layout = clazz.newInstance();
            if (clazz.equals(AbsoluteLayout.class)) {
                layout.setWidth("100px");
                layout.setHeight("150px");
            }
            layout.addStyleName("vaadin-layout");
            return layout;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected C createComponent() {
        Type type = GenericTypeReflector.getTypeParameter(getClass(),
                HasValueRequiredIndicator.class.getTypeParameters()[0]);
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getRawType();
        }

        if (type instanceof Class<?>) {
            Class<?> clazz = (Class<?>) type;
            try {
                C component = (C) clazz.newInstance();
                initValue(component);
                component.setRequiredIndicatorVisible(true);
                component.addStyleName("test-component");
                return component;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalStateException(
                    "Cannot infer component type " + type.getTypeName());
        }
    }

    /**
     * Initialize value for the {@code component}.
     *
     * @param component
     *            a UI component
     */
    protected abstract void initValue(C component);

    private static List<Class<? extends Layout>> getVaadinLayouts() {
        List<Class<? extends Layout>> layouts = new ArrayList<>();
        layouts.add(AbsoluteLayout.class);
        layouts.add(VerticalLayout.class);
        layouts.add(HorizontalLayout.class);
        layouts.add(FormLayout.class);
        layouts.add(CssLayout.class);
        layouts.add(GridLayout.class);
        return layouts;
    }

}

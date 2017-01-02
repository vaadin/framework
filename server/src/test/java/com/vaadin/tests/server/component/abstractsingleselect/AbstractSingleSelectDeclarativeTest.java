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
package com.vaadin.tests.server.component.abstractsingleselect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBaseBase;
import com.vaadin.tests.server.component.abstractlisting.AbstractListingDeclarativeTest;
import com.vaadin.ui.AbstractSingleSelect;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.declarative.DesignContext;

/**
 * {@link AbstractSingleSelect} component declarative test.
 * <p>
 * Test inherits test methods from a {@link AbstractListingDeclarativeTest}
 * class providing here only common cases for {@link AbstractSingleSelect}s.
 *
 * @author Vaadin Ltd
 *
 *
 * @param <T>
 *            a component type
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class AbstractSingleSelectDeclarativeTest<T extends AbstractSingleSelect>
        extends AbstractListingDeclarativeTest<T> {

    @Override
    @Test
    public void dataSerialization() throws InstantiationException,
            IllegalAccessException, InvocationTargetException {
        List<String> items = Arrays.asList("foo", "bar", "foobar");

        String type = "com.vaadin.SomeType";
        String attribute = "data-type";

        String design = String.format(
                "<%s %s='%s'>\n" + "<option item='foo'>foo</option>\n"
                        + "<option item='bar' selected>bar</option>"
                        + "<option item='foobar'>foobar</option></%s>",
                getComponentTag(), attribute, type, getComponentTag());
        T component = getComponentClass().newInstance();
        component.setItems(items);
        component.setSelectedItem("bar");

        DesignContext context = readComponentAndCompare(design, component);
        Assert.assertEquals(type,
                context.getCustomAttributes(context.getRootComponent())
                        .get(attribute));
        context = new DesignContext();
        context.setCustomAttribute(component, attribute, type);
        context.setShouldWriteDataDelegate(
                DeclarativeTestBaseBase.ALWAYS_WRITE_DATA);
        testWrite(component, design, context);
    }

    @Override
    @Test
    public void valueSerialization() throws InstantiationException,
            IllegalAccessException, InvocationTargetException {
        List<String> items = Arrays.asList("foo", "bar", "foobar");

        String type = "com.vaadin.SomeType";
        String attribute = "data-type";

        String design = String.format(
                "<%s  %s='%s'>\n" + "<option item='foo'>foo</option>\n"
                        + "<option item='bar' selected>bar</option>"
                        + "<option item='foobar'>foobar</option></%s>",
                getComponentTag(), attribute, type, getComponentTag());
        T component = getComponentClass().newInstance();
        component.setItems(items);
        component.setValue("bar");

        DesignContext context = readComponentAndCompare(design, component);
        Assert.assertEquals(type,
                context.getCustomAttributes(context.getRootComponent())
                        .get(attribute));
        context = new DesignContext();
        context.setCustomAttribute(component, attribute, type);
        context.setShouldWriteDataDelegate(
                DeclarativeTestBaseBase.ALWAYS_WRITE_DATA);
        testWrite(component, design, context);
    }

    @Test
    public void dataWithCaptionGeneratorSerialization()
            throws InstantiationException, IllegalAccessException,
            InvocationTargetException {
        List<String> items = Arrays.asList("foo", "bar", "foobar");
        T component = getComponentClass().newInstance();
        Method setItemCaptionGenerator = getItemCaptionGeneratorMethod(
                component);
        if (setItemCaptionGenerator == null) {
            return;
        }
        String design = String.format(
                "<%s>\n" + "<option item='foo'>foo1</option>\n"
                        + "<option item='bar' selected>bar1</option>"
                        + "<option item='foobar'>foobar1</option></%s>",
                getComponentTag(), getComponentTag());
        component.setItems(items);
        component.setValue("bar");
        ItemCaptionGenerator generator = item -> item + "1";
        setItemCaptionGenerator.invoke(component, generator);

        testRead(design, component);
        testWrite(design, component, true);
    }

    @Override
    @Test
    public void readOnlySelection() throws InstantiationException,
            IllegalAccessException, InvocationTargetException {
        T component = getComponentClass().newInstance();

        List<String> items = Arrays.asList("foo", "bar", "foobar");

        String design = String.format(
                "<%s readonly>\n" + "<option item='foo'>foo</option>\n"
                        + "<option item='bar'>bar</option>"
                        + "<option item='foobar'>foobar</option>",
                getComponentTag(), getComponentTag());

        component.setItems(items);
        component.setReadOnly(true);

        testRead(design, component);
        testWrite(design, component, true);
    }

    private Method getItemCaptionGeneratorMethod(T component)
            throws IllegalAccessException, InvocationTargetException {
        try {
            return component.getClass().getMethod("setItemCaptionGenerator",
                    new Class[] { ItemCaptionGenerator.class });
        } catch (NoSuchMethodException ignore) {
            // ignore if there is no such method
            return null;
        }
    }

}

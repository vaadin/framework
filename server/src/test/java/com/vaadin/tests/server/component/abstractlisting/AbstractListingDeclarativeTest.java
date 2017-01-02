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
package com.vaadin.tests.server.component.abstractlisting;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.provider.Query;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.server.ThemeResource;
import com.vaadin.tests.design.DeclarativeTestBaseBase;
import com.vaadin.tests.server.component.abstractcomponent.AbstractComponentDeclarativeTestBase;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractListing;
import com.vaadin.ui.IconGenerator;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.declarative.DesignContext;

/**
 * {@link AbstractListing} component declarative test.
 * <p>
 * Test ignores comparison for {@link ItemCaptionGenerator},
 * {@link IconGenerator} and {@link SerializablePredicate} "properties" since
 * they are functions and it doesn't matter which implementation is chosen. But
 * test checks generated item captions, item icon generation and enabled items
 * generations if they are available in the component as public methods.
 * <p>
 * Common {@link AbstractComponent} properties are tested in
 * {@link AbstractComponentDeclarativeTestBase}
 *
 * @see AbstractComponentDeclarativeTestBase
 *
 * @author Vaadin Ltd
 *
 *
 * @param <T>
 *            a component type
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class AbstractListingDeclarativeTest<T extends AbstractListing>
        extends AbstractComponentDeclarativeTestBase<T> {

    private static final String EXTERNAL_URL = "http://example.com/example.gif";

    private static final String FILE_PATH = "img/example.gif";

    private static final String THEME_PATH = "example.gif";

    @Test
    public abstract void dataSerialization() throws InstantiationException,
            IllegalAccessException, InvocationTargetException;

    @Test
    public abstract void valueSerialization() throws InstantiationException,
            IllegalAccessException, InvocationTargetException;

    @Test
    public void itemIconsSerialization() throws InstantiationException,
            IllegalAccessException, InvocationTargetException {
        T component = getComponentClass().newInstance();
        Method setIconGenerator = getIconGeneratorMethod(component);
        if (setIconGenerator == null) {
            return;
        }

        List<String> items = Arrays.asList("foo", "bar", "foobar", "barfoo");

        String design = String.format(
                "<%s>\n" + "<option item='foo' icon='%s'>foo</option>\n"
                        + "<option item='bar' icon='%s'>bar</option>"
                        + "<option item='foobar' icon='theme://%s'>foobar</option>"
                        + "<option item='barfoo'>barfoo</option>" + "</%s>",
                getComponentTag(), EXTERNAL_URL, FILE_PATH, THEME_PATH,
                getComponentTag());

        component.setItems(items);
        IconGenerator generator = item -> generateIcons(item, items);
        setIconGenerator.invoke(component, generator);

        testRead(design, component);
        testWrite(design, component, true);
    }

    @Test
    public void enabledItemsSerialization() throws InstantiationException,
            IllegalAccessException, InvocationTargetException {
        T component = getComponentClass().newInstance();
        Method setEnabledITemsGenerator = getEnabledItemsProviderMethod(
                component);
        if (setEnabledITemsGenerator == null) {
            return;
        }

        List<String> items = Arrays.asList("foo", "bar", "foobar");

        String design = String.format(
                "<%s>\n" + "<option item='foo'>foo</option>\n"
                        + "<option item='bar' disabled>bar</option>"
                        + "<option item='foobar'>foobar</option>",
                getComponentTag(), getComponentTag());

        component.setItems(items);
        SerializablePredicate predicate = item -> !item.equals("bar");
        setEnabledITemsGenerator.invoke(component, predicate);

        testRead(design, component);
        testWrite(design, component, true);
    }

    @Test
    public abstract void readOnlySelection() throws InstantiationException,
            IllegalAccessException, InvocationTargetException;

    @Override
    protected boolean acceptProperty(Class<?> clazz, Method readMethod,
            Method writeMethod) {
        if (readMethod != null) {
            Class<?> returnType = readMethod.getReturnType();
            if (ItemCaptionGenerator.class.equals(returnType)
                    || IconGenerator.class.equals(returnType)
                    || SerializablePredicate.class.equals(returnType)) {
                return false;
            }
        }
        return super.acceptProperty(clazz, readMethod, writeMethod);
    }

    public DesignContext readComponentAndCompare(String design, T expected,
            Consumer<DesignContext> configureContext) {
        DesignContext context = super.readComponentAndCompare(design, expected);
        configureContext.accept(context);
        T read = (T) context.getRootComponent();
        testReadData(design, expected, read, context);
        return context;
    }

    public T testRead(String design, T expected, boolean testWrite) {
        T read = testRead(design, expected);
        if (testWrite) {
            DesignContext context = new DesignContext();
            context.setShouldWriteDataDelegate(
                    DeclarativeTestBaseBase.ALWAYS_WRITE_DATA);
            testReadData(design, expected, read, context);
        }
        return read;
    }

    private void testReadData(String design, T expected, T read,
            DesignContext context) {
        Assert.assertEquals(
                read.getDataCommunicator().getDataProvider()
                        .size(new Query<>()),
                expected.getDataCommunicator().getDataProvider()
                        .size(new Query<>()));
        testWrite(read, design, context);
    }

    private Method getIconGeneratorMethod(T component)
            throws IllegalAccessException, InvocationTargetException {
        try {
            return component.getClass().getMethod("setItemIconGenerator",
                    new Class[] { IconGenerator.class });
        } catch (NoSuchMethodException ignore) {
            // ignore if there is no such method
            return null;
        }
    }

    private Method getEnabledItemsProviderMethod(T component)
            throws IllegalAccessException, InvocationTargetException {
        try {
            return component.getClass().getMethod("setItemEnabledProvider",
                    new Class[] { SerializablePredicate.class });
        } catch (NoSuchMethodException ignore) {
            // ignore if there is no such method
            return null;
        }
    }

    private Resource generateIcons(Object item, List<String> items) {
        int index = items.indexOf(item);
        switch (index) {
        case 0:
            return new ExternalResource(EXTERNAL_URL);
        case 1:
            return new FileResource(new File(FILE_PATH));
        case 2:
            return new ThemeResource(THEME_PATH);
        }
        return null;
    }

}

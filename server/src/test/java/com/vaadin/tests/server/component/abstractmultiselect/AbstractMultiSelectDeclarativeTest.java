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
package com.vaadin.tests.server.component.abstractmultiselect;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import com.vaadin.data.Listing;
import com.vaadin.tests.server.component.abstractlisting.AbstractListingDeclarativeTest;
import com.vaadin.ui.AbstractMultiSelect;

/**
 * {@link AbstractMultiSelect} component declarative test.
 * <p>
 * Test inherits test methods from a {@link AbstractListingDeclarativeTest}
 * class providing here only common cases for {@link AbstractMultiSelect}s.
 *
 * @see AbstractListingDeclarativeTest
 *
 * @author Vaadin Ltd
 *
 *
 * @param <T>
 *            a component type
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class AbstractMultiSelectDeclarativeTest<T extends AbstractMultiSelect & Listing>
        extends AbstractListingDeclarativeTest<T> {

    @Override
    @Test
    public void dataSerialization() throws InstantiationException,
            IllegalAccessException, InvocationTargetException {
        List<String> items = Arrays.asList("foo", "bar", "foobar");

        String design = String.format(
                "<%s>\n" + "<option item='foo' selected>foo1</option>\n"
                        + "<option item='bar'>bar1</option>"
                        + "<option item='foobar' selected>foobar1</option></%s>",
                getComponentTag(), getComponentTag());
        T component = getComponentClass().newInstance();
        component.setItems(items);
        component.select("foo");
        component.select("foobar");
        component.setItemCaptionGenerator(item -> item + "1");

        testRead(design, component);
        testWrite(design, component, true);
    }

    @Override
    @Test
    public void valueSerialization() throws InstantiationException,
            IllegalAccessException, InvocationTargetException {
        List<String> items = Arrays.asList("foo", "bar", "foobar");

        String design = String.format(
                "<%s>\n" + "<option item='foo' selected>foo1</option>\n"
                        + "<option item='bar'>bar1</option>"
                        + "<option item='foobar' selected>foobar1</option></%s>",
                getComponentTag(), getComponentTag());
        T component = getComponentClass().newInstance();
        component.setItems(items);
        component.setValue(new HashSet<>(Arrays.asList("foo", "foobar")));
        component.setItemCaptionGenerator(item -> item + "1");

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

}

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
package com.vaadin.tests.data.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.legacy.data.util.converter.LegacyStringToCollectionConverter;
import com.vaadin.legacy.data.util.converter.LegacyStringToEnumConverter;
import com.vaadin.legacy.data.util.converter.LegacyStringToIntegerConverter;
import com.vaadin.legacy.data.util.converter.LegacyStringToCollectionConverter.CollectionFactory;

/**
 * Tests for {@link LegacyStringToCollectionConverter}.
 *
 * @author Vaadin Ltd
 */
public class StringToCollectionConverterTest {

    @Test
    public void convertToModel_defaultCtor() {
        LegacyStringToCollectionConverter converter = new LegacyStringToCollectionConverter();
        Collection<?> model = converter.convertToModel("a, b, c", List.class,
                null);
        Assert.assertTrue("Unexpected model class", model instanceof ArrayList);
        Iterator<?> iterator = model.iterator();
        Assert.assertEquals("Incorrect fist token", "a", iterator.next());
        Assert.assertEquals("Incorrect second token", "b", iterator.next());
        Assert.assertEquals("Incorrect third token", "c", iterator.next());
    }

    @Test
    public void convertToModel_customDelimiter() {
        LegacyStringToCollectionConverter converter = new LegacyStringToCollectionConverter(
                "x");
        Collection<?> model = converter.convertToModel("axbxc", List.class,
                null);
        Assert.assertTrue("Unexpected model class", model instanceof ArrayList);
        Iterator<?> iterator = model.iterator();
        Assert.assertEquals("Incorrect fist token", "a", iterator.next());
        Assert.assertEquals("Incorrect second token", "b", iterator.next());
        Assert.assertEquals("Incorrect third token", "c", iterator.next());
    }

    @Test
    public void convertToModel_customConverter() {
        LegacyStringToCollectionConverter converter = new LegacyStringToCollectionConverter(
                ",", new LegacyStringToIntegerConverter(), Integer.class);
        Collection<?> model = converter.convertToModel("6,2,5", List.class,
                null);
        Assert.assertTrue("Unexpected model class", model instanceof ArrayList);
        Iterator<?> iterator = model.iterator();
        Assert.assertEquals("Incorrect fist token", 6, iterator.next());
        Assert.assertEquals("Incorrect second token", 2, iterator.next());
        Assert.assertEquals("Incorrect third token", 5, iterator.next());
    }

    @Test
    public void convertToModel_setAsCollection() {
        LegacyStringToCollectionConverter converter = new LegacyStringToCollectionConverter(
                " ", new LegacyStringToEnumConverter(), TestEnum.class);
        Collection<?> model = converter.convertToModel("Z X Y", Set.class,
                null);
        Assert.assertTrue("Unexpected model class", model instanceof HashSet);
        EnumSet<TestEnum> set = EnumSet.allOf(TestEnum.class);
        set.removeAll(model);
        Assert.assertTrue("Some values are not in resutling collection",
                set.isEmpty());
    }

    @Test
    public void convertToModel_customFactory() {
        CollectionFactory factory = new CollectionFactory() {

            @Override
            public Collection<?> createCollection(
                    Class<? extends Collection> type) {
                return new Vector();
            }
        };
        LegacyStringToCollectionConverter converter = new LegacyStringToCollectionConverter(
                ", ", null, String.class, factory);
        Collection<?> model = converter.convertToModel("a, b, c",
                Collection.class, null);
        Assert.assertTrue("Unexpected model class", model instanceof Vector);
        Iterator<?> iterator = model.iterator();
        Assert.assertEquals("Incorrect fist token", "a", iterator.next());
        Assert.assertEquals("Incorrect second token", "b", iterator.next());
        Assert.assertEquals("Incorrect third token", "c", iterator.next());
    }

    @Test
    public void convertToPresentation_default() {
        LegacyStringToCollectionConverter converter = new LegacyStringToCollectionConverter();
        String presentation = converter.convertToPresentation(
                Arrays.asList("a", "b", "c"), String.class, null);

        Assert.assertEquals("a, b, c", presentation);
    }

    @Test
    public void convertToPresentation_customDelimiter() {
        LegacyStringToCollectionConverter converter = new LegacyStringToCollectionConverter(
                "x");
        String presentation = converter.convertToPresentation(
                Arrays.asList("a", "b", "c"), String.class, null);

        Assert.assertEquals("axbxc", presentation);
    }

    @Test
    public void convertToPresentation_customConverter() {
        LegacyStringToCollectionConverter converter = new LegacyStringToCollectionConverter(
                ",", new LegacyStringToEnumConverter(), TestEnum.class);
        String presentation = converter.convertToPresentation(
                Arrays.asList(TestEnum.Z, TestEnum.Y), String.class, null);

        Assert.assertEquals("Z,Y", presentation);
    }

    @Test
    public void convertToModel_singleItem() {
        LegacyStringToCollectionConverter converter = new LegacyStringToCollectionConverter();
        Collection<?> model = converter.convertToModel("a", List.class, null);
        Iterator<?> iterator = model.iterator();
        Assert.assertEquals("Incorrect fist token", "a", iterator.next());
        Assert.assertFalse("More than one item detected after conversation",
                iterator.hasNext());
    }

    @Test
    public void convertToModel_null() {
        LegacyStringToCollectionConverter converter = new LegacyStringToCollectionConverter();
        Assert.assertNull(converter.convertToModel(null, ArrayList.class,
                Locale.ENGLISH));
    }

    @Test
    public void convertToPresentation_null() {
        LegacyStringToCollectionConverter converter = new LegacyStringToCollectionConverter();
        Assert.assertNull(converter.convertToPresentation(null, String.class,
                Locale.ENGLISH));
    }

    public enum TestEnum {
        X, Y, Z;
    }
}

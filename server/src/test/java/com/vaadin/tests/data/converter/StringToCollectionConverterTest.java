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

import com.vaadin.data.util.converter.StringToCollectionConverter;
import com.vaadin.data.util.converter.StringToCollectionConverter.CollectionFactory;
import com.vaadin.data.util.converter.StringToEnumConverter;
import com.vaadin.data.util.converter.StringToIntegerConverter;

/**
 * Tests for {@link StringToCollectionConverter}.
 *
 * @author Vaadin Ltd
 */
public class StringToCollectionConverterTest {

    @Test
    public void convertToModel_defaultCtor() {
        StringToCollectionConverter converter = new StringToCollectionConverter();
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
        StringToCollectionConverter converter = new StringToCollectionConverter(
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
        StringToCollectionConverter converter = new StringToCollectionConverter(
                ",", new StringToIntegerConverter(), Integer.class);
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
        StringToCollectionConverter converter = new StringToCollectionConverter(
                " ", new StringToEnumConverter(), TestEnum.class);
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
        StringToCollectionConverter converter = new StringToCollectionConverter(
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
        StringToCollectionConverter converter = new StringToCollectionConverter();
        String presentation = converter.convertToPresentation(
                Arrays.asList("a", "b", "c"), String.class, null);

        Assert.assertEquals("a, b, c", presentation);
    }

    @Test
    public void convertToPresentation_customDelimiter() {
        StringToCollectionConverter converter = new StringToCollectionConverter(
                "x");
        String presentation = converter.convertToPresentation(
                Arrays.asList("a", "b", "c"), String.class, null);

        Assert.assertEquals("axbxc", presentation);
    }

    @Test
    public void convertToPresentation_customConverter() {
        StringToCollectionConverter converter = new StringToCollectionConverter(
                ",", new StringToEnumConverter(), TestEnum.class);
        String presentation = converter.convertToPresentation(
                Arrays.asList(TestEnum.Z, TestEnum.Y), String.class, null);

        Assert.assertEquals("Z,Y", presentation);
    }

    @Test
    public void convertToModel_singleItem() {
        StringToCollectionConverter converter = new StringToCollectionConverter();
        Collection<?> model = converter.convertToModel("a", List.class, null);
        Iterator<?> iterator = model.iterator();
        Assert.assertEquals("Incorrect fist token", "a", iterator.next());
        Assert.assertFalse("More than one item detected after conversation",
                iterator.hasNext());
    }

    @Test
    public void convertToModel_null() {
        StringToCollectionConverter converter = new StringToCollectionConverter();
        Assert.assertNull(converter.convertToModel(null, ArrayList.class,
                Locale.ENGLISH));
    }

    @Test
    public void convertToPresentation_null() {
        StringToCollectionConverter converter = new StringToCollectionConverter();
        Assert.assertNull(converter.convertToPresentation(null, String.class,
                Locale.ENGLISH));
    }

    public enum TestEnum {
        X, Y, Z;
    }
}

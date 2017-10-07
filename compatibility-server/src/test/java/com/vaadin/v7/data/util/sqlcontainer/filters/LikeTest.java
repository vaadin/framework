package com.vaadin.v7.data.util.sqlcontainer.filters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.data.util.PropertysetItem;
import com.vaadin.v7.data.util.filter.Like;

public class LikeTest {

    @Test
    public void passesFilter_valueIsNotStringType_shouldFail() {
        Like like = new Like("test", "%foo%");

        Item item = new PropertysetItem();
        item.addItemProperty("test", new ObjectProperty<Integer>(5));

        assertFalse(like.passesFilter("id", item));
    }

    @Test
    public void passesFilter_containsLikeQueryOnStringContainingValue_shouldSucceed() {
        Like like = new Like("test", "%foo%");

        Item item = new PropertysetItem();
        item.addItemProperty("test", new ObjectProperty<String>("asdfooghij"));

        assertTrue(like.passesFilter("id", item));
    }

    @Test
    public void passesFilter_containsLikeQueryOnStringContainingValueCaseInsensitive_shouldSucceed() {
        Like like = new Like("test", "%foo%");
        like.setCaseSensitive(false);

        Item item = new PropertysetItem();
        item.addItemProperty("test", new ObjectProperty<String>("asdfOOghij"));

        assertTrue(like.passesFilter("id", item));
    }

    @Test
    public void passesFilter_containsLikeQueryOnStringContainingValueConstructedCaseInsensitive_shouldSucceed() {
        Like like = new Like("test", "%foo%", false);

        Item item = new PropertysetItem();
        item.addItemProperty("test", new ObjectProperty<String>("asdfOOghij"));

        assertTrue(like.passesFilter("id", item));
    }

    @Test
    public void passesFilter_containsLikeQueryOnStringNotContainingValue_shouldFail() {
        Like like = new Like("test", "%foo%");

        Item item = new PropertysetItem();
        item.addItemProperty("test", new ObjectProperty<String>("asdbarghij"));

        assertFalse(like.passesFilter("id", item));
    }

    @Test
    public void passesFilter_containsLikeQueryOnStringExactlyEqualToValue_shouldSucceed() {
        Like like = new Like("test", "%foo%");

        Item item = new PropertysetItem();
        item.addItemProperty("test", new ObjectProperty<String>("foo"));

        assertTrue(like.passesFilter("id", item));
    }

    @Test
    public void passesFilter_containsLikeQueryOnStringEqualToValueMinusOneCharAtTheEnd_shouldFail() {
        Like like = new Like("test", "%foo%");

        Item item = new PropertysetItem();
        item.addItemProperty("test", new ObjectProperty<String>("fo"));

        assertFalse(like.passesFilter("id", item));
    }

    @Test
    public void passesFilter_beginsWithLikeQueryOnStringBeginningWithValue_shouldSucceed() {
        Like like = new Like("test", "foo%");

        Item item = new PropertysetItem();
        item.addItemProperty("test", new ObjectProperty<String>("foobar"));

        assertTrue(like.passesFilter("id", item));
    }

    @Test
    public void passesFilter_beginsWithLikeQueryOnStringNotBeginningWithValue_shouldFail() {
        Like like = new Like("test", "foo%");

        Item item = new PropertysetItem();
        item.addItemProperty("test", new ObjectProperty<String>("barfoo"));

        assertFalse(like.passesFilter("id", item));
    }

    @Test
    public void passesFilter_endsWithLikeQueryOnStringEndingWithValue_shouldSucceed() {
        Like like = new Like("test", "%foo");

        Item item = new PropertysetItem();
        item.addItemProperty("test", new ObjectProperty<String>("barfoo"));

        assertTrue(like.passesFilter("id", item));
    }

    @Test
    public void passesFilter_endsWithLikeQueryOnStringNotEndingWithValue_shouldFail() {
        Like like = new Like("test", "%foo");

        Item item = new PropertysetItem();
        item.addItemProperty("test", new ObjectProperty<String>("foobar"));

        assertFalse(like.passesFilter("id", item));
    }

    @Test
    public void passesFilter_startsWithAndEndsWithOnMatchingValue_shouldSucceed() {
        Like like = new Like("test", "foo%bar");

        Item item = new PropertysetItem();
        item.addItemProperty("test", new ObjectProperty<String>("fooASDFbar"));

        assertTrue(like.passesFilter("id", item));
    }

    @Test
    public void appliesToProperty_valueIsProperty_shouldBeTrue() {
        Like like = new Like("test", "%foo");
        assertTrue(like.appliesToProperty("test"));
    }

    @Test
    public void appliesToProperty_valueIsNotProperty_shouldBeFalse() {
        Like like = new Like("test", "%foo");
        assertFalse(like.appliesToProperty("bar"));
    }

    @Test
    public void equals_sameInstances_shouldBeTrue() {
        Like like1 = new Like("test", "%foo");
        Like like2 = like1;
        assertTrue(like1.equals(like2));
    }

    @Test
    public void equals_twoEqualInstances_shouldBeTrue() {
        Like like1 = new Like("test", "foo");
        Like like2 = new Like("test", "foo");
        assertTrue(like1.equals(like2));
    }

    @Test
    public void equals_differentValues_shouldBeFalse() {
        Like like1 = new Like("test", "foo");
        Like like2 = new Like("test", "bar");
        assertFalse(like1.equals(like2));
    }

    @Test
    public void equals_differentProperties_shouldBeFalse() {
        Like like1 = new Like("foo", "test");
        Like like2 = new Like("bar", "test");
        assertFalse(like1.equals(like2));
    }

    @Test
    public void equals_differentPropertiesAndValues_shouldBeFalse() {
        Like like1 = new Like("foo", "bar");
        Like like2 = new Like("baz", "zomg");
        assertFalse(like1.equals(like2));
    }

    @Test
    public void equals_differentClasses_shouldBeFalse() {
        Like like1 = new Like("foo", "bar");
        Object obj = new Object();
        assertFalse(like1.equals(obj));
    }

    @Test
    public void equals_bothHaveNullProperties_shouldBeTrue() {
        Like like1 = new Like(null, "foo");
        Like like2 = new Like(null, "foo");
        assertTrue(like1.equals(like2));
    }

    @Test
    public void equals_bothHaveNullValues_shouldBeTrue() {
        Like like1 = new Like("foo", null);
        Like like2 = new Like("foo", null);
        assertTrue(like1.equals(like2));
    }

    @Test
    public void equals_onePropertyIsNull_shouldBeFalse() {
        Like like1 = new Like(null, "bar");
        Like like2 = new Like("foo", "baz");
        assertFalse(like1.equals(like2));
    }

    @Test
    public void equals_oneValueIsNull_shouldBeFalse() {
        Like like1 = new Like("foo", null);
        Like like2 = new Like("baz", "bar");
        assertFalse(like1.equals(like2));
    }

    @Test
    public void hashCode_equalInstances_shouldBeEqual() {
        Like like1 = new Like("test", "foo");
        Like like2 = new Like("test", "foo");
        assertEquals(like1.hashCode(), like2.hashCode());
    }

    @Test
    public void hashCode_differentPropertiesAndValues_shouldNotEqual() {
        Like like1 = new Like("foo", "bar");
        Like like2 = new Like("baz", "zomg");
        assertTrue(like1.hashCode() != like2.hashCode());
    }
}

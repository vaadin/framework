package com.vaadin.data.util.sqlcontainer.filters;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.data.util.filter.Like;

public class LikeTest {

    @Test
    public void passesFilter_valueIsNotStringType_shouldFail() {
        Like like = new Like("test", "%foo%");

        Item item = new PropertysetItem();
        item.addItemProperty("test", new ObjectProperty<Integer>(5));

        Assert.assertFalse(like.passesFilter("id", item));
    }

    @Test
    public void passesFilter_containsLikeQueryOnStringContainingValue_shouldSucceed() {
        Like like = new Like("test", "%foo%");

        Item item = new PropertysetItem();
        item.addItemProperty("test", new ObjectProperty<String>("asdfooghij"));

        Assert.assertTrue(like.passesFilter("id", item));
    }

    @Test
    public void passesFilter_containsLikeQueryOnStringContainingValueCaseInsensitive_shouldSucceed() {
        Like like = new Like("test", "%foo%");
        like.setCaseSensitive(false);

        Item item = new PropertysetItem();
        item.addItemProperty("test", new ObjectProperty<String>("asdfOOghij"));

        Assert.assertTrue(like.passesFilter("id", item));
    }

    @Test
    public void passesFilter_containsLikeQueryOnStringContainingValueConstructedCaseInsensitive_shouldSucceed() {
        Like like = new Like("test", "%foo%", false);

        Item item = new PropertysetItem();
        item.addItemProperty("test", new ObjectProperty<String>("asdfOOghij"));

        Assert.assertTrue(like.passesFilter("id", item));
    }

    @Test
    public void passesFilter_containsLikeQueryOnStringNotContainingValue_shouldFail() {
        Like like = new Like("test", "%foo%");

        Item item = new PropertysetItem();
        item.addItemProperty("test", new ObjectProperty<String>("asdbarghij"));

        Assert.assertFalse(like.passesFilter("id", item));
    }

    @Test
    public void passesFilter_containsLikeQueryOnStringExactlyEqualToValue_shouldSucceed() {
        Like like = new Like("test", "%foo%");

        Item item = new PropertysetItem();
        item.addItemProperty("test", new ObjectProperty<String>("foo"));

        Assert.assertTrue(like.passesFilter("id", item));
    }

    @Test
    public void passesFilter_containsLikeQueryOnStringEqualToValueMinusOneCharAtTheEnd_shouldFail() {
        Like like = new Like("test", "%foo%");

        Item item = new PropertysetItem();
        item.addItemProperty("test", new ObjectProperty<String>("fo"));

        Assert.assertFalse(like.passesFilter("id", item));
    }

    @Test
    public void passesFilter_beginsWithLikeQueryOnStringBeginningWithValue_shouldSucceed() {
        Like like = new Like("test", "foo%");

        Item item = new PropertysetItem();
        item.addItemProperty("test", new ObjectProperty<String>("foobar"));

        Assert.assertTrue(like.passesFilter("id", item));
    }

    @Test
    public void passesFilter_beginsWithLikeQueryOnStringNotBeginningWithValue_shouldFail() {
        Like like = new Like("test", "foo%");

        Item item = new PropertysetItem();
        item.addItemProperty("test", new ObjectProperty<String>("barfoo"));

        Assert.assertFalse(like.passesFilter("id", item));
    }

    @Test
    public void passesFilter_endsWithLikeQueryOnStringEndingWithValue_shouldSucceed() {
        Like like = new Like("test", "%foo");

        Item item = new PropertysetItem();
        item.addItemProperty("test", new ObjectProperty<String>("barfoo"));

        Assert.assertTrue(like.passesFilter("id", item));
    }

    @Test
    public void passesFilter_endsWithLikeQueryOnStringNotEndingWithValue_shouldFail() {
        Like like = new Like("test", "%foo");

        Item item = new PropertysetItem();
        item.addItemProperty("test", new ObjectProperty<String>("foobar"));

        Assert.assertFalse(like.passesFilter("id", item));
    }

    @Test
    public void passesFilter_startsWithAndEndsWithOnMatchingValue_shouldSucceed() {
        Like like = new Like("test", "foo%bar");

        Item item = new PropertysetItem();
        item.addItemProperty("test", new ObjectProperty<String>("fooASDFbar"));

        Assert.assertTrue(like.passesFilter("id", item));
    }

    @Test
    public void appliesToProperty_valueIsProperty_shouldBeTrue() {
        Like like = new Like("test", "%foo");
        Assert.assertTrue(like.appliesToProperty("test"));
    }

    @Test
    public void appliesToProperty_valueIsNotProperty_shouldBeFalse() {
        Like like = new Like("test", "%foo");
        Assert.assertFalse(like.appliesToProperty("bar"));
    }

    @Test
    public void equals_sameInstances_shouldBeTrue() {
        Like like1 = new Like("test", "%foo");
        Like like2 = like1;
        Assert.assertTrue(like1.equals(like2));
    }

    @Test
    public void equals_twoEqualInstances_shouldBeTrue() {
        Like like1 = new Like("test", "foo");
        Like like2 = new Like("test", "foo");
        Assert.assertTrue(like1.equals(like2));
    }

    @Test
    public void equals_differentValues_shouldBeFalse() {
        Like like1 = new Like("test", "foo");
        Like like2 = new Like("test", "bar");
        Assert.assertFalse(like1.equals(like2));
    }

    @Test
    public void equals_differentProperties_shouldBeFalse() {
        Like like1 = new Like("foo", "test");
        Like like2 = new Like("bar", "test");
        Assert.assertFalse(like1.equals(like2));
    }

    @Test
    public void equals_differentPropertiesAndValues_shouldBeFalse() {
        Like like1 = new Like("foo", "bar");
        Like like2 = new Like("baz", "zomg");
        Assert.assertFalse(like1.equals(like2));
    }

    @Test
    public void equals_differentClasses_shouldBeFalse() {
        Like like1 = new Like("foo", "bar");
        Object obj = new Object();
        Assert.assertFalse(like1.equals(obj));
    }

    @Test
    public void equals_bothHaveNullProperties_shouldBeTrue() {
        Like like1 = new Like(null, "foo");
        Like like2 = new Like(null, "foo");
        Assert.assertTrue(like1.equals(like2));
    }

    @Test
    public void equals_bothHaveNullValues_shouldBeTrue() {
        Like like1 = new Like("foo", null);
        Like like2 = new Like("foo", null);
        Assert.assertTrue(like1.equals(like2));
    }

    @Test
    public void equals_onePropertyIsNull_shouldBeFalse() {
        Like like1 = new Like(null, "bar");
        Like like2 = new Like("foo", "baz");
        Assert.assertFalse(like1.equals(like2));
    }

    @Test
    public void equals_oneValueIsNull_shouldBeFalse() {
        Like like1 = new Like("foo", null);
        Like like2 = new Like("baz", "bar");
        Assert.assertFalse(like1.equals(like2));
    }

    @Test
    public void hashCode_equalInstances_shouldBeEqual() {
        Like like1 = new Like("test", "foo");
        Like like2 = new Like("test", "foo");
        Assert.assertEquals(like1.hashCode(), like2.hashCode());
    }

    @Test
    public void hashCode_differentPropertiesAndValues_shouldNotEqual() {
        Like like1 = new Like("foo", "bar");
        Like like2 = new Like("baz", "zomg");
        Assert.assertTrue(like1.hashCode() != like2.hashCode());
    }
}

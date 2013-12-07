package com.vaadin.tests.server.component.abstractselect;

import java.util.Collection;

import junit.framework.TestCase;

import org.junit.Assert;

import com.vaadin.ui.OptionGroup;

public class TestVarargsItemAddition extends TestCase {

    public void itemAddition() throws Exception {

        OptionGroup optionGroup = new OptionGroup();

        optionGroup.addItems("foo", "bar", "car");

        Collection<?> itemIds = optionGroup.getItemIds();
        Assert.assertEquals(3, itemIds.size());
        Assert.assertTrue(itemIds.contains("foo"));
        Assert.assertTrue(itemIds.contains("bar"));
        Assert.assertTrue(itemIds.contains("car"));

    }
}

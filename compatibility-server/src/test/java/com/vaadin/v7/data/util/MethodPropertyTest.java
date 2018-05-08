package com.vaadin.v7.data.util;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.v7.data.util.NestedMethodPropertyTest.Address;

public class MethodPropertyTest {

    private Address testObject;

    @Before
    public void setup() {
        testObject = new Address("some street", 123);
    }

    @Test
    public void getValue() {
        MethodProperty<String> mp = new MethodProperty<String>(testObject,
                "street");
        assertEquals("some street", mp.getValue());
    }

    @Test
    public void getValueAfterBeanUpdate() {
        MethodProperty<String> mp = new MethodProperty<String>(testObject,
                "street");
        testObject.setStreet("Foo street");
        assertEquals("Foo street", mp.getValue());
    }

    @Test
    public void setValue() {
        MethodProperty<String> mp = new MethodProperty<String>(testObject,
                "street");
        mp.setValue("Foo street");
        assertEquals("Foo street", testObject.getStreet());
    }

    @Test
    public void changeInstance() {
        MethodProperty<String> mp = new MethodProperty<String>(testObject,
                "street");
        Address newStreet = new Address("new street", 999);
        mp.setInstance(newStreet);
        assertEquals("new street", mp.getValue());
        assertEquals("some street", testObject.getStreet());

    }

    @Test(expected = IllegalArgumentException.class)
    public void changeInstanceToIncompatible() {
        MethodProperty<String> mp = new MethodProperty<String>(testObject,
                "street");
        mp.setInstance("foobar");

    }

}

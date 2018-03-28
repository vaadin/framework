package com.vaadin.tests.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;

import org.apache.commons.lang.SerializationUtils;
import org.junit.Test;

import com.vaadin.ui.UniqueSerializable;

public class UniqueSerializableTest implements Serializable {

    @Test
    public void testUniqueness() {
        UniqueSerializable o1 = new UniqueSerializable() {
        };
        UniqueSerializable o2 = new UniqueSerializable() {
        };
        assertFalse(o1 == o2);
        assertFalse(o1.equals(o2));
    }

    @Test
    public void testSerialization() {
        UniqueSerializable o1 = new UniqueSerializable() {
        };
        UniqueSerializable d1 = (UniqueSerializable) SerializationUtils
                .deserialize(SerializationUtils.serialize(o1));
        assertTrue(d1.equals(o1));
    }

}

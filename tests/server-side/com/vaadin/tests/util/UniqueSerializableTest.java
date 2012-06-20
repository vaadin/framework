/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.tests.util;

import java.io.Serializable;

import junit.framework.TestCase;

import org.apache.commons.lang.SerializationUtils;

import com.vaadin.ui.UniqueSerializable;

public class UniqueSerializableTest extends TestCase implements Serializable {

    public void testUniqueness() {
        UniqueSerializable o1 = new UniqueSerializable() {
        };
        UniqueSerializable o2 = new UniqueSerializable() {
        };
        assertFalse(o1 == o2);
        assertFalse(o1.equals(o2));
    }

    public void testSerialization() {
        UniqueSerializable o1 = new UniqueSerializable() {
        };
        UniqueSerializable d1 = (UniqueSerializable) SerializationUtils
                .deserialize(SerializationUtils.serialize(o1));
        assertTrue(d1.equals(o1));
    }

}

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
package com.vaadin.v7.data.util;

import org.junit.Assert;
import org.junit.Test;

public class ObjectPropertyTest {

    public static class TestSuperClass {
        private String name;

        public TestSuperClass(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return getName();
        }
    }

    public static class TestSubClass extends TestSuperClass {
        public TestSubClass(String name) {
            super("Subclass: " + name);
        }
    }

    private TestSuperClass super1 = new TestSuperClass("super1");
    private TestSubClass sub1 = new TestSubClass("sub1");

    @Test
    public void testSimple() {
        ObjectProperty<TestSuperClass> prop1 = new ObjectProperty<TestSuperClass>(
                super1, TestSuperClass.class);
        Assert.assertEquals("super1", prop1.getValue().getName());
        prop1 = new ObjectProperty<TestSuperClass>(super1);
        Assert.assertEquals("super1", prop1.getValue().getName());

        ObjectProperty<TestSubClass> prop2 = new ObjectProperty<TestSubClass>(
                sub1, TestSubClass.class);
        Assert.assertEquals("Subclass: sub1", prop2.getValue().getName());
        prop2 = new ObjectProperty<TestSubClass>(sub1);
        Assert.assertEquals("Subclass: sub1", prop2.getValue().getName());
    }

    @Test
    public void testSetValueObjectSuper() {
        ObjectProperty<TestSuperClass> prop = new ObjectProperty<TestSuperClass>(
                super1, TestSuperClass.class);
        Assert.assertEquals("super1", prop.getValue().getName());
        prop.setValue(new TestSuperClass("super2"));
        Assert.assertEquals("super1", super1.getName());
        Assert.assertEquals("super2", prop.getValue().getName());
    }

    @Test
    public void testSetValueObjectSub() {
        ObjectProperty<TestSubClass> prop = new ObjectProperty<TestSubClass>(
                sub1, TestSubClass.class);
        Assert.assertEquals("Subclass: sub1", prop.getValue().getName());
        prop.setValue(new TestSubClass("sub2"));
        Assert.assertEquals("Subclass: sub1", sub1.getName());
        Assert.assertEquals("Subclass: sub2", prop.getValue().getName());
    }

    @Test
    public void testSetValueStringSuper() {
        ObjectProperty<TestSuperClass> prop = new ObjectProperty<TestSuperClass>(
                super1, TestSuperClass.class);
        Assert.assertEquals("super1", prop.getValue().getName());
        prop.setValue(new TestSuperClass("super2"));
        Assert.assertEquals("super1", super1.getName());
        Assert.assertEquals("super2", prop.getValue().getName());
    }

    @Test
    public void testSetValueStringSub() {
        ObjectProperty<TestSubClass> prop = new ObjectProperty<TestSubClass>(
                sub1, TestSubClass.class);
        Assert.assertEquals("Subclass: sub1", prop.getValue().getName());
        prop.setValue(new TestSubClass("sub2"));
        Assert.assertEquals("Subclass: sub1", sub1.getName());
        Assert.assertEquals("Subclass: sub2", prop.getValue().getName());
    }

    @Test
    public void testMixedGenerics() {
        ObjectProperty<TestSuperClass> prop = new ObjectProperty<TestSuperClass>(
                sub1);
        Assert.assertEquals("Subclass: sub1", prop.getValue().getName());
        Assert.assertEquals(prop.getType(), TestSubClass.class);
        // create correct subclass based on the runtime type of the instance
        // given to ObjectProperty constructor, which is a subclass of the type
        // parameter
        prop.setValue(new TestSubClass("sub2"));
        Assert.assertEquals("Subclass: sub2", prop.getValue().getName());
    }

}

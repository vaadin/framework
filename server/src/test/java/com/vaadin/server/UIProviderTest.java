/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;

/**
 * Tests for {@link UIProvider} class.
 * 
 * @author Vaadin Ltd
 */
public class UIProviderTest {

    @Test
    public void getAnnotationFor_widgetsetAnnotationForSubclass_annotationFound() {
        Assert.assertNotNull("Widgetset annotation is not found for subclass",
                UIProvider.getAnnotationFor(TestClass.class, Widgetset.class));
    }

    @Test
    public void getAnnotationFor_themeAnnotationForSubclass_annotationFound() {
        Assert.assertNotNull("Theme annotation is not found for subclass",
                UIProvider.getAnnotationFor(TestClass.class, Theme.class));
    }

    @Test
    public void getAnnotationFor_themeAnnotationForSubclass_annotationOverridden() {
        Assert.assertEquals(
                "Theme annotation is not overridden correctly in subclass",
                "c", UIProvider.getAnnotationFor(TestClass.class, Theme.class)
                        .value());
    }

    @Test
    public void getAnnotationFor_notInheritedAnnotationForSubclass_annotationFound() {
        Assert.assertNotNull(
                "TestAnnotation annotation is not found for subclass",
                UIProvider.getAnnotationFor(TestClass.class,
                        TestAnnotation.class));
    }

    @Test
    public void getAnnotationFor_directAnnotationForSubclass_annotationFound() {
        Assert.assertNotNull(
                "TestAnnotation1 annotation is not found for subclass",
                UIProvider.getAnnotationFor(TestClass.class,
                        TestAnnotation1.class));
    }

    @Test
    public void getAnnotationFor_annotationInheritedFromInterface_annotationFound() {
        Assert.assertNotNull(
                "Theme annotation is not inherited from interface", UIProvider
                        .getAnnotationFor(ClassImplementingInterface.class,
                                Theme.class));
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface TestAnnotation {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface TestAnnotation1 {

    }

    @Widgetset("a")
    @Theme("b")
    @TestAnnotation
    public static class TestSuperClass {

    }

    @TestAnnotation1
    @Theme("c")
    public static class TestClass extends TestSuperClass {

    }

    @Theme("d")
    public interface InterfaceWithAnnotation {
    }

    public static class ClassImplementingInterface implements
            InterfaceWithAnnotation {
    }

}

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

package com.vaadin.tests.server.navigator;

import junit.framework.TestCase;

import com.vaadin.navigator.Navigator.ClassBasedViewProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;

public class ClassBasedViewProviderTest extends TestCase {

    public static class TestView extends Label implements View {
        public String parameters = null;

        @Override
        public void enter(ViewChangeEvent event) {
            parameters = event.getParameters();
        }

    }

    public static class TestView2 extends TestView {

    }

    public void testCreateProviderWithNullName() throws Exception {
        try {
            new ClassBasedViewProvider(null, TestView.class);
            fail("Should not be able to create view provider with null name");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testCreateProviderWithEmptyStringName() throws Exception {
        new ClassBasedViewProvider("", TestView.class);
    }

    public void testCreateProviderNullViewClass() throws Exception {
        try {
            new ClassBasedViewProvider("test", null);
            fail("Should not be able to create view provider with null view class");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testViewNameGetter() throws Exception {
        ClassBasedViewProvider provider1 = new ClassBasedViewProvider("",
                TestView.class);
        assertEquals("View name should be empty", "", provider1.getViewName());

        ClassBasedViewProvider provider2 = new ClassBasedViewProvider("test",
                TestView.class);
        assertEquals("View name does not match", "test",
                provider2.getViewName());
    }

    public void testViewClassGetter() throws Exception {
        ClassBasedViewProvider provider = new ClassBasedViewProvider("test",
                TestView.class);
        assertEquals("Incorrect view class returned by getter", TestView.class,
                provider.getViewClass());
    }

    public void testGetViewNameForNullString() throws Exception {
        ClassBasedViewProvider provider = new ClassBasedViewProvider("test",
                TestView.class);
        assertNull("Received view name for null view string",
                provider.getViewName((String) null));
    }

    public void testGetViewNameForEmptyString() throws Exception {
        ClassBasedViewProvider provider1 = new ClassBasedViewProvider("",
                TestView.class);
        assertEquals(
                "Did not find view name for empty view string in a provider with empty string registered",
                "", provider1.getViewName(""));

        ClassBasedViewProvider provider2 = new ClassBasedViewProvider("test",
                TestView.class);
        assertNull(
                "Found view name for empty view string when none registered",
                provider2.getViewName(""));
    }

    public void testGetViewNameWithParameters() throws Exception {
        ClassBasedViewProvider provider = new ClassBasedViewProvider("test",
                TestView.class);
        assertEquals("Incorrect view name found for view string", "test",
                provider.getViewName("test"));
        assertEquals(
                "Incorrect view name found for view string ending with slash",
                "test", provider.getViewName("test/"));
        assertEquals(
                "Incorrect view name found for view string with parameters",
                "test", provider.getViewName("test/params/are/here"));
    }

    public void testGetView() throws Exception {
        ClassBasedViewProvider provider = new ClassBasedViewProvider("test",
                TestView.class);

        View view = provider.getView("test");
        assertNotNull("Did not get view from a provider", view);
        assertEquals("Incorrect view type", TestView.class, view.getClass());
    }

    public void testGetViewIncorrectViewName() throws Exception {
        ClassBasedViewProvider provider = new ClassBasedViewProvider("test",
                TestView.class);

        View view = provider.getView("test2");
        assertNull("Got view from a provider for incorrect view name", view);
    }

}

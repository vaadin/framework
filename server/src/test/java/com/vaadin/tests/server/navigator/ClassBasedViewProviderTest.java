package com.vaadin.tests.server.navigator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.vaadin.navigator.Navigator.ClassBasedViewProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;

public class ClassBasedViewProviderTest {

    public static class TestView extends Label implements View {
        public String parameters = null;

        @Override
        public void enter(ViewChangeEvent event) {
            parameters = event.getParameters();
        }

    }

    public static class TestView2 extends TestView {

    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateProviderWithNullName() throws Exception {
        new ClassBasedViewProvider(null, TestView.class);
        fail("Should not be able to create view provider with null name");
    }

    @Test
    public void testCreateProviderWithEmptyStringName() throws Exception {
        new ClassBasedViewProvider("", TestView.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateProviderNullViewClass() throws Exception {
        new ClassBasedViewProvider("test", null);
        fail("Should not be able to create view provider with null view class");
    }

    @Test
    public void testViewNameGetter() throws Exception {
        ClassBasedViewProvider provider1 = new ClassBasedViewProvider("",
                TestView.class);
        assertEquals("View name should be empty", "", provider1.getViewName());

        ClassBasedViewProvider provider2 = new ClassBasedViewProvider("test",
                TestView.class);
        assertEquals("View name does not match", "test",
                provider2.getViewName());
    }

    @Test
    public void testViewClassGetter() throws Exception {
        ClassBasedViewProvider provider = new ClassBasedViewProvider("test",
                TestView.class);
        assertEquals("Incorrect view class returned by getter", TestView.class,
                provider.getViewClass());
    }

    @Test
    public void testGetViewNameForNullString() throws Exception {
        ClassBasedViewProvider provider = new ClassBasedViewProvider("test",
                TestView.class);
        assertNull("Received view name for null view string",
                provider.getViewName(null));
    }

    @Test
    public void testGetViewNameForEmptyString() throws Exception {
        ClassBasedViewProvider provider1 = new ClassBasedViewProvider("",
                TestView.class);
        assertEquals(
                "Did not find view name for empty view string in a provider with empty string registered",
                "", provider1.getViewName(""));

        ClassBasedViewProvider provider2 = new ClassBasedViewProvider("test",
                TestView.class);
        assertNull("Found view name for empty view string when none registered",
                provider2.getViewName(""));
    }

    @Test
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

    @Test
    public void testGetView() throws Exception {
        ClassBasedViewProvider provider = new ClassBasedViewProvider("test",
                TestView.class);

        View view = provider.getView("test");
        assertNotNull("Did not get view from a provider", view);
        assertEquals("Incorrect view type", TestView.class, view.getClass());
    }

    @Test
    public void testGetViewIncorrectViewName() throws Exception {
        ClassBasedViewProvider provider = new ClassBasedViewProvider("test",
                TestView.class);

        View view = provider.getView("test2");
        assertNull("Got view from a provider for incorrect view name", view);
    }

}

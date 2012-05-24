/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.server.navigator;

import junit.framework.TestCase;

import com.vaadin.navigator.Navigator.ClassBasedViewProvider;
import com.vaadin.navigator.View;
import com.vaadin.ui.Label;

public class ClassBasedViewProviderTest extends TestCase {

    private ClassBasedViewProvider provider;

    public static class TestView extends Label implements View {
        public boolean initialized = false;
        public String parameters = null;

        public void init() {
            initialized = true;
        }

        public void navigateTo(String parameters, Object... internalParameters) {
            this.parameters = parameters;
        }

    }

    public static class TestView2 extends TestView {

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        provider = new ClassBasedViewProvider();
    }

    public void testAddViewWithNullName() throws Exception {
        try {
            provider.addView(null, TestView.class);
            fail("Should not be able to add view with null name");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testAddViewWithEmptyStringName() throws Exception {
        try {
            provider.addView("", TestView.class);
            fail("Should not be able to add view with empty name");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testAddViewNull() throws Exception {
        try {
            provider.addView("test", null);
            fail("Should not be able to add null view");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testAddViewSameName() throws Exception {
        try {
            provider.addView("test", TestView.class);
            provider.addView("test", TestView2.class);
            fail("Should not be able to add two views with same name");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testAddViewSameClass() throws Exception {
        try {
            provider.addView("test", TestView.class);
            provider.addView("test2", TestView.class);
            fail("Should not be able to add same view class with two different names");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testGetViewNameForNullString() throws Exception {
        assertNull(
                "Found view name for null view string in an empty view provider",
                provider.getViewName((String) null));

        provider.addView("test", TestView.class);
        assertNull("Found view name for null view string",
                provider.getViewName((String) null));
    }

    public void testGetViewNameForNullClass() throws Exception {
        assertNull("Found view name for null class",
                provider.getViewName((Class<View>) null));
    }

    public void testGetViewNameForEmptyString() throws Exception {
        assertNull(
                "Found view name for empty view string in an empty provider",
                provider.getViewName(""));
        provider.addView("test", TestView.class);
        assertNull("Found view name for empty view string",
                provider.getViewName(""));
    }

    public void testGetViewNameForClass() throws Exception {
        provider.addView("test", TestView.class);
        assertEquals("No view name found for view class", "test",
                provider.getViewName(TestView.class));
    }

    public void testGetViewNameWithParameters() throws Exception {
        provider.addView("test", TestView.class);
        assertEquals("Incorrect view name found for view string", "test",
                provider.getViewName("test"));
        assertEquals(
                "Incorrect view name found for view string ending with slash",
                "test", provider.getViewName("test/"));
        assertEquals(
                "Incorrect view name found for view string with parameters",
                "test", provider.getViewName("test/params/are/here"));
    }

    public void testGetViewNameMultipleRegisteredWithParameters()
            throws Exception {
        provider.addView("test", TestView.class);
        provider.addView("test2", TestView2.class);
        assertEquals("Incorrect view name found for view string", "test",
                provider.getViewName("test/test2/params"));
    }

    public void testGetViewNameNestedNames() throws Exception {
        provider.addView("test/subview", TestView2.class);
        provider.addView("test", TestView.class);
        assertEquals("Incorrect view name found for subview string",
                "test/subview", provider.getViewName("test/subview"));
        assertEquals(
                "Incorrect view name found for subview string with empty parameters",
                "test/subview", provider.getViewName("test/subview/"));
        assertEquals(
                "Incorrect view name found for subview string with parameters",
                "test/subview", provider.getViewName("test/subview/parameters"));
        assertEquals("Incorrect view name found for top level view string",
                "test", provider.getViewName("test"));
        assertEquals(
                "Incorrect view name found for top level view string with empty parameters",
                "test", provider.getViewName("test/"));
        assertEquals(
                "Incorrect view name found for top level view string with parameters starting like subview name",
                "test", provider.getViewName("test/subviewnothere"));
    }

    public void testGetViewClass() throws Exception {
        assertNull("View class found for empty view provider",
                provider.getViewClass("test"));
        provider.addView("test", TestView.class);
        assertEquals("View class not found", TestView.class,
                provider.getViewClass("test"));
        assertNull("View class found for unregistered view name",
                provider.getViewClass("test2"));
    }

    public void testGetViewSimple() throws Exception {
        assertNull("Found view in an empty view provider",
                provider.getViewName("test"));

        provider.addView("test", TestView.class);
        View view = provider.getView("test");
        assertNotNull("Did not get view from a provider", view);
        assertEquals("Incorrect view type", TestView.class, view.getClass());
        assertTrue("View not initialized", ((TestView) view).initialized);
    }

    public void testGetViewMultipleRegistered() throws Exception {
        provider.addView("test", TestView.class);
        provider.addView("test2", TestView2.class);
        assertEquals("Incorrect view type", TestView.class,
                provider.getView("test").getClass());
        assertEquals("Incorrect view type", TestView2.class,
                provider.getView("test2").getClass());
        assertEquals("Incorrect view type", TestView.class,
                provider.getView("test").getClass());
    }

    public void testRemoveView() throws Exception {
        provider.addView("test", TestView.class);
        assertNotNull("Did not get view from a provider",
                provider.getView("test"));
        provider.removeView("test");
        assertNull("View class found for removed view name",
                provider.getViewClass("test"));
        assertNull("View name found for removed view",
                provider.getViewName(TestView.class));
        // cached view?
        assertNull(
                "Received view instance from a provider after removing view type",
                provider.getView("test"));
    }

    public void testGetViewCached() throws Exception {
        provider.addView("test", TestView.class);
        View view1 = provider.getView("test");
        View view2 = provider.getView("test");
        assertSame("View instance not cached", view1, view2);
    }

}

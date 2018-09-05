package com.vaadin.tests.design;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.junit.Ignore;
import org.junit.Test;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignException;

public class DesignTest {

    @Test
    public void readStream() throws FileNotFoundException {
        Component root = Design.read(getClass()
                .getResourceAsStream("verticallayout-two-children.html"));
        VerticalLayout rootLayout = (VerticalLayout) root;
        assertEquals(VerticalLayout.class, root.getClass());

        assertEquals(2, rootLayout.getComponentCount());
        assertEquals(TextField.class, rootLayout.getComponent(0).getClass());
        assertEquals(Button.class, rootLayout.getComponent(1).getClass());
    }

    @Test(expected = DesignException.class)
    @Ignore("Feature needs to be fixed")
    public void readWithIncorrectRoot() throws FileNotFoundException {
        Design.read(
                getClass().getResourceAsStream("verticallayout-one-child.html"),
                new Panel());
    }

    public static class MyVerticalLayout extends VerticalLayout {

    }

    @Test
    public void readWithSubClassRoot() throws FileNotFoundException {
        Design.read(
                getClass().getResourceAsStream("verticallayout-one-child.html"),
                new MyVerticalLayout());
    }

    @Test
    public void writeComponentToStream() throws IOException {
        HorizontalLayout root = new HorizontalLayout(new Button("OK"),
                new Button("Cancel"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Design.write(root, baos);
        Component newRoot = Design
                .read(new ByteArrayInputStream(baos.toByteArray()));

        assertHierarchyEquals(root, newRoot);
    }

    @Test
    public void writeDesignContextToStream() throws IOException {
        DesignContext dc = Design.read(getClass()
                .getResourceAsStream("verticallayout-two-children.html"), null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Design.write(dc, baos);
        Component newRoot = Design
                .read(new ByteArrayInputStream(baos.toByteArray()));

        assertHierarchyEquals(dc.getRootComponent(), newRoot);
    }

    @Test(expected = DesignException.class)
    public void testDuplicateIds() throws FileNotFoundException {
        Design.read(getClass().getResourceAsStream("duplicate-ids.html"));
    }

    @Test(expected = DesignException.class)
    public void testDuplicateLocalIds() throws FileNotFoundException {
        Design.read(getClass().getResourceAsStream("duplicate-local-ids.html"));
    }

    private void assertHierarchyEquals(Component expected, Component actual) {
        if (expected.getClass() != actual.getClass()) {
            throw new AssertionError(
                    "Component classes do not match. Expected: "
                            + expected.getClass().getName() + ", was: "
                            + actual.getClass().getName());
        }

        if (expected instanceof HasComponents) {
            HasComponents expectedHC = (HasComponents) expected;
            HasComponents actualHC = (HasComponents) actual;
            Iterator<Component> eI = expectedHC.iterator();
            Iterator<Component> aI = actualHC.iterator();

            while (eI.hasNext()) {
                assertHierarchyEquals(eI.next(), aI.next());
            }
        }
    }
}

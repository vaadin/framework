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
package com.vaadin.tests.design;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.junit.Assert;
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
        Component root = Design
                .read(new FileInputStream(
                        "server/tests/src/com/vaadin/tests/design/verticallayout-two-children.html"));
        VerticalLayout rootLayout = (VerticalLayout) root;
        Assert.assertEquals(VerticalLayout.class, root.getClass());

        Assert.assertEquals(2, rootLayout.getComponentCount());
        Assert.assertEquals(TextField.class, rootLayout.getComponent(0)
                .getClass());
        Assert.assertEquals(Button.class, rootLayout.getComponent(1).getClass());
    }

    @Test(expected = DesignException.class)
    @Ignore("Feature needs to be fixed")
    public void readWithIncorrectRoot() throws FileNotFoundException {
        Design.read(
                new FileInputStream(
                        "server/tests/src/com/vaadin/tests/design/verticallayout-one-child.html"),
                new Panel());
    }

    public static class MyVerticalLayout extends VerticalLayout {

    }

    @Test
    public void readWithSubClassRoot() throws FileNotFoundException {
        Design.read(
                new FileInputStream(
                        "server/tests/src/com/vaadin/tests/design/verticallayout-one-child.html"),
                new MyVerticalLayout());
    }

    @Test
    public void writeComponentToStream() throws IOException {
        HorizontalLayout root = new HorizontalLayout(new Button("OK"),
                new Button("Cancel"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Design.write(root, baos);
        Component newRoot = Design.read(new ByteArrayInputStream(baos
                .toByteArray()));

        assertHierarchyEquals(root, newRoot);
    }

    @Test
    public void writeDesignContextToStream() throws IOException {
        DesignContext dc = Design
                .read(new FileInputStream(
                        "server/tests/src/com/vaadin/tests/design/verticallayout-two-children.html"),
                        null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Design.write(dc, baos);
        Component newRoot = Design.read(new ByteArrayInputStream(baos
                .toByteArray()));

        assertHierarchyEquals(dc.getRootComponent(), newRoot);
    }

    @Test(expected = DesignException.class)
    public void testDuplicateIds() throws FileNotFoundException {
        Design.read(new FileInputStream(
                "server/tests/src/com/vaadin/tests/design/duplicate-ids.html"));
    }

    @Test(expected = DesignException.class)
    public void testDuplicateLocalIds() throws FileNotFoundException {
        Design.read(new FileInputStream(
                "server/tests/src/com/vaadin/tests/design/duplicate-local-ids.html"));
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

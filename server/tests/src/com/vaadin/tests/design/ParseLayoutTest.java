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
import java.io.InputStream;

import junit.framework.TestCase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.junit.Test;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignException;

/**
 * A test for checking that parsing a layout preserves the IDs and the mapping
 * from prefixes to package names (for example <meta name=”package-mapping”
 * content=”my:com.addon.mypackage” />)
 * 
 * @since
 * @author Vaadin Ltd
 */
public class ParseLayoutTest extends TestCase {
    // The context is used for accessing the created component hierarchy.
    private DesignContext ctx;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ctx = Design
                .read(new FileInputStream(
                        "server/tests/src/com/vaadin/tests/design/testFile.html"),
                        null);
    }

    /*
     * Checks the component hierarchy created by parsing a design. Also checks
     * that components can be found by id and caption.
     */
    @Test
    public void testGettingByIDAndCaption() throws FileNotFoundException {
        findElements(ctx);
        checkHierarchy(ctx);
    }

    /*
     * Check that captions, ids and package mappings are preserved when an html
     * tree is generated from a DesignContext containing the component root of
     * the component hierarchy. Done by writing the design to a string and then
     * reading it back, not using the original context information after reading
     * the written design. The mapping from prefixes to package names is checked
     * directly from the html tree.
     */
    @Test
    public void testThatSerializationPreservesProperties() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Design.write(ctx, out);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        DesignContext newContext = Design.read(in, null);
        findElements(newContext);
        checkHierarchy(newContext);
        // Check the mapping from prefixes to package names using the html tree
        String[] expectedPrefixes = { "my" };
        String[] expectedPackageNames = { "com.addon.mypackage" };
        int index = 0;

        Document doc = Jsoup.parse(out.toString("UTF-8"));
        Element head = doc.head();
        for (Node child : head.childNodes()) {
            if ("meta".equals(child.nodeName())) {
                String name = child.attributes().get("name");
                if ("package-mapping".equals(name)) {
                    String content = child.attributes().get("content");
                    String[] parts = content.split(":");
                    assertEquals("Unexpected prefix.", expectedPrefixes[index],
                            parts[0]);
                    assertEquals("Unexpected package name.",
                            expectedPackageNames[index], parts[1]);
                    index++;
                }
            }
        }
        assertEquals("Unexpected number of prefix - package name pairs.", 1,
                index);
    }

    /*
     * Check that the field binding works if root instance with member fields is
     * passed to the LayoutHandler
     * 
     * @throws IOException
     */
    public void testFieldBinding() throws IOException {
        LayoutTemplate template = new LayoutTemplate();
        InputStream htmlFile = new FileInputStream(
                "server/tests/src/com/vaadin/tests/design/testFile.html");
        Design.read(htmlFile, template);
        assertNotNull(template.getFirstButton());
        assertNotNull(template.getSecondButton());
        assertNotNull(template.getYetanotherbutton());
        assertNotNull(template.getClickme());
        assertEquals("Native click me", template.getFirstButton().getCaption());
    }

    /*
     * Check that the field binding fails if some of the fields in the root
     * instance were not bound
     * 
     * @throws IOException
     */
    public void testUnboundFields() throws IOException {
        InvalidLayoutTemplate template = new InvalidLayoutTemplate();
        InputStream htmlFile = new FileInputStream(
                "server/tests/src/com/vaadin/tests/design/testFile.html");
        try {
            Design.read(htmlFile, template);
            // we are expecting an exception
            fail();
        } catch (DesignException e) {
            // expected
        }
    }

    /*
     * Checks that the correct components occur in the correct order in the
     * component hierarchy rooted at context.getComponentRoot().
     */
    private void checkHierarchy(DesignContext context) {
        Component root = context.getRootComponent();
        VerticalLayout vlayout = (VerticalLayout) root;
        int numComponents = vlayout.getComponentCount();
        assertEquals("Wrong number of child components", 3, numComponents);

        // Check the contents of the horizontal layout
        HorizontalLayout hlayout = (HorizontalLayout) vlayout.getComponent(0);
        int numHLComponents = hlayout.getComponentCount();
        assertEquals(5, numHLComponents);
        Label label = (Label) hlayout.getComponent(0);
        assertEquals("Wrong caption.", "FooBar", label.getCaption());
        NativeButton nb = (NativeButton) hlayout.getComponent(1);
        assertEquals("Wrong caption.", "Native click me", nb.getCaption());
        nb = (NativeButton) hlayout.getComponent(2);
        assertEquals("Wrong caption.", "Another button", nb.getCaption());
        nb = (NativeButton) hlayout.getComponent(3);
        assertEquals("Wrong caption.", "Yet another button", nb.getCaption());
        Button b = (Button) hlayout.getComponent(4);
        assertEquals("Wrong caption.", "Click me", b.getCaption());
        assertEquals("Wrong width.", 150f, b.getWidth());

        // Check the remaining two components of the vertical layout
        TextField tf = (TextField) vlayout.getComponent(1);
        assertEquals("Wrong caption.", "Text input", tf.getCaption());
        TextArea ta = (TextArea) vlayout.getComponent(2);
        assertEquals("Wrong caption.", "Text area", ta.getCaption());
        assertEquals("Wrong width.", 300f, ta.getWidth());
        assertEquals("Wrong height.", 200f, ta.getHeight());
    }

    /*
     * Checks that the correct elements are found using a local id, a global id
     * or a caption.
     */
    private void findElements(DesignContext designContext) {
        NativeButton firstButton = (NativeButton) designContext
                .getComponentByLocalId("firstButton");
        NativeButton firstButton_2 = (NativeButton) designContext
                .getComponentByCaption("Native click me");
        NativeButton secondButton = (NativeButton) designContext
                .getComponentById("secondButton");
        NativeButton secondButton_2 = (NativeButton) designContext
                .getComponentByLocalId("localID");
        NativeButton secondButton_3 = (NativeButton) designContext
                .getComponentByCaption("Another button");
        NativeButton thirdButton = (NativeButton) designContext
                .getComponentByCaption("Yet another button");
        // Check that the first button was found using both identifiers.
        assertEquals("The found buttons should be identical but they are not.",
                firstButton, firstButton_2);
        assertTrue("The found button element is incorrect.", firstButton
                .getCaption().equals("Native click me"));
        // Check that the second button was found using all three identifiers.
        assertEquals("The found buttons should be identical but they are not.",
                secondButton, secondButton_2);
        assertEquals("The found buttons should be identical but they are not.",
                secondButton_2, secondButton_3);
        assertTrue("The found button is incorrect.", secondButton.getCaption()
                .equals("Another button"));
        // Check that the third button was found by caption.
        assertTrue("The found button is incorrect.", thirdButton.getCaption()
                .equals("Yet another button"));
    }

}

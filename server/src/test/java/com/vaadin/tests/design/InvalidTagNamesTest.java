package com.vaadin.tests.design;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;

import org.junit.Test;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignException;

public class InvalidTagNamesTest {

    @Test(expected = DesignException.class)
    public void tagWithoutDash() {
        readDesign("<vbutton>foo</vbutton>");
    }

    @Test
    public void emptyTag() {
        // JSoup parses empty tags into text nodes
        Component c = readDesign("<>foo</>");
        assertNull(c);
    }

    @Test(expected = DesignException.class)
    public void onlyPrefix() {
        readDesign("<vaadin->foo</vaadin->");
    }

    @Test
    public void onlyClass() {
        // JSoup will refuse to parse tags starting with - and convert them into
        // text nodes instead
        Component c = readDesign("<-v>foo</-v>");
        assertNull(c);
    }

    @Test(expected = DesignException.class)
    public void unknownClass() {
        readDesign("<vaadin-unknownbutton>foo</vaadin-unknownbutton>");
    }

    @Test(expected = DesignException.class)
    public void unknownTag() {
        readDesign("<x-button></x-button>");
    }

    // @Test(expected = DesignException.class)
    // This is a side effect of not actively checking for invalid input. Will be
    // parsed currently as <vaadin-button> (this should not be considered API)
    public void tagEndsInDash() {
        Component c = readDesign("<vaadin-button-></vaadin-button->");
        assertTrue(c.getClass() == Button.class);
    }

    // @Test(expected = DesignException.class)
    // This is a side effect of not actively checking for invalid input. Will be
    // parsed currently as <vaadin-button> (this should not be considered API)
    public void tagEndsInTwoDashes() {
        Component c = readDesign("<vaadin-button--></vaadin-button-->");
        assertTrue(c.getClass() == Button.class);
    }

    // @Test(expected = DesignException.class)
    // This is a side effect of not actively checking for invalid input. Will be
    // parsed currently as <vaadin-button> (this should not be considered API)
    public void tagWithTwoDashes() {
        Component c = readDesign("<vaadin--button></vaadin--button>");
        assertTrue(c.getClass() == Button.class);
    }

    @Test(expected = DesignException.class)
    public void specialCharacters() {
        readDesign("<vaadin-button-&!#></vaadin-button-&!#>");
    }

    private Component readDesign(String string) {
        return Design.read(new ByteArrayInputStream(string.getBytes(UTF_8)));
    }
}

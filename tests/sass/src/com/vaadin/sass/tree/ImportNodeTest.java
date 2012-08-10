/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.sass.tree;

import org.junit.Assert;
import org.junit.Test;

import com.steadystate.css.parser.SACMediaListImpl;

public class ImportNodeTest {
    @Test
    public void testIsPureCssImportShouldReturnTrueWhenIsURL() {
        ImportNode node = new ImportNode("", null, true);
        Assert.assertTrue(node.isPureCssImport());
    }

    @Test
    public void testIsPureCssImportShouldReturnTrueWhenStartsWithHttp() {
        ImportNode node = new ImportNode("http://abc", null, false);
        Assert.assertTrue(node.isPureCssImport());
    }

    @Test
    public void testIsPureCssImportShouldReturnTrueWhenEndsWithCss() {
        ImportNode node = new ImportNode("abc.css", null, false);
        Assert.assertTrue(node.isPureCssImport());
    }

    @Test
    public void testIsPureCssImportShouldReturnTrueWhenHasMediaQueries() {
        SACMediaListImpl ml = new SACMediaListImpl();
        ml.add("screen");
        ImportNode node = new ImportNode("", ml, false);
        Assert.assertTrue(node.isPureCssImport());
    }

    @Test
    public void testIsPureCssImportShouldReturnFalseInOtherCases() {
        ImportNode node = new ImportNode("", null, false);
        Assert.assertFalse(node.isPureCssImport());
    }

    @Test
    public void testToStringWhenIsURL() {
        ImportNode node = new ImportNode("test", null, true);
        Assert.assertEquals("@import url(test);", node.toString());
    }

    @Test
    public void testToStringWhenIsNotURL() {
        ImportNode node = new ImportNode("test", null, false);
        Assert.assertEquals("@import \"test\";", node.toString());
    }

    @Test
    public void testToStringWithMediaQueries() {
        SACMediaListImpl ml = new SACMediaListImpl();
        ml.add("screen");
        ImportNode node = new ImportNode("test", ml, true);
        Assert.assertEquals("@import url(test) screen;", node.toString());
    }
}

package com.vaadin.shared.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

public class SharedUtilTest {

    @Test
    public void trailingSlashIsTrimmed() {
        assertThat(SharedUtil.trimTrailingSlashes("/path/"), is("/path"));
    }

    @Test
    public void noTrailingSlashForTrimming() {
        assertThat(SharedUtil.trimTrailingSlashes("/path"), is("/path"));
    }

    @Test
    public void trailingSlashesAreTrimmed() {
        assertThat(SharedUtil.trimTrailingSlashes("/path///"), is("/path"));
    }

    @Test
    public void emptyStringIsHandled() {
        assertThat(SharedUtil.trimTrailingSlashes(""), is(""));
    }

    @Test
    public void rootSlashIsTrimmed() {
        assertThat(SharedUtil.trimTrailingSlashes("/"), is(""));
    }

    @Test
    public void camelCaseToHumanReadable() {
        Assert.assertEquals("First Name",
                SharedUtil.camelCaseToHumanFriendly("firstName"));
        Assert.assertEquals("First Name",
                SharedUtil.camelCaseToHumanFriendly("first name"));
        Assert.assertEquals("First Name2",
                SharedUtil.camelCaseToHumanFriendly("firstName2"));
        Assert.assertEquals("First",
                SharedUtil.camelCaseToHumanFriendly("first"));
        Assert.assertEquals("First",
                SharedUtil.camelCaseToHumanFriendly("First"));
        Assert.assertEquals("Some XYZ Abbreviation",
                SharedUtil.camelCaseToHumanFriendly("SomeXYZAbbreviation"));

        // Javadoc examples
        Assert.assertEquals("My Bean Container",
                SharedUtil.camelCaseToHumanFriendly("MyBeanContainer"));
        Assert.assertEquals("Awesome URL Factory",
                SharedUtil.camelCaseToHumanFriendly("AwesomeURLFactory"));
        Assert.assertEquals("Some Uri Action",
                SharedUtil.camelCaseToHumanFriendly("SomeUriAction"));

    }

    @Test
    public void splitCamelCase() {
        assertCamelCaseSplit("firstName", "first", "Name");
        assertCamelCaseSplit("fooBar", "foo", "Bar");
        assertCamelCaseSplit("fooBar", "foo", "Bar");
        assertCamelCaseSplit("fBar", "f", "Bar");
        assertCamelCaseSplit("FBar", "F", "Bar");
        assertCamelCaseSplit("MYCdi", "MY", "Cdi");
        assertCamelCaseSplit("MyCDIUI", "My", "CDIUI");
        assertCamelCaseSplit("MyCDIUITwo", "My", "CDIUI", "Two");
        assertCamelCaseSplit("first name", "first", "name");

    }

    private void assertCamelCaseSplit(String camelCaseString, String... parts) {
        String[] splitParts = SharedUtil.splitCamelCase(camelCaseString);
        Assert.assertArrayEquals(parts, splitParts);
    }

    @Test
    public void join() {
        String s1 = "foo-bar-baz";
        String s2 = "foo--bar";

        Assert.assertEquals("foobarbaz", SharedUtil.join(s1.split("-"), ""));
        Assert.assertEquals("foo!bar!baz", SharedUtil.join(s1.split("-"), "!"));
        Assert.assertEquals("foo!!bar!!baz",
                SharedUtil.join(s1.split("-"), "!!"));

        Assert.assertEquals("foo##bar", SharedUtil.join(s2.split("-"), "#"));
    }

    @Test
    public void dashSeparatedToCamelCase() {
        Assert.assertEquals(null, SharedUtil.dashSeparatedToCamelCase(null));
        Assert.assertEquals("", SharedUtil.dashSeparatedToCamelCase(""));
        Assert.assertEquals("foo", SharedUtil.dashSeparatedToCamelCase("foo"));
        Assert.assertEquals("fooBar",
                SharedUtil.dashSeparatedToCamelCase("foo-bar"));
        Assert.assertEquals("fooBar",
                SharedUtil.dashSeparatedToCamelCase("foo--bar"));
        Assert.assertEquals("fooBarBaz",
                SharedUtil.dashSeparatedToCamelCase("foo-bar-baz"));
        Assert.assertEquals("fooBarBaz",
                SharedUtil.dashSeparatedToCamelCase("foo-Bar-Baz"));
    }

    @Test
    public void methodUppercaseWithTurkishLocale() {
        Locale defaultLocale = Locale.getDefault();
        try {
            Locale.setDefault(new Locale("tr", "TR"));
            Assert.assertEquals("Integer", SharedUtil.capitalize("integer"));
        } finally {
            Locale.setDefault(defaultLocale);
        }
    }

    @Test
    public void duplicatesInArray() {
        Assert.assertTrue(
                SharedUtil.containsDuplicates(new Object[] { "a", "a" }));
        Assert.assertTrue(
                SharedUtil.containsDuplicates(new Object[] { "a", "b", "a" }));
        Assert.assertTrue(SharedUtil
                .containsDuplicates(new Object[] { "a", "b", "a", "b" }));
        Assert.assertTrue(
                SharedUtil.containsDuplicates(new Object[] { 1, "b", 1 }));

        Assert.assertFalse(SharedUtil.containsDuplicates(new Object[] {}));
        Assert.assertFalse(SharedUtil.containsDuplicates(new Object[] { "a" }));
        Assert.assertFalse(
                SharedUtil.containsDuplicates(new Object[] { "a", "b" }));
        Assert.assertFalse(
                SharedUtil.containsDuplicates(new Object[] { "1", 1 }));
    }

    @Test
    public void getDuplicates() {
        Assert.assertEquals("", SharedUtil.getDuplicates(new Object[] { "a" }));
        Assert.assertEquals("a",
                SharedUtil.getDuplicates(new Object[] { "a", "a" }));
        Assert.assertEquals("a, b",
                SharedUtil.getDuplicates(new Object[] { "a", "b", "a", "b" }));
        Assert.assertEquals("a, b, c", SharedUtil
                .getDuplicates(new Object[] { "c", "a", "b", "a", "b", "c" }));
        Assert.assertEquals("1.2",
                SharedUtil.getDuplicates(new Object[] { 1.2, "a", 1.2 }));
    }

    @Test
    public void propertyIdToHumanFriendly() {
        Assert.assertEquals("", SharedUtil.propertyIdToHumanFriendly(""));
        Assert.assertEquals("First Name",
                SharedUtil.propertyIdToHumanFriendly("firstName"));
        Assert.assertEquals("First Name",
                SharedUtil.propertyIdToHumanFriendly("FirstName"));
        Assert.assertEquals("First Name",
                SharedUtil.propertyIdToHumanFriendly("FIRST_NAME"));
        Assert.assertEquals("Firstname",
                SharedUtil.propertyIdToHumanFriendly("FIRSTNAME"));

        Assert.assertEquals("2015 Q3",
                SharedUtil.propertyIdToHumanFriendly("2015_Q3"));
        Assert.assertEquals("Column X",
                SharedUtil.propertyIdToHumanFriendly("_COLUMN_X"));
        Assert.assertEquals("Column X",
                SharedUtil.propertyIdToHumanFriendly("__COLUMN_X"));
        Assert.assertEquals("1column Foobar",
                SharedUtil.propertyIdToHumanFriendly("1COLUMN_FOOBAR"));
        Assert.assertEquals("Result 2015",
                SharedUtil.propertyIdToHumanFriendly("RESULT_2015"));
        Assert.assertEquals("2015result",
                SharedUtil.propertyIdToHumanFriendly("2015RESULT"));
        Assert.assertEquals("Result2015",
                SharedUtil.propertyIdToHumanFriendly("RESULT2015"));

    }
}

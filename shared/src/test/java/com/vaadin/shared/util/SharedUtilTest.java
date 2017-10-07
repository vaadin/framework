package com.vaadin.shared.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

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
        assertEquals("First Name",
                SharedUtil.camelCaseToHumanFriendly("firstName"));
        assertEquals("First Name",
                SharedUtil.camelCaseToHumanFriendly("first name"));
        assertEquals("First Name2",
                SharedUtil.camelCaseToHumanFriendly("firstName2"));
        assertEquals("First", SharedUtil.camelCaseToHumanFriendly("first"));
        assertEquals("First", SharedUtil.camelCaseToHumanFriendly("First"));
        assertEquals("Some XYZ Abbreviation",
                SharedUtil.camelCaseToHumanFriendly("SomeXYZAbbreviation"));

        // Javadoc examples
        assertEquals("My Bean Container",
                SharedUtil.camelCaseToHumanFriendly("MyBeanContainer"));
        assertEquals("Awesome URL Factory",
                SharedUtil.camelCaseToHumanFriendly("AwesomeURLFactory"));
        assertEquals("Some Uri Action",
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
        assertArrayEquals(parts, splitParts);
    }

    @Test
    public void join() {
        String s1 = "foo-bar-baz";
        String s2 = "foo--bar";

        assertEquals("foobarbaz", SharedUtil.join(s1.split("-"), ""));
        assertEquals("foo!bar!baz", SharedUtil.join(s1.split("-"), "!"));
        assertEquals("foo!!bar!!baz", SharedUtil.join(s1.split("-"), "!!"));

        assertEquals("foo##bar", SharedUtil.join(s2.split("-"), "#"));
    }

    @Test
    public void dashSeparatedToCamelCase() {
        assertEquals(null, SharedUtil.dashSeparatedToCamelCase(null));
        assertEquals("", SharedUtil.dashSeparatedToCamelCase(""));
        assertEquals("foo", SharedUtil.dashSeparatedToCamelCase("foo"));
        assertEquals("fooBar", SharedUtil.dashSeparatedToCamelCase("foo-bar"));
        assertEquals("fooBar", SharedUtil.dashSeparatedToCamelCase("foo--bar"));
        assertEquals("fooBarBaz",
                SharedUtil.dashSeparatedToCamelCase("foo-bar-baz"));
        assertEquals("fooBarBaz",
                SharedUtil.dashSeparatedToCamelCase("foo-Bar-Baz"));
    }

    @Test
    public void methodUppercaseWithTurkishLocale() {
        Locale defaultLocale = Locale.getDefault();
        try {
            Locale.setDefault(new Locale("tr", "TR"));
            assertEquals("Integer", SharedUtil.capitalize("integer"));
            assertEquals("I", SharedUtil.capitalize("i"));
        } finally {
            Locale.setDefault(defaultLocale);
        }
    }

    @Test
    public void duplicatesInArray() {
        assertTrue(SharedUtil.containsDuplicates(new Object[] { "a", "a" }));
        assertTrue(
                SharedUtil.containsDuplicates(new Object[] { "a", "b", "a" }));
        assertTrue(SharedUtil
                .containsDuplicates(new Object[] { "a", "b", "a", "b" }));
        assertTrue(SharedUtil.containsDuplicates(new Object[] { 1, "b", 1 }));

        assertFalse(SharedUtil.containsDuplicates(new Object[] {}));
        assertFalse(SharedUtil.containsDuplicates(new Object[] { "a" }));
        assertFalse(SharedUtil.containsDuplicates(new Object[] { "a", "b" }));
        assertFalse(SharedUtil.containsDuplicates(new Object[] { "1", 1 }));
    }

    @Test
    public void getDuplicates() {
        assertEquals("", SharedUtil.getDuplicates(new Object[] { "a" }));
        assertEquals("a", SharedUtil.getDuplicates(new Object[] { "a", "a" }));
        assertEquals("a, b",
                SharedUtil.getDuplicates(new Object[] { "a", "b", "a", "b" }));
        assertEquals("a, b, c", SharedUtil
                .getDuplicates(new Object[] { "c", "a", "b", "a", "b", "c" }));
        assertEquals("1.2",
                SharedUtil.getDuplicates(new Object[] { 1.2, "a", 1.2 }));
    }

    @Test
    public void propertyIdToHumanFriendly() {
        assertEquals("", SharedUtil.propertyIdToHumanFriendly(""));
        assertEquals("First Name",
                SharedUtil.propertyIdToHumanFriendly("firstName"));
        assertEquals("First Name",
                SharedUtil.propertyIdToHumanFriendly("FirstName"));
        assertEquals("First Name",
                SharedUtil.propertyIdToHumanFriendly("FIRST_NAME"));
        assertEquals("Firstname",
                SharedUtil.propertyIdToHumanFriendly("FIRSTNAME"));

        assertEquals("2015 Q3",
                SharedUtil.propertyIdToHumanFriendly("2015_Q3"));
        assertEquals("Column X",
                SharedUtil.propertyIdToHumanFriendly("_COLUMN_X"));
        assertEquals("Column X",
                SharedUtil.propertyIdToHumanFriendly("__COLUMN_X"));
        assertEquals("1column Foobar",
                SharedUtil.propertyIdToHumanFriendly("1COLUMN_FOOBAR"));
        assertEquals("Result 2015",
                SharedUtil.propertyIdToHumanFriendly("RESULT_2015"));
        assertEquals("2015result",
                SharedUtil.propertyIdToHumanFriendly("2015RESULT"));
        assertEquals("Result2015",
                SharedUtil.propertyIdToHumanFriendly("RESULT2015"));

    }
}

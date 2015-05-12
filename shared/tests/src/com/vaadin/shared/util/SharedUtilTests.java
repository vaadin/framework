package com.vaadin.shared.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Assert;
import org.junit.Test;

public class SharedUtilTests {

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
}

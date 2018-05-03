package com.vaadin.tests.serialization;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class EncodeResultDisplayTest extends SingleBrowserTest {
    @Test
    public void testEncodeResults() {
        openTestURL();

        int logRow = 0;

        Assert.assertEquals("Void: null", getLogRow(logRow++));
        Assert.assertEquals("SimpleTestBean: {\"value\":5}",
                getLogRow(logRow++));
        Assert.assertEquals("List: [\"Three\",\"Four\"]", getLogRow(logRow++));
        Assert.assertEquals("String[]: [\"One\",\"Two\"]", getLogRow(logRow++));
        Assert.assertEquals("Double: 2.2", getLogRow(logRow++));
        // PhantomJS likes to add a couple of extra decimals
        Assert.assertTrue(getLogRow(logRow++).startsWith("Float: 1.1"));
        Assert.assertEquals("Long: 2147483648", getLogRow(logRow++));
        Assert.assertEquals("Integer: 3", getLogRow(logRow++));
        Assert.assertEquals("Byte: 1", getLogRow(logRow++));
        Assert.assertEquals("Character: \"v\"", getLogRow(logRow++));
        Assert.assertEquals("String: \"My string\"", getLogRow(logRow++));
    }
}

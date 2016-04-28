package com.vaadin.tests.applicationcontext;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ChangeSessionIdTest extends MultiBrowserTest {

    @Test
    public void testSessionIdChange() throws Exception {
        openTestURL();
        checkLogMatches("1. Session id: .*");
        $(ButtonElement.class).first().click();
        checkLogMatches("2. Session id changed successfully from .* to .*");
        $(ButtonElement.class).get(1).click();
        checkLogMatches("3. Session id: .*");
    }

    private void checkLogMatches(String expected) {
        String actual = getLogRow(0);
        Assert.assertTrue(String.format(
                "Unexpected log row.\n expected format: '%s'\n was: '%s'",
                expected, actual), actual.matches(expected));
    }
}

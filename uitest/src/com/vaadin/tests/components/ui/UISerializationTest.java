package com.vaadin.tests.components.ui;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class UISerializationTest extends SingleBrowserTest {

    @Test
    @Ignore
    // Broken on all browsers since 9696e6c3e7e952b66ac3f5c9ddc3dfca4233451e
    public void tb2test() throws Exception {
        openTestURL();
        $(ButtonElement.class).first().click();
        assertThat(getLogRow(0), startsWith("3. Diff states match, size: "));
        assertThat(getLogRow(1), startsWith("2. Deserialized UI in "));
        assertThat(
                getLogRow(2),
                allOf(startsWith("1. Serialized UI in"),
                        containsString(" into "), endsWith(" bytes")));
    }
}

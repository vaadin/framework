package com.vaadin.tests.components.ui;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class UISerializationTest extends SingleBrowserTest {

    @Test
    public void uiIsSerialized() throws Exception {
        openTestURL();

        serialize();

        assertThat(getLogRow(0), startsWith("3. Diff states match, size: "));
        assertThat(getLogRow(1), startsWith("2. Deserialized UI in "));
        assertThat(
                getLogRow(2),
                allOf(startsWith("1. Serialized UI in"),
                        containsString(" into "), endsWith(" bytes")));
    }

    private void serialize() {
        $(ButtonElement.class).first().click();
    }
}

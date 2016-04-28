package com.vaadin.tests.application;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ThreadLocalInstancesTest extends MultiBrowserTest {
    @Test
    public void tb2test() throws Exception {
        openTestURL();
        $(ButtonElement.class).first().click();
        assertLogText("1. some app in class init", 15);
        assertLogText("2. null root in class init", 14);
        assertLogText("3. some app in app constructor", 13);
        assertLogText("4. null root in app constructor", 12);
        assertLogText("5. some app in app init", 11);
        assertLogText("6. null root in app init", 10);
        assertLogText("7. some app in root init", 9);
        assertLogText("8. this root in root init", 8);
        assertLogText("9. some app in root paint", 7);
        assertLogText("10. this root in root paint", 6);
        assertLogText("11. some app in background thread", 5);
        assertLogText("12. this root in background thread", 4);
        assertLogText("13. some app in resource handler", 3);
        assertLogText("14. this root in resource handler", 2);
        assertLogText("15. some app in button listener", 1);
        assertLogText("16. this root in button listener", 0);
    }

    private void assertLogText(String expected, int index) {
        Assert.assertEquals("Incorrect log text,", expected, getLogRow(index));
    }
}

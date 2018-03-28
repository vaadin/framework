package com.vaadin.tests.debug;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class ProfilerZeroOverheadTest extends SingleBrowserTest {
    @Test
    public void testZeroOverhead() {
        openTestURL();

        /*
         * This will get the compiled JS for the
         * ProfilerCompilationCanary.canaryWithProfiler method. Expected to be
         * something like "function canaryWithProfiler(){\n}" with a PRETTY
         * non-draft widgetset.
         */
        String canaryMethodString = findElement(By.className("gwt-Label"))
                .getText();

        // Only look at the method body to avoid false negatives if e.g.
        // obfuscation changes
        int bodyStart = canaryMethodString.indexOf('{');
        int bodyEnd = canaryMethodString.lastIndexOf('}');

        String methodBody = canaryMethodString.substring(bodyStart + 1,
                bodyEnd);

        // Method body shouldn't contain anything else than whitespace
        if (!methodBody.replaceAll("\\s", "").isEmpty()) {
            fail("Canary method is not empty: " + canaryMethodString);
        }
    }
}

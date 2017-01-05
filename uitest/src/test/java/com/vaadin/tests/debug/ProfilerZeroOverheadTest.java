/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.debug;

import org.junit.Assert;
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
            Assert.fail("Canary method is not empty: " + canaryMethodString);
        }
    }
}

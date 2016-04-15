/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.tests.resources;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class SpecialCharsInThemeResources extends SingleBrowserTest {

    @Test
    public void loadThemeResource() {
        loadResource("/VAADIN/themes/tests-tickets/ordinary.txt");
        checkSource();
    }

    @Test
    public void loadThemeResourceWithPercentage() {
        loadResource("/VAADIN/themes/tests-tickets/percentagein%2520name.txt");
        checkSource();
    }

    @Test
    public void loadThemeResourceWithSpecialChars() {
        loadResource("/VAADIN/themes/tests-tickets/folder%20with%20space/resource%20with%20special%20$chars@.txt");
        checkSource();
    }

    private void loadResource(String path) {
        getDriver().get(getBaseURL() + path);
    }

    private void checkSource() {
        String source = getDriver().getPageSource();
        Assert.assertTrue("Incorrect contents (was: " + source + ")",
                source.contains("Just ordinary contents here"));
    }
}

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
package com.vaadin.tests.tb3;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Test which uses theme returned by {@link #getTheme()} for running the test
 */
@RunWith(ParameterizedTB3Runner.class)
public abstract class MultiBrowserThemeTest extends MultiBrowserTest {

    public static final List<String> themesToTest = Collections
            .unmodifiableList(Arrays.asList(new String[] { "valo", "reindeer",
                    "runo", "chameleon", "base" }));

    private String theme;

    public void setTheme(String theme) {
        this.theme = theme;
    }

    @Parameters
    public static Collection<String> getThemes() {
        return themesToTest;
    }

    @Override
    protected boolean requireWindowFocusForIE() {
        return true;
    }

    @Override
    protected void openTestURL(Class<?> uiClass, String... parameters) {
        Set<String> params = new HashSet<>(Arrays.asList(parameters));
        params.add("theme=" + theme);
        super.openTestURL(uiClass, params.toArray(new String[params.size()]));
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        List<DesiredCapabilities> browsersToTest = getBrowsersExcludingPhantomJS();
        browsersToTest.add(PHANTOMJS2());
        return browsersToTest;
    }
}

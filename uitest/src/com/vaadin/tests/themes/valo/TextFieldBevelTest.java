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
package com.vaadin.tests.themes.valo;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for $v-textfield-bevel value when $v-bevel is unset.
 * 
 * @author Vaadin Ltd
 */
public class TextFieldBevelTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        List<DesiredCapabilities> browsers = super.getBrowsersToTest();

        // IE8 doesn't support box-shadow.
        browsers.remove(Browser.IE8.getDesiredCapabilities());

        return browsers;
    }

    @Test
    public void bevelChangesBoxShadow() {
        openTestURL();
        String boxShadowWithBevel = getBoxShadow();

        openTestUrlWithoutBevel();
        String boxShadowWithoutBevel = getBoxShadow();

        assertThat(boxShadowWithBevel, is(not(boxShadowWithoutBevel)));
    }

    private void openTestUrlWithoutBevel() {
        getDriver().get(
                getTestUrl()
                        + "$"
                        + TextFieldBevel.ValoDefaultTextFieldBevel.class
                                .getSimpleName() + "?restartApplication");
    }

    private String getBoxShadow() {
        return $(TextFieldElement.class).first().getCssValue("box-shadow");
    }
}

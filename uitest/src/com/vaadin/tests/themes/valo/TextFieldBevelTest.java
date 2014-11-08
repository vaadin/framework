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

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for $v-textfield-bevel value when $v-bevel is unset.
 * 
 * @author Vaadin Ltd
 */
public class TextFieldBevelTest extends MultiBrowserTest {

    @Test
    public void testTextFieldBevel() {
        String url = getTestUrl();
        StringBuilder defaultValoUi = new StringBuilder(
                TextFieldBevel.class.getSimpleName());
        defaultValoUi.append('$');
        defaultValoUi.append(TextFieldBevel.ValoDefaultTextFieldBevel.class
                .getSimpleName());
        url = url.replace(TextFieldBevel.class.getSimpleName(),
                defaultValoUi.toString());
        getDriver().get(url);

        String defaultBoxShadow = $(TextFieldElement.class).first()
                .getCssValue("box-shadow");

        if (url.contains("restartApplication")) {
            openTestURL();
        } else {
            openTestURL("restartApplication");
        }

        String boxShadow = $(TextFieldElement.class).first().getCssValue(
                "box-shadow");

        Assert.assertNotEquals(
                "Set v-bevel to 'false' doesn't affect 'v-textfield-bevel' value",
                defaultBoxShadow, boxShadow);
    }
}

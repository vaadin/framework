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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for centered image icon in button with 'icon-align-top' style.
 * 
 * @author Vaadin Ltd
 */
public class AlignTopIconInButtonTest extends MultiBrowserTest {

    @Test
    public void iconIsCenteredInsideButton() {
        openTestURL();

        WebElement wrapper = findElement(By.className("v-button-wrap"));
        WebElement icon = wrapper.findElement(By.className("v-icon"));
        int leftSpace = icon.getLocation().getX()
                - wrapper.getLocation().getX();
        int rightSpace = wrapper.getLocation().getX()
                + wrapper.getSize().getWidth() - icon.getLocation().getX()
                - icon.getSize().getWidth();

        assertThat(Math.abs(rightSpace - leftSpace), is(lessThanOrEqualTo(2)));
    }
}

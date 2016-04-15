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
package com.vaadin.tests.components.gridlayout;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridLayoutElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public abstract class GridLayoutBaseTest extends MultiBrowserTest {
    @Test
    public void cellSizesAreCorrectlyCalculated() {
        openTestURL();

        hideMiddleRowAndColumn();
        final List<WebElement> slots4x4 = getSlots(1);

        waitUntilColumnAndRowAreHidden(slots4x4);
        final List<WebElement> slots5x5 = getSlots(0);

        for (int i = 0; i < slots5x5.size(); i++) {
            WebElement compared = slots5x5.get(i);
            WebElement actual = slots4x4.get(i);
            assertEquals("Different top coordinate for element " + i,
                    compared.getCssValue("top"), actual.getCssValue("top"));
            assertEquals("Different left coordinate for element " + i,
                    compared.getCssValue("left"), actual.getCssValue("left"));
        }
    }

    private void waitUntilColumnAndRowAreHidden(final List<WebElement> slots4x4) {
        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return getSlots(0).size() == slots4x4.size();
            }
        }, 5);
    }

    private List<WebElement> getSlots(int index) {
        GridLayoutElement layout = $(GridLayoutElement.class).get(index);

        return layout.findElements(By.className("v-gridlayout-slot"));
    }

    private void hideMiddleRowAndColumn() {
        $(ButtonElement.class).first().click();
    }
}

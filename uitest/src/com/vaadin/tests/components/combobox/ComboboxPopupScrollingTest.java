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
package com.vaadin.tests.components.combobox;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboboxPopupScrollingTest extends MultiBrowserTest {

    @Test
    public void testNoScrollbarsValo() {
        testNoScrollbars("valo");
    }

    @Test
    public void testNoScrollbarsChameleon() {
        testNoScrollbars("chameleon");
    }

    @Test
    public void testNoScrollbarsRuno() {
        testNoScrollbars("runo");
    }

    @Test
    public void testNoScrollbarsReindeer() {
        testNoScrollbars("reindeer");
    }

    private void testNoScrollbars(String theme) {
        openTestURL("theme=" + theme);

        for (CustomComboBoxElement cb : $(CustomComboBoxElement.class).all()) {
            String caption = cb.getCaption();
            cb.openPopup();
            WebElement popup = cb.getSuggestionPopup();
            WebElement scrollable = popup.findElement(By
                    .className("v-filterselect-suggestmenu"));
            assertNoHorizontalScrollbar(scrollable, caption);
            assertNoVerticalScrollbar(scrollable, caption);
        }
    }

}

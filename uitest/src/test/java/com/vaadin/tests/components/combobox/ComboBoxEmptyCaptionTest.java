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
package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 *
 */
public class ComboBoxEmptyCaptionTest extends MultiBrowserTest {

    @Before
    public void setUp() {
        openTestURL();
    }

    @Test
    public void emptyItemCaption() {
        ComboBoxElement combo = $(ComboBoxElement.class).first();
        // empty string in caption becomes &nbsp; because of #7506
        ensureSuggestions(combo, " ", "item1", "item2", "item3", "item4",
                "item5", "item6", "item7", "item8", "item9");
    }

    @Test
    public void hasEmptyItemCaption() {
        ComboBoxElement combo = $(ComboBoxElement.class).first();
        // set some caption for the empty selection element
        $(ButtonElement.class).first().click();
        ensureSuggestions(combo, "empty", "item1", "item2", "item3", "item4",
                "item5", "item6", "item7", "item8", "item9");
    }

    @Test
    public void resetEmptyItem() {
        ComboBoxElement combo = $(ComboBoxElement.class).first();
        // set some caption for the empty selection element
        $(ButtonElement.class).first().click();
        // set empty string back as an empty caption
        $(ButtonElement.class).get(1).click();
        ensureSuggestions(combo, " ", "item1", "item2", "item3", "item4",
                "item5", "item6", "item7", "item8", "item9");
    }

    @Test
    public void disableEmptyItem() {
        ComboBoxElement combo = $(ComboBoxElement.class).first();
        // set some caption for the empty selection element
        $(ButtonElement.class).get(2).click();
        ensureSuggestions(combo, "item1", "item2", "item3", "item4", "item5",
                "item6", "item7", "item8", "item9", "item10");
    }

    private void ensureSuggestions(ComboBoxElement element,
            String... suggestions) {
        element.openPopup();
        System.out.println(element.getPopupSuggestions());
        Assert.assertEquals(Arrays.asList(suggestions),
                new ArrayList<>(element.getPopupSuggestions()));
    }
}

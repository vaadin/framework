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
package com.vaadin.tests.elements.combobox;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxGetSuggestionsTest extends MultiBrowserTest {
    @Test
    public void testSuggestions() {
        openTestURL();
        ComboBoxElement cb = $(ComboBoxElement.class).get(0);
        List<String> suggestions = cb.getPopupSuggestions();
        List<String> expectedSuggestions = new ArrayList<String>();
        for (int i = 1; i < 11; i++) {
            expectedSuggestions.add("item" + i);
        }
        Assert.assertEquals(expectedSuggestions, suggestions);
    }
}

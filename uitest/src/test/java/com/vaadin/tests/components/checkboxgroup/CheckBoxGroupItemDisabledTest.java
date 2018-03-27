/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.tests.components.checkboxgroup;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.CheckBoxGroupElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CheckBoxGroupItemDisabledTest extends MultiBrowserTest {

    @Test
    public void itemDisabledOnInit() {
        openTestURL();
        List<WebElement> options = $(CheckBoxGroupElement.class).first()
                .getOptionElements();
        options.stream().forEach(option -> {
            Integer value = Integer.parseInt(option.getText());
            boolean disabled = !CheckBoxGroupItemDisabled.ENABLED_PROVIDER
                    .test(value);
            assertEquals(
                    "Unexpected status of v-disabled stylename for item "
                            + value,
                    disabled,
                    option.getAttribute("class").contains("v-disabled"));
        });
    }
}

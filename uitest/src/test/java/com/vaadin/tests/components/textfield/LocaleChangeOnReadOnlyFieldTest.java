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
package com.vaadin.tests.components.textfield;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class LocaleChangeOnReadOnlyFieldTest extends MultiBrowserTest {

    @Test
    public void localeIsChangedOnReadOnlyField() {
        openTestURL();

        TextFieldElement textField = $(TextFieldElement.class).first();
        assertThat(textField.getValue(), is("1,024,000"));

        $(ButtonElement.class).caption("Change Locale").first().click();
        assertThat(textField.getValue(), is("1.024.000"));
    }

}

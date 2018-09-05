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
package com.vaadin.v7.shared.ui.textfield;

import com.vaadin.shared.annotations.NoLayout;
import com.vaadin.v7.shared.AbstractFieldState;

@Deprecated
public class AbstractTextFieldState extends AbstractFieldState {
    {
        primaryStyleName = "v-textfield";
    }

    /**
     * Maximum character count in text field.
     */
    @NoLayout
    public int maxLength = -1;

    /**
     * Number of visible columns in the TextField.
     */
    public int columns = 0;

    /**
     * The prompt to display in an empty field. Null when disabled.
     */
    @NoLayout
    public String inputPrompt = null;

    /**
     * The text in the field.
     */
    @NoLayout
    public String text = null;
}

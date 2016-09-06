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
package com.vaadin.shared.ui.textfield;

import com.vaadin.shared.AbstractFieldState;
import com.vaadin.shared.annotations.DelegateToWidget;
import com.vaadin.shared.annotations.NoLayout;

/**
 * State class for AbstractTextField.
 */
public abstract class AbstractTextFieldState extends AbstractFieldState {

    /**
     * Maximum character count in text field.
     */
    @DelegateToWidget
    @NoLayout
    public int maxLength = -1;

    /**
     * The prompt to display in an empty field. Null when disabled.
     */
    @DelegateToWidget
    @NoLayout
    public String placeholder = null;

    /**
     * The text in the field.
     */
    @DelegateToWidget
    @NoLayout
    public String text = "";

    @NoLayout
    public ValueChangeMode valueChangeMode = ValueChangeMode.LAZY;

    @NoLayout
    public int valueChangeTimeout = 400;

}

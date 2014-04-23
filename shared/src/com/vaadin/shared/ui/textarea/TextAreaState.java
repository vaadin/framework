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
package com.vaadin.shared.ui.textarea;

import com.vaadin.shared.annotations.DelegateToWidget;
import com.vaadin.shared.ui.textfield.AbstractTextFieldState;

public class TextAreaState extends AbstractTextFieldState {
    {
        primaryStyleName = "v-textarea";
    }

    /**
     * Number of visible rows in the text area. The default is 5.
     */
    @DelegateToWidget
    public int rows = 5;

    /**
     * Tells if word-wrapping should be used in the text area.
     */
    @DelegateToWidget
    public boolean wordwrap = true;

}

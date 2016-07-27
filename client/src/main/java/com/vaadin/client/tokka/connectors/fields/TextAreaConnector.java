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

package com.vaadin.client.tokka.connectors.fields;

import com.vaadin.client.tokka.ui.VTextArea;
import com.vaadin.shared.tokka.ui.components.fields.TextAreaState;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tokka.ui.components.fields.TextArea;

@Connect(TextArea.class)
public class TextAreaConnector extends TextFieldConnector {

    @Override
    public TextAreaState getState() {
        return (TextAreaState) super.getState();
    }

    @Override
    public VTextArea getWidget() {
        return (VTextArea) super.getWidget();
    }
}

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
package com.vaadin.ui;

import com.vaadin.shared.ui.button.NativeButtonState;

@SuppressWarnings("serial")
public class NativeButton extends Button {

    public NativeButton() {
        super();
    }

    public NativeButton(String caption) {
        super(caption);
    }

    public NativeButton(String caption, ClickListener listener) {
        super(caption, listener);
    }

    @Override
    protected NativeButtonState getState() {
        return (NativeButtonState) super.getState();
    }

    @Override
    protected NativeButtonState getState(boolean markAsDirty) {
        return (NativeButtonState) super.getState(markAsDirty);
    }

}

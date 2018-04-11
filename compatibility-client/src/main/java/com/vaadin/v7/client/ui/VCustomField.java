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
package com.vaadin.v7.client.ui;

import com.vaadin.client.Focusable;

@Deprecated
public class VCustomField extends VCustomComponent implements Focusable {

    private Focusable focusDelegate;

    @Override
    public void focus() {
        if (focusDelegate != null) {
            focusDelegate.focus();
        }
    }

    /**
     * Sets the focusable widget to focus instead of this custom field.
     *
     * @param focusDelegate
     *            the widget to delegate focus to
     */
    public void setFocusDelegate(Focusable focusDelegate) {
        this.focusDelegate = focusDelegate;

    }

    /**
     * Sets the focusable widget to focus instead of this custom field.
     *
     * @param focusDelegate
     *            the widget to delegate focus to
     */
    public void setFocusDelegate(
            final com.google.gwt.user.client.ui.Focusable focusDelegate) {
        this.focusDelegate = new Focusable() {
            @Override
            public void focus() {
                focusDelegate.setFocus(true);
            }
        };

    }

}

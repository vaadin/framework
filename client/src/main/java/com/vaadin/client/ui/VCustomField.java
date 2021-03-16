/*
 * Copyright 2000-2021 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.client.ui;

import com.vaadin.client.Focusable;

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

/*
 * Copyright 2000-2021 Vaadin Ltd.
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

package com.vaadin.v7.client.ui.datefield;

import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.UIDL;
import com.vaadin.v7.client.ui.VTextualDate;
import com.vaadin.v7.shared.ui.datefield.Resolution;
import com.vaadin.v7.shared.ui.datefield.TextualDateFieldState;

public class TextualDateConnector extends AbstractDateFieldConnector {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        VTextualDate date = getWidget();
        Resolution origRes = date.getCurrentResolution();
        String oldLocale = date.getCurrentLocale();
        super.updateFromUIDL(uidl, client);
        if (origRes != date.getCurrentResolution()
                || oldLocale != date.getCurrentLocale()) {
            // force recreating format string
            date.formatStr = null;
        }
        if (uidl.hasAttribute("format")) {
            date.formatStr = uidl.getStringAttribute("format");
        }

        date.inputPrompt = uidl
                .getStringAttribute(VTextualDate.ATTR_INPUTPROMPT);

        date.lenient = !uidl.getBooleanAttribute("strict");

        date.buildDate();
        // not a FocusWidget -> needs own tabindex handling
        date.text.setTabIndex(getState().tabIndex);

        if (date.isReadonly()) {
            date.text.addStyleDependentName("readonly");
        } else {
            date.text.removeStyleDependentName("readonly");
        }
    }

    @Override
    public VTextualDate getWidget() {
        return (VTextualDate) super.getWidget();
    }

    @Override
    public TextualDateFieldState getState() {
        return (TextualDateFieldState) super.getState();
    }
}

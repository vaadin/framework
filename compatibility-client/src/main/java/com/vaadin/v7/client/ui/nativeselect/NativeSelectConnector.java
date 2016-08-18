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

package com.vaadin.v7.client.ui.nativeselect;

import com.vaadin.client.ui.ConnectorFocusAndBlurHandler;
import com.vaadin.client.ui.VNativeSelect;
import com.vaadin.shared.ui.Connect;
import com.vaadin.v7.client.ui.optiongroup.OptionGroupBaseConnector;
import com.vaadin.v7.ui.NativeSelect;

@Connect(NativeSelect.class)
public class NativeSelectConnector extends OptionGroupBaseConnector {

    @Override
    protected void init() {
        super.init();
        ConnectorFocusAndBlurHandler.addHandlers(this, getWidget().getSelect());
    }

    @Override
    public VNativeSelect getWidget() {
        return (VNativeSelect) super.getWidget();
    }
}

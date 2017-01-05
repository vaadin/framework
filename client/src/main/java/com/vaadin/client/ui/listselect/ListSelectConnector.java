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
package com.vaadin.client.ui.listselect;

import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.connectors.AbstractMultiSelectConnector;
import com.vaadin.client.ui.VListSelect;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.listselect.ListSelectState;
import com.vaadin.ui.ListSelect;

/**
 * Client side connector for {@link ListSelect} component.
 *
 * @author Vaadin Ltd
 *
 */
@Connect(ListSelect.class)
public class ListSelectConnector extends AbstractMultiSelectConnector {

    @Override
    public VListSelect getWidget() {
        return (VListSelect) super.getWidget();
    }

    @Override
    public MultiSelectWidget getMultiSelectWidget() {
        return getWidget();
    }

    @Override
    public ListSelectState getState() {
        return (ListSelectState) super.getState();
    }

    @OnStateChange("readOnly")
    void updateReadOnly() {
        getWidget().setReadOnly(isReadOnly());
    }

    @OnStateChange("tabIndex")
    void updateTabIndex() {
        getWidget().setTabIndex(getState().tabIndex);
    }
}

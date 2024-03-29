/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.client.ui.splitpanel;

import com.vaadin.client.ui.VSplitPanelHorizontal;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.shared.ui.splitpanel.HorizontalSplitPanelState;
import com.vaadin.ui.HorizontalSplitPanel;

/**
 * A connector class for the HorizontalSplitPanel component. Eagerly loaded.
 *
 * @author Vaadin Ltd
 */
@Connect(value = HorizontalSplitPanel.class, loadStyle = LoadStyle.EAGER)
public class HorizontalSplitPanelConnector extends AbstractSplitPanelConnector {

    @Override
    public VSplitPanelHorizontal getWidget() {
        return (VSplitPanelHorizontal) super.getWidget();
    }

    @Override
    public HorizontalSplitPanelState getState() {
        return (HorizontalSplitPanelState) super.getState();
    }

}

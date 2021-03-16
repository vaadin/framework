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
package com.vaadin.client.ui.splitpanel;

import com.vaadin.client.ui.VSplitPanelVertical;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.shared.ui.splitpanel.VerticalSplitPanelState;
import com.vaadin.ui.VerticalSplitPanel;

@Connect(value = VerticalSplitPanel.class, loadStyle = LoadStyle.EAGER)
public class VerticalSplitPanelConnector extends AbstractSplitPanelConnector {

    @Override
    public VSplitPanelVertical getWidget() {
        return (VSplitPanelVertical) super.getWidget();
    }

    @Override
    public VerticalSplitPanelState getState() {
        return (VerticalSplitPanelState) super.getState();
    }

}

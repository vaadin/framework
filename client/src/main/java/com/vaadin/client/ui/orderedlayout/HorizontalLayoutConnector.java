/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.client.ui.orderedlayout;

import com.vaadin.client.ui.VHorizontalLayout;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.shared.ui.orderedlayout.HorizontalLayoutState;
import com.vaadin.ui.HorizontalLayout;

/**
 * Connects the client widget {@link VHorizontalLayout} with the Vaadin server
 * side counterpart {@link HorizontalLayout}
 */
@Connect(value = HorizontalLayout.class, loadStyle = LoadStyle.EAGER)
public class HorizontalLayoutConnector extends AbstractOrderedLayoutConnector {

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.client.ui.orderedlayout.AbstractOrderedLayoutConnector#
     * getWidget ()
     */
    @Override
    public VHorizontalLayout getWidget() {
        return (VHorizontalLayout) super.getWidget();
    }

    @Override
    public HorizontalLayoutState getState() {
        return (HorizontalLayoutState) super.getState();
    }

}

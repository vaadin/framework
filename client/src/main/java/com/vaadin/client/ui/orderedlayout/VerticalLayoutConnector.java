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
package com.vaadin.client.ui.orderedlayout;

import com.vaadin.client.ui.VVerticalLayout;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.shared.ui.orderedlayout.VerticalLayoutState;
import com.vaadin.ui.VerticalLayout;

/**
 * Connects the client widget {@link VVerticalLayout} with the Vaadin server
 * side counterpart {@link VerticalLayout}
 */
@Connect(value = VerticalLayout.class, loadStyle = LoadStyle.EAGER)
public class VerticalLayoutConnector extends AbstractOrderedLayoutConnector {

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.client.ui.orderedlayout.AbstractOrderedLayoutConnector#
     * getWidget ()
     */
    @Override
    public VVerticalLayout getWidget() {
        return (VVerticalLayout) super.getWidget();
    }

    @Override
    public VerticalLayoutState getState() {
        return (VerticalLayoutState) super.getState();
    }
}

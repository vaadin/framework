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

package com.vaadin.client.ui;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.shared.ui.Orientation;

public class VSplitPanelHorizontal extends VAbstractSplitPanel {

    public VSplitPanelHorizontal() {
        super(Orientation.HORIZONTAL);
    }

    @Override
    protected void startResize() {
        if (getFirstWidget() != null && isWidgetFullWidth(getFirstWidget())) {
            getFirstContainer().getStyle().setOverflow(Overflow.HIDDEN);
        }

        if (getSecondWidget() != null && isWidgetFullWidth(getSecondWidget())) {
            getSecondContainer().getStyle().setOverflow(Overflow.HIDDEN);
        }
    }

    @Override
    protected void stopResize() {
        getFirstContainer().getStyle().clearOverflow();
        getSecondContainer().getStyle().clearOverflow();
    }

    private boolean isWidgetFullWidth(Widget w) {
        return w.getElement().getStyle().getWidth().equals("100%");
    }
}

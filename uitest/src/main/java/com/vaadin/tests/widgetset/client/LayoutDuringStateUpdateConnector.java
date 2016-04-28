/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.tests.widgetset.client;

import com.google.gwt.user.client.ui.Label;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.PostLayoutListener;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.widgetset.server.LayoutDuringStateUpdateComponent;

@Connect(LayoutDuringStateUpdateComponent.class)
public class LayoutDuringStateUpdateConnector extends
        AbstractComponentConnector implements PostLayoutListener {
    private int layoutCount = 0;

    @Override
    protected void init() {
        super.init();
        updateLabelText();
    }

    @Override
    public Label getWidget() {
        return (Label) super.getWidget();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        try {
            getLayoutManager().layoutNow();
        } catch (AssertionError e) {
            // Ignore
        }
    }

    private void updateLabelText() {
        getWidget().setText("Layout phase count: " + layoutCount);
    }

    @Override
    public void postLayout() {
        layoutCount++;
        updateLabelText();
    }

}

/* 
 * Copyright 2011 Vaadin Ltd.
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

package com.vaadin.tests.widgetset.client.minitutorials.v7a2;

import com.google.gwt.user.client.ui.Image;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.communication.URLReference;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.minitutorials.v7a2.ResourceInStateComponent;

@Connect(ResourceInStateComponent.class)
public class ResourceInStateConnector extends AbstractComponentConnector {
    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        URLReference icon = getState().getMyIcon();
        if (icon != null) {
            getWidget().setUrl(icon.getURL());
        } else {
            getWidget().setUrl("");
        }

    }

    @Override
    public ResourceInStateState getState() {
        return (ResourceInStateState) super.getState();
    }

    @Override
    public Image getWidget() {
        return (Image) super.getWidget();
    }
}

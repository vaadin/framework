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

package com.vaadin.tests.minitutorials.v7a2;

import com.vaadin.tests.widgetset.client.minitutorials.v7a2.ComponentInStateState;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;

public class ComponentInStateComponent extends AbstractComponent {

    @Override
    public ComponentInStateState getState() {
        return (ComponentInStateState) super.getState();
    }

    public void setOtherComponent(Component component) {
        getState().otherComponent = component;
    }

    public Component getOtherComponent() {
        return (Component) getState().otherComponent;
    }
}

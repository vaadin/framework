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
package com.vaadin.tests.util;

import com.vaadin.tests.widgetset.client.ResizeTerrorizerControlConnector.ResizeTerorrizerState;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

public class ResizeTerrorizer extends VerticalLayout {
    private final ResizeTerrorizerControl control;

    public class ResizeTerrorizerControl extends AbstractComponent {

        public ResizeTerrorizerControl(Component target) {
            getState().target = target;
        }

        @Override
        protected ResizeTerorrizerState getState() {
            return (ResizeTerorrizerState) super.getState();
        }
    }

    public ResizeTerrorizer(Component target) {
        target.setWidth("700px");
        setSizeFull();
        addComponent(target);
        setExpandRatio(target, 1);
        control = new ResizeTerrorizerControl(target);
        addComponent(control);
    }

    public void setDefaultWidthOffset(int px) {
        control.getState().defaultWidthOffset = px;
    }

    public void setDefaultHeightOffset(int px) {
        control.getState().defaultHeightOffset = px;
    }

    public void setUseUriFragments(boolean useUriFragments) {
        control.getState().useUriFragments = useUriFragments;
    }

    public boolean isUseUriFragments() {
        return control.getState().useUriFragments;
    }
}

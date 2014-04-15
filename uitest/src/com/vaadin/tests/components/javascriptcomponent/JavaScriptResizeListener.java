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

package com.vaadin.tests.components.javascriptcomponent;

import com.vaadin.annotations.JavaScript;
import com.vaadin.data.Property;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;

public class JavaScriptResizeListener extends AbstractTestUI {

    @JavaScript("ResizeJsConnector.js")
    public class ResizeJsComponent extends AbstractJavaScriptComponent {
        public void setListenerEnabled(boolean enabled) {
            callFunction("setListenerEnabled", Boolean.valueOf(enabled));
        }
    }

    private final ResizeJsComponent resizeJsComponent = new ResizeJsComponent();

    private final CssLayout holder = new CssLayout();

    @Override
    protected void setup(VaadinRequest request) {

        addComponent(new Button("Change holder size",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        updateHolderSize();
                    }
                }));
        addComponent(new CheckBox("Listener active") {
            {
                setImmediate(true);
                addValueChangeListener(new ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        resizeJsComponent.setListenerEnabled(event
                                .getProperty().getValue() == Boolean.TRUE);
                    }
                });
            }
        });

        updateHolderSize();
        addComponent(holder);

        resizeJsComponent.setSizeFull();
        holder.addComponent(resizeJsComponent);
    }

    private void updateHolderSize() {
        if (holder.getHeight() == 100) {
            holder.setHeight("50px");
        } else {
            holder.setHeight("100px");
        }

        if (holder.getWidth() == 100) {
            holder.setWidth("200px");
        } else {
            holder.setWidth("100px");
        }
    }

    @Override
    protected String getTestDescription() {
        return "Test for getting resize events for javascript components";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(11996);
    }

}

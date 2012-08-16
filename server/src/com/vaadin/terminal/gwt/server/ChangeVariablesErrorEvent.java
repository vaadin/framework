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
package com.vaadin.terminal.gwt.server;

import java.util.Map;

import com.vaadin.ui.AbstractComponent.ComponentErrorEvent;
import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public class ChangeVariablesErrorEvent implements ComponentErrorEvent {

    private Throwable throwable;
    private Component component;

    private Map<String, Object> variableChanges;

    public ChangeVariablesErrorEvent(Component component, Throwable throwable,
            Map<String, Object> variableChanges) {
        this.component = component;
        this.throwable = throwable;
        this.variableChanges = variableChanges;
    }

    @Override
    public Throwable getThrowable() {
        return throwable;
    }

    public Component getComponent() {
        return component;
    }

    public Map<String, Object> getVariableChanges() {
        return variableChanges;
    }

}
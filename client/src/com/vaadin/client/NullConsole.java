/*
 * Copyright 2000-2013 Vaadin Ltd.
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

package com.vaadin.client;

import java.util.Set;

import com.google.gwt.core.client.GWT;

/**
 * Client side console implementation for non-debug mode that discards all
 * messages.
 * 
 */
public class NullConsole implements Console {

    @Override
    public void dirUIDL(ValueMap u, ApplicationConnection conn) {
    }

    @Override
    public void error(String msg) {
        GWT.log(msg);
    }

    @Override
    public void log(String msg) {
        GWT.log(msg);
    }

    @Override
    public void printObject(Object msg) {
        GWT.log(msg.toString());
    }

    @Override
    public void printLayoutProblems(ValueMap meta,
            ApplicationConnection applicationConnection,
            Set<ComponentConnector> zeroHeightComponents,
            Set<ComponentConnector> zeroWidthComponents) {
    }

    @Override
    public void log(Throwable e) {
        GWT.log(e.getMessage(), e);
    }

    @Override
    public void error(Throwable e) {
        GWT.log(e.getMessage(), e);
    }

    @Override
    public void setQuietMode(boolean quietDebugMode) {
    }

    @Override
    public void init() {
    }

}

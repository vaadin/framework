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
package com.vaadin.terminal.gwt.client;

/**
 * GWT's HasFocus is way too overkill for just receiving focus in simple
 * components. Vaadin uses this interface in addition to GWT's HasFocus to pass
 * focus requests from server to actual ui widgets in browsers.
 * 
 * So in to make your server side focusable component receive focus on client
 * side it must either implement this or HasFocus interface.
 */
public interface Focusable {
    /**
     * Sets focus to this widget.
     */
    public void focus();
}

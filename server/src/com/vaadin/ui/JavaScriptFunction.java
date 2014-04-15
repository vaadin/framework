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

package com.vaadin.ui;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;

import com.vaadin.server.AbstractJavaScriptExtension;

/**
 * Defines a method that is called by a client-side JavaScript function. When
 * the corresponding JavaScript function is called, the {@link #call(JSONArray)}
 * method is invoked.
 * 
 * @see JavaScript#addFunction(String, JavaScriptCallback)
 * @see AbstractJavaScriptComponent#addFunction(String, JavaScriptCallback)
 * @see AbstractJavaScriptExtension#addFunction(String, JavaScriptCallback)
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public interface JavaScriptFunction extends Serializable {
    /**
     * Invoked whenever the corresponding JavaScript function is called in the
     * browser.
     * <p>
     * Because of the asynchronous nature of the communication between client
     * and server, no return value can be sent back to the browser.
     * 
     * @param arguments
     *            an array with JSON representations of the arguments with which
     *            the JavaScript function was called.
     * @throws JSONException
     *             if the arguments can not be interpreted
     */
    public void call(JSONArray arguments) throws JSONException;
}

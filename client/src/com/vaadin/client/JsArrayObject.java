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

package com.vaadin.client;

import com.google.gwt.core.client.JavaScriptObject;

public final class JsArrayObject<T> extends JavaScriptObject {

    protected JsArrayObject() {
        // JSO constructor
    }

    public native void add(T value)
    /*-{
        this.push(value);
    }-*/;

    public native int size()
    /*-{
        return this.length;
    }-*/;

    public native T get(int i)
    /*-{
        return this[i];
    }-*/;

}

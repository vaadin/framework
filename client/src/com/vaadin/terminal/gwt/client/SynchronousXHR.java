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

import com.google.gwt.xhr.client.XMLHttpRequest;

public class SynchronousXHR extends XMLHttpRequest {

    protected SynchronousXHR() {
    }

    public native final void synchronousPost(String uri, String requestData)
    /*-{
        try {
            this.open("POST", uri, false);
            this.setRequestHeader("Content-Type", "text/plain;charset=utf-8");
            this.send(requestData);
        } catch (e) {
           // No errors are managed as this is synchronous forceful send that can just fail
        }
    }-*/;

}

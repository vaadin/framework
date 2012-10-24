/*
 * Copyright 2012 Vaadin Ltd.
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

package com.vaadin.server;

import com.vaadin.ui.UI;

public class BrowserPopupUIProvider extends UIProvider {

    private final String path;
    private final Class<? extends UI> uiClass;

    public BrowserPopupUIProvider(Class<? extends UI> uiClass, String path) {
        this.path = ensureInitialSlash(path);
        this.uiClass = uiClass;
    }

    private static String ensureInitialSlash(String path) {
        if (path == null) {
            return null;
        } else if (!path.startsWith("/")) {
            return '/' + path;
        } else {
            return path;
        }
    }

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
        String requestPathInfo = event.getRequest().getRequestPathInfo();
        if (path.equals(requestPathInfo)) {
            return uiClass;
        } else {
            return null;
        }
    }
}

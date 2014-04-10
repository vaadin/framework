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

package com.vaadin.tests.minitutorials.v7a3;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.ui.UI;

@JavaScript("analytics_connector.js")
public class Analytics extends AbstractJavaScriptExtension {

    public Analytics(UI ui, String account) {
        extend(ui);
        pushCommand("_setAccount", account);
    }

    public void trackPageview(String name) {
        pushCommand("_trackPageview", name);
    }

    private void pushCommand(Object... commandAndArguments) {
        // Cast to Object to use Object[] commandAndArguments as the first
        // varargs argument instead of as the full varargs argument array.
        callFunction("pushCommand", (Object) commandAndArguments);
    }
}

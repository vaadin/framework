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

package com.vaadin.server;

import com.vaadin.ui.UI;

public class DefaultUIProvider extends UIProvider {

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
        VaadinRequest request = event.getRequest();

        Object uiClassNameObj = request
                .getService()
                .getDeploymentConfiguration()
                .getApplicationOrSystemProperty(VaadinSession.UI_PARAMETER,
                        null);

        if (uiClassNameObj instanceof String) {
            String uiClassName = uiClassNameObj.toString();

            ClassLoader classLoader = request.getService().getClassLoader();
            try {
                Class<? extends UI> uiClass = Class.forName(uiClassName, true,
                        classLoader).asSubclass(UI.class);

                return uiClass;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Could not find UI class", e);
            }
        }

        return null;
    }
}

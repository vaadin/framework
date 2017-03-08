/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.tests.integration.push.BasicPush;
import com.vaadin.ui.UI;

public class IntegrationTestUIProvider extends UIProvider {

    public static final String[] defaultPackages = {
            "com.vaadin.tests.integration",
            "com.vaadin.tests.integration.push" };

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
        Class<? extends UI> uiClass = findUIClassFromPath(event);
        return uiClass != null ? uiClass : BasicPush.class;
    }

    private Class<? extends UI> findUIClassFromPath(
            UIClassSelectionEvent event) {
        String pathInfo = event.getRequest().getPathInfo();
        if (pathInfo != null) {
            String className = pathInfo.substring(1);
            if (className.startsWith("run/")) {
                className = className.substring(4);
            }

            if (className.contains(".")) {
                return getUIClass(className);
            } else {
                return getUIClassFromDefaultPackage(className);
            }
        }
        return null;
    }

    private Class<? extends UI> getUIClassFromDefaultPackage(String className) {
        for (String pkgName : defaultPackages) {
            Class<? extends UI> uiClass = getUIClass(pkgName + "." + className);
            if (uiClass != null) {
                return uiClass;
            }
        }
        return null;
    }

    private Class<? extends UI> getUIClass(String className) {
        try {
            Class<?> loadClass = getClass().getClassLoader()
                    .loadClass(className.replace("/", "."));
            return (Class<? extends UI>) loadClass;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

}

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

package com.vaadin.terminal;

import com.vaadin.Application;
import com.vaadin.UIRequiresMoreInformationException;
import com.vaadin.ui.UI;

public class DefaultRootProvider extends AbstractRootProvider {

    @Override
    public Class<? extends UI> getUIClass(Application application,
            WrappedRequest request) throws UIRequiresMoreInformationException {
        Object rootClassNameObj = application
                .getProperty(Application.UI_PARAMETER);

        if (rootClassNameObj instanceof String) {
            String rootClassName = rootClassNameObj.toString();

            ClassLoader classLoader = request.getDeploymentConfiguration()
                    .getClassLoader();
            if (classLoader == null) {
                classLoader = getClass().getClassLoader();
            }
            try {
                Class<? extends UI> rootClass = Class.forName(rootClassName,
                        true, classLoader).asSubclass(UI.class);

                return rootClass;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Could not find root class", e);
            }
        }

        return null;
    }
}

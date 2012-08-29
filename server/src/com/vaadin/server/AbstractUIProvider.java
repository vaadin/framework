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

package com.vaadin.server;

import com.vaadin.Application;
import com.vaadin.ui.UI;

public abstract class AbstractUIProvider implements UIProvider {

    @Override
    public UI instantiateUI(Application application,
            Class<? extends UI> type, WrappedRequest request) {
        try {
            return type.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("Could not instantiate root class", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not access root class", e);
        }
    }
}

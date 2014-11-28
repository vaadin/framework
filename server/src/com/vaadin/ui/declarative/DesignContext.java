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
package com.vaadin.ui.declarative;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class contains contextual information that is collected when a component
 * tree is constructed based on HTML design template
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class DesignContext {

    // cache for object instances
    private static Map<Class<?>, Object> instanceCache = Collections
            .synchronizedMap(new HashMap<Class<?>, Object>());

    /**
     * Returns the default instance for the given class. The instance must not
     * be modified by the caller.
     * 
     * @since
     * @param instanceClass
     * @return
     */
    public <T> T getDefaultInstance(Class<T> instanceClass) {
        T instance = (T) instanceCache.get(instanceClass);
        if (instance == null) {
            try {
                instance = instanceClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            instanceCache.put(instanceClass, instance);
        }
        return instance;
    }
}

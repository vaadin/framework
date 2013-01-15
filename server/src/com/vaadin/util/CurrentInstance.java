/*
 * Copyright 2000-2013 Vaadin Ltd.
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

package com.vaadin.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Keeps track of various thread local instances used by the framework.
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 */
public class CurrentInstance implements Serializable {
    private final Object instance;
    private final boolean inheritable;

    private static InheritableThreadLocal<Map<Class<?>, CurrentInstance>> instances = new InheritableThreadLocal<Map<Class<?>, CurrentInstance>>() {
        @Override
        protected Map<Class<?>, CurrentInstance> childValue(
                Map<Class<?>, CurrentInstance> parentValue) {
            Map<Class<?>, CurrentInstance> value = new HashMap<Class<?>, CurrentInstance>();

            // Copy all inheritable values to child map
            for (Entry<Class<?>, CurrentInstance> e : parentValue.entrySet()) {
                if (e.getValue().inheritable) {
                    value.put(e.getKey(), e.getValue());
                }
            }

            return value;
        }

        @Override
        protected Map<java.lang.Class<?>, CurrentInstance> initialValue() {
            return new HashMap<Class<?>, CurrentInstance>();
        }
    };

    private CurrentInstance(Object instance, boolean inheritable) {
        this.instance = instance;
        this.inheritable = inheritable;
    }

    /**
     * Gets the current instance of a specific type if available.
     * 
     * @param type
     *            the class to get an instance of
     * @return the current instance or the provided type, or <code>null</code>
     *         if there is no current instance.
     */
    public static <T> T get(Class<T> type) {
        CurrentInstance currentInstance = instances.get().get(type);
        if (currentInstance != null) {
            return type.cast(currentInstance.instance);
        } else {
            return null;
        }
    }

    /**
     * Sets the current instance of the given type.
     * 
     * @see #setInheritable(Class, Object)
     * @see ThreadLocal
     * 
     * @param type
     *            the class that should be used when getting the current
     *            instance back
     * @param instance
     *            the actual instance
     */
    public static <T> void set(Class<T> type, T instance) {
        set(type, instance, false);
    }

    /**
     * Sets the current inheritable instance of the given type. A current
     * instance that is inheritable will be available for child threads.
     * 
     * @see #set(Class, Object)
     * @see InheritableThreadLocal
     * 
     * @param type
     *            the class that should be used when getting the current
     *            instance back
     * @param instance
     *            the actual instance
     */
    public static <T> void setInheritable(Class<T> type, T instance) {
        set(type, instance, true);
    }

    private static <T> void set(Class<T> type, T instance, boolean inheritable) {
        if (instance == null) {
            instances.get().remove(type);
        } else {
            assert type.isInstance(instance) : "Invald instance type";
            CurrentInstance previousInstance = instances.get().put(type,
                    new CurrentInstance(instance, inheritable));
            if (previousInstance != null) {
                assert previousInstance.inheritable == inheritable : "Inheritable status mismatch for "
                        + type;
            }
        }
    }

    /**
     * Clears all current instances.
     */
    public static void clearAll() {
        instances.get().clear();
    }
}

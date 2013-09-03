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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

/**
 * Keeps track of various current instances for the current thread. All the
 * instances are automatically cleared after handling a request from the client
 * to avoid leaking memory. The inheritable values are also maintained when
 * execution is moved to another thread, both when a new thread is created and
 * when {@link VaadinSession#access(Runnable)} or {@link UI#access(Runnable)} is
 * used.
 * <p>
 * Currently the framework uses the following instances:
 * </p>
 * <p>
 * Inheritable: {@link UI}, {@link VaadinService}, {@link VaadinSession}.
 * </p>
 * <p>
 * Non-inheritable: {@link VaadinRequest}, {@link VaadinResponse}.
 * </p>
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class CurrentInstance implements Serializable {
    private final Object instance;
    private final boolean inheritable;

    private static InheritableThreadLocal<Map<Class<?>, CurrentInstance>> instances = new InheritableThreadLocal<Map<Class<?>, CurrentInstance>>() {
        @Override
        protected Map<Class<?>, CurrentInstance> childValue(
                Map<Class<?>, CurrentInstance> parentValue) {
            if (parentValue == null) {
                return null;
            }

            Map<Class<?>, CurrentInstance> value = new WeakValueMap<Class<?>, CurrentInstance>();

            // Copy all inheritable values to child map
            for (Entry<Class<?>, CurrentInstance> e : parentValue.entrySet()) {
                if (e.getValue().inheritable) {
                    value.put(e.getKey(), e.getValue());
                }
            }

            return value;
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
        Map<Class<?>, CurrentInstance> map = instances.get();
        if (map == null) {
            return null;
        }
        CurrentInstance currentInstance = map.get(type);
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
     * instance that is inheritable will be available for child threads and in
     * code run by {@link VaadinSession#access(Runnable)} and
     * {@link UI#access(Runnable)}.
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
        Map<Class<?>, CurrentInstance> map = instances.get();
        if (instance == null) {
            // remove the instance
            if (map == null) {
                return;
            }
            map.remove(type);
            if (map.isEmpty()) {
                instances.remove();
                map = null;
            }
        } else {
            assert type.isInstance(instance) : "Invald instance type";
            if (map == null) {
                map = new HashMap<Class<?>, CurrentInstance>();
                instances.set(map);
            }

            CurrentInstance previousInstance = map.put(type,
                    new CurrentInstance(instance, inheritable));
            if (previousInstance != null) {
                assert previousInstance.inheritable == inheritable : "Inheritable status mismatch for "
                        + type
                        + " (previous was "
                        + previousInstance.inheritable
                        + ", new is "
                        + inheritable + ")";
            }
        }
    }

    /**
     * Clears all current instances.
     */
    public static void clearAll() {
        instances.remove();
    }

    /**
     * Restores the given instances to the given values. Note that this should
     * only be used internally to restore Vaadin classes.
     * 
     * @since 7.1
     * 
     * @param old
     *            A Class -> CurrentInstance map to set as current instances
     */
    public static void restoreInstances(Map<Class<?>, CurrentInstance> old) {
        for (Class c : old.keySet()) {
            CurrentInstance ci = old.get(c);
            set(c, ci.instance, ci.inheritable);
        }
    }

    /**
     * Gets the currently set instances so that they can later be restored using
     * {@link #restoreInstances(Map)}.
     * 
     * @since 7.1
     * 
     * @param onlyInheritable
     *            <code>true</code> if only the inheritable instances should be
     *            included; <code>false</code> to get all instances.
     * @return a map containing the current instances
     */
    public static Map<Class<?>, CurrentInstance> getInstances(
            boolean onlyInheritable) {
        Map<Class<?>, CurrentInstance> map = instances.get();
        if (map == null) {
            return Collections.emptyMap();
        } else {
            Map<Class<?>, CurrentInstance> copy = new HashMap<Class<?>, CurrentInstance>();
            for (Class<?> c : map.keySet()) {
                CurrentInstance ci = map.get(c);
                if (ci.inheritable || !onlyInheritable) {
                    copy.put(c, ci);
                }
            }
            return copy;
        }
    }

    /**
     * Sets current instances for the UI and all related classes. The previously
     * defined values can be restored by passing the returned map to
     * {@link #restoreInstances(Map)}.
     * 
     * @since 7.1
     * 
     * @param ui
     *            The UI
     * @return A map containing the old values of the instances that this method
     *         updated.
     */
    public static Map<Class<?>, CurrentInstance> setCurrent(UI ui) {
        Map<Class<?>, CurrentInstance> old = new HashMap<Class<?>, CurrentInstance>();
        old.put(UI.class, new CurrentInstance(UI.getCurrent(), true));
        UI.setCurrent(ui);
        old.putAll(setCurrent(ui.getSession()));
        return old;
    }

    /**
     * Sets current instances for the {@link VaadinSession} and all related
     * classes. The previously defined values can be restored by passing the
     * returned map to {@link #restoreInstances(Map)}.
     * 
     * @since 7.1
     * 
     * @param session
     *            The VaadinSession
     * @return A map containing the old values of the instances this method
     *         updated.
     */
    public static Map<Class<?>, CurrentInstance> setCurrent(
            VaadinSession session) {
        Map<Class<?>, CurrentInstance> old = new HashMap<Class<?>, CurrentInstance>();
        old.put(VaadinSession.class,
                new CurrentInstance(VaadinSession.getCurrent(), true));
        old.put(VaadinService.class,
                new CurrentInstance(VaadinService.getCurrent(), true));
        VaadinService service = null;
        if (session != null) {
            service = session.getService();
        }

        VaadinSession.setCurrent(session);
        VaadinService.setCurrent(service);

        return old;
    }
}

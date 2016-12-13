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

package com.vaadin.event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import com.vaadin.shared.Registration;

public class SimpleEventRouter<E extends Serializable> {

    private Collection<Listener<E>> listeners = null;

    public Registration addListener(Listener<E> listener) {
        if (listeners == null) {
            listeners = new LinkedHashSet<>();
        }
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }

    public void fireEvent(E event) {
        if (listeners == null) {
            return;
        }
        for (Listener<E> listener : new ArrayList<>(listeners)) {
            // very few callers have been doing error handling here, so not
            // complicating the basic event list with it
            listener.onEvent(event);
        }
    }

    public boolean hasListeners() {
        return listeners != null && !listeners.isEmpty();
    }
}

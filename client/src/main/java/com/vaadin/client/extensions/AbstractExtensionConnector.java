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

package com.vaadin.client.extensions;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.ui.AbstractConnector;

public abstract class AbstractExtensionConnector extends AbstractConnector {
    boolean hasBeenAttached = false;

    @Override
    public void setParent(ServerConnector parent) {
        ServerConnector oldParent = getParent();
        if (oldParent == parent) {
            // Nothing to do
            return;
        }
        if (hasBeenAttached && parent != null) {
            throw new IllegalStateException(
                    "An extension can not be moved from one parent to another.");
        }

        super.setParent(parent);

        if (parent != null) {
            extend(parent);
            hasBeenAttached = true;
        }
    }

    /**
     * Called when the extension is attached to its parent. This method is only
     * called once as an extension cannot be moved from one parent to another.
     * 
     * @param target
     *            The connector this extension extends
     */
    protected abstract void extend(ServerConnector target);
}

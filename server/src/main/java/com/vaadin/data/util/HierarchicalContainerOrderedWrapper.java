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
package com.vaadin.data.util;

import java.util.Collection;

import com.vaadin.data.Container.Hierarchical;

/**
 * A wrapper class for adding external ordering to containers not implementing
 * the {@link com.vaadin.data.Container.Ordered} interface while retaining
 * {@link Hierarchical} features.
 * 
 * @see ContainerOrderedWrapper
 */
@SuppressWarnings({ "serial" })
public class HierarchicalContainerOrderedWrapper extends
        ContainerOrderedWrapper implements Hierarchical {

    private Hierarchical hierarchical;

    public HierarchicalContainerOrderedWrapper(Hierarchical toBeWrapped) {
        super(toBeWrapped);
        hierarchical = toBeWrapped;
    }

    @Override
    public boolean areChildrenAllowed(Object itemId) {
        return hierarchical.areChildrenAllowed(itemId);
    }

    @Override
    public Collection<?> getChildren(Object itemId) {
        return hierarchical.getChildren(itemId);
    }

    @Override
    public Object getParent(Object itemId) {
        return hierarchical.getParent(itemId);
    }

    @Override
    public boolean hasChildren(Object itemId) {
        return hierarchical.hasChildren(itemId);
    }

    @Override
    public boolean isRoot(Object itemId) {
        return hierarchical.isRoot(itemId);
    }

    @Override
    public Collection<?> rootItemIds() {
        return hierarchical.rootItemIds();
    }

    @Override
    public boolean setChildrenAllowed(Object itemId, boolean areChildrenAllowed)
            throws UnsupportedOperationException {
        return hierarchical.setChildrenAllowed(itemId, areChildrenAllowed);
    }

    @Override
    public boolean setParent(Object itemId, Object newParentId)
            throws UnsupportedOperationException {
        return hierarchical.setParent(itemId, newParentId);
    }

}

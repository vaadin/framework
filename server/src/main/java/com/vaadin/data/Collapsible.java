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
package com.vaadin.data;

import com.vaadin.data.Container.Hierarchical;
import com.vaadin.data.Container.Ordered;

/**
 * Container needed by large lazy loading hierarchies displayed e.g. in
 * TreeTable.
 * <p>
 * Container of this type gets notified when a subtree is opened/closed in a
 * component displaying its content. This allows container to lazy load subtrees
 * and release memory when a sub-tree is no longer displayed.
 * <p>
 * Methods from {@link Container.Ordered} (and from {@linkContainer.Indexed} if
 * implemented) are expected to work as in "preorder" of the currently visible
 * hierarchy. This means for example that the return value of size method
 * changes when subtree is collapsed/expanded. In other words items in collapsed
 * sub trees should be "ignored" by container when the container is accessed
 * with methods introduced in {@link Container.Ordered} or
 * {@linkContainer.Indexed}. From the accessors point of view, items in
 * collapsed subtrees don't exist.
 * <p>
 * 
 */
public interface Collapsible extends Hierarchical, Ordered {

    /**
     * <p>
     * Collapsing the {@link Item} indicated by <code>itemId</code> hides all
     * children, and their respective children, from the {@link Container}.
     * </p>
     * 
     * <p>
     * If called on a leaf {@link Item}, this method does nothing.
     * </p>
     * 
     * @param itemId
     *            the identifier of the collapsed {@link Item}
     * @param collapsed
     *            <code>true</code> if you want to collapse the children below
     *            this {@link Item}. <code>false</code> if you want to
     *            uncollapse the children.
     */
    public void setCollapsed(Object itemId, boolean collapsed);

    /**
     * <p>
     * Checks whether the {@link Item}, identified by <code>itemId</code> is
     * collapsed or not.
     * </p>
     * 
     * <p>
     * If an {@link Item} is "collapsed" its children are not included in
     * methods used to list Items in this container.
     * </p>
     * 
     * @param itemId
     *            The {@link Item}'s identifier that is to be checked.
     * @return <code>true</code> iff the {@link Item} identified by
     *         <code>itemId</code> is currently collapsed, otherwise
     *         <code>false</code>.
     */
    public boolean isCollapsed(Object itemId);

}

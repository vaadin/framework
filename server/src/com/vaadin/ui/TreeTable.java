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

package com.vaadin.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.data.Collapsible;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Hierarchical;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.util.ContainerHierarchicalWrapper;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.HierarchicalContainerOrderedWrapper;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.treetable.TreeTableConstants;
import com.vaadin.ui.Tree.CollapseEvent;
import com.vaadin.ui.Tree.CollapseListener;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Tree.ExpandListener;

/**
 * TreeTable extends the {@link Table} component so that it can also visualize a
 * hierarchy of its Items in a similar manner that {@link Tree} does. The tree
 * hierarchy is always displayed in the first actual column of the TreeTable.
 * <p>
 * The TreeTable supports the usual {@link Table} features like lazy loading, so
 * it should be no problem to display lots of items at once. Only required rows
 * and some cache rows are sent to the client.
 * <p>
 * TreeTable supports standard {@link Hierarchical} container interfaces, but
 * also a more fine tuned version - {@link Collapsible}. A container
 * implementing the {@link Collapsible} interface stores the collapsed/expanded
 * state internally and can this way scale better on the server side than with
 * standard Hierarchical implementations. Developer must however note that
 * {@link Collapsible} containers can not be shared among several users as they
 * share UI state in the container.
 */
@SuppressWarnings({ "serial" })
public class TreeTable extends Table implements Hierarchical {

    private interface ContainerStrategy extends Serializable {
        public int size();

        public boolean isNodeOpen(Object itemId);

        public int getDepth(Object itemId);

        public void toggleChildVisibility(Object itemId);

        public Object getIdByIndex(int index);

        public int indexOfId(Object id);

        public Object nextItemId(Object itemId);

        public Object lastItemId();

        public Object prevItemId(Object itemId);

        public boolean isLastId(Object itemId);

        public Collection<?> getItemIds();

        public void containerItemSetChange(ItemSetChangeEvent event);
    }

    private abstract class AbstractStrategy implements ContainerStrategy {

        /**
         * Consider adding getDepth to {@link Collapsible}, might help
         * scalability with some container implementations.
         */

        @Override
        public int getDepth(Object itemId) {
            int depth = 0;
            Hierarchical hierarchicalContainer = getContainerDataSource();
            while (!hierarchicalContainer.isRoot(itemId)) {
                depth++;
                itemId = hierarchicalContainer.getParent(itemId);
            }
            return depth;
        }

        @Override
        public void containerItemSetChange(ItemSetChangeEvent event) {
        }

    }

    /**
     * This strategy is used if current container implements {@link Collapsible}
     * .
     * 
     * open-collapsed logic diverted to container, otherwise use default
     * implementations.
     */
    private class CollapsibleStrategy extends AbstractStrategy {

        private Collapsible c() {
            return (Collapsible) getContainerDataSource();
        }

        @Override
        public void toggleChildVisibility(Object itemId) {
            c().setCollapsed(itemId, !c().isCollapsed(itemId));
        }

        @Override
        public boolean isNodeOpen(Object itemId) {
            return !c().isCollapsed(itemId);
        }

        @Override
        public int size() {
            return TreeTable.super.size();
        }

        @Override
        public Object getIdByIndex(int index) {
            return TreeTable.super.getIdByIndex(index);
        }

        @Override
        public int indexOfId(Object id) {
            return TreeTable.super.indexOfId(id);
        }

        @Override
        public boolean isLastId(Object itemId) {
            // using the default impl
            return TreeTable.super.isLastId(itemId);
        }

        @Override
        public Object lastItemId() {
            // using the default impl
            return TreeTable.super.lastItemId();
        }

        @Override
        public Object nextItemId(Object itemId) {
            return TreeTable.super.nextItemId(itemId);
        }

        @Override
        public Object prevItemId(Object itemId) {
            return TreeTable.super.prevItemId(itemId);
        }

        @Override
        public Collection<?> getItemIds() {
            return TreeTable.super.getItemIds();
        }

    }

    /**
     * Strategy for Hierarchical but not Collapsible container like
     * {@link HierarchicalContainer}.
     * 
     * Store collapsed/open states internally, fool Table to use preorder when
     * accessing items from container via Ordered/Indexed methods.
     */
    private class HierarchicalStrategy extends AbstractStrategy {

        private final HashSet<Object> openItems = new HashSet<Object>();

        @Override
        public boolean isNodeOpen(Object itemId) {
            return openItems.contains(itemId);
        }

        @Override
        public int size() {
            return getPreOrder().size();
        }

        @Override
        public Collection<Object> getItemIds() {
            return Collections.unmodifiableCollection(getPreOrder());
        }

        @Override
        public boolean isLastId(Object itemId) {
            if (itemId == null) {
                return false;
            }

            return itemId.equals(lastItemId());
        }

        @Override
        public Object lastItemId() {
            if (getPreOrder().size() > 0) {
                return getPreOrder().get(getPreOrder().size() - 1);
            } else {
                return null;
            }
        }

        @Override
        public Object nextItemId(Object itemId) {
            int indexOf = getPreOrder().indexOf(itemId);
            if (indexOf == -1) {
                return null;
            }
            indexOf++;
            if (indexOf == getPreOrder().size()) {
                return null;
            } else {
                return getPreOrder().get(indexOf);
            }
        }

        @Override
        public Object prevItemId(Object itemId) {
            int indexOf = getPreOrder().indexOf(itemId);
            indexOf--;
            if (indexOf < 0) {
                return null;
            } else {
                return getPreOrder().get(indexOf);
            }
        }

        @Override
        public void toggleChildVisibility(Object itemId) {
            boolean removed = openItems.remove(itemId);
            if (!removed) {
                openItems.add(itemId);
                getLogger().log(Level.FINEST, "Item {0} is now expanded",
                        itemId);
            } else {
                getLogger().log(Level.FINEST, "Item {0} is now collapsed",
                        itemId);
            }
            clearPreorderCache();
        }

        private void clearPreorderCache() {
            preOrder = null; // clear preorder cache
        }

        List<Object> preOrder;

        /**
         * Preorder of ids currently visible
         * 
         * @return
         */
        private List<Object> getPreOrder() {
            if (preOrder == null) {
                preOrder = new ArrayList<Object>();
                Collection<?> rootItemIds = getContainerDataSource()
                        .rootItemIds();
                for (Object id : rootItemIds) {
                    preOrder.add(id);
                    addVisibleChildTree(id);
                }
            }
            return preOrder;
        }

        private void addVisibleChildTree(Object id) {
            if (isNodeOpen(id)) {
                Collection<?> children = getContainerDataSource().getChildren(
                        id);
                if (children != null) {
                    for (Object childId : children) {
                        preOrder.add(childId);
                        addVisibleChildTree(childId);
                    }
                }
            }

        }

        @Override
        public int indexOfId(Object id) {
            return getPreOrder().indexOf(id);
        }

        @Override
        public Object getIdByIndex(int index) {
            return getPreOrder().get(index);
        }

        @Override
        public void containerItemSetChange(ItemSetChangeEvent event) {
            // preorder becomes invalid on sort, item additions etc.
            clearPreorderCache();
            super.containerItemSetChange(event);
        }

    }

    /**
     * Creates an empty TreeTable with a default container.
     */
    public TreeTable() {
        super(null, new HierarchicalContainer());
    }

    /**
     * Creates an empty TreeTable with a default container.
     * 
     * @param caption
     *            the caption for the TreeTable
     */
    public TreeTable(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Creates a TreeTable instance with given captions and data source.
     * 
     * @param caption
     *            the caption for the component
     * @param dataSource
     *            the dataSource that is used to list items in the component
     */
    public TreeTable(String caption, Container dataSource) {
        super(caption, dataSource);
    }

    private ContainerStrategy cStrategy;
    private Object focusedRowId = null;
    private Object hierarchyColumnId;

    /**
     * The item id that was expanded or collapsed during this request. Reset at
     * the end of paint and only used for determining if a partial or full paint
     * should be done.
     * 
     * Can safely be reset to null whenever a change occurs that would prevent a
     * partial update from rendering the correct result, e.g. rows added or
     * removed during an expand operation.
     */
    private Object toggledItemId;
    private boolean animationsEnabled;
    private boolean clearFocusedRowPending;

    /**
     * If the container does not send item set change events, always do a full
     * repaint instead of a partial update when expanding/collapsing nodes.
     */
    private boolean containerSupportsPartialUpdates;

    private ContainerStrategy getContainerStrategy() {
        if (cStrategy == null) {
            if (getContainerDataSource() instanceof Collapsible) {
                cStrategy = new CollapsibleStrategy();
            } else {
                cStrategy = new HierarchicalStrategy();
            }
        }
        return cStrategy;
    }

    @Override
    protected void paintRowAttributes(PaintTarget target, Object itemId)
            throws PaintException {
        super.paintRowAttributes(target, itemId);
        target.addAttribute("depth", getContainerStrategy().getDepth(itemId));
        if (getContainerDataSource().areChildrenAllowed(itemId)) {
            target.addAttribute("ca", true);
            target.addAttribute("open",
                    getContainerStrategy().isNodeOpen(itemId));
        }
    }

    @Override
    protected void paintRowIcon(PaintTarget target, Object[][] cells,
            int indexInRowbuffer) throws PaintException {
        // always paint if present (in parent only if row headers visible)
        if (getRowHeaderMode() == ROW_HEADER_MODE_HIDDEN) {
            Resource itemIcon = getItemIcon(cells[CELL_ITEMID][indexInRowbuffer]);
            if (itemIcon != null) {
                target.addAttribute("icon", itemIcon);
            }
        } else if (cells[CELL_ICON][indexInRowbuffer] != null) {
            target.addAttribute("icon",
                    (Resource) cells[CELL_ICON][indexInRowbuffer]);
        }
    }

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        super.changeVariables(source, variables);

        if (variables.containsKey("toggleCollapsed")) {
            String object = (String) variables.get("toggleCollapsed");
            Object itemId = itemIdMapper.get(object);
            toggledItemId = itemId;
            toggleChildVisibility(itemId, false);
            if (variables.containsKey("selectCollapsed")) {
                // ensure collapsed is selected unless opened with selection
                // head
                if (isSelectable()) {
                    select(itemId);
                }
            }
        } else if (variables.containsKey("focusParent")) {
            String key = (String) variables.get("focusParent");
            Object refId = itemIdMapper.get(key);
            Object itemId = getParent(refId);
            focusParent(itemId);
        }
    }

    private void focusParent(Object itemId) {
        boolean inView = false;
        Object inPageId = getCurrentPageFirstItemId();
        for (int i = 0; inPageId != null && i < getPageLength(); i++) {
            if (inPageId.equals(itemId)) {
                inView = true;
                break;
            }
            inPageId = nextItemId(inPageId);
            i++;
        }
        if (!inView) {
            setCurrentPageFirstItemId(itemId);
        }
        // Select the row if it is selectable.
        if (isSelectable()) {
            if (isMultiSelect()) {
                setValue(Collections.singleton(itemId));
            } else {
                setValue(itemId);
            }
        }
        setFocusedRow(itemId);
    }

    private void setFocusedRow(Object itemId) {
        focusedRowId = itemId;
        if (focusedRowId == null) {
            // Must still inform the client that the focusParent request has
            // been processed
            clearFocusedRowPending = true;
        }
        markAsDirty();
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        if (focusedRowId != null) {
            target.addAttribute("focusedRow", itemIdMapper.key(focusedRowId));
            focusedRowId = null;
        } else if (clearFocusedRowPending) {
            // Must still inform the client that the focusParent request has
            // been processed
            target.addAttribute("clearFocusPending", true);
            clearFocusedRowPending = false;
        }
        target.addAttribute("animate", animationsEnabled);
        if (hierarchyColumnId != null) {
            Object[] visibleColumns2 = getVisibleColumns();
            for (int i = 0; i < visibleColumns2.length; i++) {
                Object object = visibleColumns2[i];
                if (hierarchyColumnId.equals(object)) {
                    target.addAttribute(
                            TreeTableConstants.ATTRIBUTE_HIERARCHY_COLUMN_INDEX,
                            i);
                    break;
                }
            }
        }
        super.paintContent(target);
        toggledItemId = null;
    }

    /*
     * Override methods for partial row updates and additions when expanding /
     * collapsing nodes.
     */

    @Override
    protected boolean isPartialRowUpdate() {
        return toggledItemId != null && containerSupportsPartialUpdates
                && !isRowCacheInvalidated();
    }

    @Override
    protected int getFirstAddedItemIndex() {
        return indexOfId(toggledItemId) + 1;
    }

    @Override
    protected int getAddedRowCount() {
        return countSubNodesRecursively(getContainerDataSource(), toggledItemId);
    }

    private int countSubNodesRecursively(Hierarchical hc, Object itemId) {
        int count = 0;
        // we need the number of children for toggledItemId no matter if its
        // collapsed or expanded. Other items' children are only counted if the
        // item is expanded.
        if (getContainerStrategy().isNodeOpen(itemId)
                || itemId == toggledItemId) {
            Collection<?> children = hc.getChildren(itemId);
            if (children != null) {
                count += children != null ? children.size() : 0;
                for (Object id : children) {
                    count += countSubNodesRecursively(hc, id);
                }
            }
        }
        return count;
    }

    @Override
    protected int getFirstUpdatedItemIndex() {
        return indexOfId(toggledItemId);
    }

    @Override
    protected int getUpdatedRowCount() {
        return 1;
    }

    @Override
    protected boolean shouldHideAddedRows() {
        return !getContainerStrategy().isNodeOpen(toggledItemId);
    }

    private void toggleChildVisibility(Object itemId, boolean forceFullRefresh) {
        getContainerStrategy().toggleChildVisibility(itemId);
        // ensure that page still has first item in page, DON'T clear the
        // caches.
        setCurrentPageFirstItemIndex(getCurrentPageFirstItemIndex(), false);

        if (isCollapsed(itemId)) {
            fireCollapseEvent(itemId);
        } else {
            fireExpandEvent(itemId);
        }

        if (containerSupportsPartialUpdates && !forceFullRefresh) {
            markAsDirty();
        } else {
            // For containers that do not send item set change events, always do
            // full repaint instead of partial row update.
            refreshRowCache();
        }
    }

    @Override
    public int size() {
        return getContainerStrategy().size();
    }

    @Override
    public Hierarchical getContainerDataSource() {
        return (Hierarchical) super.getContainerDataSource();
    }

    @Override
    public void setContainerDataSource(Container newDataSource) {
        cStrategy = null;

        // FIXME: This disables partial updates until TreeTable is fixed so it
        // does not change component hierarchy during paint
        containerSupportsPartialUpdates = (newDataSource instanceof ItemSetChangeNotifier) && false;

        if (newDataSource != null && !(newDataSource instanceof Hierarchical)) {
            newDataSource = new ContainerHierarchicalWrapper(newDataSource);
        }

        if (newDataSource != null && !(newDataSource instanceof Ordered)) {
            newDataSource = new HierarchicalContainerOrderedWrapper(
                    (Hierarchical) newDataSource);
        }

        super.setContainerDataSource(newDataSource);
    }

    @Override
    public void containerItemSetChange(
            com.vaadin.data.Container.ItemSetChangeEvent event) {
        // Can't do partial repaints if items are added or removed during the
        // expand/collapse request
        toggledItemId = null;
        getContainerStrategy().containerItemSetChange(event);
        super.containerItemSetChange(event);
    }

    @Override
    protected Object getIdByIndex(int index) {
        return getContainerStrategy().getIdByIndex(index);
    }

    @Override
    protected int indexOfId(Object itemId) {
        return getContainerStrategy().indexOfId(itemId);
    }

    @Override
    public Object nextItemId(Object itemId) {
        return getContainerStrategy().nextItemId(itemId);
    }

    @Override
    public Object lastItemId() {
        return getContainerStrategy().lastItemId();
    }

    @Override
    public Object prevItemId(Object itemId) {
        return getContainerStrategy().prevItemId(itemId);
    }

    @Override
    public boolean isLastId(Object itemId) {
        return getContainerStrategy().isLastId(itemId);
    }

    @Override
    public Collection<?> getItemIds() {
        return getContainerStrategy().getItemIds();
    }

    @Override
    public boolean areChildrenAllowed(Object itemId) {
        return getContainerDataSource().areChildrenAllowed(itemId);
    }

    @Override
    public Collection<?> getChildren(Object itemId) {
        return getContainerDataSource().getChildren(itemId);
    }

    @Override
    public Object getParent(Object itemId) {
        return getContainerDataSource().getParent(itemId);
    }

    @Override
    public boolean hasChildren(Object itemId) {
        return getContainerDataSource().hasChildren(itemId);
    }

    @Override
    public boolean isRoot(Object itemId) {
        return getContainerDataSource().isRoot(itemId);
    }

    @Override
    public Collection<?> rootItemIds() {
        return getContainerDataSource().rootItemIds();
    }

    @Override
    public boolean setChildrenAllowed(Object itemId, boolean areChildrenAllowed)
            throws UnsupportedOperationException {
        return getContainerDataSource().setChildrenAllowed(itemId,
                areChildrenAllowed);
    }

    @Override
    public boolean setParent(Object itemId, Object newParentId)
            throws UnsupportedOperationException {
        return getContainerDataSource().setParent(itemId, newParentId);
    }

    /**
     * Sets the Item specified by given identifier as collapsed or expanded. If
     * the Item is collapsed, its children are not displayed to the user.
     * 
     * @param itemId
     *            the identifier of the Item
     * @param collapsed
     *            true if the Item should be collapsed, false if expanded
     */
    public void setCollapsed(Object itemId, boolean collapsed) {
        if (isCollapsed(itemId) != collapsed) {
            if (null == toggledItemId && !isRowCacheInvalidated()
                    && getVisibleItemIds().contains(itemId)) {
                // optimization: partial refresh if only one item is
                // collapsed/expanded
                toggledItemId = itemId;
                toggleChildVisibility(itemId, false);
            } else {
                // make sure a full refresh takes place - otherwise neither
                // partial nor full repaint of table content is performed
                toggledItemId = null;
                toggleChildVisibility(itemId, true);
            }
        }
    }

    /**
     * Checks if Item with given identifier is collapsed in the UI.
     * 
     * <p>
     * 
     * @param itemId
     *            the identifier of the checked Item
     * @return true if the Item with given id is collapsed
     * @see Collapsible#isCollapsed(Object)
     */
    public boolean isCollapsed(Object itemId) {
        return !getContainerStrategy().isNodeOpen(itemId);
    }

    /**
     * Explicitly sets the column in which the TreeTable visualizes the
     * hierarchy. If hierarchyColumnId is not set, the hierarchy is visualized
     * in the first visible column.
     * 
     * @param hierarchyColumnId
     */
    public void setHierarchyColumn(Object hierarchyColumnId) {
        this.hierarchyColumnId = hierarchyColumnId;
    }

    /**
     * @return the identifier of column into which the hierarchy will be
     *         visualized or null if the column is not explicitly defined.
     */
    public Object getHierarchyColumnId() {
        return hierarchyColumnId;
    }

    /**
     * Adds an expand listener.
     * 
     * @param listener
     *            the Listener to be added.
     */
    public void addExpandListener(ExpandListener listener) {
        addListener(ExpandEvent.class, listener, ExpandListener.EXPAND_METHOD);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addExpandListener(ExpandListener)}
     **/
    @Deprecated
    public void addListener(ExpandListener listener) {
        addExpandListener(listener);
    }

    /**
     * Removes an expand listener.
     * 
     * @param listener
     *            the Listener to be removed.
     */
    public void removeExpandListener(ExpandListener listener) {
        removeListener(ExpandEvent.class, listener,
                ExpandListener.EXPAND_METHOD);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeExpandListener(ExpandListener)}
     **/
    @Deprecated
    public void removeListener(ExpandListener listener) {
        removeExpandListener(listener);
    }

    /**
     * Emits an expand event.
     * 
     * @param itemId
     *            the item id.
     */
    protected void fireExpandEvent(Object itemId) {
        fireEvent(new ExpandEvent(this, itemId));
    }

    /**
     * Adds a collapse listener.
     * 
     * @param listener
     *            the Listener to be added.
     */
    public void addCollapseListener(CollapseListener listener) {
        addListener(CollapseEvent.class, listener,
                CollapseListener.COLLAPSE_METHOD);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addCollapseListener(CollapseListener)}
     **/
    @Deprecated
    public void addListener(CollapseListener listener) {
        addCollapseListener(listener);
    }

    /**
     * Removes a collapse listener.
     * 
     * @param listener
     *            the Listener to be removed.
     */
    public void removeCollapseListener(CollapseListener listener) {
        removeListener(CollapseEvent.class, listener,
                CollapseListener.COLLAPSE_METHOD);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeCollapseListener(CollapseListener)}
     **/
    @Deprecated
    public void removeListener(CollapseListener listener) {
        removeCollapseListener(listener);
    }

    /**
     * Emits a collapse event.
     * 
     * @param itemId
     *            the item id.
     */
    protected void fireCollapseEvent(Object itemId) {
        fireEvent(new CollapseEvent(this, itemId));
    }

    /**
     * @return true if animations are enabled
     */
    public boolean isAnimationsEnabled() {
        return animationsEnabled;
    }

    /**
     * Animations can be enabled by passing true to this method. Currently
     * expanding rows slide in from the top and collapsing rows slide out the
     * same way. NOTE! not supported in Internet Explorer 6 or 7.
     * 
     * @param animationsEnabled
     *            true or false whether to enable animations or not.
     */
    public void setAnimationsEnabled(boolean animationsEnabled) {
        this.animationsEnabled = animationsEnabled;
        markAsDirty();
    }

    private static final Logger getLogger() {
        return Logger.getLogger(TreeTable.class.getName());
    }

    @Override
    protected List<Object> getItemIds(int firstIndex, int rows) {
        List<Object> itemIds = new ArrayList<Object>();
        for (int i = firstIndex; i < firstIndex + rows; i++) {
            itemIds.add(getIdByIndex(i));
        }
        return itemIds;
    }
}

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
package com.vaadin.client.ui.tree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.dom.client.Element;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.Paintable;
import com.vaadin.client.TooltipInfo;
import com.vaadin.client.UIDL;
import com.vaadin.client.Util;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.VTree;
import com.vaadin.client.ui.VTree.TreeNode;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.shared.ui.tree.TreeConstants;
import com.vaadin.shared.ui.tree.TreeState;
import com.vaadin.ui.Tree;

@Connect(Tree.class)
public class TreeConnector extends AbstractComponentConnector implements
        Paintable {

    protected final Map<TreeNode, TooltipInfo> tooltipMap = new HashMap<TreeNode, TooltipInfo>();

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (!isRealUpdate(uidl)) {
            return;
        }

        getWidget().rendering = true;

        getWidget().client = client;

        if (uidl.hasAttribute("partialUpdate")) {
            handleUpdate(uidl);
            getWidget().rendering = false;
            return;
        }

        getWidget().paintableId = uidl.getId();

        getWidget().immediate = getState().immediate;

        getWidget().disabled = !isEnabled();
        getWidget().readonly = isReadOnly();

        getWidget().dragMode = uidl.hasAttribute("dragMode") ? uidl
                .getIntAttribute("dragMode") : 0;

        getWidget().isNullSelectionAllowed = uidl
                .getBooleanAttribute("nullselect");

        if (uidl.hasAttribute("alb")) {
            getWidget().bodyActionKeys = uidl.getStringArrayAttribute("alb");
        }

        getWidget().body.clear();
        // clear out any references to nodes that no longer are attached
        getWidget().clearNodeToKeyMap();
        tooltipMap.clear();

        TreeNode childTree = null;
        UIDL childUidl = null;
        for (final Iterator<?> i = uidl.getChildIterator(); i.hasNext();) {
            childUidl = (UIDL) i.next();
            if ("actions".equals(childUidl.getTag())) {
                updateActionMap(childUidl);
                continue;
            } else if ("-ac".equals(childUidl.getTag())) {
                getWidget().updateDropHandler(childUidl);
                continue;
            }
            childTree = getWidget().new TreeNode();
            getConnection().getVTooltip().connectHandlersToWidget(childTree);
            updateNodeFromUIDL(childTree, childUidl);
            getWidget().body.add(childTree);
            childTree.addStyleDependentName("root");
            childTree.childNodeContainer.addStyleDependentName("root");
        }
        if (childTree != null && childUidl != null) {
            boolean leaf = !childUidl.getTag().equals("node");
            childTree.addStyleDependentName(leaf ? "leaf-last" : "last");
            childTree.childNodeContainer.addStyleDependentName("last");
        }
        final String selectMode = uidl.getStringAttribute("selectmode");
        getWidget().selectable = !"none".equals(selectMode);
        getWidget().isMultiselect = "multi".equals(selectMode);

        if (getWidget().isMultiselect) {
            if (BrowserInfo.get().isTouchDevice()) {
                // Always use the simple mode for touch devices that do not have
                // shift/ctrl keys (#8595)
                getWidget().multiSelectMode = MultiSelectMode.SIMPLE;
            } else {
                getWidget().multiSelectMode = MultiSelectMode.valueOf(uidl
                        .getStringAttribute("multiselectmode"));
            }
        }

        getWidget().selectedIds = uidl.getStringArrayVariableAsSet("selected");

        // Update lastSelection and focusedNode to point to *actual* nodes again
        // after the old ones have been cleared from the body. This fixes focus
        // and keyboard navigation issues as described in #7057 and other
        // tickets.
        if (getWidget().lastSelection != null) {
            getWidget().lastSelection = getWidget().getNodeByKey(
                    getWidget().lastSelection.key);
        }
        if (getWidget().focusedNode != null) {
            getWidget().setFocusedNode(
                    getWidget().getNodeByKey(getWidget().focusedNode.key));
        }

        if (getWidget().lastSelection == null
                && getWidget().focusedNode == null
                && !getWidget().selectedIds.isEmpty()) {
            getWidget().setFocusedNode(
                    getWidget().getNodeByKey(
                            getWidget().selectedIds.iterator().next()));
            getWidget().focusedNode.setFocused(false);
        }

        getWidget().rendering = false;

    }

    @Override
    public VTree getWidget() {
        return (VTree) super.getWidget();
    }

    private void handleUpdate(UIDL uidl) {
        final TreeNode rootNode = getWidget().getNodeByKey(
                uidl.getStringAttribute("rootKey"));
        if (rootNode != null) {
            if (!rootNode.getState()) {
                // expanding node happened server side
                rootNode.setState(true, false);
            }
            renderChildNodes(rootNode, (Iterator) uidl.getChildIterator());
        }
    }

    /**
     * Registers action for the root and also for individual nodes
     * 
     * @param uidl
     */
    private void updateActionMap(UIDL uidl) {
        final Iterator<?> it = uidl.getChildIterator();
        while (it.hasNext()) {
            final UIDL action = (UIDL) it.next();
            final String key = action.getStringAttribute("key");
            final String caption = action
                    .getStringAttribute(TreeConstants.ATTRIBUTE_ACTION_CAPTION);
            String iconUrl = null;
            if (action.hasAttribute(TreeConstants.ATTRIBUTE_ACTION_ICON)) {
                iconUrl = getConnection()
                        .translateVaadinUri(
                                action.getStringAttribute(TreeConstants.ATTRIBUTE_ACTION_ICON));
            }
            getWidget().registerAction(key, caption, iconUrl);
        }

    }

    public void updateNodeFromUIDL(TreeNode treeNode, UIDL uidl) {
        String nodeKey = uidl.getStringAttribute("key");
        treeNode.setText(uidl
                .getStringAttribute(TreeConstants.ATTRIBUTE_NODE_CAPTION));
        treeNode.key = nodeKey;

        getWidget().registerNode(treeNode);

        if (uidl.hasAttribute("al")) {
            treeNode.actionKeys = uidl.getStringArrayAttribute("al");
        }

        if (uidl.getTag().equals("node")) {
            if (uidl.getChildCount() == 0) {
                treeNode.childNodeContainer.setVisible(false);
            } else {
                renderChildNodes(treeNode, (Iterator) uidl.getChildIterator());
                treeNode.childrenLoaded = true;
            }
        } else {
            treeNode.addStyleName(TreeNode.CLASSNAME + "-leaf");
        }
        if (uidl.hasAttribute(TreeConstants.ATTRIBUTE_NODE_STYLE)) {
            treeNode.setNodeStyleName(uidl
                    .getStringAttribute(TreeConstants.ATTRIBUTE_NODE_STYLE));
        }

        String description = uidl.getStringAttribute("descr");
        if (description != null) {
            tooltipMap.put(treeNode, new TooltipInfo(description));
        }

        if (uidl.getBooleanAttribute("expanded") && !treeNode.getState()) {
            treeNode.setState(true, false);
        }

        if (uidl.getBooleanAttribute("selected")) {
            treeNode.setSelected(true);
            // ensure that identifier is in selectedIds array (this may be a
            // partial update)
            getWidget().selectedIds.add(nodeKey);
        }

        treeNode.setIcon(uidl
                .getStringAttribute(TreeConstants.ATTRIBUTE_NODE_ICON));
    }

    void renderChildNodes(TreeNode containerNode, Iterator<UIDL> i) {
        containerNode.childNodeContainer.clear();
        containerNode.childNodeContainer.setVisible(true);
        while (i.hasNext()) {
            final UIDL childUidl = i.next();
            // actions are in bit weird place, don't mix them with children,
            // but current node's actions
            if ("actions".equals(childUidl.getTag())) {
                updateActionMap(childUidl);
                continue;
            }
            final TreeNode childTree = getWidget().new TreeNode();
            getConnection().getVTooltip().connectHandlersToWidget(childTree);
            updateNodeFromUIDL(childTree, childUidl);
            containerNode.childNodeContainer.add(childTree);
            if (!i.hasNext()) {
                childTree
                        .addStyleDependentName(childTree.isLeaf() ? "leaf-last"
                                : "last");
                childTree.childNodeContainer.addStyleDependentName("last");
            }
        }
        containerNode.childrenLoaded = true;
    }

    @Override
    public boolean isReadOnly() {
        return super.isReadOnly() || getState().propertyReadOnly;
    }

    @Override
    public TreeState getState() {
        return (TreeState) super.getState();
    }

    @Override
    public TooltipInfo getTooltipInfo(Element element) {

        TooltipInfo info = null;

        // Try to find a tooltip for a node
        if (element != getWidget().getElement()) {
            Object node = Util.findWidget(
                    (com.google.gwt.user.client.Element) element,
                    TreeNode.class);

            if (node != null) {
                TreeNode tnode = (TreeNode) node;
                if (tnode.isCaptionElement(element)) {
                    info = tooltipMap.get(tnode);
                }
            }
        }

        // If no tooltip found for the node or if the target was not a node, use
        // the default tooltip
        if (info == null) {
            info = super.getTooltipInfo(element);
        }

        return info;
    }

}

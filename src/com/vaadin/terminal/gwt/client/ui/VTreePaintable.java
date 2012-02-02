/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.TooltipInfo;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.VTree.TreeNode;

public class VTreePaintable extends VAbstractPaintableWidget {

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // Ensure correct implementation and let container manage caption
        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        getWidgetForPaintable().rendering = true;

        getWidgetForPaintable().client = client;

        if (uidl.hasAttribute("partialUpdate")) {
            handleUpdate(uidl);
            getWidgetForPaintable().rendering = false;
            return;
        }

        getWidgetForPaintable().paintableId = uidl.getId();

        getWidgetForPaintable().immediate = uidl.hasAttribute("immediate");

        getWidgetForPaintable().disabled = uidl.getBooleanAttribute("disabled");
        getWidgetForPaintable().readonly = uidl.getBooleanAttribute("readonly");

        getWidgetForPaintable().dragMode = uidl.hasAttribute("dragMode") ? uidl
                .getIntAttribute("dragMode") : 0;

        getWidgetForPaintable().isNullSelectionAllowed = uidl
                .getBooleanAttribute("nullselect");

        if (uidl.hasAttribute("alb")) {
            getWidgetForPaintable().bodyActionKeys = uidl
                    .getStringArrayAttribute("alb");
        }

        getWidgetForPaintable().body.clear();
        // clear out any references to nodes that no longer are attached
        getWidgetForPaintable().clearNodeToKeyMap();
        TreeNode childTree = null;
        UIDL childUidl = null;
        for (final Iterator<?> i = uidl.getChildIterator(); i.hasNext();) {
            childUidl = (UIDL) i.next();
            if ("actions".equals(childUidl.getTag())) {
                updateActionMap(childUidl);
                continue;
            } else if ("-ac".equals(childUidl.getTag())) {
                getWidgetForPaintable().updateDropHandler(childUidl);
                continue;
            }
            childTree = getWidgetForPaintable().new TreeNode();
            updateNodeFromUIDL(childTree, childUidl);
            getWidgetForPaintable().body.add(childTree);
            childTree.addStyleDependentName("root");
            childTree.childNodeContainer.addStyleDependentName("root");
        }
        if (childTree != null && childUidl != null) {
            boolean leaf = !childUidl.getTag().equals("node");
            childTree.addStyleDependentName(leaf ? "leaf-last" : "last");
            childTree.childNodeContainer.addStyleDependentName("last");
        }
        final String selectMode = uidl.getStringAttribute("selectmode");
        getWidgetForPaintable().selectable = !"none".equals(selectMode);
        getWidgetForPaintable().isMultiselect = "multi".equals(selectMode);

        if (getWidgetForPaintable().isMultiselect) {
            getWidgetForPaintable().multiSelectMode = uidl
                    .getIntAttribute("multiselectmode");
        }

        getWidgetForPaintable().selectedIds = uidl
                .getStringArrayVariableAsSet("selected");

        // Update lastSelection and focusedNode to point to *actual* nodes again
        // after the old ones have been cleared from the body. This fixes focus
        // and keyboard navigation issues as described in #7057 and other
        // tickets.
        if (getWidgetForPaintable().lastSelection != null) {
            getWidgetForPaintable().lastSelection = getWidgetForPaintable()
                    .getNodeByKey(getWidgetForPaintable().lastSelection.key);
        }
        if (getWidgetForPaintable().focusedNode != null) {
            getWidgetForPaintable().setFocusedNode(
                    getWidgetForPaintable().getNodeByKey(
                            getWidgetForPaintable().focusedNode.key));
        }

        if (getWidgetForPaintable().lastSelection == null
                && getWidgetForPaintable().focusedNode == null
                && !getWidgetForPaintable().selectedIds.isEmpty()) {
            getWidgetForPaintable().setFocusedNode(
                    getWidgetForPaintable().getNodeByKey(
                            getWidgetForPaintable().selectedIds.iterator()
                                    .next()));
            getWidgetForPaintable().focusedNode.setFocused(false);
        }

        getWidgetForPaintable().rendering = false;

    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VTree.class);
    }

    @Override
    public VTree getWidgetForPaintable() {
        return (VTree) super.getWidgetForPaintable();
    }

    private void handleUpdate(UIDL uidl) {
        final TreeNode rootNode = getWidgetForPaintable().getNodeByKey(
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
            final String caption = action.getStringAttribute("caption");
            String iconUrl = null;
            if (action.hasAttribute("icon")) {
                iconUrl = getConnection().translateVaadinUri(
                        action.getStringAttribute("icon"));
            }
            getWidgetForPaintable().registerAction(key, caption, iconUrl);
        }

    }

    public void updateNodeFromUIDL(TreeNode treeNode, UIDL uidl) {
        String nodeKey = uidl.getStringAttribute("key");
        treeNode.setText(uidl.getStringAttribute("caption"));
        treeNode.key = nodeKey;

        getWidgetForPaintable().registerNode(treeNode);

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
        if (uidl.hasAttribute("style")) {
            treeNode.setNodeStyleName(uidl.getStringAttribute("style"));
        }

        String description = uidl.getStringAttribute("descr");
        if (description != null && getConnection() != null) {
            // Set tooltip
            TooltipInfo info = new TooltipInfo(description);
            getConnection().registerTooltip(this, nodeKey, info);
        } else {
            // Remove possible previous tooltip
            getConnection().registerTooltip(this, nodeKey, null);
        }

        if (uidl.getBooleanAttribute("expanded") && !treeNode.getState()) {
            treeNode.setState(true, false);
        }

        if (uidl.getBooleanAttribute("selected")) {
            treeNode.setSelected(true);
            // ensure that identifier is in selectedIds array (this may be a
            // partial update)
            getWidgetForPaintable().selectedIds.add(nodeKey);
        }

        treeNode.setIcon(uidl.getStringAttribute("icon"));
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
            final TreeNode childTree = getWidgetForPaintable().new TreeNode();
            updateNodeFromUIDL(childTree, childUidl);
            containerNode.add(childTree);
            if (!i.hasNext()) {
                childTree
                        .addStyleDependentName(childTree.isLeaf() ? "leaf-last"
                                : "last");
                childTree.childNodeContainer.addStyleDependentName("last");
            }
        }
        containerNode.childrenLoaded = true;
    }
}

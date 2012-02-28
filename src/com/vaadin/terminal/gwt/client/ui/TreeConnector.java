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

public class TreeConnector extends AbstractComponentConnector {

    public static final String ATTRIBUTE_NODE_STYLE = "style";
    public static final String ATTRIBUTE_NODE_CAPTION = "caption";
    public static final String ATTRIBUTE_NODE_ICON = AbstractComponentConnector.ATTRIBUTE_ICON;

    public static final String ATTRIBUTE_ACTION_CAPTION = "caption";
    public static final String ATTRIBUTE_ACTION_ICON = AbstractComponentConnector.ATTRIBUTE_ICON;

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // Ensure correct implementation and let container manage caption
        super.updateFromUIDL(uidl, client);
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

        getWidget().immediate = getState().isImmediate();

        getWidget().disabled = getState().isDisabled();
        getWidget().readonly = getState().isReadOnly();

        getWidget().dragMode = uidl.hasAttribute("dragMode") ? uidl
                .getIntAttribute("dragMode") : 0;

        getWidget().isNullSelectionAllowed = uidl
                .getBooleanAttribute("nullselect");

        if (uidl.hasAttribute("alb")) {
            getWidget().bodyActionKeys = uidl
                    .getStringArrayAttribute("alb");
        }

        getWidget().body.clear();
        // clear out any references to nodes that no longer are attached
        getWidget().clearNodeToKeyMap();
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
            getWidget().multiSelectMode = uidl
                    .getIntAttribute("multiselectmode");
        }

        getWidget().selectedIds = uidl
                .getStringArrayVariableAsSet("selected");

        // Update lastSelection and focusedNode to point to *actual* nodes again
        // after the old ones have been cleared from the body. This fixes focus
        // and keyboard navigation issues as described in #7057 and other
        // tickets.
        if (getWidget().lastSelection != null) {
            getWidget().lastSelection = getWidget()
                    .getNodeByKey(getWidget().lastSelection.key);
        }
        if (getWidget().focusedNode != null) {
            getWidget().setFocusedNode(
                    getWidget().getNodeByKey(
                            getWidget().focusedNode.key));
        }

        if (getWidget().lastSelection == null
                && getWidget().focusedNode == null
                && !getWidget().selectedIds.isEmpty()) {
            getWidget().setFocusedNode(
                    getWidget().getNodeByKey(
                            getWidget().selectedIds.iterator()
                                    .next()));
            getWidget().focusedNode.setFocused(false);
        }

        getWidget().rendering = false;

    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VTree.class);
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
                    .getStringAttribute(ATTRIBUTE_ACTION_CAPTION);
            String iconUrl = null;
            if (action.hasAttribute(ATTRIBUTE_ACTION_ICON)) {
                iconUrl = getConnection().translateVaadinUri(
                        action.getStringAttribute(ATTRIBUTE_ACTION_ICON));
            }
            getWidget().registerAction(key, caption, iconUrl);
        }

    }

    public void updateNodeFromUIDL(TreeNode treeNode, UIDL uidl) {
        String nodeKey = uidl.getStringAttribute("key");
        treeNode.setText(uidl.getStringAttribute(ATTRIBUTE_NODE_CAPTION));
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
        if (uidl.hasAttribute(ATTRIBUTE_NODE_STYLE)) {
            treeNode.setNodeStyleName(uidl
                    .getStringAttribute(ATTRIBUTE_NODE_STYLE));
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
            getWidget().selectedIds.add(nodeKey);
        }

        treeNode.setIcon(uidl.getStringAttribute(ATTRIBUTE_NODE_ICON));
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
}

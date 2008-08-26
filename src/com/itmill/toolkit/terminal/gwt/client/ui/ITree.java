/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;

/**
 * 
 */
public class ITree extends FlowPanel implements Paintable {

    public static final String CLASSNAME = "i-tree";

    private Set selectedIds = new HashSet();
    private ApplicationConnection client;
    private String paintableId;
    private boolean selectable;
    private boolean isMultiselect;

    private final HashMap keyToNode = new HashMap();

    /**
     * This map contains captions and icon urls for actions like: * "33_c" ->
     * "Edit" * "33_i" -> "http://dom.com/edit.png"
     */
    private final HashMap actionMap = new HashMap();

    private boolean immediate;

    private boolean isNullSelectionAllowed = true;

    private boolean disabled = false;

    private boolean readonly;

    public ITree() {
        super();
        setStyleName(CLASSNAME);
    }

    private void updateActionMap(UIDL c) {
        final Iterator it = c.getChildIterator();
        while (it.hasNext()) {
            final UIDL action = (UIDL) it.next();
            final String key = action.getStringAttribute("key");
            final String caption = action.getStringAttribute("caption");
            actionMap.put(key + "_c", caption);
            if (action.hasAttribute("icon")) {
                // TODO need some uri handling ??
                actionMap.put(key + "_i", client.translateToolkitUri(action
                        .getStringAttribute("icon")));
            }
        }

    }

    public String getActionCaption(String actionKey) {
        return (String) actionMap.get(actionKey + "_c");
    }

    public String getActionIcon(String actionKey) {
        return (String) actionMap.get(actionKey + "_i");
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // Ensure correct implementation and let container manage caption
        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        this.client = client;

        if (uidl.hasAttribute("partialUpdate")) {
            handleUpdate(uidl);
            return;
        }

        paintableId = uidl.getId();

        immediate = uidl.hasAttribute("immediate");

        disabled = uidl.getBooleanAttribute("disabled");
        readonly = uidl.getBooleanAttribute("readonly");

        isNullSelectionAllowed = uidl.getBooleanAttribute("nullselect");

        clear();
        for (final Iterator i = uidl.getChildIterator(); i.hasNext();) {
            final UIDL childUidl = (UIDL) i.next();
            if ("actions".equals(childUidl.getTag())) {
                updateActionMap(childUidl);
                continue;
            }
            final TreeNode childTree = new TreeNode();
            this.add(childTree);
            childTree.updateFromUIDL(childUidl, client);
        }
        final String selectMode = uidl.getStringAttribute("selectmode");
        selectable = !"none".equals(selectMode);
        isMultiselect = "multi".equals(selectMode);

        selectedIds = uidl.getStringArrayVariableAsSet("selected");

    }

    private void handleUpdate(UIDL uidl) {
        final TreeNode rootNode = (TreeNode) keyToNode.get(uidl
                .getStringAttribute("rootKey"));
        if (rootNode != null) {
            if (!rootNode.getState()) {
                // expanding node happened server side
                rootNode.setState(true, false);
            }
            rootNode.renderChildNodes(uidl.getChildIterator());
        }

    }

    public void setSelected(TreeNode treeNode, boolean selected) {
        if (selected) {
            if (!isMultiselect) {
                while (selectedIds.size() > 0) {
                    final String id = (String) selectedIds.iterator().next();
                    final TreeNode oldSelection = (TreeNode) keyToNode.get(id);
                    if (oldSelection != null) {
                        // can be null if the node is not visible (parent
                        // expanded)
                        oldSelection.setSelected(false);
                    }
                    selectedIds.remove(id);
                }
            }
            treeNode.setSelected(true);
            selectedIds.add(treeNode.key);
        } else {
            if (!isNullSelectionAllowed) {
                if (!isMultiselect || selectedIds.size() == 1) {
                    return;
                }
            }
            selectedIds.remove(treeNode.key);
            treeNode.setSelected(false);
        }
        client.updateVariable(paintableId, "selected", selectedIds.toArray(),
                immediate);
    }

    public boolean isSelected(TreeNode treeNode) {
        return selectedIds.contains(treeNode.key);
    }

    protected class TreeNode extends SimplePanel implements ActionOwner {

        public static final String CLASSNAME = "i-tree-node";

        String key;

        private String[] actionKeys = null;

        private boolean childrenLoaded;

        private Element nodeCaptionDiv;

        protected Element nodeCaptionSpan;

        private FlowPanel childNodeContainer;

        private boolean open;

        private Icon icon;

        private Element ie6compatnode;

        public TreeNode() {
            constructDom();
            sinkEvents(Event.ONCLICK);
        }

        public void onBrowserEvent(Event event) {
            super.onBrowserEvent(event);
            if (disabled) {
                return;
            }
            if (DOM.eventGetType(event) == Event.ONCLICK) {
                final Element target = DOM.eventGetTarget(event);
                if (DOM.compare(getElement(), target)
                        || DOM.compare(ie6compatnode, target)) {
                    // state change
                    toggleState();
                } else if (!readonly && DOM.compare(target, nodeCaptionSpan)) {
                    // caption click = selection change
                    toggleSelection();
                }
                DOM.eventCancelBubble(event, true);
            } else {
                ApplicationConnection.getConsole().log(
                        "ITree event?? " + DOM.eventToString(event));
            }

        }

        private void toggleSelection() {
            if (selectable) {
                ITree.this.setSelected(this, !isSelected());
            }
        }

        private void toggleState() {
            setState(!getState(), true);
        }

        protected void constructDom() {
            // workaround for a very weird IE6 issue #1245
            ie6compatnode = DOM.createDiv();
            setStyleName(ie6compatnode, CLASSNAME + "-ie6compatnode");
            DOM.setInnerText(ie6compatnode, " ");
            DOM.appendChild(getElement(), ie6compatnode);

            DOM.sinkEvents(ie6compatnode, Event.ONCLICK);

            nodeCaptionDiv = DOM.createDiv();
            DOM.setElementProperty(nodeCaptionDiv, "className", CLASSNAME
                    + "-caption");
            nodeCaptionSpan = DOM.createSpan();
            DOM.appendChild(getElement(), nodeCaptionDiv);
            DOM.appendChild(nodeCaptionDiv, nodeCaptionSpan);

            childNodeContainer = new FlowPanel();
            childNodeContainer.setStylePrimaryName(CLASSNAME + "-children");
            setWidget(childNodeContainer);
        }

        public void onDetach() {
            Util.removeContextMenuEvent(nodeCaptionSpan);
            super.onDetach();
        }

        public void onAttach() {
            attachContextMenuEvent(nodeCaptionSpan);
            super.onAttach();
        }

        public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
            setText(uidl.getStringAttribute("caption"));
            key = uidl.getStringAttribute("key");

            keyToNode.put(key, this);

            if (uidl.hasAttribute("al")) {
                actionKeys = uidl.getStringArrayAttribute("al");
            }

            if (uidl.getTag().equals("node")) {
                if (uidl.getChildCount() == 0) {
                    childNodeContainer.setVisible(false);
                } else {
                    renderChildNodes(uidl.getChildIterator());
                    childrenLoaded = true;
                }
            } else {
                addStyleName(CLASSNAME + "-leaf");
            }
            addStyleName(CLASSNAME);

            if (uidl.getBooleanAttribute("expanded") && !getState()) {
                setState(true, false);
            }

            if (uidl.getBooleanAttribute("selected")) {
                setSelected(true);
            }

            if (uidl.hasAttribute("icon")) {
                if (icon == null) {
                    icon = new Icon(client);
                    DOM.insertBefore(nodeCaptionDiv, icon.getElement(),
                            nodeCaptionSpan);
                }
                icon.setUri(uidl.getStringAttribute("icon"));
            } else {
                if (icon != null) {
                    DOM.removeChild(nodeCaptionDiv, icon.getElement());
                    icon = null;
                }
            }
        }

        private void setState(boolean state, boolean notifyServer) {
            if (open == state) {
                return;
            }
            if (state) {
                if (!childrenLoaded && notifyServer) {
                    client.updateVariable(paintableId, "requestChildTree",
                            true, false);
                }
                if (notifyServer) {
                    client.updateVariable(paintableId, "expand",
                            new String[] { key }, true);
                }
                addStyleName(CLASSNAME + "-expanded");
                childNodeContainer.setVisible(true);
            } else {
                removeStyleName(CLASSNAME + "-expanded");
                childNodeContainer.setVisible(false);
                if (notifyServer) {
                    client.updateVariable(paintableId, "collapse",
                            new String[] { key }, true);
                }
            }

            open = state;
        }

        private boolean getState() {
            return open;
        }

        private void setText(String text) {
            DOM.setInnerText(nodeCaptionSpan, text);
        }

        private void renderChildNodes(Iterator i) {
            childNodeContainer.clear();
            childNodeContainer.setVisible(true);
            while (i.hasNext()) {
                final UIDL childUidl = (UIDL) i.next();
                // actions are in bit weird place, don't mix them with children,
                // but current node's actions
                if ("actions".equals(childUidl.getTag())) {
                    updateActionMap(childUidl);
                    continue;
                }
                final TreeNode childTree = new TreeNode();
                childNodeContainer.add(childTree);
                childTree.updateFromUIDL(childUidl, client);
            }
            childrenLoaded = true;
        }

        public boolean isChildrenLoaded() {
            return childrenLoaded;
        }

        public Action[] getActions() {
            if (actionKeys == null) {
                return new Action[] {};
            }
            final Action[] actions = new Action[actionKeys.length];
            for (int i = 0; i < actions.length; i++) {
                final String actionKey = actionKeys[i];
                final TreeAction a = new TreeAction(this, String.valueOf(key),
                        actionKey);
                a.setCaption(getActionCaption(actionKey));
                a.setIconUrl(getActionIcon(actionKey));
                actions[i] = a;
            }
            return actions;
        }

        public ApplicationConnection getClient() {
            return client;
        }

        public String getPaintableId() {
            return paintableId;
        }

        /**
         * Adds/removes IT Mill Toolkit spesific style name. This method ought
         * to be called only from Tree.
         * 
         * @param selected
         */
        public void setSelected(boolean selected) {
            // add style name to caption dom structure only, not to subtree
            setStyleName(nodeCaptionDiv, "i-tree-node-selected", selected);
        }

        public boolean isSelected() {
            return ITree.this.isSelected(this);
        }

        public void showContextMenu(Event event) {
            if (!readonly && !disabled) {
                if (actionKeys != null) {
                    int left = DOM.eventGetClientX(event);
                    int top = DOM.eventGetClientY(event);
                    top += Window.getScrollTop();
                    left += Window.getScrollLeft();
                    client.getContextMenu().showAt(this, left, top);
                }
                DOM.eventCancelBubble(event, true);
            }

        }

        private native void attachContextMenuEvent(Element el)
        /*-{
        	var node = this;
        	el.oncontextmenu = function(e) {
        		if(!e)
        			e = $wnd.event;
        		node.@com.itmill.toolkit.terminal.gwt.client.ui.ITree.TreeNode::showContextMenu(Lcom/google/gwt/user/client/Event;)(e);
        		return false;
        	};
        }-*/;

    }
}

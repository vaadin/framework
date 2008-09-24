package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.BrowserInfo;
import com.itmill.toolkit.terminal.gwt.client.ContainerResizedListener;
import com.itmill.toolkit.terminal.gwt.client.ICaption;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;

public class IAccordion extends ITabsheetBase implements
        ContainerResizedListener {

    public static final String CLASSNAME = "i-accordion";

    private ArrayList stack = new ArrayList();

    private Set paintables = new HashSet();

    private String height;

    private HashMap lazyUpdateMap = new HashMap();

    public IAccordion() {
        super(CLASSNAME);
        // IE6 needs this to calculate offsetHeight correctly
        if (BrowserInfo.get().isIE6()) {
            DOM.setStyleAttribute(getElement(), "zoom", "1");
        }
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        iLayout();
        // finally render possible hidden tabs
        if (lazyUpdateMap.size() > 0) {
            for (Iterator iterator = lazyUpdateMap.keySet().iterator(); iterator
                    .hasNext();) {
                StackItem item = (StackItem) iterator.next();
                item.setContent((UIDL) lazyUpdateMap.get(item));
            }
            lazyUpdateMap.clear();
        }
    }

    private StackItem getSelectedStack() {
        if (stack.size() == 0) {
            return null;
        }
        return (StackItem) stack.get(activeTabIndex);
    }

    protected void renderTab(UIDL tabUidl, int index, boolean selected) {
        StackItem item;
        if (stack.size() <= index) {
            item = new StackItem(tabUidl);
            if (stack.size() == 0) {
                item.addStyleDependentName("first");
            }
            stack.add(item);
            add(item, getElement());
        } else {
            item = (StackItem) stack.get(index);
            item.updateCaption(tabUidl);
        }

        if (selected) {
            item.open();
            // content node is expected to be prepared prior render phase, force
            // layout phase
            iLayout();
            item.setContent(tabUidl.getChildUIDL(0));
        } else if (tabUidl.getChildCount() > 0) {
            // content node is expected to be prepared prior render phase, force
            // layout phase
            lazyUpdateMap.put(item, tabUidl.getChildUIDL(0));
        }
    }

    protected void selectTab(final int index, final UIDL contentUidl) {
        StackItem item = (StackItem) stack.get(index);
        if (index != activeTabIndex) {
            StackItem old = (StackItem) stack.get(activeTabIndex);
            if (old.isOpen()) {
                old.close();
            }
            activeTabIndex = index;
            item.open();
            iLayout();
        }
        item.setContent(contentUidl);
    }

    public void onSelectTab(StackItem item) {
        final int index = stack.indexOf(item);
        if (index != activeTabIndex && !disabled && !readonly
                && !disabledTabKeys.contains(tabKeys.get(index))) {
            if (getSelectedStack() != null) {
                getSelectedStack().close();
            }
            addStyleDependentName("loading");
            // run updating variables in deferred command to bypass some FF
            // optimization issues
            DeferredCommand.addCommand(new Command() {
                public void execute() {
                    client.updateVariable(id, "selected", ""
                            + tabKeys.get(index), true);
                }
            });
        }
    }

    public void setHeight(String height) {
        this.height = height;
        super.setHeight(height);
    }

    private void iLayout() {
        iLayout(-1, -1);
    }

    public void iLayout(int availableWidth, int availableHeight) {
        StackItem item = getSelectedStack();
        if (item == null) {
            return;
        }

        if (height != null && !height.equals("")) {

            int usedPixels = 0;
            for (Iterator iterator = stack.iterator(); iterator.hasNext();) {
                StackItem si = (StackItem) iterator.next();
                if (si != item) {
                    usedPixels += si.getOffsetHeight();
                }
            }

            int offsetHeight = getOffsetHeight();

            int spaceForOpenItem = offsetHeight - usedPixels;

            if (spaceForOpenItem > 0) {
                item.setHeight(spaceForOpenItem + "px");
            }
        } else {
            super.setHeight("");
            item.setHeight("");
        }

        Util.runDescendentsLayout(item);
    }

    /**
     * 
     */
    protected class StackItem extends ComplexPanel implements ClickListener {

        public void setHeight(String height) {
            super.setHeight(height);
            if (!"".equals(height)) {
                int offsetHeight = getOffsetHeight();
                int captionHeight = DOM.getElementPropertyInt(captionNode,
                        "offsetHeight");
                int contentSpace = offsetHeight - captionHeight;
                if (contentSpace < 0) {
                    contentSpace = 0;
                }
                fixContentNodeSize(contentSpace);
            } else {
                DOM.setStyleAttribute(content, "height", "");
            }
        }

        void fixContentNodeSize(int contentHeight) {
            DOM.setStyleAttribute(content, "height", contentHeight + "px");
            if (!open) {
                // fix also width
                DOM
                        .setStyleAttribute(content, "width", getOffsetWidth()
                                + "px");
            }
        }

        private ICaption caption;
        private boolean open = false;
        private Element content = DOM.createDiv();
        private Element captionNode = DOM.createDiv();
        private Paintable paintable;

        public StackItem(UIDL tabUidl) {
            setElement(DOM.createDiv());
            caption = new ICaption(null, client);
            caption.addClickListener(this);
            super.add(caption, captionNode);
            DOM.appendChild(captionNode, caption.getElement());
            DOM.appendChild(getElement(), captionNode);
            DOM.appendChild(getElement(), content);
            setStylePrimaryName(CLASSNAME + "-item");
            DOM.setElementProperty(content, "className", CLASSNAME
                    + "-item-content");
            DOM.setElementProperty(captionNode, "className", CLASSNAME
                    + "-item-caption");
            DOM.setStyleAttribute(content, "overflow", "auto");
            // Force 'hasLayout' in IE6 (prevents layout problems)
            if (BrowserInfo.get().isIE6()) {
                DOM.setStyleAttribute(content, "zoom", "1");
                DOM.setStyleAttribute(getElement(), "overflow", "hidden");
            }
            close();

            updateCaption(tabUidl);
        }

        public Element getContainerElement() {
            return content;
        }

        public Widget getPaintable() {
            if (getWidgetCount() > 1) {
                return getWidget(1);
            } else {
                return null;
            }
        }

        public void open() {
            open = true;
            DOM.setStyleAttribute(content, "position", "");
            DOM.setStyleAttribute(content, "top", "");
            DOM.setStyleAttribute(content, "left", "");
            DOM.setStyleAttribute(content, "visibility", "");
            DOM.setStyleAttribute(content, "width", "");
            if (height != null && !"".equals(height)) {
                setHeight("100%");
            } else {
                setHeight("");
            }
            DOM.setStyleAttribute(content, "overflow", "auto");
            addStyleDependentName("open");
        }

        public void close() {
            DOM.setStyleAttribute(content, "width", getOffsetWidth() + "px");
            DOM.setStyleAttribute(content, "height", getOffsetHeight() + "px");
            DOM.setStyleAttribute(content, "overflow", "hidden");
            DOM.setStyleAttribute(content, "visibility", "hidden");
            DOM.setStyleAttribute(content, "position", "absolute");
            DOM.setStyleAttribute(content, "top", "0");
            DOM.setStyleAttribute(content, "left", "0px");
            removeStyleDependentName("open");
            setHeight(""); // only open StackItem may contain height
            open = false;
        }

        public boolean isOpen() {
            return open;
        }

        public void setContent(UIDL contentUidl) {
            if (!isOpen()) {
                // ensure content node has right size
                StackItem openItem = getSelectedStack();
                int availableH = openItem.getOffsetHeight()
                        - DOM
                                .getElementPropertyInt(captionNode,
                                        "offsetHeight");
                fixContentNodeSize(availableH);
            }
            final Paintable newPntbl = client.getPaintable(contentUidl);
            if (getPaintable() == null) {
                add((Widget) newPntbl, content);
                paintables.add(newPntbl);
            } else if (getPaintable() != newPntbl) {
                client.unregisterPaintable((Paintable) getWidget(1));
                paintables.remove(getWidget(1));
                remove(1);
                add((Widget) newPntbl, content);
                paintables.add(newPntbl);
            }
            paintable = newPntbl;
            paintable.updateFromUIDL(contentUidl, client);
        }

        public void onClick(Widget sender) {
            onSelectTab(this);
        }

        public void updateCaption(UIDL uidl) {
            caption.updateCaption(uidl);
        }
    }

    protected void clearPaintables() {
        stack.clear();
        clear();
    }

    protected Iterator getPaintableIterator() {
        return paintables.iterator();
    }

    public boolean hasChildComponent(Widget component) {
        if (paintables.contains(component)) {
            return true;
        } else {
            return false;
        }
    }

    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        // TODO Auto-generated method stub
    }

    public void updateCaption(Paintable component, UIDL uidl) {
        for (Iterator iterator = stack.iterator(); iterator.hasNext();) {
            StackItem si = (StackItem) iterator.next();
            if (si.getPaintable() == component) {
                si.updateCaption(uidl);
                return;
            }
        }
    }

    public boolean childComponentSizesUpdated() {
        // TODO Auto-generated method stub
        return false;
    }

}

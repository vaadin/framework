package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.ArrayList;
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
import com.itmill.toolkit.terminal.gwt.client.Caption;
import com.itmill.toolkit.terminal.gwt.client.ContainerResizedListener;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;

public class IAccordion extends ITabsheetBase implements
        ContainerResizedListener {

    public static final String CLASSNAME = "i-accordion";

    private ArrayList stack = new ArrayList();

    private Set paintables = new HashSet();

    private String height;

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
            item.setContent(tabUidl.getChildUIDL(0));
        } else if (tabUidl.getChildCount() > 0) {
            item.setContent(tabUidl.getChildUIDL(0));
        }
    }

    protected void selectTab(final int index, final UIDL contentUidl) {
        StackItem item = (StackItem) stack.get(index);
        if (index != activeTabIndex) {
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

    public void setWidth(String width) {
        if (width.equals("100%")) {
            super.setWidth("");
        } else {
            super.setWidth(width);
        }
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void iLayout() {
        StackItem item = getSelectedStack();
        if (item == null) {
            return;
        }

        if (height != null && height != "") {
            // Detach visible widget from document flow for a while to calculate
            // used height correctly
            Widget w = item.getPaintable();
            String originalPositioning = "";
            if (w != null) {
                originalPositioning = DOM.getStyleAttribute(w.getElement(),
                        "position");
                DOM.setStyleAttribute(w.getElement(), "visibility", "hidden");
                DOM.setStyleAttribute(w.getElement(), "position", "absolute");
            }
            DOM.setStyleAttribute(item.getContainerElement(), "height", "0");

            // Calculate target height
            super.setHeight(height);
            int targetHeight = DOM.getElementPropertyInt(DOM
                    .getParent(getElement()), "offsetHeight");
            super.setHeight("");

            // Calculate used height
            int usedHeight = getOffsetHeight();

            int h = targetHeight - usedHeight;
            if (h < 0) {
                h = 0;
            }
            DOM.setStyleAttribute(item.getContainerElement(), "height", h
                    + "px");

            // Put widget back into normal flow
            if (w != null) {
                DOM.setStyleAttribute(w.getElement(), "position",
                        originalPositioning);
                DOM.setStyleAttribute(w.getElement(), "visibility", "");
            }
        } else {
            DOM.setStyleAttribute(item.getContainerElement(), "height", "");
        }

        Util.runDescendentsLayout(this);
    }

    /**
     * TODO Caption widget not properly attached
     */
    protected class StackItem extends ComplexPanel implements ClickListener {

        private Caption caption;
        private boolean open = false;
        private Element content = DOM.createDiv();
        private Element captionNode = DOM.createDiv();
        private Paintable paintable;

        public StackItem(UIDL tabUidl) {
            setElement(DOM.createDiv());
            caption = new Caption(null, client);
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
            if (getPaintable() != null) {
                remove(getPaintable());
            }
            DOM.setStyleAttribute(content, "visibility", "");
            DOM.setStyleAttribute(content, "position", "");
            addStyleDependentName("open");
            if (getPaintable() != null) {
                add(getPaintable(), content);
            }
        }

        public void close() {
            open = false;
            DOM.setStyleAttribute(content, "visibility", "hidden");
            DOM.setStyleAttribute(content, "position", "absolute");
            removeStyleDependentName("open");
        }

        public boolean isOpen() {
            return open;
        }

        public void setContent(UIDL contentUidl) {
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

}

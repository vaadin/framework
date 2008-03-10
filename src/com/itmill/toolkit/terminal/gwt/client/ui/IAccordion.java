package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.ArrayList;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.ContainerResizedListener;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;

public class IAccordion extends ITabsheetBase implements
        ContainerResizedListener {

    public static final String CLASSNAME = "i-accordion";

    private ArrayList stack;

    private String height;

    public IAccordion() {
        super(CLASSNAME);
        stack = new ArrayList();
        // IE6 needs this to calculate offsetHeight correctly
        if (Util.isIE6()) {
            DOM.setStyleAttribute(getElement(), "zoom", "1");
        }
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);

        iLayout();
    }

    public void clear() {
        super.clear();
        stack.clear();
    }

    private StackItem getSelectedStack() {
        if (stack.size() == 0) {
            return null;
        }
        return (StackItem) stack.get(activeTabIndex);
    }

    protected void renderTab(UIDL contentUidl, String caption, int index,
            boolean selected) {
        // TODO check indexes, now new tabs get placed last (changing tab order
        // is not supported from server-side)
        StackItem item = new StackItem(caption);
        if (selected) {
            item.setContent(contentUidl);
            item.open();
        }

        if (stack.size() == 0) {
            item.addStyleDependentName("first");
        }

        stack.add(item);
        add(item);
    }

    protected void selectTab(final int index, final UIDL contentUidl) {
        if (index != activeTabIndex) {
            activeTabIndex = index;
            StackItem item = (StackItem) stack.get(index);
            item.setContent(contentUidl);
            item.open();
            iLayout();
        }
    }

    public void onSelectTab(StackItem item) {
        final int index = stack.indexOf(item);
        if (index != activeTabIndex && !disabled && !readonly) {
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
        if (item == null)
            return;

        if (height != null && height != "") {
            // Detach visible widget from document flow for a while to calculate
            // used height correctly
            Widget w = item.getWidget();
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
            if (h < 0)
                h = 0;
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

    protected class StackItem extends SimplePanel {

        private String caption;
        private Element captionNode;
        private boolean open = false;
        private Element content;

        protected StackItem() {
            setElement(DOM.createDiv());
            captionNode = DOM.createDiv();
            content = DOM.createDiv();
            // Additional SPAN element for styling
            DOM.appendChild(captionNode, DOM.createSpan());
            DOM.appendChild(getElement(), captionNode);
            DOM.appendChild(getElement(), content);
            setStylePrimaryName(CLASSNAME + "-item");
            DOM.setElementProperty(content, "className", CLASSNAME
                    + "-item-content");
            DOM.setElementProperty(captionNode, "className", CLASSNAME
                    + "-item-caption");
            DOM.setStyleAttribute(content, "overflow", "auto");
            DOM.setStyleAttribute(content, "display", "none");
            // Force 'hasLayout' in IE6 (prevents layout problems)
            if (Util.isIE6()) {
                DOM.setStyleAttribute(content, "zoom", "1");
            }
            sinkEvents(Event.ONCLICK);
        }

        public StackItem(String caption) {
            this();
            setCaption(caption);
        }

        public StackItem(String caption, UIDL contentUidl) {
            this();
            setCaption(caption);
            setContent(contentUidl);
        }

        public void setCaption(String caption) {
            this.caption = caption;
            DOM.setInnerText(DOM.getFirstChild(captionNode), caption);
        }

        public String getCaption() {
            return caption;
        }

        public void open() {
            open = true;
            DOM.setStyleAttribute(content, "display", "");
            addStyleDependentName("open");
        }

        public void close() {
            open = false;
            DOM.setStyleAttribute(content, "display", "none");
            removeStyleDependentName("open");
        }

        public boolean isOpen() {
            return open;
        }

        public void setContent(UIDL contentUidl) {
            final Paintable content = client.getPaintable(contentUidl);
            if (content != getWidget()) {
                client.unregisterPaintable((Paintable) getWidget());
            }
            setWidget((Widget) content);
            content.updateFromUIDL(contentUidl, client);
        }

        public Element getContainerElement() {
            return content;
        }

        public Element getCaptionElement() {
            return captionNode;
        }

        public void onBrowserEvent(Event evt) {
            if (DOM.eventGetType(evt) == Event.ONCLICK) {
                Element target = DOM.eventGetTarget(evt);
                if (DOM.compare(target, captionNode)
                        || DOM.compare(target, DOM.getFirstChild(captionNode))) {
                    ((IAccordion) getParent()).onSelectTab(this);
                }
            }
        }

    }

}

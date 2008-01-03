/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.ContainerResizedListener;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;

public class ITabsheet extends ITabsheetBase implements
        ContainerResizedListener {

    public static final String CLASSNAME = "i-tabsheet";

    private final TabBar tb;
    private final ITabsheetPanel tp;
    private final Element contentNode, deco;

    private String height;
    private String width;

    private final TabListener tl = new TabListener() {

        public void onTabSelected(SourcesTabEvents sender, final int tabIndex) {
            if (client != null && activeTabIndex != tabIndex) {
                addStyleDependentName("loading");
                // run updating variables in deferred command to bypass some FF
                // optimization issues
                DeferredCommand.addCommand(new Command() {
                    public void execute() {
                        client.updateVariable(id, "selected", ""
                                + tabKeys.get(tabIndex), true);
                    }
                });
            }
        }

        public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
            if (disabled) {
                return false;
            }
            return true;
        }

    };

    public ITabsheet() {
        super(CLASSNAME);

        tb = new TabBar();
        tp = new ITabsheetPanel();
        contentNode = DOM.createDiv();
        deco = DOM.createDiv();

        addStyleDependentName("loading"); // Indicate initial progress
        tb.setStyleName(CLASSNAME + "-tabs");
        DOM
                .setElementProperty(contentNode, "className", CLASSNAME
                        + "-content");
        DOM.setElementProperty(deco, "className", CLASSNAME + "-deco");

        add(tb);
        DOM.appendChild(getElement(), contentNode);
        insert(tp, contentNode, 0, true);
        DOM.appendChild(getElement(), deco);

        tb.addTabListener(tl);

        clear();

        // TODO Use for Safari only. Fix annoying 1px first cell in TabBar.
        DOM.setStyleAttribute(DOM.getFirstChild(DOM.getFirstChild(DOM
                .getFirstChild(tb.getElement()))), "display", "none");
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);

        // Add proper stylenames for all elements (easier to prevent unwanted
        // style inheritance)
        if (uidl.hasAttribute("style")) {
            final String[] styles = uidl.getStringAttribute("style").split(" ");
            final String contentBaseClass = "CLASSNAME" + "-content";
            String contentClass = contentBaseClass;
            final String decoBaseClass = CLASSNAME + "-deco";
            String decoClass = decoBaseClass;
            for (int i = 0; i < styles.length; i++) {
                tb.addStyleDependentName(styles[i]);
                contentClass += " " + contentBaseClass + "-" + styles[i];
                decoClass += " " + decoBaseClass + "-" + styles[i];
            }
            DOM.setElementProperty(contentNode, "className", contentClass);
            DOM.setElementProperty(deco, "className", decoClass);
        } else {
            tb.setStyleName(CLASSNAME + "-tabs");
            DOM.setElementProperty(contentNode, "className", CLASSNAME
                    + "-content");
            DOM.setElementProperty(deco, "className", CLASSNAME + "-deco");
        }

    }

    protected void renderTab(final UIDL contentUidl, String caption, int index,
            boolean selected) {
        // TODO check indexes, now new tabs get placed last (changing tab order
        // is not supported from server-side)
        tb.addTab(caption);
        if (selected) {
            renderContent(contentUidl);
            tb.selectTab(index);
        }
        // Add place-holder content
        tp.add(new Label(""));
    }

    protected void selectTab(int index, final UIDL contentUidl) {
        if (index != activeTabIndex) {
            activeTabIndex = index;
            renderContent(contentUidl);
        }
    }

    private void renderContent(final UIDL contentUIDL) {
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                final Paintable content = client.getPaintable(contentUIDL);
                tp.remove(activeTabIndex);
                tp.insert((Widget) content, activeTabIndex);
                tp.showWidget(activeTabIndex);
                (content).updateFromUIDL(contentUIDL, client);
                ITabsheet.this.removeStyleDependentName("loading");
                ITabsheet.this.iLayout();
            }
        });
    }

    public void clear() {
        int i = tb.getTabCount();
        while (i > 0) {
            tb.removeTab(--i);
        }
        tp.clear();

        // Get rid of unnecessary 100% cell heights in TabBar (really ugly hack)
        final Element tr = DOM.getChild(DOM.getChild(tb.getElement(), 0), 0);
        final Element rest = DOM.getChild(DOM.getChild(tr, DOM
                .getChildCount(tr) - 1), 0);
        DOM.removeElementAttribute(rest, "style");
    }

    public void setHeight(String height) {
        if (this.height == null && height == null)
            return;
        String oldHeight = this.height;
        this.height = height;
        if ((this.height != null && height == null)
                || (this.height == null && height != null)
                || !oldHeight.equals(height)) {
            iLayout();
        }
    }

    public void setWidth(String width) {
        String oldWidth = this.width;
        this.width = width;
        if ("100%".equals(width)) {
            // Allow browser to calculate width
            super.setWidth("");
        } else {
            super.setWidth(width);
        }
        if ((this.width != null && width == null)
                || (this.width == null && width != null)
                || !oldWidth.equals(width))
            // Run descendant layout functions
            Util.runDescendentsLayout(this);
    }

    public void iLayout() {
        if (height != null && height != "") {

            // Save scroll position
            int scrollTop = DOM.getElementPropertyInt(contentNode, "scrollTop");
            int scrollLeft = DOM.getElementPropertyInt(contentNode,
                    "scrollLeft");

            // Take content out of flow for a while
            final String originalPositioning = DOM.getStyleAttribute(tp
                    .getElement(), "position");
            DOM.setStyleAttribute(tp.getElement(), "position", "absolute");

            // Set defaults for content element
            DOM.setStyleAttribute(contentNode, "overflow", "hidden");
            DOM.setStyleAttribute(contentNode, "height", "");

            // Calculate target height
            super.setHeight(height);
            final int targetHeight = getOffsetHeight();

            // Calculate used height
            super.setHeight("");
            final int usedHeight = DOM.getElementPropertyInt(deco, "offsetTop")
                    + DOM.getElementPropertyInt(deco, "offsetHeight")
                    - DOM.getElementPropertyInt(getElement(), "offsetTop");

            // Calculate needed content area height
            int newHeight = targetHeight - usedHeight;
            if (newHeight < 0) {
                newHeight = 0;
            }

            // Set proper values for content element
            DOM.setStyleAttribute(contentNode, "height", newHeight + "px");
            DOM.setStyleAttribute(contentNode, "overflow", "auto");

            // Restore content to normal flow
            DOM.setStyleAttribute(tp.getElement(), "position",
                    originalPositioning);

            // Restore scroll position
            DOM.setElementPropertyInt(contentNode, "scrollTop", scrollTop);
            DOM.setElementPropertyInt(contentNode, "scrollLeft", scrollLeft);

        } else {
            DOM.setStyleAttribute(contentNode, "height", "");
            DOM.setStyleAttribute(contentNode, "overflow", "");
        }
        Util.runDescendentsLayout(this);
    }
}

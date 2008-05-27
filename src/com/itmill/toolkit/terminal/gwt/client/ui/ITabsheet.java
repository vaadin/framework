/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Iterator;

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
import com.itmill.toolkit.terminal.gwt.client.Caption;
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

    private boolean waitingForResponse;

    /**
     * Previous visible widget is set invisible with CSS (not display: none, but
     * visibility: hidden), to avoid flickering during render process. Normal
     * visibility must be returned later when new widget is rendered.
     */
    private Widget previousVisibleWidget;

    private final TabListener tl = new TabListener() {

        public void onTabSelected(SourcesTabEvents sender, final int tabIndex) {
            if (client != null && activeTabIndex != tabIndex) {
                addStyleDependentName("loading");
                // run updating variables in deferred command to bypass some
                // FF
                // optimization issues
                DeferredCommand.addCommand(new Command() {
                    public void execute() {
                        previousVisibleWidget = tp.getWidget(tp
                                .getVisibleWidget());
                        DOM.setStyleAttribute(previousVisibleWidget
                                .getElement(), "visibility", "hidden");
                        client.updateVariable(id, "selected", tabKeys.get(
                                tabIndex).toString(), true);
                    }
                });
                waitingForResponse = true;
            }
        }

        public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
            if (disabled || waitingForResponse) {
                return false;
            }
            final Object tabKey = tabKeys.get(tabIndex);
            if (disabledTabKeys.contains(tabKey)) {
                return false;
            }

            return true;
        }

    };

    public ITabsheet() {
        super(CLASSNAME);

        tb = new TabBar();
        tp = new ITabsheetPanel();
        tp.setStyleName(CLASSNAME + "-tabsheetpanel");
        contentNode = DOM.createDiv();
        deco = DOM.createDiv();

        addStyleDependentName("loading"); // Indicate initial progress
        tb.setStyleName(CLASSNAME + "-tabs");
        DOM
                .setElementProperty(contentNode, "className", CLASSNAME
                        + "-content");
        DOM.setElementProperty(deco, "className", CLASSNAME + "-deco");

        add(tb, getElement());
        DOM.appendChild(getElement(), contentNode);
        add(tp, contentNode);
        DOM.appendChild(getElement(), deco);

        tb.addTabListener(tl);

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
            final String contentBaseClass = CLASSNAME + "-content";
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

        if (uidl.hasAttribute("hidetabs")) {
            tb.setVisible(false);
            addStyleName(CLASSNAME + "-hidetabs");
        } else {
            tb.setVisible(true);
            removeStyleName(CLASSNAME + "-hidetabs");
        }
        waitingForResponse = false;
    }

    protected void renderTab(final UIDL tabUidl, int index, boolean selected) {
        // TODO check indexes, now new tabs get placed last (changing tab order
        // is not supported from server-side)
        Caption c = new Caption(null, client);
        c.updateCaption(tabUidl);
        tb.addTab(c);
        if (selected) {
            renderContent(tabUidl.getChildUIDL(0));
            tb.selectTab(index);
        } else if (tabUidl.getChildCount() > 0) {
            // updating a drawn child on hidden tab
            Paintable paintable = client.getPaintable(tabUidl.getChildUIDL(0));
            paintable.updateFromUIDL(tabUidl.getChildUIDL(0), client);
        }
        // Add place-holder content
        tp.add(new Label(""));
    }

    protected void selectTab(int index, final UIDL contentUidl) {
        if (index != activeTabIndex) {
            activeTabIndex = index;
            tb.selectTab(activeTabIndex);
        }
        renderContent(contentUidl);
    }

    private void renderContent(final UIDL contentUIDL) {
        final Paintable content = client.getPaintable(contentUIDL);
        if (tp.getWidgetCount() > activeTabIndex) {
            Widget old = tp.getWidget(activeTabIndex);
            if (old != content) {
                tp.remove(activeTabIndex);
                if (old instanceof Paintable) {
                    client.unregisterPaintable((Paintable) old);
                }
                tp.insert((Widget) content, activeTabIndex);
            }
        } else {
            tp.add((Widget) content);
        }

        tp.showWidget(activeTabIndex);

        ITabsheet.this.iLayout();
        (content).updateFromUIDL(contentUIDL, client);
        ITabsheet.this.removeStyleDependentName("loading");
        if (previousVisibleWidget != null) {
            DOM.setStyleAttribute(previousVisibleWidget.getElement(),
                    "visibility", "");
            previousVisibleWidget = null;
        }
    }

    public void setHeight(String height) {
        if (this.height == null && height == null) {
            return;
        }
        String oldHeight = this.height;
        this.height = height;
        if ((this.height != null && height == null)
                || (this.height == null && height != null)
                || !height.equals(oldHeight)) {
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
                || !width.equals(oldWidth)) {
            // Run descendant layout functions
            Util.runDescendentsLayout(this);
        }
    }

    public void iLayout() {
        if (height != null && height != "") {
            super.setHeight(height);

            final int contentHeight = getOffsetHeight()
                    - DOM.getElementPropertyInt(deco, "offsetHeight")
                    - tb.getOffsetHeight();

            // Set proper values for content element
            DOM.setStyleAttribute(contentNode, "height", contentHeight + "px");
            DOM.setStyleAttribute(contentNode, "overflow", "auto");
            tp.setHeight("100%");

        } else {
            DOM.setStyleAttribute(contentNode, "height", "");
            DOM.setStyleAttribute(contentNode, "overflow", "");
        }
        Util.runDescendentsLayout(this);
    }

    protected void clearPaintables() {

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

    protected Iterator getPaintableIterator() {
        return tp.iterator();
    }
}

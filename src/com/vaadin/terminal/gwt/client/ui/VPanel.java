/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.Set;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.Focusable;
import com.vaadin.terminal.gwt.client.RenderInformation;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VPaintableMap;
import com.vaadin.terminal.gwt.client.VPaintableWidget;
import com.vaadin.terminal.gwt.client.ui.ShortcutActionHandler.ShortcutActionHandlerOwner;

public class VPanel extends SimplePanel implements Container,
        ShortcutActionHandlerOwner, Focusable {

    public static final String CLASSNAME = "v-panel";

    ApplicationConnection client;

    String id;

    final Element captionNode = DOM.createDiv();

    private final Element captionText = DOM.createSpan();

    private Icon icon;

    final Element bottomDecoration = DOM.createDiv();

    final Element contentNode = DOM.createDiv();

    private Element errorIndicatorElement;

    private String height;

    VPaintableWidget layout;

    ShortcutActionHandler shortcutHandler;

    private String width = "";

    private Element geckoCaptionMeter;

    int scrollTop;

    int scrollLeft;

    private RenderInformation renderInformation = new RenderInformation();

    private int borderPaddingHorizontal = -1;

    private int borderPaddingVertical = -1;

    private int captionPaddingHorizontal = -1;

    private int captionMarginLeft = -1;

    boolean rendering;

    private int contentMarginLeft = -1;

    private String previousStyleName;

    private TouchScrollDelegate touchScrollDelegate;

    public VPanel() {
        super();
        DivElement captionWrap = Document.get().createDivElement();
        captionWrap.appendChild(captionNode);
        captionNode.appendChild(captionText);

        captionWrap.setClassName(CLASSNAME + "-captionwrap");
        captionNode.setClassName(CLASSNAME + "-caption");
        contentNode.setClassName(CLASSNAME + "-content");
        bottomDecoration.setClassName(CLASSNAME + "-deco");

        getElement().appendChild(captionWrap);

        /*
         * Make contentNode focusable only by using the setFocus() method. This
         * behaviour can be changed by invoking setTabIndex() in the serverside
         * implementation
         */
        contentNode.setTabIndex(-1);

        getElement().appendChild(contentNode);

        getElement().appendChild(bottomDecoration);
        setStyleName(CLASSNAME);
        DOM.sinkEvents(getElement(), Event.ONKEYDOWN);
        DOM.sinkEvents(contentNode, Event.ONSCROLL | Event.TOUCHEVENTS);
        contentNode.getStyle().setProperty("position", "relative");
        getElement().getStyle().setProperty("overflow", "hidden");
        addHandler(new TouchStartHandler() {
            public void onTouchStart(TouchStartEvent event) {
                getTouchScrollDelegate().onTouchStart(event);
            }
        }, TouchStartEvent.getType());
    }

    /**
     * Sets the keyboard focus on the Panel
     * 
     * @param focus
     *            Should the panel have focus or not.
     */
    public void setFocus(boolean focus) {
        if (focus) {
            getContainerElement().focus();
        } else {
            getContainerElement().blur();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.client.Focusable#focus()
     */
    public void focus() {
        setFocus(true);

    }

    @Override
    protected Element getContainerElement() {
        return contentNode;
    }

    void setCaption(String text) {
        DOM.setInnerHTML(captionText, text);
    }

    @Override
    public void setStyleName(String style) {
        if (!style.equals(previousStyleName)) {
            super.setStyleName(style);
            detectContainerBorders();
            previousStyleName = style;
        }
    }

    void handleError(UIDL uidl) {
        if (uidl.hasAttribute("error")) {
            if (errorIndicatorElement == null) {
                errorIndicatorElement = DOM.createSpan();
                DOM.setElementProperty(errorIndicatorElement, "className",
                        "v-errorindicator");
                DOM.sinkEvents(errorIndicatorElement, Event.MOUSEEVENTS);
                sinkEvents(Event.MOUSEEVENTS);
            }
            DOM.insertBefore(captionNode, errorIndicatorElement, captionText);
        } else if (errorIndicatorElement != null) {
            DOM.removeChild(captionNode, errorIndicatorElement);
            errorIndicatorElement = null;
        }
    }

    void setIconUri(UIDL uidl, ApplicationConnection client) {
        final String iconUri = uidl
                .hasAttribute(VAbstractPaintableWidget.ATTRIBUTE_ICON) ? uidl
                .getStringAttribute(VAbstractPaintableWidget.ATTRIBUTE_ICON)
                : null;
        if (iconUri == null) {
            if (icon != null) {
                DOM.removeChild(captionNode, icon.getElement());
                icon = null;
            }
        } else {
            if (icon == null) {
                icon = new Icon(client);
                DOM.insertChild(captionNode, icon.getElement(), 0);
            }
            icon.setUri(iconUri);
        }
    }

    public void runHacks(boolean runGeckoFix) {
        if ((BrowserInfo.get().isIE()) && (width == null || width.equals(""))) {
            /*
             * IE (what version??) needs width to be specified for the root DIV
             * so we calculate that from the sizes of the caption and layout
             */
            int captionWidth = captionText.getOffsetWidth()
                    + getCaptionMarginLeft() + getCaptionPaddingHorizontal();
            int layoutWidth = layout.getWidgetForPaintable().getOffsetWidth()
                    + getContainerBorderWidth();
            int width = layoutWidth;
            if (captionWidth > width) {
                width = captionWidth;
            }

            super.setWidth(width + "px");
        }

        if (runGeckoFix && BrowserInfo.get().isGecko()) {
            // workaround for #1764
            if (width == null || width.equals("")) {
                if (geckoCaptionMeter == null) {
                    geckoCaptionMeter = DOM.createDiv();
                    DOM.appendChild(captionNode, geckoCaptionMeter);
                }
                int captionWidth = DOM.getElementPropertyInt(captionText,
                        "offsetWidth");
                int availWidth = DOM.getElementPropertyInt(geckoCaptionMeter,
                        "offsetWidth");
                if (captionWidth == availWidth) {
                    /*
                     * Caption width defines panel width -> Gecko based browsers
                     * somehow fails to float things right, without the
                     * "noncode" below
                     */
                    setWidth(getOffsetWidth() + "px");
                } else {
                    DOM.setStyleAttribute(captionNode, "width", "");
                }
            }
        }

        client.runDescendentsLayout(this);

        Util.runWebkitOverflowAutoFix(contentNode);

    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);

        final Element target = DOM.eventGetTarget(event);
        final int type = DOM.eventGetType(event);
        if (type == Event.ONKEYDOWN && shortcutHandler != null) {
            shortcutHandler.handleKeyboardEvent(event);
            return;
        }
        if (type == Event.ONSCROLL) {
            int newscrollTop = DOM.getElementPropertyInt(contentNode,
                    "scrollTop");
            int newscrollLeft = DOM.getElementPropertyInt(contentNode,
                    "scrollLeft");
            if (client != null
                    && (newscrollLeft != scrollLeft || newscrollTop != scrollTop)) {
                scrollLeft = newscrollLeft;
                scrollTop = newscrollTop;
                client.updateVariable(id, "scrollTop", scrollTop, false);
                client.updateVariable(id, "scrollLeft", scrollLeft, false);
            }
        } else if (captionNode.isOrHasChild(target)) {
            if (client != null) {
                client.handleWidgetTooltipEvent(event, this);
            }
        }
    }

    protected TouchScrollDelegate getTouchScrollDelegate() {
        if (touchScrollDelegate == null) {
            touchScrollDelegate = new TouchScrollDelegate(contentNode);
        }
        return touchScrollDelegate;

    }

    @Override
    public void setHeight(String height) {
        this.height = height;
        super.setHeight(height);
        if (height != null && !"".equals(height)) {
            final int targetHeight = getOffsetHeight();
            int containerHeight = targetHeight
                    - captionNode.getParentElement().getOffsetHeight()
                    - bottomDecoration.getOffsetHeight()
                    - getContainerBorderHeight();
            if (containerHeight < 0) {
                containerHeight = 0;
            }
            DOM.setStyleAttribute(contentNode, "height", containerHeight + "px");
        } else {
            DOM.setStyleAttribute(contentNode, "height", "");
        }
        if (!rendering) {
            runHacks(true);
        }
    }

    private int getCaptionMarginLeft() {
        if (captionMarginLeft < 0) {
            detectContainerBorders();
        }
        return captionMarginLeft;
    }

    private int getContentMarginLeft() {
        if (contentMarginLeft < 0) {
            detectContainerBorders();
        }
        return contentMarginLeft;
    }

    private int getCaptionPaddingHorizontal() {
        if (captionPaddingHorizontal < 0) {
            detectContainerBorders();
        }
        return captionPaddingHorizontal;
    }

    private int getContainerBorderHeight() {
        if (borderPaddingVertical < 0) {
            detectContainerBorders();
        }
        return borderPaddingVertical;
    }

    @Override
    public void setWidth(String width) {
        if (this.width.equals(width)) {
            return;
        }

        this.width = width;
        super.setWidth(width);
        if (!rendering) {
            runHacks(true);

            if (height.equals("")) {
                // Width change may affect height
                Util.updateRelativeChildrenAndSendSizeUpdateEvent(client, this,
                        this);
            }

        }
    }

    private int getContainerBorderWidth() {
        if (borderPaddingHorizontal < 0) {
            detectContainerBorders();
        }
        return borderPaddingHorizontal;
    }

    private void detectContainerBorders() {
        DOM.setStyleAttribute(contentNode, "overflow", "hidden");

        borderPaddingHorizontal = Util.measureHorizontalBorder(contentNode);
        borderPaddingVertical = Util.measureVerticalBorder(contentNode);

        DOM.setStyleAttribute(contentNode, "overflow", "auto");

        captionPaddingHorizontal = Util.measureHorizontalPaddingAndBorder(
                captionNode, 26);

        captionMarginLeft = Util.measureMarginLeft(captionNode);
        contentMarginLeft = Util.measureMarginLeft(contentNode);

    }

    public boolean hasChildComponent(Widget component) {
        if (component != null && component == layout) {
            return true;
        } else {
            return false;
        }
    }

    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        // TODO This is untested as no layouts require this
        if (oldComponent != layout.getWidgetForPaintable()) {
            return;
        }

        setWidget(newComponent);
        layout = VPaintableMap.get(client).getPaintable(newComponent);
    }

    public RenderSpace getAllocatedSpace(Widget child) {
        int w = 0;
        int h = 0;

        if (width != null && !width.equals("")) {
            w = getOffsetWidth() - getContainerBorderWidth();
            if (w < 0) {
                w = 0;
            }
        }

        if (height != null && !height.equals("")) {
            h = contentNode.getOffsetHeight() - getContainerBorderHeight();
            if (h < 0) {
                h = 0;
            }
        }

        return new RenderSpace(w, h, true);
    }

    public boolean requestLayout(Set<Widget> children) {
        // content size change might cause change to its available space
        // (scrollbars)
        client.handleComponentRelativeSize(layout.getWidgetForPaintable());
        if (height != null && height != "" && width != null && width != "") {
            /*
             * If the height and width has been specified the child components
             * cannot make the size of the layout change
             */
            return true;
        }
        runHacks(false);
        return !renderInformation.updateSize(getElement());
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        detectContainerBorders();
    }

    public ShortcutActionHandler getShortcutActionHandler() {
        return shortcutHandler;
    }

}

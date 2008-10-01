/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Set;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.BrowserInfo;
import com.itmill.toolkit.terminal.gwt.client.ClientExceptionHandler;
import com.itmill.toolkit.terminal.gwt.client.Container;
import com.itmill.toolkit.terminal.gwt.client.ContainerResizedListener;
import com.itmill.toolkit.terminal.gwt.client.IErrorMessage;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.RenderInformation;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;
import com.itmill.toolkit.terminal.gwt.client.RenderInformation.Size;

public class IPanel extends SimplePanel implements Container,
        ContainerResizedListener {

    public static final String CLASSNAME = "i-panel";

    ApplicationConnection client;

    String id;

    private final Element captionNode = DOM.createDiv();

    private final Element captionText = DOM.createSpan();

    private Icon icon;

    private final Element bottomDecoration = DOM.createDiv();

    private final Element contentNode = DOM.createDiv();

    private Element errorIndicatorElement;

    private IErrorMessage errorMessage;

    private String height;

    private Paintable layout;

    ShortcutActionHandler shortcutHandler;

    private String width;

    private Element geckoCaptionMeter;

    private int scrollTop;

    private int scrollLeft;

    private RenderInformation renderInformation = new RenderInformation();

    private int borderPaddingHorizontal = 0;

    public IPanel() {
        super();
        DOM.appendChild(getElement(), captionNode);
        DOM.appendChild(captionNode, captionText);
        DOM.appendChild(getElement(), contentNode);
        DOM.appendChild(getElement(), bottomDecoration);
        setStyleName(CLASSNAME);
        DOM
                .setElementProperty(captionNode, "className", CLASSNAME
                        + "-caption");
        DOM
                .setElementProperty(contentNode, "className", CLASSNAME
                        + "-content");
        DOM.setElementProperty(bottomDecoration, "className", CLASSNAME
                + "-deco");
        DOM.sinkEvents(getElement(), Event.ONKEYDOWN);
        DOM.sinkEvents(contentNode, Event.ONSCROLL);
    }

    @Override
    protected Element getContainerElement() {
        return contentNode;
    }

    private void setCaption(String text) {
        DOM.setInnerText(captionText, text);
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // Ensure correct implementation
        if (client.updateComponent(this, uidl, false)) {
            return;
        }

        this.client = client;
        id = uidl.getId();

        // Restore default stylenames
        DOM
                .setElementProperty(captionNode, "className", CLASSNAME
                        + "-caption");
        DOM
                .setElementProperty(contentNode, "className", CLASSNAME
                        + "-content");
        DOM.setElementProperty(bottomDecoration, "className", CLASSNAME
                + "-deco");

        // Handle caption displaying
        boolean hasCaption = false;
        if (uidl.hasAttribute("caption")
                && !uidl.getStringAttribute("caption").equals("")) {
            setCaption(uidl.getStringAttribute("caption"));
            hasCaption = true;
        } else {
            setCaption("");
            DOM.setElementProperty(captionNode, "className", CLASSNAME
                    + "-nocaption");
        }

        setIconUri(uidl, client);

        handleDescription(uidl);

        handleError(uidl);

        // Add proper stylenames for all elements. This way we can prevent
        // unwanted CSS selector inheritance.
        if (uidl.hasAttribute("style")) {
            final String[] styles = uidl.getStringAttribute("style").split(" ");
            final String captionBaseClass = CLASSNAME
                    + (hasCaption ? "-caption" : "-nocaption");
            final String contentBaseClass = CLASSNAME + "-content";
            final String decoBaseClass = CLASSNAME + "-deco";
            String captionClass = captionBaseClass;
            String contentClass = contentBaseClass;
            String decoClass = decoBaseClass;
            for (int i = 0; i < styles.length; i++) {
                captionClass += " " + captionBaseClass + "-" + styles[i];
                contentClass += " " + contentBaseClass + "-" + styles[i];
                decoClass += " " + decoBaseClass + "-" + styles[i];
            }
            DOM.setElementProperty(captionNode, "className", captionClass);
            DOM.setElementProperty(contentNode, "className", contentClass);
            DOM.setElementProperty(bottomDecoration, "className", decoClass);
        }

        // Height adjustment
        iLayout(false);

        // Render content
        final UIDL layoutUidl = uidl.getChildUIDL(0);
        final Paintable newLayout = client.getPaintable(layoutUidl);
        if (newLayout != layout) {
            if (layout != null) {
                client.unregisterPaintable(layout);
            }
            setWidget((Widget) newLayout);
            layout = newLayout;
        }
        (layout).updateFromUIDL(layoutUidl, client);

        // We may have actions attached to this panel
        if (uidl.getChildCount() > 1) {
            final int cnt = uidl.getChildCount();
            for (int i = 1; i < cnt; i++) {
                UIDL childUidl = uidl.getChildUIDL(i);
                if (childUidl.getTag().equals("actions")) {
                    if (shortcutHandler == null) {
                        shortcutHandler = new ShortcutActionHandler(id, client);
                    }
                    shortcutHandler.updateActionMap(childUidl);
                }
            }
        }

        if (uidl.hasVariable("scrollTop")
                && uidl.getIntVariable("scrollTop") != scrollTop) {
            scrollTop = uidl.getIntVariable("scrollTop");
            DOM.setElementPropertyInt(contentNode, "scrollTop", scrollTop);
        }

        if (uidl.hasVariable("scrollLeft")
                && uidl.getIntVariable("scrollLeft") != scrollLeft) {
            scrollLeft = uidl.getIntVariable("scrollLeft");
            DOM.setElementPropertyInt(contentNode, "scrollLeft", scrollLeft);
        }

    }

    private void handleError(UIDL uidl) {
        if (uidl.hasAttribute("error")) {
            final UIDL errorUidl = uidl.getErrors();
            if (errorIndicatorElement == null) {
                errorIndicatorElement = DOM.createDiv();
                DOM.setElementProperty(errorIndicatorElement, "className",
                        "i-errorindicator");
                DOM.sinkEvents(errorIndicatorElement, Event.MOUSEEVENTS);
                sinkEvents(Event.MOUSEEVENTS);
            }
            DOM.insertBefore(captionNode, errorIndicatorElement, captionText);
            if (errorMessage == null) {
                errorMessage = new IErrorMessage();
            }
            errorMessage.updateFromUIDL(errorUidl);

        } else if (errorIndicatorElement != null) {
            DOM.removeChild(captionNode, errorIndicatorElement);
            errorIndicatorElement = null;
        }
    }

    private void handleDescription(UIDL uidl) {
        DOM.setElementProperty(captionText, "title", uidl
                .hasAttribute("description") ? uidl
                .getStringAttribute("description") : "");
    }

    private void setIconUri(UIDL uidl, ApplicationConnection client) {
        final String iconUri = uidl.hasAttribute("icon") ? uidl
                .getStringAttribute("icon") : null;
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

    public void iLayout() {
        iLayout(true);
    }

    public void iLayout(boolean runGeckoFix) {
        renderInformation.updateSize(getElement());

        if (BrowserInfo.get().isIE6() && width != null && !width.equals("")) {
            /*
             * IE6 requires overflow-hidden elements to have a width specified
             */
            /*
             * Fixes #1923 IPanel: Horizontal scrollbar does not appear in IE6
             * with wide content
             */

            /*
             * Caption must be shrunk for parent measurements to return correct
             * result in IE6
             */
            DOM.setStyleAttribute(captionNode, "width", "1px");

            int parentPadding = Util.measureHorizontalPadding(getElement(), 0);

            int parentWidthExcludingPadding = getElement().getOffsetWidth()
                    - parentPadding;

            int captionMarginLeft = captionNode.getAbsoluteLeft()
                    - getElement().getAbsoluteLeft();
            Util.setWidthExcludingPadding(captionNode,
                    parentWidthExcludingPadding - captionMarginLeft, 26);

            int contentMarginLeft = contentNode.getAbsoluteLeft()
                    - getElement().getAbsoluteLeft();

            Util.setWidthExcludingPadding(contentNode,
                    parentWidthExcludingPadding - contentMarginLeft, 2);

        }

        if (height != null && height != "") {
            final boolean hasChildren = getWidget() != null;
            Element contentEl = null;
            String origPositioning = null;

            if (hasChildren) {
                // Remove children temporary form normal flow to detect proper
                // size
                contentEl = getWidget().getElement();
                origPositioning = DOM.getStyleAttribute(contentEl, "position");
                DOM.setStyleAttribute(contentEl, "position", "absolute");
            }

            // Set defaults
            DOM.setStyleAttribute(contentNode, "overflow", "hidden");
            DOM.setStyleAttribute(contentNode, "height", "");

            // Calculate target height
            super.setHeight(height);
            final int targetHeight = getOffsetHeight();

            // Calculate used height
            super.setHeight("");
            if (BrowserInfo.get().isIE() && !hasChildren) {
                DOM.setStyleAttribute(contentNode, "height", "0px");
            }

            final int bottomTop = DOM.getElementPropertyInt(bottomDecoration,
                    "offsetTop");
            final int bottomHeight = DOM.getElementPropertyInt(
                    bottomDecoration, "offsetHeight");
            final int elementTop = DOM.getElementPropertyInt(getElement(),
                    "offsetTop");

            final int usedHeight = bottomTop + bottomHeight - elementTop;

            // Calculate content area height (don't allow negative values)
            int contentAreaHeight = targetHeight - usedHeight;
            if (contentAreaHeight < 0) {
                contentAreaHeight = 0;
            }

            renderInformation.setContentAreaHeight(contentAreaHeight);

            // Set proper values for content element
            DOM.setStyleAttribute(contentNode, "height", contentAreaHeight
                    + "px");
            DOM.setStyleAttribute(contentNode, "overflow", "auto");

            // Restore content to flow
            if (hasChildren) {
                DOM.setStyleAttribute(contentEl, "position", origPositioning);
            }

            // restore scroll position
            DOM.setElementPropertyInt(contentNode, "scrollTop", scrollTop);
            DOM.setElementPropertyInt(contentNode, "scrollLeft", scrollLeft);

        } else {
            DOM.setStyleAttribute(contentNode, "height", "");
        }

        if (width != null && !width.equals("")) {
            renderInformation.setContentAreaWidth(renderInformation
                    .getRenderedSize().getWidth()
                    - borderPaddingHorizontal);
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
    }

    @Override
    public void onBrowserEvent(Event event) {
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
        } else if (errorIndicatorElement != null
                && DOM.compare(target, errorIndicatorElement)) {
            switch (type) {
            case Event.ONMOUSEOVER:
                if (errorMessage != null) {
                    errorMessage.showAt(errorIndicatorElement);
                }
                break;
            case Event.ONMOUSEOUT:
                if (errorMessage != null) {
                    errorMessage.hide();
                }
                break;
            case Event.ONCLICK:
                ApplicationConnection.getConsole().log(
                        DOM.getInnerHTML(errorMessage.getElement()));
                return;
            default:
                break;
            }
        }
    }

    /**
     * Panel handles dimensions by itself.
     */
    @Override
    public void setHeight(String height) {
        this.height = height;
    }

    /**
     * Panel handles dimensions by itself.
     */
    @Override
    public void setWidth(String width) {
        this.width = width;

        super.setWidth(width);

        if (width.endsWith("px")) {
            try {
                // FIXME: More sane implementation
                borderPaddingHorizontal = Util.measureHorizontalPadding(
                        contentNode, -2);
                if (borderPaddingHorizontal < 0) {
                    borderPaddingHorizontal = -borderPaddingHorizontal;
                }
            } catch (Exception e) {
                ClientExceptionHandler.displayError(e);
            }
        }

    }

    public boolean hasChildComponent(Widget component) {
        if (component != null && component == layout) {
            return true;
        } else {
            return false;
        }
    }

    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        // TODO
    }

    public Size getAllocatedSpace(Widget child) {
        return renderInformation.getContentAreaSize();
    }

    public boolean requestLayout(Set<Paintable> child) {

        if (height != null && width != null) {
            /*
             * If the height and width has been specified the child components
             * cannot make the size of the layout change
             */

            return true;
        }

        if (renderInformation.updateSize(getElement())) {
            return false;
        } else {
            return true;
        }

    }

    public void updateCaption(Paintable component, UIDL uidl) {
        // TODO
    }

}

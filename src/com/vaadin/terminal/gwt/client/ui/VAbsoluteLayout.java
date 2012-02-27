/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VCaption;
import com.vaadin.terminal.gwt.client.VPaintableMap;
import com.vaadin.terminal.gwt.client.VPaintableWidget;

public class VAbsoluteLayout extends ComplexPanel {

    /** Tag name for widget creation */
    public static final String TAGNAME = "absolutelayout";

    /** Class name, prefix in styling */
    public static final String CLASSNAME = "v-absolutelayout";

    private DivElement marginElement;

    protected final Element canvas = DOM.createDiv();

    private Object previousStyleName;

    Map<String, AbsoluteWrapper> pidToComponentWrappper = new HashMap<String, AbsoluteWrapper>();

    protected ApplicationConnection client;

    public VAbsoluteLayout() {
        setElement(Document.get().createDivElement());
        setStyleName(CLASSNAME);
        marginElement = Document.get().createDivElement();
        canvas.getStyle().setProperty("position", "relative");
        canvas.getStyle().setProperty("overflow", "hidden");
        marginElement.appendChild(canvas);
        getElement().appendChild(marginElement);
    }

    AbsoluteWrapper getWrapper(ApplicationConnection client, UIDL componentUIDL) {
        AbsoluteWrapper wrapper = pidToComponentWrappper.get(componentUIDL
                .getId());
        if (wrapper == null) {
            wrapper = new AbsoluteWrapper(client.getPaintable(componentUIDL));
            pidToComponentWrappper.put(componentUIDL.getId(), wrapper);
            add(wrapper);
        }
        return wrapper;

    }

    @Override
    public void add(Widget child) {
        super.add(child, canvas);
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        // TODO do this so that canvas gets the sized properly (the area
        // inside marginals)
        canvas.getStyle().setProperty("width", width);
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        // TODO do this so that canvas gets the sized properly (the area
        // inside marginals)
        canvas.getStyle().setProperty("height", height);
    }

    public class AbsoluteWrapper extends SimplePanel {
        private String css;
        String left;
        String top;
        String right;
        String bottom;
        private String zIndex;

        private VPaintableWidget paintable;
        private VCaption caption;

        public AbsoluteWrapper(VPaintableWidget paintable) {
            this.paintable = paintable;
            setStyleName(CLASSNAME + "-wrapper");
        }

        public void updateCaption(UIDL uidl) {

            boolean captionIsNeeded = VCaption.isNeeded(uidl,
                    paintable.getState());
            if (captionIsNeeded) {
                if (caption == null) {
                    caption = new VCaption(paintable, client);
                    VAbsoluteLayout.this.add(caption);
                }
                caption.updateCaption(uidl);
                updateCaptionPosition();
            } else {
                if (caption != null) {
                    caption.removeFromParent();
                    caption = null;
                }
            }
        }

        @Override
        public void setWidget(Widget w) {
            // this fixes #5457 (Widget implementation can change on-the-fly)
            paintable = VPaintableMap.get(client).getPaintable(w);
            super.setWidget(w);
        }

        public void destroy() {
            if (caption != null) {
                caption.removeFromParent();
            }
            client.unregisterPaintable(paintable);
            removeFromParent();
        }

        public void updateFromUIDL(UIDL componentUIDL) {
            setPosition(componentUIDL.getStringAttribute("css"));
            if (getWidget() != paintable.getWidgetForPaintable()) {
                setWidget(paintable.getWidgetForPaintable());
            }
            UIDL childUIDL = componentUIDL.getChildUIDL(0);
            paintable.updateFromUIDL(childUIDL, client);
            if (childUIDL.hasAttribute("cached")) {
                // child may need relative size adjustment if wrapper details
                // have changed this could be optimized (check if wrapper size
                // has changed)
                client.handleComponentRelativeSize(paintable
                        .getWidgetForPaintable());
            }
        }

        public void setPosition(String stringAttribute) {
            if (css == null || !css.equals(stringAttribute)) {
                css = stringAttribute;
                top = right = bottom = left = zIndex = null;
                if (!css.equals("")) {
                    String[] properties = css.split(";");
                    for (int i = 0; i < properties.length; i++) {
                        String[] keyValue = properties[i].split(":");
                        if (keyValue[0].equals("left")) {
                            left = keyValue[1];
                        } else if (keyValue[0].equals("top")) {
                            top = keyValue[1];
                        } else if (keyValue[0].equals("right")) {
                            right = keyValue[1];
                        } else if (keyValue[0].equals("bottom")) {
                            bottom = keyValue[1];
                        } else if (keyValue[0].equals("z-index")) {
                            zIndex = keyValue[1];
                        }
                    }
                }
                // ensure ne values
                Style style = getElement().getStyle();
                /*
                 * IE8 dies when nulling zIndex, even in IE7 mode. All other css
                 * properties (and even in older IE's) accept null values just
                 * fine. Assign empty string instead of null.
                 */
                if (zIndex != null) {
                    style.setProperty("zIndex", zIndex);
                } else {
                    style.setProperty("zIndex", "");
                }
                style.setProperty("top", top);
                style.setProperty("left", left);
                style.setProperty("right", right);
                style.setProperty("bottom", bottom);

            }
            updateCaptionPosition();
        }

        void updateCaptionPosition() {
            if (caption != null) {
                Style style = caption.getElement().getStyle();
                style.setProperty("position", "absolute");
                style.setPropertyPx("left", getElement().getOffsetLeft());
                style.setPropertyPx("top", getElement().getOffsetTop()
                        - caption.getHeight());
            }
        }
    }

    /**
     * Returns the deepest nested child component which contains "element". The
     * child component is also returned if "element" is part of its caption.
     * 
     * @param element
     *            An element that is a nested sub element of the root element in
     *            this layout
     * @return The Paintable which the element is a part of. Null if the element
     *         belongs to the layout and not to a child.
     */
    VPaintableWidget getComponent(Element element) {
        return Util.getPaintableForElement(client, this, element);
    }

}

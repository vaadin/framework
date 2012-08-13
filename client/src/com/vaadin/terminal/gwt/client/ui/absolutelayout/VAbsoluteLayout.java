/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.absolutelayout;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.VCaption;

public class VAbsoluteLayout extends ComplexPanel {

    /** Tag name for widget creation */
    public static final String TAGNAME = "absolutelayout";

    /** Class name, prefix in styling */
    public static final String CLASSNAME = "v-absolutelayout";

    private DivElement marginElement;

    protected final Element canvas = DOM.createDiv();

    private Object previousStyleName;

    protected ApplicationConnection client;

    public VAbsoluteLayout() {
        setElement(Document.get().createDivElement());
        setStyleName(CLASSNAME);
        marginElement = Document.get().createDivElement();
        canvas.getStyle().setProperty("position", "relative");
        canvas.getStyle().setProperty("overflow", "hidden");
        marginElement.appendChild(canvas);
        getElement().appendChild(marginElement);

        canvas.setClassName(CLASSNAME + "-canvas");
        canvas.setClassName(CLASSNAME + "-margin");
    }

    @Override
    public void add(Widget child) {
        super.add(child, canvas);
    }

    public static class AbsoluteWrapper extends SimplePanel {
        private String css;
        String left;
        String top;
        String right;
        String bottom;
        private String zIndex;

        private VCaption caption;

        public AbsoluteWrapper(Widget child) {
            setWidget(child);
            setStyleName(CLASSNAME + "-wrapper");
        }

        public VCaption getCaption() {
            return caption;
        }

        public void setCaption(VCaption caption) {
            this.caption = caption;
        }

        public void destroy() {
            if (caption != null) {
                caption.removeFromParent();
            }
            removeFromParent();
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

}

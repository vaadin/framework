package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.BrowserInfo;
import com.itmill.toolkit.terminal.gwt.client.Container;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.RenderSpace;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IAbsoluteLayout extends ComplexPanel implements Container {

    /** Tag name for widget creation */
    public static final String TAGNAME = "absolutelayout";

    /** Class name, prefix in styling */
    public static final String CLASSNAME = "i-absolutelayout";

    private DivElement marginElement;

    protected final Element canvas = DOM.createDiv();

    private int excessPixelsHorizontal;

    private int excessPixelsVertical;

    private Object previousStyleName;

    private Map<String, AbsoluteWrapper> pidToComponentWrappper = new HashMap<String, AbsoluteWrapper>();

    protected ApplicationConnection client;

    private boolean rendering;

    public IAbsoluteLayout() {
        setElement(Document.get().createDivElement());
        setStyleName(CLASSNAME);
        marginElement = Document.get().createDivElement();
        canvas.getStyle().setProperty("position", "relative");
        marginElement.appendChild(canvas);
        getElement().appendChild(marginElement);
    }

    public RenderSpace getAllocatedSpace(Widget child) {
        // TODO needs some special handling for components with only on edge
        // horizontally or vertically defined
        AbsoluteWrapper wrapper = (AbsoluteWrapper) child.getParent();
        int w;
        if (wrapper.left != null && wrapper.right != null) {
            w = wrapper.getOffsetWidth();
        } else if (wrapper.right != null) {
            // left == null
            // available width == right edge == offsetleft + width
            w = wrapper.getOffsetWidth() + wrapper.getElement().getOffsetLeft();
        } else {
            // left != null && right == null || left == null &&
            // right == null
            // available width == canvas width - offset left
            w = canvas.getOffsetWidth() - wrapper.getElement().getOffsetLeft();
        }
        int h;
        if (wrapper.top != null && wrapper.bottom != null) {
            h = wrapper.getOffsetHeight();
        } else if (wrapper.bottom != null) {
            // top not defined, available space 0... bottom of wrapper
            h = wrapper.getElement().getOffsetTop() + wrapper.getOffsetHeight();
        } else {
            // top defined or both undefined, available space == canvas - top
            h = canvas.getOffsetHeight() - wrapper.getElement().getOffsetTop();
        }

        return new RenderSpace(w, h);
    }

    public boolean hasChildComponent(Widget component) {
        for (Iterator<Entry<String, AbsoluteWrapper>> iterator = pidToComponentWrappper
                .entrySet().iterator(); iterator.hasNext();) {
            if (iterator.next().getValue().paintable == component) {
                return true;
            }
        }
        return false;
    }

    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        for (Widget wrapper : getChildren()) {
            AbsoluteWrapper w = (AbsoluteWrapper) wrapper;
            if (w.getWidget() == oldComponent) {
                w.setWidget(newComponent);
                return;
            }
        }
    }

    public boolean requestLayout(Set<Paintable> children) {
        // component inside an absolute panel never affects parent nor the
        // layout
        return true;
    }

    public void updateCaption(Paintable component, UIDL uidl) {
        // TODO Auto-generated method stub

    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        rendering = true;
        this.client = client;
        // TODO margin handling
        if (client.updateComponent(this, uidl, true)) {
            rendering = false;
            return;
        }

        HashSet<String> unrenderedPids = new HashSet<String>(
                pidToComponentWrappper.keySet());

        for (Iterator<UIDL> childIterator = uidl.getChildIterator(); childIterator
                .hasNext();) {
            UIDL cc = childIterator.next();
            UIDL componentUIDL = cc.getChildUIDL(0);
            unrenderedPids.remove(componentUIDL.getId());
            getWrapper(client, componentUIDL).updateFromUIDL(cc);
        }

        for (String pid : unrenderedPids) {
            AbsoluteWrapper absoluteWrapper = pidToComponentWrappper.get(pid);
            pidToComponentWrappper.remove(pid);
            absoluteWrapper.destroy();
        }
        rendering = false;
    }

    private AbsoluteWrapper getWrapper(ApplicationConnection client,
            UIDL componentUIDL) {
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
    public void setStyleName(String style) {
        super.setStyleName(style);
        if (previousStyleName == null || !previousStyleName.equals(style)) {
            excessPixelsHorizontal = -1;
            excessPixelsVertical = -1;
        }
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        // TODO do this so that canvas gets the sized properly (the area
        // inside marginals)
        canvas.getStyle().setProperty("width", width);

        if (!rendering && BrowserInfo.get().isIE6()) {
            relayoutWrappersForIe6();
        }
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        // TODO do this so that canvas gets the sized properly (the area
        // inside marginals)
        canvas.getStyle().setProperty("height", height);

        if (!rendering && BrowserInfo.get().isIE6()) {
            relayoutWrappersForIe6();
        }
    }

    private void relayoutWrappersForIe6() {
        for (Widget wrapper : getChildren()) {
            ((AbsoluteWrapper) wrapper).ie6Layout();
        }
    }

    public class AbsoluteWrapper extends SimplePanel {
        private String css;
        private String left;
        private String top;
        private String right;
        private String bottom;
        private String zIndex;

        private Paintable paintable;

        public AbsoluteWrapper(Paintable paintable) {
            this.paintable = paintable;
            setStyleName(CLASSNAME + "-wrapper");
        }

        public void destroy() {
            client.unregisterPaintable(paintable);
            removeFromParent();
        }

        public void updateFromUIDL(UIDL componentUIDL) {
            setPosition(componentUIDL.getStringAttribute("css"));
            if (getWidget() != paintable) {
                setWidget((Widget) paintable);
            }
            paintable.updateFromUIDL(componentUIDL.getChildUIDL(0), client);
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
                style.setProperty("zIndex", zIndex);
                style.setProperty("top", top);
                style.setProperty("left", left);
                style.setProperty("right", right);
                style.setProperty("bottom", bottom);

                if (BrowserInfo.get().isIE6()) {
                    ie6Layout();
                }
            }

        }

        private void ie6Layout() {
            // special handling for IE6 is needed, it does not support
            // setting both left/right or top/bottom
            Style style = getElement().getStyle();
            if (bottom != null && top != null) {
                // define height for wrapper to simulate bottom property
                int bottompixels = measureForIE6(bottom);
                ApplicationConnection.getConsole().log("ALB" + bottompixels);
                int height = canvas.getOffsetHeight() - bottompixels
                        - getElement().getOffsetTop();
                ApplicationConnection.getConsole().log("ALB" + height);
                if (height < 0) {
                    height = 0;
                }
                style.setPropertyPx("height", height);
            } else {
                // reset possibly existing value
                style.setProperty("height", "");
            }
            if (left != null && right != null) {
                // define width for wrapper to simulate right property
                int rightPixels = measureForIE6(right);
                ApplicationConnection.getConsole().log("ALR" + rightPixels);
                int width = canvas.getOffsetWidth() - rightPixels
                        - getElement().getOffsetWidth();
                ApplicationConnection.getConsole().log("ALR" + width);
                if (width < 0) {
                    width = 0;
                }
                style.setPropertyPx("width", width);
            } else {
                // reset possibly existing value
                style.setProperty("width", "");
            }
        }

    }

    private Element measureElement;

    private int measureForIE6(String cssLength) {
        if (measureElement == null) {
            measureElement = DOM.createDiv();
            measureElement.getStyle().setProperty("position", "absolute");
            canvas.appendChild(measureElement);
        }
        measureElement.getStyle().setProperty("width", cssLength);
        return measureElement.getOffsetWidth();
    }

}

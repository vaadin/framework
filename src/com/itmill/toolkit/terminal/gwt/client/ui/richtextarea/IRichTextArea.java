/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui.richtextarea;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;
import com.itmill.toolkit.terminal.gwt.client.ui.Field;

/**
 * This class represents a basic text input field with one row.
 * 
 * @author IT Mill Ltd.
 * 
 */
public class IRichTextArea extends Composite implements Paintable, Field,
        ChangeListener, FocusListener {

    /**
     * The input node CSS classname.
     */
    public static final String CLASSNAME = "i-richtextarea";

    protected String id;

    protected ApplicationConnection client;

    private boolean immediate = false;

    private RichTextArea rta = new RichTextArea();

    private IRichTextToolbar formatter = new IRichTextToolbar(rta);

    private HTML html = new HTML();

    private final FlowPanel fp = new FlowPanel();

    private boolean enabled = true;

    private int extraHorizontalPixels = -1;
    private int extraVerticalPixels = -1;

    public IRichTextArea() {
        fp.add(formatter);

        rta.setWidth("100%");
        rta.addFocusListener(this);

        fp.add(rta);

        initWidget(fp);
        setStyleName(CLASSNAME);

    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            rta.setEnabled(enabled);
            if (enabled) {
                fp.remove(html);
                fp.add(rta);
            } else {
                html.setHTML(rta.getHTML());
                fp.remove(rta);
                fp.add(html);
            }

            this.enabled = enabled;
        }
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        this.client = client;
        id = uidl.getId();

        if (uidl.hasVariable("text")) {
            rta.setHTML(uidl.getStringVariable("text"));
        }
        setEnabled(!uidl.getBooleanAttribute("disabled"));

        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        immediate = uidl.getBooleanAttribute("immediate");

    }

    public void onChange(Widget sender) {
        if (client != null && id != null) {
            client.updateVariable(id, "text", rta.getText(), immediate);
        }
    }

    public void onFocus(Widget sender) {

    }

    public void onLostFocus(Widget sender) {
        final String html = rta.getHTML();
        client.updateVariable(id, "text", html, immediate);

    }

    /**
     * @return space used by components paddings and borders
     */
    private int getExtraHorizontalPixels() {
        if (extraHorizontalPixels < 0) {
            detectExtraSizes();
        }
        return extraHorizontalPixels;
    }

    /**
     * @return space used by components paddings and borders
     */
    private int getExtraVerticalPixels() {
        if (extraVerticalPixels < 0) {
            detectExtraSizes();
        }
        return extraVerticalPixels;
    }

    /**
     * Detects space used by components paddings and borders. 
     */
    private void detectExtraSizes() {
        Element clone = Util.cloneNode(getElement(), false);
        DOM.setElementAttribute(clone, "id", "");
        DOM.setStyleAttribute(clone, "visibility", "hidden");
        DOM.setStyleAttribute(clone, "position", "absolute");
        // due FF3 bug set size to 10px and later subtract it from extra pixels
        DOM.setStyleAttribute(clone, "width", "10px");
        DOM.setStyleAttribute(clone, "height", "10px");
        DOM.appendChild(DOM.getParent(getElement()), clone);
        extraHorizontalPixels = DOM.getElementPropertyInt(clone, "offsetWidth") - 10;
        extraVerticalPixels = DOM.getElementPropertyInt(clone, "offsetHeight") - 10;

        DOM.removeChild(DOM.getParent(getElement()), clone);
    }

    @Override
    public void setHeight(String height) {
        if (height.endsWith("px")) {
            int h = Integer.parseInt(height.substring(0, height.length() - 2));
            h -= getExtraVerticalPixels();
            if (h < 0) {
                h = 0;
            }

            super.setHeight(h + "px");
            int editorHeight = h - formatter.getOffsetHeight();
            rta.setHeight(editorHeight + "px");
        } else {
            super.setHeight(height);
            rta.setHeight("");
        }
    }

    @Override
    public void setWidth(String width) {
        if (width.endsWith("px")) {
            int w = Integer.parseInt(width.substring(0, width.length() - 2));
            w -= getExtraHorizontalPixels();
            if (w < 0) {
                w = 0;
            }

            super.setWidth(w + "px");
        } else {
            super.setWidth(width);
        }
    }

}

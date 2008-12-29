/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui.richtextarea;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.BrowserInfo;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;
import com.itmill.toolkit.terminal.gwt.client.ui.Field;

/**
 * This class implements a basic client side rich text editor component.
 * 
 * @author IT Mill Ltd.
 * 
 */
public class IRichTextArea extends Composite implements Paintable, Field,
        ChangeListener, FocusListener, KeyboardListener {

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

    private int maxLength = -1;

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

    public void updateFromUIDL(final UIDL uidl, ApplicationConnection client) {
        this.client = client;
        id = uidl.getId();

        if (uidl.hasVariable("text")) {
            if (BrowserInfo.get().isIE()) {
                // rta is rather buggy in IE (as pretty much everything is)
                // it needs some "shaking" not to fall into uneditable state
                // see #2374
                rta.getBasicFormatter().toggleBold();
                rta.getBasicFormatter().toggleBold();
            }
            rta.setHTML(uidl.getStringVariable("text"));

        }
        setEnabled(!uidl.getBooleanAttribute("disabled"));

        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        immediate = uidl.getBooleanAttribute("immediate");
        int newMaxLength = uidl.hasAttribute("maxLength") ? uidl
                .getIntAttribute("maxLength") : -1;
        if (newMaxLength >= 0) {
            if (maxLength == -1) {
                rta.addKeyboardListener(this);
            }
            maxLength = newMaxLength;
        } else if (maxLength != -1) {
            getElement().setAttribute("maxlength", "");
            maxLength = -1;
            rta.removeKeyboardListener(this);
        }
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
        } else {
            super.setHeight(height);
        }

        if (height == null || height.equals("")) {
            rta.setHeight("");
        } else {
            int editorHeight = getOffsetHeight() - getExtraVerticalPixels()
                    - formatter.getOffsetHeight();
            rta.setHeight(editorHeight + "px");
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
        } else if (width.equals("") && BrowserInfo.get().isIE()) {
            /*
             * IE cannot calculate the width of the 100% iframe correctly if
             * there is no width specified for the parent so we use the width of
             * the toolbar.
             */
            super.setWidth(formatter.getOffsetWidth() + "px");
        } else {
            super.setWidth(width);
        }
    }

    public void onKeyDown(Widget sender, char keyCode, int modifiers) {
        // NOP
    }

    public void onKeyPress(Widget sender, char keyCode, int modifiers) {
        if (maxLength >= 0) {
            DeferredCommand.addCommand(new Command() {
                public void execute() {
                    if (rta.getHTML().length() > maxLength) {
                        rta.setHTML(rta.getHTML().substring(0, maxLength));
                    }
                }
            });
        }
    }

    public void onKeyUp(Widget sender, char keyCode, int modifiers) {
        // NOP
    }

}

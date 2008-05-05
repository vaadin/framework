/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui.richtextarea;

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

/**
 * This class represents a basic text input field with one row.
 * 
 * @author IT Mill Ltd.
 * 
 */
public class IRichTextArea extends Composite implements Paintable,
        ChangeListener, FocusListener {

    /**
     * The input node CSS classname.
     */
    public static final String CLASSNAME = "i-richtextarea";

    protected String id;

    protected ApplicationConnection client;

    private boolean immediate = false;

    private RichTextArea rta = new RichTextArea();

    private RichTextToolbar formatter = new RichTextToolbar(rta);

    private HTML html = new HTML();

    private final FlowPanel fp = new FlowPanel();

    private boolean enabled = true;

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

}

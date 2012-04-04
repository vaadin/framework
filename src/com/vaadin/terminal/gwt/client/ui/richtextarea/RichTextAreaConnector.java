/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.richtextarea;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.AbstractFieldConnector;
import com.vaadin.terminal.gwt.client.ui.Component;
import com.vaadin.terminal.gwt.client.ui.Component.LoadStyle;
import com.vaadin.terminal.gwt.client.ui.ShortcutActionHandler.BeforeShortcutActionListener;
import com.vaadin.ui.RichTextArea;

@Component(value = RichTextArea.class, loadStyle = LoadStyle.LAZY)
public class RichTextAreaConnector extends AbstractFieldConnector implements
        BeforeShortcutActionListener {

    @Override
    public void updateFromUIDL(final UIDL uidl, ApplicationConnection client) {
        getWidget().client = client;
        getWidget().id = uidl.getId();

        if (uidl.hasVariable("text")) {
            getWidget().currentValue = uidl.getStringVariable("text");
            if (getWidget().rta.isAttached()) {
                getWidget().rta.setHTML(getWidget().currentValue);
            } else {
                getWidget().html.setHTML(getWidget().currentValue);
            }
        }
        if (isRealUpdate(uidl)) {
            getWidget().setEnabled(isEnabled());
        }

        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }

        getWidget().setReadOnly(isReadOnly());
        getWidget().immediate = getState().isImmediate();
        int newMaxLength = uidl.hasAttribute("maxLength") ? uidl
                .getIntAttribute("maxLength") : -1;
        if (newMaxLength >= 0) {
            if (getWidget().maxLength == -1) {
                getWidget().keyPressHandler = getWidget().rta
                        .addKeyPressHandler(getWidget());
            }
            getWidget().maxLength = newMaxLength;
        } else if (getWidget().maxLength != -1) {
            getWidget().getElement().setAttribute("maxlength", "");
            getWidget().maxLength = -1;
            getWidget().keyPressHandler.removeHandler();
        }

        if (uidl.hasAttribute("selectAll")) {
            getWidget().selectAll();
        }

    }

    public void onBeforeShortcutAction(Event e) {
        getWidget().synchronizeContentToServer();
    }

    @Override
    public VRichTextArea getWidget() {
        return (VRichTextArea) super.getWidget();
    };

    @Override
    protected Widget createWidget() {
        return GWT.create(VRichTextArea.class);
    }

}

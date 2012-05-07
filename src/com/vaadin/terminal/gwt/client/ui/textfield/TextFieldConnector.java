/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui.textfield;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.AbstractFieldConnector;
import com.vaadin.terminal.gwt.client.ui.Connect;
import com.vaadin.terminal.gwt.client.ui.Connect.LoadStyle;
import com.vaadin.terminal.gwt.client.ui.ShortcutActionHandler.BeforeShortcutActionListener;
import com.vaadin.ui.TextField;

@Connect(value = TextField.class, loadStyle = LoadStyle.EAGER)
public class TextFieldConnector extends AbstractFieldConnector implements
        Paintable, BeforeShortcutActionListener {

    @Override
    public AbstractTextFieldState getState() {
        return (AbstractTextFieldState) super.getState();
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // Save details
        getWidget().client = client;
        getWidget().paintableId = uidl.getId();

        if (!isRealUpdate(uidl)) {
            return;
        }

        getWidget().setReadOnly(isReadOnly());

        getWidget().setInputPrompt(getState().getInputPrompt());
        getWidget().setMaxLength(getState().getMaxLength());
        getWidget().setImmediate(getState().isImmediate());

        getWidget().listenTextChangeEvents = hasEventListener("ie");
        if (getWidget().listenTextChangeEvents) {
            getWidget().textChangeEventMode = uidl
                    .getStringAttribute(VTextField.ATTR_TEXTCHANGE_EVENTMODE);
            if (getWidget().textChangeEventMode
                    .equals(VTextField.TEXTCHANGE_MODE_EAGER)) {
                getWidget().textChangeEventTimeout = 1;
            } else {
                getWidget().textChangeEventTimeout = uidl
                        .getIntAttribute(VTextField.ATTR_TEXTCHANGE_TIMEOUT);
                if (getWidget().textChangeEventTimeout < 1) {
                    // Sanitize and allow lazy/timeout with timeout set to 0 to
                    // work as eager
                    getWidget().textChangeEventTimeout = 1;
                }
            }
            getWidget().sinkEvents(VTextField.TEXTCHANGE_EVENTS);
            getWidget().attachCutEventListener(getWidget().getElement());
        }
        getWidget().setColumns(getState().getColumns());

        final String text = getState().getText();

        /*
         * We skip the text content update if field has been repainted, but text
         * has not been changed. Additional sanity check verifies there is no
         * change in the que (in which case we count more on the server side
         * value).
         */
        if (!(uidl
                .getBooleanAttribute(VTextField.ATTR_NO_VALUE_CHANGE_BETWEEN_PAINTS)
                && getWidget().valueBeforeEdit != null && text
                    .equals(getWidget().valueBeforeEdit))) {
            getWidget().updateFieldContent(text);
        }

        if (uidl.hasAttribute("selpos")) {
            final int pos = uidl.getIntAttribute("selpos");
            final int length = uidl.getIntAttribute("sellen");
            /*
             * Gecko defers setting the text so we need to defer the selection.
             */
            Scheduler.get().scheduleDeferred(new Command() {
                public void execute() {
                    getWidget().setSelectionRange(pos, length);
                }
            });
        }
    }

    @Override
    public VTextField getWidget() {
        return (VTextField) super.getWidget();
    }

    public void onBeforeShortcutAction(Event e) {
        getWidget().valueChange(false);
    }

}

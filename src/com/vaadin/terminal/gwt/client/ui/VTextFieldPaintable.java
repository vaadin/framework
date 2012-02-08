/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.ShortcutActionHandler.BeforeShortcutActionListener;

public class VTextFieldPaintable extends VAbstractPaintableWidget implements
        BeforeShortcutActionListener {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // Save details
        getWidgetForPaintable().client = client;
        getWidgetForPaintable().paintableId = uidl.getId();

        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }

        if (uidl.getBooleanAttribute("readonly")) {
            getWidgetForPaintable().setReadOnly(true);
        } else {
            getWidgetForPaintable().setReadOnly(false);
        }

        getWidgetForPaintable().inputPrompt = uidl
                .getStringAttribute(VTextField.ATTR_INPUTPROMPT);

        getWidgetForPaintable().setMaxLength(
                uidl.hasAttribute("maxLength") ? uidl
                        .getIntAttribute("maxLength") : -1);

        getWidgetForPaintable().immediate = uidl
                .getBooleanAttribute("immediate");

        getWidgetForPaintable().listenTextChangeEvents = client
                .hasEventListeners(this, "ie");
        if (getWidgetForPaintable().listenTextChangeEvents) {
            getWidgetForPaintable().textChangeEventMode = uidl
                    .getStringAttribute(VTextField.ATTR_TEXTCHANGE_EVENTMODE);
            if (getWidgetForPaintable().textChangeEventMode
                    .equals(VTextField.TEXTCHANGE_MODE_EAGER)) {
                getWidgetForPaintable().textChangeEventTimeout = 1;
            } else {
                getWidgetForPaintable().textChangeEventTimeout = uidl
                        .getIntAttribute(VTextField.ATTR_TEXTCHANGE_TIMEOUT);
                if (getWidgetForPaintable().textChangeEventTimeout < 1) {
                    // Sanitize and allow lazy/timeout with timeout set to 0 to
                    // work as eager
                    getWidgetForPaintable().textChangeEventTimeout = 1;
                }
            }
            getWidgetForPaintable().sinkEvents(VTextField.TEXTCHANGE_EVENTS);
            getWidgetForPaintable().attachCutEventListener(
                    getWidgetForPaintable().getElement());
        }

        if (uidl.hasAttribute("cols")) {
            getWidgetForPaintable().setColumns(
                    new Integer(uidl.getStringAttribute("cols")).intValue());
        }

        final String text = uidl.getStringVariable("text");

        /*
         * We skip the text content update if field has been repainted, but text
         * has not been changed. Additional sanity check verifies there is no
         * change in the que (in which case we count more on the server side
         * value).
         */
        if (!(uidl
                .getBooleanAttribute(VTextField.ATTR_NO_VALUE_CHANGE_BETWEEN_PAINTS)
                && getWidgetForPaintable().valueBeforeEdit != null && text
                    .equals(getWidgetForPaintable().valueBeforeEdit))) {
            getWidgetForPaintable().updateFieldContent(text);
        }

        if (uidl.hasAttribute("selpos")) {
            final int pos = uidl.getIntAttribute("selpos");
            final int length = uidl.getIntAttribute("sellen");
            /*
             * Gecko defers setting the text so we need to defer the selection.
             */
            Scheduler.get().scheduleDeferred(new Command() {
                public void execute() {
                    getWidgetForPaintable().setSelectionRange(pos, length);
                }
            });
        }

        // Here for backward compatibility; to be moved to TextArea.
        // Optimization: server does not send attribute for the default 'true'
        // state.
        if (uidl.hasAttribute("wordwrap")
                && uidl.getBooleanAttribute("wordwrap") == false) {
            getWidgetForPaintable().setWordwrap(false);
        } else {
            getWidgetForPaintable().setWordwrap(true);
        }
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VTextField.class);
    }

    @Override
    public VTextField getWidgetForPaintable() {
        return (VTextField) super.getWidgetForPaintable();
    }

    public void onBeforeShortcutAction(Event e) {
        getWidgetForPaintable().valueChange(false);
    }

}

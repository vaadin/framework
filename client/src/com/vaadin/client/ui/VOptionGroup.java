/*
 * Copyright 2011 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.client.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.UIDL;
import com.vaadin.client.Util;
import com.vaadin.shared.EventId;
import com.vaadin.shared.ui.optiongroup.OptionGroupConstants;

public class VOptionGroup extends VOptionGroupBase implements FocusHandler,
        BlurHandler {

    public static final String CLASSNAME = "v-select-optiongroup";

    /** For internal use only. May be removed or replaced in the future. */
    public final Panel panel;

    private final Map<CheckBox, String> optionsToKeys;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean sendFocusEvents = false;
    /** For internal use only. May be removed or replaced in the future. */
    public boolean sendBlurEvents = false;
    /** For internal use only. May be removed or replaced in the future. */
    public List<HandlerRegistration> focusHandlers = null;
    /** For internal use only. May be removed or replaced in the future. */
    public List<HandlerRegistration> blurHandlers = null;

    private final LoadHandler iconLoadHandler = new LoadHandler() {
        @Override
        public void onLoad(LoadEvent event) {
            Util.notifyParentOfSizeChange(VOptionGroup.this, true);
        }
    };

    /**
     * used to check whether a blur really was a blur of the complete
     * optiongroup: if a control inside this optiongroup gains focus right after
     * blur of another control inside this optiongroup (meaning: if onFocus
     * fires after onBlur has fired), the blur and focus won't be sent to the
     * server side as only a focus change inside this optiongroup occured
     */
    private boolean blurOccured = false;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean htmlContentAllowed = false;

    public VOptionGroup() {
        super(CLASSNAME);
        panel = (Panel) optionsContainer;
        optionsToKeys = new HashMap<CheckBox, String>();
    }

    /*
     * Return true if no elements were changed, false otherwise.
     */
    @Override
    public void buildOptions(UIDL uidl) {
        panel.clear();
        for (final Iterator<?> it = uidl.getChildIterator(); it.hasNext();) {
            final UIDL opUidl = (UIDL) it.next();
            CheckBox op;

            String itemHtml = opUidl.getStringAttribute("caption");
            if (!htmlContentAllowed) {
                itemHtml = Util.escapeHTML(itemHtml);
            }

            String icon = opUidl.getStringAttribute("icon");
            if (icon != null && icon.length() != 0) {
                String iconUrl = client.translateVaadinUri(icon);
                itemHtml = "<img src=\"" + iconUrl + "\" class=\""
                        + Icon.CLASSNAME + "\" alt=\"\" />" + itemHtml;
            }

            if (isMultiselect()) {
                op = new VCheckBox();
                op.setHTML(itemHtml);
            } else {
                op = new RadioButton(paintableId, itemHtml, true);
                op.setStyleName("v-radiobutton");
            }

            if (icon != null && icon.length() != 0) {
                Util.sinkOnloadForImages(op.getElement());
                op.addHandler(iconLoadHandler, LoadEvent.getType());
            }

            op.addStyleName(CLASSNAME_OPTION);
            op.setValue(opUidl.getBooleanAttribute("selected"));
            boolean enabled = !opUidl
                    .getBooleanAttribute(OptionGroupConstants.ATTRIBUTE_OPTION_DISABLED)
                    && !isReadonly() && !isDisabled();
            op.setEnabled(enabled);
            setStyleName(op.getElement(),
                    ApplicationConnection.DISABLED_CLASSNAME, !enabled);
            op.addClickHandler(this);
            optionsToKeys.put(op, opUidl.getStringAttribute("key"));
            panel.add(op);
        }
    }

    @Override
    protected String[] getSelectedItems() {
        return selectedKeys.toArray(new String[selectedKeys.size()]);
    }

    @Override
    public void onClick(ClickEvent event) {
        super.onClick(event);
        if (event.getSource() instanceof CheckBox) {
            final boolean selected = ((CheckBox) event.getSource()).getValue();
            final String key = optionsToKeys.get(event.getSource());
            if (!isMultiselect()) {
                selectedKeys.clear();
            }
            if (selected) {
                selectedKeys.add(key);
            } else {
                selectedKeys.remove(key);
            }
            client.updateVariable(paintableId, "selected", getSelectedItems(),
                    isImmediate());
        }
    }

    @Override
    public void setTabIndex(int tabIndex) {
        for (Iterator<Widget> iterator = panel.iterator(); iterator.hasNext();) {
            FocusWidget widget = (FocusWidget) iterator.next();
            widget.setTabIndex(tabIndex);
        }
    }

    @Override
    public void focus() {
        Iterator<Widget> iterator = panel.iterator();
        if (iterator.hasNext()) {
            ((Focusable) iterator.next()).setFocus(true);
        }
    }

    @Override
    public void onFocus(FocusEvent arg0) {
        if (!blurOccured) {
            // no blur occured before this focus event
            // panel was blurred => fire the event to the server side if
            // requested by server side
            if (sendFocusEvents) {
                client.updateVariable(paintableId, EventId.FOCUS, "", true);
            }
        } else {
            // blur occured before this focus event
            // another control inside the panel (checkbox / radio box) was
            // blurred => do not fire the focus and set blurOccured to false, so
            // blur will not be fired, too
            blurOccured = false;
        }
    }

    @Override
    public void onBlur(BlurEvent arg0) {
        blurOccured = true;
        if (sendBlurEvents) {
            Scheduler.get().scheduleDeferred(new Command() {
                @Override
                public void execute() {
                    // check whether blurOccured still is true and then send the
                    // event out to the server
                    if (blurOccured) {
                        client.updateVariable(paintableId, EventId.BLUR, "",
                                true);
                        blurOccured = false;
                    }
                }
            });
        }
    }
}

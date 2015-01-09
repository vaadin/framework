/*
 * Copyright 2000-2014 Vaadin Ltd.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.aria.client.Roles;
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
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.StyleConstants;
import com.vaadin.client.UIDL;
import com.vaadin.client.Util;
import com.vaadin.client.WidgetUtil;
import com.vaadin.shared.EventId;
import com.vaadin.shared.ui.optiongroup.OptionGroupConstants;

public class VOptionGroup extends VOptionGroupBase implements FocusHandler,
        BlurHandler {

    public static final String CLASSNAME = "v-select-optiongroup";

    /** For internal use only. May be removed or replaced in the future. */
    public final Panel panel;

    private final Map<CheckBox, String> optionsToKeys;

    private final Map<CheckBox, Boolean> optionsEnabled;

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

    private boolean wasHtmlContentAllowed = false;
    private boolean wasMultiselect = false;

    public VOptionGroup() {
        super(CLASSNAME);
        panel = (Panel) optionsContainer;
        optionsToKeys = new HashMap<CheckBox, String>();
        optionsEnabled = new HashMap<CheckBox, Boolean>();

        wasMultiselect = isMultiselect();
    }

    /*
     * Try to update content of existing elements, rebuild panel entirely
     * otherwise
     */
    @Override
    public void buildOptions(UIDL uidl) {
        /*
         * In order to retain focus, we need to update values rather than
         * recreate panel from scratch (#10451). However, the panel will be
         * rebuilt (losing focus) if number of elements or their order is
         * changed.
         */
        HashMap<String, CheckBox> keysToOptions = new HashMap<String, CheckBox>();
        for (Map.Entry<CheckBox, String> entry : optionsToKeys.entrySet()) {
            keysToOptions.put(entry.getValue(), entry.getKey());
        }
        ArrayList<Widget> existingwidgets = new ArrayList<Widget>();
        ArrayList<Widget> newwidgets = new ArrayList<Widget>();

        // Get current order of elements
        for (Widget wid : panel) {
            existingwidgets.add(wid);
        }

        optionsEnabled.clear();

        if (isMultiselect()) {
            Roles.getGroupRole().set(getElement());
        } else {
            Roles.getRadiogroupRole().set(getElement());
        }

        for (final Iterator<?> it = uidl.getChildIterator(); it.hasNext();) {
            final UIDL opUidl = (UIDL) it.next();

            String itemHtml = opUidl.getStringAttribute("caption");
            if (!htmlContentAllowed) {
                itemHtml = WidgetUtil.escapeHTML(itemHtml);
            }

            String iconUrl = opUidl.getStringAttribute("icon");
            if (iconUrl != null && iconUrl.length() != 0) {
                Icon icon = client.getIcon(iconUrl);
                itemHtml = icon.getElement().getString() + itemHtml;
            }

            String key = opUidl.getStringAttribute("key");
            CheckBox op = keysToOptions.get(key);

            // Need to recreate object if isMultiselect is changed (#10451)
            // OR if htmlContentAllowed changed due to Safari 5 issue
            if ((op == null) || (htmlContentAllowed != wasHtmlContentAllowed)
                    || (isMultiselect() != wasMultiselect)) {
                // Create a new element
                if (isMultiselect()) {
                    op = new VCheckBox();
                } else {
                    op = new RadioButton(paintableId);
                    op.setStyleName("v-radiobutton");
                }
                if (iconUrl != null && iconUrl.length() != 0) {
                    WidgetUtil.sinkOnloadForImages(op.getElement());
                    op.addHandler(iconLoadHandler, LoadEvent.getType());
                }

                op.addStyleName(CLASSNAME_OPTION);
                op.addClickHandler(this);

                optionsToKeys.put(op, key);
            }

            op.setHTML(itemHtml);
            op.setValue(opUidl.getBooleanAttribute("selected"));
            boolean optionEnabled = !opUidl
                    .getBooleanAttribute(OptionGroupConstants.ATTRIBUTE_OPTION_DISABLED);
            boolean enabled = optionEnabled && !isReadonly() && isEnabled();
            op.setEnabled(enabled);
            optionsEnabled.put(op, optionEnabled);

            setStyleName(op.getElement(), StyleConstants.DISABLED,
                    !(optionEnabled && isEnabled()));

            newwidgets.add(op);
        }

        if (!newwidgets.equals(existingwidgets)) {
            // Rebuild the panel, losing focus
            panel.clear();
            for (Widget wid : newwidgets) {
                panel.add(wid);
            }
        }

        wasHtmlContentAllowed = htmlContentAllowed;
        wasMultiselect = isMultiselect();
    }

    @Override
    protected String[] getSelectedItems() {
        return selectedKeys.toArray(new String[selectedKeys.size()]);
    }

    @Override
    public void onClick(ClickEvent event) {
        super.onClick(event);
        if (event.getSource() instanceof CheckBox) {
            CheckBox source = (CheckBox) event.getSource();

            if (BrowserInfo.get().isWebkit()) {
                // Webkit does not focus non-text input elements on click
                // (#11854)
                source.setFocus(true);
            }

            final boolean selected = source.getValue();
            final String key = optionsToKeys.get(source);
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
    protected void updateEnabledState() {
        boolean optionGroupEnabled = isEnabled() && !isReadonly();
        // sets options enabled according to the widget's enabled,
        // readonly and each options own enabled
        for (Widget w : panel) {
            if (w instanceof HasEnabled) {
                HasEnabled hasEnabled = (HasEnabled) w;
                Boolean isOptionEnabled = optionsEnabled.get(w);
                if (isOptionEnabled == null) {
                    hasEnabled.setEnabled(optionGroupEnabled);
                    setStyleName(w.getElement(), StyleConstants.DISABLED,
                            !isEnabled());
                } else {
                    hasEnabled
                            .setEnabled(isOptionEnabled && optionGroupEnabled);
                    setStyleName(w.getElement(), StyleConstants.DISABLED,
                            !(isOptionEnabled && isEnabled()));
                }
            }
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

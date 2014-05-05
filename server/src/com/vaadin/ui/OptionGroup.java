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

package com.vaadin.ui;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.vaadin.data.Container;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.shared.ui.optiongroup.OptionGroupConstants;

/**
 * Configures select to be used as an option group.
 */
@SuppressWarnings("serial")
public class OptionGroup extends AbstractSelect implements
        FieldEvents.BlurNotifier, FieldEvents.FocusNotifier {

    private Set<Object> disabledItemIds = new HashSet<Object>();
    private boolean htmlContentAllowed = false;

    public OptionGroup() {
        super();
    }

    public OptionGroup(String caption, Collection<?> options) {
        super(caption, options);
    }

    public OptionGroup(String caption, Container dataSource) {
        super(caption, dataSource);
    }

    public OptionGroup(String caption) {
        super(caption);
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        target.addAttribute("type", "optiongroup");
        if (isHtmlContentAllowed()) {
            target.addAttribute(OptionGroupConstants.HTML_CONTENT_ALLOWED, true);
        }
        super.paintContent(target);
    }

    @Override
    protected void paintItem(PaintTarget target, Object itemId)
            throws PaintException {
        super.paintItem(target, itemId);
        if (!isItemEnabled(itemId)) {
            target.addAttribute(OptionGroupConstants.ATTRIBUTE_OPTION_DISABLED,
                    true);
        }
    }

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        super.changeVariables(source, variables);

        if (variables.containsKey(FocusEvent.EVENT_ID)) {
            fireEvent(new FocusEvent(this));
        }
        if (variables.containsKey(BlurEvent.EVENT_ID)) {
            fireEvent(new BlurEvent(this));
        }
    }

    @Override
    public void addBlurListener(BlurListener listener) {
        addListener(BlurEvent.EVENT_ID, BlurEvent.class, listener,
                BlurListener.blurMethod);
    }

    /**
     * @deprecated As of 7.0, replaced by {@link #addBlurListener(BlurListener)}
     **/
    @Override
    @Deprecated
    public void addListener(BlurListener listener) {
        addBlurListener(listener);
    }

    @Override
    public void removeBlurListener(BlurListener listener) {
        removeListener(BlurEvent.EVENT_ID, BlurEvent.class, listener);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeBlurListener(BlurListener)}
     **/
    @Override
    @Deprecated
    public void removeListener(BlurListener listener) {
        removeBlurListener(listener);
    }

    @Override
    public void addFocusListener(FocusListener listener) {
        addListener(FocusEvent.EVENT_ID, FocusEvent.class, listener,
                FocusListener.focusMethod);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addFocusListener(FocusListener)}
     **/
    @Override
    @Deprecated
    public void addListener(FocusListener listener) {
        addFocusListener(listener);
    }

    @Override
    public void removeFocusListener(FocusListener listener) {
        removeListener(FocusEvent.EVENT_ID, FocusEvent.class, listener);

    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeFocusListener(FocusListener)}
     **/
    @Override
    @Deprecated
    public void removeListener(FocusListener listener) {
        removeFocusListener(listener);
    }

    @Override
    protected void setValue(Object newValue, boolean repaintIsNotNeeded) {
        if (repaintIsNotNeeded) {
            /*
             * Check that value from changeVariables() doesn't contain unallowed
             * selections: In the multi select mode, the user has selected or
             * deselected a disabled item. In the single select mode, the user
             * has selected a disabled item.
             */
            if (isMultiSelect()) {
                Set<?> currentValueSet = (Set<?>) getValue();
                Set<?> newValueSet = (Set<?>) newValue;
                for (Object itemId : currentValueSet) {
                    if (!isItemEnabled(itemId) && !newValueSet.contains(itemId)) {
                        markAsDirty();
                        return;
                    }
                }
                for (Object itemId : newValueSet) {
                    if (!isItemEnabled(itemId)
                            && !currentValueSet.contains(itemId)) {
                        markAsDirty();
                        return;
                    }
                }
            } else {
                if (newValue == null) {
                    newValue = getNullSelectionItemId();
                }
                if (!isItemEnabled(newValue)) {
                    markAsDirty();
                    return;
                }
            }
        }
        super.setValue(newValue, repaintIsNotNeeded);
    }

    /**
     * Sets an item disabled or enabled. In the multiselect mode, a disabled
     * item cannot be selected or deselected by the user. In the single
     * selection mode, a disable item cannot be selected.
     * 
     * However, programmatical selection or deselection of an disable item is
     * possible. By default, items are enabled.
     * 
     * @param itemId
     *            the id of the item to be disabled or enabled
     * @param enabled
     *            if true the item is enabled, otherwise the item is disabled
     */
    public void setItemEnabled(Object itemId, boolean enabled) {
        if (itemId != null) {
            if (enabled) {
                disabledItemIds.remove(itemId);
            } else {
                disabledItemIds.add(itemId);
            }
            markAsDirty();
        }
    }

    /**
     * Returns true if the item is enabled.
     * 
     * @param itemId
     *            the id of the item to be checked
     * @return true if the item is enabled, false otherwise
     * @see #setItemEnabled(Object, boolean)
     */
    public boolean isItemEnabled(Object itemId) {
        if (itemId != null) {
            return !disabledItemIds.contains(itemId);
        }
        return true;
    }

    /**
     * Sets whether html is allowed in the item captions. If set to true, the
     * captions are passed to the browser as html and the developer is
     * responsible for ensuring no harmful html is used. If set to false, the
     * content is passed to the browser as plain text.
     * 
     * @param htmlContentAllowed
     *            true if the captions are used as html, false if used as plain
     *            text
     */
    public void setHtmlContentAllowed(boolean htmlContentAllowed) {
        this.htmlContentAllowed = htmlContentAllowed;
        markAsDirty();
    }

    /**
     * Checks whether captions are interpreted as html or plain text.
     * 
     * @return true if the captions are used as html, false if used as plain
     *         text
     * @see #setHtmlContentAllowed(boolean)
     */
    public boolean isHtmlContentAllowed() {
        return htmlContentAllowed;
    }
}

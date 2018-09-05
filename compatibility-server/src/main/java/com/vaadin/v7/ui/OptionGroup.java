/*
 * Copyright 2000-2018 Vaadin Ltd.
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

package com.vaadin.v7.ui;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Element;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignFormatter;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.event.FieldEvents;
import com.vaadin.v7.shared.ui.optiongroup.OptionGroupConstants;
import com.vaadin.v7.shared.ui.optiongroup.OptionGroupState;

/**
 * Configures select to be used as an option group.
 *
 * @author Vaadin Ltd
 *
 * @deprecated As of 8.0, use {@link RadioButtonGroup} for single select or
 *             {@link CheckBoxGroup} for multiselect instead
 */
@Deprecated
public class OptionGroup extends AbstractSelect
        implements FieldEvents.BlurNotifier, FieldEvents.FocusNotifier {

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
            target.addAttribute(OptionGroupConstants.HTML_CONTENT_ALLOWED,
                    true);
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

    @Override
    public void removeBlurListener(BlurListener listener) {
        removeListener(BlurEvent.EVENT_ID, BlurEvent.class, listener);
    }

    @Override
    public void addFocusListener(FocusListener listener) {
        addListener(FocusEvent.EVENT_ID, FocusEvent.class, listener,
                FocusListener.focusMethod);
    }

    @Override
    public void removeFocusListener(FocusListener listener) {
        removeListener(FocusEvent.EVENT_ID, FocusEvent.class, listener);

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
                    if (!isItemEnabled(itemId)
                            && !newValueSet.contains(itemId)) {
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

    @Override
    protected Object readItem(Element child, Set<String> selected,
            DesignContext context) {
        Object itemId = super.readItem(child, selected, context);

        if (child.hasAttr("disabled")) {
            setItemEnabled(itemId, false);
        }

        return itemId;
    }

    @Override
    protected Element writeItem(Element design, Object itemId,
            DesignContext context) {
        Element elem = super.writeItem(design, itemId, context);

        if (!isItemEnabled(itemId)) {
            elem.attr("disabled", "");
        }
        if (isHtmlContentAllowed()) {
            // need to unencode HTML entities. AbstractSelect.writeDesign can't
            // check if HTML content is allowed, so it always encodes entities
            // like '>', '<' and '&'; in case HTML content is allowed this is
            // undesirable so we need to unencode entities. Entities other than
            // '<' and '>' will be taken care by Jsoup.
            elem.html(DesignFormatter.decodeFromTextNode(elem.html()));
        }

        return elem;
    }

    @Override
    protected OptionGroupState getState() {
        return (OptionGroupState) super.getState();
    }

    @Override
    protected OptionGroupState getState(boolean markAsDirty) {
        return (OptionGroupState) super.getState(markAsDirty);
    }
}

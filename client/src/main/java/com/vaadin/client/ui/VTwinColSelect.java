/*
 * Copyright 2000-2016 Vaadin Ltd.
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.Focusable;
import com.vaadin.client.StyleConstants;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.connectors.AbstractMultiSelectConnector.MultiSelectWidget;
import com.vaadin.shared.Registration;

import elemental.json.JsonObject;

/**
 * A list builder widget that has two selects; one for selectable options,
 * another for selected options, and buttons for selecting and deselecting the
 * items.
 *
 * @author Vaadin Ltd
 */
public class VTwinColSelect extends Composite implements MultiSelectWidget,
        Field, ClickHandler, Focusable, HasEnabled, KeyDownHandler,
        MouseDownHandler, DoubleClickHandler, SubPartAware {

    private static final String SUBPART_OPTION_SELECT = "leftSelect";
    private static final String SUBPART_OPTION_SELECT_ITEM = SUBPART_OPTION_SELECT
            + "-item";
    private static final String SUBPART_SELECTION_SELECT = "rightSelect";
    private static final String SUBPART_SELECTION_SELECT_ITEM = SUBPART_SELECTION_SELECT
            + "-item";
    private static final String SUBPART_LEFT_CAPTION = "leftCaption";
    private static final String SUBPART_RIGHT_CAPTION = "rightCaption";
    private static final String SUBPART_ADD_BUTTON = "add";
    private static final String SUBPART_REMOVE_BUTTON = "remove";

    /** Primary style name for twin col select. */
    public static final String CLASSNAME = "v-select-twincol";

    private static final int VISIBLE_COUNT = 10;

    private static final int DEFAULT_COLUMN_COUNT = 10;

    private final DoubleClickListBox optionsListBox;

    private final DoubleClickListBox selectionsListBox;

    private final FlowPanel optionsContainer;

    private final FlowPanel captionWrapper;

    private final VButton addItemsLeftToRightButton;

    private final VButton removeItemsRightToLeftButton;

    private final FlowPanel buttons;

    private final Panel panel;

    private HTML optionsCaption = null;

    private HTML selectionsCaption = null;

    private List<BiConsumer<Set<String>, Set<String>>> selectionChangeListeners;

    private boolean enabled;
    private boolean readOnly;

    private int rows = 0;

    /**
     * A multiselect ListBox which catches double clicks.
     */
    public class DoubleClickListBox extends ListBox
            implements HasDoubleClickHandlers {
        /**
         * Constructs a new DoubleClickListBox.
         */
        public DoubleClickListBox() {
            setMultipleSelect(true);
        }

        @Override
        public HandlerRegistration addDoubleClickHandler(
                DoubleClickHandler handler) {
            return addDomHandler(handler, DoubleClickEvent.getType());
        }
    }

    /**
     * Constructs a new VTwinColSelect.
     */
    public VTwinColSelect() {
        selectionChangeListeners = new ArrayList<>();

        optionsContainer = new FlowPanel();
        initWidget(optionsContainer);
        optionsContainer.setStyleName(CLASSNAME);

        captionWrapper = new FlowPanel();

        optionsListBox = new DoubleClickListBox();
        optionsListBox.addClickHandler(this);
        optionsListBox.addDoubleClickHandler(this);
        optionsListBox.setVisibleItemCount(VISIBLE_COUNT);
        optionsListBox.setStyleName(CLASSNAME + "-options");

        selectionsListBox = new DoubleClickListBox();
        selectionsListBox.addClickHandler(this);
        selectionsListBox.addDoubleClickHandler(this);
        selectionsListBox.setVisibleItemCount(VISIBLE_COUNT);
        selectionsListBox.setStyleName(CLASSNAME + "-selections");

        buttons = new FlowPanel();
        buttons.setStyleName(CLASSNAME + "-buttons");
        addItemsLeftToRightButton = new VButton();
        addItemsLeftToRightButton.setText(">>");
        addItemsLeftToRightButton.addClickHandler(this);
        removeItemsRightToLeftButton = new VButton();
        removeItemsRightToLeftButton.setText("<<");
        removeItemsRightToLeftButton.addClickHandler(this);

        panel = optionsContainer;

        panel.add(captionWrapper);
        captionWrapper.getElement().getStyle().setOverflow(Overflow.HIDDEN);
        // Hide until there actually is a caption to prevent IE from rendering
        // extra empty space
        captionWrapper.setVisible(false);

        panel.add(optionsListBox);
        buttons.add(addItemsLeftToRightButton);
        final HTML br = new HTML("<span/>");
        br.setStyleName(CLASSNAME + "-deco");
        buttons.add(br);
        buttons.add(removeItemsRightToLeftButton);
        panel.add(buttons);
        panel.add(selectionsListBox);

        optionsListBox.addKeyDownHandler(this);
        optionsListBox.addMouseDownHandler(this);

        selectionsListBox.addMouseDownHandler(this);
        selectionsListBox.addKeyDownHandler(this);

        updateEnabledState();
    }

    /**
     * Gets the options caption HTML Widget.
     *
     * @return the options caption widget
     */
    protected HTML getOptionsCaption() {
        if (optionsCaption == null) {
            optionsCaption = new HTML();
            optionsCaption.setStyleName(CLASSNAME + "-caption-left");
            optionsCaption.getElement().getStyle()
                    .setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
            captionWrapper.add(optionsCaption);
        }

        return optionsCaption;
    }

    /**
     * Gets the selections caption HTML widget.
     *
     * @return the selections caption widget
     */
    protected HTML getSelectionsCaption() {
        if (selectionsCaption == null) {
            selectionsCaption = new HTML();
            selectionsCaption.setStyleName(CLASSNAME + "-caption-right");
            selectionsCaption.getElement().getStyle()
                    .setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
            captionWrapper.add(selectionsCaption);
        }

        return selectionsCaption;
    }

    /**
     * For internal use only. May be removed or replaced in the future.
     *
     * @return the caption wrapper widget
     */
    public Widget getCaptionWrapper() {
        return captionWrapper;
    }

    /**
     * Sets the number of visible items for the list boxes.
     *
     * @param rows
     *            the number of items to show
     * @see ListBox#setVisibleItemCount(int)
     */
    public void setRows(int rows) {
        if (this.rows != rows) {
            this.rows = rows;
            optionsListBox.setVisibleItemCount(rows);
            selectionsListBox.setVisibleItemCount(rows);
        }
    }

    /**
     * Returns the number of visible items for the list boxes.
     *
     * @return the number of items to show
     * @see ListBox#setVisibleItemCount(int)
     */
    public int getRows() {
        return rows;
    }

    /**
     * Updates the captions above the left (options) and right (selections)
     * columns. {code null} value clear the caption.
     *
     * @param leftCaption
     *            the left caption to set, or {@code null} to clear
     * @param rightCaption
     *            the right caption to set, or {@code null} to clear
     */
    public void updateCaptions(String leftCaption, String rightCaption) {
        boolean hasCaptions = leftCaption != null || rightCaption != null;

        if (leftCaption == null) {
            removeOptionsCaption();
        } else {
            getOptionsCaption().setText(leftCaption);

        }

        if (rightCaption == null) {
            removeSelectionsCaption();
        } else {
            getSelectionsCaption().setText(rightCaption);
        }

        captionWrapper.setVisible(hasCaptions);
    }

    private void removeOptionsCaption() {
        if (optionsCaption == null) {
            return;
        }

        if (optionsCaption.getParent() != null) {
            captionWrapper.remove(optionsCaption);
        }

        optionsCaption = null;
    }

    private void removeSelectionsCaption() {
        if (selectionsCaption == null) {
            return;
        }

        if (selectionsCaption.getParent() != null) {
            captionWrapper.remove(selectionsCaption);
        }

        selectionsCaption = null;
    }

    @Override
    public Registration addSelectionChangeListener(
            BiConsumer<Set<String>, Set<String>> listener) {
        Objects.nonNull(listener);
        selectionChangeListeners.add(listener);
        return (Registration) () -> selectionChangeListeners.remove(listener);
    }

    @Override
    public void setItems(List<JsonObject> items) {
        // filter selected items
        List<JsonObject> selection = items.stream()
                .filter(item -> MultiSelectWidget.isSelected(item))
                .collect(Collectors.toList());
        items.removeAll(selection);

        updateListBox(optionsListBox, items);
        updateListBox(selectionsListBox, selection);
    }

    private static void updateListBox(ListBox listBox,
            List<JsonObject> options) {
        for (int i = 0; i < options.size(); i++) {
            final JsonObject item = options.get(i);
            // reuse existing option if possible
            if (i < listBox.getItemCount()) {
                listBox.setItemText(i, MultiSelectWidget.getCaption(item));
                listBox.setValue(i, MultiSelectWidget.getKey(item));
            } else {
                listBox.addItem(MultiSelectWidget.getCaption(item),
                        MultiSelectWidget.getKey(item));
            }
        }
        // remove extra
        for (int i = listBox.getItemCount() - 1; i >= options.size(); i--) {
            listBox.removeItem(i);
        }
    }

    private static boolean[] getSelectionBitmap(ListBox listBox) {
        final boolean[] selectedIndexes = new boolean[listBox.getItemCount()];
        for (int i = 0; i < listBox.getItemCount(); i++) {
            if (listBox.isItemSelected(i)) {
                selectedIndexes[i] = true;
            } else {
                selectedIndexes[i] = false;
            }
        }
        return selectedIndexes;
    }

    private void moveSelectedItemsLeftToRight() {
        Set<String> movedItems = moveSelectedItems(optionsListBox,
                selectionsListBox);
        selectionChangeListeners
                .forEach(e -> e.accept(movedItems, Collections.emptySet()));
    }

    private void moveSelectedItemsRightToLeft() {
        Set<String> movedItems = moveSelectedItems(selectionsListBox,
                optionsListBox);
        selectionChangeListeners
                .forEach(e -> e.accept(Collections.emptySet(), movedItems));
    }

    private static Set<String> moveSelectedItems(ListBox source,
            ListBox target) {
        final boolean[] sel = getSelectionBitmap(source);
        final Set<String> movedItems = new HashSet<>();
        for (int i = 0; i < sel.length; i++) {
            if (sel[i]) {
                final int optionIndex = i
                        - (sel.length - source.getItemCount());
                movedItems.add(source.getValue(optionIndex));

                // Move selection to another column
                final String text = source.getItemText(optionIndex);
                final String value = source.getValue(optionIndex);
                target.addItem(text, value);
                target.setItemSelected(target.getItemCount() - 1, true);
                source.removeItem(optionIndex);
            }
        }

        // If no items are left move the focus to the selections
        if (source.getItemCount() == 0) {
            target.setFocus(true);
        } else {
            source.setFocus(true);
        }

        return movedItems;
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == addItemsLeftToRightButton) {
            moveSelectedItemsLeftToRight();
        } else if (event.getSource() == removeItemsRightToLeftButton) {
            moveSelectedItemsRightToLeft();
        } else if (event.getSource() == optionsListBox) {
            // unselect all in other list, to avoid mistakes (i.e wrong button)
            final int count = selectionsListBox.getItemCount();
            for (int i = 0; i < count; i++) {
                selectionsListBox.setItemSelected(i, false);
            }
        } else if (event.getSource() == selectionsListBox) {
            // unselect all in other list, to avoid mistakes (i.e wrong button)
            final int count = optionsListBox.getItemCount();
            for (int i = 0; i < count; i++) {
                optionsListBox.setItemSelected(i, false);
            }
        }
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void clearInternalHeights() {
        selectionsListBox.setHeight("");
        optionsListBox.setHeight("");
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void setInternalHeights() {
        int captionHeight = WidgetUtil.getRequiredHeight(captionWrapper);
        int totalHeight = getOffsetHeight();

        String selectHeight = totalHeight - captionHeight + "px";

        selectionsListBox.setHeight(selectHeight);
        optionsListBox.setHeight(selectHeight);
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void clearInternalWidths() {
        String colWidth = DEFAULT_COLUMN_COUNT + "em";
        String containerWidth = 2 * DEFAULT_COLUMN_COUNT + 4 + "em";
        // Caption wrapper width == optionsSelect + buttons +
        // selectionsSelect
        String captionWrapperWidth = 2 * DEFAULT_COLUMN_COUNT + 4 - 0.5 + "em";

        optionsListBox.setWidth(colWidth);
        if (optionsCaption != null) {
            optionsCaption.setWidth(colWidth);
        }
        selectionsListBox.setWidth(colWidth);
        if (selectionsCaption != null) {
            selectionsCaption.setWidth(colWidth);
        }
        buttons.setWidth("3.5em");
        optionsContainer.setWidth(containerWidth);
        captionWrapper.setWidth(captionWrapperWidth);
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void setInternalWidths() {
        getElement().getStyle().setPosition(Position.RELATIVE);
        int bordersAndPaddings = WidgetUtil
                .measureHorizontalPaddingAndBorder(buttons.getElement(), 0);

        int buttonWidth = WidgetUtil.getRequiredWidth(buttons);
        int totalWidth = getOffsetWidth();

        int spaceForSelect = (totalWidth - buttonWidth - bordersAndPaddings)
                / 2;

        optionsListBox.setWidth(spaceForSelect + "px");
        if (optionsCaption != null) {
            optionsCaption.setWidth(spaceForSelect + "px");
        }

        selectionsListBox.setWidth(spaceForSelect + "px");
        if (selectionsCaption != null) {
            selectionsCaption.setWidth(spaceForSelect + "px");
        }
        captionWrapper.setWidth("100%");
    }

    /**
     * Sets the tab index.
     *
     * @param tabIndex
     *            the tab index to set
     */
    public void setTabIndex(int tabIndex) {
        optionsListBox.setTabIndex(tabIndex);
        selectionsListBox.setTabIndex(tabIndex);
        addItemsLeftToRightButton.setTabIndex(tabIndex);
        removeItemsRightToLeftButton.setTabIndex(tabIndex);
    }

    /**
     * Sets this twin column select as read only, meaning selection cannot be
     * changed.
     *
     * @param readOnly
     *            {@code true} for read only, {@code false} for not read only
     */
    public void setReadOnly(boolean readOnly) {
        if (this.readOnly != readOnly) {
            this.readOnly = readOnly;
            updateEnabledState();
        }
    }

    /**
     * Returns {@code true} if this twin column select is in read only mode,
     * {@code false} if not.
     *
     * @return {@code true} for read only, {@code false} for not read only
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            updateEnabledState();
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    private void updateEnabledState() {
        boolean enabled = isEnabled() && !isReadOnly();
        optionsListBox.setEnabled(enabled);
        selectionsListBox.setEnabled(enabled);
        addItemsLeftToRightButton.setEnabled(enabled);
        removeItemsRightToLeftButton.setEnabled(enabled);
        addItemsLeftToRightButton.setStyleName(StyleConstants.DISABLED,
                !enabled);
        removeItemsRightToLeftButton.setStyleName(StyleConstants.DISABLED,
                !enabled);
    }

    @Override
    public void focus() {
        optionsListBox.setFocus(true);
    }

    /**
     * Get the key that selects an item in the table. By default it is the Enter
     * key but by overriding this you can change the key to whatever you want.
     *
     * @return the key that selects an item
     */
    protected int getNavigationSelectKey() {
        return KeyCodes.KEY_ENTER;
    }

    @Override
    public void onKeyDown(KeyDownEvent event) {
        int keycode = event.getNativeKeyCode();

        // Catch tab and move between select:s
        if (keycode == KeyCodes.KEY_TAB
                && event.getSource() == optionsListBox) {
            // Prevent default behavior
            event.preventDefault();

            // Remove current selections
            for (int i = 0; i < optionsListBox.getItemCount(); i++) {
                optionsListBox.setItemSelected(i, false);
            }

            // Focus selections
            selectionsListBox.setFocus(true);
        }

        if (keycode == KeyCodes.KEY_TAB && event.isShiftKeyDown()
                && event.getSource() == selectionsListBox) {
            // Prevent default behavior
            event.preventDefault();

            // Remove current selections
            for (int i = 0; i < selectionsListBox.getItemCount(); i++) {
                selectionsListBox.setItemSelected(i, false);
            }

            // Focus options
            optionsListBox.setFocus(true);
        }

        if (keycode == getNavigationSelectKey()) {
            // Prevent default behavior
            event.preventDefault();

            // Decide which select the selection was made in
            if (event.getSource() == optionsListBox) {
                // Prevents the selection to become a single selection when
                // using Enter key
                // as the selection key (default)
                optionsListBox.setFocus(false);

                moveSelectedItemsLeftToRight();

            } else if (event.getSource() == selectionsListBox) {
                // Prevents the selection to become a single selection when
                // using Enter key
                // as the selection key (default)
                selectionsListBox.setFocus(false);

                moveSelectedItemsRightToLeft();
            }
        }

    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        // Ensure that items are deselected when selecting
        // from a different source. See #3699 for details.
        if (event.getSource() == optionsListBox) {
            for (int i = 0; i < selectionsListBox.getItemCount(); i++) {
                selectionsListBox.setItemSelected(i, false);
            }
        } else if (event.getSource() == selectionsListBox) {
            for (int i = 0; i < optionsListBox.getItemCount(); i++) {
                optionsListBox.setItemSelected(i, false);
            }
        }

    }

    @Override
    public void onDoubleClick(DoubleClickEvent event) {
        if (event.getSource() == optionsListBox) {
            moveSelectedItemsLeftToRight();
            optionsListBox.setSelectedIndex(-1);
            optionsListBox.setFocus(false);
        } else if (event.getSource() == selectionsListBox) {
            moveSelectedItemsRightToLeft();
            selectionsListBox.setSelectedIndex(-1);
            selectionsListBox.setFocus(false);
        }

    }

    @Override
    public com.google.gwt.user.client.Element getSubPartElement(
            String subPart) {
        if (SUBPART_OPTION_SELECT.equals(subPart)) {
            return optionsListBox.getElement();
        } else if (subPart.startsWith(SUBPART_OPTION_SELECT_ITEM)) {
            String idx = subPart.substring(SUBPART_OPTION_SELECT_ITEM.length());
            return (com.google.gwt.user.client.Element) optionsListBox
                    .getElement().getChild(Integer.parseInt(idx));
        } else if (SUBPART_SELECTION_SELECT.equals(subPart)) {
            return selectionsListBox.getElement();
        } else if (subPart.startsWith(SUBPART_SELECTION_SELECT_ITEM)) {
            String idx = subPart
                    .substring(SUBPART_SELECTION_SELECT_ITEM.length());
            return (com.google.gwt.user.client.Element) selectionsListBox
                    .getElement().getChild(Integer.parseInt(idx));
        } else if (optionsCaption != null
                && SUBPART_LEFT_CAPTION.equals(subPart)) {
            return optionsCaption.getElement();
        } else if (selectionsCaption != null
                && SUBPART_RIGHT_CAPTION.equals(subPart)) {
            return selectionsCaption.getElement();
        } else if (SUBPART_ADD_BUTTON.equals(subPart)) {
            return addItemsLeftToRightButton.getElement();
        } else if (SUBPART_REMOVE_BUTTON.equals(subPart)) {
            return removeItemsRightToLeftButton.getElement();
        }

        return null;
    }

    @Override
    public String getSubPartName(
            com.google.gwt.user.client.Element subElement) {
        if (optionsCaption != null
                && optionsCaption.getElement().isOrHasChild(subElement)) {
            return SUBPART_LEFT_CAPTION;
        } else if (selectionsCaption != null
                && selectionsCaption.getElement().isOrHasChild(subElement)) {
            return SUBPART_RIGHT_CAPTION;
        } else if (optionsListBox.getElement().isOrHasChild(subElement)) {
            if (optionsListBox.getElement() == subElement) {
                return SUBPART_OPTION_SELECT;
            } else {
                int idx = WidgetUtil.getChildElementIndex(subElement);
                return SUBPART_OPTION_SELECT_ITEM + idx;
            }
        } else if (selectionsListBox.getElement().isOrHasChild(subElement)) {
            if (selectionsListBox.getElement() == subElement) {
                return SUBPART_SELECTION_SELECT;
            } else {
                int idx = WidgetUtil.getChildElementIndex(subElement);
                return SUBPART_SELECTION_SELECT_ITEM + idx;
            }
        } else if (addItemsLeftToRightButton.getElement()
                .isOrHasChild(subElement)) {
            return SUBPART_ADD_BUTTON;
        } else if (removeItemsRightToLeftButton.getElement()
                .isOrHasChild(subElement)) {
            return SUBPART_REMOVE_BUTTON;
        }

        return null;
    }
}

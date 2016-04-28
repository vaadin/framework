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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.vaadin.client.StyleConstants;
import com.vaadin.client.UIDL;
import com.vaadin.client.WidgetUtil;
import com.vaadin.shared.ui.twincolselect.TwinColSelectConstants;

public class VTwinColSelect extends VOptionGroupBase implements KeyDownHandler,
        MouseDownHandler, DoubleClickHandler, SubPartAware {

    public static final String CLASSNAME = "v-select-twincol";

    private static final int VISIBLE_COUNT = 10;

    private static final int DEFAULT_COLUMN_COUNT = 10;

    private final DoubleClickListBox options;

    private final DoubleClickListBox selections;

    /** For internal use only. May be removed or replaced in the future. */
    public FlowPanel captionWrapper;

    private HTML optionsCaption = null;

    private HTML selectionsCaption = null;

    private final VButton add;

    private final VButton remove;

    private final FlowPanel buttons;

    private final Panel panel;

    /**
     * A ListBox which catches double clicks
     * 
     */
    public class DoubleClickListBox extends ListBox implements
            HasDoubleClickHandlers {
        public DoubleClickListBox(boolean isMultipleSelect) {
            super(isMultipleSelect);
        }

        public DoubleClickListBox() {
            super();
        }

        @Override
        public HandlerRegistration addDoubleClickHandler(
                DoubleClickHandler handler) {
            return addDomHandler(handler, DoubleClickEvent.getType());
        }
    }

    public VTwinColSelect() {
        super(CLASSNAME);

        captionWrapper = new FlowPanel();

        options = new DoubleClickListBox();
        options.addClickHandler(this);
        options.addDoubleClickHandler(this);
        options.setVisibleItemCount(VISIBLE_COUNT);
        options.setStyleName(CLASSNAME + "-options");

        selections = new DoubleClickListBox();
        selections.addClickHandler(this);
        selections.addDoubleClickHandler(this);
        selections.setVisibleItemCount(VISIBLE_COUNT);
        selections.setStyleName(CLASSNAME + "-selections");

        buttons = new FlowPanel();
        buttons.setStyleName(CLASSNAME + "-buttons");
        add = new VButton();
        add.setText(">>");
        add.addClickHandler(this);
        remove = new VButton();
        remove.setText("<<");
        remove.addClickHandler(this);

        panel = ((Panel) optionsContainer);

        panel.add(captionWrapper);
        captionWrapper.getElement().getStyle().setOverflow(Overflow.HIDDEN);
        // Hide until there actually is a caption to prevent IE from rendering
        // extra empty space
        captionWrapper.setVisible(false);

        panel.add(options);
        buttons.add(add);
        final HTML br = new HTML("<span/>");
        br.setStyleName(CLASSNAME + "-deco");
        buttons.add(br);
        buttons.add(remove);
        panel.add(buttons);
        panel.add(selections);

        options.addKeyDownHandler(this);
        options.addMouseDownHandler(this);

        selections.addMouseDownHandler(this);
        selections.addKeyDownHandler(this);

        updateEnabledState();
    }

    public HTML getOptionsCaption() {
        if (optionsCaption == null) {
            optionsCaption = new HTML();
            optionsCaption.setStyleName(CLASSNAME + "-caption-left");
            optionsCaption.getElement().getStyle()
                    .setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
            captionWrapper.add(optionsCaption);
        }

        return optionsCaption;
    }

    public HTML getSelectionsCaption() {
        if (selectionsCaption == null) {
            selectionsCaption = new HTML();
            selectionsCaption.setStyleName(CLASSNAME + "-caption-right");
            selectionsCaption.getElement().getStyle()
                    .setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
            captionWrapper.add(selectionsCaption);
        }

        return selectionsCaption;
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void updateCaptions(UIDL uidl) {
        String leftCaption = (uidl
                .hasAttribute(TwinColSelectConstants.ATTRIBUTE_LEFT_CAPTION) ? uidl
                .getStringAttribute(TwinColSelectConstants.ATTRIBUTE_LEFT_CAPTION)
                : null);
        String rightCaption = (uidl
                .hasAttribute(TwinColSelectConstants.ATTRIBUTE_RIGHT_CAPTION) ? uidl
                .getStringAttribute(TwinColSelectConstants.ATTRIBUTE_RIGHT_CAPTION)
                : null);

        boolean hasCaptions = (leftCaption != null || rightCaption != null);

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
    public void buildOptions(UIDL uidl) {
        options.setMultipleSelect(isMultiselect());
        selections.setMultipleSelect(isMultiselect());
        options.clear();
        selections.clear();
        for (final Iterator<?> i = uidl.getChildIterator(); i.hasNext();) {
            final UIDL optionUidl = (UIDL) i.next();
            if (optionUidl.hasAttribute("selected")) {
                selections.addItem(optionUidl.getStringAttribute("caption"),
                        optionUidl.getStringAttribute("key"));
            } else {
                options.addItem(optionUidl.getStringAttribute("caption"),
                        optionUidl.getStringAttribute("key"));
            }
        }

        if (getRows() > 0) {
            options.setVisibleItemCount(getRows());
            selections.setVisibleItemCount(getRows());

        }
    }

    @Override
    protected String[] getSelectedItems() {
        final ArrayList<String> selectedItemKeys = new ArrayList<String>();
        for (int i = 0; i < selections.getItemCount(); i++) {
            selectedItemKeys.add(selections.getValue(i));
        }
        return selectedItemKeys.toArray(new String[selectedItemKeys.size()]);
    }

    private boolean[] getSelectionBitmap(ListBox listBox) {
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

    private void addItem() {
        Set<String> movedItems = moveSelectedItems(options, selections);
        selectedKeys.addAll(movedItems);

        client.updateVariable(paintableId, "selected",
                selectedKeys.toArray(new String[selectedKeys.size()]),
                isImmediate());
    }

    private void removeItem() {
        Set<String> movedItems = moveSelectedItems(selections, options);
        selectedKeys.removeAll(movedItems);

        client.updateVariable(paintableId, "selected",
                selectedKeys.toArray(new String[selectedKeys.size()]),
                isImmediate());
    }

    private Set<String> moveSelectedItems(ListBox source, ListBox target) {
        final boolean[] sel = getSelectionBitmap(source);
        final Set<String> movedItems = new HashSet<String>();
        int lastSelected = 0;
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

                if (source.getItemCount() > 0) {
                    lastSelected = optionIndex > 0 ? optionIndex - 1 : 0;
                }
            }
        }

        if (source.getItemCount() > 0) {
            source.setSelectedIndex(lastSelected);
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
        super.onClick(event);
        if (event.getSource() == add) {
            addItem();

        } else if (event.getSource() == remove) {
            removeItem();

        } else if (event.getSource() == options) {
            // unselect all in other list, to avoid mistakes (i.e wrong button)
            final int c = selections.getItemCount();
            for (int i = 0; i < c; i++) {
                selections.setItemSelected(i, false);
            }
        } else if (event.getSource() == selections) {
            // unselect all in other list, to avoid mistakes (i.e wrong button)
            final int c = options.getItemCount();
            for (int i = 0; i < c; i++) {
                options.setItemSelected(i, false);
            }
        }
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void clearInternalHeights() {
        selections.setHeight("");
        options.setHeight("");
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void setInternalHeights() {
        int captionHeight = WidgetUtil.getRequiredHeight(captionWrapper);
        int totalHeight = getOffsetHeight();

        String selectHeight = (totalHeight - captionHeight) + "px";

        selections.setHeight(selectHeight);
        options.setHeight(selectHeight);
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void clearInternalWidths() {
        int cols = -1;
        if (getColumns() > 0) {
            cols = getColumns();
        } else {
            cols = DEFAULT_COLUMN_COUNT;
        }

        if (cols >= 0) {
            String colWidth = cols + "em";
            String containerWidth = (2 * cols + 4) + "em";
            // Caption wrapper width == optionsSelect + buttons +
            // selectionsSelect
            String captionWrapperWidth = (2 * cols + 4 - 0.5) + "em";

            options.setWidth(colWidth);
            if (optionsCaption != null) {
                optionsCaption.setWidth(colWidth);
            }
            selections.setWidth(colWidth);
            if (selectionsCaption != null) {
                selectionsCaption.setWidth(colWidth);
            }
            buttons.setWidth("3.5em");
            optionsContainer.setWidth(containerWidth);
            captionWrapper.setWidth(captionWrapperWidth);
        }
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void setInternalWidths() {
        getElement().getStyle().setPosition(Position.RELATIVE);
        int bordersAndPaddings = WidgetUtil.measureHorizontalPaddingAndBorder(
                buttons.getElement(), 0);

        int buttonWidth = WidgetUtil.getRequiredWidth(buttons);
        int totalWidth = getOffsetWidth();

        int spaceForSelect = (totalWidth - buttonWidth - bordersAndPaddings) / 2;

        options.setWidth(spaceForSelect + "px");
        if (optionsCaption != null) {
            optionsCaption.setWidth(spaceForSelect + "px");
        }

        selections.setWidth(spaceForSelect + "px");
        if (selectionsCaption != null) {
            selectionsCaption.setWidth(spaceForSelect + "px");
        }
        captionWrapper.setWidth("100%");
    }

    @Override
    public void setTabIndex(int tabIndex) {
        options.setTabIndex(tabIndex);
        selections.setTabIndex(tabIndex);
        add.setTabIndex(tabIndex);
        remove.setTabIndex(tabIndex);
    }

    @Override
    public void updateEnabledState() {
        boolean enabled = isEnabled() && !isReadonly();
        options.setEnabled(enabled);
        selections.setEnabled(enabled);
        add.setEnabled(enabled);
        remove.setEnabled(enabled);
        add.setStyleName(StyleConstants.DISABLED, !enabled);
        remove.setStyleName(StyleConstants.DISABLED, !enabled);
    }

    @Override
    public void focus() {
        options.setFocus(true);
    }

    /**
     * Get the key that selects an item in the table. By default it is the Enter
     * key but by overriding this you can change the key to whatever you want.
     * 
     * @return
     */
    protected int getNavigationSelectKey() {
        return KeyCodes.KEY_ENTER;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.KeyDownHandler#onKeyDown(com.google.gwt
     * .event.dom.client.KeyDownEvent)
     */
    @Override
    public void onKeyDown(KeyDownEvent event) {
        int keycode = event.getNativeKeyCode();

        // Catch tab and move between select:s
        if (keycode == KeyCodes.KEY_TAB && event.getSource() == options) {
            // Prevent default behavior
            event.preventDefault();

            // Remove current selections
            for (int i = 0; i < options.getItemCount(); i++) {
                options.setItemSelected(i, false);
            }

            // Focus selections
            selections.setFocus(true);
        }

        if (keycode == KeyCodes.KEY_TAB && event.isShiftKeyDown()
                && event.getSource() == selections) {
            // Prevent default behavior
            event.preventDefault();

            // Remove current selections
            for (int i = 0; i < selections.getItemCount(); i++) {
                selections.setItemSelected(i, false);
            }

            // Focus options
            options.setFocus(true);
        }

        if (keycode == getNavigationSelectKey()) {
            // Prevent default behavior
            event.preventDefault();

            // Decide which select the selection was made in
            if (event.getSource() == options) {
                // Prevents the selection to become a single selection when
                // using Enter key
                // as the selection key (default)
                options.setFocus(false);

                addItem();

            } else if (event.getSource() == selections) {
                // Prevents the selection to become a single selection when
                // using Enter key
                // as the selection key (default)
                selections.setFocus(false);

                removeItem();
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.MouseDownHandler#onMouseDown(com.google
     * .gwt.event.dom.client.MouseDownEvent)
     */
    @Override
    public void onMouseDown(MouseDownEvent event) {
        // Ensure that items are deselected when selecting
        // from a different source. See #3699 for details.
        if (event.getSource() == options) {
            for (int i = 0; i < selections.getItemCount(); i++) {
                selections.setItemSelected(i, false);
            }
        } else if (event.getSource() == selections) {
            for (int i = 0; i < options.getItemCount(); i++) {
                options.setItemSelected(i, false);
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.DoubleClickHandler#onDoubleClick(com.
     * google.gwt.event.dom.client.DoubleClickEvent)
     */
    @Override
    public void onDoubleClick(DoubleClickEvent event) {
        if (event.getSource() == options) {
            addItem();
            options.setSelectedIndex(-1);
            options.setFocus(false);
        } else if (event.getSource() == selections) {
            removeItem();
            selections.setSelectedIndex(-1);
            selections.setFocus(false);
        }

    }

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

    @Override
    public com.google.gwt.user.client.Element getSubPartElement(String subPart) {
        if (SUBPART_OPTION_SELECT.equals(subPart)) {
            return options.getElement();
        } else if (subPart.startsWith(SUBPART_OPTION_SELECT_ITEM)) {
            String idx = subPart.substring(SUBPART_OPTION_SELECT_ITEM.length());
            return (com.google.gwt.user.client.Element) options.getElement()
                    .getChild(Integer.parseInt(idx));
        } else if (SUBPART_SELECTION_SELECT.equals(subPart)) {
            return selections.getElement();
        } else if (subPart.startsWith(SUBPART_SELECTION_SELECT_ITEM)) {
            String idx = subPart.substring(SUBPART_SELECTION_SELECT_ITEM
                    .length());
            return (com.google.gwt.user.client.Element) selections.getElement()
                    .getChild(Integer.parseInt(idx));
        } else if (optionsCaption != null
                && SUBPART_LEFT_CAPTION.equals(subPart)) {
            return optionsCaption.getElement();
        } else if (selectionsCaption != null
                && SUBPART_RIGHT_CAPTION.equals(subPart)) {
            return selectionsCaption.getElement();
        } else if (SUBPART_ADD_BUTTON.equals(subPart)) {
            return add.getElement();
        } else if (SUBPART_REMOVE_BUTTON.equals(subPart)) {
            return remove.getElement();
        }

        return null;
    }

    @Override
    public String getSubPartName(com.google.gwt.user.client.Element subElement) {
        if (optionsCaption != null
                && optionsCaption.getElement().isOrHasChild(subElement)) {
            return SUBPART_LEFT_CAPTION;
        } else if (selectionsCaption != null
                && selectionsCaption.getElement().isOrHasChild(subElement)) {
            return SUBPART_RIGHT_CAPTION;
        } else if (options.getElement().isOrHasChild(subElement)) {
            if (options.getElement() == subElement) {
                return SUBPART_OPTION_SELECT;
            } else {
                int idx = WidgetUtil.getChildElementIndex(subElement);
                return SUBPART_OPTION_SELECT_ITEM + idx;
            }
        } else if (selections.getElement().isOrHasChild(subElement)) {
            if (selections.getElement() == subElement) {
                return SUBPART_SELECTION_SELECT;
            } else {
                int idx = WidgetUtil.getChildElementIndex(subElement);
                return SUBPART_SELECTION_SELECT_ITEM + idx;
            }
        } else if (add.getElement().isOrHasChild(subElement)) {
            return SUBPART_ADD_BUTTON;
        } else if (remove.getElement().isOrHasChild(subElement)) {
            return SUBPART_REMOVE_BUTTON;
        }

        return null;
    }
}

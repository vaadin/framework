/*
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.dom.client.Style.Overflow;
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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;

public class VTwinColSelect extends VOptionGroupBase implements KeyDownHandler,
        MouseDownHandler, DoubleClickHandler, SubPartAware {

    private static final String CLASSNAME = "v-select-twincol";
    public static final String ATTRIBUTE_LEFT_CAPTION = "lc";
    public static final String ATTRIBUTE_RIGHT_CAPTION = "rc";

    private static final int VISIBLE_COUNT = 10;

    private static final int DEFAULT_COLUMN_COUNT = 10;

    private final DoubleClickListBox options;

    private final DoubleClickListBox selections;

    private FlowPanel captionWrapper;

    private HTML optionsCaption = null;

    private HTML selectionsCaption = null;

    private final VButton add;

    private final VButton remove;

    private final FlowPanel buttons;

    private final Panel panel;

    private boolean widthSet = false;

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
    }

    public HTML getOptionsCaption() {
        if (optionsCaption == null) {
            optionsCaption = new HTML();
            optionsCaption.setStyleName(CLASSNAME + "-options-caption");
            optionsCaption.getElement().getStyle()
                    .setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
            captionWrapper.add(optionsCaption);
        }

        return optionsCaption;
    }

    public HTML getSelectionsCaption() {
        if (selectionsCaption == null) {
            selectionsCaption = new HTML();
            selectionsCaption.setStyleName(CLASSNAME + "-selections-caption");
            selectionsCaption.getElement().getStyle()
                    .setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
            captionWrapper.add(selectionsCaption);
        }

        return selectionsCaption;
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // Captions are updated before super call to ensure the widths are set
        // correctly
        updateCaptions(uidl);

        super.updateFromUIDL(uidl, client);
        // If the server request that a cached instance should be used, do
        // nothing
        // if (uidl.isCachedComponent()) {
        if (uidl.getBooleanAttribute("cached")) {
            // Cached update, nothing to do)
            return;
        }

    }

    private void updateCaptions(UIDL uidl) {
        String leftCaption = (uidl.hasAttribute(ATTRIBUTE_LEFT_CAPTION) ? uidl
                .getStringAttribute(ATTRIBUTE_LEFT_CAPTION) : null);
        String rightCaption = (uidl.hasAttribute(ATTRIBUTE_RIGHT_CAPTION) ? uidl
                .getStringAttribute(ATTRIBUTE_RIGHT_CAPTION) : null);

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
    protected void buildOptions(UIDL uidl) {
        final boolean enabled = !isDisabled() && !isReadonly();
        options.setMultipleSelect(isMultiselect());
        selections.setMultipleSelect(isMultiselect());
        options.setEnabled(enabled);
        selections.setEnabled(enabled);
        add.setEnabled(enabled);
        remove.setEnabled(enabled);
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

        int cols = -1;
        if (getColumns() > 0) {
            cols = getColumns();
        } else if (!widthSet) {
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

    private boolean[] getItemsToAdd() {
        final boolean[] selectedIndexes = new boolean[options.getItemCount()];
        for (int i = 0; i < options.getItemCount(); i++) {
            if (options.isItemSelected(i)) {
                selectedIndexes[i] = true;
            } else {
                selectedIndexes[i] = false;
            }
        }
        return selectedIndexes;
    }

    private boolean[] getItemsToRemove() {
        final boolean[] selectedIndexes = new boolean[selections.getItemCount()];
        for (int i = 0; i < selections.getItemCount(); i++) {
            if (selections.isItemSelected(i)) {
                selectedIndexes[i] = true;
            } else {
                selectedIndexes[i] = false;
            }
        }
        return selectedIndexes;
    }

    private void addItem() {
        final boolean[] sel = getItemsToAdd();
        for (int i = 0; i < sel.length; i++) {
            if (sel[i]) {
                final int optionIndex = i
                        - (sel.length - options.getItemCount());
                selectedKeys.add(options.getValue(optionIndex));

                // Move selection to another column
                final String text = options.getItemText(optionIndex);
                final String value = options.getValue(optionIndex);
                selections.addItem(text, value);
                selections.setItemSelected(selections.getItemCount() - 1, true);
                options.removeItem(optionIndex);

                if (options.getItemCount() > 0) {
                    options.setItemSelected(optionIndex > 0 ? optionIndex - 1
                            : 0, true);
                }
            }
        }

        // If no items are left move the focus to the selections
        if (options.getItemCount() == 0) {
            selections.setFocus(true);
        } else {
            options.setFocus(true);
        }

        client.updateVariable(id, "selected",
                selectedKeys.toArray(new String[selectedKeys.size()]),
                isImmediate());
    }

    private void removeItem() {
        final boolean[] sel = getItemsToRemove();
        for (int i = 0; i < sel.length; i++) {
            if (sel[i]) {
                final int selectionIndex = i
                        - (sel.length - selections.getItemCount());
                selectedKeys.remove(selections.getValue(selectionIndex));

                // Move selection to another column
                final String text = selections.getItemText(selectionIndex);
                final String value = selections.getValue(selectionIndex);
                options.addItem(text, value);
                options.setItemSelected(options.getItemCount() - 1, true);
                selections.removeItem(selectionIndex);

                if (selections.getItemCount() > 0) {
                    selections.setItemSelected(
                            selectionIndex > 0 ? selectionIndex - 1 : 0, true);
                }
            }
        }

        // If no items are left move the focus to the selections
        if (selections.getItemCount() == 0) {
            options.setFocus(true);
        } else {
            selections.setFocus(true);
        }

        client.updateVariable(id, "selected",
                selectedKeys.toArray(new String[selectedKeys.size()]),
                isImmediate());
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

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        if ("".equals(height)) {
            options.setHeight("");
            selections.setHeight("");
        } else {
            setInternalHeights();
        }
    }

    private void setInternalHeights() {
        int captionHeight = 0;
        int totalHeight;
        if (BrowserInfo.get().isIE6()) {
            String o = getElement().getStyle().getOverflow();

            getElement().getStyle().setOverflow(Overflow.HIDDEN);
            totalHeight = getOffsetHeight();
            getElement().getStyle().setProperty("overflow", o);
        } else {
            totalHeight = getOffsetHeight();
        }

        if (optionsCaption != null) {
            captionHeight = Util.getRequiredHeight(optionsCaption);
        } else if (selectionsCaption != null) {
            captionHeight = Util.getRequiredHeight(selectionsCaption);
        }
        String selectHeight = (totalHeight - captionHeight) + "px";

        selections.setHeight(selectHeight);
        options.setHeight(selectHeight);

    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        if (!"".equals(width) && width != null) {
            setInternalWidths();
            widthSet = true;
        } else {
            widthSet = false;
        }
    }

    private void setInternalWidths() {
        DOM.setStyleAttribute(getElement(), "position", "relative");
        // TODO: Should really take borders/padding/margin into account.
        // Compensating for now with a guess.
        int buttonsExtraWidth = Util.measureHorizontalPaddingAndBorder(
                buttons.getElement(), 0);
        int buttonWidth = Util.getRequiredWidth(buttons) + buttonsExtraWidth;
        int totalWidth = getOffsetWidth();

        int spaceForSelect = (totalWidth - buttonWidth) / 2;

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
    protected void setTabIndex(int tabIndex) {
        options.setTabIndex(tabIndex);
        selections.setTabIndex(tabIndex);
        add.setTabIndex(tabIndex);
        remove.setTabIndex(tabIndex);
    }

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
    private static final String SUBPART_SELECTION_SELECT = "rightSelect";
    private static final String SUBPART_LEFT_CAPTION = "leftCaption";
    private static final String SUBPART_RIGHT_CAPTION = "rightCaption";
    private static final String SUBPART_ADD_BUTTON = "add";
    private static final String SUBPART_REMOVE_BUTTON = "remove";

    public Element getSubPartElement(String subPart) {
        if (SUBPART_OPTION_SELECT.equals(subPart)) {
            return options.getElement();
        } else if (SUBPART_SELECTION_SELECT.equals(subPart)) {
            return selections.getElement();
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

    public String getSubPartName(Element subElement) {
        if (optionsCaption != null
                && optionsCaption.getElement().isOrHasChild(subElement)) {
            return SUBPART_LEFT_CAPTION;
        } else if (selectionsCaption != null
                && selectionsCaption.getElement().isOrHasChild(subElement)) {
            return SUBPART_RIGHT_CAPTION;
        } else if (options.getElement().isOrHasChild(subElement)) {
            return SUBPART_OPTION_SELECT;
        } else if (selections.getElement().isOrHasChild(subElement)) {
            return SUBPART_SELECTION_SELECT;
        } else if (add.getElement().isOrHasChild(subElement)) {
            return SUBPART_ADD_BUTTON;
        } else if (remove.getElement().isOrHasChild(subElement)) {
            return SUBPART_REMOVE_BUTTON;
        }

        return null;
    }
}

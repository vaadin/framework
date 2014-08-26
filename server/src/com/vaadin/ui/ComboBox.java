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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Container;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.combobox.ComboBoxConstants;
import com.vaadin.shared.ui.combobox.FilteringMode;

/**
 * A filtering dropdown single-select. Suitable for newItemsAllowed, but it's
 * turned of by default to avoid mistakes. Items are filtered based on user
 * input, and loaded dynamically ("lazy-loading") from the server. You can turn
 * on newItemsAllowed and change filtering mode (and also turn it off), but you
 * can not turn on multi-select mode.
 * 
 */
@SuppressWarnings("serial")
public class ComboBox extends AbstractSelect implements
        AbstractSelect.Filtering, FieldEvents.BlurNotifier,
        FieldEvents.FocusNotifier {

    private String inputPrompt = null;

    /**
     * Holds value of property pageLength. 0 disables paging.
     */
    protected int pageLength = 10;

    // Current page when the user is 'paging' trough options
    private int currentPage = -1;

    private FilteringMode filteringMode = FilteringMode.STARTSWITH;

    private String filterstring;
    private String prevfilterstring;

    /**
     * Number of options that pass the filter, excluding the null item if any.
     */
    private int filteredSize;

    /**
     * Cache of filtered options, used only by the in-memory filtering system.
     */
    private List<Object> filteredOptions;

    /**
     * Flag to indicate that request repaint is called by filter request only
     */
    private boolean optionRequest;

    /**
     * True while painting to suppress item set change notifications that could
     * be caused by temporary filtering.
     */
    private boolean isPainting;

    /**
     * Flag to indicate whether to scroll the selected item visible (select the
     * page on which it is) when opening the popup or not. Only applies to
     * single select mode.
     * 
     * This requires finding the index of the item, which can be expensive in
     * many large lazy loading containers.
     */
    private boolean scrollToSelectedItem = true;

    /**
     * If text input is not allowed, the ComboBox behaves like a pretty
     * NativeSelect - the user can not enter any text and clicking the text
     * field opens the drop down with options
     */
    private boolean textInputAllowed = true;

    public ComboBox() {
        initDefaults();
    }

    public ComboBox(String caption, Collection<?> options) {
        super(caption, options);
        initDefaults();
    }

    public ComboBox(String caption, Container dataSource) {
        super(caption, dataSource);
        initDefaults();
    }

    public ComboBox(String caption) {
        super(caption);
        initDefaults();
    }

    /**
     * Initialize the ComboBox with default settings
     */
    private void initDefaults() {
        setNewItemsAllowed(false);
        setImmediate(true);
    }

    /**
     * Gets the current input prompt.
     * 
     * @see #setInputPrompt(String)
     * @return the current input prompt, or null if not enabled
     */
    public String getInputPrompt() {
        return inputPrompt;
    }

    /**
     * Sets the input prompt - a textual prompt that is displayed when the
     * select would otherwise be empty, to prompt the user for input.
     * 
     * @param inputPrompt
     *            the desired input prompt, or null to disable
     */
    public void setInputPrompt(String inputPrompt) {
        this.inputPrompt = inputPrompt;
        markAsDirty();
    }

    private boolean isFilteringNeeded() {
        return filterstring != null && filterstring.length() > 0
                && filteringMode != FilteringMode.OFF;
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        isPainting = true;
        try {
            if (inputPrompt != null) {
                target.addAttribute(ComboBoxConstants.ATTR_INPUTPROMPT,
                        inputPrompt);
            }

            if (!textInputAllowed) {
                target.addAttribute(ComboBoxConstants.ATTR_NO_TEXT_INPUT, true);
            }

            // clear caption change listeners
            getCaptionChangeListener().clear();

            // The tab ordering number
            if (getTabIndex() != 0) {
                target.addAttribute("tabindex", getTabIndex());
            }

            // If the field is modified, but not committed, set modified
            // attribute
            if (isModified()) {
                target.addAttribute("modified", true);
            }

            if (isNewItemsAllowed()) {
                target.addAttribute("allownewitem", true);
            }

            boolean needNullSelectOption = false;
            if (isNullSelectionAllowed()) {
                target.addAttribute("nullselect", true);
                needNullSelectOption = (getNullSelectionItemId() == null);
                if (!needNullSelectOption) {
                    target.addAttribute("nullselectitem", true);
                }
            }

            // Constructs selected keys array
            String[] selectedKeys = new String[(getValue() == null
                    && getNullSelectionItemId() == null ? 0 : 1)];

            target.addAttribute("pagelength", pageLength);

            target.addAttribute("filteringmode", getFilteringMode().toString());

            // Paints the options and create array of selected id keys
            int keyIndex = 0;

            target.startTag("options");

            if (currentPage < 0) {
                optionRequest = false;
                currentPage = 0;
                filterstring = "";
            }

            boolean nullFilteredOut = isFilteringNeeded();
            // null option is needed and not filtered out, even if not on
            // current
            // page
            boolean nullOptionVisible = needNullSelectOption
                    && !nullFilteredOut;

            // first try if using container filters is possible
            List<?> options = getOptionsWithFilter(nullOptionVisible);
            if (null == options) {
                // not able to use container filters, perform explicit in-memory
                // filtering
                options = getFilteredOptions();
                filteredSize = options.size();
                options = sanitetizeList(options, nullOptionVisible);
            }

            final boolean paintNullSelection = needNullSelectOption
                    && currentPage == 0 && !nullFilteredOut;

            if (paintNullSelection) {
                target.startTag("so");
                target.addAttribute("caption", "");
                target.addAttribute("key", "");
                target.endTag("so");
            }

            final Iterator<?> i = options.iterator();
            // Paints the available selection options from data source

            while (i.hasNext()) {

                final Object id = i.next();

                if (!isNullSelectionAllowed() && id != null
                        && id.equals(getNullSelectionItemId())
                        && !isSelected(id)) {
                    continue;
                }

                // Gets the option attribute values
                final String key = itemIdMapper.key(id);
                final String caption = getItemCaption(id);
                final Resource icon = getItemIcon(id);
                getCaptionChangeListener().addNotifierForItem(id);

                // Paints the option
                target.startTag("so");
                if (icon != null) {
                    target.addAttribute("icon", icon);
                }
                target.addAttribute("caption", caption);
                if (id != null && id.equals(getNullSelectionItemId())) {
                    target.addAttribute("nullselection", true);
                }
                target.addAttribute("key", key);
                if (keyIndex < selectedKeys.length && isSelected(id)) {
                    // at most one item can be selected at a time
                    selectedKeys[keyIndex++] = key;
                }
                target.endTag("so");
            }
            target.endTag("options");

            target.addAttribute("totalitems", size()
                    + (needNullSelectOption ? 1 : 0));
            if (filteredSize > 0 || nullOptionVisible) {
                target.addAttribute("totalMatches", filteredSize
                        + (nullOptionVisible ? 1 : 0));
            }

            // Paint variables
            target.addVariable(this, "selected", selectedKeys);
            if (isNewItemsAllowed()) {
                target.addVariable(this, "newitem", "");
            }

            target.addVariable(this, "filter", filterstring);
            target.addVariable(this, "page", currentPage);

            currentPage = -1; // current page is always set by client

            optionRequest = true;
        } finally {
            isPainting = false;
        }

    }

    /**
     * Sets whether it is possible to input text into the field or whether the
     * field area of the component is just used to show what is selected. By
     * disabling text input, the comboBox will work in the same way as a
     * {@link NativeSelect}
     * 
     * @see #isTextInputAllowed()
     * 
     * @param textInputAllowed
     *            true to allow entering text, false to just show the current
     *            selection
     */
    public void setTextInputAllowed(boolean textInputAllowed) {
        this.textInputAllowed = textInputAllowed;
        markAsDirty();
    }

    /**
     * Returns true if the user can enter text into the field to either filter
     * the selections or enter a new value if {@link #isNewItemsAllowed()}
     * returns true. If text input is disabled, the comboBox will work in the
     * same way as a {@link NativeSelect}
     * 
     * @return
     */
    public boolean isTextInputAllowed() {
        return textInputAllowed;
    }

    /**
     * Returns the filtered options for the current page using a container
     * filter.
     * 
     * As a size effect, {@link #filteredSize} is set to the total number of
     * items passing the filter.
     * 
     * The current container must be {@link Filterable} and {@link Indexed}, and
     * the filtering mode must be suitable for container filtering (tested with
     * {@link #canUseContainerFilter()}).
     * 
     * Use {@link #getFilteredOptions()} and
     * {@link #sanitetizeList(List, boolean)} if this is not the case.
     * 
     * @param needNullSelectOption
     * @return filtered list of options (may be empty) or null if cannot use
     *         container filters
     */
    protected List<?> getOptionsWithFilter(boolean needNullSelectOption) {
        Container container = getContainerDataSource();

        if (pageLength == 0 && !isFilteringNeeded()) {
            // no paging or filtering: return all items
            filteredSize = container.size();
            assert filteredSize >= 0;
            return new ArrayList<Object>(container.getItemIds());
        }

        if (!(container instanceof Filterable)
                || !(container instanceof Indexed)
                || getItemCaptionMode() != ITEM_CAPTION_MODE_PROPERTY) {
            return null;
        }

        Filterable filterable = (Filterable) container;

        Filter filter = buildFilter(filterstring, filteringMode);

        // adding and removing filters leads to extraneous item set
        // change events from the underlying container, but the ComboBox does
        // not process or propagate them based on the flag filteringContainer
        if (filter != null) {
            filterable.addContainerFilter(filter);
        }

        // try-finally to ensure that the filter is removed from container even
        // if a exception is thrown...
        try {
            Indexed indexed = (Indexed) container;

            int indexToEnsureInView = -1;

            // if not an option request (item list when user changes page), go
            // to page with the selected item after filtering if accepted by
            // filter
            Object selection = getValue();
            if (isScrollToSelectedItem() && !optionRequest && selection != null) {
                // ensure proper page
                indexToEnsureInView = indexed.indexOfId(selection);
            }

            filteredSize = container.size();
            assert filteredSize >= 0;
            currentPage = adjustCurrentPage(currentPage, needNullSelectOption,
                    indexToEnsureInView, filteredSize);
            int first = getFirstItemIndexOnCurrentPage(needNullSelectOption,
                    filteredSize);
            int last = getLastItemIndexOnCurrentPage(needNullSelectOption,
                    filteredSize, first);

            // Compute the number of items to fetch from the indexes given or
            // based on the filtered size of the container
            int lastItemToFetch = Math.min(last, filteredSize - 1);
            int nrOfItemsToFetch = (lastItemToFetch + 1) - first;

            List<?> options = indexed.getItemIds(first, nrOfItemsToFetch);

            return options;
        } finally {
            // to the outside, filtering should not be visible
            if (filter != null) {
                filterable.removeContainerFilter(filter);
            }
        }
    }

    /**
     * Constructs a filter instance to use when using a Filterable container in
     * the <code>ITEM_CAPTION_MODE_PROPERTY</code> mode.
     * 
     * Note that the client side implementation expects the filter string to
     * apply to the item caption string it sees, so changing the behavior of
     * this method can cause problems.
     * 
     * @param filterString
     * @param filteringMode
     * @return
     */
    protected Filter buildFilter(String filterString,
            FilteringMode filteringMode) {
        Filter filter = null;

        if (null != filterString && !"".equals(filterString)) {
            switch (filteringMode) {
            case OFF:
                break;
            case STARTSWITH:
                filter = new SimpleStringFilter(getItemCaptionPropertyId(),
                        filterString, true, true);
                break;
            case CONTAINS:
                filter = new SimpleStringFilter(getItemCaptionPropertyId(),
                        filterString, true, false);
                break;
            }
        }
        return filter;
    }

    @Override
    public void containerItemSetChange(Container.ItemSetChangeEvent event) {
        if (!isPainting) {
            super.containerItemSetChange(event);
        }
    }

    /**
     * Makes correct sublist of given list of options.
     * 
     * If paint is not an option request (affected by page or filter change),
     * page will be the one where possible selection exists.
     * 
     * Detects proper first and last item in list to return right page of
     * options. Also, if the current page is beyond the end of the list, it will
     * be adjusted.
     * 
     * @param options
     * @param needNullSelectOption
     *            flag to indicate if nullselect option needs to be taken into
     *            consideration
     */
    private List<?> sanitetizeList(List<?> options, boolean needNullSelectOption) {

        if (pageLength != 0 && options.size() > pageLength) {

            int indexToEnsureInView = -1;

            // if not an option request (item list when user changes page), go
            // to page with the selected item after filtering if accepted by
            // filter
            Object selection = getValue();
            if (isScrollToSelectedItem() && !optionRequest && selection != null) {
                // ensure proper page
                indexToEnsureInView = options.indexOf(selection);
            }

            int size = options.size();
            currentPage = adjustCurrentPage(currentPage, needNullSelectOption,
                    indexToEnsureInView, size);
            int first = getFirstItemIndexOnCurrentPage(needNullSelectOption,
                    size);
            int last = getLastItemIndexOnCurrentPage(needNullSelectOption,
                    size, first);
            return options.subList(first, last + 1);
        } else {
            return options;
        }
    }

    /**
     * Returns the index of the first item on the current page. The index is to
     * the underlying (possibly filtered) contents. The null item, if any, does
     * not have an index but takes up a slot on the first page.
     * 
     * @param needNullSelectOption
     *            true if a null option should be shown before any other options
     *            (takes up the first slot on the first page, not counted in
     *            index)
     * @param size
     *            number of items after filtering (not including the null item,
     *            if any)
     * @return first item to show on the UI (index to the filtered list of
     *         options, not taking the null item into consideration if any)
     */
    private int getFirstItemIndexOnCurrentPage(boolean needNullSelectOption,
            int size) {
        // Not all options are visible, find out which ones are on the
        // current "page".
        int first = currentPage * pageLength;
        if (needNullSelectOption && currentPage > 0) {
            first--;
        }
        return first;
    }

    /**
     * Returns the index of the last item on the current page. The index is to
     * the underlying (possibly filtered) contents. If needNullSelectOption is
     * true, the null item takes up the first slot on the first page,
     * effectively reducing the first page size by one.
     * 
     * @param needNullSelectOption
     *            true if a null option should be shown before any other options
     *            (takes up the first slot on the first page, not counted in
     *            index)
     * @param size
     *            number of items after filtering (not including the null item,
     *            if any)
     * @param first
     *            index in the filtered view of the first item of the page
     * @return index in the filtered view of the last item on the page
     */
    private int getLastItemIndexOnCurrentPage(boolean needNullSelectOption,
            int size, int first) {
        // page length usable for non-null items
        int effectivePageLength = pageLength
                - (needNullSelectOption && (currentPage == 0) ? 1 : 0);
        return Math.min(size - 1, first + effectivePageLength - 1);
    }

    /**
     * Adjusts the index of the current page if necessary: make sure the current
     * page is not after the end of the contents, and optionally go to the page
     * containg a specific item. There are no side effects but the adjusted page
     * index is returned.
     * 
     * @param page
     *            page number to use as the starting point
     * @param needNullSelectOption
     *            true if a null option should be shown before any other options
     *            (takes up the first slot on the first page, not counted in
     *            index)
     * @param indexToEnsureInView
     *            index of an item that should be included on the page (in the
     *            data set, not counting the null item if any), -1 for none
     * @param size
     *            number of items after filtering (not including the null item,
     *            if any)
     */
    private int adjustCurrentPage(int page, boolean needNullSelectOption,
            int indexToEnsureInView, int size) {
        if (indexToEnsureInView != -1) {
            int newPage = (indexToEnsureInView + (needNullSelectOption ? 1 : 0))
                    / pageLength;
            page = newPage;
        }
        // adjust the current page if beyond the end of the list
        if (page * pageLength > size) {
            page = (size + (needNullSelectOption ? 1 : 0)) / pageLength;
        }
        return page;
    }

    /**
     * Filters the options in memory and returns the full filtered list.
     * 
     * This can be less efficient than using container filters, so use
     * {@link #getOptionsWithFilter(boolean)} if possible (filterable container
     * and suitable item caption mode etc.).
     * 
     * @return
     */
    protected List<?> getFilteredOptions() {
        if (!isFilteringNeeded()) {
            prevfilterstring = null;
            filteredOptions = new LinkedList<Object>(getItemIds());
            return filteredOptions;
        }

        if (filterstring.equals(prevfilterstring)) {
            return filteredOptions;
        }

        Collection<?> items;
        if (prevfilterstring != null
                && filterstring.startsWith(prevfilterstring)) {
            items = filteredOptions;
        } else {
            items = getItemIds();
        }
        prevfilterstring = filterstring;

        filteredOptions = new LinkedList<Object>();
        for (final Iterator<?> it = items.iterator(); it.hasNext();) {
            final Object itemId = it.next();
            String caption = getItemCaption(itemId);
            if (caption == null || caption.equals("")) {
                continue;
            } else {
                caption = caption.toLowerCase();
            }
            switch (filteringMode) {
            case CONTAINS:
                if (caption.indexOf(filterstring) > -1) {
                    filteredOptions.add(itemId);
                }
                break;
            case STARTSWITH:
            default:
                if (caption.startsWith(filterstring)) {
                    filteredOptions.add(itemId);
                }
                break;
            }
        }

        return filteredOptions;
    }

    /**
     * Invoked when the value of a variable has changed.
     * 
     * @see com.vaadin.ui.AbstractComponent#changeVariables(java.lang.Object,
     *      java.util.Map)
     */
    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        // Not calling super.changeVariables due the history of select
        // component hierarchy

        // Selection change
        if (variables.containsKey("selected")) {
            final String[] ka = (String[]) variables.get("selected");

            // Single select mode
            if (ka.length == 0) {

                // Allows deselection only if the deselected item is visible
                final Object current = getValue();
                final Collection<?> visible = getVisibleItemIds();
                if (visible != null && visible.contains(current)) {
                    setValue(null, true);
                }
            } else {
                final Object id = itemIdMapper.get(ka[0]);
                if (id != null && id.equals(getNullSelectionItemId())) {
                    setValue(null, true);
                } else {
                    setValue(id, true);
                }
            }
        }

        String newFilter;
        if ((newFilter = (String) variables.get("filter")) != null) {
            // this is a filter request
            currentPage = ((Integer) variables.get("page")).intValue();
            filterstring = newFilter;
            if (filterstring != null) {
                filterstring = filterstring.toLowerCase();
            }
            requestRepaint();
        } else if (isNewItemsAllowed()) {
            // New option entered (and it is allowed)
            final String newitem = (String) variables.get("newitem");
            if (newitem != null && newitem.length() > 0) {
                getNewItemHandler().addNewItem(newitem);
                // rebuild list
                filterstring = null;
                prevfilterstring = null;
            }
        }

        if (variables.containsKey(FocusEvent.EVENT_ID)) {
            fireEvent(new FocusEvent(this));
        }
        if (variables.containsKey(BlurEvent.EVENT_ID)) {
            fireEvent(new BlurEvent(this));
        }

    }

    @Override
    public void setFilteringMode(FilteringMode filteringMode) {
        this.filteringMode = filteringMode;
    }

    @Override
    public FilteringMode getFilteringMode() {
        return filteringMode;
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

    /**
     * ComboBox does not support multi select mode.
     * 
     * @deprecated As of 7.0, use {@link ListSelect}, {@link OptionGroup} or
     *             {@link TwinColSelect} instead
     * @see com.vaadin.ui.AbstractSelect#setMultiSelect(boolean)
     * @throws UnsupportedOperationException
     *             if trying to activate multiselect mode
     */
    @Deprecated
    @Override
    public void setMultiSelect(boolean multiSelect) {
        if (multiSelect) {
            throw new UnsupportedOperationException("Multiselect not supported");
        }
    }

    /**
     * ComboBox does not support multi select mode.
     * 
     * @deprecated As of 7.0, use {@link ListSelect}, {@link OptionGroup} or
     *             {@link TwinColSelect} instead
     * 
     * @see com.vaadin.ui.AbstractSelect#isMultiSelect()
     * 
     * @return false
     */
    @Deprecated
    @Override
    public boolean isMultiSelect() {
        return false;
    }

    /**
     * Returns the page length of the suggestion popup.
     * 
     * @return the pageLength
     */
    public int getPageLength() {
        return pageLength;
    }

    /**
     * Sets the page length for the suggestion popup. Setting the page length to
     * 0 will disable suggestion popup paging (all items visible).
     * 
     * @param pageLength
     *            the pageLength to set
     */
    public void setPageLength(int pageLength) {
        this.pageLength = pageLength;
        markAsDirty();
    }

    /**
     * Sets whether to scroll the selected item visible (directly open the page
     * on which it is) when opening the combo box popup or not. Only applies to
     * single select mode.
     * 
     * This requires finding the index of the item, which can be expensive in
     * many large lazy loading containers.
     * 
     * @param scrollToSelectedItem
     *            true to find the page with the selected item when opening the
     *            selection popup
     */
    public void setScrollToSelectedItem(boolean scrollToSelectedItem) {
        this.scrollToSelectedItem = scrollToSelectedItem;
    }

    /**
     * Returns true if the select should find the page with the selected item
     * when opening the popup (single select combo box only).
     * 
     * @see #setScrollToSelectedItem(boolean)
     * 
     * @return true if the page with the selected item will be shown when
     *         opening the popup
     */
    public boolean isScrollToSelectedItem() {
        return scrollToSelectedItem;
    }

}

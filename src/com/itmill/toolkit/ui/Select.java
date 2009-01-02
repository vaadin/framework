/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.ui;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.terminal.Resource;

/**
 * <p>
 * A class representing a selection of items the user has selected in a UI. The
 * set of choices is presented as a set of {@link com.itmill.toolkit.data.Item}s
 * in a {@link com.itmill.toolkit.data.Container}.
 * </p>
 * 
 * <p>
 * A <code>Select</code> component may be in single- or multiselect mode.
 * Multiselect mode means that more than one item can be selected
 * simultaneously.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public class Select extends AbstractSelect implements AbstractSelect.Filtering {

    /**
     * Holds value of property pageLength. 0 disables paging.
     */
    protected int pageLength = 10;

    private int columns = 0;

    // Current page when the user is 'paging' trough options
    private int currentPage = -1;

    private int filteringMode = FILTERINGMODE_STARTSWITH;

    private String filterstring;
    private String prevfilterstring;
    private List filteredOptions;

    /**
     * Flag to indicate that request repaint is called by filter request only
     */
    private boolean optionRequest;

    /* Constructors */

    /* Component methods */

    public Select() {
        super();
    }

    public Select(String caption, Collection options) {
        super(caption, options);
    }

    public Select(String caption, Container dataSource) {
        super(caption, dataSource);
    }

    public Select(String caption) {
        super(caption);
    }

    /**
     * Paints the content of this component.
     * 
     * @param target
     *            the Paint Event.
     * @throws PaintException
     *             if the paint operation failed.
     */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        if (isMultiSelect()) {
            // background compatibility hack. This object shouldn't be used for
            // multiselect lists anymore (ListSelect instead). This fallbacks to
            // a simpler paint method in super class.
            super.paintContent(target);
            return;
        }

        // clear caption change listeners
        getCaptionChangeListener().clear();

        // The tab ordering number
        if (getTabIndex() != 0) {
            target.addAttribute("tabindex", getTabIndex());
        }

        // If the field is modified, but not committed, set modified attribute
        if (isModified()) {
            target.addAttribute("modified", true);
        }

        // Adds the required attribute
        if (isRequired()) {
            target.addAttribute("required", true);
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
        String[] selectedKeys;
        if (isMultiSelect()) {
            selectedKeys = new String[((Set) getValue()).size()];
        } else {
            selectedKeys = new String[(getValue() == null
                    && getNullSelectionItemId() == null ? 0 : 1)];
        }

        target.addAttribute("filteringmode", getFilteringMode());

        // Paints the options and create array of selected id keys
        // TODO Also use conventional rendering if lazy loading is not supported
        // by terminal
        int keyIndex = 0;

        target.startTag("options");

        if (currentPage < 0) {
            optionRequest = false;
            currentPage = 0;
            filterstring = "";
        }

        List options = getFilteredOptions();
        options = sanitetizeList(options, needNullSelectOption);

        final boolean paintNullSelection = needNullSelectOption
                && (currentPage == 0 && (filterstring == null || filterstring
                        .equals("")));

        if (paintNullSelection) {
            target.startTag("so");
            target.addAttribute("caption", "");
            target.addAttribute("key", "");
            target.endTag("so");
        }

        final Iterator i = options.iterator();
        // Paints the available selection options from data source

        while (i.hasNext()) {

            final Object id = i.next();

            if (!isNullSelectionAllowed() && id != null
                    && id.equals(getNullSelectionItemId()) && !isSelected(id)) {
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
            if (isSelected(id) && keyIndex < selectedKeys.length) {
                target.addAttribute("selected", true);
                selectedKeys[keyIndex++] = key;
            }
            target.endTag("so");
        }
        target.endTag("options");

        target.addAttribute("totalitems", size()
                + (needNullSelectOption ? 1 : 0));
        if (filteredOptions != null) {
            target.addAttribute("totalMatches", filteredOptions.size()
                    + (needNullSelectOption ? 1 : 0));
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
    }

    /**
     * Makes correct sublist of given list of options.
     * 
     * If paint is not an option request (affected by page or filter change),
     * page will be the one where possible selection exists.
     * 
     * Detects proper first and last item in list to return right page of
     * options.
     * 
     * @param options
     * @param needNullSelectOption
     *            flag to indicate if nullselect option needs to be taken into
     *            consideration
     */
    private List sanitetizeList(List options, boolean needNullSelectOption) {

        if (options.size() > pageLength) {
            int first = currentPage * pageLength;
            int last = first + pageLength;
            if (needNullSelectOption) {
                if (currentPage > 0) {
                    first--;
                }
                last--;
            }
            if (options.size() < last) {
                last = options.size();
            }
            if (!optionRequest) {
                // TODO ensure proper page
                if (!isMultiSelect()) {
                    Object selection = getValue();
                    if (selection != null) {
                        int index = options.indexOf(selection);
                        if (index != -1 && (index < first || index >= last)) {
                            int newPage = (index + (needNullSelectOption ? 1
                                    : 0))
                                    / pageLength;
                            currentPage = newPage;
                            return sanitetizeList(options, needNullSelectOption);
                        }
                    }
                }
            }

            return options.subList(first, last);
        } else {
            return options;
        }
    }

    protected List getFilteredOptions() {
        if (filterstring == null || filterstring.equals("")
                || filteringMode == FILTERINGMODE_OFF) {
            prevfilterstring = null;
            filteredOptions = new LinkedList(getItemIds());
            return filteredOptions;
        }

        if (filterstring.equals(prevfilterstring)) {
            return filteredOptions;
        }

        Collection items;
        if (prevfilterstring != null
                && filterstring.startsWith(prevfilterstring)) {
            items = filteredOptions;
        } else {
            items = getItemIds();
        }
        prevfilterstring = filterstring;

        filteredOptions = new LinkedList();
        for (final Iterator it = items.iterator(); it.hasNext();) {
            final Object itemId = it.next();
            String caption = getItemCaption(itemId);
            if (caption == null || caption.equals("")) {
                continue;
            } else {
                caption = caption.toLowerCase();
            }
            switch (filteringMode) {
            case FILTERINGMODE_CONTAINS:
                if (caption.indexOf(filterstring) > -1) {
                    filteredOptions.add(itemId);
                }
                break;
            case FILTERINGMODE_STARTSWITH:
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
     * @see com.itmill.toolkit.ui.AbstractComponent#changeVariables(java.lang.Object,
     *      java.util.Map)
     */
    @Override
    public void changeVariables(Object source, Map variables) {

        // Selection change
        if (variables.containsKey("selected")) {
            final String[] ka = (String[]) variables.get("selected");

            if (isMultiSelect()) {
                // Multiselect mode

                // TODO Optimize by adding repaintNotNeeded whan applicaple

                // Converts the key-array to id-set
                final LinkedList s = new LinkedList();
                for (int i = 0; i < ka.length; i++) {
                    final Object id = itemIdMapper.get(ka[i]);
                    if (id != null && containsId(id)) {
                        s.add(id);
                    }
                }

                // Limits the deselection to the set of visible items
                // (non-visible items can not be deselected)
                final Collection visible = getVisibleItemIds();
                if (visible != null) {
                    Set newsel = (Set) getValue();
                    if (newsel == null) {
                        newsel = new HashSet();
                    } else {
                        newsel = new HashSet(newsel);
                    }
                    newsel.removeAll(visible);
                    newsel.addAll(s);
                    setValue(newsel, true);
                }
            } else {
                // Single select mode
                if (ka.length == 0) {

                    // Allows deselection only if the deselected item is visible
                    final Object current = getValue();
                    final Collection visible = getVisibleItemIds();
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
        }

        String newFilter;
        if ((newFilter = (String) variables.get("filter")) != null) {
            // this is a filter request
            currentPage = ((Integer) variables.get("page")).intValue();
            filterstring = newFilter;
            if (filterstring != null) {
                filterstring = filterstring.toLowerCase();
            }
            optionRepaint();
            return;
        }

        // Try to set the property value

        // New option entered (and it is allowed)
        final String newitem = (String) variables.get("newitem");
        if (newitem != null && newitem.length() > 0) {
            getNewItemHandler().addNewItem(newitem);
            // rebuild list
            filterstring = null;
            prevfilterstring = null;
        }

    }

    @Override
    public void requestRepaint() {
        super.requestRepaint();
        optionRequest = false;
        prevfilterstring = filterstring;
        filterstring = null;
    }

    private void optionRepaint() {
        super.requestRepaint();
    }

    /**
     * Gets the component UIDL tag.
     * 
     * @return the Component UIDL tag as string.
     */
    @Override
    public String getTag() {
        return "select";
    }

    public void setFilteringMode(int filteringMode) {
        this.filteringMode = filteringMode;
    }

    public int getFilteringMode() {
        return filteringMode;
    }

    /**
     * Note, one should use more generic setWidth(String) method instead of
     * this. This now days actually converts columns to width with em css unit.
     * 
     * Sets the number of columns in the editor. If the number of columns is set
     * 0, the actual number of displayed columns is determined implicitly by the
     * adapter.
     * 
     * @deprecated
     * 
     * @param columns
     *            the number of columns to set.
     */
    @Deprecated
    public void setColumns(int columns) {
        if (columns < 0) {
            columns = 0;
        }
        if (this.columns != columns) {
            this.columns = columns;
            setWidth(columns, Select.UNITS_EM);
            requestRepaint();
        }
    }

    /**
     * @deprecated see setter function
     * @return
     */
    @Deprecated
    public int getColumns() {
        return columns;
    }

}

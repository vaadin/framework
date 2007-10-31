/* *************************************************************************
 
 IT Mill Toolkit 

 Development of Browser User Interfaces Made Easy

 Copyright (C) 2000-2006 IT Mill Ltd
 
 *************************************************************************

 This product is distributed under commercial license that can be found
 from the product package on license.pdf. Use of this product might 
 require purchasing a commercial license from IT Mill Ltd. For guidelines 
 on usage, see licensing-guidelines.html

 *************************************************************************
 
 For more information, contact:
 
 IT Mill Ltd                           phone: +358 2 4802 7180
 Ruukinkatu 2-4                        fax:   +358 2 4802 7181
 20540, Turku                          email:  info@itmill.com
 Finland                               company www: www.itmill.com
 
 Primary source for information and releases: www.itmill.com

 ********************************************************************** */

package com.itmill.toolkit.ui;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Property;
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
	protected int pageLength = 15;

	// current page when the user is 'paging' trough options
	private int currentPage;

	private int filteringMode = FILTERINGMODE_STARTSWITH;

	private String filterstring;
	private String prevfilterstring;
	private List filteredOptions;

	/* Constructors ********************************************************* */

	/* Component methods **************************************************** */

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
	public void paintContent(PaintTarget target) throws PaintException {
		// Focus control id
		if (getFocusableId() > 0) {
			target.addAttribute("focusid", getFocusableId());
		}

		// The tab ordering number
		if (getTabIndex() > 0) {
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

		// Paints select attributes
		if (isMultiSelect()) {
			target.addAttribute("selectmode", "multi");
		}
		if (isNewItemsAllowed()) {
			target.addAttribute("allownewitem", true);
		}
		if (!isNullSelectionAllowed()) {
			target.addAttribute("nullselect", false);
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

		/*
		 * if (!isLazyLoading()) { // Support for external null selection item
		 * id Collection ids = getItemIds(); if (getNullSelectionItemId() !=
		 * null && (!ids.contains(getNullSelectionItemId()))) { // Gets the
		 * option attribute values Object id = getNullSelectionItemId(); String
		 * key = this.itemIdMapper.key(id); String caption = getItemCaption(id);
		 * Resource icon = getItemIcon(id); // Paints option
		 * target.startTag("so"); if (icon != null) {
		 * target.addAttribute("icon", icon); } target.addAttribute("caption",
		 * caption); target.addAttribute("nullselection", true);
		 * target.addAttribute("key", key); if (isSelected(id)) {
		 * target.addAttribute("selected", true); selectedKeys[keyIndex++] =
		 * key; } target.endTag("so"); } }
		 */
		/*
		 * Iterator i; if (this.filterstring != null) { i =
		 * this.optionFilter.filter(this.filterstring,
		 * this.lazyLoadingPageLength, this.page).iterator();
		 * target.addAttribute("totalMatches", this.optionFilter
		 * .getMatchCount()); } else { i = getItemIds().iterator(); }
		 */
		List options = getFilteredOptions();
		if (options.size() > this.pageLength) {
			int first = this.currentPage * this.pageLength;
			int last = first + this.pageLength;
			if (options.size() < last) {
				last = options.size();
			}
			options = options.subList(first, last);
		}
		Iterator i = options.iterator();
		// Paints the available selection options from data source

		target.startTag("options");
		while (i.hasNext()) {

			// Gets the option attribute values
			Object id = i.next();
			String key = this.itemIdMapper.key(id);
			String caption = getItemCaption(id);
			Resource icon = getItemIcon(id);

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

		target.addAttribute("totalitems", size());
		if (this.filteredOptions != null) {
			target.addAttribute("totalMatches", this.filteredOptions.size());
		}

		// Paint variables
		target.addVariable(this, "selected", selectedKeys);
		if (isNewItemsAllowed()) {
			target.addVariable(this, "newitem", "");
		}

		target.addVariable(this, "filter", this.filterstring);
		target.addVariable(this, "page", this.currentPage);

	}

	protected List getFilteredOptions() {
		if (this.filterstring == null || this.filterstring.equals("")
				|| this.filteringMode == FILTERINGMODE_OFF) {
			this.filteredOptions = new LinkedList(getItemIds());
			return this.filteredOptions;
		}

		if (this.filterstring.equals(this.prevfilterstring)) {
			return this.filteredOptions;
		}

		Collection items;
		if (prevfilterstring != null
				&& filterstring.startsWith(this.prevfilterstring)) {
			items = filteredOptions;
		} else {
			items = getItemIds();
		}
		prevfilterstring = filterstring;

		this.filteredOptions = new LinkedList();
		for (Iterator it = items.iterator(); it.hasNext();) {
			Object itemId = it.next();
			String caption = getItemCaption(itemId);
			if (caption == null || caption.equals("")) {
				continue;
			}
			switch (this.filteringMode) {
			case FILTERINGMODE_CONTAINS:
				if (caption.indexOf(this.filterstring) > -1) {
					this.filteredOptions.add(itemId);
				}
				break;
			case FILTERINGMODE_STARTSWITH:
			default:
				if (caption.startsWith(this.filterstring)) {
					this.filteredOptions.add(itemId);
				}
				break;
			}
		}

		return this.filteredOptions;
	}

	/**
	 * Invoked when the value of a variable has changed.
	 * 
	 * @see com.itmill.toolkit.ui.AbstractComponent#changeVariables(java.lang.Object,
	 *      java.util.Map)
	 */
	public void changeVariables(Object source, Map variables) {
		String newFilter;
		if ((newFilter = (String) variables.get("filter")) != null) {
			// this is a filter request
			this.currentPage = ((Integer) variables.get("page")).intValue();
			this.filterstring = newFilter;
			requestRepaint();
			return;
		}

		// Try to set the property value

		// New option entered (and it is allowed)
		String newitem = (String) variables.get("newitem");
		if (newitem != null && newitem.length() > 0) {

			// Checks for readonly
			if (isReadOnly()) {
				throw new Property.ReadOnlyException();
			}

			// Adds new option
			if (addItem(newitem) != null) {

				// Sets the caption property, if used
				if (getItemCaptionPropertyId() != null) {
					try {
						getContainerProperty(newitem,
								getItemCaptionPropertyId()).setValue(newitem);
					} catch (Property.ConversionException ignored) {
						// The conversion exception is safely ignored, the
						// caption is
						// just missing
					}
				}
			}
		}

		// Selection change
		if (variables.containsKey("selected")) {
			String[] ka = (String[]) variables.get("selected");

			// Multiselect mode
			if (isMultiSelect()) {

				// TODO Optimize by adding repaintNotNeeded whan applicaple

				// Converts the key-array to id-set
				LinkedList s = new LinkedList();
				for (int i = 0; i < ka.length; i++) {
					Object id = this.itemIdMapper.get(ka[i]);
					if (id != null && containsId(id)) {
						s.add(id);
					} else if (this.itemIdMapper.isNewIdKey(ka[i])
							&& newitem != null && newitem.length() > 0) {
						s.add(newitem);
					}
				}

				// Limits the deselection to the set of visible items
				// (non-visible items can not be deselected)
				Collection visible = getVisibleItemIds();
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
			}

			// Single select mode
			else {
				if (ka.length == 0) {

					// Allows deselection only if the deselected item is visible
					Object current = getValue();
					Collection visible = getVisibleItemIds();
					if (visible != null && visible.contains(current)) {
						setValue(null, true);
					}
				} else {
					Object id = this.itemIdMapper.get(ka[0]);
					if (id != null && id.equals(getNullSelectionItemId())) {
						setValue(null, true);
					} else if (this.itemIdMapper.isNewIdKey(ka[0])) {
						setValue(newitem);
					} else {
						setValue(id, true);
					}
				}
			}
		}
	}

	/**
	 * Gets the component UIDL tag.
	 * 
	 * @return the Component UIDL tag as string.
	 */
	public String getTag() {
		return "select";
	}

	public void setFilteringMode(int filteringMode) {
		this.filteringMode = filteringMode;
	}

	public int getFilteringMode() {
		return this.filteringMode;
	}
}

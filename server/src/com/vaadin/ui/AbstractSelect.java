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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropTarget;
import com.vaadin.event.dd.TargetDetailsImpl;
import com.vaadin.event.dd.acceptcriteria.ClientSideCriterion;
import com.vaadin.event.dd.acceptcriteria.ContainsDataFlavor;
import com.vaadin.event.dd.acceptcriteria.TargetDetailIs;
import com.vaadin.server.KeyMapper;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.dd.VerticalDropLocation;

/**
 * <p>
 * A class representing a selection of items the user has selected in a UI. The
 * set of choices is presented as a set of {@link com.vaadin.data.Item}s in a
 * {@link com.vaadin.data.Container}.
 * </p>
 * 
 * <p>
 * A <code>Select</code> component may be in single- or multiselect mode.
 * Multiselect mode means that more than one item can be selected
 * simultaneously.
 * </p>
 * 
 * @author Vaadin Ltd.
 * @since 5.0
 */
@SuppressWarnings("serial")
// TODO currently cannot specify type more precisely in case of multi-select
public abstract class AbstractSelect extends AbstractField<Object> implements
        Container, Container.Viewer, Container.PropertySetChangeListener,
        Container.PropertySetChangeNotifier, Container.ItemSetChangeNotifier,
        Container.ItemSetChangeListener, LegacyComponent {

    public enum ItemCaptionMode {
        /**
         * Item caption mode: Item's ID's <code>String</code> representation is
         * used as caption.
         */
        ID,
        /**
         * Item caption mode: Item's <code>String</code> representation is used
         * as caption.
         */
        ITEM,
        /**
         * Item caption mode: Index of the item is used as caption. The index
         * mode can only be used with the containers implementing the
         * {@link com.vaadin.data.Container.Indexed} interface.
         */
        INDEX,
        /**
         * Item caption mode: If an Item has a caption it's used, if not, Item's
         * ID's <code>String</code> representation is used as caption. <b>This
         * is the default</b>.
         */
        EXPLICIT_DEFAULTS_ID,
        /**
         * Item caption mode: Captions must be explicitly specified.
         */
        EXPLICIT,
        /**
         * Item caption mode: Only icons are shown, captions are hidden.
         */
        ICON_ONLY,
        /**
         * Item caption mode: Item captions are read from property specified
         * with <code>setItemCaptionPropertyId</code>.
         */
        PROPERTY;
    }

    /**
     * @deprecated As of 7.0, use {@link ItemCaptionMode#ID} instead
     */
    @Deprecated
    public static final ItemCaptionMode ITEM_CAPTION_MODE_ID = ItemCaptionMode.ID;

    /**
     * @deprecated As of 7.0, use {@link ItemCaptionMode#ITEM} instead
     */
    @Deprecated
    public static final ItemCaptionMode ITEM_CAPTION_MODE_ITEM = ItemCaptionMode.ITEM;

    /**
     * @deprecated As of 7.0, use {@link ItemCaptionMode#INDEX} instead
     */
    @Deprecated
    public static final ItemCaptionMode ITEM_CAPTION_MODE_INDEX = ItemCaptionMode.INDEX;

    /**
     * @deprecated As of 7.0, use {@link ItemCaptionMode#EXPLICIT_DEFAULTS_ID}
     *             instead
     */
    @Deprecated
    public static final ItemCaptionMode ITEM_CAPTION_MODE_EXPLICIT_DEFAULTS_ID = ItemCaptionMode.EXPLICIT_DEFAULTS_ID;

    /**
     * @deprecated As of 7.0, use {@link ItemCaptionMode#EXPLICIT} instead
     */
    @Deprecated
    public static final ItemCaptionMode ITEM_CAPTION_MODE_EXPLICIT = ItemCaptionMode.EXPLICIT;

    /**
     * @deprecated As of 7.0, use {@link ItemCaptionMode#ICON_ONLY} instead
     */
    @Deprecated
    public static final ItemCaptionMode ITEM_CAPTION_MODE_ICON_ONLY = ItemCaptionMode.ICON_ONLY;

    /**
     * @deprecated As of 7.0, use {@link ItemCaptionMode#PROPERTY} instead
     */
    @Deprecated
    public static final ItemCaptionMode ITEM_CAPTION_MODE_PROPERTY = ItemCaptionMode.PROPERTY;

    /**
     * Interface for option filtering, used to filter options based on user
     * entered value. The value is matched to the item caption.
     * <code>FilteringMode.OFF</code> (0) turns the filtering off.
     * <code>FilteringMode.STARTSWITH</code> (1) matches from the start of the
     * caption. <code>FilteringMode.CONTAINS</code> (1) matches anywhere in the
     * caption.
     */
    public interface Filtering extends Serializable {

        /**
         * @deprecated As of 7.0, use {@link FilteringMode#OFF} instead
         */
        @Deprecated
        public static final FilteringMode FILTERINGMODE_OFF = FilteringMode.OFF;
        /**
         * @deprecated As of 7.0, use {@link FilteringMode#STARTSWITH} instead
         */
        @Deprecated
        public static final FilteringMode FILTERINGMODE_STARTSWITH = FilteringMode.STARTSWITH;
        /**
         * @deprecated As of 7.0, use {@link FilteringMode#CONTAINS} instead
         */
        @Deprecated
        public static final FilteringMode FILTERINGMODE_CONTAINS = FilteringMode.CONTAINS;

        /**
         * Sets the option filtering mode.
         * 
         * @param filteringMode
         *            the filtering mode to use
         */
        public void setFilteringMode(FilteringMode filteringMode);

        /**
         * Gets the current filtering mode.
         * 
         * @return the filtering mode in use
         */
        public FilteringMode getFilteringMode();

    }

    /**
     * Is the select in multiselect mode?
     */
    private boolean multiSelect = false;

    /**
     * Select options.
     */
    protected Container items;

    /**
     * Is the user allowed to add new options?
     */
    private boolean allowNewOptions;

    /**
     * Keymapper used to map key values.
     */
    protected KeyMapper<Object> itemIdMapper = new KeyMapper<Object>();

    /**
     * Item icons.
     */
    private final HashMap<Object, Resource> itemIcons = new HashMap<Object, Resource>();

    /**
     * Item captions.
     */
    private final HashMap<Object, String> itemCaptions = new HashMap<Object, String>();

    /**
     * Item caption mode.
     */
    private ItemCaptionMode itemCaptionMode = ItemCaptionMode.EXPLICIT_DEFAULTS_ID;

    /**
     * Item caption source property id.
     */
    private Object itemCaptionPropertyId = null;

    /**
     * Item icon source property id.
     */
    private Object itemIconPropertyId = null;

    /**
     * List of property set change event listeners.
     */
    private Set<Container.PropertySetChangeListener> propertySetEventListeners = null;

    /**
     * List of item set change event listeners.
     */
    private Set<Container.ItemSetChangeListener> itemSetEventListeners = null;

    /**
     * Item id that represents null selection of this select.
     * 
     * <p>
     * Data interface does not support nulls as item ids. Selecting the item
     * identified by this id is the same as selecting no items at all. This
     * setting only affects the single select mode.
     * </p>
     */
    private Object nullSelectionItemId = null;

    // Null (empty) selection is enabled by default
    private boolean nullSelectionAllowed = true;
    private NewItemHandler newItemHandler;

    // Caption (Item / Property) change listeners
    CaptionChangeListener captionChangeListener;

    /* Constructors */

    /**
     * Creates an empty Select. The caption is not used.
     */
    public AbstractSelect() {
        setContainerDataSource(new IndexedContainer());
    }

    /**
     * Creates an empty Select with caption.
     */
    public AbstractSelect(String caption) {
        setContainerDataSource(new IndexedContainer());
        setCaption(caption);
    }

    /**
     * Creates a new select that is connected to a data-source.
     * 
     * @param caption
     *            the Caption of the component.
     * @param dataSource
     *            the Container datasource to be selected from by this select.
     */
    public AbstractSelect(String caption, Container dataSource) {
        setCaption(caption);
        setContainerDataSource(dataSource);
    }

    /**
     * Creates a new select that is filled from a collection of option values.
     * 
     * @param caption
     *            the Caption of this field.
     * @param options
     *            the Collection containing the options.
     */
    public AbstractSelect(String caption, Collection<?> options) {

        // Creates the options container and add given options to it
        final Container c = new IndexedContainer();
        if (options != null) {
            for (final Iterator<?> i = options.iterator(); i.hasNext();) {
                c.addItem(i.next());
            }
        }

        setCaption(caption);
        setContainerDataSource(c);
    }

    /* Component methods */

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

        // Paints select attributes
        if (isMultiSelect()) {
            target.addAttribute("selectmode", "multi");
        }
        if (isNewItemsAllowed()) {
            target.addAttribute("allownewitem", true);
        }
        if (isNullSelectionAllowed()) {
            target.addAttribute("nullselect", true);
            if (getNullSelectionItemId() != null) {
                target.addAttribute("nullselectitem", true);
            }
        }

        // Constructs selected keys array
        String[] selectedKeys;
        if (isMultiSelect()) {
            selectedKeys = new String[((Set<?>) getValue()).size()];
        } else {
            selectedKeys = new String[(getValue() == null
                    && getNullSelectionItemId() == null ? 0 : 1)];
        }

        // ==
        // first remove all previous item/property listeners
        getCaptionChangeListener().clear();
        // Paints the options and create array of selected id keys

        target.startTag("options");
        int keyIndex = 0;
        // Support for external null selection item id
        final Collection<?> ids = getItemIds();
        if (isNullSelectionAllowed() && getNullSelectionItemId() != null
                && !ids.contains(getNullSelectionItemId())) {
            final Object id = getNullSelectionItemId();
            // Paints option
            target.startTag("so");
            paintItem(target, id);
            if (isSelected(id)) {
                selectedKeys[keyIndex++] = itemIdMapper.key(id);
            }
            target.endTag("so");
        }

        final Iterator<?> i = getItemIds().iterator();
        // Paints the available selection options from data source
        while (i.hasNext()) {
            // Gets the option attribute values
            final Object id = i.next();
            if (!isNullSelectionAllowed() && id != null
                    && id.equals(getNullSelectionItemId())) {
                // Remove item if it's the null selection item but null
                // selection is not allowed
                continue;
            }
            final String key = itemIdMapper.key(id);
            // add listener for each item, to cause repaint if an item changes
            getCaptionChangeListener().addNotifierForItem(id);
            target.startTag("so");
            paintItem(target, id);
            if (isSelected(id) && keyIndex < selectedKeys.length) {
                selectedKeys[keyIndex++] = key;
            }
            target.endTag("so");
        }
        target.endTag("options");
        // ==

        // Paint variables
        target.addVariable(this, "selected", selectedKeys);
        if (isNewItemsAllowed()) {
            target.addVariable(this, "newitem", "");
        }

    }

    protected void paintItem(PaintTarget target, Object itemId)
            throws PaintException {
        final String key = itemIdMapper.key(itemId);
        final String caption = getItemCaption(itemId);
        final Resource icon = getItemIcon(itemId);
        if (icon != null) {
            target.addAttribute("icon", icon);
        }
        target.addAttribute("caption", caption);
        if (itemId != null && itemId.equals(getNullSelectionItemId())) {
            target.addAttribute("nullselection", true);
        }
        target.addAttribute("key", key);
        if (isSelected(itemId)) {
            target.addAttribute("selected", true);
        }
    }

    /**
     * Invoked when the value of a variable has changed.
     * 
     * @see com.vaadin.ui.AbstractComponent#changeVariables(java.lang.Object,
     *      java.util.Map)
     */
    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {

        // New option entered (and it is allowed)
        if (isNewItemsAllowed()) {
            final String newitem = (String) variables.get("newitem");
            if (newitem != null && newitem.length() > 0) {
                getNewItemHandler().addNewItem(newitem);
            }
        }

        // Selection change
        if (variables.containsKey("selected")) {
            final String[] clientSideSelectedKeys = (String[]) variables
                    .get("selected");

            // Multiselect mode
            if (isMultiSelect()) {

                // TODO Optimize by adding repaintNotNeeded when applicable

                // Converts the key-array to id-set
                final LinkedList<Object> acceptedSelections = new LinkedList<Object>();
                for (int i = 0; i < clientSideSelectedKeys.length; i++) {
                    final Object id = itemIdMapper
                            .get(clientSideSelectedKeys[i]);
                    if (!isNullSelectionAllowed()
                            && (id == null || id == getNullSelectionItemId())) {
                        // skip empty selection if nullselection is not allowed
                        markAsDirty();
                    } else if (id != null && containsId(id)) {
                        acceptedSelections.add(id);
                    }
                }

                if (!isNullSelectionAllowed() && acceptedSelections.size() < 1) {
                    // empty selection not allowed, keep old value
                    markAsDirty();
                    return;
                }

                // Limits the deselection to the set of visible items
                // (non-visible items can not be deselected)
                Collection<?> visibleNotSelected = getVisibleItemIds();
                if (visibleNotSelected != null) {
                    visibleNotSelected = new HashSet<Object>(visibleNotSelected);
                    // Don't remove those that will be added to preserve order
                    visibleNotSelected.removeAll(acceptedSelections);

                    @SuppressWarnings("unchecked")
                    Set<Object> newsel = (Set<Object>) getValue();
                    if (newsel == null) {
                        newsel = new LinkedHashSet<Object>();
                    } else {
                        newsel = new LinkedHashSet<Object>(newsel);
                    }
                    newsel.removeAll(visibleNotSelected);
                    newsel.addAll(acceptedSelections);
                    setValue(newsel, true);
                }
            } else {
                // Single select mode
                if (!isNullSelectionAllowed()
                        && (clientSideSelectedKeys.length == 0
                                || clientSideSelectedKeys[0] == null || clientSideSelectedKeys[0] == getNullSelectionItemId())) {
                    markAsDirty();
                    return;
                }
                if (clientSideSelectedKeys.length == 0) {
                    // Allows deselection only if the deselected item is
                    // visible
                    final Object current = getValue();
                    final Collection<?> visible = getVisibleItemIds();
                    if (visible != null && visible.contains(current)) {
                        setValue(null, true);
                    }
                } else {
                    final Object id = itemIdMapper
                            .get(clientSideSelectedKeys[0]);
                    if (!isNullSelectionAllowed() && id == null) {
                        markAsDirty();
                    } else if (id != null
                            && id.equals(getNullSelectionItemId())) {
                        setValue(null, true);
                    } else {
                        setValue(id, true);
                    }
                }
            }
        }
    }

    /**
     * TODO refine doc Setter for new item handler that is called when user adds
     * new item in newItemAllowed mode.
     * 
     * @param newItemHandler
     */
    public void setNewItemHandler(NewItemHandler newItemHandler) {
        this.newItemHandler = newItemHandler;
    }

    /**
     * TODO refine doc
     * 
     * @return
     */
    public NewItemHandler getNewItemHandler() {
        if (newItemHandler == null) {
            newItemHandler = new DefaultNewItemHandler();
        }
        return newItemHandler;
    }

    public interface NewItemHandler extends Serializable {
        void addNewItem(String newItemCaption);
    }

    /**
     * TODO refine doc
     * 
     * This is a default class that handles adding new items that are typed by
     * user to selects container.
     * 
     * By extending this class one may implement some logic on new item addition
     * like database inserts.
     * 
     */
    public class DefaultNewItemHandler implements NewItemHandler {
        @Override
        public void addNewItem(String newItemCaption) {
            // Checks for readonly
            if (isReadOnly()) {
                throw new Property.ReadOnlyException();
            }

            // Adds new option
            if (addItem(newItemCaption) != null) {

                // Sets the caption property, if used
                if (getItemCaptionPropertyId() != null) {
                    getContainerProperty(newItemCaption,
                            getItemCaptionPropertyId())
                            .setValue(newItemCaption);
                }
                if (isMultiSelect()) {
                    Set values = new HashSet((Collection) getValue());
                    values.add(newItemCaption);
                    setValue(values);
                } else {
                    setValue(newItemCaption);
                }
            }
        }
    }

    /**
     * Gets the visible item ids. In Select, this returns list of all item ids,
     * but can be overriden in subclasses if they paint only part of the items
     * to the terminal or null if no items is visible.
     */
    public Collection<?> getVisibleItemIds() {
        return getItemIds();
    }

    /* Property methods */

    /**
     * Returns the type of the property. <code>getValue</code> and
     * <code>setValue</code> methods must be compatible with this type: one can
     * safely cast <code>getValue</code> to given type and pass any variable
     * assignable to this type as a parameter to <code>setValue</code>.
     * 
     * @return the Type of the property.
     */
    @Override
    public Class<?> getType() {
        if (isMultiSelect()) {
            return Set.class;
        } else {
            return Object.class;
        }
    }

    /**
     * Gets the selected item id or in multiselect mode a set of selected ids.
     * 
     * @see com.vaadin.ui.AbstractField#getValue()
     */
    @Override
    public Object getValue() {
        final Object retValue = super.getValue();

        if (isMultiSelect()) {

            // If the return value is not a set
            if (retValue == null) {
                return new HashSet<Object>();
            }
            if (retValue instanceof Set) {
                return Collections.unmodifiableSet((Set<?>) retValue);
            } else if (retValue instanceof Collection) {
                return new HashSet<Object>((Collection<?>) retValue);
            } else {
                final Set<Object> s = new HashSet<Object>();
                if (items.containsId(retValue)) {
                    s.add(retValue);
                }
                return s;
            }

        } else {
            return retValue;
        }
    }

    /**
     * Sets the visible value of the property.
     * 
     * <p>
     * The value of the select is the selected item id. If the select is in
     * multiselect-mode, the value is a set of selected item keys. In
     * multiselect mode all collections of id:s can be assigned.
     * </p>
     * 
     * @param newValue
     *            the New selected item or collection of selected items.
     * @see com.vaadin.ui.AbstractField#setValue(java.lang.Object)
     */
    @Override
    public void setValue(Object newValue) throws Property.ReadOnlyException {
        if (newValue == getNullSelectionItemId()) {
            newValue = null;
        }

        setValue(newValue, false);
    }

    /**
     * Sets the visible value of the property.
     * 
     * <p>
     * The value of the select is the selected item id. If the select is in
     * multiselect-mode, the value is a set of selected item keys. In
     * multiselect mode all collections of id:s can be assigned.
     * </p>
     * 
     * @param newValue
     *            the New selected item or collection of selected items.
     * @param repaintIsNotNeeded
     *            True if caller is sure that repaint is not needed.
     * @see com.vaadin.ui.AbstractField#setValue(java.lang.Object,
     *      java.lang.Boolean)
     */
    @Override
    protected void setValue(Object newValue, boolean repaintIsNotNeeded)
            throws Property.ReadOnlyException {

        if (isMultiSelect()) {
            if (newValue == null) {
                super.setValue(new LinkedHashSet<Object>(), repaintIsNotNeeded);
            } else if (Collection.class.isAssignableFrom(newValue.getClass())) {
                super.setValue(new LinkedHashSet<Object>(
                        (Collection<?>) newValue), repaintIsNotNeeded);
            }
        } else if (newValue == null || items.containsId(newValue)) {
            super.setValue(newValue, repaintIsNotNeeded);
        }
    }

    /* Container methods */

    /**
     * Gets the item from the container with given id. If the container does not
     * contain the requested item, null is returned.
     * 
     * @param itemId
     *            the item id.
     * @return the item from the container.
     */
    @Override
    public Item getItem(Object itemId) {
        return items.getItem(itemId);
    }

    /**
     * Gets the item Id collection from the container.
     * 
     * @return the Collection of item ids.
     */
    @Override
    public Collection<?> getItemIds() {
        return items.getItemIds();
    }

    /**
     * Gets the property Id collection from the container.
     * 
     * @return the Collection of property ids.
     */
    @Override
    public Collection<?> getContainerPropertyIds() {
        return items.getContainerPropertyIds();
    }

    /**
     * Gets the property type.
     * 
     * @param propertyId
     *            the Id identifying the property.
     * @see com.vaadin.data.Container#getType(java.lang.Object)
     */
    @Override
    public Class<?> getType(Object propertyId) {
        return items.getType(propertyId);
    }

    /*
     * Gets the number of items in the container.
     * 
     * @return the Number of items in the container.
     * 
     * @see com.vaadin.data.Container#size()
     */
    @Override
    public int size() {
        int size = items.size();
        assert size >= 0;
        return size;
    }

    /**
     * Tests, if the collection contains an item with given id.
     * 
     * @param itemId
     *            the Id the of item to be tested.
     */
    @Override
    public boolean containsId(Object itemId) {
        if (itemId != null) {
            return items.containsId(itemId);
        } else {
            return false;
        }
    }

    /**
     * Gets the Property identified by the given itemId and propertyId from the
     * Container
     * 
     * @see com.vaadin.data.Container#getContainerProperty(Object, Object)
     */
    @Override
    public Property getContainerProperty(Object itemId, Object propertyId) {
        return items.getContainerProperty(itemId, propertyId);
    }

    /**
     * Adds the new property to all items. Adds a property with given id, type
     * and default value to all items in the container.
     * 
     * This functionality is optional. If the function is unsupported, it always
     * returns false.
     * 
     * @return True if the operation succeeded.
     * @see com.vaadin.data.Container#addContainerProperty(java.lang.Object,
     *      java.lang.Class, java.lang.Object)
     */
    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type,
            Object defaultValue) throws UnsupportedOperationException {

        final boolean retval = items.addContainerProperty(propertyId, type,
                defaultValue);
        if (retval && !(items instanceof Container.PropertySetChangeNotifier)) {
            firePropertySetChange();
        }
        return retval;
    }

    /**
     * Removes all items from the container.
     * 
     * This functionality is optional. If the function is unsupported, it always
     * returns false.
     * 
     * @return True if the operation succeeded.
     * @see com.vaadin.data.Container#removeAllItems()
     */
    @Override
    public boolean removeAllItems() throws UnsupportedOperationException {

        final boolean retval = items.removeAllItems();
        itemIdMapper.removeAll();
        if (retval) {
            setValue(null);
            if (!(items instanceof Container.ItemSetChangeNotifier)) {
                fireItemSetChange();
            }
        }
        return retval;
    }

    /**
     * Creates a new item into container with container managed id. The id of
     * the created new item is returned. The item can be fetched with getItem()
     * method. if the creation fails, null is returned.
     * 
     * @return the Id of the created item or null in case of failure.
     * @see com.vaadin.data.Container#addItem()
     */
    @Override
    public Object addItem() throws UnsupportedOperationException {

        final Object retval = items.addItem();
        if (retval != null
                && !(items instanceof Container.ItemSetChangeNotifier)) {
            fireItemSetChange();
        }
        return retval;
    }

    /**
     * Create a new item into container. The created new item is returned and
     * ready for setting property values. if the creation fails, null is
     * returned. In case the container already contains the item, null is
     * returned.
     * 
     * This functionality is optional. If the function is unsupported, it always
     * returns null.
     * 
     * @param itemId
     *            the Identification of the item to be created.
     * @return the Created item with the given id, or null in case of failure.
     * @see com.vaadin.data.Container#addItem(java.lang.Object)
     */
    @Override
    public Item addItem(Object itemId) throws UnsupportedOperationException {

        final Item retval = items.addItem(itemId);
        if (retval != null
                && !(items instanceof Container.ItemSetChangeNotifier)) {
            fireItemSetChange();
        }
        return retval;
    }

    /**
     * Adds given items with given item ids to container.
     * 
     * @since 7.2
     * @param itemId
     *            item identifiers to be added to underlying container
     * @throws UnsupportedOperationException
     *             if the underlying container don't support adding items with
     *             identifiers
     */
    public void addItems(Object... itemId) throws UnsupportedOperationException {
        for (Object id : itemId) {
            addItem(id);
        }
    }

    /**
     * Adds given items with given item ids to container.
     * 
     * @since 7.2
     * @param itemIds
     *            item identifiers to be added to underlying container
     * @throws UnsupportedOperationException
     *             if the underlying container don't support adding items with
     *             identifiers
     */
    public void addItems(Collection<?> itemIds)
            throws UnsupportedOperationException {
        addItems(itemIds.toArray());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#removeItem(java.lang.Object)
     */
    @Override
    public boolean removeItem(Object itemId)
            throws UnsupportedOperationException {

        unselect(itemId);
        final boolean retval = items.removeItem(itemId);
        itemIdMapper.remove(itemId);
        if (retval && !(items instanceof Container.ItemSetChangeNotifier)) {
            fireItemSetChange();
        }
        return retval;
    }

    /**
     * Checks that the current selection is valid, i.e. the selected item ids
     * exist in the container. Updates the selection if one or several selected
     * item ids are no longer available in the container.
     */
    @SuppressWarnings("unchecked")
    public void sanitizeSelection() {
        Object value = getValue();
        if (value == null) {
            return;
        }

        boolean changed = false;

        if (isMultiSelect()) {
            Collection<Object> valueAsCollection = (Collection<Object>) value;
            List<Object> newSelection = new ArrayList<Object>(
                    valueAsCollection.size());
            for (Object subValue : valueAsCollection) {
                if (containsId(subValue)) {
                    newSelection.add(subValue);
                } else {
                    changed = true;
                }
            }
            if (changed) {
                setValue(newSelection);
            }
        } else {
            if (!containsId(value)) {
                setValue(null);
            }
        }

    }

    /**
     * Removes the property from all items. Removes a property with given id
     * from all the items in the container.
     * 
     * This functionality is optional. If the function is unsupported, it always
     * returns false.
     * 
     * @return True if the operation succeeded.
     * @see com.vaadin.data.Container#removeContainerProperty(java.lang.Object)
     */
    @Override
    public boolean removeContainerProperty(Object propertyId)
            throws UnsupportedOperationException {

        final boolean retval = items.removeContainerProperty(propertyId);
        if (retval && !(items instanceof Container.PropertySetChangeNotifier)) {
            firePropertySetChange();
        }
        return retval;
    }

    /* Container.Viewer methods */

    /**
     * Sets the Container that serves as the data source of the viewer.
     * 
     * As a side-effect the fields value (selection) is set to null due old
     * selection not necessary exists in new Container.
     * 
     * @see com.vaadin.data.Container.Viewer#setContainerDataSource(Container)
     * 
     * @param newDataSource
     *            the new data source.
     */
    @Override
    public void setContainerDataSource(Container newDataSource) {
        if (newDataSource == null) {
            newDataSource = new IndexedContainer();
        }

        getCaptionChangeListener().clear();

        if (items != newDataSource) {

            // Removes listeners from the old datasource
            if (items != null) {
                if (items instanceof Container.ItemSetChangeNotifier) {
                    ((Container.ItemSetChangeNotifier) items)
                            .removeItemSetChangeListener(this);
                }
                if (items instanceof Container.PropertySetChangeNotifier) {
                    ((Container.PropertySetChangeNotifier) items)
                            .removePropertySetChangeListener(this);
                }
            }

            // Assigns new data source
            items = newDataSource;

            // Clears itemIdMapper also
            itemIdMapper.removeAll();

            // Adds listeners
            if (items != null) {
                if (items instanceof Container.ItemSetChangeNotifier) {
                    ((Container.ItemSetChangeNotifier) items)
                            .addItemSetChangeListener(this);
                }
                if (items instanceof Container.PropertySetChangeNotifier) {
                    ((Container.PropertySetChangeNotifier) items)
                            .addPropertySetChangeListener(this);
                }
            }

            /*
             * We expect changing the data source should also clean value. See
             * #810, #4607, #5281
             */
            setValue(null);

            markAsDirty();

        }
    }

    /**
     * Gets the viewing data-source container.
     * 
     * @see com.vaadin.data.Container.Viewer#getContainerDataSource()
     */
    @Override
    public Container getContainerDataSource() {
        return items;
    }

    /* Select attributes */

    /**
     * Is the select in multiselect mode? In multiselect mode
     * 
     * @return the Value of property multiSelect.
     */
    public boolean isMultiSelect() {
        return multiSelect;
    }

    /**
     * Sets the multiselect mode. Setting multiselect mode false may lose
     * selection information: if selected items set contains one or more
     * selected items, only one of the selected items is kept as selected.
     * 
     * Subclasses of AbstractSelect can choose not to support changing the
     * multiselect mode, and may throw {@link UnsupportedOperationException}.
     * 
     * @param multiSelect
     *            the New value of property multiSelect.
     */
    public void setMultiSelect(boolean multiSelect) {
        if (multiSelect && getNullSelectionItemId() != null) {
            throw new IllegalStateException(
                    "Multiselect and NullSelectionItemId can not be set at the same time.");
        }
        if (multiSelect != this.multiSelect) {

            // Selection before mode change
            final Object oldValue = getValue();

            this.multiSelect = multiSelect;

            // Convert the value type
            if (multiSelect) {
                final Set<Object> s = new HashSet<Object>();
                if (oldValue != null) {
                    s.add(oldValue);
                }
                setValue(s);
            } else {
                final Set<?> s = (Set<?>) oldValue;
                if (s == null || s.isEmpty()) {
                    setValue(null);
                } else {
                    // Set the single select to contain only the first
                    // selected value in the multiselect
                    setValue(s.iterator().next());
                }
            }

            markAsDirty();
        }
    }

    /**
     * Does the select allow adding new options by the user. If true, the new
     * options can be added to the Container. The text entered by the user is
     * used as id. Note that data-source must allow adding new items.
     * 
     * @return True if additions are allowed.
     */
    public boolean isNewItemsAllowed() {
        return allowNewOptions;
    }

    /**
     * Enables or disables possibility to add new options by the user.
     * 
     * @param allowNewOptions
     *            the New value of property allowNewOptions.
     */
    public void setNewItemsAllowed(boolean allowNewOptions) {

        // Only handle change requests
        if (this.allowNewOptions != allowNewOptions) {

            this.allowNewOptions = allowNewOptions;

            markAsDirty();
        }
    }

    /**
     * Override the caption of an item. Setting caption explicitly overrides id,
     * item and index captions.
     * 
     * @param itemId
     *            the id of the item to be recaptioned.
     * @param caption
     *            the New caption.
     */
    public void setItemCaption(Object itemId, String caption) {
        if (itemId != null) {
            itemCaptions.put(itemId, caption);
            markAsDirty();
        }
    }

    /**
     * Gets the caption of an item. The caption is generated as specified by the
     * item caption mode. See <code>setItemCaptionMode()</code> for more
     * details.
     * 
     * @param itemId
     *            the id of the item to be queried.
     * @return the caption for specified item.
     */
    public String getItemCaption(Object itemId) {

        // Null items can not be found
        if (itemId == null) {
            return null;
        }

        String caption = null;

        switch (getItemCaptionMode()) {

        case ID:
            caption = itemId.toString();
            break;

        case INDEX:
            if (items instanceof Container.Indexed) {
                caption = String.valueOf(((Container.Indexed) items)
                        .indexOfId(itemId));
            } else {
                caption = "ERROR: Container is not indexed";
            }
            break;

        case ITEM:
            final Item i = getItem(itemId);
            if (i != null) {
                caption = i.toString();
            }
            break;

        case EXPLICIT:
            caption = itemCaptions.get(itemId);
            break;

        case EXPLICIT_DEFAULTS_ID:
            caption = itemCaptions.get(itemId);
            if (caption == null) {
                caption = itemId.toString();
            }
            break;

        case PROPERTY:
            final Property<?> p = getContainerProperty(itemId,
                    getItemCaptionPropertyId());
            if (p != null) {
                Object value = p.getValue();
                if (value != null) {
                    caption = value.toString();
                }
            }
            break;
        }

        // All items must have some captions
        return caption != null ? caption : "";
    }

    /**
     * Sets tqhe icon for an item.
     * 
     * @param itemId
     *            the id of the item to be assigned an icon.
     * @param icon
     *            the icon to use or null.
     */
    public void setItemIcon(Object itemId, Resource icon) {
        if (itemId != null) {
            if (icon == null) {
                itemIcons.remove(itemId);
            } else {
                itemIcons.put(itemId, icon);
            }
            markAsDirty();
        }
    }

    /**
     * Gets the item icon.
     * 
     * @param itemId
     *            the id of the item to be assigned an icon.
     * @return the icon for the item or null, if not specified.
     */
    public Resource getItemIcon(Object itemId) {
        final Resource explicit = itemIcons.get(itemId);
        if (explicit != null) {
            return explicit;
        }

        if (getItemIconPropertyId() == null) {
            return null;
        }

        final Property<?> ip = getContainerProperty(itemId,
                getItemIconPropertyId());
        if (ip == null) {
            return null;
        }
        final Object icon = ip.getValue();
        if (icon instanceof Resource) {
            return (Resource) icon;
        }

        return null;
    }

    /**
     * Sets the item caption mode.
     * 
     * <p>
     * The mode can be one of the following ones:
     * <ul>
     * <li><code>ITEM_CAPTION_MODE_EXPLICIT_DEFAULTS_ID</code> : Items
     * Id-objects <code>toString</code> is used as item caption. If caption is
     * explicitly specified, it overrides the id-caption.
     * <li><code>ITEM_CAPTION_MODE_ID</code> : Items Id-objects
     * <code>toString</code> is used as item caption.</li>
     * <li><code>ITEM_CAPTION_MODE_ITEM</code> : Item-objects
     * <code>toString</code> is used as item caption.</li>
     * <li><code>ITEM_CAPTION_MODE_INDEX</code> : The index of the item is used
     * as item caption. The index mode can only be used with the containers
     * implementing <code>Container.Indexed</code> interface.</li>
     * <li><code>ITEM_CAPTION_MODE_EXPLICIT</code> : The item captions must be
     * explicitly specified.</li>
     * <li><code>ITEM_CAPTION_MODE_PROPERTY</code> : The item captions are read
     * from property, that must be specified with
     * <code>setItemCaptionPropertyId</code>.</li>
     * </ul>
     * The <code>ITEM_CAPTION_MODE_EXPLICIT_DEFAULTS_ID</code> is the default
     * mode.
     * </p>
     * 
     * @param mode
     *            the One of the modes listed above.
     */
    public void setItemCaptionMode(ItemCaptionMode mode) {
        if (mode != null) {
            itemCaptionMode = mode;
            markAsDirty();
        }
    }

    /**
     * Gets the item caption mode.
     * 
     * <p>
     * The mode can be one of the following ones:
     * <ul>
     * <li><code>ITEM_CAPTION_MODE_EXPLICIT_DEFAULTS_ID</code> : Items
     * Id-objects <code>toString</code> is used as item caption. If caption is
     * explicitly specified, it overrides the id-caption.
     * <li><code>ITEM_CAPTION_MODE_ID</code> : Items Id-objects
     * <code>toString</code> is used as item caption.</li>
     * <li><code>ITEM_CAPTION_MODE_ITEM</code> : Item-objects
     * <code>toString</code> is used as item caption.</li>
     * <li><code>ITEM_CAPTION_MODE_INDEX</code> : The index of the item is used
     * as item caption. The index mode can only be used with the containers
     * implementing <code>Container.Indexed</code> interface.</li>
     * <li><code>ITEM_CAPTION_MODE_EXPLICIT</code> : The item captions must be
     * explicitly specified.</li>
     * <li><code>ITEM_CAPTION_MODE_PROPERTY</code> : The item captions are read
     * from property, that must be specified with
     * <code>setItemCaptionPropertyId</code>.</li>
     * </ul>
     * The <code>ITEM_CAPTION_MODE_EXPLICIT_DEFAULTS_ID</code> is the default
     * mode.
     * </p>
     * 
     * @return the One of the modes listed above.
     */
    public ItemCaptionMode getItemCaptionMode() {
        return itemCaptionMode;
    }

    /**
     * Sets the item caption property.
     * 
     * <p>
     * Setting the id to a existing property implicitly sets the item caption
     * mode to <code>ITEM_CAPTION_MODE_PROPERTY</code>. If the object is in
     * <code>ITEM_CAPTION_MODE_PROPERTY</code> mode, setting caption property id
     * null resets the item caption mode to
     * <code>ITEM_CAPTION_EXPLICIT_DEFAULTS_ID</code>.
     * </p>
     * <p>
     * Note that the type of the property used for caption must be String
     * </p>
     * <p>
     * Setting the property id to null disables this feature. The id is null by
     * default
     * </p>
     * .
     * 
     * @param propertyId
     *            the id of the property.
     * 
     */
    public void setItemCaptionPropertyId(Object propertyId) {
        if (propertyId != null) {
            itemCaptionPropertyId = propertyId;
            setItemCaptionMode(ITEM_CAPTION_MODE_PROPERTY);
            markAsDirty();
        } else {
            itemCaptionPropertyId = null;
            if (getItemCaptionMode() == ITEM_CAPTION_MODE_PROPERTY) {
                setItemCaptionMode(ITEM_CAPTION_MODE_EXPLICIT_DEFAULTS_ID);
            }
            markAsDirty();
        }
    }

    /**
     * Gets the item caption property.
     * 
     * @return the Id of the property used as item caption source.
     */
    public Object getItemCaptionPropertyId() {
        return itemCaptionPropertyId;
    }

    /**
     * Sets the item icon property.
     * 
     * <p>
     * If the property id is set to a valid value, each item is given an icon
     * got from the given property of the items. The type of the property must
     * be assignable to Resource.
     * </p>
     * 
     * <p>
     * Note : The icons set with <code>setItemIcon</code> function override the
     * icons from the property.
     * </p>
     * 
     * <p>
     * Setting the property id to null disables this feature. The id is null by
     * default
     * </p>
     * .
     * 
     * @param propertyId
     *            the id of the property that specifies icons for items or null
     * @throws IllegalArgumentException
     *             If the propertyId is not in the container or is not of a
     *             valid type
     */
    public void setItemIconPropertyId(Object propertyId)
            throws IllegalArgumentException {
        if (propertyId == null) {
            itemIconPropertyId = null;
        } else if (!getContainerPropertyIds().contains(propertyId)) {
            throw new IllegalArgumentException(
                    "Property id not found in the container");
        } else if (Resource.class.isAssignableFrom(getType(propertyId))) {
            itemIconPropertyId = propertyId;
        } else {
            throw new IllegalArgumentException(
                    "Property type must be assignable to Resource");
        }
        markAsDirty();
    }

    /**
     * Gets the item icon property.
     * 
     * <p>
     * If the property id is set to a valid value, each item is given an icon
     * got from the given property of the items. The type of the property must
     * be assignable to Icon.
     * </p>
     * 
     * <p>
     * Note : The icons set with <code>setItemIcon</code> function override the
     * icons from the property.
     * </p>
     * 
     * <p>
     * Setting the property id to null disables this feature. The id is null by
     * default
     * </p>
     * .
     * 
     * @return the Id of the property containing the item icons.
     */
    public Object getItemIconPropertyId() {
        return itemIconPropertyId;
    }

    /**
     * Tests if an item is selected.
     * 
     * <p>
     * In single select mode testing selection status of the item identified by
     * {@link #getNullSelectionItemId()} returns true if the value of the
     * property is null.
     * </p>
     * 
     * @param itemId
     *            the Id the of the item to be tested.
     * @see #getNullSelectionItemId()
     * @see #setNullSelectionItemId(Object)
     * 
     */
    public boolean isSelected(Object itemId) {
        if (itemId == null) {
            return false;
        }
        if (isMultiSelect()) {
            return ((Set<?>) getValue()).contains(itemId);
        } else {
            final Object value = getValue();
            return itemId.equals(value == null ? getNullSelectionItemId()
                    : value);
        }
    }

    /**
     * Selects an item.
     * 
     * <p>
     * In single select mode selecting item identified by
     * {@link #getNullSelectionItemId()} sets the value of the property to null.
     * </p>
     * 
     * @param itemId
     *            the identifier of Item to be selected.
     * @see #getNullSelectionItemId()
     * @see #setNullSelectionItemId(Object)
     * 
     */
    public void select(Object itemId) {
        if (!isMultiSelect()) {
            setValue(itemId);
        } else if (!isSelected(itemId) && itemId != null
                && items.containsId(itemId)) {
            final Set<Object> s = new HashSet<Object>((Set<?>) getValue());
            s.add(itemId);
            setValue(s);
        }
    }

    /**
     * Unselects an item.
     * 
     * @param itemId
     *            the identifier of the Item to be unselected.
     * @see #getNullSelectionItemId()
     * @see #setNullSelectionItemId(Object)
     * 
     */
    public void unselect(Object itemId) {
        if (isSelected(itemId)) {
            if (isMultiSelect()) {
                final Set<Object> s = new HashSet<Object>((Set<?>) getValue());
                s.remove(itemId);
                setValue(s);
            } else {
                setValue(null);
            }
        }
    }

    /**
     * Notifies this listener that the Containers contents has changed.
     * 
     * @see com.vaadin.data.Container.PropertySetChangeListener#containerPropertySetChange(com.vaadin.data.Container.PropertySetChangeEvent)
     */
    @Override
    public void containerPropertySetChange(
            Container.PropertySetChangeEvent event) {
        firePropertySetChange();
    }

    /**
     * Adds a new Property set change listener for this Container.
     * 
     * @see com.vaadin.data.Container.PropertySetChangeNotifier#addListener(com.vaadin.data.Container.PropertySetChangeListener)
     */
    @Override
    public void addPropertySetChangeListener(
            Container.PropertySetChangeListener listener) {
        if (propertySetEventListeners == null) {
            propertySetEventListeners = new LinkedHashSet<Container.PropertySetChangeListener>();
        }
        propertySetEventListeners.add(listener);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addPropertySetChangeListener(com.vaadin.data.Container.PropertySetChangeListener)}
     **/
    @Override
    @Deprecated
    public void addListener(Container.PropertySetChangeListener listener) {
        addPropertySetChangeListener(listener);
    }

    /**
     * Removes a previously registered Property set change listener.
     * 
     * @see com.vaadin.data.Container.PropertySetChangeNotifier#removeListener(com.vaadin.data.Container.PropertySetChangeListener)
     */
    @Override
    public void removePropertySetChangeListener(
            Container.PropertySetChangeListener listener) {
        if (propertySetEventListeners != null) {
            propertySetEventListeners.remove(listener);
            if (propertySetEventListeners.isEmpty()) {
                propertySetEventListeners = null;
            }
        }
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removePropertySetChangeListener(com.vaadin.data.Container.PropertySetChangeListener)}
     **/
    @Override
    @Deprecated
    public void removeListener(Container.PropertySetChangeListener listener) {
        removePropertySetChangeListener(listener);
    }

    /**
     * Adds an Item set change listener for the object.
     * 
     * @see com.vaadin.data.Container.ItemSetChangeNotifier#addListener(com.vaadin.data.Container.ItemSetChangeListener)
     */
    @Override
    public void addItemSetChangeListener(
            Container.ItemSetChangeListener listener) {
        if (itemSetEventListeners == null) {
            itemSetEventListeners = new LinkedHashSet<Container.ItemSetChangeListener>();
        }
        itemSetEventListeners.add(listener);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addItemSetChangeListener(com.vaadin.data.Container.ItemSetChangeListener)}
     **/
    @Override
    @Deprecated
    public void addListener(Container.ItemSetChangeListener listener) {
        addItemSetChangeListener(listener);
    }

    /**
     * Removes the Item set change listener from the object.
     * 
     * @see com.vaadin.data.Container.ItemSetChangeNotifier#removeListener(com.vaadin.data.Container.ItemSetChangeListener)
     */
    @Override
    public void removeItemSetChangeListener(
            Container.ItemSetChangeListener listener) {
        if (itemSetEventListeners != null) {
            itemSetEventListeners.remove(listener);
            if (itemSetEventListeners.isEmpty()) {
                itemSetEventListeners = null;
            }
        }
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeItemSetChangeListener(com.vaadin.data.Container.ItemSetChangeListener)}
     **/
    @Override
    @Deprecated
    public void removeListener(Container.ItemSetChangeListener listener) {
        removeItemSetChangeListener(listener);
    }

    @Override
    public Collection<?> getListeners(Class<?> eventType) {
        if (Container.ItemSetChangeEvent.class.isAssignableFrom(eventType)) {
            if (itemSetEventListeners == null) {
                return Collections.EMPTY_LIST;
            } else {
                return Collections
                        .unmodifiableCollection(itemSetEventListeners);
            }
        } else if (Container.PropertySetChangeEvent.class
                .isAssignableFrom(eventType)) {
            if (propertySetEventListeners == null) {
                return Collections.EMPTY_LIST;
            } else {
                return Collections
                        .unmodifiableCollection(propertySetEventListeners);
            }
        }

        return super.getListeners(eventType);
    }

    /**
     * Lets the listener know a Containers Item set has changed.
     * 
     * @see com.vaadin.data.Container.ItemSetChangeListener#containerItemSetChange(com.vaadin.data.Container.ItemSetChangeEvent)
     */
    @Override
    public void containerItemSetChange(Container.ItemSetChangeEvent event) {
        // Clears the item id mapping table
        itemIdMapper.removeAll();

        // Notify all listeners
        fireItemSetChange();
    }

    /**
     * Fires the property set change event.
     */
    protected void firePropertySetChange() {
        if (propertySetEventListeners != null
                && !propertySetEventListeners.isEmpty()) {
            final Container.PropertySetChangeEvent event = new PropertySetChangeEvent(
                    this);
            final Object[] listeners = propertySetEventListeners.toArray();
            for (int i = 0; i < listeners.length; i++) {
                ((Container.PropertySetChangeListener) listeners[i])
                        .containerPropertySetChange(event);
            }
        }
        markAsDirty();
    }

    /**
     * Fires the item set change event.
     */
    protected void fireItemSetChange() {
        if (itemSetEventListeners != null && !itemSetEventListeners.isEmpty()) {
            final Container.ItemSetChangeEvent event = new ItemSetChangeEvent(
                    this);
            final Object[] listeners = itemSetEventListeners.toArray();
            for (int i = 0; i < listeners.length; i++) {
                ((Container.ItemSetChangeListener) listeners[i])
                        .containerItemSetChange(event);
            }
        }
        markAsDirty();
    }

    /**
     * Implementation of item set change event.
     */
    private static class ItemSetChangeEvent extends EventObject implements
            Serializable, Container.ItemSetChangeEvent {

        private ItemSetChangeEvent(Container source) {
            super(source);
        }

        /**
         * Gets the Property where the event occurred.
         * 
         * @see com.vaadin.data.Container.ItemSetChangeEvent#getContainer()
         */
        @Override
        public Container getContainer() {
            return (Container) getSource();
        }

    }

    /**
     * Implementation of property set change event.
     */
    private static class PropertySetChangeEvent extends EventObject implements
            Container.PropertySetChangeEvent, Serializable {

        private PropertySetChangeEvent(Container source) {
            super(source);
        }

        /**
         * Retrieves the Container whose contents have been modified.
         * 
         * @see com.vaadin.data.Container.PropertySetChangeEvent#getContainer()
         */
        @Override
        public Container getContainer() {
            return (Container) getSource();
        }

    }

    /**
     * For multi-selectable fields, also an empty collection of values is
     * considered to be an empty field.
     * 
     * @see AbstractField#isEmpty().
     */
    @Override
    protected boolean isEmpty() {
        if (!multiSelect) {
            return super.isEmpty();
        } else {
            Object value = getValue();
            return super.isEmpty()
                    || (value instanceof Collection && ((Collection<?>) value)
                            .isEmpty());
        }
    }

    /**
     * Allow or disallow empty selection by the user. If the select is in
     * single-select mode, you can make an item represent the empty selection by
     * calling <code>setNullSelectionItemId()</code>. This way you can for
     * instance set an icon and caption for the null selection item.
     * 
     * @param nullSelectionAllowed
     *            whether or not to allow empty selection
     * @see #setNullSelectionItemId(Object)
     * @see #isNullSelectionAllowed()
     */
    public void setNullSelectionAllowed(boolean nullSelectionAllowed) {
        if (nullSelectionAllowed != this.nullSelectionAllowed) {
            this.nullSelectionAllowed = nullSelectionAllowed;
            markAsDirty();
        }
    }

    /**
     * Checks if null empty selection is allowed by the user.
     * 
     * @return whether or not empty selection is allowed
     * @see #setNullSelectionAllowed(boolean)
     */
    public boolean isNullSelectionAllowed() {
        return nullSelectionAllowed;
    }

    /**
     * Returns the item id that represents null value of this select in single
     * select mode.
     * 
     * <p>
     * Data interface does not support nulls as item ids. Selecting the item
     * identified by this id is the same as selecting no items at all. This
     * setting only affects the single select mode.
     * </p>
     * 
     * @return the Object Null value item id.
     * @see #setNullSelectionItemId(Object)
     * @see #isSelected(Object)
     * @see #select(Object)
     */
    public Object getNullSelectionItemId() {
        return nullSelectionItemId;
    }

    /**
     * Sets the item id that represents null value of this select.
     * 
     * <p>
     * Data interface does not support nulls as item ids. Selecting the item
     * identified by this id is the same as selecting no items at all. This
     * setting only affects the single select mode.
     * </p>
     * 
     * @param nullSelectionItemId
     *            the nullSelectionItemId to set.
     * @see #getNullSelectionItemId()
     * @see #isSelected(Object)
     * @see #select(Object)
     */
    public void setNullSelectionItemId(Object nullSelectionItemId) {
        if (nullSelectionItemId != null && isMultiSelect()) {
            throw new IllegalStateException(
                    "Multiselect and NullSelectionItemId can not be set at the same time.");
        }
        this.nullSelectionItemId = nullSelectionItemId;
    }

    /**
     * Notifies the component that it is connected to an application.
     * 
     * @see com.vaadin.ui.AbstractField#attach()
     */
    @Override
    public void attach() {
        super.attach();
    }

    /**
     * Detaches the component from application.
     * 
     * @see com.vaadin.ui.AbstractComponent#detach()
     */
    @Override
    public void detach() {
        getCaptionChangeListener().clear();
        super.detach();
    }

    // Caption change listener
    protected CaptionChangeListener getCaptionChangeListener() {
        if (captionChangeListener == null) {
            captionChangeListener = new CaptionChangeListener();
        }
        return captionChangeListener;
    }

    /**
     * This is a listener helper for Item and Property changes that should cause
     * a repaint. It should be attached to all items that are displayed, and the
     * default implementation does this in paintContent(). Especially
     * "lazyloading" components should take care to add and remove listeners as
     * appropriate. Call addNotifierForItem() for each painted item (and
     * remember to clear).
     * 
     * NOTE: singleton, use getCaptionChangeListener().
     * 
     */
    protected class CaptionChangeListener implements
            Item.PropertySetChangeListener, Property.ValueChangeListener {

        // TODO clean this up - type is either Item.PropertySetChangeNotifier or
        // Property.ValueChangeNotifier
        HashSet<Object> captionChangeNotifiers = new HashSet<Object>();

        public void addNotifierForItem(Object itemId) {
            switch (getItemCaptionMode()) {
            case ITEM:
                final Item i = getItem(itemId);
                if (i == null) {
                    return;
                }
                if (i instanceof Item.PropertySetChangeNotifier) {
                    ((Item.PropertySetChangeNotifier) i)
                            .addPropertySetChangeListener(getCaptionChangeListener());
                    captionChangeNotifiers.add(i);
                }
                Collection<?> pids = i.getItemPropertyIds();
                if (pids != null) {
                    for (Iterator<?> it = pids.iterator(); it.hasNext();) {
                        Property<?> p = i.getItemProperty(it.next());
                        if (p != null
                                && p instanceof Property.ValueChangeNotifier) {
                            ((Property.ValueChangeNotifier) p)
                                    .addValueChangeListener(getCaptionChangeListener());
                            captionChangeNotifiers.add(p);
                        }
                    }

                }
                break;
            case PROPERTY:
                final Property<?> p = getContainerProperty(itemId,
                        getItemCaptionPropertyId());
                if (p != null && p instanceof Property.ValueChangeNotifier) {
                    ((Property.ValueChangeNotifier) p)
                            .addValueChangeListener(getCaptionChangeListener());
                    captionChangeNotifiers.add(p);
                }
                break;

            }
            if (getItemIconPropertyId() != null) {
                final Property p = getContainerProperty(itemId,
                        getItemIconPropertyId());
                if (p != null && p instanceof Property.ValueChangeNotifier) {
                    ((Property.ValueChangeNotifier) p)
                            .addValueChangeListener(getCaptionChangeListener());
                    captionChangeNotifiers.add(p);
                }
            }
        }

        public void clear() {
            for (Iterator<Object> it = captionChangeNotifiers.iterator(); it
                    .hasNext();) {
                Object notifier = it.next();
                if (notifier instanceof Item.PropertySetChangeNotifier) {
                    ((Item.PropertySetChangeNotifier) notifier)
                            .removePropertySetChangeListener(getCaptionChangeListener());
                } else {
                    ((Property.ValueChangeNotifier) notifier)
                            .removeValueChangeListener(getCaptionChangeListener());
                }
            }
            captionChangeNotifiers.clear();
        }

        @Override
        public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
            markAsDirty();
        }

        @Override
        public void itemPropertySetChange(
                com.vaadin.data.Item.PropertySetChangeEvent event) {
            markAsDirty();
        }

    }

    /**
     * Criterion which accepts a drop only if the drop target is (one of) the
     * given Item identifier(s). Criterion can be used only on a drop targets
     * that extends AbstractSelect like {@link Table} and {@link Tree}. The
     * target and identifiers of valid Items are given in constructor.
     * 
     * @since 6.3
     */
    public static class TargetItemIs extends AbstractItemSetCriterion {

        /**
         * @param select
         *            the select implementation that is used as a drop target
         * @param itemId
         *            the identifier(s) that are valid drop locations
         */
        public TargetItemIs(AbstractSelect select, Object... itemId) {
            super(select, itemId);
        }

        @Override
        public boolean accept(DragAndDropEvent dragEvent) {
            AbstractSelectTargetDetails dropTargetData = (AbstractSelectTargetDetails) dragEvent
                    .getTargetDetails();
            if (dropTargetData.getTarget() != select) {
                return false;
            }
            return itemIds.contains(dropTargetData.getItemIdOver());
        }

    }

    /**
     * Abstract helper class to implement item id based criterion.
     * 
     * Note, inner class used not to open itemIdMapper for public access.
     * 
     * @since 6.3
     * 
     */
    private static abstract class AbstractItemSetCriterion extends
            ClientSideCriterion {
        protected final Collection<Object> itemIds = new HashSet<Object>();
        protected AbstractSelect select;

        public AbstractItemSetCriterion(AbstractSelect select, Object... itemId) {
            if (itemIds == null || select == null) {
                throw new IllegalArgumentException(
                        "Accepted item identifiers must be accepted.");
            }
            Collections.addAll(itemIds, itemId);
            this.select = select;
        }

        @Override
        public void paintContent(PaintTarget target) throws PaintException {
            super.paintContent(target);
            String[] keys = new String[itemIds.size()];
            int i = 0;
            for (Object itemId : itemIds) {
                String key = select.itemIdMapper.key(itemId);
                keys[i++] = key;
            }
            target.addAttribute("keys", keys);
            target.addAttribute("s", select);
        }

    }

    /**
     * This criterion accepts a only a {@link Transferable} that contains given
     * Item (practically its identifier) from a specific AbstractSelect.
     * 
     * @since 6.3
     */
    public static class AcceptItem extends AbstractItemSetCriterion {

        /**
         * @param select
         *            the select from which the item id's are checked
         * @param itemId
         *            the item identifier(s) of the select that are accepted
         */
        public AcceptItem(AbstractSelect select, Object... itemId) {
            super(select, itemId);
        }

        @Override
        public boolean accept(DragAndDropEvent dragEvent) {
            DataBoundTransferable transferable = (DataBoundTransferable) dragEvent
                    .getTransferable();
            if (transferable.getSourceComponent() != select) {
                return false;
            }
            return itemIds.contains(transferable.getItemId());
        }

        /**
         * A simple accept criterion which ensures that {@link Transferable}
         * contains an {@link Item} (or actually its identifier). In other words
         * the criterion check that drag is coming from a {@link Container} like
         * {@link Tree} or {@link Table}.
         */
        public static final ClientSideCriterion ALL = new ContainsDataFlavor(
                "itemId");

    }

    /**
     * TargetDetails implementation for subclasses of {@link AbstractSelect}
     * that implement {@link DropTarget}.
     * 
     * @since 6.3
     */
    public class AbstractSelectTargetDetails extends TargetDetailsImpl {

        /**
         * The item id over which the drag event happened.
         */
        protected Object idOver;

        /**
         * Constructor that automatically converts itemIdOver key to
         * corresponding item Id
         * 
         */
        protected AbstractSelectTargetDetails(Map<String, Object> rawVariables) {
            super(rawVariables, (DropTarget) AbstractSelect.this);
            // eagar fetch itemid, mapper may be emptied
            String keyover = (String) getData("itemIdOver");
            if (keyover != null) {
                idOver = itemIdMapper.get(keyover);
            }
        }

        /**
         * If the drag operation is currently over an {@link Item}, this method
         * returns the identifier of that {@link Item}.
         * 
         */
        public Object getItemIdOver() {
            return idOver;
        }

        /**
         * Returns a detailed vertical location where the drop happened on Item.
         */
        public VerticalDropLocation getDropLocation() {
            String detail = (String) getData("detail");
            if (detail == null) {
                return null;
            }
            return VerticalDropLocation.valueOf(detail);
        }

    }

    /**
     * An accept criterion to accept drops only on a specific vertical location
     * of an item.
     * <p>
     * This accept criterion is currently usable in Tree and Table
     * implementations.
     */
    public static class VerticalLocationIs extends TargetDetailIs {
        public static VerticalLocationIs TOP = new VerticalLocationIs(
                VerticalDropLocation.TOP);
        public static VerticalLocationIs BOTTOM = new VerticalLocationIs(
                VerticalDropLocation.BOTTOM);
        public static VerticalLocationIs MIDDLE = new VerticalLocationIs(
                VerticalDropLocation.MIDDLE);

        private VerticalLocationIs(VerticalDropLocation l) {
            super("detail", l.name());
        }
    }

    /**
     * Implement this interface and pass it to Tree.setItemDescriptionGenerator
     * or Table.setItemDescriptionGenerator to generate mouse over descriptions
     * ("tooltips") for the rows and cells in Table or for the items in Tree.
     */
    public interface ItemDescriptionGenerator extends Serializable {

        /**
         * Called by Table when a cell (and row) is painted or a item is painted
         * in Tree
         * 
         * @param source
         *            The source of the generator, the Tree or Table the
         *            generator is attached to
         * @param itemId
         *            The itemId of the painted cell
         * @param propertyId
         *            The propertyId of the cell, null when getting row
         *            description
         * @return The description or "tooltip" of the item.
         */
        public String generateDescription(Component source, Object itemId,
                Object propertyId);
    }
}

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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.thirdparty.guava.common.collect.Sets;
import com.google.gwt.thirdparty.guava.common.collect.Sets.SetView;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Container.PropertySetChangeEvent;
import com.vaadin.data.Container.PropertySetChangeListener;
import com.vaadin.data.Container.PropertySetChangeNotifier;
import com.vaadin.data.Container.Sortable;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.RpcDataProviderExtension;
import com.vaadin.data.RpcDataProviderExtension.DataProviderKeyMapper;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.BindException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroupFieldFactory;
import com.vaadin.data.sort.Sort;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.ConverterUtil;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ItemClickEvent.ItemClickNotifier;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.event.SelectionEvent.SelectionNotifier;
import com.vaadin.event.SortEvent;
import com.vaadin.event.SortEvent.SortListener;
import com.vaadin.event.SortEvent.SortNotifier;
import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.JsonCodec;
import com.vaadin.server.KeyMapper;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.grid.EditorClientRpc;
import com.vaadin.shared.ui.grid.EditorServerRpc;
import com.vaadin.shared.ui.grid.GridClientRpc;
import com.vaadin.shared.ui.grid.GridColumnState;
import com.vaadin.shared.ui.grid.GridConstants;
import com.vaadin.shared.ui.grid.GridServerRpc;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.shared.ui.grid.GridState.SharedSelectionMode;
import com.vaadin.shared.ui.grid.GridStaticCellType;
import com.vaadin.shared.ui.grid.GridStaticSectionState;
import com.vaadin.shared.ui.grid.GridStaticSectionState.CellState;
import com.vaadin.shared.ui.grid.GridStaticSectionState.RowState;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.grid.ScrollDestination;
import com.vaadin.shared.util.SharedUtil;
import com.vaadin.ui.renderers.Renderer;
import com.vaadin.ui.renderers.TextRenderer;
import com.vaadin.util.ReflectTools;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonValue;

/**
 * A grid component for displaying tabular data.
 * <p>
 * Grid is always bound to a {@link Container.Indexed}, but is not a
 * {@code Container} of any kind in of itself. The contents of the given
 * Container is displayed with the help of {@link Renderer Renderers}.
 * 
 * <h3 id="grid-headers-and-footers">Headers and Footers</h3>
 * <p>
 * 
 * 
 * <h3 id="grid-converters-and-renderers">Converters and Renderers</h3>
 * <p>
 * Each column has its own {@link Renderer} that displays data into something
 * that can be displayed in the browser. That data is first converted with a
 * {@link com.vaadin.data.util.converter.Converter Converter} into something
 * that the Renderer can process. This can also be an implicit step - if a
 * column has a simple data type, like a String, no explicit assignment is
 * needed.
 * <p>
 * Usually a renderer takes some kind of object, and converts it into a
 * HTML-formatted string.
 * <p>
 * <code><pre>
 * Grid grid = new Grid(myContainer);
 * Column column = grid.getColumn(STRING_DATE_PROPERTY);
 * column.setConverter(new StringToDateConverter());
 * column.setRenderer(new MyColorfulDateRenderer());
 * </pre></code>
 * 
 * <h3 id="grid-lazyloading">Lazy Loading</h3>
 * <p>
 * The data is accessed as it is needed by Grid and not any sooner. In other
 * words, if the given Container is huge, but only the first few rows are
 * displayed to the user, only those (and a few more, for caching purposes) are
 * accessed.
 * 
 * <h3 id="grid-selection-modes-and-models">Selection Modes and Models</h3>
 * <p>
 * Grid supports three selection <em>{@link SelectionMode modes}</em> (single,
 * multi, none), and comes bundled with one
 * <em>{@link SelectionModel model}</em> for each of the modes. The distinction
 * between a selection mode and selection model is as follows: a <em>mode</em>
 * essentially says whether you can have one, many or no rows selected. The
 * model, however, has the behavioral details of each. A single selection model
 * may require that the user deselects one row before selecting another one. A
 * variant of a multiselect might have a configurable maximum of rows that may
 * be selected. And so on.
 * <p>
 * <code><pre>
 * Grid grid = new Grid(myContainer);
 * 
 * // uses the bundled SingleSelectionModel class
 * grid.setSelectionMode(SelectionMode.SINGLE);
 * 
 * // changes the behavior to a custom selection model
 * grid.setSelectionModel(new MyTwoSelectionModel());
 * </pre></code>
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class Grid extends AbstractComponent implements SelectionNotifier,
        SortNotifier, SelectiveRenderer, ItemClickNotifier {

    /**
     * Custom field group that allows finding property types before an item has
     * been bound.
     */
    private final class CustomFieldGroup extends FieldGroup {

        public CustomFieldGroup() {
            setFieldFactory(EditorFieldFactory.get());
        }

        @Override
        protected Class<?> getPropertyType(Object propertyId)
                throws BindException {
            if (getItemDataSource() == null) {
                return datasource.getType(propertyId);
            } else {
                return super.getPropertyType(propertyId);
            }
        }
    }

    /**
     * Field factory used by default in the editor.
     * 
     * Aims to fields of suitable type and with suitable size for use in the
     * editor row.
     */
    public static class EditorFieldFactory extends
            DefaultFieldGroupFieldFactory {
        private static final EditorFieldFactory INSTANCE = new EditorFieldFactory();

        protected EditorFieldFactory() {
        }

        /**
         * Returns the singleton instance
         * 
         * @return the singleton instance
         */
        public static EditorFieldFactory get() {
            return INSTANCE;
        }

        @Override
        public <T extends Field> T createField(Class<?> type, Class<T> fieldType) {
            T f = super.createField(type, fieldType);
            if (f != null) {
                f.setWidth("100%");
            }
            return f;
        }

        @Override
        protected AbstractSelect createCompatibleSelect(
                Class<? extends AbstractSelect> fieldType) {
            if (anySelect(fieldType)) {
                return super.createCompatibleSelect(ComboBox.class);
            }
            return super.createCompatibleSelect(fieldType);
        }

        @Override
        protected void populateWithEnumData(AbstractSelect select,
                Class<? extends Enum> enumClass) {
            // Use enums directly and the EnumToStringConverter to be consistent
            // with what is shown in the Grid
            @SuppressWarnings("unchecked")
            EnumSet<?> enumSet = EnumSet.allOf(enumClass);
            for (Object r : enumSet) {
                select.addItem(r);
            }
        }
    }

    /**
     * Error handler for the editor
     */
    public interface EditorErrorHandler extends Serializable {

        /**
         * Called when an exception occurs while the editor row is being saved
         * 
         * @param event
         *            An event providing more information about the error
         */
        void commitError(CommitErrorEvent event);
    }

    /**
     * An event which is fired when saving the editor fails
     */
    public static class CommitErrorEvent extends Component.Event {

        private CommitException cause;

        private Set<Column> errorColumns = new HashSet<Column>();

        private String userErrorMessage;

        public CommitErrorEvent(Grid grid, CommitException cause) {
            super(grid);
            this.cause = cause;
            userErrorMessage = cause.getLocalizedMessage();
        }

        /**
         * Retrieves the cause of the failure
         * 
         * @return the cause of the failure
         */
        public CommitException getCause() {
            return cause;
        }

        @Override
        public Grid getComponent() {
            return (Grid) super.getComponent();
        }

        /**
         * Checks if validation exceptions caused this error
         * 
         * @return true if the problem was caused by a validation error
         */
        public boolean isValidationFailure() {
            return cause.getCause() instanceof InvalidValueException;
        }

        /**
         * Marks that an error indicator should be shown for the editor of a
         * column.
         * 
         * @param column
         *            the column to show an error for
         */
        public void addErrorColumn(Column column) {
            errorColumns.add(column);
        }

        /**
         * Gets all the columns that have been marked as erroneous.
         * 
         * @return an umodifiable collection of erroneous columns
         */
        public Collection<Column> getErrorColumns() {
            return Collections.unmodifiableCollection(errorColumns);
        }

        /**
         * Gets the error message to show to the user.
         * 
         * @return error message to show
         */
        public String getUserErrorMessage() {
            return userErrorMessage;
        }

        /**
         * Sets the error message to show to the user.
         * 
         * @param userErrorMessage
         *            the user error message to set
         */
        public void setUserErrorMessage(String userErrorMessage) {
            this.userErrorMessage = userErrorMessage;
        }

    }

    /**
     * Default error handler for the editor
     * 
     */
    public class DefaultEditorErrorHandler implements EditorErrorHandler {

        @Override
        public void commitError(CommitErrorEvent event) {
            Map<Field<?>, InvalidValueException> invalidFields = event
                    .getCause().getInvalidFields();

            if (!invalidFields.isEmpty()) {
                Object firstErrorPropertyId = null;
                Field<?> firstErrorField = null;

                FieldGroup fieldGroup = event.getCause().getFieldGroup();
                for (Column column : getColumns()) {
                    Object propertyId = column.getPropertyId();
                    Field<?> field = fieldGroup.getField(propertyId);
                    if (invalidFields.keySet().contains(field)) {
                        event.addErrorColumn(column);

                        if (firstErrorPropertyId == null) {
                            firstErrorPropertyId = propertyId;
                            firstErrorField = field;
                        }
                    }
                }

                /*
                 * Validation error, show first failure as
                 * "<Column header>: <message>"
                 */
                String caption = getColumn(firstErrorPropertyId)
                        .getHeaderCaption();
                String message = invalidFields.get(firstErrorField)
                        .getLocalizedMessage();

                event.setUserErrorMessage(caption + ": " + message);
            } else {
                com.vaadin.server.ErrorEvent.findErrorHandler(Grid.this).error(
                        new ConnectorErrorEvent(Grid.this, event.getCause()));
            }
        }

        private Object getFirstPropertyId(FieldGroup fieldGroup,
                Set<Field<?>> keySet) {
            for (Column c : getColumns()) {
                Object propertyId = c.getPropertyId();
                Field<?> f = fieldGroup.getField(propertyId);
                if (keySet.contains(f)) {
                    return propertyId;
                }
            }
            return null;
        }
    }

    /**
     * Selection modes representing built-in {@link SelectionModel
     * SelectionModels} that come bundled with {@link Grid}.
     * <p>
     * Passing one of these enums into
     * {@link Grid#setSelectionMode(SelectionMode)} is equivalent to calling
     * {@link Grid#setSelectionModel(SelectionModel)} with one of the built-in
     * implementations of {@link SelectionModel}.
     * 
     * @see Grid#setSelectionMode(SelectionMode)
     * @see Grid#setSelectionModel(SelectionModel)
     */
    public enum SelectionMode {
        /** A SelectionMode that maps to {@link SingleSelectionModel} */
        SINGLE {
            @Override
            protected SelectionModel createModel() {
                return new SingleSelectionModel();
            }

        },

        /** A SelectionMode that maps to {@link MultiSelectionModel} */
        MULTI {
            @Override
            protected SelectionModel createModel() {
                return new MultiSelectionModel();
            }
        },

        /** A SelectionMode that maps to {@link NoSelectionModel} */
        NONE {
            @Override
            protected SelectionModel createModel() {
                return new NoSelectionModel();
            }
        };

        protected abstract SelectionModel createModel();
    }

    /**
     * The server-side interface that controls Grid's selection state.
     */
    public interface SelectionModel extends Serializable {
        /**
         * Checks whether an item is selected or not.
         * 
         * @param itemId
         *            the item id to check for
         * @return <code>true</code> iff the item is selected
         */
        boolean isSelected(Object itemId);

        /**
         * Returns a collection of all the currently selected itemIds.
         * 
         * @return a collection of all the currently selected itemIds
         */
        Collection<Object> getSelectedRows();

        /**
         * Injects the current {@link Grid} instance into the SelectionModel.
         * <p>
         * <em>Note:</em> This method should not be called manually.
         * 
         * @param grid
         *            the Grid in which the SelectionModel currently is, or
         *            <code>null</code> when a selection model is being detached
         *            from a Grid.
         */
        void setGrid(Grid grid);

        /**
         * Resets the SelectiomModel to an initial state.
         * <p>
         * Most often this means that the selection state is cleared, but
         * implementations are free to interpret the "initial state" as they
         * wish. Some, for example, may want to keep the first selected item as
         * selected.
         */
        void reset();

        /**
         * A SelectionModel that supports multiple selections to be made.
         * <p>
         * This interface has a contract of having the same behavior, no matter
         * how the selection model is interacted with. In other words, if
         * something is forbidden to do in e.g. the user interface, it must also
         * be forbidden to do in the server-side and client-side APIs.
         */
        public interface Multi extends SelectionModel {

            /**
             * Marks items as selected.
             * <p>
             * This method does not clear any previous selection state, only
             * adds to it.
             * 
             * @param itemIds
             *            the itemId(s) to mark as selected
             * @return <code>true</code> if the selection state changed.
             *         <code>false</code> if all the given itemIds already were
             *         selected
             * @throws IllegalArgumentException
             *             if the <code>itemIds</code> varargs array is
             *             <code>null</code> or given itemIds don't exist in the
             *             container of Grid
             * @see #deselect(Object...)
             */
            boolean select(Object... itemIds) throws IllegalArgumentException;

            /**
             * Marks items as selected.
             * <p>
             * This method does not clear any previous selection state, only
             * adds to it.
             * 
             * @param itemIds
             *            the itemIds to mark as selected
             * @return <code>true</code> if the selection state changed.
             *         <code>false</code> if all the given itemIds already were
             *         selected
             * @throws IllegalArgumentException
             *             if <code>itemIds</code> is <code>null</code> or given
             *             itemIds don't exist in the container of Grid
             * @see #deselect(Collection)
             */
            boolean select(Collection<?> itemIds)
                    throws IllegalArgumentException;

            /**
             * Marks items as deselected.
             * 
             * @param itemIds
             *            the itemId(s) to remove from being selected
             * @return <code>true</code> if the selection state changed.
             *         <code>false</code> if none the given itemIds were
             *         selected previously
             * @throws IllegalArgumentException
             *             if the <code>itemIds</code> varargs array is
             *             <code>null</code>
             * @see #select(Object...)
             */
            boolean deselect(Object... itemIds) throws IllegalArgumentException;

            /**
             * Marks items as deselected.
             * 
             * @param itemIds
             *            the itemId(s) to remove from being selected
             * @return <code>true</code> if the selection state changed.
             *         <code>false</code> if none the given itemIds were
             *         selected previously
             * @throws IllegalArgumentException
             *             if <code>itemIds</code> is <code>null</code>
             * @see #select(Collection)
             */
            boolean deselect(Collection<?> itemIds)
                    throws IllegalArgumentException;

            /**
             * Marks all the items in the current Container as selected
             * 
             * @return <code>true</code> iff some items were previously not
             *         selected
             * @see #deselectAll()
             */
            boolean selectAll();

            /**
             * Marks all the items in the current Container as deselected
             * 
             * @return <code>true</code> iff some items were previously selected
             * @see #selectAll()
             */
            boolean deselectAll();

            /**
             * Marks items as selected while deselecting all items not in the
             * given Collection.
             * 
             * @param itemIds
             *            the itemIds to mark as selected
             * @return <code>true</code> if the selection state changed.
             *         <code>false</code> if all the given itemIds already were
             *         selected
             * @throws IllegalArgumentException
             *             if <code>itemIds</code> is <code>null</code> or given
             *             itemIds don't exist in the container of Grid
             */
            boolean setSelected(Collection<?> itemIds)
                    throws IllegalArgumentException;

            /**
             * Marks items as selected while deselecting all items not in the
             * varargs array.
             * 
             * @param itemIds
             *            the itemIds to mark as selected
             * @return <code>true</code> if the selection state changed.
             *         <code>false</code> if all the given itemIds already were
             *         selected
             * @throws IllegalArgumentException
             *             if the <code>itemIds</code> varargs array is
             *             <code>null</code> or given itemIds don't exist in the
             *             container of Grid
             */
            boolean setSelected(Object... itemIds)
                    throws IllegalArgumentException;
        }

        /**
         * A SelectionModel that supports for only single rows to be selected at
         * a time.
         * <p>
         * This interface has a contract of having the same behavior, no matter
         * how the selection model is interacted with. In other words, if
         * something is forbidden to do in e.g. the user interface, it must also
         * be forbidden to do in the server-side and client-side APIs.
         */
        public interface Single extends SelectionModel {

            /**
             * Marks an item as selected.
             * 
             * @param itemIds
             *            the itemId to mark as selected; <code>null</code> for
             *            deselect
             * @return <code>true</code> if the selection state changed.
             *         <code>false</code> if the itemId already was selected
             * @throws IllegalStateException
             *             if the selection was illegal. One such reason might
             *             be that the given id was null, indicating a deselect,
             *             but implementation doesn't allow deselecting.
             *             re-selecting something
             * @throws IllegalArgumentException
             *             if given itemId does not exist in the container of
             *             Grid
             */
            boolean select(Object itemId) throws IllegalStateException,
                    IllegalArgumentException;

            /**
             * Gets the item id of the currently selected item.
             * 
             * @return the item id of the currently selected item, or
             *         <code>null</code> if nothing is selected
             */
            Object getSelectedRow();

            /**
             * Sets whether it's allowed to deselect the selected row through
             * the UI. Deselection is allowed by default.
             * 
             * @param deselectAllowed
             *            <code>true</code> if the selected row can be
             *            deselected without selecting another row instead;
             *            otherwise <code>false</code>.
             */
            public void setDeselectAllowed(boolean deselectAllowed);

            /**
             * Sets whether it's allowed to deselect the selected row through
             * the UI.
             * 
             * @return <code>true</code> if deselection is allowed; otherwise
             *         <code>false</code>
             */
            public boolean isDeselectAllowed();
        }

        /**
         * A SelectionModel that does not allow for rows to be selected.
         * <p>
         * This interface has a contract of having the same behavior, no matter
         * how the selection model is interacted with. In other words, if the
         * developer is unable to select something programmatically, it is not
         * allowed for the end-user to select anything, either.
         */
        public interface None extends SelectionModel {

            /**
             * {@inheritDoc}
             * 
             * @return always <code>false</code>.
             */
            @Override
            public boolean isSelected(Object itemId);

            /**
             * {@inheritDoc}
             * 
             * @return always an empty collection.
             */
            @Override
            public Collection<Object> getSelectedRows();
        }
    }

    /**
     * A base class for SelectionModels that contains some of the logic that is
     * reusable.
     */
    public static abstract class AbstractSelectionModel implements
            SelectionModel {
        protected final LinkedHashSet<Object> selection = new LinkedHashSet<Object>();
        protected Grid grid = null;

        @Override
        public boolean isSelected(final Object itemId) {
            return selection.contains(itemId);
        }

        @Override
        public Collection<Object> getSelectedRows() {
            return new ArrayList<Object>(selection);
        }

        @Override
        public void setGrid(final Grid grid) {
            this.grid = grid;
        }

        /**
         * Sanity check for existence of item id.
         * 
         * @param itemId
         *            item id to be selected / deselected
         * 
         * @throws IllegalArgumentException
         *             if item Id doesn't exist in the container of Grid
         */
        protected void checkItemIdExists(Object itemId)
                throws IllegalArgumentException {
            if (!grid.getContainerDataSource().containsId(itemId)) {
                throw new IllegalArgumentException("Given item id (" + itemId
                        + ") does not exist in the container");
            }
        }

        /**
         * Sanity check for existence of item ids in given collection.
         * 
         * @param itemIds
         *            item id collection to be selected / deselected
         * 
         * @throws IllegalArgumentException
         *             if at least one item id doesn't exist in the container of
         *             Grid
         */
        protected void checkItemIdsExist(Collection<?> itemIds)
                throws IllegalArgumentException {
            for (Object itemId : itemIds) {
                checkItemIdExists(itemId);
            }
        }

        /**
         * Fires a {@link SelectionEvent} to all the {@link SelectionListener
         * SelectionListeners} currently added to the Grid in which this
         * SelectionModel is.
         * <p>
         * Note that this is only a helper method, and routes the call all the
         * way to Grid. A {@link SelectionModel} is not a
         * {@link SelectionNotifier}
         * 
         * @param oldSelection
         *            the complete {@link Collection} of the itemIds that were
         *            selected <em>before</em> this event happened
         * @param newSelection
         *            the complete {@link Collection} of the itemIds that are
         *            selected <em>after</em> this event happened
         */
        protected void fireSelectionEvent(
                final Collection<Object> oldSelection,
                final Collection<Object> newSelection) {
            grid.fireSelectionEvent(oldSelection, newSelection);
        }
    }

    /**
     * A default implementation of a {@link SelectionModel.Single}
     */
    public static class SingleSelectionModel extends AbstractSelectionModel
            implements SelectionModel.Single {
        @Override
        public boolean select(final Object itemId) {
            if (itemId == null) {
                return deselect(getSelectedRow());
            }

            checkItemIdExists(itemId);

            final Object selectedRow = getSelectedRow();
            final boolean modified = selection.add(itemId);
            if (modified) {
                final Collection<Object> deselected;
                if (selectedRow != null) {
                    deselectInternal(selectedRow, false);
                    deselected = Collections.singleton(selectedRow);
                } else {
                    deselected = Collections.emptySet();
                }

                fireSelectionEvent(deselected, selection);
            }

            return modified;
        }

        private boolean deselect(final Object itemId) {
            return deselectInternal(itemId, true);
        }

        private boolean deselectInternal(final Object itemId,
                boolean fireEventIfNeeded) {
            final boolean modified = selection.remove(itemId);
            if (fireEventIfNeeded && modified) {
                fireSelectionEvent(Collections.singleton(itemId),
                        Collections.emptySet());
            }
            return modified;
        }

        @Override
        public Object getSelectedRow() {
            if (selection.isEmpty()) {
                return null;
            } else {
                return selection.iterator().next();
            }
        }

        /**
         * Resets the selection state.
         * <p>
         * If an item is selected, it will become deselected.
         */
        @Override
        public void reset() {
            deselect(getSelectedRow());
        }

        @Override
        public void setDeselectAllowed(boolean deselectAllowed) {
            grid.getState().singleSelectDeselectAllowed = deselectAllowed;
        }

        @Override
        public boolean isDeselectAllowed() {
            return grid.getState(false).singleSelectDeselectAllowed;
        }
    }

    /**
     * A default implementation for a {@link SelectionModel.None}
     */
    public static class NoSelectionModel implements SelectionModel.None {
        @Override
        public void setGrid(final Grid grid) {
            // NOOP, not needed for anything
        }

        @Override
        public boolean isSelected(final Object itemId) {
            return false;
        }

        @Override
        public Collection<Object> getSelectedRows() {
            return Collections.emptyList();
        }

        /**
         * Semantically resets the selection model.
         * <p>
         * Effectively a no-op.
         */
        @Override
        public void reset() {
            // NOOP
        }
    }

    /**
     * A default implementation of a {@link SelectionModel.Multi}
     */
    public static class MultiSelectionModel extends AbstractSelectionModel
            implements SelectionModel.Multi {

        /**
         * The default selection size limit.
         * 
         * @see #setSelectionLimit(int)
         */
        public static final int DEFAULT_MAX_SELECTIONS = 1000;

        private int selectionLimit = DEFAULT_MAX_SELECTIONS;

        @Override
        public boolean select(final Object... itemIds)
                throws IllegalArgumentException {
            if (itemIds != null) {
                // select will fire the event
                return select(Arrays.asList(itemIds));
            } else {
                throw new IllegalArgumentException(
                        "Vararg array of itemIds may not be null");
            }
        }

        /**
         * {@inheritDoc}
         * <p>
         * All items might not be selected if the limit set using
         * {@link #setSelectionLimit(int)} is exceeded.
         */
        @Override
        public boolean select(final Collection<?> itemIds)
                throws IllegalArgumentException {
            if (itemIds == null) {
                throw new IllegalArgumentException("itemIds may not be null");
            }

            // Sanity check
            checkItemIdsExist(itemIds);

            final boolean selectionWillChange = !selection.containsAll(itemIds)
                    && selection.size() < selectionLimit;
            if (selectionWillChange) {
                final HashSet<Object> oldSelection = new HashSet<Object>(
                        selection);
                if (selection.size() + itemIds.size() >= selectionLimit) {
                    // Add one at a time if there's a risk of overflow
                    Iterator<?> iterator = itemIds.iterator();
                    while (iterator.hasNext()
                            && selection.size() < selectionLimit) {
                        selection.add(iterator.next());
                    }
                } else {
                    selection.addAll(itemIds);
                }
                fireSelectionEvent(oldSelection, selection);
            }
            return selectionWillChange;
        }

        /**
         * Sets the maximum number of rows that can be selected at once. This is
         * a mechanism to prevent exhausting server memory in situations where
         * users select lots of rows. If the limit is reached, newly selected
         * rows will not become recorded.
         * <p>
         * Old selections are not discarded if the current number of selected
         * row exceeds the new limit.
         * <p>
         * The default limit is {@value #DEFAULT_MAX_SELECTIONS} rows.
         * 
         * @param selectionLimit
         *            the non-negative selection limit to set
         * @throws IllegalArgumentException
         *             if the limit is negative
         */
        public void setSelectionLimit(int selectionLimit) {
            if (selectionLimit < 0) {
                throw new IllegalArgumentException(
                        "The selection limit must be non-negative");
            }
            this.selectionLimit = selectionLimit;
        }

        /**
         * Gets the selection limit.
         * 
         * @see #setSelectionLimit(int)
         * 
         * @return the selection limit
         */
        public int getSelectionLimit() {
            return selectionLimit;
        }

        @Override
        public boolean deselect(final Object... itemIds)
                throws IllegalArgumentException {
            if (itemIds != null) {
                // deselect will fire the event
                return deselect(Arrays.asList(itemIds));
            } else {
                throw new IllegalArgumentException(
                        "Vararg array of itemIds may not be null");
            }
        }

        @Override
        public boolean deselect(final Collection<?> itemIds)
                throws IllegalArgumentException {
            if (itemIds == null) {
                throw new IllegalArgumentException("itemIds may not be null");
            }

            final boolean hasCommonElements = !Collections.disjoint(itemIds,
                    selection);
            if (hasCommonElements) {
                final HashSet<Object> oldSelection = new HashSet<Object>(
                        selection);
                selection.removeAll(itemIds);
                fireSelectionEvent(oldSelection, selection);
            }
            return hasCommonElements;
        }

        @Override
        public boolean selectAll() {
            // select will fire the event
            final Indexed container = grid.getContainerDataSource();
            if (container != null) {
                return select(container.getItemIds());
            } else if (selection.isEmpty()) {
                return false;
            } else {
                /*
                 * this should never happen (no container but has a selection),
                 * but I guess the only theoretically correct course of
                 * action...
                 */
                return deselectAll();
            }
        }

        @Override
        public boolean deselectAll() {
            // deselect will fire the event
            return deselect(getSelectedRows());
        }

        /**
         * {@inheritDoc}
         * <p>
         * The returned Collection is in <strong>order of selection</strong>
         * &ndash; the item that was first selected will be first in the
         * collection, and so on. Should an item have been selected twice
         * without being deselected in between, it will have remained in its
         * original position.
         */
        @Override
        public Collection<Object> getSelectedRows() {
            // overridden only for JavaDoc
            return super.getSelectedRows();
        }

        /**
         * Resets the selection model.
         * <p>
         * Equivalent to calling {@link #deselectAll()}
         */
        @Override
        public void reset() {
            deselectAll();
        }

        @Override
        public boolean setSelected(Collection<?> itemIds)
                throws IllegalArgumentException {
            if (itemIds == null) {
                throw new IllegalArgumentException("itemIds may not be null");
            }

            checkItemIdsExist(itemIds);

            boolean changed = false;
            Set<Object> selectedRows = new HashSet<Object>(itemIds);
            final Collection<Object> oldSelection = getSelectedRows();
            SetView<?> added = Sets.difference(selectedRows, selection);
            if (!added.isEmpty()) {
                changed = true;
                selection.addAll(added.immutableCopy());
            }

            SetView<?> removed = Sets.difference(selection, selectedRows);
            if (!removed.isEmpty()) {
                changed = true;
                selection.removeAll(removed.immutableCopy());
            }

            if (changed) {
                fireSelectionEvent(oldSelection, selection);
            }

            return changed;
        }

        @Override
        public boolean setSelected(Object... itemIds)
                throws IllegalArgumentException {
            if (itemIds != null) {
                return setSelected(Arrays.asList(itemIds));
            } else {
                throw new IllegalArgumentException(
                        "Vararg array of itemIds may not be null");
            }
        }
    }

    /**
     * A data class which contains information which identifies a row in a
     * {@link Grid}.
     * <p>
     * Since this class follows the <code>Flyweight</code>-pattern any instance
     * of this object is subject to change without the user knowing it and so
     * should not be stored anywhere outside of the method providing these
     * instances.
     */
    public static class RowReference implements Serializable {
        private final Grid grid;

        private Object itemId;

        /**
         * Creates a new row reference for the given grid.
         * 
         * @param grid
         *            the grid that the row belongs to
         */
        public RowReference(Grid grid) {
            this.grid = grid;
        }

        /**
         * Sets the identifying information for this row
         * 
         * @param itemId
         *            the item id of the row
         */
        public void set(Object itemId) {
            this.itemId = itemId;
        }

        /**
         * Gets the grid that contains the referenced row.
         * 
         * @return the grid that contains referenced row
         */
        public Grid getGrid() {
            return grid;
        }

        /**
         * Gets the item id of the row.
         * 
         * @return the item id of the row
         */
        public Object getItemId() {
            return itemId;
        }

        /**
         * Gets the item for the row.
         * 
         * @return the item for the row
         */
        public Item getItem() {
            return grid.getContainerDataSource().getItem(itemId);
        }
    }

    /**
     * A data class which contains information which identifies a cell in a
     * {@link Grid}.
     * <p>
     * Since this class follows the <code>Flyweight</code>-pattern any instance
     * of this object is subject to change without the user knowing it and so
     * should not be stored anywhere outside of the method providing these
     * instances.
     */
    public static class CellReference implements Serializable {
        private final RowReference rowReference;

        private Object propertyId;

        public CellReference(RowReference rowReference) {
            this.rowReference = rowReference;
        }

        /**
         * Sets the identifying information for this cell
         * 
         * @param propertyId
         *            the property id of the column
         */
        public void set(Object propertyId) {
            this.propertyId = propertyId;
        }

        /**
         * Gets the grid that contains the referenced cell.
         * 
         * @return the grid that contains referenced cell
         */
        public Grid getGrid() {
            return rowReference.getGrid();
        }

        /**
         * @return the property id of the column
         */
        public Object getPropertyId() {
            return propertyId;
        }

        /**
         * @return the property for the cell
         */
        public Property<?> getProperty() {
            return getItem().getItemProperty(propertyId);
        }

        /**
         * Gets the item id of the row of the cell.
         * 
         * @return the item id of the row
         */
        public Object getItemId() {
            return rowReference.getItemId();
        }

        /**
         * Gets the item for the row of the cell.
         * 
         * @return the item for the row
         */
        public Item getItem() {
            return rowReference.getItem();
        }

        /**
         * Gets the value of the cell.
         * 
         * @return the value of the cell
         */
        public Object getValue() {
            return getProperty().getValue();
        }
    }

    /**
     * Callback interface for generating custom style names for data rows
     * 
     * @see Grid#setRowStyleGenerator(RowStyleGenerator)
     */
    public interface RowStyleGenerator extends Serializable {

        /**
         * Called by Grid to generate a style name for a row
         * 
         * @param rowReference
         *            The row to generate a style for
         * @return the style name to add to this row, or {@code null} to not set
         *         any style
         */
        public String getStyle(RowReference rowReference);
    }

    /**
     * Callback interface for generating custom style names for cells
     * 
     * @see Grid#setCellStyleGenerator(CellStyleGenerator)
     */
    public interface CellStyleGenerator extends Serializable {

        /**
         * Called by Grid to generate a style name for a column
         * 
         * @param cellReference
         *            The cell to generate a style for
         * @return the style name to add to this cell, or {@code null} to not
         *         set any style
         */
        public String getStyle(CellReference cellReference);
    }

    /**
     * Abstract base class for Grid header and footer sections.
     * 
     * @param <ROWTYPE>
     *            the type of the rows in the section
     */
    protected static abstract class StaticSection<ROWTYPE extends StaticSection.StaticRow<?>>
            implements Serializable {

        /**
         * Abstract base class for Grid header and footer rows.
         * 
         * @param <CELLTYPE>
         *            the type of the cells in the row
         */
        abstract static class StaticRow<CELLTYPE extends StaticCell> implements
                Serializable {

            private RowState rowState = new RowState();
            protected StaticSection<?> section;
            private Map<Object, CELLTYPE> cells = new LinkedHashMap<Object, CELLTYPE>();
            private Map<Set<CELLTYPE>, CELLTYPE> cellGroups = new HashMap<Set<CELLTYPE>, CELLTYPE>();

            protected StaticRow(StaticSection<?> section) {
                this.section = section;
            }

            protected void addCell(Object propertyId) {
                CELLTYPE cell = createCell();
                cell.setColumnId(section.grid.getColumn(propertyId).getState().id);
                cells.put(propertyId, cell);
                rowState.cells.add(cell.getCellState());
            }

            protected void removeCell(Object propertyId) {
                CELLTYPE cell = cells.remove(propertyId);
                if (cell != null) {
                    Set<CELLTYPE> cellGroupForCell = getCellGroupForCell(cell);
                    if (cellGroupForCell != null) {
                        removeCellFromGroup(cell, cellGroupForCell);
                    }
                    rowState.cells.remove(cell.getCellState());
                }
            }

            private void removeCellFromGroup(CELLTYPE cell,
                    Set<CELLTYPE> cellGroup) {
                String columnId = cell.getColumnId();
                for (Set<String> group : rowState.cellGroups.keySet()) {
                    if (group.contains(columnId)) {
                        if (group.size() > 2) {
                            // Update map key correctly
                            CELLTYPE mergedCell = cellGroups.remove(cellGroup);
                            cellGroup.remove(cell);
                            cellGroups.put(cellGroup, mergedCell);

                            group.remove(columnId);
                        } else {
                            rowState.cellGroups.remove(group);
                            cellGroups.remove(cellGroup);
                        }
                        return;
                    }
                }
            }

            /**
             * Creates and returns a new instance of the cell type.
             * 
             * @return the created cell
             */
            protected abstract CELLTYPE createCell();

            protected RowState getRowState() {
                return rowState;
            }

            /**
             * Returns the cell for the given property id on this row. If the
             * column is merged returned cell is the cell for the whole group.
             * 
             * @param propertyId
             *            the property id of the column
             * @return the cell for the given property, merged cell for merged
             *         properties, null if not found
             */
            public CELLTYPE getCell(Object propertyId) {
                CELLTYPE cell = cells.get(propertyId);
                Set<CELLTYPE> cellGroup = getCellGroupForCell(cell);
                if (cellGroup != null) {
                    cell = cellGroups.get(cellGroup);
                }
                return cell;
            }

            /**
             * Merges columns cells in a row
             * 
             * @param propertyIds
             *            The property ids of columns to merge
             * @return The remaining visible cell after the merge
             */
            public CELLTYPE join(Object... propertyIds) {
                assert propertyIds.length > 1 : "You need to merge at least 2 properties";

                Set<CELLTYPE> cells = new HashSet<CELLTYPE>();
                for (int i = 0; i < propertyIds.length; ++i) {
                    cells.add(getCell(propertyIds[i]));
                }

                return join(cells);
            }

            /**
             * Merges columns cells in a row
             * 
             * @param cells
             *            The cells to merge. Must be from the same row.
             * @return The remaining visible cell after the merge
             */
            public CELLTYPE join(CELLTYPE... cells) {
                assert cells.length > 1 : "You need to merge at least 2 cells";

                return join(new HashSet<CELLTYPE>(Arrays.asList(cells)));
            }

            protected CELLTYPE join(Set<CELLTYPE> cells) {
                for (CELLTYPE cell : cells) {
                    if (getCellGroupForCell(cell) != null) {
                        throw new IllegalArgumentException(
                                "Cell already merged");
                    } else if (!this.cells.containsValue(cell)) {
                        throw new IllegalArgumentException(
                                "Cell does not exist on this row");
                    }
                }

                // Create new cell data for the group
                CELLTYPE newCell = createCell();

                Set<String> columnGroup = new HashSet<String>();
                for (CELLTYPE cell : cells) {
                    columnGroup.add(cell.getColumnId());
                }
                rowState.cellGroups.put(columnGroup, newCell.getCellState());
                cellGroups.put(cells, newCell);
                return newCell;
            }

            private Set<CELLTYPE> getCellGroupForCell(CELLTYPE cell) {
                for (Set<CELLTYPE> group : cellGroups.keySet()) {
                    if (group.contains(cell)) {
                        return group;
                    }
                }
                return null;
            }

            /**
             * Returns the custom style name for this row.
             * 
             * @return the style name or null if no style name has been set
             */
            public String getStyleName() {
                return getRowState().styleName;
            }

            /**
             * Sets a custom style name for this row.
             * 
             * @param styleName
             *            the style name to set or null to not use any style
             *            name
             */
            public void setStyleName(String styleName) {
                getRowState().styleName = styleName;
            }

        }

        /**
         * A header or footer cell. Has a simple textual caption.
         */
        abstract static class StaticCell implements Serializable {

            private CellState cellState = new CellState();
            private StaticRow<?> row;

            protected StaticCell(StaticRow<?> row) {
                this.row = row;
            }

            void setColumnId(String id) {
                cellState.columnId = id;
            }

            String getColumnId() {
                return cellState.columnId;
            }

            /**
             * Gets the row where this cell is.
             * 
             * @return row for this cell
             */
            public StaticRow<?> getRow() {
                return row;
            }

            protected CellState getCellState() {
                return cellState;
            }

            /**
             * Sets the text displayed in this cell.
             * 
             * @param text
             *            a plain text caption
             */
            public void setText(String text) {
                removeComponentIfPresent();
                cellState.text = text;
                cellState.type = GridStaticCellType.TEXT;
                row.section.markAsDirty();
            }

            /**
             * Returns the text displayed in this cell.
             * 
             * @return the plain text caption
             */
            public String getText() {
                if (cellState.type != GridStaticCellType.TEXT) {
                    throw new IllegalStateException(
                            "Cannot fetch Text from a cell with type "
                                    + cellState.type);
                }
                return cellState.text;
            }

            /**
             * Returns the HTML content displayed in this cell.
             * 
             * @return the html
             * 
             */
            public String getHtml() {
                if (cellState.type != GridStaticCellType.HTML) {
                    throw new IllegalStateException(
                            "Cannot fetch HTML from a cell with type "
                                    + cellState.type);
                }
                return cellState.html;
            }

            /**
             * Sets the HTML content displayed in this cell.
             * 
             * @param html
             *            the html to set
             */
            public void setHtml(String html) {
                removeComponentIfPresent();
                cellState.html = html;
                cellState.type = GridStaticCellType.HTML;
                row.section.markAsDirty();
            }

            /**
             * Returns the component displayed in this cell.
             * 
             * @return the component
             */
            public Component getComponent() {
                if (cellState.type != GridStaticCellType.WIDGET) {
                    throw new IllegalStateException(
                            "Cannot fetch Component from a cell with type "
                                    + cellState.type);
                }
                return (Component) cellState.connector;
            }

            /**
             * Sets the component displayed in this cell.
             * 
             * @param component
             *            the component to set
             */
            public void setComponent(Component component) {
                removeComponentIfPresent();
                component.setParent(row.section.grid);
                cellState.connector = component;
                cellState.type = GridStaticCellType.WIDGET;
                row.section.markAsDirty();
            }

            /**
             * Returns the custom style name for this cell.
             * 
             * @return the style name or null if no style name has been set
             */
            public String getStyleName() {
                return cellState.styleName;
            }

            /**
             * Sets a custom style name for this cell.
             * 
             * @param styleName
             *            the style name to set or null to not use any style
             *            name
             */
            public void setStyleName(String styleName) {
                cellState.styleName = styleName;
                row.section.markAsDirty();
            }

            private void removeComponentIfPresent() {
                Component component = (Component) cellState.connector;
                if (component != null) {
                    component.setParent(null);
                    cellState.connector = null;
                }
            }
        }

        protected Grid grid;
        protected List<ROWTYPE> rows = new ArrayList<ROWTYPE>();

        /**
         * Sets the visibility of the whole section.
         * 
         * @param visible
         *            true to show this section, false to hide
         */
        public void setVisible(boolean visible) {
            if (getSectionState().visible != visible) {
                getSectionState().visible = visible;
                markAsDirty();
            }
        }

        /**
         * Returns the visibility of this section.
         * 
         * @return true if visible, false otherwise.
         */
        public boolean isVisible() {
            return getSectionState().visible;
        }

        /**
         * Removes the row at the given position.
         * 
         * @param index
         *            the position of the row
         * 
         * @throws IllegalArgumentException
         *             if no row exists at given index
         * @see #removeRow(StaticRow)
         * @see #addRowAt(int)
         * @see #appendRow()
         * @see #prependRow()
         */
        public ROWTYPE removeRow(int rowIndex) {
            if (rowIndex >= rows.size() || rowIndex < 0) {
                throw new IllegalArgumentException("No row at given index "
                        + rowIndex);
            }
            ROWTYPE row = rows.remove(rowIndex);
            getSectionState().rows.remove(rowIndex);

            markAsDirty();
            return row;
        }

        /**
         * Removes the given row from the section.
         * 
         * @param row
         *            the row to be removed
         * 
         * @throws IllegalArgumentException
         *             if the row does not exist in this section
         * @see #removeRow(int)
         * @see #addRowAt(int)
         * @see #appendRow()
         * @see #prependRow()
         */
        public void removeRow(ROWTYPE row) {
            try {
                removeRow(rows.indexOf(row));
            } catch (IndexOutOfBoundsException e) {
                throw new IllegalArgumentException(
                        "Section does not contain the given row");
            }
        }

        /**
         * Gets row at given index.
         * 
         * @param rowIndex
         *            0 based index for row. Counted from top to bottom
         * @return row at given index
         */
        public ROWTYPE getRow(int rowIndex) {
            if (rowIndex >= rows.size() || rowIndex < 0) {
                throw new IllegalArgumentException("No row at given index "
                        + rowIndex);
            }
            return rows.get(rowIndex);
        }

        /**
         * Adds a new row at the top of this section.
         * 
         * @return the new row
         * @see #appendRow()
         * @see #addRowAt(int)
         * @see #removeRow(StaticRow)
         * @see #removeRow(int)
         */
        public ROWTYPE prependRow() {
            return addRowAt(0);
        }

        /**
         * Adds a new row at the bottom of this section.
         * 
         * @return the new row
         * @see #prependRow()
         * @see #addRowAt(int)
         * @see #removeRow(StaticRow)
         * @see #removeRow(int)
         */
        public ROWTYPE appendRow() {
            return addRowAt(rows.size());
        }

        /**
         * Inserts a new row at the given position.
         * 
         * @param index
         *            the position at which to insert the row
         * @return the new row
         * 
         * @throws IndexOutOfBoundsException
         *             if the index is out of bounds
         * @see #appendRow()
         * @see #prependRow()
         * @see #removeRow(StaticRow)
         * @see #removeRow(int)
         */
        public ROWTYPE addRowAt(int index) {
            if (index > rows.size() || index < 0) {
                throw new IllegalArgumentException(
                        "Unable to add row at index " + index);
            }
            ROWTYPE row = createRow();
            rows.add(index, row);
            getSectionState().rows.add(index, row.getRowState());

            for (Object id : grid.columns.keySet()) {
                row.addCell(id);
            }

            markAsDirty();
            return row;
        }

        /**
         * Gets the amount of rows in this section.
         * 
         * @return row count
         */
        public int getRowCount() {
            return rows.size();
        }

        protected abstract GridStaticSectionState getSectionState();

        protected abstract ROWTYPE createRow();

        /**
         * Informs the grid that state has changed and it should be redrawn.
         */
        protected void markAsDirty() {
            grid.markAsDirty();
        }

        /**
         * Removes a column for given property id from the section.
         * 
         * @param propertyId
         *            property to be removed
         */
        protected void removeColumn(Object propertyId) {
            for (ROWTYPE row : rows) {
                row.removeCell(propertyId);
            }
        }

        /**
         * Adds a column for given property id to the section.
         * 
         * @param propertyId
         *            property to be added
         */
        protected void addColumn(Object propertyId) {
            for (ROWTYPE row : rows) {
                row.addCell(propertyId);
            }
        }

        /**
         * Performs a sanity check that section is in correct state.
         * 
         * @throws IllegalStateException
         *             if merged cells are not i n continuous range
         */
        protected void sanityCheck() throws IllegalStateException {
            List<String> columnOrder = grid.getState().columnOrder;
            for (ROWTYPE row : rows) {
                for (Set<String> cellGroup : row.getRowState().cellGroups
                        .keySet()) {
                    if (!checkCellGroupAndOrder(columnOrder, cellGroup)) {
                        throw new IllegalStateException(
                                "Not all merged cells were in a continuous range.");
                    }
                }
            }
        }

        private boolean checkCellGroupAndOrder(List<String> columnOrder,
                Set<String> cellGroup) {
            if (!columnOrder.containsAll(cellGroup)) {
                return false;
            }

            for (int i = 0; i < columnOrder.size(); ++i) {
                if (!cellGroup.contains(columnOrder.get(i))) {
                    continue;
                }

                for (int j = 1; j < cellGroup.size(); ++j) {
                    if (!cellGroup.contains(columnOrder.get(i + j))) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    }

    /**
     * Represents the header section of a Grid.
     */
    protected static class Header extends StaticSection<HeaderRow> {

        private HeaderRow defaultRow = null;
        private final GridStaticSectionState headerState = new GridStaticSectionState();

        protected Header(Grid grid) {
            this.grid = grid;
            grid.getState(true).header = headerState;
            HeaderRow row = createRow();
            rows.add(row);
            setDefaultRow(row);
            getSectionState().rows.add(row.getRowState());
        }

        /**
         * Sets the default row of this header. The default row is a special
         * header row providing a user interface for sorting columns.
         * 
         * @param row
         *            the new default row, or null for no default row
         * 
         * @throws IllegalArgumentException
         *             this header does not contain the row
         */
        public void setDefaultRow(HeaderRow row) {
            if (row == defaultRow) {
                return;
            }

            if (row != null && !rows.contains(row)) {
                throw new IllegalArgumentException(
                        "Cannot set a default row that does not exist in the section");
            }

            if (defaultRow != null) {
                defaultRow.setDefaultRow(false);
            }

            if (row != null) {
                row.setDefaultRow(true);
            }

            defaultRow = row;
            markAsDirty();
        }

        /**
         * Returns the current default row of this header. The default row is a
         * special header row providing a user interface for sorting columns.
         * 
         * @return the default row or null if no default row set
         */
        public HeaderRow getDefaultRow() {
            return defaultRow;
        }

        @Override
        protected GridStaticSectionState getSectionState() {
            return headerState;
        }

        @Override
        protected HeaderRow createRow() {
            return new HeaderRow(this);
        }

        @Override
        public HeaderRow removeRow(int rowIndex) {
            HeaderRow row = super.removeRow(rowIndex);
            if (row == defaultRow) {
                // Default Header Row was just removed.
                setDefaultRow(null);
            }
            return row;
        }

        @Override
        protected void sanityCheck() throws IllegalStateException {
            super.sanityCheck();

            boolean hasDefaultRow = false;
            for (HeaderRow row : rows) {
                if (row.getRowState().defaultRow) {
                    if (!hasDefaultRow) {
                        hasDefaultRow = true;
                    } else {
                        throw new IllegalStateException(
                                "Multiple default rows in header");
                    }
                }
            }
        }
    }

    /**
     * Represents a header row in Grid.
     */
    public static class HeaderRow extends StaticSection.StaticRow<HeaderCell> {

        protected HeaderRow(StaticSection<?> section) {
            super(section);
        }

        private void setDefaultRow(boolean value) {
            getRowState().defaultRow = value;
        }

        @Override
        protected HeaderCell createCell() {
            return new HeaderCell(this);
        }
    }

    /**
     * Represents a header cell in Grid. Can be a merged cell for multiple
     * columns.
     */
    public static class HeaderCell extends StaticSection.StaticCell {

        protected HeaderCell(HeaderRow row) {
            super(row);
        }
    }

    /**
     * Represents the footer section of a Grid. By default Footer is not
     * visible.
     */
    protected static class Footer extends StaticSection<FooterRow> {

        private final GridStaticSectionState footerState = new GridStaticSectionState();

        protected Footer(Grid grid) {
            this.grid = grid;
            grid.getState(true).footer = footerState;
        }

        @Override
        protected GridStaticSectionState getSectionState() {
            return footerState;
        }

        @Override
        protected FooterRow createRow() {
            return new FooterRow(this);
        }

        @Override
        protected void sanityCheck() throws IllegalStateException {
            super.sanityCheck();
        }
    }

    /**
     * Represents a footer row in Grid.
     */
    public static class FooterRow extends StaticSection.StaticRow<FooterCell> {

        protected FooterRow(StaticSection<?> section) {
            super(section);
        }

        @Override
        protected FooterCell createCell() {
            return new FooterCell(this);
        }

    }

    /**
     * Represents a footer cell in Grid.
     */
    public static class FooterCell extends StaticSection.StaticCell {

        protected FooterCell(FooterRow row) {
            super(row);
        }
    }

    /**
     * A column in the grid. Can be obtained by calling
     * {@link Grid#getColumn(Object propertyId)}.
     */
    public static class Column implements Serializable {

        /**
         * The state of the column shared to the client
         */
        private final GridColumnState state;

        /**
         * The grid this column is associated with
         */
        private final Grid grid;

        /**
         * Backing property for column
         */
        private final Object propertyId;

        private Converter<?, Object> converter;

        /**
         * A check for allowing the {@link #Column(Grid, GridColumnState)
         * constructor} to call {@link #setConverter(Converter)} with a
         * <code>null</code>, even if model and renderer aren't compatible.
         */
        private boolean isFirstConverterAssignment = true;

        /**
         * Internally used constructor.
         * 
         * @param grid
         *            The grid this column belongs to. Should not be null.
         * @param state
         *            the shared state of this column
         * @param propertyId
         *            the backing property id for this column
         */
        Column(Grid grid, GridColumnState state, Object propertyId) {
            this.grid = grid;
            this.state = state;
            this.propertyId = propertyId;
            internalSetRenderer(new TextRenderer());
        }

        /**
         * Returns the serializable state of this column that is sent to the
         * client side connector.
         * 
         * @return the internal state of the column
         */
        GridColumnState getState() {
            return state;
        }

        /**
         * Returns the property id for the backing property of this Column
         * 
         * @return property id
         */
        public Object getPropertyId() {
            return propertyId;
        }

        /**
         * Returns the caption of the header. By default the header caption is
         * the property id of the column.
         * 
         * @return the text in the default row of header, null if no default row
         * 
         * @throws IllegalStateException
         *             if the column no longer is attached to the grid
         */
        public String getHeaderCaption() throws IllegalStateException {
            checkColumnIsAttached();
            HeaderRow row = grid.getHeader().getDefaultRow();
            if (row != null) {
                return row.getCell(grid.getPropertyIdByColumnId(state.id))
                        .getText();
            }
            return null;
        }

        /**
         * Sets the caption of the header.
         * 
         * @param caption
         *            the text to show in the caption
         * @return the column itself
         * 
         * @throws IllegalStateException
         *             if the column is no longer attached to any grid
         */
        public Column setHeaderCaption(String caption)
                throws IllegalStateException {
            checkColumnIsAttached();
            HeaderRow row = grid.getHeader().getDefaultRow();
            if (row != null) {
                row.getCell(grid.getPropertyIdByColumnId(state.id)).setText(
                        caption);
            }
            return this;
        }

        /**
         * Returns the width (in pixels). By default a column is 100px wide.
         * 
         * @return the width in pixels of the column
         * @throws IllegalStateException
         *             if the column is no longer attached to any grid
         */
        public double getWidth() throws IllegalStateException {
            checkColumnIsAttached();
            return state.width;
        }

        /**
         * Sets the width (in pixels).
         * <p>
         * This overrides any configuration set by any of
         * {@link #setExpandRatio(int)}, {@link #setMinimumWidth(double)} or
         * {@link #setMaximumWidth(double)}.
         * 
         * @param pixelWidth
         *            the new pixel width of the column
         * @return the column itself
         * 
         * @throws IllegalStateException
         *             if the column is no longer attached to any grid
         * @throws IllegalArgumentException
         *             thrown if pixel width is less than zero
         */
        public Column setWidth(double pixelWidth) throws IllegalStateException,
                IllegalArgumentException {
            checkColumnIsAttached();
            if (pixelWidth < 0) {
                throw new IllegalArgumentException(
                        "Pixel width should be greated than 0 (in "
                                + toString() + ")");
            }
            state.width = pixelWidth;
            grid.markAsDirty();
            return this;
        }

        /**
         * Marks the column width as undefined meaning that the grid is free to
         * resize the column based on the cell contents and available space in
         * the grid.
         * 
         * @return the column itself
         */
        public Column setWidthUndefined() {
            checkColumnIsAttached();
            state.width = -1;
            grid.markAsDirty();
            return this;
        }

        /**
         * Checks if column is attached and throws an
         * {@link IllegalStateException} if it is not
         * 
         * @throws IllegalStateException
         *             if the column is no longer attached to any grid
         */
        protected void checkColumnIsAttached() throws IllegalStateException {
            if (grid.getColumnByColumnId(state.id) == null) {
                throw new IllegalStateException("Column no longer exists.");
            }
        }

        /**
         * Sets this column as the last frozen column in its grid.
         * 
         * @return the column itself
         * 
         * @throws IllegalArgumentException
         *             if the column is no longer attached to any grid
         * @see Grid#setFrozenColumnCount(int)
         */
        public Column setLastFrozenColumn() {
            checkColumnIsAttached();
            grid.setFrozenColumnCount(grid.getState(false).columnOrder
                    .indexOf(getState().id) + 1);
            return this;
        }

        /**
         * Sets the renderer for this column.
         * <p>
         * If a suitable converter isn't defined explicitly, the session
         * converter factory is used to find a compatible converter.
         * 
         * @param renderer
         *            the renderer to use
         * @return the column itself
         * 
         * @throws IllegalArgumentException
         *             if no compatible converter could be found
         * 
         * @see VaadinSession#getConverterFactory()
         * @see ConverterUtil#getConverter(Class, Class, VaadinSession)
         * @see #setConverter(Converter)
         */
        public Column setRenderer(Renderer<?> renderer) {
            if (!internalSetRenderer(renderer)) {
                throw new IllegalArgumentException(
                        "Could not find a converter for converting from the model type "
                                + getModelType()
                                + " to the renderer presentation type "
                                + renderer.getPresentationType() + " (in "
                                + toString() + ")");
            }
            return this;
        }

        /**
         * Sets the renderer for this column and the converter used to convert
         * from the property value type to the renderer presentation type.
         * 
         * @param renderer
         *            the renderer to use, cannot be null
         * @param converter
         *            the converter to use
         * @return the column itself
         * 
         * @throws IllegalArgumentException
         *             if the renderer is already associated with a grid column
         */
        public <T> Column setRenderer(Renderer<T> renderer,
                Converter<? extends T, ?> converter) {
            if (renderer.getParent() != null) {
                throw new IllegalArgumentException(
                        "Cannot set a renderer that is already connected to a grid column (in "
                                + toString() + ")");
            }

            if (getRenderer() != null) {
                grid.removeExtension(getRenderer());
            }

            grid.addRenderer(renderer);
            state.rendererConnector = renderer;
            setConverter(converter);
            return this;
        }

        /**
         * Sets the converter used to convert from the property value type to
         * the renderer presentation type.
         * 
         * @param converter
         *            the converter to use, or {@code null} to not use any
         *            converters
         * @return the column itself
         * 
         * @throws IllegalArgumentException
         *             if the types are not compatible
         */
        public Column setConverter(Converter<?, ?> converter)
                throws IllegalArgumentException {
            Class<?> modelType = getModelType();
            if (converter != null) {
                if (!converter.getModelType().isAssignableFrom(modelType)) {
                    throw new IllegalArgumentException(
                            "The converter model type "
                                    + converter.getModelType()
                                    + " is not compatible with the property type "
                                    + modelType + " (in " + toString() + ")");

                } else if (!getRenderer().getPresentationType()
                        .isAssignableFrom(converter.getPresentationType())) {
                    throw new IllegalArgumentException(
                            "The converter presentation type "
                                    + converter.getPresentationType()
                                    + " is not compatible with the renderer presentation type "
                                    + getRenderer().getPresentationType()
                                    + " (in " + toString() + ")");
                }
            }

            else {
                /*
                 * Since the converter is null (i.e. will be removed), we need
                 * to know that the renderer and model are compatible. If not,
                 * we can't allow for this to happen.
                 * 
                 * The constructor is allowed to call this method with null
                 * without any compatibility checks, therefore we have a special
                 * case for it.
                 */

                Class<?> rendererPresentationType = getRenderer()
                        .getPresentationType();
                if (!isFirstConverterAssignment
                        && !rendererPresentationType
                                .isAssignableFrom(modelType)) {
                    throw new IllegalArgumentException(
                            "Cannot remove converter, "
                                    + "as renderer's presentation type "
                                    + rendererPresentationType.getName()
                                    + " and column's "
                                    + "model "
                                    + modelType.getName()
                                    + " type aren't "
                                    + "directly compatible with each other (in "
                                    + toString() + ")");
                }
            }

            isFirstConverterAssignment = false;

            @SuppressWarnings("unchecked")
            Converter<?, Object> castConverter = (Converter<?, Object>) converter;
            this.converter = castConverter;

            return this;
        }

        /**
         * Returns the renderer instance used by this column.
         * 
         * @return the renderer
         */
        public Renderer<?> getRenderer() {
            return (Renderer<?>) getState().rendererConnector;
        }

        /**
         * Returns the converter instance used by this column.
         * 
         * @return the converter
         */
        public Converter<?, ?> getConverter() {
            return converter;
        }

        private <T> boolean internalSetRenderer(Renderer<T> renderer) {

            Converter<? extends T, ?> converter;
            if (isCompatibleWithProperty(renderer, getConverter())) {
                // Use the existing converter (possibly none) if types
                // compatible
                converter = (Converter<? extends T, ?>) getConverter();
            } else {
                converter = ConverterUtil.getConverter(
                        renderer.getPresentationType(), getModelType(),
                        getSession());
            }
            setRenderer(renderer, converter);
            return isCompatibleWithProperty(renderer, converter);
        }

        private VaadinSession getSession() {
            UI ui = grid.getUI();
            return ui != null ? ui.getSession() : null;
        }

        private boolean isCompatibleWithProperty(Renderer<?> renderer,
                Converter<?, ?> converter) {
            Class<?> type;
            if (converter == null) {
                type = getModelType();
            } else {
                type = converter.getPresentationType();
            }
            return renderer.getPresentationType().isAssignableFrom(type);
        }

        private Class<?> getModelType() {
            return grid.getContainerDataSource().getType(
                    grid.getPropertyIdByColumnId(state.id));
        }

        /**
         * Sets whether the column should be sortable by the user. The grid can
         * be sorted by a sortable column by clicking or tapping the column's
         * default header. Programmatic sorting using the Grid.sort methods is
         * not affected by this setting.
         * 
         * @param sortable
         *            <code>true</code> if the user should be able to sort the
         *            column, false otherwise
         * @return the column itself
         */
        public Column setSortable(boolean sortable) {
            checkColumnIsAttached();

            if (sortable) {
                if (!(grid.datasource instanceof Sortable)) {
                    throw new IllegalStateException(
                            "Can't set column "
                                    + toString()
                                    + " sortable. The Container of Grid does not implement Sortable");
                } else if (!((Sortable) grid.datasource)
                        .getSortableContainerPropertyIds().contains(propertyId)) {
                    throw new IllegalStateException(
                            "Can't set column "
                                    + toString()
                                    + " sortable. Container doesn't support sorting by property "
                                    + propertyId);
                }
            }

            state.sortable = sortable;
            grid.markAsDirty();
            return this;
        }

        /**
         * Returns whether the user is able to sort the grid by this column.
         * 
         * @return true if the column is sortable by the user, false otherwise
         */
        public boolean isSortable() {
            return state.sortable;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "[propertyId:"
                    + grid.getPropertyIdByColumnId(state.id) + "]";
        }

        /**
         * Sets the ratio with which the column expands.
         * <p>
         * By default, all columns expand equally (treated as if all of them had
         * an expand ratio of 1). Once at least one column gets a defined expand
         * ratio, the implicit expand ratio is removed, and only the defined
         * expand ratios are taken into account.
         * <p>
         * If a column has a defined width ({@link #setWidth(double)}), it
         * overrides this method's effects.
         * <p>
         * <em>Example:</em> A grid with three columns, with expand ratios 0, 1
         * and 2, respectively. The column with a <strong>ratio of 0 is exactly
         * as wide as its contents requires</strong>. The column with a ratio of
         * 1 is as wide as it needs, <strong>plus a third of any excess
         * space</strong>, because we have 3 parts total, and this column
         * reserves only one of those. The column with a ratio of 2, is as wide
         * as it needs to be, <strong>plus two thirds</strong> of the excess
         * width.
         * 
         * @param expandRatio
         *            the expand ratio of this column. {@code 0} to not have it
         *            expand at all. A negative number to clear the expand
         *            value.
         * @throws IllegalStateException
         *             if the column is no longer attached to any grid
         * @see #setWidth(double)
         */
        public Column setExpandRatio(int expandRatio)
                throws IllegalStateException {
            checkColumnIsAttached();

            getState().expandRatio = expandRatio;
            grid.markAsDirty();
            return this;
        }

        /**
         * Returns the column's expand ratio.
         * 
         * @return the column's expand ratio
         * @see #setExpandRatio(int)
         */
        public int getExpandRatio() {
            return getState().expandRatio;
        }

        /**
         * Clears the expand ratio for this column.
         * <p>
         * Equal to calling {@link #setExpandRatio(int) setExpandRatio(-1)}
         * 
         * @throws IllegalStateException
         *             if the column is no longer attached to any grid
         */
        public Column clearExpandRatio() throws IllegalStateException {
            return setExpandRatio(-1);
        }

        /**
         * Sets the minimum width for this column.
         * <p>
         * This defines the minimum guaranteed pixel width of the column
         * <em>when it is set to expand</em>.
         * 
         * @throws IllegalStateException
         *             if the column is no longer attached to any grid
         * @see #setExpandRatio(int)
         */
        public Column setMinimumWidth(double pixels)
                throws IllegalStateException {
            checkColumnIsAttached();

            final double maxwidth = getMaximumWidth();
            if (pixels >= 0 && pixels > maxwidth && maxwidth >= 0) {
                throw new IllegalArgumentException("New minimum width ("
                        + pixels + ") was greater than maximum width ("
                        + maxwidth + ")");
            }
            getState().minWidth = pixels;
            grid.markAsDirty();
            return this;
        }

        /**
         * Return the minimum width for this column.
         * 
         * @return the minimum width for this column
         * @see #setMinimumWidth(double)
         */
        public double getMinimumWidth() {
            return getState().minWidth;
        }

        /**
         * Sets the maximum width for this column.
         * <p>
         * This defines the maximum allowed pixel width of the column
         * <em>when it is set to expand</em>.
         * 
         * @param pixels
         *            the maximum width
         * @throws IllegalStateException
         *             if the column is no longer attached to any grid
         * @see #setExpandRatio(int)
         */
        public Column setMaximumWidth(double pixels) {
            checkColumnIsAttached();

            final double minwidth = getMinimumWidth();
            if (pixels >= 0 && pixels < minwidth && minwidth >= 0) {
                throw new IllegalArgumentException("New maximum width ("
                        + pixels + ") was less than minimum width (" + minwidth
                        + ")");
            }

            getState().maxWidth = pixels;
            grid.markAsDirty();
            return this;
        }

        /**
         * Returns the maximum width for this column.
         * 
         * @return the maximum width for this column
         * @see #setMaximumWidth(double)
         */
        public double getMaximumWidth() {
            return getState().maxWidth;
        }

        /**
         * Sets whether the properties corresponding to this column should be
         * editable when the item editor is active. By default columns are
         * editable.
         * <p>
         * Values in non-editable columns are currently not displayed when the
         * editor is active, but this will probably change in the future. They
         * are not automatically assigned an editor field and, if one is
         * manually assigned, it is not used. Columns that cannot (or should
         * not) be edited even in principle should be set non-editable.
         * 
         * @param editable
         *            {@code true} if this column should be editable,
         *            {@code false} otherwise
         * @return this column
         * 
         * @throws IllegalStateException
         *             if the editor is currently active
         * 
         * @see Grid#editItem(Object)
         * @see Grid#isEditorActive()
         */
        public Column setEditable(boolean editable) {
            checkColumnIsAttached();
            if (grid.isEditorActive()) {
                throw new IllegalStateException(
                        "Cannot change column editable status while the editor is active");
            }
            getState().editable = editable;
            grid.markAsDirty();
            return this;
        }

        /**
         * Returns whether the properties corresponding to this column should be
         * editable when the item editor is active.
         * 
         * @return {@code true} if this column is editable, {@code false}
         *         otherwise
         * 
         * @see Grid#editItem(Object)
         * @see #setEditable(boolean)
         */

        public boolean isEditable() {
            return getState().editable;
        }

        /**
         * Sets the field component used to edit the properties in this column
         * when the item editor is active. If an item has not been set, then the
         * binding is postponed until the item is set using
         * {@link #editItem(Object)}.
         * <p>
         * Setting the field to <code>null</code> clears any previously set
         * field, causing a new field to be created the next time the item
         * editor is opened.
         * 
         * @param editor
         *            the editor field
         * @return this column
         */
        public Column setEditorField(Field<?> editor) {
            grid.setEditorField(getPropertyId(), editor);
            return this;
        }

        /**
         * Returns the editor field used to edit the properties in this column
         * when the item editor is active. Returns null if the column is not
         * {@link Column#isEditable() editable}.
         * <p>
         * When {@link #editItem(Object) editItem} is called, fields are
         * automatically created and bound for any unbound properties.
         * <p>
         * Getting a field before the editor has been opened depends on special
         * support from the {@link FieldGroup} in use. Using this method with a
         * user-provided <code>FieldGroup</code> might cause
         * {@link BindException} to be thrown.
         * 
         * @return the bound field; or <code>null</code> if the respective
         *         column is not editable
         * 
         * @throws IllegalArgumentException
         *             if there is no column for the provided property id
         * @throws BindException
         *             if no field has been configured and there is a problem
         *             building or binding
         */
        public Field<?> getEditorField() {
            return grid.getEditorField(getPropertyId());
        }
    }

    /**
     * An abstract base class for server-side Grid renderers.
     * {@link com.vaadin.client.widget.grid.Renderer Grid renderers}. This class
     * currently extends the AbstractExtension superclass, but this fact should
     * be regarded as an implementation detail and subject to change in a future
     * major or minor Vaadin revision.
     * 
     * @param <T>
     *            the type this renderer knows how to present
     */
    public static abstract class AbstractRenderer<T> extends AbstractExtension
            implements Renderer<T> {

        private final Class<T> presentationType;

        protected AbstractRenderer(Class<T> presentationType) {
            this.presentationType = presentationType;
        }

        /**
         * This method is inherited from AbstractExtension but should never be
         * called directly with an AbstractRenderer.
         */
        @Deprecated
        @Override
        protected Class<Grid> getSupportedParentType() {
            return Grid.class;
        }

        /**
         * This method is inherited from AbstractExtension but should never be
         * called directly with an AbstractRenderer.
         */
        @Deprecated
        @Override
        protected void extend(AbstractClientConnector target) {
            super.extend(target);
        }

        @Override
        public Class<T> getPresentationType() {
            return presentationType;
        }

        @Override
        public JsonValue encode(T value) {
            return encode(value, getPresentationType());
        }

        /**
         * Encodes the given value to JSON.
         * <p>
         * This is a helper method that can be invoked by an
         * {@link #encode(Object) encode(T)} override if serializing a value of
         * type other than {@link #getPresentationType() the presentation type}
         * is desired. For instance, a {@code Renderer<Date>} could first turn a
         * date value into a formatted string and return
         * {@code encode(dateString, String.class)}.
         * 
         * @param value
         *            the value to be encoded
         * @param type
         *            the type of the value
         * @return a JSON representation of the given value
         */
        protected <U> JsonValue encode(U value, Class<U> type) {
            return JsonCodec.encode(value, null, type,
                    getUI().getConnectorTracker()).getEncodedValue();
        }

        /**
         * Gets the item id for a row key.
         * <p>
         * A key is used to identify a particular row on both a server and a
         * client. This method can be used to get the item id for the row key
         * that the client has sent.
         * 
         * @param rowKey
         *            the row key for which to retrieve an item id
         * @return the item id corresponding to {@code key}
         */
        protected Object getItemId(String rowKey) {
            return getParentGrid().getKeyMapper().getItemId(rowKey);
        }

        /**
         * Gets the column for a column id.
         * <p>
         * An id is used to identify a particular column on both a server and a
         * client. This method can be used to get the column for the column id
         * that the client has sent.
         * 
         * @param columnId
         *            the column id for which to retrieve a column
         * @return the column corresponding to {@code columnId}
         */
        protected Column getColumn(String columnId) {
            return getParentGrid().getColumnByColumnId(columnId);
        }

        /**
         * Gets the parent Grid of the renderer.
         * 
         * @return parent grid
         * @throws IllegalStateException
         *             if parent is not Grid
         */
        protected Grid getParentGrid() {
            if (getParent() instanceof Grid) {
                Grid grid = (Grid) getParent();
                return grid;
            } else {
                throw new IllegalStateException(
                        "Renderers can be used only with Grid");
            }
        }
    }

    /**
     * The data source attached to the grid
     */
    private Container.Indexed datasource;

    /**
     * Property id to column instance mapping
     */
    private final Map<Object, Column> columns = new HashMap<Object, Column>();

    /**
     * Key generator for column server-to-client communication
     */
    private final KeyMapper<Object> columnKeys = new KeyMapper<Object>();

    /**
     * The current sort order
     */
    private final List<SortOrder> sortOrder = new ArrayList<SortOrder>();

    /**
     * Property listener for listening to changes in data source properties.
     */
    private final PropertySetChangeListener propertyListener = new PropertySetChangeListener() {

        @Override
        public void containerPropertySetChange(PropertySetChangeEvent event) {
            Collection<?> properties = new HashSet<Object>(event.getContainer()
                    .getContainerPropertyIds());

            // Find columns that need to be removed.
            List<Column> removedColumns = new LinkedList<Column>();
            for (Object propertyId : columns.keySet()) {
                if (!properties.contains(propertyId)) {
                    removedColumns.add(getColumn(propertyId));
                }
            }

            // Actually remove columns.
            for (Column column : removedColumns) {
                Object propertyId = column.getPropertyId();
                internalRemoveColumn(propertyId);
                columnKeys.remove(propertyId);
            }
            datasourceExtension.columnsRemoved(removedColumns);

            // Add new columns
            List<Column> addedColumns = new LinkedList<Column>();
            for (Object propertyId : properties) {
                if (!columns.containsKey(propertyId)) {
                    addedColumns.add(appendColumn(propertyId));
                }
            }
            datasourceExtension.columnsAdded(addedColumns);

            if (getFrozenColumnCount() > columns.size()) {
                setFrozenColumnCount(columns.size());
            }

            // Unset sortable for non-sortable columns.
            if (datasource instanceof Sortable) {
                Collection<?> sortables = ((Sortable) datasource)
                        .getSortableContainerPropertyIds();
                for (Object propertyId : columns.keySet()) {
                    Column column = columns.get(propertyId);
                    if (!sortables.contains(propertyId) && column.isSortable()) {
                        column.setSortable(false);
                    }
                }
            }
        }
    };

    private RpcDataProviderExtension datasourceExtension;

    /**
     * The selection model that is currently in use. Never <code>null</code>
     * after the constructor has been run.
     */
    private SelectionModel selectionModel;

    /**
     * Used to know whether selection change events originate from the server or
     * the client so the selection change handler knows whether the changes
     * should be sent to the client.
     */
    private boolean applyingSelectionFromClient;

    private final Header header = new Header(this);
    private final Footer footer = new Footer(this);

    private Object editedItemId = null;
    private FieldGroup editorFieldGroup = new CustomFieldGroup();

    private CellStyleGenerator cellStyleGenerator;
    private RowStyleGenerator rowStyleGenerator;

    /**
     * <code>true</code> if Grid is using the internal IndexedContainer created
     * in Grid() constructor, or <code>false</code> if the user has set their
     * own Container.
     * 
     * @see #setContainerDataSource()
     * @see #Grid()
     */
    private boolean defaultContainer = true;

    private EditorErrorHandler editorErrorHandler = new DefaultEditorErrorHandler();

    private static final Method SELECTION_CHANGE_METHOD = ReflectTools
            .findMethod(SelectionListener.class, "select", SelectionEvent.class);

    private static final Method SORT_ORDER_CHANGE_METHOD = ReflectTools
            .findMethod(SortListener.class, "sort", SortEvent.class);

    /**
     * Creates a new Grid with a new {@link IndexedContainer} as the data
     * source.
     */
    public Grid() {
        this(null, null);
    }

    /**
     * Creates a new Grid using the given data source.
     * 
     * @param dataSource
     *            the indexed container to use as a data source
     */
    public Grid(final Container.Indexed dataSource) {
        this(null, dataSource);
    }

    /**
     * Creates a new Grid with the given caption and a new
     * {@link IndexedContainer} data source.
     * 
     * @param caption
     *            the caption of the grid
     */
    public Grid(String caption) {
        this(caption, null);
    }

    /**
     * Creates a new Grid with the given caption and data source. If the data
     * source is null, a new {@link IndexedContainer} will be used.
     * 
     * @param caption
     *            the caption of the grid
     * @param dataSource
     *            the indexed container to use as a data source
     */
    public Grid(String caption, Container.Indexed dataSource) {
        if (dataSource == null) {
            internalSetContainerDataSource(new IndexedContainer());
        } else {
            setContainerDataSource(dataSource);
        }
        setCaption(caption);
        initGrid();
    }

    /**
     * Grid initial setup
     */
    private void initGrid() {
        setSelectionMode(SelectionMode.SINGLE);
        addSelectionListener(new SelectionListener() {
            @Override
            public void select(SelectionEvent event) {
                if (applyingSelectionFromClient) {
                    /*
                     * Avoid sending changes back to the client if they
                     * originated from the client. Instead, the RPC handler is
                     * responsible for keeping track of the resulting selection
                     * state and notifying the client if it doens't match the
                     * expectation.
                     */
                    return;
                }

                /*
                 * The rows are pinned here to ensure that the client gets the
                 * correct key from server when the selected row is first
                 * loaded.
                 * 
                 * Once the client has gotten info that it is supposed to select
                 * a row, it will pin the data from the client side as well and
                 * it will be unpinned once it gets deselected. Nothing on the
                 * server side should ever unpin anything from KeyMapper.
                 * Pinning is mostly a client feature and is only used when
                 * selecting something from the server side.
                 */
                for (Object addedItemId : event.getAdded()) {
                    if (!getKeyMapper().isPinned(addedItemId)) {
                        getKeyMapper().pin(addedItemId);
                    }
                }

                getState().selectedKeys = getKeyMapper().getKeys(
                        getSelectedRows());
            }
        });

        registerRpc(new GridServerRpc() {

            @Override
            public void select(List<String> selection) {
                Collection<Object> receivedSelection = getKeyMapper()
                        .getItemIds(selection);

                applyingSelectionFromClient = true;
                try {
                    SelectionModel selectionModel = getSelectionModel();
                    if (selectionModel instanceof SelectionModel.Single
                            && selection.size() <= 1) {
                        Object select = null;
                        if (selection.size() == 1) {
                            select = getKeyMapper().getItemId(selection.get(0));
                        }
                        ((SelectionModel.Single) selectionModel).select(select);
                    } else if (selectionModel instanceof SelectionModel.Multi) {
                        ((SelectionModel.Multi) selectionModel)
                                .setSelected(receivedSelection);
                    } else {
                        throw new IllegalStateException("SelectionModel "
                                + selectionModel.getClass().getSimpleName()
                                + " does not support selecting the given "
                                + selection.size() + " items.");
                    }
                } finally {
                    applyingSelectionFromClient = false;
                }

                Collection<Object> actualSelection = getSelectedRows();

                // Make sure all selected rows are pinned
                for (Object itemId : actualSelection) {
                    if (!getKeyMapper().isPinned(itemId)) {
                        getKeyMapper().pin(itemId);
                    }
                }

                // Don't mark as dirty since this might be the expected state
                getState(false).selectedKeys = getKeyMapper().getKeys(
                        actualSelection);

                JsonObject diffState = getUI().getConnectorTracker()
                        .getDiffState(Grid.this);

                final String diffstateKey = "selectedKeys";

                assert diffState.hasKey(diffstateKey) : "Field name has changed";

                if (receivedSelection.equals(actualSelection)) {
                    /*
                     * We ended up with the same selection state that the client
                     * sent us. There's nothing to send back to the client, just
                     * update the diffstate so subsequent changes will be
                     * detected.
                     */
                    JsonArray diffSelected = Json.createArray();
                    for (String rowKey : getState(false).selectedKeys) {
                        diffSelected.set(diffSelected.length(), rowKey);
                    }
                    diffState.put(diffstateKey, diffSelected);
                } else {
                    /*
                     * Actual selection is not what the client expects. Make
                     * sure the client gets a state change event by clearing the
                     * diffstate and marking as dirty
                     */
                    diffState.remove(diffstateKey);
                    markAsDirty();
                }
            }

            @Override
            public void sort(String[] columnIds, SortDirection[] directions,
                    boolean userOriginated) {
                assert columnIds.length == directions.length;

                List<SortOrder> order = new ArrayList<SortOrder>(
                        columnIds.length);
                for (int i = 0; i < columnIds.length; i++) {
                    Object propertyId = getPropertyIdByColumnId(columnIds[i]);
                    order.add(new SortOrder(propertyId, directions[i]));
                }

                setSortOrder(order, userOriginated);
            }

            @Override
            public void selectAll() {
                assert getSelectionModel() instanceof SelectionModel.Multi : "Not a multi selection model!";

                ((SelectionModel.Multi) getSelectionModel()).selectAll();
            }

            @Override
            public void itemClick(String rowKey, String columnId,
                    MouseEventDetails details) {
                Object itemId = getKeyMapper().getItemId(rowKey);
                Item item = datasource.getItem(itemId);
                Object propertyId = getPropertyIdByColumnId(columnId);
                fireEvent(new ItemClickEvent(Grid.this, item, itemId,
                        propertyId, details));
            }
        });

        registerRpc(new EditorServerRpc() {

            @Override
            public void bind(int rowIndex) {
                boolean success = false;
                try {
                    Object id = getContainerDataSource().getIdByIndex(rowIndex);
                    if (editedItemId == null) {
                        editedItemId = id;
                    }

                    if (editedItemId.equals(id)) {
                        success = true;
                        doEditItem();
                    }
                } catch (Exception e) {
                    handleError(e);
                }
                getEditorRpc().confirmBind(success);
            }

            @Override
            public void cancel(int rowIndex) {
                try {
                    // For future proofing even though cannot currently fail
                    doCancelEditor();
                } catch (Exception e) {
                    handleError(e);
                }
            }

            @Override
            public void save(int rowIndex) {
                List<String> errorColumnIds = null;
                String errorMessage = null;
                boolean success = false;
                try {
                    saveEditor();
                    success = true;
                } catch (CommitException e) {
                    try {
                        CommitErrorEvent event = new CommitErrorEvent(
                                Grid.this, e);
                        getEditorErrorHandler().commitError(event);

                        errorMessage = event.getUserErrorMessage();

                        errorColumnIds = new ArrayList<String>();
                        for (Column column : event.getErrorColumns()) {
                            errorColumnIds.add(column.state.id);
                        }
                    } catch (Exception ee) {
                        // A badly written error handler can throw an exception,
                        // which would lock up the Grid
                        handleError(ee);
                    }
                } catch (Exception e) {
                    handleError(e);
                }
                getEditorRpc().confirmSave(success, errorMessage,
                        errorColumnIds);
            }

            private void handleError(Exception e) {
                com.vaadin.server.ErrorEvent.findErrorHandler(Grid.this).error(
                        new ConnectorErrorEvent(Grid.this, e));
            }
        });
    }

    @Override
    public void beforeClientResponse(boolean initial) {
        try {
            header.sanityCheck();
            footer.sanityCheck();
        } catch (Exception e) {
            e.printStackTrace();
            setComponentError(new ErrorMessage() {

                @Override
                public ErrorLevel getErrorLevel() {
                    return ErrorLevel.CRITICAL;
                }

                @Override
                public String getFormattedHtmlMessage() {
                    return "Incorrectly merged cells";
                }

            });
        }

        super.beforeClientResponse(initial);
    }

    /**
     * Sets the grid data source.
     * 
     * @param container
     *            The container data source. Cannot be null.
     * @throws IllegalArgumentException
     *             if the data source is null
     */
    public void setContainerDataSource(Container.Indexed container) {
        defaultContainer = false;
        internalSetContainerDataSource(container);
    }

    private void internalSetContainerDataSource(Container.Indexed container) {
        if (container == null) {
            throw new IllegalArgumentException(
                    "Cannot set the datasource to null");
        }
        if (datasource == container) {
            return;
        }

        // Remove old listeners
        if (datasource instanceof PropertySetChangeNotifier) {
            ((PropertySetChangeNotifier) datasource)
                    .removePropertySetChangeListener(propertyListener);
        }

        if (datasourceExtension != null) {
            removeExtension(datasourceExtension);
        }

        datasource = container;

        resetEditor();

        //
        // Adjust sort order
        //

        if (container instanceof Container.Sortable) {

            // If the container is sortable, go through the current sort order
            // and match each item to the sortable properties of the new
            // container. If the new container does not support an item in the
            // current sort order, that item is removed from the current sort
            // order list.
            Collection<?> sortableProps = ((Container.Sortable) getContainerDataSource())
                    .getSortableContainerPropertyIds();

            Iterator<SortOrder> i = sortOrder.iterator();
            while (i.hasNext()) {
                if (!sortableProps.contains(i.next().getPropertyId())) {
                    i.remove();
                }
            }

            sort(false);
        } else {
            // Clear sorting order. Don't sort.
            sortOrder.clear();
        }

        datasourceExtension = new RpcDataProviderExtension(container);
        datasourceExtension.extend(this, columnKeys);

        /*
         * selectionModel == null when the invocation comes from the
         * constructor.
         */
        if (selectionModel != null) {
            selectionModel.reset();
        }

        // Listen to changes in properties and remove columns if needed
        if (datasource instanceof PropertySetChangeNotifier) {
            ((PropertySetChangeNotifier) datasource)
                    .addPropertySetChangeListener(propertyListener);
        }
        /*
         * activeRowHandler will be updated by the client-side request that
         * occurs on container change - no need to actively re-insert any
         * ValueChangeListeners at this point.
         */

        setFrozenColumnCount(0);

        if (columns.isEmpty()) {
            // Add columns
            for (Object propertyId : datasource.getContainerPropertyIds()) {
                Column column = appendColumn(propertyId);

                // Initial sorting is defined by container
                if (datasource instanceof Sortable) {
                    column.setSortable(((Sortable) datasource)
                            .getSortableContainerPropertyIds().contains(
                                    propertyId));
                } else {
                    column.setSortable(false);
                }
            }
        } else {
            Collection<?> properties = datasource.getContainerPropertyIds();
            for (Object property : columns.keySet()) {
                if (!properties.contains(property)) {
                    throw new IllegalStateException(
                            "Found at least one column in Grid that does not exist in the given container: "
                                    + property
                                    + " with the header \""
                                    + getColumn(property).getHeaderCaption()
                                    + "\"");
                }

                if (!(datasource instanceof Sortable)
                        || !((Sortable) datasource)
                                .getSortableContainerPropertyIds().contains(
                                        property)) {
                    columns.get(property).setSortable(false);
                }
            }
        }
    }

    /**
     * Returns the grid data source.
     * 
     * @return the container data source of the grid
     */
    public Container.Indexed getContainerDataSource() {
        return datasource;
    }

    /**
     * Returns a column based on the property id
     * 
     * @param propertyId
     *            the property id of the column
     * @return the column or <code>null</code> if not found
     */
    public Column getColumn(Object propertyId) {
        return columns.get(propertyId);
    }

    /**
     * Returns a copy of currently configures columns in their current visual
     * order in this Grid.
     * 
     * @return unmodifiable copy of current columns in visual order
     */
    public List<Column> getColumns() {
        List<Column> columns = new ArrayList<Grid.Column>();
        for (String columnId : getState(false).columnOrder) {
            columns.add(getColumnByColumnId(columnId));
        }
        return Collections.unmodifiableList(columns);
    }

    /**
     * Adds a new Column to Grid. Also adds the property to container with data
     * type String, if property for column does not exist in it. Default value
     * for the new property is an empty String.
     * <p>
     * Note that adding a new property is only done for the default container
     * that Grid sets up with the default constructor.
     * 
     * @param propertyId
     *            the property id of the new column
     * @return the new column
     * 
     * @throws IllegalStateException
     *             if column for given property already exists in this grid
     */

    public Column addColumn(Object propertyId) throws IllegalStateException {
        if (datasource.getContainerPropertyIds().contains(propertyId)
                && !columns.containsKey(propertyId)) {
            appendColumn(propertyId);
        } else {
            addColumnProperty(propertyId, String.class, "");
        }

        // Inform the data provider of this new column.
        Column column = getColumn(propertyId);
        List<Column> addedColumns = new ArrayList<Column>();
        addedColumns.add(column);
        datasourceExtension.columnsAdded(addedColumns);

        return column;
    }

    /**
     * Adds a new Column to Grid. This function makes sure that the property
     * with the given id and data type exists in the container. If property does
     * not exists, it will be created.
     * <p>
     * Default value for the new property is 0 if type is Integer, Double and
     * Float. If type is String, default value is an empty string. For all other
     * types the default value is null.
     * <p>
     * Note that adding a new property is only done for the default container
     * that Grid sets up with the default constructor.
     * 
     * @param propertyId
     *            the property id of the new column
     * @param type
     *            the data type for the new property
     * @return the new column
     * 
     * @throws IllegalStateException
     *             if column for given property already exists in this grid or
     *             property already exists in the container with wrong type
     */
    public Column addColumn(Object propertyId, Class<?> type) {
        addColumnProperty(propertyId, type, null);
        return getColumn(propertyId);
    }

    protected void addColumnProperty(Object propertyId, Class<?> type,
            Object defaultValue) throws IllegalStateException {
        if (!defaultContainer) {
            throw new IllegalStateException(
                    "Container for this Grid is not a default container from Grid() constructor");
        }

        if (!columns.containsKey(propertyId)) {
            if (!datasource.getContainerPropertyIds().contains(propertyId)) {
                datasource.addContainerProperty(propertyId, type, defaultValue);
            } else {
                Property<?> containerProperty = datasource
                        .getContainerProperty(datasource.firstItemId(),
                                propertyId);
                if (containerProperty.getType() == type) {
                    appendColumn(propertyId);
                } else {
                    throw new IllegalStateException(
                            "DataSource already has the given property "
                                    + propertyId + " with a different type");
                }
            }
        } else {
            throw new IllegalStateException(
                    "Grid already has a column for property " + propertyId);
        }
    }

    /**
     * Removes all columns from this Grid.
     */
    public void removeAllColumns() {
        List<Column> removed = new ArrayList<Column>(columns.values());
        Set<Object> properties = new HashSet<Object>(columns.keySet());
        for (Object propertyId : properties) {
            removeColumn(propertyId);
        }
        datasourceExtension.columnsRemoved(removed);
    }

    /**
     * Used internally by the {@link Grid} to get a {@link Column} by
     * referencing its generated state id. Also used by {@link Column} to verify
     * if it has been detached from the {@link Grid}.
     * 
     * @param columnId
     *            the client id generated for the column when the column is
     *            added to the grid
     * @return the column with the id or <code>null</code> if not found
     */
    Column getColumnByColumnId(String columnId) {
        Object propertyId = getPropertyIdByColumnId(columnId);
        return getColumn(propertyId);
    }

    /**
     * Used internally by the {@link Grid} to get a property id by referencing
     * the columns generated state id.
     * 
     * @param columnId
     *            The state id of the column
     * @return The column instance or null if not found
     */
    Object getPropertyIdByColumnId(String columnId) {
        return columnKeys.get(columnId);
    }

    @Override
    protected GridState getState() {
        return (GridState) super.getState();
    }

    @Override
    protected GridState getState(boolean markAsDirty) {
        return (GridState) super.getState(markAsDirty);
    }

    /**
     * Creates a new column based on a property id and appends it as the last
     * column.
     * 
     * @param datasourcePropertyId
     *            The property id of a property in the datasource
     */
    private Column appendColumn(Object datasourcePropertyId) {
        if (datasourcePropertyId == null) {
            throw new IllegalArgumentException("Property id cannot be null");
        }
        assert datasource.getContainerPropertyIds().contains(
                datasourcePropertyId) : "Datasource should contain the property id";

        GridColumnState columnState = new GridColumnState();
        columnState.id = columnKeys.key(datasourcePropertyId);

        Column column = new Column(this, columnState, datasourcePropertyId);
        columns.put(datasourcePropertyId, column);

        getState().columns.add(columnState);
        getState().columnOrder.add(columnState.id);
        header.addColumn(datasourcePropertyId);
        footer.addColumn(datasourcePropertyId);

        column.setHeaderCaption(SharedUtil.propertyIdToHumanFriendly(String
                .valueOf(datasourcePropertyId)));

        if (datasource instanceof Sortable
                && ((Sortable) datasource).getSortableContainerPropertyIds()
                        .contains(datasourcePropertyId)) {
            column.setSortable(true);
        }

        return column;
    }

    /**
     * Removes a column from Grid based on a property id.
     * 
     * @param propertyId
     *            The property id of column to be removed
     * 
     * @throws IllegalArgumentException
     *             if there is no column for given property id in this grid
     */
    public void removeColumn(Object propertyId) throws IllegalArgumentException {
        if (!columns.keySet().contains(propertyId)) {
            throw new IllegalArgumentException(
                    "There is no column for given property id " + propertyId);
        }

        List<Column> removed = new ArrayList<Column>();
        removed.add(getColumn(propertyId));
        internalRemoveColumn(propertyId);
        datasourceExtension.columnsRemoved(removed);
    }

    private void internalRemoveColumn(Object propertyId) {
        setEditorField(propertyId, null);
        header.removeColumn(propertyId);
        footer.removeColumn(propertyId);
        Column column = columns.remove(propertyId);
        getState().columnOrder.remove(columnKeys.key(propertyId));
        getState().columns.remove(column.getState());
        removeExtension(column.getRenderer());
    }

    /**
     * Sets a new column order for the grid. All columns which are not ordered
     * here will remain in the order they were before as the last columns of
     * grid.
     * 
     * @param propertyIds
     *            properties in the order columns should be
     */
    public void setColumnOrder(Object... propertyIds) {
        List<String> columnOrder = new ArrayList<String>();
        for (Object propertyId : propertyIds) {
            if (columns.containsKey(propertyId)) {
                columnOrder.add(columnKeys.key(propertyId));
            } else {
                throw new IllegalArgumentException(
                        "Grid does not contain column for property "
                                + String.valueOf(propertyId));
            }
        }

        List<String> stateColumnOrder = getState().columnOrder;
        if (stateColumnOrder.size() != columnOrder.size()) {
            stateColumnOrder.removeAll(columnOrder);
            columnOrder.addAll(stateColumnOrder);
        }
        getState().columnOrder = columnOrder;
    }

    /**
     * Sets the number of frozen columns in this grid. Setting the count to 0
     * means that no data columns will be frozen, but the built-in selection
     * checkbox column will still be frozen if it's in use. Setting the count to
     * -1 will also disable the selection column.
     * <p>
     * The default value is 0.
     * 
     * @param numberOfColumns
     *            the number of columns that should be frozen
     * 
     * @throws IllegalArgumentException
     *             if the column count is < 0 or > the number of visible columns
     */
    public void setFrozenColumnCount(int numberOfColumns) {
        if (numberOfColumns < -1 || numberOfColumns > columns.size()) {
            throw new IllegalArgumentException(
                    "count must be between -1 and the current number of columns ("
                            + columns + ")");
        }

        getState().frozenColumnCount = numberOfColumns;
    }

    /**
     * Gets the number of frozen columns in this grid. 0 means that no data
     * columns will be frozen, but the built-in selection checkbox column will
     * still be frozen if it's in use. -1 means that not even the selection
     * column is frozen.
     * 
     * @see #setFrozenColumnCount(int)
     * 
     * @return the number of frozen columns
     */
    public int getFrozenColumnCount() {
        return getState(false).frozenColumnCount;
    }

    /**
     * Scrolls to a certain item, using {@link ScrollDestination#ANY}.
     * 
     * @param itemId
     *            id of item to scroll to.
     * @throws IllegalArgumentException
     *             if the provided id is not recognized by the data source.
     */
    public void scrollTo(Object itemId) throws IllegalArgumentException {
        scrollTo(itemId, ScrollDestination.ANY);
    }

    /**
     * Scrolls to a certain item, using user-specified scroll destination.
     * 
     * @param itemId
     *            id of item to scroll to.
     * @param destination
     *            value specifying desired position of scrolled-to row.
     * @throws IllegalArgumentException
     *             if the provided id is not recognized by the data source.
     */
    public void scrollTo(Object itemId, ScrollDestination destination)
            throws IllegalArgumentException {

        int row = datasource.indexOfId(itemId);

        if (row == -1) {
            throw new IllegalArgumentException(
                    "Item with specified ID does not exist in data source");
        }

        GridClientRpc clientRPC = getRpcProxy(GridClientRpc.class);
        clientRPC.scrollToRow(row, destination);
    }

    /**
     * Scrolls to the beginning of the first data row.
     */
    public void scrollToStart() {
        GridClientRpc clientRPC = getRpcProxy(GridClientRpc.class);
        clientRPC.scrollToStart();
    }

    /**
     * Scrolls to the end of the last data row.
     */
    public void scrollToEnd() {
        GridClientRpc clientRPC = getRpcProxy(GridClientRpc.class);
        clientRPC.scrollToEnd();
    }

    /**
     * Sets the number of rows that should be visible in Grid's body, while
     * {@link #getHeightMode()} is {@link HeightMode#ROW}.
     * <p>
     * If Grid is currently not in {@link HeightMode#ROW}, the given value is
     * remembered, and applied once the mode is applied.
     * 
     * @param rows
     *            The height in terms of number of rows displayed in Grid's
     *            body. If Grid doesn't contain enough rows, white space is
     *            displayed instead. If <code>null</code> is given, then Grid's
     *            height is undefined
     * @throws IllegalArgumentException
     *             if {@code rows} is zero or less
     * @throws IllegalArgumentException
     *             if {@code rows} is {@link Double#isInifinite(double)
     *             infinite}
     * @throws IllegalArgumentException
     *             if {@code rows} is {@link Double#isNaN(double) NaN}
     */
    public void setHeightByRows(double rows) {
        if (rows <= 0.0d) {
            throw new IllegalArgumentException(
                    "More than zero rows must be shown.");
        } else if (Double.isInfinite(rows)) {
            throw new IllegalArgumentException(
                    "Grid doesn't support infinite heights");
        } else if (Double.isNaN(rows)) {
            throw new IllegalArgumentException("NaN is not a valid row count");
        }

        getState().heightByRows = rows;
    }

    /**
     * Gets the amount of rows in Grid's body that are shown, while
     * {@link #getHeightMode()} is {@link HeightMode#ROW}.
     * 
     * @return the amount of rows that are being shown in Grid's body
     * @see #setHeightByRows(double)
     */
    public double getHeightByRows() {
        return getState(false).heightByRows;
    }

    /**
     * {@inheritDoc}
     * <p>
     * <em>Note:</em> This method will change the widget's size in the browser
     * only if {@link #getHeightMode()} returns {@link HeightMode#CSS}.
     * 
     * @see #setHeightMode(HeightMode)
     */
    @Override
    public void setHeight(float height, Unit unit) {
        super.setHeight(height, unit);
    }

    /**
     * Defines the mode in which the Grid widget's height is calculated.
     * <p>
     * If {@link HeightMode#CSS} is given, Grid will respect the values given
     * via a {@code setHeight}-method, and behave as a traditional Component.
     * <p>
     * If {@link HeightMode#ROW} is given, Grid will make sure that the body
     * will display as many rows as {@link #getHeightByRows()} defines.
     * <em>Note:</em> If headers/footers are inserted or removed, the widget
     * will resize itself to still display the required amount of rows in its
     * body. It also takes the horizontal scrollbar into account.
     * 
     * @param heightMode
     *            the mode in to which Grid should be set
     */
    public void setHeightMode(HeightMode heightMode) {
        /*
         * This method is a workaround for the fact that Vaadin re-applies
         * widget dimensions (height/width) on each state change event. The
         * original design was to have setHeight an setHeightByRow be equals,
         * and whichever was called the latest was considered in effect.
         * 
         * But, because of Vaadin always calling setHeight on the widget, this
         * approach doesn't work.
         */

        getState().heightMode = heightMode;
    }

    /**
     * Returns the current {@link HeightMode} the Grid is in.
     * <p>
     * Defaults to {@link HeightMode#CSS}.
     * 
     * @return the current HeightMode
     */
    public HeightMode getHeightMode() {
        return getState(false).heightMode;
    }

    /* Selection related methods: */

    /**
     * Takes a new {@link SelectionModel} into use.
     * <p>
     * The SelectionModel that is previously in use will have all its items
     * deselected.
     * <p>
     * If the given SelectionModel is already in use, this method does nothing.
     * 
     * @param selectionModel
     *            the new SelectionModel to use
     * @throws IllegalArgumentException
     *             if {@code selectionModel} is <code>null</code>
     */
    public void setSelectionModel(SelectionModel selectionModel)
            throws IllegalArgumentException {
        if (selectionModel == null) {
            throw new IllegalArgumentException(
                    "Selection model may not be null");
        }

        if (this.selectionModel != selectionModel) {
            // this.selectionModel is null on init
            if (this.selectionModel != null) {
                this.selectionModel.reset();
                this.selectionModel.setGrid(null);
            }

            this.selectionModel = selectionModel;
            this.selectionModel.setGrid(this);
            this.selectionModel.reset();

            if (selectionModel.getClass().equals(SingleSelectionModel.class)) {
                getState().selectionMode = SharedSelectionMode.SINGLE;
            } else if (selectionModel.getClass().equals(
                    MultiSelectionModel.class)) {
                getState().selectionMode = SharedSelectionMode.MULTI;
            } else if (selectionModel.getClass().equals(NoSelectionModel.class)) {
                getState().selectionMode = SharedSelectionMode.NONE;
            } else {
                throw new UnsupportedOperationException("Grid currently "
                        + "supports only its own bundled selection models");
            }
        }
    }

    /**
     * Returns the currently used {@link SelectionModel}.
     * 
     * @return the currently used SelectionModel
     */
    public SelectionModel getSelectionModel() {
        return selectionModel;
    }

    /**
     * Sets the Grid's selection mode.
     * <p>
     * Grid supports three selection modes: multiselect, single select and no
     * selection, and this is a convenience method for choosing between one of
     * them.
     * <p>
     * Technically, this method is a shortcut that can be used instead of
     * calling {@code setSelectionModel} with a specific SelectionModel
     * instance. Grid comes with three built-in SelectionModel classes, and the
     * {@link SelectionMode} enum represents each of them.
     * <p>
     * Essentially, the two following method calls are equivalent:
     * <p>
     * <code><pre>
     * grid.setSelectionMode(SelectionMode.MULTI);
     * grid.setSelectionModel(new MultiSelectionMode());
     * </pre></code>
     * 
     * 
     * @param selectionMode
     *            the selection mode to switch to
     * @return The {@link SelectionModel} instance that was taken into use
     * @throws IllegalArgumentException
     *             if {@code selectionMode} is <code>null</code>
     * @see SelectionModel
     */
    public SelectionModel setSelectionMode(final SelectionMode selectionMode)
            throws IllegalArgumentException {
        if (selectionMode == null) {
            throw new IllegalArgumentException("selection mode may not be null");
        }
        final SelectionModel newSelectionModel = selectionMode.createModel();
        setSelectionModel(newSelectionModel);
        return newSelectionModel;
    }

    /**
     * Checks whether an item is selected or not.
     * 
     * @param itemId
     *            the item id to check for
     * @return <code>true</code> iff the item is selected
     */
    // keep this javadoc in sync with SelectionModel.isSelected
    public boolean isSelected(Object itemId) {
        return selectionModel.isSelected(itemId);
    }

    /**
     * Returns a collection of all the currently selected itemIds.
     * <p>
     * This method is a shorthand that delegates to the
     * {@link #getSelectionModel() selection model}.
     * 
     * @return a collection of all the currently selected itemIds
     */
    // keep this javadoc in sync with SelectionModel.getSelectedRows
    public Collection<Object> getSelectedRows() {
        return getSelectionModel().getSelectedRows();
    }

    /**
     * Gets the item id of the currently selected item.
     * <p>
     * This method is a shorthand that delegates to the
     * {@link #getSelectionModel() selection model}. Only
     * {@link SelectionModel.Single} is supported.
     * 
     * @return the item id of the currently selected item, or <code>null</code>
     *         if nothing is selected
     * @throws IllegalStateException
     *             if the selection model does not implement
     *             {@code SelectionModel.Single}
     */
    // keep this javadoc in sync with SelectionModel.Single.getSelectedRow
    public Object getSelectedRow() throws IllegalStateException {
        if (selectionModel instanceof SelectionModel.Single) {
            return ((SelectionModel.Single) selectionModel).getSelectedRow();
        } else if (selectionModel instanceof SelectionModel.Multi) {
            throw new IllegalStateException("Cannot get unique selected row: "
                    + "Grid is in multiselect mode "
                    + "(the current selection model is "
                    + selectionModel.getClass().getName() + ").");
        } else if (selectionModel instanceof SelectionModel.None) {
            throw new IllegalStateException("Cannot get selected row: "
                    + "Grid selection is disabled "
                    + "(the current selection model is "
                    + selectionModel.getClass().getName() + ").");
        } else {
            throw new IllegalStateException("Cannot get selected row: "
                    + "Grid selection model does not implement "
                    + SelectionModel.Single.class.getName() + " or "
                    + SelectionModel.Multi.class.getName()
                    + "(the current model is "
                    + selectionModel.getClass().getName() + ").");
        }
    }

    /**
     * Marks an item as selected.
     * <p>
     * This method is a shorthand that delegates to the
     * {@link #getSelectionModel() selection model}. Only
     * {@link SelectionModel.Single} and {@link SelectionModel.Multi} are
     * supported.
     * 
     * @param itemIds
     *            the itemId to mark as selected
     * @return <code>true</code> if the selection state changed,
     *         <code>false</code> if the itemId already was selected
     * @throws IllegalArgumentException
     *             if the {@code itemId} doesn't exist in the currently active
     *             Container
     * @throws IllegalStateException
     *             if the selection was illegal. One such reason might be that
     *             the implementation already had an item selected, and that
     *             needs to be explicitly deselected before re-selecting
     *             something.
     * @throws IllegalStateException
     *             if the selection model does not implement
     *             {@code SelectionModel.Single} or {@code SelectionModel.Multi}
     */
    // keep this javadoc in sync with SelectionModel.Single.select
    public boolean select(Object itemId) throws IllegalArgumentException,
            IllegalStateException {
        if (selectionModel instanceof SelectionModel.Single) {
            return ((SelectionModel.Single) selectionModel).select(itemId);
        } else if (selectionModel instanceof SelectionModel.Multi) {
            return ((SelectionModel.Multi) selectionModel).select(itemId);
        } else if (selectionModel instanceof SelectionModel.None) {
            throw new IllegalStateException("Cannot select row '" + itemId
                    + "': Grid selection is disabled "
                    + "(the current selection model is "
                    + selectionModel.getClass().getName() + ").");
        } else {
            throw new IllegalStateException("Cannot select row '" + itemId
                    + "': Grid selection model does not implement "
                    + SelectionModel.Single.class.getName() + " or "
                    + SelectionModel.Multi.class.getName()
                    + "(the current model is "
                    + selectionModel.getClass().getName() + ").");
        }
    }

    /**
     * Marks an item as unselected.
     * <p>
     * This method is a shorthand that delegates to the
     * {@link #getSelectionModel() selection model}. Only
     * {@link SelectionModel.Single} and {@link SelectionModel.Multi} are
     * supported.
     * 
     * @param itemId
     *            the itemId to remove from being selected
     * @return <code>true</code> if the selection state changed,
     *         <code>false</code> if the itemId was already selected
     * @throws IllegalArgumentException
     *             if the {@code itemId} doesn't exist in the currently active
     *             Container
     * @throws IllegalStateException
     *             if the deselection was illegal. One such reason might be that
     *             the implementation requires one or more items to be selected
     *             at all times.
     * @throws IllegalStateException
     *             if the selection model does not implement
     *             {@code SelectionModel.Single} or {code SelectionModel.Multi}
     */
    // keep this javadoc in sync with SelectionModel.Single.deselect
    public boolean deselect(Object itemId) throws IllegalStateException {
        if (selectionModel instanceof SelectionModel.Single) {
            if (isSelected(itemId)) {
                return ((SelectionModel.Single) selectionModel).select(null);
            }
            return false;
        } else if (selectionModel instanceof SelectionModel.Multi) {
            return ((SelectionModel.Multi) selectionModel).deselect(itemId);
        } else if (selectionModel instanceof SelectionModel.None) {
            throw new IllegalStateException("Cannot deselect row '" + itemId
                    + "': Grid selection is disabled "
                    + "(the current selection model is "
                    + selectionModel.getClass().getName() + ").");
        } else {
            throw new IllegalStateException("Cannot deselect row '" + itemId
                    + "': Grid selection model does not implement "
                    + SelectionModel.Single.class.getName() + " or "
                    + SelectionModel.Multi.class.getName()
                    + "(the current model is "
                    + selectionModel.getClass().getName() + ").");
        }
    }

    /**
     * Fires a selection change event.
     * <p>
     * <strong>Note:</strong> This is not a method that should be called by
     * application logic. This method is publicly accessible only so that
     * {@link SelectionModel SelectionModels} would be able to inform Grid of
     * these events.
     * 
     * @param addedSelections
     *            the selections that were added by this event
     * @param removedSelections
     *            the selections that were removed by this event
     */
    public void fireSelectionEvent(Collection<Object> oldSelection,
            Collection<Object> newSelection) {
        fireEvent(new SelectionEvent(this, oldSelection, newSelection));
    }

    @Override
    public void addSelectionListener(SelectionListener listener) {
        addListener(SelectionEvent.class, listener, SELECTION_CHANGE_METHOD);
    }

    @Override
    public void removeSelectionListener(SelectionListener listener) {
        removeListener(SelectionEvent.class, listener, SELECTION_CHANGE_METHOD);
    }

    /**
     * Gets the
     * {@link com.vaadin.data.RpcDataProviderExtension.DataProviderKeyMapper
     * DataProviderKeyMapper} being used by the data source.
     * 
     * @return the key mapper being used by the data source
     */
    DataProviderKeyMapper getKeyMapper() {
        return datasourceExtension.getKeyMapper();
    }

    /**
     * Adds a renderer to this grid's connector hierarchy.
     * 
     * @param renderer
     *            the renderer to add
     */
    void addRenderer(Renderer<?> renderer) {
        addExtension(renderer);
    }

    /**
     * Sets the current sort order using the fluid Sort API. Read the
     * documentation for {@link Sort} for more information.
     * <p>
     * <em>Note:</em> Sorting by a property that has no column in Grid will hide
     * all possible sorting indicators.
     * 
     * @param s
     *            a sort instance
     * 
     * @throws IllegalStateException
     *             if container is not sortable (does not implement
     *             Container.Sortable)
     * @throws IllegalArgumentException
     *             if trying to sort by non-existing property
     */
    public void sort(Sort s) {
        setSortOrder(s.build());
    }

    /**
     * Sort this Grid in ascending order by a specified property.
     * <p>
     * <em>Note:</em> Sorting by a property that has no column in Grid will hide
     * all possible sorting indicators.
     * 
     * @param propertyId
     *            a property ID
     * 
     * @throws IllegalStateException
     *             if container is not sortable (does not implement
     *             Container.Sortable)
     * @throws IllegalArgumentException
     *             if trying to sort by non-existing property
     */
    public void sort(Object propertyId) {
        sort(propertyId, SortDirection.ASCENDING);
    }

    /**
     * Sort this Grid in user-specified {@link SortOrder} by a property.
     * <p>
     * <em>Note:</em> Sorting by a property that has no column in Grid will hide
     * all possible sorting indicators.
     * 
     * @param propertyId
     *            a property ID
     * @param direction
     *            a sort order value (ascending/descending)
     * 
     * @throws IllegalStateException
     *             if container is not sortable (does not implement
     *             Container.Sortable)
     * @throws IllegalArgumentException
     *             if trying to sort by non-existing property
     */
    public void sort(Object propertyId, SortDirection direction) {
        sort(Sort.by(propertyId, direction));
    }

    /**
     * Clear the current sort order, and re-sort the grid.
     */
    public void clearSortOrder() {
        sortOrder.clear();
        sort(false);
    }

    /**
     * Sets the sort order to use.
     * <p>
     * <em>Note:</em> Sorting by a property that has no column in Grid will hide
     * all possible sorting indicators.
     * 
     * @param order
     *            a sort order list.
     * 
     * @throws IllegalStateException
     *             if container is not sortable (does not implement
     *             Container.Sortable)
     * @throws IllegalArgumentException
     *             if order is null or trying to sort by non-existing property
     */
    public void setSortOrder(List<SortOrder> order) {
        setSortOrder(order, false);
    }

    private void setSortOrder(List<SortOrder> order, boolean userOriginated)
            throws IllegalStateException, IllegalArgumentException {
        if (!(getContainerDataSource() instanceof Container.Sortable)) {
            throw new IllegalStateException(
                    "Attached container is not sortable (does not implement Container.Sortable)");
        }

        if (order == null) {
            throw new IllegalArgumentException("Order list may not be null!");
        }

        sortOrder.clear();

        Collection<?> sortableProps = ((Container.Sortable) getContainerDataSource())
                .getSortableContainerPropertyIds();

        for (SortOrder o : order) {
            if (!sortableProps.contains(o.getPropertyId())) {
                throw new IllegalArgumentException(
                        "Property "
                                + o.getPropertyId()
                                + " does not exist or is not sortable in the current container");
            }
        }

        sortOrder.addAll(order);
        sort(userOriginated);
    }

    /**
     * Get the current sort order list.
     * 
     * @return a sort order list
     */
    public List<SortOrder> getSortOrder() {
        return Collections.unmodifiableList(sortOrder);
    }

    /**
     * Apply sorting to data source.
     */
    private void sort(boolean userOriginated) {

        Container c = getContainerDataSource();
        if (c instanceof Container.Sortable) {
            Container.Sortable cs = (Container.Sortable) c;

            final int items = sortOrder.size();
            Object[] propertyIds = new Object[items];
            boolean[] directions = new boolean[items];

            SortDirection[] stateDirs = new SortDirection[items];

            for (int i = 0; i < items; ++i) {
                SortOrder order = sortOrder.get(i);

                stateDirs[i] = order.getDirection();
                propertyIds[i] = order.getPropertyId();
                switch (order.getDirection()) {
                case ASCENDING:
                    directions[i] = true;
                    break;
                case DESCENDING:
                    directions[i] = false;
                    break;
                default:
                    throw new IllegalArgumentException("getDirection() of "
                            + order + " returned an unexpected value");
                }
            }

            cs.sort(propertyIds, directions);

            fireEvent(new SortEvent(this, new ArrayList<SortOrder>(sortOrder),
                    userOriginated));

            if (columns.keySet().containsAll(Arrays.asList(propertyIds))) {
                String[] columnKeys = new String[items];
                for (int i = 0; i < items; ++i) {
                    columnKeys[i] = this.columnKeys.key(propertyIds[i]);
                }
                getState().sortColumns = columnKeys;
                getState(false).sortDirs = stateDirs;
            } else {
                // Not all sorted properties are in Grid. Remove any indicators.
                getState().sortColumns = new String[] {};
                getState(false).sortDirs = new SortDirection[] {};
            }
        } else {
            throw new IllegalStateException(
                    "Container is not sortable (does not implement Container.Sortable)");
        }
    }

    /**
     * Adds a sort order change listener that gets notified when the sort order
     * changes.
     * 
     * @param listener
     *            the sort order change listener to add
     */
    @Override
    public void addSortListener(SortListener listener) {
        addListener(SortEvent.class, listener, SORT_ORDER_CHANGE_METHOD);
    }

    /**
     * Removes a sort order change listener previously added using
     * {@link #addSortListener(SortListener)}.
     * 
     * @param listener
     *            the sort order change listener to remove
     */
    @Override
    public void removeSortListener(SortListener listener) {
        removeListener(SortEvent.class, listener, SORT_ORDER_CHANGE_METHOD);
    }

    /* Grid Headers */

    /**
     * Returns the header section of this grid. The default header contains a
     * single row displaying the column captions.
     * 
     * @return the header
     */
    protected Header getHeader() {
        return header;
    }

    /**
     * Gets the header row at given index.
     * 
     * @param rowIndex
     *            0 based index for row. Counted from top to bottom
     * @return header row at given index
     * @throws IllegalArgumentException
     *             if no row exists at given index
     */
    public HeaderRow getHeaderRow(int rowIndex) {
        return header.getRow(rowIndex);
    }

    /**
     * Inserts a new row at the given position to the header section. Shifts the
     * row currently at that position and any subsequent rows down (adds one to
     * their indices).
     * 
     * @param index
     *            the position at which to insert the row
     * @return the new row
     * 
     * @throws IllegalArgumentException
     *             if the index is less than 0 or greater than row count
     * @see #appendHeaderRow()
     * @see #prependHeaderRow()
     * @see #removeHeaderRow(HeaderRow)
     * @see #removeHeaderRow(int)
     */
    public HeaderRow addHeaderRowAt(int index) {
        return header.addRowAt(index);
    }

    /**
     * Adds a new row at the bottom of the header section.
     * 
     * @return the new row
     * @see #prependHeaderRow()
     * @see #addHeaderRowAt(int)
     * @see #removeHeaderRow(HeaderRow)
     * @see #removeHeaderRow(int)
     */
    public HeaderRow appendHeaderRow() {
        return header.appendRow();
    }

    /**
     * Returns the current default row of the header section. The default row is
     * a special header row providing a user interface for sorting columns.
     * Setting a header text for column updates cells in the default header.
     * 
     * @return the default row or null if no default row set
     */
    public HeaderRow getDefaultHeaderRow() {
        return header.getDefaultRow();
    }

    /**
     * Gets the row count for the header section.
     * 
     * @return row count
     */
    public int getHeaderRowCount() {
        return header.getRowCount();
    }

    /**
     * Adds a new row at the top of the header section.
     * 
     * @return the new row
     * @see #appendHeaderRow()
     * @see #addHeaderRowAt(int)
     * @see #removeHeaderRow(HeaderRow)
     * @see #removeHeaderRow(int)
     */
    public HeaderRow prependHeaderRow() {
        return header.prependRow();
    }

    /**
     * Removes the given row from the header section.
     * 
     * @param row
     *            the row to be removed
     * 
     * @throws IllegalArgumentException
     *             if the row does not exist in this section
     * @see #removeHeaderRow(int)
     * @see #addHeaderRowAt(int)
     * @see #appendHeaderRow()
     * @see #prependHeaderRow()
     */
    public void removeHeaderRow(HeaderRow row) {
        header.removeRow(row);
    }

    /**
     * Removes the row at the given position from the header section.
     * 
     * @param index
     *            the position of the row
     * 
     * @throws IllegalArgumentException
     *             if no row exists at given index
     * @see #removeHeaderRow(HeaderRow)
     * @see #addHeaderRowAt(int)
     * @see #appendHeaderRow()
     * @see #prependHeaderRow()
     */
    public void removeHeaderRow(int rowIndex) {
        header.removeRow(rowIndex);
    }

    /**
     * Sets the default row of the header. The default row is a special header
     * row providing a user interface for sorting columns.
     * 
     * @param row
     *            the new default row, or null for no default row
     * 
     * @throws IllegalArgumentException
     *             header does not contain the row
     */
    public void setDefaultHeaderRow(HeaderRow row) {
        header.setDefaultRow(row);
    }

    /**
     * Sets the visibility of the header section.
     * 
     * @param visible
     *            true to show header section, false to hide
     */
    public void setHeaderVisible(boolean visible) {
        header.setVisible(visible);
    }

    /**
     * Returns the visibility of the header section.
     * 
     * @return true if visible, false otherwise.
     */
    public boolean isHeaderVisible() {
        return header.isVisible();
    }

    /* Grid Footers */

    /**
     * Returns the footer section of this grid. The default header contains a
     * single row displaying the column captions.
     * 
     * @return the footer
     */
    protected Footer getFooter() {
        return footer;
    }

    /**
     * Gets the footer row at given index.
     * 
     * @param rowIndex
     *            0 based index for row. Counted from top to bottom
     * @return footer row at given index
     * @throws IllegalArgumentException
     *             if no row exists at given index
     */
    public FooterRow getFooterRow(int rowIndex) {
        return footer.getRow(rowIndex);
    }

    /**
     * Inserts a new row at the given position to the footer section. Shifts the
     * row currently at that position and any subsequent rows down (adds one to
     * their indices).
     * 
     * @param index
     *            the position at which to insert the row
     * @return the new row
     * 
     * @throws IllegalArgumentException
     *             if the index is less than 0 or greater than row count
     * @see #appendFooterRow()
     * @see #prependFooterRow()
     * @see #removeFooterRow(FooterRow)
     * @see #removeFooterRow(int)
     */
    public FooterRow addFooterRowAt(int index) {
        return footer.addRowAt(index);
    }

    /**
     * Adds a new row at the bottom of the footer section.
     * 
     * @return the new row
     * @see #prependFooterRow()
     * @see #addFooterRowAt(int)
     * @see #removeFooterRow(FooterRow)
     * @see #removeFooterRow(int)
     */
    public FooterRow appendFooterRow() {
        return footer.appendRow();
    }

    /**
     * Gets the row count for the footer.
     * 
     * @return row count
     */
    public int getFooterRowCount() {
        return footer.getRowCount();
    }

    /**
     * Adds a new row at the top of the footer section.
     * 
     * @return the new row
     * @see #appendFooterRow()
     * @see #addFooterRowAt(int)
     * @see #removeFooterRow(FooterRow)
     * @see #removeFooterRow(int)
     */
    public FooterRow prependFooterRow() {
        return footer.prependRow();
    }

    /**
     * Removes the given row from the footer section.
     * 
     * @param row
     *            the row to be removed
     * 
     * @throws IllegalArgumentException
     *             if the row does not exist in this section
     * @see #removeFooterRow(int)
     * @see #addFooterRowAt(int)
     * @see #appendFooterRow()
     * @see #prependFooterRow()
     */
    public void removeFooterRow(FooterRow row) {
        footer.removeRow(row);
    }

    /**
     * Removes the row at the given position from the footer section.
     * 
     * @param index
     *            the position of the row
     * 
     * @throws IllegalArgumentException
     *             if no row exists at given index
     * @see #removeFooterRow(FooterRow)
     * @see #addFooterRowAt(int)
     * @see #appendFooterRow()
     * @see #prependFooterRow()
     */
    public void removeFooterRow(int rowIndex) {
        footer.removeRow(rowIndex);
    }

    /**
     * Sets the visibility of the footer section.
     * 
     * @param visible
     *            true to show footer section, false to hide
     */
    public void setFooterVisible(boolean visible) {
        footer.setVisible(visible);
    }

    /**
     * Returns the visibility of the footer section.
     * 
     * @return true if visible, false otherwise.
     */
    public boolean isFooterVisible() {
        return footer.isVisible();
    }

    @Override
    public Iterator<Component> iterator() {
        List<Component> componentList = new ArrayList<Component>();

        Header header = getHeader();
        for (int i = 0; i < header.getRowCount(); ++i) {
            HeaderRow row = header.getRow(i);
            for (Object propId : columns.keySet()) {
                HeaderCell cell = row.getCell(propId);
                if (cell.getCellState().type == GridStaticCellType.WIDGET) {
                    componentList.add(cell.getComponent());
                }
            }
        }

        Footer footer = getFooter();
        for (int i = 0; i < footer.getRowCount(); ++i) {
            FooterRow row = footer.getRow(i);
            for (Object propId : columns.keySet()) {
                FooterCell cell = row.getCell(propId);
                if (cell.getCellState().type == GridStaticCellType.WIDGET) {
                    componentList.add(cell.getComponent());
                }
            }
        }

        componentList.addAll(getEditorFields());
        return componentList.iterator();
    }

    @Override
    public boolean isRendered(Component childComponent) {
        if (getEditorFields().contains(childComponent)) {
            // Only render editor fields if the editor is open
            return isEditorActive();
        } else {
            // TODO Header and footer components should also only be rendered if
            // the header/footer is visible
            return true;
        }
    }

    EditorClientRpc getEditorRpc() {
        return getRpcProxy(EditorClientRpc.class);
    }

    /**
     * Sets the style generator that is used for generating styles for cells
     * 
     * @param cellStyleGenerator
     *            the cell style generator to set, or <code>null</code> to
     *            remove a previously set generator
     */
    public void setCellStyleGenerator(CellStyleGenerator cellStyleGenerator) {
        this.cellStyleGenerator = cellStyleGenerator;
        getState().hasCellStyleGenerator = (cellStyleGenerator != null);

        datasourceExtension.refreshCache();
    }

    /**
     * Gets the style generator that is used for generating styles for cells
     * 
     * @return the cell style generator, or <code>null</code> if no generator is
     *         set
     */
    public CellStyleGenerator getCellStyleGenerator() {
        return cellStyleGenerator;
    }

    /**
     * Sets the style generator that is used for generating styles for rows
     * 
     * @param rowStyleGenerator
     *            the row style generator to set, or <code>null</code> to remove
     *            a previously set generator
     */
    public void setRowStyleGenerator(RowStyleGenerator rowStyleGenerator) {
        this.rowStyleGenerator = rowStyleGenerator;
        getState().hasRowStyleGenerator = (rowStyleGenerator != null);

        datasourceExtension.refreshCache();
    }

    /**
     * Gets the style generator that is used for generating styles for rows
     * 
     * @return the row style generator, or <code>null</code> if no generator is
     *         set
     */
    public RowStyleGenerator getRowStyleGenerator() {
        return rowStyleGenerator;
    }

    /**
     * Adds a row to the underlying container. The order of the parameters
     * should match the current visible column order.
     * <p>
     * Please note that it's generally only safe to use this method during
     * initialization. After Grid has been initialized and the visible column
     * order might have been changed, it's better to instead add items directly
     * to the underlying container and use {@link Item#getItemProperty(Object)}
     * to make sure each value is assigned to the intended property.
     * 
     * @param values
     *            the cell values of the new row, in the same order as the
     *            visible column order, not <code>null</code>.
     * @return the item id of the new row
     * @throws IllegalArgumentException
     *             if values is null
     * @throws IllegalArgumentException
     *             if its length does not match the number of visible columns
     * @throws IllegalArgumentException
     *             if a parameter value is not an instance of the corresponding
     *             property type
     * @throws UnsupportedOperationException
     *             if the container does not support adding new items
     */
    public Object addRow(Object... values) {
        if (values == null) {
            throw new IllegalArgumentException("Values cannot be null");
        }

        Indexed dataSource = getContainerDataSource();
        List<String> columnOrder = getState(false).columnOrder;

        if (values.length != columnOrder.size()) {
            throw new IllegalArgumentException("There are "
                    + columnOrder.size() + " visible columns, but "
                    + values.length + " cell values were provided.");
        }

        // First verify all parameter types
        for (int i = 0; i < columnOrder.size(); i++) {
            Object propertyId = getPropertyIdByColumnId(columnOrder.get(i));

            Class<?> propertyType = dataSource.getType(propertyId);
            if (values[i] != null && !propertyType.isInstance(values[i])) {
                throw new IllegalArgumentException("Parameter " + i + "("
                        + values[i] + ") is not an instance of "
                        + propertyType.getCanonicalName());
            }
        }

        Object itemId = dataSource.addItem();
        try {
            Item item = dataSource.getItem(itemId);
            for (int i = 0; i < columnOrder.size(); i++) {
                Object propertyId = getPropertyIdByColumnId(columnOrder.get(i));
                Property<Object> property = item.getItemProperty(propertyId);
                property.setValue(values[i]);
            }
        } catch (RuntimeException e) {
            try {
                dataSource.removeItem(itemId);
            } catch (Exception e2) {
                getLogger().log(Level.SEVERE,
                        "Error recovering from exception in addRow", e);
            }
            throw e;
        }

        return itemId;
    }

    private static Logger getLogger() {
        return Logger.getLogger(Grid.class.getName());
    }

    /**
     * Sets whether or not the item editor UI is enabled for this grid. When the
     * editor is enabled, the user can open it by double-clicking a row or
     * hitting enter when a row is focused. The editor can also be opened
     * programmatically using the {@link #editItem(Object)} method.
     * 
     * @param isEnabled
     *            <code>true</code> to enable the feature, <code>false</code>
     *            otherwise
     * @throws IllegalStateException
     *             if an item is currently being edited
     * 
     * @see #getEditedItemId()
     */
    public void setEditorEnabled(boolean isEnabled)
            throws IllegalStateException {
        if (isEditorActive()) {
            throw new IllegalStateException(
                    "Cannot disable the editor while an item ("
                            + getEditedItemId() + ") is being edited");
        }
        if (isEditorEnabled() != isEnabled) {
            getState().editorEnabled = isEnabled;
        }
    }

    /**
     * Checks whether the item editor UI is enabled for this grid.
     * 
     * @return <code>true</code> iff the editor is enabled for this grid
     * 
     * @see #setEditorEnabled(boolean)
     * @see #getEditedItemId()
     */
    public boolean isEditorEnabled() {
        return getState(false).editorEnabled;
    }

    /**
     * Gets the id of the item that is currently being edited.
     * 
     * @return the id of the item that is currently being edited, or
     *         <code>null</code> if no item is being edited at the moment
     */
    public Object getEditedItemId() {
        return editedItemId;
    }

    /**
     * Gets the field group that is backing the item editor of this grid.
     * 
     * @return the backing field group
     */
    public FieldGroup getEditorFieldGroup() {
        return editorFieldGroup;
    }

    /**
     * Sets the field group that is backing the item editor of this grid.
     * 
     * @param fieldGroup
     *            the backing field group
     * 
     * @throws IllegalStateException
     *             if the editor is currently active
     */
    public void setEditorFieldGroup(FieldGroup fieldGroup) {
        if (isEditorActive()) {
            throw new IllegalStateException(
                    "Cannot change field group while an item ("
                            + getEditedItemId() + ") is being edited");
        }
        editorFieldGroup = fieldGroup;
    }

    /**
     * Returns whether an item is currently being edited in the editor.
     * 
     * @return true iff the editor is open
     */
    public boolean isEditorActive() {
        return editedItemId != null;
    }

    private void checkColumnExists(Object propertyId) {
        if (getColumn(propertyId) == null) {
            throw new IllegalArgumentException(
                    "There is no column with the property id " + propertyId);
        }
    }

    private Field<?> getEditorField(Object propertyId) {
        checkColumnExists(propertyId);

        if (!getColumn(propertyId).isEditable()) {
            return null;
        }

        Field<?> editor = editorFieldGroup.getField(propertyId);
        if (editor == null) {
            editor = editorFieldGroup.buildAndBind(propertyId);
        }

        if (editor.getParent() != Grid.this) {
            assert editor.getParent() == null;
            editor.setParent(this);
        }
        return editor;
    }

    /**
     * Opens the editor interface for the provided item. Scrolls the Grid to
     * bring the item to view if it is not already visible.
     * 
     * @param itemId
     *            the id of the item to edit
     * @throws IllegalStateException
     *             if the editor is not enabled or already editing an item
     * @throws IllegalArgumentException
     *             if the {@code itemId} is not in the backing container
     * @see #setEditorEnabled(boolean)
     */
    public void editItem(Object itemId) throws IllegalStateException,
            IllegalArgumentException {
        if (!isEditorEnabled()) {
            throw new IllegalStateException("Item editor is not enabled");
        } else if (editedItemId != null) {
            throw new IllegalStateException("Editing item + " + itemId
                    + " failed. Item editor is already editing item "
                    + editedItemId);
        } else if (!getContainerDataSource().containsId(itemId)) {
            throw new IllegalArgumentException("Item with id " + itemId
                    + " not found in current container");
        }
        editedItemId = itemId;
        getEditorRpc().bind(getContainerDataSource().indexOfId(itemId));
    }

    protected void doEditItem() {
        Item item = getContainerDataSource().getItem(editedItemId);

        editorFieldGroup.setItemDataSource(item);

        for (Column column : getColumns()) {
            column.getState().editorConnector = getEditorField(column
                    .getPropertyId());
        }
    }

    private void setEditorField(Object propertyId, Field<?> field) {
        checkColumnExists(propertyId);

        Field<?> oldField = editorFieldGroup.getField(propertyId);
        if (oldField != null) {
            editorFieldGroup.unbind(oldField);
            oldField.setParent(null);
        }

        if (field != null) {
            field.setParent(this);
            editorFieldGroup.bind(field, propertyId);
        }
    }

    /**
     * Saves all changes done to the bound fields.
     * <p>
     * <em>Note:</em> This is a pass-through call to the backing field group.
     * 
     * @throws CommitException
     *             If the commit was aborted
     * 
     * @see FieldGroup#commit()
     */
    public void saveEditor() throws CommitException {
        editorFieldGroup.commit();
    }

    /**
     * Cancels the currently active edit if any. Hides the editor and discards
     * possible unsaved changes in the editor fields.
     */
    public void cancelEditor() {
        if (isEditorActive()) {
            getEditorRpc().cancel(
                    getContainerDataSource().indexOfId(editedItemId));
            doCancelEditor();
        }
    }

    protected void doCancelEditor() {
        editedItemId = null;
        editorFieldGroup.discard();
    }

    void resetEditor() {
        if (isEditorActive()) {
            /*
             * Simply force cancel the editing; throwing here would just make
             * Grid.setContainerDataSource semantics more complicated.
             */
            cancelEditor();
        }
        for (Field<?> editor : getEditorFields()) {
            editor.setParent(null);
        }

        editedItemId = null;
        editorFieldGroup = new CustomFieldGroup();
    }

    /**
     * Gets a collection of all fields bound to the item editor of this grid.
     * <p>
     * When {@link #editItem(Object) editItem} is called, fields are
     * automatically created and bound to any unbound properties.
     * 
     * @return a collection of all the fields bound to the item editor
     */
    Collection<Field<?>> getEditorFields() {
        Collection<Field<?>> fields = editorFieldGroup.getFields();
        assert allAttached(fields);
        return fields;
    }

    private boolean allAttached(Collection<? extends Component> components) {
        for (Component component : components) {
            if (component.getParent() != this) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets the field factory for the {@link FieldGroup}. The field factory is
     * only used when {@link FieldGroup} creates a new field.
     * <p>
     * <em>Note:</em> This is a pass-through call to the backing field group.
     * 
     * @param fieldFactory
     *            The field factory to use
     */
    public void setEditorFieldFactory(FieldGroupFieldFactory fieldFactory) {
        editorFieldGroup.setFieldFactory(fieldFactory);
    }

    /**
     * Sets the error handler for the editor.
     * 
     * The error handler is called whenever there is an exception in the editor.
     * 
     * @param editorErrorHandler
     *            The editor error handler to use
     * @throws IllegalArgumentException
     *             if the error handler is null
     */
    public void setEditorErrorHandler(EditorErrorHandler editorErrorHandler)
            throws IllegalArgumentException {
        if (editorErrorHandler == null) {
            throw new IllegalArgumentException(
                    "The error handler cannot be null");
        }
        this.editorErrorHandler = editorErrorHandler;
    }

    /**
     * Gets the error handler used for the editor
     * 
     * @see #setErrorHandler(com.vaadin.server.ErrorHandler)
     * @return the editor error handler, never null
     */
    public EditorErrorHandler getEditorErrorHandler() {
        return editorErrorHandler;
    }

    /**
     * Gets the field factory for the {@link FieldGroup}. The field factory is
     * only used when {@link FieldGroup} creates a new field.
     * <p>
     * <em>Note:</em> This is a pass-through call to the backing field group.
     * 
     * @return The field factory in use
     */
    public FieldGroupFieldFactory getEditorFieldFactory() {
        return editorFieldGroup.getFieldFactory();
    }

    /**
     * Sets the caption on the save button in the Grid editor.
     * 
     * @param saveCaption
     *            the caption to set
     * @throws IllegalArgumentException
     *             if {@code saveCaption} is {@code null}
     */
    public void setEditorSaveCaption(String saveCaption)
            throws IllegalArgumentException {
        if (saveCaption == null) {
            throw new IllegalArgumentException("Save caption cannot be null");
        }
        getState().editorSaveCaption = saveCaption;
    }

    /**
     * Gets the current caption of the save button in the Grid editor.
     * 
     * @return the current caption of the save button
     */
    public String getEditorSaveCaption() {
        return getState(false).editorSaveCaption;
    }

    /**
     * Sets the caption on the cancel button in the Grid editor.
     * 
     * @param cancelCaption
     *            the caption to set
     * @throws IllegalArgumentException
     *             if {@code cancelCaption} is {@code null}
     */
    public void setEditorCancelCaption(String cancelCaption)
            throws IllegalArgumentException {
        if (cancelCaption == null) {
            throw new IllegalArgumentException("Cancel caption cannot be null");
        }
        getState().editorCancelCaption = cancelCaption;
    }

    /**
     * Gets the current caption of the cancel button in the Grid editor.
     * 
     * @return the current caption of the cancel button
     */
    public String getEditorCancelCaption() {
        return getState(false).editorCancelCaption;
    }

    @Override
    public void addItemClickListener(ItemClickListener listener) {
        addListener(GridConstants.ITEM_CLICK_EVENT_ID, ItemClickEvent.class,
                listener, ItemClickEvent.ITEM_CLICK_METHOD);
    }

    @Override
    @Deprecated
    public void addListener(ItemClickListener listener) {
        addItemClickListener(listener);
    }

    @Override
    public void removeItemClickListener(ItemClickListener listener) {
        removeListener(GridConstants.ITEM_CLICK_EVENT_ID, ItemClickEvent.class,
                listener);
    }

    @Override
    @Deprecated
    public void removeListener(ItemClickListener listener) {
        removeItemClickListener(listener);
    }

    /**
     * Requests that the column widths should be recalculated.
     * <p>
     * In most cases Grid will know when column widths need to be recalculated
     * but this method can be used to force recalculation in situations when
     * grid does not recalculate automatically.
     * 
     * @since 7.4.1
     */
    public void recalculateColumnWidths() {
        getRpcProxy(GridClientRpc.class).recalculateColumnWidths();
    }
}

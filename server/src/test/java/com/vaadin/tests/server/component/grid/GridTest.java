package com.vaadin.tests.server.component.grid;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.easymock.Capture;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.vaadin.data.Binder.Binding;
import com.vaadin.data.ValidationException;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.DataCommunicator;
import com.vaadin.data.provider.DataGenerator;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.data.provider.bov.Person;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.server.SerializableComparator;
import com.vaadin.shared.communication.SharedState;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.tests.util.MockUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.NumberRenderer;

import elemental.json.Json;
import elemental.json.JsonObject;
import junit.framework.AssertionFailedError;

public class GridTest {

    private Grid<String> grid;
    private Column<String, String> fooColumn;
    private Column<String, Integer> lengthColumn;
    private Column<String, Object> objectColumn;
    private Column<String, String> randomColumn;
    private GridState state;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        grid = new Grid<String>() {
            {
                state = getState(false);
            }
        };

        fooColumn = grid.addColumn(ValueProvider.identity()).setId("foo");
        lengthColumn = grid.addColumn(String::length, new NumberRenderer())
                .setId("length");
        objectColumn = grid.addColumn(string -> new Object());
        randomColumn = grid.addColumn(ValueProvider.identity())
                .setId("randomColumnId");
    }

    @Test
    public void testGridAssistiveCaption() {
        assertEquals(null, fooColumn.getAssistiveCaption());
        fooColumn.setAssistiveCaption("Press Enter to sort.");
        assertEquals("Press Enter to sort.", fooColumn.getAssistiveCaption());
    }

    @Test
    public void testCreateGridWithDataCommunicator() {
        DataCommunicator<String> specificDataCommunicator = new DataCommunicator<>();

        TestGrid<String> grid = new TestGrid<>(String.class,
                specificDataCommunicator);

        assertEquals(specificDataCommunicator, grid.getDataCommunicator());
    }

    @Test
    public void testGridHeightModeChange() {
        assertEquals("Initial height mode was not CSS", HeightMode.CSS,
                grid.getHeightMode());
        grid.setHeightByRows(13.24);
        assertEquals("Setting height by rows did not change height mode",
                HeightMode.ROW, grid.getHeightMode());
        grid.setHeight("100px");
        assertEquals("Setting height did not change height mode.",
                HeightMode.CSS, grid.getHeightMode());
    }

    @Test
    public void testFrozenColumnCountTooBig() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(
                "count must be between -1 and the current number of columns (4): 5");

        grid.setFrozenColumnCount(5);
    }

    @Test
    public void testFrozenColumnCountTooSmall() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(
                "count must be between -1 and the current number of columns (4): -2");

        grid.setFrozenColumnCount(-2);
    }

    @Test
    public void testSetFrozenColumnCount() {
        for (int i = -1; i < 2; ++i) {
            grid.setFrozenColumnCount(i);
            assertEquals("Frozen column count not updated", i,
                    grid.getFrozenColumnCount());
        }
    }

    @Test
    public void testGridColumnIdentifier() {
        grid.getColumn("foo").setCaption("Bar");
        assertEquals("Column header not updated correctly", "Bar",
                grid.getHeaderRow(0).getCell("foo").getText());
    }

    @Test
    public void testGridMultipleColumnsWithSameIdentifier() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Duplicate ID for columns");

        grid.addColumn(t -> t).setId("foo");
    }

    @Test
    public void testAddSelectionListener_singleSelectMode() {
        grid.setItems("foo", "bar", "baz");

        Capture<SelectionEvent<String>> eventCapture = new Capture<>();

        grid.addSelectionListener(event -> eventCapture.setValue(event));

        grid.getSelectionModel().select("foo");

        SelectionEvent<String> event = eventCapture.getValue();
        assertNotNull(event);
        assertFalse(event.isUserOriginated());
        assertEquals("foo", event.getFirstSelectedItem().get());
        assertEquals("foo",
                event.getAllSelectedItems().stream().findFirst().get());

        grid.getSelectionModel().select("bar");

        event = eventCapture.getValue();
        assertNotNull(event);
        assertFalse(event.isUserOriginated());
        assertEquals("bar", event.getFirstSelectedItem().get());
        assertEquals("bar",
                event.getAllSelectedItems().stream().findFirst().get());

        grid.getSelectionModel().deselect("bar");

        event = eventCapture.getValue();
        assertNotNull(event);
        assertFalse(event.isUserOriginated());
        assertEquals(Optional.empty(), event.getFirstSelectedItem());
        assertEquals(0, event.getAllSelectedItems().size());
    }

    @Test
    public void testAddSelectionListener_multiSelectMode() {
        grid.setItems("foo", "bar", "baz");
        grid.setSelectionMode(SelectionMode.MULTI);

        Capture<SelectionEvent<String>> eventCapture = new Capture<>();

        grid.addSelectionListener(event -> eventCapture.setValue(event));

        grid.getSelectionModel().select("foo");

        SelectionEvent<String> event = eventCapture.getValue();
        assertNotNull(event);
        assertFalse(event.isUserOriginated());
        assertEquals("foo", event.getFirstSelectedItem().get());
        assertEquals("foo",
                event.getAllSelectedItems().stream().findFirst().get());

        grid.getSelectionModel().select("bar");

        event = eventCapture.getValue();
        assertNotNull(event);
        assertFalse(event.isUserOriginated());
        assertEquals("foo", event.getFirstSelectedItem().get());
        assertEquals("foo",
                event.getAllSelectedItems().stream().findFirst().get());
        assertArrayEquals(new String[] { "foo", "bar" },
                event.getAllSelectedItems().toArray(new String[2]));

        grid.getSelectionModel().deselect("foo");

        event = eventCapture.getValue();
        assertNotNull(event);
        assertFalse(event.isUserOriginated());
        assertEquals("bar", event.getFirstSelectedItem().get());
        assertEquals("bar",
                event.getAllSelectedItems().stream().findFirst().get());
        assertArrayEquals(new String[] { "bar" },
                event.getAllSelectedItems().toArray(new String[1]));

        grid.getSelectionModel().deselectAll();

        event = eventCapture.getValue();
        assertNotNull(event);
        assertFalse(event.isUserOriginated());
        assertEquals(Optional.empty(), event.getFirstSelectedItem());
        assertEquals(0, event.getAllSelectedItems().size());
    }

    @Test
    public void testAddSelectionListener_noSelectionMode() {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage(
                "This selection model doesn't allow selection, cannot add selection listeners to it");

        grid.setSelectionMode(SelectionMode.NONE);

        grid.addSelectionListener(event -> fail("never ever happens (tm)"));
    }

    @Test
    public void sortByColumn_sortOrderIsAscendingOneColumn() {
        Column<String, ?> column = grid.getColumns().get(1);
        grid.sort(column);

        GridSortOrder<String> sortOrder = grid.getSortOrder().get(0);
        assertEquals(column, sortOrder.getSorted());
        assertEquals(SortDirection.ASCENDING, sortOrder.getDirection());
    }

    @Test
    public void sortByColumnDesc_sortOrderIsDescendingOneColumn() {
        Column<String, ?> column = grid.getColumns().get(1);
        grid.sort(column, SortDirection.DESCENDING);

        GridSortOrder<String> sortOrder = grid.getSortOrder().get(0);
        assertEquals(column, sortOrder.getSorted());
        assertEquals(SortDirection.DESCENDING, sortOrder.getDirection());
    }

    @Test
    public void setSortOrder() {
        Column<String, ?> column1 = grid.getColumns().get(1);
        Column<String, ?> column2 = grid.getColumns().get(2);
        List<GridSortOrder<String>> order = Arrays.asList(
                new GridSortOrder<>(column2, SortDirection.DESCENDING),
                new GridSortOrder<>(column1, SortDirection.ASCENDING));
        grid.setSortOrder(order);

        List<GridSortOrder<String>> sortOrder = grid.getSortOrder();
        assertEquals(column2, sortOrder.get(0).getSorted());
        assertEquals(SortDirection.DESCENDING, sortOrder.get(0).getDirection());

        assertEquals(column1, sortOrder.get(1).getSorted());
        assertEquals(SortDirection.ASCENDING, sortOrder.get(1).getDirection());
    }

    @Test
    public void clearSortOrder() throws Exception {
        Column<String, ?> column = grid.getColumns().get(1);
        grid.sort(column);

        grid.clearSortOrder();

        assertEquals(0, grid.getSortOrder().size());

        // Make sure state is updated.
        assertEquals(0, state.sortColumns.length);
        assertEquals(0, state.sortDirs.length);
    }

    @Test
    public void sortOrderDoesnotContainRemovedColumns() {
        Column<String, ?> sortColumn = grid.getColumns().get(1);
        grid.sort(sortColumn);

        // Get id of column and check it's sorted.
        String id = state.columnOrder.get(1);
        assertEquals(id, state.sortColumns[0]);

        // Remove column and make sure it's cleared correctly
        grid.removeColumn(sortColumn);
        assertFalse("Column not removed", state.columnOrder.contains(id));
        assertEquals(0, state.sortColumns.length);
        assertEquals(0, state.sortDirs.length);
    }

    @Test
    public void sortListener_eventIsFired() {
        Column<String, ?> column1 = grid.getColumns().get(1);
        Column<String, ?> column2 = grid.getColumns().get(2);

        List<GridSortOrder<String>> list = new ArrayList<>();
        AtomicReference<Boolean> fired = new AtomicReference<>();
        grid.addSortListener(event -> {
            assertTrue(list.isEmpty());
            fired.set(true);
            list.addAll(event.getSortOrder());
        });
        grid.sort(column1, SortDirection.DESCENDING);

        assertEquals(column1, list.get(0).getSorted());
        assertEquals(SortDirection.DESCENDING, list.get(0).getDirection());

        List<GridSortOrder<String>> order = Arrays.asList(
                new GridSortOrder<>(column2, SortDirection.DESCENDING),
                new GridSortOrder<>(column1, SortDirection.ASCENDING));
        list.clear();

        grid.setSortOrder(order);

        assertEquals(column2, list.get(0).getSorted());
        assertEquals(SortDirection.DESCENDING, list.get(0).getDirection());

        assertEquals(column1, list.get(1).getSorted());
        assertEquals(SortDirection.ASCENDING, list.get(1).getDirection());

        list.clear();
        fired.set(false);
        grid.clearSortOrder();
        assertEquals(0, list.size());
        assertTrue(fired.get());
    }

    @Test
    public void beanGrid() {
        Grid<Person> grid = new Grid<>(Person.class);

        Column<Person, ?> nameColumn = grid.getColumn("name");
        Column<Person, ?> bornColumn = grid.getColumn("born");

        assertNotNull(nameColumn);
        assertNotNull(bornColumn);

        assertEquals("Name", nameColumn.getCaption());
        assertEquals("Born", bornColumn.getCaption());

        JsonObject json = getRowData(grid, new Person("Lorem", 2000));

        Set<String> values = Stream.of(json.keys()).map(json::getString)
                .collect(Collectors.toSet());

        assertEquals(new HashSet<>(Arrays.asList("Lorem", "2000")), values);

        assertSingleSortProperty(nameColumn, "name");
        assertSingleSortProperty(bornColumn, "born");
    }

    @Test
    public void beanGrid_editor() throws ValidationException {
        Grid<Person> grid = new Grid<>(Person.class);

        Column<Person, ?> nameColumn = grid.getColumn("name");

        TextField nameField = new TextField();
        nameColumn.setEditorComponent(nameField);

        Optional<Binding<Person, ?>> maybeBinding = grid.getEditor().getBinder()
                .getBinding("name");
        assertTrue(maybeBinding.isPresent());

        Binding<Person, ?> binding = maybeBinding.get();
        assertSame(nameField, binding.getField());

        Person person = new Person("Lorem", 2000);
        grid.getEditor().getBinder().setBean(person);

        assertEquals("Lorem", nameField.getValue());

        nameField.setValue("Ipsum");
        assertEquals("Ipsum", person.getName());
    }

    @Test
    public void oneArgSetEditor_nonBeanGrid() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(
                "A Grid created without a bean type class literal or a custom property set"
                        + " doesn't support finding properties by name.");

        Grid<Person> grid = new Grid<>();
        Column<Person, String> nameCol = grid.addColumn(Person::getName)
                .setId("name");

        nameCol.setEditorComponent(new TextField());
    }

    @Test
    public void addExistingColumnById_throws() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("There is already a column for name");

        Grid<Person> grid = new Grid<>(Person.class);
        grid.addColumn("name");
    }

    @Test
    public void removeByColumn_readdById() {
        Grid<Person> grid = new Grid<>(Person.class);
        grid.removeColumn(grid.getColumn("name"));

        grid.addColumn("name");

        List<Column<Person, ?>> columns = grid.getColumns();
        assertEquals(2, columns.size());
        assertEquals("born", columns.get(0).getId());
        assertEquals("name", columns.get(1).getId());
    }

    @Test
    public void removeColumnByColumn() {
        grid.removeColumn(fooColumn);

        assertEquals(Arrays.asList(lengthColumn, objectColumn, randomColumn),
                grid.getColumns());
    }

    @Test
    public void removeColumnByColumn_alreadyRemoved() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(
                "Column with id foo cannot be removed from the grid");

        grid.removeColumn(fooColumn);
        grid.removeColumn(fooColumn);

        assertEquals(Arrays.asList(lengthColumn, objectColumn, randomColumn),
                grid.getColumns());
    }

    @Test
    public void removeColumnById_alreadyRemoved() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("There is no column with the id foo");

        grid.removeColumn("foo");
        grid.removeColumn("foo");
    }

    @Test
    public void removeColumnById() {
        grid.removeColumn("foo");

        assertEquals(Arrays.asList(lengthColumn, objectColumn, randomColumn),
                grid.getColumns());
    }

    @Test
    public void removeAllColumns() {
        grid.removeAllColumns();

        assertEquals(Collections.emptyList(), grid.getColumns());
    }

    @Test
    public void removeAllColumnsInGridWithoutColumns() {
        grid.removeAllColumns();
        grid.removeAllColumns();
        assertEquals(Collections.emptyList(), grid.getColumns());
    }

    @Test
    public void removeFrozenColumn() {
        grid.setFrozenColumnCount(3);
        grid.removeColumn(fooColumn);
        assertEquals(2, grid.getFrozenColumnCount());
    }

    @Test
    public void removeHiddenFrozenColumn() {
        lengthColumn.setHidden(true);
        grid.setFrozenColumnCount(3);
        grid.removeColumn(lengthColumn);
        assertEquals(2, grid.getFrozenColumnCount());
    }

    @Test
    public void removeNonFrozenColumn() {
        grid.setFrozenColumnCount(3);
        grid.removeColumn(randomColumn);
        assertEquals(3, grid.getFrozenColumnCount());
    }

    @Test
    public void testFrozenColumnRemoveColumn() {
        assertEquals("Grid should not start with a frozen column", 0,
                grid.getFrozenColumnCount());

        int columnCount = grid.getColumns().size();
        grid.setFrozenColumnCount(columnCount);

        grid.removeColumn(grid.getColumns().get(0));
        assertEquals(
                "Frozen column count should be updated when removing a frozen column",
                columnCount - 1, grid.getFrozenColumnCount());
    }

    @Test
    public void setColumns_reorder() {
        // Will remove other columns
        grid.setColumns("length", "foo");

        List<Column<String, ?>> columns = grid.getColumns();

        assertEquals(2, columns.size());
        assertEquals("length", columns.get(0).getId());
        assertEquals("foo", columns.get(1).getId());
    }

    @Test
    public void setColumns_addColumn_notBeangrid() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(
                "A Grid created without a bean type class literal or a custom property set"
                        + " doesn't support finding properties by name.");

        // Not possible to add a column in a grid that cannot add columns based
        // on a string
        grid.setColumns("notHere");
    }

    @Test
    public void setColumns_addColumns_beangrid() {
        Grid<Person> grid = new Grid<>(Person.class);

        // Remove so we can add it back
        grid.removeColumn("name");

        grid.setColumns("born", "name");

        List<Column<Person, ?>> columns = grid.getColumns();
        assertEquals(2, columns.size());
        assertEquals("born", columns.get(0).getId());
        assertEquals("name", columns.get(1).getId());
    }

    @Test
    public void setColumns_addColumns_v2_beangrid() {
        Grid<Person> grid = new Grid<>(Person.class);

        // Remove so we can add it back
        grid.removeColumn("name");

        List<String> columnIds = new ArrayList<>();
        columnIds.add("born");
        columnIds.add("name");

        grid.setColumns(columnIds);

        List<Column<Person, ?>> columns = grid.getColumns();
        assertEquals(2, columns.size());
        assertEquals("born", columns.get(0).getId());
        assertEquals("name", columns.get(1).getId());
    }    

    @Test
    public void setColumnOrder_byColumn() {
        grid.setColumnOrder(randomColumn, lengthColumn);

        assertEquals(Arrays.asList(randomColumn, lengthColumn, fooColumn,
                objectColumn), grid.getColumns());
    }

    @Test
    public void setColumnOrder_byColumn_removedColumn() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("setColumnOrder should not be called "
                + "with columns that are not in the grid.");

        grid.removeColumn(randomColumn);
        grid.setColumnOrder(randomColumn, lengthColumn);
    }

    @Test
    public void setColumnOrder_byString() {
        grid.setColumnOrder("randomColumnId", "length");

        assertEquals(Arrays.asList(randomColumn, lengthColumn, fooColumn,
                objectColumn), grid.getColumns());
    }

    @Test
    public void setColumnOrder_byString_removedColumn() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("There is no column with the id randomColumnId");

        grid.removeColumn("randomColumnId");
        grid.setColumnOrder("randomColumnId", "length");
    }

    @Test
    public void defaultSorting_comparableTypes() {
        testValueProviderSorting(1, 2, 3);
    }

    @Test
    public void defaultSorting_strings() {
        testValueProviderSorting("a", "b", "c");
    }

    @Test
    public void defaultSorting_notComparable() {
        assert !Comparable.class.isAssignableFrom(AtomicInteger.class);

        testValueProviderSorting(new AtomicInteger(10), new AtomicInteger(8),
                new AtomicInteger(9));
    }

    @Test
    public void defaultSorting_differentComparables() {
        testValueProviderSorting(10.1, 200, 3000.1, 4000);
    }

    @Test
    public void defaultSorting_mutuallyComparableTypes() {
        testValueProviderSorting(new Date(10), new java.sql.Date(1000000),
                new Date(100000000));
    }

    private static void testValueProviderSorting(Object... expectedOrder) {
        SerializableComparator<Object> comparator = new Grid<>()
                .addColumn(ValueProvider.identity())
                .getComparator(SortDirection.ASCENDING);

        assertNotNull(comparator);

        List<Object> values = new ArrayList<>(Arrays.asList(expectedOrder));
        Collections.shuffle(values, new Random(42));

        assertArrayEquals(expectedOrder,
                values.stream().sorted(comparator).toArray());
    }

    @Test
    public void addBeanColumn_validRenderer() {
        Grid<Person> grid = new Grid<>(Person.class);

        grid.removeColumn("born");
        grid.addColumn("born", new NumberRenderer(new DecimalFormat("#,###",
                DecimalFormatSymbols.getInstance(Locale.US))));

        Person person = new Person("Name", 2017);

        JsonObject rowData = getRowData(grid, person);

        String formattedValue = Stream.of(rowData.keys())
                .map(rowData::getString).filter(value -> !value.equals("Name"))
                .findFirst().orElse(null);
        assertEquals(formattedValue, "2,017");
    }

    @Test
    public void addBeanColumn_invalidRenderer() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("NumberRenderer");
        thrown.expectMessage(
                " cannot be used with a property of type java.lang.String");

        Grid<Person> grid = new Grid<>(Person.class);

        grid.removeColumn("name");
        grid.addColumn("name", new NumberRenderer());
    }

    @Test
    public void columnId_sortProperty() {
        assertSingleSortProperty(lengthColumn, "length");
    }

    @Test
    public void columnId_sortProperty_noId() {
        assertEquals(0,
                objectColumn.getSortOrder(SortDirection.ASCENDING).count());
    }

    @Test
    public void sortProperty_setId_doesntOverride() {
        objectColumn.setSortProperty("foo");
        objectColumn.setId("bar");

        assertSingleSortProperty(objectColumn, "foo");
    }

    private static void assertSingleSortProperty(Column<?, ?> column,
            String expectedProperty) {
        QuerySortOrder[] sortOrders = column
                .getSortOrder(SortDirection.ASCENDING)
                .toArray(QuerySortOrder[]::new);

        assertEquals(1, sortOrders.length);
        assertEquals(SortDirection.ASCENDING, sortOrders[0].getDirection());
        assertEquals(expectedProperty, sortOrders[0].getSorted());
    }

    private static <T> JsonObject getRowData(Grid<T> grid, T row) {
        JsonObject json = Json.createObject();
        if (grid.getColumns().isEmpty()) {
            return json;
        }

        // generateData only works if Grid is attached
        new MockUI().setContent(grid);

        Method getter = findDataGeneratorGetterMethod();
        grid.getColumns().forEach(column -> {
            DataGenerator<T> dataGenerator;
            try {
                dataGenerator = (DataGenerator<T>) getter.invoke(column,
                        new Object[] {});
                dataGenerator.generateData(row, json);
            } catch (IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                throw new AssertionFailedError(
                        "Cannot get DataGenerator from Column");
            }
        });

        // Detach again
        grid.getUI().setContent(null);

        return json.getObject("d");
    }

    private static Method findDataGeneratorGetterMethod() {
        try {
            Method getter = Column.class.getDeclaredMethod("getDataGenerator",
                    new Class<?>[] {});
            getter.setAccessible(true);
            return getter;
        } catch (NoSuchMethodException | SecurityException e) {
            throw new AssertionFailedError(
                    "Cannot get DataGenerator from Column");
        }
    }

    @Test
    public void removeColumnToThrowForInvalidColumn() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(
                "Column with id null cannot be removed from the grid");

        Grid<Person> grid1 = new Grid<>();
        Grid<Person> grid2 = new Grid<>();
        Column<Person, ?> column1 = grid1.addColumn(ValueProvider.identity());
        grid2.removeColumn(column1);
    }

    @Test
    public void testColumnSortable() {
        Column<String, String> column = grid.addColumn(String::toString);

        // Use in-memory data provider
        grid.setItems(Collections.emptyList());

        Assert.assertTrue("Column should be initially sortable",
                column.isSortable());
        Assert.assertTrue("User should be able to sort the column",
                column.isSortableByUser());

        column.setSortable(false);

        Assert.assertFalse("Column should not be sortable",
                column.isSortable());
        Assert.assertFalse(
                "User should not be able to sort the column with in-memory data",
                column.isSortableByUser());

        // Use CallBackDataProvider
        grid.setDataProvider(
                DataProvider.fromCallbacks(q -> Stream.of(), q -> 0));

        Assert.assertFalse("Column should not be sortable",
                column.isSortable());
        Assert.assertFalse("User should not be able to sort the column",
                column.isSortableByUser());

        column.setSortable(true);

        Assert.assertTrue("Column should be marked sortable",
                column.isSortable());
        Assert.assertFalse(
                "User should not be able to sort the column since no sort order is provided",
                column.isSortableByUser());

        column.setSortProperty("toString");

        Assert.assertTrue("Column should be marked sortable",
                column.isSortable());
        Assert.assertTrue(
                "User should be able to sort the column with the sort order",
                column.isSortableByUser());
    }

    @Test
    public void extendGridCustomDataCommunicator() {
        Grid<String> grid = new MyGrid<>();
    }

    public class MyDataCommunicator<T> extends DataCommunicator<T> {
        @Override
        protected int getMaximumAllowedRows() {
            return 600;
        }
    }

    public class MyGrid<T> extends Grid<T> {

        public MyGrid() {
            super(new MyDataCommunicator());
        }

    }
}

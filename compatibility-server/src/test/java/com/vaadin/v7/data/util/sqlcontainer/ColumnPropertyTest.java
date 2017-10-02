package com.vaadin.v7.data.util.sqlcontainer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Test;

import com.vaadin.v7.data.Property.ReadOnlyException;
import com.vaadin.v7.data.util.sqlcontainer.ColumnProperty.NotNullableException;
import com.vaadin.v7.data.util.sqlcontainer.query.QueryDelegate;

public class ColumnPropertyTest {

    @Test
    public void constructor_legalParameters_shouldSucceed() {
        ColumnProperty cp = new ColumnProperty("NAME", false, true, true, false,
                "Ville", String.class);
        assertNotNull(cp);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_missingPropertyId_shouldFail() {
        new ColumnProperty(null, false, true, true, false, "Ville",
                String.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_missingType_shouldFail() {
        new ColumnProperty("NAME", false, true, true, false, "Ville", null);
    }

    @Test
    public void getValue_defaultValue_returnsVille() {
        ColumnProperty cp = new ColumnProperty("NAME", false, true, true, false,
                "Ville", String.class);
        assertEquals("Ville", cp.getValue());
    }

    @Test
    public void setValue_readWriteNullable_returnsKalle() {
        ColumnProperty cp = new ColumnProperty("NAME", false, true, true, false,
                "Ville", String.class);
        SQLContainer container = EasyMock.createMock(SQLContainer.class);
        RowItem owner = new RowItem(container, new RowId(new Object[] { 1 }),
                Arrays.asList(cp));
        container.itemChangeNotification(owner);
        EasyMock.replay(container);
        cp.setValue("Kalle");
        assertEquals("Kalle", cp.getValue());
        EasyMock.verify(container);
    }

    @Test(expected = ReadOnlyException.class)
    public void setValue_readOnlyNullable_shouldFail() {
        ColumnProperty cp = new ColumnProperty("NAME", true, true, true, false,
                "Ville", String.class);
        SQLContainer container = EasyMock.createMock(SQLContainer.class);
        new RowItem(container, new RowId(new Object[] { 1 }),
                Arrays.asList(cp));
        EasyMock.replay(container);
        cp.setValue("Kalle");
        EasyMock.verify(container);
    }

    @Test
    public void setValue_readWriteNullable_nullShouldWork() {
        ColumnProperty cp = new ColumnProperty("NAME", false, true, true, false,
                "Ville", String.class);
        SQLContainer container = EasyMock.createMock(SQLContainer.class);
        RowItem owner = new RowItem(container, new RowId(new Object[] { 1 }),
                Arrays.asList(cp));
        container.itemChangeNotification(owner);
        EasyMock.replay(container);
        cp.setValue(null);
        assertNull(cp.getValue());
        EasyMock.verify(container);
    }

    @Test(expected = NotNullableException.class)
    public void setValue_readWriteNotNullable_nullShouldFail() {
        ColumnProperty cp = new ColumnProperty("NAME", false, true, false,
                false, "Ville", String.class);
        SQLContainer container = EasyMock.createMock(SQLContainer.class);
        RowItem owner = new RowItem(container, new RowId(new Object[] { 1 }),
                Arrays.asList(cp));
        container.itemChangeNotification(owner);
        EasyMock.replay(container);
        cp.setValue(null);
        assertNotNull(cp.getValue());
        EasyMock.verify(container);
    }

    @Test
    public void getType_normal_returnsStringClass() {
        ColumnProperty cp = new ColumnProperty("NAME", false, true, true, false,
                "Ville", String.class);
        assertSame(String.class, cp.getType());
    }

    @Test
    public void isReadOnly_readWriteNullable_returnsTrue() {
        ColumnProperty cp = new ColumnProperty("NAME", false, true, true, false,
                "Ville", String.class);
        assertFalse(cp.isReadOnly());
    }

    @Test
    public void isReadOnly_readOnlyNullable_returnsTrue() {
        ColumnProperty cp = new ColumnProperty("NAME", true, true, true, false,
                "Ville", String.class);
        assertTrue(cp.isReadOnly());
    }

    @Test
    public void setReadOnly_readOnlyChangeAllowed_shouldSucceed() {
        ColumnProperty cp = new ColumnProperty("NAME", false, true, true, false,
                "Ville", String.class);
        cp.setReadOnly(true);
        assertTrue(cp.isReadOnly());
    }

    @Test
    public void setReadOnly_readOnlyChangeDisallowed_shouldFail() {
        ColumnProperty cp = new ColumnProperty("NAME", false, false, true,
                false, "Ville", String.class);
        cp.setReadOnly(true);
        assertFalse(cp.isReadOnly());
    }

    @Test
    public void getPropertyId_normal_returnsNAME() {
        ColumnProperty cp = new ColumnProperty("NAME", false, false, true,
                false, "Ville", String.class);
        assertEquals("NAME", cp.getPropertyId());
    }

    @Test
    public void isModified_valueModified_returnsTrue() {
        ColumnProperty cp = new ColumnProperty("NAME", false, true, true, false,
                "Ville", String.class);
        SQLContainer container = EasyMock.createMock(SQLContainer.class);
        RowItem owner = new RowItem(container, new RowId(new Object[] { 1 }),
                Arrays.asList(cp));
        container.itemChangeNotification(owner);
        EasyMock.replay(container);
        cp.setValue("Kalle");
        assertEquals("Kalle", cp.getValue());
        assertTrue(cp.isModified());
        EasyMock.verify(container);
    }

    @Test
    public void isModified_valueNotModified_returnsFalse() {
        ColumnProperty cp = new ColumnProperty("NAME", false, false, true,
                false, "Ville", String.class);
        assertFalse(cp.isModified());
    }

    @Test
    public void setValue_nullOnNullable_shouldWork() {
        ColumnProperty cp = new ColumnProperty("NAME", false, true, true, false,
                "asdf", String.class);
        SQLContainer container = EasyMock.createMock(SQLContainer.class);
        new RowItem(container, new RowId(new Object[] { 1 }),
                Arrays.asList(cp));
        cp.setValue(null);
        assertNull(cp.getValue());
    }

    @Test
    public void setValue_resetTonullOnNullable_shouldWork() {
        ColumnProperty cp = new ColumnProperty("NAME", false, true, true, false,
                null, String.class);
        SQLContainer container = EasyMock.createMock(SQLContainer.class);
        new RowItem(container, new RowId(new Object[] { 1 }),
                Arrays.asList(cp));
        cp.setValue("asdf");
        assertEquals("asdf", cp.getValue());
        cp.setValue(null);
        assertNull(cp.getValue());
    }

    @Test
    public void setValue_sendsItemChangeNotification() throws SQLException {

        class TestContainer extends SQLContainer {
            Object value = null;
            boolean modified = false;

            public TestContainer(QueryDelegate delegate) throws SQLException {
                super(delegate);
            }

            @Override
            public void itemChangeNotification(RowItem changedItem) {
                ColumnProperty cp = (ColumnProperty) changedItem
                        .getItemProperty("NAME");
                value = cp.getValue();
                modified = cp.isModified();
            }
        }

        ColumnProperty property = new ColumnProperty("NAME", false, true, true,
                false, "Ville", String.class);

        Statement statement = EasyMock.createNiceMock(Statement.class);
        EasyMock.replay(statement);

        ResultSetMetaData metadata = EasyMock
                .createNiceMock(ResultSetMetaData.class);
        EasyMock.replay(metadata);

        ResultSet resultSet = EasyMock.createNiceMock(ResultSet.class);
        EasyMock.expect(resultSet.getStatement()).andReturn(statement);
        EasyMock.expect(resultSet.getMetaData()).andReturn(metadata);
        EasyMock.replay(resultSet);

        QueryDelegate delegate = EasyMock.createNiceMock(QueryDelegate.class);
        EasyMock.expect(delegate.getResults(0, 1)).andReturn(resultSet);
        EasyMock.replay(delegate);

        TestContainer container = new TestContainer(delegate);

        new RowItem(container, new RowId(new Object[] { 1 }),
                Arrays.asList(property));

        property.setValue("Kalle");
        assertEquals("Kalle", container.value);
        assertTrue(container.modified);
    }

    @Test
    public void versionColumnsShouldNotBeInValueMap_shouldReturnFalse() {
        ColumnProperty property = new ColumnProperty("NAME", false, true, true,
                false, "Ville", String.class);
        property.setVersionColumn(true);

        assertFalse(property.isPersistent());
    }

    @Test
    public void neverWritableColumnsShouldNotBeInValueMap_shouldReturnFalse() {
        ColumnProperty property = new ColumnProperty("NAME", true, false, true,
                false, "Ville", String.class);

        assertFalse(property.isPersistent());
    }

    @Test
    public void writableColumnsShouldBeInValueMap_shouldReturnTrue() {
        ColumnProperty property = new ColumnProperty("NAME", false, true, true,
                false, "Ville", String.class);

        assertTrue(property.isPersistent());
    }

    @Test
    public void writableButReadOnlyColumnsShouldNotBeInValueMap_shouldReturnFalse() {
        ColumnProperty property = new ColumnProperty("NAME", true, true, true,
                false, "Ville", String.class);

        assertFalse(property.isPersistent());
    }

    @Test
    public void primKeysShouldBeRowIdentifiers_shouldReturnTrue() {
        ColumnProperty property = new ColumnProperty("NAME", false, true, true,
                true, "Ville", String.class);

        assertTrue(property.isRowIdentifier());
    }

    @Test
    public void versionColumnsShouldBeRowIdentifiers_shouldReturnTrue() {
        ColumnProperty property = new ColumnProperty("NAME", false, true, true,
                false, "Ville", String.class);
        property.setVersionColumn(true);

        assertTrue(property.isRowIdentifier());
    }

    @Test
    public void nonPrimKeyOrVersionColumnsShouldBeNotRowIdentifiers_shouldReturnFalse() {
        ColumnProperty property = new ColumnProperty("NAME", false, true, true,
                false, "Ville", String.class);

        assertFalse(property.isRowIdentifier());
    }

    @Test
    public void getOldValueShouldReturnPreviousValue_shouldReturnVille() {
        ColumnProperty property = new ColumnProperty("NAME", false, true, true,
                false, "Ville", String.class);

        // Here we really don't care about the container management, but in
        // order to set the value for a column the owner (RowItem) must be set
        // and to create the owner we must have a container...
        List<ColumnProperty> properties = new ArrayList<ColumnProperty>();
        properties.add(property);

        SQLContainer container = EasyMock.createNiceMock(SQLContainer.class);
        RowItem rowItem = new RowItem(container, new RowId(new Object[] { 1 }),
                Arrays.asList(property));

        property.setValue("Kalle");
        // Just check that the new value was actually set...
        assertEquals("Kalle", property.getValue());
        // Assert that old value is the original value...
        assertEquals("Ville", property.getOldValue());
    }

}

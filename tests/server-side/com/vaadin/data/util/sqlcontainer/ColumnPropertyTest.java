package com.vaadin.data.util.sqlcontainer;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import com.vaadin.data.Property.ReadOnlyException;
import com.vaadin.data.util.sqlcontainer.ColumnProperty.NotNullableException;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

public class ColumnPropertyTest {

    @Test
    public void constructor_legalParameters_shouldSucceed() {
        ColumnProperty cp = new ColumnProperty("NAME", false, true, true,
                "Ville", String.class);
        Assert.assertNotNull(cp);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_missingPropertyId_shouldFail() {
        new ColumnProperty(null, false, true, true, "Ville", String.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_missingType_shouldFail() {
        new ColumnProperty("NAME", false, true, true, "Ville", null);
    }

    @Test
    public void getValue_defaultValue_returnsVille() {
        ColumnProperty cp = new ColumnProperty("NAME", false, true, true,
                "Ville", String.class);
        Assert.assertEquals("Ville", cp.getValue());
    }

    @Test
    public void setValue_readWriteNullable_returnsKalle() {
        ColumnProperty cp = new ColumnProperty("NAME", false, true, true,
                "Ville", String.class);
        SQLContainer container = EasyMock.createMock(SQLContainer.class);
        RowItem owner = new RowItem(container, new RowId(new Object[] { 1 }),
                Arrays.asList(cp));
        container.itemChangeNotification(owner);
        EasyMock.replay(container);
        cp.setValue("Kalle");
        Assert.assertEquals("Kalle", cp.getValue());
        EasyMock.verify(container);
    }

    @Test(expected = ReadOnlyException.class)
    public void setValue_readOnlyNullable_shouldFail() {
        ColumnProperty cp = new ColumnProperty("NAME", true, true, true,
                "Ville", String.class);
        SQLContainer container = EasyMock.createMock(SQLContainer.class);
        new RowItem(container, new RowId(new Object[] { 1 }), Arrays.asList(cp));
        EasyMock.replay(container);
        cp.setValue("Kalle");
        EasyMock.verify(container);
    }

    @Test
    public void setValue_readWriteNullable_nullShouldWork() {
        ColumnProperty cp = new ColumnProperty("NAME", false, true, true,
                "Ville", String.class);
        SQLContainer container = EasyMock.createMock(SQLContainer.class);
        RowItem owner = new RowItem(container, new RowId(new Object[] { 1 }),
                Arrays.asList(cp));
        container.itemChangeNotification(owner);
        EasyMock.replay(container);
        cp.setValue(null);
        Assert.assertNull(cp.getValue());
        EasyMock.verify(container);
    }

    @Test(expected = NotNullableException.class)
    public void setValue_readWriteNotNullable_nullShouldFail() {
        ColumnProperty cp = new ColumnProperty("NAME", false, true, false,
                "Ville", String.class);
        SQLContainer container = EasyMock.createMock(SQLContainer.class);
        RowItem owner = new RowItem(container, new RowId(new Object[] { 1 }),
                Arrays.asList(cp));
        container.itemChangeNotification(owner);
        EasyMock.replay(container);
        cp.setValue(null);
        Assert.assertNotNull(cp.getValue());
        EasyMock.verify(container);
    }

    @Test
    public void getType_normal_returnsStringClass() {
        ColumnProperty cp = new ColumnProperty("NAME", false, true, true,
                "Ville", String.class);
        Assert.assertSame(String.class, cp.getType());
    }

    @Test
    public void isReadOnly_readWriteNullable_returnsTrue() {
        ColumnProperty cp = new ColumnProperty("NAME", false, true, true,
                "Ville", String.class);
        Assert.assertFalse(cp.isReadOnly());
    }

    @Test
    public void isReadOnly_readOnlyNullable_returnsTrue() {
        ColumnProperty cp = new ColumnProperty("NAME", true, true, true,
                "Ville", String.class);
        Assert.assertTrue(cp.isReadOnly());
    }

    @Test
    public void setReadOnly_readOnlyChangeAllowed_shouldSucceed() {
        ColumnProperty cp = new ColumnProperty("NAME", false, true, true,
                "Ville", String.class);
        cp.setReadOnly(true);
        Assert.assertTrue(cp.isReadOnly());
    }

    @Test
    public void setReadOnly_readOnlyChangeDisallowed_shouldFail() {
        ColumnProperty cp = new ColumnProperty("NAME", false, false, true,
                "Ville", String.class);
        cp.setReadOnly(true);
        Assert.assertFalse(cp.isReadOnly());
    }

    @Test
    public void getPropertyId_normal_returnsNAME() {
        ColumnProperty cp = new ColumnProperty("NAME", false, false, true,
                "Ville", String.class);
        Assert.assertEquals("NAME", cp.getPropertyId());
    }

    @Test
    public void isModified_valueModified_returnsTrue() {
        ColumnProperty cp = new ColumnProperty("NAME", false, true, true,
                "Ville", String.class);
        SQLContainer container = EasyMock.createMock(SQLContainer.class);
        RowItem owner = new RowItem(container, new RowId(new Object[] { 1 }),
                Arrays.asList(cp));
        container.itemChangeNotification(owner);
        EasyMock.replay(container);
        cp.setValue("Kalle");
        Assert.assertEquals("Kalle", cp.getValue());
        Assert.assertTrue(cp.isModified());
        EasyMock.verify(container);
    }

    @Test
    public void isModified_valueNotModified_returnsFalse() {
        ColumnProperty cp = new ColumnProperty("NAME", false, false, true,
                "Ville", String.class);
        Assert.assertFalse(cp.isModified());
    }

    @Test
    public void setValue_nullOnNullable_shouldWork() {
        ColumnProperty cp = new ColumnProperty("NAME", false, true, true,
                "asdf", String.class);
        SQLContainer container = EasyMock.createMock(SQLContainer.class);
        new RowItem(container, new RowId(new Object[] { 1 }), Arrays.asList(cp));
        cp.setValue(null);
        Assert.assertNull(cp.getValue());
    }

    @Test
    public void setValue_resetTonullOnNullable_shouldWork() {
        ColumnProperty cp = new ColumnProperty("NAME", false, true, true, null,
                String.class);
        SQLContainer container = EasyMock.createMock(SQLContainer.class);
        new RowItem(container, new RowId(new Object[] { 1 }), Arrays.asList(cp));
        cp.setValue("asdf");
        Assert.assertEquals("asdf", cp.getValue());
        cp.setValue(null);
        Assert.assertNull(cp.getValue());
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
                "Ville", String.class);

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
        Assert.assertEquals("Kalle", container.value);
        Assert.assertTrue(container.modified);
    }
}

package com.vaadin.data.util.sqlcontainer;

import org.junit.Assert;
import org.junit.Test;

public class RowIdTest {

    @Test
    public void constructor_withArrayOfPrimaryKeyColumns_shouldSucceed() {
        RowId id = new RowId(new Object[] { "id", "name" });
        Assert.assertArrayEquals(new Object[] { "id", "name" }, id.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_withNullParameter_shouldFail() {
        new RowId(null);
    }

    @Test
    public void hashCode_samePrimaryKeys_sameResult() {
        RowId id = new RowId(new Object[] { "id", "name" });
        RowId id2 = new RowId(new Object[] { "id", "name" });
        Assert.assertEquals(id.hashCode(), id2.hashCode());
    }

    @Test
    public void hashCode_differentPrimaryKeys_differentResult() {
        RowId id = new RowId(new Object[] { "id", "name" });
        RowId id2 = new RowId(new Object[] { "id" });
        Assert.assertFalse(id.hashCode() == id2.hashCode());
    }

    @Test
    public void equals_samePrimaryKeys_returnsTrue() {
        RowId id = new RowId(new Object[] { "id", "name" });
        RowId id2 = new RowId(new Object[] { "id", "name" });
        Assert.assertEquals(id, id2);
    }

    @Test
    public void equals_differentPrimaryKeys_returnsFalse() {
        RowId id = new RowId(new Object[] { "id", "name" });
        RowId id2 = new RowId(new Object[] { "id" });
        Assert.assertFalse(id.equals(id2.hashCode()));
    }

    @Test
    public void equals_differentDataType_returnsFalse() {
        RowId id = new RowId(new Object[] { "id", "name" });
        Assert.assertFalse(id.equals("Tudiluu"));
        Assert.assertFalse(id.equals(new Integer(1337)));
    }

    @Test
    public void toString_defaultCtor_noException() {
        RowId rowId = new RowId();
        Assert.assertTrue("Unexpected to string for empty Row Id", rowId
                .toString().isEmpty());
    }
}

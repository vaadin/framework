package com.vaadin.v7.data.util.sqlcontainer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ReadOnlyRowIdTest {

    @Test
    public void getRowNum_shouldReturnRowNumGivenInConstructor() {
        int rowNum = 1337;
        ReadOnlyRowId rid = new ReadOnlyRowId(rowNum);
        assertEquals(rowNum, rid.getRowNum());
    }

    @Test
    public void hashCode_shouldBeEqualToHashCodeOfRowNum() {
        int rowNum = 1337;
        ReadOnlyRowId rid = new ReadOnlyRowId(rowNum);
        assertEquals(Integer.valueOf(rowNum).hashCode(), rid.hashCode());
    }

    @Test
    public void equals_compareWithNull_shouldBeFalse() {
        ReadOnlyRowId rid = new ReadOnlyRowId(1337);
        assertFalse(rid.equals(null));
    }

    @Test
    public void equals_compareWithSameInstance_shouldBeTrue() {
        ReadOnlyRowId rid = new ReadOnlyRowId(1337);
        ReadOnlyRowId rid2 = rid;
        assertTrue(rid.equals(rid2));
    }

    @Test
    public void equals_compareWithOtherType_shouldBeFalse() {
        ReadOnlyRowId rid = new ReadOnlyRowId(1337);
        assertFalse(rid.equals(new Object()));
    }

    @Test
    public void equals_compareWithOtherRowId_shouldBeFalse() {
        ReadOnlyRowId rid = new ReadOnlyRowId(1337);
        ReadOnlyRowId rid2 = new ReadOnlyRowId(42);
        assertFalse(rid.equals(rid2));
    }

    @Test
    public void toString_rowNumberIsReturned() {
        int i = 1;
        ReadOnlyRowId rowId = new ReadOnlyRowId(i);
        assertEquals("Unexpected toString value", String.valueOf(i),
                rowId.toString());
    }
}

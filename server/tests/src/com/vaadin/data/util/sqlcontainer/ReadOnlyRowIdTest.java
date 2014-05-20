package com.vaadin.data.util.sqlcontainer;

import org.junit.Assert;
import org.junit.Test;

public class ReadOnlyRowIdTest {

    @Test
    public void getRowNum_shouldReturnRowNumGivenInConstructor() {
        int rowNum = 1337;
        ReadOnlyRowId rid = new ReadOnlyRowId(rowNum);
        Assert.assertEquals(rowNum, rid.getRowNum());
    }

    @Test
    public void hashCode_shouldBeEqualToHashCodeOfRowNum() {
        int rowNum = 1337;
        ReadOnlyRowId rid = new ReadOnlyRowId(rowNum);
        Assert.assertEquals(Integer.valueOf(rowNum).hashCode(), rid.hashCode());
    }

    @Test
    public void equals_compareWithNull_shouldBeFalse() {
        ReadOnlyRowId rid = new ReadOnlyRowId(1337);
        Assert.assertFalse(rid.equals(null));
    }

    @Test
    public void equals_compareWithSameInstance_shouldBeTrue() {
        ReadOnlyRowId rid = new ReadOnlyRowId(1337);
        ReadOnlyRowId rid2 = rid;
        Assert.assertTrue(rid.equals(rid2));
    }

    @Test
    public void equals_compareWithOtherType_shouldBeFalse() {
        ReadOnlyRowId rid = new ReadOnlyRowId(1337);
        Assert.assertFalse(rid.equals(new Object()));
    }

    @Test
    public void equals_compareWithOtherRowId_shouldBeFalse() {
        ReadOnlyRowId rid = new ReadOnlyRowId(1337);
        ReadOnlyRowId rid2 = new ReadOnlyRowId(42);
        Assert.assertFalse(rid.equals(rid2));
    }
}

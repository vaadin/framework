/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.v7.data.util.sqlcontainer;

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

    @Test
    public void toString_rowNumberIsReturned() {
        int i = 1;
        ReadOnlyRowId rowId = new ReadOnlyRowId(i);
        Assert.assertEquals("Unexpected toString value", String.valueOf(i),
                rowId.toString());
    }
}

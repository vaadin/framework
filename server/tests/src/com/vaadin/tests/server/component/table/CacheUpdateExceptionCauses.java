/*
 * Copyright 2012 Vaadin Ltd.
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

package com.vaadin.tests.server.component.table;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CacheUpdateException;

public class CacheUpdateExceptionCauses {
    @Test
    public void testSingleCauseException() {
        Table table = new Table();
        Throwable[] causes = new Throwable[] { new RuntimeException(
                "Broken in one way.") };

        CacheUpdateException exception = new CacheUpdateException(table,
                "Error during Table cache update.", causes);

        Assert.assertSame(causes[0], exception.getCause());
        Assert.assertEquals("Error during Table cache update.",
                exception.getMessage());
    }

    @Test
    public void testMultipleCauseException() {
        Table table = new Table();
        Throwable[] causes = new Throwable[] {
                new RuntimeException("Broken in the first way."),
                new RuntimeException("Broken in the second way.") };

        CacheUpdateException exception = new CacheUpdateException(table,
                "Error during Table cache update.", causes);

        Assert.assertSame(causes[0], exception.getCause());
        Assert.assertEquals(
                "Error during Table cache update. Additional causes not shown.",
                exception.getMessage());
    }
}

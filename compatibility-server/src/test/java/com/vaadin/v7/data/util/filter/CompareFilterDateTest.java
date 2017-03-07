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
package com.vaadin.v7.data.util.filter;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.data.util.PropertysetItem;
import com.vaadin.v7.data.util.filter.Compare.Equal;
import com.vaadin.v7.data.util.filter.Compare.Greater;
import com.vaadin.v7.data.util.filter.Compare.GreaterOrEqual;
import com.vaadin.v7.data.util.filter.Compare.Less;
import com.vaadin.v7.data.util.filter.Compare.LessOrEqual;

public class CompareFilterDateTest extends AbstractFilterTestBase<Compare> {

    protected Item itemNullUtilDate;
    protected Item itemNullSqlDate;
    protected Item itemUtilDate;
    protected Item itemSqlDate;

    protected SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");

    protected Filter equalCompUtilDate;
    protected Filter greaterCompUtilDate;
    protected Filter lessCompUtilDate;
    protected Filter greaterEqualCompUtilDate;
    protected Filter lessEqualCompUtilDate;

    protected Filter equalCompSqlDate;
    protected Filter greaterCompSqlDate;
    protected Filter lessCompSqlDate;
    protected Filter greaterEqualCompSqlDate;
    protected Filter lessEqualCompSqlDate;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        equalCompUtilDate = new Equal(PROPERTY1, formatter.parse("26072016"));
        greaterCompUtilDate = new Greater(PROPERTY1,
                formatter.parse("26072016"));
        lessCompUtilDate = new Less(PROPERTY1, formatter.parse("26072016"));
        greaterEqualCompUtilDate = new GreaterOrEqual(PROPERTY1,
                formatter.parse("26072016"));
        lessEqualCompUtilDate = new LessOrEqual(PROPERTY1,
                formatter.parse("26072016"));

        equalCompSqlDate = new Equal(PROPERTY1,
                new java.sql.Date(formatter.parse("26072016").getTime()));
        greaterCompSqlDate = new Greater(PROPERTY1,
                new java.sql.Date(formatter.parse("26072016").getTime()));
        lessCompSqlDate = new Less(PROPERTY1,
                new java.sql.Date(formatter.parse("26072016").getTime()));
        greaterEqualCompSqlDate = new GreaterOrEqual(PROPERTY1,
                new java.sql.Date(formatter.parse("26072016").getTime()));
        lessEqualCompSqlDate = new LessOrEqual(PROPERTY1,
                new java.sql.Date(formatter.parse("26072016").getTime()));

        itemNullUtilDate = new PropertysetItem();
        itemNullUtilDate.addItemProperty(PROPERTY1,
                new ObjectProperty<Date>(null, Date.class));
        itemNullSqlDate = new PropertysetItem();
        itemNullSqlDate.addItemProperty(PROPERTY1,
                new ObjectProperty<java.sql.Date>(null, java.sql.Date.class));
        itemUtilDate = new PropertysetItem();
        itemUtilDate.addItemProperty(PROPERTY1, new ObjectProperty<Date>(
                formatter.parse("25072016"), Date.class));
        itemSqlDate = new PropertysetItem();
        itemSqlDate.addItemProperty(PROPERTY1,
                new ObjectProperty<java.sql.Date>(
                        new java.sql.Date(
                                formatter.parse("25072016").getTime()),
                        java.sql.Date.class));
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        itemNullUtilDate = null;
        itemNullSqlDate = null;
        itemUtilDate = null;
        itemSqlDate = null;
    }

    @Test
    public void testCompareUtilDatesAndUtilDates() {
        Assert.assertFalse(
                equalCompUtilDate.passesFilter(null, itemNullUtilDate));
        Assert.assertFalse(equalCompUtilDate.passesFilter(null, itemUtilDate));
        Assert.assertFalse(
                greaterCompUtilDate.passesFilter(null, itemUtilDate));
        Assert.assertTrue(lessCompUtilDate.passesFilter(null, itemUtilDate));
        Assert.assertFalse(
                greaterEqualCompUtilDate.passesFilter(null, itemUtilDate));
        Assert.assertTrue(
                lessEqualCompUtilDate.passesFilter(null, itemUtilDate));
    }

    @Test
    public void testCompareUtilDatesAndSqlDates() {
        Assert.assertFalse(
                equalCompUtilDate.passesFilter(null, itemNullSqlDate));
        Assert.assertFalse(equalCompUtilDate.passesFilter(null, itemSqlDate));
        Assert.assertFalse(greaterCompUtilDate.passesFilter(null, itemSqlDate));
        Assert.assertTrue(lessCompUtilDate.passesFilter(null, itemSqlDate));
        Assert.assertFalse(
                greaterEqualCompUtilDate.passesFilter(null, itemSqlDate));
        Assert.assertTrue(
                lessEqualCompUtilDate.passesFilter(null, itemSqlDate));
    }

    @Test
    public void testCompareSqlDatesAndSqlDates() {
        Assert.assertFalse(
                equalCompSqlDate.passesFilter(null, itemNullSqlDate));
        Assert.assertFalse(equalCompSqlDate.passesFilter(null, itemSqlDate));
        Assert.assertFalse(greaterCompSqlDate.passesFilter(null, itemSqlDate));
        Assert.assertTrue(lessCompSqlDate.passesFilter(null, itemSqlDate));
        Assert.assertFalse(
                greaterEqualCompSqlDate.passesFilter(null, itemSqlDate));
        Assert.assertTrue(lessEqualCompSqlDate.passesFilter(null, itemSqlDate));
    }

    @Test
    public void testCompareSqlDatesAndUtilDates() {
        Assert.assertFalse(
                equalCompSqlDate.passesFilter(null, itemNullUtilDate));
        Assert.assertFalse(equalCompSqlDate.passesFilter(null, itemUtilDate));
        Assert.assertFalse(greaterCompSqlDate.passesFilter(null, itemUtilDate));
        Assert.assertTrue(lessCompSqlDate.passesFilter(null, itemUtilDate));
        Assert.assertFalse(
                greaterEqualCompSqlDate.passesFilter(null, itemUtilDate));
        Assert.assertTrue(
                lessEqualCompSqlDate.passesFilter(null, itemUtilDate));
    }

}

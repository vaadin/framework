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
        assertFalse(
                equalCompUtilDate.passesFilter(null, itemNullUtilDate));
        assertFalse(equalCompUtilDate.passesFilter(null, itemUtilDate));
        assertFalse(
                greaterCompUtilDate.passesFilter(null, itemUtilDate));
        assertTrue(lessCompUtilDate.passesFilter(null, itemUtilDate));
        assertFalse(
                greaterEqualCompUtilDate.passesFilter(null, itemUtilDate));
        assertTrue(
                lessEqualCompUtilDate.passesFilter(null, itemUtilDate));
    }

    @Test
    public void testCompareUtilDatesAndSqlDates() {
        assertFalse(
                equalCompUtilDate.passesFilter(null, itemNullSqlDate));
        assertFalse(equalCompUtilDate.passesFilter(null, itemSqlDate));
        assertFalse(greaterCompUtilDate.passesFilter(null, itemSqlDate));
        assertTrue(lessCompUtilDate.passesFilter(null, itemSqlDate));
        assertFalse(
                greaterEqualCompUtilDate.passesFilter(null, itemSqlDate));
        assertTrue(
                lessEqualCompUtilDate.passesFilter(null, itemSqlDate));
    }

    @Test
    public void testCompareSqlDatesAndSqlDates() {
        assertFalse(
                equalCompSqlDate.passesFilter(null, itemNullSqlDate));
        assertFalse(equalCompSqlDate.passesFilter(null, itemSqlDate));
        assertFalse(greaterCompSqlDate.passesFilter(null, itemSqlDate));
        assertTrue(lessCompSqlDate.passesFilter(null, itemSqlDate));
        assertFalse(
                greaterEqualCompSqlDate.passesFilter(null, itemSqlDate));
        assertTrue(lessEqualCompSqlDate.passesFilter(null, itemSqlDate));
    }

    @Test
    public void testCompareSqlDatesAndUtilDates() {
        assertFalse(
                equalCompSqlDate.passesFilter(null, itemNullUtilDate));
        assertFalse(equalCompSqlDate.passesFilter(null, itemUtilDate));
        assertFalse(greaterCompSqlDate.passesFilter(null, itemUtilDate));
        assertTrue(lessCompSqlDate.passesFilter(null, itemUtilDate));
        assertFalse(
                greaterEqualCompSqlDate.passesFilter(null, itemUtilDate));
        assertTrue(
                lessEqualCompSqlDate.passesFilter(null, itemUtilDate));
    }

}

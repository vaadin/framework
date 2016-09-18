package com.vaadin.data.util.filter;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Compare.Greater;
import com.vaadin.data.util.filter.Compare.GreaterOrEqual;
import com.vaadin.data.util.filter.Compare.Less;
import com.vaadin.data.util.filter.Compare.LessOrEqual;

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

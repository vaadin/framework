package com.vaadin.data.util.sqlcontainer.query;

import java.util.ArrayList;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Between;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Compare.Greater;
import com.vaadin.data.util.filter.Compare.GreaterOrEqual;
import com.vaadin.data.util.filter.Compare.Less;
import com.vaadin.data.util.filter.Compare.LessOrEqual;
import com.vaadin.data.util.filter.IsNull;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.Not;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.StringDecorator;

public class QueryBuilderTest {

    private StatementHelper mockedStatementHelper(Object... values) {
        StatementHelper sh = EasyMock.createMock(StatementHelper.class);
        for (Object val : values) {
            sh.addParameterValue(val);
            EasyMock.expectLastCall();
        }
        EasyMock.replay(sh);
        return sh;
    }

    // escape bad characters and wildcards

    @Test
    public void getWhereStringForFilter_equals() {
        StatementHelper sh = mockedStatementHelper("Fido");
        Equal f = new Equal("NAME", "Fido");
        Assert.assertEquals("\"NAME\" = ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_greater() {
        StatementHelper sh = mockedStatementHelper(18);
        Greater f = new Greater("AGE", 18);
        Assert.assertEquals("\"AGE\" > ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_less() {
        StatementHelper sh = mockedStatementHelper(65);
        Less f = new Less("AGE", 65);
        Assert.assertEquals("\"AGE\" < ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_greaterOrEqual() {
        StatementHelper sh = mockedStatementHelper(18);
        GreaterOrEqual f = new GreaterOrEqual("AGE", 18);
        Assert.assertEquals("\"AGE\" >= ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_lessOrEqual() {
        StatementHelper sh = mockedStatementHelper(65);
        LessOrEqual f = new LessOrEqual("AGE", 65);
        Assert.assertEquals("\"AGE\" <= ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_simpleStringFilter() {
        StatementHelper sh = mockedStatementHelper("Vi%");
        SimpleStringFilter f = new SimpleStringFilter("NAME", "Vi", false, true);
        Assert.assertEquals("\"NAME\" LIKE ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_simpleStringFilterMatchAnywhere() {
        StatementHelper sh = mockedStatementHelper("%Vi%");
        SimpleStringFilter f = new SimpleStringFilter("NAME", "Vi", false,
                false);
        Assert.assertEquals("\"NAME\" LIKE ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_simpleStringFilterMatchAnywhereIgnoreCase() {
        StatementHelper sh = mockedStatementHelper("%VI%");
        SimpleStringFilter f = new SimpleStringFilter("NAME", "Vi", true, false);
        Assert.assertEquals("UPPER(\"NAME\") LIKE ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_startsWith() {
        StatementHelper sh = mockedStatementHelper("Vi%");
        Like f = new Like("NAME", "Vi%");
        Assert.assertEquals("\"NAME\" LIKE ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_startsWithNumber() {
        StatementHelper sh = mockedStatementHelper("1%");
        Like f = new Like("AGE", "1%");
        Assert.assertEquals("\"AGE\" LIKE ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_endsWith() {
        StatementHelper sh = mockedStatementHelper("%lle");
        Like f = new Like("NAME", "%lle");
        Assert.assertEquals("\"NAME\" LIKE ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_contains() {
        StatementHelper sh = mockedStatementHelper("%ill%");
        Like f = new Like("NAME", "%ill%");
        Assert.assertEquals("\"NAME\" LIKE ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_between() {
        StatementHelper sh = mockedStatementHelper(18, 65);
        Between f = new Between("AGE", 18, 65);
        Assert.assertEquals("\"AGE\" BETWEEN ? AND ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_caseInsensitive_equals() {
        StatementHelper sh = mockedStatementHelper("FIDO");
        Like f = new Like("NAME", "Fido");
        f.setCaseSensitive(false);
        Assert.assertEquals("UPPER(\"NAME\") LIKE ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_caseInsensitive_startsWith() {
        StatementHelper sh = mockedStatementHelper("VI%");
        Like f = new Like("NAME", "Vi%");
        f.setCaseSensitive(false);
        Assert.assertEquals("UPPER(\"NAME\") LIKE ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_caseInsensitive_endsWith() {
        StatementHelper sh = mockedStatementHelper("%LLE");
        Like f = new Like("NAME", "%lle");
        f.setCaseSensitive(false);
        Assert.assertEquals("UPPER(\"NAME\") LIKE ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_caseInsensitive_contains() {
        StatementHelper sh = mockedStatementHelper("%ILL%");
        Like f = new Like("NAME", "%ill%");
        f.setCaseSensitive(false);
        Assert.assertEquals("UPPER(\"NAME\") LIKE ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilters_listOfFilters() {
        StatementHelper sh = mockedStatementHelper("%lle", 18);
        ArrayList<Filter> filters = new ArrayList<Filter>();
        filters.add(new Like("NAME", "%lle"));
        filters.add(new Greater("AGE", 18));
        Assert.assertEquals(" WHERE \"NAME\" LIKE ? AND \"AGE\" > ?",
                QueryBuilder.getWhereStringForFilters(filters, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilters_oneAndFilter() {
        StatementHelper sh = mockedStatementHelper("%lle", 18);
        ArrayList<Filter> filters = new ArrayList<Filter>();
        filters.add(new And(new Like("NAME", "%lle"), new Greater("AGE", 18)));
        Assert.assertEquals(" WHERE (\"NAME\" LIKE ? AND \"AGE\" > ?)",
                QueryBuilder.getWhereStringForFilters(filters, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilters_oneOrFilter() {
        StatementHelper sh = mockedStatementHelper("%lle", 18);
        ArrayList<Filter> filters = new ArrayList<Filter>();
        filters.add(new Or(new Like("NAME", "%lle"), new Greater("AGE", 18)));
        Assert.assertEquals(" WHERE (\"NAME\" LIKE ? OR \"AGE\" > ?)",
                QueryBuilder.getWhereStringForFilters(filters, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilters_complexCompoundFilters() {
        StatementHelper sh = mockedStatementHelper("%lle", 18, 65, "Pelle");
        ArrayList<Filter> filters = new ArrayList<Filter>();
        filters.add(new Or(new And(new Like("NAME", "%lle"), new Or(new Less(
                "AGE", 18), new Greater("AGE", 65))),
                new Equal("NAME", "Pelle")));
        Assert.assertEquals(
                " WHERE ((\"NAME\" LIKE ? AND (\"AGE\" < ? OR \"AGE\" > ?)) OR \"NAME\" = ?)",
                QueryBuilder.getWhereStringForFilters(filters, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilters_complexCompoundFiltersAndSingleFilter() {
        StatementHelper sh = mockedStatementHelper("%lle", 18, 65, "Pelle",
                "Virtanen");
        ArrayList<Filter> filters = new ArrayList<Filter>();
        filters.add(new Or(new And(new Like("NAME", "%lle"), new Or(new Less(
                "AGE", 18), new Greater("AGE", 65))),
                new Equal("NAME", "Pelle")));
        filters.add(new Equal("LASTNAME", "Virtanen"));
        Assert.assertEquals(
                " WHERE ((\"NAME\" LIKE ? AND (\"AGE\" < ? OR \"AGE\" > ?)) OR \"NAME\" = ?) AND \"LASTNAME\" = ?",
                QueryBuilder.getWhereStringForFilters(filters, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilters_emptyList_shouldReturnEmptyString() {
        ArrayList<Filter> filters = new ArrayList<Filter>();
        Assert.assertEquals("", QueryBuilder.getWhereStringForFilters(filters,
                new StatementHelper()));
    }

    @Test
    public void getWhereStringForFilters_NotFilter() {
        StatementHelper sh = mockedStatementHelper(18);
        ArrayList<Filter> filters = new ArrayList<Filter>();
        filters.add(new Not(new Equal("AGE", 18)));
        Assert.assertEquals(" WHERE NOT \"AGE\" = ?",
                QueryBuilder.getWhereStringForFilters(filters, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilters_complexNegatedFilter() {
        StatementHelper sh = mockedStatementHelper(65, 18);
        ArrayList<Filter> filters = new ArrayList<Filter>();
        filters.add(new Not(new Or(new Equal("AGE", 65), new Equal("AGE", 18))));
        Assert.assertEquals(" WHERE NOT (\"AGE\" = ? OR \"AGE\" = ?)",
                QueryBuilder.getWhereStringForFilters(filters, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilters_isNull() {
        ArrayList<Filter> filters = new ArrayList<Filter>();
        filters.add(new IsNull("NAME"));
        Assert.assertEquals(" WHERE \"NAME\" IS NULL", QueryBuilder
                .getWhereStringForFilters(filters, new StatementHelper()));
    }

    @Test
    public void getWhereStringForFilters_isNotNull() {
        ArrayList<Filter> filters = new ArrayList<Filter>();
        filters.add(new Not(new IsNull("NAME")));
        Assert.assertEquals(" WHERE \"NAME\" IS NOT NULL", QueryBuilder
                .getWhereStringForFilters(filters, new StatementHelper()));
    }

    @Test
    public void getWhereStringForFilters_customStringDecorator() {
        QueryBuilder.setStringDecorator(new StringDecorator("[", "]"));
        ArrayList<Filter> filters = new ArrayList<Filter>();
        filters.add(new Not(new IsNull("NAME")));
        Assert.assertEquals(" WHERE [NAME] IS NOT NULL", QueryBuilder
                .getWhereStringForFilters(filters, new StatementHelper()));
        // Reset the default string decorator
        QueryBuilder.setStringDecorator(new StringDecorator("\"", "\""));
    }
}

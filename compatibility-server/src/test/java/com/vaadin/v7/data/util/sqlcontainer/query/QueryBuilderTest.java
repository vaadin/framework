package com.vaadin.v7.data.util.sqlcontainer.query;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Test;

import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.util.filter.And;
import com.vaadin.v7.data.util.filter.Between;
import com.vaadin.v7.data.util.filter.Compare.Equal;
import com.vaadin.v7.data.util.filter.Compare.Greater;
import com.vaadin.v7.data.util.filter.Compare.GreaterOrEqual;
import com.vaadin.v7.data.util.filter.Compare.Less;
import com.vaadin.v7.data.util.filter.Compare.LessOrEqual;
import com.vaadin.v7.data.util.filter.IsNull;
import com.vaadin.v7.data.util.filter.Like;
import com.vaadin.v7.data.util.filter.Not;
import com.vaadin.v7.data.util.filter.Or;
import com.vaadin.v7.data.util.filter.SimpleStringFilter;
import com.vaadin.v7.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.v7.data.util.sqlcontainer.query.generator.filter.QueryBuilder;
import com.vaadin.v7.data.util.sqlcontainer.query.generator.filter.StringDecorator;

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
        assertEquals("\"NAME\" = ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_greater() {
        StatementHelper sh = mockedStatementHelper(18);
        Greater f = new Greater("AGE", 18);
        assertEquals("\"AGE\" > ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_less() {
        StatementHelper sh = mockedStatementHelper(65);
        Less f = new Less("AGE", 65);
        assertEquals("\"AGE\" < ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_greaterOrEqual() {
        StatementHelper sh = mockedStatementHelper(18);
        GreaterOrEqual f = new GreaterOrEqual("AGE", 18);
        assertEquals("\"AGE\" >= ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_lessOrEqual() {
        StatementHelper sh = mockedStatementHelper(65);
        LessOrEqual f = new LessOrEqual("AGE", 65);
        assertEquals("\"AGE\" <= ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_simpleStringFilter() {
        StatementHelper sh = mockedStatementHelper("Vi%");
        SimpleStringFilter f = new SimpleStringFilter("NAME", "Vi", false,
                true);
        assertEquals("\"NAME\" LIKE ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_simpleStringFilterMatchAnywhere() {
        StatementHelper sh = mockedStatementHelper("%Vi%");
        SimpleStringFilter f = new SimpleStringFilter("NAME", "Vi", false,
                false);
        assertEquals("\"NAME\" LIKE ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_simpleStringFilterMatchAnywhereIgnoreCase() {
        StatementHelper sh = mockedStatementHelper("%VI%");
        SimpleStringFilter f = new SimpleStringFilter("NAME", "Vi", true,
                false);
        assertEquals("UPPER(\"NAME\") LIKE ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_startsWith() {
        StatementHelper sh = mockedStatementHelper("Vi%");
        Like f = new Like("NAME", "Vi%");
        assertEquals("\"NAME\" LIKE ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_startsWithNumber() {
        StatementHelper sh = mockedStatementHelper("1%");
        Like f = new Like("AGE", "1%");
        assertEquals("\"AGE\" LIKE ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_endsWith() {
        StatementHelper sh = mockedStatementHelper("%lle");
        Like f = new Like("NAME", "%lle");
        assertEquals("\"NAME\" LIKE ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_contains() {
        StatementHelper sh = mockedStatementHelper("%ill%");
        Like f = new Like("NAME", "%ill%");
        assertEquals("\"NAME\" LIKE ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_between() {
        StatementHelper sh = mockedStatementHelper(18, 65);
        Between f = new Between("AGE", 18, 65);
        assertEquals("\"AGE\" BETWEEN ? AND ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_caseInsensitive_equals() {
        StatementHelper sh = mockedStatementHelper("FIDO");
        Like f = new Like("NAME", "Fido");
        f.setCaseSensitive(false);
        assertEquals("UPPER(\"NAME\") LIKE ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_caseInsensitive_startsWith() {
        StatementHelper sh = mockedStatementHelper("VI%");
        Like f = new Like("NAME", "Vi%");
        f.setCaseSensitive(false);
        assertEquals("UPPER(\"NAME\") LIKE ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_caseInsensitive_endsWith() {
        StatementHelper sh = mockedStatementHelper("%LLE");
        Like f = new Like("NAME", "%lle");
        f.setCaseSensitive(false);
        assertEquals("UPPER(\"NAME\") LIKE ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilter_caseInsensitive_contains() {
        StatementHelper sh = mockedStatementHelper("%ILL%");
        Like f = new Like("NAME", "%ill%");
        f.setCaseSensitive(false);
        assertEquals("UPPER(\"NAME\") LIKE ?",
                QueryBuilder.getWhereStringForFilter(f, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilters_listOfFilters() {
        StatementHelper sh = mockedStatementHelper("%lle", 18);
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Like("NAME", "%lle"));
        filters.add(new Greater("AGE", 18));
        assertEquals(" WHERE \"NAME\" LIKE ? AND \"AGE\" > ?",
                QueryBuilder.getWhereStringForFilters(filters, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilters_oneAndFilter() {
        StatementHelper sh = mockedStatementHelper("%lle", 18);
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new And(new Like("NAME", "%lle"), new Greater("AGE", 18)));
        assertEquals(" WHERE (\"NAME\" LIKE ? AND \"AGE\" > ?)",
                QueryBuilder.getWhereStringForFilters(filters, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilters_oneOrFilter() {
        StatementHelper sh = mockedStatementHelper("%lle", 18);
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Or(new Like("NAME", "%lle"), new Greater("AGE", 18)));
        assertEquals(" WHERE (\"NAME\" LIKE ? OR \"AGE\" > ?)",
                QueryBuilder.getWhereStringForFilters(filters, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilters_complexCompoundFilters() {
        StatementHelper sh = mockedStatementHelper("%lle", 18, 65, "Pelle");
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Or(
                new And(new Like("NAME", "%lle"),
                        new Or(new Less("AGE", 18), new Greater("AGE", 65))),
                new Equal("NAME", "Pelle")));
        assertEquals(
                " WHERE ((\"NAME\" LIKE ? AND (\"AGE\" < ? OR \"AGE\" > ?)) OR \"NAME\" = ?)",
                QueryBuilder.getWhereStringForFilters(filters, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilters_complexCompoundFiltersAndSingleFilter() {
        StatementHelper sh = mockedStatementHelper("%lle", 18, 65, "Pelle",
                "Virtanen");
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Or(
                new And(new Like("NAME", "%lle"),
                        new Or(new Less("AGE", 18), new Greater("AGE", 65))),
                new Equal("NAME", "Pelle")));
        filters.add(new Equal("LASTNAME", "Virtanen"));
        assertEquals(
                " WHERE ((\"NAME\" LIKE ? AND (\"AGE\" < ? OR \"AGE\" > ?)) OR \"NAME\" = ?) AND \"LASTNAME\" = ?",
                QueryBuilder.getWhereStringForFilters(filters, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilters_emptyList_shouldReturnEmptyString() {
        List<Filter> filters = new ArrayList<Filter>();
        assertEquals("", QueryBuilder.getWhereStringForFilters(filters,
                new StatementHelper()));
    }

    @Test
    public void getWhereStringForFilters_NotFilter() {
        StatementHelper sh = mockedStatementHelper(18);
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Not(new Equal("AGE", 18)));
        assertEquals(" WHERE NOT \"AGE\" = ?",
                QueryBuilder.getWhereStringForFilters(filters, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilters_complexNegatedFilter() {
        StatementHelper sh = mockedStatementHelper(65, 18);
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(
                new Not(new Or(new Equal("AGE", 65), new Equal("AGE", 18))));
        assertEquals(" WHERE NOT (\"AGE\" = ? OR \"AGE\" = ?)",
                QueryBuilder.getWhereStringForFilters(filters, sh));
        EasyMock.verify(sh);
    }

    @Test
    public void getWhereStringForFilters_isNull() {
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new IsNull("NAME"));
        assertEquals(" WHERE \"NAME\" IS NULL", QueryBuilder
                .getWhereStringForFilters(filters, new StatementHelper()));
    }

    @Test
    public void getWhereStringForFilters_isNotNull() {
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Not(new IsNull("NAME")));
        assertEquals(" WHERE \"NAME\" IS NOT NULL", QueryBuilder
                .getWhereStringForFilters(filters, new StatementHelper()));
    }

    @Test
    public void getWhereStringForFilters_customStringDecorator() {
        QueryBuilder.setStringDecorator(new StringDecorator("[", "]"));
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Not(new IsNull("NAME")));
        assertEquals(" WHERE [NAME] IS NOT NULL", QueryBuilder
                .getWhereStringForFilters(filters, new StatementHelper()));
        // Reset the default string decorator
        QueryBuilder.setStringDecorator(new StringDecorator("\"", "\""));
    }
}

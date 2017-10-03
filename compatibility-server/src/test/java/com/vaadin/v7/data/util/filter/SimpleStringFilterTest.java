package com.vaadin.v7.data.util.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SimpleStringFilterTest
        extends AbstractFilterTestBase<SimpleStringFilter> {

    protected static TestItem<String, String> createTestItem() {
        return new TestItem<String, String>("abcde", "TeSt");
    }

    protected TestItem<String, String> getTestItem() {
        return createTestItem();
    }

    protected SimpleStringFilter f(Object propertyId, String filterString,
            boolean ignoreCase, boolean onlyMatchPrefix) {
        return new SimpleStringFilter(propertyId, filterString, ignoreCase,
                onlyMatchPrefix);
    }

    protected boolean passes(Object propertyId, String filterString,
            boolean ignoreCase, boolean onlyMatchPrefix) {
        return f(propertyId, filterString, ignoreCase, onlyMatchPrefix)
                .passesFilter(null, getTestItem());
    }

    @Test
    public void testStartsWithCaseSensitive() {
        assertTrue(passes(PROPERTY1, "ab", false, true));
        assertTrue(passes(PROPERTY1, "", false, true));

        assertFalse(passes(PROPERTY2, "ab", false, true));
        assertFalse(passes(PROPERTY1, "AB", false, true));
    }

    @Test
    public void testStartsWithCaseInsensitive() {
        assertTrue(passes(PROPERTY1, "AB", true, true));
        assertTrue(passes(PROPERTY2, "te", true, true));
        assertFalse(passes(PROPERTY2, "AB", true, true));
    }

    @Test
    public void testContainsCaseSensitive() {
        assertTrue(passes(PROPERTY1, "ab", false, false));
        assertTrue(passes(PROPERTY1, "abcde", false, false));
        assertTrue(passes(PROPERTY1, "cd", false, false));
        assertTrue(passes(PROPERTY1, "e", false, false));
        assertTrue(passes(PROPERTY1, "", false, false));

        assertFalse(passes(PROPERTY2, "ab", false, false));
        assertFalse(passes(PROPERTY1, "es", false, false));
    }

    @Test
    public void testContainsCaseInsensitive() {
        assertTrue(passes(PROPERTY1, "AB", true, false));
        assertTrue(passes(PROPERTY1, "aBcDe", true, false));
        assertTrue(passes(PROPERTY1, "CD", true, false));
        assertTrue(passes(PROPERTY1, "", true, false));

        assertTrue(passes(PROPERTY2, "es", true, false));

        assertFalse(passes(PROPERTY2, "ab", true, false));
    }

    @Test
    public void testAppliesToProperty() {
        SimpleStringFilter filter = f(PROPERTY1, "ab", false, true);
        assertTrue(filter.appliesToProperty(PROPERTY1));
        assertFalse(filter.appliesToProperty(PROPERTY2));
        assertFalse(filter.appliesToProperty("other"));
    }

    @Test
    public void testEqualsHashCode() {
        SimpleStringFilter filter = f(PROPERTY1, "ab", false, true);

        SimpleStringFilter f1 = f(PROPERTY2, "ab", false, true);
        SimpleStringFilter f1b = f(PROPERTY2, "ab", false, true);
        SimpleStringFilter f2 = f(PROPERTY1, "cd", false, true);
        SimpleStringFilter f2b = f(PROPERTY1, "cd", false, true);
        SimpleStringFilter f3 = f(PROPERTY1, "ab", true, true);
        SimpleStringFilter f3b = f(PROPERTY1, "ab", true, true);
        SimpleStringFilter f4 = f(PROPERTY1, "ab", false, false);
        SimpleStringFilter f4b = f(PROPERTY1, "ab", false, false);

        // equal but not same instance
        assertEquals(f1, f1b);
        assertEquals(f2, f2b);
        assertEquals(f3, f3b);
        assertEquals(f4, f4b);

        // more than one property differ
        assertFalse(f1.equals(f2));
        assertFalse(f1.equals(f3));
        assertFalse(f1.equals(f4));
        assertFalse(f2.equals(f1));
        assertFalse(f2.equals(f3));
        assertFalse(f2.equals(f4));
        assertFalse(f3.equals(f1));
        assertFalse(f3.equals(f2));
        assertFalse(f3.equals(f4));
        assertFalse(f4.equals(f1));
        assertFalse(f4.equals(f2));
        assertFalse(f4.equals(f3));

        // only one property differs
        assertFalse(filter.equals(f1));
        assertFalse(filter.equals(f2));
        assertFalse(filter.equals(f3));
        assertFalse(filter.equals(f4));

        assertFalse(f1.equals(null));
        assertFalse(f1.equals(new Object()));

        assertEquals(f1.hashCode(), f1b.hashCode());
        assertEquals(f2.hashCode(), f2b.hashCode());
        assertEquals(f3.hashCode(), f3b.hashCode());
        assertEquals(f4.hashCode(), f4b.hashCode());
    }

    @Test
    public void testNonExistentProperty() {
        assertFalse(passes("other1", "ab", false, true));
    }

    @Test
    public void testNullValueForProperty() {
        TestItem<String, String> item = createTestItem();
        item.addItemProperty("other1", new NullProperty());

        assertFalse(f("other1", "ab", false, true).passesFilter(null, item));
    }

}

package com.vaadin.data.util.filter;

import org.junit.Assert;

public class SimpleStringFilterTest extends
        AbstractFilterTestBase<SimpleStringFilter> {

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

    public void testStartsWithCaseSensitive() {
        Assert.assertTrue(passes(PROPERTY1, "ab", false, true));
        Assert.assertTrue(passes(PROPERTY1, "", false, true));

        Assert.assertFalse(passes(PROPERTY2, "ab", false, true));
        Assert.assertFalse(passes(PROPERTY1, "AB", false, true));
    }

    public void testStartsWithCaseInsensitive() {
        Assert.assertTrue(passes(PROPERTY1, "AB", true, true));
        Assert.assertTrue(passes(PROPERTY2, "te", true, true));
        Assert.assertFalse(passes(PROPERTY2, "AB", true, true));
    }

    public void testContainsCaseSensitive() {
        Assert.assertTrue(passes(PROPERTY1, "ab", false, false));
        Assert.assertTrue(passes(PROPERTY1, "abcde", false, false));
        Assert.assertTrue(passes(PROPERTY1, "cd", false, false));
        Assert.assertTrue(passes(PROPERTY1, "e", false, false));
        Assert.assertTrue(passes(PROPERTY1, "", false, false));

        Assert.assertFalse(passes(PROPERTY2, "ab", false, false));
        Assert.assertFalse(passes(PROPERTY1, "es", false, false));
    }

    public void testContainsCaseInsensitive() {
        Assert.assertTrue(passes(PROPERTY1, "AB", true, false));
        Assert.assertTrue(passes(PROPERTY1, "aBcDe", true, false));
        Assert.assertTrue(passes(PROPERTY1, "CD", true, false));
        Assert.assertTrue(passes(PROPERTY1, "", true, false));

        Assert.assertTrue(passes(PROPERTY2, "es", true, false));

        Assert.assertFalse(passes(PROPERTY2, "ab", true, false));
    }

    public void testAppliesToProperty() {
        SimpleStringFilter filter = f(PROPERTY1, "ab", false, true);
        Assert.assertTrue(filter.appliesToProperty(PROPERTY1));
        Assert.assertFalse(filter.appliesToProperty(PROPERTY2));
        Assert.assertFalse(filter.appliesToProperty("other"));
    }

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
        Assert.assertEquals(f1, f1b);
        Assert.assertEquals(f2, f2b);
        Assert.assertEquals(f3, f3b);
        Assert.assertEquals(f4, f4b);

        // more than one property differ
        Assert.assertFalse(f1.equals(f2));
        Assert.assertFalse(f1.equals(f3));
        Assert.assertFalse(f1.equals(f4));
        Assert.assertFalse(f2.equals(f1));
        Assert.assertFalse(f2.equals(f3));
        Assert.assertFalse(f2.equals(f4));
        Assert.assertFalse(f3.equals(f1));
        Assert.assertFalse(f3.equals(f2));
        Assert.assertFalse(f3.equals(f4));
        Assert.assertFalse(f4.equals(f1));
        Assert.assertFalse(f4.equals(f2));
        Assert.assertFalse(f4.equals(f3));

        // only one property differs
        Assert.assertFalse(filter.equals(f1));
        Assert.assertFalse(filter.equals(f2));
        Assert.assertFalse(filter.equals(f3));
        Assert.assertFalse(filter.equals(f4));

        Assert.assertFalse(f1.equals(null));
        Assert.assertFalse(f1.equals(new Object()));

        Assert.assertEquals(f1.hashCode(), f1b.hashCode());
        Assert.assertEquals(f2.hashCode(), f2b.hashCode());
        Assert.assertEquals(f3.hashCode(), f3b.hashCode());
        Assert.assertEquals(f4.hashCode(), f4b.hashCode());
    }

    public void testNonExistentProperty() {
        Assert.assertFalse(passes("other1", "ab", false, true));
    }

    public void testNullValueForProperty() {
        TestItem<String, String> item = createTestItem();
        item.addItemProperty("other1", new NullProperty());

        Assert.assertFalse(f("other1", "ab", false, true).passesFilter(null,
                item));
    }

}

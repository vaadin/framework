package com.vaadin.data.validator;

import org.junit.Test;

public class RegexpValidatorTest extends ValidatorTestBase {

    @Test
    public void testNullStringFails() {
        assertPasses(null, new RegexpValidator("Should be 'abc'", "abc"));
    }

    @Test
    public void testEmptyPatternMatchesEmptyString() {
        assertPasses("", new RegexpValidator("Should be empty", "", true));
    }

    @Test
    public void testEmptyPatternDoesNotMatchNonEmptyString() {
        assertFails("x", new RegexpValidator("Should be empty", "", true));
    }

    @Test
    public void testPatternMatchesString() {
        RegexpValidator v = new RegexpValidator(
                "Should be foo and bar repeating", "(foo|bar)+", true);

        assertPasses("foo", v);
        assertPasses("barfoo", v);
        assertPasses("foobarbarbarfoobarfoofoobarbarfoofoofoobar", v);
    }

    @Test
    public void testPatternDoesNotMatchString() {
        RegexpValidator v = new RegexpValidator(
                "Should be foo and bar repeating", "(foo|bar)+", true);

        assertFails("", v);
        assertFails("barf", v);
        assertFails(" bar", v);
        assertFails("foobarbarbarfoobar.foofoobarbarfoofoofoobar", v);
    }

    @Test
    public void testEmptyPatternFoundInAnyString() {
        RegexpValidator v = new RegexpValidator("Should always pass", "",
                false);

        assertPasses("", v);
        assertPasses("      ", v);
        assertPasses("qwertyuiopasdfghjklzxcvbnm", v);
    }

    @Test
    public void testPatternFoundInString() {
        RegexpValidator v = new RegexpValidator("Should contain a number",
                "\\d+", false);

        assertPasses("0", v);
        assertPasses("     123     ", v);
        assertPasses("qwerty9iop", v);
    }

    @Test
    public void testPatternNotFoundInString() {
        RegexpValidator v = new RegexpValidator("Should contain a number",
                "\\d+", false);

        assertFails("", v);
        assertFails("qwertyiop", v);
    }
}

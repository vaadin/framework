package com.vaadin.tokka.data.validators;

import org.junit.Test;

public class EmailValidatorTest extends ValidatorTestBase {

    @Test(expected = NullPointerException.class)
    public void testNullStringFails() {
        validator("null should throw").apply(null);
    }

    @Test
    public void testEmptyStringFails() {
        assertFails("", validator("empty string not allowed"));
    }

    @Test
    public void testStringWithoutAtSignFails() {
        assertFails("johannesd.vaadin", validator("@ is required"));
    }

    @Test
    public void testMissingLocalPartFails() {
        RegexpValidator v = validator("local part is required");
        assertFails("@localhost", v);
        assertFails(" @localhost", v);
    }

    @Test
    public void testNonAsciiEmailFails() {
        RegexpValidator v = validator("accented letters not allowed");
        assertFails("jöhännes@vaadin.com", v);
        assertFails("johannes@váádìn.com", v);
        assertFails("johannes@vaadin.cõm", v);
    }

    @Test
    public void testLocalPartWithPunctuationPasses() {
        RegexpValidator v = shouldNotFail();
        assertPasses("johannesd+test@vaadin.com", v);
        assertPasses("johannes.dahlstrom@vaadin.com", v);
        assertPasses("johannes_d@vaadin.com", v);
    }

    @Test
    public void testEmailWithoutDomainPartFails() {
        assertFails("johannesd@", validator("domain part is required"));
    }

    @Test
    public void testComplexDomainPasses() {
        assertPasses("johannesd@foo.bar.baz.vaadin.com", shouldNotFail());
    }

    @Test
    public void testDomainWithPunctuationPasses() {
        assertPasses("johannesd@vaadin-dev.com", shouldNotFail());
    }

    @Test
    public void testMissingTldFails() {
        assertFails("johannesd@localhost", validator("tld is required"));
    }

    @Test
    public void testOneLetterTldFails() {
        assertFails("johannesd@vaadin.f",
                validator("one-letter tld not allowed"));
    }

    @Test
    public void testLongTldPasses() {
        assertPasses("joonas@vaadin.management", shouldNotFail());
    }

    @Test
    public void testIdnTldPasses() {
        assertPasses("leif@vaadin.XN--VERMGENSBERATER-CTB", shouldNotFail());
    }

    @Test
    public void testYelledEmailPasses() {
        assertPasses("JOHANNESD@VAADIN.COM", shouldNotFail());
    }

    @Test
    public void testEmailWithDigitsPasses() {
        assertPasses("johannes84@v44d1n.com", shouldNotFail());
    }

    private EmailValidator validator(String errorMessage) {
        return new EmailValidator(errorMessage);
    }

    private EmailValidator shouldNotFail() {
        return validator("this should not fail");
    }
}

package com.vaadin.data.validator;

import java.util.Calendar;
import java.util.Locale;

import org.junit.Test;

import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.BeanToValidate;

public class BeanValidatorTest extends ValidatorTestBase {

    @Test
    public void testFirstNameNullFails() {
        assertFails(null, "may not be null", validator("firstname"));
    }

    @Test
    public void testFirstNameTooShortFails() {
        assertFails("x", "size must be between 3 and 16",
                validator("firstname"));
    }

    @Test
    public void testFirstNameLongEnoughPasses() {
        assertPasses("Magi", validator("firstname"));
    }

    @Test
    public void testAgeTooYoungFails() {
        assertFails(14, "Must be 18 or above", validator("age"));
    }

    @Test
    public void testDateOfBirthNullPasses() {
        assertPasses(null, validator("dateOfBirth"));
    }

    @Test
    public void testDateOfBirthInTheFutureFails() {
        Calendar year3k = Calendar.getInstance();
        year3k.set(3000, 0, 1);
        assertFails(year3k, "must be in the past", validator("dateOfBirth"));
    }

    @Test
    public void testAddressesEmptyArrayPasses() {
        Address[] noAddresses = {};
        assertPasses(noAddresses, validator("addresses"));
    }

    @Test
    public void testAddressesNullFails() {
        assertFails(null, "may not be null", validator("addresses"));
    }

    @Test
    public void testInvalidDecimalsFailsInFrench() {
        BeanValidator v = validator("decimals", Locale.FRENCH);
        assertFails("1234.567", "Valeur numérique hors limite "
                + "(<3 chiffres>.<2 chiffres> attendus)", v);
    }

    @Test
    public void testAddressNestedPropertyInvalidPostalCodeFails() {
        assertFails(100_000, "must be less than or equal to 99999",
                validator("address.postalCode"));
    }

    @Test
    public void testNullValuePasses() {
        assertPasses(null, validator("nickname"));
    }

    private BeanValidator validator(String propertyName) {
        return new BeanValidator(BeanToValidate.class, propertyName);
    }

    private BeanValidator validator(String propertyName, Locale locale) {
        return new BeanValidator(BeanToValidate.class, propertyName, locale);
    }
}

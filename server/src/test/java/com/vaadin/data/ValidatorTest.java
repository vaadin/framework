package com.vaadin.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Locale;
import java.util.Objects;

import org.junit.Test;

import com.vaadin.data.validator.ValidatorTestBase;

/**
 * @author Vaadin Ltd
 *
 */
public class ValidatorTest extends ValidatorTestBase {

    @Test
    public void alwaysPass() {
        Validator<String> alwaysPass = Validator.alwaysPass();
        ValidationResult result = alwaysPass.apply("foo", new ValueContext());
        assertFalse(result.isError());
    }

    @Test
    public void from() {
        Validator<String> validator = Validator.from(Objects::nonNull,
                "Cannot be null");
        ValidationResult result = validator.apply(null, new ValueContext());
        assertTrue(result.isError());

        result = validator.apply("", new ValueContext());
        assertFalse(result.isError());
    }

    @Test
    public void withValidator_customErrorMessageProvider() {
        String finnishError = "Käyttäjän tulee olla täysi-ikäinen";
        String englishError = "The user must be an adult";
        String notTranslatableError = "NOT TRANSLATABLE";

        Validator<Integer> ageValidator = Validator.from(age -> age >= 18,
                ctx -> {
                    Locale locale = ctx.getLocale().orElse(Locale.ENGLISH);

                    if (locale.getLanguage().equals("fi")) {
                        return finnishError;
                    }
                    if (locale.getLanguage().equals("en")) {
                        return englishError;
                    }
                    return notTranslatableError;
                });

        setLocale(Locale.ENGLISH);
        assertFails(17, englishError, ageValidator);
        setLocale(new Locale("fi", "FI"));
        assertFails(17, finnishError, ageValidator);
        setLocale(Locale.GERMAN);
        assertFails(17, notTranslatableError, ageValidator);
    }
}

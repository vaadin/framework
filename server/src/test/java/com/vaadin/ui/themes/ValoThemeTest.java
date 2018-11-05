package com.vaadin.ui.themes;

public class ValoThemeTest {
    /*
     * No runtime behavior to test. Just verifying that it's possible to create
     * a custom subclass for inheriting the constants from the built-in theme.
     */
    public static class CustomValoTheme extends ValoTheme {
        public static String MY_THEME_CONSTANT = "my theme constant";
    }
}

package com.vaadin.tests.server;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import junit.framework.TestCase;

import com.vaadin.tests.VaadinClasses;
import com.vaadin.ui.themes.BaseTheme;

public class TestThemeNames extends TestCase {
    public void testThemeNames() {
        File baseDir = new File(SourceFileChecker.getBaseDir()
                + "WebContent/VAADIN/themes/");

        List<Class<? extends BaseTheme>> themeClasses = VaadinClasses
                .getThemeClasses();
        for (Class<? extends BaseTheme> themeClass : themeClasses) {
            try {
                Field field = themeClass.getField("THEME_NAME");
                String themeName = (String) field.get(null);

                File themeDir = new File(baseDir, themeName);
                File styleFile = new File(themeDir, "styles.css");

                assertTrue("Can't find " + styleFile + " for theme "
                        + themeClass.getName(), styleFile.exists());

                // Test that casing matches
                assertEquals(themeDir.getCanonicalFile().getName(), themeName);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}

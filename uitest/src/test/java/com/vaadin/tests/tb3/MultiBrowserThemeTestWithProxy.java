package com.vaadin.tests.tb3;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

@RunWith(ParameterizedTB3Runner.class)
public abstract class MultiBrowserThemeTestWithProxy
        extends MultiBrowserTestWithProxy {

    private String theme;

    public void setTheme(String theme) {
        this.theme = theme;
    }

    @Parameters
    public static Collection<String> getThemes() {
        return Arrays.asList(new String[] { "valo", "reindeer", "runo",
                "chameleon", "base" });
    }

    @Override
    protected boolean requireWindowFocusForIE() {
        return true;
    }

    @Override
    protected void openTestURL(Class<?> uiClass, String... parameters) {
        Set<String> params = new HashSet<>(Arrays.asList(parameters));
        params.add("theme=" + theme);
        super.openTestURL(uiClass, params.toArray(new String[params.size()]));
    }
}

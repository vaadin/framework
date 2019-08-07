package com.vaadin.tests.components.uitest;

public class BackgroundColorThemeTestUI extends ThemeTestUI {
    @Override
    protected boolean showAdditionalControlFields() {
        return true;
    }

    @Override
    protected Integer getTicketNumber() {
        return 11671;
    }

    @Override
    protected String getTestDescription() {
        return super.getTestDescription()
                + "<br>Read-only styles shouldn't override the transparent background of borderless components.";
    }
}

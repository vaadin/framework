package com.vaadin.tests.components.upload;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

import com.vaadin.ui.themes.*;
import org.junit.Test;

public class UploadImmediateButtonWidthChameleonTest
        extends UploadImmediateButtonWidthTest {

    @Override
    protected String getTheme() {
        return ChameleonTheme.THEME_NAME;
    }

    @Test
    public void immediateButtonWithUndefinedWidth() {
        assertThat(getButtonWidth("upload3"), closeTo(69, 4));
    }
}

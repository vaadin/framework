package com.vaadin.tests.components.upload;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

import org.junit.Test;

import com.vaadin.ui.themes.ValoTheme;

public class UploadImmediateButtonWidthValoTest
        extends UploadImmediateButtonWidthTest {

    @Override
    protected String getTheme() {
        return ValoTheme.THEME_NAME;
    }

    @Test
    public void immediateButtonWithUndefinedWidth() {
        assertThat(getButtonWidth("upload3"), closeTo(89, 2));
    }
}

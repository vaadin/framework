package com.vaadin.tests.components.upload;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

import org.junit.Test;

import com.vaadin.v7.ui.themes.Runo;

public class UploadImmediateButtonWidthRunoTest
        extends UploadImmediateButtonWidthTest {

    @Override
    protected String getTheme() {
        return Runo.THEME_NAME;
    }

    @Test
    public void immediateButtonWithUndefinedWidth() {
        assertThat(getButtonWidth("upload3"), closeTo(72, 6));
    }
}

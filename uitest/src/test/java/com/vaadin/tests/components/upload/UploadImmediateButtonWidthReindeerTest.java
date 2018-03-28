package com.vaadin.tests.components.upload;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

import org.junit.Test;

import com.vaadin.v7.ui.themes.Reindeer;

public class UploadImmediateButtonWidthReindeerTest
        extends UploadImmediateButtonWidthTest {

    @Override
    protected String getTheme() {
        return Reindeer.THEME_NAME;
    }

    @Test
    public void immediateButtonWithUndefinedWidth() {
        assertThat(getButtonWidth("upload3"), closeTo(67, 8));
    }
}

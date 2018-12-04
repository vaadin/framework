package com.vaadin.tests.components.progressindicator;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.ProgressBar;
import com.vaadin.v7.ui.themes.Runo;

@Theme(Runo.THEME_NAME)
public class ProgressBarStaticRuno extends AbstractReindeerTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        ProgressBar progressBar = new ProgressBar();
        progressBar.addStyleName(Runo.PROGRESSBAR_STATIC);
        addComponent(progressBar);
    }
}

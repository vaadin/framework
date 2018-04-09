package com.vaadin.v7.tests.server.component.progressbar;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.v7.ui.ProgressBar;

/**
 * Test cases for reading the properties of selection components.
 *
 * @author Vaadin Ltd
 */
public class ProgressBarDeclarativeTest
        extends DeclarativeTestBase<ProgressBar> {

    public String getBasicDesign() {
        return "<vaadin7-progress-bar value=0.5 indeterminate>";
    }

    public ProgressBar getBasicExpected() {
        ProgressBar ns = new ProgressBar();
        ns.setIndeterminate(true);
        ns.setValue(0.5f);
        return ns;
    }

    @Test
    public void testReadBasic() {
        testRead(getBasicDesign(), getBasicExpected());
    }

    @Test
    public void testWriteBasic() {
        testWrite(stripOptionTags(getBasicDesign()), getBasicExpected());
    }

    @Test
    public void testReadEmpty() {
        testRead("<vaadin7-progress-bar>", new ProgressBar());
    }

    @Test
    public void testWriteEmpty() {
        testWrite("<vaadin7-progress-bar>", new ProgressBar());
    }

    @Test
    public void testReadOnlyValue() {
        String design = "<vaadin7-progress-bar readonly value=0.5 indeterminate>";
        ProgressBar progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setValue(0.5f);
        progressBar.setReadOnly(true);

        testRead(design, progressBar);
        testWrite(design, progressBar);
    }

}

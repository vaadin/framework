/*
 * Copyright 2000-2017 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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

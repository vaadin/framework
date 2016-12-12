/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.server.component.upload;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Upload;

/**
 * Tests the declarative support for implementations of {@link Upload}.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class UploadDeclarativeTest extends DeclarativeTestBase<Upload> {

    @Test
    public void testReadBasic() {
        testRead(getBasicDesign(), getBasicExpected());
    }

    @Test
    public void testWriteBasic() {
        testWrite(getBasicDesign(), getBasicExpected());
    }

    private String getBasicDesign() {
        return "<vaadin-upload button-caption='Send the file' tabindex=5 />";
    }

    private Upload getBasicExpected() {
        Upload u = new Upload();
        u.setButtonCaption("Send the file");
        u.setTabIndex(5);
        return u;
    }

    @Test
    public void testReadEmpty() {
        testRead("<vaadin-upload />", new Upload());
    }

    @Test
    public void testWriteEmpty() {
        testWrite("<vaadin-upload />", new Upload());
    }

    @Test
    public void testImmediateModeDefault() {
        Assert.assertTrue(
                testRead("<v-upload />", new Upload()).isImmediateMode());

        Upload upload = new Upload();
        upload.setImmediateMode(false);
        Assert.assertFalse(testRead("<v-upload immediate-mode=false />", upload)
                .isImmediateMode());
    }
}

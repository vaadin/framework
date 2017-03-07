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
package com.vaadin.tests.components.combobox;

import java.util.Date;

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.ComboBox;

public class ComboBoxLargeIcons extends TestBase {

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    @Override
    protected String getDescription() {
        return "<p>All items in the Combobox has large icons. The size of the dropdown should fit the contents, also when changing pages. The height of the dropdown shouldn't exceed the browser's viewport, but fewer items should be visible then.</p><p>The size of the shadow behind the dropdown must also be correctly sized.</p><p>Note that the image URL change for every restart to keep the browser from using cached images.</p>";
    }

    @Override
    protected void setup() {
        ComboBox<String> cb = new ComboBox<String>();
        cb.setItems("folder-add", "folder-delete", "arrow-down", "arrow-left",
                "arrow-right", "arrow-up", "document-add", "document-delete",
                "document-doc", "document-edit", "document-image",
                "document-pdf", "document-ppt", "document-txt", "document-web",
                "document");
        getLayout().addComponent(cb);
        // FIXME cb.setNullSelectionAllowed(false);
        cb.setItemIconGenerator(icon -> new ThemeResource(
                "../runo/icons/32/" + icon + ".png?" + new Date().getTime()));
    }
}

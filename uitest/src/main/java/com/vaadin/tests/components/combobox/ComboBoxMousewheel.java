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

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.util.ItemDataProvider;
import com.vaadin.ui.ComboBox;

/**
 * Tests mousewheel handling in ComboBox.
 *
 * @author Vaadin Ltd
 */
public class ComboBoxMousewheel extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(createComboBox("Paged"));

        ComboBox<String> cb = createComboBox("Unpaged");
        cb.setPageLength(0);
        addComponent(cb);
    }

    private ComboBox<String> createComboBox(String caption) {
        ComboBox<String> cb = new ComboBox<>(caption);
        cb.setDataProvider(new ItemDataProvider(100));
        cb.setId(caption);
        return cb;
    }

    @Override
    protected String getTestDescription() {
        return "ComboBox scrolling should be possible to both directions on Paged + IE as well.<br>"
                + "IE should not move paging up when scrolled down.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 16918;
    }

}

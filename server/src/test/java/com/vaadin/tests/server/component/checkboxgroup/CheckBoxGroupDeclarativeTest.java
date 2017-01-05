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
package com.vaadin.tests.server.component.checkboxgroup;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.vaadin.tests.server.component.abstractmultiselect.AbstractMultiSelectDeclarativeTest;
import com.vaadin.ui.CheckBoxGroup;

/**
 * Declarative support test for CheckBoxGroup.
 * <p>
 * Only {@link CheckBoxGroup#setHtmlContentAllowed(boolean)} is tested here
 * explicitly. All other tests are in the super class (
 * {@link AbstractMultiSelectDeclarativeTest}).
 *
 * @see AbstractMultiSelectDeclarativeTest
 *
 * @author Vaadin Ltd
 *
 */
@SuppressWarnings("rawtypes")
public class CheckBoxGroupDeclarativeTest
        extends AbstractMultiSelectDeclarativeTest<CheckBoxGroup> {

    private static final String SIMPLE_HTML = "<span>foo</span>";

    private static final String HTML = "<div class='wrapper'><div>bar</div></div>";

    private static final String HTML_ENTITIES = "<b>a & b</b>";

    @Test
    public void serializeDataWithHtmlContentAllowed() {
        CheckBoxGroup<String> group = new CheckBoxGroup<>();

        List<String> items = Arrays.asList("foo", "bar", "foobar");

        String design = String.format(
                "<%s html-content-allowed>\n"
                        + "<option item='foo'>%s</option>\n"
                        + "<option item='bar'>%s</option>"
                        + "<option item='foobar'>%s</option>",
                getComponentTag(), SIMPLE_HTML, HTML,
                HTML_ENTITIES.replace("&", "&amp;"), getComponentTag());

        group.setItems(items);
        group.setHtmlContentAllowed(true);
        group.setItemCaptionGenerator(item -> generateCaption(item, items));

        testRead(design, group, true);
        testWrite(design, group, true);
    }

    @Override
    protected String getComponentTag() {
        return "vaadin-check-box-group";
    }

    @Override
    protected Class<CheckBoxGroup> getComponentClass() {
        return CheckBoxGroup.class;
    }

    private String generateCaption(String item, List<String> items) {
        int index = items.indexOf(item);
        switch (index) {
        case 0:
            return SIMPLE_HTML;
        case 1:
            return HTML;
        case 2:
            return HTML_ENTITIES;
        }
        return null;
    }

}
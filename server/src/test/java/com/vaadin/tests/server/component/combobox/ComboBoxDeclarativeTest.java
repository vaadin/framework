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
package com.vaadin.tests.server.component.combobox;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.vaadin.tests.server.component.abstractsingleselect.AbstractSingleSelectDeclarativeTest;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.StyleGenerator;

/**
 * Declarative support test for ComboBox.
 * <p>
 * There are only ComboBox specific properties explicit tests. All other tests
 * are in the super class ( {@link AbstractSingleSelectDeclarativeTest}).
 *
 * @see AbstractSingleSelectDeclarativeTest
 *
 * @author Vaadin Ltd
 *
 */
@SuppressWarnings("rawtypes")
public class ComboBoxDeclarativeTest
        extends AbstractSingleSelectDeclarativeTest<ComboBox> {

    @Test
    public void comboBoxSpecificPropertiesSerialize() {
        String placeholder = "testPlaceholder";
        boolean textInputAllowed = false;
        int pageLength = 7;
        String popupWidth = "11%";
        boolean emptySelectionAllowed = false;
        String emptySelectionCaption = "foo";

        String design = String.format(
                "<%s placeholder='%s' text-input-allowed='%s' page-length='%d' "
                        + "popup-width='%s' empty-selection-allowed='%s' "
                        + "scroll-to-selected-item empty-selection-caption='%s'/>",
                getComponentTag(), placeholder, textInputAllowed, pageLength,
                popupWidth, emptySelectionAllowed, emptySelectionCaption);

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPlaceholder(placeholder);
        comboBox.setTextInputAllowed(textInputAllowed);
        comboBox.setPageLength(pageLength);
        comboBox.setPopupWidth(popupWidth);
        comboBox.setScrollToSelectedItem(true);
        comboBox.setEmptySelectionAllowed(emptySelectionAllowed);
        comboBox.setEmptySelectionCaption(emptySelectionCaption);

        testRead(design, comboBox);
        testWrite(design, comboBox);
    }

    @Test
    public void extendedComboBox() {
        ExtendedComboBox combo = new ExtendedComboBox();
        String design = "<html>" //
                + "<head>" //
                + "<meta name='package-mapping' content='com_vaadin_tests_server_component_combobox:com.vaadin.tests.server.component.combobox'>"
                + "</meta>" + "</head>" + "<body>"
                + "<com_vaadin_tests_server_component_combobox-extended-combo-box>"
                + "</com_vaadin_tests_server_component_combobox-extended-combo-box>"
                + "</body></html>";
        testWrite(design, combo);
        testRead(design, combo);
    }

    @Test
    public void optionStylesSerialization() throws InstantiationException,
            IllegalAccessException, InvocationTargetException {
        List<String> items = Arrays.asList("foo", "bar", "foobar");

        String design = String.format(
                "<%s>\n" + "<option item='foo' style='foo-style'>foo</option>\n"
                        + "<option item='bar' style='bar-style'>bar</option>"
                        + "<option item='foobar' style='foobar-style'>foobar</option></%s>",
                getComponentTag(), getComponentTag());
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems(items);
        comboBox.setStyleGenerator(item -> item + "-style");

        testRead(design, comboBox);
        testWrite(design, comboBox, true);
    }

    @Override
    protected String getComponentTag() {
        return "vaadin-combo-box";
    }

    @Override
    protected Class<? extends ComboBox> getComponentClass() {
        return ComboBox.class;
    }

    @Override
    protected boolean acceptProperty(Class<?> clazz, Method readMethod,
            Method writeMethod) {
        if (readMethod != null) {
            Class<?> returnType = readMethod.getReturnType();
            if (StyleGenerator.class.equals(returnType)) {
                return false;
            }
        }
        return super.acceptProperty(clazz, readMethod, writeMethod);
    }

}

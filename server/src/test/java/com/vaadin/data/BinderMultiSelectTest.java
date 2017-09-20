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
package com.vaadin.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.tests.data.bean.BeanWithEnums;
import com.vaadin.tests.data.bean.TestEnum;
import com.vaadin.ui.CheckBoxGroup;

public class BinderMultiSelectTest
        extends BinderTestBase<Binder<BeanWithEnums>, BeanWithEnums> {
    public static class TestEnumSetToStringConverter
            implements Converter<Set<TestEnum>, String> {
        @Override
        public Result<String> convertToModel(Set<TestEnum> value,
                ValueContext context) {
            return Result.ok(value.stream().map(TestEnum::name)
                    .collect(Collectors.joining(",")));
        }

        @Override
        public Set<TestEnum> convertToPresentation(String value,
                ValueContext context) {
            return Stream.of(value.split(","))
                    .filter(string -> !string.isEmpty()).map(TestEnum::valueOf)
                    .collect(Collectors.toSet());
        }
    }

    private final Binder<AtomicReference<String>> converterBinder = new Binder<>();

    private CheckBoxGroup<TestEnum> select;

    @Before
    public void setUp() {
        binder = new Binder<>();
        item = new BeanWithEnums();
        select = new CheckBoxGroup<>();
        select.setItems(TestEnum.values());

        converterBinder.forField(select)
                .withConverter(new TestEnumSetToStringConverter())
                .bind(AtomicReference::get, AtomicReference::set);
    }

    @Test
    public void beanBound_bindSelectByShortcut_selectionUpdated() {
        item.setEnums(Collections.singleton(TestEnum.ONE));
        binder.setBean(item);
        binder.bind(select, BeanWithEnums::getEnums, BeanWithEnums::setEnums);

        assertEquals(Collections.singleton(TestEnum.ONE),
                select.getSelectedItems());
    }

    @Test
    public void beanBound_bindSelect_selectionUpdated() {
        item.setEnums(Collections.singleton(TestEnum.TWO));
        binder.setBean(item);
        binder.forField(select).bind(BeanWithEnums::getEnums,
                BeanWithEnums::setEnums);

        assertEquals(Collections.singleton(TestEnum.TWO),
                select.getSelectedItems());
    }

    @Test
    public void selectBound_bindBeanWithoutEnums_selectedItemNotPresent() {
        bindEnum();

        assertTrue(select.getSelectedItems().isEmpty());
    }

    @Test
    public void selectBound_bindBean_selectionUpdated() {
        item.setEnums(Collections.singleton(TestEnum.ONE));
        bindEnum();

        assertEquals(Collections.singleton(TestEnum.ONE),
                select.getSelectedItems());
    }

    @Test
    public void bound_setSelection_beanValueUpdated() {
        bindEnum();

        select.select(TestEnum.TWO);

        assertEquals(Collections.singleton(TestEnum.TWO), item.getEnums());
    }

    @Test
    public void bound_setSelection_beanValueIsACopy() {
        bindEnum();

        select.select(TestEnum.TWO);

        Set<TestEnum> enums = item.getEnums();

        binder.setBean(new BeanWithEnums());
        select.select(TestEnum.ONE);

        assertEquals(Collections.singleton(TestEnum.TWO), enums);
    }

    @Test
    public void bound_deselect_beanValueUpdatedToNull() {
        item.setEnums(Collections.singleton(TestEnum.ONE));
        bindEnum();

        select.deselect(TestEnum.ONE);

        assertTrue(item.getEnums().isEmpty());
    }

    @Test
    public void unbound_changeSelection_beanValueNotUpdated() {
        item.setEnums(Collections.singleton(TestEnum.ONE));
        bindEnum();
        binder.removeBean();

        select.select(TestEnum.TWO);

        assertEquals(Collections.singleton(TestEnum.ONE), item.getEnums());
    }

    @Test
    public void withConverter_load_selectUpdated() {
        converterBinder.readBean(new AtomicReference<>("TWO"));

        assertEquals(Collections.singleton(TestEnum.TWO),
                select.getSelectedItems());
    }

    @Test
    public void withConverter_save_referenceUpdated() {
        select.select(TestEnum.ONE);
        select.select(TestEnum.TWO);

        AtomicReference<String> reference = new AtomicReference<>("");
        converterBinder.writeBeanIfValid(reference);

        assertEquals("ONE,TWO", reference.get());
    }

    @Test
    public void withValidator_validate_validatorUsed() {
        binder.forField(select)
                .withValidator(selection -> selection.size() % 2 == 1,
                        "Must select odd number of items")
                .bind(BeanWithEnums::getEnums, BeanWithEnums::setEnums);
        binder.setBean(item);

        assertFalse(binder.validate().isOk());

        select.select(TestEnum.TWO);

        assertTrue(binder.validate().isOk());
    }

    protected void bindEnum() {
        binder.forField(select).bind(BeanWithEnums::getEnums,
                BeanWithEnums::setEnums);
        binder.setBean(item);
    }
}

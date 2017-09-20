package com.vaadin.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.tests.data.bean.BeanWithEnums;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.tests.data.bean.TestEnum;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.MultiSelect;
import com.vaadin.ui.components.grid.MultiSelectionModelImpl;

public class GridAsMultiSelectInBinderTest
        extends BinderTestBase<Binder<BeanWithEnums>, BeanWithEnums> {
    public class TestEnumSetToStringConverter
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

    private class CustomMultiSelectModel extends MultiSelectionModelImpl<Sex> {

        @Override
        public void updateSelection(Set<Sex> addedItems, Set<Sex> removedItems,
                boolean userOriginated) {
            super.updateSelection(addedItems, removedItems, userOriginated);
        }

    }

    private Binder<AtomicReference<String>> converterBinder = new Binder<>();
    private Grid<TestEnum> grid;
    private MultiSelect<TestEnum> select;

    @Before
    public void setUp() {
        binder = new Binder<>();
        item = new BeanWithEnums();
        grid = new Grid<>();
        grid.setItems(TestEnum.values());
        grid.setSelectionMode(SelectionMode.MULTI);
        select = grid.asMultiSelect();

        converterBinder.forField(select)
                .withConverter(new TestEnumSetToStringConverter())
                .bind(AtomicReference::get, AtomicReference::set);
    }

    @Test(expected = IllegalStateException.class)
    public void boundGridInBinder_selectionModelChanged_throws() {
        grid.setSelectionMode(SelectionMode.SINGLE);

        select.select(TestEnum.ONE);
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

    @Test
    public void addValueChangeListener_selectionUpdated_eventTriggeredForMultiSelect() {
        CustomMultiSelectModel model = new CustomMultiSelectModel();
        Grid<Sex> grid = new Grid<Sex>() {
            {
                setSelectionModel(model);
            }
        };
        grid.setItems(Sex.values());
        MultiSelect<Sex> select = grid.asMultiSelect();

        List<Sex> selected = new ArrayList<>();
        List<Boolean> userOriginated = new ArrayList<>();
        select.addValueChangeListener(event -> {
            selected.addAll(event.getValue());
            userOriginated.add(event.isUserOriginated());
            assertSame(grid, event.getComponent());
            // cannot compare that the event source is the select since a new
            // MultiSelect wrapper object has been created for the event

            assertEquals(select.getValue(), event.getValue());
        });

        select.select(Sex.UNKNOWN);

        assertEquals(Arrays.asList(Sex.UNKNOWN), selected);

        model.updateSelection(new LinkedHashSet<>(Arrays.asList(Sex.MALE)),
                Collections.emptySet(), true); // simulate client side selection
        assertEquals(Arrays.asList(Sex.UNKNOWN, Sex.UNKNOWN, Sex.MALE),
                selected);
        selected.clear();

        select.select(Sex.MALE); // NOOP
        assertEquals(Arrays.asList(), selected);
        selected.clear();

        // client side deselect
        model.updateSelection(Collections.emptySet(),
                new LinkedHashSet<>(Arrays.asList(Sex.UNKNOWN)), true);

        assertEquals(Arrays.asList(Sex.MALE), selected);
        selected.clear();

        select.deselect(Sex.UNKNOWN); // NOOP
        assertEquals(Arrays.asList(), selected);
        selected.clear();

        select.deselect(Sex.FEMALE, Sex.MALE); // partly NOOP
        assertEquals(Arrays.asList(), selected);

        model.selectItems(Sex.FEMALE, Sex.MALE);
        assertEquals(Arrays.asList(Sex.FEMALE, Sex.MALE), selected);
        selected.clear();

        model.updateSelection(new LinkedHashSet<>(Arrays.asList(Sex.FEMALE)),
                Collections.emptySet(), true); // client side NOOP
        assertEquals(Arrays.asList(), selected);

        assertEquals(Arrays.asList(false, true, true, false, false),
                userOriginated);
    }

    protected void bindEnum() {
        binder.forField(select).bind(BeanWithEnums::getEnums,
                BeanWithEnums::setEnums);
        binder.setBean(item);
    }
}

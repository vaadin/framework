package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.SelectionModel;
import com.vaadin.server.Extension;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.components.grid.GridMultiSelect;
import com.vaadin.ui.components.grid.GridSingleSelect;
import com.vaadin.ui.components.grid.MultiSelectionModel;
import com.vaadin.ui.components.grid.MultiSelectionModelImpl;
import com.vaadin.ui.components.grid.NoSelectionModel;
import com.vaadin.ui.components.grid.SingleSelectionModel;
import com.vaadin.ui.components.grid.SingleSelectionModelImpl;

public class GridSelectionModeTest {

    private Grid<String> grid;

    @Before
    public void setup() {
        grid = new Grid<>();
        grid.setItems("foo", "bar", "baz");
    }

    @Test
    public void testSelectionModes() {
        assertEquals(SingleSelectionModelImpl.class,
                grid.getSelectionModel().getClass());

        assertEquals(MultiSelectionModelImpl.class,
                grid.setSelectionMode(SelectionMode.MULTI).getClass());
        assertEquals(MultiSelectionModelImpl.class,
                grid.getSelectionModel().getClass());

        assertEquals(NoSelectionModel.class,
                grid.setSelectionMode(SelectionMode.NONE).getClass());
        assertEquals(NoSelectionModel.class,
                grid.getSelectionModel().getClass());

        assertEquals(SingleSelectionModelImpl.class,
                grid.setSelectionMode(SelectionMode.SINGLE).getClass());
        assertEquals(SingleSelectionModelImpl.class,
                grid.getSelectionModel().getClass());
    }

    @Test(expected = NullPointerException.class)
    public void testNullSelectionMode() {
        grid.setSelectionMode(null);
    }

    @Test
    public void testGridAsMultiSelectHasAllAPI() {
        assertAllAPIAvailable(GridMultiSelect.class, MultiSelectionModel.class,
                "asMultiSelect");
    }

    @Test
    public void testGridAsSingleSelectHasAllAPI() {
        assertAllAPIAvailable(GridSingleSelect.class,
                SingleSelectionModel.class, "asSingleSelect");
    }

    @SuppressWarnings("rawtypes")
    protected void assertAllAPIAvailable(Class<?> testedClass,
            Class<? extends SelectionModel> selectionModelClass,
            String... ignoredMethods) {
        List<String> ignored = Arrays.asList(ignoredMethods);
        List<Method> missing = new ArrayList<>();
        Arrays.stream(selectionModelClass.getMethods()).filter(method -> {
            if (ignored.contains(method.getName())) {
                // Explicitly ignored method.
                return false;
            }

            try {
                // Skip methods from Extension interface
                Extension.class.getMethod(method.getName(),
                        method.getParameterTypes());
                return false;
            } catch (Exception e) {
                return true;
            }
        }).forEach(method ->

        {
            try {
                testedClass.getMethod(method.getName(),
                        method.getParameterTypes());
            } catch (Exception e) {
                missing.add(method);
            }
        });
        if (!missing.isEmpty()) {
            Assert.fail("Methods "
                    + missing.stream().map(Method::getName)
                            .collect(Collectors.joining(", "))
                    + " not found in " + testedClass.getSimpleName());
        }
    }
}

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
package com.vaadin.server;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.navigator.Navigator;
import com.vaadin.tests.VaadinClasses;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.components.colorpicker.ColorPickerHistory;
import com.vaadin.ui.components.colorpicker.ColorPickerPopup;
import com.vaadin.ui.components.colorpicker.ColorPickerPreview;
import com.vaadin.ui.components.colorpicker.ColorPickerSelect;
import com.vaadin.ui.components.grid.NoSelectionModel;

/**
 * @author Vaadin Ltd
 *
 */
public class DedicatedStateTest {

    private static final Set<String> WHITE_LIST = createWhiteList();

    @Test
    public void checkDedicatedStates() {
        VaadinClasses.getAllServerSideClasses().stream().filter(
                clazz -> AbstractClientConnector.class.isAssignableFrom(clazz))
                .forEach(this::checkState);
    }

    private void checkState(Class<?> clazz) {
        if (WHITE_LIST.contains(clazz.getCanonicalName())) {
            return;
        }
        Method getStateNoArg = getStateNoArg(clazz);
        Class<?> stateType = getStateNoArg.getReturnType();
        // check that stateType differs from the super class's state type
        Class<?> superclass = clazz.getSuperclass();
        if (!clazz.equals(AbstractClientConnector.class)
                && !superclass.equals(AbstractExtension.class)) {
            Assert.assertNotEquals(
                    "Class " + clazz
                            + " has the same state type as its super class "
                            + clazz.getSuperclass(),
                    stateType, getStateNoArg(superclass).getReturnType());
        }
        try {
            Method getStateOneArg = clazz.getDeclaredMethod("getState",
                    boolean.class);
            Assert.assertEquals(stateType, getStateOneArg.getReturnType());
        } catch (NoSuchMethodException e) {
            Assert.fail("Class " + clazz
                    + " doesn't have its own getState(boolean) method");
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private Method getStateNoArg(Class<?> clazz) {
        try {
            return clazz.getDeclaredMethod("getState");
        } catch (NoSuchMethodException e) {
            Assert.fail("Class " + clazz
                    + " doesn't have its own getState() method");
            return null;
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private static Set<String> createWhiteList() {
        Set<String> list = new HashSet<>();
        list.add(AbstractExtension.class.getCanonicalName());
        list.add(Navigator.EmptyView.class.getCanonicalName());
        list.add(ColorPickerHistory.class.getCanonicalName());
        list.add(ColorPickerPopup.class.getCanonicalName());
        list.add(ColorPickerPreview.class.getCanonicalName());
        list.add(ColorPickerSelect.class.getCanonicalName());
        list.add(NoSelectionModel.class.getCanonicalName());
        list.add(LegacyWindow.class.getCanonicalName());
        return list;
    }
}

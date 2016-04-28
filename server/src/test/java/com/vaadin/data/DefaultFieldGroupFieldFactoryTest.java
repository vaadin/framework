/*
 * Copyright 2000-2014 Vaadin Ltd.
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

import java.lang.reflect.Constructor;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;

public class DefaultFieldGroupFieldFactoryTest {

    private DefaultFieldGroupFieldFactory fieldFactory;

    @Before
    public void setupFieldFactory() {
        fieldFactory = DefaultFieldGroupFieldFactory.get();
    }

    @Test
    public void noPublicConstructor() {
        Class<DefaultFieldGroupFieldFactory> clazz = DefaultFieldGroupFieldFactory.class;
        Constructor<?>[] constructors = clazz.getConstructors();
        Assert.assertEquals(
                "DefaultFieldGroupFieldFactory contains public constructors",
                0, constructors.length);
    }

    @Test
    public void testSameInstance() {
        DefaultFieldGroupFieldFactory factory1 = DefaultFieldGroupFieldFactory
                .get();
        DefaultFieldGroupFieldFactory factory2 = DefaultFieldGroupFieldFactory
                .get();
        Assert.assertTrue(
                "DefaultFieldGroupFieldFactory.get() method returns different instances",
                factory1 == factory2);
        Assert.assertNotNull(
                "DefaultFieldGroupFieldFactory.get() method returns null",
                factory1);
    }

    @Test
    public void testDateGenerationForPopupDateField() {
        Field f = fieldFactory.createField(Date.class, DateField.class);
        Assert.assertNotNull(f);
        Assert.assertEquals(PopupDateField.class, f.getClass());
    }

    @Test
    public void testDateGenerationForInlineDateField() {
        Field f = fieldFactory.createField(Date.class, InlineDateField.class);
        Assert.assertNotNull(f);
        Assert.assertEquals(InlineDateField.class, f.getClass());
    }

    @Test
    public void testDateGenerationForTextField() {
        Field f = fieldFactory.createField(Date.class, TextField.class);
        Assert.assertNotNull(f);
        Assert.assertEquals(TextField.class, f.getClass());
    }

    @Test
    public void testDateGenerationForField() {
        Field f = fieldFactory.createField(Date.class, Field.class);
        Assert.assertNotNull(f);
        Assert.assertEquals(PopupDateField.class, f.getClass());
    }

    public enum SomeEnum {
        FOO, BAR;
    }

    @Test
    public void testEnumComboBox() {
        Field f = fieldFactory.createField(SomeEnum.class, ComboBox.class);
        Assert.assertNotNull(f);
        Assert.assertEquals(ComboBox.class, f.getClass());
    }

    @Test
    public void testEnumAnySelect() {
        Field f = fieldFactory
                .createField(SomeEnum.class, AbstractSelect.class);
        Assert.assertNotNull(f);
        Assert.assertEquals(ListSelect.class, f.getClass());
    }

    @Test
    public void testEnumAnyField() {
        Field f = fieldFactory.createField(SomeEnum.class, Field.class);
        Assert.assertNotNull(f);
        Assert.assertEquals(ListSelect.class, f.getClass());
    }
}

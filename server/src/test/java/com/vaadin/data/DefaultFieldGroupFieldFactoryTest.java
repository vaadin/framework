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

import java.lang.reflect.Constructor;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.legacy.ui.LegacyDateField;
import com.vaadin.legacy.ui.LegacyField;
import com.vaadin.legacy.ui.LegacyInlineDateField;
import com.vaadin.legacy.ui.LegacyPopupDateField;
import com.vaadin.legacy.ui.LegacyTextField;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ListSelect;

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
                "DefaultFieldGroupFieldFactory contains public constructors", 0,
                constructors.length);
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
        LegacyField f = fieldFactory.createField(Date.class,
                LegacyDateField.class);
        Assert.assertNotNull(f);
        Assert.assertEquals(LegacyPopupDateField.class, f.getClass());
    }

    @Test
    public void testDateGenerationForInlineDateField() {
        LegacyField f = fieldFactory.createField(Date.class,
                LegacyInlineDateField.class);
        Assert.assertNotNull(f);
        Assert.assertEquals(LegacyInlineDateField.class, f.getClass());
    }

    @Test
    public void testDateGenerationForTextField() {
        LegacyField f = fieldFactory.createField(Date.class,
                LegacyTextField.class);
        Assert.assertNotNull(f);
        Assert.assertEquals(LegacyTextField.class, f.getClass());
    }

    @Test
    public void testDateGenerationForField() {
        LegacyField f = fieldFactory.createField(Date.class, LegacyField.class);
        Assert.assertNotNull(f);
        Assert.assertEquals(LegacyPopupDateField.class, f.getClass());
    }

    public enum SomeEnum {
        FOO, BAR;
    }

    @Test
    public void testEnumComboBox() {
        LegacyField f = fieldFactory.createField(SomeEnum.class,
                ComboBox.class);
        Assert.assertNotNull(f);
        Assert.assertEquals(ComboBox.class, f.getClass());
    }

    @Test
    public void testEnumAnySelect() {
        LegacyField f = fieldFactory.createField(SomeEnum.class,
                AbstractSelect.class);
        Assert.assertNotNull(f);
        Assert.assertEquals(ListSelect.class, f.getClass());
    }

    @Test
    public void testEnumAnyField() {
        LegacyField f = fieldFactory.createField(SomeEnum.class,
                LegacyField.class);
        Assert.assertNotNull(f);
        Assert.assertEquals(ListSelect.class, f.getClass());
    }
}

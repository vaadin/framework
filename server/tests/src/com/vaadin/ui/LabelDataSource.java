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
package com.vaadin.ui;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.tests.util.AlwaysLockedVaadinSession;

public class LabelDataSource {

    Label label;
    private static final String STRING_DS_VALUE = "String DatA source";
    private static final int INTEGER_DS_VALUE = 1587;
    private static final String INTEGER_STRING_VALUE_FI = "1 587";
    private static final String INTEGER_STRING_VALUE_EN_US = "1,587";
    private static final Object INTEGER_STRING_VALUE_DE = "1.587";
    ObjectProperty<String> stringDataSource;
    private ObjectProperty<Integer> integerDataSource;
    VaadinSession vaadinSession;

    @Before
    public void setup() {
        vaadinSession = new AlwaysLockedVaadinSession(null);
        VaadinSession.setCurrent(vaadinSession);

        label = new Label();
        stringDataSource = new ObjectProperty<String>(STRING_DS_VALUE);
        integerDataSource = new ObjectProperty<Integer>(INTEGER_DS_VALUE);
    }

    @Test
    public void stringDataSource() {
        label.setPropertyDataSource(stringDataSource);
        Assert.assertEquals(STRING_DS_VALUE, label.getState().text);
        Assert.assertEquals(STRING_DS_VALUE, label.getValue());
        Assert.assertEquals(stringDataSource, label.getPropertyDataSource());
        label.setPropertyDataSource(null);
        Assert.assertEquals(STRING_DS_VALUE, label.getState().text);
        Assert.assertEquals(STRING_DS_VALUE, label.getValue());
        Assert.assertEquals(null, label.getPropertyDataSource());
        label.setValue("foo");
        Assert.assertEquals("foo", label.getState().text);
        Assert.assertEquals("foo", label.getValue());
        Assert.assertNull(label.getPropertyDataSource());

    }

    @Test
    public void integerDataSourceFi() {
        label.setLocale(new Locale("fi", "FI"));
        label.setPropertyDataSource(integerDataSource);
        Assert.assertEquals(INTEGER_STRING_VALUE_FI, label.getState().text);
        Assert.assertEquals(INTEGER_STRING_VALUE_FI, label.getValue());
        Assert.assertEquals(integerDataSource, label.getPropertyDataSource());
    }

    @Test
    public void integerDataSourceEn() {
        label.setLocale(new Locale("en", "US"));
        label.setPropertyDataSource(integerDataSource);
        Assert.assertEquals(INTEGER_STRING_VALUE_EN_US, label.getState().text);
        Assert.assertEquals(INTEGER_STRING_VALUE_EN_US, label.getValue());
        Assert.assertEquals(integerDataSource, label.getPropertyDataSource());
    }

    @Test
    public void changeLocaleAfterDataSource() {
        label.setLocale(new Locale("en", "US"));
        label.setPropertyDataSource(integerDataSource);
        label.setLocale(new Locale("fi", "FI"));
        Assert.assertEquals(INTEGER_STRING_VALUE_FI, label.getState().text);
        Assert.assertEquals(INTEGER_STRING_VALUE_FI, label.getValue());
        Assert.assertEquals(integerDataSource, label.getPropertyDataSource());
    }

    @Test
    public void setRemoveDataSource() {
        label.setValue("before");
        label.setPropertyDataSource(stringDataSource);
        Assert.assertEquals(STRING_DS_VALUE, label.getValue());
        label.setPropertyDataSource(null);
        Assert.assertEquals(STRING_DS_VALUE, label.getValue());
        label.setValue("after");
        Assert.assertEquals("after", label.getValue());
    }

    public class MockUI extends UI {

        public MockUI() {
            setSession(vaadinSession);
        }

        @Override
        protected void init(VaadinRequest request) {
        }

    }

    @Test
    public void attachToSessionWithDifferentLocale() {
        label.setValue("before");
        // label.setLocale(Locale.GERMANY);
        label.setPropertyDataSource(integerDataSource);
        UI ui = new MockUI();
        ui.setLocale(Locale.GERMANY);
        ui.setContent(label);
        Assert.assertEquals(INTEGER_STRING_VALUE_DE, label.getState().text);
    }
}

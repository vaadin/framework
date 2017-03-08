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
package com.vaadin.v7.tests.server.component.abstractfield;

import java.text.NumberFormat;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.tests.util.AlwaysLockedVaadinSession;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.util.converter.StringToIntegerConverter;
import com.vaadin.v7.ui.TextField;

public class AbsFieldDataSourceLocaleChangeTest {

    private VaadinSession vaadinSession;
    private UI ui;

    @Before
    public void setup() {
        vaadinSession = new AlwaysLockedVaadinSession(null);
        VaadinSession.setCurrent(vaadinSession);
        ui = new UI() {

            @Override
            protected void init(VaadinRequest request) {

            }
        };
        ui.setSession(vaadinSession);
        UI.setCurrent(ui);
    }

    @Test
    public void localeChangesOnAttach() {
        TextField tf = new TextField();

        tf.setConverter(new StringToIntegerConverter() {
            @Override
            protected NumberFormat getFormat(Locale locale) {
                if (locale == null) {
                    NumberFormat format = super.getFormat(locale);
                    format.setGroupingUsed(false);
                    format.setMinimumIntegerDigits(10);
                    return format;
                }
                return super.getFormat(locale);
            }
        });
        tf.setImmediate(true);
        tf.setConvertedValue(10000);
        Assert.assertEquals("0000010000", tf.getValue());

        VerticalLayout vl = new VerticalLayout();
        ui.setContent(vl);
        ui.setLocale(new Locale("en", "US"));

        vl.addComponent(tf);
        Assert.assertEquals("10,000", tf.getValue());
    }
}

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
package com.vaadin.tests.server.component.abstractselect;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.OptionGroup;

public class OptionGroupDeclarativeTests extends
        DeclarativeTestBase<OptionGroup> {

    private OptionGroup og;

    @Before
    public void init() {
        og = new OptionGroup();
    }

    @Test
    public void testBasicSyntax() {

        String expected = "<v-option-group />";
        testReadWrite(expected);

    }

    @Test
    public void testOptionSyntax() {

        og.addItems("foo", "bar", "baz", "bang");

        //@formatter:off
        String expected = 
                "<v-option-group>"
                + "<option>foo</option>"
                + "<option>bar</option>"
                + "<option>baz</option>"
                + "<option>bang</option>"
                + "</v-option-group>";
        //@formatter:on

        testReadWrite(expected);

    }

    @Test
    public void testDisabledOptionSyntax() {

        og.addItems("foo", "bar", "baz", "bang");
        og.setItemEnabled("baz", false);

        //@formatter:off
        String expected = 
                "<v-option-group>"
                + "<option>foo</option>"
                + "<option>bar</option>"
                + "<option disabled>baz</option>"
                + "<option>bang</option>"
                + "</v-option-group>";
        //@formatter:on

        testReadWrite(expected);

    }

    @Test
    public void testIconSyntax() {

        og.addItems("foo", "bar", "baz", "bang");
        og.setItemIcon("bar", new ThemeResource("foobar.png"));

        //@formatter:off
        String expected = 
                "<v-option-group>"
                + "<option>foo</option>"
                + "<option icon='theme://foobar.png'>bar</option>"
                + "<option>baz</option>"
                + "<option>bang</option>"
                + "</v-option-group>";
        //@formatter:on

        testReadWrite(expected);

    }

    @Test
    public void testHTMLCaption() {

        og.addItems("foo", "bar", "baz", "bang");

        og.setHtmlContentAllowed(true);

        og.setItemCaption("foo", "<b>True</b>");
        og.setItemCaption("bar", "<font color='red'>False</font>");

        //@formatter:off
        String expected = 
                "<v-option-group html-content-allowed='true'>"
                + "<option item-id=\"foo\"><b>True</b></option>"
                + "<option item-id=\"bar\"><font color='red'>False</font></option>"
                + "<option>baz</option>"
                + "<option>bang</option>"
                + "</v-option-group>";
        //@formatter:on

        testReadWrite(expected);
    }

    @Test
    public void testPlaintextCaption() {

        og.addItems("foo", "bar", "baz", "bang");

        og.setItemCaption("foo", "<b>True</b>");
        og.setItemCaption("bar", "<font color='red'>False</font>");

        //@formatter:off
        String expected = 
                "<v-option-group>"
                + "<option item-id=\"foo\"><b>True</b></option>"
                + "<option item-id=\"bar\"><font color='red'>False</font></option>"
                + "<option>baz</option>"
                + "<option>bang</option>"
                + "</v-option-group>";
        //@formatter:on

        testReadWrite(expected);
    }

    private void testReadWrite(String design) {
        testWrite(design, og, true);
        testRead(design, og);
    }

    @Override
    public OptionGroup testRead(String design, OptionGroup expected) {

        OptionGroup read = super.testRead(design, expected);
        testWrite(design, read, true);

        return read;
    }

}

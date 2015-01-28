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
package com.vaadin.tests.design;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignException;

public class InvalidTagNames {

    @Test(expected = DesignException.class)
    public void tagWithoutDash() {
        readDesign("<vbutton>foo</vbutton>");
    }

    @Test
    public void emptyTag() {
        // JSoup parses empty tags into text nodes
        Component c = readDesign("<>foo</>");
        Assert.assertNull(c);
    }

    @Test(expected = DesignException.class)
    public void onlyPrefix() {
        readDesign("<v->foo</v->");
    }

    @Test
    public void onlyClass() {
        // JSoup will refuse to parse tags starting with - and convert them into
        // text nodes instead
        Component c = readDesign("<-v>foo</-v>");
        Assert.assertNull(c);
    }

    @Test(expected = DesignException.class)
    public void unknownClass() {
        readDesign("<v-unknownbutton>foo</v-unknownbutton>");
    }

    @Test(expected = DesignException.class)
    public void unknownTag() {
        readDesign("<x-button></x-button>");
    }

    // @Test(expected = DesignException.class)
    // This is a side effect of not actively checking for invalid input. Will be
    // parsed currently as <v-button> (this should not be considered API)
    public void tagEndsInDash() {
        Component c = readDesign("<v-button-></v-button->");
        Assert.assertTrue(c.getClass() == Button.class);
    }

    // @Test(expected = DesignException.class)
    // This is a side effect of not actively checking for invalid input. Will be
    // parsed currently as <v-button> (this should not be considered API)
    public void tagEndsInTwoDashes() {
        Component c = readDesign("<v-button--></v-button-->");
        Assert.assertTrue(c.getClass() == Button.class);
    }

    // @Test(expected = DesignException.class)
    // This is a side effect of not actively checking for invalid input. Will be
    // parsed currently as <v-button> (this should not be considered API)
    public void tagWithTwoDashes() {
        Component c = readDesign("<v--button></v--button>");
        Assert.assertTrue(c.getClass() == Button.class);
    }

    @Test(expected = DesignException.class)
    public void specialCharacters() {
        readDesign("<v-button-&!#></v-button-&!#>");
    }

    private Component readDesign(String string) {
        try {
            return Design.read(new ByteArrayInputStream(string
                    .getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}

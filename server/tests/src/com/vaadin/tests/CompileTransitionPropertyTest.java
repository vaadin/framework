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
package com.vaadin.tests;

import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.vaadin.sass.internal.ScssStylesheet;

/*
 * This test checks that the transition mixin in valo/bourbon is usable (#15484).
 */
public class CompileTransitionPropertyTest {

    @Test
    public void testCompilation() throws Exception {
        ScssStylesheet ss = ScssStylesheet
                .get("server/tests/src/com/vaadin/tests/styles.scss");
        ss.compile();
        // extract the style rules for .my-label
        String compiled = ss.printState();
        Pattern pattern = Pattern.compile("(.my-label)(\\s)+(\\{)[^\\}]*");
        Matcher matcher = pattern.matcher(compiled);
        assertTrue("Could not find style rules for .my-label.", matcher.find());
        String elementStyle = matcher.group();
        elementStyle = elementStyle.replaceFirst(
                "(.my-label)(\\s)+(\\{)(\\s)*", "");
        // Check that the correct rules are present
        Pattern p1 = Pattern
                .compile("transition-property(\\s*):(\\s*)transform(\\s*);");
        Pattern p2 = Pattern
                .compile("-moz-transition-property(\\s*):(\\s*)-moz-transform(\\s*);");
        Pattern p3 = Pattern
                .compile("-webkit-transition-property(\\s*):(\\s*)-webkit-transform(\\s*);");
        assertTrue("The style 'transition-property: transform' is missing.", p1
                .matcher(elementStyle).find());
        assertTrue(
                "The style '-moz-transition-property: -moz-transform' is missing.",
                p2.matcher(elementStyle).find());
        assertTrue(
                "The style '-webkit-transition-property: -webkit-transform' is missing.",
                p3.matcher(elementStyle).find());
        // Check that there are no other styles for .my-label
        String modifiedStyle = p1.matcher(elementStyle).replaceFirst("");
        modifiedStyle = p2.matcher(modifiedStyle).replaceFirst("");
        modifiedStyle = p3.matcher(modifiedStyle).replaceFirst("");
        // Only whitespace should remain after removing the style rules
        modifiedStyle = modifiedStyle.replaceAll("(\\s)", "");
        assertTrue("Unexpected style rules for .my-label: " + modifiedStyle,
                modifiedStyle.length() == 0);
    }
}
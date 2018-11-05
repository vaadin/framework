package com.vaadin.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
        String file = getClass().getResource("styles.scss").getFile();
        if (file.contains("%20")) {
            fail("path contains spaces, please move the project");
        }
        ScssStylesheet ss = ScssStylesheet.get(file);
        ss.compile();
        // extract the style rules for .my-label
        String compiled = ss.printState();
        Pattern pattern = Pattern.compile("(.my-label)(\\s)+(\\{)[^\\}]*");
        Matcher matcher = pattern.matcher(compiled);
        assertTrue("Could not find style rules for .my-label.", matcher.find());
        String elementStyle = matcher.group();
        elementStyle = elementStyle.replaceFirst("(.my-label)(\\s)+(\\{)(\\s)*",
                "");
        // Check that the correct rules are present
        Pattern p1 = Pattern
                .compile("transition-property(\\s*):(\\s*)transform(\\s*);");
        Pattern p2 = Pattern.compile(
                "-moz-transition-property(\\s*):(\\s*)-moz-transform(\\s*);");
        Pattern p3 = Pattern.compile(
                "-webkit-transition-property(\\s*):(\\s*)-webkit-transform(\\s*);");
        assertTrue("The style 'transition-property: transform' is missing.",
                p1.matcher(elementStyle).find());
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
                modifiedStyle.isEmpty());
    }
}

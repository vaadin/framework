package com.vaadin.sass.internal.util;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilTest {

    @Test
    public void testContainsVariable() {
        String sentence = "$var1 var2";
        String word = "var";
        Assert.assertFalse(StringUtil.containsVariable(sentence, word));

        word = "var1";
        Assert.assertTrue(StringUtil.containsVariable(sentence, word));

        String var2 = "var2";
        Assert.assertFalse(StringUtil.containsVariable(sentence, var2));
    }

    @Test
    public void testContainsVariableWithDash() {
        String sentence = "$var- var2";
        String word = "var";
        Assert.assertFalse(StringUtil.containsVariable(sentence, word));
    }

    @Test
    public void testReplaceVariable() {
        String sentence = "$var1 var2";
        String word = "var";
        String value = "abc";
        Assert.assertEquals(sentence,
                StringUtil.replaceVariable(sentence, word, value));

        word = "var1";
        Assert.assertEquals("abc var2",
                StringUtil.replaceVariable(sentence, word, value));

        String var2 = "var2";
        Assert.assertEquals(sentence,
                StringUtil.replaceVariable(sentence, var2, value));
    }

    @Test
    public void testReplaceVariableWithDash() {
        String sentence = "$var- var2";
        String word = "var";
        String value = "abc";
        Assert.assertEquals(sentence,
                StringUtil.replaceVariable(sentence, word, value));
    }

    @Test
    public void testContainsSubString() {
        String sentence = "var1 var2";
        String word = "var";
        Assert.assertFalse(StringUtil.containsSubString(sentence, word));

        word = "var1";
        Assert.assertTrue(StringUtil.containsSubString(sentence, word));

        String var2 = "var2";
        Assert.assertTrue(StringUtil.containsSubString(sentence, var2));

        Assert.assertTrue(StringUtil.containsSubString(".error.intrusion",
                ".error"));

        Assert.assertFalse(StringUtil.containsSubString(".foo", "oo"));
    }

    @Test
    public void testContainsSubStringWithDash() {
        String sentence = "var- var2";
        String word = "var";
        Assert.assertFalse(StringUtil.containsSubString(sentence, word));
    }

    @Test
    public void testReplaceSubString() {
        String sentence = "var1 var2";
        String word = "var";
        String value = "abc";

        word = "var1";
        Assert.assertEquals("abc var2",
                StringUtil.replaceSubString(sentence, word, value));

        String var2 = "var1 abc";
        Assert.assertEquals(sentence,
                StringUtil.replaceSubString(sentence, var2, value));

        Assert.assertEquals(".foo",
                StringUtil.replaceSubString(".foo", "oo", "aa"));
    }

    @Test
    public void testReplaceSubStringWithDash() {
        String sentence = "var- var2";
        String word = "var";
        String value = "abc";
        Assert.assertEquals(sentence,
                StringUtil.replaceSubString(sentence, word, value));
    }

    @Test
    public void testRemoveDuplicatedClassSelector() {
        Assert.assertEquals(".seriousError", StringUtil
                .removeDuplicatedSubString(".seriousError.seriousError", "."));
    }
}

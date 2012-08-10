/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.sass.parser;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;

import com.vaadin.sass.handler.SCSSDocumentHandler;
import com.vaadin.sass.handler.SCSSDocumentHandlerImpl;

public class ParserTest {

    @Test
    public void testParsePropertyValue() throws CSSException, IOException {
        Parser parser = new Parser();

        LexicalUnit value = parser.parsePropertyValue(new InputSource(
                new StringReader("$margin/2;")));

        Assert.assertEquals("margin", value.getStringValue());
        Assert.assertEquals(SCSSLexicalUnit.SCSS_VARIABLE,
                value.getLexicalUnitType());
        value = value.getNextLexicalUnit();
        Assert.assertEquals(LexicalUnit.SAC_OPERATOR_SLASH,
                value.getLexicalUnitType());
        value = value.getNextLexicalUnit();
        Assert.assertEquals(LexicalUnit.SAC_INTEGER, value.getLexicalUnitType());
        Assert.assertEquals(2, value.getIntegerValue());

    }

    @Test
    public void testCanIngoreSingleLineComment() {
        Parser parser = new Parser();
        SCSSDocumentHandler handler = new SCSSDocumentHandlerImpl();
        parser.setDocumentHandler(handler);
        try {
            parser.parseStyleSheet(new InputSource(new StringReader(
                    "//kjaljsföajsfalkj\n@12abcg;")));
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
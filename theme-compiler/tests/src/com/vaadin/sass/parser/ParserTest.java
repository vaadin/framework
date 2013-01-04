/*
 * Copyright 2000-2013 Vaadin Ltd.
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

package com.vaadin.sass.parser;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;

import com.vaadin.sass.internal.handler.SCSSDocumentHandler;
import com.vaadin.sass.internal.handler.SCSSDocumentHandlerImpl;
import com.vaadin.sass.internal.parser.Parser;
import com.vaadin.sass.internal.parser.SCSSLexicalUnit;

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
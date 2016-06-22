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
package com.vaadin.ui.declarative;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

/**
 * Tests for {@link Design} declarative support class.
 * 
 * @author Vaadin Ltd
 */
public class DesignTest {

    private static Charset CP1251_CHARSET = Charset.forName("cp1251");
    private static Charset UTF8_CHARSET = Charset.forName("UTF-8");

    private static String NON_ASCII_STRING = "\u043C";

    private static Charset DEFAULT_CHARSET = Charset.defaultCharset();

    @AfterClass
    public static void restoreCharset() throws NoSuchFieldException,
            SecurityException, IllegalArgumentException, IllegalAccessException {
        setCharset(DEFAULT_CHARSET);
    }

    @Test
    public void write_cp1251SystemDefaultEncoding_resultEqualsToUtf8Encoding()
            throws IOException, NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        setCp1251Charset();
        String cp1251Html = getHtml();
        setUtf8Charset();
        String utf8Html = getHtml();
        Assert.assertEquals("Html written with UTF-8 as default encoding "
                + "differs from html written with cp1251 encoding", cp1251Html,
                utf8Html);
    }

    @Test
    public void write_cp1251SystemDefaultEncoding_writtenLabelHasCorrectValue()
            throws IOException, NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        setCp1251Charset();
        String cp1251Html = getHtml();
        Assert.assertEquals("Non ascii string parsed from serialized HTML "
                + "differs from expected", NON_ASCII_STRING,
                getHtmlLabelValue(cp1251Html));
    }

    @Test
    public void write_utf8SystemDefaultEncoding_writtenLabelHasCorrectValue()
            throws IOException, NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        setUtf8Charset();
        String utf8 = getHtml();
        Assert.assertEquals("Non ascii string parsed from serialized HTML "
                + "differs from expected", NON_ASCII_STRING,
                getHtmlLabelValue(utf8));
    }

    private String getHtmlLabelValue(String html) {
        Document document = Jsoup.parse(html);
        Element label = document.select("vaadin-label").get(0);

        StringBuilder builder = new StringBuilder();
        for (Node child : label.childNodes()) {
            if (child instanceof TextNode) {
                builder.append(((TextNode) child).getWholeText());
            }
        }
        return builder.toString().trim();
    }

    private String getHtml() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Component label = new Label(NON_ASCII_STRING);
        Design.write(label, out);
        return out.toString(UTF8_CHARSET.name());
    }

    private void setCp1251Charset() throws NoSuchFieldException,
            SecurityException, IllegalArgumentException, IllegalAccessException {
        setCharset(CP1251_CHARSET);
    }

    private void setUtf8Charset() throws NoSuchFieldException,
            SecurityException, IllegalArgumentException, IllegalAccessException {
        setCharset(UTF8_CHARSET);
    }

    private static void setCharset(Charset charset)
            throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        Field field = Charset.class.getDeclaredField("defaultCharset");
        field.setAccessible(true);
        field.set(null, charset);
    }

}

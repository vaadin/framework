package com.vaadin.ui.declarative;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.junit.AfterClass;
import org.junit.Test;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

/**
 * Tests for {@link Design} declarative support class.
 *
 * @author Vaadin Ltd
 */
public class DesignTest {

    private static final Charset CP1251_CHARSET = Charset.forName("cp1251");
    private static final Charset UTF8_CHARSET = StandardCharsets.UTF_8;

    private static final String NON_ASCII_STRING = "\u043C";

    private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();

    @AfterClass
    public static void restoreCharset()
            throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
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
        assertEquals(
                "Html written with UTF-8 as default encoding "
                        + "differs from html written with cp1251 encoding",
                cp1251Html, utf8Html);
    }

    @Test
    public void write_cp1251SystemDefaultEncoding_writtenLabelHasCorrectValue()
            throws IOException, NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        setCp1251Charset();
        String cp1251Html = getHtml();
        assertEquals(
                "Non ascii string parsed from serialized HTML "
                        + "differs from expected",
                NON_ASCII_STRING, getHtmlLabelValue(cp1251Html));
    }

    @Test
    public void write_utf8SystemDefaultEncoding_writtenLabelHasCorrectValue()
            throws IOException, NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        setUtf8Charset();
        String utf8 = getHtml();
        assertEquals(
                "Non ascii string parsed from serialized HTML "
                        + "differs from expected",
                NON_ASCII_STRING, getHtmlLabelValue(utf8));
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

    private void setCp1251Charset()
            throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        setCharset(CP1251_CHARSET);
    }

    private void setUtf8Charset()
            throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
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

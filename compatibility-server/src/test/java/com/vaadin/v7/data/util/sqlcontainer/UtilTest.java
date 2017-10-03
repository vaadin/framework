package com.vaadin.v7.data.util.sqlcontainer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UtilTest {

    @Test
    public void escapeSQL_noQuotes_returnsSameString() {
        assertEquals("asdf", SQLUtil.escapeSQL("asdf"));
    }

    @Test
    public void escapeSQL_singleQuotes_returnsEscapedString() {
        assertEquals("O''Brien", SQLUtil.escapeSQL("O'Brien"));
    }

    @Test
    public void escapeSQL_severalQuotes_returnsEscapedString() {
        assertEquals("asdf''ghjk''qwerty",
                SQLUtil.escapeSQL("asdf'ghjk'qwerty"));
    }

    @Test
    public void escapeSQL_doubleQuotes_returnsEscapedString() {
        assertEquals("asdf\\\"foo", SQLUtil.escapeSQL("asdf\"foo"));
    }

    @Test
    public void escapeSQL_multipleDoubleQuotes_returnsEscapedString() {
        assertEquals("asdf\\\"foo\\\"bar", SQLUtil.escapeSQL("asdf\"foo\"bar"));
    }

    @Test
    public void escapeSQL_backslashes_returnsEscapedString() {
        assertEquals("foo\\\\nbar\\\\r", SQLUtil.escapeSQL("foo\\nbar\\r"));
    }

    @Test
    public void escapeSQL_x00_removesX00() {
        assertEquals("foobar", SQLUtil.escapeSQL("foo\\x00bar"));
    }

    @Test
    public void escapeSQL_x1a_removesX1a() {
        assertEquals("foobar", SQLUtil.escapeSQL("foo\\x1abar"));
    }
}

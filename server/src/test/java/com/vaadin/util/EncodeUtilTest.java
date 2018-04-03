package com.vaadin.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EncodeUtilTest {
    @Test
    public void rfc5987Encode() {
        assertEquals("A", EncodeUtil.rfc5987Encode("A"));
        assertEquals("%20", EncodeUtil.rfc5987Encode(" "));
        assertEquals("%c3%a5", EncodeUtil.rfc5987Encode("å"));
        assertEquals("%e6%97%a5", EncodeUtil.rfc5987Encode("日"));

        assertEquals("A" + "%20" + "%c3%a5" + "%e6%97%a5",
                EncodeUtil.rfc5987Encode("A å日"));
    }
}

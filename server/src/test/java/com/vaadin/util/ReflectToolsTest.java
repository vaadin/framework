package com.vaadin.util;

import static org.junit.Assert.assertSame;

import java.io.Serializable;

import org.junit.Test;

public class ReflectToolsTest implements Serializable {
    @Test
    public void findCommonBaseType_sameType() {
        assertSame(Number.class,
                ReflectTools.findCommonBaseType(Number.class, Number.class));
    }

    @Test
    public void findCommonBaseType_aExtendsB() {
        assertSame(Number.class,
                ReflectTools.findCommonBaseType(Integer.class, Number.class));
    }

    @Test
    public void findCommonBaseType_bExtendsA() {
        assertSame(Number.class,
                ReflectTools.findCommonBaseType(Number.class, Integer.class));
    }

    @Test
    public void findCommonBaseType_commonBase() {
        assertSame(Number.class,
                ReflectTools.findCommonBaseType(Double.class, Integer.class));
    }

    @Test
    public void findCommonBaseType_noCommonBase() {
        assertSame(Object.class,
                ReflectTools.findCommonBaseType(String.class, Number.class));
    }
}

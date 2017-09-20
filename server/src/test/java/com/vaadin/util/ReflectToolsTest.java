/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.util;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.Test;

public class ReflectToolsTest implements Serializable {
    @Test
    public void findCommonBaseType_sameType() {
        Assert.assertSame(Number.class,
                ReflectTools.findCommonBaseType(Number.class, Number.class));
    }

    @Test
    public void findCommonBaseType_aExtendsB() {
        Assert.assertSame(Number.class,
                ReflectTools.findCommonBaseType(Integer.class, Number.class));
    }

    @Test
    public void findCommonBaseType_bExtendsA() {
        Assert.assertSame(Number.class,
                ReflectTools.findCommonBaseType(Number.class, Integer.class));
    }

    @Test
    public void findCommonBaseType_commonBase() {
        Assert.assertSame(Number.class,
                ReflectTools.findCommonBaseType(Double.class, Integer.class));
    }

    @Test
    public void findCommonBaseType_noCommonBase() {
        Assert.assertSame(Object.class,
                ReflectTools.findCommonBaseType(String.class, Number.class));
    }
}
